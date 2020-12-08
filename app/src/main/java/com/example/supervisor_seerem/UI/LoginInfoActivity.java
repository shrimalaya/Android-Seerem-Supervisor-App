package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

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
import java.util.Locale;

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
    List<DocumentSnapshot> allSupervisors = new ArrayList<>();

    DocumentManager manager;
    SharedPreferences sharedPreferences;
    String savedTheme;

    //Apply theme from startup. This will affect the rest of the app.
    private void applyTheme(){
        SharedPreferences themePrefs = getSharedPreferences("ThemeData", Context.MODE_PRIVATE);
        savedTheme = themePrefs.getString("theme",
                getString(R.string.light_mode_radio_button));
        switch(savedTheme){
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }

    private void changeLocale() {
        SharedPreferences languagePrefs = getSharedPreferences("LanguageChoice", Context.MODE_PRIVATE);
        String language = languagePrefs.getString("language", "");
        Locale newLocale = new Locale(language);
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = newLocale;
        resources.updateConfiguration(configuration, displayMetrics);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeLocale();
        setContentView(R.layout.activity_login_info);
        manager = DocumentManager.getInstance();
        sharedPreferences = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        applyTheme();


        usernameInput = findViewById(R.id.editUsername);
        if(sharedPreferences.getString("username", "") != null) {
            usernameInput.setText(sharedPreferences.getString("username", ""));
        }

        passwordInput = findViewById(R.id.editPassword);
        buttonLogin = findViewById(R.id.buttonLogin);


        getSupervisorData(new DocListCallback() {
            @Override
            public void onCallback(List<DocumentSnapshot> docs) {
                allSupervisors.clear();
                allSupervisors.addAll(docs);
                Log.d("LOGININFO", "Size of allSupervisors = " + allSupervisors.size());

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
        final ProgressDialog progressDialog = new ProgressDialog(LoginInfoActivity.this);
        progressDialog.setMessage(getString(R.string.fetching_data_message));
        progressDialog.setTitle(getString(R.string.logging_in_title));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        String usernameToCheck = usernameInput.getText().toString();
        usernameToCheck = usernameToCheck.toUpperCase();
        String passwordToCheck = passwordInput.getText().toString();
        if (usernameToCheck.isEmpty() || passwordToCheck.isEmpty()) {
            if (usernameToCheck.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.error_no_username), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
            if (passwordToCheck.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.error_no_password), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        } else {
            final Intent toUserInfo = new Intent(this, UserInfoActivity.class);
//            Intent siteInfoIntent = SiteInfoActivity.launchSiteInfoIntent(LoginInfoActivity.this);


            String savedUsername = sharedPreferences.getString("username", "");
            String savedpassword = sharedPreferences.getString("password", "");

            if ((savedUsername == null && savedpassword == null) || // If no Username and Password have been added to the device
                    (!savedUsername.equals(usernameToCheck))) {// A new Username denotes a different (and new) account

                DocumentSnapshot user = null;
                for (DocumentSnapshot doc : allSupervisors) {
                    if (doc.get(CONSTANTS.ID_KEY).equals(usernameToCheck)) {
                        user = doc;
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        // API suggested apply() instead of commit() to do this storage in the background
                        // instead of immediately.
                        editor.putString("username", usernameToCheck);
                        editor.putString("password", passwordToCheck);
                        editor.apply();

                        manager.setCurrentUser(new Supervisor(user.getString(CONSTANTS.ID_KEY), user.getString(CONSTANTS.FIRST_NAME_KEY)
                                , user.getString(CONSTANTS.LAST_NAME_KEY), user.getString(CONSTANTS.COMPANY_ID_KEY)));

                        manager.retrieveAllData(new DocumentManager.RetrieveCallback() {
                            @Override
                            public void onCallback(Boolean result) {
                                if(result) {
                                    progressDialog.dismiss();
                                    startActivity(toUserInfo);
                                    finish();
                                }
                            }
                        });
                    }
                }

                if (user == null) {
                    Toast.makeText(this, getString(R.string.login_no_username_found), Toast.LENGTH_LONG).show();
                }

            } else {// The Username has been stored but the Password is wrong.
                if (savedUsername.equals(usernameToCheck) && !savedpassword.equals(passwordToCheck)) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.error_wrong_password), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                } else if (savedUsername.equals(usernameToCheck) && savedpassword.equals(passwordToCheck)) {
                    DocumentSnapshot user = null;
                    for (DocumentSnapshot doc : allSupervisors) {
                        if (doc.get(CONSTANTS.ID_KEY).equals(usernameToCheck)) {
                            user = doc;

                            manager.setCurrentUser(new Supervisor(user.getString(CONSTANTS.ID_KEY), user.getString(CONSTANTS.FIRST_NAME_KEY)
                                    , user.getString(CONSTANTS.LAST_NAME_KEY), user.getString(CONSTANTS.COMPANY_ID_KEY)));

                            manager.retrieveAllData(new DocumentManager.RetrieveCallback() {
                                @Override
                                public void onCallback(Boolean result) {
                                    if(result) {
                                        progressDialog.dismiss();
                                        startActivity(toUserInfo);
                                        finish();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
    }


    private interface DocListCallback {
        void onCallback(List<DocumentSnapshot> docs);
    }

    private void getSupervisorData(final DocListCallback callback) {
        mRef.collection("supervisors")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isComplete()) {
                            callback.onCallback(task.getResult().getDocuments());
                        }
                    }
                });
    }
}