package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.UI.util.DayLeaveAdapter;
import com.example.supervisor_seerem.UI.util.OvertimeAdapter;
import com.example.supervisor_seerem.model.CONSTANTS;
import com.example.supervisor_seerem.model.DocumentManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddDayLeaveActivity extends AppCompatActivity implements View.OnClickListener{

    EditText dayLeaveMonthEditText;
    EditText dayLeaveDayEditText;
    EditText dayLeaveYearEditText;
    EditText dayLeaveDurationEditText;
    Button selectDateButton;
    TextView selectedDate;
    EditText dayLeaveExplanationEditText;
    String currentDate;

    DayLeaveAdapter mAdapter;
    RecyclerView mRecycler;
    Calendar cal = Calendar.getInstance();

    private List<DocumentSnapshot> mUserDocs = new ArrayList<>();

    private DatePickerDialog.OnDateSetListener dateSetListener;
    DocumentManager documentManager = DocumentManager.getInstance();;
    public static Intent launchAddDayLeaveIntent(Context context) {
        Intent dayLeaveIntent = new Intent(context, AddDayLeaveActivity.class);
        return dayLeaveIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_day_leave);
        setupToolbar();
        setupComponents();

        retrieveData();
        displayData();
        mAdapter.notifyDataSetChanged();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.back_and_save_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setTitle(R.string.add_day_leave_title_toolbar);
        getSupportActionBar().setTitle(R.string.add_day_leave_title_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    //Get the data from DocumentManager
    private void retrieveData(){

        //Add the user's overtime Documents to the list of data be displayed
        mUserDocs.clear();
        //mAdapter.notifyDataSetChanged();
        for (DocumentSnapshot overtime: documentManager.getSickLeaves()) {
            if ((overtime.getString(CONSTANTS.ID_KEY)).equals(documentManager.getCurrentUser().getId())) {
                mUserDocs.add(overtime);
            }
        }

    }

    // Actually set the contents of the adapter.
    public void displayData(){
        mRecycler = findViewById(R.id.day_leave_recyclerview);
        mAdapter = new DayLeaveAdapter(mUserDocs);
        mRecycler.setAdapter(mAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.checkmark:
                // TODO: Do whatever in onCreate() or somewhere else,
                //       then save changes accordingly (in SharedPrefs or Database)

                //Toast.makeText(this, "Need to save changes", Toast.LENGTH_SHORT).show();
                storeInputs();
                //finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupComponents(){
        selectDateButton = findViewById(R.id.buttonSelectDateDialog);
        selectDateButton.setOnClickListener(this);

        selectedDate = findViewById(R.id.textViewSelectedDate);
        dayLeaveDurationEditText = findViewById(R.id.addDayLeaveDuration);
        dayLeaveExplanationEditText = findViewById(R.id.editTextDayLeaveExplanation);
        int year = cal.get(Calendar.YEAR);
        int day = cal.get(Calendar.DAY_OF_MONTH);//Get day in current month since different months hav edifferent avilable days
        int month = cal.get(Calendar.MONTH);

        SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
        cal.set(year, month, day);
        currentDate = sdf.format(cal.getTime());
        selectedDate.setText(currentDate);
    }

    private boolean areAnyImportantInputsEmpty(String[] inputs){
        for (String input : inputs) {
            Log.i("Checking!: ", input);
            if (input.isEmpty()) {
                return false;
            }
            Log.i("OKAY!: ", input + " is okay!");
        }
        return true;
    }

    private void storeInputs(){
        String dayLeaveStartDate = selectedDate.getText().toString();
        String dayLeaveDuration = dayLeaveDurationEditText.getText().toString();

        String[] inputs = {dayLeaveStartDate, dayLeaveDuration};
        // Prevent user from saving if they leave any parts of User Info blank.
        if(!areAnyImportantInputsEmpty(inputs)){
            Toast.makeText(this,getText(R.string.error_request_info_incomplete), Toast.LENGTH_LONG).show();
        }else{

            // Explanation isn't mandatory.
            String dayLeaveExplaantion = dayLeaveExplanationEditText.getText().toString();
            if(dayLeaveExplaantion.isEmpty()){
                dayLeaveExplaantion = "N/A";
            }

//            DocumentReference sickLeaveRef = FirebaseFirestore.getInstance()
//                    .collection(CONSTANTS.PENDING_SICKLEAVE_COLLECTION)
//                    .document(documentManager.getCurrentUser().getId());
            //Store dates not picked using Material Design's calendar.
            // Code adapted from Alex Mamo @https://stackoverflow.com/a/51502409


//            DocumentReference sickLeaveRef = FirebaseFirestore.getInstance()
//                    .collection(CONSTANTS.PENDING_SICKLEAVE_COLLECTION)
//                    .document(documentManager.getCurrentUser().getId()).collection("day_plus_duration_requests").document(currentDate);

            //
//            DocumentReference sickLeaveRef = FirebaseFirestore.getInstance()
//                    .collection(CONSTANTS.PENDING_SICKLEAVE_COLLECTION)
//                    .document(documentManager.getCurrentUser().getId()).collection.document(currentDate);
                        DocumentReference sickLeaveRef = FirebaseFirestore.getInstance()
                    .collection(CONSTANTS.PENDING_USER_HOURS_CHANGES_COLLECTION)
                    .document(documentManager.getCurrentUser().getId()).collection(CONSTANTS.PENDING_SICK_LEAVE_COLLECTION).document(currentDate);
            Map<String,Object> user = new HashMap<>();
            user.put(CONSTANTS.ID_KEY, documentManager.getCurrentUser().getId());
            user.put(CONSTANTS.SICK_DAY_DATE_KEY, dayLeaveStartDate);
            user.put(CONSTANTS.SICK_DAY_DURATION_KEY, dayLeaveDuration);
            user.put(CONSTANTS.SICK_DAY_EXPLANATION_KEY, dayLeaveExplaantion);

            sickLeaveRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), getText(R.string.add_day_leave_save_success), Toast.LENGTH_LONG).show();
                }
            });
            sickLeaveRef.set(user).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), getText(R.string.add_day_leave_save_fail), Toast.LENGTH_LONG).show();
                    Log.i("onFailure()", e.toString());
                }
            });
        }
    }

    private void selectDateViaDatePickerDialog() {
    //    if (Build.VERSION.SDK_INT >= 24) {
            int year = cal.get(Calendar.YEAR);
            int day = cal.get(Calendar.DAY_OF_MONTH);//Get day in current month since different months hav edifferent avilable days
            int month = cal.get(Calendar.MONTH);

            //final String monthWord = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
            DatePickerDialog dialog = new DatePickerDialog(AddDayLeaveActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            dateSetListener, year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                Log.i("Hello!", monthWord + dayOfMonth + year);
//                selectDateButton.setText(monthWord + dayOfMonth + year);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
                    cal.set(year, month, dayOfMonth);
                    String dateString = sdf.format(cal.getTime());
                    selectedDate.setText(dateString);
                }
            };

        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save_changes, menu);
        return true;
    }

    // Should update data upon resuming/starting activity
    @Override
    protected void onResume() {
        super.onResume();
        retrieveData();
        displayData();
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.buttonSelectDateDialog) {
            selectDateViaDatePickerDialog();
        }
    }


}