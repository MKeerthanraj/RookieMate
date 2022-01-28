package com.kenoDigital.rookiemate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.kenoDigital.rookiemate.SignInPages.SignInPage;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        (new Handler()).postDelayed(this::openApp,2000);
    }

    private void openApp() {
        startActivity(new Intent(SplashScreen.this, SignInPage.class));
        finish();
    }

}