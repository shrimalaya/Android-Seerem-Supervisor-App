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

    DocumentManager documentManager;
    EditText editTextOvertimeHours;
    EditText editTextOvertimeExplanation;
    Spinner spinner;
    OvertimeAdapter mAdapter;
    RecyclerView mRecycler;

    private List<DocumentSnapshot> mAllDocs = new ArrayList<>();
    private List<DocumentSnapshot> mUserDocs = new ArrayList<>();

    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    Calendar cal = Calendar.getInstance();

    public static Intent launchAddOvertimeIntent(Context context) {
        Intent overtimeIntent = new Intent(context, AddOvertimeActivity.class);
        return overtimeIntent;
    }

    //Retrieve all documents about the overtime from the database.
//    private void retrieveAndDisplayPendingRequests(){
//        database.collection(CONSTANTS.PENDING_USER_HOURS_CHANGES_COLLECTION)
//                .document(documentManager.getCurrentUser().getId()).collection(CONSTANTS.PENDING_OVERTIME_COLLECTION)
//                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()){
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.d("HEY!", document.getId() + " => " + document.getData());
//                        //String theData = document.getString();
//                    }
//                } else {
//                    Log.d("OOPS!", "COULDN'T GET OVERTIME!");
//                }
//            }
//        });
//    }

    //Get the data from DocumentManager
    private void retrieveData(){
        //mAllDocs

        mUserDocs.clear();
        mUserDocs.addAll(documentManager.getOvertime());

        //Add the user's overtime Documents to the list of data be displayed
        mUserDocs.clear();
        for (DocumentSnapshot overtime: documentManager.getOvertime()) {
            if ((overtime.getString(CONSTANTS.ID_KEY)).equals(documentManager.getCurrentUser().getId())) {
                mUserDocs.add(overtime);
            }
        }

    }

    // Actually set the contents of the adapter.
    public void displayData(){
        //updateDisplayOvertime();
        mRecycler = findViewById(R.id.overtime_view_requests_recyclerview);
        mAdapter = new OvertimeAdapter(mUserDocs);
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);
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
                storeInputs();

                mAdapter.notifyDataSetChanged();
                retrieveData();
                displayData();
//                Toast.makeText(this, "Need to save changes", Toast.LENGTH_SHORT).show();
//                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
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
        if(!areAllImportantInputsFilled(inputs)){
            Toast.makeText(this, R.string.error_request_info_incomplete, Toast.LENGTH_LONG).show();
        }else{
            // Explanation is optional; set it to N/A if nothing was given.
            String selectedOvertimeExplanation = editTextOvertimeExplanation.getText().toString();
            if(selectedOvertimeExplanation.isEmpty()){
                selectedOvertimeExplanation = "N/A";
            }

            //Get the current day to save the request in a UNIQUeLY_NAAMED  collection on Firebase
            int year = cal.get(Calendar.YEAR);
            int day = cal.get(Calendar.DAY_OF_MONTH);//Get day in current month since different months hav edifferent avilable days
            int month = cal.get(Calendar.MONTH);

            SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
            cal.set(year, month, day);
            String currentDate = sdf.format(cal.getTime());

//            DocumentReference overtimeRef = FirebaseFirestore.getInstance()
//                    .collection(CONSTANTS.PENDING_OVERTIME_COLLECTION)
//                    .document(documentManager.getCurrentUser().getId()).collection("overtime_requests").document(currentDate);

            // Store data in the user's pending overwtires colledction, in a document named after the current date.
            DocumentReference overtimeRef = FirebaseFirestore.getInstance()
                    .collection(CONSTANTS.PENDING_USER_HOURS_CHANGES_COLLECTION)
                    .document(documentManager.getCurrentUser().getId()).collection(CONSTANTS.PENDING_OVERTIME_COLLECTION).document(currentDate);
            Map<String,Object> user = new HashMap<>();
            user.put(CONSTANTS.ID_KEY, documentManager.getCurrentUser().getId());
            user.put(CONSTANTS.OVERTIME_DAY_KEY, selectedDay);
            user.put(CONSTANTS.OVERTIME_DURATION_KEY, selectedOvertimeHours);
            user.put(CONSTANTS.OVERTIME_EXPLANATION_KEY, selectedOvertimeExplanation);

            overtimeRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), getText(R.string.add_overtime_success), Toast.LENGTH_LONG).show();
                }
            });
            overtimeRef.set(user).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), getText(R.string.add_overtime_fail), Toast.LENGTH_LONG).show();
                    Log.i("onFailure()", e.toString());
                }
            });
            mAdapter.notifyDataSetChanged();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_overtime);
        documentManager = DocumentManager.getInstance();

        setupToolbar();
        setUpComponents();

        retrieveData();
        displayData();
        mAdapter.notifyDataSetChanged();
    }

    // Should update data upon resuming/starting activity
    @Override
    protected void onResume() {
        super.onResume();
        retrieveData();
        displayData();
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