package at.crud.assistant.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import at.crud.assistant.models.CalendarDay;
import at.crud.assistant.models.Event;
import at.crud.assistant.models.RecurringAction;
import at.crud.assistant.models.RecurringActionSettings;
import at.crud.assistant.utils.EventFactory;
import at.crud.assistant.utils.EventRepository;


public class AppointmentFinder {


    protected EventRepository eventRepository;
    protected FreetimeCalculator freetimeCalculator;
    protected String[] calendarIds;

    public AppointmentFinder(String[] calendarIds, EventRepository eventRepository, FreetimeCalculator freetimeCalculator) {
        this.eventRepository = eventRepository;
        this.freetimeCalculator = freetimeCalculator;
        this.calendarIds = calendarIds;
    }


    public List<Event> findPossibleAppointments(RecurringAction recurringAction, Date startDate, Date endDate) {
        List<CalendarDay> availableDays = getAvailableDays(recurringAction, startDate, endDate);
        return makeAppointments(availableDays, recurringAction);
    }

    protected List<CalendarDay> getAvailableDays(RecurringAction recurringAction, Date startDate, Date endDate) {
        if (recurringAction.getFirstDay() != null && recurringAction.getFirstDay().after(startDate)) {
            startDate = recurringAction.getFirstDay();
        }
        if (recurringAction.getLastDay() != null && recurringAction.getLastDay().before(endDate)) {
            endDate = recurringAction.getLastDay();
        }
        Calendar cIterator = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(endDate);
        List<CalendarDay> dayList = new ArrayList<>();
        long availableMinutesOverall = 0;
        for (cIterator.setTime(startDate); cIterator.before(calendarEnd); cIterator.add(Calendar.DATE, recurringAction.getSettings().getDayIntervall())) {

            CalendarDay cDay = new CalendarDay();
            cDay.setDate(new Date(cIterator.getTimeInMillis()));
            List<Event> eventList = eventRepository.findEventsForDay(calendarIds, cIterator);
            cDay.setEventList(eventList);
            int minutes = freetimeCalculator.getFreeMinutes(cDay.getDate(), eventList);
            if (minutes > 0) {
                availableMinutesOverall += minutes;
                cDay.setMinutesAvailable(minutes);
                dayList.add(cDay);
            }
        }

        for (CalendarDay calDay : dayList) {
            float percentage = ((float) calDay.getMinutesAvailable()) / (float) availableMinutesOverall;
            calDay.setPercentageAvailable(percentage);
        }

        Collections.sort(dayList);
        return dayList;
    }

    protected List<Event> makeAppointments(List<CalendarDay> availableDays, RecurringAction recurringAction) {
        RecurringActionSettings settings = recurringAction.getSettings();
        int overallPensumInMinutes = Math.round(settings.getHoursPerWeek() * 60);
        int optimumDuration = Math.round((settings.getMinimalDurationMinutes() + settings.getMaximalDurationMinutes()) / 20) * 10;
        int optimumCount = Math.round(overallPensumInMinutes / optimumDuration);
        float eventsPerDay = (float)optimumCount / availableDays.size();
        float eventCountForNextDay = eventsPerDay + 1.0f;

        List<Event> eventList = new ArrayList<>();
        for (CalendarDay day : availableDays) {
            int actionDuration = Math.min(optimumDuration, overallPensumInMinutes);

            while (overallPensumInMinutes >settings.getMinimalDurationMinutes() && eventCountForNextDay > 1.0f) {
                Calendar calendarSpace = freetimeCalculator.searchForSpace(day, actionDuration);
                if (calendarSpace != null) {
                    Event event = EventFactory.createEvent(calendarSpace, actionDuration, recurringAction.getTitle());
                    eventList.add(event);
                    day.getEventList().add(event);
                    overallPensumInMinutes -= actionDuration;
                    eventCountForNextDay -= 1.0f;
                }
            }
            eventCountForNextDay = eventCountForNextDay + eventsPerDay;

        }

        return eventList;
    }

}
