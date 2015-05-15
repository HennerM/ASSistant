package at.crud.assistant;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.List;

import at.crud.assistant.models.Event;
import at.crud.assistant.models.RecurringAction;
import at.crud.assistant.services.AppointmentWizard;
import at.crud.assistant.utils.DatabaseHelper;

public class EventsActivity extends ActionBarActivity {

    public static final int VIEW_REQUEST_CODE = 5;
    public static final String INTENT_EXTRA_RECURRING_ACTION_ID = "recurringActionId";
    private static final String LIST_FRAGMENT_TAG = "list";

    private int recurringActionId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            recurringActionId = getIntent().getIntExtra(INTENT_EXTRA_RECURRING_ACTION_ID, 0);
            EventlistFragment eventlistFragment = new EventlistFragment();
            eventlistFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction()
                    .add(R.id.container, eventlistFragment, LIST_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.action_edit:
                if (recurringActionId != 0) {
                    intent = new Intent(this, EditActivity.class);
                    intent.setAction(EditActivity.EDIT_ACTION);
                    intent.putExtra(EditActivity.EXTRA_RECURRINGACTION, recurringActionId);
                    startActivityForResult(intent, EditActivity.EDIT_ACTION_REQUEST);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EditActivity.EDIT_ACTION_REQUEST:
                switch (resultCode) {
                    case EditActivity.RESULT_DELETED:
                        setResult(EditActivity.RESULT_DELETED);
                        finish();
                        break;
                    case RESULT_OK:
                        ((EventlistFragment)getFragmentManager().findFragmentByTag(LIST_FRAGMENT_TAG)).refresh();
                }
        }
    }

    public static class EventlistFragment extends Fragment {

        private ListView lvEvents;
        protected RecurringAction recurringAction;
        protected DatabaseHelper databaseHelper;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle arguments = getArguments();
            if (arguments == null) {
                return;
            }

            int recurringActionId = arguments.getInt(EventsActivity.INTENT_EXTRA_RECURRING_ACTION_ID, 0);
            if (recurringActionId == 0) {
                throw new InvalidParameterException("required RecurringAction ID not available");
            } else {
                databaseHelper = new DatabaseHelper(getActivity());
                try {
                    recurringAction = databaseHelper.getRecurringActionDao().queryForId(recurringActionId);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            if (((ActionBarActivity) getActivity()).getSupportActionBar() != null) {
                ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(recurringAction.getTitle());
            }

            View view = inflater.inflate(R.layout.fragment_eventlist, container, false);
            lvEvents = (ListView) view.findViewById(R.id.lvEvents);

            new LoadEventsBackgroundTask().execute(recurringAction);
            return view;
        }

        public void refresh() {
            try {
                recurringAction = databaseHelper.getRecurringActionDao().queryForId(recurringAction.getId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            new LoadEventsBackgroundTask().execute(recurringAction);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            databaseHelper.close();
            lvEvents = null;
            recurringAction = null;
        }


        private class LoadEventsBackgroundTask extends AsyncTask<RecurringAction, Void, List<Event>> {

            @Override
            protected List<Event> doInBackground(RecurringAction[] params) {
                AppointmentWizard appointmentWizard = new AppointmentWizard(getActivity());
                return appointmentWizard.getEventsNextWeekForAction(params[0]);
            }

            @Override
            protected void onPostExecute(List<Event> resultList) {
                EventAdapter et = new EventAdapter(getActivity(), android.R.layout.simple_list_item_1, resultList);
                lvEvents.setAdapter(et);
            }
        }

    }

    public static class EventAdapter extends ArrayAdapter<Event> {

        public EventAdapter(Context context, int resource, List<Event> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
            Event event = getItem(position);
            textView.setText(event.toString());

            return convertView;
        }
    }


}
