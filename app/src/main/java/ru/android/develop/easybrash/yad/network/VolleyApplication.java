package ru.android.develop.easybrash.yad.network;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;

import ru.android.develop.easybrash.yad.R;

/**
 * Created by tagnik'zur on 11.08.2015.
 */
public class VolleyApplication extends Application {
    private static VolleyApplication sInstance;

    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        mRequestQueue = Volley.newRequestQueue(this);
//        mRequestQueue = Volley.newRequestQueue(this, new ExtHttpClientStack(new SslHttpClient(
//                keyStore, "test123"
//        )));
        sInstance = this;
    }

    public synchronized static VolleyApplication getInstance() {
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
