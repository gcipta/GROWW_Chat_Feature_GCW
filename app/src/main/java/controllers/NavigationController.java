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
import java.util.List;

import json.DataParser;
import json.DownloadJsonApi;
import models.CompassDirection;

public class NavigationController implements INavigationComponent {

    private GoogleMap mMap;

    public NavigationController(GoogleMap mMap) {

        this.mMap = mMap;
    }

    /**
     * construct the URL to do a REST call.
     *
     *
     * @param userLocation
     * @param destinationLocation
     * @return url to get the JSON API.
     */
    public String getDirectionsUrl(Location userLocation, Location destinationLocation) {



        StringBuilder url = new StringBuilder("http://open.mapquestapi.com/directions/v2/" +
                "route?key=swzvFTlzalR1nkyUfc5AVrnZV7ExjCLv&from=");
        url.append(userLocation.getLatitude() + "," + userLocation.getLongitude());
        url.append("&to=" + destinationLocation.getLatitude() + "," +
                destinationLocation.getLongitude());

        Log.d("Route URL", url.toString());

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

        // Generate the URL
        String url = getDirectionsUrl(userLocation, destinationLocation);

        try {

            // Download the routes as a JSON
            DownloadJsonApi downloadJsonApi= new DownloadJsonApi();
            JSONObject routes = downloadJsonApi.readJsonFromUrl(url);

            // Parse the routes
            DataParser dataParser = new DataParser();
            List<LatLng> latLngRoutes = dataParser.parseDirections(routes.toString());

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
