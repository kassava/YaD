package ru.android.develop.easybrash.yad;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.android.develop.easybrash.yad.group.ChildItem;
import ru.android.develop.easybrash.yad.group.GroupItem;

/**
 * Created by ultra on 18.08.2015.
 */
public class DataService extends IntentService {
    private final String LOG_TAG = this.getClass().getSimpleName();
    public static final String DATASERVICE_ACTION = "ru.android.develop.easybrash.yad.response";
    public static final String EXTRA_KEY_OUT = "EXTRA_OUT";

    public DataService() {
        super("dataservice");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "onHandleIntent");

        List<GroupItem> sendItems = new ArrayList<GroupItem>();

        GroupItem groupItem = new GroupItem();
        groupItem.title = "payments";
        ChildItem childItem = new ChildItem();
        childItem.title = "pay1";
        groupItem.items.add(childItem);
        childItem = new ChildItem();
        groupItem.items.add(childItem);
        sendItems.add(groupItem);

        // return result
        Intent intentResponse = new Intent();
        intentResponse.setAction(DATASERVICE_ACTION);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        intentResponse.putExtra(EXTRA_KEY_OUT, sendItems.toArray());
        sendBroadcast(intentResponse);
    }
}
