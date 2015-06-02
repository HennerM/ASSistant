package at.crud.assistant.services;

import android.provider.CalendarContract;

import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.List;

import at.crud.assistant.models.CalendarDay;
import at.crud.assistant.models.Event;
import at.crud.assistant.models.RecurringAction;
import at.crud.assistant.models.RecurringActionSettings;


public class FreetimeCalculator {

    public int getFreeMinutes(List<Event> eventList) {
        int actualMinutes = 24 * 60;
        for (Event event : eventList) {
            if (event.isAllDay() && event.getAvailability() == CalendarContract.Events.AVAILABILITY_BUSY) {
                return 0;
            }
            if (event.getAvailability() == CalendarContract.Events.AVAILABILITY_BUSY) {
                long deltaMilliSeconds = event.getEnd().getTime() - event.getStart().getTime();
                int deltaMinutes = (int) (deltaMilliSeconds / 1000 / 60);
                actualMinutes = actualMinutes - deltaMinutes;
            }
        }
        return actualMinutes;
    }

    public Calendar searchForSpace(RecurringActionSettings settings, CalendarDay calDay, int spaceMinutes) {
        if (spaceMinutes <= 0) {
            throw new InvalidParameterException("Space duration can't be <= 0");
        }

        Calendar calendarIterator = CalendarUtil.fromDate(calDay.getDate());
        CalendarUtil.setToMidnight(calendarIterator);
        calendarIterator.add(Calendar.MINUTE, settings.getCasualDayStartTime());

        Calendar dayEnd = CalendarUtil.fromDate(calDay.getDate());
        CalendarUtil.setToMidnight(dayEnd);
        dayEnd.add(Calendar.MINUTE, settings.getCasualDayEndTime());

        while (calendarIterator.before(dayEnd)) {
            Calendar calendarIteratorEnd = (Calendar) calendarIterator.clone();
            calendarIteratorEnd.add(Calendar.MINUTE, spaceMinutes);

            boolean overlapping = false;
            if (calDay.getEventList().size() > 0) {
                for (Event event : calDay.getEventList()) {
                    if (event.getAvailability() == CalendarContract.Events.AVAILABILITY_BUSY) {
                        overlapping = overlapping || CalendarUtil.overlapping(calendarIterator, calendarIteratorEnd,
                                CalendarUtil.fromDate(event.getStart()), CalendarUtil.fromDate(event.getEnd()));
                    }
                }
            }

            if (!overlapping) {
                return calendarIterator;
            }

            calendarIterator.add(Calendar.MINUTE, spaceMinutes);
        }
        return null;

    }

}
