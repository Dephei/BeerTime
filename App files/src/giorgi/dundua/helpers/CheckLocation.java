package giorgi.dundua.helpers;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;


public class CheckLocation {
	final static int HTTP_STATUS_OK = 200;
	private static final byte[] buff = new byte[1024];

	public static String getFile(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		String retVal = null;
		try {
			HttpResponse response = client.execute(request);
			StatusLine status = response.getStatusLine();
			int statusCode = status.getStatusCode();
			if (statusCode == HTTP_STATUS_OK) {
				HttpEntity entity = response.getEntity();
				InputStream ist = entity.getContent();
				ByteArrayOutputStream content = new ByteArrayOutputStream();
				int readCount = 0;
				while ((readCount = ist.read(buff)) != -1) {
					content.write(buff, 0, readCount);
				}
				retVal = new String(content.toByteArray());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retVal;
	}
	
	public static ArrayList<String> destination(String url) {
		String json = getFile(url);		
		if (json == null){
			return null;
		}		
		ArrayList<String> respArray = new ArrayList<String>();;
		try {
			JSONObject obj = new JSONObject(json);
			if (obj.has("message")){
				return null;
			}
			String destinationReached = obj.getString("destinationReached");
			String pointsAwarded = obj.getString("pointsAwarded");
			respArray.add(destinationReached);
			respArray.add(pointsAwarded);
//			String error = obj.getString("message");
//			if (error.equals("The user has no current goal")) {				
//				return null;
//			}				
			}	
		catch (JSONException e) {
			e.printStackTrace();
		}		
		return respArray;
	}
	

}
