package com.example.gavv.my_groww_project;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

import controllers.LocationController;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Button showDetailsButton;

    private LocationController userLocationController;


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
     * Set the map on the location.
     * @param title Title of the marker.
     */
    public void centerMapOnLocation(String title) {

        Location location = userLocationController.getLocation();
        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.clear();

        mMap.addMarker(new MarkerOptions().position(userLatLng).title(title));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));

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
                this.userLocationController.setLocation(
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));

                centerMapOnLocation("Your Location");

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        showDetailsButton = (Button) findViewById(R.id.showDetailsButton);

        showDetailsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, userLocationController.showDetails(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        // Create the user's location controller.
        userLocationController = new LocationController(geocoder);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {

                userLocationController.setLocation(location);
                centerMapOnLocation("Your Location");

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


                this.userLocationController.setLocation(locationManager.
                        getLastKnownLocation(LocationManager.GPS_PROVIDER));

                centerMapOnLocation(
                        "Your Location");
                if (this.userLocationController.getLocation() != null) {
                    centerMapOnLocation("Your Location");
                }
            }
        }


    }
}
