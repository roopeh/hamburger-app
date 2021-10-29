package com.roopeh.app.hamburger;

import android.Manifest;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class RestaurantInfoFragment extends PermissionsFragment {
    private final Restaurant _res;
    private ImageView _mapView;

    public RestaurantInfoFragment(Restaurant res) {
        super();
        _res = res;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_restaurant_info, container, false);

        final TextView name = rootView.findViewById(R.id.restaurantInfoName);
        name.setText(_res.getName());

        final TextView status = rootView.findViewById(R.id.restaurantInfoStatus);
        if (_res.isRestaurantOpen())
            status.setText(getString(R.string.restaurantStatusOpen));
        else
            status.setText(getString(R.string.restaurantStatusClosed));

        TextView hours = rootView.findViewById(R.id.restaurantInfoLeftHours);
        String hourStr =
                getString(R.string.restaurantHoursMon, generateHourStringForDay(Calendar.MONDAY)) + "\n" +
                getString(R.string.restaurantHoursThu, generateHourStringForDay(Calendar.THURSDAY)) + "\n" +
                getString(R.string.restaurantHoursSun, generateHourStringForDay(Calendar.SUNDAY));
        hours.setText(hourStr);

        hours = rootView.findViewById(R.id.restaurantInfoMiddleHours);
        hourStr =
                getString(R.string.restaurantHoursTue, generateHourStringForDay(Calendar.TUESDAY)) + "\n" +
                getString(R.string.restaurantHoursFri, generateHourStringForDay(Calendar.FRIDAY));
        hours.setText(hourStr);

        hours = rootView.findViewById(R.id.restaurantInfoRightHours);
        hourStr =
                getString(R.string.restaurantHoursWed, generateHourStringForDay(Calendar.WEDNESDAY)) + "\n" +
                getString(R.string.restaurantHoursSat, generateHourStringForDay(Calendar.SATURDAY));
        hours.setText(hourStr);

        final TextView address = rootView.findViewById(R.id.restaurantInfoAddress);
        address.setText(_res.getLocationString());

        final TextView phone = rootView.findViewById(R.id.restaurantInfoPhone);
        phone.setText(_res.getPhoneNumber());

        _mapView = rootView.findViewById(R.id.restaurantInfoMaps);

        final ImageButton backButton = rootView.findViewById(R.id.restaurantInfoBackButton);
        backButton.setOnClickListener(v -> Objects.requireNonNull((MainActivity)getActivity()).returnToPreviousFragment(false));

        activityResultLauncher.launch(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION});

        return rootView;
    }

    private String generateHourStringForDay(int weekday) {
        final int startHour = _res.getDates().getStartHoursForDay(weekday);
        final int endHour = _res.getDates().getEndHoursForDay(weekday);
        if (startHour == -1 || endHour == -1)
            return getString(R.string.restaurantStatusClosed).toLowerCase();

        return getString(R.string.restaurantHours, startHour) + " - " + getString(R.string.restaurantHours, endHour);
    }

    private void showMapImage(final double lat, final double lon) {
        _mapView.setVisibility(View.VISIBLE);
        _mapView.setOnClickListener(v -> {
            final Uri uri = Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon + "(" + Uri.encode(_res.getName()) + ")");
            final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        });
    }

    @Override
    protected void onPermissionsCheck(final boolean allGranted) {
        if (!allGranted)
            return;

        try {
            final Geocoder geocoder = new Geocoder(getContext());
            final List<Address> all = geocoder.getFromLocationName(_res.getLocationString(), 3);
            final Address address = all.get(0);
            showMapImage(address.getLatitude(), address.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
