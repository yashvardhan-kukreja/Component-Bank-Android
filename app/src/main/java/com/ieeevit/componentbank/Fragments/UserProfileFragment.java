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
    TextView name, regnum, componentsIssued;
    ListView componentsList;
    List<Component> components;
    List<String> dates;
    String CURRENTLY_ISSUED_COMPONENTS;
    ProgressDialog progressDialog;
    TextView noComponentsIssued;
    public UserProfileFragment(Context context, String currentUsername, String currentUserEmail, String currentUserRegNum, String currentUserPhoneNum) {
        this.context = context;
        this.currentUsername = currentUsername;
        this.currentUserEmail = currentUserEmail;
        this.currentUserRegNum = currentUserRegNum;
        this.currentUserPhoneNum = currentUserPhoneNum;
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
        //componentsIssued = v.findViewById(R.id.profileComponentsIssued);
        CURRENTLY_ISSUED_COMPONENTS = getResources().getString(R.string.base_url) + "/currentlyIssuedComponents";
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
                        //componentsIssued.setText("Components Issued: " + jsonObject.getString("number"));
                        JSONArray jsonArray = jsonObject.getJSONArray("componentsIssued");
                        components.clear();
                        dates.clear();
                        if (jsonArray.length() == 1){
                            componentsList.setVisibility(View.GONE);
                            noComponentsIssued.setVisibility(View.VISIBLE);
                        } else {
                            noComponentsIssued.setVisibility(View.GONE);
                            componentsList.setVisibility(View.VISIBLE);
                            for (int i=jsonArray.length()-1; i>-1;i--){
                                if (jsonArray.length()>1 && i==0)
                                    continue;
                                else if (jsonArray.getJSONObject(i).getString("returned").equals("true"))
                                    continue;
                                components.add((new Component(jsonArray.getJSONObject(i).getString("name"), jsonArray.getJSONObject(i).getString("code"), jsonArray.getJSONObject(i).getString("quantity"))));
                                dates.add(jsonArray.getJSONObject(i).getString("date"));
                                componentsList.setAdapter((new ComponentsListAdapter(context, components, dates,0)));

                            }
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
                return params;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);
        //End of the request


        return v;
    }
}
