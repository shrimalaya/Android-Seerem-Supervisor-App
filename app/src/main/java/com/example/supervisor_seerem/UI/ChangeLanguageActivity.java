package com.example.supervisor_seerem.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.supervisor_seerem.R;

public class ChangeLanguageActivity extends AppCompatActivity {

    public static Intent launchChangeLanguageIntent(Context context) {
        Intent languageIntent = new Intent(context, ChangeLanguageActivity.class);
        return languageIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
    }
}