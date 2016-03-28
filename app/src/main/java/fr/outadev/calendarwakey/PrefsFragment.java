package fr.outadev.calendarwakey;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Created by outadoc on 2016-03-10.
 */
public class PrefsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    public static final String TAG = PrefsFragment.class.getName();
    public static final String KEY_CONFIG = "config";

    private ConfigurationManager mConfig;
    private Preference prefAlarmSetting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_general);
        mConfig = new ConfigurationManager(getActivity());

        prefAlarmSetting = findPreference("pref_alarm_setting_time");

        prefAlarmSetting.setOnPreferenceChangeListener(this);
        prefAlarmSetting.setOnPreferenceClickListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        switch (preference.getKey()) {
            case "pref_alarm_setting_time":
                updatePostChange();
                return true;
        }

        return false;
    }

    private void updatePostChange() {
        prefAlarmSetting.setSummary(mConfig.getAlarmSettingTime().toString(DateTimeFormat.shortTime()));
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "pref_alarm_setting_time":
                selectTime(preference.getKey());
                return true;
        }

        return false;
    }

    private void selectTime(final String preferenceKey) {
        LocalTime current = mConfig.getTimeFromPreference(preferenceKey);
        TimePickerDialog dialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                LocalTime newTime = new LocalTime(hourOfDay, minute);
                mConfig.saveTimeToPreference(preferenceKey, newTime);
            }
        }, current.getHourOfDay(), current.getMinuteOfHour(), DateFormat.is24HourFormat(getActivity()));

        dialog.show();
    }

    public static PrefsFragment newInstance() {
        return new PrefsFragment();
    }

}