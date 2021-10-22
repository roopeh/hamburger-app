package com.roopeh.app.hamburger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Objects;

import androidx.fragment.app.Fragment;

public class PaymentFragment extends Fragment implements ApiResponseInterface {
    final private Order _order;
    final private Coupon _coupon;

    private TextView successText;
    private Button successReturn;

    public PaymentFragment(Order order, Coupon usedCoupon) {
        _order = order;
        _coupon = usedCoupon;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_payment, container, false);

        final ImageButton returnButton = rootView.findViewById(R.id.paymentReturn);
        returnButton.setOnClickListener(v -> Objects.requireNonNull((MainActivity)getActivity()).returnToPreviousFragment(false));

        final Button payButton = rootView.findViewById(R.id.paymentButton);
        successText = rootView.findViewById(R.id.paymentSuccessText);
        successReturn = rootView.findViewById(R.id.paymentSuccessReturn);

        payButton.setOnClickListener(v -> {
            _order.setOrderDate(System.currentTimeMillis() / 1000);
            _order.setPickupDate();
            _order.setPaid(true);

            // Save to db
            new ApiConnector(this).saveOrder(getContext(), _order);
        });

        successReturn.setOnClickListener(v -> Objects.requireNonNull((MainActivity)getActivity()).loadFragment(new HomeFragment(), false));
        return rootView;
    }

    @Override
    public void onResponse(Helper.ApiResponseType apiResponse, Bundle bundle) {
        ApiJsonParser.parseDatabaseData(getContext(), apiResponse, bundle);

        if (bundle.getString("result").equals("true")) {
            final User user = Helper.getInstance().getUser();

            // Remove used coupon
            if (_coupon.getType() != Helper.Constants.COUPON_TYPE_EMPTY_COUPON)
                user.removeCoupon(_coupon);

            // Clear shopping items
            user.getCart().emptyCart();

            user.setCurrentOrder(_order);
            Objects.requireNonNull((MainActivity)getActivity()).createOrderTimer();

            // Make views visible
            successText.setVisibility(View.VISIBLE);
            successReturn.setVisibility(View.VISIBLE);
        }
    }
}
