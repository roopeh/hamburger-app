package com.roopeh.app.hamburger;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    // Type definitions for GridView
    final static public int TYPE_DEFAULT = 0;
    final static public int TYPE_JOB = 1;
    final static public int TYPE_SOME = 2;
    // Types for developers
    final static public int TYPE_LOGIN = 5;
    final static public int TYPE_LOGOUT = 6;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        final Button orderButton = rootView.findViewById(R.id.homeOrderButton);
        final ExpendableGridView grid = rootView.findViewById(R.id.homeGrid);
        final List<Integer> content = new ArrayList<>();
        final User user = Helper.getInstance().getUser();

        // If user is not logged in, has no current order or the order is ready, hide current order button
        if (user == null || user.getCurrentOrder() == null || user.getCurrentOrder().isOrderReady()) {
            orderButton.setVisibility(View.GONE);
            // Adjust grid's layout params
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)grid.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.homeMainImage);
            grid.setLayoutParams(params);
        } else {
            orderButton.setOnClickListener(v -> Objects.requireNonNull((MainActivity)getActivity()).loadFragment(new CurrentOrderFragment(), false));
        }

        // Add stuff to grids
        // First 4 are ad / news grids
        for (int i = 0; i < 4; ++i)
            content.add(TYPE_DEFAULT);

        // Login and logout buttons for quick access (only in beta version)
        content.add(TYPE_LOGIN);
        content.add(TYPE_LOGOUT);

        // Next job application grid
        content.add(TYPE_JOB);
        // Final grid is social media
        content.add(TYPE_SOME);

        final HomeGridAdapter adapter = new HomeGridAdapter(getContext(), content);
        grid.setAdapter(adapter);
        grid.setExpanded(true);

        return rootView;
    }
}

class HomeGridAdapter extends BaseAdapter {
    final private Context _context;
    final private List<Integer> _content;

    public HomeGridAdapter(Context context, List<Integer> content) {
        _context = context;
        _content = content;
    }

    @Override
    public int getCount() {
        return _content.size();
    }

    @Override
    public Integer getItem(int position) {
        return _content.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = View.inflate(_context, R.layout.grid_home, null);

        switch (getItem(position)) {
            case HomeFragment.TYPE_DEFAULT: {
                final TextView defaultText = view.findViewById(R.id.homeGridText);
                defaultText.setVisibility(View.VISIBLE);
                defaultText.setText("");
            } break;
            case HomeFragment.TYPE_JOB:
            case HomeFragment.TYPE_SOME: {
                final RelativeLayout extraLayout = view.findViewById(R.id.homeGridExtraLayout);
                final TextView jobText = view.findViewById(R.id.homeGridSecondText);
                final TextView someText = view.findViewById(R.id.homeGridThirdText);
                extraLayout.setVisibility(View.VISIBLE);

                if (getItem(position) == HomeFragment.TYPE_JOB) {
                    jobText.setVisibility(View.VISIBLE);
                } else {
                    someText.setVisibility(View.VISIBLE);
                }
            } break;
            case HomeFragment.TYPE_LOGIN:
            case HomeFragment.TYPE_LOGOUT: {
                final Button button = view.findViewById(R.id.homeGridDevButton);
                button.setVisibility(View.VISIBLE);

                if (getItem(position) == HomeFragment.TYPE_LOGIN)
                    button.setText("Login");
                else
                    button.setText("Logout");

                button.setOnClickListener(v -> {
                    if (getItem(position) == HomeFragment.TYPE_LOGIN)
                        Helper.getInstance().setUser("test", "test");
                    else
                        Helper.getInstance().logoutUser();
                });
            } break;
            default: break;
        }

        return view;
    }
}
