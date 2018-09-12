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
            jsonArray = jsonObject.getJSONObject("response")
                    .getJSONArray("route")
                    .getJSONObject(0)
                    .getJSONArray("leg")
                    .getJSONObject(0)
                    .getJSONArray("maneuver");

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
                JSONObject point = maneuverJson.getJSONObject(i).getJSONObject("position");

                latLngRoutes.add(new LatLng(point.getDouble("latitude"),
                        point.getDouble("longitude")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("LatLngRoutes ArrayList", latLngRoutes.toString());
        return latLngRoutes;


    }
}