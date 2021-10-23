package com.roopeh.app.hamburger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment implements ApiResponseInterface {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        final TextView registerButton = rootView.findViewById(R.id.loginRegisterLink);
        registerButton.setOnClickListener(v -> Objects.requireNonNull((MainActivity)getActivity()).loadFragment(new RegisterFragment(), false));

        final ImageButton loginButton = rootView.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            if (Helper.getInstance().getUser() != null) {
                Toast.makeText(getContext(), "Olet jo kirjautunut sisään", Toast.LENGTH_SHORT).show();
                return;
            }

            final EditText userField = rootView.findViewById(R.id.loginUsername);
            final EditText passField = rootView.findViewById(R.id.loginPassword);

            final String user = userField.getText().toString();
            final String pass = passField.getText().toString();

            if (user.isEmpty()) {
                Toast.makeText(getContext(), "Syötä käyttäjänimi", Toast.LENGTH_SHORT).show();
                return;
            } else if (pass.isEmpty()) {
                Toast.makeText(getContext(), "Syötä salasana", Toast.LENGTH_SHORT).show();
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

        if (!bundle.getString("result").equals("true"))
            return;

        // Check for possible current order
        if (Helper.getInstance().getUser() != null)
            Helper.getInstance().getUser().checkForCurrentOrder(Objects.requireNonNull((MainActivity)getActivity()));

        // Redirect to home fragment
        Objects.requireNonNull((MainActivity)getActivity()).loadFragment(new HomeFragment(), false);
    }
}
