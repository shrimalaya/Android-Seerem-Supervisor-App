package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.model.CONSTANTS;
import com.example.supervisor_seerem.model.DocumentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.model.Document;

import java.net.IDN;
import java.util.ArrayList;
import java.util.List;

public class AdditionalInfoActivity extends AppCompatActivity {

    private String workerID;
    private DocumentSnapshot selectedWorker;
    private DocumentManager manager = DocumentManager.getInstance();

    TextView workerName, monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    TextView totalHrs, overtimeHrs;
    TextView bldGrp, medicalConditions, contact, number, relationship;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_info);

        setupNavigationBar();
        receiveIntent();
        populateData();
    }

    private void populateData() {
        DocumentSnapshot emergencyInfo = null;
        DocumentSnapshot availablity = null;

        for(DocumentSnapshot doc: manager.getEmergencyInfo()) {
            if(doc.getString(CONSTANTS.ID_KEY).equals(workerID)) {
                emergencyInfo = doc;
                break;
            }
        }

        for(DocumentSnapshot doc: manager.getAvailabilities()) {
            if(doc.getString(CONSTANTS.ID_KEY).equals(workerID)) {
                availablity = doc;
                break;
            }
        }

        workerName = findViewById(R.id.txt_additionalInfo_workerName);
        String name = selectedWorker.getString(CONSTANTS.FIRST_NAME_KEY) + " " + selectedWorker.getString(CONSTANTS.LAST_NAME_KEY);
        workerName.setText(name.toUpperCase());

        if(availablity != null) {
            monday = findViewById(R.id.txt_monday);
            monday.setText(checkNull(availablity.getString(CONSTANTS.MONDAY_KEY)));

            tuesday = findViewById(R.id.txt_tuesday);
            tuesday.setText(checkNull(availablity.getString(CONSTANTS.TUESDAY_KEY)));

            wednesday = findViewById(R.id.txt_wednesday);
            wednesday.setText(checkNull(availablity.getString(CONSTANTS.WEDNESDAY_KEY)));

            thursday = findViewById(R.id.txt_thursday);
            thursday.setText(checkNull(availablity.getString(CONSTANTS.THURSDAY_KEY)));

            friday = findViewById(R.id.txt_friday);
            friday.setText(checkNull(availablity.getString(CONSTANTS.FRIDAY_KEY)));

            saturday = findViewById(R.id.txt_saturday);
            saturday.setText(checkNull(availablity.getString(CONSTANTS.SATURDAY_KEY)));

            sunday = findViewById(R.id.txt_sunday);
            sunday.setText(checkNull(availablity.getString(CONSTANTS.SUNDAY_KEY)));

            totalHrs = findViewById(R.id.txt_additional_totalHrs);
            totalHrs.setText("Not calculated!");

            overtimeHrs = findViewById(R.id.txt_additional_overtimeHrs);
            overtimeHrs.setText("Not calculated!");
        }

        if(emergencyInfo != null) {
            bldGrp = findViewById(R.id.txt_additional_bldGrp);
            bldGrp.setText(checkNull(emergencyInfo.getString(CONSTANTS.BLOOD_GROUP_KEY)).toUpperCase());

            medicalConditions = findViewById(R.id.txt_additional_medical);
            medicalConditions.setText(checkNull(emergencyInfo.getString(CONSTANTS.MEDICAL_CONDITIONS_KEY)).toUpperCase());

            contact = findViewById(R.id.txt_additional_contactName);
            contact.setText(checkNull(emergencyInfo.getString(CONSTANTS.EMERGENCY_NAME_KEY)).toUpperCase());

            number = findViewById(R.id.txt_additional_emerNumber);
            number.setText(checkNull(emergencyInfo.getString(CONSTANTS.EMERGENCY_CONTACT_KEY)));

            relationship = findViewById(R.id.txt_additional_relationship);
            relationship.setText(checkNull(emergencyInfo.getString(CONSTANTS.RELATIONSHIP_KEY)).toUpperCase());
        }

    }

    private String checkNull(String data) {
        if(data == null || data.isEmpty()) {
            return " - ";
        } else {
            return data;
        }
    }

    private void receiveIntent() {
        Intent intent = getIntent();
        workerID = intent.getStringExtra("ID");

        for(DocumentSnapshot doc: manager.getWorkers()) {
            if(doc.getString(CONSTANTS.ID_KEY).equals(workerID)) {
                selectedWorker = doc;
                break;
            }
        }
        System.out.println("TEST3> Selected worker = " + selectedWorker.getString(CONSTANTS.ID_KEY));
    }

    private void setupNavigationBar() {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavigationBar);
        navigation.setSelectedItemId(R.id.workerNavigation);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.workerNavigation:
                        // Go back to the calling activity
                        finish();
                        return true;

                    case R.id.siteNavigation:
                        Intent siteIntent = SiteInfoActivity.launchSiteInfoIntent(AdditionalInfoActivity.this);
                        startActivity(siteIntent);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.mapNavigation:
                        Intent mapIntent = SiteMapActivity.launchMapIntent(AdditionalInfoActivity.this);
                        startActivity(mapIntent);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.sensorNavigation:
                        Intent sensorIntent = SensorsUsageActivity.launchSensorUsageIntent(AdditionalInfoActivity.this);
                        startActivity(sensorIntent);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.userNavigation:
                        Intent userIntent = UserInfoActivity.launchUserInfoIntent(AdditionalInfoActivity.this);
                        startActivity(userIntent);
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_additional_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case (R.id.menu_additional_contact):
                Intent intent = new Intent(AdditionalInfoActivity.this, CommunicationActivity.class);
                intent.putExtra("ID", selectedWorker.getString(CONSTANTS.ID_KEY));
                startActivity(intent);
                return true;

            case (R.id.menu_additional_refresh):
                manager.retrieveAllData();
                DocumentSnapshot tempCurrWorker = null;
                for(DocumentSnapshot doc: manager.getWorkers()) {
                    if(workerID.equals(doc.getString(CONSTANTS.ID_KEY))) {
                        tempCurrWorker = doc;
                        break;
                    }
                }

                if(tempCurrWorker == null) {
                    this.finish();
                }

                populateData();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}