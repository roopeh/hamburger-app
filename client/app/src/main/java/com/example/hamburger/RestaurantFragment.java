package com.example.hamburger;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;

public class RestaurantFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_restaurant, container, false);
        ListView restaurantList = rootView.findViewById(R.id.restaurantList);

        // Dummy content
        List<String> tests = new ArrayList<>();
        for (int i = 1; i < 20; ++i)
            tests.add("Restaurant " + i +"\nOsoite, Kaupunki");

        RestaurantListAdapter test = new RestaurantListAdapter(getContext(), tests);
        restaurantList.setAdapter(test);
        return rootView;
    }
}

class RestaurantListAdapter extends BaseAdapter {
    Context _context;
    List<String> _list;

    public RestaurantListAdapter(Context cont, List<String> list) {
        _context = cont;
        _list = list;
    }

    @Override
    public int getCount() {
        return _list.size();
    }

    @Override
    public Object getItem(int position) {
        return _list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(_context, R.layout.restaurant_list_item, null);
        TextView content = view.findViewById(R.id.restaurantListItemText);
        content.setText(_list.get(position));
        return view;
    }
}
