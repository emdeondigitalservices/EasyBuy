package com.emdeondigital.easybuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.emdeondigital.easybuy.model.Location;
import com.emdeondigital.easybuy.model.SingleProductModel;
import com.emdeondigital.easybuy.orders.OrderDetailsActivity;
import com.emdeondigital.easybuy.orders.OrderPlacedActivity;

import java.util.ArrayList;
import java.util.UUID;

public class PincodeActivity extends AppCompatActivity {

    ArrayList<Location> locationList;
    private ArrayList<String> pinCodeList = new ArrayList<>();
    Button submitPincode;
    String pincode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pincode);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Select Your Pincode");

        submitPincode = findViewById(R.id.submitPincode);
        Bundle bundle = getIntent().getExtras();
        locationList = (ArrayList<Location>) bundle.get("locationList");
        for(Location location : locationList){
            pinCodeList.add(location.getPinCode());
        }

        //Creating the instance of ArrayAdapter containing list of language names
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item,pinCodeList);
        //Getting the instance of AutoCompleteTextView
        AutoCompleteTextView actv =  (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        actv.setTextColor(Color.RED);
        actv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pincode = s.toString();
                if(pincode.length() == 6){
                    submitPincode.setVisibility(View.VISIBLE);
                }else if(pincode.length() < 6){
                    submitPincode.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //handle click event and set desc on textview
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                submitPincode.setVisibility(View.VISIBLE);
            }
        });
        actv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(PincodeActivity.this, "onItemSelected ",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(PincodeActivity.this, "onNothingSelected ",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void submitPincode(View view) {
            Intent intent = new Intent(PincodeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
