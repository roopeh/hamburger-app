package com.roopeh.app.hamburger;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
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

public class CartFragment extends Fragment {
    private Spinner couponSpinner;
    private TextView listedPrice;
    private TextView totalSum;
    private Button orderButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_cart, container, false);

        final ScrollView emptyView = rootView.findViewById(R.id.shopCartEmptyView);
        final ScrollView defaultView = rootView.findViewById(R.id.shopCartDefaultView);

        final ImageButton backButton = rootView.findViewById(R.id.shopCartBackButton);
        backButton.setOnClickListener(v -> Objects.requireNonNull((MainActivity)getActivity()).returnToPreviousFragment(false));

        final User user = Helper.getInstance().getUser();
        // Load correct layout
        if (user.getCart().isCartEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            // No need to proceed
            return rootView;
        }

        defaultView.setVisibility(View.VISIBLE);

        final ListView shoppingItems = rootView.findViewById(R.id.shopCartProducts);
        couponSpinner = rootView.findViewById(R.id.shopCartCoupons);
        final Spinner restaurantSpinner = rootView.findViewById(R.id.shopCartRestaurant);
        listedPrice = rootView.findViewById(R.id.shopCartPriceListed);
        totalSum = rootView.findViewById(R.id.shopCartPriceSum);
        orderButton = rootView.findViewById(R.id.shopCartPaymentButton);

        // Products
        final ProductsListAdapter shoppingAdapter = new ProductsListAdapter(getContext(), user, this);
        shoppingItems.setAdapter(shoppingAdapter);
        // Need to calculate height manually for listview because it is in a scrollview
        calculateHeightForList(shoppingItems);

        // Coupons
        populateCoupons(couponSpinner);
        couponSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                populatePrice();
                populateFinalPrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

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
            Order order = new Order(user.getCart().getItems());
            order.setRestaurant((Restaurant)restaurantSpinner.getSelectedItem());
            order.setPrices(getSum(), getDiscount(), getSum() - getDiscount());

            // If payment is done at restaurant, skip payment page and finalize order
            if (paymentRestaurant.isChecked()) {
                order.setDate(System.currentTimeMillis() / 1000);

                // Remove used coupon
                final Coupon coupon = (Coupon)couponSpinner.getSelectedItem();
                if (coupon.getType() != Coupon.TYPE_EMPTY_COUPON)
                    user.removeCoupon(coupon);

                // Clear shopping items
                user.getCart().emptyCart();

                user.setCurrentOrder(order);
                Toast.makeText(getContext(), "Tilaus onnistui!", Toast.LENGTH_LONG).show();
                Objects.requireNonNull((MainActivity)getActivity()).loadFragment(new HomeFragment(), false);
            } else if (paymentCard.isChecked()) {
                Objects.requireNonNull((MainActivity)getActivity()).loadFragment(new PaymentFragment(order, (Coupon)couponSpinner.getSelectedItem()), false);
            }
        });

        return rootView;
    }

    private void populateCoupons(Spinner couponSpinner) {
        List<Coupon> coupons = new ArrayList<>();
        coupons.add(new Coupon(Coupon.TYPE_EMPTY_COUPON, 0));

        // Check for available coupons
        for (final Coupon coupon : Helper.getInstance().getUser().getCoupons()) {
            // Check if the coupon can be used for any item in the shopping cart
            switch (coupon.getType()) {
                case Coupon.TYPE_FREE_LARGE_DRINK: {
                    for (final ShoppingItem item : Helper.getInstance().getUser().getCart().getItems()) {
                        if (!item.getProduct().isMeal())
                            continue;

                        if (item.getMealDrink() == 0)
                            continue;

                        if (item.isLargeDrink()) {
                            coupons.add(coupon);
                            break;
                        }
                    }
                } break;
                case Coupon.TYPE_FREE_LARGE_EXTRAS: {
                    for (final ShoppingItem item : Helper.getInstance().getUser().getCart().getItems()) {
                        if (!item.getProduct().isMeal())
                            continue;

                        if (item.getMealExtra() == 0)
                            continue;

                        if (item.isLargeExtra()) {
                            coupons.add(coupon);
                            break;
                        }
                    }
                } break;
                case Coupon.TYPE_50_OFF: {
                    coupons.add(coupon);
                } break;
                default: break;
            }
        }

        final CouponAdapter couponAdapter = new CouponAdapter(getContext(), android.R.layout.simple_spinner_item, coupons);
        couponAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        couponSpinner.setAdapter(couponAdapter);
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
            restaurants.add(new Restaurant("Ei avoinna olevia ravintoloita"));

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
        switch (coupon.getType()) {
            case Coupon.TYPE_FREE_LARGE_DRINK:
                return 0.5;
            case Coupon.TYPE_FREE_LARGE_EXTRAS:
                return 0.4;
            case Coupon.TYPE_50_OFF:
                return getSum() / 2;
            default:
                return 0.0;
        }
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

    private void calculateHeightForList(ListView list) {
        final ListAdapter listAdapter = list.getAdapter();
        int totalHeight = 0;
        for (int size = 0; size < listAdapter.getCount(); ++size) {
            final View listItem = listAdapter.getView(size, null, list);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        final ViewGroup.LayoutParams params = list.getLayoutParams();
        params.height = totalHeight + (list.getDividerHeight() * (listAdapter.getCount() - 1));
        list.setLayoutParams(params);
    }
}

class ProductsListAdapter extends BaseAdapter {
    final private Context _context;
    final private List<ShoppingItem> _content;
    final private User _user;
    final private CartFragment _frag;

    public ProductsListAdapter(Context context, User user, CartFragment frag) {
        _context = context;
        _content = user.getCart().getItems();
        _user = user;
        _frag = frag;
    }

    @Override
    public int getCount() {
        return _content.size();
    }

    @Override
    public Object getItem(int position) {
        return _content.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = View.inflate(_context, R.layout.shop_cart_list_item, null);
        ShoppingItem item = _content.get(position);

        final TextView name = view.findViewById(R.id.shopCartItemName);
        name.setText("- " + item.getProduct().getName());

        final TextView price = view.findViewById(R.id.shopCartItemPrice);
        price.setText(String.format(Locale.getDefault(), "%.2f", item.getPrice()) + " €");

        final TextView extraInfo = view.findViewById(R.id.shopCartItemExtra);
        if (item.getProduct().isMeal()) {
            String extra = "";
            if (item.getMealDrink() > 0) {
                extra += _context.getResources().getStringArray(R.array.stringMealDrinks)[item.getMealDrink()];
                if (item.isLargeDrink())
                    extra += " (Large)";

                extra += "\n";
            }

            if (item.getMealExtra() > 0) {
                extra += _context.getResources().getStringArray(R.array.stringMealExtras)[item.getMealExtra()];
                if (item.isLargeExtra())
                    extra += " (Large)";
            }

            extraInfo.setVisibility(View.VISIBLE);
            extraInfo.setText(extra);
        }

        final ImageButton removeButton = view.findViewById(R.id.shopCartItemRemove);
        removeButton.setOnClickListener(v -> {
            _user.getCart().removeFromCart(item);
            // Close shopping cart if it's empty now
            if (_user.getCart().isCartEmpty()) {
                Objects.requireNonNull((MainActivity)_frag.getActivity()).returnToPreviousFragment(false);
                return;
            }

            notifyDataSetChanged();
            _frag.populatePrice();
            _frag.populateFinalPrice();
        });

        return view;
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
