package a3osoft.jiratracker.network;

import android.content.Context;
import android.util.Log;

import java.sql.SQLException;

import a3osoft.jiratracker.jira.Worklog;
import a3osoft.jiratracker.database.DatabaseHelper;
import retrofit2.Response;
import rx.Observer;

public class JiraWorklogObserver implements Observer<Response<Void>> {

    private final Worklog worklog;
    private final Context context;

    public JiraWorklogObserver(Context context, Worklog worklog) {
        this.context = context;
        this.worklog = worklog;
    }

    @Override
    public void onCompleted() {
        try {
            Log.d("Worklog", "success");
            DatabaseHelper.getInstance(context).getWorklogDao().delete(worklog);
            Log.d("Worklog", "successfull delete");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Throwable e) {
        Log.e("Worklog", e.getMessage());
    }

    @Override
    public void onNext(final Response<Void> response) {

    }
}
