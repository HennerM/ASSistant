package at.crud.assistant.services;

import java.util.Calendar;
import java.util.Date;


public class CalendarUtil {

    public static void setToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static void setToLastSecond(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 59);
    }

    public static boolean overlapping(Calendar start, Calendar end, Calendar compareStart, Calendar compareEnd) {
        return (compareStart.before(start) && compareEnd.after(start)) ||
                (compareStart.after(start) && compareEnd.before(end)) ||
                (compareStart.before(end) && compareEnd.after(end));
    }

    public static Calendar fromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static long getMilliSecondsSinceMidnight(Calendar calendar) {
        Calendar midnight = (Calendar)calendar.clone();
        setToMidnight(midnight);
        return calendar.getTimeInMillis() - midnight.getTimeInMillis();
    }

}
