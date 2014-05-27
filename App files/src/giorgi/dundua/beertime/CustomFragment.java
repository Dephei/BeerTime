package giorgi.dundua.beertime;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;

public abstract class CustomFragment extends Fragment {

    public void createActionBar(MenuInflater inflater, Menu menu){
    }
    public void prepareActionBar(Menu menu){
    }
    public void setActionBar(Context context, ActionBar actionBar){
    }
    public void refreshList() {
    }
}
