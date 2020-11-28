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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Displays different ways through which the user can talk to company employee,
 * with intents to launch the supervisor's desired and available communication options.
 */
public class CommunicationActivity extends AppCompatActivity implements View.OnClickListener{

    TextView communicationRecipient;
    TextView displayedPhoneNumber;
    TextView displayedEmail;
    TextView displayedLink;

    Button goToLink;

    String employeeID;
    DocumentSnapshot employee;
    DocumentSnapshot contactInfo = null;

    String employeeFirstName;
    String employeeLastName;
    String employeeFullName;
    String employeePhoneNumber;
    String employeeEmail;
    String employeeLink;

    String linkType;

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

    private void parseEmployeeLinkType(String employeeLink) {
        if(employeeLink.equals(" - ")) {
            // No link provided
            linkType = "none";
            return;
        }

        Pattern zoom = Pattern.compile("zoom\\.us", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Pattern teams = Pattern.compile("teams\\.microsoft", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Pattern skype = Pattern.compile("join\\.skype", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Pattern meet = Pattern.compile("meet\\.google", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        Matcher matcher = zoom.matcher(employeeLink);
        if (matcher.find()) {
            // zoom
            linkType = "zoom";
            return;
        }

        matcher = teams.matcher(employeeLink);
        if (matcher.find()) {
            // teams
            linkType = "teams";
            return;
        }

        matcher = skype.matcher(employeeLink);
        if (matcher.find()) {
            // skype
            linkType = "skype";
            return;
        }

        matcher = meet.matcher(employeeLink);
        if (matcher.find()) {
            // meet
            linkType = "meet";
            return;
        }

//        https://join.skype.com/XU4heiXvGWq8 = meeting link
//        https://join.skype.com/invite/VX3ey1mN0ODZ = username
    }

    // Intent should include fields in Contact which the user was able to pass.
    private void retrieveIntent() {

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
            employeePhoneNumber = checkNull(contactInfo.getString(CONSTANTS.PHONE_CONTACT_KEY));
            employeeEmail = checkNull(contactInfo.getString(CONSTANTS.EMAIL_CONTACT_KEY));

            //These should be passed as with implicit intents somehow to use with the appropriate app.
            employeeLink = checkNull((contactInfo.getString(CONSTANTS.LINK_CONTACT_KEY)));
            parseEmployeeLinkType(employeeLink);
            Log.d("COMMUNICATION", "Link type: " + linkType);
        } else {
            linkType = "none";
        }

    }

    private String checkNull(String string) {
        if(string == null || string.isEmpty()) {
            return " - ";
        } else {
            return string;
        }
    }

    // Some sort of identification to tell which employee the user wants to contact
    private void setUpTextViews() {
        communicationRecipient = findViewById(R.id.displayCommunicationRecipient);
        displayedPhoneNumber = findViewById(R.id.linkedPhoneNumber);
        displayedEmail = findViewById(R.id.linkedEmail);
        displayedLink = findViewById(R.id.linkedInternetCommunication);

        goToLink = findViewById(R.id.buttonInternetLink);
        goToLink.setOnClickListener(this);

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

        if(employeeLink == null){
            employeeLink = getString(R.string.communication_no_zoom);
            goToLink.setBackgroundColor(ContextCompat.getColor(this, R.color.grey));
        }

        communicationRecipient.setText(employeeFullName);
        displayedPhoneNumber.setText(employeePhoneNumber);
        displayedEmail.setText(employeeEmail);
        displayedPhoneNumber.setAutoLinkMask(Linkify.PHONE_NUMBERS);
        displayedLink.setText(employeeMEETS);
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
    private boolean checkForCommunicationAppPackages(String packageName, PackageManager packageManager){
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

        // These were taken from the links to the corresponding apps in the Play Store.
        // On mobile, click on the top right 3 dots -> Share -> Copy.
        // The strings below are everything after after "...details?id=" in the copied liked
        String zoomPackage = "us.zoom.videomeetings";
        String googleMeetsPackage = "com.google.android.apps.meetings";
        String microsoftTeamsPackage = "com.microsoft.teams";
        String skypePackage = "com.skype.raider";

        if (view.getId() == R.id.buttonInternetLink) {
            // Code for checking for installed Apps and launching the Play Store adapted from different answers in:
            // https://stackoverflow.com/questions/11753000/how-to-open-the-google-play-store-directly-from-my-android-application
            if (checkForCommunicationAppPackages(googleMeetsPackage,
                    pm)) {

                Intent googleMeetIntent = getPackageManager()
                        .getLaunchIntentForPackage(googleMeetsPackage);
                startActivity(googleMeetIntent);
            } else {
                Toast.makeText(this, R.string.install_meets_prompt, Toast.LENGTH_LONG).show();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + googleMeetsPackage)));
                } catch (android.content.ActivityNotFoundException e) {
                    //If google play is not installed, launch on the web.
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id="
                                    + googleMeetsPackage)));
                }
            }
        }
    }
}