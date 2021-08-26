package com.roopeh.app.hamburger;

import java.util.Arrays;
import java.util.Calendar;

public class Restaurant {
    private final String _name;
    private String _address;
    private String _city;

    private final RestaurantDates dates;

    public Restaurant(String name) {
        _name = name;
        dates = new RestaurantDates();
    }

    final public String getName() {
        return _name;
    }

    final public String getAddress() {
        return _address;
    }

    final public RestaurantDates getDates() {
        return dates;
    }

    final public String getLocationString() {
        return _address + ", " + _city;
    }

    final public boolean isRestaurantOpen() {
        // TODO: does not work correctly if end hours are in the next day
        Calendar now = Calendar.getInstance();
        final int start = dates.getStartHoursForDay(now.get(Calendar.DAY_OF_WEEK));
        final int end = dates.getEndHoursForDay(now.get(Calendar.DAY_OF_WEEK));
        if (start == -1 || end == -1)
            return false;

        return now.get(Calendar.HOUR_OF_DAY) >= start && now.get(Calendar.HOUR_OF_DAY) < end;
    }

    public void setLocation(String address, String city) {
        if (!address.isEmpty())
            _address = address;

        if (!city.isEmpty())
            _city = city;
    }

    public void setHours(int weekday, String start, String end) {
        if (weekday > Calendar.SATURDAY)
            return;

        dates.setHours(weekday, start + end);
    }
}

class RestaurantDates {
    // TODO: database table format; shop id, weekday, start hours, end hours

    // Empty value for index means the shop is closed for that day
    // Note, index 0 = sunday and index 6 = saturday
    // i.e. value "1022" means the shop opens at 10 and is closed at 22
    private final String[] dates;

    public RestaurantDates() {
        dates = new String[7];
        Arrays.fill(dates, "");
    }

    final public int getStartHoursForDay(int weekday) {
        if (weekday > Calendar.SATURDAY)
            return -1;

        if (dates[weekday - 1].isEmpty())
            return -1;

        return Integer.parseInt(dates[weekday - 1].substring(0, 2));
    }

    final public int getEndHoursForDay(int weekday) {
        if (weekday > Calendar.SATURDAY)
            return -1;

        if (dates[weekday - 1].isEmpty())
            return -1;

        return Integer.parseInt(dates[weekday - 1].substring(2, 4));
    }

    public void setHours(int weekday, String hours) {
        dates[weekday - 1] = hours;
    }
}
