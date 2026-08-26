package com.abhishek.maharashtratourism;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    private TextView loginTextView;
    private EditText nameEditText, emailEditText, phoneEditText, passwordEditText, confirmPasswordEditText,otpPhoneEditText;
    private ImageView imageViewProfile;
    private Button buttonSelectImage, signUpButton,sendPhoneOtpButton,verifyPhoneOtpButton;
    private Uri imageUri = null;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String verificationId; // Stores Phone OTP Verification ID
    private boolean isPhoneVerified=false;
    private ProgressDialog progress;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance(); // Initialize Firebase Auth
        loginTextView = findViewById(R.id.loginTextView);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        signUpButton = findViewById(R.id.signUpButton);
        otpPhoneEditText=findViewById(R.id.otpPhoneEditText);
        sendPhoneOtpButton=findViewById(R.id.sendPhoneOtpButton);
        verifyPhoneOtpButton=findViewById(R.id.verifyPhoneOtpButton);

        progress=new ProgressDialog(this);

        loginTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, TouristLoginActivity.class);
            startActivity(intent);
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");

        //mobile number verification process
        sendPhoneOtpButton.setOnClickListener(v -> sendPhoneOTP());
        verifyPhoneOtpButton.setOnClickListener(v -> verifyPhoneOTP());

        buttonSelectImage.setOnClickListener(v -> selectImage());

        signUpButton.setOnClickListener(v -> registerUser());
    }

    private void sendPhoneOTP() {

        progress.setMessage("Sending OTP.....");
        progress.show();

        sendPhoneOtpButton.setEnabled(false);
        String phone = phoneEditText.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
            sendPhoneOtpButton.setEnabled(true);
            progress.dismiss();
            return;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phone, 60, TimeUnit.SECONDS, this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        isPhoneVerified = true;
                        enableSignUp();
                        phoneEditText.setEnabled(false);
                        sendPhoneOtpButton.setEnabled(false);
                        otpPhoneEditText.setVisibility(View.GONE);
                        verifyPhoneOtpButton.setVisibility(View.GONE);
                        progress.dismiss();
                        Toast.makeText(SignUpActivity.this, "Phone Verified Automatically!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        sendPhoneOtpButton.setEnabled(true);
                        progress.dismiss();
                        Toast.makeText(SignUpActivity.this, "Phone Verification Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        SignUpActivity.this.verificationId = verificationId;
                        otpPhoneEditText.setVisibility(View.VISIBLE);
                        verifyPhoneOtpButton.setVisibility(View.VISIBLE);
                        progress.dismiss();
                        Toast.makeText(SignUpActivity.this, "OTP Sent!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        
    }

    private void verifyPhoneOTP() {

        progress.setMessage("Verifying OTP.....");
        progress.show();

        String otp = otpPhoneEditText.getText().toString().trim();
        if (TextUtils.isEmpty(otp)) {
            Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show();
            progress.dismiss();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                isPhoneVerified = true;
                enableSignUp();
                Toast.makeText(this, "Mobile Number Verified!", Toast.LENGTH_SHORT).show();
                phoneEditText.setEnabled(false);
                sendPhoneOtpButton.setEnabled(false);
                otpPhoneEditText.setVisibility(View.GONE);
                verifyPhoneOtpButton.setVisibility(View.GONE);
                progress.dismiss();
            } else {
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                sendPhoneOtpButton.setEnabled(true);
                progress.dismiss();
            }
        });
    }

    private void enableSignUp() {
        if (isPhoneVerified) {
            signUpButton.setEnabled(true);
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imageViewProfile.setImageURI(imageUri);
        }
    }

    private void registerUser() {
        String name = nameEditText.getText().toString().trim();
        String mobile = phoneEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (name.isEmpty() || mobile.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else if (mobile.length() != 10) {
            Toast.makeText(SignUpActivity.this, "Please enter valid phone number", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(SignUpActivity.this, "Password must contain 6 characters", Toast.LENGTH_SHORT).show();
        } else {

            progress.setMessage("Registering User.....");
            progress.show();
            // Firebase Authentication - Create user with email and password
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user != null ? user.getUid() : null;

                            if (userId != null) {
                                if (imageUri != null) {
                                    uploadImageAndSaveUser(userId, name, mobile, email, password);
                                } else {
                                    saveUserToDatabase(userId, name, mobile, email, password, null);
                                }
                            }
                        } else {
                            Toast.makeText(SignUpActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }
                    });
        }
    }

    private void uploadImageAndSaveUser(String userId, String name, String mobile, String email, String password) {
        String imageId = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child("profile_images/" + imageId);
        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            saveUserToDatabase(userId, name, mobile, email, password, uri.toString());
        })).addOnFailureListener(e -> {
            Log.e("SignUp", "Image upload failed: " + e.getMessage());
            Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
            progress.dismiss();
        });
    }

    private void saveUserToDatabase(String userId, String name, String mobile, String email, String password, String profileImageUrl) {
        User user = new User(name, mobile, email, password, profileImageUrl);

        databaseReference.child(userId).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignUpActivity.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                progress.dismiss();
                finish();
            } else {
                Toast.makeText(SignUpActivity.this, "Registration failed!"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        });
    }
}
