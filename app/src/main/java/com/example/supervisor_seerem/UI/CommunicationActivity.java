package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
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
 * Displays different way through which the user can talk to others.
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
        employeeFirstName = getIntent().getStringExtra("EMPLOYEE_FIRST_NAME");
        employeeLastName = getIntent().getStringExtra("EMPLOYEE_LAST_NAME");
        employeePhoneNumber = getIntent().getStringExtra("EMPLOYEE_PHONE_NUMBER");
        employeeEmail = getIntent().getStringExtra("EMPLOYEE_EMAIL");

        //These should be passed as with implicit intents somehow to use with the appropriate app.
        employeeMEETS = getIntent().getStringExtra("EMPLOYEE_MEETS");
        employeeTEAMS = getIntent().getStringExtra("EMPLOYEE_TEAMS");
        employeeZoom = getIntent().getStringExtra("EMPLOYEE_ZOOM");

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
        goToZoom = findViewById(R.id.buttonZoom);


        if(employeeLastName == null || employeeFirstName == null){
            employeeFullName = getString(R.string.communication_no_employee);
        }else{
            employeeFullName = getString(R.string.employee_last_name_first_name, employeeLastName, employeeFirstName);
        }
        // Testing autolink
        // Phone number doesn't work yet...
        if(employeePhoneNumber == null){
            employeePhoneNumber = getString(R.string.communication_no_phone);
        }

        if(employeeEmail == null){
            employeeEmail = getString(R.string.communication_no_email);
        }

        if(employeeMEETS == null){
            employeeMEETS = getString(R.string.communication_no_google_meet);
            goToMeets.setEnabled(false);
        }else{
            employeeMEETS = getString(R.string.communication_google_meet_exists);
            goToMeets.setEnabled(true);
        }

        if(employeeTEAMS == null){
            employeeTEAMS = getString(R.string.communication_no_microsoft_teams);
            goToTeams.setEnabled(false);
        }else{
            employeeTEAMS = getString(R.string.communication_microsoft_teams_exists);
            goToMeets.setEnabled(true);
        }

        if(employeeSkype == null){
            employeeSkype = getString(R.string.communication_no_microsoft_teams);
            goToTeams.setEnabled(false);
        }else{
            employeeSkype = getString(R.string.communication_microsoft_teams_exists);
            goToMeets.setEnabled(true);
        }

        if(employeeZoom == null){
            employeeZoom = getString(R.string.communication_no_zoom);
            goToZoom.setEnabled(false);
        }else{
            employeeZoom = getString(R.string.communication_zoom_exists);
            goToZoom.setEnabled(true);
        }

        communicationRecipient.setText(employeeFullName);
        displayedPhoneNumber.setText(employeePhoneNumber);
        displayedEmail.setText(employeeEmail);
        displayedPhoneNumber.setAutoLinkMask(Linkify.PHONE_NUMBERS);
        displayedMeets.setText(employeeMEETS);
        displayedTeams.setText(employeeTEAMS);
        displayedSkype.setText(employeeSkype);
        displayedZoom.setText(employeeZoom);
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
    // For apps which autolink do not cover, we will have to creat Implicit Intents ourselves.
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.buttonGoogleMeet){

        }else if(view.getId() == R.id.buttonMicrosoftTeams){

        }else if(view.getId() == R.id.buttonSkype){

        }

    }
}