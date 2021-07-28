package com.example.hamburger;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MenuAdapter extends ArrayAdapter<String> {
    Context _context;
    int _resourceId;
    String[] _data;

    public MenuAdapter(Context mContext, int resourceId, String[] data) {
        super(mContext, resourceId, data);
        this._context = mContext;
        this._resourceId = resourceId;
        this._data = data;
    }

    public View getView(int pos, View convertView, ViewGroup parent) {
        View listItem = convertView;

        LayoutInflater inflater = ((Activity) _context).getLayoutInflater();
        listItem = inflater.inflate(_resourceId, parent, false);

        TextView text = listItem.findViewById(R.id.menuItemText);
        text.setText(_data[pos]);

        return listItem;
    }
}
