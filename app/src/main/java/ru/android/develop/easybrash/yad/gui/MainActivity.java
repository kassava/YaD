package ru.android.develop.easybrash.yad.gui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import ru.android.develop.easybrash.yad.R;



public class MainActivity extends AppCompatActivity
        implements DrawerFragment.FragmentDrawerListener,
        SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private final String LOG_TAG = this.getClass().getSimpleName();

    public static final String FILTER_STRING = "filter_string";
    public static final String FILTER_ACTION = "filter_action";
    private final boolean HIDE_MENU_ITEM = true;
    private final boolean SHOW_MENU_ITEM = false;

    private Toolbar mToolbar;
    private DrawerFragment mDrawerFragment;
    private DrawerLayout drawerLayout;
    private SearchView mSearchView;

    private boolean mState = SHOW_MENU_ITEM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mDrawerFragment = (DrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        mDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        mDrawerFragment.setDrawerListener(this);

        displayView(0);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = PaymentsFragment.newInstance(position);
                title = getString(R.string.title_payments);
                mState = SHOW_MENU_ITEM;
                break;
            case 1:
                fragment = AboutFragment.newInstance(position);
                title = getString(R.string.title_about);
                mState = HIDE_MENU_ITEM;
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);

        if (mState == HIDE_MENU_ITEM) {
            menu.findItem(R.id.action_search).setVisible(false);
        } else {
            menu.findItem(R.id.action_search).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        Log.i(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onClose() {
        Log.d(LOG_TAG, "onClose");
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(LOG_TAG, "onQueryTextSubmit: " + query);

        Intent intentResponse = new Intent();
        intentResponse.setAction(FILTER_ACTION);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        intentResponse.putExtra(FILTER_STRING, query);
        sendBroadcast(intentResponse);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(LOG_TAG, "onQueryTextChange");

        Intent intentResponse = new Intent();
        intentResponse.setAction(FILTER_ACTION);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        intentResponse.putExtra(FILTER_STRING, newText);
        sendBroadcast(intentResponse);

        return false;
    }
}
