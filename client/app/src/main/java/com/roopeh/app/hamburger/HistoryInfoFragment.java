package com.roopeh.app.hamburger;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class HistoryInfoFragment extends Fragment {
    final private Order _order;

    public HistoryInfoFragment(Order order) {
        _order = order;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_history_info, container, false);

        final ImageButton returnButton = rootView.findViewById(R.id.historyInfoBackButton);
        final TextView restaurantName = rootView.findViewById(R.id.historyInfoRestaurant);
        final TextView restaurantAddress = rootView.findViewById(R.id.historyInfoAddress);
        final TextView restaurantPhone = rootView.findViewById(R.id.historyInfoPhone);
        final TextView orderDate = rootView.findViewById(R.id.historyInfoDate);
        final ListView products = rootView.findViewById(R.id.historyInfoProducts);
        final TextView priceListed = rootView.findViewById(R.id.historyInfoPriceListed);
        final TextView priceTotal = rootView.findViewById(R.id.historyInfoPriceSum);

        returnButton.setOnClickListener(v -> Objects.requireNonNull((MainActivity)getActivity()).returnToPreviousFragment(false));

        // Restaurant name
        restaurantName.setText(_order.getRestaurant().getName());

        // Restaurant address
        restaurantAddress.setText("Ravintolan osoite: " + _order.getRestaurant().getLocationString());

        // Restaurant phone number
        restaurantPhone.setText("Ravintolan puh. nro: " + _order.getRestaurant().getPhoneNumber());

        // Order date
        final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        orderDate.setText("Tilaus tehty: " + format.format(_order.getOrderDate() * 1000));

        // Products
        final ProductsListAdapter adapter = new ProductsListAdapter(getContext(), _order);
        products.setAdapter(adapter);
        // Need to calculate height manually for listview because it is in a scrollview
        calculateHeightForList(products);

        // Price
        String priceStr = "Välisumma " + String.format(Locale.getDefault(), "%.2f", _order.getOriginalPrice()) + " €";
        if (_order.getDiscountPrice() > 0) {
            priceStr += "\n";
            priceStr += "Alennuskuponki -" + String.format(Locale.getDefault(), "%.2f", _order.getDiscountPrice()) + " €";
        }
        priceListed.setText(priceStr);

        // Total price
        priceTotal.setText(String.format(Locale.getDefault(), "%.2f", _order.getTotalPrice()) + " €");

        return rootView;
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
