package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.supervisor_seerem.R;

import java.util.Locale;

public class ChangeLanguageActivity extends AppCompatActivity {

    public static Intent launchChangeLanguageIntent(Context context) {
        Intent languageIntent = new Intent(context, ChangeLanguageActivity.class);
        return languageIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
        setupToolbar();
        setupLanguageRadioButton();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save_changes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.checkmark:
                // TODO: Do whatever in onCreate() or somewhere else,
                //       then save changes accordingly (in SharedPrefs or Database)
                Toast.makeText(this, "Need to save changes", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
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

    private void setupLanguageRadioButton() {
        RadioGroup languageRadioGroup = (RadioGroup) findViewById(R.id.language_radio_group);
        languageRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedID) {
                switch(checkedID) {
                    case R.id.english_radio_button:
                        changeLocale("en");
                        break;

                    case R.id.french_radio_button:
                        changeLocale("fr");
                        break;
                }
            }
        });
    }

    private void changeLocale(String language) {
        Locale newLocale = new Locale(language);
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = newLocale;
        resources.updateConfiguration(configuration, displayMetrics);
    }
}