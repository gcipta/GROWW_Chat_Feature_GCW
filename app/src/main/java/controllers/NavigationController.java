package controllers;

import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.example.gavv.my_groww_project.HelperMapsActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

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

    /**
     * A function to get the direction angle to the destination.
     * @return angle in degrees
     */
    private Double findDirectionAngle(LatLng startPoint, LatLng endPoint) {

        Double diffLat = endPoint.latitude - startPoint.latitude;
        Double diffLng = endPoint.longitude - startPoint.longitude;

        if (diffLat > 0 && diffLng < 0) {

            return 360.0 - Math.toDegrees(Math.atan(Math.abs(diffLng/diffLat)));
        } else if (diffLat < 0 && diffLng < 0) {

            return 180 + Math.toDegrees(Math.atan(Math.abs(diffLng/diffLat)));
        } else if (diffLat < 0 && diffLng > 0) {

            return 90 + Math.toDegrees(Math.atan(Math.abs(diffLng/diffLat)));
        }

        return Math.toDegrees(Math.atan(diffLng/diffLat));

    }

    @Override
    public CompassDirection setCompass(LatLng startPoint, LatLng endPoint) {


        // Find the direction angle and return it
        Log.d("Angle Compass", findDirectionAngle(startPoint, endPoint).toString());
        CompassDirection compassDirection = new CompassDirection(
                Float.parseFloat(findDirectionAngle(startPoint, endPoint).toString()));

        return compassDirection;
    }

    @Override
    public void editCompassDirection(CompassDirection oldDirec, CompassDirection newDirec) {

    }

    @Override
    public List<CompassDirection> getCompassDirection(Location userLocation,
                                                      Location destinationLocation) {

        List<CompassDirection> directions = new ArrayList<>();

        // Generate the URL
        String url = getDirectionsUrl(userLocation, destinationLocation);


        try {
            // Download the routes as JSON.
            DownloadJsonApi downloadJsonApi = new DownloadJsonApi();
            JSONObject routes = downloadJsonApi.readJsonFromUrl(url);

            // There is a way to get to the destination, then it will show the arrow direction
            // on the screen.
            if (routes != null) {

                // Get the list of Latitude and Longitude of the routes.
                DataParser dataParser = new DataParser();

                List<LatLng> latLngRoutes = dataParser.parseDirections(routes.toString(),
                        DataParser.ROUTES);

                Log.d("ROUTES TO DESTINATION", latLngRoutes.toString());

                // Find the arrow direction from the user's location to the destination
                for (int i = 0; i < latLngRoutes.size() - 1; i++) {

                    directions.add(setCompass(latLngRoutes.get(i), latLngRoutes.get(i + 1)));

                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        finally {
            return directions;
        }
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

            // There is a way to get to the destination, then the map will display the routes.
            if (routes != null) {

                // Parse the routes
                DataParser dataParser = new DataParser();

                // Get the polylines and show it on the map
                List<LatLng> mPolylines = dataParser.parseDirections(routes.toString(),
                        DataParser.POLYLINES);

                Log.d("mPolylines", mPolylines.toString());

                PolylineOptions routeCoordinates = new PolylineOptions();

                for (LatLng latLng : mPolylines) {
                    routeCoordinates.add(new LatLng(latLng.latitude, latLng.longitude));
                }

                routeCoordinates.width(10).color(Color.parseColor("#1684FD"));
                direction = mMap.addPolyline(routeCoordinates);
            } else {

                return null;
            }

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
