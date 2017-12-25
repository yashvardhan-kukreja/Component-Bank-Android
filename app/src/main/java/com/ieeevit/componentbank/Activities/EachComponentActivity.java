package com.ieeevit.componentbank.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.ieeevit.componentbank.Adapters.ListOfUsersAdapter;
import com.ieeevit.componentbank.Classes.User;
import com.ieeevit.componentbank.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EachComponentActivity extends AppCompatActivity {
TextView name, code, availability, value, countText, noIssuers;
ListView listOfIssuers;
String nameStr, codeStr, availStr, valStr; //Component Details
List<User> users;
FloatingActionButton fab;
String ISSUE_COMPONENT_URL;
ImageView plus, minus;
EditText numberOfComponentsToBeIssued;
Button yesConfirmation, cancelConfirmation;
ArrayList<String> names, regnums, phonenums, issuedDates, emails, quantities; //Users associated with those components;
AlertDialog confirmationDialog;
String currentUsername, currentUserEmail, currentUserRegNum, currentUserPhoneNum;
int count = 0;
    @Override
    public void onBackPressed() {
        currentUsername = getIntent().getExtras().getString("currentusername");
        currentUserEmail = getIntent().getExtras().getString("currentuseremail");
        currentUserRegNum = getIntent().getExtras().getString("currentuserregnum");
        currentUserPhoneNum = getIntent().getExtras().getString("currentuserphonenum");
        Intent i = new Intent(EachComponentActivity.this, TabbedActivity.class);
        i.putExtra("name", currentUsername);
        i.putExtra("email", currentUserEmail);
        i.putExtra("regnum", currentUserRegNum);
        i.putExtra("phonenum", currentUserPhoneNum);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_component);
        users = new ArrayList<>();
        names = new ArrayList<>();
        regnums = new ArrayList<>();
        phonenums = new ArrayList<>();
        issuedDates = new ArrayList<>();
        emails = new ArrayList<>();
        quantities =  new ArrayList<>();

        ISSUE_COMPONENT_URL = getResources().getString(R.string.base_url) + "/issueComponent";

        //Fetching the details of the component from the previous activity
        nameStr = getIntent().getExtras().getString("name");
        codeStr = getIntent().getExtras().getString("code");
        availStr = getIntent().getExtras().getString("quantity");
        valStr = getIntent().getExtras().getString("value");

        //Fetching the details of the current user from the previous activity
        currentUsername = getIntent().getExtras().getString("currentusername");
        currentUserEmail = getIntent().getExtras().getString("currentuseremail");
        currentUserRegNum = getIntent().getExtras().getString("currentuserregnum");
        currentUserPhoneNum = getIntent().getExtras().getString("currentuserphonenum");


        //Fetching list of the all the users with their details which have currently issued this component
        names = getIntent().getExtras().getStringArrayList("usernames");
        regnums = getIntent().getExtras().getStringArrayList("userregnums");
        phonenums = getIntent().getExtras().getStringArrayList("userphonenums");
        issuedDates = getIntent().getExtras().getStringArrayList("userissuedates");
        emails = getIntent().getExtras().getStringArrayList("useremails");
        quantities = getIntent().getExtras().getStringArrayList("userquantities");

        for (int i=0;i<names.size();i++){
            users.add((new User(names.get(i).toString(), regnums.get(i).toString(), emails.get(i).toString(), phonenums.get(i).toString())));
        }

        name = findViewById(R.id.componentName);
        code = findViewById(R.id.componentCode);
        availability = findViewById(R.id.componentsAvailable);
        value = findViewById(R.id.componentValue);
        listOfIssuers = findViewById(R.id.listOfComponentIssuers);
        fab = findViewById(R.id.issueAComponent);
        noIssuers = findViewById(R.id.noIssuers);
        listOfIssuers.setVisibility(View.GONE);

        //Building and handling the final alert dialog for issuing the component
        final AlertDialog.Builder builder = new AlertDialog.Builder(EachComponentActivity.this);
        View v = getLayoutInflater().inflate(R.layout.dialog_component_confirmation, null, false);
        plus = v.findViewById(R.id.plusButton);
        minus = v.findViewById(R.id.minusButton);
        countText = v.findViewById(R.id.countText);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count+=1;
                countText.setText(Integer.toString(count));
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count == 0)
                    return;
                count-=1;
                countText.setText(Integer.toString(count));
            }
        });
        yesConfirmation = v.findViewById(R.id.yesConfirmation);
        cancelConfirmation = v.findViewById(R.id.cancelConfirmation);
        builder.setView(v);
        confirmationDialog = builder.create();

        // When the app is run for the first time, a target view will pop up displaying the description about how to issue a component using a Tap Target View
        SharedPreferences sharedPreferences2 = getSharedPreferences("firsttimeissuepage", MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        if (sharedPreferences2.getString("firsttime", "").equals("") || sharedPreferences2.getString("firsttime", "").equals(null) || sharedPreferences2.getString("firsttime", "").equals("false")){
            TapTargetView.showFor(EachComponentActivity.this, TapTarget.forView(fab, "Issue a component!", "If the components aren't available, then you can borrow them from any of the issuers :)").outerCircleColor(R.color.rippleColor).outerCircleAlpha(0.9f).transparentTarget(true));
            editor2.putString("firsttime", "true");
            editor2.commit();
        }

        //FAB handling the issue of the component
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmationDialog.show();
                yesConfirmation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Checking whether the user has requested the components whose quantity is available
                        if (count == 0){
                            Toast.makeText(EachComponentActivity.this, "Seriously! Zero components?", Toast.LENGTH_LONG).show();
                            return;
                        }
                         else if (Integer.parseInt(availStr) - count < 0){
                            Toast.makeText(EachComponentActivity.this, "Cool down! We don't have so many components available", Toast.LENGTH_LONG).show();
                            return;
                        }

                        //Request for issuing the component
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, ISSUE_COMPONENT_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    String success = jsonObject.getString("success");
                                    String message = jsonObject.getString("message");
                                    Toast.makeText(EachComponentActivity.this, message, Toast.LENGTH_LONG).show();
                                    if (success.equals("true")){
                                        availStr = Integer.toString(Integer.parseInt(availStr) - count);
                                        availability.setText("Available: " + availStr);
                                        Intent i = new Intent(EachComponentActivity.this, TabbedActivity.class);
                                        i.putExtra("name", currentUsername);
                                        i.putExtra("email", currentUserEmail);
                                        i.putExtra("regnum", currentUserRegNum);
                                        i.putExtra("phonenum", currentUserPhoneNum);
                                        startActivity(i);
                                    }
                                    confirmationDialog.dismiss();
                                } catch (JSONException e) {
                                    confirmationDialog.dismiss();
                                    Toast.makeText(EachComponentActivity.this, "An error occured", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                confirmationDialog.dismiss();
                                Toast.makeText(EachComponentActivity.this, "An error occured", Toast.LENGTH_LONG).show();
                                volleyError.printStackTrace();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("componentcode", codeStr);
                                params.put("number", Integer.toString(count));
                                params.put("issuedbyname", currentUsername);
                                params.put("issuedbyregnum", currentUserRegNum);
                                params.put("issuedbyphonenum", currentUserPhoneNum);
                                params.put("issuedbyemail", currentUserEmail);
                                params.put("issuedondate", (new SimpleDateFormat("dd-MM-yyyy").format(new Date())));
                                return params;
                            }
                        };
                        Volley.newRequestQueue(EachComponentActivity.this).add(stringRequest);
                        //End of the request for issuing the component
                    }
                });

                cancelConfirmation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        confirmationDialog.dismiss();
                    }
                });
            }
        });

        name.setText(nameStr);
        code.setText(codeStr);
        availability.setText("Available: " + availStr);
        value.setText("Value: Rs." + valStr);
        if (users.size() <= 1){
            listOfIssuers.setVisibility(View.GONE);
            noIssuers.setVisibility(View.VISIBLE);
        } else {
            listOfIssuers.setVisibility(View.VISIBLE);
            noIssuers.setVisibility(View.GONE);
            listOfIssuers.setAdapter((new ListOfUsersAdapter(EachComponentActivity.this, EachComponentActivity.this,users, issuedDates, quantities)));
        }


    }
}
