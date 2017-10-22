package a3osoft.jiratracker.network;

import com.google.gson.Gson;

import a3osoft.jiratracker.AppConfig;
import a3osoft.jiratracker.jira.JiraIssueResponse;
import a3osoft.jiratracker.jira.Worklog;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class RestClient {

    private static RestClient instance;
    private RestInterface restService;

    private RestClient() {
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(AppConfig.BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
        restService = retrofit.create(RestInterface.class);
    }

    public static RestClient getInstance() {
        if (instance == null) {
            instance = new RestClient();
        }
        return instance;
    }

    public Observable<JiraIssueResponse> getIssues(String jql, String token) {
        return restService.getIssues(jql, token);
    }

    public Observable<Response<Void>> uploadWorklog(Worklog worklog, String token) {
        return restService.uploadWorklog(worklog.getIssueKey(), worklog.createJson(), token);
    }
}