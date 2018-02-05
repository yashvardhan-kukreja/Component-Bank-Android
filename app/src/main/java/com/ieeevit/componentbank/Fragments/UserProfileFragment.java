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

import com.ieeevit.componentbank.Adapters.ComponentsListAdapter;
import com.ieeevit.componentbank.Classes.Component;
import com.ieeevit.componentbank.NetworkAPIs.MemberAPI;
import com.ieeevit.componentbank.NetworkModels.GetMemberComponentsModel;
import com.ieeevit.componentbank.NetworkModels.TransactionMemberComponentsModel;
import com.ieeevit.componentbank.NetworkModels.TransactionModel;
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
 * Created by Yash 1300 on 12-12-2017.
 */

@SuppressLint("ValidFragment")
public class UserProfileFragment extends Fragment {

    @BindView(R.id.profilePageGreeting) TextView name;
    @BindView(R.id.profileRegNum) TextView regnum;
    @BindView(R.id.profileCompIssued) TextView componentsIssued;
    @BindView(R.id.profileCompRequested) TextView componentsRequested;
    @BindView(R.id.profileIssuedComponentsList) ListView componentsList;
    @BindView(R.id.noComponentsIssued) TextView noComponentsIssued;
    @BindString(R.string.base_url) String BASE_URL_MEMBER;

    Context context;
    String currentUsername, currentUserEmail, currentUserRegNum, currentUserPhoneNum;
    List<Component> components;
    List<String> dates;
    ProgressDialog progressDialog;
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
        ButterKnife.bind(this, v);

        components = new ArrayList<>();
        dates = new ArrayList<>();
        componentsRequested.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading your details...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        componentsList.setVisibility(View.GONE);
        name.setText(currentUsername);
        regnum.setText(currentUserRegNum);

        // Creating the retrofit instance
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL_MEMBER).addConverterFactory(GsonConverterFactory.create()).build();
        MemberAPI memberAPI = retrofit.create(MemberAPI.class);

        // Network call for getting the list of issued components with some specific details
        Call<GetMemberComponentsModel> getIssuedComponents = memberAPI.getIssuedComponents(token);
        getIssuedComponents.enqueue(new Callback<GetMemberComponentsModel>() {
            @Override
            public void onResponse(Call<GetMemberComponentsModel> call, retrofit2.Response<GetMemberComponentsModel> response) {
                progressDialog.dismiss();
                String success = response.body().getSuccess().toString();
                String message = response.body().getMessage();
                if (success.equals("true")){
                    List<TransactionMemberComponentsModel> transactions= response.body().getTransactions();
                    components.clear();
                    dates.clear();
                    if (transactions.size() == 0){
                        componentsList.setVisibility(View.GONE);
                        noComponentsIssued.setVisibility(View.VISIBLE);
                        componentsIssued.setText("Components Issued: 0");
                    } else {
                        noComponentsIssued.setVisibility(View.GONE);
                        componentsList.setVisibility(View.VISIBLE);
                        int sum = 0;
                        for (int i=transactions.size()-1; i>-1; i--){
                            sum += transactions.get(i).getQuantity();
                            String timestamp = transactions.get(i).getDate();
                            components.add((new Component(transactions.get(i).getComponentName(), null, Integer.toString(transactions.get(i).getQuantity()), transactions.get(i).getComponentId())));
                            dates.add(syncTimeStamp(timestamp));
                            componentsList.setAdapter((new ComponentsListAdapter(context, components, dates,0)));
                        }
                        componentsIssued.setText("Components Issued: " + Integer.toString(sum));
                    }
                } else {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GetMemberComponentsModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context, "An error occured", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
        // End of the network call
        return v;
    }

    // Function for syncing time and date with IST
    private String syncTimeStamp(String timestamp){
        StringBuilder dateBuilder = new StringBuilder(timestamp);
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

        return (finalDate + " " + finalTime);
    }
}
