package controllers;

import com.google.android.gms.maps.GoogleMap;

import models.CompassDirection;
import models.Location;

public class NavigationController implements INavigationComponent {

    private GoogleMap mMap;

    public NavigationController(GoogleMap mMap) {

        this.mMap = mMap;
    }

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

    @Override
    public String getDirectionsUrl(Location userLocation, Location destinationLocation) {
        StringBuilder url = new StringBuilder("https://route.api.here.com/routing/7.2/" +
                "calculateroute.json?app_id=vasjqUzfKhzOc7gUXhlq&app_code=QFDsWEGLzcT29CPk3HT5Gg");

        url.append("&waypoint0=geo!" + userLocation.getLatitude() + "," +
                userLocation.getLongitude());
        url.append("&waypoint1=geo!" + destinationLocation.getLatitude() + "," +
                destinationLocation.getLongitude());
        url.append("&departure=now&mode=fastest;");
        url.append("pedestrian;traffic:disabled");

        return url.toString();
    }
}
