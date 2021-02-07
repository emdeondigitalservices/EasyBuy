package com.emdeondigital.easybuy.products;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.emdeondigital.easybuy.LoginActivity;
import com.emdeondigital.easybuy.MainActivity;
import com.emdeondigital.easybuy.R;
import com.emdeondigital.easybuy.model.Product;
import com.emdeondigital.easybuy.model.SingleProductModel;
import com.emdeondigital.easybuy.sessions.UserSession;
import com.emdeondigital.easybuy.users.RegistrationActivity;
import com.emdeondigital.easybuy.util.GeneralMethods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IndividualProductActivity extends AppCompatActivity {

    ImageView productimage;
    TextView productname;
    TextView productprice;
    TextView addToCart;
    TextView viewCart;
    TextView buyNow;
    TextView productdesc;
    RatingBar starRating;
    private static TextView cartCount;
    private AppBarLayout appBarLayout;
    EditText quantityProductPage;
    LottieAnimationView addToWishlist;
    EditText customheader;
    EditText custommessage;
    private Product product;
    private int quantity = 1;
    private UserSession session;
    private String usermobile, useremail,userId;
    LinearLayout qntyLayout;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initialize();
    }

    private void initialize() {

        productprice = findViewById(R.id.productprice);
        productname = findViewById(R.id.productname);
        productdesc = findViewById(R.id.productdesc);
        productimage = findViewById(R.id.productimage);
        quantityProductPage = findViewById(R.id.quantityProductPage);
        customheader = findViewById(R.id.customheader);
        custommessage = findViewById(R.id.custommessage);
        addToWishlist = findViewById(R.id.add_to_wishlist);
        starRating = findViewById(R.id.starRating);
        qntyLayout = findViewById(R.id.qntyLayout);
        product = (Product) getIntent().getSerializableExtra("product");

        productprice.setText("₹ " + product.getPrice());
        starRating.setRating(Float.parseFloat(product.getStarRating()));
        productname.setText(product.getName());
        productdesc.setText(product.getDescription());
        quantityProductPage.setText("1");
        Glide.with(IndividualProductActivity.this).load(product.getImageURL()).into(productimage);

        //SharedPreference for Cart Value
        session = new UserSession(getApplicationContext());

        //validating session
        session.isLoggedIn();
        userId = session.getUserDetails().get(UserSession.KEY_UID);
        usermobile = session.getUserDetails().get(UserSession.KEY_MOBiLE);
        useremail = session.getUserDetails().get(UserSession.KEY_EMAIL);

        //setting textwatcher for no of items field
        quantityProductPage.addTextChangedListener(productcount);

        appBarLayout = findViewById(R.id.toolbarwrap);
        cartCount = appBarLayout.findViewById(R.id.cartCount);
        if(session.getCartValue() > 0) {
            cartCount.setText(String.valueOf(session.getCartValue()));
        }else{
            cartCount.setVisibility(View.GONE);
        }
        addToCart = findViewById(R.id.addToCart);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });
        viewCart = findViewById(R.id.viewCart);

        Set<String> productIds = new HashSet<>();
        productIds = session.getKeyAddToCartProducts();
        List<String> productIdsList = GeneralMethods.convertSetToList(productIds);
        if(productIdsList != null && productIdsList.contains(product.getId())){
            addToCart.setVisibility(View.GONE);
            viewCart.setVisibility(View.VISIBLE);
            qntyLayout.setVisibility(View.VISIBLE);
        }
    }

    //check that product count must not exceed 500
    TextWatcher productcount = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (quantityProductPage.getText().toString().equals("")) {
                quantityProductPage.setText("0");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            //none
            if (Integer.parseInt(quantityProductPage.getText().toString()) >= 500) {
                Toast.makeText(IndividualProductActivity.this, "Product Count Must be less than 500", Toast.LENGTH_LONG).show();
            }
        }

    };

    public void decrement(View view) {
        if (quantity > 1) {
            quantity--;
            quantityProductPage.setText(String.valueOf(quantity));
        }
    }

    public void increment(View view) {
        if (quantity < 500) {
            quantity++;
            quantityProductPage.setText(String.valueOf(quantity));
        } else {
            Toast.makeText(IndividualProductActivity.this, "Product Count Must be less than 500", Toast.LENGTH_LONG).show();
        }
    }

    public void shareProduct(View view) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Found amazing " + productname.getText().toString() + "on EasyBuy App.";
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private SingleProductModel getProductObject() {

        return new SingleProductModel(product.getId(), Integer.parseInt(quantityProductPage.getText().toString()), useremail, usermobile, product.getName(),
                product.getPrice(), product.getImageURL(), product.getDescription(),customheader.getText().toString(),custommessage.getText().toString());

    }

    public void addToCart() {

        //if ( customheader.getText().toString().length() == 0 ||  custommessage.getText().toString().length() ==0 ){

            //Snackbar.make(view, "Header or Message Empty", Snackbar.LENGTH_LONG)
                    //.setAction("Action", null).show();
        //}else{

            saveToCart();

        //}
    }

    public void addToWishList(View view) {
        addToWishlist.playAnimation();
        saveToWishList();
    }

    public void saveToCart(){
        Map<String, Object> cart = new HashMap<>();
        cart.put("uid",userId);
        cart.put("prid", product.getId());
        cart.put("prname", product.getName());
        cart.put("imageurl", product.getImageURL());
        cart.put("quantity", "1");
        cart.put("sellingprice", product.getSellingPrice());
        cart.put("status","N");
        try {
            // Add a new document with a generated ID
            db.collection("cart").document(userId)
                    .set(cart)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            session.increaseCartValue();
                            //session.setCartValue(session.getCartValue()+1);
                            cartCount.setText(String.valueOf(session.getCartValue()));
                            Log.e("Cart Value IP", session.getCartValue() + " ");
                            addToCart.setVisibility(View.GONE);
                            viewCart.setVisibility(View.VISIBLE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveToWishList(){
        Map<String, Object> user = new HashMap<>();
        user.put("uid", userId);
        user.put("productId", product.getId());
        try {
            // Add a new document with a generated ID
            db.collection("wishlist").document(userId)
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            session.increaseWishlistValue();
                            Toast.makeText(IndividualProductActivity.this, "Product added to Wishlist", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void viewCart(View view) {
        startActivity(new Intent(IndividualProductActivity.this, AddToCartActivity.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
