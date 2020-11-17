package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.supervisor_seerem.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Displays different way through which the user can talk to others.
 */
public class CommunicationActivity extends AppCompatActivity implements View.OnClickListener{

    TextView displayedPhoneNumber;
    TextView displayedEmail;
    TextView displayedMeets;
    TextView displayedTeams;
    TextView displayedSkype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);
        setupNavigationBar();
        setUpTextViews();
    }

    // Some sort of identification to tell which employee the user wants to contact
    private void setUpTextViews(){
        displayedPhoneNumber = findViewById(R.id.linkedPhoneNumber);
        displayedEmail = findViewById(R.id.linkedEmail);
        displayedMeets = findViewById(R.id.linkedGoogleMeet);
        displayedTeams = findViewById(R.id.linkedTeams);
        displayedSkype = findViewById(R.id.linkedSkype);

        // Testing autolink
        // Phone number doesn't work yet...
        displayedPhoneNumber.setText(""+ 1234567890);
        displayedEmail.setText("test@gmail.com");
        displayedPhoneNumber.setAutoLinkMask(Linkify.PHONE_NUMBERS);

    }

    private void setupNavigationBar() {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavigationBar);
        navigation.setSelectedItemId(R.id.userNavigation);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.workerNavigation:
                        Intent workerIntent = WorkerInfoActivity.launchWorkerInfoIntent(CommunicationActivity.this);
                        startActivity(workerIntent);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.siteNavigation:
                        Intent siteIntent = SiteInfoActivity.launchSiteInfoIntent(CommunicationActivity.this);
                        startActivity(siteIntent);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.mapNavigation:
                        Intent mapIntent = SiteMapActivity.launchMapIntent(CommunicationActivity.this);
                        startActivity(mapIntent);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.sensorNavigation:
                        Intent sensorIntent = SensorsUsageActivity.launchSensorUsageIntent(CommunicationActivity.this);
                        startActivity(sensorIntent);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.userNavigation:
                        // home activity --> do nothing
                        return true;
                }
                return false;
            }
        });
    }

    // For apps which autolink do not cover, we will have to creat Implicit Intents ourselves.
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.buttonGoogleMeet){

        }else if(view.getId() == R.id.buttonMicrosoftTeams){

        }else if(view.getId() == R.id.buttonSkype){

        }

    }
}