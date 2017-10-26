package a3osoft.jiratracker;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import a3osoft.jiratracker.database.DatabaseHelper;
import a3osoft.jiratracker.jira.Issue;
import a3osoft.jiratracker.jira.JiraResponse;
import a3osoft.jiratracker.jira.Worklog;
import a3osoft.jiratracker.network.JiraIssueObserver;
import a3osoft.jiratracker.network.JiraWorklogObserver;
import a3osoft.jiratracker.network.RestClient;
import a3osoft.jiratracker.network.UploadListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;
import rx.Observable;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements JiraResponse<Issue>, UploadListener, Tracker{

    @BindView(R.id.dashboard_recycler) RecyclerView recyclerView;
    @BindView(R.id.total_worklogs) TextView totalWorklogsTV;

    private IssueAdapter issueAdapter;
    private Issue trackingIssue;
    private String token;
    private boolean isUploadingWorklogs;
    private boolean isDownloadingIssues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        this.issueAdapter = new IssueAdapter(this, this);
        recyclerView.setAdapter(issueAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateTotalUnsychronizedTime();
        this.token = DatabaseHelper.getInstance(this).getAuthToken();
        try {
            getIssuesFromDatabase();
        } catch (SQLException e) {
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
        isDownloadingIssues = true;
        RestClient.getInstance()
                .getIssues(AppConfig.MY_ISSUES, token)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new JiraIssueObserver(this));
    }

    private void uploadAllWorklogs() throws SQLException {
        DatabaseHelper helper = DatabaseHelper.getInstance(this);
        List<Worklog> worklogs = helper.getWorklogDao().queryForAll();
        isUploadingWorklogs = !worklogs.isEmpty();
        List<Observable<Response<Void>>> observables = new ArrayList<>();
        JiraWorklogObserver observer = new JiraWorklogObserver(this);
        for(Worklog worklog : worklogs) {
            Observable<Response<Void>> observable = RestClient.getInstance().uploadWorklog(worklog, token);
            observables.add(observable);
        }

        Observable.concatEager(observables)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(observer);
    }

    private void logOut(){
        try {
            DatabaseHelper.getInstance(this).clearAllData();
            finish();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTotalUnsychronizedTime(){
        try {
            List<Worklog> worklogs = DatabaseHelper.getInstance(this).getWorklogDao().queryForAll();
            long total = 0;
            for(Worklog worklog : worklogs){
                total += worklog.getDurationInSeconds();
            }
            long millis = TimeUnit.SECONDS.toMillis(total);

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            totalWorklogsTV.setText(getString(R.string.total_time, dateFormat.format(millis)));
        } catch (SQLException e) {
            e.printStackTrace();
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
                if(this.trackingIssue != null && this.trackingIssue.isTracking()){
                    this.trackingIssue.stopTracking(this);
                }
                showProgressDialog(this);
                try {
                    uploadAllWorklogs();
                    downloadMyAllOpenIssues();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.logout:
                logOut();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onError(Throwable e) {
        Log.d("downloading", String.valueOf(e.getMessage()));
    }

    @Override
    public void onSuccess(List<Issue> issues) {
        isDownloadingIssues = false;
        issueAdapter.clear();
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
                if(!isUploadingWorklogs) hideProgressDialog();
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
        issueAdapter.notifyDataSetChanged();
        updateTotalUnsychronizedTime();
    }

    @Override
    public void successfulUpload() {
        isUploadingWorklogs = false;
        DatabaseHelper.getInstance(this).cleaWorklogs();
        Log.d("Worklog", "successfull delete");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isDownloadingIssues) hideProgressDialog();
                issueAdapter.notifyDataSetChanged();
                updateTotalUnsychronizedTime();
            }
        });
    }
}
