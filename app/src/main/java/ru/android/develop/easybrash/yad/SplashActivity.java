package ru.android.develop.easybrash.yad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    public static String prefName = "Random";
    private SharedPreferences prefs;
    private TextView textView;

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

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(1500);
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

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

}
