package com.roopeh.app.hamburger;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
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

        final ListView products = view.findViewById(R.id.historyListProducts);
        final HistoryListProductsAdapter adapter = new HistoryListProductsAdapter(_context, order.getItems());
        products.setAdapter(adapter);
        // Need to manually calculate height for listview because it is in another listview
        calculateHeightForList(products);

        final TextView price = view.findViewById(R.id.historyListPrice);
        price.setText(String.format(Locale.getDefault(), "%.2f", order.getTotalPrice()) + " €");

        return view;
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

// Adapter for listview inside HistoryListAdapter
class HistoryListProductsAdapter extends BaseAdapter {
    final private Context _context;
    final private List<ShoppingItem> _list;

    public HistoryListProductsAdapter(Context context, List<ShoppingItem> items) {
        _context = context;
        _list = items;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getCount() {
        return _list.size();
    }

    @Override
    public ShoppingItem getItem(int position) {
        return _list.get(position);
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

        // Same layout is used by multiple lists, this list does not need price
        final TextView price = view.findViewById(R.id.orderItemPrice);
        price.setVisibility(View.GONE);

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
