package com.example.supervisor_seerem.UI;

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

/**
 * This class is meant to be the first one the user sees upon launching the app.
 * Allows the user to input a username and password to log in and access the full app functionality
 * These will be stored in Shared Preferences
 * @Author Michael Mora
 */
public class LoginInfoActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_info);

        usernameInput = findViewById(R.id.editUsername);
        passwordInput = findViewById(R.id.editPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInputs();
            }
        });
    }

    private void checkInputs() {
        String usernameToCheck = usernameInput.getText().toString();
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


//            Intent toUserInfo = new Intent(this, UserInfoActivity.class);
            Intent siteInfoIntent = SiteInfoActivity.launchSiteInfoIntent(LoginInfoActivity.this);

            SharedPreferences loginSharedPreferences = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
            String savedUsername = loginSharedPreferences.getString("username", null);
            String savedpassword = loginSharedPreferences.getString("password", null);

            if ((savedUsername == null && savedpassword == null) || // If no Username and Password have been added to the device
                    (!savedUsername.equals(usernameToCheck))){// A new Username denotes a different (and new) account
                SharedPreferences.Editor editor = loginSharedPreferences.edit();
                // API suggested apply() instead of commit() to do this storage in the background
                // instead of immediately.
                editor.putString("username", usernameToCheck);
                    editor.putString("password", passwordToCheck);
                editor.apply();
                startActivity(siteInfoIntent);
            } else {// The Username has been stored but the Password is wrong.
                if (savedUsername.equals(usernameToCheck) && !savedpassword.equals(passwordToCheck)){
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.error_wrong_password), Toast.LENGTH_LONG).show();
                } else if (savedUsername.equals(usernameToCheck) && savedpassword.equals(passwordToCheck)) {
                    startActivity(siteInfoIntent);
                }
            }
        }
    }
}