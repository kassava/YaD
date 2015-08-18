package ru.android.develop.easybrash.yad.group;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ultra on 18.08.2015.
 */
public class ChildItem implements Parcelable {
    public String title;
    public String hint;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.hint);
    }

    public ChildItem() {
    }

    protected ChildItem(Parcel in) {
        this.title = in.readString();
        this.hint = in.readString();
    }

    public static final Parcelable.Creator<ChildItem> CREATOR = new Parcelable.Creator<ChildItem>() {
        public ChildItem createFromParcel(Parcel source) {
            return new ChildItem(source);
        }

        public ChildItem[] newArray(int size) {
            return new ChildItem[size];
        }
    };
}
