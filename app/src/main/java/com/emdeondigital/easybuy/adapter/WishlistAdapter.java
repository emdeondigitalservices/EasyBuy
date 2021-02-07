package com.emdeondigital.easybuy.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emdeondigital.easybuy.R;
import com.emdeondigital.easybuy.model.GenericProductModel;
import com.emdeondigital.easybuy.model.Product;
import com.emdeondigital.easybuy.model.SingleProductModel;
import com.emdeondigital.easybuy.products.IndividualProductActivity;
import com.emdeondigital.easybuy.users.WishlistActivity;

import java.util.ArrayList;

public class WishlistAdapter extends  RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    Context context;
    private ArrayList<SingleProductModel> productList;
    public static class WishlistViewHolder extends RecyclerView.ViewHolder{

        TextView cardname;
        ImageView cardimage;
        TextView cardprice;
        TextView cardcount;
        ImageView carddelete;

        View mView;
        public WishlistViewHolder(View v) {
            super(v);
            mView = v;
            cardname = v.findViewById(R.id.cart_prtitle);
            cardimage = v.findViewById(R.id.image_cartlist);
            cardprice = v.findViewById(R.id.cart_prprice);
            cardcount = v.findViewById(R.id.cart_prcount);
            carddelete = v.findViewById(R.id.deletecard);
        }
    }
    public WishlistAdapter(Context context, ArrayList<SingleProductModel> photoList) {
        this.context = context;
        this.productList = photoList;
    }

    @Override
    public WishlistAdapter.WishlistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_deals, parent, false);
        context=view.getContext();
        return new WishlistAdapter.WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WishlistAdapter.WishlistViewHolder viewHolder, int position) {
        final SingleProductModel model=productList.get(position);
        viewHolder.cardname.setText(model.getPrname());
        viewHolder.cardprice.setText("₹ "+model.getPrprice());
        viewHolder.cardcount.setText("Quantity : "+model.getNo_of_items());
        Glide.with(context).load(model.getPrimage()).into(viewHolder.cardimage);

        viewHolder.carddelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*getRef(position).removeValue();
                session.decreaseWishlistValue();
                startActivity(new Intent(WishlistActivity.this,WishlistActivity.class));
                finish();*/
            }
        });

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, IndividualProductActivity.class);
                intent.putExtra("product",new GenericProductModel(model.getPrid(),model.getPrname(),model.getPrimage(),model.getPrdesc(),Float.parseFloat(model.getPrprice())));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

}

