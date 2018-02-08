package com.ieeevit.componentbankredefined.Fragments;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ieeevit.componentbankredefined.Activities.AdminTabbedActivity;
import com.ieeevit.componentbankredefined.Adapters.ListOfIssuersAdapter;
import com.ieeevit.componentbankredefined.Classes.User;
import com.ieeevit.componentbankredefined.NetworkAPIs.AdminAPI;
import com.ieeevit.componentbankredefined.NetworkModels.BasicModel;
import com.ieeevit.componentbankredefined.NetworkModels.GetMemberReqIssueModel;
import com.ieeevit.componentbankredefined.NetworkModels.TransactionReqIssuersModel;
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
 * Created by Yash 1300 on 04-01-2018.
 */

@SuppressLint("ValidFragment")
public class AdminIssuersFragment extends Fragment {

    @BindView(R.id.issuersListAdmin) ListView issuers;
    @BindView(R.id.adminIssuersMainTitle) TextView mainTitle;
    @BindString(R.string.base_url_admin) String BASE_URL_ADMIN;

    Context context;
    ProgressDialog progressDialog;
    List<String> dates;
    List<String> quantities;
    List<String> transactionIds;
    List<String> compNames;
    List<User> users;
    String token;
    String transId;
    int choice; //0 for issuers, 1 for requests

    @SuppressLint("ValidFragment")
    public AdminIssuersFragment(Context context, String token, int choice) {
        this.context = context;
        this.choice = choice;
        this.token = token;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_issuers, container, false);
        ButterKnife.bind(this, v);
        
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.dismiss();
        dates = new ArrayList<>();
        quantities = new ArrayList<>();
        users = new ArrayList<>();
        transactionIds = new ArrayList<>();
        compNames = new ArrayList<>();

        // Creating the retrofit instance
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL_ADMIN).addConverterFactory(GsonConverterFactory.create()).build();
        final AdminAPI adminAPI = retrofit.create(AdminAPI.class);

        // Declaring the network call on the basis of choice
        Call<GetMemberReqIssueModel> mainCall;
        if (choice == 0){
            mainCall = adminAPI.getIssuers(token);
            mainTitle.setText("Have the issuers returned the components?");
        }
        else{
            mainCall = adminAPI.getRequests(token);
            mainTitle.setText("Approve/Delete the component requests!");
        }

        // Network call getting the list of issuers or requests on the basis of the choice
        mainCall.enqueue(new Callback<GetMemberReqIssueModel>() {
            @Override
            public void onResponse(Call<GetMemberReqIssueModel> call, retrofit2.Response<GetMemberReqIssueModel> response) {
                String success = response.body().getSuccess().toString();
                String message = response.body().getMessage();
                if (success.equals("false")){
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    return;
                }
                dates.clear();
                users.clear();
                quantities.clear();
                transactionIds.clear();
                compNames.clear();

                List<TransactionReqIssuersModel> transactions = response.body().getOutput();
                if (transactions.size() == 0)
                    progressDialog.dismiss();
                else {
                    for (int i=(transactions.size()-1);i>-1;i--){
                        String timestamp = transactions.get(i).getDate();
                        dates.add(syncTimeStamp(timestamp));
                        quantities.add(Integer.toString(transactions.get(i).getQuantity()));
                        compNames.add(transactions.get(i).getComponentName());
                        transactionIds.add(transactions.get(i).getId());

                        users.add((new User(transactions.get(i).getMemberId().getName(), transactions.get(i).getMemberId().getRegno(), transactions.get(i).getMemberId().getEmail(), transactions.get(i).getMemberId().getPhoneno())));
                        issuers.setAdapter((new ListOfIssuersAdapter(context, users, dates, quantities, compNames, choice)));
                        if (i == 0 )
                            progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetMemberReqIssueModel> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
                Toast.makeText(context, "An error occured!!", Toast.LENGTH_SHORT).show();
            }
        });
        // End of the network call

        issuers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = i;
                transId = transactionIds.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_admin, null, false);

                TextView mainText = dialogView.findViewById(R.id.dialogAdminText);
                TextView subText = dialogView.findViewById(R.id.dialogAdminSubText);
                Button yes = dialogView.findViewById(R.id.adminDialogYes);
                Button cancel = dialogView.findViewById(R.id.adminDialogCancel);
                builder.setView(dialogView);
                final AlertDialog dialog = builder.create();
                if (choice == 0){
                    mainText.setText("Return the component");
                    subText.setText("Has the issuer returned the component?");
                    cancel.setText("Cancel");
                    dialog.show();
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Network call for returning the component
                            Call<BasicModel> returnComponent = adminAPI.returnComponent(token, transId);
                            returnComponent.enqueue(new Callback<BasicModel>() {
                                @Override
                                public void onResponse(Call<BasicModel> call, retrofit2.Response<BasicModel> response) {
                                    String message = response.body().getMessage();
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    Intent i = new Intent(context, AdminTabbedActivity.class);
                                    i.putExtra("token", token);
                                    i.putExtra("pagerItem",  "1");
                                    getContext().startActivity(i);
                                }

                                @Override
                                public void onFailure(Call<BasicModel> call, Throwable t) {
                                    t.printStackTrace();
                                    Toast.makeText(context, "An error occured", Toast.LENGTH_SHORT).show();
                                }
                            });
                            // End of the network call
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                } else {
                    mainText.setText("Approve/Delete the request");
                    subText.setText("Do you want to approve or delete the specified request?");
                    dialog.show();
                    yes.setText("Approve");
                    cancel.setText("Delete");
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            // Network call for approving the request of user for issuing a component
                            Call<BasicModel> approve = adminAPI.approve(token, transId);
                            approve.enqueue(new Callback<BasicModel>() {
                                @Override
                                public void onResponse(Call<BasicModel> call, retrofit2.Response<BasicModel> response) {
                                    String message = response.body().getMessage();
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    Intent i = new Intent(context, AdminTabbedActivity.class);
                                    i.putExtra("token", token);
                                    i.putExtra("pagerItem",  "0");
                                    getContext().startActivity(i);
                                }

                                @Override
                                public void onFailure(Call<BasicModel> call, Throwable t) {
                                    t.printStackTrace();
                                    Toast.makeText(context, "An error occured", Toast.LENGTH_SHORT).show();

                                }
                            });
                            // End of the network call
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            // Network call for deleting the request of a user for issuing a component
                            Call<BasicModel> deleteRequest = adminAPI.deleteRequest(token, transId);
                            deleteRequest.enqueue(new Callback<BasicModel>() {
                                @Override
                                public void onResponse(Call<BasicModel> call, retrofit2.Response<BasicModel> response) {
                                    String success = response.body().getSuccess().toString();
                                    String message = response.body().getMessage();
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                    if (success.equals("false")){
                                        dialog.dismiss();
                                        return;
                                    }
                                    Intent i = new Intent(context, AdminTabbedActivity.class);
                                    i.putExtra("token", token);
                                    i.putExtra("pagerItem",  "0");
                                    getContext().startActivity(i);
                                }

                                @Override
                                public void onFailure(Call<BasicModel> call, Throwable t) {
                                    t.printStackTrace();
                                    Toast.makeText(context, "An error occured", Toast.LENGTH_SHORT).show();
                                }
                            });
                            // End of the network call

                            dialog.dismiss();
                        }
                    });
                }
            }
        });

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
