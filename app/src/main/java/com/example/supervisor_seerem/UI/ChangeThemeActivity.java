package com.example.supervisor_seerem.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.supervisor_seerem.R;

public class ChangeThemeActivity extends AppCompatActivity {

    public static Intent launchChangeThemeIntent(Context context) {
        Intent changeThemeIntent = new Intent(context, ChangeThemeActivity.class);
        return changeThemeIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_theme);
    }
}