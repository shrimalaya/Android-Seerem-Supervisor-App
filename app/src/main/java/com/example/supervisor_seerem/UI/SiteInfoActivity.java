package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.model.CONSTANTS;
import com.example.supervisor_seerem.model.Site;
import com.example.supervisor_seerem.model.Supervisor;
import com.example.supervisor_seerem.model.WorksiteAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SiteInfoActivity extends AppCompatActivity {

    // We don't need to display the COMPANY_ID for the sites since only a supervisor belonging to the company can see it
    // However, we need to have company id for sites to filter which supervisors can actually see it.

    RecyclerView mRecycler;
    WorksiteAdapter mAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mWorksitesRef = db.collection(CONSTANTS.WORKSITES_COLLECTION);
    private Supervisor currUser;
    private String USER_ID = CONSTANTS.USER_ID;

    // TODO: Reduce this to two variables instead of three
    private List<Site> mAllSites = new ArrayList<>();
    private List<Site> mCompanySites = new ArrayList<>();
    private List<Site> mAssignedSites = new ArrayList<>();

    public ArrayList<DocumentSnapshot> mDocs = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_info);

        mRecycler = (RecyclerView) findViewById(R.id.recycler_worksite);



        getUserData();
        //displayData();
    }




    public void displayData() {
        System.out.println("TEST1> Before adapter: size of docs = " + mDocs.size());
        mAdapter = new WorksiteAdapter(mDocs);
        if(mAdapter == null) {
            System.out.println("TEST1> Adapter null");
        } else {
            mRecycler.setAdapter(mAdapter);
        }
    }

    public void getUserData() {
        final DocumentReference mSupervisor = db.document(CONSTANTS.SUPERVISORS_COLLECTION + "/" + CONSTANTS.USER_ID);
        mSupervisor.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isComplete()) {
                    DocumentSnapshot doc = task.getResult();
                    System.out.println("TEST1> User id = " + doc.getString(CONSTANTS.ID_KEY));
                    currUser = new Supervisor(doc.getString(CONSTANTS.ID_KEY),
                            doc.getString(CONSTANTS.FIRST_NAME_KEY),
                            doc.getString(CONSTANTS.LAST_NAME_KEY),
                            doc.getString(CONSTANTS.COMPANY_ID_KEY),
                            doc.getString(CONSTANTS.WORKSITE_ID_KEY));

                    getAllData();
                }
            }
        });
    }

    public void getAllData() {
        mWorksitesRef
                .whereEqualTo(CONSTANTS.COMPANY_ID_KEY, currUser.getCompany_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isComplete()) {
                            ArrayList<DocumentSnapshot> mDocs_temp = new ArrayList<>();
                            mDocs_temp.addAll(task.getResult().getDocuments());
                            System.out.println("TEST1> Size of temp data = " + mDocs_temp.size());
                            mDocs = mDocs_temp;
                            // displayData();
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
                getAllData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //    // TODO: Figure out a way to get data (wait for the async tasks to complete) and send it to adapter
//    getUserData(new UserSupervisorCallback() {
//        @Override
//        public void onCallback(DocumentSnapshot doc) {
//            System.out.println("TEST1> User id = " + doc.getString(CONSTANTS.ID_KEY));
//            currUser = new Supervisor(doc.getString(CONSTANTS.ID_KEY),
//                    doc.getString(CONSTANTS.FIRST_NAME_KEY),
//                    doc.getString(CONSTANTS.LAST_NAME_KEY),
//                    doc.getString(CONSTANTS.COMPANY_ID_KEY),
//                    doc.getString(CONSTANTS.WORKSITE_ID_KEY));
//
//            getAllData(new AllDataCallback() {
//                @Override
//                public void onCallback(ArrayList<DocumentSnapshot> docs) {
//                    mDocs = new ArrayList<>();
//                    mDocs.addAll(docs);
//                    System.out.println("TEST1> Size of final data = " + mDocs.size());
//                    displayData();
//                }
//            });
//        }
//    });




    //    private interface AllDataCallback {
//        void onCallback(ArrayList<DocumentSnapshot> docs);
//    }
//
//    private interface UserSupervisorCallback {
//        void onCallback(DocumentSnapshot doc);
//    }
//
//    private interface DisplayCallback {
//        void onCallBack();
//    }
}