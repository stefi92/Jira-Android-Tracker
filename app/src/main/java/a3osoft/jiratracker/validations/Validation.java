package a3osoft.jiratracker.validations;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "validations")
public class Validation {

    @DatabaseField(id = true)
    private int id;
    @DatabaseField
    private String message;
    @DatabaseField
    private String from;
    @DatabaseField
    private String to;

    public Validation(){

    }

    public Validation(int id, String message, String from, String to){
        this.id = id;
        this.message = message;
        this.from = from;
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Validation{" +
                "message='" + message + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}