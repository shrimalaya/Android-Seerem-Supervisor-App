package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.model.CONSTANTS;
import com.example.supervisor_seerem.model.Supervisor;
import com.example.supervisor_seerem.UI.util.WorksiteAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SiteInfoActivity extends AppCompatActivity {

    // We don't need to display the COMPANY_ID for the sites since only a supervisor belonging to the company can see it
    // However, we need to have company id for sites to filter which supervisors can actually see it.

    RecyclerView mRecycler;
    WorksiteAdapter mAdapter;

    private Boolean showAllSites = false;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mWorksitesRef = db.collection(CONSTANTS.WORKSITES_COLLECTION);
    private Supervisor currUser;
    public List<DocumentSnapshot> mAllDocs = new ArrayList<>();
    public List<DocumentSnapshot> mUserDocs = new ArrayList<>();
    public List<DocumentSnapshot> mShowDocs = new ArrayList<>();

    public static Intent launchSiteInfoIntent(Context context) {
        Intent siteInfoIntent = new Intent(context, SiteInfoActivity.class);
        return siteInfoIntent;
    }

    private void setupNavigationBar() {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavigationBar);
        navigation.setSelectedItemId(R.id.siteNavigation);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.workerNavigation:
                        Intent workerIntent = WorkerInfoActivity.launchWorkerInfoIntent(SiteInfoActivity.this);
                        startActivity(workerIntent);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.siteNavigation:
                        // home activity --> do nothing
                        return true;

                    case R.id.mapNavigation:
                        Intent mapIntent = SiteMapActivity.launchMapIntent(SiteInfoActivity.this);
                        startActivity(mapIntent);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.sensorNavigation:
                        Intent sensorIntent = SensorsUsageActivity.launchSensorUsageIntent(SiteInfoActivity.this);
                        startActivity(sensorIntent);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.userNavigation:
                        Intent userIntent = UserInfoActivity.launchUserInfoIntent(SiteInfoActivity.this);
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
        setContentView(R.layout.activity_site_info);

        setupNavigationBar();
        mRecycler = (RecyclerView) findViewById(R.id.siteInfoRecyclerView);

        // TODO: repopulate online database and set the key as employee_id (SP0001, SP0002, etc)
        CONSTANTS.USER_ID = "sladha";

        retrieveData();
    }

    /**
     -> This is an async task and therefore requires a callback in order to wait for the data to be
        received
     -> Get user info
     -> Use company_id of user to get all sites of that company (in DocumentSnapshot data type)
     -> Display using WorksiteAdapter by passing List of DocumentSnapshots
     */
    private void retrieveData() {
        getUserData(new UserSupervisorCallback() {
            @Override
            public void onCallback(DocumentSnapshot doc) {
                System.out.println("TEST1> User id = " + doc.getString(CONSTANTS.ID_KEY));
                currUser = new Supervisor(doc.getString(CONSTANTS.ID_KEY),
                        doc.getString(CONSTANTS.FIRST_NAME_KEY),
                        doc.getString(CONSTANTS.LAST_NAME_KEY),
                        doc.getString(CONSTANTS.COMPANY_ID_KEY),
                        doc.getString(CONSTANTS.WORKSITE_ID_KEY));

                getAllData(new AllDataCallback() {
                    @Override
                    public void onCallback(List<DocumentSnapshot> docs) {
                        mAllDocs = new ArrayList<>();
                        mAllDocs.addAll(docs);
                        System.out.println("TEST1> Size of final data = " + mAllDocs.size());

                        mUserDocs = new ArrayList<>();
                        for (DocumentSnapshot doc: docs) {
                            if(doc.getString(CONSTANTS.ID_KEY).equals(currUser.getWorksite_id())) {
                                mUserDocs.add(doc);
                            }
                        }
                        displayData();
                    }
                });
            }
        });
    }

    public void getUserData(final UserSupervisorCallback callback) {
        DocumentReference mSupervisor = db.collection(CONSTANTS.SUPERVISORS_COLLECTION).document(CONSTANTS.USER_ID);
        mSupervisor.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isComplete()) {
                    DocumentSnapshot doc = task.getResult();
                    System.out.println("TEST1> User id = " + doc.getString(CONSTANTS.ID_KEY));
                    callback.onCallback(doc);
                }
            }
        });
    }

    public void getAllData(final AllDataCallback callback) {
        mWorksitesRef
                .whereEqualTo(CONSTANTS.COMPANY_ID_KEY, currUser.getCompany_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()) {
                            System.out.println("TEST1> Size of temp data = " + task.getResult().getDocuments().size());
                            callback.onCallback(task.getResult().getDocuments());
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_site_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case (R.id.refresh_site_info):
                getAllData(new AllDataCallback() {
                    @Override
                    public void onCallback(List<DocumentSnapshot> docs) {
                        mAllDocs.clear();
                        mAllDocs.addAll(docs);

                        mUserDocs.clear();
                        for (DocumentSnapshot doc: docs) {
                            if(doc.getString(CONSTANTS.ID_KEY).equals(currUser.getWorksite_id())) {
                                mUserDocs.add(doc);
                            }
                        }

                        updateDisplaySites();
                        mAdapter.notifyDataSetChanged();
                    }
                });
                return true;

            case (R.id.menu_display_all_user):
                TextView displayAllOrSelectedSites = findViewById(R.id.txtAllOrSelectedSites);

                if(showAllSites) {
                    showAllSites = false;
                    displayAllOrSelectedSites.setText("Your Worksites");
                    item.setTitle("Display All Worksites");
                } else {
                    showAllSites = true;
                    displayAllOrSelectedSites.setText("All Company Worksites");
                    item.setTitle("Display My Worksites");
                }

                updateDisplaySites();
                mAdapter.notifyDataSetChanged();
                return true;

            case (R.id.map_menu_siteInfo):
                Intent i = new Intent(this, SiteMapActivity.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateDisplaySites() {
        if(showAllSites) {
            mShowDocs.clear();
            mShowDocs.addAll(mAllDocs);
        } else {
            mShowDocs.clear();
            mShowDocs.addAll(mUserDocs);
        }
    }

    public void displayData() {
        System.out.println("TEST1> Before adapter: size of docs = " + mAllDocs.size());

        updateDisplaySites();
        mAdapter = new WorksiteAdapter(mShowDocs);

        if (mAdapter == null) {
            System.out.println("TEST1> Adapter null");
        } else {
            mRecycler.setAdapter(mAdapter);
        }
    }

    private interface AllDataCallback {
        void onCallback(List<DocumentSnapshot> docs);
    }

    private interface UserSupervisorCallback {
        void onCallback(DocumentSnapshot doc);
    }

}