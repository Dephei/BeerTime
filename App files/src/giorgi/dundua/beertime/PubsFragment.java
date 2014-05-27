package giorgi.dundua.beertime;

import giorgi.dundua.helpers.Constants;
import giorgi.dundua.helpers.GoogleDirections;
import giorgi.dundua.helpers.GoogleDirectionsTask;
import giorgi.dundua.helpers.JsonParserCurrentPub;
import giorgi.dundua.helpers.Mapping;
import giorgi.dundua.models.Pub;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.navigationdrawer.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class PubsFragment extends CustomFragment {
		public static final String ARG_MENU_NUMBER = "menu_number";
		Button pubs_button;
		Button pub_on_map;
		private TextView tv_pub_name;
		private TextView tv_pub_address;
		private static TextView tv_pub_distance;
		private static TextView tv_pub_duration;
		RadioGroup transportModeGroup;
		RadioButton transportModeButton;
		public static String transportMode;
		RadioButton walking;
		RadioButton biking;		
		View view;				
		Pub myPub;
		public static String mEmail;
		static Double pubId;
		public static String name;
		static String address;		
		static Double lat;
		static Double longt;
		static String distanceToTarget;
		static String durationToTarget;
		public static LatLng pubLatLng;
		public static LatLng userLatLng;
		static SharedPreferences prefBox;
        Handler userLocationUpdaterHandler;
        Runnable userLocationUpdaterRunnable; 
        Handler googleDirectionsHandler;
        Runnable googleDirectionsRunable;
		AlertDialog.Builder builder;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			prefBox = getActivity().getSharedPreferences("BeerTimePreferance", 0);
			mEmail = prefBox.getString("UserEmail", "");
			if (mEmail.equals(Authorization.mEmail)){
				pubId = Double.parseDouble(prefBox.getString("PubID", "0"));
				name = prefBox.getString("PubName", "");
				address = prefBox.getString("PubAddress", "");
				lat = Double.parseDouble(prefBox.getString("PubLat", "0"));
				longt = Double.parseDouble(prefBox.getString("PubLongt", "0"));
				distanceToTarget = prefBox.getString("PubDistance", "");
				durationToTarget = prefBox.getString("PubDuration", "");
				mEmail = Authorization.mEmail;
				prefBox.edit().putString("UserEmail", mEmail).commit();
			} else {
				clearCurrentTarget();
				mEmail = Authorization.mEmail;
				prefBox.edit().putString("UserEmail", mEmail).commit();
			}
			transportMode = "walking";
			GoogleDirections.transportType = transportMode;
			view = inflater.inflate(R.layout.pubs, container,
					false);			
			setupViews();
			getUserLocation(getActivity());
			runUserLocationUpdater();
			runGoogleDirectionsChecker();
			getCurrentPubLocation();
			
			transportModeGroup = (RadioGroup) view.findViewById(R.id.transportType);			
			walking.setOnClickListener(new OnClickListener() {					
				@Override
				public void onClick(View v) {
					if (name != null && name.length() != 0){
						transportMode = (String) walking.getText();
						GoogleDirections.transportType = transportMode;
						new GoogleDirectionsTask
						(tv_pub_distance , tv_pub_duration, userLatLng, pubLatLng)
						.execute();
					}					
				}
			});
			biking.setOnClickListener(new OnClickListener() {					
				@Override
				public void onClick(View v) {
					if (name != null && name.length() != 0){
						transportMode = (String) biking.getText();
						GoogleDirections.transportType = transportMode;
						new GoogleDirectionsTask
						(tv_pub_distance , tv_pub_duration, userLatLng, pubLatLng)
						.execute();	
					}					
				}
			});
			
			pubs_button.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View view) {
					builder = new AlertDialog.Builder(getActivity());
					builder.setMessage(Constants.newPubWarningDialog)
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						clearCurrentTarget();
						showPub();
					}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
					}
					});
					final AlertDialog alert = builder.create();
					alert.show();					
				}				
			});
			pub_on_map.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					if (name != null && name.length() != 0) {						
						getUserLocation(getActivity());
						Fragment mapFragment= new BasicMapFragment();
						FragmentManager fragmentManager = getFragmentManager();
						FragmentTransaction transaction = fragmentManager.beginTransaction();
						Fragment f = getFragmentManager()
							     .findFragmentById(R.id.map);
							   if (f !=null) {
							    transaction.remove(f);
							   }
						transaction
								.replace(R.id.content_frame, mapFragment).commit();
					
					((ListView)getActivity().findViewById(R.id.left_drawer))
					.setItemChecked(0, true);
					}								
				}
			});
			return view;
		} 
		
		@Override
		public void onPause() {
			super.onPause();
			userLocationUpdaterHandler.removeCallbacks(userLocationUpdaterRunnable);
			googleDirectionsHandler.removeCallbacks(googleDirectionsRunable);			
		}
		public void runUserLocationUpdater(){
			final int interval = 1000;
			userLocationUpdaterHandler = new Handler();
			userLocationUpdaterRunnable = new Runnable() {				
				@Override
				public void run() {
					getUserLocation(getActivity());
					userLocationUpdaterHandler.postDelayed(userLocationUpdaterRunnable, interval);
				}
			};
			userLocationUpdaterHandler.postDelayed(userLocationUpdaterRunnable, interval);
		}
		public void runGoogleDirectionsChecker(){
			final int interval = 30000;
			googleDirectionsHandler = new Handler();
			googleDirectionsRunable = new Runnable() {				
				@Override
				public void run() {
					if (name != null && name.length() != 0) {
						new GoogleDirectionsTask
						(tv_pub_distance , tv_pub_duration, userLatLng, pubLatLng)
						.execute();
						prefBox.edit().
						putString("PubDistance", (String) tv_pub_distance.getText()).commit();
						prefBox.edit().
						putString("PubDuration", (String) tv_pub_duration.getText()).commit();						
					}
					googleDirectionsHandler.postDelayed(googleDirectionsRunable, interval);
				}
			};
			googleDirectionsHandler.postDelayed(googleDirectionsRunable, interval);
		}		
		
		public static void clearCurrentTarget() {
			pubId = 0.0;
			name = "";					
			address = "";
			lat = 0.0;
			longt = 0.0;
			pubLatLng = null;
			distanceToTarget = "";
			durationToTarget = "";
			prefBox.edit().putString("PubId", longt.toString()).commit();
			prefBox.edit().putString("PubName", name).commit();
			prefBox.edit().putString("PubAddress", address).commit();
			prefBox.edit().putString("PubLat", lat.toString()).commit();
			prefBox.edit().putString("PubLongt", longt.toString()).commit();
			prefBox.edit().putString("PubDistance", distanceToTarget).commit();
			prefBox.edit().putString("PubDuration", durationToTarget).commit();
			if (BasicMapFragment.currPubMarker != null) {
				BasicMapFragment.currPubMarker.remove();
			}				
		}		

		private void setupViews() {
			pubs_button = (Button) view.findViewById(R.id.pubs_button);
			pub_on_map = (Button)view.findViewById(R.id.pub_on_map);
			walking = (RadioButton)view.findViewById(R.id.walking);
			biking = (RadioButton)view.findViewById(R.id.biking);
			tv_pub_name = (TextView)view.findViewById(R.id.pub_name);
			tv_pub_distance = (TextView)view.findViewById(R.id.pub_distance);
			tv_pub_duration = (TextView)view.findViewById(R.id.pub_duration);
			if (name != "") {
				tv_pub_name.setText(name);				
			} 
			tv_pub_address = (TextView)view.findViewById(R.id.pub_address);
			if (address !="") {
				tv_pub_address.setText(address);
			}
			if (name != "") {
				tv_pub_distance.setText(distanceToTarget);				
			} 
			if (name != "") {
				tv_pub_duration.setText(durationToTarget);				
			} 
		}
		public static void getCurrentPubLocation (){
			if (name != null && name.length() != 0) {
				pubLatLng = new LatLng(lat, longt);
			}		
		}
		public static void getUserLocation (Activity activity) {
			LocationManager locMan = (LocationManager) activity
    				.getSystemService(activity.LOCATION_SERVICE);					        
	        userLatLng = Mapping.myCurrentLocation(activity, locMan);
		}
		
		private void showPub() {
			new PubsTask().execute();			
		}			
		
		public class PubsTask extends AsyncTask<Void, Void , ArrayList<Pub>>{
			@Override
			protected ArrayList<Pub> doInBackground(Void... params) {
				ArrayList<Pub> pubs = JsonParserCurrentPub
						.getPubList(Constants.getPubList+Authorization.mEmail);
				return pubs;			
				}

			@Override
			protected void onPostExecute(ArrayList<Pub> result) {
				if (result == null){	
					Toast.makeText(getActivity(), "No More Destinations", 
							Toast.LENGTH_LONG).show();
					return;
				}
				super.onPostExecute(result);
				myPub = result.get(0);
				pubId = myPub.getId();
				name = myPub.getName();					
				address = myPub.getAddress();
				lat = myPub.getLat();
				longt = myPub.getLongt();
				getCurrentPubLocation();
				prefBox.edit().putString("PubID", pubId.toString()).commit();
				prefBox.edit().putString("PubName", name).commit();
				prefBox.edit().putString("PubAddress", address).commit();
				prefBox.edit().putString("PubLat", lat.toString()).commit();
				prefBox.edit().putString("PubLongt", longt.toString()).commit();						
				tv_pub_name.setText(name);
				tv_pub_address.setText(address);
				new GoogleDirectionsTask
				(tv_pub_distance , tv_pub_duration, userLatLng, pubLatLng)
				.execute();				
			}
		}		
	}	