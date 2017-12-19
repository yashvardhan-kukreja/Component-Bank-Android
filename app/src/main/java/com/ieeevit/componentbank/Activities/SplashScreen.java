package com.ieeevit.componentbank.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ieeevit.componentbank.R;

public class SplashScreen extends AppCompatActivity {
TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        title = findViewById(R.id.splashTitle);
        YoYo.with(Techniques.FadeIn).duration(2500).repeat(0).playOn(title);
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity((new Intent(SplashScreen.this, LogInActivity.class)));
            }
        }, 3000);
    }
}
