package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.UI.util.OvertimeAdapter;
import com.example.supervisor_seerem.model.CONSTANTS;
import com.example.supervisor_seerem.model.DocumentManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddOvertimeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private DocumentManager documentManager = DocumentManager.getInstance();
    EditText editTextOvertimeHours;
    EditText editTextOvertimeExplanation;
    Spinner spinner;
    OvertimeAdapter mAdapter;
    RecyclerView mRecycler;

    private List<DocumentSnapshot> mAllDocs = new ArrayList<>();
    private List<DocumentSnapshot> mUserDocs = new ArrayList<>();

    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    Calendar cal;

    public static Intent launchAddOvertimeIntent(Context context) {
        Intent overtimeIntent = new Intent(context, AddOvertimeActivity.class);
        return overtimeIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_overtime);

        setupToolbar();
        setUpComponents();

        retrieveData();
        displayData();
        mAdapter.notifyDataSetChanged();
    }

    //Get the data from DocumentManager
    private void retrieveData(){
        //Add the user's overtime Documents to the list of data be displayed
        mUserDocs.clear();
        //mAdapter.notifyDataSetChanged();
        for (DocumentSnapshot overtime: documentManager.getOvertime()) {
            if ((overtime.getString(CONSTANTS.ID_KEY)).equals(documentManager.getCurrentUser().getId())) {
                mUserDocs.add(overtime);
            }
        }
    }

    // Actually set the contents of the adapter.
    public void displayData(){
        mRecycler = findViewById(R.id.overtime_view_requests_recyclerview);
        mAdapter = new OvertimeAdapter(mUserDocs);
        mRecycler.setAdapter(mAdapter);
    }

    // Refresh data locally
    private void refreshOvertimeData() {
        getOvertimeData(new OvertimeCallback() {
            @Override
            public void onCallback(List<DocumentSnapshot> docs) {
                documentManager.setOvertime(docs);
                retrieveData();
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private interface OvertimeCallback {
        void onCallback(List<DocumentSnapshot> docs);
    }
    private void getOvertimeData(final OvertimeCallback callback) {
        database.collection(CONSTANTS.PENDING_USER_HOURS_CHANGES_COLLECTION)
                .document(documentManager.getCurrentUser().getId())
                .collection(CONSTANTS.PENDING_OVERTIME_COLLECTION)
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
        toolbar.setTitle(R.string.add_overtime_title_toolbar);
        getSupportActionBar().setTitle(R.string.add_overtime_title_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private boolean areAllImportantInputsFilled(String[] inputs){
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
        // From Ace @ https://stackoverflow.com/a/9751330
        TextView textView = (TextView) spinner.getSelectedView();
        String selectedDay = textView.getText().toString();
        String selectedOvertimeHours = editTextOvertimeHours.getText().toString();
        String inputs[] = {selectedDay, selectedOvertimeHours};

        Boolean dayUnique = true;
        if(!selectedDay.isEmpty()) {
            for (DocumentSnapshot request: mUserDocs) {
                if(selectedDay.equals(request.getString(CONSTANTS.OVERTIME_DAY_KEY))) {
                    dayUnique = false;
                    break;
                }
            }
        }

        if(!areAllImportantInputsFilled(inputs)){
            Toast.makeText(this, R.string.error_request_info_incomplete, Toast.LENGTH_LONG).show();
        }else if (dayUnique) {
            // Explanation is optional; set it to N/A if nothing was given.
            String selectedOvertimeExplanation = editTextOvertimeExplanation.getText().toString();
            if(selectedOvertimeExplanation.isEmpty()){
                selectedOvertimeExplanation = "N/A";
            }

            //Get the current day to save the request in a UNIQUeLY_NAAMED  collection on Firebase
            cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int day = cal.get(Calendar.DAY_OF_MONTH);//Get day in current month since different months hav edifferent avilable days
            int month = cal.get(Calendar.MONTH);

            SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd");
            cal.set(year, month, day);
            String currentDate = sdf.format(cal.getTime());

            // Store data in the user's pending overwtires colledction, in a document named after the current date.
            DocumentReference dayLeaveRef= FirebaseFirestore.getInstance()
                    .collection(CONSTANTS.PENDING_USER_HOURS_CHANGES_COLLECTION)
                    .document(documentManager.getCurrentUser().getId()).collection(CONSTANTS.PENDING_OVERTIME_COLLECTION).document(currentDate);

            Map<String,Object> user = new HashMap<>();
            user.put(CONSTANTS.ID_KEY, documentManager.getCurrentUser().getId());
            user.put(CONSTANTS.OVERTIME_DAY_KEY, selectedDay);
            user.put(CONSTANTS.OVERTIME_DURATION_KEY, selectedOvertimeHours);
            user.put(CONSTANTS.OVERTIME_EXPLANATION_KEY, selectedOvertimeExplanation);
            user.put(CONSTANTS.REQUEST_DATE_KEY, sdf2.format(cal.getTime()));

            dayLeaveRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), getText(R.string.add_overtime_success), Toast.LENGTH_LONG).show();
                }
            });

            dayLeaveRef.set(user).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), getText(R.string.add_overtime_fail), Toast.LENGTH_LONG).show();
                    Log.i("onFailure()", e.toString());
                }
            });

            editTextOvertimeExplanation.setText("");
            editTextOvertimeHours.setText("");
        } else {
            Toast.makeText(this, R.string.select_unique_day, Toast.LENGTH_LONG).show();
        }

    }

    private void setUpComponents(){
        editTextOvertimeHours = findViewById(R.id.add_overtime_hours);
        editTextOvertimeExplanation = findViewById(R.id.add_overtime_explanation);
        spinner = (Spinner)findViewById(R.id.days_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.days_of_week,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
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
                refreshOvertimeData();
                hideSoftKeyboard();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    // Should update data upon resuming/starting activity
    @Override
    protected void onResume() {
        super.onResume();
        refreshOvertimeData();
        mAdapter.notifyDataSetChanged();
    }

    // Strictly speaking, we aren't actually doing anything with the selected day
    // UNTIL the checkmark is pressed. Therefore, we can keep the required methods to Override empty.
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}