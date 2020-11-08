package com.example.supervisor_seerem.UI.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.supervisor_seerem.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mapWindow;
    private Context context;

    public MapInfoWindowAdapter(Context context) {
        this.context = context;
        mapWindow = LayoutInflater.from(context).inflate(R.layout.customized_info_window, null);
    }

    private void renderWindowText (Marker marker, View view) {

        // TODO: get information from worksite or user to display in the textviews below
        String title = marker.getTitle();
        TextView titleTextView = (TextView) view.findViewById(R.id.title);
        if (!title.equals("")) {
            titleTextView.setText(title);
        }

        String snippet = marker.getSnippet();
        TextView snippetTextView = (TextView) view.findViewById(R.id.infoSnippet);
        if (!snippet.equals("")) {
            snippetTextView.setText(snippet);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mapWindow);
        return mapWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mapWindow);
        return mapWindow;
    }
}
