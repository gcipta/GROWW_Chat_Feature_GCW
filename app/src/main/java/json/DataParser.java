package json;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataParser {

    /**
     * Retrieve the directions from a JSON file and parse it.
     * @param jsonData
     * @return
     */
    public Object[] parseDirections(String jsonData) {

        JSONArray jsonArray = null;
        JSONObject jsonObject;


        try {
            jsonObject = new JSONObject(jsonData);

            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0)
                    .getJSONArray("legs")
                    .getJSONObject(0)
                    .getJSONArray("steps");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPaths(jsonArray);
    }

    /**
     * Get the paths to the destination
     * @param maneuverJson
     * @return a list of LatLng from the source to the destination.
     */
    private Object[] getPaths(JSONArray maneuverJson) {

        int count = maneuverJson.length();
        Object objectParse[] = new Object[2];

        List<LatLng> latLngRoutes= new ArrayList<LatLng>();

        // It is used later when make a REST call to get the road details.
        StringBuilder latLngString = new StringBuilder("");

        for (int i = 0; i < maneuverJson.length(); i++) {
            try {

                // Get the Latitude and Longitude of the paths and store them in a list.
                if (i == 0) {

                    // Need to include the start point in the list as well.
                    JSONObject point = maneuverJson.getJSONObject(i)
                            .getJSONObject("start_location");

                    latLngRoutes.add(new LatLng(point.getDouble("lat"),
                            point.getDouble("lng")));

                    latLngString.append(point.getDouble("lat") + ","
                            + point.getDouble("lng"));

                }

                // Add the Longitude and Latitude into the list.
                JSONObject point = maneuverJson.getJSONObject(i).getJSONObject("end_location");
                latLngRoutes.add(new LatLng(point.getDouble("lat"),
                        point.getDouble("lng")));

                latLngString.append("|" + point.getDouble("lat") + ","
                        + point.getDouble("lng"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        objectParse[0] = latLngRoutes;
        objectParse[1] = latLngString.toString();

        return objectParse;
    }

    /**
     * Get the roads to the destination and return it as a list of LatLng.
     * @param roads
     * @return
     */
    public List<LatLng> getRoads(JSONArray roads, Location userLocation,
                                 Location destinationLocation) {

        List<LatLng> latLngRoads = new ArrayList<LatLng>();

        // Add the start point.
        latLngRoads.add(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));

        // Loop through the JSON file to get the Latitude and Longitude of the roads.
        for (int i = 0; i < roads.length(); i++) {

            try {
                JSONObject jsonLocation = roads.getJSONObject(i).getJSONObject("location");
                latLngRoads.add(new LatLng(jsonLocation.getDouble("latitude"),
                        jsonLocation.getDouble("longitude")));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Add the destination point.
        latLngRoads.add(new LatLng(destinationLocation.getLatitude(),
                destinationLocation.getLongitude()));

        return latLngRoads;
    }
}