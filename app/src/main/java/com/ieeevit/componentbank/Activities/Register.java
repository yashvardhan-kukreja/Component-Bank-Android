package com.ieeevit.componentbank.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ieeevit.componentbank.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
EditText name, regno, email, password, contact, conpassword;
Button register;
ProgressDialog progressDialog;
String REGISTER_URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        REGISTER_URL = getResources().getString(R.string.base_url) + "/create";
        progressDialog = new ProgressDialog(Register.this);
        progressDialog.setMessage("Registering you...");
        progressDialog.setCancelable(false);
        name = findViewById(R.id.registerName);
        regno = findViewById(R.id.registerRegNum);
        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        contact = findViewById(R.id.registerContactNum);
        conpassword = findViewById(R.id.registerConfirmPassword);
        register = findViewById(R.id.registerButton);

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
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                progressDialog.dismiss();
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    String success = jsonObject.getString("success");
                                    String message = jsonObject.getString("message");
                                    if (success.equals("false")){
                                        Toast.makeText(Register.this, message, Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(Register.this, "You have registered successfully", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(Register.this, LogInActivity.class));
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(Register.this, "An error occured", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                progressDialog.dismiss();
                                Toast.makeText(Register.this, "An error occured", Toast.LENGTH_LONG).show();
                                volleyError.printStackTrace();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("name", name.getText().toString());
                                params.put("username", regno.getText().toString());
                                params.put("password", password.getText().toString());
                                params.put("email", email.getText().toString());
                                params.put("phoneno", contact.getText().toString());
                                return params;
                            }
                        };
                        Volley.newRequestQueue(Register.this).add(stringRequest);
                    }
                }

            }
        });
    }
}
