package com.ieeevit.componentbank.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LogInActivity extends AppCompatActivity {
EditText email, password;
Button login;
TextView signup;
String LOGIN_URL = "http://192.168.43.76:8000/api";
String EMAIL_URL = "http://192.168.43.76:8000/api";
ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        progressDialog = new ProgressDialog(LogInActivity.this);
        progressDialog.setMessage("Logging In...");
        progressDialog.setCancelable(false);
        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        login = findViewById(R.id.loginButton);
        signup = findViewById(R.id.signUpLink);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();

                final String emailString = email.getText().toString();
                final String passwordString = password.getText().toString();
                if (emailString.isEmpty() || passwordString.isEmpty()){
                    Toast.makeText(LogInActivity.this, "Please enter all the details", Toast.LENGTH_LONG).show();
                } else {
                    StringRequest stringRequest1 = new StringRequest(Request.Method.POST, LOGIN_URL + "/login", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            progressDialog.dismiss();
                            try {
                                JSONObject jsonObject1 = new JSONObject(s);
                                String success = jsonObject1.getString("success");
                                String message = jsonObject1.getString("message");
                                if (success.equals("false")){
                                    Toast.makeText(LogInActivity.this, message, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(LogInActivity.this, "You have successfully logged in", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(LogInActivity.this, TabbedActivity.class);
                                    i.putExtra("name", jsonObject1.getString("name"));
                                    i.putExtra("regnum", jsonObject1.getString("regnum"));
                                    i.putExtra("email", jsonObject1.getString("email"));
                                    i.putExtra("phonenum", jsonObject1.getString("phonenum"));
                                    startActivity(i);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(LogInActivity.this, "An error occured", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            progressDialog.dismiss();
                            volleyError.printStackTrace();
                            Toast.makeText(LogInActivity.this, "An error occured", Toast.LENGTH_LONG).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("username", emailString);
                            params.put("password", passwordString);
                            return params;
                        }
                    };
                    Volley.newRequestQueue(LogInActivity.this).add(stringRequest1);
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
