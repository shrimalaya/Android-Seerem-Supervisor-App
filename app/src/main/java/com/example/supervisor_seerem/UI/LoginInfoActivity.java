package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.model.CONSTANTS;
import com.example.supervisor_seerem.model.DocumentManager;
import com.example.supervisor_seerem.model.Supervisor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is meant to be the first one the user sees upon launching the app.
 * Allows the user to input a username and password to log in and access the full app functionality
 * These will be stored in Shared Preferences
 */
public class LoginInfoActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button buttonLogin;
    FirebaseFirestore mRef = FirebaseFirestore.getInstance();
    private DocumentManager manager = DocumentManager.getInstance();
    List<DocumentSnapshot> allSupervisors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_info);

        usernameInput = findViewById(R.id.editUsername);
        passwordInput = findViewById(R.id.editPassword);
        buttonLogin = findViewById(R.id.buttonLogin);


        getSupervisorData(new DocListCallback() {
            @Override
            public void onCallback(List<DocumentSnapshot> docs) {
                allSupervisors.clear();
                allSupervisors.addAll(docs);
                System.out.println("TEST3> Size of allSupervisors = " + allSupervisors.size());

                buttonLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkInputs();
                    }
                });
            }
        });
    }

    private void checkInputs() {
        String usernameToCheck = usernameInput.getText().toString();
        usernameToCheck = usernameToCheck.toUpperCase();
        String passwordToCheck = passwordInput.getText().toString();
        if (usernameToCheck.isEmpty() || passwordToCheck.isEmpty()) {
            if (usernameToCheck.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.error_no_username), Toast.LENGTH_LONG).show();
            }
            if (passwordToCheck.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.error_no_password), Toast.LENGTH_LONG).show();
            }
        } else {
            Intent toUserInfo = new Intent(this, UserInfoActivity.class);
//            Intent siteInfoIntent = SiteInfoActivity.launchSiteInfoIntent(LoginInfoActivity.this);

            SharedPreferences loginSharedPreferences = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
            String savedUsername = loginSharedPreferences.getString("username", null);
            String savedpassword = loginSharedPreferences.getString("password", null);

            if ((savedUsername == null && savedpassword == null) || // If no Username and Password have been added to the device
                    (!savedUsername.equals(usernameToCheck))) {// A new Username denotes a different (and new) account

                DocumentSnapshot user = null;
                for(DocumentSnapshot doc: allSupervisors) {
                    if(doc.get(CONSTANTS.ID_KEY).equals(usernameToCheck)) {
                        user = doc;
                        SharedPreferences.Editor editor = loginSharedPreferences.edit();
                        // API suggested apply() instead of commit() to do this storage in the background
                        // instead of immediately.
                        editor.putString("username", usernameToCheck);
                        editor.putString("password", passwordToCheck);
                        editor.apply();

                        manager.setCurrentUser(new Supervisor(user.getString(CONSTANTS.ID_KEY), user.getString(CONSTANTS.FIRST_NAME_KEY)
                                , user.getString(CONSTANTS.LAST_NAME_KEY), user.getString(CONSTANTS.COMPANY_ID_KEY)));
                        CONSTANTS.USER_COMPANY = user.getString(CONSTANTS.COMPANY_ID_KEY);
                        CONSTANTS.USER_ID = user.getString(CONSTANTS.ID_KEY);

                        System.out.println("TEST3> New user id: " + manager.getCurrentUser().getFirstName());

                        startActivity(toUserInfo);
                        break;
                    }
                }

                if(user == null) {
                    Toast.makeText(this, "Username not found in Supervisor database!", Toast.LENGTH_LONG).show();
                }

            } else {// The Username has been stored but the Password is wrong.
                if (savedUsername.equals(usernameToCheck) && !savedpassword.equals(passwordToCheck)){
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.error_wrong_password), Toast.LENGTH_LONG).show();
                } else if (savedUsername.equals(usernameToCheck) && savedpassword.equals(passwordToCheck)) {
                    DocumentSnapshot user = null;
                    for(DocumentSnapshot doc: allSupervisors) {
                        if (doc.get(CONSTANTS.ID_KEY).equals(usernameToCheck)) {
                            user = doc;

                            manager.setCurrentUser(new Supervisor(user.getString(CONSTANTS.ID_KEY), user.getString(CONSTANTS.FIRST_NAME_KEY)
                                    , user.getString(CONSTANTS.LAST_NAME_KEY), user.getString(CONSTANTS.COMPANY_ID_KEY)));
                            CONSTANTS.USER_COMPANY = user.getString(CONSTANTS.COMPANY_ID_KEY);
                            CONSTANTS.USER_ID = user.getString(CONSTANTS.ID_KEY);

                            System.out.println("TEST3> curr user id: " + manager.getCurrentUser().getId());

                            startActivity(toUserInfo);
                            break;
                        }
                    }
                }
            }
        }
    }


    private interface DocListCallback{
        void onCallback(List<DocumentSnapshot> docs);
    }

    private void getSupervisorData(final DocListCallback callback) {
        mRef.collection("supervisors")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isComplete()) {

                            System.out.println("TEST3> Size of allSupervisors = " + task.getResult().getDocuments().size());

                            callback.onCallback(task.getResult().getDocuments());
                        }
                    }
                });
    }
}