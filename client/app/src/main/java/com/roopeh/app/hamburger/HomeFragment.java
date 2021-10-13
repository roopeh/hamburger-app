package com.roopeh.app.hamburger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        final RecyclerView grid = rootView.findViewById(R.id.homeGrid);
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

        final HomeGridAdapter adapter = new HomeGridAdapter(content);
        grid.setLayoutManager(new GridLayoutManager(getContext(), 2));
        grid.addItemDecoration(new RecyclerViewDivider(Helper.Constants.GRID_DIVIDER, Helper.Constants.GRID_DIVIDER, 2));
        grid.setAdapter(adapter);

        return rootView;
    }
}

class HomeGridAdapter extends RecyclerView.Adapter<HomeGridAdapter.ViewHolder> {
    final private List<Integer> _list;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final private TextView defaultText;
        final private RelativeLayout extraLayout;
        final private TextView jobText;
        final private TextView socialText;
        final private Button devButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            defaultText = itemView.findViewById(R.id.homeGridText);
            extraLayout = itemView.findViewById(R.id.homeGridExtraLayout);
            jobText = itemView.findViewById(R.id.homeGridSecondText);
            socialText = itemView.findViewById(R.id.homeGridThirdText);
            devButton = itemView.findViewById(R.id.homeGridDevButton);
        }

        final public TextView getDefaultText() {
            return defaultText;
        }

        final public RelativeLayout getExtraLayout() {
            return extraLayout;
        }

        final public TextView getJobText() {
            return jobText;
        }

        final public TextView getSocialText() {
            return socialText;
        }

        final public Button getDevButton() {
            return devButton;
        }
    }

    public HomeGridAdapter(List<Integer> list) {
        _list = list;
    }

    @NonNull
    @Override
    public HomeGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View listItem = inflater.inflate(R.layout.grid_home, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeGridAdapter.ViewHolder holder, int position) {
        switch (_list.get(position)) {
            case HomeFragment.TYPE_DEFAULT: {
                holder.getDefaultText().setVisibility(View.VISIBLE);
                holder.getDefaultText().setText("");
            } break;
            case HomeFragment.TYPE_JOB:
            case HomeFragment.TYPE_SOME: {
                holder.getExtraLayout().setVisibility(View.VISIBLE);

                if (_list.get(position) == HomeFragment.TYPE_JOB) {
                    holder.getJobText().setVisibility(View.VISIBLE);
                } else {
                    holder.getSocialText().setVisibility(View.VISIBLE);
                }
            } break;
            case HomeFragment.TYPE_LOGIN:
            case HomeFragment.TYPE_LOGOUT: {
                holder.getDevButton().setVisibility(View.VISIBLE);

                if (_list.get(position) == HomeFragment.TYPE_LOGIN)
                    holder.getDevButton().setText("Login");
                else
                    holder.getDevButton().setText("Logout");

                holder.getDevButton().setOnClickListener(v -> {
                    if (_list.get(position) == HomeFragment.TYPE_LOGIN)
                        Helper.getInstance().setUser("test", "test");
                    else
                        Helper.getInstance().logoutUser();
                });
            } break;
            default: break;
        }
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }
}
