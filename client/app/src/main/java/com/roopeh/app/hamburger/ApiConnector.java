package com.roopeh.app.hamburger;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiConnector {
    final private ApiResponseInterface _apiResponse;

    public ApiConnector(ApiResponseInterface apiResponse) {
        _apiResponse = apiResponse;
    }

    // Api methods
    public void login(Context context, String username, String pass) {
        final String urlStr = Helper.Constants.API_LINK + "/login.php";
        final Bundle bundle = new Bundle();

        final Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", pass);

        // Show alert dialog
        final AlertDialog dialog = Helper.getInstance().createAlertDialog(context, "Logging in...");

        final ApiPostRequest request = new ApiPostRequest(urlStr, params,
            response -> {
                Log.d("DEBUG_TAG", "Successful response via StringRequest: " + response);
                try {
                    final JSONObject result = new JSONObject(response);

                    final boolean success = Boolean.parseBoolean(result.getString("result"));
                    if (success) {
                        final JSONObject userData = result.getJSONArray("userdata").getJSONObject(0);

                        // Save user data to a Bundle
                        final JSONArray userStrings = userData.names();
                        for (int i = 0; i < (userStrings != null ? userStrings.length() : 0); ++i) {
                            bundle.putString(userStrings.getString(i), userData.getString(userStrings.getString(i)));
                        }

                        // Save raw coupon JSON data to a Bundle
                        final JSONArray couponData = result.getJSONArray("coupons");
                        bundle.putString("coupons-json", couponData.toString());

                        // Save raw order item JSON data to a Bundle
                        final JSONArray orderItemData = result.getJSONArray("order-items");
                        bundle.putString("order-items-json", orderItemData.toString());

                        // Save order data to a Bundle
                        final JSONArray orderData = result.getJSONArray("orders");
                        bundle.putString("orders-json", orderData.toString());
                    }

                    bundle.putString("result", result.getString("result"));
                } catch (JSONException e) {
                    bundle.putString("result", "error");
                    e.printStackTrace();
                }

                dialog.dismiss();
                _apiResponse.onResponse(Helper.ApiResponseType.LOGIN, bundle);
            }, error -> {
                bundle.putString("result", "error");
                dialog.dismiss();
                _apiResponse.onResponse(Helper.ApiResponseType.LOGIN, bundle);

                Log.d("DEBUG_TAG", "Error in StringRequest: " + error);
                error.printStackTrace();
            });

        ApplicationController.getInstance().addToRequestQueue(request);
    }

    // Get method

    // Post method
    private static class ApiPostRequest extends ApiRequest {
        public ApiPostRequest(String url, Map<String, String> params, Response.Listener<String> listener, Response.ErrorListener error) {
            super(Method.POST, url, params, listener, error);
        }

        @Override
        public Map<String, String> getHeaders() {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            return headers;
        }
    }

    // Api request class
    private static class ApiRequest extends StringRequest {
        final private Map<String, String> _params;

        public ApiRequest(int method, String url, Map<String, String> params, Response.Listener<String> listener, Response.ErrorListener error) {
            super(method, url, listener, error);

            _params = params;
        }

        @Override
        protected Map<String, String> getParams() {
            return _params;
        }
    }
}
