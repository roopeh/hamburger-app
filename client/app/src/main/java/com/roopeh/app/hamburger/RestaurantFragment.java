package com.roopeh.app.hamburger;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.fragment.app.Fragment;

public class RestaurantFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_restaurant, container, false);
        ListView restaurantList = rootView.findViewById(R.id.restaurantList);

        // Dummy content
        List<Restaurant> tests = new ArrayList<>();
        for (int i = 1; i < 20; ++i) {
            Restaurant res = new Restaurant("Restaurant " + i);
            res.setLocation("Testikatu " + (i * 12), "Oulu");

            res.setHours(1, "08", "10");
            res.setHours(2, "11", "13");
            res.setHours(3, "14", "16");
            res.setHours(4, "16", "18");
            res.setHours(5, "20", "23");
            res.setHours(7, "10", "14");

            tests.add(res);
        }

        RestaurantListAdapter test = new RestaurantListAdapter(getContext(), tests);
        restaurantList.setAdapter(test);

        restaurantList.setOnItemClickListener((parent, view, position, id) -> {
            final Restaurant res = (Restaurant)parent.getItemAtPosition(position);
            Objects.requireNonNull(((MainActivity)getActivity())).loadFragment(new RestaurantInfoFragment(res));
        });
        return rootView;
    }
}

class RestaurantListAdapter extends BaseAdapter {
    final private Context _context;
    final private List<Restaurant> _list;

    public RestaurantListAdapter(Context cont, List<Restaurant> list) {
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
        Restaurant res = _list.get(position);

        TextView name = view.findViewById(R.id.restaurantListItemName);
        name.setText(res.getName());

        TextView location = view.findViewById(R.id.restaurantListItemLocation);
        location.setText(res.getLocationString());

        // TODO
        //TextView dist = view.findViewById(R.id.restaurantListItemDistance);
        //dist.setText();

        TextView status = view.findViewById(R.id.restaurantListItemStatus);
        if (res.isRestaurantOpen())
            status.setText("Open");
        else
            status.setText("Closed");

        return view;
    }
}
