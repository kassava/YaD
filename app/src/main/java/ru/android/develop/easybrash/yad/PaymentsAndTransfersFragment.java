package ru.android.develop.easybrash.yad;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ultra on 10.08.2015.
 */
public class PaymentsAndTransfersFragment extends Fragment implements OnClickListener,
                Model.Observer {
    private final String LOG_TAG = "PaysAndTransFragment";
    private static final String TAG_WORKER = "TAG_WORKER";

    private static final String ARG_SECTION_NUMBER = "section_number";
    private Button button;
    private Model mModel;
    private TextView textView;

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
        View rootView = inflater.inflate(R.layout.payments_screen,
                container, false);

        button = (Button) rootView.findViewById(R.id.button);
        button.setOnClickListener(this);

        textView = (TextView) rootView.findViewById(R.id.textView3);

        Log.d(LOG_TAG, "textView: " + textView);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        mModel = new Model(getActivity());
        mModel.registerObserver(this);
    }

    @Override
    public void onClick(View view) {
        Log.d(LOG_TAG, "onClick");
        switch(view.getId()) {
            case R.id.button:
                mModel.signIn();
                break;
            default:
                break;
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
        textView.setText(string);
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
