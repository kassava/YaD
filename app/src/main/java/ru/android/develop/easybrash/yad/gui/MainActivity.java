package ru.android.develop.easybrash.yad.gui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Toast;

import ru.android.develop.easybrash.yad.GetFromDBDataService;
import ru.android.develop.easybrash.yad.Model;
import ru.android.develop.easybrash.yad.R;
import ru.android.develop.easybrash.yad.network.VolleyApplication;


public class MainActivity extends AppCompatActivity
        implements Model.Observer, FragmentDrawer.FragmentDrawerListener,
        SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private Model mModel;
    private static final String TAG_WORKER = "TAG_WORKER";
    private final String LOG_TAG = this.getClass().getSimpleName();

    public static final String FILTER_STRING = "filter_string";
    public static final String FILTER_ACTION = "filter_action";

    private Toolbar mToolbar;
    private FragmentDrawer mDrawerFragment;
    private DrawerLayout drawerLayout;
    private SearchView mSearchView;

    public static String jsonStr;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mDrawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        mDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        mDrawerFragment.setDrawerListener(this);



        displayView(0);

        // Work with retain fragment.
//        final WorkerFragment retainedWorkerFragment =
//                (WorkerFragment) getSupportFragmentManager().findFragmentByTag(TAG_WORKER);
//
//        if (retainedWorkerFragment != null) {
//            mModel = retainedWorkerFragment.getModel();
//        } else {
//            final WorkerFragment workerFragment = new WorkerFragment();
//
//            getSupportFragmentManager().beginTransaction()
//                    .add(workerFragment, TAG_WORKER)
//                    .commit();
//
//            mModel = workerFragment.getModel();
//        }
//        mModel.registerObserver(this);

//        mModel.signIn();
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    @Override
    public void onBackPressed() {
        Log.d(LOG_TAG, "onBackPressed");

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
                break;
            case 1:
                fragment = new FavoritesFragment();
                title = getString(R.string.title_history);
                break;
            case 2:
                fragment = new FavoritesFragment();
                title = getString(R.string.title_favorites);
                break;
            case 3:
                fragment = new SettingsFragment();
                title = getString(R.string.title_settings);
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

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void hideDrawerIcon() {
        getSupportActionBar().setDisplayShowHomeEnabled(false);
//        getSupportActionBar().hide();

    }

    public void showDrawerIcon() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().show();
    }

    // old
    public void onNavigationDrawerItemSelected(int position) {

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
//        android.app.FragmentManager fragmentManager = getFragmentManager();

        switch (position) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PaymentsFragment.newInstance(position))
                        .commit();
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, FavoritesFragment.newInstance(position))
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, SettingsFragment.newInstance(position))
                        .commit();
                break;
            default:
                break;
        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

//        MenuItem searchItem = menu.findItem(R.id.action_search);


        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
//            Intent intent = new Intent(MainActivity.this, SearchActvity.class);
//            startActivity(intent);
//            setContentView(R.layout.search_activity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        Log.i(LOG_TAG, "onDestroy");
        super.onDestroy();
//        mModel.unregisterObserver(this);
//
//        if (isFinishing()) {
//            mModel.stopSignIn();
//        }
    }

    @Override
    public void onSignInStarted(final Model signInModel) {
        Log.i(LOG_TAG, "onSignInStarted");
    }

    @Override
    public void onSignInSucceeded(final String string) {
        Log.i(LOG_TAG, "onSignInSucceeded");
        Log.d(LOG_TAG, "str: " + string);
        jsonStr = string;
    }

    @Override
    public void onSignInFailed(final Model signInModel) {
        Log.i(LOG_TAG, "onSignInFailed");
        Toast.makeText(this, "Receiving error", Toast.LENGTH_SHORT).show();
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