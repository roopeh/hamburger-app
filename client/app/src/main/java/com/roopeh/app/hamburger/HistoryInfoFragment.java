package com.roopeh.app.hamburger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryInfoFragment extends Fragment {
    final private Order _order;

    public HistoryInfoFragment(Order order) {
        _order = order;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_history_info, container, false);

        // User could end up here when navigating with back button so make sure user is logged in
        if (Helper.getInstance().getUser() == null) {
            Objects.requireNonNull((MainActivity)getActivity()).returnToPreviousFragment(false);
            return null;
        }

        final ImageButton returnButton = rootView.findViewById(R.id.historyInfoBackButton);
        final TextView restaurantName = rootView.findViewById(R.id.historyInfoRestaurant);
        final TextView restaurantAddress = rootView.findViewById(R.id.historyInfoAddress);
        final TextView restaurantPhone = rootView.findViewById(R.id.historyInfoPhone);
        final TextView orderDate = rootView.findViewById(R.id.historyInfoDate);
        final RecyclerView products = rootView.findViewById(R.id.historyInfoProducts);
        final TextView priceListed = rootView.findViewById(R.id.historyInfoPriceListed);
        final TextView priceTotal = rootView.findViewById(R.id.historyInfoPriceSum);

        returnButton.setOnClickListener(v -> Objects.requireNonNull((MainActivity)getActivity()).returnToPreviousFragment(false));

        // Restaurant name
        restaurantName.setText(_order.getRestaurant().getName());

        // Restaurant address
        restaurantAddress.setText(getString(R.string.orderRestaurantAddress, _order.getRestaurant().getLocationString()));

        // Restaurant phone number
        restaurantPhone.setText(getString(R.string.orderRestaurantPhone, _order.getRestaurant().getPhoneNumber()));

        // Order date
        final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone(Helper.Constants.STRING_TIMEZONE));
        orderDate.setText(getString(R.string.orderCreationDate, format.format(_order.getOrderDate() * 1000)));

        // Products
        final ProductsListAdapter adapter = new ProductsListAdapter(getContext(), _order);
        products.setLayoutManager(new LinearLayoutManager(getContext()));
        products.addItemDecoration(new RecyclerViewDivider(Helper.Constants.LIST_DIVIDER));
        products.setAdapter(adapter);

        // Price
        String priceStr = getString(R.string.orderPriceSubtotal, _order.getOriginalPrice());
        if (_order.getDiscountPrice() > 0) {
            priceStr += "\n" + getString(R.string.orderPriceDiscount, _order.getDiscountPrice());
        }
        priceListed.setText(priceStr);

        // Total price
        priceTotal.setText(getString(R.string.orderEuroAmount, _order.getTotalPrice()));

        return rootView;
    }
}
