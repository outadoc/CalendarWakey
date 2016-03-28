package fr.outadev.calendarwakey;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.joda.time.format.DateTimeFormat;

/**
 * Created by outadoc on 2016-03-10.
 */
public class PrefsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

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
    public boolean onPreferenceChange(Preference preference, Object o) {
        for (String key : ConfigurationManager.TIME_PREFERENCES) {
            if (key.equals(preference.getKey())) {
                preference.setSummary((String)o);
                break;
            }
        }

        return true;
    }

    private void setupListeners() {
        for (String key : ConfigurationManager.TIME_PREFERENCES) {
            Preference pref = findPreference(key);
            pref.setOnPreferenceChangeListener(this);
        }
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