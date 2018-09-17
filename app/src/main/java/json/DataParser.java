package json;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataParser {

    // An integer that is used to choose options whether routes or polylines that will be returned.
    public static final int ROUTES = 0;
    public static final int POLYLINES = 1;

    /**
     * Retrieve the directions from a JSON file and parse it.
     * @param jsonData
     * @return
     */
    public List<LatLng> parseDirections(String jsonData, Integer options) {

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

        if (options == ROUTES) {

            return getRoutes(jsonArray);
        } else if (options == POLYLINES) {

            return getPolylines(jsonArray);
        }

        return null;
    }

    /**
     * Get the paths to the destination
     * @param maneuverJson
     * @return a list of LatLng from the source to the destination.
     */
    private List<LatLng> getRoutes(JSONArray maneuverJson) {

        int count = maneuverJson.length();

        List<LatLng> latLngRoutes= new ArrayList<LatLng>();

        for (int i = 0; i < maneuverJson.length(); i++) {
            try {

                // Get the Latitude and Longitude of the paths and store them in a list.
                if (i == 0) {

                    // Need to include the start point in the list as well.
                    JSONObject point = maneuverJson.getJSONObject(i)
                            .getJSONObject("start_location");

                    latLngRoutes.add(new LatLng(point.getDouble("lat"),
                            point.getDouble("lng")));
                }

                // Add the Longitude and Latitude into the list.
                JSONObject point = maneuverJson.getJSONObject(i).getJSONObject("end_location");
                latLngRoutes.add(new LatLng(point.getDouble("lat"),
                        point.getDouble("lng")));


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return latLngRoutes;
    }


    /**
     * Get the string polylines from the JSON.
     * @param steps
     * @return
     */
    public List<LatLng> getPolylines(JSONArray steps) {

        List<LatLng> mPolylines = new ArrayList<LatLng>();

        for (int i = 0; i < steps.length(); i++) {

            JSONObject polyline;

            try {
                polyline = steps.getJSONObject(i)
                        .getJSONObject("polyline");

                mPolylines.addAll(PolyUtil.decode(polyline.getString("points")));


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d("JSONObject Polylines", mPolylines.toString());

        return mPolylines;
    }
}