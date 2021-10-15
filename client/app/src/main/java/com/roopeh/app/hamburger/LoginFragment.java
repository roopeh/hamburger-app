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
            final EditText userField = rootView.findViewById(R.id.loginUsername);
            final EditText passField = rootView.findViewById(R.id.loginPassword);

            final String user = userField.getText().toString().toUpperCase();
            final String pass = passField.getText().toString();

            // TODO: password encryption
            new ApiConnector(this).login(getContext(), user, pass);
        });

        return rootView;
    }

    @Override
    public void onResponse(Helper.ApiResponseType apiResponse, Bundle bundle) {
        onLoginResponse(getContext(), apiResponse, bundle);
    }

    public static void onLoginResponse(Context context, Helper.ApiResponseType apiResponse, Bundle bundle) {
        if (apiResponse != Helper.ApiResponseType.LOGIN)
            return;

        final String response = bundle.getString("result");
        if (response == null || response.isEmpty() || response.equals("error")) {
            Toast.makeText(context, "Error in the API", Toast.LENGTH_SHORT).show();
            return;
        } else if (response.equals("false")) {
            Toast.makeText(context, "Virheellinen käyttäjätunnus tai salasana", Toast.LENGTH_SHORT).show();
            return;
        }

        final int id = Integer.parseInt(bundle.getString("id"));
        final User user = new User(id, bundle.getString("username"));
        user.setFirstName(bundle.getString("first_name"));
        user.setLastName(bundle.getString("last_name"));
        user.setEmail(bundle.getString("email"));
        user.setPhoneNumber(bundle.getString("phone_number"));

        Toast.makeText(context, "Hei, " + user.getFirstName(), Toast.LENGTH_SHORT).show();
    }
}
