package com.roopeh.app.hamburger;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import androidx.fragment.app.Fragment;

public class UserFragment extends Fragment implements ApiResponseInterface {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_user, container, false);

        // User could end up here when navigating with back button so make sure user is logged in
        if (Helper.getInstance().getUser() == null) {
            Objects.requireNonNull((MainActivity)getActivity()).returnToPreviousFragment(false);
            return null;
        }

        final TextView userName = rootView.findViewById(R.id.userHelloText);
        final Button logoutButton = rootView.findViewById(R.id.userLogoutButton);
        final EditText firstPass = rootView.findViewById(R.id.userPasswordFirst);
        final EditText secondPass = rootView.findViewById(R.id.userPasswordSecond);
        final CheckBox checkBox = rootView.findViewById(R.id.userPasswordShow);
        final Button passButton = rootView.findViewById(R.id.userPasswordButton);

        final User user = Helper.getInstance().getUser();

        userName.setText("Hei, " + user.getFirstName() + " " + user.getLastName() + "!");

        // Logout button
        logoutButton.setOnClickListener(v -> {
            new ApiConnector(this).logout(getContext());
        });

        // Show passwords?
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                firstPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                secondPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                firstPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                secondPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });

        // Change passwords button
        passButton.setOnClickListener(v -> handlePassChange(firstPass.getText().toString(), secondPass.getText().toString()));

        return rootView;
    }

    private void handlePassChange(String pass1, String pass2) {
        // Check that password fields match
        if (!pass1.equals(pass2)) {
            Toast.makeText(getContext(), "Salasanat eivät täsmää", Toast.LENGTH_LONG).show();
            return;
        }

        // todo: api call
        Toast.makeText(getContext(), "Success!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(Helper.ApiResponseType apiResponse, Bundle bundle) {
        ApiJsonParser.parseDatabaseData(getContext(), apiResponse, bundle);
        Helper.getInstance().logoutUser();
        Objects.requireNonNull((MainActivity)getActivity()).loadFragment(new HomeFragment(), false);
    }
}
