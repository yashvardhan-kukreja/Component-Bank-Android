package com.ieeevit.componentbankredefined.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ieeevit.componentbankredefined.NetworkAPIs.AuthAPI;
import com.ieeevit.componentbankredefined.NetworkModels.LoginModel;
import com.ieeevit.componentbankredefined.R;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LogInActivity extends AppCompatActivity {

    @BindView(R.id.loginEmail) EditText email;
    @BindView(R.id.loginPassword) EditText password;
    @BindView(R.id.loginButton) Button login;
    @BindView(R.id.signUpLink) TextView signup;
    @BindView(R.id.keepMeLoggedIn) CheckBox checkBox;
    @BindString(R.string.base_url_auth) String BASE_URL_AUTH;
    ProgressDialog progressDialog;
    int keepMeLoggedIn = 0;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ButterKnife.bind(this);

        SharedPreferences sharedPreferences = getSharedPreferences("logindetails", MODE_PRIVATE);
        if (!(sharedPreferences.getString("token", "") == "" || sharedPreferences.getString("token", "") == null)){
            Intent i = new Intent(LogInActivity.this, TabbedActivity.class);
            i.putExtra("name", sharedPreferences.getString("name", ""));
            i.putExtra("regnum", sharedPreferences.getString("regnum", ""));
            i.putExtra("email", sharedPreferences.getString("email", ""));
            i.putExtra("phonenum", sharedPreferences.getString("phonenum", ""));
            i.putExtra("token", sharedPreferences.getString("token", ""));
            i.putExtra("numissued", sharedPreferences.getString("numissued", ""));
            if (sharedPreferences.getString("isAdmin","").equals("0"))
                startActivity(i);
            else {
                Intent i2 = new Intent(LogInActivity.this, AdminTabbedActivity.class);
                i2.putExtra("token", sharedPreferences.getString("token", ""));
                startActivity(i2);
            }
            return;
        }

        progressDialog = new ProgressDialog(LogInActivity.this);
        progressDialog.setMessage("Logging In...");
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    keepMeLoggedIn = 1;
                else
                    keepMeLoggedIn = 0;

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
                    Toast.makeText(LogInActivity.this, "Please enter all the details", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog.show();

                    // Creating the retrofit instance
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL_AUTH).addConverterFactory(GsonConverterFactory.create()).build();
                    AuthAPI authAPI = retrofit.create(AuthAPI.class);

                    // Network call for logging in
                    Call<LoginModel> login = authAPI.login(email.getText().toString(), password.getText().toString());
                    login.enqueue(new Callback<LoginModel>() {
                        @Override
                        public void onResponse(Call<LoginModel> call, retrofit2.Response<LoginModel> response) {
                            progressDialog.dismiss();
                            String success = response.body().getSuccess().toString();
                            String message = response.body().getMessage();
                            if (success.equals("false")){
                                Toast.makeText(LogInActivity.this, message, Toast.LENGTH_LONG).show();
                            } else {
                                if (keepMeLoggedIn == 1){
                                    SharedPreferences sharedPreferences = getSharedPreferences("logindetails", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("email", response.body().getEmail());
                                    editor.putString("name", response.body().getName());
                                    editor.putString("regnum", response.body().getRegno());
                                    editor.putString("phonenum", response.body().getPhoneno());
                                    editor.putString("numissued", Integer.toString(response.body().getIssuedComponents()));
                                    editor.putString("token", response.body().getToken());
                                    editor.putString("isAdmin", response.body().getIsAdmin());
                                    editor.commit();
                                }
                                if (response.body().getIsAdmin().equals("0")){
                                    Toast.makeText(LogInActivity.this, "Aloha!!", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(LogInActivity.this, TabbedActivity.class);
                                    i.putExtra("name", response.body().getName());
                                    i.putExtra("regnum", response.body().getRegno());
                                    i.putExtra("email", response.body().getEmail());
                                    i.putExtra("phonenum", response.body().getPhoneno());
                                    i.putExtra("numissued", Integer.toString(response.body().getIssuedComponents()));
                                    i.putExtra("numrequested", Integer.toString(response.body().getRequesedComponents()));
                                    i.putExtra("token", response.body().getToken());
                                    startActivity(i);
                                } else {
                                    Intent i = new Intent(LogInActivity.this, AdminTabbedActivity.class);
                                    i.putExtra("token", response.body().getToken());
                                    i.putExtra("pagerItem",  "0");
                                    Toast.makeText(LogInActivity.this, "Aloha Admin!!", Toast.LENGTH_LONG).show();
                                    startActivity(i);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginModel> call, Throwable t) {
                            progressDialog.dismiss();
                            t.printStackTrace();
                            Toast.makeText(LogInActivity.this, "An error occured", Toast.LENGTH_LONG).show();

                        }
                    });
                    // End of the network call
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInActivity.this, Register.class));
            }
        });
    }
}
