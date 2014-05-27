package giorgi.dundua.helpers;

import giorgi.dundua.models.Pub;
import giorgi.dundua.models.UserInfo;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GetUserInfo {
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
	
	public static UserInfo getPubList(String url) {
		UserInfo ui = new UserInfo();
		ArrayList<Pub> list = new ArrayList<Pub>();
		String json = getFile(url);
		
		if (json == null){
			return null;
		}
		
		JSONArray respArray;
		try {
			JSONObject obj = new JSONObject(json);
			int userScore = obj.getInt("score");
			respArray = obj.getJSONArray("visitedPubs");
			
			for (int i=0; i< respArray.length(); i++){
				JSONObject pub = respArray.getJSONObject(i);
				int id = pub.getInt("id");
				String name = (String) pub.getString("name");				
				String address = (String) pub.getString("address"); 
				double lat = pub.getDouble("latitude");
				double longt = pub.getDouble("longitude");
				int pubId = pub.getInt("pub_id");
				Pub pubObj = new Pub();
				pubObj.setId(id);
				pubObj.setName(name);
				pubObj.setAddress(address);
				pubObj.setLat(lat);
				pubObj.setLongt(longt);
				pubObj.setPubId(pubId);
				list.add(pubObj);
			}
			ui.setPubs(list);
			ui.setScore(userScore);	
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		return ui;
	}
	

}
