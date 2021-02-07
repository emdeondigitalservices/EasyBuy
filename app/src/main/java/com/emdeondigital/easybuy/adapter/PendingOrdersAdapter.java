package com.emdeondigital.easybuy.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.emdeondigital.easybuy.PincodeActivity;
import com.emdeondigital.easybuy.R;
import com.emdeondigital.easybuy.admin.AdminOrderDetailsActivity;
import com.emdeondigital.easybuy.model.Location;
import com.emdeondigital.easybuy.model.SingleProductModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PendingOrdersAdapter extends  RecyclerView.Adapter<PendingOrdersAdapter.LocationViewHolder> {

    Context context;
    private Map<String, ArrayList<SingleProductModel>> ordersMap;
    private ArrayList<String> pinCodeList = new ArrayList<>();
    class LocationViewHolder extends RecyclerView.ViewHolder {

        TextView orderId;
        View mView;

        LocationViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            orderId = itemView.findViewById(R.id.orderId);
        }
    }

    public PendingOrdersAdapter(Context context, Map<String, ArrayList<SingleProductModel>> ordersMap) {
        this.context = context;
        this.ordersMap = ordersMap;
    }

    @Override
    public PendingOrdersAdapter.LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_pendingorders, parent, false);
        context=view.getContext();
        return new PendingOrdersAdapter.LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PendingOrdersAdapter.LocationViewHolder holder, int position) {
        int i = 0;
        for (Map.Entry<String, ArrayList<SingleProductModel>> entry : ordersMap.entrySet()) {
            if(position == i){
                String key = entry.getKey();
                holder.orderId.setText(key);
                break;
            }
            i++;
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AdminOrderDetailsActivity.class);
                intent.putExtra("orderId",holder.orderId.getText());
                intent.putExtra("orderList",ordersMap.get(holder.orderId.getText()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        Toast.makeText(context, "ordersMap.size() : "+ordersMap.size(), Toast.LENGTH_LONG).show();
        return ordersMap.size();
    }

}

