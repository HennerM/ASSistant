package at.crud.assistant;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import at.crud.assistant.models.RecurringAction;
import at.crud.assistant.utils.BaseViewHolder;
import at.crud.assistant.utils.DatabaseHelper;
import at.crud.assistant.utils.RecurringActionAdapter;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ListFragment(), "list")
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent intent;
        switch (id) {
            case R.id.action_new:
                intent = new Intent(this, EditActivity.class);
                startActivityForResult(intent, EditActivity.NEW_ACTION_REQUEST);
                break;
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EditActivity.NEW_ACTION_REQUEST:
            case EditActivity.EDIT_ACTION_REQUEST:
                ((ListFragment) getFragmentManager().findFragmentByTag("list")).refreshList();
                break;
            case EventsActivity.VIEW_REQUEST_CODE:
                switch (resultCode) {
                    case EditActivity.RESULT_DELETED:
                        ((ListFragment) getFragmentManager().findFragmentByTag("list")).refreshList();
                        break;
                }
        }
    }


    public static class ListFragment extends Fragment implements BaseViewHolder.OnItemClickListener<RecurringAction> {

        private DatabaseHelper databaseHelper;
        private RecurringActionAdapter recurringActionAdapter = null;
        private RecyclerView recyclerView;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            databaseHelper = new DatabaseHelper(getActivity().getApplicationContext());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_list, container, false);
            recyclerView = (RecyclerView) view.findViewById(R.id.rvActionList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            refreshList();
            return view;
        }

        private List<RecurringAction> getRecurringActions() {
            List<RecurringAction> recurringActionList = new LinkedList<>();
            try {
                recurringActionList = databaseHelper.getRecurringActionDao().queryForAll();
                if (recurringActionList.size() > 0) {
                    enableReceiver();
                } else {
                    disableReceiver();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return recurringActionList;
        }

        public void refreshList() {
            List<RecurringAction> recurringActions = getRecurringActions();
            if (recurringActions.size() > 0) {
                recurringActionAdapter = new RecurringActionAdapter(getRecurringActions(), this);
                recyclerView.setAdapter(recurringActionAdapter);
            }
        }


        @Override
        public void onClick(View v, RecurringAction element) {
            Intent intent = new Intent(getActivity(), EventsActivity.class);
            intent.putExtra(EventsActivity.INTENT_EXTRA_RECURRING_ACTION_ID, element.getId());
            getActivity().startActivityForResult(intent, EventsActivity.VIEW_REQUEST_CODE);
        }

        private void enableReceiver() {
            ComponentName receiver = new ComponentName(getActivity(), BootReceiver.class);
            PackageManager pm = getActivity().getPackageManager();
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

            Intent wizardIntent = new Intent(getActivity(), WizardService.class);
            PendingIntent pendingIntent = PendingIntent.getService(getActivity(), BootReceiver.REQUEST_CODE_ALARM, wizardIntent, 0);
            AlarmManager alarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            alarmMgr.cancel(pendingIntent);
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    WizardService.INTERVALL_SECONDS,
                    WizardService.INTERVALL_SECONDS, pendingIntent);
        }

        private void disableReceiver() {
            ComponentName receiver = new ComponentName(getActivity(), BootReceiver.class);
            PackageManager pm = getActivity().getPackageManager();
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            databaseHelper.close();
            recurringActionAdapter = null;
            recyclerView = null;
        }
    }


}
