package fr.outadev.calendarwakey;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Created by outadoc on 2016-03-28.
 */
public class TimePreference extends DialogPreference {

    private LocalTime lastTime;
    private TimePicker picker;

    public TimePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        picker.setIs24HourView(DateFormat.is24HourFormat(getContext()));
        return picker;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        if (lastTime != null) {
            picker.setCurrentHour(lastTime.getHourOfDay());
            picker.setCurrentMinute(lastTime.getMinuteOfHour());
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            lastTime = new LocalTime(picker.getCurrentHour(), picker.getCurrentMinute());
            String time = lastTime.toString(DateTimeFormat.shortTime());

            if (callChangeListener(time)) {
                persistString(time);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time;

        if (restoreValue) {
            if (defaultValue == null) {
                time = getPersistedString("00:00");
            } else {
                time = getPersistedString(defaultValue.toString());
            }
        } else {
            time = defaultValue.toString();
        }

        lastTime = LocalTime.parse(time);
    }

}
