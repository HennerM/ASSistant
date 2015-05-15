package at.crud.assistant;

import android.app.AlarmManager;
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

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_completed")) {
            AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        }
    }
}
