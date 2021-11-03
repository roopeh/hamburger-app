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
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        final RecyclerView restaurantList = rootView.findViewById(R.id.restaurantList);
        adapter = new RestaurantListAdapter(Helper.getInstance().getRestaurants(), this);
        restaurantList.setLayoutManager(new LinearLayoutManager(getContext()));
        restaurantList.addItemDecoration(new RecyclerViewDivider(Helper.Constants.GRID_DIVIDER));
        restaurantList.setAdapter(adapter);

        activityResultLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});

        return rootView;
    }

    @Override
    protected void onPermissionsCheck(final boolean allGranted) {
        if (!allGranted)
            return;

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
            final List<Address> all = geocoder.getFromLocationName(address, 3);
            final Address restaurantAddress = all.get(0);
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
            _frag.adapter.notifyItemRangeChanged(0, _frag.adapter.getItemCount());
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) { }

        @Override
        public void onProviderDisabled(@NonNull String provider) { }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }
    }
}

class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.ViewHolder> {
    final private List<Restaurant> _list;
    final private RestaurantFragment _frag;
    final private Context _context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final private TextView restaurantName;
        final private TextView restaurantLoc;
        final private TextView restaurantDist;
        final private TextView restaurantStatus;

        public ViewHolder(@NonNull View itemView, List<Restaurant> list, RestaurantFragment fragment) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                final Restaurant res = list.get(getAdapterPosition());
                Objects.requireNonNull((MainActivity)fragment.getActivity()).loadFragment(new RestaurantInfoFragment(res), false);
            });

            restaurantName = itemView.findViewById(R.id.restaurantListItemName);
            restaurantLoc = itemView.findViewById(R.id.restaurantListItemLocation);
            restaurantDist = itemView.findViewById(R.id.restaurantListItemDistance);
            restaurantStatus = itemView.findViewById(R.id.restaurantListItemStatus);
        }

        final public TextView getRestaurantName() {
            return restaurantName;
        }

        final public TextView getRestaurantLocation() {
            return restaurantLoc;
        }

        final public TextView getRestaurantDistance() {
            return restaurantDist;
        }

        final public TextView getRestaurantStatus() {
            return restaurantStatus;
        }
    }

    public RestaurantListAdapter(List<Restaurant> list, RestaurantFragment fragment) {
        _list = list;
        _frag = fragment;
        _context = _frag.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View listItem = inflater.inflate(R.layout.restaurant_list_item, parent, false);
        return new ViewHolder(listItem, _list, _frag);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Restaurant res = _list.get(position);
        holder.getRestaurantName().setText(res.getName());
        holder.getRestaurantLocation().setText(res.getLocationString());

        // Get distance to restaurant
        // Check if its calculated already
        if (holder.getRestaurantDistance().getText().toString().isEmpty()) {
            final double distance = _frag.getDistance(res.getLocationString());
            if (distance > 0) {
                if (distance >= 10000) {
                    final int intDistance = (int)Math.round(distance / 1000);
                    holder.getRestaurantDistance().setText(_context.getString(R.string.restaurantDistanceOver10km, intDistance));
                } else if (distance >= 1000) {
                    final double doubleDistance = distance / 1000;
                    holder.getRestaurantDistance().setText(_context.getString(R.string.restaurantDistanceOver1km, doubleDistance));
                } else {
                    final int intDistance = (int)Math.round(distance);
                    holder.getRestaurantDistance().setText(_context.getString(R.string.restaurantDistanceLess1km, intDistance));
                }
            }
        }

        // Restaurant status
        if (res.isRestaurantOpen())
            holder.getRestaurantStatus().setText(_context.getString(R.string.restaurantStatusOpen));
        else
            holder.getRestaurantStatus().setText(_context.getString(R.string.restaurantStatusClosed));
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }
}
