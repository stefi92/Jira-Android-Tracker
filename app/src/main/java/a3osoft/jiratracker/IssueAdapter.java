package a3osoft.jiratracker;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import a3osoft.jiratracker.jira.Issue;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.IssueViewHolder> {

    private static final String TAG = "IssueItem";
    private List<Issue> issues;
    private Tracker tracker;

    class IssueViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView key;
        TextView title;
        TextView type;
        TextView status;
        Issue issue;

        IssueViewHolder(View v) {
            super(v);
            key = (TextView) v.findViewById(R.id.issue_key);
            title = (TextView) v.findViewById(R.id.issue_name);
            type = (TextView) v.findViewById(R.id.issue_type);
            status = (TextView) v.findViewById(R.id.issue_status);
        }

        void bind(final int position) {
            issue = issues.get(position);
            key.setText(issue.getKey());
            title.setText(issue.getName());
            status.setText(issue.getStatus());
            type.setText(issue.getType());
            if(issue.isTracking()){
                itemView.setBackgroundColor(Color.GREEN);
            }
            else{
                itemView.setBackgroundColor(Color.WHITE);
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            tracker.startTracking(issue);
        }
    }

    IssueAdapter(Tracker tracker, List<Issue> issues) {
        this.tracker = tracker;
        this.issues = issues;
        notifyDataSetChanged();
    }

    IssueAdapter(Tracker tracker) {
        this.tracker = tracker;
        this.issues = new ArrayList<>();
    }

    public void addIssue(Issue issue){
        this.issues.add(issue);
    }

    public void addAll(List<Issue> issues){
        this.issues.addAll(issues);
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
