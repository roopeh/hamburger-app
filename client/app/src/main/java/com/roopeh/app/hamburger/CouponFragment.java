package com.roopeh.app.hamburger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CouponFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_coupon, container, false);
        final RelativeLayout layout = rootView.findViewById(R.id.couponsEmptyLayout);
        final RecyclerView couponList = rootView.findViewById(R.id.couponsList);

        final User user = Helper.getInstance().getUser();
        // Check if user has any coupons
        if (user == null || user.getCoupons().isEmpty()) {
            layout.setVisibility(View.VISIBLE);
            return rootView;
        }

        couponList.setVisibility(View.VISIBLE);

        final CouponListAdapter adapter = new CouponListAdapter(user.getCoupons());
        couponList.setLayoutManager(new LinearLayoutManager(getContext()));
        couponList.addItemDecoration(new RecyclerViewDivider(30));
        couponList.setAdapter(adapter);

        return rootView;
    }
}

class CouponListAdapter extends RecyclerView.Adapter<CouponListAdapter.ViewHolder> {
    final private List<Coupon> _list;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final private TextView name;
        final private TextView descr;
        final private TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.couponsName);
            descr = itemView.findViewById(R.id.couponsDescription);
            date = itemView.findViewById(R.id.couponsDate);
        }

        final public TextView getCouponName() {
            return name;
        }

        final public TextView getCouponDescription() {
            return descr;
        }

        final public TextView getCouponDate() {
            return date;
        }
    }

    public CouponListAdapter(List<Coupon> list) {
        _list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View listItem = inflater.inflate(R.layout.coupon_list_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Coupon coupon = _list.get(position);
        holder.getCouponName().setText(coupon.getName());
        holder.getCouponDescription().setText(coupon.getDescription());
        holder.getCouponDate().setText("Voimassa " + coupon.getExpiryDate() + " asti");
    }

    @Override
    public int getItemCount() {
        return _list.size();
    }
}
