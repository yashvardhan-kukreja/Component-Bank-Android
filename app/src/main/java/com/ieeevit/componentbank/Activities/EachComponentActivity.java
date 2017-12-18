package com.ieeevit.componentbank.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
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
String ISSUE_COMPONENT_URL = "";
EditText numberOfComponentsToBeIssued;
Button yesConfirmation, cancelConfirmation;
String[] names, regnums, phonenums, issuedDates, emails; //Users associated with those components;
AlertDialog confirmationDialog;
String currentUsername, currentUserEmail, currentUserRegNum, currentUserPhoneNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_component);
        users = new ArrayList<>();

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
        names = getIntent().getExtras().getStringArray("usernames");
        regnums = getIntent().getExtras().getStringArray("userregnums");
        phonenums = getIntent().getExtras().getStringArray("userphonenums");
        issuedDates = getIntent().getExtras().getStringArray("userissuedates");
        emails = getIntent().getExtras().getStringArray("useremails");

        for (int i=0;i<names.length;i++){
            users.add((new User(names[i], regnums[i], emails[i], phonenums[i])));
        }

        name = findViewById(R.id.componentName);
        code = findViewById(R.id.componentCode);
        availability = findViewById(R.id.componentsAvailable);
        value = findViewById(R.id.componentValue);
        listOfIssuers = findViewById(R.id.listOfComponentIssuers);
        fab = findViewById(R.id.issueAComponent);
        AlertDialog.Builder builder = new AlertDialog.Builder(EachComponentActivity.this);
        View v = getLayoutInflater().inflate(R.layout.dialog_component_confirmation, null, false);
        numberOfComponentsToBeIssued = v.findViewById(R.id.numberOfComponentsToBeIssued);
        yesConfirmation = v.findViewById(R.id.yesConfirmation);
        cancelConfirmation = v.findViewById(R.id.cancelConfirmation);
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
                                        availability.setText(Integer.toString(Integer.parseInt(availStr) - Integer.parseInt(numberOfComponentsToBeIssued.getText().toString())));
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
        availability.setText(availStr);
        value.setText(valStr);

        listOfIssuers.setAdapter((new ListOfUsersAdapter(EachComponentActivity.this, users)));
        listOfIssuers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                View v = getLayoutInflater().inflate(R.layout.dialog_user_details, null, false);
                TextView dialogName = v.findViewById(R.id.dialogUserName);
                TextView dialogRegNum = v.findViewById(R.id.dialogRegNum);
                TextView dialogEmail = v.findViewById(R.id.dialogEmailId);
                TextView dialogContactNum = v.findViewById(R.id.dialogContactNum);
                TextView dialogIssueDate = v.findViewById(R.id.dialogIssueDate);

                dialogName.setText(users.get(i).getName());
                dialogRegNum.setText("Reg. Number: "+users.get(i).getRegNum());
                dialogEmail.setText("E-mail ID: "+users.get(i).getEmail());
                dialogContactNum.setText("Contact Number: "+users.get(i).getPhonenum());
                dialogIssueDate.setText("Issued On: "+issuedDates[i]);
                AlertDialog.Builder builder = new AlertDialog.Builder(EachComponentActivity.this);
                builder.setView(v);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }
}
