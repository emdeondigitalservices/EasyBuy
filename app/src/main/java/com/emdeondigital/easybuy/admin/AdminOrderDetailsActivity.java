package com.emdeondigital.easybuy.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.emdeondigital.easybuy.R;
import com.emdeondigital.easybuy.fragments.AdminOrderDetailFragment;
import com.emdeondigital.easybuy.fragments.ItemFragment;
import com.emdeondigital.easybuy.fragments.OrderDetailFragment;
import com.emdeondigital.easybuy.model.SingleProductModel;
import com.emdeondigital.easybuy.orders.OrderDetailsActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class AdminOrderDetailsActivity extends AppCompatActivity {

    //Fragments
    private TabLayout tabLayout;
    private ViewPager adminOrderViewPager;
    private ArrayList<SingleProductModel> cartcollect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FrameLayout cartSection = findViewById(R.id.cartSection);
        cartSection.setVisibility(View.GONE);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setVisibility(View.VISIBLE);

        //Fragments
        adminOrderViewPager = findViewById(R.id.adminOrderViewPager);
        setupViewPager(adminOrderViewPager);

        tabLayout.setupWithViewPager(adminOrderViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Bundle bundle = getIntent().getExtras();
        bundle.putString("orderId", bundle.get("orderId").toString());
        ArrayList<SingleProductModel> orderList = null;
        orderList = (ArrayList<SingleProductModel>) bundle.get("orderList");
        bundle.putSerializable("orderList",orderList);
        AdminOrderDetailFragment orderDetailFragment = new AdminOrderDetailFragment();
        orderDetailFragment.setArguments(bundle);

        ItemFragment itemFragment = new ItemFragment();
        //itemFragment.setSession(session);
        adapter.addFragment(orderDetailFragment, "Order Details");
        //adapter.addFragment(itemFragment, "Items");
        viewPager.setAdapter(adapter);
        // addOnPageChangeListener event change the tab on slide
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
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
