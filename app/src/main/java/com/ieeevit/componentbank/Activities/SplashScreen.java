package com.ieeevit.componentbank.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ieeevit.componentbank.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreen extends AppCompatActivity {

    @BindView(R.id.splashImage) ImageView splashImage;
    @BindView(R.id.ieeeSplash) ImageView ieeeSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);

        YoYo.with(Techniques.FadeIn).duration(2500).repeat(0).playOn(splashImage);
        YoYo.with(Techniques.FadeIn).duration(2500).repeat(0).playOn(ieeeSplash);
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity((new Intent(SplashScreen.this, LogInActivity.class)));
            }
        }, 2800);
    }
}
