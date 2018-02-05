package com.ieeevit.componentbank.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ieeevit.componentbank.NetworkAPIs.AuthAPI;
import com.ieeevit.componentbank.NetworkModels.BasicModel;
import com.ieeevit.componentbank.R;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Register extends AppCompatActivity {

    @BindView(R.id.registerName) EditText name;
    @BindView(R.id.registerRegNum) EditText regno;
    @BindView(R.id.registerEmail) EditText email;
    @BindView(R.id.registerPassword) EditText password;
    @BindView(R.id.registerContactNum) EditText contact;
    @BindView(R.id.registerConfirmPassword) EditText conpassword;
    @BindView(R.id.registerButton) Button register;
    @BindString(R.string.base_url_auth) String BASE_URL_AUTH;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(Register.this);
        progressDialog.setMessage("Registering you...");
        progressDialog.setCancelable(false);

        contact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>10){
                    contact.setText(charSequence.subSequence(0,10).toString());
                    contact.setSelection(10);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                if (name.getText().toString().isEmpty() || regno.getText().toString().isEmpty() || email.getText().toString().isEmpty() || password.getText().toString().isEmpty() || contact.getText().toString().isEmpty() || conpassword.getText().toString().isEmpty()){
                    progressDialog.dismiss();
                    Toast.makeText(Register.this, "Please enter all the details", Toast.LENGTH_LONG).show();
                } else {
                    if (!password.getText().toString().equals(conpassword.getText().toString())){
                        progressDialog.dismiss();
                        Toast.makeText(Register.this, "Password and confirm password doesn't match", Toast.LENGTH_LONG).show();
                    } else {

                        // Creating the retrofit instance
                        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL_AUTH).addConverterFactory(GsonConverterFactory.create()).build();
                        AuthAPI authAPI = retrofit.create(AuthAPI.class);

                        // Network call for registering the user
                        Call<BasicModel> register = authAPI.register(name.getText().toString(), regno.getText().toString(), email.getText().toString(), password.getText().toString(), contact.getText().toString());
                        register.enqueue(new Callback<BasicModel>() {
                            @Override
                            public void onResponse(Call<BasicModel> call, retrofit2.Response<BasicModel> response) {
                                progressDialog.dismiss();
                                String success = response.body().getSuccess().toString();
                                String message = response.body().getMessage();
                                if (success.equals("false")){
                                    Toast.makeText(Register.this, message, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(Register.this, "You have registered successfully", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(Register.this, LogInActivity.class));
                                }
                            }

                            @Override
                            public void onFailure(Call<BasicModel> call, Throwable t) {
                                progressDialog.dismiss();
                                Toast.makeText(Register.this, "An error occured", Toast.LENGTH_LONG).show();
                                t.printStackTrace();
                            }
                        });
                        // End of the network call
                    }
                }
            }
        });
    }
}
