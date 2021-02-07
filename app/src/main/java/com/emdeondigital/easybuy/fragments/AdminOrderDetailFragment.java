package com.emdeondigital.easybuy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.emdeondigital.easybuy.R;
import com.emdeondigital.easybuy.model.OrderDetails;
import com.emdeondigital.easybuy.model.SingleProductModel;
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
 * Use the {@link AdminOrderDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminOrderDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView numItems;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdminOrderDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminOrderDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminOrderDetailFragment newInstance(String param1, String param2) {
        AdminOrderDetailFragment fragment = new AdminOrderDetailFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            showOrderDetails(view,getArguments().getString("orderId"));
        }
    }

    private void showOrderDetails(View v,String orderId){
        db.collection("orderdetails")
                .whereEqualTo("orderId", orderId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                numItems = v.findViewById(R.id.no_of_items);
                                numItems.setText(document.getData().get("totalNumOfItems").toString());
                                //OrderDetails od = new OrderDetails();
                                //od.setId(document.getId());
                                //od.setTotalNumOfItems(document.getData().get("totalNumOfItems").toString());
                                //od.setTotalPrice(document.getData().get("totalPrice").toString());
                            }

                        } else {
                            Toast.makeText(getContext(), "Exception", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
