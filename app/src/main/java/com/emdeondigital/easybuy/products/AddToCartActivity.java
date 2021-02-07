package com.emdeondigital.easybuy.products;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.emdeondigital.easybuy.R;
import com.emdeondigital.easybuy.adapter.AddToCartAdapter;
import com.emdeondigital.easybuy.model.SingleProductModel;
import com.emdeondigital.easybuy.orders.OrderDetailsActivity;
import com.emdeondigital.easybuy.sessions.UserSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class AddToCartActivity extends AppCompatActivity {

    public static String page = "AddToCart";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private AddToCartAdapter productListAdapter;
    //to get user session data
    private UserSession session;
    private HashMap<String,String> user;
    private String name,email,photo,mobile,uid;
    private LottieAnimationView tv_no_item;
    private LinearLayout activitycartlist;
    private LottieAnimationView emptycart;
    private float totalcost=0;
    private int totalproducts=0;
    ArrayList<SingleProductModel> productList = new ArrayList<SingleProductModel>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Cart");

        FrameLayout cartSection = findViewById(R.id.cartSection);
        cartSection.setVisibility(View.GONE);

        tv_no_item = findViewById(R.id.tv_no_cards);
        activitycartlist = findViewById(R.id.activity_cart_list);
        emptycart = findViewById(R.id.empty_cart);
        //create new session object by passing application context
        session = new UserSession(getApplicationContext());

        getValues();

        if(session.getCartValue()>0) {
            populateAddToCartRecyclerView();
        }else if(session.getCartValue() == 0)  {
            tv_no_item.setVisibility(View.GONE);
            activitycartlist.setVisibility(View.GONE);
            emptycart.setVisibility(View.VISIBLE);
        }
    }

    private void getValues() {

        //validating session
        session.isLoggedIn();

        //get User details if logged in
        user = session.getUserDetails();

        name = user.get(UserSession.KEY_NAME);
        email = user.get(UserSession.KEY_EMAIL);
        mobile = user.get(UserSession.KEY_MOBiLE);
        photo = user.get(UserSession.KEY_PHOTO);
        uid = user.get(UserSession.KEY_UID);
    }

    private void populateAddToCartRecyclerView(){
        db.collection("cart")
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                SingleProductModel product = new SingleProductModel();
                                product.setDocId(document.getId());
                                product.setUserId(uid);
                                product.setPrid(document.getData().get("prid").toString());
                                product.setPrname(document.getData().get("prname").toString());
                                product.setPrimage(document.getData().get("imageurl").toString());
                                product.setSellingPrice(document.getData().get("sellingprice").toString());
                                product.setQnty(document.getData().get("quantity").toString());
                                productList.add(product);
                                totalcost += Integer.parseInt(product.getQnty())*Float.parseFloat(product.getSellingPrice());
                                totalproducts += Integer.parseInt(product.getQnty());
                            }
                            if(productList.size() > 0){
                                activitycartlist.setVisibility(View.VISIBLE);
                                generateProductList(productList);

                            }else{
                                emptycart.setVisibility(View.VISIBLE);
                            }
                            if(tv_no_item.getVisibility()== View.VISIBLE){
                                tv_no_item.setVisibility(View.GONE);
                            }

                        } else {
                            if(tv_no_item.getVisibility()== View.VISIBLE){
                                tv_no_item.setVisibility(View.GONE);
                            }
                            Toast.makeText(AddToCartActivity.this, "Exception", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /*Method to generate List of employees using RecyclerView with custom adapter*/
    private void generateProductList(ArrayList<SingleProductModel> productWishList) {
        recyclerView = findViewById(R.id.addtocartrecyclerview);

        productListAdapter = new AddToCartAdapter(this,productWishList,session);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AddToCartActivity.this);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(productListAdapter);
    }

    public void checkout(View view) {
        Intent intent = new Intent(AddToCartActivity.this, OrderDetailsActivity.class);
        intent.putExtra("totalprice",Float.toString(totalcost));
        intent.putExtra("totalproducts",Integer.toString(totalproducts));
        intent.putExtra("cartproducts",productList);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
