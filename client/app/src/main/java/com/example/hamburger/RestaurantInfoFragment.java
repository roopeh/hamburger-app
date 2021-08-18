package com.example.hamburger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;
import java.util.Objects;

import androidx.fragment.app.Fragment;

public class RestaurantInfoFragment extends Fragment {
    private final Restaurant _res;

    public RestaurantInfoFragment(Restaurant res) {
        _res = res;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_restaurant_info, container, false);

        TextView name = rootView.findViewById(R.id.restaurantInfoName);
        name.setText(_res.getName());

        TextView status = rootView.findViewById(R.id.restaurantInfoStatus);
        if (_res.isRestaurantOpen())
            status.setText("Open");
        else
            status.setText("Closed");

        TextView hours = rootView.findViewById(R.id.restaurantInfoLeftHours);
        String hourStr =
                "Ma " + generateHourStringForDay(1) + "\n" +
                "To " + generateHourStringForDay(4) + "\n" +
                "Su " + generateHourStringForDay(0);
        hours.setText(hourStr);

        hours = rootView.findViewById(R.id.restaurantInfoMiddleHours);
        hourStr =
                "Ti " + generateHourStringForDay(2) + "\n" +
                "Pe " + generateHourStringForDay(5);
        hours.setText(hourStr);

        hours = rootView.findViewById(R.id.restaurantInfoRightHours);
        hourStr =
                "Ke " + generateHourStringForDay(3) + "\n" +
                "La " + generateHourStringForDay(6);
        hours.setText(hourStr);

        TextView address = rootView.findViewById(R.id.restaurantInfoAddress);
        address.setText(_res.getAddress());

        // TODO: google maps widget

        ImageButton backButton = rootView.findViewById(R.id.restaurantInfoBackButton);
        backButton.setOnClickListener(v -> Objects.requireNonNull(((MainActivity)getActivity())).loadFragment(new RestaurantFragment()));

        return rootView;
    }

    private String generateHourStringForDay(int weekday) {
        final int startHour = _res.getDates().getStartHoursForDay(weekday);
        final int endHour = _res.getDates().getEndHoursForDay(weekday);
        if (startHour == -1 || endHour == -1)
            return "kiinni";

        return String.format((Locale)null, "%02d", startHour) + "-" + String.format((Locale)null, "%02d", endHour);
    }
}
