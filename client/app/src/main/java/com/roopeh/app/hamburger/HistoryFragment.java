package com.roopeh.app.hamburger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        final RelativeLayout layout = rootView.findViewById(R.id.historyEmptyLayout);
        final RecyclerView historyList = rootView.findViewById(R.id.historyList);

        final User user = Helper.getInstance().getUser();
        // Check if user has previous purchases
        if (user == null || user.getAllOrders().isEmpty()) {
            layout.setVisibility(View.VISIBLE);
            return rootView;
        }

        historyList.setVisibility(View.VISIBLE);

        // TODO: new orders should be on top, not in bottom

        final HistoryListAdapter adapter = new HistoryListAdapter(user.getAllOrders(), this);
        historyList.setLayoutManager(new LinearLayoutManager(getContext()));
        historyList.addItemDecoration(new RecyclerViewDivider(Helper.Constants.GRID_DIVIDER));
        historyList.setAdapter(adapter);

        return rootView;
    }
}

class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {
    final private List<Order> _list;
    final private HistoryFragment _frag;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final private TextView name;
        final private TextView date;
        final private TextView products;
        final private TextView price;

        public ViewHolder(@NonNull View itemView, List<Order> list, HistoryFragment fragment) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                final Order order = list.get(getAdapterPosition());
                Objects.requireNonNull((MainActivity)fragment.getActivity()).loadFragment(new HistoryInfoFragment(order), false);
            });

            name = itemView.findViewById(R.id.historyListRestaurant);
            date = itemView.findViewById(R.id.historyListDate);
            products = itemView.findViewById(R.id.historyListProducts);
            price = itemView.findViewById(R.id.historyListPrice);
        }

        final public TextView getName() {
            return name;
        }

        final public TextView getDate() {
            return date;
        }

        final public TextView getProducts() {
            return products;
        }

        final public TextView getPrice() {
            return price;
        }
    }

    public HistoryListAdapter(List<Order> list, HistoryFragment frag) {
        _list = list;
        _frag = frag;
    }

    @NonNull
    @Override
    public HistoryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View listItem = inflater.inflate(R.layout.history_list_item, parent, false);
        return new ViewHolder(listItem, _list, _frag);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryListAdapter.ViewHolder holder, int position) {
        final Order order = _list.get(position);
        holder.getName().setText(order.getRestaurant().getName());

        final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        holder.getDate().setText(format.format(order.getOrderDate() * 1000));

        final StringBuilder productsString = new StringBuilder();
        for (int i = 0; i < order.getItems().size(); ++i) {
            final ShoppingItem item = order.getItems().get(i);
            productsString.append("- ").append(item.getProduct().getName());
            // No new line at final row
            if (i < (order.getItems().size() - 1))
                productsString.append("\n");
        }
        holder.getProducts().setText(productsString.toString());

        holder.getPrice().setText(String.format(Locale.getDefault(), "%.2f", order.getTotalPrice()) + " €");
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }
}
