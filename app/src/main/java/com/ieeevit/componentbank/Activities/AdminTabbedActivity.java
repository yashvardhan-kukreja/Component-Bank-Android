package com.ieeevit.componentbank.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.ieeevit.componentbank.Fragments.AdminComponentsFragment;
import com.ieeevit.componentbank.Fragments.AdminIssuersFragment;
import com.ieeevit.componentbank.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AdminTabbedActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    FloatingActionButton addComponent;
    String REG_COMPONENT_URL;
    String token;
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        REG_COMPONENT_URL = getResources().getString(R.string.base_url_admin) + "/registerComponent";
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        View logout = findViewById(R.id.optionsmenulogoutadmin);
        addComponent = findViewById(R.id.addComponentFAB);

        token = getIntent().getExtras().getString("token");
        View mainLayout = findViewById(android.R.id.content);
        Snackbar.make(mainLayout, "Click on the components to add more such components", Snackbar.LENGTH_LONG).show();

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

                        //Request for registering a new component
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, REG_COMPONENT_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    String success = jsonObject.getString("success");
                                    String message = jsonObject.getString("message");
                                    Toast.makeText(AdminTabbedActivity.this, message, Toast.LENGTH_SHORT).show();
                                    if (success.equals("true")){
                                        compRegDialog.dismiss();
                                        Intent i = new Intent(AdminTabbedActivity.this, AdminTabbedActivity.class);
                                        i.putExtra("token", token);
                                        startActivity(i);
                                        return;
                                    }
                                    Intent i = new Intent(AdminTabbedActivity.this, AdminTabbedActivity.class);
                                    i.putExtra("token", token);
                                    startActivity(i);

                                } catch (JSONException e) {
                                    Toast.makeText(AdminTabbedActivity.this, "An error occured", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                volleyError.printStackTrace();
                                Toast.makeText(AdminTabbedActivity.this, "An error occured", Toast.LENGTH_SHORT).show();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("name", componentName.getText().toString());
                                params.put("quantity", componentQuantity.getText().toString());
                                params.put("value", componentValue.getText().toString());
                                params.put("token", token);
                                return params;
                            }
                        };
                        Volley.newRequestQueue(AdminTabbedActivity.this).add(stringRequest);
                        //End of the request
                    }
                });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logoutaction) {
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
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
