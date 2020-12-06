package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.model.DocumentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Text;

public class SensorsUsageActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private DocumentManager documentManager = DocumentManager.getInstance();

    private SensorManager mySensorManager;
    private Sensor temperatureSensor;
    private Sensor pressureSensor;
    private SensorEventListener temperatureEventListener;
    private SensorEventListener pressureEventListener;

    private TextView temperatureTextView;
    private TextView temperatureValueTextView;
    private TextView pressureTextView;
    private TextView pressureValueTextView;

    public static Intent launchSensorUsageIntent(Context context) {
        Intent sensorIntent = new Intent(context, SensorsUsageActivity.class);
        return sensorIntent;
    }

    private void setupSidebarNavigationDrawer() {
        drawer = findViewById(R.id.sidebar_drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.sidebar_navigation_view);

        // customized toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_for_sidebar);
        setSupportActionBar(toolbar);

        // toggle to open/close the sidebar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.sidebar_navigation_open, R.string.sidebar_navigation_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // header
        View headerView = navigationView.getHeaderView(0);
        String savedFirstName = documentManager.getCurrentUser().getFirstName();
        String savedLastName = documentManager.getCurrentUser().getLastName();
        TextView sidebarFullName = (TextView) headerView.findViewById(R.id.sidebar_header_fullname_textview);
        sidebarFullName.setText(savedFirstName + " " + savedLastName);

        final SharedPreferences loginSharedPreferences = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        String savedUsername = loginSharedPreferences.getString("username", null);
        TextView sidebarUsername = (TextView) headerView.findViewById(R.id.sidebar_header_username_textview);
        sidebarUsername.setText(savedUsername);

        // onClickListener
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.sidebar_user:
                        Intent userIntent = UserInfoActivity.launchUserInfoIntent(SensorsUsageActivity.this);
                        startActivity(userIntent);
                        finish();
                        break;

                    case R.id.sidebar_overtime:
                        Intent overtimeIntent = AddOvertimeActivity.launchAddOvertimeIntent(SensorsUsageActivity.this);
                        startActivity(overtimeIntent);
                        break;

                    case R.id.sidebar_day_leave:
                        Intent dayLeaveIntent = AddDayLeaveActivity.launchAddDayLeaveIntent(SensorsUsageActivity.this);
                        startActivity(dayLeaveIntent);
                        break;

                    case R.id.sidebar_all_workers:
                        Intent workerIntent = WorkerInfoActivity.launchWorkerInfoIntent(SensorsUsageActivity.this);
                        startActivity(workerIntent);
                        finish();
                        break;

                    case R.id.sidebar_company:
                        Intent employeeDirectoryIntent = new Intent(SensorsUsageActivity.this, EmployeeDirectoryActivity.class);
                        startActivity(employeeDirectoryIntent);
                        break;

                    case R.id.sidebar_light_dark_mode:
                        Intent changeThemeIntent = ChangeThemeActivity.launchChangeThemeIntent(SensorsUsageActivity.this);
                        startActivity(changeThemeIntent);
                        break;

                    case R.id.sidebar_languages:
                        Intent changeLanguageIntent = ChangeLanguageActivity.launchChangeLanguageIntent(SensorsUsageActivity.this);
                        startActivity(changeLanguageIntent);
                        break;

                    case R.id.sidebar_change_password:
                        Intent changePasswordIntent = ChangePasswordActivity.launchChangePasswordIntent(SensorsUsageActivity.this);
                        startActivity(changePasswordIntent);
                        break;

                    case R.id.sidebar_log_out:
                        launchLogOutDialog();
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void setupNavigationBar() {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavigationBar);
        navigation.setSelectedItemId(R.id.sensorNavigation);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.workerNavigation:
                        Intent workerIntent = WorkerInfoActivity.launchWorkerInfoIntent(SensorsUsageActivity.this);
                        startActivity(workerIntent);
                        finish();
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.siteNavigation:
                        Intent siteIntent = SiteInfoActivity.launchSiteInfoIntent(SensorsUsageActivity.this);
                        startActivity(siteIntent);
                        finish();
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.mapNavigation:
                        Intent mapIntent = SiteMapActivity.launchMapIntent(SensorsUsageActivity.this);
                        startActivity(mapIntent);
                        finish();
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.sensorNavigation:
                        // home activity --> do nothing
                        return true;

                    case R.id.userNavigation:
                        Intent userIntent = UserInfoActivity.launchUserInfoIntent(SensorsUsageActivity.this);
                        startActivity(userIntent);
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finishAffinity();
            Intent intent = UserInfoActivity.launchUserInfoIntent(SensorsUsageActivity.this);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors_usage);
        setupNavigationBar();
        setupSidebarNavigationDrawer();

        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        temperatureTextView.setText(R.string.temperatureTextView);
        temperatureValueTextView = (TextView) findViewById(R.id.temperatureValueTextView);
        pressureTextView = (TextView) findViewById(R.id.pressureTextView);
        pressureTextView.setText(R.string.pressureTextView);
        pressureValueTextView = (TextView) findViewById(R.id.pressureValueTextView);

        mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Perform check for Temperature and Pressure sensors
        if (mySensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            setupTemperatureSensor();
        } else {
            temperatureValueTextView.setText(R.string.noTemperatureSensorTextView);
        }

        if (mySensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
            setupPressureSensor();
        } else{
            pressureValueTextView.setText(R.string.noPressureSensorTextView);
        }
        setupPressureSensor();
    }

    private void setupTemperatureSensor() {
        temperatureSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        temperatureEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float temperature = sensorEvent.values[0];
                //temperatureValueTextView.setText("" + temperature);
                temperatureValueTextView.setText(getString(R.string.temperatureUnitsTextView, temperature));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    private void setupPressureSensor() {
        pressureSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        pressureEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float pressure = sensorEvent.values[0];
                //pressureValueTextView.setText("" + pressure);
                pressureValueTextView.setText( getString(R.string.pressureUnitsTextView, pressure));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    private void launchLogOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SensorsUsageActivity.this,
                                                            R.style.AlertDialog);
        builder.setMessage(getString(R.string.log_out_message));
        builder.setTitle(getString(R.string.log_out_title));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.log_out_dialog_positive),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finishAffinity();
                        Intent intent = new Intent(SensorsUsageActivity.this, LoginInfoActivity.class);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton(getString(R.string.log_out_dialog_negative),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#B32134"));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#B32134"));
    }

    @Override
    protected void onResume() {
        super.onResume();

        mySensorManager.registerListener(temperatureEventListener, temperatureSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(pressureEventListener, pressureSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // release sensors
        mySensorManager.unregisterListener(temperatureEventListener);
        mySensorManager.unregisterListener(pressureEventListener);
    }
}