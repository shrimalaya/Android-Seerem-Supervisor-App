package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.model.CONSTANTS;
import com.example.supervisor_seerem.model.DocumentManager;
import com.example.supervisor_seerem.model.Worker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

/**
 * Displays different ways through which the user can talk to company employee,
 * with intents to launch the supervisor's desired and available communication options.
 */
public class CommunicationActivity extends AppCompatActivity implements View.OnClickListener{

    TextView communicationRecipient;
    TextView displayedPhoneNumber;
    TextView displayedEmail;
    TextView displayedMeets;
    TextView displayedTeams;
    TextView displayedSkype;
    TextView displayedZoom;
    Button goToMeets;
    Button goToTeams;
    Button goToSkype;
    Button goToZoom;

    String employeeID;
    DocumentSnapshot employee;
    DocumentSnapshot contactInfo = null;

    String employeeFirstName;
    String employeeLastName;
    String employeeFullName;
    String employeePhoneNumber;
    String employeeEmail;
    String employeeMEETS;
    String employeeTEAMS;
    String employeeSkype;
    String employeeZoom;

    int employeeClickablePhoneNumber;

    FirebaseFirestore firestoreReference = FirebaseFirestore.getInstance();
    private DocumentManager manager = DocumentManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);
        //List<DocumentSnapshot> contacts = manager.getContacts();

        setupNavigationBar();
        retrieveIntent();
        setUpTextViews();
    }

