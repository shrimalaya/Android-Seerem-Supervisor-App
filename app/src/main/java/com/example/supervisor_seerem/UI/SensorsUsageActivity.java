package com.example.supervisor_seerem.UI;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.example.supervisor_seerem.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

public class SensorsUsageActivity extends AppCompatActivity {

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
        super.onBackPressed();
//        finish();
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavigationBar);
//        navigation.setSelectedItemId(R.id.userNavigation);
        finishAffinity();
        Intent intent = UserInfoActivity.launchUserInfoIntent(SensorsUsageActivity.this);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors_usage);
        setupNavigationBar();

        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        temperatureTextView.setText(R.string.temperatureTextView);
        temperatureValueTextView = (TextView) findViewById(R.id.temperatureValueTextView);
        pressureTextView = (TextView) findViewById(R.id.pressureTextView);
        pressureTextView.setText(R.string.pressureTextView);
        pressureValueTextView = (TextView) findViewById(R.id.pressureValueTextView);

        mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Perform check for Temperature and Pressure sensors
        if(mySensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) !=  null){
            setupTemperatureSensor();
        }else{
            temperatureValueTextView.setText(R.string.noTemperatureSensorTextView);
        }

        if(mySensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null){
            setupPressureSensor();
        }else{
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

        mySensorManager.unregisterListener(temperatureEventListener);
        mySensorManager.unregisterListener(pressureEventListener);
    }
}