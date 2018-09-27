package com.example.gavv.my_groww_project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import controllers.GuideNoteController;
import controllers.LocationController;

public class HelpeeMapsActivity extends AppCompatActivity {

    GuideNoteController guideNoteController;

    LocationManager locationManager;
    LocationListener locationListener;

    private LocationController userLocationController;

    private static final DatabaseReference ROOT_REF =
            FirebaseDatabase.getInstance().getReference();
    private static final DatabaseReference USER_REF = ROOT_REF.child("users");
    private static final String HELPEE_UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private Button requestButton;
    private String helperUid;
    private boolean isMakingRequest;


    /**
     * A function to listen to the GPS provider.
     */
    public void startListening() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        }
    }

    /**
     * A function to pair the helpee with the available helper on the list.
     */
    private void findHelper() {

        if (helperUid != null) {

            // Update the helper's data to indicate there is a request.
            DatabaseReference helperRef = USER_REF.child(helperUid);

            HashMap<String, Object> request = new HashMap<String, Object>();
            request.put("requestHelpeeID", FirebaseAuth.getInstance().getCurrentUser()
                    .getUid());

            helperRef.updateChildren(request);


            // Set the isHelping flag on the helper to be true.
            DatabaseReference helpersAvaiRef = helperRef.child("isHelping");
            helpersAvaiRef.setValue(true);

            // Save the helper UID in the helpee's database.
            DatabaseReference reqHelpee = ROOT_REF.child("Requests").child(HELPEE_UID);
            HashMap<String, Object> helperUidData = new HashMap<>();
            helperUidData.put("helperUid", helperUid);
            reqHelpee.updateChildren(helperUidData);
        }

    }

    /**
     * A function to update helpee's location on the database.
     */
    private void updateLocationOnDatabase(DatabaseReference ref) {

        GeoFire geoFire = new GeoFire(ref);

        geoFire.setLocation(HELPEE_UID, new GeoLocation(
                userLocationController.getUserLocation().getLatitude(),
                userLocationController.getUserLocation().getLongitude()));

        // Store the request on the database
        HashMap<String, Object> requestData = new HashMap<>();
        requestData.put("helperUid", helperUid);
        requestData.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        ref.child(HELPEE_UID).updateChildren(requestData);
    }

    /**
     * A function to initiate the button functionality to make request.
     */
    private void initRequestButton() {

        requestButton = (Button) findViewById(R.id.requestButton);

        this.requestButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DatabaseReference ref =
                        FirebaseDatabase.getInstance().getReference("Requests");

                updateLocationOnDatabase(ref);
                requestButton.setText("Cancel Request");

                Toast.makeText(HelpeeMapsActivity.this, "Your request has" +
                            "been cancelled", Toast.LENGTH_LONG);

                    // Update the making request flag to true and store it in the database.
                    isMakingRequest = false;
                    HashMap<String, Object> makingRequest = new HashMap<>();
                    makingRequest.put("makingRequest", isMakingRequest);
                    USER_REF.child(HELPEE_UID).updateChildren(makingRequest);

                    Log.d("Helper Uid", helperUid);

                    // Remove the helper UID.
                    USER_REF.child(HELPEE_UID).child("helperUid").removeValue();

                    // Remove the request on the helper.
                    DatabaseReference helperRef = USER_REF.child(helperUid)
                            .child("requestHelpeeID");

                    helperRef.removeValue();

                    // Set the isHelping flag on the helper to be false.
                    DatabaseReference helpersAvaiRef = USER_REF.child(helperUid).child("isHelping");
                    helpersAvaiRef.setValue(false);

                    // Delete the request from the server
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.removeLocation(HELPEE_UID);

                    // Delete the destination from the server.
                    DatabaseReference destinationRef = USER_REF.child(HELPEE_UID).child("destination");
                    destinationRef.removeValue();

                    // Back to the main activity
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        0, 0, locationListener);

                // Needed to show the user's location after the permission is granted.
                this.userLocationController.setUserLocation(
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            }
        }
    }

    /**
     * A function to check if the helper has sent a destination location to the helpee.
     */
    private void checkDestination() {

        // Get the destination information from the database.
        DatabaseReference mHelpeeDest = USER_REF.child(HELPEE_UID).child("destination");

        mHelpeeDest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    Location destinationLocation = new Location("");
                    destinationLocation.setLatitude(dataSnapshot.child("l").child("0")
                            .getValue(Double.class));
                    destinationLocation.setLongitude(dataSnapshot.child("l").child("1")
                            .getValue(Double.class));

                    TextView destinationTextView = (TextView) findViewById(R.id.destination);
                    String location = "Lat: " + destinationLocation.getLatitude() + "\n"
                            + " Lng: " + destinationLocation.getLongitude();
                    destinationTextView.setText(location);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });
    }

    /**
     * A function to check whether the helpee has made a request or not from the database.
     */
    private void getMakingRequestStatus() {

        // Check whether the user has made a request to the helper before.
        DatabaseReference mMakingReqRef = USER_REF.child(HELPEE_UID).child("makingRequest");

        mMakingReqRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    isMakingRequest = dataSnapshot.getValue(boolean.class);
                    helperUid = getIntent().getStringExtra("helper_id");

                    if (isMakingRequest) {
                        getHelperUidFromRequest();
                    }

                    // Initialize the request button.
                    requestButton = (Button) findViewById(R.id.requestButton);
                    initRequestButton();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Get the helper Uid from the database in case the helpee has made a request before.
     */
    private void getHelperUidFromRequest() {
        DatabaseReference mHelperUidRef = ROOT_REF.child(HELPEE_UID).child("helperUid");

        mHelperUidRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    helperUid = dataSnapshot.getValue(String.class);

                    Log.d("MY HELPER UID", helperUid);
                } else {
                    Log.d("MY HELPER UID", " IS NULL!!" + HELPEE_UID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkDestination();

        helperUid = getIntent().getStringExtra("helper_id");

        if (helperUid != null) {

            Log.d("MY HELPER UID", helperUid);
        } else {
            Log.d("MY HELPER UID", " IS NULL!!" + HELPEE_UID);
        }


//        getHelperUidFromRequest();



        initRequestButton();




//
//        getMakingRequestStatus();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpee_maps);

        // Initialise the Guide Note Controller.
        guideNoteController = (GuideNoteController) this.getSupportFragmentManager()
                .findFragmentById(R.id.guideNoteFragment);

        // Initialise the Location Controller.
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        userLocationController = new LocationController(geocoder);

        guideNoteController.createGuideNote("Faraday Street",
                "On Faraday Street Take Bus no. 767");
        guideNoteController.createGuideNote("University of Melbourne",
                "Get off at University of Melbourne Stop");


        // Initialise the location manager and location listener to get the the helpee's location.
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                userLocationController.setUserLocation(location);

                if (isMakingRequest) {
                    updateLocationOnDatabase(ROOT_REF.child("Requests"));
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        // CHECK THE LOCATION PERMISSION.
        // If the device running SDK < 23
        if (Build.VERSION.SDK_INT < 23) {

            startListening();

        } else {

            // Check the permission to share the location.
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {

                // Asking for permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {

                // We have permissions.
                // Update the location based on the GPS.
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        5000, 0, locationListener);


                this.userLocationController.setUserLocation(locationManager.
                        getLastKnownLocation(LocationManager.GPS_PROVIDER));

                updateLocationOnDatabase(ROOT_REF.child("Requests"));

            }
        }

    }


}
