package giorgi.dundua.helpers;

import giorgi.dundua.beertime.Authorization;
import giorgi.dundua.models.Pub;
import giorgi.dundua.models.UserInfo;
import java.util.ArrayList;
import android.content.Context;
import android.navigationdrawer.R;
import android.os.AsyncTask;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GetUserInfoTask extends AsyncTask<Void, Void, UserInfo> {
	Pub myPubs;
	static Double Id;
	static String name;
	static String address;		
	static Double lat;
	static Double longt;
	static int pubId;
	public static LatLng pubLatLng;
	public static LatLng userLatLng;
	GoogleMap map;
	Context context;
	
	public GetUserInfoTask (GoogleMap map, Context context){
		this.map = map;
		this.context = context;
	}	
	@Override
	protected UserInfo doInBackground(Void... params) {
		UserInfo ui= GetUserInfo.getPubList
				(Constants.getUserInfo+Authorization.mEmail);
		return ui;
	}
	@Override
	protected void onPostExecute(UserInfo result) {
		if (result == null){
			Toast.makeText(context, Constants.noServer, Toast.LENGTH_LONG).show();
			return;
		}
		super.onPostExecute(result);
		ArrayList<Pub> pubList = result.getPubs();
		for (int i=0; i< pubList.size(); i++){
			myPubs = pubList.get(i);
			Id = myPubs.getId();
			name = myPubs.getName();					
			address = myPubs.getAddress();
			lat = myPubs.getLat();
			longt = myPubs.getLongt();
			pubId = myPubs.getPubId();
			pubLatLng = new LatLng(lat, longt);
			MarkerOptions mOpts = new MarkerOptions().position(pubLatLng).title(name).
					icon(BitmapDescriptorFactory.
							fromResource(R.drawable.icon_beer_active));
			map.addMarker(mOpts);
		}		
	}
}
