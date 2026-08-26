package com.abhishek.maharashtratourism;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class GuideEditProfileActivity extends AppCompatActivity {

    private EditText updatedGuideName,updatedGuidePhone,updatedGuideEmail,updatedGuideFees,updatedGuideCity,updatedGuideLanguages,updatedGuideSpecialization,updatedGuideExperience;
    private Button selectImageBtn,updateGuideBtn;
    private ImageView guideImage;
    private DatabaseReference guideRef;
    private StorageReference guideStorageRef;
    private Uri imageUri=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_edit_profile);

        updatedGuideName=findViewById(R.id.updatedGuideName);
        updatedGuidePhone=findViewById(R.id.updatedGuidePhone);
        updatedGuideEmail=findViewById(R.id.updatedGuideEmail);
        updatedGuideFees=findViewById(R.id.updatedGuideFees);
        updatedGuideCity=findViewById(R.id.updatedGuideCity);
        updatedGuideLanguages=findViewById(R.id.updatedGuideLanguages);
        updatedGuideSpecialization=findViewById(R.id.updatedGuideSpecialization);
        updatedGuideExperience=findViewById(R.id.updatedGuideExperience);
        guideImage=findViewById(R.id.guideImage);
        selectImageBtn=findViewById(R.id.selectImageBtn);
        updateGuideBtn=findViewById(R.id.updateGuideBtn);

        SharedPreferences sharedPreferences=getSharedPreferences("MyAppPrefs",MODE_PRIVATE);
        String userId=sharedPreferences.getString("userId",null);


        guideRef=FirebaseDatabase.getInstance().getReference("guides");
        guideStorageRef=FirebaseStorage.getInstance().getReference("guide_profiles");

        loadGuideInfo(userId);

        selectImageBtn.setOnClickListener(v -> selectImage());
        updateGuideBtn.setOnClickListener(v -> updateGuideInDatabase(userId));

    }

    private void updateGuideInDatabase(String userId) {

        String newEmail = updatedGuideEmail.getText().toString().trim();

        guideRef.child(userId).child("password").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String pass = snapshot.getValue(String.class);
                    if (pass != null) {
                        // Proceed with re-authentication
                        reauthenticateAndUpdateEmail(userId, pass, newEmail);
                    } else {
                        Toast.makeText(GuideEditProfileActivity.this, "Password not found!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void reauthenticateAndUpdateEmail(String userId, String pass, String newEmail) {
        // Get current Firebase user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //Re-authenticate user (Needed before email update)
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), pass); // You must ask user to re-enter their password
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Step 2: Update Email in Firebase Authentication
                user.updateEmail(newEmail).addOnCompleteListener(emailUpdateTask -> {
                    if (emailUpdateTask.isSuccessful()) {
                        if(imageUri != null){
                            StorageReference imageRef=guideStorageRef.child(userId+".jpg");
                            imageRef.putFile(imageUri)
                                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                                            .addOnSuccessListener(uri -> updateGuideDetails(uri.toString(),userId))
                                    ).addOnFailureListener(e -> {
                                        Toast.makeText(GuideEditProfileActivity.this,"Image upload Failed",Toast.LENGTH_SHORT).show();
                                    });
                        }else{
                            updateGuideDetails(null,userId);
                        }

                    } else {
                        Toast.makeText(GuideEditProfileActivity.this, "Email update failed: " + emailUpdateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(GuideEditProfileActivity.this, "Re-authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateGuideDetails(String newImageUrl,String userId) {

        HashMap<String, Object> updateMap=new HashMap<>();

        updateMap.put("name",updatedGuideName.getText().toString().trim());
        updateMap.put("phone",updatedGuidePhone.getText().toString().trim());
        updateMap.put("email",updatedGuideEmail.getText().toString().trim());
        updateMap.put("fees",updatedGuideFees.getText().toString().trim());
        updateMap.put("city",updatedGuideCity.getText().toString().trim());
        updateMap.put("languages",updatedGuideLanguages.getText().toString().trim());
        updateMap.put("specialization",updatedGuideSpecialization.getText().toString().trim());
        updateMap.put("experience",updatedGuideExperience.getText().toString().trim());

        if(newImageUrl != null){
            updateMap.put("profileImageUrl",newImageUrl);
        }

        guideRef.child(userId).updateChildren(updateMap)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(GuideEditProfileActivity.this,"Guide Details Updated!",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(GuideEditProfileActivity.this,GuideDashboardActivity.class));
                        finishAffinity();
                    }else{
                        Toast.makeText(GuideEditProfileActivity.this,"Failed to Updated Details!",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void selectImage() {

        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,100);
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && resultCode==RESULT_OK && data!=null){
            imageUri=data.getData();
            guideImage.setImageURI(imageUri);
        }else{
            Toast.makeText(GuideEditProfileActivity.this,"Failed to load image",Toast.LENGTH_SHORT).show();
        }
    }

    private void loadGuideInfo(String userId) {

        guideRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String name=snapshot.child("name").getValue(String.class);
                    String mobile=snapshot.child("phone").getValue(String.class);
                    String email=snapshot.child("email").getValue(String.class);
                    String fees=snapshot.child("fees").getValue(String.class);
                    String experience=snapshot.child("experience").getValue(String.class);
                    String languages=snapshot.child("languages").getValue(String.class);
                    String specialization=snapshot.child("specialization").getValue(String.class);
                    String city=snapshot.child("city").getValue(String.class);
                    String imageUrl=snapshot.child("profileImageUrl").getValue(String.class);


                    updatedGuideName.setText(name);
                    updatedGuidePhone.setText(mobile);
                    updatedGuideEmail.setText(email);
                    updatedGuideFees.setText(fees);
                    updatedGuideCity.setText(city);
                    updatedGuideLanguages.setText(languages);
                    updatedGuideSpecialization.setText(specialization);
                    updatedGuideExperience.setText(experience);

                    if(imageUrl!=null && !imageUrl.isEmpty()){
                        Picasso.get().load(imageUrl).into(guideImage);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GuideEditProfileActivity.this,"Failed to load details",Toast.LENGTH_SHORT).show();
            }
        });

    }
}