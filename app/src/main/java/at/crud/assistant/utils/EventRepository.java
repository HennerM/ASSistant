package at.crud.assistant.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import at.crud.assistant.models.Event;
import at.crud.assistant.services.CalendarUtil;


public class EventRepository {


    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.ALL_DAY,
            CalendarContract.Events.AVAILABILITY
    };

    public static final int PROJECTION_ID_INDEX = 0;
    public static final int PROJECTION_TITLE = 1;
    public static final int PROJECTION_START = 2;
    public static final int PROJECTION_END = 3;
    public static final int PROJECTION_ALL_DAY = 4;
    public static final int PROJECTION_AVAILABILITY = 5;

    private static final Uri CONTENT_URI = CalendarContract.Events.CONTENT_URI;


    private ContentResolver cr;
    private CalendarRepository calendarRepository;

    public EventRepository(ContentResolver contentResolver, CalendarRepository calendarRepository) {
        this.cr = contentResolver;
        this.calendarRepository = calendarRepository;
    }

    public List<Event> findAllEventsForCalendars(String[] ids) {
        StringBuilder selection = new StringBuilder(CalendarContract.Events.CALENDAR_ID+ " IN (");
        for (int i = 0; i < ids.length; i++) {
            selection.append("?");
            if (i < ids.length-1) {
                selection.append(",");
            }
        }
        selection.append(")");

        Cursor eventCursor = cr.query(CONTENT_URI, EVENT_PROJECTION, selection.toString(), ids, null);
        return getListFromCursor(eventCursor);
    }

    public List<Event> findEventsForDay(String[] calIds, Calendar day) {
        CalendarUtil.setToMidnight(day);
        Date dayStart = new Date(day.getTimeInMillis());
        CalendarUtil.setToLastSecond(day);
        Date dayEnd = new Date(day.getTimeInMillis());
        return findEventsInCalendarsAndTimespan(calIds, dayStart, dayEnd);
    }

    public List<Event> findEventsInCalendarsAndTimespan(String[] ids, Date startDate, Date endDate) {
        StringBuilder selection = new StringBuilder(CalendarContract.Events.CALENDAR_ID+ " IN ("+ StringUtils.join(ids,",")+ ")");

       selection.append(" AND (");
        selection.append("(" +CalendarContract.Events.DTSTART + "<= ?1 AND " + CalendarContract.Events.DTEND + " >= ?1 )");
        selection.append(" OR ");
        selection.append("(" +CalendarContract.Events.DTSTART + ">= ?1 AND " + CalendarContract.Events.DTEND + " <= ?2 )");
        selection.append(" OR ");
        selection.append("(" +CalendarContract.Events.DTSTART + "<= ?2 AND " + CalendarContract.Events.DTEND + " >= ?2 )");
        selection.append(")");
        ArrayList<String> arguments = new ArrayList<String>(2);
        arguments.add(String.valueOf(startDate.getTime()));
        arguments.add(String.valueOf(endDate.getTime()));

        Cursor eventCursor = cr.query(CONTENT_URI, EVENT_PROJECTION, selection.toString(), arguments.toArray(new String[arguments.size()]), null);
        return getListFromCursor(eventCursor);
    }

    private List<Event> getListFromCursor(Cursor cursor) {
        List<Event> eventList;
        if (cursor.getCount() > 0) {
            eventList = new ArrayList<>();
            while (cursor.moveToNext()) {
                Event event = EventFactory.createFromCursor(cursor);
                eventList.add(event);
            }
        } else {
            eventList = new ArrayList<>();
        }
        cursor.close();
        return eventList;
    }

    public Uri insert(ContentValues eventValues) {
        return cr.insert(CalendarContract.Events.CONTENT_URI, eventValues);
    }

    public void deleteEvent(Uri uri) {
        Log.d("EventRepository", uri.toString());
        cr.delete(uri, null, null);
    }


}
