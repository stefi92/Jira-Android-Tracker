package a3osoft.jiratracker.alarm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AlarmService extends Service {

    Alarm alarm = new Alarm();

    public void onCreate() {
        super.onCreate();
        Log.e("alarm", "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("alarm", "onStartCommand()");
        alarm.setAlarm(getApplicationContext());
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
