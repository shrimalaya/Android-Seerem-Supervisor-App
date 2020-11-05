package com.example.supervisor_seerem.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.model.CONSTANTS;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CONSTANTS.USER_ID = "sladha";

        Intent intent = new Intent(MainActivity.this, SiteInfoActivity.class);
        startActivity(intent);
    }
}