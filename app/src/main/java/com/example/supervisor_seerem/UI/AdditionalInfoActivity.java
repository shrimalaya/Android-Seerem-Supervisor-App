package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

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

    /**
     * HH:mm = 24hr format
     * hh:mm = 12 hr format
     */
    private double timeParser(String time) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");

        if(time.equals(" - ") || time.equals("-")) {
            return 0;
        }

        String arr[] = null;
        if(time != null) {
           if(time.split("-") != null) {
               arr = time.split("-");
           }
        }

        Date d1 = dateFormat.parse(arr[0]);
        Date d2 = dateFormat.parse(arr[1]);
        double difference = (d2.getTime() - d1.getTime())/(1000*60*60.0);

        return difference;
    }

    /**
     * Overtime = more than 8 hours of shift in a day
     */
    private void populateData() {
        DocumentSnapshot emergencyInfo = null;
        DocumentSnapshot availablity = null;
        double total = 0;
        double overtime = 0;

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
            double hrs = 0;

            monday = findViewById(R.id.txt_monday);
            monday.setText(checkNull(availablity.getString(CONSTANTS.MONDAY_KEY)));
            try {
                hrs = timeParser(availablity.getString(CONSTANTS.MONDAY_KEY));
                total += hrs;
                System.out.println("TEST4> Time difference M: " + hrs);
                if(hrs > 8) {
                    overtime+=(hrs-8);
                }
            } catch (ParseException e) {
                System.out.println("TEST4> Parse Exception: " + e.toString());
            }

            tuesday = findViewById(R.id.txt_tuesday);
            tuesday.setText(checkNull(availablity.getString(CONSTANTS.TUESDAY_KEY)));
            try {
                hrs = timeParser(availablity.getString(CONSTANTS.TUESDAY_KEY));
                total += hrs;
                System.out.println("TEST4> Time difference T: " + hrs);
                if(hrs > 8) {
                    overtime+=(hrs-8);
                }
            } catch (ParseException e) {
                System.out.println("TEST4> Parse Exception: " + e.toString());
            }

            wednesday = findViewById(R.id.txt_wednesday);
            wednesday.setText(checkNull(availablity.getString(CONSTANTS.WEDNESDAY_KEY)));
            try {
                hrs = timeParser(availablity.getString(CONSTANTS.WEDNESDAY_KEY));
                total += hrs;
                System.out.println("TEST4> Time difference W: " + hrs);
                if(hrs > 8) {
                    overtime+=(hrs-8);
                }
            } catch (ParseException e) {
                System.out.println("TEST4> Parse Exception: " + e.toString());
            }

            thursday = findViewById(R.id.txt_thursday);
            thursday.setText(checkNull(availablity.getString(CONSTANTS.THURSDAY_KEY)));
            try {
                hrs = timeParser(availablity.getString(CONSTANTS.THURSDAY_KEY));
                total += hrs;
                System.out.println("TEST4> Time difference T: " + hrs);
                if(hrs > 8) {
                    overtime+=(hrs-8);
                }
            } catch (ParseException e) {
                System.out.println("TEST4> Parse Exception: " + e.toString());
            }

            friday = findViewById(R.id.txt_friday);
            friday.setText(checkNull(availablity.getString(CONSTANTS.FRIDAY_KEY)));
            try {
                hrs = timeParser(availablity.getString(CONSTANTS.FRIDAY_KEY));
                total += hrs;
                System.out.println("TEST4> Time difference F: " + hrs);
                if(hrs > 8) {
                    overtime+=(hrs-8);
                }
            } catch (ParseException e) {
                System.out.println("TEST4> Parse Exception: " + e.toString());
            }

            saturday = findViewById(R.id.txt_saturday);
            saturday.setText(checkNull(availablity.getString(CONSTANTS.SATURDAY_KEY)));
            try {
                hrs = timeParser(availablity.getString(CONSTANTS.SATURDAY_KEY));
                total += hrs;
                System.out.println("TEST4> Time difference in hours: " + hrs);
                if(hrs > 8) {
                    overtime+=(hrs-8);
                }
            } catch (ParseException e) {
                System.out.println("TEST4> Parse Exception Saturday: " + e.toString());
            }

            sunday = findViewById(R.id.txt_sunday);
            sunday.setText(checkNull(availablity.getString(CONSTANTS.SUNDAY_KEY)));
            try {
                hrs = timeParser(availablity.getString(CONSTANTS.SUNDAY_KEY));
                total += hrs;
                System.out.println("TEST4> Time difference in hours: " + hrs);
                if(hrs > 8) {
                    overtime+=(hrs-8);
                }
            } catch (ParseException e) {
                System.out.println("TEST4> Parse Exception Sunday: " + e.toString());
            }

            totalHrs = findViewById(R.id.txt_additional_totalHrs);
            totalHrs.setText("" + total);

            overtimeHrs = findViewById(R.id.txt_additional_overtimeHrs);
            overtimeHrs.setText("" + overtime);
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
                final ProgressDialog progressDialog = new ProgressDialog(AdditionalInfoActivity.this);
                progressDialog.setMessage("Refreshing All Data!");
                progressDialog.setTitle("Please wait");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();

                manager.retrieveAllData(new DocumentManager.RetrieveCallback() {
                    @Override
                    public void onCallback(Boolean result) {
                        if(result) {
                            DocumentSnapshot tempCurrWorker = null;
                            for (DocumentSnapshot doc : manager.getWorkers()) {
                                if (workerID.equals(doc.getString(CONSTANTS.ID_KEY))) {
                                    tempCurrWorker = doc;
                                    break;
                                }
                            }

                            if (tempCurrWorker == null) {
                                finish();
                            }

                            progressDialog.dismiss();
                            populateData();
                            Log.d("ADDITIONALINFO", "All data refreshed");
                        }
                    }
                });
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}