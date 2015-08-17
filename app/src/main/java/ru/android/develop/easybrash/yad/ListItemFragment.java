package ru.android.develop.easybrash.yad;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirections;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by tagnik'zur on 15.08.2015.
 */
public class ListItemFragment extends ListFragment implements
        SwipeActionAdapter.SwipeActionListener {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private final static String ARG_SECTION_NUMBER = "section_number";

    private ListItemFragment mFragment;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SwipeActionAdapter mAdapter;

    /**
     * Return a new instance of this fragment for the given section number
     */
    public static ListItemFragment newInstance(int sectionNumber) {
        ListItemFragment fragment = new ListItemFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.llist_item_fragment,
                container, false);

        mFragment = this;
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView
                .findViewById(R.id.swipe_refresh_layout);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(LOG_TAG, "onRefresh");

                        String[] content = new String[20];
                        for (int i = 0; i < 20; i++) content[i] = "Row " + (i + 21);
                        ArrayAdapter<String> stringAdapter = new ArrayAdapter<String>(
                                getActivity(),
                                R.layout.row,
                                R.id.text,
                                new ArrayList<String>(Arrays.asList(content))
                        );
                        mAdapter = new SwipeActionAdapter(stringAdapter);
                        mAdapter.setSwipeActionListener(mFragment)
                                .setDimBackgrounds(true)
                                .setListView(getListView());
                        setListAdapter(mAdapter);

                        mAdapter.addBackground(SwipeDirections.DIRECTION_NORMAL_LEFT, R.layout.row_bg_left)
                                .addBackground(SwipeDirections.DIRECTION_NORMAL_RIGHT, R.layout.row_bg_right);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1500);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(LOG_TAG, "onResume");
        ((MainActivity)getActivity()).setActionBarTitle("");
        ((MainActivity)getActivity()).hideDrawerIcon();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity)getActivity()).setActionBarTitle(getString(R.string.title_payments));
        ((MainActivity)getActivity()).showDrawerIcon();
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);

        Log.d(LOG_TAG, "onActivityCreated");

        String[] content = new String[3];
        for (int i = 0; i < 3; i++) content[i] = "Row " + (i + 31);
        ArrayAdapter<String> stringAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.row,
                R.id.text,
                new ArrayList<String>(Arrays.asList(content))
        );
        mAdapter = new SwipeActionAdapter(stringAdapter);
        mAdapter.setSwipeActionListener(this)
                .setDimBackgrounds(true)
                .setListView(getListView());
        setListAdapter(mAdapter);

        mAdapter.addBackground(SwipeDirections.DIRECTION_NORMAL_LEFT,R.layout.row_bg_left)
                .addBackground(SwipeDirections.DIRECTION_NORMAL_RIGHT, R.layout.row_bg_right);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id){
        Toast.makeText(
                getActivity(),
                "Clicked " + mAdapter.getItem(position),
                Toast.LENGTH_SHORT
        ).show();

//        FragmentManager fm = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fm
//                .beginTransaction();
//        PaymentsFragment fm2 = new PaymentsFragment();
//        fragmentTransaction.replace(R.id.container,
//                SettingsFragment.newInstance(position), "HELLO");
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();
//        Bundle bundle = new Bundle();
//        bundle.putString("position", list.get(position).store_id);
//        fm2.setArguments(bundle);

    }

    @Override
    public boolean hasActions(int position){
        return true;
    }

    @Override
    public boolean shouldDismiss(int position, int direction){
        return direction == SwipeDirections.DIRECTION_NORMAL_LEFT;
    }

    @Override
    public void onSwipe(int[] positionList, int[] directionList){
        for(int i=0;i<positionList.length;i++) {
            int direction = directionList[i];
            int position = positionList[i];
            String dir = "";

            switch (direction) {
                case SwipeDirections.DIRECTION_FAR_LEFT:
                    dir = "Far left";
//                    mModel.signIn();
                    break;
                case SwipeDirections.DIRECTION_NORMAL_LEFT:
                    dir = "Left";
                    break;
                case SwipeDirections.DIRECTION_FAR_RIGHT:
                    dir = "Far right";
                    break;
                case SwipeDirections.DIRECTION_NORMAL_RIGHT:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Test Dialog").setMessage("You swiped right").create().show();
                    dir = "Right";
                    break;
            }
            Toast.makeText(
                    getActivity(),
                    dir + " swipe Action triggered on " + mAdapter.getItem(position),
                    Toast.LENGTH_SHORT
            ).show();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        inflater.inflate(R.menu.list_item_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }

        if (item.getItemId() == R.id.action_item) {
            Toast.makeText(getActivity(), "Item action.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
