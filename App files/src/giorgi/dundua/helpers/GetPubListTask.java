package giorgi.dundua.helpers;

import giorgi.dundua.adapters.PubListSpeciaLAdapter;
import giorgi.dundua.beertime.Authorization;
import giorgi.dundua.models.Pub;
import giorgi.dundua.models.UserInfo;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GetPubListTask extends AsyncTask<Void, Void, UserInfo>{
	Context context;
	ArrayList<Pub> userPubs;
	Pub temporary;
	String userPub;
	int userScore;
	TextView tv_score;
	ListView lv_pubs_feed;
	

	public GetPubListTask (TextView tv_score, ListView lv_pubs_feed, Context context){
		this.context = context;
		this.tv_score = tv_score;
		this.lv_pubs_feed = lv_pubs_feed; 
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
		PubListSpeciaLAdapter adapter = new PubListSpeciaLAdapter(context, result);
		lv_pubs_feed.setAdapter(adapter);
		tv_score.setText("Your Score: "+result.getScore());
		/*
		super.onPostExecute(result);
		userPubs = result.getPubs();
		userScore = result.getScore();
		tv_score.setText(userScore);
		for (int i=0; i< userPubs.size(); i++){
			temporary = userPubs.get(i);
			userPub = temporary.getName();
			tv_pub.setText(userPub);
		}
		}
		*/
		}		
}
