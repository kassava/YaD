package ru.android.develop.easybrash.yad.gui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.android.develop.easybrash.yad.R;

/**
 * Created by tagnik'zur on 10.08.2015.
 */
public class AboutFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    /**
     * Return a new instance of this fragment for the given section number
     */
    public static AboutFragment newInstance(int sectionNumber) {
        AboutFragment fragment = new AboutFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_about,
                container, false);

        return rootView;
    }
}
