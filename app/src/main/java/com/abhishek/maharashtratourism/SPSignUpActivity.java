package com.abhishek.maharashtratourism;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class SPSignUpActivity extends AppCompatActivity {

    private EditText nameEditText,emailEditText,phoneEditText,otpPhoneEditText,cityEditText,experienceEditText,feesEditText,passwordEdittext,confirmPasswordEditText,spinnerLanguages,spinnerSpecialization;
    private Button sendPhoneOtpButton,verifyPhoneOtpButton,selectImageBtn,btnSignUp,buttonUploadAadhar,buttonUploadOther,buttonSaveDoc;
    private ImageView serviceProviderImage;
    private Uri imageUri=null, aadharUri, otherUri;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String verificationId; // Stores Phone OTP Verification ID
    private boolean isPhoneVerified=false,isAdharUpload=false,isCertificateUpload=false,isDocUpload=false;
    HashMap<String, String> documentUrls = new HashMap<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spsign_up);
        getSupportActionBar().hide();

        TextView signupText=findViewById(R.id.signupText);
        signupText.setOnClickListener(v -> startActivity(new Intent(SPSignUpActivity.this,ServiceProviderLoginActivity.class)));

        progressDialog = new ProgressDialog(this);

        nameEditText=findViewById(R.id.nameEditText);
        emailEditText=findViewById(R.id.emailEditText);
        phoneEditText=findViewById(R.id.phoneEditText);
        otpPhoneEditText=findViewById(R.id.otpPhoneEditText);
        cityEditText=findViewById(R.id.cityEditText);
        experienceEditText=findViewById(R.id.experienceEditText);
        feesEditText=findViewById(R.id.feesEditText);
        passwordEdittext=findViewById(R.id.passwordEdittext);
        confirmPasswordEditText=findViewById(R.id.confirmPasswordEditText);
        spinnerLanguages=findViewById(R.id.spinnerLanguages);
        spinnerSpecialization=findViewById(R.id.spinnerSpecialization);
        sendPhoneOtpButton=findViewById(R.id.sendPhoneOtpButton);
        verifyPhoneOtpButton=findViewById(R.id.verifyPhoneOtpButton);
        selectImageBtn=findViewById(R.id.selectImageBtn);
        btnSignUp=findViewById(R.id.btnSignUp);
        serviceProviderImage=findViewById(R.id.serviceProviderImage);
        buttonSaveDoc=findViewById(R.id.buttonSaveDoc);

        buttonUploadAadhar=findViewById(R.id.buttonUploadAadhar);
        buttonUploadOther=findViewById(R.id.buttonUploadOther);

        databaseReference = FirebaseDatabase.getInstance().getReference("guides");
        storageReference = FirebaseStorage.getInstance().getReference("guide_profiles");
        mAuth = FirebaseAuth.getInstance();

        //mobile number verification process
        sendPhoneOtpButton.setOnClickListener(v -> sendPhoneOTP());
        verifyPhoneOtpButton.setOnClickListener(v -> verifyPhoneOTP());

        buttonUploadAadhar.setOnClickListener(v -> selectDocument(101));
        buttonUploadOther.setOnClickListener(v -> selectDocument(103));

        buttonSaveDoc.setOnClickListener(v -> uploadDocumentsAndSaveData());

        selectImageBtn.setOnClickListener(v -> selectImage());
        btnSignUp.setOnClickListener(v -> registerGuide());

    }

    private void sendPhoneOTP() {
        progressDialog.setMessage("Sending OTP....");
        progressDialog.show();
        sendPhoneOtpButton.setEnabled(false);
        String phone = phoneEditText.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            sendPhoneOtpButton.setEnabled(true);
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
                        progressDialog.dismiss();
                        Toast.makeText(SPSignUpActivity.this, "Phone Verified Automatically!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        sendPhoneOtpButton.setEnabled(true);
                        progressDialog.dismiss();
                        Toast.makeText(SPSignUpActivity.this, "Phone Verification Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        SPSignUpActivity.this.verificationId = verificationId;
                        otpPhoneEditText.setVisibility(View.VISIBLE);
                        verifyPhoneOtpButton.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                        Toast.makeText(SPSignUpActivity.this, "OTP Sent!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void verifyPhoneOTP() {
        progressDialog.setMessage("Verifying OTP....");
        progressDialog.show();
        String otp = otpPhoneEditText.getText().toString().trim();

        if (TextUtils.isEmpty(otp)) {
            progressDialog.dismiss();
            Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show();
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
                progressDialog.dismiss();
            } else {
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                sendPhoneOtpButton.setEnabled(true);
                progressDialog.dismiss();
            }
        });
        
    }

    private void selectDocument(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Allows selecting any file type (PDF, Images, etc.)
        startActivityForResult(intent, requestCode);
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
            serviceProviderImage.setImageURI(imageUri);
        }

        if (resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                switch (requestCode) {
                    case 101: {
                        aadharUri = fileUri;
                        isAdharUpload = true;

                        if (aadharUri.toString().length() > 50) {
                            buttonUploadAadhar.setText(aadharUri.toString().substring(0, 50));
                        } else {
                            buttonUploadAadhar.setText(aadharUri.toString());
                        }

                        enableUpload();
                        Toast.makeText(this, "Aadhaar Card Selected", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 103: {
                        otherUri = fileUri;
                        isCertificateUpload = true;

                        if (otherUri.toString().length() > 50) {
                            buttonUploadOther.setText(otherUri.toString().substring(0, 50));
                        } else {
                            buttonUploadOther.setText(otherUri.toString());
                        }

                        enableUpload();
                        Toast.makeText(this, "Document Selected", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        }
    }


    private void uploadDocumentsAndSaveData() {

        if (aadharUri != null) {
            uploadDocumentToStorage("Aadhaar", aadharUri, documentUrls);
        }else{
            Toast.makeText(getApplicationContext(), "Please select Aadhaar", Toast.LENGTH_SHORT).show();
        }
        if (otherUri != null) {
            uploadDocumentToStorage( "Document", otherUri, documentUrls);
        }else{
            Toast.makeText(getApplicationContext(), "Please select Certificate", Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadDocumentToStorage(String docType, Uri fileUri, HashMap<String, String> documentUrls) {

        progressDialog.setMessage("Uploading "+docType+".....");
        progressDialog.show();

        StorageReference fileRef = storageReference.child("documents/" + docType + ".pdf");

        fileRef.putFile(fileUri).addOnSuccessListener(taskSnapshot ->
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    documentUrls.put(docType, uri.toString());
                    Toast.makeText(this, docType + " Uploaded!", Toast.LENGTH_SHORT).show();
                    isDocUpload=true;
                    progressDialog.dismiss();
                    enableSignUp();
                })
        ).addOnFailureListener(e ->{
            Toast.makeText(this, "Failed to Upload " + docType, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            isDocUpload=false;
        });
    }


    private void enableUpload() {
        if(isAdharUpload && isCertificateUpload){
            buttonSaveDoc.setEnabled(true);
        }
    }

    private void enableSignUp() {
        if (isPhoneVerified && isDocUpload) {
            btnSignUp.setEnabled(true);
        }
    }

    private void registerGuide() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEdittext.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        String languages = spinnerLanguages.getText().toString().trim();
        String specialization = spinnerSpecialization.getText().toString().trim();
        String experience = experienceEditText.getText().toString().trim();
        String fees = feesEditText.getText().toString().trim();
        String confirmPass=confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) ||
                TextUtils.isEmpty(password)||TextUtils.isEmpty(city) || TextUtils.isEmpty(experience) ||
                TextUtils.isEmpty(fees)||TextUtils.isEmpty(confirmPass) || TextUtils.isEmpty(languages) || TextUtils.isEmpty(specialization)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }else if (!password.equals(confirmPass)) {
            Toast.makeText(SPSignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        }else if (password.length() < 6) {
            Toast.makeText(SPSignUpActivity.this, "Password must contain 6 characters", Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.setMessage("Registering User.....");
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser guide = mAuth.getCurrentUser();
                            if (guide != null) {
                                uploadImageAndSaveData(guide.getUid(), name, email, phone, city, languages, specialization, experience, fees,password);
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SPSignUpActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void uploadImageAndSaveData(String guideId, String name, String email, String phone, String city, String languages, String specialization, String experience, String fees, String password) {
        if (imageUri != null) {
            StorageReference imageRef = storageReference.child(guideId + ".jpg");

            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        saveGuideToDatabase(guideId, name, email, phone, city, languages, specialization, experience, fees,password, uri.toString(),documentUrls);
                    })
            ).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(SPSignUpActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
            });
        } else {
            saveGuideToDatabase(guideId, name, email, phone, city, languages, specialization, experience, fees,password, null,documentUrls);
        }

    }

    private void saveGuideToDatabase(String guideId, String name, String email, String phone, String city, String languages, String specialization, String experience, String fees, String password, String profileImageUrl,HashMap<String,String> documentUrls) {

        boolean verified=false;
        String status="Available";

        HashMap<String, Object> serviceProviderData = new HashMap<>();
        serviceProviderData.put("name", name);
        serviceProviderData.put("email", email);
        serviceProviderData.put("phone", phone);
        serviceProviderData.put("city", city);
        serviceProviderData.put("languages", languages);
        serviceProviderData.put("specialization", specialization);
        serviceProviderData.put("experience", experience);
        serviceProviderData.put("fees", fees);
        serviceProviderData.put("password", password);
        serviceProviderData.put("verified", verified);
        serviceProviderData.put("status", status);
        serviceProviderData.put("profileImageUrl", profileImageUrl);
        serviceProviderData.put("documents", documentUrls);

        databaseReference.child(guideId).setValue(serviceProviderData).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(SPSignUpActivity.this, "Guide Registered Successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SPSignUpActivity.this, ServiceProviderLoginActivity.class)); // Redirect to dashboard
                finish();
            } else {
                Toast.makeText(SPSignUpActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}