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
            case Helper.Constants.COUPON_TYPE_FREE_LARGE_DRINK:
                return "Large drink";
            case Helper.Constants.COUPON_TYPE_FREE_LARGE_EXTRAS:
                return "Large extras";
            case Helper.Constants.COUPON_TYPE_50_OFF:
                return "50% off";
            case Helper.Constants.COUPON_TYPE_EMPTY_COUPON:
                return "No coupon";
            default:
                return "Unknown coupon type";
        }
    }

    public final String getDescription() {
        switch (_type) {
            case Helper.Constants.COUPON_TYPE_FREE_LARGE_DRINK:
                return "Large drink for free";
            case Helper.Constants.COUPON_TYPE_FREE_LARGE_EXTRAS:
                return "Large extras for free";
            case Helper.Constants.COUPON_TYPE_50_OFF:
                return "50% cheaper!";
            case Helper.Constants.COUPON_TYPE_EMPTY_COUPON:
                return "No coupon";
            default:
                return "Unknown coupon type";
        }
    }

    /*
     * Static methods
     */
    public static double getDiscountFromCoupon(final int couponType, final double sum) {
        switch (couponType) {
            case Helper.Constants.COUPON_TYPE_FREE_LARGE_DRINK:
                return 0.5;
            case Helper.Constants.COUPON_TYPE_FREE_LARGE_EXTRAS:
                return 0.4;
            case Helper.Constants.COUPON_TYPE_50_OFF:
                return sum / 2;
            default:
                return 0.0;
        }
    }

    public static boolean isCouponAvailableForCart(final int couponType) {
        switch (couponType) {
            case Helper.Constants.COUPON_TYPE_FREE_LARGE_DRINK: {
                for (final ShoppingItem item : Helper.getInstance().getUser().getCart().getItems()) {
                    if (!item.getProduct().isMeal())
                        continue;

                    if (item.getMealDrink() == 0)
                        continue;

                    if (item.isLargeDrink()) {
                        return true;
                    }
                }
            } break;
            case Helper.Constants.COUPON_TYPE_FREE_LARGE_EXTRAS: {
                for (final ShoppingItem item : Helper.getInstance().getUser().getCart().getItems()) {
                    if (!item.getProduct().isMeal())
                        continue;

                    if (item.getMealExtra() == 0)
                        continue;

                    if (item.isLargeExtra()) {
                        return true;
                    }
                }
            } break;
            case Helper.Constants.COUPON_TYPE_50_OFF: {
                return true;
            }
            default: break;
        }

        return false;
    }
}
