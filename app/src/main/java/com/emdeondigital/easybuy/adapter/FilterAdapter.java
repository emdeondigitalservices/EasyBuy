package com.emdeondigital.easybuy.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.emdeondigital.easybuy.R;
import com.emdeondigital.easybuy.model.FilterItems;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<FilterItems> Acat = new ArrayList<>();

    public FilterAdapter(Context mContext, ArrayList<FilterItems> acat) {
        this.mContext = mContext;
        Acat = acat;
    }


    @Override
    public int getCount() {
        return Acat.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) mContext
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.filterlistviewitems, null);
        TextView mtxtName = (TextView) convertView.findViewById(R.id.text1);
        EditText checkBox = (EditText) convertView.findViewById(R.id.priceFilter);

        final int pos = position;


        //checkBox.setChecked(Acat.get(position).getSelected());

        checkBox.setTag(Acat.get(position));

        mtxtName.setText(Acat.get(position).getTec());
        return convertView;
    }

    public List<FilterItems> getAddOnist() {
        return Acat;
    }


}
