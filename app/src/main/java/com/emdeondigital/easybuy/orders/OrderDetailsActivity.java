package com.emdeondigital.easybuy.orders;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.emdeondigital.easybuy.R;
import com.emdeondigital.easybuy.fragments.ItemFragment;
import com.emdeondigital.easybuy.fragments.OrderDetailFragment;
import com.emdeondigital.easybuy.model.PlacedOrderModel;
import com.emdeondigital.easybuy.model.SingleProductModel;
import com.emdeondigital.easybuy.sessions.UserSession;
import com.google.android.material.tabs.TabLayout;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OrderDetailsActivity extends AppCompatActivity {

    private UserSession session;
    private HashMap<String,String> user;
    private String order_reference_id;
    private ArrayList<SingleProductModel> cartcollect;
    private String placed_user_name,getPlaced_user_email,getPlaced_user_mobile_no,currdatetime;
    MaterialEditText orderName,orderEmail,orderNumber,orderAddress,orderPincode;
    TextView totalAmount;
    TextView noOfItems;
    TextView deliveryDate;
    private String payment_mode="COD";

    //Fragments
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        FrameLayout cartSection = findViewById(R.id.cartSection);
        cartSection.setVisibility(View.GONE);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setVisibility(View.VISIBLE);


        session = new UserSession(getApplicationContext());

        //Fragments
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);


        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        currdatetime = sdf.format(new Date());

        if(session.isLoggedIn()){
            user = session.getUserDetails();

            placed_user_name=user.get(UserSession.KEY_NAME);
            getPlaced_user_email=user.get(UserSession.KEY_EMAIL);
            getPlaced_user_mobile_no=user.get(UserSession.KEY_MOBiLE);
        }
        orderName = findViewById(R.id.ordername);
        orderEmail = findViewById(R.id.orderemail);
        orderNumber = findViewById(R.id.ordernumber);
        orderAddress = findViewById(R.id.orderaddress);
        orderPincode = findViewById(R.id.orderpincode);
        totalAmount = findViewById(R.id.total_amount);
        noOfItems = findViewById(R.id.no_of_items);
        deliveryDate = findViewById(R.id.delivery_date);
        Bundle bundle = getIntent().getExtras();
        cartcollect = (ArrayList<SingleProductModel>) bundle.get("cartproducts");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public PlacedOrderModel getProductObject() {
        return new PlacedOrderModel(order_reference_id,noOfItems.getText().toString(),totalAmount.getText().toString(),
                deliveryDate.getText().toString(),payment_mode,orderName.getText().toString(),orderEmail.getText().toString(),
                orderNumber.getText().toString(),orderAddress.getText().toString(),orderPincode.getText().toString(),
                placed_user_name,getPlaced_user_email,getPlaced_user_mobile_no);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Bundle bundle = getIntent().getExtras();
        bundle.putString("totalprice", bundle.get("totalprice").toString());
        bundle.putString("totalproducts", bundle.get("totalproducts").toString());
        cartcollect = (ArrayList<SingleProductModel>) bundle.get("cartproducts");
        bundle.putSerializable("cartproducts",cartcollect);
        OrderDetailFragment orderDetailFragment = new OrderDetailFragment();
        orderDetailFragment.setArguments(bundle);

        ItemFragment itemFragment = new ItemFragment();
        itemFragment.setSession(session);
        adapter.addFragment(orderDetailFragment, "Order Details");
        adapter.addFragment(itemFragment, "Items");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
