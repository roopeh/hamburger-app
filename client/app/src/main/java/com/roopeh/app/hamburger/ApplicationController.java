package com.roopeh.app.hamburger;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/*
 * Based on https://arnab.ch/blog/2013/08/asynchronous-http-requests-in-android-using-volley/
 */

public class ApplicationController extends Application {
    private RequestQueue _queue;

    private static ApplicationController _instance;

    @Override
    public void onCreate() {
        super.onCreate();

        _instance = this;
    }

    public static synchronized ApplicationController getInstance() {
        return _instance;
    }

    public RequestQueue getRequestQueue() {
        if (_queue == null)
            _queue = Volley.newRequestQueue(getApplicationContext());

        return _queue;
    }

    public <T> void addToRequestQueue(Request<T> request, String tag) {
        request.setTag(TextUtils.isEmpty(tag) ? Helper.Constants.VOLLEY_TAG : tag);
        getRequestQueue().add(request);
    }

    public <T> void addToRequestQueue(Request<T> request) {
        request.setTag(Helper.Constants.VOLLEY_TAG);
        getRequestQueue().add(request);
    }

    public void cancelPendingRequests(Object tag) {
        if (_queue != null)
            _queue.cancelAll(tag);
    }
}
