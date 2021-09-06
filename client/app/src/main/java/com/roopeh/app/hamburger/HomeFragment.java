package com.roopeh.app.hamburger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Objects;

import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        Button loginBut = rootView.findViewById(R.id.testLogin);
        Button logoutBut = rootView.findViewById(R.id.testLogout);
        Button shopBut = rootView.findViewById(R.id.testShopCart);

        loginBut.setOnClickListener(v -> Objects.requireNonNull((MainActivity)getActivity()).setUser("test", "test"));
        logoutBut.setOnClickListener(v -> Objects.requireNonNull((MainActivity)getActivity()).logoutUser());
        shopBut.setOnClickListener(v -> Objects.requireNonNull((MainActivity)getActivity()).loadFragment(new CartFragment()));

        return rootView;
    }
}
