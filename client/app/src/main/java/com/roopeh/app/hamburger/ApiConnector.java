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
        _initLoadDialog = Helper.getInstance().createAlertDialog(context, context.getString(R.string.apiInitialLoad));
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
                        final JSONArray restaurantData = result.getJSONArray("restaurant_data");
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
                        final JSONArray productData = result.getJSONArray("product_data");
                        bundle.putString("products-json", productData.toString());
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

    public void register(Context context, final Map<String, String> params) {
        final String urlStr = Helper.Constants.API_LINK + "/register.php";
        final Bundle bundle = new Bundle();

        // Show alert dialog
        final AlertDialog dialog = Helper.getInstance().createAlertDialog(context, context.getString(R.string.apiRegister));

        final ApiPostRequest request = new ApiPostRequest(urlStr, params,
            response -> {
                Log.d("DEBUG_TAG", "Successful response via StringRequest: " + response);
                try {
                    final JSONObject result = new JSONObject(response);

                    final boolean success = Boolean.parseBoolean(result.getString("result"));
                    if (!success) {
                        bundle.putString("status", result.getString("status"));
                        bundle.putString("error_text", result.getString("query_status"));
                    }

                    bundle.putString("result", result.getString("result"));
                } catch (JSONException e) {
                    bundle.putString("result", "error");
                    e.printStackTrace();
                }

                dialog.dismiss();
                _apiResponse.onResponse(Helper.ApiResponseType.REGISTER, bundle);
            }, error -> {
                dialog.dismiss();
                ApiRequest.handleErrorInRequest(_apiResponse, Helper.ApiResponseType.REGISTER, bundle, error);
        });

        ApplicationController.getInstance().addToRequestQueue(request);
    }

    public void login(Context context, String username, String pass) {
        if (Helper.getInstance().getUser() != null)
            return;

        final String urlStr = Helper.Constants.API_LINK + "/login.php";
        final Bundle bundle = new Bundle();

        final Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", pass);

        // Show alert dialog
        final AlertDialog dialog = Helper.getInstance().createAlertDialog(context, context.getString(R.string.apiLogin));

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
                        // May be empty
                        if (result.has("coupons")) {
                            final JSONArray couponData = result.getJSONArray("coupons");
                            bundle.putString("coupons-json", couponData.toString());
                        }

                        // Save raw order item JSON data to a Bundle
                        // May be empty
                        if (result.has("order-items")) {
                            final JSONArray orderItemData = result.getJSONArray("order-items");
                            bundle.putString("order-items-json", orderItemData.toString());
                        }

                        // Save order data to a Bundle
                        // May be empty
                        if (result.has("orders")) {
                            final JSONArray orderData = result.getJSONArray("orders");
                            bundle.putString("orders-json", orderData.toString());
                        }
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

    public void saveOrder(Context context, final Order order) {
        final String urlStr = Helper.Constants.API_LINK + "/save_order.php";
        final Bundle bundle = new Bundle();

        final Map<String, String> params = new HashMap<>();
        // Order data
        params.put("owner_id", String.valueOf(Helper.getInstance().getUser().getUserId()));
        params.put("order_date", String.valueOf(order.getOrderDate()));
        params.put("pickup_date", String.valueOf(order.getPickupDate()));
        params.put("restaurant_id", String.valueOf(order.getRestaurant().getId()));
        params.put("paid_status", order.isPaid() ? "1" : "0");
        params.put("original_price", String.valueOf(order.getOriginalPrice()));
        params.put("discount_price", String.valueOf(order.getDiscountPrice()));
        params.put("total_price", String.valueOf(order.getTotalPrice()));

        // Order item data
        final JSONArray allItemObj = new JSONArray();
        boolean dataCreated = true;
        for (int i = 0; i < order.getItems().size(); ++i) {
            try {
                final JSONObject itemObj = new JSONObject();
                final ShoppingItem item = order.getItems().get(i);
                itemObj.put("owner_id", String.valueOf(Helper.getInstance().getUser().getUserId()));
                itemObj.put("product_id", String.valueOf(item.getProduct().getId()));
                itemObj.put("price", String.valueOf(item.getPrice()));
                if (item.getProduct().isMeal()) {
                    itemObj.put("meal_drink", String.valueOf(item.getMealDrink()));
                    itemObj.put("large_drink", item.isLargeDrink() ? "1" : "0");
                    itemObj.put("meal_extra", String.valueOf(item.getMealExtra()));
                    itemObj.put("large_extra", item.isLargeExtra() ? "1" : "0");
                } else {
                    itemObj.put("meal_drink", "0");
                    itemObj.put("large_drink", "0");
                    itemObj.put("meal_extra", "0");
                    itemObj.put("large_extra", "0");
                }

                allItemObj.put(itemObj);
            } catch (JSONException e) {
                e.printStackTrace();
                dataCreated = false;
                break;
            }
        }

        if (!dataCreated) {
            bundle.putString("result", "error");
            _apiResponse.onResponse(Helper.ApiResponseType.SAVE_ORDER, bundle);
            return;
        }

        // Finally add items in JSON to params
        params.put("order_items", allItemObj.toString());

        // Create alert dialog
        final AlertDialog dialog = Helper.getInstance().createAlertDialog(context, context.getString(R.string.apiCreateOrder));

        final ApiPostRequest request = new ApiPostRequest(urlStr, params,
            response -> {
                Log.d("DEBUG_TAG", "Successful response via StringRequest: " + response);
                try {
                    final JSONObject result = new JSONObject(response);

                    final boolean success = Boolean.parseBoolean(result.getString("result"));
                    if (success) {
                        final int orderId = Integer.parseInt(result.getString("order_id"));
                        order.setId(orderId);
                    } else {
                        bundle.putString("error_text", result.getString("query_status"));
                    }

                    bundle.putString("result", result.getString("result"));
                } catch (JSONException e) {
                    bundle.putString("result", "error");
                    e.printStackTrace();
                }

                dialog.dismiss();
                _apiResponse.onResponse(Helper.ApiResponseType.SAVE_ORDER, bundle);
            }, error -> {
                dialog.dismiss();
                ApiRequest.handleErrorInRequest(_apiResponse, Helper.ApiResponseType.SAVE_ORDER, bundle, error);
        });

        ApplicationController.getInstance().addToRequestQueue(request);
    }

    public void logout(Context context) {
        final User user = Helper.getInstance().getUser();
        if (user == null)
            return;

        final String urlStr = Helper.Constants.API_LINK + "/logout.php";
        final Bundle bundle = new Bundle();

        final Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user.getUserId()));
        params.put("coupon_size", String.valueOf(user.getCoupons().size()));

        // Save coupons
        final JSONArray allCouponsObj = new JSONArray();
        boolean dataCreated = true;
        for (int i = 0; i < user.getCoupons().size(); ++i) {
            try {
                final JSONObject couponObj = new JSONObject();
                final Coupon coupon = user.getCoupons().get(i);
                couponObj.put("type", String.valueOf(coupon.getType()));
                couponObj.put("expiry_date", String.valueOf(coupon.getExpiryDateRaw()));

                allCouponsObj.put(couponObj);
            } catch (JSONException e) {
                e.printStackTrace();
                dataCreated = false;
                break;
            }
        }

        if (!dataCreated) {
            bundle.putString("result", "error");
            _apiResponse.onResponse(Helper.ApiResponseType.LOGOUT, bundle);
            return;
        }

        params.put("coupons", allCouponsObj.toString());

        // Show alert dialog
        final AlertDialog dialog = Helper.getInstance().createAlertDialog(context, context.getString(R.string.apiLogout));

        final ApiPostRequest request = new ApiPostRequest(urlStr, params,
            response -> {
                Log.d("DEBUG_TAG", "Successful response via StringRequest: " + response);
                try {
                    final JSONObject result = new JSONObject(response);

                    final boolean success = Boolean.parseBoolean(result.getString("result"));
                    if (!success)
                        bundle.putString("error_text", result.getString("query_status"));

                    bundle.putString("result", result.getString("result"));
                } catch (JSONException e) {
                    bundle.putString("result", "error");
                    e.printStackTrace();
                }

                dialog.dismiss();
                _apiResponse.onResponse(Helper.ApiResponseType.LOGOUT, bundle);
            }, error -> {
                dialog.dismiss();
                ApiRequest.handleErrorInRequest(_apiResponse, Helper.ApiResponseType.LOGOUT, bundle, error);
        });

        ApplicationController.getInstance().addToRequestQueue(request);
    }

    public void changePassword(Context context, String password) {
        if (Helper.getInstance().getUser() == null)
            return;

        final String urlStr = Helper.Constants.API_LINK + "/new_pass.php";
        final Bundle bundle = new Bundle();

        final Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(Helper.getInstance().getUser().getUserId()));
        params.put("password", password);

        // Show alert dialog
        final AlertDialog dialog = Helper.getInstance().createAlertDialog(context, context.getString(R.string.apiChangePass));

        final ApiPostRequest request = new ApiPostRequest(urlStr, params,
            response -> {
                Log.d("DEBUG_TAG", "Successful response via StringRequest: " + response);
                try {
                    final JSONObject result = new JSONObject(response);

                    final boolean success = Boolean.parseBoolean(result.getString("result"));
                    if (!success) {
                        bundle.putString("error_text", result.getString("query_status"));
                    }

                    bundle.putString("result", result.getString("result"));
                } catch (JSONException e) {
                    bundle.putString("result", "error");
                    e.printStackTrace();
                }

                dialog.dismiss();
                _apiResponse.onResponse(Helper.ApiResponseType.CHANGE_PASS, bundle);
            }, error -> {
                dialog.dismiss();
                ApiRequest.handleErrorInRequest(_apiResponse, Helper.ApiResponseType.CHANGE_PASS, bundle, error);
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
        final public Map<String, String> getHeaders() {
            final HashMap<String, String> headers = new HashMap<>();
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
