package com.roopeh.app.hamburger;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiConnector {
    final private ApiResponseInterface _apiResponse;

    private AlertDialog _initLoadDialog = null;

    public ApiConnector(ApiResponseInterface apiResponse) {
        _apiResponse = apiResponse;
    }

    // Api methods
    public void initRestaurantsAndProducts(Context context) {
        _initLoadDialog = Helper.getInstance().createAlertDialog(context, "Loading...");
        loadRestaurants();
        loadProducts();
    }

    private void loadRestaurants() {
        final String urlStr = Helper.Constants.API_LINK + "/restaurants.php";
        final Bundle bundle = new Bundle();

        final ApiGetRequest request = new ApiGetRequest(urlStr, new HashMap<>(),
            response -> {
                Log.d("DEBUG_TAG", "Successful response via StringRequest: " + response);
                try {
                    final JSONObject result = new JSONObject(response);

                    final boolean success = Boolean.parseBoolean(result.getString("result"));
                    if (success) {
                        final JSONArray restaurantData = result.getJSONArray("restaurantdata");
                        bundle.putString("restaurants-json", restaurantData.toString());
                    }

                    bundle.putString("result", result.getString("result"));
                } catch (JSONException e) {
                    bundle.putString("result", "error");
                    e.printStackTrace();
                }

                _apiResponse.onResponse(Helper.ApiResponseType.RESTAURANTS, bundle);
            }, error -> ApiRequest.handleErrorInRequest(_apiResponse, Helper.ApiResponseType.RESTAURANTS, bundle, error));

        ApplicationController.getInstance().addToRequestQueue(request);
    }

    private void loadProducts() {
        final String urlStr = Helper.Constants.API_LINK + "/products.php";
        final Bundle bundle = new Bundle();

        final ApiGetRequest request = new ApiGetRequest(urlStr, new HashMap<>(),
                response -> {
                    Log.d("DEBUG_TAG", "Successful response via StringRequest: " + response);
                    try {
                        final JSONObject result = new JSONObject(response);

                        final boolean success = Boolean.parseBoolean(result.getString("result"));
                        if (success) {
                            final JSONArray productdata = result.getJSONArray("productdata");
                            bundle.putString("products-json", productdata.toString());
                        }

                        bundle.putString("result", result.getString("result"));
                    } catch (JSONException e) {
                        bundle.putString("result", "error");
                        e.printStackTrace();
                    }

                    _initLoadDialog.dismiss();
                    _apiResponse.onResponse(Helper.ApiResponseType.PRODUCTS, bundle);
                }, error -> {
                    _initLoadDialog.dismiss();
                    ApiRequest.handleErrorInRequest(_apiResponse, Helper.ApiResponseType.PRODUCTS, bundle, error);
        });

        ApplicationController.getInstance().addToRequestQueue(request);
    }

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
                dialog.dismiss();
                ApiRequest.handleErrorInRequest(_apiResponse, Helper.ApiResponseType.LOGIN, bundle, error);
            });

        ApplicationController.getInstance().addToRequestQueue(request);
    }

    // Get method
    private static class ApiGetRequest extends ApiRequest {
        public ApiGetRequest(String url, Map<String, String> params, Response.Listener<String> listener, Response.ErrorListener error) {
            super(Method.GET, url, params, listener, error);
        }
    }

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

        static public void handleErrorInRequest(ApiResponseInterface apiResponse, Helper.ApiResponseType responseType, Bundle bundle, VolleyError error) {
            bundle.putString("result", "error");
            apiResponse.onResponse(responseType, bundle);

            Log.d("DEBUG_TAG", "Error in StringRequest: " + error);
            error.printStackTrace();
        }
    }
}
