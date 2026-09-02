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

public class TouristLoginActivity extends AppCompatActivity {

    private EditText numberEditText, passwordEditText;
    private Button loginButton;
    private TextView signupTextView;
    private FirebaseAuth mAuth; // Firebase Authentication instance
    private ProgressDialog progress;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_login);

        userRef= FirebaseDatabase.getInstance().getReference("users");

        mAuth = FirebaseAuth.getInstance(); // Initialize Firebase Auth
        numberEditText = findViewById(R.id.numberEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signupTextView = findViewById(R.id.signupTextView);
        progress=new ProgressDialog(this);
        progress.setMessage("Login User.....");

        loginButton.setOnClickListener(v -> loginUser());

        signupTextView.setOnClickListener(v -> {
            Intent intent = new Intent(TouristLoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        progress.show();
        String email = numberEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate inputs
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
            progress.dismiss();
        } else {
            // Firebase Authentication login
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                checkIfUser(mAuth.getCurrentUser().getUid());
                            }
                        } else {
                            // If login fails
                            Toast.makeText(TouristLoginActivity.this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }
                    });
        }
    }

    private void checkIfUser(String uid) {
        userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progress.dismiss();
                if (snapshot.exists()){
                    // Login successful
                    Toast.makeText(TouristLoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                    // Save the user ID (UID) to SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userId", uid); // Save the Firebase UID
                    editor.putBoolean("isLoggedIn", true);
                    editor.putInt("user",1);
                    editor.apply();

                    // Navigate to the MainActivity
                    Intent intent = new Intent(TouristLoginActivity.this, MainActivity.class);
                    startActivity(intent);

                    finish(); // Close the login screen
                }else{
                    Toast.makeText(getApplicationContext(), "Your are not a signup as user!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
