package fr.outadev.calendarwakey;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormat;

import java.util.Collection;

public class ApplyNextAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = ApplyNextAlarmReceiver.class.getName();
    private static final int STATUS_NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConfigurationManager conf = new ConfigurationManager(context);

        Collection<Integer> days = conf.getEnabledWeekDays();
        if (!days.contains(LocalDate.now().plusDays(1).getDayOfWeek())) return;

        setAlarmFromCalendarEvents(context);
    }

    static void enable(Context context) {
        disable(context);
        Log.d(TAG, "enabling " + ApplyNextAlarmReceiver.class.getSimpleName());

        ConfigurationManager conf = new ConfigurationManager(context);
        LocalDate receiverStartDate = LocalDate.now();

        if (conf.getAlarmSettingTime().isBefore(LocalTime.now())) {
            receiverStartDate = receiverStartDate.plusDays(1);
        }

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                receiverStartDate.toDateTime(conf.getAlarmSettingTime()).getMillis(),
                AlarmManager.INTERVAL_DAY,
                getBroadcast(context)
        );
    }

    static void disable(Context context) {
        Log.d(TAG, "disabling " + ApplyNextAlarmReceiver.class.getSimpleName());

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(getBroadcast(context));
    }

    public static PendingIntent getBroadcast(Context context) {
        Intent intent = new Intent(context, ApplyNextAlarmReceiver.class);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public void setAlarmFromCalendarEvents(Context context) {
        ConfigurationManager conf = new ConfigurationManager(context);
        CalendarProvider calendarProvider = new CalendarProvider(context);

        AlarmEvent event = calendarProvider.getAlarmEventForTomorrow();

        if (event == null || event.getStartTime() == null) {
            notifyNoAlarm(context);
            return;
        }

        try {
            DateTime wakeUpTime = event.getStartTime().minus(conf.getPostWakeFreeTime()).withZone(DateTimeZone.getDefault());

            Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(AlarmClock.EXTRA_MESSAGE, event.getName() + " (" + event.getCalendarName() + ")");
            i.putExtra(AlarmClock.EXTRA_HOUR, wakeUpTime.getHourOfDay());
            i.putExtra(AlarmClock.EXTRA_MINUTES, wakeUpTime.getMinuteOfHour());
            i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);

            context.startActivity(i);
            notifySuccess(context, event, wakeUpTime);
        } catch (Exception e) {
            notifyError(context, event, e);
        }
    }

    public void notifyNoAlarm(Context context) {
        Log.e(TAG, "Nothing seems to be planned for tomorrow");
    }

    public void notifyError(Context context, AlarmEvent event, Exception e) {
        Log.e(TAG, "An error occurred while trying to set the alarm for " + event.getStartTime());
        e.printStackTrace();

        NotificationManagerCompat
                .from(context)
                .notify(STATUS_NOTIFICATION_ID,
                        new NotificationCompat.Builder(context)
                                .setContentTitle("Could not set alarm")
                                .setCategory(NotificationCompat.CATEGORY_ALARM)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setStyle(new NotificationCompat.BigTextStyle()
                                                .bigText("Something happened and we could not set your alarm " +
                                                        "for your event \"" + event.getName() + "\" scheduled for " +
                                                        event.getStartTime().toLocalTime().toString(DateTimeFormat.shortTime()))
                                ).build()
                );
    }

    public void notifySuccess(Context context, AlarmEvent event, DateTime wakeUpTime) {
        ConfigurationManager conf = new ConfigurationManager(context);
        Log.i(TAG, "Successfully set alarm for " + event.getStartTime());

        NotificationManagerCompat
                .from(context)
                .notify(STATUS_NOTIFICATION_ID,
                        new NotificationCompat.Builder(context)
                                .setContentTitle("Waking up at " + wakeUpTime.toString(DateTimeFormat.shortTime()))
                                .setCategory(NotificationCompat.CATEGORY_ALARM)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setStyle(new NotificationCompat.InboxStyle()
                                                .addLine(event.getStartTime().toString(DateTimeFormat.shortTime()) + " - " + event.getName())
                                                .addLine("Waking you up " + PeriodFormat.getDefault().print(conf.getPostWakeFreeTime().toPeriod()) + " before")
                                ).build()
                );
    }
}
