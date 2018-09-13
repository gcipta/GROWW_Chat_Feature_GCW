package models;

import android.location.Location;

public class LocationGuideNote extends GuideNote {

    private Location location;

    public LocationGuideNote(String content, Location location){
        super(content);
        setLocation(location);
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
