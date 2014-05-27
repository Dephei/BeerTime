package giorgi.dundua.beertime;

import android.navigationdrawer.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HelpFragment extends CustomFragment {
	public static final String ARG_MENU_NUMBER = "menu_number";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.help, container,
				false);
		return rootView;
	}	
}