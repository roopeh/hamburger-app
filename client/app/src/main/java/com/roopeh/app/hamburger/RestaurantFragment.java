package com.roopeh.app.hamburger;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class RestaurantFragment extends PermissionsFragment {
    private RestaurantListAdapter adapter;
    private Geocoder geocoder = null;
    private LocationManager locationManager = null;
    private Location ownLocation = null;

    public RestaurantFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_restaurant, container, false);

        final ListView restaurantList = rootView.findViewById(R.id.restaurantList);
        adapter = new RestaurantListAdapter(getContext(), Helper.getInstance().getRestaurants(), this);
        restaurantList.setAdapter(adapter);

        restaurantList.setOnItemClickListener((parent, view, position, id) -> {
            final Restaurant res = (Restaurant) parent.getItemAtPosition(position);
            Objects.requireNonNull(((MainActivity) getActivity())).loadFragment(new RestaurantInfoFragment(res), false);
        });

        activityResultLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});

        return rootView;
    }

    @Override
    protected void onPermissionsCheck(final boolean allGranted) {
        geocoder = new Geocoder(getContext());

        try {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return;

            locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new SingleLocationListener(this), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final public double getDistance(final String address) {
        if (geocoder == null || locationManager == null || ownLocation == null)
            return -1;

        Location restaurantLocation = null;

        try {
            List<Address> all = geocoder.getFromLocationName(address, 3);
            Address restaurantAddress = all.get(0);
            restaurantLocation = new Location("Restaurant");
            restaurantLocation.setLatitude(restaurantAddress.getLatitude());
            restaurantLocation.setLongitude(restaurantAddress.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }

       if (restaurantLocation == null)
           return -1;

        return ownLocation.distanceTo(restaurantLocation);
    }

    private class SingleLocationListener implements LocationListener {
        final private RestaurantFragment _frag;

        public SingleLocationListener(RestaurantFragment frag) {
            _frag = frag;
        }

        @Override
        public void onLocationChanged(@NonNull Location location) {
            _frag.ownLocation = location;
            locationManager.removeUpdates(this);
            _frag.adapter.notifyDataSetChanged();
        }
    }
}

class RestaurantListAdapter extends BaseAdapter {
    final private Context _context;
    final private List<Restaurant> _list;
    final private RestaurantFragment _frag;

    public RestaurantListAdapter(Context cont, List<Restaurant> list, RestaurantFragment fragment) {
        _context = cont;
        _list = list;
        _frag = fragment;
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
        final View view = View.inflate(_context, R.layout.restaurant_list_item, null);
        Restaurant res = _list.get(position);

        final TextView name = view.findViewById(R.id.restaurantListItemName);
        name.setText(res.getName());

        final TextView location = view.findViewById(R.id.restaurantListItemLocation);
        location.setText(res.getLocationString());

        // Get distance to restaurant
        final TextView dist = view.findViewById(R.id.restaurantListItemDistance);
        final double distance = _frag.getDistance(res.getLocationString());
        if (distance > 0) {
            if (distance >= 10000) {
                final int intDistance = (int)Math.round(distance / 1000);
                dist.setText(intDistance + " km");
            } else if (distance >= 1000) {
                final double doubleDistance = distance / 1000;
                dist.setText(String.format(Locale.getDefault(), "%.1f", doubleDistance) + " km");
            } else {
                final int intDistance = (int)Math.round(distance);
                dist.setText(intDistance + " m");
            }
        }

        final TextView status = view.findViewById(R.id.restaurantListItemStatus);
        if (res.isRestaurantOpen())
            status.setText("Open");
        else
            status.setText("Closed");

        return view;
    }
}
