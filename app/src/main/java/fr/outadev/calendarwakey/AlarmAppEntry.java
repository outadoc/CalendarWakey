package fr.outadev.calendarwakey;

/**
 * Created by outadoc on 2016-03-29.
 */
public class AlarmAppEntry {

    private CharSequence mPackageName;
    private CharSequence mAppName;

    public AlarmAppEntry(CharSequence packageName, CharSequence appName) {
        mPackageName = packageName;
        mAppName = appName;
    }

    public CharSequence getAppName() {
        return mAppName;
    }

    public void setAppName(String appName) {
        mAppName = appName;
    }

    public CharSequence getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

}
