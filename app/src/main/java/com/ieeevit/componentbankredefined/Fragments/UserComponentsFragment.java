package com.ieeevit.componentbankredefined.Fragments;

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

import com.ieeevit.componentbankredefined.Activities.EachComponentActivity;
import com.ieeevit.componentbankredefined.Adapters.ComponentsListAdapter;
import com.ieeevit.componentbankredefined.Classes.Component;
import com.ieeevit.componentbankredefined.NetworkAPIs.MemberAPI;
import com.ieeevit.componentbankredefined.NetworkModels.AllComponentsModel;
import com.ieeevit.componentbankredefined.NetworkModels.ComponentModel;
import com.ieeevit.componentbankredefined.R;

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
 * Created by Yash 1300 on 12-12-2017.
 */

@SuppressLint("ValidFragment")
public class UserComponentsFragment extends Fragment {

    @BindView(R.id.componentsList) ListView components;
    @BindString(R.string.base_url) String BASE_URL_MEMBER;Context context;

    List<Component> componentList;
    ProgressDialog progressDialog;
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
        ButterKnife.bind(this, v);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading the components...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        componentList = new ArrayList<>();

        // Creating the retrofit instance
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL_MEMBER).addConverterFactory(GsonConverterFactory.create()).build();
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
        return v;
    }
}
