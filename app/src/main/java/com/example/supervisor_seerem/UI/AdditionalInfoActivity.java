package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.model.CONSTANTS;
import com.example.supervisor_seerem.model.DocumentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;

public class AdditionalInfoActivity extends AppCompatActivity {

    private String workerID;
    private DocumentSnapshot selectedWorker;
    private DocumentManager manager = DocumentManager.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_info);

        setupNavigationBar();
        receiveIntent();
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
}