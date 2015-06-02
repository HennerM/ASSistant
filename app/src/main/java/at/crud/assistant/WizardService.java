package at.crud.assistant;

import android.app.AlarmManager;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import at.crud.assistant.services.AppointmentWizard;


public class WizardService extends IntentService {

    public static final long INTERVALL_SECONDS = AlarmManager.INTERVAL_HALF_DAY;

    public WizardService() {
        super("WizardService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppointmentWizard wizard = new AppointmentWizard(this);
        Log.d(WizardService.class.toString(), "wizard Service started..");
        wizard.refreshAllActions();
        stopSelf();
    }
}
