package giorgi.dundua.beertime;

import giorgi.dundua.helpers.AbstractGetNameTask;
import giorgi.dundua.helpers.CheckUser;
import giorgi.dundua.helpers.Constants;
import giorgi.dundua.helpers.GetNameInForeground;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.navigationdrawer.R;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;

public class Authorization extends Activity {
	 private static final String TAG = "PlayAuthorization";
	    private static final String SCOPE = Constants.googleUserInfo;
	    public static final String EXTRA_ACCOUNTNAME = "extra_accountname";

	    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
	    static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;
	    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;
	    
	    Button continue_button;
	    Button choose_account_button;

	    public static String mEmail;
		private TextView tv_account_info;
		SharedPreferences prefBox;
		
		public String imageUrl;
		private ImageView iv_profilePic;
		static Bitmap profilePic;
	    
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.authorization);
	        prefBox = getSharedPreferences("BeerTimePreferance", 0);
	        mEmail = prefBox.getString("AccountName", "");
	        
	        if (mEmail != "") {
	        	getUsername();	
	        }
	                
	       setupViews();	        
	       continue_button.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View arg0) {
					checkUserOnServer ();					
				}
			});
	       choose_account_button.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				pickUserAccount();				
			}
		});
	    }
	    
	    protected void checkUserOnServer() {
	    	PubsFragment.getUserLocation(this);
	    	if (PubsFragment.userLatLng!=null){
	    		LatLng userLatLngt = PubsFragment.userLatLng;
		    	String userLat = Double.toString(userLatLngt.latitude);
				String userLngt = Double.toString(userLatLngt.longitude);;
		    	new checkUserOnServerTask().execute(mEmail, userLat, userLngt);
	    	} else {
				Toast.makeText(this, "Turn On Location Servises", Toast.LENGTH_LONG).show();
	    	} 
		}
	    
	    public class checkUserOnServerTask extends AsyncTask<String, Void, String> {
	    	String checkUserUrl;
			@Override
			protected String doInBackground(String... params) {
				checkUserUrl = CheckUser.checkUser(Constants.updateUser
							+params[0]+"&latitude="+params[1]+"&longitude="+params[2]); 
				return checkUserUrl;
			}
			@Override
			protected void onPostExecute(String result) {
				if (checkUserUrl != null) {
					continueToGame();
				} else {
					Toast.makeText(Authorization.this, 
							Constants.noServer, Toast.LENGTH_LONG).show();					
				}				
				super.onPostExecute(result);
			}	    	
	    }	    

		public void setupViews(){
	    	continue_button = (Button)findViewById(R.id.continue_button);
	    	choose_account_button = (Button)findViewById(R.id.choose_account_button);
	    	tv_account_info = (TextView)findViewById(R.id.account);
	    	iv_profilePic = (ImageView) findViewById(R.id.iv_profilePic);
		}
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
	            if (resultCode == RESULT_OK) {
	                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
	                if (mEmail != ""){
	                	prefBox.edit().putString("AccountName", mEmail).commit();
		                getUsername();
	                } else {
	                	Toast.makeText(this, Constants.noAccountText, Toast.LENGTH_SHORT).show();
	                }
	            } else if (resultCode == RESULT_CANCELED) {
	                Toast.makeText(this, Constants.noAccountText, Toast.LENGTH_SHORT).show();
	            }
	        } else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR ||
	                requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
	                && resultCode == RESULT_OK) {
	            handleAuthorizeResult(resultCode, data);
	            return;
	        }
	        super.onActivityResult(requestCode, resultCode, data);
	    }
	    
	    private void handleAuthorizeResult(int resultCode, Intent data) {
	        if (data == null) {
	            show("Unknown error, click the button again");
	            return;
	        }
	        if (resultCode == RESULT_OK) {
	            Log.i(TAG, "Retrying");
	            getTask(this, mEmail, SCOPE).execute();
	            return;
	        }
	        if (resultCode == RESULT_CANCELED) {
	            show("User rejected authorization.");
	            return;
	        }
	        show("Unknown error, click the button again");
	    }

	    /** Called by button in the layout */
	    public void continueToGame () {
	    	if (mEmail != "" && profilePic != null) {
	    		Intent intent = new Intent(Authorization.this, MainActivity.class);
		    	startActivity(intent);				
			} else {
				Toast.makeText(this, Constants.noAccountText, Toast.LENGTH_SHORT).show();					
			}	    	
	    }	    

	    /** Attempt to get the user name. If the email address isn't known yet,
	     * then call pickUserAccount() method so the user can pick an account.
	     */
	    private void getUsername() {
	        if (mEmail == null) {
	            pickUserAccount();
	        } else {
	            if (isDeviceOnline()) {
	                getTask(Authorization.this, mEmail, SCOPE).execute();
	            } else {
	                Toast.makeText(this, "No network connection available", Toast.LENGTH_SHORT).show();
	            }
	        }
	    }
	    
	    /** Starts an activity in Google Play Services so the user can pick an account */
	    private void pickUserAccount() {
	        String[] accountTypes = new String[]{"com.google"};
	        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
	                accountTypes, false, null, null, null, null);
	        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
	    }

	    /** Checks whether the device currently has a network connection */
	    private boolean isDeviceOnline() {
	        ConnectivityManager connMgr = (ConnectivityManager)
	                getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	        if (networkInfo != null && networkInfo.isConnected()) {
	            return true;
	        }
	        return false;
	    }
	    
	    /**
	     * This method is a hook for background threads and async tasks that need to update the UI.
	     * It does this by launching a runnable under the UI thread.
	     */
	    public void show(final String message) {
	        runOnUiThread(new Runnable() {
	            @Override
	            public void run() {
	                tv_account_info.setText("Hello " + message + "!");	            	
	            }
	        });
	    }
        public void setImageFromUrl(final String pImageUrl) {
	        runOnUiThread(new Runnable() {
	            @Override
	            public void run() {
	            	   	new SetProfileImageTask().execute(pImageUrl);
	            }
	        });
        }

	    /**
	     * This method is a hook for background threads and async tasks that need to provide the
	     * user a response UI when an exception occurs.
	     */
	    public void handleException(final Exception e) {
	        runOnUiThread(new Runnable() {
	            @Override
	            public void run() {
	                if (e instanceof GooglePlayServicesAvailabilityException) {
	                    // The Google Play services APK is old, disabled, or not present.
	                    // Show a dialog created by Google Play services that allows
	                    // the user to update the APK
	                    int statusCode = ((GooglePlayServicesAvailabilityException)e)
	                            .getConnectionStatusCode();
	                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
	                            Authorization.this,
	                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
	                    dialog.show();
	                } else if (e instanceof UserRecoverableAuthException) {
	                    // Unable to authenticate, such as when the user has not yet granted
	                    // the app access to the account, but the user can fix this.
	                    // Forward the user to an activity in Google Play services.
	                    Intent intent = ((UserRecoverableAuthException)e).getIntent();
	                    startActivityForResult(intent,
	                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
	                }
	            }
	        });
	    }

	    private AbstractGetNameTask getTask(
	            Authorization activity, String email, String scope) {
	                return new GetNameInForeground(activity, email, scope);   
	    }

	    private Bitmap getImageBitmap(String url) {
	        Bitmap bm = null;
	        try {
	            URL aURL = new URL(url);
	            URLConnection conn = aURL.openConnection();
	            conn.connect();
	            InputStream is = conn.getInputStream();
	            BufferedInputStream bis = new BufferedInputStream(is);
	            bm = BitmapFactory.decodeStream(bis);
	            bis.close();
	            is.close();
	       } catch (IOException e) {
	           Log.e(TAG, "Error getting bitmap", e);
	       }
	       return bm;
	    } 
	    
	    public class SetProfileImageTask extends AsyncTask<String, Void, Bitmap>{
			@Override
			protected Bitmap doInBackground(String... params) {
				profilePic = getImageBitmap(params[0]);
				
				return profilePic;
			}
			@Override
			protected void onPostExecute(Bitmap result) {				
				super.onPostExecute(result);
				iv_profilePic.setImageBitmap(result);
			}
	    }
}
