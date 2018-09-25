package com.example.gavv.my_groww_project;

import android.Manifest;
import android.content.Context;
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
    private static final DatabaseReference HELPEE_REF = ROOT_REF.child("Users").child("Helpees");
    private static final DatabaseReference HELPER_REF = ROOT_REF.child("Users").child("Helpers");
    private static final String HELPEE_UID = FirebaseAuth.getInstance().getUid();

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

        Query query = ROOT_REF.child("HelpersAvailable").orderByChild("email")
                .equalTo("williamliandri@yahoo.com");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {

                    for(DataSnapshot datas: dataSnapshot.getChildren()) {
                        helperUid = datas.getKey();
                    }

                    Map<String, String> data = (Map)dataSnapshot.getValue();

                    // Remove the Helpers from the Helpers Available list
                    DatabaseReference helpersAvaiRef = ROOT_REF.child("HelpersAvailable")
                            .child(helperUid);
                    helpersAvaiRef.removeValue();

                    // Update the helper's data to indicate there is a request.
                    DatabaseReference helperRef = HELPER_REF.child(helperUid);

                    HashMap<String, Object> request = new HashMap<String, Object>();
                    request.put("requestHelpeeID", FirebaseAuth.getInstance().getCurrentUser()
                            .getUid());

                    helperRef.updateChildren(request);


                } else if (dataSnapshot == null) {
                    Log.d("Find Helper", "Data Snapshot is NULL");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * A function to update helpee's location on the database.
     */
    private void updateLocationOnDatabase(DatabaseReference ref) {

        GeoFire geoFire = new GeoFire(ref);

        geoFire.setLocation(HELPEE_UID, new GeoLocation(
                userLocationController.getUserLocation().getLatitude(),
                userLocationController.getUserLocation().getLongitude()));
    }

    /**
     * A function to initiate the button functionality to make request.
     */
    private void initRequestButton() {

        this.requestButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DatabaseReference ref =
                        FirebaseDatabase.getInstance().getReference("Requests");

                if (!isMakingRequest) {

                    updateLocationOnDatabase(ref);

                    // Update the making request flag to true and store it in the database.
                    isMakingRequest = true;
                    HashMap<String, Object> makingRequest = new HashMap<>();
                    makingRequest.put("makingRequest", isMakingRequest);
                    HELPEE_REF.child(HELPEE_UID).updateChildren(makingRequest);


                    HashMap<String, Object> details = new HashMap<String, Object>();

                    details.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    ref.child(HELPEE_UID).updateChildren(details);

                    findHelper();

                    requestButton.setText("Cancel Request");
                } else {

                    Toast.makeText(HelpeeMapsActivity.this, "Your request has" +
                            "been cancelled", Toast.LENGTH_LONG);

                    // Update the making request flag to true and store it in the database.
                    isMakingRequest = false;
                    HashMap<String, Object> makingRequest = new HashMap<>();
                    makingRequest.put("makingRequest", isMakingRequest);
                    HELPEE_REF.child(HELPEE_UID).updateChildren(makingRequest);

                    // Remove the request on the helper.
                    DatabaseReference helperRef = FirebaseDatabase.getInstance().getReference()
                            .child("Users").child("Helpers").child(helperUid)
                            .child("requestHelpeeID");

                    helperRef.removeValue();

                    // Add Helpers on the Helpers Available list
                    DatabaseReference helpersAvaiRef = ROOT_REF.child("HelpersAvailable")
                            .child(helperUid);
                    HashMap<String, Object> helperData = new HashMap<>();
                    helperData.put("email", "williamliandri@yahoo.com");
                    helpersAvaiRef.updateChildren(helperData);

                    // Delete the request from the server
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.removeLocation(HELPEE_UID);

                    requestButton.setText("Request");

                }

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

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference mHelpeeDest = HELPEE_REF.child(HELPEE_UID).child("destination");

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


        // Initialize the request button.
        requestButton = (Button) findViewById(R.id.requestButton);
        initRequestButton();

        // Initialise the location manager and location listener to get the the helpee's location.
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.d("Location", "Changed!");
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
            }
        }

    }
}
