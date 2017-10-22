package a3osoft.jiratracker.jira;

import android.content.Context;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import a3osoft.jiratracker.database.DatabaseHelper;

@DatabaseTable(tableName = "issues")
public class Issue {

    @SerializedName("key")
    @DatabaseField(canBeNull=false, id = true)
    private String key;

    @SerializedName("fields")
    @DatabaseField(dataType= DataType.SERIALIZABLE)
    private Fields fields;

    @DatabaseField(columnName = "date_start")
    private Date dateStart;

    public Issue(){

    }

    public Issue(String key, Fields fields) {
        this.key = key;
        this.fields = fields;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName(){
        return this.fields.summary;
    }

    public String getType(){
        return this.fields.type.name;
    }

    public String getStatus(){
        return this.fields.status.name;
    }

    public boolean isTracking(){
        return dateStart != null;
    }

    public Date getDateStart(){
        return dateStart;
    }

    public void startTracking(Context context, Date date){
        this.dateStart = date;
        try {
            DatabaseHelper.getInstance(context).getIssueDao().update(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void stopTracking(Context context){
        try {
            createWorklog(context);
            this.dateStart = null;
            DatabaseHelper.getInstance(context).getIssueDao().update(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createWorklog(Context context) throws SQLException {
        long millis = new Date().getTime() - this.dateStart.getTime();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        if(seconds > 60){
            Worklog worklog = new Worklog(this.key, this.dateStart, seconds, false);
            worklog.createJson();
            DatabaseHelper.getInstance(context).getWorklogDao().create(worklog);
            Log.e("WORKLOG", "worklog created!");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Issue)) return false;
        Issue issue = (Issue) o;
        return Objects.equals(key, issue.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "Issue{" +
                "key='" + key + '\'' +
                ", status='" + getStatus() + '\'' +
                ", summary='" + getName() + '\'' +
                ", type='" + getType() + '\'' +
                '}';
    }

    private class Fields  implements Serializable{
        @SerializedName("summary")
        @DatabaseField
        String summary;

        @SerializedName("issuetype")
        @DatabaseField(dataType=DataType.SERIALIZABLE)
        Type type;

        @SerializedName("status")
        @DatabaseField(dataType=DataType.SERIALIZABLE)
        Status status;
    }

    private class Type implements Serializable{
        @SerializedName("name")
        @DatabaseField
        String name;
    }

    private class Status implements Serializable{
        @SerializedName("name")
        @DatabaseField
        String name;
    }
}
