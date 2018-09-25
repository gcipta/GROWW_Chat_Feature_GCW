package com.example.gavv.my_groww_project;

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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import adapter.PlaceAutocompleteAdapter;
import controllers.LocationController;
import controllers.NavigationController;

public class HelperMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerDragListener,
        GoogleApiClient.OnConnectionFailedListener{

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

    private AutoCompleteTextView inputSearch;
    private PlaceAutocompleteAdapter mPlaceAutoCompleteAdapter;
    private GoogleApiClient mGoogleApiClient;

    private LatLngBounds latLngBounds;

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();


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
            } else {
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

    private void startDirection() {

        isGuiding = true;

        // Ensure the destination is not null.
        if (userLocationController.getDestinationLocation() != null) {

            // Find the destination
            direction = navigationController.displayDirection(
                    userLocationController.getUserLocation(),
                    userLocationController.getDestinationLocation());

            // If it is impossible to get to the destination, display an error message.
            if (direction == null) {

                Toast.makeText(HelperMapsActivity.this,
                        "The destination is not achieveable. " +
                        "Try different transportation mode or pick another destination point.",
                        Toast.LENGTH_LONG).show();
            }

        }
    }

    // BUTTON FUNCTIONALITY

    /**
     * Set a button to show user's location details.
     */
    private void initUserLocationDetailsButton() {

        // Show user's location details.
        showUserLocationDetailsButton = (Button) findViewById(R.id.showUserDetailsButton);

        showUserLocationDetailsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(HelperMapsActivity.this,
                        userLocationController.getUserLocationDetails(), Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Set a button to show destination details.
     */
    private void initDestinationLocationDetailsButton() {

        // Show destination location details.
        showDestinationLocationDetailsButton = (Button) findViewById(R.id
                .showDestinationDetailsButton);

        showDestinationLocationDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String address = "";

                if (userLocationController.getDestinationLocation() != null) {
                    address = userLocationController.getDestinationDetails();
                } else {
                    address = "You have not chosen any destination yet!";
                }

                Toast.makeText(HelperMapsActivity.this, address, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Set a button to show a direction from user's location to the chosen destination.
     */
    private void initDirectionButton() {

        // Show the direction to the destination.
        directionButton = (Button) findViewById(R.id.directionButton);

        directionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userLocationController.getDestinationLocation() != null) {
                    startDirection();
                } else {
                    Toast.makeText(HelperMapsActivity.this,
                            "You have not chosen any destination yet!",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    /**
     * Set a button to do zoom in and out on the map.
     */
    private void initZoomButton() {

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
     * Search bar functionality
     */
    private void initSearchBar() {

        // Initialize a Google Api Client.
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        LatLng userLatLng = new LatLng(userLocationController.getUserLocation().getLatitude(),
                userLocationController.getUserLocation().getLongitude());

        LatLng boundLatLng = new LatLng(
                userLocationController.getUserLocation().getLatitude() + 5,
                userLocationController.getUserLocation().getLongitude() + 5);

        latLngBounds = new LatLngBounds(userLatLng, boundLatLng);

         // Initialise the Autocomplete Adapter
        mPlaceAutoCompleteAdapter = new PlaceAutocompleteAdapter(this,
                Places.getGeoDataClient(this),
                latLngBounds, null);

        inputSearch.setAdapter(mPlaceAutoCompleteAdapter);

        // Set a hint on the search bar.
        inputSearch.setHint("Enter Address, City or ZIP Code");
        inputSearch.setHintTextColor(Color.LTGRAY);

        // When the user click enters, it will direct to the destination.
        inputSearch.setFocusableInTouchMode(true);
        inputSearch.requestFocus();

        inputSearch.setOnItemClickListener(mAutocompleteClickListener);

    }

    /**
     * Showing the location details when the user clicks on the list.
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item = mPlaceAutoCompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResults =
                    Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);

            placeResults.setResultCallback(mUpdatePlaceDetailsCallback);

        }
    };

    /**
     * A callback function to get information about the location.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {

            if (!places.getStatus().isSuccess()) {
                Log.d("Place Query", "Place query did not complete.");

                // Need to release the buffer to avoid memory leak.
                places.release();
                return;
            }

            final Place place = places.get(0);

            Log.d("Place details:",  "LatLng: " + place.getLatLng());
            Log.d("Place details: ", "Address" + place.getAddress());

            // Update the destination and show it.
            Location newDestination = new Location("");
            newDestination.setLatitude(place.getLatLng().latitude);
            newDestination.setLongitude(place.getLatLng().longitude);

            userLocationController.setDestinationLocation(newDestination);

            centerMapOnLocation("Your Location");

            // Start to show the direction.
            startDirection();

            places.release();


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper_maps);

        inputSearch = (AutoCompleteTextView) findViewById(R.id.input_search);

        // Need this to be able to download JSON from URL.
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // Set on-click-listener function on the button.
        initUserLocationDetailsButton();
        initDestinationLocationDetailsButton();
        initDirectionButton();
        initZoomButton();

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

        initSearchBar();

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
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

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

        // Clean the input text.
        if (inputSearch.getText() != null) {
            inputSearch.setText("");
        }

        Log.d("Destination Location", "To" + marker.getPosition().latitude + ", "
        + marker.getPosition().longitude);

        marker.setTitle(userLocationController.getDestinationDetails());

        // If it is still guiding, then it will give a new direction.
        if (isGuiding) {

            startDirection();

        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
