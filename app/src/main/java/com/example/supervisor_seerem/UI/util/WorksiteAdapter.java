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
import com.example.supervisor_seerem.UI.SiteInfoActivity;
import com.example.supervisor_seerem.UI.SiteMapActivity;
import com.example.supervisor_seerem.model.CONSTANTS;
import com.example.supervisor_seerem.model.ModelLocation;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import static com.example.supervisor_seerem.R.layout.worksite_row;


public class WorksiteAdapter extends RecyclerView.Adapter<WorksiteAdapter.WorksiteViewHolder> {

    public List<DocumentSnapshot> mList;
    Context context;

    public WorksiteAdapter(List<DocumentSnapshot> mList) { this.mList = mList; }

    @NonNull
    @Override
    public WorksiteAdapter.WorksiteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(worksite_row, parent, false);
        WorksiteViewHolder mHolder = new WorksiteViewHolder(v);
        return mHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final WorksiteAdapter.WorksiteViewHolder holder, int position) {
        final DocumentSnapshot curr = mList.get(position);



        ModelLocation location = new ModelLocation(curr.getGeoPoint(CONSTANTS.LOCATION_KEY).getLatitude(),
                                        curr.getGeoPoint(CONSTANTS.LOCATION_KEY).getLongitude());

        ModelLocation masterpoint = new ModelLocation(curr.getGeoPoint(CONSTANTS.MASTERPOINT_KEY).getLatitude(),
                curr.getGeoPoint(CONSTANTS.MASTERPOINT_KEY).getLongitude());

        holder.siteIDTextView.setText(curr.getString(CONSTANTS.ID_KEY));
        holder.projectIDTextView.setText(curr.getString(CONSTANTS.PROJECT_ID_KEY));
        holder.locationTextView.setText(location.toString());
        holder.masterpointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String siteID = curr.getString(CONSTANTS.ID_KEY);
                Intent mapIntent = SiteMapActivity.launchMapWithZoomToLocation(context, "Masterpoint");
                mapIntent.putExtra("MASTERPOINT OF SITE ID FROM SiteInfoActivity", siteID);
                context.startActivity(mapIntent);
            }
        });

        holder.HSElinkTextView.setText(curr.getString(CONSTANTS.HSE_LINK_KEY));
        holder.operationHoursTextView.setText(curr.getString(CONSTANTS.OPERATION_HRS_KEY));
        holder.siteNameTextView.setText(curr.getString(CONSTANTS.WORKSITE_NAME_KEY));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class WorksiteViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout mLayout;
        public TextView siteIDTextView;
        public TextView projectIDTextView;
        public TextView locationTextView;
        public TextView masterpointButton;
        public TextView HSElinkTextView;
        public TextView operationHoursTextView;
        public TextView siteNameTextView;

        public WorksiteViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayout = (ConstraintLayout) itemView;

            siteIDTextView = (TextView) itemView.findViewById(R.id.txt_siteId_worksite);
            projectIDTextView = (TextView) itemView.findViewById(R.id.txt_projectId_worksite);
            locationTextView = (TextView) itemView.findViewById(R.id.txt_location_worksite);
            masterpointButton = (TextView) itemView.findViewById(R.id.btn_masterpoint_worksite);
            HSElinkTextView = (TextView) itemView.findViewById(R.id.txt_HSElink_worksite);
            operationHoursTextView = itemView.findViewById(R.id.txt_operationHours_worksite);
            siteNameTextView = itemView.findViewById(R.id.txt_siteName_worksiteRow);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String siteID = siteIDTextView.getText().toString();
                    Intent mapIntent = SiteMapActivity.launchMapWithZoomToLocation(view.getContext(), "SiteInfo");
                    mapIntent.putExtra("SITE ID FROM SiteInfoActivity", siteID);
                    mapIntent.putExtra("SHOW ALL SITES", SiteInfoActivity.getShowAllSites());
                    view.getContext().startActivity(mapIntent);
                }
            });
        }
    }
}