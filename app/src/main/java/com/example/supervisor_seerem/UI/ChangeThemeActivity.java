package com.example.supervisor_seerem.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.example.supervisor_seerem.R;

import java.util.Locale;

public class ChangeThemeActivity extends AppCompatActivity {

    private String savedTheme;
    SharedPreferences sharedPreferences;

    Switch modeSwitch;

    public static Intent launchChangeThemeIntent(Context context) {
        Intent changeThemeIntent = new Intent(context, ChangeThemeActivity.class);
        return changeThemeIntent;
    }

    private void setupToolbar() {

        Toolbar toolbar = findViewById(R.id.back_and_save_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        sharedPreferences = getSharedPreferences("ThemeData", Context.MODE_PRIVATE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentTheme = sharedPreferences.getString("theme",
                        "light");
                if (currentTheme.equals("light")) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    //Toast.makeText(getApplicationContext(), "Light saved", Toast.LENGTH_SHORT).show();
                } else if (currentTheme.equals("dark")) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    //Toast.makeText(getApplicationContext(), "Dark saved", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
        toolbar.getContext().setTheme(R.style.AppTheme);
    }

    private void switchMode() {
        sharedPreferences = getSharedPreferences("ThemeData", Context.MODE_PRIVATE);
        // Default to Light theme if for whatever reason nothing has been saved
        savedTheme = sharedPreferences.getString("theme", "light");

        if (savedTheme.equals("light")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            modeSwitch.setChecked(false);
        } else if (savedTheme.equals("dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            modeSwitch.setChecked(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_theme);
        setupToolbar();
        changeLocale();
        modeSwitch = findViewById(R.id.switch1);
        switchMode();

        // According to https://stackoverflow.com/a/11278528 setOnCheckedChangeListener()
        // Should be used to check for both switch touches and drags + releases.
        // Using setOnClickListener() will only check for switch touches, meaning drags + releases
        // won't run the desired code.
        modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (modeSwitch.isChecked()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("theme", "dark");
                    editor.apply();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("theme", "light");
                    editor.apply();
                }
            }
        });

    }

    private void changeLocale() {
        SharedPreferences languagePrefs = getSharedPreferences("LanguageChoice", Context.MODE_PRIVATE);
        String language = languagePrefs.getString("language", "en");
        Locale newLocale = new Locale(language);
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = newLocale;
        resources.updateConfiguration(configuration, displayMetrics);
    }
}
