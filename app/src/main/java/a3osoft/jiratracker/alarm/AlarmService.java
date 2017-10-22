package a3osoft.jiratracker.alarm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

import a3osoft.jiratracker.database.DatabaseHelper;
import a3osoft.jiratracker.validations.Validation;

public class AlarmService extends Service {

    public static final String VALIDATION_BOUNDLE_KEY = "VALIDATION_BOUNDLE_KEY";

    private Alarm alarm = new Alarm();

    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int validationId = intent.getExtras().getInt(VALIDATION_BOUNDLE_KEY);
        Validation validation = DatabaseHelper.getInstance(this).getValidation(validationId);
        Pair<Integer, Integer> dateFrom = validation.getDateFrom();
        Log.d("Validation", validation.toString());
        alarm.setAlarm(this, dateFrom.first, dateFrom.second, validation.getId());
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
