package fr.outadev.calendarwakey;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceFragment;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormat;

import java.util.List;
import java.util.Set;

/**
 * Created by outadoc on 2016-03-10.
 */
public class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = PrefsFragment.class.getName();

    private ConfigurationManager mConfig;

    private MultiSelectListPreference daysPref;
    private EditTextPreference delayPref;
    private ListPreference alarmAppPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_general);
        mConfig = new ConfigurationManager(getActivity());

        daysPref = (MultiSelectListPreference) findPreference(ConfigurationManager.PREF_ENABLED_WEEK_DAYS);
        delayPref = (EditTextPreference) findPreference(ConfigurationManager.PREF_POST_WAKEUP_FREE_TIME);
        alarmAppPref = (ListPreference) findPreference(ConfigurationManager.PREF_ALARM_APP);


        loadAlarmAppsList();

        setupListeners();
        buildSummaries();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case ConfigurationManager.PREF_ENABLED_GLOBAL:
                ApplyNextAlarmReceiver.enable(getActivity());
                break;
            case ConfigurationManager.PREF_ALARM_SETTING_TIME:
                ApplyNextAlarmReceiver.enable(getActivity());
            case ConfigurationManager.PREF_MIN_WAKEUP_TIME:
            case ConfigurationManager.PREF_MAX_WAKEUP_TIME:
                updateTimePreferenceSummary((TimePreference) findPreference(key));
                break;
            case ConfigurationManager.PREF_ENABLED_WEEK_DAYS:
                updateDaysPreferenceSummary(daysPref);
                break;
            case ConfigurationManager.PREF_POST_WAKEUP_FREE_TIME:
                updateDurationPreferenceSummary(delayPref);
                break;
            case ConfigurationManager.PREF_ALARM_APP:
                updateListPreferenceSummary(alarmAppPref);
                break;
        }
    }

    private void setupListeners() {
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    private void buildSummaries() {
        MultiSelectListPreference daysPref = (MultiSelectListPreference) findPreference(ConfigurationManager.PREF_ENABLED_WEEK_DAYS);
        EditTextPreference delayPref = (EditTextPreference) findPreference(ConfigurationManager.PREF_POST_WAKEUP_FREE_TIME);
        ListPreference alarmAppPref = (ListPreference) findPreference(ConfigurationManager.PREF_ALARM_APP);

        updateDaysPreferenceSummary(daysPref);
        updateDurationPreferenceSummary(delayPref);
        updateListPreferenceSummary(alarmAppPref);

        for (String key : ConfigurationManager.TIME_PREFERENCES) {
            updateTimePreferenceSummary((TimePreference) findPreference(key));
        }
    }

    private void updateTimePreferenceSummary(TimePreference pref) {
        pref.setSummary(mConfig.getTimeFromPreference(pref.getKey()).toString(DateTimeFormat.shortTime()));
    }

    private void updateDaysPreferenceSummary(MultiSelectListPreference pref) {
        StringBuilder builder = new StringBuilder();
        Set<Integer> enabledWeekDays = mConfig.getEnabledWeekDays();
        String[] dayStrings = getActivity().getResources().getStringArray(R.array.days_of_week);

        for (Integer day : enabledWeekDays) {
            builder.append(dayStrings[day - 1]);
            builder.append(", ");
        }

        if (builder.length() > 0) {
            builder.delete(builder.length() - 2, builder.length() - 1);
        }

        pref.setSummary(builder.toString());
    }

    private void updateListPreferenceSummary(ListPreference pref) {
        pref.setSummary(pref.getEntry());
    }

    private void updateDurationPreferenceSummary(EditTextPreference pref) {
        String str = PeriodFormat.getDefault().print(mConfig.getPostWakeFreeTime().toPeriod());
        pref.setSummary(str);
    }

    private void loadAlarmAppsList() {
        List<AlarmAppEntry> appsList = mConfig.getAvailableAlarmApps(getActivity().getPackageManager());

        CharSequence[] appDisplaySet = new String[appsList.size()];
        CharSequence[] appValuesSet = new String[appsList.size()];

        int i = 0;

        for (AlarmAppEntry app : appsList) {
            appDisplaySet[i] = app.getAppName();
            appValuesSet[i] = app.getPackageName();
            i++;
        }

        ListPreference prefAlarmApp = (ListPreference) findPreference(ConfigurationManager.PREF_ALARM_APP);
        prefAlarmApp.setEntries(appDisplaySet);
        prefAlarmApp.setEntryValues(appValuesSet);
    }

    public static PrefsFragment newInstance() {
        return new PrefsFragment();
    }
}