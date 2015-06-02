package at.crud.assistant.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class RecurringActionSettings {

    @DatabaseField(generatedId = true)
    private int id;

    // TODO make customizeable settings

    private int dayIntervall = 1;

    // TODO make global settings
    private int casualDayStartTime = 480;
    private int casualDayEndTime = 1200;

    @DatabaseField
    private int minimalDurationMinutes = 15;

    @DatabaseField
    private int maximalDurationMinutes;

    @DatabaseField
    private float hoursPerWeek;

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

    public int getMaximalDurationMinutes() {
        return maximalDurationMinutes;
    }

    public void setMaximalDurationMinutes(int maximalDurationMinutes) {
        this.maximalDurationMinutes = maximalDurationMinutes;
    }

    public float getHoursPerWeek() {
        return hoursPerWeek;
    }

    public void setHoursPerWeek(float hoursPerWeek) {
        this.hoursPerWeek = hoursPerWeek;
    }
}
