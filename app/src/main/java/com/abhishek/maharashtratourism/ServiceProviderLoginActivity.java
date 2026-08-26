package com.abhishek.maharashtratourism;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ServiceProviderLoginActivity extends AppCompatActivity {
    private TextView signupTextView;
    private EditText emailEditText,passwordEditText;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private DatabaseReference guideDatabase;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_login);
        getSupportActionBar().hide();

        signupTextView=findViewById(R.id.signupTextView);
        signupTextView.setOnClickListener(v -> startActivity(new Intent(ServiceProviderLoginActivity.this,SPSignUpActivity.class)));

        progress = new ProgressDialog(this);
        progress.setMessage("Login...");

        emailEditText=findViewById(R.id.emailEditText);
        passwordEditText=findViewById(R.id.passwordEditText);
        loginButton=findViewById(R.id.loginButton);

        mAuth=FirebaseAuth.getInstance();
        guideDatabase= FirebaseDatabase.getInstance().getReference("guides");

        loginButton.setOnClickListener(v -> loginServiceProvider());

    }

    private void loginServiceProvider() {

        String email=emailEditText.getText().toString().trim();
        String pass=passwordEditText.getText().toString().trim();

        if(email.isEmpty() || pass.isEmpty()){
            Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
        }else{
            progress.show();
            mAuth.signInWithEmailAndPassword(email,pass)
                    .addOnCompleteListener(this,task -> {
                       if(task.isSuccessful()){
                           FirebaseUser guide=mAuth.getCurrentUser();

                           if(guide!=null){
                               checkIfAdmin(mAuth.getCurrentUser().getUid());
                           }
                       }else{
                           progress.dismiss();
                           Toast.makeText(this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
                       }
                    });
        }

    }

    private void checkIfAdmin(String uid) {

        guideDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progress.dismiss();
                if(snapshot.exists()){
                    Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences=getSharedPreferences("MyAppPrefs",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userId", uid); // Save the Firebase UID
                    editor.putBoolean("isLoggedIn", true);
                    editor.putInt("user",2);
                    editor.apply();

                    // Navigate to the MainActivity
                    Intent intent = new Intent(ServiceProviderLoginActivity.this, GuideDashboardActivity.class);
                    startActivity(intent);

                    finish(); // Close the login screen

                }else{
                    Toast.makeText(getApplicationContext(), "Your are not a Service provider!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progress.dismiss();
            }
        });

    }
}