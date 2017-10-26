package a3osoft.jiratracker;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import a3osoft.jiratracker.database.DatabaseHelper;
import a3osoft.jiratracker.jira.Issue;
import a3osoft.jiratracker.jira.Worklog;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.IssueViewHolder> {

    private static final String TAG = "IssueItem";
    private List<Issue> issues;
    private Tracker tracker;
    private Context context;

    class IssueViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView issueKeyTV;
        TextView issueTitleTV;
        TextView issueStatusTV;
        TextView issueWorklogsTV;
        TextView issueTimeTrackedTV;
        Issue issue;

        IssueViewHolder(View v) {
            super(v);
            issueKeyTV = (TextView) v.findViewById(R.id.issue_key);
            issueTitleTV = (TextView) v.findViewById(R.id.issue_name);
            issueStatusTV = (TextView) v.findViewById(R.id.issue_status);
            issueWorklogsTV = (TextView) v.findViewById(R.id.issue_worklogs);
            issueTimeTrackedTV = (TextView) v.findViewById(R.id.issue_time_tracked);
        }

        void bind(final int position) {
            issue = issues.get(position);
            issueKeyTV.setText(issue.getKey());
            issueTitleTV.setText(issue.getName());
            issueStatusTV.setText(context.getString(R.string.status_resource ,issue.getStatus()));;
            updateNumberOfWorklogs();
            updateTrackedTime();
            if(issue.isTracking()){
                itemView.setBackgroundColor(Color.GREEN);
            }
            else{
                itemView.setBackgroundColor(Color.WHITE);
            }
            itemView.setOnClickListener(this);
        }

        void updateNumberOfWorklogs(){
            try {
                QueryBuilder qb = DatabaseHelper.getInstance(context).getWorklogDao().queryBuilder();
                qb.where().eq("issueKey", issue.getKey()).prepare();
                int number = qb.query().size();
                issueWorklogsTV.setText(context.getString(R.string.worklogs_number, number));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        void updateTrackedTime(){
            try {
                QueryBuilder<Worklog, Integer> qb = DatabaseHelper.getInstance(context).getWorklogDao().queryBuilder();
                qb.where().eq("issueKey", issue.getKey()).prepare();
                List<Worklog> worklogs = qb.query();
                long total = 0;
                for(Worklog worklog : worklogs){
                    total += worklog.getDurationInSeconds();
                }
                long millis = TimeUnit.SECONDS.toMillis(total);

                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                issueTimeTrackedTV.setText(dateFormat.format(millis));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View view) {
            tracker.startTracking(issue);
        }
    }

    IssueAdapter(Context context, Tracker tracker, List<Issue> issues) {
        this.context = context;
        this.tracker = tracker;
        this.issues = issues;
        notifyDataSetChanged();
    }

    IssueAdapter(Context context, Tracker tracker) {
        this.context = context;
        this.tracker = tracker;
        this.issues = new ArrayList<>();
    }

    public void addIssue(Issue issue){
        this.issues.add(issue);
    }

    public void addAll(List<Issue> issues){
        this.issues.addAll(issues);
    }

    public void clear(){
        this.issues.clear();
    }

    public int issuePosition(Issue issue){
        return issues.indexOf(issue);
    }

    @Override
    public void onBindViewHolder(IssueViewHolder viewHolder, int i) {
        viewHolder.bind(i);
    }

    @Override
    public int getItemCount() {
        return this.issues.size();
    }

    @Override
    public IssueViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.issue_card_layout, viewGroup, false);
        return new IssueViewHolder(itemView);
    }
}
