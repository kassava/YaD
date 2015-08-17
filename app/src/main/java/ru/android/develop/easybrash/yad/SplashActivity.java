package ru.android.develop.easybrash.yad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.android.develop.easybrash.yad.dao.Category;
import ru.android.develop.easybrash.yad.dao.CategoryDao;
import ru.android.develop.easybrash.yad.dao.DaoMaster;
import ru.android.develop.easybrash.yad.dao.DaoSession;
import ru.android.develop.easybrash.yad.dao.Item;
import ru.android.develop.easybrash.yad.dao.ItemDao;

public class SplashActivity extends AppCompatActivity implements Model.Observer {
    private final String LOG_TAG = this.getClass().getSimpleName();
    public static String prefName = "Random";
    private SharedPreferences prefs;
    private TextView textView;
    private Model mModel;
    private final String TAG_WORKER = "TAG_WORKER";
    public static String jsonStr;

    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private CategoryDao categoryDao;
    private ItemDao itemDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_actvity);
        textView = (TextView)findViewById(R.id.textView);

        prefs = getSharedPreferences(prefName, MODE_PRIVATE);

        if (prefs.getBoolean("FirstTime", true) == true) {
            textView.setText(R.string.hello_world);
        } else {
            textView.setText(R.string.hello2);
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("FirstTime", false);
        editor.commit();

//        Thread timerThread = new Thread(){
//            public void run(){
//                try{
//                    sleep(1500);
//                }catch(InterruptedException e){
//                    e.printStackTrace();
//                }finally{
//                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                    startActivity(intent);
//                }
//            }
//        };
//        timerThread.start();

        // Work with retain fragment.
        final WorkerFragment retainedWorkerFragment =
                (WorkerFragment) getFragmentManager().findFragmentByTag(TAG_WORKER);

        if (retainedWorkerFragment != null) {
            mModel = retainedWorkerFragment.getModel();
        } else {
            final WorkerFragment workerFragment = new WorkerFragment();

            getFragmentManager().beginTransaction()
                    .add(workerFragment, TAG_WORKER)
                    .commit();

            mModel = workerFragment.getModel();
        }
        mModel.registerObserver(this);

        mModel.signIn();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        Log.i(LOG_TAG, "onDestroy");
        super.onDestroy();
        mModel.unregisterObserver(this);

        if (isFinishing()) {
            mModel.stopSignIn();
        }
    }

    @Override
    public void onSignInStarted(final Model signInModel) {

        Log.i(LOG_TAG, "onSignInStarted");
    }

    @Override
    public void onSignInSucceeded(final String string) {
        Log.i(LOG_TAG, "onSignInSucceeded");
        Log.d(LOG_TAG, "str: " + string);
        jsonStr = string;

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "json-db", null);
        db = helper.getWritableDatabase();


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
            JSONArray jsonArray = new JSONArray(string);
            Log.d(LOG_TAG, "Length: " + jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                getElements(jsonArray.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void getElements(JSONObject jsonObj) {
//        StringBuilder strBuilder = new StringBuilder();
        JSONArray jsonArray = null;
        try {
//            strBuilder.append(jsonObj.getString("title"));
//            strBuilder.append(" = ");

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
//                        strBuilder.append(obj.get("title"));
//                        strBuilder.append(" - ");

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
//                groups.add(strBuilder.toString());
            }
        } catch (JSONException e) {
            Log.d(LOG_TAG, "JSONException");
        }
//        groups.add(strBuilder.toString());
    }

    private boolean haveSubs(JSONObject jsonObj) {
        try {
            jsonObj.getJSONArray("subs");
            return true;
        } catch (JSONException e) {
//			e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onSignInFailed(final Model signInModel) {
        Log.i(LOG_TAG, "onSignInFailed");
        Toast.makeText(this, "Receiving error", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
