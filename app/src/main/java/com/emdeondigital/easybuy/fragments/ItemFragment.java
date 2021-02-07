package com.emdeondigital.easybuy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.emdeondigital.easybuy.R;
import com.emdeondigital.easybuy.adapter.AddToCartAdapter;
import com.emdeondigital.easybuy.model.SingleProductModel;
import com.emdeondigital.easybuy.products.AddToCartActivity;
import com.emdeondigital.easybuy.sessions.UserSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<SingleProductModel> cartList = new ArrayList<SingleProductModel>();
    private RecyclerView recyclerView;
    private float totalcost=0;
    private int totalproducts=0;
    //private LinearLayout activitycartlist;
    private AddToCartAdapter productListAdapter;
    private UserSession session;

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public ItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemFragment newInstance(String param1, String param2) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //activitycartlist = view.findViewById(R.id.activity_cart_list);
        recyclerView = view.findViewById(R.id.itemaddtocartrecyclerview);
        populateAddToCartRecyclerView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item, container, false);
    }

    private void populateAddToCartRecyclerView(){
        //get User details if logged in
        HashMap<String,String> user;
        user = session.getUserDetails();
        String uid = user.get(UserSession.KEY_UID);
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
                                product.setPrid(document.getData().get("prid").toString());
                                product.setPrname(document.getData().get("prname").toString());
                                product.setPrimage(document.getData().get("imageurl").toString());
                                product.setSellingPrice(document.getData().get("sellingprice").toString());
                                product.setQnty(document.getData().get("quantity").toString());
                                cartList.add(product);
                                totalcost += Integer.parseInt(product.getQnty())*Float.parseFloat(product.getSellingPrice());
                                totalproducts += Integer.parseInt(product.getQnty());
                            }
                            if(cartList.size() > 0){
                                generateProductList(cartList);
                            }

                        } else {
                            Toast.makeText(getContext(), "Exception", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void generateProductList(ArrayList<SingleProductModel> productWishList) {
        productListAdapter = new AddToCartAdapter(getContext(),productWishList,session);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(productListAdapter);
    }

}
