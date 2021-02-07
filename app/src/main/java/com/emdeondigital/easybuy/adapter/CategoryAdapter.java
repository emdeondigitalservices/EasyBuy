package com.emdeondigital.easybuy.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emdeondigital.easybuy.MainActivity;
import com.emdeondigital.easybuy.R;
import com.emdeondigital.easybuy.model.Category;
import com.emdeondigital.easybuy.model.GenericProductModel;
import com.emdeondigital.easybuy.model.SingleProductModel;
import com.emdeondigital.easybuy.products.IndividualProductActivity;
import com.emdeondigital.easybuy.products.ProductListingActivity;

import java.util.ArrayList;

public class CategoryAdapter extends  RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    Context context;
    private ArrayList<Category> categoryList;
    class CategoryViewHolder extends RecyclerView.ViewHolder {

        ImageView imagename;
        View mView;

        CategoryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            imagename = itemView.findViewById(R.id.imagename);
        }
    }

    public CategoryAdapter(Context context, ArrayList<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_categories, parent, false);
        context=view.getContext();
        return new CategoryAdapter.CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryAdapter.CategoryViewHolder holder, int position) {
        final Category category=categoryList.get(position);
        String path = categoryList.get(position).getImageURL();
        Glide.with(context).load(path).into(holder.imagename);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductListingActivity.class);
                intent.putExtra("categoryid",category.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

}

