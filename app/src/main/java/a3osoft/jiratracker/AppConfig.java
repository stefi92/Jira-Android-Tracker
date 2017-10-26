package a3osoft.jiratracker;

public class AppConfig {

    public static final String BASE_URL = "https://3osoft.atlassian.net/";
    public static final String MY_ISSUES = "project=\"REDSD\" AND assignee=currentUser() AND " +
            "status=\"Open\"";

}
