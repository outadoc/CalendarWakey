package fr.outadev.calendarwakey;

import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Created by outadoc on 2016-03-10.
 */
public class PrefsFragment extends PreferenceFragmentCompat {

    public static final String TAG = PrefsFragment.class.getName();

    private ConfigurationManager mConfig;

    public PrefsFragment() {
        mConfig = new ConfigurationManager(getContext());
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_general);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}