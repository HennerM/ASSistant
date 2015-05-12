package at.crud.assistant.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Markus on 03.03.2015.
 */
public class CalendarDay implements Comparable<CalendarDay> {

    protected Date date;

    protected int minutesAvailable = 0;

    protected float percentageAvailable;

    protected List<Event> eventList = new ArrayList<>(0);

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getMinutesAvailable() {
        return minutesAvailable;
    }

    public void setMinutesAvailable(int minutesAvailable) {
        this.minutesAvailable = minutesAvailable;
    }

    public float getPercentageAvailable() {
        return percentageAvailable;
    }

    public void setPercentageAvailable(float percentageAvailable) {
        this.percentageAvailable = percentageAvailable;
    }

    @Override
    public int compareTo(CalendarDay another) {
        return getDate().compareTo(another.getDate());
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }
}
