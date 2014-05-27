package giorgi.dundua.beertime;

import android.app.Activity;
import android.content.Intent;
import android.navigationdrawer.R;
import android.os.Bundle;

public class SplashingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splashing);

		Thread th = new Thread() {
			public void run() {
				try {
					sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					Intent intent = new Intent(SplashingActivity.this,
							Authorization.class);
					startActivity(intent);
				}
			}
		};
		th.start();
	}
}
