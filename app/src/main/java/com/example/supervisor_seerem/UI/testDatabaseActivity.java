package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.model.CONSTANTS;
import com.example.supervisor_seerem.model.Supervisor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Documents = data entries
 * Collections = Tables
 * KEYS = keys
 *
 */
public class testDatabaseActivity extends AppCompatActivity {

    public static final String ID = CONSTANTS.ID_KEY;
    public static final String FIRST_NAME = CONSTANTS.FIRST_NAME_KEY;
    public static final String LAST_NAME = CONSTANTS.LAST_NAME_KEY;
    public static final String COMPANY_ID = CONSTANTS.COMPANY_ID_KEY;
    public static final String WORKSITE_ID = CONSTANTS.WORKSITE_ID_KEY;

    TextView txtId, txtFname, txtLname, txtCompanyId, txtWorksiteId;
    Button showResults;

    public List<Supervisor> mSupervisors = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mCollectionRef = db.collection(CONSTANTS.SUPERVISORS_COLLECTION);
    private List<DocumentSnapshot> mDocRefs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_database);
        txtId = findViewById(R.id.txtId);
        txtFname = findViewById(R.id.txtFirstName);
        txtLname = findViewById(R.id.txtLastName);
        txtCompanyId = findViewById(R.id.txtCompanyId);
        txtWorksiteId = findViewById(R.id.txtSiteId);

        showResults = findViewById(R.id.btnFetchWorkers);
        showResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(v);
            }
        });
    }

    private void printSingleSupervisor() {
        Supervisor curr = mSupervisors.get(0);
        txtId.setText(curr.getId());
        txtFname.setText(curr.getFirstName());
        txtLname.setText(curr.getLastName());
        txtCompanyId.setText(curr.getCompany_id());
        txtWorksiteId.setText(curr.getWorksite_id());
    }


    private void getData(View view) {
        mCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                mDocRefs = new ArrayList<>();
                mDocRefs.addAll(task.getResult().getDocuments());
            }
        });

        mSupervisors = new ArrayList<>();
        for(DocumentSnapshot doc: mDocRefs) {
            String id = doc.getString(ID);
            String firstName = doc.getString(FIRST_NAME);
            String lastName = doc.getString(LAST_NAME);
            String companyId = doc.getString(COMPANY_ID);
            String worksiteId = doc.getString(WORKSITE_ID);

            mSupervisors.add(new Supervisor(id, firstName, lastName, companyId, worksiteId));
        }

        if(mSupervisors.size() >= 1) {
            txtId.setText("" + mSupervisors.size());
            printSingleSupervisor();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}