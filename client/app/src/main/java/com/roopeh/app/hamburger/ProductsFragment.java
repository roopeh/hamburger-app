package com.roopeh.app.hamburger;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProductsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_products, container, false);

        final List<String> content = new ArrayList<>();
        content.add(getString(R.string.productsMeals));
        content.add(getString(R.string.productsHamburgers));

        final RecyclerView grid = rootView.findViewById(R.id.productsMainGrid);
        final ProductsMainGridAdapter adapter = new ProductsMainGridAdapter(content, this);
        grid.setLayoutManager(new GridLayoutManager(getContext(), 2));
        grid.addItemDecoration(new RecyclerViewDivider(Helper.Constants.GRID_DIVIDER, Helper.Constants.GRID_DIVIDER, 2));
        grid.setAdapter(adapter);

        return rootView;
    }
}

class ProductsMainGridAdapter extends RecyclerView.Adapter<ProductsMainGridAdapter.ViewHolder> {
    final private List<String> _list;
    final private ProductsFragment _frag;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final private TextView name;

        public ViewHolder(@NonNull View itemView, List<String> list, ProductsFragment fragment, Context context) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                final String item = list.get(getAdapterPosition());
                final int category = item.equals(context.getString(R.string.productsMeals)) ? Helper.Constants.PRODUCT_CATEGORY_MEAL : Helper.Constants.PRODUCT_CATEGORY_HAMBURGER;
                Objects.requireNonNull((MainActivity)fragment.getActivity()).loadFragment(new ProductsListFragment(category), false);
            });

            name = itemView.findViewById(R.id.productsMainGridText);
        }

        final public TextView getName() {
            return name;
        }
    }

    public ProductsMainGridAdapter(List<String> list, ProductsFragment frag) {
        _list = list;
        _frag = frag;
    }

    @NonNull
    @Override
    public ProductsMainGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View listItem = inflater.inflate(R.layout.grid_products_main, parent, false);
        return new ViewHolder(listItem, _list, _frag, _frag.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsMainGridAdapter.ViewHolder holder, int position) {
        holder.getName().setText(_list.get(position));
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }
}
