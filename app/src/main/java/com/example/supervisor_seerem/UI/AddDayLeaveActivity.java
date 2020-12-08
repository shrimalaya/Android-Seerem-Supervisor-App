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
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.UI.util.DayLeaveAdapter;
import com.example.supervisor_seerem.model.CONSTANTS;
import com.example.supervisor_seerem.model.DocumentManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddDayLeaveActivity extends AppCompatActivity implements View.OnClickListener{


    private DocumentManager documentManager = DocumentManager.getInstance();
    EditText dayLeaveDurationEditText;
    EditText dayLeaveExplanationEditText;
    Button selectDateButton;
    TextView selectedDate;
    String currentDate;

    DayLeaveAdapter mAdapter;
    RecyclerView mRecycler;
    Calendar cal = Calendar.getInstance();

    private List<DocumentSnapshot> mUserDocs = new ArrayList<>();

    private DatePickerDialog.OnDateSetListener dateSetListener;

    private FirebaseFirestore database = FirebaseFirestore.getInstance();

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

    //Get the data from DocumentManager
    private void retrieveData(){

        //Add the user's day leave Documents to the list of data be displayed
        mUserDocs.clear();
        //mAdapter.notifyDataSetChanged();
        for (DocumentSnapshot dayLeave: documentManager.getDayLeaves()) {
            if ((dayLeave.getString(CONSTANTS.ID_KEY)).equals(documentManager.getCurrentUser().getId())) {
                mUserDocs.add(dayLeave);
            }
        }

    }

    // Actually set the contents of the adapter.
    public void displayData(){
        mRecycler = findViewById(R.id.day_leave_requests_recyclerview);
        mAdapter = new DayLeaveAdapter(mUserDocs);
        mRecycler.setAdapter(mAdapter);
    }

    private void refreshDayLeaveData() {
        getDayLeaveData(new DayLeaveCallback() {
            @Override
            public void onCallback(List<DocumentSnapshot> docs) {
                documentManager.setDayLeaves(docs);
                retrieveData();
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private interface DayLeaveCallback {
        void onCallback(List<DocumentSnapshot> docs);
    }

    private void getDayLeaveData(final DayLeaveCallback callback) {
        database.collection(CONSTANTS.PENDING_USER_HOURS_CHANGES_COLLECTION)
                .document(documentManager.getCurrentUser().getId())
                .collection(CONSTANTS.PENDING_DAY_LEAVE_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isComplete()) {
                            callback.onCallback(task.getResult().getDocuments());
                        }
                    }
                });
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

    private boolean isSelectedDateUnique(String selectedDay){
        if(!selectedDay.isEmpty()) {
            for (DocumentSnapshot request: mUserDocs) {
                if(selectedDay.equals(request.getString(CONSTANTS.DAY_LEAVE_DATE_KEY))) {
                    return false;
                }
            }
        }
        return true;
    }

    private void storeInputs(){
        String dayLeaveStartDate = selectedDate.getText().toString();
        String dayLeaveDuration = dayLeaveDurationEditText.getText().toString();

        String selectedDay = selectedDate.getText().toString();

        String[] inputs = {dayLeaveStartDate, dayLeaveDuration};
        // Prevent user from saving if they leave any parts of User Info blank.

        if(!areAnyImportantInputsEmpty(inputs)){
            Toast.makeText(this,getText(R.string.error_request_info_incomplete), Toast.LENGTH_LONG).show();
        }else if(isSelectedDateUnique(selectedDay)){
            // Explanation isn't mandatory.
            String dayLeaveExplantion = dayLeaveExplanationEditText.getText().toString();
            if(dayLeaveExplantion.isEmpty()){
                dayLeaveExplantion = "N/A";
            }

            cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int day = cal.get(Calendar.DAY_OF_MONTH);//Get day in current month since different months hav edifferent avilable days
            int month = cal.get(Calendar.MONTH);

            SimpleDateFormat sdfDocumentName = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss");
            SimpleDateFormat sdfDocumentField = new SimpleDateFormat("MMM-dd");
            cal.set(year, month, day);
            currentDate = sdfDocumentName.format(cal.getTime());
            DocumentReference dayLeaveRef = FirebaseFirestore.getInstance()
                    .collection(CONSTANTS.PENDING_USER_HOURS_CHANGES_COLLECTION)
                    .document(documentManager.getCurrentUser().getId()).collection(CONSTANTS.PENDING_DAY_LEAVE_COLLECTION).document(currentDate);
            Map<String,Object> user = new HashMap<>();
            user.put(CONSTANTS.ID_KEY, documentManager.getCurrentUser().getId());
            user.put(CONSTANTS.DAY_LEAVE_DATE_KEY, dayLeaveStartDate);
            user.put(CONSTANTS.DAY_LEAVE_DURATION_KEY, dayLeaveDuration);
            user.put(CONSTANTS.DAY_LEAVE_EXPLANATION_KEY, dayLeaveExplantion);
            user.put(CONSTANTS.REQUEST_DATE_KEY, sdfDocumentField.format(cal.getTime()));

            dayLeaveRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), getText(R.string.add_day_leave_save_success), Toast.LENGTH_LONG).show();
                }
            });

            dayLeaveRef.set(user).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), getText(R.string.add_day_leave_save_fail), Toast.LENGTH_LONG).show();
                    Log.i("onFailure()", e.toString());
                }
            });
        } else {
            Toast.makeText(this, R.string.select_unique_day, Toast.LENGTH_LONG).show();
        }
    }

    private void setupComponents(){
        selectDateButton = findViewById(R.id.buttonSelectDateDialog);
        selectDateButton.setOnClickListener(this);

        //Display the current date by default
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

    private void selectDateViaDatePickerDialog() {


        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                Log.i("Hello!", monthWord + dayOfMonth + year);
//                selectDateButton.setText(monthWord + dayOfMonth + year);
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
                cal.set(year, month, dayOfMonth);
                String dateString = sdf.format(cal.getTime());
                selectedDate.setText(dateString);
            }
        };

        // Define DatePickerDialog after dateSetListener so
        // to fix a bug where a date has to be selected twice before showing in the selectedDate TextView
        // Clarified by floydheld @ https://stackoverflow.com/a/47239912
        int year = cal.get(Calendar.YEAR);
        int day = cal.get(Calendar.DAY_OF_MONTH);//Get day in current month since different months hav edifferent avilable days
        int month = cal.get(Calendar.MONTH);
            //final String monthWord = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
            DatePickerDialog dialog = new DatePickerDialog(AddDayLeaveActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            dateSetListener, year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save_changes, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.checkmark:
                storeInputs();
                refreshDayLeaveData();
                hideSoftKeyboard();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    // Should update data upon resuming/starting activity
    @Override
    protected void onResume() {
        super.onResume();
        refreshDayLeaveData();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.buttonSelectDateDialog) {
            selectDateViaDatePickerDialog();
        }
    }
}