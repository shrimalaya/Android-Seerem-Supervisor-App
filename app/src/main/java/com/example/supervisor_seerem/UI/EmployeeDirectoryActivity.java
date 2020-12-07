package com.example.supervisor_seerem.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.UI.util.EmployeeDirectoryAdapter;
import com.example.supervisor_seerem.UI.util.WorkerAdapter;
import com.example.supervisor_seerem.model.CONSTANTS;
import com.example.supervisor_seerem.model.Contact;
import com.example.supervisor_seerem.model.DirectoryItem;
import com.example.supervisor_seerem.model.DocumentManager;
import com.google.firebase.firestore.DocumentSnapshot;

import java.net.IDN;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmployeeDirectoryActivity extends AppCompatActivity {

    private EmployeeDirectoryAdapter supervisorAdapter;
    private EmployeeDirectoryAdapter workerAdapter;
    private RecyclerView workerRecycler;
    private RecyclerView supervisorRecycler;

    private List<DirectoryItem> supervisors = new ArrayList<>();
    private List<DirectoryItem> showSupervisors = new ArrayList<>();
    private List<DirectoryItem> workers = new ArrayList<>();
    private List<DirectoryItem> showWorkers = new ArrayList<>();
    private List<DocumentSnapshot> contacts = new ArrayList<>();

    private DocumentManager documentManager;

    public static Intent launchEmployeeDirectory(Context context) {
        Intent employeeDirectoryIntent = new Intent(context, EmployeeDirectoryActivity.class);
        return employeeDirectoryIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_directory);
        setupToolbar();

        setupRecyclerViews();

        documentManager = DocumentManager.getInstance();

        retrieveData();
        linkSupervisors();
        linkWorkers();

        displayData();
    }

    private void displayData() {
        workerAdapter = new EmployeeDirectoryAdapter(showWorkers);
        workerRecycler.setAdapter(workerAdapter);

        supervisorAdapter = new EmployeeDirectoryAdapter(showSupervisors);
        supervisorRecycler.setAdapter(supervisorAdapter);
    }

    private void linkWorkers() {
        workers.clear();
        for (DocumentSnapshot worker: documentManager.getWorkers()) {
            for (DocumentSnapshot contact: contacts) {
                if (contact.getString(CONSTANTS.ID_KEY).equals(worker.getString(CONSTANTS.ID_KEY))) {
                    workers.add(new DirectoryItem(contact.getString(CONSTANTS.ID_KEY),
                            worker.getString(CONSTANTS.FIRST_NAME_KEY),
                            worker.getString(CONSTANTS.LAST_NAME_KEY)));
                }
            }
        }

        showWorkers.clear();
        showWorkers.addAll(workers);
    }

    private void linkSupervisors() {
        supervisors.clear();
        for (DocumentSnapshot supervisor: documentManager.getSupervisors()) {
            for (DocumentSnapshot contact: contacts) {
                if (contact.getString(CONSTANTS.ID_KEY).equals(supervisor.getString(CONSTANTS.ID_KEY))) {
                    supervisors.add(new DirectoryItem(contact.getString(CONSTANTS.ID_KEY),
                            supervisor.getString(CONSTANTS.FIRST_NAME_KEY),
                            supervisor.getString(CONSTANTS.LAST_NAME_KEY)));
                }
            }
        }

        showSupervisors.clear();
        showSupervisors.addAll(supervisors);

    }

    private void retrieveData() {
        contacts.clear();
        contacts.addAll(documentManager.getContacts());
    }

    private void setupRecyclerViews() {
        workerRecycler = (RecyclerView) findViewById(R.id.directory_worker_recycler);
        supervisorRecycler = (RecyclerView) findViewById(R.id.directory_supervisor_recycler);
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

    // Search function
    private void search(String expr) {
        List<DirectoryItem> resultsWorkers = new ArrayList<>();
        Pattern pattern = Pattern.compile(expr, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        // search workers
        for (DirectoryItem doc : workers) {
            if (resultsWorkers.contains(doc)) {
                //skip
            } else {
                // Look for matching name
                Matcher matcher = pattern.matcher(doc.getFirstName()
                        + " " + doc.getLastName());
                if (matcher.find() == true) {
                    resultsWorkers.add(doc);
                    continue;
                }

                // Look for matching ID
                matcher = pattern.matcher(doc.getID());
                if (matcher.find()) {
                    resultsWorkers.add(doc);
                    continue;
                }
            }
        }

        showWorkers.clear();
        showWorkers.addAll(resultsWorkers);
        workerAdapter.notifyDataSetChanged();

        // Search supervisors
        List<DirectoryItem> resultsSupervisor = new ArrayList<>();
        for (DirectoryItem doc : supervisors) {
            if (resultsSupervisor.contains(doc)) {
                //skip
            } else {
                // Look for matching name
                Matcher matcher = pattern.matcher(doc.getFirstName()
                        + " " + doc.getLastName());
                if (matcher.find() == true) {
                    resultsSupervisor.add(doc);
                    continue;
                }

                // Look for matching ID
                matcher = pattern.matcher(doc.getID());
                if (matcher.find()) {
                    resultsSupervisor.add(doc);
                    continue;
                }
            }
        }

        showSupervisors.clear();
        showSupervisors.addAll(resultsSupervisor);
        supervisorAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_employee_directory, menu);

        /**
         * Search layout referenced from: https://www.youtube.com/watch?v=pM1fAmUQn8g&ab_channel=CodinginFlow
         */
        final MenuItem search = menu.findItem(R.id.employee_search);

        final SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Search here!");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
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

        return super.onOptionsItemSelected(item);
    }

}