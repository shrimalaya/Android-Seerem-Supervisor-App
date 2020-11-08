package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.UI.util.MapInfoWindowAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SiteMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private boolean isLocationPermissionsGranted = false;
    private GoogleMap siteMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private EditText searchInputEditText;
    private ImageView myLocationImageView;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 10000;
    private static final float DEFAULT_ZOOM = 15f; // 15 = street level view

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        siteMap = googleMap;

        if (isLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // if somehow the permissions are still not granted...
                return;
            }

            // otherwise, if everything's okay...
            setupMap();
            siteMap.setMyLocationEnabled(true);
            siteMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_map);
        searchInputEditText = (EditText) findViewById(R.id.searchInputEditText);
        myLocationImageView = (ImageView) findViewById(R.id.myLocationImageView);

        checkGooglePlayServicesAvailable();
        getLocationPermission();

//        zoomToLocation();
    }

    private void zoomToLocation() {
//        TODO: get location of the clicked site and zoom in it
//        Intent intent = getIntent();
//        String location = intent.getStringExtra("LOCATION_FROM_SITEINFO");
//        String[] attributes = location.split(",");
//        double lat = Double.parseDouble(attributes[0]);
//        double lng = Double.parseDouble(attributes[1]);
//        moveCamera(new LatLng(lat, lng), DEFAULT_ZOOM, "Site Location");
    }

    private void checkGooglePlayServicesAvailable() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(SiteMapActivity.this);

        if (available != ConnectionResult.SUCCESS) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
                // no services, but can deal with it
                Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(SiteMapActivity.this, available, ERROR_DIALOG_REQUEST);
                dialog.show();
            } else {
                Toast.makeText(SiteMapActivity.this,
                                "No services available!",
                                Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupMap() {
        searchInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if (actionID == EditorInfo.IME_ACTION_SEARCH ||
                    actionID == EditorInfo.IME_ACTION_DONE ||
                    actionID == KeyEvent.ACTION_DOWN ||
                    actionID == KeyEvent.KEYCODE_ENTER) {
                    goToLocation();
                }
                return false;
            }
        });

        myLocationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });
        hideSoftKeyboard();
    }

    private void goToLocation() {
        String searchInput = searchInputEditText.getText().toString();
        Geocoder geocoder = new Geocoder(SiteMapActivity.this);

        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocationName(searchInput, 1);
        } catch (IOException e) {
            Log.e("SiteMapActivity", "goToLocation(): IOException" + e.getMessage());
        }

        if (addresses.size() > 0) {
            Address address = addresses.get(0);
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()),
                        DEFAULT_ZOOM,
                        address.getAddressLine(0));
        }
    }

    private void getDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (isLocationPermissionsGranted) {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM, "My Location");
                        } else {
                            Toast.makeText(SiteMapActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("SiteMapActivity error", "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        siteMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        siteMap.setInfoWindowAdapter(new MapInfoWindowAdapter(SiteMapActivity.this));

        String info = "Site ID: \n" +
                        "Project ID: \n" +
                        "Hours: ";

        MarkerOptions options = new MarkerOptions()
                                .position(latLng)
                                .title(title)
                                .snippet(info);
        siteMap.addMarker(options);
        hideSoftKeyboard();
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(SiteMapActivity.this);
    }

    private void getLocationPermission() {
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) ==
                                                PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) ==
                                                    PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        isLocationPermissionsGranted = false;

        switch(requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result == PackageManager.PERMISSION_GRANTED) {
                            isLocationPermissionsGranted = true;
                            break;
                        } else {
                            isLocationPermissionsGranted = false;
                        }
                    }
                    if (isLocationPermissionsGranted) {
                        initMap();
                    }
                }
        }
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}