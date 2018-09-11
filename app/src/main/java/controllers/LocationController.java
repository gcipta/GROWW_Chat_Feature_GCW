package controllers;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LocationController implements ILocationComponent {

    Location userLocation;
    Geocoder userGeocoder;
    Location destinationLocation;

    String address = "";

    public LocationController(Geocoder geocoder) {

        this.userGeocoder = geocoder;
    }
    @Override
    public void sendLocation(Location location, int userID) {
    }

    @Override
    public Location getUserLocation() {

        return this.userLocation;
    }

    @Override
    public void setUserLocation(Location location) {

        this.userLocation = location;
    }

    @Override
    public Location getDestinationLocation() {

        return this.destinationLocation;
    }

    @Override
    public void setDestinationLocation(Location location) {
        this.destinationLocation = location;
    }

    @Override
    public String getLocationDetails(Location location) {

        // Reset the address
        address = "";

        try {
            List<Address> listAddresses = userGeocoder.getFromLocation(location.getLatitude(),
                   location.getLongitude(), 1);

            if (listAddresses != null && listAddresses.size() > 0) {

                Log.i("PlaceInfo", listAddresses.get(0).toString());

                Address currAddress = listAddresses.get(0);

                if (currAddress.getSubThoroughfare() != null) {
                    address += currAddress.getSubThoroughfare() + " ";
                }

                if (currAddress.getThoroughfare() != null) {
                    address += currAddress.getThoroughfare() + ", ";
                }

                if (currAddress.getLocality() != null) {
                    address += currAddress.getLocality() + " ";
                }

                if (currAddress.getPostalCode() != null) {
                    address += currAddress.getPostalCode() + ", ";
                }

                if (currAddress.getCountryName() != null) {
                    address += currAddress.getCountryName() + ". ";
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    @Override
    public String getDestinationDetails() {

        if (this.destinationLocation != null) {
            return getLocationDetails(this.destinationLocation);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        address = sdf.format(new Date());

        return address;
    }

    @Override
    public String getUserLocationDetails() {

        if (this.userLocation != null) {
            return getLocationDetails(this.userLocation);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        address = sdf.format(new Date());

        return address;
    }
}
