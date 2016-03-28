package fr.outadev.calendarwakey;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Created by outadoc on 2016-03-10.
 */
public class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = PrefsFragment.class.getName();

    private ConfigurationManager mConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_general);
        mConfig = new ConfigurationManager(getActivity());

        setupListeners();
        buildSummaries();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        ApplyNextAlarmReceiver.enable(getActivity());
    }

    private void setupListeners() {
        for (String key : ConfigurationManager.TIME_PREFERENCES) {
            Preference pref = findPreference(key);
            pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if (preference instanceof TimePreference) {
                        preference.setSummary(LocalTime.parse((String) o).toString(DateTimeFormat.shortTime()));
                    }

                    return true;
                }

            });
        }

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    private void buildSummaries() {
        for (String key : ConfigurationManager.TIME_PREFERENCES) {
            Preference pref = findPreference(key);
            pref.setSummary(mConfig.getTimeFromPreference(key).toString(DateTimeFormat.shortTime()));
        }
    }

    public static PrefsFragment newInstance() {
        return new PrefsFragment();
    }
}