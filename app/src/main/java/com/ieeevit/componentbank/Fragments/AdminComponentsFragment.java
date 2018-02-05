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

import com.ieeevit.componentbank.Activities.AdminTabbedActivity;
import com.ieeevit.componentbank.Adapters.ComponentsListAdapter;
import com.ieeevit.componentbank.Classes.Component;
import com.ieeevit.componentbank.NetworkAPIs.AdminAPI;
import com.ieeevit.componentbank.NetworkAPIs.MemberAPI;
import com.ieeevit.componentbank.NetworkModels.AllComponentsModel;
import com.ieeevit.componentbank.NetworkModels.BasicModel;
import com.ieeevit.componentbank.NetworkModels.ComponentModel;
import com.ieeevit.componentbank.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Yash 1300 on 07-01-2018.
 */

@SuppressLint("ValidFragment")
public class AdminComponentsFragment extends Fragment {

    @BindView(R.id.adminComponentsList) ListView components;
    @BindString(R.string.base_url_admin) String BASE_URL_ADMIN;
    @BindString(R.string.base_url) String BASE_URL_MEMBER;

    Context context;
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
        ButterKnife.bind(this, v);

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

        // Creating the retrofit instances
        Retrofit retrofitMember = new Retrofit.Builder().baseUrl(BASE_URL_MEMBER).addConverterFactory(GsonConverterFactory.create()).build();
        Retrofit retrofitAdmin = new Retrofit.Builder().baseUrl(BASE_URL_ADMIN).addConverterFactory(GsonConverterFactory.create()).build();

        MemberAPI memberAPI = retrofitMember.create(MemberAPI.class);
        final AdminAPI adminAPI = retrofitAdmin.create(AdminAPI.class);


        // Network call for getting the list of all the components
        Call<AllComponentsModel> getAllComponents = memberAPI.getAllComponents(token);
        getAllComponents.enqueue(new Callback<AllComponentsModel>() {
            @Override
            public void onResponse(Call<AllComponentsModel> call, retrofit2.Response<AllComponentsModel> response) {
                String success = response.body().getSuccess().toString(); // For parsing the success
                String message = response.body().getMessage(); // For parsing the message

                if (success.equals("false")){
                    progressDialog.dismiss();
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    List<ComponentModel> componentModels = response.body().getComponents();
                    componentList.clear();
                    valuesList.clear();
                    componentList = new ArrayList<>();
                    for (int position=0; position<componentModels.size(); position++){
                        Component component = new Component(componentModels.get(position).getName(), Integer.toString(componentModels.get(position).getValue()), Integer.toString(componentModels.get(position).getQuantity()), componentModels.get(position).getId());
                        componentList.add(component);
                        valuesList.add("Rs. " + Integer.toString(componentModels.get(position).getValue()));
                        components.setAdapter((new ComponentsListAdapter(context, componentList, valuesList,0))); // Setting the list of dates as list of values because just like dates, the values will be displayed as String. Did this to avoid creating a whole new adapter
                        if (position == (componentModels.size() - 1)){
                            progressDialog.dismiss();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AllComponentsModel> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
                Toast.makeText(context, "An error occured", Toast.LENGTH_SHORT).show();
            }
        });

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
                            countText.setText(Integer.toString(count));
                        }
                    });

                    dialogYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            // Network call for adding components
                            Call<BasicModel> addComponents = adminAPI.addComponents(token, compId, Integer.toString(count));
                            addComponents.enqueue(new Callback<BasicModel>() {
                                @Override
                                public void onResponse(Call<BasicModel> call, retrofit2.Response<BasicModel> response) {
                                    String success = response.body().getSuccess().toString();
                                    String message = response.body().getMessage();
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                    if (success.equals("false")){
                                        dialog.dismiss();
                                        return;
                                    }
                                    Intent intent = new Intent(context, AdminTabbedActivity.class);
                                    intent.putExtra("token", token);
                                    intent.putExtra("pagerItem",  "2");
                                    startActivity(intent);
                                }

                                @Override
                                public void onFailure(Call<BasicModel> call, Throwable t) {
                                    t.printStackTrace();
                                    Toast.makeText(context, "An error occured", Toast.LENGTH_SHORT).show();
                                }
                            });
                            // End of the network Call

                            /*StringRequest stringRequest1 = new StringRequest(Request.Method.POST, ADD_COMPONENTS_URL, new Response.Listener<String>() {
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
                            //End of the request*/
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
