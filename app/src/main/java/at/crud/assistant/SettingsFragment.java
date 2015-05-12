package at.crud.assistant;

import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceFragment;

import at.crud.assistant.utils.CalendarRepository;

public class SettingsFragment extends PreferenceFragment {

    public static final String PREFERENCE_KEY_RELEVANT_CALENDARS = "pref_relevant_calendars";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalendarRepository calendarRepository = new CalendarRepository(this.getActivity().getContentResolver());
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        MultiSelectListPreference listPreference = (MultiSelectListPreference)findPreference(PREFERENCE_KEY_RELEVANT_CALENDARS);
        listPreference.setEntries(calendarRepository.getAvailableCalendarNames());
        listPreference.setEntryValues(calendarRepository.getAvailableCalendarIds());
    }
}
