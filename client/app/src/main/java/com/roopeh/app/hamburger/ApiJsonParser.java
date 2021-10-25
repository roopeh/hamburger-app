package com.roopeh.app.hamburger;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ApiJsonParser {
    public static void parseDatabaseData(Context context, Helper.ApiResponseType apiResponse, Bundle bundle) {
        final String response = bundle.getString("result");

        final boolean hasErrorInGetResponse = response == null || response.isEmpty() || !response.equals("true");
        final boolean hasErrorInPostResponse = response == null || response.isEmpty() || response.equals("error");

        switch (apiResponse) {
            /*
             * RESTAURANTS
             */
            case RESTAURANTS: {
                if (hasErrorInGetResponse) {
                    Toast.makeText(context, R.string.apiRestaurantError, Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    // Parse raw restaurants JSON data
                    final JSONArray restaurantData = new JSONArray(bundle.getString("restaurants-json"));
                    final List<Restaurant> restaurants = new ArrayList<>();
                    for (int i = 0; i < restaurantData.length(); ++i) {
                        final JSONObject resObj = restaurantData.getJSONObject(i);
                        final Restaurant res = new Restaurant(Integer.parseInt(resObj.getString("id")), resObj.getString("name"));
                        res.setLocation(resObj.getString("address"), resObj.getString("city"));
                        res.setPhoneNumber(resObj.getString("phone_number"));

                        // Set opening hours
                        String dates = resObj.getString("dates_sun");
                        res.setHours(Calendar.SUNDAY, dates.substring(0, 2), dates.substring(2));
                        dates = resObj.getString("dates_mon");
                        res.setHours(Calendar.MONDAY, dates.substring(0, 2), dates.substring(2));
                        dates = resObj.getString("dates_tue");
                        res.setHours(Calendar.TUESDAY, dates.substring(0, 2), dates.substring(2));
                        dates = resObj.getString("dates_wed");
                        res.setHours(Calendar.WEDNESDAY, dates.substring(0, 2), dates.substring(2));
                        dates = resObj.getString("dates_thu");
                        res.setHours(Calendar.THURSDAY, dates.substring(0, 2), dates.substring(2));
                        dates = resObj.getString("dates_fri");
                        res.setHours(Calendar.FRIDAY, dates.substring(0, 2), dates.substring(2));
                        dates = resObj.getString("dates_sat");
                        res.setHours(Calendar.SATURDAY, dates.substring(0, 2), dates.substring(2));

                        restaurants.add(res);
                    }

                    Helper.getInstance().initializeRestaurants(restaurants);
                } catch (JSONException e) {
                    Toast.makeText(context, R.string.apiRestaurantError, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } break;
            /*
             * PRODUCTS
             */
            case PRODUCTS: {
                if (hasErrorInGetResponse) {
                    Toast.makeText(context, R.string.apiProductsError, Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    // Parse raw products JSON data
                    final JSONArray productData = new JSONArray(bundle.getString("products-json"));
                    final List<Product> products = new ArrayList<>();
                    for (int i = 0; i < productData.length(); ++i) {
                        final JSONObject productObj = productData.getJSONObject(i);
                        final int type = productObj.getInt("type");
                        final Product product = type == Helper.Constants.PRODUCT_CATEGORY_MEAL ?
                                new Meal(productObj.getInt("id"), productObj.getString("name")) :
                                new Product(productObj.getInt("id"), productObj.getString("name"));

                        product.setPrice(productObj.getDouble("price"));
                        product.setKoostumus(productObj.getString("koostumus"));
                        product.setRavinto(productObj.getString("ravinto"));
                        products.add(product);
                    }

                    Helper.getInstance().initializeProducts(products);
                } catch (JSONException e) {
                    Toast.makeText(context, R.string.apiProductsError, Toast.LENGTH_SHORT).show();
                }

            } break;
            /*
             * REGISTER
             */
            case REGISTER: {
                if (hasErrorInPostResponse) {
                    // API error
                    Toast.makeText(context, R.string.apiError, Toast.LENGTH_SHORT).show();
                    return;
                } else if (response.equals("false")) {
                    final int toastStringId;
                    switch (Integer.parseInt(bundle.getString("status"))) {
                        // Duplicate username
                        case 1:
                            toastStringId = R.string.apiRegisterDuplicateUser;
                            break;
                        // Duplicate email address
                        case 2:
                            toastStringId = R.string.apiRegisterDuplicateEmail;
                            break;
                        // Duplicate phone number
                        case 3:
                            toastStringId = R.string.apiRegisterDuplicatePhone;
                            break;
                        // Database error
                        default:
                            toastStringId = R.string.apiDatabaseError;
                            Log.d("DEBUG_TAG", "Database error: " + bundle.getString("error_text"));
                            break;
                    }

                    Toast.makeText(context, toastStringId, Toast.LENGTH_SHORT).show();
                    return;
                }

                // No need to do anything else here
            } break;
            /*
             * LOGIN
             */
            case LOGIN: {
                if (hasErrorInPostResponse) {
                    Toast.makeText(context, R.string.apiError, Toast.LENGTH_SHORT).show();
                    return;
                } else if (response.equals("false")) {
                    Toast.makeText(context, R.string.apiLoginWrongInfo, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Parse user info
                final int id = Integer.parseInt(bundle.getString("id"));
                final User user = new User(id, bundle.getString("username"));
                user.setFirstLogin(Integer.parseInt(bundle.getString("first_login")) != 0);
                user.setFirstName(bundle.getString("first_name"));
                user.setLastName(bundle.getString("last_name"));
                user.setEmail(bundle.getString("email"));
                user.setPhoneNumber(bundle.getString("phone_number"));

                try {
                    if (bundle.containsKey("coupons-json")) {
                        final long dateNow = System.currentTimeMillis() / 1000;

                        // Parse raw coupon JSON data
                        final JSONArray couponData = new JSONArray(bundle.getString("coupons-json"));
                        for (int i = 0; i < couponData.length(); ++i) {
                            final JSONObject couponObj = couponData.getJSONObject(i);

                            final Coupon coupon = new Coupon(couponObj.getInt("coupon_type"), couponObj.getLong("expiry_date"));
                            // Check if the coupon has expired
                            if (coupon.isValidAnymore(dateNow))
                                continue;

                            user.addCoupon(coupon);
                        }
                    }

                    final Map<Integer, List<ShoppingItem>> orderItemList = new HashMap<>();
                    if (bundle.containsKey("order-items-json")) {
                        // Parse raw order items JSON data
                        // and save them to a Map
                        final JSONArray orderItemData = new JSONArray(bundle.getString("order-items-json"));
                        for (int i = 0; i < orderItemData.length(); ++i) {
                            final JSONObject itemObj = orderItemData.getJSONObject(i);

                            final int orderId = itemObj.getInt("order_id");
                            if (orderItemList.get(orderId) == null)
                                orderItemList.put(orderId, new ArrayList<>());

                            // Create ShoppingItem here and save ShoppingItems to a Map
                            // so it can be placed into Order next
                            final Product product = Helper.getInstance().getProductById(itemObj.getInt("product_id"));
                            if (product == null)
                                continue;

                            final ShoppingItem item = new ShoppingItem(product);
                            item.setPrice(itemObj.getDouble("price"));
                            if (product.isMeal()) {
                                item.setMealDrink(itemObj.getInt("meal_drink"), itemObj.getInt("large_drink") != 0);
                                item.setMealExtra(itemObj.getInt("meal_extra"), itemObj.getInt("large_extra") != 0);
                            }
                            Objects.requireNonNull(orderItemList.get(orderId)).add(item);
                        }
                    }

                    if (bundle.containsKey("orders-json")) {
                        // Parse raw order JSON data
                        final JSONArray orderData = new JSONArray(bundle.getString("orders-json"));
                        for (int i = 0; i < orderData.length(); ++i) {
                            final JSONObject orderObj = orderData.getJSONObject(i);
                            final int orderId = orderObj.getInt("id");

                            final Restaurant res = Helper.getInstance().getRestaurantById(Integer.parseInt(orderObj.getString("restaurant_id")));
                            if (res == null)
                                continue;

                            final Order order = new Order(orderId, orderItemList.get(orderId));
                            order.setRestaurant(res);
                            order.setOrderDate(orderObj.getLong("order_date"));
                            order.setPickupDate(orderObj.getLong("pickup_date"));
                            order.setPaid(orderObj.getInt("paid_status") != 0);
                            order.setPrices(orderObj.getDouble("original_price"), orderObj.getDouble("discount_price"), orderObj.getDouble("total_price"));
                            user.addOrder(order);
                        }
                    }

                    // Last but not least; sort orders from newest to oldest
                    user.sortOrders();

                    // All done
                    Toast.makeText(context, context.getString(R.string.apiLoginHello, user.getFirstName()), Toast.LENGTH_SHORT).show();
                    Helper.getInstance().setUser(user);
                } catch (JSONException e) {
                    Toast.makeText(context, R.string.apiError, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } break;
            /*
             * SAVE ORDER, LOGOUT
             */
            case SAVE_ORDER:
            case LOGOUT: {
                if (hasErrorInPostResponse) {
                    // API error
                    Toast.makeText(context, R.string.apiError, Toast.LENGTH_SHORT).show();
                    return;
                } else if (response.equals("false")) {
                    // Database error
                    Toast.makeText(context, R.string.apiDatabaseError, Toast.LENGTH_SHORT).show();
                    Log.d("DEBUG_TAG", "Database error: " + bundle.getString("error_text"));
                    return;
                }

                // No need to do anything else here
            } break;
            default: break;
        }
    }
}
