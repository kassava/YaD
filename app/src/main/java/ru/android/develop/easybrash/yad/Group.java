package ru.android.develop.easybrash.yad;

/**
 * Created by tagnik'zur on 16.08.2015.
 */

import java.util.ArrayList;
import java.util.List;

public class Group {

    public String string;
    public final List<String> children = new ArrayList<String>();

    public Group(String string) {
        this.string = string;
    }

}
