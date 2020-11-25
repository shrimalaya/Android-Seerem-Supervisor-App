package com.example.supervisor_seerem.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.supervisor_seerem.R;

public class AddOvertimeActivity extends AppCompatActivity {

    public static Intent launchAddOvertimeIntent(Context context) {
        Intent overtimeIntent = new Intent(context, AddOvertimeActivity.class);
        return overtimeIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_overtime);
    }
}