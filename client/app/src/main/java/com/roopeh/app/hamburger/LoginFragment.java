package com.roopeh.app.hamburger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Objects;

import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment implements ApiResponseInterface {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        // User could end up here when navigating with back button so make sure user is not logged in
        if (Helper.getInstance().getUser() != null) {
            Objects.requireNonNull((MainActivity)getActivity()).returnToPreviousFragment(false);
            return null;
        }

        final TextView registerButton = rootView.findViewById(R.id.loginRegisterLink);
        registerButton.setOnClickListener(v -> Objects.requireNonNull((MainActivity)getActivity()).loadFragment(new RegisterFragment(), false));

        final ImageButton loginButton = rootView.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            final EditText userField = rootView.findViewById(R.id.loginUsername);
            final EditText passField = rootView.findViewById(R.id.loginPassword);

            final String user = userField.getText().toString();
            final String pass = passField.getText().toString();

            if (user.isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.loginMissingUsername), Toast.LENGTH_SHORT).show();
                return;
            } else if (pass.isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.loginMissingPassword), Toast.LENGTH_SHORT).show();
                return;
            }

            // Encrypt password with SHA-256
            final String encryptedPass = Helper.getInstance().encryptPasswordReturnInHex(pass);
            new ApiConnector(this).login(getContext(), user.toUpperCase(), encryptedPass);
        });

        return rootView;
    }

    @Override
    public void onResponse(Helper.ApiResponseType apiResponse, Bundle bundle) {
        ApiJsonParser.parseDatabaseData(getContext(), apiResponse, bundle);
        onPostLogin(Objects.requireNonNull((MainActivity)getActivity()));

        if (!bundle.getString("result").equals("true"))
            return;

        // Redirect to home fragment
        Objects.requireNonNull((MainActivity)getActivity()).loadFragment(new HomeFragment(), false);
    }

    public static void onPostLogin(MainActivity activity) {
        final User user = Helper.getInstance().getUser();
        if (user != null) {
            // Check for possible current order
            user.checkForCurrentOrder(activity);

            // On first login, give user all coupons with 1 month expiry time
            if (user.isFirstLogin()) {
                // Generate expiry date
                final Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, 1);
                final long expiryDate = calendar.getTimeInMillis() / 1000;

                int type = Helper.Constants.COUPON_TYPE_FREE_LARGE_DRINK;
                while (type <= Helper.Constants.COUPON_TYPE_50_OFF) {
                    // Add two of each coupon
                    for (int i = 0; i < 2; ++i) {
                        long date = expiryDate;

                        if (Helper.Constants.TEST_COUPON_EXPIRY == 1) {
                            // For testing purposes, set some coupons with old expiry date
                            if (i == 1) {
                                final Calendar c = Calendar.getInstance();
                                c.add(Calendar.DAY_OF_MONTH, -1);
                                date = c.getTimeInMillis() / 1000;
                            }
                        }

                        user.addCoupon(new Coupon(type, date));
                    }

                    ++type;
                }
            }
        }
    }
}
