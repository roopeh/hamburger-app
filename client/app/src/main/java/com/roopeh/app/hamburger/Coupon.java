package com.roopeh.app.hamburger;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Coupon {
    final private int _type;
    final private long _expirydate;

    // Coupon types
    public static final int TYPE_FREE_LARGE_DRINK               = 1;
    public static final int TYPE_FREE_LARGE_EXTRAS              = 2;
    public static final int TYPE_50_OFF                         = 3;

    public Coupon(int type, long expirydate) {
        _type = type;
        _expirydate = expirydate;
    }

    public final int getType() {
        return _type;
    }

    public final String getExpiryDate() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return Instant.ofEpochSecond(_expirydate).atZone(ZoneId.of("GMT+3")).format(formatter);
    }

    public final String getName() {
        switch (_type) {
            case TYPE_FREE_LARGE_DRINK:
                return "Large drink";
            case TYPE_FREE_LARGE_EXTRAS:
                return "Large extras";
            case TYPE_50_OFF:
                return "50% off";
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
            default:
                return "Unknown coupon type";
        }
    }
}
