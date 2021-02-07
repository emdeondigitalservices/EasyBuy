package com.emdeondigital.easybuy.products;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emdeondigital.easybuy.R;
import com.emdeondigital.easybuy.adapter.FilterAdapter;
import com.emdeondigital.easybuy.model.FilterItems;
import com.emdeondigital.easybuy.model.GenericProductModel;
import com.emdeondigital.easybuy.model.Product;
import com.emdeondigital.easybuy.model.SingleProductModel;
import com.emdeondigital.easybuy.sessions.UserSession;
import com.emdeondigital.easybuy.util.GeneralMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ProductListingActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //to get user session data
    private UserSession session;
    private HashMap<String,String> user;
    private String name,email,photo,mobile,uid;
    private RecyclerView recyclerView;
    private ProductListAdapter productListAdapter;
    private static RelativeLayout progressBarLayout;
    private ProgressBar defaultProgressBar;
    private String catId;
    LinearLayout ll;
    ConstraintLayout cl;
    private static TextView cartCount;
    private AppBarLayout appBarLayout;
    private FrameLayout filterSection,cartSection;
    private ImageView filter;
    AlertDialog dialog;
    AlertDialog pleasewait;
    FilterAdapter filterAdapter;
    ArrayList<FilterItems> filterItemlist=new ArrayList<>();
    private static List<String> productIdsList = null;
    private ArrayList<SingleProductModel> productListMin = new ArrayList<SingleProductModel>();
    ArrayList<SingleProductModel> productList = new ArrayList<SingleProductModel>();
    ArrayList<SingleProductModel> arrayList = new ArrayList<SingleProductModel>();
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_listing);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressBarLayout = findViewById(R.id.progressBarLayout);
        defaultProgressBar = progressBarLayout.findViewById(R.id.defaultProgressBar);
        getValues();

        generateProductList(productListMin);
        initScrollListener();

        Intent i = getIntent();
        catId = i.getStringExtra("categoryid");

        appBarLayout = findViewById(R.id.toolbarwrap);
        cartCount = appBarLayout.findViewById(R.id.cartCount);
        if(session.getCartValue() > 0) {
            cartCount.setText(String.valueOf(session.getCartValue()));
        }else{
            cartCount.setVisibility(View.GONE);
        }
        /*filterSection = findViewById(R.id.filterSection);
        filter = filterSection.findViewById(R.id.filter);
        filterSection.setVisibility(View.VISIBLE);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilter();
            }
        });*/
        ll = findViewById(R.id.ll_products);

        cl= findViewById(R.id.ll_empty);

        Set<String> productIds = new HashSet<>();
        productIds = session.getKeyAddToCartProducts();
        productIdsList = GeneralMethods.convertSetToList(productIds);

        getProductList();
        cartSection = findViewById(R.id.cartSection);
        cartSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProductListingActivity.this, AddToCartActivity.class));
                finish();
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        filter(text);
        return false;
    }

    private void showFilter(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductListingActivity.this,R.style.CustomDialog);
        pleasewait = builder.create();
        pleasewait.setCancelable(false);
        View customView3 = LayoutInflater.from(ProductListingActivity.this).inflate(
                R.layout.dialogpleasewait, null, false);
        pleasewait.setView(customView3);
        pleasewait.show();

        dialog = new AlertDialog.Builder(ProductListingActivity.this).create();
        dialog.setTitle("Filter by : ");
        dialog.setCancelable(true);
        View customView = LayoutInflater.from(ProductListingActivity.this).inflate(
                R.layout.filter_listview, null, false);
        ListView listView = (ListView) customView.findViewById(R.id.filterListView);
        FilterItems cat = new FilterItems("Price less than","1", false);
        FilterItems cat1 = new FilterItems("Price greater than","2", false);
        pleasewait.dismiss();

        filterItemlist.add(cat);
        filterItemlist.add(cat1);
        filterAdapter=new FilterAdapter(ProductListingActivity.this,filterItemlist);
        filterAdapter.notifyDataSetChanged();
        listView.setAdapter(filterAdapter);
        dialog.setView(customView);
        dialog.show();
    }

    private void getValues() {

        //create new session object by passing application context
        session = new UserSession(getApplicationContext());
        //validating session
        if(session.isLoggedIn()) {
            //get User details if logged in
            user = session.getUserDetails();

            name = user.get(UserSession.KEY_NAME);
            email = user.get(UserSession.KEY_EMAIL);
            mobile = user.get(UserSession.KEY_MOBiLE);
            photo = user.get(UserSession.KEY_PHOTO);
            uid = user.get(UserSession.KEY_UID);
        }
    }

    private void getProductList(){
        db.collection("products")
                .whereEqualTo("categoryid", catId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                SingleProductModel product = new SingleProductModel();
                                product.setUserId(uid);
                                product.setPrid(document.getId());
                                product.setPrname(document.getData().get("name").toString());
                                product.setPrimage(document.getData().get("imageURL").toString());
                                product.setPrprice(document.getData().get("price").toString());
                                product.setSellingPrice(document.getData().get("sellingprice").toString());
                                product.setDiscount(document.getData().get("discount").toString());
                                product.setStarRating(document.getData().get("star").toString());
                                productList.add(product);
                            }
                            if(productList.size() > 0){
                                arrayList.addAll(productList);
                                ll.setVisibility(View.VISIBLE);
                                populateData(productList);
                            }else{
                                cl.setVisibility(View.VISIBLE);
                            }


                        } else {
                            Toast.makeText(ProductListingActivity.this, "Exception", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /*Method to generate List of employees using RecyclerView with custom adapter*/
    private void generateProductList(ArrayList<SingleProductModel> productWishList) {
        recyclerView = findViewById(R.id.recyclerview_products);
        productListAdapter = new ProductListAdapter(this,productWishList,session);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ProductListingActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(productListAdapter);
        SearchView simpleSearchView = findViewById(R.id.simpleSearchView); // inititate a search view
        simpleSearchView.setQueryHint("Search View"); // set the hint text to display in the query text field
        simpleSearchView.setOnQueryTextListener(this);
    }

    /*public void viewCart(View view) {
        startActivity(new Intent(ProductListingActivity.this, AddToCartActivity.class));
        finish();
    }*/

    public static class ProductListAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        private ArrayList<SingleProductModel> productList;
        private ArrayList<SingleProductModel> arraylist;
        UserSession session;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;

        private class LoadingViewHolder extends RecyclerView.ViewHolder {

            ProgressBar progressBar;

            public LoadingViewHolder(@NonNull View itemView) {
                super(itemView);
                progressBar = itemView.findViewById(R.id.progressBar);
            }
        }

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
        public ProductListAdapter(Context context, ArrayList<SingleProductModel> productList, UserSession session) {
            this.context = context;
            this.productList = productList;
            this.arraylist = productList;
            this.session = session;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View view = layoutInflater.inflate(R.layout.row_product_list, parent, false);
                context = view.getContext();
                return new ProductListingActivity.ProductListAdapter.ProductListViewHolder(view);
            }else{
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View view = layoutInflater.inflate(R.layout.item_layout, parent, false);
                context = view.getContext();
                return new ProductListingActivity.ProductListAdapter.LoadingViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof ProductListViewHolder) {
                populateProductList((ProductListViewHolder) viewHolder, position);
            }else if (viewHolder instanceof LoadingViewHolder) {
                showLoadingView((LoadingViewHolder) viewHolder, position);
            }

        }

        /**
         * The following method decides the type of ViewHolder to display in the RecyclerView
         *
         * @param position
         * @return
         */
        @Override
        public int getItemViewType(int position) {
            return productList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        private void showLoadingView(LoadingViewHolder viewHolder, int position) {
            //ProgressBar would be displayed
        }

        private void populateProductList(ProductListViewHolder viewHolder,int position){
            final SingleProductModel model=productList.get(position);
            viewHolder.product_name.setText(model.getPrname());
            Glide.with(context).load(model.getPrimage()).into(viewHolder.product_img);
            viewHolder.discount.setText(model.getDiscount());
            viewHolder.starRating.setRating(Float.parseFloat(model.getStarRating()));
            viewHolder.selling_price.setText("₹ "+model.getSellingPrice());
            viewHolder.quantityProductPage.setText("1");
            if(productIdsList != null && productIdsList.contains(model.getPrid())){
                viewHolder.product_add.setVisibility(View.GONE);
                viewHolder.decrementQuantity.setVisibility(View.VISIBLE);
                viewHolder.incrementQuantity.setVisibility(View.VISIBLE);
                viewHolder.quantityProductPage.setVisibility(View.VISIBLE);
            }

            SpannableString string = new SpannableString("₹ "+model.getPrprice());
            string.setSpan(new StrikethroughSpan(), 0, ("₹ "+model.getPrprice()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            CharSequence  strikeThroughPrice = string;
            viewHolder.product_price.setText(strikeThroughPrice, TextView.BufferType.SPANNABLE);
            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Product product = getProduct(model);
                    Intent intent = new Intent(context, IndividualProductActivity.class);
                    intent.putExtra("product",product);
                    context.startActivity(intent);
                }
            });
            viewHolder.product_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBarLayout.setVisibility(View.VISIBLE);
                    EditText qty = viewHolder.quantityProductPage;
                    model.setQnty("1");
                    addToCart(model,viewHolder);
                }
            });
            viewHolder.decrementQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(model.getQnty() != null) {
                        int quantity = Integer.parseInt(model.getQnty());
                        if (quantity > 1) {
                            quantity--;
                            viewHolder.quantityProductPage.setText(String.valueOf(quantity));
                            updateQnty(model, String.valueOf(quantity));
                        }else{
                            viewHolder.product_add.setVisibility(View.VISIBLE);
                            viewHolder.decrementQuantity.setVisibility(View.GONE);
                            viewHolder.incrementQuantity.setVisibility(View.GONE);
                            viewHolder.quantityProductPage.setVisibility(View.GONE);
                        }
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
            return productList == null ? 0 : productList.size();
        }

        public void addToCart(SingleProductModel model, ProductListingActivity.ProductListAdapter.ProductListViewHolder viewHolder){
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
                                cartCount.setVisibility(View.VISIBLE);

                                session.increaseCartValue();
                                cartCount.setText(String.valueOf(session.getCartValue()));
                                viewHolder.product_add.setVisibility(View.GONE);
                                viewHolder.decrementQuantity.setVisibility(View.VISIBLE);
                                viewHolder.incrementQuantity.setVisibility(View.VISIBLE);
                                viewHolder.quantityProductPage.setVisibility(View.VISIBLE);
                                //Adding product to session
                                Set<String> productSet = session.getKeyAddToCartProducts();
                                productSet.add(model.getPrid());
                                session.setKeyAddToCartProducts(productSet);
                                progressBarLayout.setVisibility(View.GONE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBarLayout.setVisibility(View.GONE);
                                Toast.makeText(context, "Failure", Toast.LENGTH_LONG).show();
                            }
                        });
            } catch (Exception e) {
                // Hiding the progressDialog after done uploading.
                //progressDialog.dismiss();
                progressBarLayout.setVisibility(View.GONE);
                e.printStackTrace();
            }

        }

        private Product getProduct(SingleProductModel model){
            Product product = new Product();
            product.setId(model.getPrid());
            product.setName(model.getPrname());
            product.setImageURL(model.getPrimage());
            product.setPrice(model.getPrprice());
            product.setSellingPrice(model.getSellingPrice());
            product.setStarRating(model.getStarRating());
            product.setDiscount(model.getDiscount());
            return product;
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //Logic for Pagination
    private void populateData(ArrayList<SingleProductModel> productList) {
        int i = 0;
        while (i < 10 && productList.size() > i) {
            productListMin.add(productList.get(i));
            i++;
        }
        productListAdapter.notifyDataSetChanged();
        //generateProductList(productListMin);
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase();

        productListMin.clear();

        if (charText.length() == 0) {
            productListMin.addAll(arrayList);
        } else {
            for (SingleProductModel wp : arrayList) {
                if (wp.getPrname().toLowerCase().contains(charText)) {
                    productListMin.add(wp);
                }
            }
        }
        productListAdapter.notifyDataSetChanged();
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productListMin.size() - 1) {
                        //bottom of list!
                        if(productListMin.size() > 0 && productList.size() > 0 && productListMin.size() < productList.size()) {
                            loadMore();
                        }
                        isLoading = true;
                    }
                }
            }
        });


    }

    private void loadMore() {
        productListMin.add(null);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                productListAdapter.notifyItemInserted(productListMin.size() - 1);
                productListMin.remove(productListMin.size() - 1);
                int scrollPosition = productListMin.size();
                productListAdapter.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;
                int nextLimit = currentSize + 10;

                while (currentSize - 1 < nextLimit && productList.size() > currentSize) {
                    productListMin.add(productList.get(currentSize));
                    currentSize++;
                }

                productListAdapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);


    }
}
