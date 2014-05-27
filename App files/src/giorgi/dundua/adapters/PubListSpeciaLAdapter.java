package giorgi.dundua.adapters;

import giorgi.dundua.models.Pub;
import giorgi.dundua.models.UserInfo;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.navigationdrawer.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PubListSpeciaLAdapter extends BaseAdapter {	
	
	Context context;
	UserInfo ui = new UserInfo(); 
	ArrayList<Pub> pubs = new ArrayList<Pub>();
	
	public PubListSpeciaLAdapter(Context context, UserInfo ui){
		this.context = context;
		this.pubs = ui.getPubs();		
	}
	
	@Override
	public int getCount() {
		return pubs.size();
	}
	@Override
	public Object getItem(int position) {
		return pubs.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				LayoutInflater mInflater = (LayoutInflater)
						context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.pub_list_item, null);
			}
			TextView tv_pub = (TextView)convertView.findViewById(R.id.tv_pub);
			tv_pub.setText(pubs.get(position).getName());
						
		return convertView;		
		}
}

