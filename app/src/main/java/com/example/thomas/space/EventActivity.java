package com.example.thomas.space;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.List;

public class EventActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, OnMapReadyCallback {

    private static final String TAG = "EventActivity";
    private GestureDetectorCompat gestureDetector;

    private static final int ERROR_DIALOG_REQUEST = 9001; // error handling for wrong version (google map services)
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;


    // vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        ///// gesture /////
        // gesture setup
        this.gestureDetector = new GestureDetectorCompat(this, this);

        // move bottom text when tapping on the screen
        final Button click = findViewById(R.id.button_show);
        View bottomSheet = findViewById(R.id.bottom_sheet);
        final BottomSheetBehavior mBottomSheetBehaviour = BottomSheetBehavior.from(bottomSheet);

        // tap the bottom button in the view to change bottom sheet behaviour
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_HIDDEN
                        || mBottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    click.setText("");

                    // give some time for the bottom sheet to process then call geoLocate to adjust the zoom
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            geoLocate();
                        }
                    }, 250);
                } else {
                    mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);

                    // give some time for the bottom sheet to process then call geoLocate to adjust the zoom
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            geoLocate();
                        }
                    }, 250);
                }
            }
        });

        if (isServicesOK()) {
            // ask for location permission (method below)
            getLocationPermission();
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        // setup all info for the bottomSheet
        setBottomSheet();

        // Join event
        final CheckBox checkBox = findViewById(R.id.checkbox_going);
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Events");
        // Get the parse ID
        Intent intent = getIntent();
        final String id = intent.getStringExtra("id");
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkBox.isChecked()) {
                    // Update the item in parse
                    query.getInBackground(id, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            // update value
                            object.add("participants", ParseUser.getCurrentUser().getUsername());
                            object.saveInBackground();

                            Toast.makeText(EventActivity.this, "You are in", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // remove the user if it is unchecked
                    query.getInBackground(id, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            object.getList("participants").remove(ParseUser.getCurrentUser().getUsername());
                            List newList = object.getList("participants");
                            object.remove("participants");
                            object.put("participants", newList);

                            object.saveInBackground();
                        }
                    });
                }
            }
        });

        // Check checkbox. If user already selected the event then is will be checked
        query.getInBackground(id, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                // need try catch as the list might be null
                try {
                    List participant = object.getList("participants");
                    for (Object usernames : participant) {
                        if (object.getList("participants").contains(ParseUser.getCurrentUser().getUsername())) {
                            checkBox.setChecked(true);
                        }
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    ////////// Gesture //////////
    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float distanceX, float distanceY) {

//        // make the bottom sheet has a bottom sheet behaviour
//        View bottomSheet = findViewById(R.id.bottom_sheet);
//        BottomSheetBehavior mBottomSheetBehaviour = BottomSheetBehavior.from(bottomSheet);
//
//        if (distanceY < 0 ) { // && -500 <= distanceX && distanceX <= 500) {
//            // check if bottom sheet is hidden or expanded to do different action
//            if (mBottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_HIDDEN) {
//                mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            } else {
//                mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
//            }
//
//        } else if (distanceY > 0 ) { // && -500 <= distanceX && distanceX <= 500) {
//            if (mBottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED) {
//                mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            } else {
//                mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
//            }
//        }

        Log.d(TAG, "1: " + motionEvent.getAction() + " 2: " + motionEvent1.getAction() + " 3: " + distanceX + " 4: " + distanceY);
        return true;
    }
    ////////// Gesture //////////

    // method to stick everything tgt
    // get the touch event and check if it is a gestureDetector, if not the continue as it was
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);

    }
    ////////// Gesture ends //////////

    ////////// Maps start //////////

    /**
     * Checking google services version (for google map)
     *
     * @return
     */
    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(EventActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            // everything is fine and the user can  make map requests
            Log.d(TAG, "isServicesOK: Google Player Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // an error occurred but we can resolve it (e.g. wrong version)
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            // get a Dialog from google for the error
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(EventActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * Ask for permission for location for google map, including getting device location and putting a marker on the map
     */
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true; // if we already have permission then we set it to true
                /////////
            } else { // ask for permission
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else { // ask for permission
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is ready.");
        mMap = googleMap;

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // a text will slowly pop up when the bottom sheet is hidden
                View bottomSheet = findViewById(R.id.bottom_sheet);
                BottomSheetBehavior mBottomSheetBehaviour = BottomSheetBehavior.from(bottomSheet);
                if (mBottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Button click = findViewById(R.id.button_show);
                            click.setText("Tap me for info");
                            click.setTextColor(Color.parseColor("#BC757575"));
                        }
                    }, 250);
                }
            }
        });
    }

    /**
     * Get location through the main page activity recycler view
     */
    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        // get the search string
        Intent intent = getIntent();
        ParseQuery<ParseObject> query = new ParseQuery<>("Events");
        query.whereEqualTo("objectId", intent.getStringExtra("id"));
        query.setLimit(1);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        // set up for pointing location
                        // Toast.makeText(EventActivity.this, objects.get(0).getString("startTime"), Toast.LENGTH_SHORT).show();
                        String location = objects.get(0).getString("location");

                        List<Address> addressList = null;

                        // if search bar is not empty, geocode transfer words to Lat and Lng
                        if (location != null || !location.equals("")) {

                            Geocoder geocoder = new Geocoder(EventActivity.this);
                            try {

                                addressList = geocoder.getFromLocationName(location, 1);

                            } catch (IOException error) {
                                error.printStackTrace();
                            }

                            // adjust zoom according to bottom sheet, if bottom sheet is expanded the it will adjust its zoom
                            View bottomSheet = findViewById(R.id.bottom_sheet);
                            final BottomSheetBehavior mBottomSheetBehaviour = BottomSheetBehavior.from(bottomSheet);
                            double adjustment = 0;
                            if (mBottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                                adjustment = 0.007;
                            }

                            // to make sure the app won't crash if google map cannot find the location
                            try {
                                Address address = addressList.get(0); // get the first address in the addressList
                                LatLng adjustZoom = new LatLng(address.getLatitude() - adjustment, address.getLongitude()); // store the coordinate
                                LatLng latlng = new LatLng(address.getLatitude(), address.getLongitude()); // store the coordinate
                                mMap.clear();
                                mMap.addMarker(new MarkerOptions()
                                        .position(latlng)
                                        .title(address.getAddressLine(0))
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(adjustZoom, 14));
                            } catch (Exception notFound) {
                                Toast.makeText(EventActivity.this, "Location not found in google map", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Transfer all information from parse to bottom sheet
     */
    public void setBottomSheet() {
        Intent intent = getIntent();
        ParseQuery<ParseObject> query = new ParseQuery<>("Events");
        query.whereEqualTo("objectId", intent.getStringExtra("id"));
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            /**
             * It will search a list of ParseObject and store it in the List
             * The result is limited to 1 so we can access the data by get(0)
             *
             * @param objects
             * @param e
             */
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        TextView textView = findViewById(R.id.textView_eventName);
                        textView.setText(objects.get(0).getString("eventName"));
                        textView = findViewById(R.id.textView_startDate);
                        textView.setText(objects.get(0).getString("startDate"));
                        textView = findViewById(R.id.textView_startTime);
                        textView.setText(objects.get(0).getString("startTime"));
                        textView = findViewById(R.id.textView_endDate);
                        textView.setText(objects.get(0).getString("endDate"));
                        textView = findViewById(R.id.textView_endTime);
                        textView.setText(objects.get(0).getString("endTime"));
                        textView = findViewById(R.id.textView_location);
                        textView.setText(objects.get(0).getString("location"));
                        geoLocate();
                        textView = findViewById(R.id.textView_detail);
                        textView.setText(objects.get(0).getString("details"));
                        textView = findViewById(R.id.textView_going);
                        // String string = (String.join(", ",objects.get(0).getList("participants").toString());
                        // get the text and get rid of the brackets
                        if (objects.get(0).getList("participants") != null) {
                            String temp = objects.get(0).getList("participants").toString();
                            temp = temp.substring(temp.indexOf("[") + 1, temp.indexOf("]"));
                            textView.setText(temp);
                        }
                        textView = findViewById(R.id.textView_creator);
                        textView.setText(objects.get(0).getString("username"));

                        // Only the creator can edit or delete the event
                        String creator = textView.getText().toString();
                        if (!creator.equals(ParseUser.getCurrentUser().getUsername())) {
                            Button button = findViewById(R.id.button_delete);
                            button.setEnabled(false);
                            button.setTextColor(Color.parseColor("#A9A9A9"));
                            button = findViewById(R.id.button_edit);
                            button.setEnabled(false);
                            button.setTextColor(Color.parseColor("#A9A9A9"));
                        }
                    }
                }
            }
        });

    }

    public void backToMainPage(View view) {
        Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
        finish(); //prevent going back to EventActivity page
        startActivity(intent);
    }

    public void deleteEvent(final View view) {
        Intent intent = getIntent();
        ParseQuery<ParseObject> query = new ParseQuery<>("Events");
        query.whereEqualTo("objectId", intent.getStringExtra("id"));
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject status, ParseException e) {
                // MainActivity is the context not the application
                // this will be referring onItemClick only, not MainActivity, so we use MainActivity.this
                new AlertDialog.Builder(EventActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete this event?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // delete event
                                try {
                                    status.delete();
                                    status.saveInBackground();
                                    backToMainPage(view);

                                } catch (ParseException ex) {
                                    ex.printStackTrace();
                                }

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    public void editEvent(View view) {
        Intent getinfo = getIntent();
        String id = getinfo.getStringExtra("id");
        Intent intent = new Intent(getApplicationContext(), CreateEventActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("edit", true);
        startActivity(intent);
        // make and if state to see if it is edit or not and put the item in the text field
        // make sure on user can edit and delete the event
    }
}