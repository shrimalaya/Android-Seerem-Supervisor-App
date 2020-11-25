package com.example.supervisor_seerem.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.supervisor_seerem.R;

public class ChangePasswordActivity extends AppCompatActivity {

    public static Intent launchChangePasswordIntent(Context context) {
        Intent changePasswordIntent = new Intent(context, ChangePasswordActivity.class);
        return changePasswordIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
    }
}