package com.example.gavv.my_groww_project;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import controllers.LocationController;
import controllers.NavigationController;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener{

    private GoogleMap mMap;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Button showUserLocationDetailsButton;
    private Button showDestinationLocationDetailsButton;
    private Button directionButton;

    private Button zoomInButton;
    private Button zoomOutButton;

    private LocationController userLocationController;
    private NavigationController navigationController;

    private Polyline direction;
    private boolean isGuiding = false;


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

        Location userLocation = userLocationController.getUserLocation();
        Location destinationLocation = userLocationController.getDestinationLocation();

        mMap.clear();

        if (userLocation != null) {

            LatLng userLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

            // Mark user's location on the map.
            // Set a marker on the user's location.
            mMap.addMarker(new MarkerOptions().position(userLatLng).title(title));

            // If the user has set a destination, show it on the map.
            if (destinationLocation != null) {

                LatLng destinationLatLng = new LatLng(destinationLocation.getLatitude(),
                        destinationLocation.getLongitude());

                Log.d("Destination Address: ", userLocationController.getDestinationDetails());
                mMap.addMarker(new MarkerOptions().position(destinationLatLng)
                        .title(userLocationController.getDestinationDetails())
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_BLUE)))
                        .setDraggable(true);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng,
                        mMap.getCameraPosition().zoom));
            } else  {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng,
                       15));
        }
        }
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

                centerMapOnLocation("Your Location");

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Need this to be able to download JSON from URL.
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // Show user's and destination location details.
        showUserLocationDetailsButton = (Button) findViewById(R.id.showUserDetailsButton);
        showDestinationLocationDetailsButton = (Button) findViewById(R.id
                .showDestinationDetailsButton);

        showUserLocationDetailsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this,
                        userLocationController.getUserLocationDetails(), Toast.LENGTH_LONG).show();
            }
        });

        showDestinationLocationDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String address = "";

                if (userLocationController.getDestinationLocation() != null) {
                    address = userLocationController.getDestinationDetails();
                } else {
                    address = "You have not chosen any destination yet!";
                }

                Toast.makeText(MapsActivity.this, address, Toast.LENGTH_LONG).show();
            }
        });

        // Show the direction to the destination.
        directionButton = (Button) findViewById(R.id.directionButton);

        directionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isGuiding = true;

                // Ensure the destination is not null.
                if (userLocationController.getDestinationLocation() != null) {

                    // Find the destination
                    direction = navigationController.displayDirection(
                            userLocationController.getUserLocation(),
                            userLocationController.getDestinationLocation());
                }
            }
        });

        // Zoom In and Zoom Out Button
        zoomInButton = (Button) findViewById(R.id.zoomInButton);
        zoomOutButton = (Button) findViewById(R.id.zoomOutButton);

        zoomInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mMap.moveCamera(CameraUpdateFactory.zoomIn());

            }
        });

        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.moveCamera(CameraUpdateFactory.zoomOut());
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        // Construct the user's location controller.
        userLocationController = new LocationController(geocoder);

        // Construct navigation controller.
        navigationController = new NavigationController(this.mMap);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.d("Location", "Changed!");
                userLocationController.setUserLocation(location);
                centerMapOnLocation("Your Location");

                // If the app is guiding the user, then it will update the direction.
                if (isGuiding) {
                    direction.remove();
                    direction = navigationController.displayDirection(
                            userLocationController.getUserLocation(),
                            userLocationController.getDestinationLocation());
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

                if (this.userLocationController.getUserLocation() != null) {
                    centerMapOnLocation("Your Location");
                }
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        String address = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude,
                    1);

            if(addresses != null && addresses.size() > 0) {

                if(addresses.get(0).getThoroughfare() != null) {


                    if (addresses.get(0).getSubThoroughfare() != null) {
                        address += addresses.get(0).getSubThoroughfare() + " ";
                    }

                    address += addresses.get(0).getThoroughfare();

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // If there is no address, then it will show the current date.
        if (address == "") {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            address = sdf.format(new Date());
        }

        // Update the location
        Location newDestination = new Location(LocationManager.GPS_PROVIDER);
        newDestination.setLatitude(latLng.latitude);
        newDestination.setLongitude(latLng.longitude);
        userLocationController.setDestinationLocation(newDestination);

        centerMapOnLocation("Your Location");

        Log.d("Map Long: ", "Clicked");
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

        Log.d("Marker Drag Start", "Remove Polyline");
        // Remove the previous path from the map.
        if (direction != null) {
            direction.remove();
        }

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        // Update the location
        Location newDestination = new Location(LocationManager.GPS_PROVIDER);
        newDestination.setLatitude(marker.getPosition().latitude);
        newDestination.setLongitude(marker.getPosition().longitude);
        userLocationController.setDestinationLocation(newDestination);

        Log.d("Destination Location", "To" + marker.getPosition().latitude + ", "
        + marker.getPosition().longitude);

        marker.setTitle(userLocationController.getDestinationDetails());

        // If it is still guiding, then it will give a new direction.
        if (isGuiding) {
            direction = navigationController.displayDirection(
                    userLocationController.getUserLocation(),
                    userLocationController.getDestinationLocation());
        }

    }
}
