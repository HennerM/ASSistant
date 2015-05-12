package at.crud.assistant.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.Calendar;

import at.crud.assistant.models.Event;

/**
 * Created by Markus on 03.03.2015.
 */
public class EventFactory {

    public static Event createEvent(Calendar start, int durationMinutes, String title) {
        Event event = new Event();
        event.setStart(start.getTime());
        start.add(Calendar.MINUTE, durationMinutes);
        event.setEnd(start.getTime());
        event.setTitle(title);
        event.setAllDay(false);
        return event;
    }

    public static ContentValues createContentValueFromEvent(int recurringActionId, Event event, int calendarId) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, event.getStart().getTime());
        values.put(CalendarContract.Events.DTEND, event.getEnd().getTime());
        values.put(CalendarContract.Events.TITLE, event.getTitle());
        values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Vienna");
        values.put(CalendarContract.Events.CUSTOM_APP_PACKAGE, "at.crud.assistant");
        Uri uri = ContentUris.withAppendedId(Uri.parse("content://at.crud.assistant/events"), recurringActionId);
        values.put(CalendarContract.Events.CUSTOM_APP_URI, uri.toString());
        values.put(CalendarContract.Events.DESCRIPTION, "auto generated reccuring event");
        return values;
    }

}
