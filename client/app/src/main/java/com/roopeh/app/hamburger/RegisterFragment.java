package com.roopeh.app.hamburger;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.fragment.app.Fragment;

public class RegisterFragment extends Fragment implements ApiResponseInterface {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        // User could end up here when navigating with back button so make sure user is not logged in
        if (Helper.getInstance().getUser() != null) {
            Objects.requireNonNull((MainActivity)getActivity()).returnToPreviousFragment(false);
            return null;
        }

        final CheckBox showPassword = rootView.findViewById(R.id.registerShowPass);
        showPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            final EditText pass1 = rootView.findViewById(R.id.registerPass1);
            final EditText pass2 = rootView.findViewById(R.id.registerPass2);
            if (!isChecked) {
                pass1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                pass2.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                pass1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                pass2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });

        final ImageButton registerButton = rootView.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> handleRegister(rootView));

        // Make the text under register button also clickable
        final TextView registerButtonText = rootView.findViewById(R.id.registerButtonText);
        registerButtonText.setOnClickListener(v -> handleRegister(rootView));

        return rootView;
    }

    private void handleRegister(View rootView) {
        final EditText user = rootView.findViewById(R.id.registerUsername);
        final EditText firstName = rootView.findViewById(R.id.registerFirstname);
        final EditText lastName = rootView.findViewById(R.id.registerLastname);
        final EditText email = rootView.findViewById(R.id.registerEmail);
        final EditText phone = rootView.findViewById(R.id.registerNumber);
        final EditText pass1 = rootView.findViewById(R.id.registerPass1);
        final EditText pass2 = rootView.findViewById(R.id.registerPass2);

        // Check that all fields are filled
        if (TextUtils.isEmpty(user.getText()) || TextUtils.isEmpty(firstName.getText()) || TextUtils.isEmpty(lastName.getText()) || TextUtils.isEmpty(email.getText())
                || TextUtils.isEmpty(phone.getText()) || TextUtils.isEmpty(pass1.getText()) || TextUtils.isEmpty(pass2.getText())) {
            Toast.makeText(getContext(), getString(R.string.registerFieldsEmpty), Toast.LENGTH_LONG).show();
            return;
        }

        // Check for valid email address
        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
            Toast.makeText(getContext(), getString(R.string.registerEmailError), Toast.LENGTH_LONG).show();
            return;
        }

        // Check for valid phone number
        if (!Patterns.PHONE.matcher(phone.getText()).matches()) {
            Toast.makeText(getContext(), getString(R.string.registerNumberError), Toast.LENGTH_LONG).show();
            return;
        }

        // Check that password fields match
        if (!pass1.getText().toString().equals(pass2.getText().toString())) {
            Toast.makeText(getContext(), getString(R.string.registerPasswordError), Toast.LENGTH_LONG).show();
            return;
        }

        final Map<String, String> userData = new HashMap<>();
        userData.put("username", user.getText().toString().toUpperCase());
        userData.put("password", Helper.getInstance().encryptPasswordReturnInHex(pass1.getText().toString()));
        userData.put("first_name", firstName.getText().toString());
        userData.put("last_name", lastName.getText().toString());
        userData.put("email", email.getText().toString());
        userData.put("phone", phone.getText().toString());

        new ApiConnector(this).register(getContext(), userData);

        // Close keyboard
        Helper.getInstance().hideKeyboard(getActivity());
    }

    @Override
    public void onResponse(Helper.ApiResponseType apiResponse, Bundle bundle) {
        ApiJsonParser.parseDatabaseData(getContext(), apiResponse, bundle);

        if (bundle.getString("result").equals("true")) {
            Toast.makeText(getContext(), getString(R.string.registerSuccess), Toast.LENGTH_SHORT).show();
            Objects.requireNonNull((MainActivity)getActivity()).loadFragment(new LoginFragment(), false);
        }
    }
}
