package com.ieeevit.componentbankredefined.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.ieeevit.componentbankredefined.Adapters.ListOfUsersAdapter;
import com.ieeevit.componentbankredefined.Classes.User;
import com.ieeevit.componentbankredefined.NetworkAPIs.MemberAPI;
import com.ieeevit.componentbankredefined.NetworkModels.BasicModel;
import com.ieeevit.componentbankredefined.NetworkModels.GetIssuersModel;
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

public class EachComponentActivity extends AppCompatActivity {

    @BindView(R.id.componentName) TextView name;
    @BindView(R.id.componentsAvailable) TextView availability;
    @BindView(R.id.componentValue) TextView value;
    @BindView(R.id.listOfComponentIssuers) ListView listOfIssuers;
    @BindView(R.id.issueAComponent) FloatingActionButton fab;
    @BindView(R.id.noIssuers) TextView noIssuers;
    @BindString(R.string.base_url) String BASE_URL_MEMBER;

    TextView countText;
    ProgressDialog progressDialog;
    String token;
    String nameStr, availStr, valStr, id; //Component Details
    List<User> users;
    ImageView plus, minus;
    Button yesConfirmation, deleteConfirmation, cancelConfirmation;
    ArrayList<String>issuedDates, quantities; //Users associated with those components;
    AlertDialog confirmationDialog;
    String currentUsername, currentUserEmail, currentUserRegNum, currentUserPhoneNum, numreq, numissue;
    int count = 0;

    @Override
    public void onBackPressed() {
        currentUsername = getIntent().getExtras().getString("currentusername");
        currentUserEmail = getIntent().getExtras().getString("currentuseremail");
        currentUserRegNum = getIntent().getExtras().getString("currentuserregnum");
        currentUserPhoneNum = getIntent().getExtras().getString("currentuserphonenum");
        token = getIntent().getExtras().getString("token");
        numreq = getIntent().getExtras().getString("numrequested");
        numissue = getIntent().getExtras().getString("numissued");
        Intent i = new Intent(EachComponentActivity.this, TabbedActivity.class);
        i.putExtra("name", currentUsername);
        i.putExtra("email", currentUserEmail);
        i.putExtra("regnum", currentUserRegNum);
        i.putExtra("phonenum", currentUserPhoneNum);
        i.putExtra("numissued", numissue);
        i.putExtra("numrequested", numreq);
        i.putExtra("token", token);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_component);
        ButterKnife.bind(this);
        users = new ArrayList<>();
        issuedDates = new ArrayList<>();
        quantities =  new ArrayList<>();

        //Fetching the details of the component from the previous activity
        id = getIntent().getExtras().getString("componentId");


        //Fetching the details of the current user from the previous activity
        currentUsername = getIntent().getExtras().getString("currentusername");
        currentUserEmail = getIntent().getExtras().getString("currentuseremail");
        currentUserRegNum = getIntent().getExtras().getString("currentuserregnum");
        currentUserPhoneNum = getIntent().getExtras().getString("currentuserphonenum");
        numreq = getIntent().getExtras().getString("numrequested");
        numissue = getIntent().getExtras().getString("numissued");
        token = getIntent().getExtras().getString("token");

        listOfIssuers.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(EachComponentActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Creating the retrofit instance
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL_MEMBER).addConverterFactory(GsonConverterFactory.create()).build();
        final MemberAPI memberAPI = retrofit.create(MemberAPI.class);

        // Network Call for getting all the issuers of the current component with their details
        Call<GetIssuersModel> getIssuers = memberAPI.getIssuers(token, id);
        getIssuers.enqueue(new Callback<GetIssuersModel>() {
            @Override
            public void onResponse(Call<GetIssuersModel> call, retrofit2.Response<GetIssuersModel> response) {
                progressDialog.dismiss();
                String success = response.body().getSuccess().toString();
                String message = response.body().getMessage();
                if (success.equals("false")){
                    Toast.makeText(EachComponentActivity.this, message, Toast.LENGTH_SHORT).show();
                    return;
                }

                //Fetching the details of the component
                nameStr = response.body().getComponent().getName();
                availStr = Integer.toString(response.body().getComponent().getQuantity());
                valStr = Integer.toString(response.body().getComponent().getValue());

                //Setting the details of the component
                name.setText(nameStr);
                availability.setText("Available: " + availStr);
                value.setText("Value: Rs." + valStr);

                users.clear();
                issuedDates.clear();
                quantities.clear();
                List<TransactionReqIssuersModel> transactions = response.body().getTransactions();
                for (int i=0;i<transactions.size();i++){
                    String timestamp = transactions.get(i).getDate();
                    issuedDates.add(syncTimeStamp(timestamp));
                    users.add(new User(transactions.get(i).getMemberId().getName(), transactions.get(i).getMemberId().getRegno(), transactions.get(i).getMemberId().getEmail(), transactions.get(i).getMemberId().getPhoneno()));
                    quantities.add(Integer.toString(transactions.get(i).getQuantity()));
                }
                if (users.size() == 0){
                    listOfIssuers.setVisibility(View.GONE);
                    noIssuers.setVisibility(View.VISIBLE);
                } else {
                    listOfIssuers.setVisibility(View.VISIBLE);
                    noIssuers.setVisibility(View.GONE);
                    listOfIssuers.setAdapter((new ListOfUsersAdapter(EachComponentActivity.this, EachComponentActivity.this,users, issuedDates, quantities)));
                }
            }

            @Override
            public void onFailure(Call<GetIssuersModel> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
                Toast.makeText(EachComponentActivity.this, "An error occured", Toast.LENGTH_SHORT).show();

            }
        });
        // End of the network call

