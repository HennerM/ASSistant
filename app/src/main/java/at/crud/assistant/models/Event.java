package at.crud.assistant.models;

import android.net.Uri;

import java.text.DateFormat;
import java.util.Date;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

public class Event {

    @DatabaseField( id = true)
    protected String uri;

    protected String title;

    protected Date start;

    protected Date end;

    protected boolean allDay;

    @DatabaseField( foreign = true )
    private RecurringAction recurringAction;

    public Uri getUri() {
        return Uri.parse(uri);
    }

    public void setUri(Uri uri) {
        this.uri = uri.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public RecurringAction getRecurringAction() {
        return recurringAction;
    }

    public void setRecurringAction(RecurringAction recurringAction) {
        this.recurringAction = recurringAction;
    }

    @Override
    public String toString() {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();

        return title + " (" + dateFormat.format(start) + " - " + dateFormat.format(end) + ")";
    }
}
