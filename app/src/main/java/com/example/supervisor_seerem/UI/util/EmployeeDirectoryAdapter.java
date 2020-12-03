package com.example.supervisor_seerem.UI.util;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.UI.AdditionalInfoActivity;
import com.example.supervisor_seerem.UI.CommunicationActivity;
import com.example.supervisor_seerem.UI.EmployeeDirectoryActivity;
import com.example.supervisor_seerem.model.CONSTANTS;
import com.example.supervisor_seerem.model.DirectoryItem;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import static com.example.supervisor_seerem.R.layout.directory_row;
import static com.example.supervisor_seerem.R.layout.worker_row;

public class EmployeeDirectoryAdapter extends RecyclerView.Adapter<EmployeeDirectoryAdapter.EmployeeViewHolder> {
    public List<DirectoryItem> mList;
    Context context;

    public EmployeeDirectoryAdapter (List<DirectoryItem> mList) { this.mList = mList; }

    @NonNull
    @Override
    public EmployeeDirectoryAdapter.EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(directory_row, parent, false);
        EmployeeDirectoryAdapter.EmployeeViewHolder mHolder = new EmployeeDirectoryAdapter.EmployeeViewHolder(v);
        return mHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeDirectoryAdapter.EmployeeViewHolder holder, int position) {
        final DirectoryItem curr = mList.get(position);

        holder.IDTextView.setText(curr.getID());
        holder.firstNameTextView.setText(curr.getFirstName());
        holder.lastNameTextView.setText(curr.getLastName());

        holder.selectedID = curr.getID();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class EmployeeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ConstraintLayout mLayout;
        public TextView IDTextView;
        public TextView firstNameTextView;
        public TextView lastNameTextView;

        public String selectedID;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayout = (ConstraintLayout) itemView;
            IDTextView = itemView.findViewById(R.id.directory_row_id);
            firstNameTextView = itemView.findViewById(R.id.directory_row_firstName);
            lastNameTextView = itemView.findViewById(R.id.directory_row_lastName);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, CommunicationActivity.class);
            intent.putExtra("ID", selectedID);
            context.startActivity(intent);

        }
    }
}
