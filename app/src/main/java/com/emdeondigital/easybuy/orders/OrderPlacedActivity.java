package com.emdeondigital.easybuy.orders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.emdeondigital.easybuy.R;
import com.emdeondigital.easybuy.model.SingleProductModel;
import com.emdeondigital.easybuy.products.ProductListingActivity;
import com.emdeondigital.easybuy.sessions.UserSession;
import com.emdeondigital.easybuy.util.GeneralMethods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderPlacedActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0;
    TextView orderidview;
    private String orderid,currdatetime,order_reference_id;
    private UserSession session;
    ArrayList<SingleProductModel> cartcollect;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<String> mobNumbers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);

        session = new UserSession(getApplicationContext());
        Intent i = getIntent();
        cartcollect = (ArrayList<SingleProductModel>) i.getSerializableExtra("order");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        currdatetime = sdf.format(new Date());
        String uniqueID = UUID.randomUUID().toString();
        order_reference_id = "EASY-"+uniqueID + "-" + getordernumber();

        for(SingleProductModel model : cartcollect){
            orderPlaced(model);
        }


    }

    public String getordernumber() {
        return currdatetime.replaceAll("-","");
    }

    private void initialize() {
        orderidview = findViewById(R.id.orderid);
        //orderid = getIntent().getStringExtra("orderid");
        orderidview.setText(order_reference_id);
        session.setCartValue(0);
    }

    protected void sendSMSMessage() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mobNumbers.add("+918075461503");
                    mobNumbers.add("+919686590050");
                    String message = getString(R.string.orderPlacedSMS);
                    for(String mobile : mobNumbers) {
                        GeneralMethods.sendSMS(mobile,message,null,null,null);
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }

    /*public void sendSMS(String mobNumber, String message, String scAddress,String sentIntent,String deliveryIntent){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(mobNumber, null, message,
                null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();
    }*/

    public void finishActivity(View view) {
        finish();
    }

    public void orderPlaced(SingleProductModel model){
        Map<String, Object> orders = new HashMap<>();
        orders.put("placedDate", currdatetime);
        orders.put("uid", model.getUserId());
        orders.put("prid", model.getPrid());
        orders.put("prname", model.getPrname());
        orders.put("imageurl", model.getPrimage());
        orders.put("quantity", model.getQnty());
        orders.put("sellingprice", model.getSellingPrice());
        orders.put("status","P");
        orders.put("orderId",order_reference_id);

        try {
            // Add a new document with a generated ID
            db.collection("orders")
                    .add(orders)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            initialize();
                            sendSMSMessage();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(OrderPlacedActivity.this, "Failure", Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (Exception e) {
            // Hiding the progressDialog after done uploading.
            //progressDialog.dismiss();
            e.printStackTrace();
        }

    }

}

