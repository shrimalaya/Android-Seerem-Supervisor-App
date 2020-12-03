package com.example.supervisor_seerem.UI.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.model.CONSTANTS;
import com.google.firebase.firestore.DocumentSnapshot;

import static com.example.supervisor_seerem.R.layout.day_leave_row;
import java.util.List;

public class DayLeaveAdapter extends RecyclerView.Adapter<DayLeaveAdapter.DayLeaveViewHolder> {

    public List<DocumentSnapshot> mList;
    Context context;

    public DayLeaveAdapter(List<DocumentSnapshot> mList) {
        this.mList = mList;

    }

    @NonNull
    @Override
    public DayLeaveAdapter.DayLeaveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(day_leave_row, parent, false);
        DayLeaveViewHolder mHolder = new DayLeaveViewHolder(v);
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull DayLeaveAdapter.DayLeaveViewHolder holder, int position) {
        //holder.dayLeaveStartTextView.setText(cur);
        final DocumentSnapshot curr = mList.get(position);

        holder.dayLeaveStartTextView.setText(curr.getString(CONSTANTS.OVERTIME_DAY_KEY));
        holder.dayLeaveStartDaysDurationTextView.setText(curr.getString(CONSTANTS.OVERTIME_DURATION_KEY));
        holder.dayLeaveExplanation.setText(curr.getString(CONSTANTS.OVERTIME_EXPLANATION_KEY));


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class DayLeaveViewHolder extends RecyclerView.ViewHolder {

    public TextView dayLeaveStartTextView;
    public TextView dayLeaveStartDaysDurationTextView;
    public TextView dayLeaveExplanation;
    public DayLeaveViewHolder(@NonNull View itemView) {
        super(itemView);

        dayLeaveStartTextView = itemView.findViewById(R.id.day_leave_row_date);
        dayLeaveStartDaysDurationTextView = itemView.findViewById(R.id.day_leave_row_duration);
        dayLeaveExplanation = itemView.findViewById(R.id.day_leave_row_explanation);
    }
}

}
