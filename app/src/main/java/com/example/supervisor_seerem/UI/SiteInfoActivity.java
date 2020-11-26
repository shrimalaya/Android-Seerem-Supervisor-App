package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.model.CONSTANTS;
import com.example.supervisor_seerem.model.DocumentManager;
import com.example.supervisor_seerem.UI.util.WorksiteAdapter;
import com.example.supervisor_seerem.model.Site;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.widget.Toast.LENGTH_SHORT;

public class SiteInfoActivity extends AppCompatActivity {

    // We don't need to display the COMPANY_ID for the sites since only a supervisor belonging to the company can see it
    // However, we need to have company id for sites to filter which supervisors can actually see it.

    RecyclerView mRecycler;
    WorksiteAdapter mAdapter;

    private Boolean showAllSites = false;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mWorksitesRef = db.collection(CONSTANTS.WORKSITES_COLLECTION);
    private DocumentManager documentManager = DocumentManager.getInstance();
    public List<DocumentSnapshot> mAllDocs = new ArrayList<>();
    public List<DocumentSnapshot> mUserDocs = new ArrayList<>();
    public List<DocumentSnapshot> mShowDocs = new ArrayList<>();
    public List<DocumentSnapshot> mTimedDocs = new ArrayList<>();
    public List<DocumentSnapshot> mOnlineDocs = new ArrayList<>();
    public List<DocumentSnapshot> mOfflineDocs = new ArrayList<>();
    public List<DocumentSnapshot> mOnlineUserDocs = new ArrayList<>();
    public List<DocumentSnapshot> mOfflineUserSites = new ArrayList<>();
    public List<DocumentSnapshot> mOfflineAllSites = new ArrayList<>();
    public List<DocumentSnapshot> mOnlineAllSites = new ArrayList<>();

    private DrawerLayout drawer;
    private boolean showOfflineSites = false;

    public static Intent launchSiteInfoIntent(Context context) {
        Intent siteInfoIntent = new Intent(context, SiteInfoActivity.class);
        return siteInfoIntent;
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
                        Intent userIntent = UserInfoActivity.launchUserInfoIntent(SiteInfoActivity.this);
                        startActivity(userIntent);
                        finish();
                        break;

                    case R.id.sidebar_overtime:
                        Intent overtimeIntent = AddOvertimeActivity.launchAddOvertimeIntent(SiteInfoActivity.this);
                        startActivity(overtimeIntent);
                        break;

                    case R.id.sidebar_day_leave:
                        Intent dayLeaveIntent = AddDayLeaveActivity.launchAddDayLeaveIntent(SiteInfoActivity.this);
                        startActivity(dayLeaveIntent);
                        break;

                    case R.id.sidebar_search:
                        break;

                    case R.id.sidebar_all_workers:
                        Intent workerIntent = WorkerInfoActivity.launchWorkerInfoIntent(SiteInfoActivity.this);
                        startActivity(workerIntent);
                        finish();
                        break;

                    case R.id.sidebar_company:
                        break;

                    case R.id.sidebar_ui_preferences:
                        Intent uiPrefsIntent = UIPreferencesActivity.launchUIPreferencesIntent(SiteInfoActivity.this);
                        startActivity(uiPrefsIntent);
                        break;

                    case R.id.sidebar_light_dark_mode:
                        Intent changeThemeIntent = ChangeThemeActivity.launchChangeThemeIntent(SiteInfoActivity.this);
                        startActivity(changeThemeIntent);
                        break;

                    case R.id.sidebar_languages:
                        Intent changeLanguageIntent = ChangeLanguageActivity.launchChangeLanguageIntent(SiteInfoActivity.this);
                        startActivity(changeLanguageIntent);
                        break;

                    case R.id.sidebar_change_password:
                        Intent changePasswordIntent = ChangePasswordActivity.launchChangePasswordIntent(SiteInfoActivity.this);
                        startActivity(changePasswordIntent);
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
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
                        finish();
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.siteNavigation:
                        // home activity --> do nothing
                        return true;

                    case R.id.mapNavigation:
                        Intent mapIntent = SiteMapActivity.launchMapIntent(SiteInfoActivity.this);
                        startActivity(mapIntent);
                        finish();
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.sensorNavigation:
                        Intent sensorIntent = SensorsUsageActivity.launchSensorUsageIntent(SiteInfoActivity.this);
                        startActivity(sensorIntent);
                        finish();
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.userNavigation:
                        Intent userIntent = UserInfoActivity.launchUserInfoIntent(SiteInfoActivity.this);
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
            Intent intent = UserInfoActivity.launchUserInfoIntent(SiteInfoActivity.this);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_info);

