package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.model.CONSTANTS;
import com.example.supervisor_seerem.model.DocumentManager;
import com.example.supervisor_seerem.model.Worker;
import com.example.supervisor_seerem.UI.util.WorkerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WorkerInfoActivity extends AppCompatActivity {

    private DocumentManager documentManager = DocumentManager.getInstance();

    private WorkerAdapter mAdapter;
    private RecyclerView mRecycler;
    private DrawerLayout drawer;

    private List<Worker> mList = new ArrayList<>();
    private List<DocumentSnapshot> mAllDocs = new ArrayList<>();
    private List<DocumentSnapshot> mUserDocs = new ArrayList<>();
    private List<DocumentSnapshot> mShowDocs = new ArrayList<>();

    private Boolean showAllWorkers = false;

    public static Intent launchWorkerInfoIntent(Context context) {
        Intent workerInfoIntent = new Intent(context, WorkerInfoActivity.class);
        return workerInfoIntent;
    }

    private void setupSidebarNavigationDrawer() {
        drawer = findViewById(R.id.sidebar_drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.sidebar_navigation_view);

        // customized toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_for_sidebar);
        setSupportActionBar(toolbar);

        // toggle to open/close the sidebar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.sidebar_navigation_open, R.string.sidebar_navigation_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // header
        View headerView = navigationView.getHeaderView(0);
        String savedFirstName = documentManager.getCurrentUser().getFirstName();
        String savedLastName = documentManager.getCurrentUser().getLastName();
        TextView sidebarFullName = (TextView) headerView.findViewById(R.id.sidebar_header_fullname_textview);
        sidebarFullName.setText(savedFirstName + " " + savedLastName);

        final SharedPreferences loginSharedPreferences = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        String savedUsername = loginSharedPreferences.getString("username", null);
        TextView sidebarUsername = (TextView) headerView.findViewById(R.id.sidebar_header_username_textview);
        sidebarUsername.setText(savedUsername);

        // onClickListener
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.sidebar_user:
                        Intent userIntent = UserInfoActivity.launchUserInfoIntent(WorkerInfoActivity.this);
                        startActivity(userIntent);
                        finish();
                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.sidebar_overtime:
                        Intent overtimeIntent = AddOvertimeActivity.launchAddOvertimeIntent(WorkerInfoActivity.this);
                        startActivity(overtimeIntent);
                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.sidebar_day_leave:
                        Intent dayLeaveIntent = AddDayLeaveActivity.launchAddDayLeaveIntent(WorkerInfoActivity.this);
                        startActivity(dayLeaveIntent);
                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.sidebar_search:
                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.sidebar_all_workers:
                        // just close sidebar because it goes to the same activity
                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.sidebar_company:
                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.sidebar_ui_preferences:
                        Intent uiPrefsIntent = UIPreferencesActivity.launchUIPreferencesIntent(WorkerInfoActivity.this);
                        startActivity(uiPrefsIntent);
                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.sidebar_light_dark_mode:
                        Intent changeThemeIntent = ChangeThemeActivity.launchChangeThemeIntent(WorkerInfoActivity.this);
                        startActivity(changeThemeIntent);
                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.sidebar_languages:
                        Intent changeLanguageIntent = ChangeLanguageActivity.launchChangeLanguageIntent(WorkerInfoActivity.this);
                        startActivity(changeLanguageIntent);
                        drawer.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.sidebar_change_password:
                        Intent changePasswordIntent = ChangePasswordActivity.launchChangePasswordIntent(WorkerInfoActivity.this);
                        startActivity(changePasswordIntent);
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                }
                return true;
            }
        });
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
                        finish();
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.mapNavigation:
                        Intent mapIntent = SiteMapActivity.launchMapIntent(WorkerInfoActivity.this);
                        startActivity(mapIntent);
                        finish();
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.sensorNavigation:
                        Intent sensorIntent = SensorsUsageActivity.launchSensorUsageIntent(WorkerInfoActivity.this);
                        startActivity(sensorIntent);
                        finish();
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.userNavigation:
                        Intent userIntent = UserInfoActivity.launchUserInfoIntent(WorkerInfoActivity.this);
                        startActivity(userIntent);
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finishAffinity();
            Intent intent = UserInfoActivity.launchUserInfoIntent(WorkerInfoActivity.this);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_info);

        setupNavigationBar();
        setupSidebarNavigationDrawer();
        mRecycler = findViewById(R.id.workerInfoRecycler);

        displayData();
    }

    private void updateDisplaySites() {
        mAllDocs.clear();
        mAllDocs.addAll(documentManager.getWorkers());

        mUserDocs = new ArrayList<>();
        for (DocumentSnapshot doc: documentManager.getWorkers()) {
            if((doc.getString(CONSTANTS.SUPERVISOR_ID_KEY)).equals(documentManager.getCurrentUser().getId())) {
                mUserDocs.add(doc);
            }
        }

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

        if (mAdapter == null) {
            System.out.println("TEST1> Adapter null");
        } else {
            mRecycler.setAdapter(mAdapter);
        }
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
                documentManager.retrieveAllData();

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