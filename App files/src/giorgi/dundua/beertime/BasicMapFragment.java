package giorgi.dundua.beertime;

import giorgi.dundua.helpers.Constants;
import giorgi.dundua.helpers.GetUserInfoTask;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.navigationdrawer.R;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class BasicMapFragment extends Fragment implements LocationListener, RoutingListener{
	/**
	 * Note that this may be null if the Google Play services APK is not
	 * available.
	 */
	private GoogleMap mMap;
	static Marker currPubMarker;
	static Marker currUserMarker;
	private String provider;
	LocationManager locMan;
	static Routing routing;
    Polyline polyline;
    Handler userMarkerUpdaterHandler;
    Runnable userMarkerUpdaterRunnable;
    Handler routingRouteUpdaterHandler;
    Runnable routingRouteUpdaterRunnable;
    public static Bitmap bmp;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.map, container, false); 
		setUpMapIfNeeded();	
		addPubMarkers();
		routingRouteUpdater();
		return rootView;
	}
	
	@Override
	public void onDestroyView() {
	    SupportMapFragment f = (SupportMapFragment) getFragmentManager()
	            .findFragmentById(R.id.map);
	    if (f != null) {
	        try {
	            getFragmentManager().beginTransaction().remove(f).commit();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    super.onDestroyView();
	}

	@Override
	public void onResume() {
		super.onResume();
		locMan.requestLocationUpdates(provider, 400, 1, this);
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}
	}
	protected void addPubMarkers(){
    	new GetUserInfoTask(mMap, getActivity()).execute();
    }   

	private void setUpMap() {
		mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		mMap.setMyLocationEnabled(true);
		mMap.setIndoorEnabled(true);
		mMap.setBuildingsEnabled(true);
		/////////////////////////////////////////
		locMan = (LocationManager) getActivity()
				.getSystemService(getActivity().LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		provider = locMan.getBestProvider(criteria, true);
		Location myCurrLocation = locMan.getLastKnownLocation(provider);		
		/////////////////////////////////////////
		//location problem dialog section
		if (myCurrLocation == null){
			final Dialog dialog = new Dialog(getActivity());
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.dialog_location_problem);
			Button b_close_dialog = (Button)dialog.findViewById(R.id.b_close_dialog);
			b_close_dialog.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}		
		// end dialog section 		
		/////////////////////////////////
		if (myCurrLocation == null ){
			locMan.requestLocationUpdates(provider, 400, 1, this);
			myCurrLocation = locMan.getLastKnownLocation(provider);
		}
		/////////////////////////////////
		int zoomMap = 15;
		mMap.moveCamera(CameraUpdateFactory.newLatLng(PubsFragment.userLatLng));
		mMap.animateCamera(CameraUpdateFactory.zoomTo(zoomMap));
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		bmp = Bitmap.createBitmap(100, 100, conf);
		Canvas canvasUser = new Canvas(bmp);
		if (PubsFragment.name != null && PubsFragment.name.length() != 0) {
			currPubMarker = mMap.addMarker(new MarkerOptions().position(PubsFragment.pubLatLng)
					.title(PubsFragment.name+Constants.currentPubMarkerTitle)
					.icon(BitmapDescriptorFactory.
							fromResource(R.drawable.icon_beer_active)));
			mMap.moveCamera(CameraUpdateFactory.newLatLng(PubsFragment.pubLatLng));
			mMap.animateCamera(CameraUpdateFactory.zoomTo(zoomMap));
			///////////////////
			routing = new Routing(Routing.TravelMode.WALKING); ///////////
			routing.registerListener(this);
	        routing.execute(PubsFragment.userLatLng, PubsFragment.pubLatLng);
	        
		}			

		// paint defines the text color, stroke width, size
		Paint color = new Paint();
		color.setTextSize(35);
		color.setColor(Color.BLACK);

		// modify canvas
		canvasUser.drawBitmap(Bitmap.createScaledBitmap(Authorization.profilePic, 100, 100, false), 0, 0, color);
		currUserMarker = mMap.addMarker(new MarkerOptions().position(PubsFragment.userLatLng)
				.title(Constants.currentUserMarkerTitle)
				.icon(BitmapDescriptorFactory.fromBitmap(bmp))
				.anchor(0.5f, 1));
		runUserMarkerUpdater();
	}
	public static Bitmap getBitmapFromURL(String src) {
	    try {
	        URL url = new URL(src);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
	        return myBitmap;
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	} 
	
	@Override
	public void onRoutingSuccess(PolylineOptions mPolyOptions) {
		PolylineOptions polyoptions = new PolylineOptions();
	      polyoptions.color(Color.BLUE);
	      polyoptions.width(10);
	      polyoptions.addAll(mPolyOptions.getPoints());
	      polyline = mMap.addPolyline(polyoptions);
		
	}
	@Override
	public void onPause() {
		super.onPause();
		userMarkerUpdaterHandler.removeCallbacks(userMarkerUpdaterRunnable);
		routingRouteUpdaterHandler.removeCallbacks(routingRouteUpdaterRunnable);
	}
	public void runUserMarkerUpdater(){
		final int interval = 1000;
		userMarkerUpdaterHandler = new Handler();
	    userMarkerUpdaterRunnable = new Runnable() {			
			@Override
			public void run() {
				currUserMarker.remove();
				PubsFragment.getUserLocation(getActivity());
				currUserMarker = mMap.addMarker(new MarkerOptions().position(PubsFragment.userLatLng)
						.title(Constants.currentUserMarkerTitle)
						.icon(BitmapDescriptorFactory.fromBitmap(bmp))
						.anchor(0.5f, 1));	
				userMarkerUpdaterHandler.postDelayed(userMarkerUpdaterRunnable, interval);
			}
		};
		userMarkerUpdaterHandler.postDelayed(userMarkerUpdaterRunnable, interval);
	}
	
	public void routingRouteUpdater(){
		final int interval = 30000;
		routingRouteUpdaterHandler = new Handler();
		routingRouteUpdaterRunnable = new Runnable() {			
			@Override
			public void run() {
				if (PubsFragment.name != null && PubsFragment.name.length() != 0) {
					routing.cancel(true);
					routing = new Routing(Routing.TravelMode.WALKING);
					routing.registerListener(BasicMapFragment.this);
					routing.execute(PubsFragment.userLatLng, PubsFragment.pubLatLng);
					polyline.remove();
					PolylineOptions polyoptions = new PolylineOptions();
					polyoptions.color(Color.BLUE);
				    polyoptions.width(10);
				    polyoptions.addAll(polyoptions.getPoints());
				    polyline = mMap.addPolyline(polyoptions);
					routingRouteUpdaterHandler.postDelayed(routingRouteUpdaterRunnable, interval);
				}				
			}
		};
		routingRouteUpdaterHandler.postDelayed(routingRouteUpdaterRunnable, interval);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}	
	@Override
	public void onRoutingFailure() {
		// TODO Auto-generated method stub		
	}
	@Override
	public void onRoutingStart() {
		// TODO Auto-generated method stub		
	}
}
