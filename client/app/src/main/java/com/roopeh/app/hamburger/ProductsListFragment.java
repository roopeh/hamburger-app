package com.roopeh.app.hamburger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProductsListFragment extends Fragment {
    final private int _category;

    public ProductsListFragment(int category) {
        _category = category;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_products_list, container, false);
        TextView header = rootView.findViewById(R.id.productsListHeader);
        List<Product> content = new ArrayList<>();

        // Populate list with correct products
        if (_category == Helper.Constants.CATEGORY_MEAL) {
            header.setText("Ateriat");
            for (Product meal : Helper.getInstance().getProducts()) {
                if (meal.isMeal())
                    content.add(meal);
            }
        } else {
            header.setText("Hampurilaiset");
            for (Product product : Helper.getInstance().getProducts()) {
                if (!product.isMeal()) {
                    content.add(product);
                }
            }
        }

        final RecyclerView grid = rootView.findViewById(R.id.productsListGrid);
        final ProductsListGridAdapter adapter = new ProductsListGridAdapter(content, this);
        grid.setLayoutManager(new GridLayoutManager(getContext(), 2));
        grid.addItemDecoration(new RecyclerViewDivider(Helper.Constants.GRID_DIVIDER, Helper.Constants.GRID_DIVIDER, 2));
        grid.setAdapter(adapter);

        ImageButton returnButton = rootView.findViewById(R.id.productsListBackButton);
        returnButton.setOnClickListener(v -> Objects.requireNonNull((MainActivity)getActivity()).returnToPreviousFragment(false));
        return rootView;
    }
}

class ProductsListGridAdapter extends RecyclerView.Adapter<ProductsListGridAdapter.ViewHolder> {
    final private List<Product> _list;
    final private ProductsListFragment _frag;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final private ImageView image;
        final private TextView name;

        public ViewHolder(@NonNull View itemView, List<Product> list, ProductsListFragment fragment) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                final Product product = list.get(getAdapterPosition());
                Objects.requireNonNull((MainActivity)fragment.getActivity()).loadFragment(new ProductsInfoFragment(product), false);
            });

            image = itemView.findViewById(R.id.productsListGridImage);
            name = itemView.findViewById(R.id.productsListGridText);
        }

        final public ImageView getProductImage() {
            return image;
        }

        final public TextView getProductName() {
            return name;
        }
    }

    public ProductsListGridAdapter(List<Product> list, ProductsListFragment frag) {
        _list = list;
        _frag = frag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View listItem = inflater.inflate(R.layout.grid_products_list, parent, false);
        return new ViewHolder(listItem, _list, _frag);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Product product = _list.get(position);
        //holder.getProductImage() TODO
        holder.getProductName().setText(product.getName());
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }
}
