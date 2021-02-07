package com.emdeondigital.easybuy.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emdeondigital.easybuy.R;
import com.emdeondigital.easybuy.model.GenericProductModel;
import com.emdeondigital.easybuy.model.SingleProductModel;
import com.emdeondigital.easybuy.products.IndividualProductActivity;
import com.emdeondigital.easybuy.sessions.UserSession;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AddToCartAdapter extends  RecyclerView.Adapter<AddToCartAdapter.ProductlistViewHolder> {

    Context context;
    private ArrayList<SingleProductModel> productList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    UserSession session;

    public static class ProductlistViewHolder extends RecyclerView.ViewHolder{

        TextView cart_prtitle;
        ImageView image_cartlist,deletecard;
        TextView cart_prprice;
        TextView cart_prcount;
        EditText quantityProductPage;
        Button decrementQuantity,incrementQuantity;

        View mView;
        public ProductlistViewHolder(View v) {
            super(v);
            mView = v;
            cart_prtitle = v.findViewById(R.id.cart_prtitle);
            image_cartlist = v.findViewById(R.id.image_cartlist);
            cart_prprice = v.findViewById(R.id.cart_prprice);
            cart_prcount = v.findViewById(R.id.cart_prcount);
            deletecard = v.findViewById(R.id.deletecard);
            quantityProductPage = v.findViewById(R.id.quantityProductPage);
            decrementQuantity = v.findViewById(R.id.decrementQuantity);
            incrementQuantity = v.findViewById(R.id.incrementQuantity);
        }
    }
    public AddToCartAdapter(Context context, ArrayList<SingleProductModel> productList, UserSession session) {
        this.context = context;
        this.productList = productList;
        this.session = session;
    }

    @Override
    public AddToCartAdapter.ProductlistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cart_item_layout, parent, false);
        context=view.getContext();
        return new AddToCartAdapter.ProductlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddToCartAdapter.ProductlistViewHolder viewHolder, int position) {
        final SingleProductModel model=productList.get(position);
        viewHolder.cart_prtitle.setText(model.getPrname());
        Glide.with(context).load(model.getPrimage()).into(viewHolder.image_cartlist);
        String totalPrice = String.valueOf(Integer.parseInt(model.getQnty())*Float.parseFloat(model.getSellingPrice()));
        viewHolder.cart_prprice.setText("₹ "+totalPrice);
        viewHolder.cart_prcount.setText("Qty: "+model.getQnty());
        viewHolder.quantityProductPage.setText(model.getQnty());

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, IndividualProductActivity.class);
                intent.putExtra("product",model);
                context.startActivity(intent);
            }
        });

        viewHolder.deletecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCartItem(model.getDocId());
                //Removing product from session
                Set<String> productSet = session.getKeyAddToCartProducts();
                productSet.remove(model.getPrid());
                session.setKeyAddToCartProducts(productSet);
                session.decreaseCartValue();
                context.startActivity(new Intent(context,context.getClass()));
                ((Activity)context).finish();
            }
        });

        viewHolder.decrementQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(model.getQnty());
                if (quantity > 1) {
                    quantity--;
                    viewHolder.quantityProductPage.setText(String.valueOf(quantity));
                    String totalPrice = String.valueOf(quantity*Float.parseFloat(model.getSellingPrice()));
                    viewHolder.cart_prprice.setText("₹ "+totalPrice);
                    viewHolder.cart_prcount.setText("Qty: "+quantity);
                    updateQnty(model.getDocId(),String.valueOf(quantity));
                }
            }
        });
        viewHolder.incrementQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(model.getQnty());
                if (quantity < 500) {
                    quantity++;
                    viewHolder.quantityProductPage.setText(String.valueOf(quantity));
                    String totalPrice = String.valueOf(quantity*Float.parseFloat(model.getSellingPrice()));
                    viewHolder.cart_prprice.setText("₹ "+totalPrice);
                    viewHolder.cart_prcount.setText("Qty: "+quantity);
                    updateQnty(model.getDocId(),String.valueOf(quantity));
                } else {
                    Toast.makeText(context, "Product Count Must be less than 500", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateQnty(String docId, String qnty){
        DocumentReference washingtonRef = db.collection("cart").document(docId);

        //Set the "isCapital" field of the city 'DC'
        washingtonRef
                .update("quantity", qnty)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void removeCartItem(String itemId){
        db.collection("cart").document(itemId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    public void addToCart(SingleProductModel model){
        Map<String, Object> user = new HashMap<>();
        user.put("prname", model.getPrname());
        user.put("quantity", model.getQnty());
        try {
            // Add a new document with a generated ID
            db.collection("cart").document(model.getPrname())
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Hiding the progressDialog after done uploading.

                            Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();
                            // Getting image upload ID.
                            //String userId = documentReference.getId();

                            // Adding image upload id s child element into databaseReference.
                            //databaseReference.child(userId).setValue(userInfo);
                            //Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                            //startActivity(i);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Hiding the progressDialog after done uploading.
                            //progressDialog.dismiss();
                            Toast.makeText(context, "Failure", Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (Exception e) {
            // Hiding the progressDialog after done uploading.
            //progressDialog.dismiss();
            e.printStackTrace();
        }

    }

}

