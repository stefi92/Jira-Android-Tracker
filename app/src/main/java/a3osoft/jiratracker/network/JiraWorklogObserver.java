package a3osoft.jiratracker.network;

import android.util.Log;

import retrofit2.Response;
import rx.Observer;

public class JiraWorklogObserver implements Observer<Response<Void>> {

    private final UploadListener listener;

    public JiraWorklogObserver(UploadListener listener){
        this.listener = listener;
    }

    @Override
    public void onCompleted() {
        Log.d("Worklog", "succ");
        listener.successfulUpload();
    }

    @Override
    public void onError(Throwable e) {
        Log.e("Worklog", e.getMessage());
    }

    @Override
    public void onNext(Response<Void> observable) {
        Log.d("Worklog", String.valueOf(observable.code()));
    }

}
