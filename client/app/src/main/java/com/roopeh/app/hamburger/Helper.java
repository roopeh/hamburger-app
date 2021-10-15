package com.roopeh.app.hamburger;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

// Singleton helper class
public class Helper {
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
        final public static int COUPON_TYPE_EMPTY_COUPON = 10;
    }

    public enum ApiResponseType {
        LOGIN
    }

    private static Helper mInstance = null;

    private User _user = null;
    private List<Restaurant> _restaurants = null;
    private List<Product> _products = null;

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

        AlertDialog dialog = builder.create();
        dialog.show();

        TextView msgView = dialog.findViewById(android.R.id.message);
        msgView.setGravity(Gravity.CENTER);

        return dialog;
    }

    /*
     * User
     */
    public void setUser(User user) {
        if (_user != null)
            return;

        _user = user;

        // todo: dummy content (coupons)
        for (int i = 1; i <= 10; ++i) {
            int num = i;
            while (num > 3) {
                num -= 3;
            }
            _user.addCoupon(new Coupon(num, 1650995521));
        }
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
    public void initializeProducts() {
        _products = new ArrayList<>();

        // todo: dummy content
        for (int i = 0; i < 3; ++i) {
            _products.add(generateTestProduct("Juustoateria", true));
            _products.add(generateTestProduct("Pekoniateria", true));
            _products.add(generateTestProduct("Muna-ateria", true));
            _products.add(generateTestProduct("Juustohampurilainen", false));
            _products.add(generateTestProduct("Pekonihampurilainen", false));
            _products.add(generateTestProduct("Munahampurilainen", false));
        }
    }

    // Generates test products with random price
    private Product generateTestProduct(String name, boolean meal) {
        Product prod = meal ? new Meal(name) : new Product(name);

        final int rand = new Random().nextInt(20) + 1;
        final double price = new Random().nextBoolean() ? rand : rand + (0.25f * (new Random().nextInt(3) + 1));
        prod.setPrice(price);

        return prod;
    }

    final public List<Product> getProducts() {
        return _products;
    }

    /*
     * Restaurants
     */
    public void initializeRestaurants() {
        _restaurants = new ArrayList<>();

        // todo: dummy content
        Restaurant res2 = new Restaurant("Restaurant 24h");
        res2.setLocation("Kotkantie 2", "Oulu");
        res2.setHours(Calendar.MONDAY,  "00", "24");
        res2.setHours(Calendar.TUESDAY, "00", "24");
        res2.setHours(Calendar.WEDNESDAY, "00", "24");
        res2.setHours(Calendar.THURSDAY, "00", "24");
        res2.setHours(Calendar.FRIDAY, "00", "24");
        res2.setHours(Calendar.SATURDAY, "00", "24");
        res2.setHours(Calendar.SUNDAY, "00", "24");
        res2.setPhoneNumber("040-1234567");
        _restaurants.add(res2);

        for (int i = 1; i < 20; ++i) {
            Restaurant res = new Restaurant("Restaurant " + i);
            res.setLocation("Kotkantie 2", "Oulu");

            res.setHours(Calendar.TUESDAY, "08", "10");
            res.setHours(Calendar.WEDNESDAY, "11", "13");
            res.setHours(Calendar.THURSDAY, "14", "16");
            res.setHours(Calendar.FRIDAY, "16", "18");
            res.setHours(Calendar.SATURDAY, "20", "23");
            res.setHours(Calendar.SUNDAY, "10", "14");
            res.setPhoneNumber("040-1234567");

            _restaurants.add(res);
        }
    }

    final public List<Restaurant> getRestaurants() {
        return _restaurants;
    }
}
