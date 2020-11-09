package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.emergency.EmergencyNumber;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.model.CONSTANTS;
import com.example.supervisor_seerem.model.Supervisor;
import com.example.supervisor_seerem.model.database.SupervisorDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class allows the user to enter an save their personal info, and go UIPreferencesActivity and
 * SiteMapActivity.
 * @Author Michael Mora
 */
public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    // Storing of data in Cloud Firebase guided by SmallAcademy @https://www.youtube.com/watch?v=RiHGwJ_u27k

    EditText firstNameInput;
    EditText lastNameInput;
    EditText idInput;
    EditText medicalConsiderationsInput;
    EditText emergencyContactNameInput;
    EditText emergencyContactNumberInput;
    RadioGroup emergencyContactTypes;
    String chosenEmergencyContactType;
    FirebaseAuth firebaseAuthentication;

    Button goToWorksites;

    public static Intent launchUserInfoIntent(Context context) {
        Intent userInfoIntent = new Intent(context, UserInfoActivity.class);
        return userInfoIntent;
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
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.siteNavigation:
                        Intent siteIntent = SiteInfoActivity.launchSiteInfoIntent(UserInfoActivity.this);
                        startActivity(siteIntent);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.mapNavigation:
                        Intent mapIntent = SiteMapActivity.launchMapIntent(UserInfoActivity.this);
                        startActivity(mapIntent);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.sensorNavigation:
                        Intent sensorIntent = SensorsUsageActivity.launchSensorUsageIntent(UserInfoActivity.this);
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

    SupervisorDatabase supervisorDatabase;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private CollectionReference mWorksitesRef = database.collection(CONSTANTS.SUPERVISORS_COLLECTION);
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        setupNavigationBar();

        // The username saved from sharedPreference will become the name
        // Of the document.
        SharedPreferences sharedPrefs = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        currentUser = sharedPrefs.getString("username", null);

        firstNameInput = findViewById(R.id.editFirstName);
        lastNameInput = findViewById(R.id.editLastName);
        idInput = findViewById(R.id.editID);
        medicalConsiderationsInput = findViewById(R.id.editMedical);
        emergencyContactNameInput = findViewById(R.id.editEmergencyContactName);
        emergencyContactTypes = findViewById(R.id.radioContactType);
        emergencyContactNumberInput = findViewById(R.id.editEmergencyNumber);

        firebaseAuthentication = FirebaseAuth.getInstance();
        supervisorDatabase = new SupervisorDatabase(this);

        Button goToUIPreferences = (Button) findViewById(R.id.buttonUIPreferences);
        Button saveUserInfo = (Button) findViewById(R.id.buttonSaveUserInfo);
        Button goToWorkSite = (Button) findViewById(R.id.buttonSiteMap);

        goToUIPreferences.setOnClickListener(this);
        saveUserInfo.setOnClickListener(this);
        goToWorkSite.setOnClickListener(this);

        // TODO: Get user data from Cloud if it exists to autofill options during onCreate()

//        setupButtons();
        emergencyContactTypes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadioButton = (RadioButton) group.findViewById(checkedId);
                chosenEmergencyContactType = selectedRadioButton.getText().toString();
                Toast.makeText(getApplicationContext(), chosenEmergencyContactType, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean areAnyInputsEmpty(String[] inputs){
        for(int i = 0; i < inputs.length; i++){
            Log.i("Checking!: ", inputs[i]);
            if(inputs[i].isEmpty()){
                return false;
            }
            Log.i("OKAY!: ", inputs[i] + " is okay!");
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

        chosenEmergencyContactType = ((RadioButton)findViewById(emergencyContactTypes.getCheckedRadioButtonId())).getText().toString();
        String[] inputs = {firstName, lastName, id, medicalConsiderations, chosenEmergencyContactType,
                emergencyContactNumber, emergencyContactName};

        // Prevent user from saving if they leave any parts of User Info blank.
        if(!areAnyInputsEmpty(inputs)){
            Toast.makeText(this,getText(R.string.error_userinfo_incomplete), Toast.LENGTH_LONG).show();
        }else{

            //Remove original
            int rowsDeleted = supervisorDatabase.wipeData();
            Log.i("Rows deleted", ""+ rowsDeleted);

            // Refer to the collection for storing Supervisors.
            // Within that collection, create a document named after  the user_id
            // If such a document already exists, its contents will be overwritten with the new contents
            // Otherwise, the next line will create appropriately named username.
            DocumentReference supervisorDocRef = database.collection(CONSTANTS.SUPERVISORS_COLLECTION).document(currentUser);
            Map<String,Object> user = new HashMap<>();
            user.put(CONSTANTS.FIRST_NAME_KEY, firstName);
            user.put(CONSTANTS.LAST_NAME_KEY, lastName);
            user.put(CONSTANTS.ID_KEY, id);
            user.put(CONSTANTS.MEDICAL_CONDITIONS_KEY, medicalConsiderations);
            user.put(CONSTANTS.RELATIONSHIP_KEY, chosenEmergencyContactType);
            user.put(CONSTANTS.EMERGENCY_CONTACT_KEY, emergencyContactNumber);
            user.put(CONSTANTS.EMERGENCY_NAME_KEY, emergencyContactName);
            supervisorDocRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), getText(R.string.user_info_save_success), Toast.LENGTH_LONG).show();
                }
            });
            supervisorDocRef.set(user).addOnFailureListener(new OnFailureListener() {
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
            startActivity(new Intent(getBaseContext(), UIPreferencesActivity.class));
        }else if(view.getId() == R.id.buttonSaveUserInfo){
            storeInputs();
        }else if(view.getId() == R.id.buttonSiteMap){
            startActivity(new Intent(getBaseContext(), SiteInfoActivity.class));
        }
    }
}