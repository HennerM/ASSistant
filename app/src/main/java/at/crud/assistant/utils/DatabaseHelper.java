package at.crud.assistant.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import at.crud.assistant.models.Event;
import at.crud.assistant.models.RecurringAction;
import at.crud.assistant.models.RecurringActionSettings;

/**
 * Created by Markus on 22.02.2015.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "assistant.db";
    private static final int DATABASE_VERSION = 3;
    private Dao<RecurringAction, Integer> recurringActionDao = null;
    private Dao<Event, String> eventDao = null;
    private Dao<RecurringActionSettings, Integer> settingsDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, RecurringAction.class);
            TableUtils.createTable(connectionSource, Event.class);
            TableUtils.createTable(connectionSource, RecurringActionSettings.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, RecurringAction.class, true);
            TableUtils.dropTable(connectionSource, Event.class, true);
            TableUtils.dropTable(connectionSource, RecurringActionSettings.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<RecurringAction, Integer> getRecurringActionDao() throws SQLException{
        if (recurringActionDao == null) {
            recurringActionDao = getDao(RecurringAction.class);
        }
        return recurringActionDao;
    }

    @Override
    public void close() {
        super.close();
        recurringActionDao = null;
        eventDao = null;
    }

    public Dao<RecurringActionSettings, Integer> getSettingsDao() throws SQLException{
        if (settingsDao == null) {
            settingsDao = getDao(RecurringActionSettings.class);
        }
        return settingsDao;
    }

    public Dao<Event, String> getEventDao() throws SQLException{
        if (eventDao == null) {
            eventDao = getDao(Event.class);
        }
        return eventDao;
    }
}
