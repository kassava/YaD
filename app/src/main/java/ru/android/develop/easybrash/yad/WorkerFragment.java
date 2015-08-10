package ru.android.develop.easybrash.yad;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by tagnik'zur on 10.08.2015.
 */
public class WorkerFragment extends Fragment {
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
        return mSignInModel;
    }
}
