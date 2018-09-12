package controllers;

import android.location.Geocoder;
import android.location.Location;

public interface ILocationComponent {

    /**
     * send a location to a helpee
     * @param location  location to be sent to helpee
     * @param userID    user id of the helpee that the Location is sent to
     */
    void sendLocation(Location location, int userID);

    /**
     * get the location from a phone
     * @return  return the location
     */
    Location getUserLocation();

    /**
     * set the location from a phone
     * @param location
     */
    void setUserLocation(Location location);

    /**
     * set the destination location
     * @return
     */
    Location getDestinationLocation();

    /**
     * get the destination location
     * @param location
     */
    void setDestinationLocation(Location location);

    /**
     * Return the address details.
     * @return address
     */
    String getLocationDetails(Location location);

    /**
     * Return the user's address details.
     * @return destinations' address
     */

    String getDestinationDetails();

    /**
     * Return the address details.
     * @return address
     */
    String getUserLocationDetails();
}
