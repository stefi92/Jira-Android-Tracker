package a3osoft.jiratracker.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import a3osoft.jiratracker.database.DatabaseHelper;
import a3osoft.jiratracker.validations.Validation;

public class Alarm extends BroadcastReceiver {

    private static final String TAG = "Alarm";
    private static final int REQUEST_CODE = 145632;
    private static final String MESSAGE = "MESSAGE";

    @Override
    public void onReceive(Context context, Intent intent) {
//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
//        wl.acquire();
//
//        // Put here YOUR code.
//        Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show();

        Validation validation = DatabaseHelper.getInstance(context)
                .getValidation(intent.getExtras().getInt(MESSAGE));
        Log.e("Alarm", String.valueOf(validation.getMessage()));

        //wl.release();
    }

    public void setAlarm(Context context, int hour, int minute, int messageId) {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        i.putExtra(MESSAGE, messageId);
        PendingIntent pi = PendingIntent.getBroadcast(context, REQUEST_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        Log.e(TAG, String.valueOf(hour) + ":" + String.valueOf(minute));
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 5, pi);
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}