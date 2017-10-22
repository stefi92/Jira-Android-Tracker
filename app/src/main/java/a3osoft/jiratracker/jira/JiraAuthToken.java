package a3osoft.jiratracker.jira;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tokens")
public class JiraAuthToken {

    @DatabaseField(id = true)
    private String token;

    public JiraAuthToken(){

    }

    public JiraAuthToken(String token){
        this.token = token;
    }

    public String getToken(){
        return token;
    }

}
