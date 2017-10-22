package a3osoft.jiratracker.jira;

import java.util.List;

public interface JiraResponse<T> {

    void onError(Throwable e);

    void onSuccess(List<T> objects);

}
