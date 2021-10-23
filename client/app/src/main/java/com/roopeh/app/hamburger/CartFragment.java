package com.roopeh.app.hamburger;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CartFragment extends Fragment implements ApiResponseInterface {
    private Spinner couponSpinner;
    private TextView listedPrice;
    private TextView totalSum;
    private Button orderButton;

    private Order _order = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_cart, container, false);

        final ScrollView emptyView = rootView.findViewById(R.id.shopCartEmptyView);
        final ScrollView defaultView = rootView.findViewById(R.id.shopCartDefaultView);

        final ImageButton backButton = rootView.findViewById(R.id.shopCartBackButton);
        backButton.setOnClickListener(v -> Objects.requireNonNull((MainActivity)getActivity()).returnToPreviousFragment(false));

        final User user = Helper.getInstance().getUser();
        // Load correct layout
        if (user == null) {
            Objects.requireNonNull((MainActivity)getActivity()).returnToPreviousFragment(false);
            return null;
        } else if (user.getCart().isCartEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            // No need to proceed
            return rootView;
        }

        defaultView.setVisibility(View.VISIBLE);

        final RecyclerView shoppingItems = rootView.findViewById(R.id.shopCartProducts);
        couponSpinner = rootView.findViewById(R.id.shopCartCoupons);
        final Spinner restaurantSpinner = rootView.findViewById(R.id.shopCartRestaurant);
        listedPrice = rootView.findViewById(R.id.shopCartPriceListed);
        totalSum = rootView.findViewById(R.id.shopCartPriceSum);
        orderButton = rootView.findViewById(R.id.shopCartPaymentButton);

        // Products
        final ProductsListAdapter shoppingAdapter = new ProductsListAdapter(getContext(), user, this);
        shoppingItems.setLayoutManager(new LinearLayoutManager(getContext()));
        shoppingItems.addItemDecoration(new RecyclerViewDivider(Helper.Constants.LIST_DIVIDER));
        shoppingItems.setAdapter(shoppingAdapter);

        // Coupons
        populateCoupons();

        // Restaurants
        populateRestaurants(restaurantSpinner);

        // Payment
        final RadioGroup paymentGroup = rootView.findViewById(R.id.shopCartPaymentChoose);
        final RadioButton paymentCard = rootView.findViewById(R.id.shopCartPaymentCard);
        final RadioButton paymentRestaurant = rootView.findViewById(R.id.shopCartPaymentRestaurant);

        // Order button
        orderButton.setOnClickListener(v -> {
            // If user already has an active order, new order cannot be made
            if (user.getCurrentOrder() != null) {
                Toast.makeText(getContext(), "Sinulla on jo aktiivinen tilaus", Toast.LENGTH_SHORT).show();
                return;
            }

            // Make sure payment method is selected
            if (paymentGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(getContext(), "Valitse maksutapasi", Toast.LENGTH_SHORT).show();
                return;
            }

            // Should not happen
            if (!paymentRestaurant.isChecked() && !paymentCard.isChecked()) {
                Toast.makeText(getContext(), "Virhe maksussa", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create new order
            // Unique id will be created when order is saved to db
            final Order order = new Order(-1, user.getCart().getItems());
            order.setRestaurant((Restaurant)restaurantSpinner.getSelectedItem());
            order.setPrices(getSum(), getDiscount(), getSum() - getDiscount());

            // If payment is done at restaurant, skip payment page and finalize order
            if (paymentRestaurant.isChecked()) {
                order.setOrderDate(System.currentTimeMillis() / 1000);
                order.setPickupDate();
                _order = order;

                // Save to db
                new ApiConnector(this).saveOrder(getContext(), order);
            } else if (paymentCard.isChecked()) {
                Objects.requireNonNull((MainActivity)getActivity()).loadFragment(new PaymentFragment(order, (Coupon)couponSpinner.getSelectedItem()), false);
            }
        });

        return rootView;
    }

    public void populateCoupons() {
        List<Coupon> coupons = new ArrayList<>();
        coupons.add(new Coupon(Helper.Constants.COUPON_TYPE_EMPTY_COUPON, 0));

        // Check for available coupons
        for (final Coupon coupon : Helper.getInstance().getUser().getCoupons()) {
            // Check if the coupon can be used for any item in the shopping cart
            if (Coupon.isCouponAvailableForCart(coupon.getType()))
                coupons.add(coupon);
        }

        final CouponAdapter couponAdapter = new CouponAdapter(getContext(), android.R.layout.simple_spinner_item, coupons);
        couponAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        couponSpinner.setAdapter(couponAdapter);

        couponSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                populatePrice();
                populateFinalPrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void populateRestaurants(Spinner restaurantSpinner) {
        List<Restaurant> restaurants = new ArrayList<>();

        // Check for open restaurants
        for (final Restaurant res : Helper.getInstance().getRestaurants()) {
            if (res.isRestaurantOpen())
                restaurants.add(res);
        }

        // If there is no open restaurants, order cannot be made
        if (restaurants.isEmpty()) {
            restaurants.add(new Restaurant(-1, "Ei avoinna olevia ravintoloita"));

            orderButton.setText("Ei avoinna olevia ravintoloita");
            orderButton.setClickable(false);
        }

        final RestaurantAdapter restaurantAdapter = new RestaurantAdapter(getContext(), android.R.layout.simple_spinner_item, restaurants);
        restaurantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        restaurantSpinner.setAdapter(restaurantAdapter);
    }

    private double getSum() {
        double result = 0.0f;
        for (ShoppingItem item : Helper.getInstance().getUser().getCart().getItems())
            result += item.getPrice();

        return result;
    }

    private double getDiscount() {
        final Coupon coupon = (Coupon)couponSpinner.getSelectedItem();
        return Coupon.getDiscountFromCoupon(coupon.getType(), getSum());
    }

    // TODO: to strings resources
    public void populatePrice() {
        String price = "Välisumma" + " ";
        price += String.format(Locale.getDefault(), "%.2f", getSum()) + " €";

        final double discount = getDiscount();
        if (discount != 0.0) {
            price += "\n" + "Alennuskuponki" + " -";
            price += String.format(Locale.getDefault(), "%.2f", discount) + " €";
        }

        listedPrice.setText(price);
    }

    public void populateFinalPrice() {
        final String price = String.format(Locale.getDefault(), "%.2f", getSum() - getDiscount()) + " €";
        totalSum.setText(price);
    }

    @Override
    public void onResponse(Helper.ApiResponseType apiResponse, Bundle bundle) {
        ApiJsonParser.parseDatabaseData(getContext(), apiResponse, bundle);

        if (bundle.getString("result").equals("true")) {
            final User user = Helper.getInstance().getUser();

            // Remove used coupon
            final Coupon coupon = (Coupon)couponSpinner.getSelectedItem();
            if (coupon.getType() != Helper.Constants.COUPON_TYPE_EMPTY_COUPON)
                user.removeCoupon(coupon);

            // Clear shopping items
            user.getCart().emptyCart();

            user.setCurrentOrder(_order);
            _order = null;

            Toast.makeText(getContext(), "Tilaus onnistui!", Toast.LENGTH_LONG).show();
            Objects.requireNonNull((MainActivity)getActivity()).createOrderTimer();
            Objects.requireNonNull((MainActivity)getActivity()).loadFragment(new HomeFragment(), false);
        }
    }
}

class CouponAdapter extends ArrayAdapter<Coupon> {
    final private List<Coupon> _content;

    public CouponAdapter(Context context, int resource, List<Coupon> content) {
        super(context, resource, content);
        _content = content;
    }

    @Override
    public int getCount() {
        return _content.size();
    }

    @Override
    public Coupon getItem(int position) {
        return _content.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TextView label = (TextView)super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(_content.get(position).getName());

        return label;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final TextView label = (TextView)super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(_content.get(position).getName());

        return label;
    }
}

class RestaurantAdapter extends ArrayAdapter<Restaurant> {
    final private List<Restaurant> _content;

    public RestaurantAdapter(Context context, int resource, List<Restaurant> content) {
        super(context, resource, content);
        _content = content;
    }

    @Override
    public int getCount() {
        return _content.size();
    }

    @Override
    public Restaurant getItem(int position) {
        return _content.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TextView label = (TextView)super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(_content.get(position).getName());

        return label;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final TextView label = (TextView)super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(_content.get(position).getName());

        return label;
    }
}
