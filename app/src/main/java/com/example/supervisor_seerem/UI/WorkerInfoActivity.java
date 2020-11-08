package com.example.supervisor_seerem.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.model.ModelLocation;
import com.example.supervisor_seerem.model.Worker;
import com.example.supervisor_seerem.model.WorkerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkerInfoActivity extends AppCompatActivity {

    private List<Worker> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_info);

        RecyclerView mRecycler = findViewById(R.id.workerInfoRecycler);

        List<String> skills = new ArrayList<>();
        skills.add("Electrical");
        skills.add("Civil");
        skills.add("Hardware");
        Collections.sort(skills);

        mList.add(new Worker("WK0001", "Srimalaya", "Srimalaya", "SP0001",
                "WS0001", new ModelLocation(49.1887, 122.8459), skills));
        WorkerAdapter mAdapter = new WorkerAdapter(mList);
        mRecycler.setAdapter(mAdapter);
    }
}