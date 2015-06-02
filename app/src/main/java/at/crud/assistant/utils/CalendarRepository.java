package at.crud.assistant.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

/**
 * Created by Markus on 23.02.2015.
 */
public class CalendarRepository {

    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.OWNER_ACCOUNT,
            CalendarContract.Calendars.ACCOUNT_TYPE
    };
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
    private static final int PROJECTION_ACCOUNT_TYPE_INDEX = 4;

    private ContentResolver contentResolver;
    private static Cursor calendarCursor = null;

    public CalendarRepository(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }


    private Cursor getCalendarCursor() {
        if (calendarCursor == null) {
            Uri uri = CalendarContract.Calendars.CONTENT_URI;
            calendarCursor= contentResolver.query(uri, EVENT_PROJECTION, null, null, null);
        }
        calendarCursor.moveToFirst();
        return calendarCursor;
    }

    public String[] getAvailableCalendarNames() {
        Cursor cursor = getCalendarCursor();
        String[] calendars;
        if (cursor.getCount() > 0) {
            calendars = new String[cursor.getCount()];
            int i = 0;
            do {
                calendars[i] = cursor.getString(PROJECTION_DISPLAY_NAME_INDEX);
                i++;
            } while (cursor.moveToNext());
        } else {
            calendars = new String[0];
        }
        cursor.close();
        calendarCursor = null;
        return calendars;
    }

    public String[] getAvailableCalendarIds(){
        Cursor cursor = getCalendarCursor();
        String[] calendarIds;
        if (cursor.getCount() > 0) {
             calendarIds = new String[cursor.getCount()];
            int i = 0;
            do {
                String calendarId = Long.toString(cursor.getLong(PROJECTION_ID_INDEX));
                calendarIds[i] = calendarId;
                i++;
            } while (cursor.moveToNext());
        } else {
            calendarIds = new String[0];
        }
        cursor.close();
        calendarCursor = null;
        return calendarIds;
    }

}
