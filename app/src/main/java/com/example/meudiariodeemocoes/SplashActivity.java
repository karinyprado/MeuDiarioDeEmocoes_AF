package com.example.meudiariodeemocoes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(this::navigateToMain, SPLASH_TIMEOUT);
    }

    private void navigateToMain() {
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}