package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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

    private static final String ZOOM = "zoom";
    private static final String SKYPE = "skype";
    private static final String TEAMS = "teams";
    private static final String MEET = "meet";
    private static final String NONE = "none";

    TextView communicationRecipient;
    TextView displayedPhoneNumber;
    TextView displayedEmail;
    TextView displayedLink;

    ImageView linkImage;

    String employeeID;
    DocumentSnapshot employee;
    DocumentSnapshot contactInfo = null;

    String employeeFirstName;
    String employeeLastName;
    String employeeFullName;
    String employeePhoneNumber;
    String employeeEmail;
    String employeeLink;

    String linkType = NONE;

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
        setupImageIcon();
        setUpTextViews();
    }

    private void setupImageIcon() {
        linkImage = findViewById(R.id.iconInternetLink);
        linkImage.setOnClickListener(this);

        if(linkType.equals(ZOOM)) {
            linkImage.setImageResource(R.drawable.ic_zoom);
        } else if (linkType.equals(MEET)) {
            linkImage.setImageResource(R.drawable.ic_google_meet);
        } else if (linkType.equals(TEAMS)) {
            linkImage.setImageResource(R.drawable.ic_microsoftteams);
        } else if (linkType.equals(SKYPE)) {
            linkImage.setImageResource(R.drawable.ic_skype);
        } else {
            linkImage.setImageResource(R.drawable.ic_none_communication);
        }
    }

//    private interface DocListCallback{
//        void onCallback(List<DocumentSnapshot> docs);
//    }

    private void parseEmployeeLinkType(String employeeLink) {
        if(employeeLink.equals(" - ")) {
            // No link provided
            linkType = NONE;
            return;
        }

        Pattern zoom = Pattern.compile("zoom\\.us", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Pattern teams = Pattern.compile("teams\\.microsoft", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Pattern skype = Pattern.compile("join\\.skype", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Pattern meet = Pattern.compile("meet\\.google", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        Matcher matcher = zoom.matcher(employeeLink);
        if (matcher.find()) {
            // zoom
            linkType = ZOOM;
            return;
        }

        matcher = teams.matcher(employeeLink);
        if (matcher.find()) {
            // teams
            linkType = TEAMS;
            return;
        }

        matcher = skype.matcher(employeeLink);
        if (matcher.find()) {
            // skype
            linkType = SKYPE;
            return;
        }

        matcher = meet.matcher(employeeLink);
        if (matcher.find()) {
            // meet
            linkType = MEET;
            return;
        }
    }

    // Intent should include fields in Contact which the user was able to pass.
    private void retrieveIntent() {

        employeeID = getIntent().getStringExtra("ID");

        if(employeeID.contains("WK")) {
            for (DocumentSnapshot doc : manager.getWorkers()) {
                if (doc.getString(CONSTANTS.ID_KEY).equals(employeeID)) {
                    employee = doc;
                    break;
                }
            }
        } else {
            for (DocumentSnapshot doc : manager.getSupervisors()) {
                if (doc.getString(CONSTANTS.ID_KEY).equals(employeeID)) {
                    employee = doc;
                    break;
                }
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
            linkType = NONE;
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
            employeeLink = getString(R.string.communication_no_link);
        }

        communicationRecipient.setText(employeeFullName);
        displayedPhoneNumber.setText(employeePhoneNumber);
        displayedEmail.setText(employeeEmail);
        displayedPhoneNumber.setAutoLinkMask(Linkify.PHONE_NUMBERS);
        displayedLink.setText(employeeLink);
        displayedLink.setAutoLinkMask(Linkify.WEB_URLS);
    }

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

        String packageName = "";

        if (view.getId() == R.id.iconInternetLink) {
            // Code for checking for installed Apps and launching the Play Store adapted from different answers in:
            // https://stackoverflow.com/questions/11753000/how-to-open-the-google-play-store-directly-from-my-android-application
            if(linkType.equals(MEET)) {
                packageName = googleMeetsPackage;
                launchCommunicationChannel(packageName, pm);
            } else if (linkType.equals(TEAMS)) {
                packageName = microsoftTeamsPackage;
                launchCommunicationChannel(packageName, pm);
            } else if (linkType.equals(ZOOM)) {
                packageName = zoomPackage;
                launchCommunicationChannel(packageName, pm);
            } else if (linkType.equals(SKYPE)) {
                packageName = skypePackage;
                launchCommunicationChannel(packageName, pm);
            }
        }
    }

    private void launchCommunicationChannel(String packageName, PackageManager pm) {
        if (checkForCommunicationAppPackages(packageName,
                pm)) {

            Intent communicationChannelIntent = getPackageManager()
                    .getLaunchIntentForPackage(packageName);
            startActivity(communicationChannelIntent);
        } else {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + packageName)));
            } catch (android.content.ActivityNotFoundException e) {
                //If google play is not installed, launch on the web.
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id="
                                + packageName)));
            }
        }
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

}