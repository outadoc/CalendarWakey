package fr.outadev.calendarwakey;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class SettingsActivity extends AppCompatActivity {

    public static final int CALENDAR_ACCESS_PERM_REQUEST_CODE = 1;
    public static final String TAG = SettingsActivity.class.getName();

    private ConfigurationManager mConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new PrefsFragment())
                .commit();

        mConfig = new ConfigurationManager(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            calendarReadTest();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, CALENDAR_ACCESS_PERM_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CALENDAR_ACCESS_PERM_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                calendarReadTest();
            } else {
                (new AlertDialog.Builder(this))
                        .setTitle("Permission needed")
                        .setMessage("We need your permission to access your calendar so that we can set your alarm " +
                                "automatically.")
                        .create()
                        .show();
            }
        }
    }

    public void calendarReadTest() {
        CalendarProvider calendarProvider = new CalendarProvider(this);
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
        Log.e(TAG, "An error occured while trying to set the alarm for " + event.getStartTime());
        e.printStackTrace();
    }

    public static class PrefsFragment extends PreferenceFragmentCompat {

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

}
