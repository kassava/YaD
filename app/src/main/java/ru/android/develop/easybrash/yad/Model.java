package ru.android.develop.easybrash.yad;

import android.content.Context;
import android.database.Observable;
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
 * Created by tagnik'zur on 10.08.2015.
 */
public class Model {
    private static final String LOG_TAG = "Model";

    private final SignInObservable mObservable = new SignInObservable();
    private boolean mIsWorking;
    private Context mCtx;
    private String dataStr;
    private String responseStr;

    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private CategoryDao categoryDao;
    private ItemDao itemDao;

    public Model() {
        Log.i(LOG_TAG, "new Instance");
    }

    public Model(Context context) {
        Log.i(LOG_TAG, "new Instance: " + context);

        mCtx = context;
    }

    public String getDataStr() {
        return dataStr;
    }

    public void signIn(final Context context) {
        Log.d(LOG_TAG, "sign in started: " + context);

        if (mIsWorking) {
            return;
        }

        mObservable.notifyStarted();

        mIsWorking = true;

        String url = "https://money.yandex.ru/api/categories-list";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d(LOG_TAG, "response: " + response);
                        dataStr = response;

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
                            JSONArray jsonArray = new JSONArray(dataStr);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                getElements(jsonArray.getJSONObject(i));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        db.close();
                        helper.close();

                        mIsWorking = false;
                        mObservable.notifySucceeded(dataStr);

                        Log.d(LOG_TAG, "onResponse.success");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    Log.d(LOG_TAG, "error: " + error.getMessage());

                    mIsWorking = false;
                    mObservable.notifyFailed();
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

    public void stopSignIn() {
        if (mIsWorking) {
            mIsWorking = false;
        }
    }

    public void registerObserver(final Observer observer) {
        mObservable.registerObserver(observer);
        if (mIsWorking) {
            observer.onSignInStarted(this);
        }
    }

    public void unregisterObserver(final Observer observer) {
        mObservable.unregisterObserver(observer);
    }

    public interface Observer {
        void onSignInStarted(Model model);

        void onSignInSucceeded(String string);

        void onSignInFailed(Model model);
    }

    private class SignInObservable extends Observable<Observer> {
        public void notifyStarted() {
            for (final Observer observer : mObservers) {
                observer.onSignInStarted(Model.this);
            }
        }

        public void notifySucceeded(String string) {
            for (final Observer observer : mObservers) {
                observer.onSignInSucceeded(string);
            }
        }

        public void notifyFailed() {
            for (final Observer observer : mObservers) {
                observer.onSignInFailed(Model.this);
            }
        }
    }
}
