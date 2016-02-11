package at.crud.assistant;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.logging.Logger;

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
            Log.d(this.getClass().toString(), "BOOT_completed received");
            AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent wizardIntent = new Intent(context, WizardService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, REQUEST_CODE_ALARM, wizardIntent, 0);
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    WizardService.INTERVALL_SECONDS,
                    WizardService.INTERVALL_SECONDS, pendingIntent);
            context.startService(wizardIntent);
        }
    }
}
