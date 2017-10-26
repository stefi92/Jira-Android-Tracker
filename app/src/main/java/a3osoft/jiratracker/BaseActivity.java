package a3osoft.jiratracker;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class BaseActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 142536;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected void showProgressDialog(Context context){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    protected void hideProgressDialog(){
        progressDialog.dismiss();
    }

    private void createValidationFile() throws IOException {
        File validations = new File(Environment.getExternalStorageDirectory().getPath() + "/validations.xml");
        validations.createNewFile();
        Log.e("test", "file was created!");
    }

    protected boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void checkOrCreateValidationFile() throws IOException {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }
        else {
            File validations = new File(Environment.getExternalStorageDirectory().getPath() + "/validations.xml");
            Log.e("test", String.valueOf(validations.exists()));
            if (!validations.exists()) {
                validations.createNewFile();
                Log.e("test", "file was created!");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    try {
                        createValidationFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
