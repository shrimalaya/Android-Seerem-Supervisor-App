package com.example.supervisor_seerem.UI.util;

import android.content.Context;
import android.provider.DocumentsProvider;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.model.CONSTANTS;
import com.google.firebase.firestore.DocumentSnapshot;

import static com.example.supervisor_seerem.R.layout.overtime_row;

import java.util.List;

public class OvertimeAdapter extends RecyclerView.Adapter<OvertimeAdapter.OvertimeViewHolder> {

    public List<DocumentSnapshot> mList;
    Context context;

    public OvertimeAdapter (List<DocumentSnapshot> mList){
        this.mList = mList;
    }

    // Create view holder based on overtime_row
    @NonNull
    @Override
    public OvertimeAdapter.OvertimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(overtime_row, parent, false);
        OvertimeViewHolder mHolder = new OvertimeViewHolder(v);
        return mHolder;
    }

    // Actually set the texts in overtime_row to the values from the current overtime request.
    @Override
    public void onBindViewHolder(@NonNull OvertimeAdapter.OvertimeViewHolder holder, int position) {
        final DocumentSnapshot curr = mList.get(position);

        holder.overtimeDayTextView.setText(curr.getString(CONSTANTS.OVERTIME_DAY_KEY));
        holder.overtimeHoursTextview.setText(curr.getString(CONSTANTS.OVERTIME_DURATION_KEY));
        holder.overtimeExplanation.setText(curr.getString(CONSTANTS.OVERTIME_EXPLANATION_KEY));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class OvertimeViewHolder extends RecyclerView.ViewHolder {


        ConstraintLayout mLayout;
        public TextView overtimeDayTextView;
        public TextView overtimeHoursTextview;
        public TextView overtimeExplanation;

        public OvertimeViewHolder(@NonNull View itemView){
            super(itemView);
            mLayout = (ConstraintLayout) itemView;

            overtimeDayTextView = itemView.findViewById(R.id.overtime_row_day);
            overtimeHoursTextview = itemView.findViewById(R.id.overtime_row_hours);
            overtimeExplanation = itemView.findViewById(R.id.overtime_row_explanation);
        }

    }
}
