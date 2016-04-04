package fr.outadev.calendarwakey;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.AlarmClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
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

        ConfigurationManager conf = new ConfigurationManager(context);

        if (!conf.isAppEnabled()) {
            Log.i(TAG, "app is disabled in preferences, not enabling receiver");
            return;
        }

        Log.i(TAG, "enabling " + ApplyNextAlarmReceiver.class.getSimpleName());
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
        Log.i(TAG, "disabling " + ApplyNextAlarmReceiver.class.getSimpleName());

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
            i.setPackage(conf.getSelectedAlarmApp());
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
        Log.i(TAG, "nothing seems to be planned for tomorrow");
    }

    public void notifyError(Context context, AlarmEvent event, Exception e) {
        Log.e(TAG, "an error occurred while trying to set the alarm for " + event.getStartTime());
        e.printStackTrace();

        NotificationManagerCompat
                .from(context)
                .notify(STATUS_NOTIFICATION_ID,
                        new NotificationCompat.Builder(context)
                                .setContentTitle(context.getString(R.string.notif_error_title))
                                .setCategory(NotificationCompat.CATEGORY_ALARM)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setSmallIcon(R.drawable.notif_icon)
                                .setColor(context.getResources().getColor(R.color.icon_color))
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(context.getString(
                                                R.string.notif_error_message,
                                                event.getName(),
                                                event.getStartTime().toLocalTime()
                                                        .toString(DateTimeFormat.shortTime())))
                                ).build());
    }

    public void notifySuccess(Context context, AlarmEvent event, DateTime wakeUpTime) {
        ConfigurationManager conf = new ConfigurationManager(context);
        Log.i(TAG, "successfully set alarm for " + event.getStartTime());

        NotificationManagerCompat
                .from(context)
                .notify(STATUS_NOTIFICATION_ID,
                        new NotificationCompat.Builder(context)
                                .setContentTitle(context.getString(R.string.notif_success_title, wakeUpTime.toString(DateTimeFormat.shortTime())))
                                .setCategory(NotificationCompat.CATEGORY_ALARM)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setSmallIcon(R.drawable.notif_icon)
                                .setColor(context.getResources().getColor(R.color.icon_color))
                                .setStyle(new NotificationCompat.InboxStyle()
                                        .addLine(context.getString(R.string.notif_success_message_event, event.getStartTime().toString(DateTimeFormat.shortTime()), event.getName()))
                                        .addLine(context.getString(R.string.notif_success_message_delay, PeriodFormat.getDefault().print(conf.getPostWakeFreeTime().toPeriod())))
                                ).build()
                );
    }
}
