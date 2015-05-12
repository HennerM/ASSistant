package at.crud.assistant.models;

import java.text.DateFormat;
import java.util.Date;

public class Event {

    protected String title;

    protected Date start;

    protected Date end;

    protected boolean allDay;


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

    @Override
    public String toString() {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();

        return title + " (" + dateFormat.format(start) + " - " + dateFormat.format(end) + ")";
    }
}
