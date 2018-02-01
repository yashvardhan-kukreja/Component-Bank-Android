package com.ieeevit.componentbank.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ieeevit.componentbank.Activities.AdminTabbedActivity;
import com.ieeevit.componentbank.Adapters.ListOfIssuersAdapter;
import com.ieeevit.componentbank.Classes.User;
import com.ieeevit.componentbank.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yash 1300 on 04-01-2018.
 */

@SuppressLint("ValidFragment")
public class AdminIssuersFragment extends Fragment {
    Context context;
    String GET_ISSUERS_URL;
    String GET_REQUESTS_URL;
    ProgressDialog progressDialog;
    List<String> dates;
    List<String> quantities;
    List<String> transactionIds;
    List<String> compNames;
    List<User> users;
    ListView issuers;
    String token;
    String transId;
    String APPROVE_REQUEST_URL;
    String RETURN_COMPONENT_URL;
    String DELETE_REQUEST_URL;
    TextView mainTitle;
    int choice; //0 for issuers, 1 for requests

    @SuppressLint("ValidFragment")
    public AdminIssuersFragment(Context context, String token, int choice) {
        this.context = context;
        this.choice = choice;
        this.token = token;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_issuers, container, false);

        GET_ISSUERS_URL = getResources().getString(R.string.base_url_admin) + "/issuers";
        GET_REQUESTS_URL = getResources().getString(R.string.base_url_admin) + "/requests";

        APPROVE_REQUEST_URL = getContext().getResources().getString(R.string.base_url_admin) + "/approve";
        RETURN_COMPONENT_URL = getContext().getResources().getString(R.string.base_url_admin) + "/return";
        DELETE_REQUEST_URL = getContext().getResources().getString(R.string.base_url_admin) + "/deleteRequest";

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.dismiss();
        dates = new ArrayList<>();
        quantities = new ArrayList<>();
        users = new ArrayList<>();
        transactionIds = new ArrayList<>();
        compNames = new ArrayList<>();
        issuers = v.findViewById(R.id.issuersListAdmin);
        mainTitle = v.findViewById(R.id.adminIssuersMainTitle);
        String main_url;
        if (choice == 0){
            main_url = GET_ISSUERS_URL;
            mainTitle.setText("Have the issuers returned the components?");
        }
        else{
            main_url = GET_REQUESTS_URL;
            mainTitle.setText("Approve/Delete the component requests!");
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, main_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    if (success.equals("false")){
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dates.clear();
                    users.clear();
                    quantities.clear();
                    transactionIds.clear();
                    compNames.clear();
                    JSONArray jsonArray = jsonObject.getJSONArray("output");
                    if (jsonArray.length() == 0)
                        progressDialog.dismiss();
                    else {

                        for (int i=(jsonArray.length()-1);i>-1;i--){
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
                            minutes += 30;
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
                            dates.add(finalDate + "  " + finalTime);
                            quantities.add(jsonArray.getJSONObject(i).getString("quantity"));
                            compNames.add(jsonArray.getJSONObject(i).getString("componentName"));



                            transactionIds.add(jsonArray.getJSONObject(i).getString("_id"));
                            users.add((new User(jsonArray.getJSONObject(i).getJSONObject("memberId").getString("name"), jsonArray.getJSONObject(i).getJSONObject("memberId").getString("regno"), jsonArray.getJSONObject(i).getJSONObject("memberId").getString("email"), jsonArray.getJSONObject(i).getJSONObject("memberId").getString("phoneno"))));
                            issuers.setAdapter((new ListOfIssuersAdapter(context, users, dates, quantities, compNames, choice)));
                            if (i == 0 )
                                progressDialog.dismiss();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(context, "An error occured!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                volleyError.printStackTrace();
                Toast.makeText(context, "An error occured!!", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                return params;
            }
        };
        Volley.newRequestQueue(context).add(stringRequest);
        //End of the request

        issuers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = i;
                transId = transactionIds.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_admin, null, false);

                TextView mainText = dialogView.findViewById(R.id.dialogAdminText);
                TextView subText = dialogView.findViewById(R.id.dialogAdminSubText);
                Button yes = dialogView.findViewById(R.id.adminDialogYes);
                Button cancel = dialogView.findViewById(R.id.adminDialogCancel);
                builder.setView(dialogView);
                final AlertDialog dialog = builder.create();
                if (choice == 0){
                    mainText.setText("Return the component");
                    subText.setText("Has the issuer returned the component?");
                    cancel.setText("Cancel");
                    dialog.show();
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //progressDialog.setMessage("Returning the component...");
                            //progressDialog.show();
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, RETURN_COMPONENT_URL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String s) {
                                    // progressDialog.dismiss();
                                    try {
                                        JSONObject jsonObject = new JSONObject(s);
                                        String message = jsonObject.getString("message");
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        Intent i = new Intent(context, AdminTabbedActivity.class);
                                        i.putExtra("token", token);
                                        i.putExtra("pagerItem",  "1");
                                        getContext().startActivity(i);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(context, "An error occured", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    //progressDialog.dismiss();
                                    volleyError.printStackTrace();
                                    Toast.makeText(context, "An error occured", Toast.LENGTH_SHORT).show();
                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("id", transId);
                                    params.put("token", token);
                                    return params;
                                }
                            };
                            Volley.newRequestQueue(context).add(stringRequest);
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                } else {
                    mainText.setText("Approve/Delete the request");
                    subText.setText("Do you want to approve or delete the specified request?");
                    dialog.show();
                    yes.setText("Approve");
                    cancel.setText("Delete");
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //progressDialog.setMessage("Approving the request...");
                            //progressDialog.show();
                            StringRequest stringRequest1 = new StringRequest(Request.Method.POST, APPROVE_REQUEST_URL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String s) {
                                    //progressDialog.dismiss();
                                    try {
                                        JSONObject jsonObject = new JSONObject(s);
                                        String message = jsonObject.getString("message");
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        Intent i = new Intent(context, AdminTabbedActivity.class);
                                        i.putExtra("token", token);
                                        i.putExtra("pagerItem",  "0");
                                        getContext().startActivity(i);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(context, "An error occured", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    //progressDialog.dismiss();
                                    volleyError.printStackTrace();
                                    Toast.makeText(context, "An error occured", Toast.LENGTH_SHORT).show();
                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("id", transId);
                                    params.put("token", token);
                                    return params;
                                }
                            };
                            Volley.newRequestQueue(context).add(stringRequest1);
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_REQUEST_URL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String s) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(s);
                                        String success = jsonObject.getString("success");
                                        String message = jsonObject.getString("message");
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                        if (success.equals("false")){
                                            dialog.dismiss();
                                            return;
                                        }
                                        Intent i = new Intent(context, AdminTabbedActivity.class);
                                        i.putExtra("token", token);
                                        i.putExtra("pagerItem",  "0");
                                        getContext().startActivity(i);
                                    } catch (JSONException e) {
                                        Toast.makeText(context, "An error occured", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    volleyError.printStackTrace();
                                    Toast.makeText(context, "An error occured", Toast.LENGTH_SHORT).show();
                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("id", transId);
                                    params.put("token", token);
                                    return params;
                                }
                            };
                            Volley.newRequestQueue(context).add(stringRequest);
                            //End of the request

                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        return v;
    }
}
