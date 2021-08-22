package com.example.hamburger;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;

public class ProductsListFragment extends Fragment {
    int _category;

    final public static int CATEGORY_HAMBURGER = 0;
    final public static int CATEGORY_MEAL = 1;

    public ProductsListFragment(int category) {
        _category = category;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_products_list, container, false);
        TextView header = rootView.findViewById(R.id.productsListHeader);
        List<String> content = new ArrayList<>();

        // Create content
        if (_category == CATEGORY_MEAL) {
            header.setText("Ateriat");
            for (int i = 0; i < 3; ++i) {
                content.add("Juustoateria");
                content.add("Pekoniateria");
                content.add("Kinkkuateria");
            }
        } else {
            header.setText("Hampurilaiset");
            for (int i = 0; i < 3; ++i) {
                content.add("Juusto");
                content.add("Pekoni");
                content.add("Kinkku");
            }
        }

        GridView grid = rootView.findViewById(R.id.productsListGrid);
        ProductsListGridAdapter adapter = new ProductsListGridAdapter(getContext(), content);
        grid.setAdapter(adapter);
        //grid.setOnItemClickListener((parent, view, position, id) -> Objects.requireNonNull((MainActivity)getActivity()).loadFragment(new Product()));
        return rootView;
    }
}

class ProductsListGridAdapter extends BaseAdapter {
    Context _context;
    List<String> _content;

    public ProductsListGridAdapter(Context context, List<String> content) {
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
        View view = View.inflate(_context, R.layout.grid_products_list, null);

        // Product image
        //ImageView img = view.findViewById(R.id.productsListGridImage);

        // Product name
        TextView name = view.findViewById(R.id.productsListGridText);
        name.setText(_content.get(position));
        return view;
    }
}
