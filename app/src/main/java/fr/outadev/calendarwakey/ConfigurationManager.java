package fr.outadev.calendarwakey;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by outadoc on 2016-03-09.
 */
public class ConfigurationManager implements Serializable {

    private final Context mContext;
    private final SharedPreferences mPreferences;

    public ConfigurationManager(Context context) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public LocalTime getMinWakeUpTime() {
        return LocalTime.MIDNIGHT;
    }

    public LocalTime getMaxWakeUpTime() {
        // 11:00 am
        return new LocalTime(11, 0);
    }

    public Duration getPostWakeFreeTime() {
        // 1h45
        return new Duration(1000 * 60 * 105);
    }

    public LocalTime getAlarmSettingTime() {
        // 22:00
        return getTimeFromPreference("pref_alarm_setting_time");
    }

    public Collection<Integer> getEnabledWeekDays() {
        List<Integer> days = new ArrayList<>();

        days.add(DateTimeConstants.MONDAY);
        days.add(DateTimeConstants.TUESDAY);
        days.add(DateTimeConstants.WEDNESDAY);
        days.add(DateTimeConstants.THURSDAY);
        days.add(DateTimeConstants.FRIDAY);

        return days;
    }

    public LocalTime getTimeFromPreference(String key) {
        return LocalTime.parse(mPreferences.getString(key, null));
    }

    public void saveTimeToPreference(String preferenceKey, LocalTime time) {
        mPreferences.edit()
                .putString(preferenceKey, time.toString(DateTimeFormat.shortTime()))
                .apply();
    }
}
