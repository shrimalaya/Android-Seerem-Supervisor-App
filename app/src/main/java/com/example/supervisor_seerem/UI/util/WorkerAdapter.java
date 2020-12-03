package com.example.supervisor_seerem.UI.util;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.UI.AdditionalInfoActivity;
import com.example.supervisor_seerem.UI.SiteMapActivity;
import com.example.supervisor_seerem.UI.WorkerInfoActivity;
import com.example.supervisor_seerem.model.CONSTANTS;
import com.example.supervisor_seerem.model.ModelLocation;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import static com.example.supervisor_seerem.R.layout.worker_row;



public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.WorkerViewHolder> {

    public List<DocumentSnapshot> mList;
    Context context;

    public WorkerAdapter(List<DocumentSnapshot> mList) { this.mList = mList; }

    @NonNull
    @Override
    public WorkerAdapter.WorkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(worker_row, parent, false);
        WorkerViewHolder mHolder = new WorkerViewHolder(v);
        return mHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerAdapter.WorkerViewHolder holder, int position) {

        final DocumentSnapshot curr = mList.get(position);

        ModelLocation location = new ModelLocation(curr.getGeoPoint(CONSTANTS.LOCATION_KEY).getLatitude(),
                curr.getGeoPoint(CONSTANTS.LOCATION_KEY).getLongitude());

        holder.workerIDTextView.setText(curr.getString(CONSTANTS.ID_KEY));
        holder.firstNameTextView.setText(curr.getString(CONSTANTS.FIRST_NAME_KEY));
        holder.lastNameTextView.setText(curr.getString(CONSTANTS.LAST_NAME_KEY));

        holder.locationTextView.setText(location.toString());
        holder.locationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String workerID = curr.getString(CONSTANTS.ID_KEY);
                Intent mapIntent = SiteMapActivity.launchMapWithZoomToLocation(context, "WorkerInfo");
                mapIntent.putExtra("WorkerID FROM WorkerInfoActivity", workerID);
                mapIntent.putExtra("SHOW ALL WORKERS", WorkerInfoActivity.getShowAllWorkers());
                context.startActivity(mapIntent);
            }
        });

        holder.skillsTextView.setText(curr.getString(CONSTANTS.SKILLS_KEY));
        holder.siteIdTextView.setText(curr.getString(CONSTANTS.WORKSITE_ID_KEY));
        holder.supervisorIdTextView.setText(curr.getString(CONSTANTS.SUPERVISOR_ID_KEY));

        holder.additionalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, AdditionalInfoActivity.class);
                i.putExtra("ID", curr.getString(CONSTANTS.ID_KEY));
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class WorkerViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout mLayout;
        public TextView workerIDTextView;
        public TextView firstNameTextView;
        public TextView lastNameTextView;
        public TextView locationTextView;
        public TextView skillsTextView;
        public TextView siteIdTextView;
        public TextView supervisorIdTextView;
        public TextView additionalInfo;

        public WorkerViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayout = (ConstraintLayout) itemView;

            workerIDTextView = itemView.findViewById(R.id.workerRow_ID);
            firstNameTextView = itemView.findViewById(R.id.workerRow_FirstName);
            lastNameTextView = itemView.findViewById(R.id.workerRow_LastName);
            locationTextView = itemView.findViewById(R.id.workerRow_Location);
            skillsTextView = itemView.findViewById(R.id.workerRow_Skills);
            siteIdTextView = itemView.findViewById(R.id.workerRow_WorksiteId);
            supervisorIdTextView = itemView.findViewById(R.id.workerRow_supervisor);
            additionalInfo = itemView.findViewById(R.id.WorkerRow_fix_additionalInfo);
        }
    }
}
