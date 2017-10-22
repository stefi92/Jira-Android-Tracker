package a3osoft.jiratracker;

import a3osoft.jiratracker.jira.Issue;

public interface Tracker {

    void startTracking(Issue issue);

    void stopTracking(Issue issue);

}
