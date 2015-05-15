package at.crud.assistant.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class RecurringAction {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String title;

    @DatabaseField
    private Date createdDate;

    @DatabaseField
    private Date firstDay;

    @DatabaseField
    private Date lastDay;

    @DatabaseField
    private float hoursPerWeek;        // TODO hoursPerWeek refactoren

    @ForeignCollectionField
    private Collection<Event> events;

    private Settings settings = new Settings();

    public RecurringAction() {
        super();
        firstDay = new Date();
    }

    public RecurringAction(String title, Date firstDay, float hoursPerWeek) {
        this.title = title;
        this.firstDay = firstDay;
        this.hoursPerWeek = hoursPerWeek;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getFirstDay() {
        return firstDay;
    }

    public void setFirstDay(Date firstDay) {
        this.firstDay = firstDay;
    }

    public Date getLastDay() {
        return lastDay;
    }

    public void setLastDay(Date lastDay) {
        this.lastDay = lastDay;
    }

    public float getHoursPerWeek() {
        return hoursPerWeek;
    }

    public void setHoursPerWeek(float hoursPerWeek) {
        this.hoursPerWeek = hoursPerWeek;
    }

    public int getId() {
        return id;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public static class Settings {

        // TODO make customizeable settings

        private int dayIntervall = 1;
        private int casualDayStartTime = 480;
        private int casualDayEndTime = 1200;
        private int minimalDurationMinutes = 15;

        public int getDayIntervall() {
            return dayIntervall;
        }

        public void setDayIntervall(int dayIntervall) {
            this.dayIntervall = dayIntervall;
        }

        public int getCasualDayStartTime() {
            return casualDayStartTime;
        }

        public void setCasualDayStartTime(int casualDayStartTime) {
            this.casualDayStartTime = casualDayStartTime;
        }

        public int getCasualDayEndTime() {
            return casualDayEndTime;
        }

        public void setCasualDayEndTime(int casualDayEndTime) {
            this.casualDayEndTime = casualDayEndTime;
        }

        public int getMinimalDurationMinutes() {
            return minimalDurationMinutes;
        }

        public void setMinimalDurationMinutes(int minimalDurationMinutes) {
            this.minimalDurationMinutes = minimalDurationMinutes;
        }
    }
}
