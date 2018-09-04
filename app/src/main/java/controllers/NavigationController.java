package controllers;

import models.CompassDirection;
import models.Location;

public class NavigationController implements INavigationComponent {


    @Override
    public CompassDirection setCompass(Location helpee, Location target) {
        return null;
    }

    @Override
    public void editCompassDirection(CompassDirection oldDirec, CompassDirection newDirec) {

    }

    @Override
    public void displayCompassDirection(CompassDirection direc) {

    }
}
