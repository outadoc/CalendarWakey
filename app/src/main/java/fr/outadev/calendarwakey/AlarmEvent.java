package fr.outadev.calendarwakey;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Created by outadoc on 2016-03-09.
 */
public class AlarmEvent {

    private int mCalendarId;
    private String mCalendarName;
    private String mName;
    private DateTime mStartTime;

    public AlarmEvent(int calendarId, String calendarName, String name, long startTime) {
        mCalendarId = calendarId;
        mCalendarName = calendarName;
        mName = name;
        mStartTime = new DateTime(startTime);
    }

    public int getCalendarId() {
        return mCalendarId;
    }

    public void setCalendarId(int calendarId) {
        mCalendarId = calendarId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public DateTime getStartTime() {
        return mStartTime;
    }

    public void setStartTime(DateTime startTime) {
        mStartTime = startTime;
    }

    public String getCalendarName() {
        return mCalendarName;
    }

    public void setCalendarName(String calendarName) {
        mCalendarName = calendarName;
    }
}
