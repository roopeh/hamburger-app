package com.roopeh.app.hamburger;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

public class Coupon {
    final private int _type;
    final private long _expiryDate;

    // Coupon types
    public static final int TYPE_FREE_LARGE_DRINK               = 1;
    public static final int TYPE_FREE_LARGE_EXTRAS              = 2;
    public static final int TYPE_50_OFF                         = 3;
    public static final int TYPE_EMPTY_COUPON                   = 10;

    public Coupon(int type, long expiryDate) {
        _type = type;
        _expiryDate = expiryDate;
    }

    public final int getType() {
        return _type;
    }

    public final String getExpiryDate() {
        // API 26+
        /*final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return Instant.ofEpochSecond(_expiryDate).atZone(ZoneId.of("GMT+3")).format(formatter);*/
        // For older APIs
        final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        return format.format(_expiryDate * 1000);
    }

    // TODO: put to strings resource file
    public final String getName() {
        switch (_type) {
            case TYPE_FREE_LARGE_DRINK:
                return "Large drink";
            case TYPE_FREE_LARGE_EXTRAS:
                return "Large extras";
            case TYPE_50_OFF:
                return "50% off";
            case TYPE_EMPTY_COUPON:
                return "No coupon";
            default:
                return "Unknown coupon type";
        }
    }

    public final String getDescription() {
        switch (_type) {
            case TYPE_FREE_LARGE_DRINK:
                return "Large drink for free";
            case TYPE_FREE_LARGE_EXTRAS:
                return "Large extras for free";
            case TYPE_50_OFF:
                return "50% cheaper!";
            case TYPE_EMPTY_COUPON:
                return "No coupon";
            default:
                return "Unknown coupon type";
        }
    }
}
