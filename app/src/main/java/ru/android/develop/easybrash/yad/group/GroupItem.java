package ru.android.develop.easybrash.yad.group;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ultra on 18.08.2015.
 */
public class GroupItem implements Parcelable {
    public String title;
    public List<ChildItem> items = new ArrayList<ChildItem>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeTypedList(items);
    }

    public GroupItem() {
    }

    protected GroupItem(Parcel in) {
        this.title = in.readString();
        this.items = in.createTypedArrayList(ChildItem.CREATOR);
    }

    public static final Parcelable.Creator<GroupItem> CREATOR = new Parcelable.Creator<GroupItem>() {
        public GroupItem createFromParcel(Parcel source) {
            return new GroupItem(source);
        }

        public GroupItem[] newArray(int size) {
            return new GroupItem[size];
        }
    };
}
