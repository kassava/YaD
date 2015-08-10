package ru.android.develop.easybrash.yad;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ultra on 10.08.2015.
 */
public class PaymentsAndTransfersFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
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

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
