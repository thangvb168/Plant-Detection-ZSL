package com.example.plansdetection.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.plansdetection.R;
import com.example.plansdetection.fragment.DetectFragment;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private TextView tvWelcome;
    private LottieAnimationView lottie;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tvWelcome = findViewById(R.id.tvWelcome);
        lottie = findViewById(R.id.lottie);
        Log.v(TAG, "Error Splash");
        tvWelcome.animate().translationY(500).setDuration(3000).setStartDelay(0);
        lottie.animate().setDuration(4000).setStartDelay(100);


        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, 4000);
    }

}