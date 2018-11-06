package com.debjyoti.goalgator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast Receiver to catch Intents from alarm manager and start the GoalAlarm intent service.
 */
public class AlarmReceiver  extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            context.startService(new Intent(context, GoalAlarm.class));
        }

}
