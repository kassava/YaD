package ru.android.develop.easybrash.yad;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by tagnik'zur on 10.08.2015.
 */
public class WorkerFragment extends Fragment {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private final Model mSignInModel;

    public WorkerFragment() {
        mSignInModel = new Model();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public Model getModel() {
        Log.d(LOG_TAG, "getModel");
        return mSignInModel;
    }
}
