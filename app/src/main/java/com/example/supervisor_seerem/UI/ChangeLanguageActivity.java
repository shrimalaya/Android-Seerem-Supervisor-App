package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
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

    private String languageChoice;
    private SharedPreferences languageSharedPrefs;

    public static Intent launchChangeLanguageIntent(Context context) {
        Intent languageIntent = new Intent(context, ChangeLanguageActivity.class);
        return languageIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
        setupToolbar();
        languageSharedPrefs = getSharedPreferences("LanguageChoice", Context.MODE_PRIVATE);
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
                launchRestartAppDialog();
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
        if (languageSharedPrefs.getString("language", null) != null) {
            if (languageSharedPrefs.getString("language", null).equals("en")) {
                languageRadioGroup.check(R.id.english_radio_button);
                languageChoice = "en";
            } else if (languageSharedPrefs.getString("language", null).equals("fr")) {
                languageRadioGroup.check(R.id.french_radio_button);
                languageChoice = "fr";
            }
        } else {
            // default
            languageRadioGroup.check(R.id.english_radio_button);
        }
        languageRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedID) {
                switch(checkedID) {
                    case R.id.english_radio_button:
                        languageChoice = "en";
                        break;

                    case R.id.french_radio_button:
                        languageChoice = "fr";
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
        Intent intent = new Intent(this, ChangeLanguageActivity.class);
        finish();
        startActivity(intent);
    }

    private void launchRestartAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangeLanguageActivity.this,
                R.style.AlertDialog);
        builder.setMessage(getString(R.string.restart_app_message));
        builder.setTitle(getString(R.string.restart_app_title));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.restart_app_dialog_positive),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editor = languageSharedPrefs.edit();
                        editor.putString("language", languageChoice);
                        editor.apply();

                        changeLocale(languageChoice);

                        finishAffinity();
                        Intent intent = new Intent(ChangeLanguageActivity.this, LoginInfoActivity.class);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton(getString(R.string.restart_app_dialog_negative),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#B32134"));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#B32134"));
    }
}