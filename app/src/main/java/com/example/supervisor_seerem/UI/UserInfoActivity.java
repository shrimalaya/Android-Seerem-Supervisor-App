package com.example.supervisor_seerem.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.example.supervisor_seerem.R;

import java.util.ArrayList;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {

    Button goToWorksites;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        List<RadioButton> contactRadioButtons = new ArrayList<>();

        setupButtons();
    }

    private void setupButtons() {
        goToWorksites = findViewById(R.id.button2);
        goToWorksites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), SiteInfoActivity.class));
            }
        });
    }
}