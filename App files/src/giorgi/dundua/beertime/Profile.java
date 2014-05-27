package giorgi.dundua.beertime;

import giorgi.dundua.helpers.GetPubListTask;
import giorgi.dundua.models.Pub;

import java.util.ArrayList;

import android.navigationdrawer.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class Profile extends CustomFragment{
	ListView pubsleListView;
	ArrayList<Pub> userPubs;
	String userScore;
		public static final String ARG_MENU_NUMBER = "menu_number";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.profile, container,
				false);
		TextView tv_user_score = (TextView)view.findViewById(R.id.tv_user_score);
		pubsleListView = (ListView) view.findViewById(R.id.pubsleListView);		
		new GetPubListTask(tv_user_score, pubsleListView, getActivity()).execute();
		return view;
	}
}
