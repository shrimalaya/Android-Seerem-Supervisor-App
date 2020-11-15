package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.model.CONSTANTS;
import com.example.supervisor_seerem.model.DocumentManager;
import com.example.supervisor_seerem.model.Worker;
import com.example.supervisor_seerem.UI.util.WorkerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WorkerInfoActivity extends AppCompatActivity {

    private String USER_ID;
    private String USER_COMPANY;
    private DocumentManager manager = DocumentManager.getInstance();

    private WorkerAdapter mAdapter;
    private RecyclerView mRecycler;
    CollectionReference mCollection = FirebaseFirestore.getInstance().collection(CONSTANTS.WORKERS_COLLECTION);

    private List<Worker> mList = new ArrayList<>();
    private List<DocumentSnapshot> mAllDocs;
    private List<DocumentSnapshot> mUserDocs;
    private List<DocumentSnapshot> mShowDocs = new ArrayList<>();

    private Boolean showAllWorkers = false;

    public static Intent launchWorkerInfoIntent(Context context) {
        Intent workerInfoIntent = new Intent(context, WorkerInfoActivity.class);
        return workerInfoIntent;
    }

    private void setupNavigationBar() {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavigationBar);
        navigation.setSelectedItemId(R.id.workerNavigation);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.workerNavigation:
                        // home activity --> do nothing
                        return true;

                    case R.id.siteNavigation:
                        Intent siteIntent = SiteInfoActivity.launchSiteInfoIntent(WorkerInfoActivity.this);
                        startActivity(siteIntent);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.mapNavigation:
                        Intent mapIntent = SiteMapActivity.launchMapIntent(WorkerInfoActivity.this);
                        startActivity(mapIntent);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.sensorNavigation:
                        Intent sensorIntent = SensorsUsageActivity.launchSensorUsageIntent(WorkerInfoActivity.this);
                        startActivity(sensorIntent);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.userNavigation:
                        Intent userIntent = UserInfoActivity.launchUserInfoIntent(WorkerInfoActivity.this);
                        startActivity(userIntent);
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_info);

        mRecycler = findViewById(R.id.workerInfoRecycler);

        USER_ID = "SP0001";
        USER_COMPANY = "CP0002";

        retrieveData();
        setupNavigationBar();
    }

    private void updateDisplaySites() {
        if(showAllWorkers) {
            mShowDocs.clear();
            mShowDocs.addAll(mAllDocs);
        } else {
            mShowDocs.clear();
            mShowDocs.addAll(mUserDocs);
        }
    }

    public void displayData() {
        updateDisplaySites();
        mAdapter = new WorkerAdapter(mShowDocs);
        mRecycler.setAdapter(mAdapter);
    }

    private void retrieveData() {
        mAllDocs.clear();
        mAllDocs.addAll(manager.getWorkers());
        mUserDocs = new ArrayList<>();
        for (DocumentSnapshot doc: mAllDocs) {
            if((doc.getString(CONSTANTS.SUPERVISOR_ID_KEY)).equals(USER_ID)) {
                mUserDocs.add(doc);
            }
        }
        displayData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_worker_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case (R.id.menu_worker_refresh):
                manager.retrieveAllData();
                mAllDocs.clear();
                mAllDocs.addAll(manager.getWorkers());
                mUserDocs = new ArrayList<>();
                for (DocumentSnapshot doc: mAllDocs) {
                    if((doc.getString(CONSTANTS.SUPERVISOR_ID_KEY)).equals(USER_ID)) {
                        mUserDocs.add(doc);
                    }
                }

                updateDisplaySites();
                mAdapter.notifyDataSetChanged();
                return true;

            case (R.id.menu_worker_display_filter):
                TextView currentlyDisplaying = findViewById(R.id.fix_workerInfo_currentlyDisplaying);

                if(showAllWorkers) {
                    showAllWorkers = false;
                    currentlyDisplaying.setText("Assigned Workers");
                    item.setTitle("Display All Workers");
                } else {
                    showAllWorkers = true;
                    currentlyDisplaying.setText("All Company Workers");
                    item.setTitle("Display My Workers");
                }

                updateDisplaySites();
                mAdapter.notifyDataSetChanged();
                return true;

            case (R.id.menu_worker_map):
                Intent i = new Intent(this, SiteMapActivity.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

////        OLD Code
//private void retrieveData() {
//    getAllData(new AllDataCallback() {
//        @Override
//        public void onCallback(List<DocumentSnapshot> docs) {
//            mAllDocs = new ArrayList<>();
//            mAllDocs.addAll(docs);
//
//            mUserDocs = new ArrayList<>();
//            for (DocumentSnapshot doc: docs) {
//                if((doc.getString(CONSTANTS.SUPERVISOR_ID_KEY)).equals(USER_ID)) {
//                    mUserDocs.add(doc);
//                }
//            }
//            displayData();
//        }
//    });
//}
//
//private interface AllDataCallback {
//    void onCallback(List<DocumentSnapshot> docs);
//}
//
//    private void getAllData(final WorkerInfoActivity.AllDataCallback callback) {
//        mCollection
//                .whereEqualTo(CONSTANTS.COMPANY_ID_KEY, USER_COMPANY)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isComplete()) {
//                            callback.onCallback(task.getResult().getDocuments());
//                        }
//                    }
//                });
//    }
