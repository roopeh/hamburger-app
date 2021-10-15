package com.roopeh.app.hamburger;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment implements ApiResponseInterface {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        final TextView registerButton = rootView.findViewById(R.id.loginRegisterLink);
        registerButton.setOnClickListener(v -> Objects.requireNonNull((MainActivity)getActivity()).loadFragment(new RegisterFragment(), false));

        final ImageButton loginButton = rootView.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            if (Helper.getInstance().getUser() != null) {
                Toast.makeText(getContext(), "Olet jo kirjautunut sisään", Toast.LENGTH_SHORT).show();
                return;
            }

            final EditText userField = rootView.findViewById(R.id.loginUsername);
            final EditText passField = rootView.findViewById(R.id.loginPassword);

            final String user = userField.getText().toString().toUpperCase();
            final String pass = passField.getText().toString();

            // TODO: password encryption
            new ApiConnector(this).login(getContext(), user, pass);
        });

        return rootView;
    }

    @Override
    public void onResponse(Helper.ApiResponseType apiResponse, Bundle bundle) {
        onLoginResponse(getContext(), apiResponse, bundle);
    }

    public static void onLoginResponse(Context context, Helper.ApiResponseType apiResponse, Bundle bundle) {
        if (apiResponse != Helper.ApiResponseType.LOGIN)
            return;

        final String response = bundle.getString("result");
        if (response == null || response.isEmpty() || response.equals("error")) {
            Toast.makeText(context, "Error in the API", Toast.LENGTH_SHORT).show();
            return;
        } else if (response.equals("false")) {
            Toast.makeText(context, "Virheellinen käyttäjätunnus tai salasana", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse user info
        final int id = Integer.parseInt(bundle.getString("id"));
        final User user = new User(id, bundle.getString("username"));
        user.setFirstName(bundle.getString("first_name"));
        user.setLastName(bundle.getString("last_name"));
        user.setEmail(bundle.getString("email"));
        user.setPhoneNumber(bundle.getString("phone_number"));

        try {
            // Parse raw coupon JSON data
            final JSONArray couponData = new JSONArray(bundle.getString("coupons-json"));
            for (int i = 0; i < couponData.length(); ++i) {
                final JSONObject couponObj = couponData.getJSONObject(i);
                user.addCoupon(new Coupon(couponObj.getInt("coupon_type"), couponObj.getLong("expiry_date")));
            }

            // Parse raw order items JSON data
            // and save them to a Map
            final JSONArray orderItemData = new JSONArray(bundle.getString("order-items-json"));
            final Map<Integer, List<ShoppingItem>> orderItemList = new HashMap<>();
            for (int i = 0; i < orderItemData.length(); ++i) {
                final JSONObject itemObj = orderItemData.getJSONObject(i);

                final int orderId = itemObj.getInt("order_id");
                if (orderItemList.get(orderId) == null)
                    orderItemList.put(orderId, new ArrayList<>());

                // Create ShoppingItem here and save ShoppingItems to a Map
                // so it can be placed into Order next
                // TODO: fix products here
                final ShoppingItem item = new ShoppingItem(new Product("FIX PRODUCTS"));
                item.setPrice(itemObj.getDouble("price"));
                item.setMealDrink(itemObj.getInt("meal_drink"), Boolean.parseBoolean(itemObj.getString("large_drink")));
                item.setMealExtra(itemObj.getInt("meal_extra"), Boolean.parseBoolean(itemObj.getString("large_extra")));
                Objects.requireNonNull(orderItemList.get(orderId)).add(item);
            }

            // Parse raw order JSON data
            final JSONArray orderData = new JSONArray(bundle.getString("orders-json"));
            for (int i = 0; i < orderData.length(); ++i) {
                final JSONObject orderObj = orderData.getJSONObject(i);
                final int orderId = orderObj.getInt("id");

                final Order order = new Order(orderItemList.get(orderId));
                order.setOrderDate(orderObj.getLong("order_date"));
                order.setPickupDate(orderObj.getLong("pickup_date"));
                // TODO: fix restaurants here
                order.setRestaurant(new Restaurant("FIX RESTAURANTS"));
                order.setPaid(Boolean.parseBoolean(orderObj.getString("paid_status")));
                order.setPrices(orderObj.getDouble("original_price"), orderObj.getDouble("discount_price"), orderObj.getDouble("total_price"));
                user.addOrder(order);
            }

            // All done
            Toast.makeText(context, "Hei, " + user.getFirstName() + "!", Toast.LENGTH_SHORT).show();
            Helper.getInstance().setUser(user);
        } catch (JSONException e) {
            Toast.makeText(context, "Error in the API", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
