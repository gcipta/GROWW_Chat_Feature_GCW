package json;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataParser {

    /**
     * Retrieve the directions from a JSON file and parse it.
     * @param jsonData
     * @return
     */
    public List<LatLng> parseDirections(String jsonData) {

        JSONArray jsonArray = null;
        JSONObject jsonObject;


        try {
            jsonObject = new JSONObject(jsonData);

            jsonArray = jsonObject.getJSONObject("route")
                    .getJSONArray("legs")
                    .getJSONObject(0)
                    .getJSONArray("maneuvers");

            Log.d("JSON Array Route:", jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPaths(jsonArray);
    }

    /**
     * Get the paths to the destination and store it as a list of LatLng.
     * @param maneuverJson
     * @return a list of LatLng from the source to the destination.
     */
    private List<LatLng> getPaths(JSONArray maneuverJson) {

        int count = maneuverJson.length();
        List<LatLng> latLngRoutes= new ArrayList<LatLng>();

        for (int i = 0; i < maneuverJson.length(); i++) {
            try {

                // Get the Latitude and Longitude of the paths and store them in a list.
                JSONObject point = maneuverJson.getJSONObject(i).getJSONObject("startPoint");

                latLngRoutes.add(new LatLng(point.getDouble("lat"),
                        point.getDouble("lng")));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("LatLngRoutes ArrayList", latLngRoutes.toString());
        return latLngRoutes;


    }
}