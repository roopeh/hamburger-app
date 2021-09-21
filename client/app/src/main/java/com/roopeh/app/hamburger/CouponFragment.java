package com.roopeh.app.hamburger;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;

public class CouponFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_coupon, container, false);
        ListView couponList = rootView.findViewById(R.id.couponsList);

        CouponListAdapter adapter = new CouponListAdapter(getContext(), Helper.getInstance().getUser().getCoupons());
        couponList.setAdapter(adapter);

        return rootView;
    }
}

class CouponListAdapter extends BaseAdapter {
    final private Context _context;
    final private List<Coupon> _list;

    public CouponListAdapter(Context context, List<Coupon> list) {
        _context = context;
        _list = list;
    }

    @Override
    public int getCount() {
        return _list.size();
    }

    @Override
    public Object getItem(int position) {
        return _list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(_context, R.layout.coupon_list_item, null);
        Coupon coupon = _list.get(position);

        TextView name = view.findViewById(R.id.couponsName);
        name.setText(coupon.getName());

        TextView descr = view.findViewById(R.id.couponsDescription);
        descr.setText(coupon.getDescription());

        TextView date = view.findViewById(R.id.couponsDate);
        date.setText("Voimassa " + coupon.getExpiryDate() + " asti");

        return view;
    }
}
