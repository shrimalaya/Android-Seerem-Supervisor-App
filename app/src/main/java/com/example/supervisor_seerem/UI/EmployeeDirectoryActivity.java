package com.example.supervisor_seerem.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

public class EmployeeDirectoryActivity extends AppCompatActivity {

    private EmployeeDirectoryAdapter supervisorAdapter;
    private EmployeeDirectoryAdapter workerAdapter;
    private RecyclerView workerRecycler;
    private RecyclerView supervisorRecycler;

    private List<DirectoryItem> supervisors = new ArrayList<>();
    private List<DirectoryItem> workers = new ArrayList<>();
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
        workerAdapter = new EmployeeDirectoryAdapter(workers);
        workerRecycler.setAdapter(workerAdapter);

        supervisorAdapter = new EmployeeDirectoryAdapter(supervisors);
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
}