package controllers;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class LocationController implements ILocationComponent {

    Location userLocation;
    Geocoder userGeocoder;

    public LocationController(Geocoder geocoder) {

        this.userGeocoder = geocoder;
    }
    @Override
    public void sendLocation(Location location, int userID) {
    }

    @Override
    public Location getLocation() {

        return this.userLocation;
    }

    @Override
    public void setLocation(Location location) {

        this.userLocation = location;
    }

    @Override
    public String showDetails() {

        String address = "";

        try {
            List<Address> listAddresses = userGeocoder.getFromLocation(userLocation.getLatitude(),
                   userLocation.getLongitude(), 1);

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
}
