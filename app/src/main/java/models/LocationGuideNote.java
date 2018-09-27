package models;

import android.location.Location;

public class LocationGuideNote {

    private Location location;

    public LocationGuideNote(String content, Location location){
        setLocation(location);
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
