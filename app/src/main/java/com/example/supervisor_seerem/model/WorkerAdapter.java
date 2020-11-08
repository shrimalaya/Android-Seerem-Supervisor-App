package com.example.supervisor_seerem.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supervisor_seerem.R;

import java.util.List;

import static com.example.supervisor_seerem.R.layout.worker_row;



public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.WorkerViewHolder> {

    public List<Worker> mList;

    public WorkerAdapter(List<Worker> mList) { this.mList = mList; }

    @NonNull
    @Override
    public WorkerAdapter.WorkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(worker_row, parent, false);
        WorkerViewHolder mHolder = new WorkerViewHolder(v);
        return mHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerAdapter.WorkerViewHolder holder, int position) {

        Worker curr = mList.get(position);

        List<String> skills = curr.getSkills();
        String sk = "";
        for(String elt: skills) {
            sk = sk + elt + ", ";
        }

        holder.workerIDTextView.setText(curr.getEmployeeID());
        holder.firstNameTextView.setText(curr.getFirstName());
        holder.lastNameTextView.setText(curr.getLastName());
        holder.locationTextView.setText(curr.getLocation().toString());
        holder.skillsTextView.setText(sk);
        holder.siteIdTextView.setText(curr.getWorksiteID());
        holder.supervisorIdTextView.setText(curr.getSupervisorID());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class WorkerViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout mLayout;
        // TODO: Add textView for supervisorId
        public TextView workerIDTextView;
        public TextView firstNameTextView;
        public TextView lastNameTextView;
        public TextView locationTextView;
        public TextView skillsTextView;
        public TextView siteIdTextView;
        public TextView supervisorIdTextView;

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
        }
    }
}