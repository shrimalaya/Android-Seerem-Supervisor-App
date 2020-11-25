package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.supervisor_seerem.R;

public class ChangeLanguageActivity extends AppCompatActivity {

    public static Intent launchChangeLanguageIntent(Context context) {
        Intent languageIntent = new Intent(context, ChangeLanguageActivity.class);
        return languageIntent;
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.back_and_save_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setTitle(R.string.change_language_title_toolbar);
        getSupportActionBar().setTitle(R.string.change_language_title_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
        setupToolbar();
    }

}