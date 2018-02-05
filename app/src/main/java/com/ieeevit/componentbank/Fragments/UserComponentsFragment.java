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

import com.ieeevit.componentbank.Activities.EachComponentActivity;
import com.ieeevit.componentbank.Adapters.ComponentsListAdapter;
import com.ieeevit.componentbank.Classes.Component;
import com.ieeevit.componentbank.NetworkAPIs.MemberAPI;
import com.ieeevit.componentbank.NetworkModels.AllComponentsModel;
import com.ieeevit.componentbank.NetworkModels.ComponentModel;
import com.ieeevit.componentbank.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


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
        COMPONENT_LIST_GET_URL = getResources().getString(R.string.base_url);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(COMPONENT_LIST_GET_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MemberAPI memberAPI = retrofit.create(MemberAPI.class);

        // Network Call for getting the list of all the components
        Call<AllComponentsModel> getAllComponents = memberAPI.getAllComponents(token);
        getAllComponents.enqueue(new Callback<AllComponentsModel>() {
            @Override
            public void onResponse(Call<AllComponentsModel> call, retrofit2.Response<AllComponentsModel> response) {
                progressDialog.dismiss();
                String success = response.body().getSuccess().toString(); // For parsing the success
                String message = response.body().getMessage(); // For parsing the message

                if (success.equals("false")){
                    progressDialog.dismiss();
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    final List<ComponentModel> componentModels = response.body().getComponents();
                    componentList.clear();
                    componentList = new ArrayList<>();
                    for (int position=0;position<componentModels.size();position++){
                        Component component = new Component(componentModels.get(position).getName(), Integer.toString(componentModels.get(position).getValue()), Integer.toString(componentModels.get(position).getQuantity()), componentModels.get(position).getId());
                        componentList.add(component);
                        components.setAdapter((new ComponentsListAdapter(context, componentList, null,1))); // Setting the list of dates as null because there is no need of dates
                        if (position == (componentModels.size() - 1)){
                            progressDialog.dismiss();
                        }
                    }

                    // When an item of the components list is clicked

                    components.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String id  = componentModels.get(i).getId();
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
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<AllComponentsModel> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
                Toast.makeText(context, "An error occured", Toast.LENGTH_SHORT).show();
            }
        });
        // End of the network call

        /*StringRequest stringRequest = new StringRequest(Request.Method.POST, COMPONENT_LIST_GET_URL, new Response.Listener<String>() {
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

        // End of the request*/

        return v;
    }
}
