package a3osoft.jiratracker.network;

import com.google.gson.JsonObject;

import a3osoft.jiratracker.jira.JiraIssueResponse;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface RestInterface {

    @GET("rest/api/2/search")
    Observable<JiraIssueResponse> getIssues(@Query("jql") String jql, @Header("Authorization") String authorization);

    @POST("rest/api/2/issue/{issue}/worklog")
    Observable<Response<Void>> uploadWorklog(@Path("issue") String issue, @Body JsonObject worklog, @Header("Authorization") String authorization);

}
