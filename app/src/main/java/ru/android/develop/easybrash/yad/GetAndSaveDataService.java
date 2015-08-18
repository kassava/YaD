package ru.android.develop.easybrash.yad;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.android.develop.easybrash.yad.dao.Category;
import ru.android.develop.easybrash.yad.dao.CategoryDao;
import ru.android.develop.easybrash.yad.dao.DaoMaster;
import ru.android.develop.easybrash.yad.dao.DaoSession;
import ru.android.develop.easybrash.yad.dao.Item;
import ru.android.develop.easybrash.yad.dao.ItemDao;
import ru.android.develop.easybrash.yad.network.VolleyApplication;

/**
 * Created by tagnik'zur on 18.08.2015.
 */
public class GetAndSaveDataService extends IntentService {
    private final String LOG_TAG = this.getClass().getSimpleName();
    public static final String DATASERVICE_ACTION = "ru.android.develop.easybrash.yad.getsaveaction";
    public static final String RESPONSE_STRING = "RESPONSE_STRING";
    public static final String SUCCESS_STRING = "success";
    public static final String FAILED_STRING = "failed";

    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private CategoryDao categoryDao;
    private ItemDao itemDao;

    public GetAndSaveDataService() {
        super("getandsavedataservice");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = "https://money.yandex.ru/api/categories-list";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d(LOG_TAG, "response: " + response);

                        DaoMaster.DevOpenHelper helper = new DaoMaster.
                                DevOpenHelper(VolleyApplication.getInstance(), "json-db", null);
                        db = helper.getWritableDatabase();

                        // Add data to db
                        try {
                            DaoMaster.dropAllTables(db, true);
                            DaoMaster.createAllTables(db, false);
                        } catch(RuntimeException e) {
                            e.printStackTrace();
                        }
                        daoMaster = new DaoMaster(db);
                        daoSession = daoMaster.newSession();
                        categoryDao = daoSession.getCategoryDao();
                        itemDao = daoSession.getItemDao();

                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                getElements(jsonArray.getJSONObject(i));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        db.close();
                        helper.close();

//                        mIsWorking = false;
//                        mObservable.notifySucceeded(dataStr);

                        // return result
                        Intent intentResponse = new Intent();
                        intentResponse.setAction(DATASERVICE_ACTION);
                        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
                        intentResponse.putExtra(RESPONSE_STRING, SUCCESS_STRING);
                        sendBroadcast(intentResponse);

                        Log.d(LOG_TAG, "onResponse.success");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, "error: " + error.getMessage());

//                mIsWorking = false;
//                mObservable.notifyFailed();

                // return result
                Intent intentResponse = new Intent();
                intentResponse.setAction(DATASERVICE_ACTION);
                intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
                intentResponse.putExtra(RESPONSE_STRING, FAILED_STRING);
                sendBroadcast(intentResponse);
            }
        }
        );

        VolleyApplication.getInstance().getRequestQueue().add(request);
    }

    private void getElements(JSONObject jsonObj) {
        JSONArray jsonArray = null;
        try {
            Category entity = new Category();
            if (jsonObj.getString("title").equals("Игры и общение")) {
                entity.setTitle("Общение");
                categoryDao.insert(entity);
            } else {
                entity.setTitle(jsonObj.getString("title"));
                categoryDao.insert(entity);
            }

            if (haveSubs(jsonObj)) {
                jsonArray = jsonObj.getJSONArray("subs");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    if (!haveSubs(obj)) {
                        Item itemEntity = new Item();
                        itemEntity.setTitle(obj.getString("title"));
                        itemEntity.setIdentificator(obj.getString("id"));
                        itemEntity.setCategoryId(entity.getId());
                        itemDao.insert(itemEntity);
                    } else {
                        getElements(obj);
                    }
                }
            } else {
            }
        } catch (JSONException e) {
            Log.d(LOG_TAG, "JSONException");
        }
    }

    private boolean haveSubs(JSONObject jsonObj) {
        try {
            jsonObj.getJSONArray("subs");
            return true;
        } catch (JSONException e) {
            return false;
        }
    }
}
