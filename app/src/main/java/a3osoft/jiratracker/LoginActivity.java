package a3osoft.jiratracker;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import a3osoft.jiratracker.database.DatabaseHelper;
import a3osoft.jiratracker.jira.Issue;
import a3osoft.jiratracker.jira.JiraResponse;
import a3osoft.jiratracker.network.JiraIssueObserver;
import a3osoft.jiratracker.network.RestClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Credentials;
import rx.schedulers.Schedulers;

public class LoginActivity extends BaseActivity implements View.OnClickListener, JiraResponse<Issue>{

    @BindView(R.id.login_username) EditText userName;
    @BindView(R.id.login_password) EditText password;
    @BindView(R.id.login_login_button) Button loginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!DatabaseHelper.getInstance(this).getAuthToken().equals("")){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        loginButton.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        String user = userName.getText().toString();
        String pass = password.getText().toString();
        if(!user.equals("") && !pass.equals("")){
            showProgressDialog(this);
            RestClient.getInstance()
                    .getIssues(AppConfig.MY_ISSUES, Credentials.basic(user, pass))
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .subscribe(new JiraIssueObserver(this));
        }
    }

    private void storeTokenAndLogin() {
        String user = userName.getText().toString();
        String pass = password.getText().toString();
        String token = Credentials.basic(user, pass);
        DatabaseHelper.getInstance(this).setAuthToken(token);
        hideProgressDialog();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onError(Throwable e) {
        Log.e("Login", String.valueOf(e));
        hideProgressDialog();
    }

    @Override
    public void onSuccess(List<Issue> objects) {
        storeTokenAndLogin();
    }
}
