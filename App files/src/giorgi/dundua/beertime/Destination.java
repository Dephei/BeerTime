package giorgi.dundua.beertime;

import giorgi.dundua.helpers.CheckLocationTask;
import giorgi.dundua.helpers.CheckUser;
import giorgi.dundua.helpers.Constants;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.navigationdrawer.R;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import com.googlecode.tesseract.android.TessBaseAPI;

public class Destination extends CustomFragment {
	///////////////////////////////////////////////////////////////
	//Concerning OCR
	public static final String PACKAGE_NAME = "giorgi.dundua.beertime";
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/BeerTimeOCR/";
	public static final String lang = "eng";
	private static final String TAG = "BeerTimedOCR.java";
	protected Button buttonOCR;
	protected EditText pubNameField;
	protected String pathOCR;
	protected boolean boolTaken;
	protected static final String PHOTO_TAKEN = "photo_taken";
	Button destination_reached;
	public static final String ARG_MENU_NUMBER = "menu_number";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.destination, container,
				false);
		/////////////////////////////////////////////
		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };
		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
					return null;
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}
		}
		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
			try {
				AssetManager assetManager = (getActivity()).getAssets();
				InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
				OutputStream out = new FileOutputStream(DATA_PATH
						+ "tessdata/" + lang + ".traineddata");
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
				
				Log.v(TAG, "Copied " + lang + " traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
			}
		}
		pubNameField = (EditText) view.findViewById(R.id.field);
		buttonOCR = (Button) view.findViewById(R.id.button);
		buttonOCR.setOnClickListener(new ButtonClickHandler());
		pathOCR = DATA_PATH + "/ocr.jpg";
		/////////////////////////////////////////////
		//Concerning OCR
		destination_reached = (Button)view.findViewById(R.id.destination_button);
		destination_reached.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (PubsFragment.name != null && PubsFragment.name.length() != 0) {
					String pubNameOCR = pubNameField.getText().toString();
					if (pubNameOCR.equals(PubsFragment.name)){
						checkUserOnServer();
					} else {
						Toast.makeText(getActivity(), "Wrong name. Destination name is: "+PubsFragment.name, Toast.LENGTH_LONG).show();
					}					
				} else {
					Toast.makeText(getActivity(), "no target", Toast.LENGTH_LONG).show();
				}
			}
		});
		return view;
	}
	public class ButtonClickHandler implements View.OnClickListener {
		public void onClick(View view) {
			Log.v(TAG, "Starting Camera app");
			startCameraActivity();
		}
	}
	protected void startCameraActivity() {
		File file = new File(pathOCR);
		Uri outputFileUri = Uri.fromFile(file);

		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		startActivityForResult(intent, 0);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i(TAG, "resultCode: " + resultCode);

		if (resultCode == -1) {
			onPhotoTaken();
		} else {
			Log.v(TAG, "User cancelled");
		}
	}
	protected void onPhotoTaken() {
		boolTaken = true;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		Bitmap bitmap = BitmapFactory.decodeFile(pathOCR, options);
		try {
			ExifInterface exif = new ExifInterface(pathOCR);
			int exifOrientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			Log.v(TAG, "Orient: " + exifOrientation);
			int rotate = 0;
			switch (exifOrientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			}
			Log.v(TAG, "Rotation: " + rotate);
			if (rotate != 0) {
				int w = bitmap.getWidth();
				int h = bitmap.getHeight();
				Matrix mtx = new Matrix();
				mtx.preRotate(rotate);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
			}
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		} catch (IOException e) {
			Log.e(TAG, "Couldn't correct orientation: " + e.toString());
		}
		Log.v(TAG, "Before baseApi");
		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.init(DATA_PATH, lang);
		baseApi.setImage(bitmap);		
		String recognizedText = baseApi.getUTF8Text();		
		baseApi.end();
		Log.v(TAG, "OCRED TEXT: " + recognizedText);
		if ( lang.equalsIgnoreCase("eng") ) {
			recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
		}		
		recognizedText = recognizedText.trim();
		if ( recognizedText.length() != 0 ) {
			pubNameField.setText(pubNameField.getText().toString().length() == 0 ? recognizedText : pubNameField.getText() + " " + recognizedText);
			pubNameField.setSelection(pubNameField.getText().toString().length());
		}
	}
	////////////////////////////////////////////////////////////////////
	protected void checkUserOnServer(){
    	PubsFragment.getUserLocation(getActivity());
    	String mEmail = PubsFragment.mEmail;
    	LatLng userLatLngt = PubsFragment.userLatLng;
    	String userLat = Double.toString(userLatLngt.latitude);
		String userLngt = Double.toString(userLatLngt.longitude);
    	new checkUserOnServerTask().execute(mEmail, userLat, userLngt);
		
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
				new CheckLocationTask(getActivity()).execute();
			} else {
				Toast.makeText(getActivity(), 
						Constants.noServer, Toast.LENGTH_LONG).show();					
			}
		}	    	
    }
}