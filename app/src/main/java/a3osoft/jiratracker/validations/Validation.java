package a3osoft.jiratracker.validations;

import android.util.Pair;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

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

    public Pair<Integer, Integer> getDateFrom(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        try {
            int hourFrom = dateFormat.parse(from).getHours();
            int minuteFrom = dateFormat.parse(from).getMinutes();
            return Pair.create(hourFrom, minuteFrom);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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