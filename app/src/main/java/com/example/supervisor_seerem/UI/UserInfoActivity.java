package com.example.supervisor_seerem.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.emergency.EmergencyNumber;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.model.database.SupervisorDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * This class allows the user to enter an save their personal info, and go UIPreferencesActivity and
 * SiteMapActivity.
 * @Author Michael Mora
 */
public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    EditText firstNameInput;
    EditText lastNameInput;
    EditText idInput;
    EditText medicalConsiderationsInput;
    EditText emergencyContactNameInput;
    EditText emergencyContactNumberInput;
    RadioGroup emergencyContactTypes;
    String chosenEmergencyContactType;

    SupervisorDatabase supervisorDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        firstNameInput = findViewById(R.id.editFirstName);
        lastNameInput = findViewById(R.id.editLastName);
        idInput = findViewById(R.id.editID);
        medicalConsiderationsInput = findViewById(R.id.editMedical);
        emergencyContactNameInput = findViewById(R.id.editEmergencyContactName);
        emergencyContactTypes = findViewById(R.id.radioContactType);
        emergencyContactNumberInput = findViewById(R.id.editEmergencyNumber);

        supervisorDatabase = new SupervisorDatabase(this);

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
                Toast.makeText(getApplicationContext(), chosenEmergencyContactType, Toast.LENGTH_LONG).show();
            }
        });
        List<RadioButton> contactRadioButtons = new ArrayList<>();
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

            long result = supervisorDatabase.insertSupervisorData(firstName, lastName,
                    id, medicalConsiderations, chosenEmergencyContactType,
                    Integer.parseInt(emergencyContactNumberInput.getText().toString()),
                    emergencyContactName);
            // For testing ---
            // the database returns -1 if an error occured.
            if (result < 0) {
                Toast.makeText(this, "data not inserted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "data inserted!", Toast.LENGTH_SHORT).show();
            }
        }
        // ---
    }

    // Changed settings will not be saved unless the user explicitly saves the info.
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