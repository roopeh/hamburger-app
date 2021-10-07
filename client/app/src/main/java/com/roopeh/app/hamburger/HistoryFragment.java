package com.roopeh.app.hamburger;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import androidx.fragment.app.Fragment;

public class HistoryFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        final RelativeLayout layout = rootView.findViewById(R.id.historyEmptyLayout);
        final ListView historyList = rootView.findViewById(R.id.historyList);

        final User user = Helper.getInstance().getUser();
        // Check if user has previous purchases
        if (user == null || user.getAllOrders().isEmpty()) {
            layout.setVisibility(View.VISIBLE);
            return rootView;
        }

        historyList.setVisibility(View.VISIBLE);

        final HistoryListAdapter adapter = new HistoryListAdapter(getContext(), user.getAllOrders());
        historyList.setAdapter(adapter);

        historyList.setOnItemClickListener((parent, view, position, id) -> {
            final Order order = (Order)parent.getItemAtPosition(position);
            Objects.requireNonNull((MainActivity)getActivity()).loadFragment(new HistoryInfoFragment(order), false);
        });

        return rootView;
    }
}

class HistoryListAdapter extends BaseAdapter {
    final private Context _context;
    final private List<Order> _list;

    public HistoryListAdapter(Context context, List<Order> orders) {
        _context = context;
        _list = orders;
    }

    @Override
    public int getCount() {
        return _list.size();
    }

    @Override
    public Order getItem(int position) {
        return _list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = View.inflate(_context, R.layout.history_list_item, null);
        final Order order = _list.get(position);

        final TextView name = view.findViewById(R.id.historyListRestaurant);
        name.setText(order.getRestaurant().getName());

        final TextView date = view.findViewById(R.id.historyListDate);
        final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        date.setText(format.format(order.getOrderDate() * 1000));

        final TextView products = view.findViewById(R.id.historyListProducts);
        StringBuilder productsString = new StringBuilder();
        for (int i = 0; i < order.getItems().size(); ++i) {
            ShoppingItem item = order.getItems().get(i);
            productsString.append("- ").append(item.getProduct().getName());
            // No new line at final row
            if (i < (order.getItems().size() - 1))
                productsString.append("\n");
        }
        products.setText(productsString.toString());

        final TextView price = view.findViewById(R.id.historyListPrice);
        price.setText(String.format(Locale.getDefault(), "%.2f", order.getTotalPrice()) + " €");

        return view;
    }
}
