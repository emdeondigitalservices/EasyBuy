package com.emdeondigital.easybuy.orders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.emdeondigital.easybuy.R;
import com.emdeondigital.easybuy.adapter.AddToCartAdapter;
import com.emdeondigital.easybuy.adapter.PendingOrdersAdapter;
import com.emdeondigital.easybuy.fragments.AcceptedOrdersFragment;
import com.emdeondigital.easybuy.fragments.ItemFragment;
import com.emdeondigital.easybuy.fragments.OrderDetailFragment;
import com.emdeondigital.easybuy.fragments.PendingOrdersFragment;
import com.emdeondigital.easybuy.model.SingleProductModel;
import com.emdeondigital.easybuy.sessions.UserSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PendingOrdersActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private PendingOrdersAdapter pendingOrdersAdapter;
    //Fragments
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_orders);

        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Pending Orders");*/

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setVisibility(View.VISIBLE);
        //Fragments
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        //getPendingOrders();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        PendingOrdersFragment pendingOrdersFragment = new PendingOrdersFragment();

        AcceptedOrdersFragment acceptedOrdersFragment = new AcceptedOrdersFragment();

        adapter.addFragment(pendingOrdersFragment, "Pending Orders");
        adapter.addFragment(acceptedOrdersFragment, "Accepted Orders");
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
