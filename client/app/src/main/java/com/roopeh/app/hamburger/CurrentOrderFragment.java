package com.roopeh.app.hamburger;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Objects;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CurrentOrderFragment extends Fragment {
    private CountDownTimer _orderTimer = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_current_order, container, false);

        final User user = Helper.getInstance().getUser();
        // It's possible to end up here without current order when navigating with back button
        // so let's avoid crashes
        if (user == null || user.getCurrentOrder() == null) {
            Objects.requireNonNull((MainActivity)getActivity()).returnToPreviousFragment(false);
            return null;
        }

        final ImageButton returnButton = rootView.findViewById(R.id.currentOrderBackButton);
        final RecyclerView productsList = rootView.findViewById(R.id.currentOrderProducts);
        final TextView paymentStatus = rootView.findViewById(R.id.currentOrderPaymentStatus);
        final TextView orderStatus = rootView.findViewById(R.id.currentOrderStatus);
        final TextView restaurantAddress = rootView.findViewById(R.id.currentOrderRestaurantAddress);
        final TextView restaurantHours = rootView.findViewById(R.id.currentOrderRestaurantHours);
        final Button restaurantButton = rootView.findViewById(R.id.currentOrderRestaurantButton);
        final Order currentOrder = user.getCurrentOrder();

        // Could be possible to happen
        // i.e. if using back button to return to fragment
        if (currentOrder.isOrderReady()) {
            user.setCurrentOrder(null);
            Objects.requireNonNull((MainActivity)getActivity()).returnToPreviousFragment(false);
            return null;
        }

        returnButton.setOnClickListener(v -> Objects.requireNonNull((MainActivity)getActivity()).returnToPreviousFragment(false));

        // Products
        final ProductsListAdapter productsAdapter = new ProductsListAdapter(getContext(), currentOrder);
        productsList.setLayoutManager(new LinearLayoutManager(getContext()));
        productsList.addItemDecoration(new RecyclerViewDivider(Helper.Constants.LIST_DIVIDER));
        productsList.setAdapter(productsAdapter);

        // Payment status
        if (currentOrder.isPaid())
            paymentStatus.setText(R.string.orderPaymentPaid);
        else
            paymentStatus.setText(R.string.orderPaymentRestaurant);

        // Order status
        // Create a local countdown timer to update pickup time
        final long timeDiff = currentOrder.getPickupDate() - (System.currentTimeMillis() / 1000);
        _orderTimer = new CountDownTimer(timeDiff * 1000, 10 * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Round minutes with explicit type cast
                final int minUntilFinished = (int)(millisUntilFinished / 1000 / 60);
                orderStatus.setText(getString(R.string.orderTimer, minUntilFinished));
            }

            @Override
            public void onFinish() {
                // Notification is created in the real timer in MainActivity
                // so just return to previous fragment
                Objects.requireNonNull((MainActivity)getActivity()).returnToPreviousFragment(false);
            }
        }.start();

        // Restaurant address
        final Restaurant restaurant = currentOrder.getRestaurant();
        restaurantAddress.setText(getString(R.string.restaurantLocation, restaurant.getName(), restaurant.getAddress()));

        // Restaurant shop hours
        final RestaurantDates dates = restaurant.getDates();
        final Calendar now = Calendar.getInstance();
        final String restaurantString =
                getString(R.string.restaurantOpenToday) + " " +
                getString(R.string.restaurantHours, dates.getStartHoursForDay(now.get(Calendar.DAY_OF_WEEK))) +
                " - " + getString(R.string.restaurantHours, dates.getEndHoursForDay(now.get(Calendar.DAY_OF_WEEK)));
        restaurantHours.setText(restaurantString);

        restaurantButton.setOnClickListener(v -> Objects.requireNonNull((MainActivity)getActivity()).loadFragment(new RestaurantInfoFragment(restaurant), false));

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (_orderTimer != null) {
            _orderTimer.cancel();
            _orderTimer = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (_orderTimer != null) {
            _orderTimer.cancel();
            _orderTimer = null;
        }
    }
}
