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
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.example.supervisor_seerem.R;

public class ChangeThemeActivity extends AppCompatActivity{

    private RadioGroup themes;
    private RadioButton lightMode;
    private RadioButton darkMode;
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

        if(savedTheme.equals("light")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            modeSwitch.setChecked(false);
        } else if (savedTheme.equals("dark")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            modeSwitch.setChecked(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_theme);

        modeSwitch = findViewById(R.id.switch1);
        modeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        modeSwitch.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                return false;
            }
        });

        applySelectedRadioButton();
        setupToolbar();

    }
}
