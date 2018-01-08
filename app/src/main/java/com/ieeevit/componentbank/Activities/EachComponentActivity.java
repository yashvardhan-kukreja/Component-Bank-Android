package com.ieeevit.componentbank.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EachComponentActivity extends AppCompatActivity {
TextView name, availability, value, countText, noIssuers;
ProgressDialog progressDialog;
String token;
ListView listOfIssuers;
String nameStr, availStr, valStr, id; //Component Details
List<User> users;
FloatingActionButton fab;
String REQUEST_COMPONENT_URL, GET_ISSUERS_URL;
ImageView plus, minus;
Button yesConfirmation, cancelConfirmation;
ArrayList<String>issuedDates, quantities; //Users associated with those components;
AlertDialog confirmationDialog;
String currentUsername, currentUserEmail, currentUserRegNum, currentUserPhoneNum, numreq, numissue;
int count = 0;
    @Override
    public void onBackPressed() {
        currentUsername = getIntent().getExtras().getString("currentusername");
        currentUserEmail = getIntent().getExtras().getString("currentuseremail");
        currentUserRegNum = getIntent().getExtras().getString("currentuserregnum");
        currentUserPhoneNum = getIntent().getExtras().getString("currentuserphonenum");
        token = getIntent().getExtras().getString("token");
        numreq = getIntent().getExtras().getString("numrequested");
        numissue = getIntent().getExtras().getString("numissued");
        Intent i = new Intent(EachComponentActivity.this, TabbedActivity.class);
        i.putExtra("name", currentUsername);
        i.putExtra("email", currentUserEmail);
        i.putExtra("regnum", currentUserRegNum);
        i.putExtra("phonenum", currentUserPhoneNum);
        i.putExtra("numissued", numissue);
        i.putExtra("numrequested", numreq);
        i.putExtra("token", token);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_component);
        users = new ArrayList<>();
        issuedDates = new ArrayList<>();
        quantities =  new ArrayList<>();

        REQUEST_COMPONENT_URL = getResources().getString(R.string.base_url) + "/requestComponent";
        GET_ISSUERS_URL = getResources().getString(R.string.base_url) + "/getIssuers";

        //Fetching the details of the component from the previous activity
        id = getIntent().getExtras().getString("componentId");


        //Fetching the details of the current user from the previous activity
        currentUsername = getIntent().getExtras().getString("currentusername");
        currentUserEmail = getIntent().getExtras().getString("currentuseremail");
        currentUserRegNum = getIntent().getExtras().getString("currentuserregnum");
        currentUserPhoneNum = getIntent().getExtras().getString("currentuserphonenum");
        numreq = getIntent().getExtras().getString("numrequested");
        numissue = getIntent().getExtras().getString("numissued");
        token = getIntent().getExtras().getString("token");

        name = findViewById(R.id.componentName);
        availability = findViewById(R.id.componentsAvailable);
        value = findViewById(R.id.componentValue);
        listOfIssuers = findViewById(R.id.listOfComponentIssuers);
        fab = findViewById(R.id.issueAComponent);
        noIssuers = findViewById(R.id.noIssuers);
        listOfIssuers.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(EachComponentActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        //Request to get all the issuers of the current component with their details
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_ISSUERS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    if (success.equals("false")){
                        Toast.makeText(EachComponentActivity.this, message, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //Fetching the details of the component
                    nameStr = jsonObject.getJSONObject("component").getString("name");
                    availStr = jsonObject.getJSONObject("component").getString("quantity");
                    valStr = jsonObject.getJSONObject("component").getString("value");
                    //Setting the details of the component
                    name.setText(nameStr);
                    availability.setText("Available: " + availStr);
                    value.setText("Value: Rs." + valStr);

                    users.clear();
                    issuedDates.clear();
                    quantities.clear();
                    JSONArray jsonArray = jsonObject.getJSONArray("transactions");
                    for (int i=0;i<jsonArray.length();i++){
                        //Syncing time and date with Indian time and date
                        StringBuilder dateBuilder = new StringBuilder(jsonArray.getJSONObject(i).getString("date"));
                        String time = dateBuilder.toString().split("T")[1].substring(0, 8);
                        String[] timeArr = time.split(":");
                        String seconds = timeArr[2];
                        String dd = dateBuilder.toString().split("T")[0].split("-")[2];
                        String mm = dateBuilder.toString().split("T")[0].split("-")[1];
                        String yyyy = dateBuilder.toString().split("T")[0].split("-")[0];
                        int hour = Integer.parseInt(timeArr[0]);
                        int minutes = Integer.parseInt(timeArr[1]);
                        minutes += 55;
                        hour+=5;
                        if (minutes>=60){
                            hour += 1;
                            minutes -= 60;
                        }
                        if (hour >= 24){
                            hour -= 24;
                            dd = Integer.toString(Integer.parseInt(dd) + 1);
                            if (Integer.parseInt(dd) < 10)
                                dd = "0" + dd;
                        }
                        String minutesString;
                        if (minutes < 10)
                            minutesString = "0" + Integer.toString(minutes);
                        else
                            minutesString = Integer.toString(minutes);

                        String hoursString;
                        if (hour < 10)
                            hoursString = "0" + Integer.toString(hour);
                        else
                            hoursString = Integer.toString(hour);
                        String finalTime = hoursString + ":" + minutesString + ":" + seconds;
                        String finalDate = dd + "-" + mm + "-" + yyyy;

                        //Moving on
                        issuedDates.add(finalDate + "  " + finalTime);
                        users.add(new User(jsonArray.getJSONObject(i).getJSONObject("memberId").getString("name"), jsonArray.getJSONObject(i).getJSONObject("memberId").getString("regno"), jsonArray.getJSONObject(i).getJSONObject("memberId").getString("email"), jsonArray.getJSONObject(i).getJSONObject("memberId").getString("phoneno")));
                        quantities.add(jsonArray.getJSONObject(i).getString("quantity"));
                    }
                    if (users.size() == 0){
                        listOfIssuers.setVisibility(View.GONE);
                        noIssuers.setVisibility(View.VISIBLE);
                    } else {
                        listOfIssuers.setVisibility(View.VISIBLE);
                        noIssuers.setVisibility(View.GONE);
                        listOfIssuers.setAdapter((new ListOfUsersAdapter(EachComponentActivity.this, EachComponentActivity.this,users, issuedDates, quantities)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(EachComponentActivity.this, "An error occured!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                volleyError.printStackTrace();
                Toast.makeText(EachComponentActivity.this, "An error occured!!", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("token", token);
                return params;
            }
        };
        Volley.newRequestQueue(EachComponentActivity.this).add(stringRequest);
        //End of the request



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
            TapTargetView.showFor(EachComponentActivity.this, TapTarget.forView(fab, "Request a component!", "If the components aren't available, then you can request them from any of the issuers by calling them just by one touch :)").outerCircleColor(R.color.rippleColor).transparentTarget(true));
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

                        //Request for requesting to issue the component
                        StringRequest stringRequest1 = new StringRequest(Request.Method.POST, REQUEST_COMPONENT_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    String success = jsonObject.getString("success");
                                    String message = jsonObject.getString("message");
                                    Toast.makeText(EachComponentActivity.this, message, Toast.LENGTH_LONG).show();
                                    if (success.equals("true")){
                                        //availStr = Integer.toString(Integer.parseInt(availStr) - count);
                                        //availability.setText("Available: " + availStr);
                                        Intent i = new Intent(EachComponentActivity.this, TabbedActivity.class);
                                        i.putExtra("name", currentUsername);
                                        i.putExtra("email", currentUserEmail);
                                        i.putExtra("regnum", currentUserRegNum);
                                        i.putExtra("phonenum", currentUserPhoneNum);
                                        i.putExtra("numissued", numissue);
                                        i.putExtra("token", token);
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
                                params.put("id", id);
                                params.put("quantity", Integer.toString(count));
                                params.put("email", currentUserEmail);
                                params.put("token", token);
                                return params;
                            }
                        };
                        Volley.newRequestQueue(EachComponentActivity.this).add(stringRequest1);
                        //End of the request
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
    }
}
