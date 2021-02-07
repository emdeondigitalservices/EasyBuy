package com.emdeondigital.easybuy.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emdeondigital.easybuy.PincodeActivity;
import com.emdeondigital.easybuy.R;
import com.emdeondigital.easybuy.model.Category;
import com.emdeondigital.easybuy.model.Location;
import com.emdeondigital.easybuy.products.ProductListingActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationAdapter extends  RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    Context context;
    private ArrayList<Location> locationList;
    private Map<String, List<String>> uniqueStateName = new HashMap<String,List<String>>();
    private ArrayList<String> pinCodeList = new ArrayList<>();
    class LocationViewHolder extends RecyclerView.ViewHolder {

        TextView stateName;
        View mView;
        CardView stateCardView;

        LocationViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            stateName = itemView.findViewById(R.id.stateName);
            stateCardView = itemView.findViewById(R.id.stateCardView);
        }
    }

    public LocationAdapter(Context context, ArrayList<Location> locationList) {
        this.context = context;
        this.locationList = locationList;
    }

    @Override
    public LocationAdapter.LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_locations, parent, false);
        context=view.getContext();
        return new LocationAdapter.LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LocationAdapter.LocationViewHolder holder, int position) {
        final Location location = locationList.get(position);
        pinCodeList.add(locationList.get(position).getPinCode());
        if(!uniqueStateName.containsKey(locationList.get(position).getStateName())) {
            uniqueStateName.put(locationList.get(position).getStateName(), pinCodeList);
            holder.stateName.setText(locationList.get(position).getStateName());
        }
        holder.stateCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PincodeActivity.class);
                intent.putExtra("locationList",locationList);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

}

