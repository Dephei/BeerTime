package giorgi.dundua.beertime;

import android.content.res.Configuration;
import android.navigationdrawer.R;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity {
	static DrawerLayout drawerLayout;
	static ListView drawerList;
	static String[] menuTitles;
	static CustomFragment activeFragment = null;
	ActionBarDrawerToggle drawerToggle;
	private CharSequence drawerTitle;
	private CharSequence title;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		

		title = drawerTitle = getTitle();
		menuTitles = getResources().getStringArray(R.array.menu_array);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);		
		drawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, menuTitles));
		drawerList.setOnItemClickListener(new DrawerItemClickListener());
		selectItem(1, getSupportFragmentManager());
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar application icon
		drawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		drawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* navigation drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(title);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(drawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        activeFragment.createActionBar(menuInflater, menu);
        activeFragment.setActionBar(this, getSupportActionBar());

        return true;
    }

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the navigation drawer is open, hide action items related to the content view
        //boolean drawerOpen = drawerLayout.isDrawerOpen(drawerLayout);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        activeFragment.prepareActionBar(menu);

        return super.onPrepareOptionsMenu(menu);
    }
	public void refreshList(MenuItem item) {
        activeFragment.refreshList();
    }
	/**
     * A placeholder fragment containing a simple view.
     */
   
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/* The click listener for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (position == 0) {
				mapFragmentTransaction();
			} else {
				selectItem(position, getSupportFragmentManager());
			}			
		}
	}
	public static void selectItem(int position, FragmentManager fragmentManager) {
		// update the main content by replacing fragments	
		
		switch (position) {
        case 1:
            activeFragment = new PubsFragment();
            break;
        case 2:
            activeFragment = new Destination();
            break;
        case 3:
            activeFragment = new Profile();
            break;
        case 4:
            activeFragment = new HelpFragment();
            break;
        default:
            activeFragment = new PubsFragment();
            break;
		}
			fragmentManager.beginTransaction()
            .replace(R.id.content_frame, activeFragment)
            .commit(); 
		drawerList.setItemChecked(position, true);		
		drawerLayout.closeDrawer(drawerList);
	}
	public void mapFragmentTransaction (){
		FragmentManager fragmentManager;
		FragmentTransaction transaction;
		Fragment mapFragment;
		Fragment f;
		mapFragment= new BasicMapFragment();
		fragmentManager = getSupportFragmentManager();
		transaction = fragmentManager.beginTransaction();
		f = getSupportFragmentManager()
			     .findFragmentById(R.id.map);
			   if (f !=null) {
			    transaction.remove(f);
			   }
		transaction
				.replace(R.id.content_frame, mapFragment).commit();
		drawerLayout.closeDrawer(drawerList);		
	}
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		drawerToggle.onConfigurationChanged(newConfig);
	}	
}