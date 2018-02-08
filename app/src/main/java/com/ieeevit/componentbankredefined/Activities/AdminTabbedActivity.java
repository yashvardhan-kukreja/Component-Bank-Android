package com.ieeevit.componentbankredefined.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.ieeevit.componentbankredefined.Fragments.AdminComponentsFragment;
import com.ieeevit.componentbankredefined.Fragments.AdminIssuersFragment;
import com.ieeevit.componentbankredefined.Fragments.UnauthUsersFragment;
import com.ieeevit.componentbankredefined.NetworkAPIs.AdminAPI;
import com.ieeevit.componentbankredefined.NetworkModels.BasicModel;
import com.ieeevit.componentbankredefined.R;

import butterknife.BindString;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminTabbedActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    FloatingActionButton addComponent;
    @BindString(R.string.base_url_admin) String BASE_URL_ADMIN;
    String token;
    String currentPagerItem;
    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = getSharedPreferences("logindetails", MODE_PRIVATE);
        if (!(sharedPreferences.getString("token", "") == "" || sharedPreferences.getString("token", "") == null)) {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_tabbed);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //BASE_URL_ADMIN = getResources().getString(R.string.base_url_admin);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        currentPagerItem = getIntent().getExtras().getString("pagerItem");
        mViewPager.setCurrentItem(Integer.parseInt(currentPagerItem));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        View logout = findViewById(R.id.optionsmenulogoutadmin);
        addComponent = findViewById(R.id.addComponentFAB);

        token = getIntent().getExtras().getString("token");

        // When the app is run for the first time, a target view will pop up displaying the description about how to logout using a Tap Target View
        SharedPreferences sharedPreferences2 = getSharedPreferences("firsttimehomepageadmin", MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        if (sharedPreferences2.getString("firsttime", "").equals("") || sharedPreferences2.getString("firsttime", "").equals(null) || sharedPreferences2.getString("firsttime", "").equals("false")){
            TapTargetView.showFor(AdminTabbedActivity.this, TapTarget.forView(logout, "Logout!", "Touch here for logging out").outerCircleColor(R.color.rippleColor).transparentTarget(true), new TapTargetView.Listener(){
                @Override
                public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                    super.onTargetDismissed(view, userInitiated);
                    TapTargetView.showFor(AdminTabbedActivity.this, TapTarget.forView(addComponent, "Add Component!", "Specify the details of the component you want to add which will be available for lending!").outerCircleColor(R.color.rippleColor).transparentTarget(true));
                }
            });
            editor2.putString("firsttime", "true");
            editor2.commit();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminTabbedActivity.this);
        View v = LayoutInflater.from(AdminTabbedActivity.this).inflate(R.layout.dialog_new_component, null, false);
        final EditText componentName = v.findViewById(R.id.adminComponentRegName);
        final EditText componentQuantity = v.findViewById(R.id.adminComponentRegQuantity);
        final EditText componentValue = v.findViewById(R.id.adminComponentRegValue);
        final Button regComponent = v.findViewById(R.id.adminComponentRegButton);
        builder.setView(v);
        final AlertDialog compRegDialog = builder.create();
        addComponent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compRegDialog.show();
                regComponent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Creating the retrofit instance
                        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL_ADMIN).addConverterFactory(GsonConverterFactory.create()).build();
                        AdminAPI adminAPI = retrofit.create(AdminAPI.class);

                        // Network call for registering a new component
                        Call<BasicModel> registerComponent = adminAPI.registerComponent(token, componentName.getText().toString(), componentQuantity.getText().toString(), componentValue.getText().toString());
                        registerComponent.enqueue(new Callback<BasicModel>() {
                            @Override
                            public void onResponse(Call<BasicModel> call, retrofit2.Response<BasicModel> response) {
                                String success = response.body().getSuccess().toString();
                                String message = response.body().getMessage();
                                Toast.makeText(AdminTabbedActivity.this, message, Toast.LENGTH_SHORT).show();
                                if (success.equals("true")){
                                    compRegDialog.dismiss();
                                    Intent i = new Intent(AdminTabbedActivity.this, AdminTabbedActivity.class);
                                    i.putExtra("token", token);
                                    i.putExtra("pagerItem", "0"); // pagerItem corresponds to the index of the tab which will be loaded when the intent is performed
                                    startActivity(i);
                                    return;
                                }
                                Intent i = new Intent(AdminTabbedActivity.this, AdminTabbedActivity.class);
                                i.putExtra("token", token);
                                startActivity(i);

                            }

                            @Override
                            public void onFailure(Call<BasicModel> call, Throwable t) {
                                t.printStackTrace();
                                Toast.makeText(AdminTabbedActivity.this, "An error occured", Toast.LENGTH_SHORT).show();
                            }
                            // End of the network call
                        });
                    }
                });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logoutactionadmin) {
            SharedPreferences sharedPreferences = getSharedPreferences("logindetails", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
            startActivity((new Intent(AdminTabbedActivity.this, LogInActivity.class)));
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
            switch (position){
                case 0:
                    AdminIssuersFragment adminIssuersFragment1 = new AdminIssuersFragment(AdminTabbedActivity.this, token,1);
                    return adminIssuersFragment1;

                case 1:
                    AdminIssuersFragment adminIssuersFragment = new AdminIssuersFragment(AdminTabbedActivity.this, token,0);
                    return adminIssuersFragment;
                case 2:
                    AdminComponentsFragment adminComponentsFragment = new AdminComponentsFragment(AdminTabbedActivity.this, token);
                    return adminComponentsFragment;
                case 3:
                    UnauthUsersFragment unauthUsersFragment = new UnauthUsersFragment(AdminTabbedActivity.this, token);
                    return unauthUsersFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
