package ru.android.develop.easybrash.yad.gui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.android.develop.easybrash.yad.DataService;
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
    private PaymentsFragment mFragment;
    private AnimatedExpandableListView listView;
    private List<GroupItem> items = new ArrayList<GroupItem>();

    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private CategoryDao categoryDao;
    private ItemDao itemDao;

    private DataReceiver receiver;

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

    public void getDataFromDB() {
        Log.d(LOG_TAG, "getDataFromDB");

        DaoMaster.DevOpenHelper helper = new DaoMaster.
                DevOpenHelper(VolleyApplication.getInstance(), "json-db", null);
        db = helper.getWritableDatabase();

        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        categoryDao = daoSession.getCategoryDao();
        itemDao = daoSession.getItemDao();

        List<Category> categoryList = categoryDao.queryBuilder().where(
                CategoryDao.Properties.Id.isNotNull()).list();

        for (int i = 0; i < categoryList.size(); i++) {
//            Log.d(LOG_TAG, categoryList.get(i).getTitle());

            GroupItem groupItem = new GroupItem();
            groupItem.title = categoryList.get(i).getTitle();

            List<Item> itemList = itemDao.queryBuilder().where(ItemDao.Properties.
                    CategoryId.eq(categoryList.get(i).getId())).list();

            for (int j = 0; j < itemList.size(); j++) {
//                Log.d(LOG_TAG, itemList.get(j).getTitle());

                ChildItem child = new ChildItem();
                child.title = itemList.get(j).getTitle();
                groupItem.items.add(child);
            }
            items.add(groupItem);
        }

        helper.close();
        db.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");

        IntentFilter filter = new IntentFilter(DataService.DATASERVICE_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new DataReceiver();
        VolleyApplication.getInstance().registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        VolleyApplication.getInstance().unregisterReceiver(receiver);
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);

        Log.d(LOG_TAG, "onActivityCreated");

        getDataFromDB();
        setDataInView();
    }

    private void setDataInView() {
        Log.d(LOG_TAG, "setDataInView");

        ExampleAdapter mExampleAdapter = new ExampleAdapter(VolleyApplication.getInstance());
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
                DataService.class);
        VolleyApplication.getInstance().startService(msgIntent);

//        getDataFromDB();
//        setDataInView();
    }

    @Override
    public void onSignInFailed(final Model signInModel) {
        Log.i(LOG_TAG, "onSignInFailed");
        Log.d(LOG_TAG, "str: " + signInModel.getDataStr());
        mModel.unregisterObserver(this);
    }

    public class DataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            intent.getParcelableArrayExtra(DataService.EXTRA_KEY_OUT);

            mSwipeRefreshLayout.setRefreshing(false);
//            Parcelable[] array = intent.getParcelableArrayExtra(DataService.EXTRA_KEY_OUT);

//            Log.d(LOG_TAG, array[0].toString());

            List<GroupItem> sendItems = new ArrayList<GroupItem>();

            GroupItem groupItem = new GroupItem();
            groupItem.title = "payments";
            ChildItem childItem = new ChildItem();
            childItem.title = "pay1";
            groupItem.items.add(childItem);
            childItem = new ChildItem();
            groupItem.items.add(childItem);
//            sendItems.add(groupItem);
            items.add(groupItem);
            setDataInView();
        }
    }
}