        //Building and handling the final alert dialog for issuing the component
        final AlertDialog.Builder builder = new AlertDialog.Builder(EachComponentActivity.this);
        View v = getLayoutInflater().inflate(R.layout.dialog_component_confirmation, null, false);
        plus = v.findViewById(R.id.plusButton);
        minus = v.findViewById(R.id.minusButton);
        deleteConfirmation = v.findViewById(R.id.deleteConfirmation);
        deleteConfirmation.setVisibility(View.GONE);
        countText = v.findViewById(R.id.countText);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count+=1;
                countText.setText(Integer.toString(count));
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count == 0)
                    return;
                count-=1;
                countText.setText(Integer.toString(count));
            }
        });
        yesConfirmation = v.findViewById(R.id.yesConfirmation);
        cancelConfirmation = v.findViewById(R.id.cancelConfirmation);
        builder.setView(v);
        confirmationDialog = builder.create();

        // When the app is run for the first time, a target view will pop up displaying the description about how to issue a component using a Tap Target View
        SharedPreferences sharedPreferences2 = getSharedPreferences("firsttimeissuepage", MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        if (sharedPreferences2.getString("firsttime", "").equals("") || sharedPreferences2.getString("firsttime", "").equals(null) || sharedPreferences2.getString("firsttime", "").equals("false")){
            TapTargetView.showFor(EachComponentActivity.this, TapTarget.forView(fab, "Request a component!", "If the components aren't available, then you can request them from any of the issuers by calling them just by one touch :)").outerCircleColor(R.color.rippleColor).transparentTarget(true));
            editor2.putString("firsttime", "true");
            editor2.commit();
        }

        //FAB handling the issue of the component
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmationDialog.show();
                yesConfirmation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Checking whether the user has requested the components whose quantity is available
                        if (count == 0){
                            Toast.makeText(EachComponentActivity.this, "Seriously! Zero components?", Toast.LENGTH_LONG).show();
                            return;
                        }
                         else if (Integer.parseInt(availStr) - count < 0){
                            Toast.makeText(EachComponentActivity.this, "Cool down! We don't have so many components available", Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Network call for requesting to issue the component
                        Call<BasicModel> requestComponent = memberAPI.requestComponent(token, id, Integer.toString(count));
                        requestComponent.enqueue(new Callback<BasicModel>() {
                            @Override
                            public void onResponse(Call<BasicModel> call, retrofit2.Response<BasicModel> response) {
                                String success = response.body().getSuccess().toString();
                                String message = response.body().getMessage();
                                Toast.makeText(EachComponentActivity.this, message, Toast.LENGTH_LONG).show();
                                if (success.equals("true")){
                                    Intent i = new Intent(EachComponentActivity.this, TabbedActivity.class);
                                    i.putExtra("name", currentUsername);
                                    i.putExtra("email", currentUserEmail);
                                    i.putExtra("regnum", currentUserRegNum);
                                    i.putExtra("phonenum", currentUserPhoneNum);
                                    i.putExtra("numissued", numissue);
                                    i.putExtra("token", token);
                                    startActivity(i);
                                }
                                confirmationDialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<BasicModel> call, Throwable t) {
                                confirmationDialog.dismiss();
                                Toast.makeText(EachComponentActivity.this, "An error occured", Toast.LENGTH_LONG).show();
                                t.printStackTrace();
                            }
                        });
                        // End of the network call
                    }
                });

                cancelConfirmation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        confirmationDialog.dismiss();
                    }
                });
            }
        });
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
