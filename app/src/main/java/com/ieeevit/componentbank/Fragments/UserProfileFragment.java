package com.ieeevit.componentbank.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ieeevit.componentbank.Adapters.ComponentsListAdapter;
import com.ieeevit.componentbank.Classes.Component;
import com.ieeevit.componentbank.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yash 1300 on 12-12-2017.
 */

@SuppressLint("ValidFragment")
public class UserProfileFragment extends Fragment {
    Context context;
    String currentUsername, currentUserEmail, currentUserRegNum, currentUserPhoneNum;
    TextView name, regnum, componentsIssued, componentsRequested;
    ListView componentsList;
    List<Component> components;
    List<String> dates;
    String CURRENTLY_ISSUED_COMPONENTS;
    ProgressDialog progressDialog;
    TextView noComponentsIssued;
    String numreq, numissue, token;
    public UserProfileFragment(Context context, String currentUsername, String currentUserEmail, String currentUserRegNum, String currentUserPhoneNum, String numreq, String numissue, String token) {
        this.context = context;
        this.currentUsername = currentUsername;
        this.currentUserEmail = currentUserEmail;
        this.currentUserRegNum = currentUserRegNum;
        this.currentUserPhoneNum = currentUserPhoneNum;
        this.numreq = numreq;
        this.numissue = numissue;
        this.token = token;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_profile_page, container, false);
        components = new ArrayList<>();
        dates = new ArrayList<>();
        name = v.findViewById(R.id.profilePageGreeting);
        noComponentsIssued = v.findViewById(R.id.noComponentsIssued);
        regnum = v.findViewById(R.id.profileRegNum);
        componentsIssued = v.findViewById(R.id.profileCompIssued);
        componentsRequested = v.findViewById(R.id.profileCompRequested);
        componentsRequested.setVisibility(View.GONE);
        CURRENTLY_ISSUED_COMPONENTS = getResources().getString(R.string.base_url) + "/getIssuedComponents";
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading your details...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        componentsList = v.findViewById(R.id.profileIssuedComponentsList);
        componentsList.setVisibility(View.GONE);
        name.setText(currentUsername);
        regnum.setText(currentUserRegNum);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, CURRENTLY_ISSUED_COMPONENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject =  new JSONObject(s);
                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("message");
                    if (success.equals("true")){
                        //componentsRequested.setText("Components Requested: " + numreq);
                        JSONArray jsonArray = jsonObject.getJSONArray("components");
                        components.clear();
                        dates.clear();
                        if (jsonArray.length() == 0){
                            componentsList.setVisibility(View.GONE);
                            noComponentsIssued.setVisibility(View.VISIBLE);
                            componentsIssued.setText("Components Issued: 0");
                        } else {
                            noComponentsIssued.setVisibility(View.GONE);
                            componentsList.setVisibility(View.VISIBLE);
                            int sum = 0;
                            for (int i=jsonArray.length()-1; i>-1;i--){
                                sum += Integer.parseInt(jsonArray.getJSONObject(i).getString("quantity"));
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
                                components.add((new Component(jsonArray.getJSONObject(i).getString("componentName"), null, jsonArray.getJSONObject(i).getString("quantity"), jsonArray.getJSONObject(i).getString("componentId"))));
                                dates.add(finalDate + "  " + finalTime);
                                componentsList.setAdapter((new ComponentsListAdapter(context, components, dates,0)));
                            }
                            componentsIssued.setText("Components Issued: " + Integer.toString(sum));
                        }
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(context, "An error occured", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                Toast.makeText(context, "An error occured", Toast.LENGTH_LONG).show();
                volleyError.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", currentUserEmail);
                params.put("token", token);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);
        //End of the request

        return v;
    }
}
