package com.emdeondigital.easybuy.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.emdeondigital.easybuy.PincodeActivity;
import com.emdeondigital.easybuy.R;
import com.emdeondigital.easybuy.model.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatePinCodeAdapter extends  RecyclerView.Adapter<StatePinCodeAdapter.LocationViewHolder> {

    Context context;
    private Map<String, ArrayList<Location>> statePincode;
    private Map<String, List<String>> uniqueStateName = new HashMap<String,List<String>>();
    private ArrayList<String> pinCodeList = new ArrayList<>();
    class LocationViewHolder extends RecyclerView.ViewHolder {

        TextView stateName;
        View mView;

        LocationViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            stateName = itemView.findViewById(R.id.stateName);
        }
    }

    public StatePinCodeAdapter(Context context, Map<String, ArrayList<Location>> statePincode) {
        this.context = context;
        this.statePincode = statePincode;
    }

    @Override
    public StatePinCodeAdapter.LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_locations, parent, false);
        context=view.getContext();
        return new StatePinCodeAdapter.LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StatePinCodeAdapter.LocationViewHolder holder, int position) {
        int i = 0;
        for (Map.Entry<String, ArrayList<Location>> entry : statePincode.entrySet()) {
            if(position == i){
                String key = entry.getKey();
                holder.stateName.setText(key);
                break;
            }
            i++;
        }
        /*final List<String> location = statePincode;
        pinCodeList.add(locationList.get(position).getPinCode());
        if(!uniqueStateName.containsKey(locationList.get(position).getStateName())) {
            uniqueStateName.put(locationList.get(position).getStateName(), pinCodeList);
            holder.stateName.setText(locationList.get(position).getStateName());
        }*/
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PincodeActivity.class);
                intent.putExtra("locationList",statePincode.get(holder.stateName.getText()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return statePincode.size();
    }

}

