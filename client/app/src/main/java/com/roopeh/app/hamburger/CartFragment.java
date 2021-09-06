package com.roopeh.app.hamburger;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.fragment.app.Fragment;

public class CartFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);

        ScrollView emptyView = rootView.findViewById(R.id.shopCartEmptyView);
        ScrollView defaultView = rootView.findViewById(R.id.shopCartDefaultView);

        User user = Objects.requireNonNull((MainActivity)getActivity()).getUser();
        if (user != null && user.getCart().isCartEmpty())
        {
            emptyView.setVisibility(View.VISIBLE);
            //defaultView.setVisibility(View.VISIBLE);
        }

        ListView shoppingItems = rootView.findViewById(R.id.shopCartProducts);
        Spinner couponSpinner = rootView.findViewById(R.id.shopCartCoupons);
        Spinner restaurantSpinner = rootView.findViewById(R.id.shopCartRestaurant);

        // todo: Dummy content
        List<ShoppingItem> tests = new ArrayList<>();
        tests.add(new ShoppingItem(new Product("Pekoni")));

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

        ProductsListAdapter adapter = new ProductsListAdapter(getContext(), tests);
        shoppingItems.setAdapter(adapter);

        return rootView;
    }
}

class ProductsListAdapter extends BaseAdapter {
    final Context _context;
    final List<ShoppingItem> _content;

    public ProductsListAdapter(Context context, List<ShoppingItem> content) {
        _context = context;
        _content = content;
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

        return view;
    }
}
