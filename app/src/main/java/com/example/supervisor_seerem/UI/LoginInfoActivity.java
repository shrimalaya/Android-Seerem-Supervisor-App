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

    private EditText loginInput;
    private EditText passwordInput;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_info);

        loginInput = findViewById(R.id.editUsername);
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
        String loginToCheck = loginInput.getText().toString();
        String passworkToCheck = passwordInput.getText().toString();
        if (loginToCheck.isEmpty() || passworkToCheck.isEmpty()) {
            if (loginToCheck.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.error_no_username), Toast.LENGTH_LONG).show();
            }
            if (passworkToCheck.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.error_no_password), Toast.LENGTH_LONG).show();
            }
        }else{
            Intent toUserInfo = new Intent(this, UserInfoActivity.class);

            SharedPreferences loginSharedPreferences = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = loginSharedPreferences.edit();
            editor.putString("username", loginToCheck);
            editor.putString("password", passworkToCheck);

            // API suggested apply() instead of commit() to do this storage in the background
            // instead of immediately.
            editor.apply();
            startActivity(toUserInfo);
        }
    }
}