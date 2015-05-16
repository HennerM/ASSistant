package at.crud.assistant;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import at.crud.assistant.models.RecurringAction;
import at.crud.assistant.services.AppointmentWizard;
import at.crud.assistant.utils.DatabaseHelper;


public class EditActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener {

    public static final String EXTRA_RECURRINGACTION = "recurringAction";
    public static final int NEW_ACTION_REQUEST = 1;
    public static final int EDIT_ACTION_REQUEST = 2;
    public static final String NEW_ACTION = "new_action";
    public static final String EDIT_ACTION = "edit_action";
    public static final int RESULT_DELETED = 4;

    protected TextView dateTextView;
    protected EditText etTitle;
    protected EditText etHours;
    protected RecurringAction recurringAction = null;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(this);
        recurringAction = getRecurringAction();
        setContentView(R.layout.activity_edit);

        dateTextView = (TextView)findViewById(R.id.tvFirstDay);
        etTitle = (EditText)findViewById(R.id.etTitle);
        etHours = (EditText)findViewById(R.id.etHours);

        if (recurringAction != null) {
            bindToView();
        }

    }

    private RecurringAction getRecurringAction() {
        Bundle extras = getIntent().getExtras();
        RecurringAction recAction;
        if (getIntent().getAction() != null && getIntent().getAction().equals(EDIT_ACTION)) {
            int raId = extras.getInt(EXTRA_RECURRINGACTION);
            try {
                recAction = databaseHelper.getRecurringActionDao().queryForId(raId);
                setTitle(recAction.getTitle());
            } catch (SQLException e) {
                recAction = new RecurringAction();
            }
        } else {
            recAction = new RecurringAction();
        }
        return recAction;
    }

    private void bindToView() {
        dateTextView.setText(DateFormat.getDateFormat(this).format(recurringAction.getFirstDay()));
        dateTextView.setOnClickListener(showDatePickerDialog);
        etTitle.setText(recurringAction.getTitle());
        etHours.setText(Integer.toString(Math.round(recurringAction.getSettings().getHoursPerWeek())));
    }

    private View.OnClickListener showDatePickerDialog = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DialogFragment fragment = new DatePickerFragment();
            Bundle arguments = new Bundle();
            arguments.putLong("date", recurringAction.getFirstDay().getTime());
            fragment.setArguments(arguments);
            fragment.show(getSupportFragmentManager(), "datePicker");
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_done:
                if (validateAndSave()) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Log.d(EditActivity.class.toString(), "Validierungsfehler");
                }
                break;
            case R.id.action_delete:
                delete();
                setResult(RESULT_DELETED);
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private boolean validateAndSave() {
        try {
            recurringAction.setTitle(etTitle.getText().toString());
            recurringAction.getSettings().setHoursPerWeek(Float.parseFloat(etHours.getText().toString()));
            databaseHelper.getRecurringActionDao().createOrUpdate(recurringAction);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    AppointmentWizard ap = new AppointmentWizard(EditActivity.this);
                    ap.refreshAction(recurringAction);
                }
            }).run();

            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    private void delete() {
        try {
            databaseHelper.getRecurringActionDao().delete(recurringAction);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static class DatePickerFragment extends DialogFragment {

        public DatePickerFragment() {
            super();
        }


        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            Date date = new Date((long)getArguments().get("date"));
            final Calendar c = Calendar.getInstance();
            c.setTime(date);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day= c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of TimePickerDialog and return it
            return new DatePickerDialog(getActivity(), (EditActivity)getActivity(), year, month, day);
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        recurringAction.setFirstDay(c.getTime());

        dateTextView.setText(DateFormat.getDateFormat(this).format(recurringAction.getFirstDay()));
    }

    @Override
    protected void onStop() {
        super.onStop();
        recurringAction = null;
        databaseHelper.close();
        databaseHelper = null;
        dateTextView = null;
        etHours = null;
        etTitle = null;
    }
}
