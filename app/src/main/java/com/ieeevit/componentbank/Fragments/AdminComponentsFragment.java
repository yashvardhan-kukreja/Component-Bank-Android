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
import com.ieeevit.componentbank.Activities.AdminTabbedActivity;
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
 * Created by Yash 1300 on 07-01-2018.
 */

@SuppressLint("ValidFragment")
public class AdminComponentsFragment extends Fragment {
    Context context;
    ListView components;
    String COMPONENT_LIST_GET_URL, ADD_COMPONENTS_URL;
    ProgressDialog progressDialog;
    String token;
    List<Component> componentList;
    List<String> valuesList;
    TextView countText;
    int count = 0;
    Button dialogYes, dialogCancel;
    ImageView minus, plus;
    String compId;
    AlertDialog.Builder builder;
    AlertDialog dialog;

    public AdminComponentsFragment(Context context, String token) {
        this.context = context;
        this.token = token;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_components, container, false);
        components = v.findViewById(R.id.adminComponentsList);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading the components...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.dismiss();

        builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_component_confirmation, null, false);
        TextView title = dialogView.findViewById(R.id.dialogTitle);
        title.setText("Add more such components");
        dialogYes = dialogView.findViewById(R.id.yesConfirmation);
        dialogCancel = dialogView.findViewById(R.id.cancelConfirmation);
        minus = dialogView.findViewById(R.id.minusButton);
        plus = dialogView.findViewById(R.id.plusButton);
        countText = dialogView.findViewById(R.id.countText);
        builder.setView(dialogView);

        componentList = new ArrayList<>();
        valuesList = new ArrayList<>();

        //URLs for network call
        COMPONENT_LIST_GET_URL = getResources().getString(R.string.base_url) + "/getAllComponents";
        ADD_COMPONENTS_URL = getResources().getString(R.string.base_url_admin) + "/addComponents";
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
                        valuesList.clear();
                        componentList = new ArrayList<>();
                        for (int position=0;position<jsonArray.length();position++){
                            Component component = new Component(jsonArray.getJSONObject(position).getString("name"), jsonArray.getJSONObject(position).getString("value"), jsonArray.getJSONObject(position).getString("quantity"), jsonArray.getJSONObject(position).getString("_id"));
                            componentList.add(component);
                            valuesList.add("Rs. " + jsonArray.getJSONObject(position).getString("value"));
                            components.setAdapter((new ComponentsListAdapter(context, componentList, valuesList,0))); // Setting the list of dates as list of values because just like dates, the values will be displayed as String. Did this to avoid creating a whole new adapter
                            if (position == (jsonArray.length() - 1)){
                                progressDialog.dismiss();
                            }
                        }
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
        //End of request



        // When an item of the components list is clicked

        components.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    compId  = componentList.get(i).getId();
                    dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.show();
                    plus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            count += 1;
                            countText.setText(Integer.toString(count));
                        }
                    });

                    minus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            count -= 1;
                            if (count <= 0)
                                count = 0;
                            countText.setText(Integer.toString(count));
                        }
                    });

                    dialogYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            StringRequest stringRequest1 = new StringRequest(Request.Method.POST, ADD_COMPONENTS_URL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String s) {
                                    try {
                                        JSONObject jsonObject1 = new JSONObject(s);
                                        String success = jsonObject1.getString("success");
                                        String message = jsonObject1.getString("message");
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                        if (success.equals("false")){
                                            dialog.dismiss();
                                            return;
                                        }
                                        Intent intent = new Intent(context, AdminTabbedActivity.class);
                                        intent.putExtra("token", token);
                                        intent.putExtra("pagerItem",  "2");
                                        startActivity(intent);
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
                                    params.put("id", compId);
                                    params.put("quantity", Integer.toString(count));
                                    params.put("token", token);
                                    return params;
                                }
                            };
                            Volley.newRequestQueue(context).add(stringRequest1);
                            //End of the request
                        }
                    });

                    dialogCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            Intent i2 = new Intent(context, AdminTabbedActivity.class);
                            i2.putExtra("token", token);
                            i2.putExtra("pagerItem", "2");
                            startActivity(i2);
                            return;
                        }
                    });
            }
        });
        return v;
    }
}
