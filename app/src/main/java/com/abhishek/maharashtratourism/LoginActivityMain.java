package com.abhishek.maharashtratourism;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivityMain extends AppCompatActivity {

    Button serviceProviderLoginButton,touristLoginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
        getSupportActionBar().hide();

        touristLoginButton=findViewById(R.id.touristLoginButton);
        serviceProviderLoginButton=findViewById(R.id.serviceProviderLoginButton);

        touristLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the button is clicked, start the TouristLoginActivity
                Intent intent = new Intent(LoginActivityMain.this, TouristLoginActivity.class);
                startActivity(intent);
            }
        });

        serviceProviderLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the button is clicked, start the serviceProviderLoginActivity
                Intent intent = new Intent(LoginActivityMain.this, ServiceProviderLoginActivity.class);
                startActivity(intent);
            }
        });
    }
}