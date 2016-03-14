package fr.outadev.calendarwakey;

import android.content.Context;

import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        return new LocalTime(20, 0);
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
}
