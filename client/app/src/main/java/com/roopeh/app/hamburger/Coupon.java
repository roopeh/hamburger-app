package com.roopeh.app.hamburger;

import android.content.Context;
import android.os.Build;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // API 26+
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return Instant.ofEpochSecond(_expiryDate).atZone(ZoneId.of("GMT+3")).format(formatter);
        } else {
            // For older APIs
            final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            format.setTimeZone(TimeZone.getTimeZone("GMT+3"));
            return format.format(_expiryDate * 1000);
        }
    }

    final public long getExpiryDateRaw() {
        return _expiryDate;
    }

    public final String getName(final Context context) {
        switch (_type) {
            case Helper.Constants.COUPON_TYPE_FREE_LARGE_DRINK:
                return context.getString(R.string.couponNameLargeDrink);
            case Helper.Constants.COUPON_TYPE_FREE_LARGE_EXTRAS:
                return context.getString(R.string.couponNameLargeExtra);
            case Helper.Constants.COUPON_TYPE_50_OFF:
                return context.getString(R.string.couponName50off);
            case Helper.Constants.COUPON_TYPE_EMPTY_COUPON:
                return context.getString(R.string.couponEmpty);
            default:
                return context.getString(R.string.couponUnk);
        }
    }

    public final String getDescription(final Context context) {
        switch (_type) {
            case Helper.Constants.COUPON_TYPE_FREE_LARGE_DRINK:
                return context.getString(R.string.couponDescLargeDrink);
            case Helper.Constants.COUPON_TYPE_FREE_LARGE_EXTRAS:
                return context.getString(R.string.couponDescLargeExtra);
            case Helper.Constants.COUPON_TYPE_50_OFF:
                return context.getString(R.string.couponDesc50off);
            case Helper.Constants.COUPON_TYPE_EMPTY_COUPON:
                return context.getString(R.string.couponEmpty);
            default:
                return context.getString(R.string.couponUnk);
        }
    }

    final public boolean isValidAnymore(final long dateNow) {
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        return dateNow > _expiryDate && !formatter.format(dateNow * 1000).equals(formatter.format(_expiryDate * 1000));
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
                for (final ShoppingItem meal : Helper.getInstance().getUser().getCart().getItems()) {
                    if (!meal.getProduct().isMeal())
                        continue;

                    if (meal.getMealDrink() == 0)
                        continue;

                    if (meal.isLargeDrink()) {
                        return true;
                    }
                }
            } break;
            case Helper.Constants.COUPON_TYPE_FREE_LARGE_EXTRAS: {
                for (final ShoppingItem meal : Helper.getInstance().getUser().getCart().getItems()) {
                    if (!meal.getProduct().isMeal())
                        continue;

                    if (meal.getMealExtra() == 0)
                        continue;

                    if (meal.isLargeExtra()) {
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
