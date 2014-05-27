package giorgi.dundua.helpers;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


public class CheckUser {
	final static int HTTP_STATUS_OK = 200;
	private static final byte[] buff = new byte[1024];

	public static String checkUser(String url) {
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
}
