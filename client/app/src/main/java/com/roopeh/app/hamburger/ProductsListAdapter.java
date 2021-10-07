package com.roopeh.app.hamburger;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

enum ProductsListType {
    DEFAULT,        // Default list
    CART            // In shopping cart
}

class ProductsListAdapter extends BaseAdapter {
    final private Context _context;
    final private List<ShoppingItem> _content;
    final private ProductsListType _type;
    final private User _user;
    final private CartFragment _frag;

    public ProductsListAdapter(Context context, User user, CartFragment frag) {
        _type = ProductsListType.CART;
        _context = context;
        _content = user.getCart().getItems();
        _user = user;
        _frag = frag;
    }

    public ProductsListAdapter(Context context, Order order) {
        _type = ProductsListType.DEFAULT;
        _context = context;
        _content = order.getItems();
        _user = null;
        _frag = null;
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
        final View view;
        final int itemNameLayoutId, itemPriceLayoutId, itemExtraLayoutId;
        if (_type == ProductsListType.CART) {
            view = View.inflate(_context, R.layout.shop_cart_list_item, null);
            itemNameLayoutId = R.id.shopCartItemName;
            itemPriceLayoutId = R.id.shopCartItemPrice;
            itemExtraLayoutId = R.id.shopCartItemExtra;
        } else {
            view = View.inflate(_context, R.layout.order_list_item, null);
            itemNameLayoutId = R.id.orderItemName;
            itemPriceLayoutId = R.id.orderItemPrice;
            itemExtraLayoutId = R.id.orderItemExtra;
        }

        final ShoppingItem item = getItem(position);

        final TextView name = view.findViewById(itemNameLayoutId);
        name.setText("- " + item.getProduct().getName());

        final TextView price = view.findViewById(itemPriceLayoutId);
        price.setText(String.format(Locale.getDefault(), "%.2f", item.getPrice()) + " €");

        final TextView extraInfo = view.findViewById(itemExtraLayoutId);
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

        if (_type == ProductsListType.CART) {
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
        }

        return view;
    }
}
