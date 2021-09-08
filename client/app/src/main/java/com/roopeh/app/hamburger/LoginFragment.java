package com.roopeh.app.hamburger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Objects;

import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        TextView registerButton = rootView.findViewById(R.id.loginRegisterLink);
        registerButton.setOnClickListener(v -> ((MainActivity)getActivity()).loadFragment(new RegisterFragment(), false));

        ImageButton loginButton = rootView.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            EditText userField = rootView.findViewById(R.id.loginUsername);
            EditText passField = rootView.findViewById(R.id.loginPassword);

            // todo: check for sql injections etc, and for correct user information obviously
            String user = userField.getText().toString();
            String pass = passField.getText().toString();

            Objects.requireNonNull((MainActivity)getActivity()).setUser(user, pass);
        });

        return rootView;
    }
}
