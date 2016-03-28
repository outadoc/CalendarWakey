package fr.outadev.calendarwakey;

import android.app.Application;
import android.preference.PreferenceManager;

/**
 * Created by outadoc on 2016-03-28.
 */
public class CalendarWakeyApplication extends Application {

    @Override
    public void onCreate() {
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.pref_general, true);
        super.onCreate();
    }
}
