package a3osoft.jiratracker.network;

import a3osoft.jiratracker.jira.Issue;
import a3osoft.jiratracker.jira.JiraIssueResponse;
import a3osoft.jiratracker.jira.JiraResponse;
import rx.Observer;

public class JiraIssueObserver implements Observer<JiraIssueResponse> {

    private final JiraResponse<Issue> jiraResponse;

    public JiraIssueObserver(JiraResponse<Issue> jiraResponse) {
        this.jiraResponse = jiraResponse;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        jiraResponse.onError(e);
    }

    @Override
    public void onNext(final JiraIssueResponse response) {
        jiraResponse.onSuccess(response.getIssues());
    }
}
