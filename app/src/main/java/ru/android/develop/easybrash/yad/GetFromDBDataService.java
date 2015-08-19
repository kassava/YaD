package ru.android.develop.easybrash.yad;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.android.develop.easybrash.yad.dao.Category;
import ru.android.develop.easybrash.yad.dao.CategoryDao;
import ru.android.develop.easybrash.yad.dao.DaoMaster;
import ru.android.develop.easybrash.yad.dao.DaoSession;
import ru.android.develop.easybrash.yad.dao.Item;
import ru.android.develop.easybrash.yad.dao.ItemDao;
import ru.android.develop.easybrash.yad.group.ChildItem;
import ru.android.develop.easybrash.yad.group.GroupItem;
import ru.android.develop.easybrash.yad.network.VolleyApplication;

/**
 * Created by ultra on 18.08.2015.
 */
public class GetFromDBDataService extends IntentService {
    private final String LOG_TAG = this.getClass().getSimpleName();
    public static final String DATASERVICE_ACTION = "ru.android.develop.easybrash.yad.response";
    public static final String EXTRA_KEY_OUT = "EXTRA_OUT";

    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private CategoryDao categoryDao;
    private ItemDao itemDao;

    private ArrayList<GroupItem> sendItems = new ArrayList<GroupItem>();

    public GetFromDBDataService() {
        super("getfromdbdataservice");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "onHandleIntent");
        sendItems.clear();
        getDataFromDB();

        // return result
        Intent intentResponse = new Intent();
        intentResponse.setAction(DATASERVICE_ACTION);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        intentResponse.putParcelableArrayListExtra(EXTRA_KEY_OUT, sendItems);
        sendBroadcast(intentResponse);
    }

    public void getDataFromDB() {
        Log.d(LOG_TAG, "getDataFromDB");

        DaoMaster.DevOpenHelper helper = new DaoMaster.
                DevOpenHelper(VolleyApplication.getInstance(), "json-db", null);
        db = helper.getWritableDatabase();

        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        categoryDao = daoSession.getCategoryDao();
        itemDao = daoSession.getItemDao();

        List<Category> categoryList = categoryDao.queryBuilder().where(
                CategoryDao.Properties.Id.isNotNull()).list();

        for (int i = 0; i < categoryList.size(); i++) {
//            Log.d(LOG_TAG, categoryList.get(i).getTitle());

            GroupItem groupItem = new GroupItem();
            groupItem.title = categoryList.get(i).getTitle();

            List<Item> itemList = itemDao.queryBuilder().where(ItemDao.Properties.
                    CategoryId.eq(categoryList.get(i).getId())).list();

            for (int j = 0; j < itemList.size(); j++) {
//                Log.d(LOG_TAG, itemList.get(j).getTitle());

                ChildItem child = new ChildItem();
                child.title = itemList.get(j).getTitle();
                groupItem.items.add(child);
            }
            sendItems.add(groupItem);
        }

        helper.close();
        db.close();
    }
}