//    private interface DocListCallback{
//        void onCallback(List<DocumentSnapshot> docs);
//    }

    // Intent should include fields in Contact which the user was able to pass.
    private void retrieveIntent(){

        employeeID = getIntent().getStringExtra("ID");

        for(DocumentSnapshot doc: manager.getWorkers()) {
            if(doc.getString(CONSTANTS.ID_KEY).equals(employeeID)) {
                employee = doc;
                break;
            }
        }

        for(DocumentSnapshot doc: manager.getContacts()) {
            if(doc.getString(CONSTANTS.ID_KEY).equals(employee.getString(CONSTANTS.ID_KEY))) {
                contactInfo = doc;
                break;
            }
        }

        employeeFirstName = employee.getString(CONSTANTS.FIRST_NAME_KEY);
        Log.i("EMPLOYEE FIRST IS:", employeeFirstName);
        employeeLastName = employee.getString(CONSTANTS.LAST_NAME_KEY);

        if(contactInfo != null) {
            employeePhoneNumber = contactInfo.getString(CONSTANTS.PHONE_CONTACT_KEY);
            employeeEmail = contactInfo.getString(CONSTANTS.EMAIL_CONTACT_KEY);

            //These should be passed as with implicit intents somehow to use with the appropriate app.
            employeeMEETS = "";
            employeeTEAMS = "";
            employeeZoom = contactInfo.getString(CONSTANTS.LINK_CONTACT_KEY);
        }

    }

    // Some sort of identification to tell which employee the user wants to contact
    private void setUpTextViews(){
        communicationRecipient = findViewById(R.id.displayCommunicationRecipient);
        displayedPhoneNumber = findViewById(R.id.linkedPhoneNumber);
        displayedEmail = findViewById(R.id.linkedEmail);
        displayedMeets = findViewById(R.id.linkedGoogleMeet);
        displayedTeams = findViewById(R.id.linkedTeams);
        displayedSkype = findViewById(R.id.linkedSkype);
        displayedZoom = findViewById(R.id.linkedZoom);

        goToMeets = findViewById(R.id.buttonGoogleMeet);
        goToTeams = findViewById(R.id.buttonMicrosoftTeams);
        goToSkype = findViewById(R.id.buttonSkype);

        goToMeets.setOnClickListener(this);
        goToTeams.setOnClickListener(this);
        goToSkype.setOnClickListener(this);

        if(employeeLastName == null || employeeFirstName == null){
            employeeFullName = getString(R.string.communication_no_employee);
        }else{
            employeeFullName = getString(R.string.employee_last_name_first_name, employeeLastName, employeeFirstName);
        }

        if(employeePhoneNumber == null){
            employeePhoneNumber = getString(R.string.communication_no_phone);
        }

        if(employeeEmail == null){
            employeeEmail = getString(R.string.communication_no_email);
        }
        if(employeeZoom == null){
            employeeZoom = getString(R.string.communication_no_zoom);
        }

        if(employeeMEETS == null){
            employeeMEETS = getString(R.string.communication_no_google_meet);
            goToMeets.setEnabled(false);
            goToMeets.setBackgroundColor(ContextCompat.getColor(this, R.color.grey));
        }else{
            employeeMEETS = getString(R.string.communication_google_meet_exists);
            goToMeets.setEnabled(true);
        }

        if(employeeTEAMS == null){
            employeeTEAMS = getString(R.string.communication_no_microsoft_teams);
            goToTeams.setEnabled(false);
            goToTeams.setBackgroundColor(ContextCompat.getColor(this, R.color.grey));
        }else{
            employeeTEAMS = getString(R.string.communication_microsoft_teams_exists);
            goToMeets.setEnabled(true);
        }

        if(employeeSkype == null){
            employeeSkype = getString(R.string.communications_no_skype);
            goToTeams.setEnabled(false);
            goToSkype.setBackgroundColor(ContextCompat.getColor(this, R.color.grey));
        }else{
            employeeSkype = getString(R.string.communications_skype_exists);
            goToMeets.setEnabled(true);
        }

        communicationRecipient.setText(employeeFullName);
        displayedPhoneNumber.setText(employeePhoneNumber);
        displayedEmail.setText(employeeEmail);
        displayedZoom.setText(employeeZoom);
        displayedPhoneNumber.setAutoLinkMask(Linkify.PHONE_NUMBERS);
        displayedMeets.setText(employeeMEETS);
        displayedTeams.setText(employeeTEAMS);
        displayedSkype.setText(employeeSkype);
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

      // Probably unnecessary since this activity should have the data passed to it from another
      // activity (which knows what specific user is being checked for example.
//    private void getContactData(final DocListCallback callback){
//        firestoreReference.collection(CONSTANTS.CONTACT_INFO_COLLECTION)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isComplete()){
//                            callback.onCallback(task.getResult().getDocuments());
//                        }
//                    }
//                });
//    }
    // Return true if the app is installed
    // False if not
    private boolean checkForCommunicatioNAppPackages(String packageName, PackageManager packageManager){
        try{
            packageManager.getPackageInfo(packageName, 0);
            return true;
        }catch(PackageManager.NameNotFoundException e){
            return false;
        }
    }


    // For apps which autolink do not cover, we will have to creat Implicit Intents ourselves.
    @Override
    public void onClick(View view) {
        Context context = getApplicationContext();
        PackageManager pm = context.getPackageManager();
        String zoomPackage = "us.zoom.videomeetings";
        String googleMeetsPackage = "com.google.android.apps.meetings";
        String microsoftTeamsPackage = "com.microsoft.teams";
        String skypePackage = "com.skype.raider";

        if(view.getId() == R.id.buttonGoogleMeet){
            //com.google.android.apps.meetings
            // Package name taken from google play url
            // Code for checking for installed Apps and launching the Play Store adapted from:
            // https://stackoverflow.com/questions/11753000/how-to-open-the-google-play-store-directly-from-my-android-application
            if(checkForCommunicatioNAppPackages(googleMeetsPackage,
                    pm)){
                Intent googleMeetIntent = getPackageManager()
                        .getLaunchIntentForPackage(googleMeetsPackage);
                startActivity(googleMeetIntent);
            }else{
                Toast.makeText(this, R.string.install_meets_prompt, Toast.LENGTH_LONG).show();
                try{
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + googleMeetsPackage)));
                }catch(android.content.ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id="
                            + googleMeetsPackage)));
                }
            }
        }else if(view.getId() == R.id.buttonMicrosoftTeams){
            if(checkForCommunicatioNAppPackages(microsoftTeamsPackage,
                    pm)){
                Intent googleMeetIntent = getPackageManager()
                        .getLaunchIntentForPackage(microsoftTeamsPackage);
                startActivity(googleMeetIntent);
            }else{
                Toast.makeText(this, R.string.install_teams_prompt, Toast.LENGTH_LONG).show();
                try{
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + microsoftTeamsPackage)));
                }catch(android.content.ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id="
                                    + microsoftTeamsPackage)));
                }
            }
        }else if(view.getId() == R.id.buttonSkype){
            if(checkForCommunicatioNAppPackages(skypePackage,
                    pm)){
                Intent googleMeetIntent = getPackageManager()
                        .getLaunchIntentForPackage(skypePackage);
                startActivity(googleMeetIntent);
            }else{
                Toast.makeText(this, R.string.install_skype_prompt, Toast.LENGTH_LONG).show();
                try{
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + skypePackage)));
                }catch(android.content.ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id="
                                    + skypePackage)));
                }
            }
        }

    }
}