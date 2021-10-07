package com.roopeh.app.hamburger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;
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
                "Ma " + generateHourStringForDay(Calendar.MONDAY) + "\n" +
                "To " + generateHourStringForDay(Calendar.THURSDAY) + "\n" +
                "Su " + generateHourStringForDay(Calendar.SUNDAY);
        hours.setText(hourStr);

        hours = rootView.findViewById(R.id.restaurantInfoMiddleHours);
        hourStr =
                "Ti " + generateHourStringForDay(Calendar.TUESDAY) + "\n" +
                "Pe " + generateHourStringForDay(Calendar.FRIDAY);
        hours.setText(hourStr);

        hours = rootView.findViewById(R.id.restaurantInfoRightHours);
        hourStr =
                "Ke " + generateHourStringForDay(Calendar.WEDNESDAY) + "\n" +
                "La " + generateHourStringForDay(Calendar.SATURDAY);
        hours.setText(hourStr);

        TextView address = rootView.findViewById(R.id.restaurantInfoAddress);
        address.setText(_res.getLocationString());

        TextView phone = rootView.findViewById(R.id.restaurantInfoPhone);
        phone.setText(_res.getPhoneNumber());

        // TODO: google maps widget

        ImageButton backButton = rootView.findViewById(R.id.restaurantInfoBackButton);
        backButton.setOnClickListener(v -> Objects.requireNonNull((MainActivity)getActivity()).returnToPreviousFragment(false));

        return rootView;
    }

    private String generateHourStringForDay(int weekday) {
        final int startHour = _res.getDates().getStartHoursForDay(weekday);
        final int endHour = _res.getDates().getEndHoursForDay(weekday);
        if (startHour == -1 || endHour == -1)
            return "kiinni";

        return String.format(Locale.getDefault(), "%02d", startHour) + "-" + String.format(Locale.getDefault(), "%02d", endHour);
    }
}
