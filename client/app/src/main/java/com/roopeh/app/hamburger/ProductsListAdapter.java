package com.roopeh.app.hamburger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

enum ProductsListType {
    DEFAULT,        // Default list
    CART            // In shopping cart
}

class ProductsListAdapter extends RecyclerView.Adapter<ProductsListAdapter.ViewHolder> {
    final private Context _context;
    final private List<ShoppingItem> _list;
    final private ProductsListType _type;
    final private User _user;
    final private CartFragment _frag;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final private TextView name;
        final private TextView price;
        final private TextView extra;
        final private ImageButton removeButton;

        public ViewHolder(@NonNull View itemView, ProductsListType type) {
            super(itemView);

            final int itemNameLayoutId, itemPriceLayoutId, itemExtraLayoutId;
            if (type == ProductsListType.CART) {
                itemNameLayoutId = R.id.shopCartItemName;
                itemPriceLayoutId = R.id.shopCartItemPrice;
                itemExtraLayoutId = R.id.shopCartItemExtra;
                removeButton = itemView.findViewById(R.id.shopCartItemRemove);
            } else {
                itemNameLayoutId = R.id.orderItemName;
                itemPriceLayoutId = R.id.orderItemPrice;
                itemExtraLayoutId = R.id.orderItemExtra;
                removeButton = null;
            }

            name = itemView.findViewById(itemNameLayoutId);
            price = itemView.findViewById(itemPriceLayoutId);
            extra = itemView.findViewById(itemExtraLayoutId);
        }

        final public TextView getName() {
            return name;
        }

        final public TextView getPrice() {
            return price;
        }

        final public TextView getExtra() {
            return extra;
        }

        final public ImageButton getRemoveButton() {
            return removeButton;
        }
    }

    public ProductsListAdapter(Context context, User user, CartFragment frag) {
        _context = context;
        _type = ProductsListType.CART;
        _list = user.getCart().getItems();
        _user = user;
        _frag = frag;
    }

    public ProductsListAdapter(Context context, Order order) {
        _context = context;
        _type = ProductsListType.DEFAULT;
        _list = order.getItems();
        _user = null;
        _frag = null;
    }

    @NonNull
    @Override
    public ProductsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View itemView;
        if (_type == ProductsListType.CART)
            itemView = inflater.inflate(R.layout.shop_cart_list_item, parent, false);
        else
            itemView = inflater.inflate(R.layout.order_list_item, parent, false);

        return new ViewHolder(itemView, _type);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsListAdapter.ViewHolder holder, int position) {
        final ShoppingItem item = _list.get(position);
        holder.getName().setText("- " + item.getProduct().getName());
        holder.getPrice().setText(String.format(Locale.getDefault(), "%.2f", item.getPrice()) + " €");

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

            holder.getExtra().setVisibility(View.VISIBLE);
            holder.getExtra().setText(extra);
        }

        if (_type == ProductsListType.CART) {
            holder.getRemoveButton().setOnClickListener(v -> {
                _user.getCart().removeFromCart(item);
                // Close shopping cart if it's empty now
                if (_user.getCart().isCartEmpty()) {
                    Objects.requireNonNull((MainActivity)_frag.getActivity()).returnToPreviousFragment(false);
                    return;
                }

                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
                _frag.populateCoupons();
                _frag.populatePrice();
                _frag.populateFinalPrice();
            });
        }
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }
}
