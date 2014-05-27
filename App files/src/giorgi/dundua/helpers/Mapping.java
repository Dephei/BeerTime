package giorgi.dundua.helpers;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.navigationdrawer.R;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

public class Mapping{
	
	public static LatLng myCurrentLocation(Context context, LocationManager locMan){
		//Criteria criteria = new Criteria();
		String provider = LocationManager.NETWORK_PROVIDER;
		Location myCurrLocation = locMan.getLastKnownLocation(provider);
		
		boolean network_enabled = locMan
				.isProviderEnabled(provider);
		if (!network_enabled) {
			return null;
		}		
		//dialog section
		if (myCurrLocation == null){
			final Dialog dialog = new Dialog(context);
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
		
		if (myCurrLocation == null ){
			locMan.requestLocationUpdates(provider, 400, 1, new LocationListener(){
				@Override
				public void onLocationChanged(Location location) {
					// TODO Auto-generated method stub					
				}
				@Override
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
					// TODO Auto-generated method stub					
				}
				@Override
				public void onProviderEnabled(String provider) {
					// TODO Auto-generated method stub					
				}
				@Override
				public void onProviderDisabled(String provider) {
					// TODO Auto-generated method stub					
				}
				
			});
			myCurrLocation = locMan.getLastKnownLocation(provider);
		}
		
		double lat = myCurrLocation.getLatitude();
		double longt = myCurrLocation.getLongitude();

		
		return new LatLng(lat, longt);
	}
}
