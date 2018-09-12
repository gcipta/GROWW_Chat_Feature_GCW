package controllers;

import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import models.CompassDirection;

public interface INavigationComponent {

    /**
     * set the compass direction for a helpee headed to a specified target
     *
     * @param helpee the location of the helpee
     * @param target the target location, that the helpee headed towards
     * @return the compass direction required for the helpee
     */
    CompassDirection setCompass(Location helpee, Location target);

    /**
     * edit a compass direction
     *
     * @param oldDirec to be edited
     * @param newDirec new edited CompassDirection
     */
    void editCompassDirection(CompassDirection oldDirec, CompassDirection newDirec);

    /**
     * display the compass direction to the UI
     *
     * @param direc the compass direction to be displayed
     */
    void displayCompassDirection(CompassDirection direc);

    /**
     * display the direction from the user's location to the destination on the map.
     * @param userLocation
     * @param destinationLocation
     */
    Polyline displayDirection(Location userLocation, Location destinationLocation);


}
