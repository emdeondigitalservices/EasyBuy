package com.emdeondigital.easybuy;

import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emdeondigital.easybuy.adapter.StatePinCodeAdapter;
import com.emdeondigital.easybuy.model.Location;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectLocationActivity extends AppCompatActivity {

    LinearLayout pickCountryId;
    CountryCodePicker ccp;
    TextView selectCountry;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private StatePinCodeAdapter locationAdapter;
    private String countryCode;
    Map<String, ArrayList<Location>> statePincode = new HashMap<String,ArrayList<Location>>();
    RelativeLayout progressBarLayout;
    ProgressBar defaultProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Select Your Location");

        //Resources res = getResources();
        //Drawable drawable = res.getDrawable(R.drawable.circlebar);
        progressBarLayout = findViewById(R.id.progressBarLayout);
        defaultProgressBar = progressBarLayout.findViewById(R.id.defaultProgressBar);
        /*defaultProgressBar.setProgress(0);   // Main Progress
        defaultProgressBar.setSecondaryProgress(100); // Secondary Progress
        defaultProgressBar.setMax(100); // Maximum Progress
        defaultProgressBar.setProgressDrawable(drawable);
        ObjectAnimator anim = ObjectAnimator.ofInt(defaultProgressBar, "progress", 0, 100);
        anim.setDuration(15000);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();*/

        generateStateList(statePincode);
        pickCountryId = findViewById(R.id.pickCountryId);
        selectCountry = findViewById(R.id.selectCountry);
        ccp = findViewById(R.id.ccp);
        countryCode = ccp.getDefaultCountryCode();
        getStates(countryCode);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                Toast.makeText(SelectLocationActivity.this, "Updated " + ccp.getSelectedCountryCodeWithPlus(),
                        Toast.LENGTH_SHORT).show();
                getStates(ccp.getSelectedCountryCode());
            }
        });
    }

    private void getStates(String countryCode){
        progressBarLayout.setVisibility(View.VISIBLE);
        db.collection("country").document(countryCode).collection("state").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Location> locationDataList = new ArrayList<Location>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ArrayList<Location> pinCodeList = new ArrayList<Location>();
                                Location location = new Location();
                                location.setStateId(document.getId());
                                location.setStateName(document.getData().get("statename").toString());
                                location.setPinCode(document.getData().get("pincode").toString());
                                locationDataList.add(location);
                                pinCodeList.add(location);
                                if(statePincode.containsKey(document.getData().get("statename").toString())){
                                    ArrayList<Location> pinList = statePincode.get(document.getData().get("statename").toString());
                                    pinList.addAll(pinCodeList);
                                }else{
                                    statePincode.put(document.getData().get("statename").toString(),pinCodeList);
                                }
                            }
                            //generateStateList(statePincode);
                            locationAdapter.notifyDataSetChanged();
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        progressBarLayout.setVisibility(View.GONE);
                    }
                });
    }

    /*Method to generate List of employees using RecyclerView with custom adapter*/
    private void generateStateList(Map<String, ArrayList<Location>> statePincode) {
        recyclerView = findViewById(R.id.recyclerview_locations);
        locationAdapter = new StatePinCodeAdapter(this,statePincode);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SelectLocationActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(locationAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
