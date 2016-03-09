package fr.outadev.calendarwakey;

import android.content.Context;

import org.joda.time.Duration;
import org.joda.time.LocalTime;

/**
 * Created by outadoc on 2016-03-09.
 */
public class ConfigurationManager {

    private final Context mContext;

    public ConfigurationManager(Context context) {
        mContext = context;
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
        return new LocalTime(22, 0);
    }
}
