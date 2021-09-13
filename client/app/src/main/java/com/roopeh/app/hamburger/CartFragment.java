package com.roopeh.app.hamburger;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import androidx.fragment.app.Fragment;

public class CartFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);

        ScrollView emptyView = rootView.findViewById(R.id.shopCartEmptyView);
        ScrollView defaultView = rootView.findViewById(R.id.shopCartDefaultView);

        final User user = Helper.getInstance().getUser();
        // Load correct layout
        if (user.getCart().isCartEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            defaultView.setVisibility(View.VISIBLE);
        }

        ListView shoppingItems = rootView.findViewById(R.id.shopCartProducts);
        Spinner couponSpinner = rootView.findViewById(R.id.shopCartCoupons);
        Spinner restaurantSpinner = rootView.findViewById(R.id.shopCartRestaurant);

        // todo: Dummy content
        String[] tests2 = {
                "Foo", "Bar"
        };
        ArrayAdapter<String> couponAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, tests2);
        couponAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        couponSpinner.setAdapter(couponAdapter);

        // todo: Dummy content
        String[] tests3 = {
                "Foo1", "Bar1"
        };
        ArrayAdapter<String> restaurantAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, tests3);
        restaurantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        restaurantSpinner.setAdapter(restaurantAdapter);

        ProductsListAdapter shoppingAdapter = new ProductsListAdapter(getContext(), user);
        shoppingItems.setAdapter(shoppingAdapter);
        // Need to calculate height manually for listview because it is in a scrollview
        calculateHeightForList(shoppingItems);

        ImageButton backButton = rootView.findViewById(R.id.shopCartBackButton);
        backButton.setOnClickListener(v -> Objects.requireNonNull((MainActivity)getActivity()).returnToPreviousFragment(false));

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

class ProductsListAdapter extends BaseAdapter {
    final private Context _context;
    final private List<ShoppingItem> _content;
    final private User _user;

    public ProductsListAdapter(Context context, User user) {
        _context = context;
        _content = user.getCart().getItems();
        _user = user;
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
        View view = View.inflate(_context, R.layout.shop_cart_list_item, null);
        ShoppingItem item = _content.get(position);

        TextView name = view.findViewById(R.id.shopCartItemName);
        name.setText("- " + item.getProduct().getName());

        TextView price = view.findViewById(R.id.shopCartItemPrice);
        price.setText(String.valueOf(item.getProduct().getPrice()) + " €");

        TextView extraInfo = view.findViewById(R.id.shopCartItemExtra);
        if (item.getProduct().isMeal()) {
            extraInfo.setVisibility(View.VISIBLE);
            // TODO: if meal
            //extraInfo.setText("- TEST");
        }

        ImageButton removeButton = view.findViewById(R.id.shopCartItemRemove);
        removeButton.setOnClickListener(v -> {
            _user.getCart().removeFromCart(item);
            notifyDataSetChanged();
        });

        return view;
    }
}
