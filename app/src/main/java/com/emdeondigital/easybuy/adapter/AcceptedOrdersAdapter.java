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
import com.emdeondigital.easybuy.model.SingleProductModel;

import java.util.ArrayList;
import java.util.Map;

public class AcceptedOrdersAdapter extends  RecyclerView.Adapter<AcceptedOrdersAdapter.AcceptedOrdersViewHolder> {

    Context context;
    private Map<String, ArrayList<SingleProductModel>> ordersMap;
    class AcceptedOrdersViewHolder extends RecyclerView.ViewHolder {

        TextView acceptedOrderId;
        View mView;

        AcceptedOrdersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            acceptedOrderId = itemView.findViewById(R.id.acceptedOrderId);
        }
    }

    public AcceptedOrdersAdapter(Context context, Map<String, ArrayList<SingleProductModel>> ordersMap) {
        this.context = context;
        this.ordersMap = ordersMap;
    }

    @Override
    public AcceptedOrdersAdapter.AcceptedOrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_acceptedorders, parent, false);
        context=view.getContext();
        return new AcceptedOrdersAdapter.AcceptedOrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AcceptedOrdersAdapter.AcceptedOrdersViewHolder holder, int position) {
        int i = 0;
        for (Map.Entry<String, ArrayList<SingleProductModel>> entry : ordersMap.entrySet()) {
            if(position == i){
                String key = entry.getKey();
                holder.acceptedOrderId.setText(key);
                break;
            }
            i++;
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PincodeActivity.class);
                intent.putExtra("orderList",ordersMap.get(holder.acceptedOrderId.getText()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ordersMap.size();
    }

}

