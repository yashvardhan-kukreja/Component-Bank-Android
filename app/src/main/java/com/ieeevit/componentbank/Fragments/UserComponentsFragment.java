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
import java.util.List;


/**
 * Created by Yash 1300 on 12-12-2017.
 */

public class UserComponentsFragment extends Fragment {
    Context context;
    ListView components;
    List<Component> componentList;
    ProgressDialog progressDialog;
    String COMPONENT_LIST_GET_URL = "";
    ArrayList<String> namesOfUsers;
    ArrayList<String> regNumsOfUsers;
    ArrayList<String> phoneNumsOfUsers;
    ArrayList<String> issueDatesOfUsers;
    ArrayList<String> emailsOfUsers;


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

        //Request for getting the list of components with their respective details

        StringRequest stringRequest = new StringRequest(Request.Method.GET, COMPONENT_LIST_GET_URL, new Response.Listener<String>() {
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

                        final JSONArray jsonArray = jsonObject.getJSONArray("components"); // For parsing the json array of components
                        componentList.clear();
                        for (int position=0;position<jsonArray.length();position++){
                            Component component = new Component(jsonArray.getJSONObject(position).getString("name"), jsonArray.getJSONObject(position).getString("code"), jsonArray.getJSONObject(position).getString("quantity"));
                            componentList.add(component);
                            components.setAdapter((new ComponentsListAdapter(context, componentList)));
                            if (position == (jsonArray.length() - 1)){
                                progressDialog.dismiss();
                            }
                        }

                        // When an item of the components list is clicked

                        components.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                try {
                                    JSONArray jsonArray1 = jsonArray.getJSONObject(i).getJSONArray("issuedBy");
                                    for (int j=0;j<jsonArray1.length();j++){
                                        namesOfUsers.add(jsonArray1.getJSONObject(j).getString("name"));
                                        regNumsOfUsers.add(jsonArray1.getJSONObject(j).getString("regnum"));
                                        phoneNumsOfUsers.add(jsonArray1.getJSONObject(j).getString("phonenum"));
                                        issueDatesOfUsers.add(jsonArray1.getJSONObject(j).getString("issuedOn"));
                                        emailsOfUsers.add(jsonArray1.getJSONObject(j).getString("email"));
                                    }
                                    Intent intent = new Intent(context, EachComponentActivity.class);
                                    intent.putExtra("name", jsonArray.getJSONObject(i).getString("name")); // Sending the name of the component to the EachComponentActivity.java with the key "name"
                                    intent.putExtra("code", jsonArray.getJSONObject(i).getString("code")); // Sending the unique code of the component to the EachComponentActivity.java with the key "code"
                                    intent.putExtra("quantity", jsonArray.getJSONObject(i).getString("quantity")); // Sending the availability of the component to the EachComponentActivity.java with the key "quantity"
                                    intent.putExtra("value", jsonArray.getJSONObject(i).getString("value"));
                                    intent.putExtra("usernames", namesOfUsers.toArray());
                                    intent.putExtra("userregnums", regNumsOfUsers.toArray());
                                    intent.putExtra("userphonenums", phoneNumsOfUsers.toArray());
                                    intent.putExtra("userissuedates", issueDatesOfUsers.toArray());
                                    intent.putExtra("useremails", emailsOfUsers.toArray());
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
        });

        Volley.newRequestQueue(context).add(stringRequest);

        // End of the request



        return v;
    }
}
