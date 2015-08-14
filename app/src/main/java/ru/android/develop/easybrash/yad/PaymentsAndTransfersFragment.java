package ru.android.develop.easybrash.yad;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirections;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ultra on 10.08.2015.
 */
public class PaymentsAndTransfersFragment extends ListFragment implements OnClickListener,
                Model.Observer, SwipeActionAdapter.SwipeActionListener {
    private final String LOG_TAG = "PaysAndTransFragment";
    private static final String TAG_WORKER = "TAG_WORKER";

    private static final String ARG_SECTION_NUMBER = "section_number";
    private Model mModel;
    protected SwipeActionAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PaymentsAndTransfersFragment mFragment;

    /**
     * Return a new instance of this fragment for the given section number
     */
    public static PaymentsAndTransfersFragment newInstance(int sectionNumber) {
        PaymentsAndTransfersFragment fragment = new PaymentsAndTransfersFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.payments,
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

                        mAdapter.addBackground(SwipeDirections.DIRECTION_NORMAL_LEFT,R.layout.row_bg_left)
                                .addBackground(SwipeDirections.DIRECTION_NORMAL_RIGHT, R.layout.row_bg_right);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1500);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);

        Log.d(LOG_TAG, "onActivityCreated");

        String[] content = new String[20];
        for (int i = 0; i < 20; i++) content[i] = "Row " + (i + 1);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Log.d(LOG_TAG, "onAttach");

        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        mModel = new Model(getActivity());
        mModel.registerObserver(this);
    }

    @Override
    public void onClick(View view) {
        Log.d(LOG_TAG, "onClick");
//        switch(view.getId()) {
//            case R.id.button:
//                mModel.signIn();
//                break;
//            default:
//                break;
//        }
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id){
        Toast.makeText(
                getActivity(),
                "Clicked " + mAdapter.getItem(position),
                Toast.LENGTH_SHORT
        ).show();
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
        toJSON(string);
    }

    @Override
    public void onSignInFailed(final Model signInModel) {
        Log.i(LOG_TAG, "onSignInFailed");
        Log.d(LOG_TAG, "str: " + signInModel.getDataStr());
    }

    private void toJSON(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            Log.d(LOG_TAG, String.valueOf(jsonArray.length()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
