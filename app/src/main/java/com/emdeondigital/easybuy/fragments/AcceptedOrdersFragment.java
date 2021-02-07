package com.emdeondigital.easybuy.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emdeondigital.easybuy.R;
import com.emdeondigital.easybuy.adapter.AcceptedOrdersAdapter;
import com.emdeondigital.easybuy.adapter.PendingOrdersAdapter;
import com.emdeondigital.easybuy.model.SingleProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class AcceptedOrdersFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private float totalcost = 0;
    private int totalproducts = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    HashMap<String, ArrayList<SingleProductModel>> ordersMap = new HashMap<>();
    RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AcceptedOrdersFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AcceptedOrdersFragment newInstance(int columnCount) {
        AcceptedOrdersFragment fragment = new AcceptedOrdersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accepted_orders, container, false);

        recyclerView = view.findViewById(R.id.acceptedOrdersRecyclerView);

        getAcceptedOrders();

        return view;
    }

    private void getAcceptedOrders() {
        db.collection("orders")
                .whereEqualTo("status", "A")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            ArrayList<SingleProductModel> productListNew = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                SingleProductModel product = new SingleProductModel();
                                product.setDocId(document.getId());
                                product.setOrderId(document.getData().get("orderId").toString());
                                product.setPrid(document.getData().get("prid").toString());
                                product.setPrname(document.getData().get("prname").toString());
                                product.setPrimage(document.getData().get("imageurl").toString());
                                product.setSellingPrice(document.getData().get("sellingprice").toString());
                                product.setQnty(document.getData().get("quantity").toString());
                                if (ordersMap.get(product.getOrderId()) != null) {
                                    ArrayList<SingleProductModel> productList = ordersMap.get(product.getOrderId());
                                    productList.add(product);
                                } else {
                                    productListNew = new ArrayList<>();
                                    productListNew.add(product);
                                    ordersMap.put(product.getOrderId(), productListNew);
                                }
                                totalcost += Integer.parseInt(product.getQnty()) * Float.parseFloat(product.getSellingPrice());
                                totalproducts += Integer.parseInt(product.getQnty());
                            }
                            generateAcceptedOrderList(ordersMap);
                        } else {
                            Toast.makeText(getContext(), "Exception", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void generateAcceptedOrderList(HashMap<String, ArrayList<SingleProductModel>> ordersMap) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new AcceptedOrdersAdapter(getContext(), ordersMap));
    }


}
