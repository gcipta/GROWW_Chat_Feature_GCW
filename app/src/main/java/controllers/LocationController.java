package controllers;

import models.ILocationComponent;
import models.Location;

public class LocationController implements ILocationComponent {
    @Override
    public void sendLocation(Location location, int userID) {
    }

    @Override
    public Location getLocation() {
        return null;
    }
}
