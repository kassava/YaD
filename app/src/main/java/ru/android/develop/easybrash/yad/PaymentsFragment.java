package ru.android.develop.easybrash.yad;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ultra on 10.08.2015.
 */
public class PaymentsFragment extends ListFragment implements Model.Observer,
        SwipeActionAdapter.SwipeActionListener {
    private final String LOG_TAG = "PaysAndTransFragment";
    private static final String TAG_WORKER = "TAG_WORKER";

    private static final String ARG_SECTION_NUMBER = "section_number";
    private Model mModel;
    protected SwipeActionAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PaymentsFragment mFragment;
//    private ExpandableListView listView;
    private AnimatedExpandableListView listView;
    private ExampleAdapter mExampleAdapter;
    private List<GroupItem> items = new ArrayList<GroupItem>();

    SparseArray<Group> groups = new SparseArray<Group>();
    private Integer groupsIndex = 0;

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

//                        String[] content = new String[20];
//                        for (int i = 0; i < 20; i++) content[i] = "Row " + (i + 21);
//                        ArrayAdapter<String> stringAdapter = new ArrayAdapter<String>(
//                                getActivity(),
//                                R.layout.row,
//                                R.id.text,
//                                new ArrayList<String>(Arrays.asList(content))
//                        );
//                        mAdapter = new SwipeActionAdapter(stringAdapter);
//                        mAdapter.setSwipeActionListener(mFragment)
//                                .setDimBackgrounds(true)
//                                .setListView(getListView());
//                        setListAdapter(mAdapter);
//
//                        mAdapter.addBackground(SwipeDirections.DIRECTION_NORMAL_LEFT,R.layout.row_bg_left)
//                                .addBackground(SwipeDirections.DIRECTION_NORMAL_RIGHT, R.layout.row_bg_right);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1500);
            }
        });
//        createData();
        listView = (AnimatedExpandableListView) rootView.findViewById(R.id.listView);
//        MyExpandableListAdapter adapter = new MyExpandableListAdapter(getActivity(),
//                groups);
//        listView.setAdapter(adapter);

        return rootView;
    }

    public void createData() {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(SplashActivity.jsonStr);

            for (int i = 0; i < jsonArray.length(); i++) {
                getElements(jsonArray.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        groupsIndex = 0;
    }

    private void getElements(JSONObject jsonObj) {
        Group group = null;
        GroupItem item = new GroupItem();

        JSONArray jsonArray = null;
        try {
            group = new Group(jsonObj.getString("title"));
            item.title = jsonObj.getString("title");

            if (haveSubs(jsonObj)) {
                jsonArray = jsonObj.getJSONArray("subs");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    if (!haveSubs(obj)) {
                        group.children.add(obj.getString("title"));
                        ChildItem child = new ChildItem();
                        child.title = obj.getString("title");
                        item.items.add(child);
                    } else {
                        getElements(obj);
                    }
                }
            } else {
                groups.append(groupsIndex, group);
                groupsIndex++;
                items.add(item);
            }
        } catch (JSONException e) {
            Log.d(LOG_TAG, "JSONException");
        }
        groups.append(groupsIndex, group);
        groupsIndex++;
        items.add(item);
    }

    private boolean haveSubs(JSONObject jsonObj) {
        try {
            jsonObj.getJSONArray("subs");
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);

        Log.d(LOG_TAG, "onActivityCreated");

//        String[] content = new String[20];
//        for (int i = 0; i < 20; i++) content[i] = "Row " + (i + 1);
//        ArrayAdapter<String> stringAdapter = new ArrayAdapter<String>(
//                getActivity(),
//                R.layout.row,
//                R.id.text,
//                new ArrayList<String>(Arrays.asList(content))
//        );
//        mAdapter = new SwipeActionAdapter(stringAdapter);
//        mAdapter.setSwipeActionListener(this)
//                .setDimBackgrounds(true)
//                .setListView(getListView());
//        setListAdapter(mAdapter);
//
//        mAdapter.addBackground(SwipeDirections.DIRECTION_NORMAL_LEFT,R.layout.row_bg_left)
//                .addBackground(SwipeDirections.DIRECTION_NORMAL_RIGHT, R.layout.row_bg_right);

        createData();
//        ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.listView);
        MyExpandableListAdapter adapter = new MyExpandableListAdapter(getActivity(),
                groups);
//        listView.setAdapter(adapter);
        mExampleAdapter = new ExampleAdapter(getActivity());
        mExampleAdapter.setData(items);
        listView.setAdapter(mExampleAdapter);

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
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

        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        mModel = new Model(getActivity());
        mModel.registerObserver(this);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id){
        Log.d(LOG_TAG, "onListItemClick");

        Toast.makeText(
                getActivity(),
                "Clicked " + mAdapter.getItem(position),
                Toast.LENGTH_SHORT
        ).show();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm
                .beginTransaction();
//        PaymentsFragment fm2 = new PaymentsFragment();
        fragmentTransaction.replace(R.id.container_body,
                ListItemFragment.newInstance(position));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
//        Bundle bundle = new Bundle();
//        bundle.putString("position", list.get(position).store_id);
//        fm2.setArguments(bundle);
//        getSupportActionBar().setTitle(title);
//        getActivity().getActionBar().setSubtitle("Пичи пие");
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
                    mModel.signIn();
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
    public void onSignInStarted(final Model signInModel) {
        Log.i(LOG_TAG, "onSignInStarted");
    }

    @Override
    public void onSignInSucceeded(final String string) {
        Log.i(LOG_TAG, "onSignInSucceeded");
        Log.d(LOG_TAG, "str: " + string);
    }

    @Override
    public void onSignInFailed(final Model signInModel) {
        Log.i(LOG_TAG, "onSignInFailed");
        Log.d(LOG_TAG, "str: " + signInModel.getDataStr());
    }

    private static class GroupItem {
        String title;
        List<ChildItem> items = new ArrayList<ChildItem>();
    }

    private static class ChildItem {
        String title;
        String hint;
    }

    private static class ChildHolder {
        TextView title;
        TextView hint;
    }

    private static class GroupHolder {
        TextView title;
    }

    /**
     * Adapter for our list of {@link GroupItem}s.
     */
    private class ExampleAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
        private LayoutInflater inflater;

        private List<GroupItem> items;

        public ExampleAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void setData(List<GroupItem> items) {
            this.items = items;
        }

        @Override
        public ChildItem getChild(int groupPosition, int childPosition) {
            return items.get(groupPosition).items.get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder holder;
            ChildItem item = getChild(groupPosition, childPosition);
            if (convertView == null) {
                holder = new ChildHolder();
                convertView = inflater.inflate(R.layout.listrow_details, parent, false);
                holder.title = (TextView) convertView.findViewById(R.id.textView1);
//                holder.hint = (TextView) convertView.findViewById(R.id.textHint);
                convertView.setTag(holder);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }

            holder.title.setText(item.title);
//            holder.hint.setText(item.hint);

            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            return items.get(groupPosition).items.size();
        }

        @Override
        public GroupItem getGroup(int groupPosition) {
            return items.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return items.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupHolder holder;
            GroupItem item = getGroup(groupPosition);
            if (convertView == null) {
                holder = new GroupHolder();
                convertView = inflater.inflate(R.layout.listrow_group, parent, false);
                holder.title = (TextView) convertView.findViewById(R.id.textView1);
                convertView.setTag(holder);
            } else {
                holder = (GroupHolder) convertView.getTag();
            }

            holder.title.setText(item.title);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int arg0, int arg1) {
            return true;
        }

    }
}
