package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

public class SiteInfoActivity extends AppCompatActivity {

    // We don't need to display the COMPANY_ID for the sites since only a supervisor belonging to the company can see it
    // However, we need to have company id for sites to filter which supervisors can actually see it.

    RecyclerView mRecycler;
    WorksiteAdapter mAdapter;

    private static Boolean showAllSites = false;
    private Boolean showOfflineSites = false;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mWorksitesRef = db.collection(CONSTANTS.WORKSITES_COLLECTION);
    private DocumentManager documentManager = DocumentManager.getInstance();
    public List<DocumentSnapshot> mAllDocs = new ArrayList<>();
    public List<DocumentSnapshot> mUserDocs = new ArrayList<>();
    public List<DocumentSnapshot> mShowDocs = new ArrayList<>();
    public List<DocumentSnapshot> mOnlineDocs = new ArrayList<>();
    public List<DocumentSnapshot> mOfflineDocs = new ArrayList<>();

    private DrawerLayout drawer;
    private Handler handler;
    private Runnable runnable;

    public static  Boolean getShowAllSites() {
        return showAllSites;
    }
    public static Intent launchSiteInfoIntent(Context context) {
        Intent siteInfoIntent = new Intent(context, SiteInfoActivity.class);
        return siteInfoIntent;
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
        handler =new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("SITEINFO", "Curr time: " + Calendar.getInstance().getTime());
                updateDisplaySites();
                mAdapter.notifyDataSetChanged();
                handler.postDelayed(this, 60000);
            }
        };

        handler.postDelayed(runnable, 60000);
    }

    /**
     -> Get user info
     -> Display using WorksiteAdapter by passing List of DocumentSnapshots
     */
    private void retrieveData() {
        mAllDocs.clear();
        mAllDocs.addAll(documentManager.getSites());

        Log.d("SITEINFO retrieveData()", "mAllDocs.size() = " + mAllDocs.size());

        mUserDocs.clear();
        for (DocumentSnapshot site: documentManager.getSites()) {
            for(DocumentSnapshot supervisor: documentManager.getSupervisors()) {
                if (site.getString(CONSTANTS.ID_KEY).equals(supervisor.getString(CONSTANTS.WORKSITE_ID_KEY))) {
                    mUserDocs.add(site);
                }
            }
        }
        displayData();
    }

    private void checkDisplayingSitesHeader() {
        TextView displayAllOrSelectedSites = findViewById(R.id.txtAllOrSelectedSites);

        if(showOfflineSites && showAllSites) {
            displayAllOrSelectedSites.setText("Offline Company Worksites");
        } else if (showOfflineSites && !showAllSites) {
            displayAllOrSelectedSites.setText("Offline Assigned Worksites");
        } else if (!showOfflineSites && showAllSites) {
            displayAllOrSelectedSites.setText("Online Company Worksites");
        } else {
            displayAllOrSelectedSites.setText("Online Assigned Worksites");
        }
    }

    private void updateDisplaySites() {
        checkDisplayingSitesHeader();

        if(showAllSites) {
            mShowDocs.clear();
            mShowDocs.addAll(mAllDocs);
        } else {
            mShowDocs.clear();
            mShowDocs.addAll(mUserDocs);
        }

        mOnlineDocs.clear();
        mOfflineDocs.clear();

        for (DocumentSnapshot doc : mShowDocs) {
            try {
                Boolean withinOpHours = timeParser(doc.getString(CONSTANTS.OPERATION_HRS_KEY));
                if (withinOpHours) {
                    mOnlineDocs.add(doc);
                } else {
                    mOfflineDocs.add(doc);
                }
            } catch (ParseException e) {
                Log.d("SITEINFO","Parse Exception" + e.toString());
                mOnlineDocs.add(doc); // A site with no operation hours specified will be added to Online Docs
            }
        }

        mShowDocs.clear();
        if(showOfflineSites) {
            mShowDocs.addAll(mOfflineDocs);
            if(mOfflineDocs.isEmpty()) {
                Toast.makeText(this, "No Offline Sites!", Toast.LENGTH_LONG).show();
            }
        } else {
            mShowDocs.addAll(mOnlineDocs);
            if(mOnlineDocs.isEmpty()) {
                Toast.makeText(this, "No Online Sites!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void displayData() {
        updateDisplaySites();
        mAdapter = new WorksiteAdapter(mShowDocs);
        mRecycler.setAdapter(mAdapter);
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

        MenuItem showAllW = menu.getItem(2);

        if(showAllSites) {
            showAllW.setTitle(R.string.display_my_worksites);
        } else {
            showAllW.setTitle(R.string.display_all_worksites);
        }

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

        switch (item.getItemId()) {
            case (R.id.refresh_site_info):
                final ProgressDialog progressDialog = new ProgressDialog(SiteInfoActivity.this);
                progressDialog.setMessage("Refreshing All Data!");
                progressDialog.setTitle("Please wait");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();

                documentManager.retrieveAllData(new DocumentManager.RetrieveCallback() {
                    @Override
                    public void onCallback(Boolean result) {
                        if(result) {
                            mAllDocs.clear();
                            mAllDocs.addAll(documentManager.getSites());

                            mUserDocs.clear();
                            for (DocumentSnapshot site: mAllDocs) {
                                for(DocumentSnapshot supervisor: documentManager.getSupervisors()) {
                                    if (site.getString(CONSTANTS.ID_KEY).equals(supervisor.getString(CONSTANTS.WORKSITE_ID_KEY))) {
                                        mUserDocs.add(site);
                                    }
                                }
                            }

                            updateDisplaySites();
                            progressDialog.dismiss();
                            mAdapter.notifyDataSetChanged();
                            Log.d("SITEINFO", "All data refreshed");
                        }
                    }
                });

                return true;

            case (R.id.menu_display_all_user):
                if(showAllSites) {
                    showAllSites = false;
                    item.setTitle("Display All Worksites");
                } else {
                    showAllSites = true;
                    item.setTitle("Display My Worksites");
                }

                updateDisplaySites();
                mAdapter.notifyDataSetChanged();
                return true;

                case (R.id.menu_site_offline):
                    if (showOfflineSites) {
                        showOfflineSites = false;
                        item.setTitle("Display Offline Sites");
                    } else {
                        showOfflineSites = true;
                        item.setTitle("Display Online Sites");
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
            if(operation.split("-") != null) {
                arr = operation.split("-");
            }
        }

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date d1 = dateFormat.parse(arr[0]);
        Date d2 = dateFormat.parse(arr[1]);

        String currTime = dateFormat.format(Calendar.getInstance().getTime());
        Date current = dateFormat.parse(currTime);

        Boolean withinOpHrs = (current.getTime() >= d1.getTime()) && (d2.getTime() >= current.getTime());

        Log.d("SITEINFO",operation + "- within op hours: " + withinOpHrs);

        return withinOpHrs;
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

                    case R.id.sidebar_all_workers:
                        Intent workerIntent = WorkerInfoActivity.launchWorkerInfoIntent(SiteInfoActivity.this);
                        startActivity(workerIntent);
                        finish();
                        break;

                    case R.id.sidebar_company:
                        Intent employeeDirectoryIntent = new Intent(SiteInfoActivity.this, EmployeeDirectoryActivity.class);
                        startActivity(employeeDirectoryIntent);
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

                    case R.id.sidebar_log_out:
                        launchLogOutDialog();
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

    private void launchLogOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SiteInfoActivity.this,
                R.style.AlertDialog);
        builder.setMessage(getString(R.string.log_out_message));
        builder.setTitle(getString(R.string.log_out_title));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.log_out_dialog_positive),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finishAffinity();
                        Intent intent = new Intent(SiteInfoActivity.this, LoginInfoActivity.class);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton(getString(R.string.log_out_dialog_negative),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#B32134"));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#B32134"));
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDisplaySites();
        handler.postDelayed(runnable,60000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}