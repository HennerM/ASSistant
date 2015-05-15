package at.crud.assistant;

import android.app.Fragment;
import android.content.Intent;
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
            List<RecurringAction> recurringActionList = null;
            try {
                recurringActionList = databaseHelper.getRecurringActionDao().queryForAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return recurringActionList;
        }

        public void refreshList() {
            recurringActionAdapter = new RecurringActionAdapter(getRecurringActions(), this);
            recyclerView.setAdapter(recurringActionAdapter);
        }


        @Override
        public void onClick(View v, RecurringAction element) {
            RecurringAction recurringAction = element;
            Intent intent = new Intent(getActivity(), EventsActivity.class);
            intent.setAction(EditActivity.EDIT_ACTION);
            intent.putExtra(EventsActivity.INTENT_EXTRA_RECURRING_ACTION_ID, recurringAction.getId());
            getActivity().startActivityForResult(intent, EventsActivity.VIEW_REQUEST_CODE);
        }
    }

}
