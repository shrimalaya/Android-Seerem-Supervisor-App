package com.example.supervisor_seerem.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.supervisor_seerem.R;
import com.example.supervisor_seerem.UI.util.MapInfoWindowAdapter;
import com.example.supervisor_seerem.model.CONSTANTS;
import com.example.supervisor_seerem.model.DocumentManager;
import com.example.supervisor_seerem.model.ModelLocation;
import com.example.supervisor_seerem.model.Site;
import com.example.supervisor_seerem.model.Worker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SiteMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private boolean isLocationPermissionsGranted = false;
    private GoogleMap siteMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 10000;
    private static final float DEFAULT_ZOOM = 15f; // 15 = around street level view
    private static final float MAX_ZOOM = 20;
    private static final String LAUNCH_FROM_ACTIVITY = "Launched map from other activities";
    private String previousActivity;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ListenerRegistration registration;
    private DocumentManager documentManager = DocumentManager.getInstance();
    private Site clickedSite;

    private List<DocumentSnapshot> allWorkersDocs = new ArrayList<>();
    private List<DocumentSnapshot> onlineWorkersDocs = new ArrayList<>();
    private List<DocumentSnapshot> userWorkersDocs = new ArrayList<>();
    private List<DocumentSnapshot> allWorksitesDocs = new ArrayList<>();
    private List<DocumentSnapshot> userWorksitesDocs = new ArrayList<>();
    private List<DocumentSnapshot> onlineWorksitesDocs = new ArrayList<>();
    private List<DocumentSnapshot> offlineWorksitesDocs = new ArrayList<>();
    private List<DocumentSnapshot> showWorkersDocs = new ArrayList<>();
    private List<DocumentSnapshot> showWorksitesDocs = new ArrayList<>();

    private Worker clickedWorker;
    private HashMap<String, Marker> hashMapWorkerMarker;
    private List<Marker> workerMarkers = new ArrayList<>();
    private List<Marker> worksiteMarkers = new ArrayList<>();

    private Boolean showAllWorkers = false;
    private Boolean showAllWorksites = false;

    private String dayKey = CONSTANTS.SUNDAY_KEY;

    private SearchView mapSearchView;
    private FloatingActionButton myLocationFAB;
    private ImageView moreOptions;
    private DrawerLayout drawer;
    private Handler handler;
    private Runnable runnable;

    public static Intent launchMapIntent(Context context) {
        Intent mapIntent = new Intent(context, SiteMapActivity.class);
        return mapIntent;
    }

    public static Intent launchMapWithZoomToLocation(Context context, String fromActivity) {
        Intent mapIntent = new Intent(context, SiteMapActivity.class);
        mapIntent.putExtra(LAUNCH_FROM_ACTIVITY, fromActivity);
        return mapIntent;
    }

    private void getPreviousActivityName() {
        Intent intent = getIntent();
        previousActivity = intent.getStringExtra(LAUNCH_FROM_ACTIVITY);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        siteMap = googleMap;
        siteMap.setMaxZoomPreference(MAX_ZOOM);
        siteMap.getUiSettings().setZoomControlsEnabled(true);

        if (isLocationPermissionsGranted) {
            if (ActivityCompat.checkSelfPermission(this, FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // if somehow the permissions are still not granted, exit the method
                return;
            }

            // otherwise, if everything's okay, then set up the map properly
            siteMap.setMyLocationEnabled(true);
            siteMap.getUiSettings().setMyLocationButtonEnabled(false);

            Log.d("FROM MAP: ", "previousActivity is: " + previousActivity);
            if (previousActivity != null && previousActivity.equals("SiteInfo")) { // if the user clicks on a site from the list of worksites
                zoomToSiteLocation();
            } else if (previousActivity != null && previousActivity.equals("WorkerInfo")) { // if the user clicks on a worker from the list of workers
                zoomToWorkerPosition();
            } else {
                // otherwise, just show my current location and all workers' and worksites' locations
                Toast.makeText(SiteMapActivity.this, "Current time: " + Calendar.getInstance().getTime(), Toast.LENGTH_LONG).show();
                getDeviceLocation();
            }
        }

        updateDisplayWorkers();
        updateDisplayWorksites();

        siteMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                CameraUpdate location = CameraUpdateFactory.
                        newLatLngZoom(marker.getPosition(), DEFAULT_ZOOM);
                siteMap.animateCamera(location);
                marker.showInfoWindow();
                return true;
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_map);
        setupNavigationBar();
        setupSidebarNavigationDrawer();
        getPreviousActivityName();

        checkGooglePlayServicesAvailable();
        getLocationPermission();

        retrieveData();

        setupDisplayOptionsView();

        myLocationFAB = (FloatingActionButton) findViewById(R.id.my_location_floating_button);
        setupMyLocationFloatingButton();

        mapSearchView = (SearchView) findViewById(R.id.sitemap_search_view);
        setupSearchView();

//        hashMapWorkerMarker = new HashMap<>();

        handler =new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                updateDisplayWorkers();
                updateDisplayWorksites();
                handler.postDelayed(this, 10000);
            }
        };

        handler.postDelayed(runnable, 10000);

    }

    private void retrieveData() {
        allWorksitesDocs.clear();
        allWorksitesDocs.addAll(documentManager.getSites());
        Log.d("FROM MAP", "onCreate(): sitesList.size() = " + allWorksitesDocs.size());

        userWorksitesDocs.clear();
        for (DocumentSnapshot site: documentManager.getSites()) {
            for(DocumentSnapshot supervisor: documentManager.getSupervisors()) {
                if (site.getString(CONSTANTS.ID_KEY).equals(supervisor.getString(CONSTANTS.WORKSITE_ID_KEY))) {
                    userWorksitesDocs.add(site);
                }
            }
        }

        allWorkersDocs.clear();
        allWorkersDocs.addAll(documentManager.getWorkers());
        Log.d("FROM MAP", "onCreate(): workersList.size() = " + allWorkersDocs.size());

        userWorkersDocs.clear();
        for (DocumentSnapshot doc : documentManager.getWorkers()) {
            if ((doc.getString(CONSTANTS.SUPERVISOR_ID_KEY)).equals(documentManager.getCurrentUser().getId())) {
                userWorkersDocs.add(doc);
            }
        }

        updateDayOfWeek();
        System.out.println("TEST6> Day of week = " + dayKey);
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
                        R.string.map_noServices_error_message,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLocationPermission() {
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionsGranted = true;
                initializeMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(SiteMapActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        isLocationPermissionsGranted = false;

        switch (requestCode) {
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
                        initializeMap();
                    }
                }
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
                            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            zoomCamera(latLng, DEFAULT_ZOOM);
                            displayUserMarker(latLng);
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

    private void showWorksitesLocations() {
        for (Marker marker: worksiteMarkers) {
            marker.remove();
        }
        worksiteMarkers.clear();

        for (DocumentSnapshot site : onlineWorksitesDocs) {
            Site newSite = createSite(site);
            displayWorksiteMarker(newSite, true);
            displayMasterpointMarker(new LatLng(newSite.getMasterpoint().getLatitude(),
                    newSite.getMasterpoint().getLongitude()));
        }

        for (DocumentSnapshot site : offlineWorksitesDocs) {
            Site newSite = createSite(site);
            displayWorksiteMarker(newSite, false);
            displayMasterpointMarker(new LatLng(newSite.getMasterpoint().getLatitude(),
                    newSite.getMasterpoint().getLongitude()));
        }
    }

    private void showWorkersPositions() {
        for (Marker marker: workerMarkers) {
            marker.remove();
        }
        workerMarkers.clear();

        Query query = db.collection(CONSTANTS.WORKERS_COLLECTION)
                         .whereEqualTo(CONSTANTS.COMPANY_ID_KEY, documentManager.getCurrentUser().getCompany_id());
        registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) { // if error occurs
                    System.err.println("Listen failed: " + error);
                }

                System.out.println("TEST6> Size of snapshot for workers = " + snapshots.getDocuments().size());
                for (DocumentSnapshot doc : snapshots) {
                    checkOnline(doc);
                }
            }
        });
    }

    private void checkOnline(DocumentSnapshot doc) {
        System.out.println("TEST6> online? " + onlineWorkersDocs.contains(doc));
        System.out.println("TEST6> ID: " + doc.getString(CONSTANTS.ID_KEY));
        System.out.println("TEST6> CHECK no of online workers = " + onlineWorkersDocs.size());

        for (DocumentSnapshot onlineWorker: onlineWorkersDocs) {
            if(onlineWorker.getString(CONSTANTS.ID_KEY).equals(doc.getString(CONSTANTS.ID_KEY))) {
                Worker newWorker = createWorker(doc);
                displayWorkerMarker(newWorker);
            }
        }

//        if (onlineWorkersDocs.contains(doc)) {
//            Worker newWorker = createWorker(doc);
//            Marker marker = hashMapWorkerMarker.get(newWorker.getEmployeeID());
//            if (marker != null) {
//                marker.remove();
//            }
//            displayWorkerMarker(newWorker);
//        } else {
//            Worker newWorker = createWorker(doc);
//            Marker marker = hashMapWorkerMarker.get(newWorker.getEmployeeID());
//
//            if (marker!= null) {
//                marker.remove();
//            }
//        }
    }

    private void zoomCamera(LatLng latLng, float zoom) {
        // zoom to the specific latLng
        siteMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        siteMap.setInfoWindowAdapter(new MapInfoWindowAdapter(SiteMapActivity.this));

        hideSoftKeyboard();
    }

    private void zoomToSiteLocation() {
        Intent intent = getIntent();
        String clickedSiteID = intent.getStringExtra("SITE ID FROM SiteInfoActivity");
        showAllWorksites = intent.getBooleanExtra("SHOW ALL SITES", false);
        Log.d("FROM MAP", "clickedSiteID = " + clickedSiteID);

        DocumentSnapshot currentSite = null;
        Log.d("FROM MAP", "zoomToSiteLocation(): sitesList.size() = " + allWorksitesDocs.size());
        for (DocumentSnapshot site : allWorksitesDocs) {
            Log.d("FROM MAP", site.toString());
            Log.d("FROM MAP", "siteID = " + site.getString(CONSTANTS.ID_KEY));
            if (site.getString(CONSTANTS.ID_KEY).equals(clickedSiteID)) {
                currentSite = site;
                break;
            }
        }

        if (currentSite != null) {
            clickedSite = createSite(currentSite);
            System.out.println(clickedSite.toString());
            zoomCamera(new LatLng(clickedSite.getLocation().getLatitude(),
                            clickedSite.getLocation().getLongitude()),
                    DEFAULT_ZOOM);

//            showWorksitesLocations();
//            showWorkersPositions();
        }
    }

    private void zoomToWorkerPosition() {
        Intent intent = getIntent();
        String clickedWorkerID = intent.getStringExtra("WorkerID FROM WorkerInfoActivity");
        showAllWorkers = intent.getBooleanExtra("SHOW ALL WORKERS", false);
        Log.d("FROM MAP", "clickedWorkerID = " + clickedWorkerID);

        DocumentSnapshot currentWorker = null;
        Log.d("FROM MAP", "zoomToSiteLocation(): workersList.size() = " + allWorkersDocs.size());
        for (DocumentSnapshot worker : allWorkersDocs) {
            Log.d("FROM MAP", worker.toString());
            Log.d("FROM MAP", "workerID = " + worker.getString(CONSTANTS.ID_KEY));
            if (worker.getString(CONSTANTS.ID_KEY).equals(clickedWorkerID)) {
                currentWorker = worker;
                break;
            }
        }

        if (currentWorker != null) {
            clickedWorker = createWorker(currentWorker);
            System.out.println(clickedWorker.toString());
            zoomCamera(new LatLng(clickedWorker.getLocation().getLatitude(),
                            clickedWorker.getLocation().getLongitude()),
                    DEFAULT_ZOOM);

            // zoom to clicked worker, but also display all worksites and workers around
//            showWorksitesLocations();
//            showWorkersPositions();
        }
    }

    // For peg icon
    // Learned from:https://stackoverflow.com/questions/42365658/custom-marker-in-google-maps-in-android-with-vector-asset-icon
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void displayWorksiteMarker(Site site, Boolean isOnline) {
        String info = "Site;" + site.getID() +
                ";" + site.getSiteName() +
                ";" + site.getProjectID() +
                ";" + site.getOperationHour();

        LatLng latLng = new LatLng(site.getLocation().getLatitude(),
                site.getLocation().getLongitude());

        // set worksite's marker's color to green
        MarkerOptions optionsOnline = new MarkerOptions()
                .position(latLng)
                .title(getResources().getString(R.string.map_info_window_site_location_title))
                .snippet(info)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        MarkerOptions optionsOffline = new MarkerOptions()
                .position(latLng)
                .title(getResources().getString(R.string.map_info_window_site_location_title))
                .snippet(info)
                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_peg_grey));

        if (isOnline) {
            worksiteMarkers.add(siteMap.addMarker(optionsOnline));
        } else {
            worksiteMarkers.add(siteMap.addMarker(optionsOffline));
        }

    }

    private void displayWorkerMarker(Worker worker) {
        String info = "Worker;" + worker.getEmployeeID() +
                ";" + worker.getFirstName() + " " + worker.getLastName() +
                ";" + worker.getCompanyID() +
                ";" + worker.getSupervisorID();

        LatLng latLng = new LatLng(worker.getLocation().getLatitude(),
                worker.getLocation().getLongitude());

        // set worker's marker's color to blue
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(getResources().getString(R.string.map_info_window_worker_location_title))
                .snippet(info)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

        workerMarkers.add(siteMap.addMarker(options));
        Log.d("FROM MAP", "Marker's color is violet");
    }

    private void displayUserMarker(LatLng latLng) {
        String info = "User; " + getResources().getString(R.string.map_info_window_user_location_snippet);

        // set user's marker's color to red
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(getResources().getString(R.string.map_info_window_user_location_title))
                .snippet(info)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        siteMap.addMarker(options);
        Log.d("FROM MAP", "Marker's color is red");
    }

    private void displayMasterpointMarker(LatLng latLng) {
        // set customized peg for marker
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(getResources().getString(R.string.map_info_window_masterpoint_title))
                .snippet(getResources().getString(R.string.map_info_window_masterpoint_snippet))
                .icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap("alert_emergency_light")));

        worksiteMarkers.add(siteMap.addMarker(options));
        Log.d("FROM MAP", "Masterpoint custom peg");
    }

    private Site createSite(DocumentSnapshot site) {
        String siteID = site.getString(CONSTANTS.ID_KEY);
        String siteName = site.getString(CONSTANTS.WORKSITE_NAME_KEY);
        ModelLocation siteLocation = new ModelLocation(site.getGeoPoint(CONSTANTS.LOCATION_KEY).getLatitude(),
                site.getGeoPoint(CONSTANTS.LOCATION_KEY).getLongitude());
        String projectID = site.getString(CONSTANTS.PROJECT_ID_KEY);
        ModelLocation masterpointLocation = new ModelLocation(site.getGeoPoint(CONSTANTS.MASTERPOINT_KEY).getLatitude(),
                site.getGeoPoint(CONSTANTS.MASTERPOINT_KEY).getLongitude());
        String hseLink = site.getString(CONSTANTS.HSE_LINK_KEY);
        String operationHour = site.getString(CONSTANTS.OPERATION_HRS_KEY);

        Site newSite = new Site(siteID, projectID, siteName, siteLocation,
                masterpointLocation, hseLink, operationHour);

        return newSite;
    }

    private Worker createWorker(DocumentSnapshot worker) {
        String workerID = worker.getString(CONSTANTS.ID_KEY);
        String firstName = worker.getString(CONSTANTS.FIRST_NAME_KEY);
        String lastName = worker.getString(CONSTANTS.LAST_NAME_KEY);
        String supervisorID = worker.getString(CONSTANTS.SUPERVISOR_ID_KEY);
        String siteID = worker.getString(CONSTANTS.WORKSITE_ID_KEY);
        String companyID = worker.getString(CONSTANTS.COMPANY_ID_KEY);
        ModelLocation workerPosition = new ModelLocation(worker.getGeoPoint(CONSTANTS.LOCATION_KEY).getLatitude(),
                worker.getGeoPoint(CONSTANTS.LOCATION_KEY).getLongitude());
        List<String> skills = new ArrayList<String>();
        String[] workerSkills = worker.getString(CONSTANTS.SKILLS_KEY).split(",");
        for (String skill : workerSkills) {
            skills.add(skill);
        }

        Worker newWorker = new Worker(workerID, firstName, lastName, supervisorID,
                siteID, companyID, workerPosition, skills);

        return newWorker;
    }

    private void updateDisplayWorksites() {
        if(showAllWorksites) {
            showWorksitesDocs.clear();
            showWorksitesDocs.addAll(allWorksitesDocs);
        } else {
            showWorksitesDocs.clear();
            showWorksitesDocs.addAll(userWorksitesDocs);
        }


        List<DocumentSnapshot> tempOnline = new ArrayList<>();
        List<DocumentSnapshot> tempOffline = new ArrayList<>();

        for (DocumentSnapshot doc : showWorksitesDocs) {
            try {
                Boolean withinOpHours = timeParser(doc.getString(CONSTANTS.OPERATION_HRS_KEY));
                if (withinOpHours) {
                    tempOnline.add(doc);
                } else {
                    tempOffline.add(doc);
                }
            } catch (ParseException e) {
                Log.d("SITEMAP","Parse Exception" + e.toString());
                tempOnline.add(doc); // A site with no operation hours specified will be added to Online Docs
            }
        }

        Boolean onlineChanged = !tempOnline.equals(onlineWorksitesDocs);
        Boolean offlineChanged = !tempOffline.equals(offlineWorksitesDocs);
        Boolean listChanged = onlineChanged || offlineChanged;

        if(listChanged) {
            onlineWorksitesDocs.clear();
            offlineWorksitesDocs.clear();

            onlineWorksitesDocs.addAll(tempOnline);
            offlineWorksitesDocs.addAll(tempOffline);

            showWorksitesLocations();
        }
    }

    private void updateDisplayWorkers() {
        if (showAllWorkers) {
            showWorkersDocs.clear();
            showWorkersDocs.addAll(allWorkersDocs);
        } else {
            showWorkersDocs.clear();
            showWorkersDocs.addAll(userWorkersDocs);
        }

        List<DocumentSnapshot> tempOnline = new ArrayList<>();

        // Check for "TODAY's" available workers who are online
        // A worker with no availability data will be shown as offline
        for (DocumentSnapshot worker : showWorkersDocs) {
            DocumentSnapshot avail = null;
            for (DocumentSnapshot availability : documentManager.getAvailabilities()) {
                if (availability.getString(CONSTANTS.ID_KEY).equals(worker.getString(CONSTANTS.ID_KEY))) {
                    avail = availability;
                    try {
                        Boolean withinOpHours = timeParser(checkNull(availability.getString(dayKey)));
                        if (withinOpHours) {
                            tempOnline.add(worker);
                        }
                    } catch (ParseException e) {
                        Log.d("SITEMAP", e.toString());
                        // offline
                    }
                }
            }

            if (avail == null) { // No availability data found
                // offline
            }
        }

        Boolean listChanged = !tempOnline.equals(onlineWorkersDocs);

        System.out.println("TEST6> listChanged = " + listChanged);
        System.out.println("TEST6> Size of online workers = " + tempOnline.size());

        if(listChanged) {
            onlineWorkersDocs.clear();
            onlineWorkersDocs.addAll(tempOnline);

            showWorkersDocs.clear();
            showWorkersDocs.addAll(onlineWorkersDocs);

            if (onlineWorkersDocs.isEmpty()) {
                Toast.makeText(this, "No Online Workers!", Toast.LENGTH_LONG).show();
            }

            showWorkersPositions();
        }
    }

    /**
     * HH:mm = 24hr format
     * hh:mm = 12 hr format
     * Return true if timeString includes the current time
     */
    private boolean timeParser(String timeString) throws ParseException {
        String arr[] = null;
        if(timeString != null) {
            if(timeString.split("-") != null) {
                arr = timeString.split("-");
            }
        }

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date d1 = dateFormat.parse(arr[0]);
        Date d2 = dateFormat.parse(arr[1]);
        String currTime = dateFormat.format(Calendar.getInstance().getTime());
        Date current = dateFormat.parse(currTime);

        Boolean withinOpHrs = (current.getTime() >= d1.getTime()) && (d2.getTime() >= current.getTime());

        return withinOpHrs;
    }

    private String checkNull(String data) {
        if (data == null || data.isEmpty()) {
            return " - ";
        } else {
            return data;
        }
    }

    private void updateDayOfWeek() {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.MONDAY:
                dayKey = CONSTANTS.MONDAY_KEY;
                break;
            case Calendar.TUESDAY:
                dayKey = CONSTANTS.TUESDAY_KEY;
                break;
            case Calendar.WEDNESDAY:
                dayKey = CONSTANTS.WEDNESDAY_KEY;
                break;
            case Calendar.THURSDAY:
                dayKey = CONSTANTS.THURSDAY_KEY;
                break;
            case Calendar.FRIDAY:
                dayKey = CONSTANTS.FRIDAY_KEY;
                break;
            case Calendar.SATURDAY:
                dayKey = CONSTANTS.SATURDAY_KEY;
                break;
            case Calendar.SUNDAY:
                dayKey = CONSTANTS.SUNDAY_KEY;
                break;
        }
    }

    public Bitmap resizeBitmap(String imageName) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),
                getResources().getIdentifier(imageName, "drawable", getPackageName()));
        int imageWidth = imageBitmap.getWidth();
        int imageHeight = imageBitmap.getHeight();
        Log.d("FROM MAP", "imageWidth = " + imageWidth + " and imageHeight = " + imageHeight);

        // Google map default marker is of size 20x32 (width x height) pixels
        // 1 pixel = 24 bits for R, G, B --> each is 8 bits
        float scaleX = (float) 20 * 8 / imageWidth;
        float scaleY = (float) 32 * 8 / imageHeight;
        Log.d("FROM MAP", "scaleX = " + scaleX + " and scaleY = " + scaleY);
        float scaleFactor = 0;
        if (scaleX <= scaleY) {
            scaleFactor = scaleX;
        } else {
            scaleFactor = scaleY;
        }
        Log.d("FROM MAP", "scaleFactor = " + scaleFactor);

        int newWidth = (int) Math.round(imageWidth * scaleFactor);
        int newHeight = (int) Math.round(imageHeight * scaleFactor);
        Log.d("FROM MAP", "newWidth = " + newWidth + " and newHeight = " + newHeight);

        return Bitmap.createScaledBitmap(imageBitmap, newWidth, newHeight, false);
    }

    private Site searchForSite(String input) {
        DocumentSnapshot result = null;
        Pattern pattern = Pattern.compile(input, Pattern.CASE_INSENSITIVE);

        for (DocumentSnapshot doc : documentManager.getSites()) {
            Matcher matcher = pattern.matcher(doc.getString(CONSTANTS.ID_KEY));
            if (matcher.find() == true) {
                result = doc;
                break;
            }
        }

        Site newSite = null;
        if (result != null) {
            newSite = createSite(result);
        }
        return newSite;
    }

    private Worker searchForWorker(String input) {
        DocumentSnapshot result = null;
        Pattern pattern = Pattern.compile(input, Pattern.CASE_INSENSITIVE);

        for (DocumentSnapshot doc : documentManager.getWorkers()) {
            Matcher matcher = pattern.matcher(doc.getString(CONSTANTS.ID_KEY));
            if (matcher.find() == true) {
                result = doc;
                break;
            }
        }
        Worker newWorker = null;
        if (result != null) {
            newWorker = createWorker(result);
        }
        return newWorker;
    }

    private void setupSearchView() {
        mapSearchView.setQueryHint(getString(R.string.search_input_hint));
        mapSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapSearchView.setIconified(false);
            }
        });

        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String input) {
                mapSearchView.setQueryHint(getString(R.string.clear_search_input_hint));
                if (input.contains("WS")) {
                    Site searchedSite = searchForSite(input);
                    if (searchedSite != null) {
                        zoomCamera(new LatLng(searchedSite.getLocation().getLatitude(),
                                        searchedSite.getLocation().getLongitude()),
                                DEFAULT_ZOOM);
                        showWorkersPositions();
                        showWorksitesLocations();
                    } else {
                        Toast.makeText(getApplicationContext(), "No work sites found!", Toast.LENGTH_SHORT).show();
                        mapSearchView.setQuery("", false);
                        mapSearchView.clearFocus();
                        mapSearchView.setIconified(true);
                    }
                } else if (input.contains("WK")) {
                    Worker searchedWorker = searchForWorker(input);
                    if (searchedWorker != null) {
                        zoomCamera(new LatLng(searchedWorker.getLocation().getLatitude(),
                                        searchedWorker.getLocation().getLongitude()),
                                DEFAULT_ZOOM);
                        showWorkersPositions();
                        showWorksitesLocations();
                    } else {
                        Toast.makeText(getApplicationContext(), "No worker found!", Toast.LENGTH_SHORT).show();
                        mapSearchView.setQuery("", false);
                        mapSearchView.clearFocus();
                        mapSearchView.setIconified(true);
                    }
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String input) {
                if (input.equals("")) {
                    mapSearchView.setQueryHint(getString(R.string.search_input_hint));
                }
                return false;
            }
        });

        hideSoftKeyboard();
    }

    private void setupMyLocationFloatingButton() {
        myLocationFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousActivity = "SiteMap";
                siteMap.clear();
                Log.d("FROM MAP", "Clear map and go to my location!");

                getDeviceLocation();
            }
        });
    }

    private void setupDisplayOptionsView() {
        moreOptions = (ImageView) findViewById(R.id.ic_more_options);
        moreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu displayPopupMenu = new PopupMenu(SiteMapActivity.this, moreOptions);
                displayPopupMenu.getMenuInflater().inflate(R.menu.menu_sitemap_display_option, displayPopupMenu.getMenu());
                MenuItem showAllWorkersTxt = displayPopupMenu.getMenu().getItem(0);
                MenuItem showAllWorksitesTxt = displayPopupMenu.getMenu().getItem(1);

                if(showAllWorkers) {
                    showAllWorkersTxt.setTitle(getString(R.string.display_my_workers));
                } else {
                    showAllWorkersTxt.setTitle(getString(R.string.display_all_workers));
                }

                if(showAllWorksites) {
                    showAllWorksitesTxt.setTitle(getString(R.string.display_my_worksites));
                } else {
                    showAllWorksitesTxt.setTitle(getString(R.string.display_all_worksites));
                }

                displayPopupMenu.show();
                displayPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_display_workers_filter:
                                if (showAllWorkers) {
                                    showAllWorkers = false;
                                } else {
                                    showAllWorkers = true;
                                }

                                updateDisplayWorkers();
                                return true;

                            case R.id.menu_display_worksites_filter:
                                if (showAllWorksites) {
                                    showAllWorksites= false;
                                } else {
                                    showAllWorksites = true;
                                }

                                updateDisplayWorksites();
                                return true;
                        }
                        return true;
                    }
                });
            }
        });
    }

    private void setupSidebarNavigationDrawer() {
        drawer = findViewById(R.id.sidebar_drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.sidebar_navigation_view);

        // customized toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_for_sidebar);
        setSupportActionBar(toolbar);

        // header
        View headerView = navigationView.getHeaderView(0);
        String savedFirstName = documentManager.getCurrentUser().getFirstName();
        String savedLastName = documentManager.getCurrentUser().getLastName();
        TextView sidebarFullName = (TextView) headerView.findViewById(R.id.sidebar_header_fullname_textview);
        sidebarFullName.setText(savedFirstName + " " + savedLastName);

        final SharedPreferences loginSharedPreferences = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        String savedUsername = loginSharedPreferences.getString("username", null);
        TextView sidebarUsername = (TextView) headerView.findViewById(R.id.sidebar_header_username_textview);
        sidebarUsername.setText(savedUsername);

        // onClickListener
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.sidebar_user:
                        Intent userIntent = UserInfoActivity.launchUserInfoIntent(SiteMapActivity.this);
                        startActivity(userIntent);
                        finish();
                        break;

                    case R.id.sidebar_overtime:
                        Intent overtimeIntent = AddOvertimeActivity.launchAddOvertimeIntent(SiteMapActivity.this);
                        startActivity(overtimeIntent);
                        break;

                    case R.id.sidebar_day_leave:
                        Intent dayLeaveIntent = AddDayLeaveActivity.launchAddDayLeaveIntent(SiteMapActivity.this);
                        startActivity(dayLeaveIntent);
                        break;

                    case R.id.sidebar_search:
                        break;

                    case R.id.sidebar_all_workers:
                        Intent workerIntent = WorkerInfoActivity.launchWorkerInfoIntent(SiteMapActivity.this);
                        startActivity(workerIntent);
                        finish();
                        break;

                    case R.id.sidebar_company:
                        break;

                    case R.id.sidebar_ui_preferences:
                        Intent uiPrefsIntent = UIPreferencesActivity.launchUIPreferencesIntent(SiteMapActivity.this);
                        startActivity(uiPrefsIntent);
                        break;

                    case R.id.sidebar_light_dark_mode:
                        Intent changeThemeIntent = ChangeThemeActivity.launchChangeThemeIntent(SiteMapActivity.this);
                        startActivity(changeThemeIntent);
                        break;

                    case R.id.sidebar_languages:
                        Intent changeLanguageIntent = ChangeLanguageActivity.launchChangeLanguageIntent(SiteMapActivity.this);
                        startActivity(changeLanguageIntent);
                        break;

                    case R.id.sidebar_change_password:
                        Intent changePasswordIntent = ChangePasswordActivity.launchChangePasswordIntent(SiteMapActivity.this);
                        startActivity(changePasswordIntent);
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void setupNavigationBar() {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavigationBar);
        navigation.setSelectedItemId(R.id.mapNavigation);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.workerNavigation:
                        Intent workerIntent = WorkerInfoActivity.launchWorkerInfoIntent(SiteMapActivity.this);
                        finish();
                        startActivity(workerIntent);
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.siteNavigation:
                        Intent siteIntent = SiteInfoActivity.launchSiteInfoIntent(SiteMapActivity.this);
                        finish();
                        startActivity(siteIntent);
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.mapNavigation:
                        // home activity --> do nothing
                        return true;

                    case R.id.sensorNavigation:
                        Intent sensorIntent = SensorsUsageActivity.launchSensorUsageIntent(SiteMapActivity.this);
                        finish();
                        startActivity(sensorIntent);
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.userNavigation:
                        Intent userIntent = UserInfoActivity.launchUserInfoIntent(SiteMapActivity.this);
                        finish();
                        startActivity(userIntent);
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(registration!=null) {
                registration.remove();
            }
            super.onBackPressed();
//            finishAffinity();
//            Intent intent = UserInfoActivity.launchUserInfoIntent(SiteMapActivity.this);
//            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 10000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // stop listening to database real time updates to protect user's device's bandwidth
        if(registration!=null) {
            registration.remove();
        }
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}