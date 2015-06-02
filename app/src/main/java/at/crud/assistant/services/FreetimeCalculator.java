package at.crud.assistant.services;

import android.provider.CalendarContract;

import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import at.crud.assistant.models.CalendarDay;
import at.crud.assistant.models.Event;
import at.crud.assistant.models.RecurringAction;
import at.crud.assistant.models.RecurringActionSettings;


public class FreetimeCalculator {

    public int getFreeMinutes(Date day, List<Event> eventList) {
        int actualMinutes = 24 * 60;
        Calendar calStart = CalendarUtil.fromDate(day);
        CalendarUtil.setToMidnight(calStart);
        long dayStart = calStart.getTimeInMillis();
        calStart.add(Calendar.DATE, 1);
        long dayEnd = calStart.getTimeInMillis();
        for (Event event : eventList) {
            if (event.getAvailability() == CalendarContract.Events.AVAILABILITY_BUSY) {
                long startTime = Math.max(dayStart, event.getStart().getTime());
                long endTime = Math.min(dayEnd, event.getEnd().getTime());
                long deltaMilliSeconds = endTime -startTime;
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
