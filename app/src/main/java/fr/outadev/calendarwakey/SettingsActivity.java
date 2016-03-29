package fr.outadev.calendarwakey;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    public static final int CALENDAR_ACCESS_PERM_REQUEST_CODE = 1;
    public static final String TAG = SettingsActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, true);

        ConfigurationManager config = new ConfigurationManager(this);
        config.setDefaultValues();

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, PrefsFragment.newInstance())
                .commit();

        firstTimeSetupChecks();
        ApplyNextAlarmReceiver.enable(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CALENDAR_ACCESS_PERM_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                (new AlertDialog.Builder(this))
                        .setTitle(R.string.permission_cal_denied_title)
                        .setMessage(R.string.permission_cal_denied_message)
                        .create()
                        .show();
            }
        }

    }

    public void firstTimeSetupChecks() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, CALENDAR_ACCESS_PERM_REQUEST_CODE);
        }
    }
}
