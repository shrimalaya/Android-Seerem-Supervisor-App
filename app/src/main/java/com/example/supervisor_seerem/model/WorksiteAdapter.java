package com.example.supervisor_seerem.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supervisor_seerem.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import static com.example.supervisor_seerem.R.layout.worksite_row;

public class WorksiteAdapter extends RecyclerView.Adapter<WorksiteAdapter.MyViewHolder> {

    public ArrayList<DocumentSnapshot> list;
    Context context;

    public WorksiteAdapter(ArrayList<DocumentSnapshot> list) {
        this.list = list;
        System.out.println("TEST1> Received docs of size = " + this.list.size());
    }

    @Override
    public WorksiteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(worksite_row,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WorksiteAdapter.MyViewHolder holder, int position) {

        DocumentSnapshot curr = list.get(position);

        holder.siteIDTextView.setText(curr.getString(CONSTANTS.ID_KEY));
        holder.projectIDTextView.setText(curr.getString(CONSTANTS.PROJECT_ID_KEY));
        holder.locationTextView.setText(curr.getString(CONSTANTS.LOCATION_KEY));
        holder.masterpointTextView.setText(curr.getString(CONSTANTS.MASTERPOINT_KEY));
        holder.HSElinkTextView.setText(curr.getString(CONSTANTS.HSE_LINK_KEY));
        holder.operationHours.setText(curr.getString(CONSTANTS.OPERATION_HRS_KEY));
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView siteIDTextView;
        public TextView projectIDTextView;
        public TextView locationTextView;
        public TextView masterpointTextView;
        public TextView HSElinkTextView;
        public TextView operationHours;

        public ConstraintLayout mLayout;

        Context context;

        public MyViewHolder(View itemView) {
            super(itemView);
            mLayout = (ConstraintLayout) itemView;

            siteIDTextView = (TextView) itemView.findViewById(R.id.txt_siteId_worksite);
            projectIDTextView = (TextView) itemView.findViewById(R.id.txt_projectId_worksite);
            locationTextView = (TextView) itemView.findViewById(R.id.txt_location_worksite);
            masterpointTextView = (TextView) itemView.findViewById(R.id.txt_masterpoint_worksite);
            HSElinkTextView = (TextView) itemView.findViewById(R.id.txt_HSElink_worksite);
            operationHours = itemView.findViewById(R.id.txt_operationHours_worksite);

            itemView.setOnClickListener(this);
            context = itemView.getContext();

        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context,
                    "You have clicked " + ((TextView)view.findViewById(R.id.txt_siteId_worksite)).getText().toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}