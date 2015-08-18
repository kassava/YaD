package ru.android.develop.easybrash.yad.gui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import ru.android.develop.easybrash.yad.Model;
import ru.android.develop.easybrash.yad.R;
import ru.android.develop.easybrash.yad.WorkerFragment;

public class SplashActivity extends AppCompatActivity implements Model.Observer {
    private final String LOG_TAG = this.getClass().getSimpleName();
    public static String prefName = "Random";
    private SharedPreferences prefs;
    private TextView textView;
    private Model mModel;
    private final String TAG_WORKER = "TAG_WORK";
    public static String jsonStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "onCreate: ");
        setContentView(R.layout.splash_actvity);
        textView = (TextView)findViewById(R.id.textView);

        prefs = getSharedPreferences(prefName, MODE_PRIVATE);

        // Work with retain fragment.
        final WorkerFragment retainedWorkerFragment =
                (WorkerFragment) getSupportFragmentManager().findFragmentByTag(TAG_WORKER);

        if (retainedWorkerFragment != null) {
            Log.d(LOG_TAG, "retainedWorkerFragment != null");

            mModel = retainedWorkerFragment.getModel();
        } else {
            final WorkerFragment workerFragment = new WorkerFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(workerFragment, TAG_WORKER)
                    .commit();

            mModel = workerFragment.getModel();
        }
        mModel.registerObserver(this);

        if (prefs.getBoolean("FirstTime", true) == true) {
            textView.setText(R.string.hello_world);
            mModel.signIn(this);
        } else {
            textView.setText(R.string.hello2);
                    Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("FirstTime", false);
        editor.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(LOG_TAG, "onPause");
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

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSignInFailed(final Model signInModel) {
        Log.i(LOG_TAG, "onSignInFailed");
        Toast.makeText(this, "Receiving error", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
