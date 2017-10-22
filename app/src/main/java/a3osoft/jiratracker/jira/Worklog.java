package a3osoft.jiratracker.jira;

import com.google.gson.JsonObject;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@DatabaseTable(tableName = "worklogs")
public class Worklog {

    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField
    private String issueKey;
    @DatabaseField
    private Date dateStart;
    @DatabaseField
    private long durationInSeconds;
    @DatabaseField
    private boolean sync;

    public Worklog(){

    }

    public Worklog(String issueKey, Date dateStart, long durationInSeconds, boolean sync) {
        this.issueKey = issueKey;
        this.dateStart = dateStart;
        this.durationInSeconds = durationInSeconds;
        this.sync = sync;
    }

    public String getIssueKey(){
        return this.issueKey;
    }

    public JsonObject createJson(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("started", format.format(dateStart));
        jsonObject.addProperty("timeSpentSeconds", durationInSeconds);
        return jsonObject;
    }

}
