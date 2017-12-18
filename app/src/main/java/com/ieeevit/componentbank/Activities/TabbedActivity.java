package com.ieeevit.componentbank.Activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ieeevit.componentbank.Fragments.UserComponentsFragment;
import com.ieeevit.componentbank.Fragments.UserProfileFragment;
import com.ieeevit.componentbank.R;

public class TabbedActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    String currentUsername, currentUserEmail, currentUserRegNum, currentUserPhoneNum;

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

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
