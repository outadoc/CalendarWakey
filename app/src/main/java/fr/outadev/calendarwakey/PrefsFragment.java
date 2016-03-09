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

        Preference myPref = findPreference("pref_test");
        myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                calendarReadTest();
                return true;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void calendarReadTest() {
        CalendarProvider calendarProvider = new CalendarProvider(getContext());
        AlarmEvent event = calendarProvider.getAlarmEventForTomorrow();

        if (event == null || event.getStartTime() == null) {
            notifyNoAlarm();
            return;
        }

        try {
            DateTime wakeUpTime = event.getStartTime().minus(mConfig.getPostWakeFreeTime()).withZone(DateTimeZone.getDefault());

            Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
            i.putExtra(AlarmClock.EXTRA_MESSAGE, event.getName() + " (" + event.getCalendarName() + ")");
            i.putExtra(AlarmClock.EXTRA_HOUR, wakeUpTime.getHourOfDay());
            i.putExtra(AlarmClock.EXTRA_MINUTES, wakeUpTime.getMinuteOfHour());
            i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);

            startActivity(i);
        } catch (Exception e) {
            notifyError(event, e);
        }
    }

    public void notifyNoAlarm() {
        Log.e(TAG, "Nothing seems to be planned for tomorrow");
    }

    public void notifyError(AlarmEvent event, Exception e) {
        Log.e(TAG, "An error occurred while trying to set the alarm for " + event.getStartTime());
        e.printStackTrace();
    }
}