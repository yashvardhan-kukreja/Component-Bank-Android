package com.ieeevit.componentbank.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
String LOGIN_URL;
ProgressDialog progressDialog;
CheckBox checkBox;
int keepMeLoggedIn = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

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

        LOGIN_URL = getResources().getString(R.string.base_url_auth) + "/login";
        progressDialog = new ProgressDialog(LogInActivity.this);
        progressDialog.setMessage("Logging In...");
        //progressDialog.setCancelable(false);
        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        login = findViewById(R.id.loginButton);
        signup = findViewById(R.id.signUpLink);
        checkBox = findViewById(R.id.keepMeLoggedIn);
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
                    StringRequest stringRequest1 = new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
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
                                    if (keepMeLoggedIn == 1){
                                        SharedPreferences sharedPreferences = getSharedPreferences("logindetails", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("email", email.getText().toString());
                                        editor.putString("name", jsonObject1.getString("name"));
                                        editor.putString("regnum", jsonObject1.getString("regno"));
                                        editor.putString("phonenum", jsonObject1.getString("phoneno"));
                                        editor.putString("numissued", jsonObject1.getString("issuedComponents"));
                                        editor.putString("token", jsonObject1.getString("token"));
                                        editor.putString("isAdmin", jsonObject1.getString("isAdmin"));
                                        editor.commit();
                                    }
                                    if (jsonObject1.getString("isAdmin").equals("0")){
                                        Toast.makeText(LogInActivity.this, "Aloha!!", Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(LogInActivity.this, TabbedActivity.class);
                                        i.putExtra("name", jsonObject1.getString("name"));
                                        i.putExtra("regnum", jsonObject1.getString("regno"));
                                        i.putExtra("email", jsonObject1.getString("email"));
                                        i.putExtra("phonenum", jsonObject1.getString("phoneno"));
                                        i.putExtra("numissued", jsonObject1.getString("issuedComponents"));
                                        i.putExtra("numrequested", jsonObject1.getString("requestedComponents"));
                                        i.putExtra("token", jsonObject1.getString("token"));
                                        startActivity(i);
                                    } else {
                                        Intent i = new Intent(LogInActivity.this, AdminTabbedActivity.class);
                                        i.putExtra("token", jsonObject1.getString("token"));
                                        Toast.makeText(LogInActivity.this, "Aloha Admin!!", Toast.LENGTH_LONG).show();
                                        startActivity(i);
                                    }
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
                            params.put("email", email.getText().toString());
                            params.put("password", password.getText().toString());
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
