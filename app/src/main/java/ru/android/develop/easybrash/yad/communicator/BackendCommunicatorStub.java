package ru.android.develop.easybrash.yad.communicator;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import ru.android.develop.easybrash.yad.network.HttpsTrustManager;
import ru.android.develop.easybrash.yad.network.VolleyApplication;
import ru.android.develop.easybrash.yad.network.VolleySingleton;

/**
 * Created by tagnik'zur on 10.08.2015.
 */
class BackendCommunicatorStub implements BackendCommunicator {
    private final String LOG_TAG = "BackendCommunicatorStub";

    private static final String VALID_USERNAME="user1";
    private static final String VALID_PASSWORD="qwerty";

    private String jsonResponse = null;

    @Override
    public boolean postSignIn(final String userName, final String password) throws InterruptedException {
        Thread.sleep(8000);
        return VALID_USERNAME.equals(userName) && VALID_PASSWORD.equals(password);
    }

    @Override
    public String postGetData(Context context) throws InterruptedException {
        Log.d(LOG_TAG, "postGetData");
        HttpsTrustManager.allowAllSSL();
        JsonObjectRequest request = new JsonObjectRequest(
                "https://money.yandex.ru/api/categories-list", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, response.toString());
                        jsonResponse = response.toString();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG_TAG, "error");
                    }
                }
        );


//        VolleySingleton.getInstance(context).getRequestQueue().add(request);
        VolleyApplication.getInstance().getRequestQueue().add(request);

        return jsonResponse;
    }
}
