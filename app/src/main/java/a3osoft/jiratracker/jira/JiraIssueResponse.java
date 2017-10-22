package a3osoft.jiratracker.jira;

import java.util.List;

public class JiraIssueResponse {
    private int total;
    private List<Issue> issues;

    public JiraIssueResponse(int total, List<Issue> issues){
        this.total = total;
        this.issues = issues;
    }

    public List<Issue> getIssues() {
        return this.issues;
    }
}
