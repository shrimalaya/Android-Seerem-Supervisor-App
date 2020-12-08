package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.example.supervisor_seerem.R;

public class ChangeThemeActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    private RadioGroup themes;
    private RadioButton lightMode;
    private RadioButton darkMode;
    private String savedTheme;
    SharedPreferences sharedPreferences;

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
                if(currentTheme.equals("light")){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    //Toast.makeText(getApplicationContext(), "Light saved", Toast.LENGTH_SHORT).show();
                } else if (currentTheme.equals("dark")){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    //Toast.makeText(getApplicationContext(), "Dark saved", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
        toolbar.getContext().setTheme(R.style.AppTheme);
    }

    private void applySelectedRadioButton(){
    sharedPreferences = getSharedPreferences("ThemeData", Context.MODE_PRIVATE);
        // Default to Light theme if for whatever reason nothing has been saved
        savedTheme = sharedPreferences.getString("theme", "light");

        //Toast.makeText(getApplicationContext(), "MADE IT TO LIGHT!", Toast.LENGTH_SHORT).show();
        if(savedTheme.equals("light")){
            //Toast.makeText(getApplicationContext(), "MADE IT TO LIGHT!", Toast.LENGTH_SHORT).show();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            lightMode.setChecked(true);
        } else if (savedTheme.equals("dark")){
            //Toast.makeText(getApplicationContext(), "MADE IT TO DARK!!", Toast.LENGTH_SHORT).show();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            darkMode.setChecked(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_theme);
        themes = findViewById(R.id.radioTheme);
        lightMode = findViewById(R.id.radio_button_light);
        darkMode = findViewById(R.id.radio_button_dark);

        themes.setOnCheckedChangeListener(this);
        applySelectedRadioButton();
        setupToolbar();

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton selectedTheme = (RadioButton) group.findViewById(checkedId);
            String stringTheme = selectedTheme.getText().toString();

            //Toast.makeText(getApplicationContext(), "Changed to: " + stringTheme, Toast.LENGTH_SHORT).show();

            // Edit value in Shared Preferences once the mode is selected.
            if(stringTheme.equals(getString(R.string.light_mode_radio_button))){
                //Toast.makeText(getApplicationContext(), "LIGHT!", Toast.LENGTH_SHORT).show();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("theme", "light");
                editor.apply();
            }else if (stringTheme.equals(getString(R.string.dark_mode_radio_button))){
                //Toast.makeText(getApplicationContext(), "DARK!", Toast.LENGTH_SHORT).show();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("theme", "dark");
                editor.apply();
            }
        }
    }
