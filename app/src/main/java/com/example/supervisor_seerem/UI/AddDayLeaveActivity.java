package com.example.supervisor_seerem.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.supervisor_seerem.R;

public class AddDayLeaveActivity extends AppCompatActivity {

    public static Intent launchAddDayLeaveIntent(Context context) {
        Intent dayLeaveIntent = new Intent(context, AddDayLeaveActivity.class);
        return dayLeaveIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_day_leave);
    }
}