package com.roopeh.app.hamburger;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

            final String user = userField.getText().toString().toUpperCase();
            final String pass = passField.getText().toString();

            if (user.isEmpty()) {
                Toast.makeText(getContext(), "Syötä käyttäjänimi", Toast.LENGTH_SHORT).show();
                return;
            } else if (pass.isEmpty()) {
                Toast.makeText(getContext(), "Syötä salasana", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: password encryption
            new ApiConnector(this).login(getContext(), user, pass);
        });

        return rootView;
    }

    @Override
    public void onResponse(Helper.ApiResponseType apiResponse, Bundle bundle) {
        ApiJsonParser.parseDatabaseData(getContext(), apiResponse, bundle);

        // Check for possible current order
        if (Helper.getInstance().getUser() != null)
            Helper.getInstance().getUser().checkForCurrentOrder(Objects.requireNonNull((MainActivity)getActivity()));
    }
}
