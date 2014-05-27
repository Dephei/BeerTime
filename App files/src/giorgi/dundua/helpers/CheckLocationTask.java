package giorgi.dundua.helpers;

import giorgi.dundua.beertime.PubsFragment;

import java.util.ArrayList;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class CheckLocationTask extends AsyncTask<Void , Void, ArrayList<String>> {	
	Context context;
	String response;
	String userScore;
	public CheckLocationTask(Context context){
		this.context = context;
	}

	@Override
	protected ArrayList<String> doInBackground(Void... params) {
		ArrayList<String> result = CheckLocation.
				destination(Constants.checkLocation+PubsFragment.mEmail);
			return result;
	}	
		
	@Override
	protected void onPostExecute(ArrayList<String> result) {
		if (result == null){
			Toast.makeText(context, "error", Toast.LENGTH_LONG).show();
			return;
		}
		super.onPostExecute(result);
		response = result.get(0);
		userScore = result.get(1);
		Toast.makeText(context, "Destination: "+response+", points: "+userScore, 
				Toast.LENGTH_LONG).show();
		if (response.equals("true")){
			PubsFragment.clearCurrentTarget();
		}
	}
} 