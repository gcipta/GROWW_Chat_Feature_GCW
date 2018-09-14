package controllers;

import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import json.DataParser;
import json.DownloadJsonApi;
import models.CompassDirection;

public class NavigationController implements INavigationComponent {

    private GoogleMap mMap;
    private StringBuilder googleURL;

    public NavigationController(GoogleMap mMap) {

        this.mMap = mMap;
    }

    /**
     * construct the URL to do a REST call to get information about the direction.
     *
     *
     * @param userLocation
     * @param destinationLocation
     * @return url to get the JSON API.
     */
    public String getDirectionsUrl(Location userLocation, Location destinationLocation) {

        StringBuilder url =
                new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        url.append("origin=" + userLocation.getLatitude() + "," + userLocation.getLongitude());
        url.append("&destination=" + destinationLocation.getLatitude() + ","
                + destinationLocation.getLongitude());
        url.append("&key=AIzaSyCkGt3adHi7ynDGF84HLS-ZNSCY8odXhpQ");

        Log.d("Route URL", url.toString());

        return url.toString();
    }

    public String getRoadsUrl(String latLngString) {

        StringBuilder url =
                new StringBuilder("https://roads.googleapis.com/v1/snapToRoads?path=");
        url.append(latLngString);
        url.append("&interpolate=true&key=AIzaSyCkGt3adHi7ynDGF84HLS-ZNSCY8odXhpQ");

        return url.toString();
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
    public Polyline displayDirection(Location userLocation, Location destinationLocation) {

        Polyline direction = null;
        Object objectParse[] = new Object[2];

        // Generate the URL
        String url = getDirectionsUrl(userLocation, destinationLocation);

        try {

            // Download the routes as a JSON.
            DownloadJsonApi downloadJsonApi= new DownloadJsonApi();
            JSONObject routes = downloadJsonApi.readJsonFromUrl(url);

            // Parse the routes
            DataParser dataParser = new DataParser();

            Object objectParsed[] = dataParser.parseDirections(routes.toString());

            // Get the routes.
            List<LatLng> latLngRoutes = (ArrayList<LatLng>) objectParsed[0];

            Log.d("Lat Lng Routes", latLngRoutes.toString());

            // Get the roads that the routes pass through.
            String latLngString = (String) objectParsed[1];

            // Download the roads as a JSON.
            JSONArray roads = downloadJsonApi.readJsonFromUrl(getRoadsUrl(latLngString))
                    .getJSONArray("snappedPoints");

            Log.d("Roads URL", getRoadsUrl(latLngString));
            Log.d("JSONArray Roads", roads.toString());

            // Get the roads as a list of LatLng.
            List<LatLng> latLngRoads = dataParser.getRoads(roads, userLocation,
                    destinationLocation);

            direction = mMap.addPolyline(new PolylineOptions()
                    .color(Color.parseColor("#1684FD"))
                    .width(10)
                    .addAll(latLngRoutes));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        finally {
            return direction;
        }

    }
}
