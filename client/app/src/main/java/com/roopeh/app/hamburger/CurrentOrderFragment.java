package com.roopeh.app.hamburger;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CurrentOrderFragment extends Fragment {
    private CountDownTimer _orderTimer = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_current_order, container, false);

        final User user = Helper.getInstance().getUser();
        // It's possible to end up here without current order when navigating with back button
        // so let's avoid crashes
        if (user == null || user.getCurrentOrder() == null) {
            handleReturnInBackgroundThread();
            return null;
        }

        final ImageButton returnButton = rootView.findViewById(R.id.currentOrderBackButton);
        final ListView productsList = rootView.findViewById(R.id.currentOrderProducts);
        final TextView paymentStatus = rootView.findViewById(R.id.currentOrderPaymentStatus);
        final TextView orderStatus = rootView.findViewById(R.id.currentOrderStatus);
        final TextView restaurantInfo = rootView.findViewById(R.id.currentOrderRestaurantInfo);
        final Button restaurantButton = rootView.findViewById(R.id.currentOrderRestaurantButton);
        final Order currentOrder = user.getCurrentOrder();

        // Could be possible to happen
        // i.e. if using back button to return to fragment
        if (currentOrder.isOrderReady()) {
            user.setCurrentOrder(null);
            handleReturnInBackgroundThread();
            return null;
        }

        returnButton.setOnClickListener(v -> Objects.requireNonNull((MainActivity)getActivity()).returnToPreviousFragment(false));

        // Products
        final OrderProductsListAdapter productsAdapter = new OrderProductsListAdapter(getContext(), currentOrder);
        productsList.setAdapter(productsAdapter);
        // Need to calculate height manually for listview because it is in a scrollview
        calculateHeightForList(productsList);

        // Payment status
        if (currentOrder.isPaid())
            paymentStatus.setText("Tilaus maksettu");
        else
            paymentStatus.setText("Tilaus maksetaan ravintolassa");

        // Order status
        // Create a local countdown timer to update pickup time
        final long timeDiff = currentOrder.getPickupDate() - (System.currentTimeMillis() / 1000);
        _orderTimer = new CountDownTimer(timeDiff * 1000, 10 * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Round minutes with explicit type cast
                final int minUntilFinished = (int)(millisUntilFinished / 1000 / 60);
                orderStatus.setText(minUntilFinished + " min");
            }

            @Override
            public void onFinish() {
                // Notification is created in the real timer in MainActivity
                // so just return to previous fragment
                Objects.requireNonNull((MainActivity)getActivity()).returnToPreviousFragment(false);
            }
        }.start();

        // Restaurant info
        final Restaurant restaurant = currentOrder.getRestaurant();
        final RestaurantDates dates = restaurant.getDates();
        final Calendar now = Calendar.getInstance();
        final String restaurantString =
                restaurant.getName() + ", " + restaurant.getAddress() + "\n" +
                "Auki tänään: " + String.format(Locale.getDefault(), "%02d", dates.getStartHoursForDay(now.get(Calendar.DAY_OF_WEEK))) +
                " - " + String.format(Locale.getDefault(), "%02d", dates.getEndHoursForDay(now.get(Calendar.DAY_OF_WEEK)));
        restaurantInfo.setText(restaurantString);

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

    private void handleReturnInBackgroundThread() {
        // Return must be handled in background thread to prevent a crash:
        // java.lang.IllegalStateException: FragmentManager is already executing transactions
        Handler handler = new Handler();
        handler.post(() -> Objects.requireNonNull((MainActivity)getActivity()).returnToPreviousFragment(false));
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

class OrderProductsListAdapter extends BaseAdapter {
    final private Context _context;
    final private List<ShoppingItem> _content;

    public OrderProductsListAdapter(Context context, Order order) {
        _context = context;
        _content = order.getItems();
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getCount() {
        return _content.size();
    }

    @Override
    public ShoppingItem getItem(int position) {
        return _content.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = View.inflate(_context, R.layout.order_list_item, null);
        final ShoppingItem item = getItem(position);

        final TextView name = view.findViewById(R.id.orderItemName);
        name.setText("- " + item.getProduct().getName());

        final TextView price = view.findViewById(R.id.orderItemPrice);
        price.setText(String.format(Locale.getDefault(), "%.2f", item.getPrice()) + " €");

        final TextView extraInfo = view.findViewById(R.id.orderItemExtra);
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

        return view;
    }
}
