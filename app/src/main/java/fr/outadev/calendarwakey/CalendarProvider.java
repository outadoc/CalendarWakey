package fr.outadev.calendarwakey;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by outadoc on 2016-03-09.
 */
public class CalendarProvider {

    private final Context mContext;
    private final ConfigurationManager mConfig;

    public CalendarProvider(Context context) {
        mContext = context;
        mConfig = new ConfigurationManager(mContext);
    }

    /**
     * Fetches the next events in the user's calendars that may be used for setting tomorrow's wake up alarm.
     * Only the events matching the user's criteria will be selected (min and max wake up time, calendars...)
     *
     * @return A list of events that will occur between tomorrow's min and max wake up times,
     * plus post-wake free time.
     * @throws SecurityException
     */
    public Collection<AlarmEvent> getNextEligibleEvents() throws SecurityException {
        Collection<AlarmEvent> eventList = new LinkedList<>();
        ContentResolver cr = mContext.getContentResolver();

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // Calculate the minimum and maximum times at which the user wants to be woken up,
        // to only include events between the two (taking the free time into account)
        DateTime minEventTime = tomorrow.toDateTime(mConfig.getMinWakeUpTime()).plus(mConfig.getPostWakeFreeTime());
        DateTime maxEventTime = tomorrow.toDateTime(mConfig.getMaxWakeUpTime()).plus(mConfig.getPostWakeFreeTime());

        // Get corresponding calendar events (and only those)
        Uri uri = CalendarContract.Events.CONTENT_URI;
        String[] projection = new String[]{
                CalendarContract.Events.CALENDAR_ID,
                CalendarContract.Events.CALENDAR_DISPLAY_NAME,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART
        };

        String selection = CalendarContract.Events.ALL_DAY + " = 0 " +
                "AND " + CalendarContract.Events.DTSTART + " > ? " +
                "AND " + CalendarContract.Events.DTSTART + " < ?";

        String orderBy = CalendarContract.Events.DTSTART;

        Cursor eventsCursor = cr.query(uri, projection, selection, new String[]{
                String.valueOf(minEventTime.getMillis()),
                String.valueOf(maxEventTime.getMillis())
        }, orderBy);

        if (eventsCursor == null) {
            return eventList;
        }

        // Copy the results to a list
        while (eventsCursor.moveToNext()) {
            eventList.add(new AlarmEvent(
                    eventsCursor.getInt(eventsCursor.getColumnIndex(CalendarContract.Events.CALENDAR_ID)),
                    eventsCursor.getString(eventsCursor.getColumnIndex(CalendarContract.Events.CALENDAR_DISPLAY_NAME)),
                    eventsCursor.getString(eventsCursor.getColumnIndex(CalendarContract.Events.TITLE)),
                    eventsCursor.getLong(eventsCursor.getColumnIndex(CalendarContract.Events.DTSTART))
            ));
        }

        eventsCursor.close();
        return eventList;
    }

    public AlarmEvent getAlarmEventForTomorrow() throws SecurityException {
        Collection<AlarmEvent> events = getNextEligibleEvents();

        if (events.isEmpty()) {
            return null;
        }

        return events.iterator().next();
    }

}
