package com.emdeondigital.easybuy.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.emdeondigital.easybuy.LoginActivity;
import com.emdeondigital.easybuy.R;
import com.emdeondigital.easybuy.adapter.ProductDealsAdapter;
import com.emdeondigital.easybuy.adapter.WishlistAdapter;
import com.emdeondigital.easybuy.model.GenericProductModel;
import com.emdeondigital.easybuy.model.Product;
import com.emdeondigital.easybuy.model.SingleProductModel;
import com.emdeondigital.easybuy.products.IndividualProductActivity;
import com.emdeondigital.easybuy.products.ProductListingActivity;
import com.emdeondigital.easybuy.sessions.UserSession;
import com.emdeondigital.easybuy.util.GeneralMethods;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WishlistActivity extends AppCompatActivity {

    private UserSession session;
    private HashMap<String,String> user;
    private String name,email,photo,mobile,userId;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private LottieAnimationView tv_no_item;
    private FrameLayout activitycartlist;
    private LottieAnimationView emptycart;
    private ProductListAdapter wishlistAdapter;
    private static TextView cartCount;
    private AppBarLayout appBarLayout;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("WishList");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getValues();

        tv_no_item = findViewById(R.id.tv_no_cards);
        activitycartlist = findViewById(R.id.frame_container);
        emptycart = findViewById(R.id.empty_cart);

        appBarLayout = findViewById(R.id.toolbarwrap);
        cartCount = appBarLayout.findViewById(R.id.cartCount);
        if(session.getCartValue() > 0) {
            cartCount.setText(String.valueOf(session.getCartValue()));
        }else{
            cartCount.setVisibility(View.GONE);
        }

        Set<String> productIds = new HashSet<>();
        productIds = session.getWishListProducts();
        if(session.getWishlistValue()>0 || (productIds != null && productIds.size() > 0)) {
            getWishList(productIds);
        }else if(session.getWishlistValue() == 0)  {
            tv_no_item.setVisibility(View.GONE);
            activitycartlist.setVisibility(View.GONE);
            emptycart.setVisibility(View.VISIBLE);
        }
    }

    private void getWishList(Set<String> productIds){
            List<String> productIdsList = GeneralMethods.convertSetToList(productIds);
            db.collection("products")
                    .whereIn("productId", productIdsList)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(WishlistActivity.this, "Success", Toast.LENGTH_LONG).show();
                                ArrayList<SingleProductModel> productList = new ArrayList<SingleProductModel>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    SingleProductModel product = new SingleProductModel();
                                    product.setUserId(userId);
                                    product.setPrid(document.getId());
                                    product.setPrname(document.getData().get("name").toString());
                                    product.setPrimage(document.getData().get("imageURL").toString());
                                    product.setPrprice(document.getData().get("price").toString());
                                    product.setSellingPrice(document.getData().get("sellingprice").toString());
                                    product.setDiscount(document.getData().get("discount").toString());
                                    product.setStarRating(document.getData().get("star").toString());
                                    productList.add(product);
                                }
                                generateProductList(productList);
                            } else {
                                Toast.makeText(WishlistActivity.this, "Exception", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

    }

    /*Method to generate List of employees using RecyclerView with custom adapter*/
    private void generateProductList(ArrayList<SingleProductModel> productWishList) {
        mRecyclerView = findViewById(R.id.wishlistrecyclerview);
        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }

        wishlistAdapter = new ProductListAdapter(this,productWishList,session);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(WishlistActivity.this);
        //GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(wishlistAdapter);
    }


    public static class ProductListAdapter extends  RecyclerView.Adapter<WishlistActivity.ProductListAdapter.ProductListViewHolder> {

        Context context;
        private ArrayList<SingleProductModel> productList;
        UserSession session;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        public static class ProductListViewHolder extends RecyclerView.ViewHolder{

            public final TextView product_name,product_short_desc,brand_name,product_price,discount,product_add,selling_price;
            ImageView product_img,deletecard;
            RatingBar starRating;
            EditText quantityProductPage;
            Button decrementQuantity,incrementQuantity;

            View mView;

            public ProductListViewHolder(View v) {
                super(v);
                mView = v;
                product_name = v.findViewById(R.id.product_name);
                product_short_desc = v.findViewById(R.id.product_short_desc);
                brand_name = v.findViewById(R.id.brand_name);
                product_img = v.findViewById(R.id.product_img);
                product_price = v.findViewById(R.id.product_price);
                discount = v.findViewById(R.id.discount);
                product_add = v.findViewById(R.id.product_add);
                selling_price = v.findViewById(R.id.selling_price);
                starRating = v.findViewById(R.id.starRating);
                quantityProductPage = v.findViewById(R.id.quantityProductPage);
                deletecard = v.findViewById(R.id.deletecard);
                decrementQuantity = v.findViewById(R.id.decrementQuantity);
                incrementQuantity = v.findViewById(R.id.incrementQuantity);
            }
        }
        public ProductListAdapter(Context context, ArrayList<SingleProductModel> photoList, UserSession session) {
            this.context = context;
            this.productList = photoList;
            this.session = session;
        }

        @Override
        public WishlistActivity.ProductListAdapter.ProductListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.row_product_list, parent, false);
            context=view.getContext();
            return new WishlistActivity.ProductListAdapter.ProductListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(WishlistActivity.ProductListAdapter.ProductListViewHolder viewHolder, int position) {
            final SingleProductModel model=productList.get(position);
            viewHolder.product_name.setText(model.getPrname());
            viewHolder.product_price.setText("₹ "+model.getPrprice(), TextView.BufferType.SPANNABLE);
            Glide.with(context).load(model.getPrimage()).into(viewHolder.product_img);
            viewHolder.discount.setText(model.getDiscount());
            viewHolder.starRating.setRating(Float.parseFloat(model.getStarRating()));
            viewHolder.selling_price.setText("₹ "+model.getSellingPrice());
            viewHolder.quantityProductPage.setText("1");

            Spannable spannable = (Spannable) viewHolder.product_price.getText();

            spannable.setSpan(new StrikethroughSpan(), model.getSellingPrice().length(),
                    ("₹ "+model.getPrprice()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, IndividualProductActivity.class);
                    intent.putExtra("product",new GenericProductModel(model.getPrid(),model.getPrname(),
                            model.getPrimage(),model.getPrdesc(),Float.parseFloat(model.getPrprice())));
                    context.startActivity(intent);
                }
            });
            viewHolder.product_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText qty = viewHolder.quantityProductPage;
                    model.setQnty(qty.getText().toString());
                    addToCart(model,viewHolder);
                }
            });
            viewHolder.decrementQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = Integer.parseInt(model.getQnty());
                    if (quantity > 1) {
                        quantity--;
                        viewHolder.quantityProductPage.setText(String.valueOf(quantity));
                        updateQnty(model,String.valueOf(quantity));
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
                        updateQnty(model,String.valueOf(quantity));
                    } else {
                        Toast.makeText(context, "Product Count Must be less than 500", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        private void updateQnty(SingleProductModel model,String qnty){
            String docId = model.getDocId();
            DocumentReference washingtonRef = db.collection("cart").document(docId);

            //Set the "isCapital" field of the city 'DC'
            washingtonRef
                    .update("quantity", qnty)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            model.setQnty(qnty);
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

        public void addToCart(SingleProductModel model, WishlistActivity.ProductListAdapter.ProductListViewHolder viewHolder){
            Map<String, Object> cart = new HashMap<>();
            cart.put("uid", model.getUserId());
            cart.put("prid", model.getPrid());
            cart.put("prname", model.getPrname());
            cart.put("imageurl", model.getPrimage());
            cart.put("quantity", model.getQnty());
            cart.put("sellingprice", model.getSellingPrice());
            cart.put("status","N");
            try {
                // Add a new document with a generated ID
                db.collection("cart")
                        .add(cart)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                model.setDocId(documentReference.getId());
                                session.setCartValue(session.getCartValue()+1);
                                cartCount.setText(String.valueOf(session.getCartValue()));
                                viewHolder.product_add.setVisibility(View.GONE);
                                viewHolder.decrementQuantity.setVisibility(View.VISIBLE);
                                viewHolder.incrementQuantity.setVisibility(View.VISIBLE);
                                viewHolder.quantityProductPage.setVisibility(View.VISIBLE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
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



    private void getValues() {

        //create new session object by passing application context
        session = new UserSession(getApplicationContext());

        //validating session
        if(session.isLoggedIn()) {

            //get User details if logged in
            user = session.getUserDetails();
            userId = user.get(UserSession.KEY_UID);
            name = user.get(UserSession.KEY_NAME);
            email = user.get(UserSession.KEY_EMAIL);
            mobile = user.get(UserSession.KEY_MOBiLE);
            photo = user.get(UserSession.KEY_PHOTO);
        }else{
            Intent i = new Intent(WishlistActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
