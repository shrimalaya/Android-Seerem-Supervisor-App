package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.model.CONSTANTS;
import com.example.supervisor_seerem.model.DocumentManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class allows the user to enter an save their personal info, and go UIPreferencesActivity and
 * SiteMapActivity.
 * @Author Michael Mora
 */
public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseFirestore mRef = FirebaseFirestore.getInstance();
    private DocumentManager documentManager = DocumentManager.getInstance();

    // Storing of data in Cloud Firebase guided by SmallAcademy @https://www.youtube.com/watch?v=RiHGwJ_u27k

    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText idInput;
    private EditText medicalConsiderationsInput;
    private EditText emergencyContactNameInput;
    private EditText emergencyContactNumberInput;
    private RadioGroup emergencyContactTypes;
    private RadioButton emergencyTypeFamily;
    private RadioButton emergencyTypeFriend;
    private String chosenEmergencyContactType;
    private FirebaseAuth firebaseAuthentication;
    private DrawerLayout drawer;

    public static Intent launchUserInfoIntent(Context context) {
        Intent userInfoIntent = new Intent(context, UserInfoActivity.class);
        return userInfoIntent;
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
                        // just close sidebar because it goes to the same activity
                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.sidebar_overtime:
                        Intent overtimeIntent = AddOvertimeActivity.launchAddOvertimeIntent(UserInfoActivity.this);
                        startActivity(overtimeIntent);
                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.sidebar_day_leave:
                        Intent dayLeaveIntent = AddDayLeaveActivity.launchAddDayLeaveIntent(UserInfoActivity.this);
                        startActivity(dayLeaveIntent);
                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.sidebar_search:
                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.sidebar_all_workers:
                        Intent workerIntent = WorkerInfoActivity.launchWorkerInfoIntent(UserInfoActivity.this);
                        startActivity(workerIntent);
                        finish();
                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.sidebar_company:
                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.sidebar_ui_preferences:
                        Intent uiPrefsIntent = UIPreferencesActivity.launchUIPreferencesIntent(UserInfoActivity.this);
                        startActivity(uiPrefsIntent);
                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.sidebar_light_dark_mode:
                        Intent changeThemeIntent = ChangeThemeActivity.launchChangeThemeIntent(UserInfoActivity.this);
                        startActivity(changeThemeIntent);
                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.sidebar_languages:
                        Intent changeLanguageIntent = ChangeLanguageActivity.launchChangeLanguageIntent(UserInfoActivity.this);
                        startActivity(changeLanguageIntent);
                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.sidebar_change_password:
                        Intent changePasswordIntent = ChangePasswordActivity.launchChangePasswordIntent(UserInfoActivity.this);
                        startActivity(changePasswordIntent);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                }
                return true;
            }
        });
    }

    private void setupNavigationBar() {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavigationBar);
        navigation.setSelectedItemId(R.id.userNavigation);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.workerNavigation:
                        Intent workerIntent = WorkerInfoActivity.launchWorkerInfoIntent(UserInfoActivity.this);
                        startActivity(workerIntent);
                        finish();
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.siteNavigation:
                        Intent siteIntent = SiteInfoActivity.launchSiteInfoIntent(UserInfoActivity.this);
                        startActivity(siteIntent);
                        finish();
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.mapNavigation:
                        Intent mapIntent = SiteMapActivity.launchMapIntent(UserInfoActivity.this);
                        startActivity(mapIntent);
                        finish();
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.sensorNavigation:
                        Intent sensorIntent = SensorsUsageActivity.launchSensorUsageIntent(UserInfoActivity.this);
                        startActivity(sensorIntent);
                        finish();
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

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finishAffinity();
            Intent intent = new Intent(UserInfoActivity.this, LoginInfoActivity.class);
            startActivity(intent);
        }
    }


    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        setupNavigationBar();
        setupSidebarNavigationDrawer();

        // The username saved from sharedPreference will become the name
        // Of the document.
        SharedPreferences sharedPrefs = getSharedPreferences("LoginData", Context.MODE_PRIVATE);

        firstNameInput = findViewById(R.id.editFirstName);
        firstNameInput.setClickable(false);
        lastNameInput = findViewById(R.id.editLastName);
        lastNameInput.setClickable(false);
        idInput = findViewById(R.id.editID);
        idInput.setClickable(false);

        medicalConsiderationsInput = findViewById(R.id.editMedical);
        emergencyContactNameInput = findViewById(R.id.editEmergencyContactName);
        emergencyContactTypes = findViewById(R.id.radioContactType);
        emergencyContactNumberInput = findViewById(R.id.editEmergencyNumber);
        emergencyTypeFamily = findViewById(R.id.radio_family);
        emergencyTypeFriend = findViewById(R.id.radio_friend);

        firebaseAuthentication = FirebaseAuth.getInstance();

        retrieveAllData();

        Button goToUIPreferences = (Button) findViewById(R.id.buttonUIPreferences);
        Button saveUserInfo = (Button) findViewById(R.id.buttonSaveUserInfo);
        Button goToWorkSite = (Button) findViewById(R.id.buttonSiteMap);

        goToUIPreferences.setOnClickListener(this);
        saveUserInfo.setOnClickListener(this);
        goToWorkSite.setOnClickListener(this);

        emergencyContactTypes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadioButton = (RadioButton) group.findViewById(checkedId);
                chosenEmergencyContactType = selectedRadioButton.getText().toString();
                //Toast.makeText(getApplicationContext(), chosenEmergencyContactType, Toast.LENGTH_LONG).show();
            }
        });

    }

    private boolean areAnyInputsEmpty(String[] inputs){
        for (String input : inputs) {
            Log.i("Checking!: ", input);
            if (input.isEmpty()) {
                return false;
            }
            Log.i("OKAY!: ", input + " is okay!");
        }
        return true;
    }

    private void storeInputs(){
        String firstName = firstNameInput.getText().toString();
        String lastName = lastNameInput.getText().toString();
        String id = idInput.getText().toString();
        String medicalConsiderations = medicalConsiderationsInput.getText().toString();
        String emergencyContactNumber = emergencyContactNumberInput.getText().toString();
        String emergencyContactName = emergencyContactNameInput.getText().toString();

        // Check if no radio button is checked; make it an empty string if so, to prevent crashing
        if(emergencyContactTypes.getCheckedRadioButtonId() == -1){
            chosenEmergencyContactType = "";
        }else{
            chosenEmergencyContactType = ((RadioButton) findViewById(emergencyContactTypes.getCheckedRadioButtonId())).getText().toString();
        }
        
        String[] inputs = {firstName, lastName, id, medicalConsiderations, chosenEmergencyContactType,
                emergencyContactNumber, emergencyContactName};

        // Prevent user from saving if they leave any parts of User Info blank.
        if(!areAnyInputsEmpty(inputs)){
            Toast.makeText(this,getText(R.string.error_userinfo_incomplete), Toast.LENGTH_LONG).show();
        }else{
            // Refer to the collection for storing Supervisors.
            // Within that collection, create a document named after the user_id
            // If such a document already exists, its contents will be overwritten with the new contents
            // Otherwise, the next line will create appropriately named username.

            DocumentReference emergencyRef = FirebaseFirestore.getInstance()
                    .collection(CONSTANTS.EMERGENCY_INFO_COLLECTION)
                    .document(documentManager.getCurrentUser().getId());
            Map<String,Object> user = new HashMap<>();
            user.put(CONSTANTS.ID_KEY, documentManager.getCurrentUser().getId());
            user.put(CONSTANTS.MEDICAL_CONDITIONS_KEY, medicalConsiderations);
            user.put(CONSTANTS.RELATIONSHIP_KEY, chosenEmergencyContactType);
            user.put(CONSTANTS.EMERGENCY_CONTACT_KEY, emergencyContactNumber);
            user.put(CONSTANTS.EMERGENCY_NAME_KEY, emergencyContactName);

            emergencyRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), getText(R.string.user_info_save_success), Toast.LENGTH_LONG).show();
                }
            });
            emergencyRef.set(user).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), getText(R.string.user_info_save_fail), Toast.LENGTH_LONG).show();
                    Log.i("onFailure()", e.toString());
                }
            });
        }
    }

    // Changed settings will not be saved unless the user clicks the save button
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.buttonUIPreferences){
            //Passing of intents should go in the code for the Button going to CommunicationActivity
            //Testing... why do t
//            Intent intent = new Intent(UserInfoActivity.this, CommunicationActivity.class);
//            intent.putExtra( "EMPLOYEE_FIRST_NAME", "Beep");
//            intent.putExtra("EMPLOYEE_LAST_NAME","Boop");
//            intent.putExtra("EMPLOYEE_ZOOM", "https://zoom.us/");
//            intent.putExtra("EMPLOYEE_PHONE_NUMBER", "1234567890");
//            intent.putExtra("EMPLOYEE_EMAIL", "test@gmail.com");
//            intent.putExtra("EMPLOYEE_MEETS", "A google meets link");
//            intent.putExtra("EMPLOYEE_TEAMS", "A team");
//            startActivity(intent);
        }else if(view.getId() == R.id.buttonSaveUserInfo){
            storeInputs();
        }else if(view.getId() == R.id.buttonSiteMap){
            startActivity(new Intent(getBaseContext(), SiteInfoActivity.class));
        }
    }


    private interface DocListCallback{
        void onCallback(List<DocumentSnapshot> docs);
    }

    private void retrieveAllData() {
        getSupervisorData(new DocListCallback() {
            @Override
            public void onCallback(List<DocumentSnapshot> docs) {
                documentManager.setSupervisors(docs);

                getEmergencyData(new DocListCallback() {
                    @Override
                    public void onCallback(List<DocumentSnapshot> docs) {
                        documentManager.setEmergencyInfo(docs);

                        getContactData(new DocListCallback() {
                            @Override
                            public void onCallback(List<DocumentSnapshot> docs) {
                                documentManager.setContacts(docs);

                                getWorkersData(new DocListCallback() {
                                    @Override
                                    public void onCallback(List<DocumentSnapshot> docs) {
                                        documentManager.setWorkers(docs);
                                    }
                                });

                                getAvailabilityData(new DocListCallback() {
                                    @Override
                                    public void onCallback(List<DocumentSnapshot> docs) {
                                        documentManager.setAvailabilities(docs);
                                    }
                                });

                                getSitesData(new DocListCallback() {
                                    @Override
                                    public void onCallback(List<DocumentSnapshot> docs) {
                                        documentManager.setSites(docs);
                                        populateData();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private void populateData() {
        DocumentSnapshot userEmergencyInfo = null;
        for(DocumentSnapshot document: documentManager.getEmergencyInfo()) {
            System.out.println("TEST3> User ID: " + documentManager.getCurrentUser().getId());
            System.out.println("TEST3> EMERGENCY ID: " + document.getString(CONSTANTS.ID_KEY));
            if(document.getString(CONSTANTS.ID_KEY).equals(documentManager.getCurrentUser().getId())) {
                userEmergencyInfo = document;
                System.out.println("TEST3> USER EMERGENCY DOC FOUND");
            }
        }

        if (userEmergencyInfo == null) {
            Toast.makeText(getApplicationContext(), "NO USER EMERGENCY FOUND",
                    Toast.LENGTH_LONG).show();
        } else {
            String savedFirstName = documentManager.getCurrentUser().getFirstName();
            String savedLastName = documentManager.getCurrentUser().getLastName();
            String savedID = documentManager.getCurrentUser().getId();
            String savedMedicalConsiderations = userEmergencyInfo.getString(CONSTANTS.MEDICAL_CONDITIONS_KEY);
            String savedEmergencyContactType = userEmergencyInfo.getString(CONSTANTS.RELATIONSHIP_KEY);
            String savedEmergencyContactNumber = userEmergencyInfo.getString(CONSTANTS.EMERGENCY_CONTACT_KEY);
            String savedEmergencyContactName = userEmergencyInfo.getString(CONSTANTS.EMERGENCY_NAME_KEY);

            firstNameInput.setText(savedFirstName.toUpperCase());
            lastNameInput.setText(savedLastName.toUpperCase());
            idInput.setText(savedID);
            medicalConsiderationsInput.setText(savedMedicalConsiderations.toUpperCase());
            emergencyContactNameInput.setText(savedEmergencyContactName);
            //emergencyContactTypes = findViewById(R.id.radioContactType);
            emergencyContactNumberInput.setText(savedEmergencyContactNumber);
            if (savedEmergencyContactType.equals("Family")) {
                emergencyTypeFamily.setChecked(true);
                emergencyTypeFriend.setChecked(false);
            } else if (savedEmergencyContactType.equals("Friend")) {
                emergencyTypeFriend.setChecked(true);
                emergencyTypeFamily.setChecked(false);
            } else {
                emergencyTypeFamily.setChecked(true);
                emergencyTypeFriend.setChecked(false);
            }
        }
    }

    private void getWorkersData(final DocListCallback callback) {
        mRef.collection(CONSTANTS.WORKERS_COLLECTION)
                .whereEqualTo(CONSTANTS.COMPANY_ID_KEY, documentManager.getCurrentUser().getCompany_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isComplete()) {
                            System.out.println("TEST3> Size of workers = " + task.getResult().getDocuments().size());
                            callback.onCallback(task.getResult().getDocuments());
                        }
                    }
                });
    }

    private void getSitesData(final DocListCallback callback) {
        mRef.collection(CONSTANTS.WORKSITES_COLLECTION)
                .whereEqualTo(CONSTANTS.COMPANY_ID_KEY, CONSTANTS.USER_COMPANY)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isComplete()) {
                            System.out.println("TEST3> Size of sites = " + task.getResult().getDocuments().size());
                            callback.onCallback(task.getResult().getDocuments());
                        }
                    }
                });
    }

    private void getAvailabilityData(final DocListCallback callback) {
        mRef.collection(CONSTANTS.AVAILABILITY_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isComplete()) {
                            System.out.println("TEST3> Size of availability = " + task.getResult().getDocuments().size());
                            callback.onCallback(task.getResult().getDocuments());
                        }
                    }
                });
    }

    private void getContactData(final DocListCallback callback) {
        mRef.collection(CONSTANTS.CONTACT_INFO_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isComplete()) {
                            System.out.println("TEST3> Size of contacts = " + task.getResult().getDocuments().size());
                            callback.onCallback(task.getResult().getDocuments());
                        }
                    }
                });
    }

    private void getEmergencyData(final DocListCallback callback) {
        mRef.collection(CONSTANTS.EMERGENCY_INFO_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isComplete()) {
                            System.out.println("TEST3> Size of emergency = " + task.getResult().getDocuments().size());
                            callback.onCallback(task.getResult().getDocuments());
                        }
                    }
                });
    }

    private void getSupervisorData(final DocListCallback callback) {
        mRef.collection(CONSTANTS.SUPERVISORS_COLLECTION)
                .whereEqualTo(CONSTANTS.ID_KEY, documentManager.getCurrentUser().getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isComplete()) {
                            System.out.println("TEST3> Size of supervisors = " + task.getResult().getDocuments().size());
                            callback.onCallback(task.getResult().getDocuments());
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("TEST3> Resuming UserInfo Activity");
    }
}



