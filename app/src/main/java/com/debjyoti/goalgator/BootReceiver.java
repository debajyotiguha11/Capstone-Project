package com.debjyoti.goalgator;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Broadcast receiver to catch boot events and set alarm for the GoalAlarm intent service
 */
public class BootReceiver extends BroadcastReceiver{
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //Set Alarm for database incrementation and notifications
            alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent pendIntent = new Intent(context, AlarmReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(context, 0, pendIntent, 0);

            // Set the alarm to start at midnight
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 0);

            // set the alarm to repeat daily
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent);
        }
    }


}