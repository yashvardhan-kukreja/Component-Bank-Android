package com.ieeevit.componentbank.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.ieeevit.componentbank.Fragments.UserComponentsFragment;
import com.ieeevit.componentbank.Fragments.UserProfileFragment;
import com.ieeevit.componentbank.R;

public class TabbedActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    String currentUsername, currentUserEmail, currentUserRegNum, currentUserPhoneNum;
    FloatingActionButton logout;

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = getSharedPreferences("logindetails", MODE_PRIVATE);
        if (!(sharedPreferences.getString("email", "") == "" || sharedPreferences.getString("email", "") == null)) {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        currentUsername = getIntent().getExtras().getString("name");
        currentUserEmail = getIntent().getExtras().getString("email");
        currentUserRegNum = getIntent().getExtras().getString("regnum");
        currentUserPhoneNum = getIntent().getExtras().getString("phonenum");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        logout = findViewById(R.id.logoutFAB);

        // When the app is run for the first time, a target view will pop up displaying the description about how to logout using a Tap Target View
        SharedPreferences sharedPreferences2 = getSharedPreferences("firsttimehomepage", MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        if (sharedPreferences2.getString("firsttime", "").equals("") || sharedPreferences2.getString("firsttime", "").equals(null) || sharedPreferences2.getString("firsttime", "").equals("false")){
            TapTargetView.showFor(TabbedActivity.this, TapTarget.forView(logout, "Logout!", "Touch here for logging out").outerCircleColor(R.color.rippleColor).outerCircleAlpha(0.9f).transparentTarget(true));
            editor2.putString("firsttime", "true");
            editor2.commit();
        }

        //Listener for FAB for logging out
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("logindetails", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                startActivity((new Intent(TabbedActivity.this, LogInActivity.class)));
            }
        });
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    UserProfileFragment userProfileFragment = new UserProfileFragment(TabbedActivity.this, currentUsername, currentUserEmail, currentUserRegNum, currentUserPhoneNum);
                    return userProfileFragment;
                case 1:
                    UserComponentsFragment userComponentsFragment = new UserComponentsFragment(TabbedActivity.this, currentUsername, currentUserEmail, currentUserRegNum, currentUserPhoneNum);
                    return userComponentsFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
