package a3osoft.jiratracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import a3osoft.jiratracker.alarm.AlarmService;
import a3osoft.jiratracker.database.DatabaseHelper;
import a3osoft.jiratracker.jira.Issue;
import a3osoft.jiratracker.jira.JiraResponse;
import a3osoft.jiratracker.jira.Worklog;
import a3osoft.jiratracker.network.JiraIssueObserver;
import a3osoft.jiratracker.network.JiraWorklogObserver;
import a3osoft.jiratracker.network.RestClient;
import a3osoft.jiratracker.validations.SAXXMLParser;
import a3osoft.jiratracker.validations.Validation;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements JiraResponse<Issue>, Tracker{

    @BindView(R.id.dashboard_recycler)
    RecyclerView recyclerView;

    private IssueAdapter issueAdapter;
    private Issue trackingIssue;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        this.issueAdapter = new IssueAdapter(this);
        recyclerView.setAdapter(issueAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.token = DatabaseHelper.getInstance(this).getAuthToken();
        try {
            getIssuesFromDatabase();
            parseXmlValidation();
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void getTrackingIssue(List<Issue> issues){
        for(Issue issue : issues){
            if(issue.getDateStart() != null){
                trackingIssue = issue;
                break;
            }
        }
    }

    private void getIssuesFromDatabase() throws SQLException {
        DatabaseHelper helper = DatabaseHelper.getInstance(this);
        List<Issue> queryResult = helper.getIssueDao().queryForAll();
        issueAdapter.addAll(queryResult);
        issueAdapter.notifyDataSetChanged();
        getTrackingIssue(queryResult);
    }

    private void downloadMyAllOpenIssues(){
        RestClient.getInstance()
                .getIssues(AppConfig.MY_ISSUES, token)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new JiraIssueObserver(this));
    }

    private void uploadAllWorklogs() throws SQLException {
        DatabaseHelper helper = DatabaseHelper.getInstance(this);
        List<Worklog> worklogs = helper.getWorklogDao().queryForAll();
        Log.e("Worklog", "worklogs to upload: " + worklogs.size());
        for(Worklog worklog : worklogs) {
            RestClient.getInstance()
                    .uploadWorklog(worklog, token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .subscribe(new JiraWorklogObserver(this, worklog));
        }
    }

    private void logOut(){
        try {
            DatabaseHelper.getInstance(this).clearAllData();
            finish();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void parseXmlValidation() throws FileNotFoundException, SQLException {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/validations.xml");
        FileInputStream fileInputStream = new FileInputStream(file);
        List<Validation> list = SAXXMLParser.parse(fileInputStream);
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        for(Validation validation : list){
            databaseHelper.getValidationDao().createOrUpdate(validation);
        }
        //Alarm alarm = new Alarm();
        //alarm.setAlarm(this);
        Log.d("validations", String.valueOf(databaseHelper.getValidationDao().queryForAll()));
        if(!isMyServiceRunning(AlarmService.class)){
            Log.e("alarm", "alarm service not running!");
            startService(new Intent(this, AlarmService.class));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.synchronize:
                Log.e("synch", "sychronizing requested!");
                if(this.trackingIssue != null && this.trackingIssue.isTracking()){
                    this.trackingIssue.stopTracking(this);
                }
                showProgressDialog();
                try {
                    uploadAllWorklogs();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                downloadMyAllOpenIssues();
                break;
            case R.id.logout:
                logOut();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onError(Throwable e) {
        Log.d("downloading issues", String.valueOf(e.getMessage()));
    }

    @Override
    public void onSuccess(List<Issue> issues) {
        issueAdapter.addAll(issues);
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        for(Issue issue : issues) {
            try {
                databaseHelper.getIssueDao().createOrUpdate(issue);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgressDialog();
                issueAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void startTracking(Issue issue) {
        if(issue.isTracking()){
            stopTracking(issue);
            return;
        }
        if(trackingIssue != null && trackingIssue.isTracking()) {
            stopTracking(trackingIssue);
        }
        trackingIssue = issue;
        trackingIssue.startTracking(this, new Date());
        recyclerView.findViewHolderForAdapterPosition(issueAdapter.issuePosition(issue)).itemView.setBackgroundColor(Color.GREEN);

    }

    @Override
    public void stopTracking(Issue issue) {
        issue.stopTracking(this);
        recyclerView.findViewHolderForAdapterPosition(issueAdapter.issuePosition(issue)).itemView.setBackgroundColor(Color.WHITE);
    }
}