        setupNavigationBar();
        setupSidebarNavigationDrawer();

        mRecycler = findViewById(R.id.siteInfoRecyclerView);

        retrieveData();

        /**
         * Update list of sites every 1 minute to check for hours of operation
         * Learned from https://stackoverflow.com/a/21554060
         */
        final Handler handler =new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(SiteInfoActivity.this, "Curr time: " + Calendar.getInstance().getTime(), Toast.LENGTH_LONG).show();
                if(!showOfflineSites) {
                    updateDisplaySites();
                    mAdapter.notifyDataSetChanged();
                }
                handler.postDelayed(this, 60000);
            }
        }, 60000);
    }

    /**
     -> This is an async task and therefore requires a callback in order to wait for the data to be
        received
     -> Get user info
     -> Use company_id of user to get all sites of that company (in DocumentSnapshot data type)
     -> Display using WorksiteAdapter by passing List of DocumentSnapshots
     */
    private void retrieveData() {
        mAllDocs.clear();
        mAllDocs.addAll(documentManager.getSites());

        mUserDocs = new ArrayList<>();
        for (DocumentSnapshot site: documentManager.getSites()) {
            for(DocumentSnapshot supervisor: documentManager.getSupervisors()) {
                if (site.getString(CONSTANTS.ID_KEY).equals(supervisor.getString(CONSTANTS.WORKSITE_ID_KEY))) {
                    mUserDocs.add(site);
                    System.out.println("TEST3> size of worksites user: " + mUserDocs.size());
                }
            }
        }
        displayData();
    }

    private void updateDisplaySites() {
        if(showAllSites) {
            mShowDocs.clear();
            mShowDocs.addAll(mAllDocs);
        } else {
            mShowDocs.clear();
            mShowDocs.addAll(mUserDocs);
        }

        mTimedDocs.clear();
        for (DocumentSnapshot doc : mShowDocs) {
            try {
                Boolean withinOpHours = timeParser(doc.getString(CONSTANTS.OPERATION_HRS_KEY));
                if (withinOpHours) {
                    mTimedDocs.add(doc);
                } else if(showOfflineSites) {
                    mTimedDocs.add(doc);
                }
            } catch (ParseException e) {
                Log.d("Parse Exception", e.toString());
            }
        }

        mShowDocs.clear();
        mShowDocs.addAll(mTimedDocs);
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

    // Search function
    private void search(String expr) {
        List<DocumentSnapshot> results = new ArrayList<>();
        Pattern pattern = Pattern.compile(expr, Pattern.CASE_INSENSITIVE);

        for(DocumentSnapshot doc: documentManager.getSites()) {
            if(results.contains(doc)) {
                //skip
            } else {
                // Look for matching ID
                Matcher matcher = pattern.matcher(doc.getString(CONSTANTS.ID_KEY));
                if(matcher.find()) {
                    results.add(doc);
                    continue;
                }

                // Look for matching name
                matcher = pattern.matcher(doc.getString(CONSTANTS.WORKSITE_NAME_KEY));
                if(matcher.find() == true) {
                    results.add(doc);
                    continue;
                }

                // Look for matching Project ID
                matcher = pattern.matcher(doc.getString(CONSTANTS.PROJECT_ID_KEY));
                if(matcher.find()) {
                    results.add(doc);
                    continue;
                }
            }
        }

        mShowDocs.clear();
        mShowDocs.addAll(results);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_site_info, menu);

        /**
         * Search layout referenced from: https://www.youtube.com/watch?v=pM1fAmUQn8g&ab_channel=CodinginFlow
         */
        final MenuItem search = menu.findItem(R.id.menu_site_search);
        final MenuItem clear = menu.findItem(R.id.menu_site_clear);

        final SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Search Here!");

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear.setVisible(true);
            }
        });

        clear.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                searchView.setQuery("", false);
                searchView.setIconified(true);

                TextView displayAllOrSelectedSites = findViewById(R.id.txtAllOrSelectedSites);
                if(showAllSites) {
                    displayAllOrSelectedSites.setText("All Company Worksites");
                } else {
                    displayAllOrSelectedSites.setText("Assigned Worksites");
                }

                updateDisplaySites();
                mAdapter.notifyDataSetChanged();
                item.setVisible(false);
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                TextView currentlyDisplaying = findViewById(R.id.txtAllOrSelectedSites);
                currentlyDisplaying.setText("Search Results: All Sites");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                TextView currentlyDisplaying = findViewById(R.id.txtAllOrSelectedSites);
                currentlyDisplaying.setText("Search: All Sites");
                search(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        TextView displayAllOrSelectedSites = findViewById(R.id.txtAllOrSelectedSites);

        switch (item.getItemId()) {
            case (R.id.refresh_site_info):
                documentManager.retrieveAllData();
                mAllDocs.clear();
                mAllDocs.addAll(documentManager.getSites());

                mUserDocs = new ArrayList<>();
                for (DocumentSnapshot doc: documentManager.getSites()) {
                    if(doc.getString(CONSTANTS.ID_KEY).equals(documentManager.getCurrentUser().getId())) {
                        mUserDocs.add(doc);
                    }
                }

                updateDisplaySites();
                mAdapter.notifyDataSetChanged();

                return true;

            case (R.id.menu_display_all_user):
                if(showAllSites) {
                    showAllSites = false;
                    if(showOfflineSites) {
                        displayAllOrSelectedSites.setText("All Assigned Worksites");
                    } else {
                        displayAllOrSelectedSites.setText("Online Assigned Worksites");
                    }
                    item.setTitle("Display All Worksites");
                } else {
                    showAllSites = true;
                    if(showOfflineSites) {
                        displayAllOrSelectedSites.setText("All Company Worksites");
                    } else {
                        displayAllOrSelectedSites.setText("Online Company Worksites");
                    }
                    item.setTitle("Display My Worksites");
                }

                updateDisplaySites();
                mAdapter.notifyDataSetChanged();
                return true;

                case (R.id.menu_site_offline):
                    if (showOfflineSites) {
                        showOfflineSites = false;
                        item.setTitle("Display Offline Sites");
                        if(showAllSites) {
                            displayAllOrSelectedSites.setText("Online Company Worksites");
                        } else {
                            displayAllOrSelectedSites.setText("Online Assigned Worksites");
                        }

                    } else {
                        showOfflineSites = true;
                        item.setTitle("Display Online Sites");
                        if(showAllSites) {
                            displayAllOrSelectedSites.setText("All Company Worksites");
                        } else {
                            displayAllOrSelectedSites.setText("All Assigned Worksites");
                        }
                    }

                    updateDisplaySites();
                    mAdapter.notifyDataSetChanged();
                    return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * HH:mm = 24hr format
     * hh:mm = 12 hr format
     * Return true if operationTime includes the current time
     */
    private boolean timeParser(String operation) throws ParseException {
        String arr[] = null;
        if(operation != null) {
            if(operation.split(" - ") != null) {
                arr = operation.split(" - ");
            } else {
                arr = operation.split("-");
            }
        }

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date d1 = dateFormat.parse(arr[0]);
        Date d2 = dateFormat.parse(arr[1]);
        String currTime = dateFormat.format(Calendar.getInstance().getTime());
        System.out.println("TEST4> Curr time: " + currTime);
        Date current = dateFormat.parse(currTime);
        System.out.println("TEST4> Curr time in Date format: " + current);
        System.out.println("TEST4> d1 time: " + d1);
        System.out.println("TEST4> d2 time: " + d2);

        Boolean withinOpHrs = (current.getTime() >= d1.getTime()) && (d2.getTime() >= current.getTime());

        System.out.println("TEST4> Within op hours: " + withinOpHrs);
        System.out.println("");

        return withinOpHrs;
    }

}