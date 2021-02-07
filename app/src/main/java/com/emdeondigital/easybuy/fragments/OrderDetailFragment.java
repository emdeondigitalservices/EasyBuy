package com.emdeondigital.easybuy.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.emdeondigital.easybuy.R;
import com.emdeondigital.easybuy.model.SingleProductModel;
import com.emdeondigital.easybuy.orders.OrderPlacedActivity;
import com.emdeondigital.easybuy.products.ProductListingActivity;
import com.emdeondigital.easybuy.sessions.UserSession;
import com.google.android.material.snackbar.Snackbar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.whygraphics.multilineradiogroup.MultiLineRadioGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderDetailFragment extends Fragment {
    TextView totalAmount;
    TextView noOfItems;
    TextView deliveryDate;
    MaterialEditText orderName,orderEmail,orderNumber,orderAddress,orderPincode;
    private ArrayList<SingleProductModel> cartcollect;
    MultiLineRadioGroup mainActivityMultiLineRadioGroup;
    private String payment_mode="COD";
    private String order_reference_id;
    private UserSession session;
    private HashMap<String,String> user;
    private String placed_user_name,getPlaced_user_email,getPlaced_user_mobile_no,currdatetime;
    private ImageView placeOrder;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrderDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderDetailFragment newInstance(String param1, String param2) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void initialize(View view){
        orderName = view.findViewById(R.id.ordername);
        orderEmail = view.findViewById(R.id.orderemail);
        orderNumber = view.findViewById(R.id.ordernumber);
        orderAddress = view.findViewById(R.id.orderaddress);
        orderPincode = view.findViewById(R.id.orderpincode);
        totalAmount = view.findViewById(R.id.total_amount);
        noOfItems = view.findViewById(R.id.no_of_items);
        deliveryDate = view.findViewById(R.id.delivery_date);
        mainActivityMultiLineRadioGroup = view.findViewById(R.id.main_activity_multi_line_radio_group);
        placeOrder = view.findViewById(R.id.placeOrder);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
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
        initialize(view);
        productDetails();
    }

    private void productDetails() {

        if (getArguments() != null) {
            //setting total price
            totalAmount.setText(getArguments().getString("totalprice"));

            //setting number of products
            noOfItems.setText(getArguments().getString("totalproducts"));

            cartcollect = (ArrayList<SingleProductModel>) getArguments().getSerializable("cartproducts");
        }
        //delivery date
        SimpleDateFormat formattedDate = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 7);  // number of days to add
        String tomorrow = (formattedDate.format(c.getTime()));
        deliveryDate.setText(tomorrow);

        mainActivityMultiLineRadioGroup.setOnCheckedChangeListener(new MultiLineRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ViewGroup group, RadioButton button) {
                payment_mode=button.getText().toString();
            }
        });

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields(v)) {
                    Intent intent = new Intent(getContext(), OrderPlacedActivity.class);
                    intent.putExtra("order",cartcollect);
                    getContext().startActivity(intent);
                    getActivity().finish();
                }
            }
        });


    }

    private boolean validateFields(View view) {

        if (orderName.getText().toString().length() == 0 || orderEmail.getText().toString().length() == 0 ||
                orderNumber.getText().toString().length() == 0 || orderAddress.getText().toString().length() == 0 ||
                orderPincode.getText().toString().length() == 0) {
            Snackbar.make(view, "Kindly Fill all the fields", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return false;
        } else if (orderEmail.getText().toString().length() < 4 || orderEmail.getText().toString().length() > 30) {
            orderEmail.setError("Email Must consist of 4 to 30 characters");
            return false;
        } else if (!orderEmail.getText().toString().matches("^[A-za-z0-9.@]+")) {
            orderEmail.setError("Only . and @ characters allowed");
            return false;
        } else if (!orderEmail.getText().toString().contains("@") || !orderEmail.getText().toString().contains(".")) {
            orderEmail.setError("Email must contain @ and .");
            return false;
        } else if (orderNumber.getText().toString().length() < 4 || orderNumber.getText().toString().length() > 12) {
            orderNumber.setError("Number Must consist of 10 characters");
            return false;
        } else if (orderPincode.getText().toString().length() < 6 || orderPincode.getText().toString().length() > 8){
            orderPincode.setError("Pincode must be of 6 digits");
            return false;
        }

        return true;
    }
}
