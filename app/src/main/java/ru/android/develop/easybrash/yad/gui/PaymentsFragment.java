package ru.android.develop.easybrash.yad.gui;

import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ru.android.develop.easybrash.yad.GetFromDBDataService;
import ru.android.develop.easybrash.yad.Model;
import ru.android.develop.easybrash.yad.R;
import ru.android.develop.easybrash.yad.WorkerFragment;
import ru.android.develop.easybrash.yad.dao.Category;
import ru.android.develop.easybrash.yad.dao.CategoryDao;
import ru.android.develop.easybrash.yad.dao.DaoMaster;
import ru.android.develop.easybrash.yad.dao.DaoSession;
import ru.android.develop.easybrash.yad.dao.Item;
import ru.android.develop.easybrash.yad.dao.ItemDao;
import ru.android.develop.easybrash.yad.group.ChildItem;
import ru.android.develop.easybrash.yad.group.GroupItem;
import ru.android.develop.easybrash.yad.gui.adapter.ExampleAdapter;
import ru.android.develop.easybrash.yad.network.VolleyApplication;

/**
 * Created by ultra on 10.08.2015.
 */
public class PaymentsFragment extends ListFragment implements Model.Observer {
    private final String LOG_TAG = "PaysAndTransFragment";
    private static final String TAG_WORKER = "TAG_WORKER";
    private static final String ARG_SECTION_NUMBER = "section_number";

    private Model mModel;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ExampleAdapter mExampleAdapter;
    private PaymentsFragment mFragment;
    private AnimatedExpandableListView listView;
    private List<GroupItem> items = new ArrayList<GroupItem>();

    private DataReceiver receiver;
    private FilterRequestReceiver filterReceiver;

    /**
     * Return a new instance of this fragment for the given section number
     */
    public static PaymentsFragment newInstance(int sectionNumber) {
        PaymentsFragment fragment = new PaymentsFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.payments_v2,
                container, false);

        mFragment = this;
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView
                .findViewById(R.id.activity_main_swipe_refresh_layout);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(LOG_TAG, "onRefresh");

                        // Work with retain fragment.
                        final WorkerFragment retainedWorkerFragment =
                                (WorkerFragment) getFragmentManager().findFragmentByTag(TAG_WORKER);

                        if (retainedWorkerFragment != null) {
                            mModel = retainedWorkerFragment.getModel();
                        } else {
                            final WorkerFragment workerFragment = new WorkerFragment();

                            getFragmentManager().beginTransaction()
                                    .add(workerFragment, TAG_WORKER)
                                    .commit();

                            mModel = workerFragment.getModel();
                        }
                        mModel.registerObserver(mFragment);
                        mModel.signIn(VolleyApplication.getInstance());

//                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 20);
            }
        });
        listView = (AnimatedExpandableListView) rootView.findViewById(R.id.listView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");

        IntentFilter filter = new IntentFilter(GetFromDBDataService.DATASERVICE_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new DataReceiver();
        VolleyApplication.getInstance().registerReceiver(receiver, filter);

        filter = new IntentFilter(MainActivity.FILTER_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filterReceiver = new FilterRequestReceiver();
        VolleyApplication.getInstance().registerReceiver(filterReceiver, filter);

        Intent msgIntent = new Intent(VolleyApplication.getInstance(),
                GetFromDBDataService.class);
        VolleyApplication.getInstance().startService(msgIntent);
    }

    @Override
    public void onPause() {
        super.onPause();
        VolleyApplication.getInstance().unregisterReceiver(receiver);
        VolleyApplication.getInstance().unregisterReceiver(filterReceiver);
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);

        Log.d(LOG_TAG, "onActivityCreated");

        mExampleAdapter = new ExampleAdapter(VolleyApplication.getInstance());
    }

    private void setDataInView() {
        Log.d(LOG_TAG, "setDataInView");

        mExampleAdapter.setData(items);
        listView.setAdapter(mExampleAdapter);

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // Call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    listView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(LOG_TAG, "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(LOG_TAG, "onDetach");
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_main, menu);
//
//        // Get the SearchView and set the searchable configuration
//        SearchManager searchManager = (SearchManager) VolleyApplication.getInstance().getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        // Assumes current activity is the searchable activity
////        searchView.setSearchableInfo(searchManager.getSearchableInfo(VolleyApplication.getInstance().getComponentName()));
//        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
//    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id){
        Log.d(LOG_TAG, "onListItemClick");
    }

    @Override
    public void onSignInStarted(final Model signInModel) {
        Log.i(LOG_TAG, "onSignInStarted");
    }

    @Override
    public void onSignInSucceeded(final String string) {
        Log.i(LOG_TAG, "onSignInSucceeded");
        mModel.unregisterObserver(this);

        Intent msgIntent = new Intent(VolleyApplication.getInstance(),
                GetFromDBDataService.class);
        VolleyApplication.getInstance().startService(msgIntent);
    }

    @Override
    public void onSignInFailed(final Model signInModel) {
        Log.i(LOG_TAG, "onSignInFailed");
        Log.d(LOG_TAG, "str: " + signInModel.getDataStr());
        mModel.unregisterObserver(this);

        mSwipeRefreshLayout.setRefreshing(false);
    }

    public class DataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            intent.getParcelableArrayExtra(GetFromDBDataService.EXTRA_KEY_OUT);

            mSwipeRefreshLayout.setRefreshing(false);
            items.clear();
            items = intent.getParcelableArrayListExtra(GetFromDBDataService.EXTRA_KEY_OUT);
            Log.d(LOG_TAG, "DataReceiver");
            Log.d(LOG_TAG, "!!: " + items.size());

            setDataInView();
        }
    }

    private void expandAll() {
        int count = mExampleAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            listView.expandGroup(i);
        }
    }

    private void collapseAll() {
        int count = mExampleAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            listView.collapseGroup(i);
        }
    }

    public class FilterRequestReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String filter = intent.getStringExtra(MainActivity.FILTER_STRING);
            Log.d(LOG_TAG, "filter:" + filter);

            mExampleAdapter.filterData(filter);
            if (!filter.equals("")) {
                expandAll();
            } else {
                collapseAll();
            }
        }
    }
}
