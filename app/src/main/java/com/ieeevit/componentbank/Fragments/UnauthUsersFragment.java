package com.ieeevit.componentbank.Fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.ieeevit.componentbank.Adapters.ListOfUnauthUsersAdapter;
import com.ieeevit.componentbank.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yash 1300 on 10-01-2018.
 */

@SuppressLint("ValidFragment")
public class UnauthUsersFragment extends Fragment {

    Context context;
    String token;
    String finalRegnum;
    TextView mainTitle;
    ListView unauthList;
    String GET_UNAUTH_USERS_URL, AUTHORIZE_USER_URL;
    List<String> names, regnums;

    public UnauthUsersFragment(Context context, String token) {
        this.context = context;
        this.token = token;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_issuers, null, false);
        mainTitle = v.findViewById(R.id.adminIssuersMainTitle);
        mainTitle.setText("Unauthorized users");
        unauthList = v.findViewById(R.id.issuersListAdmin);
        names = new ArrayList<>();
        regnums = new ArrayList<>();
        AUTHORIZE_USER_URL = getResources().getString(R.string.base_url_admin) + "/authorize";
        GET_UNAUTH_USERS_URL = getResources().getString(R.string.base_url_admin) + "/unauthorizedUsers";

        //Request for getting the unauthorized members
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_UNAUTH_USERS_URL, new Response.Listener<String>() {
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
                    names.clear();
                    regnums.clear();
                    JSONArray jsonArray = jsonObject.getJSONArray("users");
                    for (int i=0;i<jsonArray.length();i++){
                        names.add(jsonArray.getJSONObject(i).getString("name"));
                        regnums.add(jsonArray.getJSONObject(i).getString("regno"));
                        ListOfUnauthUsersAdapter unauthAdapter = new ListOfUnauthUsersAdapter(context, names, regnums);
                        unauthList.setAdapter(unauthAdapter);
                    }

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
                params.put("token", token);
                return params;
            }
        };
        Volley.newRequestQueue(context).add(stringRequest);
        //End of the request


        unauthList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder authBuilder = new AlertDialog.Builder(context);
                finalRegnum = regnums.get(i);
                View authView = LayoutInflater.from(context).inflate(R.layout.dialog_authorize_user, null, false);
                Button authBtn = authView.findViewById(R.id.authUserButton);
                Button authCancel = authView.findViewById(R.id.authUserCancel);

                authBuilder.setView(authView);
                final AlertDialog authoriseDialog = authBuilder.create();
                authoriseDialog.show();
                authBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, AUTHORIZE_USER_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    String success = jsonObject.getString("success");
                                    String message = jsonObject.getString("message");
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                    System.out.println(token);
                                    Intent i = new Intent(context, AdminTabbedActivity.class);
                                    i.putExtra("token", token);
                                    i.putExtra("pagerItem", "3");
                                    startActivity(i);
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
                                params.put("token", token);
                                params.put("regno", finalRegnum);
                                return params;
                            }
                        };
                        Volley.newRequestQueue(context).add(stringRequest);
                    }
                });

                authCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        authoriseDialog.dismiss();
                        Intent i = new Intent(context, AdminTabbedActivity.class);
                        i.putExtra("token", token);
                        i.putExtra("pagerItem", "3");
                        startActivity(i);
                    }
                });
            }
        });
        return v;
    }
}
