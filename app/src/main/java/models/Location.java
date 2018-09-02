package models;

public class Location {

    private Double longitude;
    private Double latitude;

    public Location(Double longitude, Double latitude){
        setLongitude(longitude);
        setLatitude(latitude);
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLatitude() {
        return latitude;
    }
}
