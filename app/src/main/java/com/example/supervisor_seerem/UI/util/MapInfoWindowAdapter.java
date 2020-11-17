package com.example.supervisor_seerem.UI.util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.supervisor_seerem.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;

public class MapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

//    private View mapWindow;
    private Context context;
    private String displayPurpose;

    public MapInfoWindowAdapter(Context context) {
        this.context = context;
//        mapWindow = (View) LayoutInflater.from(context).inflate(R.layout.customized_user_map_info_window, null);
    }

    private void renderWindowText (Marker marker, View view) {
        String title = marker.getTitle();
        TextView mainTitleTextView = (TextView) view.findViewById(R.id.mapInfoWindow_mainTitle);
        if (!title.equals("")) {
            mainTitleTextView.setText(title);
        }

        String[] snippet = marker.getSnippet().split(";");
        Log.d("FROM MapWindowAdapter", "snippet.length = " + snippet.length);
        if (snippet.length > 0) {
            Log.d("FROM MapWindowAdapter", "snippet = " + snippet[0]);
            switch(displayPurpose) {
                case "User":
//                    mapWindow = LayoutInflater.from(context).inflate(R.layout.customized_user_map_info_window, null);
                    TextView snippetTextView = (TextView) view.findViewById(R.id.mapInfoWindow_userSnippet);
                    Log.d("FROM MapWindowAdapter", "snippet = " + snippet[1]);
                    if (!snippet[1].equals("")) {
                        snippetTextView.setText(snippet[1]);
                    }
                    break;
                case "Site":
//                    mapWindow = (View) LayoutInflater.from(context).inflate(R.layout.customized_site_map_info_window, null, true);
                    TextView snippetSiteID = (TextView) view.findViewById(R.id.mapInfoWindow_siteID);
                    if (snippetSiteID == null) {
                        Log.d("FROM MapWindowAdapter", "textview is null");
                    }
                    Log.d("FROM MapWindowAdapter", "snippet = " + snippet[1]);
                    if (!snippet[1].equals("")) {
                        snippetSiteID.setText(snippet[1]);
                    }

                    TextView snippetProjectID = (TextView) view.findViewById(R.id.mapInfoWindow_projectID);
                    Log.d("FROM MapWindowAdapter", "snippet = " + snippet[2]);
                    if (!snippet[2].equals("")) {
                        snippetProjectID.setText(snippet[2]);
                    }

                    TextView snippetHours = (TextView) view.findViewById(R.id.mapInfoWindow_hour);
                    Log.d("FROM MapWindowAdapter", "snippet = " + snippet[3]);
                    if (!snippet[3].equals("")) {
                        snippetHours.setText(snippet[3]);
                    }

                    TextView snippetHSE = (TextView) view.findViewById(R.id.mapInfoWindow_hse);
                    Log.d("FROM MapWindowAdapter", "snippet = " + snippet[4]);
                    if (!snippet[4].equals("")) {
                        snippetHSE.setText(snippet[4]);
                    }
                    break;
                case "Worker":
                    // TODO: Add customized map info window for worker
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        String[] snippet = marker.getSnippet().split(";");
        if (snippet.length > 0) {
            displayPurpose = snippet[0];
        }

        View mapWindow;
        if (displayPurpose.equals("User")) {
            mapWindow = (View) LayoutInflater.from(context).inflate(R.layout.customized_user_map_info_window, null);
        } else if (displayPurpose.equals("Site")) {
            mapWindow = (View) LayoutInflater.from(context).inflate(R.layout.customized_site_map_info_window, null);
        } else {
            mapWindow = (View) LayoutInflater.from(context).inflate(R.layout.customized_user_map_info_window, null);
        }
        renderWindowText(marker, mapWindow);
        return mapWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        String[] snippet = marker.getSnippet().split(";");
        if (snippet.length > 0) {
            displayPurpose = snippet[0];
        }

        View mapWindow;
        if (displayPurpose.equals("User")) {
            mapWindow = (View) LayoutInflater.from(context).inflate(R.layout.customized_user_map_info_window, null);
        } else if (displayPurpose.equals("Site")) {
            mapWindow = (View) LayoutInflater.from(context).inflate(R.layout.customized_site_map_info_window, null);
        } else {
            mapWindow = (View) LayoutInflater.from(context).inflate(R.layout.customized_user_map_info_window, null);
        }
        renderWindowText(marker, mapWindow);
        return mapWindow;
    }
}
