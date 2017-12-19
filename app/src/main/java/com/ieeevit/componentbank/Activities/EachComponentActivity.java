package com.ieeevit.componentbank.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
TextView name, code, availability, value;
ListView listOfIssuers;
String nameStr, codeStr, availStr, valStr; //Component Details
List<User> users;
FloatingActionButton fab;
String ISSUE_COMPONENT_URL;
EditText numberOfComponentsToBeIssued;
Button yesConfirmation, cancelConfirmation;
ArrayList<String> names, regnums, phonenums, issuedDates, emails, quantities; //Users associated with those components;
AlertDialog confirmationDialog;
String currentUsername, currentUserEmail, currentUserRegNum, currentUserPhoneNum;
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
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(EachComponentActivity.this);

        name = findViewById(R.id.componentName);
        code = findViewById(R.id.componentCode);
        availability = findViewById(R.id.componentsAvailable);
        value = findViewById(R.id.componentValue);
        listOfIssuers = findViewById(R.id.listOfComponentIssuers);
        fab = findViewById(R.id.issueAComponent);
        final AlertDialog.Builder builder = new AlertDialog.Builder(EachComponentActivity.this);
        View v = getLayoutInflater().inflate(R.layout.dialog_component_confirmation, null, false);
        numberOfComponentsToBeIssued = v.findViewById(R.id.numberOfComponentsToBeIssued);
        yesConfirmation = v.findViewById(R.id.yesConfirmation);
        cancelConfirmation = v.findViewById(R.id.cancelConfirmation);
        builder.setView(v);
        confirmationDialog = builder.create();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmationDialog.show();
                yesConfirmation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Checking whether the user has requested the components whose quantity is available
                        if (Integer.parseInt(availStr) - Integer.parseInt(numberOfComponentsToBeIssued.getText().toString()) < 0){
                            Toast.makeText(EachComponentActivity.this, "Cool down! We don't have so may available components", Toast.LENGTH_LONG).show();
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
                                        availStr = Integer.toString(Integer.parseInt(availStr) - Integer.parseInt(numberOfComponentsToBeIssued.getText().toString()));
                                        availability.setText("Available: " + availStr);
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
                                params.put("number", numberOfComponentsToBeIssued.getText().toString());
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
        value.setText("Value: " + valStr);

        listOfIssuers.setAdapter((new ListOfUsersAdapter(EachComponentActivity.this, users, issuedDates, quantities)));


    }
}
