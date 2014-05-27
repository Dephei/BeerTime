package giorgi.dundua.helpers;

import java.util.ArrayList;
import android.os.AsyncTask;
import android.widget.TextView;
import com.google.android.gms.maps.model.LatLng;

public class GoogleDirectionsTask extends AsyncTask<Void , Void, ArrayList<String>> {	
	TextView tv_distance;
	TextView tv_duration;
	LatLng srcLatLng;
	LatLng dstLatLng;
	public GoogleDirectionsTask
	(TextView tv_distance, TextView tv_duration, LatLng srcLatLng, LatLng dstLatLng){
		this.srcLatLng = srcLatLng;
		this.dstLatLng = dstLatLng;
		this.tv_distance = tv_distance;
		this.tv_duration = tv_duration;
	}

	@Override
	protected ArrayList<String> doInBackground(Void... params) {
		ArrayList<String> result = GoogleDirections.getDirections(srcLatLng, dstLatLng);   
			return result;
	}

	@Override
	protected void onPostExecute(ArrayList<String> result) {
		if (result == null){
			tv_distance.setText(Constants.noServer);
			return;
		}
		super.onPostExecute(result);	
		tv_distance.setText("Distance to destination: " +result.get(0));
		tv_duration.setText("Time to destination: " +result.get(1));
	}
} 