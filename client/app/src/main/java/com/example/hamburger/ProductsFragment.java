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
import java.util.Objects;

import androidx.fragment.app.Fragment;

public class ProductsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_products, container, false);

        List<String> content = new ArrayList<>();
        content.add("Ateriat");
        content.add("Hampurilaiset");

        GridView grid = rootView.findViewById(R.id.productsMainGrid);
        ProductsMainGridAdapter adapter = new ProductsMainGridAdapter(getContext(), content);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(((parent, view, position, id) -> {
            final String item = (String)parent.getItemAtPosition(position);
            final int category = item.equals("Ateriat") ? ProductsListFragment.CATEGORY_MEAL : ProductsListFragment.CATEGORY_HAMBURGER;
            Objects.requireNonNull((MainActivity)getActivity()).loadFragment(new ProductsListFragment(category));
        }));

        return rootView;
    }
}

class ProductsMainGridAdapter extends BaseAdapter {
    Context _context;
    List<String> _content;

    public ProductsMainGridAdapter(Context context, List<String> content) {
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
        View view = View.inflate(_context, R.layout.grid_products_main, null);
        TextView text = view.findViewById(R.id.productsMainGridText);
        text.setText(_content.get(position));
        return view;
    }
}
