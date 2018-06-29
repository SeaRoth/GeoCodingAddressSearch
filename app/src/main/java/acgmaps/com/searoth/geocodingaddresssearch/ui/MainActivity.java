package acgmaps.com.searoth.geocodingaddresssearch.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import acgmaps.com.searoth.geocodingaddresssearch.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        if (savedInstanceState == null) {
            MapFragment fragment = new MapFragment();
            setListener(fragment);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, MapFragment.TAG).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_save:
                listener.saveClicked();
                return true;

            case R.id.action_delete:
                listener.deleteClicked();
                return true;

            case R.id.action_search:
                listener.searchClicked();
                return true;

            case R.id.action_view_favorites:
                listener.showFavoritesClicked();
                return true;

            case R.id.action_remove_favorites:
                listener.deleteAllClicked();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private MenuInterface listener ;

    public void setListener(MenuInterface listener)
    {
        this.listener = listener ;
    }
}
