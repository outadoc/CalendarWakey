package fr.outadev.calendarwakey;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
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
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new PrefsFragment())
                .commit();

        firstTimeSetupChecks();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CALENDAR_ACCESS_PERM_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                (new AlertDialog.Builder(this))
                        .setTitle("Permission needed")
                        .setMessage("We need your permission to access your calendar so that we can set your alarm " +
                                "automatically.")
                        .create()
                        .show();
            } else {
                setupDefaultClockApp();
            }
        }

    }

    public void firstTimeSetupChecks() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, CALENDAR_ACCESS_PERM_REQUEST_CODE);
        } else {
            setupDefaultClockApp();
        }
    }

    public void setupDefaultClockApp() {
        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        startActivity(Intent.createChooser(i, "Wake up with..."));
    }
}
