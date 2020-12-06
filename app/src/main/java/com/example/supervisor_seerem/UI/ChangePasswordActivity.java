package com.example.supervisor_seerem.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.supervisor_seerem.R;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    EditText currentPasswordEditText;
    EditText newPasswordEditText;

    Button updatePassword;

    SharedPreferences loginSharedPreferences;

    String savedPassword;

    public static Intent launchChangePasswordIntent(Context context) {
        Intent changePasswordIntent = new Intent(context, ChangePasswordActivity.class);
        return changePasswordIntent;
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.back_and_save_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        loginSharedPreferences = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        savedPassword = loginSharedPreferences.getString("password", null);
        setupToolbar();
        setupViews();

    }

    private void setupViews() {
        currentPasswordEditText = findViewById(R.id.currentPassword);
        newPasswordEditText = findViewById(R.id.newPassword);
        updatePassword = findViewById(R.id.buttonUpdatePassowrd);
        updatePassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String currentPasswordToCheck = currentPasswordEditText.getText().toString();
        String newPasswordToCheck = newPasswordEditText.getText().toString();

        //If the user input a current and new password
        if (!currentPasswordToCheck.isEmpty() && !newPasswordToCheck.isEmpty()) {
            /**
             Now check if the user's inputted current and new are the same
             This is checked first instead of the else if statement. This makes it so
             the user will now be stopped if they input the same
             hings before they are told if their inputted current password is right or not.
             (If it was the other way around, the user can tell if they guessed the right password
             if the "current and new password are the same prompt" is shown because that toast
             would only possibly be shown after it is confirmed that the inputted current password
             is the same as the saved password.)
             */
            if (currentPasswordToCheck.equals(newPasswordToCheck)) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.change_password_current_and_new_are_same
                        ), Toast.LENGTH_LONG).show();
            } else if (!currentPasswordToCheck.equals(savedPassword)) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.change_password_wrong_current_password),
                        Toast.LENGTH_LONG).show();
            } else {
                // Success. Store password now and return to UserInfo
                Toast.makeText(getApplicationContext(),
                        getString(R.string.change_password_successful_change),
                        Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = loginSharedPreferences.edit();
                editor.putString("password", newPasswordToCheck);
                editor.apply();
                savedPassword = loginSharedPreferences.getString("password", null);

                finish();
                Intent intent = UserInfoActivity.launchUserInfoIntent(ChangePasswordActivity.this);
                startActivity(intent);
            }
        } else { // The user did not input something for the current and/or new password
            if (currentPasswordToCheck.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.change_password_current_empty),
                        Toast.LENGTH_LONG).show();
            }
            if (newPasswordToCheck.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.change_password_new_empty), Toast.LENGTH_LONG).show();
            }
        }
    }
}