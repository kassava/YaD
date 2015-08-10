package ru.android.develop.easybrash.yad;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ultra on 10.08.2015.
 */
public class PaymentsAndTransfers extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.payments_screen,
                container, false);

        return rootView;
    }
}
