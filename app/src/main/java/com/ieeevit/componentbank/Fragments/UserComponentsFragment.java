package com.ieeevit.componentbank.Fragments;

import android.annotation.SuppressLint;
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
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ieeevit.componentbank.Activities.EachComponentActivity;
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
public class UserComponentsFragment extends Fragment {
    Context context;
    ListView components;
    List<Component> componentList;
    ProgressDialog progressDialog;
    String COMPONENT_LIST_GET_URL;
    String currentUsername, currentUserEmail, currentUserRegNum, currentUserPhoneNum, numreq, numissue, token;

    @SuppressLint("ValidFragment")
    public UserComponentsFragment(Context context, String currentUsername, String currentUserEmail, String currentUserRegNum, String currentUserPhoneNum, String numreq, String numissue, String token) {
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
        View v = inflater.inflate(R.layout.fragment_user_components_page, container, false);
        components = v.findViewById(R.id.componentsList);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading the components...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        componentList = new ArrayList<>();
        COMPONENT_LIST_GET_URL = getResources().getString(R.string.base_url) + "/getAllComponents";
        //Request for getting the list of all components

        StringRequest stringRequest = new StringRequest(Request.Method.POST, COMPONENT_LIST_GET_URL, new Response.Listener<String>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String success = jsonObject.getString("success"); // For parsing the success
                    String message = jsonObject.getString("message"); // For parsing the message

                    if (success.equals("false")){
                        progressDialog.dismiss();
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        final JSONArray jsonArray = jsonObject.getJSONArray("components"); // For parsing the json array of components
                        componentList.clear();
                        componentList = new ArrayList<>();
                        for (int position=0;position<jsonArray.length();position++){
                            Component component = new Component(jsonArray.getJSONObject(position).getString("name"), jsonArray.getJSONObject(position).getString("value"), jsonArray.getJSONObject(position).getString("quantity"), jsonArray.getJSONObject(position).getString("_id"));
                            componentList.add(component);
                            components.setAdapter((new ComponentsListAdapter(context, componentList, null,1))); // Setting the list of dates as null because there is no need of dates
                            if (position == (jsonArray.length() - 1)){
                                progressDialog.dismiss();
                            }
                        }

                        // When an item of the components list is clicked

                        components.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                try {
                                    String id  = jsonArray.getJSONObject(i).getString("_id");
                                    Intent intent = new Intent(context, EachComponentActivity.class);
                                    intent.putExtra("componentId", id);
                                    intent.putExtra("currentusername", currentUsername);
                                    intent.putExtra("currentuserregnum", currentUserRegNum);
                                    intent.putExtra("currentuseremail", currentUserEmail);
                                    intent.putExtra("currentuserphonenum", currentUserPhoneNum);
                                    intent.putExtra("numissued", numissue);
                                    intent.putExtra("numrequested", numreq);
                                    intent.putExtra("token", token);
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    Toast.makeText(context, "An error occured", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(context, "An error occured", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
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

        // End of the request

        return v;
    }
}
