package giorgi.dundua.helpers;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class GoogleDirections {
	public static String transportType;
	public static ArrayList<String> getDirections(LatLng source, LatLng destination) {
		  String strLat = Double.toString(source.latitude);
		  String strLng = Double.toString(source.longitude);
		  String destLat = Double.toString(destination.latitude);
		  String destLong = Double.toString(destination.longitude);
		  StringBuilder urlString = new StringBuilder();
		  urlString
		  .append("http://maps.googleapis.com/maps/api/directions/json?");
		  urlString.append("origin=");// from
		  urlString.append(strLat);
		  urlString.append(",");
		  urlString.append(strLng);
		  urlString.append("&destination=");// to
		  urlString.append(destLat);
		  urlString.append(",");
		  urlString.append(destLong);
		  urlString.append("&mode="+transportType);
		  String url = urlString.toString();		 
		  String json = JsonParserCurrentPub.getFile(url);
		  if (json == null){
				return null;
			}
		  ArrayList<String> directions = new ArrayList<String>();
		  
		  try {
		   JSONObject routes = new JSONObject(json);
		   JSONObject obj = (JSONObject)routes.getJSONArray("routes").get(0);
		   JSONObject obj1 = (JSONObject)obj.getJSONArray("legs").get(0);
		   String distance = obj1.getJSONObject("distance").getString("text");
		   String duration = obj1.getJSONObject("duration").getString("text");
		   directions.add(distance);
		   directions.add(duration);
		   return directions;
		   
		  } catch (JSONException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  }

		  return directions;
		 }
}
