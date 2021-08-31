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

import androidx.fragment.app.Fragment;

public class RegisterFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        CheckBox showPassword = rootView.findViewById(R.id.registerShowPass);
        showPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            EditText pass1 = rootView.findViewById(R.id.registerPass1);
            EditText pass2 = rootView.findViewById(R.id.registerPass2);
            if (!isChecked) {
                pass1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                pass2.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                pass1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                pass2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });

        ImageButton registerButton = rootView.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> handleRegister(rootView));

        // Make the text under register button also clickable
        TextView registerButtonText = rootView.findViewById(R.id.registerButtonText);
        registerButtonText.setOnClickListener(v -> handleRegister(rootView));

        return rootView;
    }

    private void handleRegister(View rootView) {
        EditText user = rootView.findViewById(R.id.registerUsername);
        EditText firstName = rootView.findViewById(R.id.registerFirstname);
        EditText lastName = rootView.findViewById(R.id.registerLastname);
        EditText email = rootView.findViewById(R.id.registerEmail);
        EditText phone = rootView.findViewById(R.id.registerNumber);
        EditText pass1 = rootView.findViewById(R.id.registerPass1);
        EditText pass2 = rootView.findViewById(R.id.registerPass2);

        // Check that all fields are filled
        if (TextUtils.isEmpty(user.getText()) || TextUtils.isEmpty(firstName.getText()) || TextUtils.isEmpty(lastName.getText()) || TextUtils.isEmpty(email.getText())
                || TextUtils.isEmpty(phone.getText()) || TextUtils.isEmpty(pass1.getText()) || TextUtils.isEmpty(pass2.getText())) {
            Toast.makeText(getContext(), "Kaikki kentät tulee täyttää", Toast.LENGTH_LONG).show();
            return;
        }

        // Check for valid email address
        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
            Toast.makeText(getContext(), "Sähköpostiosoite on virheellinen", Toast.LENGTH_LONG).show();
            return;
        }

        // Check for valid phone number
        if (!Patterns.PHONE.matcher(phone.getText()).matches()) {
            Toast.makeText(getContext(), "Puhelinnumero on virheellinen", Toast.LENGTH_LONG).show();
            return;
        }

        // Check that password fields match
        if (!pass1.getText().toString().equals(pass2.getText().toString())) {
            Toast.makeText(getContext(), "Salasanat eivät täsmää", Toast.LENGTH_LONG).show();
            return;
        }

        // todo: api call
        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
    }
}
