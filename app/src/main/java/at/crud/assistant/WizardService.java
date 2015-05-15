package at.crud.assistant;

import android.app.IntentService;
import android.content.Intent;

import at.crud.assistant.services.AppointmentWizard;


public class WizardService extends IntentService {


    public WizardService() {
        super("WizardService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppointmentWizard wizard = new AppointmentWizard(this);
        wizard.refreshAllActions();
        stopSelf();
    }
}
