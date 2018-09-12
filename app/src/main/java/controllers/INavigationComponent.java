package controllers;

import models.CompassDirection;
import models.Location;

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
     * construct the URL to do a REST call.
     *
     *
     * @param userLocation
     * @param destinationLocation
     * @return url to get the JSON API.
     */
    String getDirectionsUrl(Location userLocation, Location destinationLocation);

}
