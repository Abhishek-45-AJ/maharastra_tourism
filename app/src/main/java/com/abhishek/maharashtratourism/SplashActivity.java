package com.abhishek.maharashtratourism;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences=getSharedPreferences("MyAppPrefs",MODE_PRIVATE);
                boolean isLoggedIn=sharedPreferences.getBoolean("isLoggedIn",false);
                String userId=sharedPreferences.getString("userId",null);
                int user=sharedPreferences.getInt("user",1) ;
                Intent intent;

                if(isLoggedIn){
                    if(user==1){
                        intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else if (user==2) {
                        intent = new Intent(SplashActivity.this, GuideDashboardActivity.class);
                        startActivity(intent);
                    }
                }else{
                    intent = new Intent(SplashActivity.this, LoginActivityMain.class);
                    startActivity(intent);
                }

                finish();
            }
        },3000);
    }
}