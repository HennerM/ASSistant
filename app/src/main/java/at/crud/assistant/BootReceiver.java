package at.crud.assistant;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * ************************************************************
 * Copyright notice
 * <p/>
 * © 2015 Markus Hennerbichler (seam media group)
 * All rights reserved
 * <p/>
 * *************************************************************
 */
public class BootReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE_ALARM = 11;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_completed")) {
            AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent receiverIntent = new Intent(context, BootReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM, receiverIntent, 0);
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    AlarmManager.INTERVAL_DAY,
                    AlarmManager.INTERVAL_DAY, alarmIntent);
        }

        Intent wizardIntent = new Intent(context, WizardService.class);
        context.startService(wizardIntent);
    }
}
