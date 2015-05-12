package at.crud.assistant;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class RecurringActionViewActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new RecurringActionViewFragment(),"list")
                    .commit();
        }
    }
}
