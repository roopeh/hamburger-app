package com.roopeh.app.hamburger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

// Singleton helper class
public class Helper {
    public static class Development {
        // 0 = disabled, 1 = enabled
        // On first login, generates some coupons with old date
        final public static int TEST_COUPON_EXPIRY = 1;

        // 0 = disabled, 1 = enabled
        // Enables fast login and logout buttons in home screen with an hardcoded account
        final public static int DEV_FAST_LOGIN = 0;

        // 0 = disabled, 1 = enabled
        // If enabled, order will be done in 20 seconds
        final public static int TEST_FAST_ORDER = 0;
    }

    public static class Constants {
        final public static String API_LINK = "http://192.168.200.53";
        final public static String VOLLEY_TAG = "VolleyTag";

        final public static int GRID_DIVIDER = 45;
        final public static int LIST_DIVIDER = 16;

        final public static int PRODUCT_CATEGORY_HAMBURGER = 0;
        final public static int PRODUCT_CATEGORY_MEAL = 1;

        final public static int COUPON_TYPE_FREE_LARGE_DRINK = 1;
        final public static int COUPON_TYPE_FREE_LARGE_EXTRAS = 2;
        final public static int COUPON_TYPE_50_OFF = 3;
        final public static int COUPON_TYPE_20_OFF = 4;
        final public static int COUPON_TYPE_EMPTY_COUPON = 10;

        final public static String STRING_TIMEZONE = "GMT+2";
    }

    public enum ApiResponseType {
        RESTAURANTS,
        PRODUCTS,
        REGISTER,
        LOGIN,
        SAVE_ORDER,
        LOGOUT,
        CHANGE_PASS
    }

    private static Helper mInstance = null;

    private User _user = null;
    private List<Restaurant> _restaurants = null;
    private List<Product> _products = null;

    final private byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.UTF_8);

    private Helper() {}

    public static Helper getInstance() {
        if (mInstance == null)
            mInstance = new Helper();

        return mInstance;
    }

    /*
     * Api related
     */
    final public AlertDialog createAlertDialog(Context context, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setCancelable(false);

        final AlertDialog dialog = builder.create();
        dialog.show();

        final TextView msgView = dialog.findViewById(android.R.id.message);
        msgView.setGravity(Gravity.CENTER);

        return dialog;
    }

    final public String encryptPasswordReturnInHex(final String password) {
        try {
            // Encrypt password with SHA-256
            final MessageDigest digester = MessageDigest.getInstance("SHA-256");
            final byte[] passBytes = password.getBytes(StandardCharsets.UTF_8);
            digester.update(passBytes, 0, passBytes.length);
            final byte[] hash = digester.digest();

            // Convert it to hex
            // Credits to https://stackoverflow.com/a/9855338
            final byte[] hexChars = new byte[hash.length * 2];
            for (int i = 0; i < hash.length; ++i) {
                final int b = hash[i] & 0xFF;
                hexChars[i * 2] = HEX_ARRAY[b >>> 4];
                hexChars[i * 2 + 1] = HEX_ARRAY[b & 0x0F];
            }

            return new String(hexChars, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return password;
    }

    /*
     * User
     */
    public void setUser(User user) {
        if (_user != null)
            return;

        _user = user;
    }

    final public User getUser() {
        return _user;
    }

    public void logoutUser() {
        if (_user == null)
            return;

        _user = null;
    }

    /*
     * Products
     */
    public void initializeProducts(List<Product> products) {
        _products = products;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // API 24+
            Collections.sort(_products, Comparator.comparing(Product::getName));
        } else {
            // For older APis
            Collections.sort(_products, (prod1, prod2) -> prod1.getName().compareTo(prod2.getName()));
        }

    }

    // Generates test products with random price
    private Product generateTestProduct(String name, boolean meal) {
        final Product prod = meal ? new Meal(-1, name) : new Product(-1, name);

        // Generates random price from 1 to 20 euros
        // also generates either .0, .25, .50 or .75 cents
        final int rand = new Random().nextInt(20) + 1;
        final double price = new Random().nextBoolean() ? rand : rand + (0.25f * (new Random().nextInt(3) + 1));
        prod.setPrice(price);

        return prod;
    }

    final public List<Product> getProducts() {
        return _products;
    }

    final public Product getProductById(int id) {
        for (final Product product : _products) {
            if (product.getId() == id)
                return product;
        }

        return null;
    }

    /*
     * Restaurants
     */
    public void initializeRestaurants(List<Restaurant> restaurants) {
        _restaurants = restaurants;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // API 24+
            Collections.sort(_restaurants, Comparator.comparing(Restaurant::getName));
        } else {
            // For older APIs
            Collections.sort(_restaurants, (res1, res2) -> res1.getName().compareTo(res2.getName()));
        }
    }

    final public List<Restaurant> getRestaurants() {
        return _restaurants;
    }

    final public Restaurant getRestaurantById(int id) {
        for (final Restaurant res : _restaurants) {
            if (res.getId() == id)
                return res;
        }

        return null;
    }

    /*
     * Misc
     */
    public void hideKeyboard(Activity activity) {
        if (activity == null)
            return;

        final InputMethodManager manager = (InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
