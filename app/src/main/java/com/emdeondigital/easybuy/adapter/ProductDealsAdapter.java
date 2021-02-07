package com.emdeondigital.easybuy.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emdeondigital.easybuy.R;
import com.emdeondigital.easybuy.model.Category;
import com.emdeondigital.easybuy.model.Product;
import com.emdeondigital.easybuy.products.IndividualProductActivity;

import java.util.ArrayList;

public class ProductDealsAdapter extends  RecyclerView.Adapter<ProductDealsAdapter.ProductDealsViewHolder> {

    Context context;
    private ArrayList<Product> productList;
    class ProductDealsViewHolder extends RecyclerView.ViewHolder {

        ImageView dealImage;
        TextView id,name,price;
        RatingBar ratingBar1;
        CardView itemCard;

        ProductDealsViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.photoId);
            name = itemView.findViewById(R.id.dealImageName);
            price = itemView.findViewById(R.id.price);
            dealImage = itemView.findViewById(R.id.dealImage);
            ratingBar1 = itemView.findViewById(R.id.ratingBar1);
            itemCard = itemView.findViewById(R.id.itemCard);
        }
    }

    public ProductDealsAdapter(Context context, ArrayList<Product> photoList) {
        this.context = context;
        this.productList = photoList;
    }

    @Override
    public ProductDealsAdapter.ProductDealsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_deals, parent, false);
        context=view.getContext();
        return new ProductDealsAdapter.ProductDealsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductDealsAdapter.ProductDealsViewHolder holder, int position) {
        final Product product=productList.get(position);
        holder.name.setText(productList.get(position).getName());
        holder.price.setText("₹ " +productList.get(position).getPrice());
        holder.ratingBar1.setRating(Float.parseFloat(product.getStarRating()));
        holder.itemCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context, IndividualProductActivity.class);
                intent.putExtra("product", product);
                context.startActivity(intent);
            }
        });
        String path = productList.get(position).getImageURL();
        Glide.with(context).load(path).into(holder.dealImage);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

}

