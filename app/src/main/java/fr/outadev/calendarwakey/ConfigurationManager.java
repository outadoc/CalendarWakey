package fr.outadev.calendarwakey;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by outadoc on 2016-03-09.
 */
public class ConfigurationManager implements Serializable {

    private static final String TAG = ConfigurationManager.class.getSimpleName();

    private final Context mContext;
    private final SharedPreferences mPreferences;

    public static final String PREF_ALARM_SETTING_TIME = "pref_alarm_setting_time";
    public static final String PREF_MIN_WAKEUP_TIME = "pref_min_wakeup_time";
    public static final String PREF_MAX_WAKEUP_TIME = "pref_max_wakeup_time";
    public static final String PREF_POST_WAKEUP_FREE_TIME = "pref_post_wakeup_free_time";

    public static final String PREF_ENABLED_GLOBAL = "pref_enabled_global";
    public static final String PREF_ENABLED_WEEK_DAYS = "pref_enabled_week_days";

    public static final String[] TIME_PREFERENCES = new String[]{PREF_ALARM_SETTING_TIME, PREF_MIN_WAKEUP_TIME, PREF_MAX_WAKEUP_TIME};

    private static final List<Map.Entry<String, LocalTime>> defaultTimes = new ArrayList<>();

    static {
        defaultTimes.add(new AbstractMap.SimpleEntry<>(PREF_ALARM_SETTING_TIME, new LocalTime(21, 0)));
        defaultTimes.add(new AbstractMap.SimpleEntry<>(PREF_MIN_WAKEUP_TIME, new LocalTime(0, 0)));
        defaultTimes.add(new AbstractMap.SimpleEntry<>(PREF_MAX_WAKEUP_TIME, new LocalTime(11, 0)));
    }

    public ConfigurationManager(Context context) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public boolean isAppEnabled() {
        return mPreferences.getBoolean(PREF_ENABLED_GLOBAL, false);
    }

    public LocalTime getMinWakeUpTime() {
        return getTimeFromPreference(PREF_MIN_WAKEUP_TIME);
    }

    public LocalTime getMaxWakeUpTime() {
        return getTimeFromPreference(PREF_MAX_WAKEUP_TIME);
    }

    public Duration getPostWakeFreeTime() {
        String prefTime = mPreferences.getString(PREF_POST_WAKEUP_FREE_TIME, "60");
        return new Duration(1000 * 60 * Integer.parseInt(prefTime));
    }

    public LocalTime getAlarmSettingTime() {
        return getTimeFromPreference(PREF_ALARM_SETTING_TIME);
    }

    public Set<Integer> getEnabledWeekDays() {
        Set<String> weekDaysPref = mPreferences.getStringSet(PREF_ENABLED_WEEK_DAYS, null);
        Set<Integer> enabledDays = new HashSet<>();

        if (weekDaysPref == null) return enabledDays;

        for (String dayIndex : weekDaysPref) {
            try {
                int day = Integer.parseInt(dayIndex);

                if (day < 1 || day > 7) {
                    Log.e(TAG, day + " is not a valid week day");
                    break;
                }

                enabledDays.add(day);
            } catch (NumberFormatException e) {
                Log.e(TAG, dayIndex + " is not a valid week day");
            }
        }

        if (enabledDays.size() > 7) {
            Log.wtf(TAG, "wat");
        }

        return enabledDays;
    }

    public void setDefaultValues() {
        for (Map.Entry<String, LocalTime> pref : defaultTimes) {
            if (getTimeFromPreference(pref.getKey()) == null) {
                saveTimeToPreference(pref.getKey(), pref.getValue());
            }
        }
    }

    public LocalTime getTimeFromPreference(String key) {
        String val = mPreferences.getString(key, null);
        return val == null ? null : LocalTime.parse(val);
    }

    public void saveTimeToPreference(String key, LocalTime time) {
        mPreferences.edit()
                .putString(key, time.toString())
                .apply();
    }
}
