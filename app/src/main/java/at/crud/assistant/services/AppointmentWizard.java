package at.crud.assistant.services;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import at.crud.assistant.R;
import at.crud.assistant.SettingsFragment;
import at.crud.assistant.models.Event;
import at.crud.assistant.models.RecurringAction;
import at.crud.assistant.utils.CalendarRepository;
import at.crud.assistant.utils.DatabaseHelper;
import at.crud.assistant.utils.EventFactory;
import at.crud.assistant.utils.EventRepository;


public class AppointmentWizard {

    // TODO move to Preference
    private static final int OUTLOOK_DAYS = 7;
    private static final int WIZARD_NOTIFICATION = 1;

    private Context context;
    private AppointmentFinder appointmentFinder;
    private DatabaseHelper databaseHelper;
    private EventRepository eventRepository;
    private Dao<Event, String> eventDao;
    private Dao<RecurringAction, Integer> recurringActionDao;

    public AppointmentWizard(Context context) {
        this.context = context;
        ContentResolver contentResolver = context.getContentResolver();
        eventRepository = new EventRepository(contentResolver, new CalendarRepository(contentResolver));
        FreetimeCalculator freetimeCalculator = new FreetimeCalculator();
        appointmentFinder = new AppointmentFinder(getCalendarIds(), eventRepository, freetimeCalculator);
        databaseHelper = new DatabaseHelper(context);
        try {
            eventDao  = databaseHelper.getEventDao();
            recurringActionDao= databaseHelper.getRecurringActionDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected String[] getCalendarIds() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> calendarPreference = sharedPref.getStringSet(SettingsFragment.PREFERENCE_KEY_RELEVANT_CALENDARS, new HashSet<String>(0));
        if (calendarPreference == null) {
            return new String[0];
        } else {
            return calendarPreference.toArray(new String[calendarPreference.size()]);
        }
    }

    protected int createAppointmentsForTimeSpan(RecurringAction recurringAction, Date startDate, Date endDate) {
        List<Event> eventList = appointmentFinder.findPossibleAppointments(recurringAction, startDate, endDate);
        int eventsCreated = 0;
        try {
            for (Event event: eventList) {
                // TODO use prefered calendar
                int calendarId = 1;

                Uri cUri = eventRepository.insert(EventFactory.createContentValueFromEvent(recurringAction.getId(), event, calendarId));
                event.setUri(cUri);
                event.setRecurringAction(recurringAction);
                eventDao.createOrUpdate(event);
                eventsCreated++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return eventsCreated;

    }

    protected void removeOldAppointments(RecurringAction recurringAction) {
        Collection<Event> events = recurringAction.getEvents();
        for (Event event: events) {
            eventRepository.deleteEvent(event.getUri());
            recurringAction.removeEvent(event);
            event.setRecurringAction(null);
            try {
                eventDao.delete(event);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            recurringActionDao.update(recurringAction);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void recreateAppointments(List<RecurringAction> actions) {
        Date startDate = getStartDate();
        Date endDate = getEndDate(startDate, OUTLOOK_DAYS);
        int nrOfAppointments = 0;
        for (RecurringAction recurringAction : actions) {
            removeOldAppointments(recurringAction);
            nrOfAppointments += createAppointmentsForTimeSpan(recurringAction, startDate, endDate);
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Termine erstellt")
                        .setContentText("es wurden " + nrOfAppointments + " neue Termine erstellt!");

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(WIZARD_NOTIFICATION, mBuilder.build());

    }

    public void refreshAllActions() {
        try {
            List<RecurringAction> actions = getActiveActions();
            recreateAppointments(actions);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void refreshAction(RecurringAction action) {
        List<RecurringAction> actionList = new LinkedList<>();
        actionList.add(action);
        recreateAppointments(actionList);
    }

    public List<Event> getEventsNextWeekForAction(RecurringAction recAction) {
        Date startDate = getStartDate();
        return appointmentFinder.findPossibleAppointments(recAction,startDate , getEndDate(startDate, OUTLOOK_DAYS));
    }

    private Date getStartDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);
        CalendarUtil.setToMidnight(c);
        return new Date(c.getTimeInMillis());
    }

    private Date getEndDate(Date startDate, int outlookDays) {
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.add(Calendar.DATE, outlookDays);
        return new Date(c.getTimeInMillis());
    }

    private List<RecurringAction> getActiveActions() throws SQLException {
        QueryBuilder<RecurringAction, Integer> queryBuilder = recurringActionDao.queryBuilder();

        queryBuilder.where().le("firstDay", getStartDate());
        return recurringActionDao.query(queryBuilder.prepare());
    }
}
