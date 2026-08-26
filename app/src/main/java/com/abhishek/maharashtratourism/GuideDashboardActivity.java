package com.abhishek.maharashtratourism;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class GuideDashboardActivity extends AppCompatActivity {

    ImageView guideProfileImage;
    TextView guideName,guideExperience,guideSpecialization,guideCity,guideVerifiedStatus;
    DatabaseReference guideRef;
    Button btnEditProfile,btnNotifications,btnEmailSupport,btnWhatsAppSupport;
    Switch switchAvailability;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_dashboard);

        guideProfileImage=findViewById(R.id.guideProfileImage);
        guideName=findViewById(R.id.guideName);
        guideExperience=findViewById(R.id.guideExperience);
        guideSpecialization=findViewById(R.id.guideSpecialization);
        guideCity=findViewById(R.id.guideCity);
        guideVerifiedStatus=findViewById(R.id.guideVerifiedStatus);
        btnEditProfile=findViewById(R.id.btnEditProfile);
        btnNotifications=findViewById(R.id.btnNotifications);
        btnWhatsAppSupport=findViewById(R.id.btnWhatsAppSupport);
        btnEmailSupport=findViewById(R.id.btnEmailSupport);
        switchAvailability=findViewById(R.id.switchAvailability);

        SharedPreferences sharedPreferences=getSharedPreferences("MyAppPrefs",MODE_PRIVATE);
        String userId=sharedPreferences.getString("userId",null);

        if (userId != null) {
            guideRef= FirebaseDatabase.getInstance().getReference("guides").child(userId);

            guideRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint({"SetTextI18n", "ResourceAsColor"})
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String name=snapshot.child("name").getValue(String.class);
                        String experience=snapshot.child("experience").getValue(String.class);
                        String specialization=snapshot.child("specialization").getValue(String.class);
                        String city=snapshot.child("city").getValue(String.class);
                        Boolean verified=snapshot.child("verified").getValue(Boolean.class);
                        String imageUrl=snapshot.child("profileImageUrl").getValue(String.class);
                        String status=snapshot.child("status").getValue(String.class);

                        guideName.setText(name);
                        guideExperience.setText("Experience: "+experience+" years");
                        guideSpecialization.setText("Specialization: "+specialization);
                        guideCity.setText("City:"+city);
                        if(Boolean.TRUE.equals(verified)){
                            guideVerifiedStatus.setTextColor(R.color.green);
                            guideVerifiedStatus.setText("✅ Verified");
                        }else {
                            guideVerifiedStatus.setTextColor(R.color.holo_red_dark);
                            guideVerifiedStatus.setText("✅ Not Verified");
                        }

                        if(status.equals("Available")){
                            switchAvailability.setChecked(true);
                        }else{
                            switchAvailability.setChecked(false);
                        }

                        if(imageUrl!=null && !imageUrl.isEmpty()){
                            Picasso.get().load(imageUrl)
                                    .transform(new CropCircleTransformation())
                                    .placeholder(R.drawable.baseline_person_24)
                                    .into(guideProfileImage);
                        }else{
                            guideProfileImage.setImageResource(R.drawable.baseline_person_24);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            Toast.makeText(this, "Guide detail couldn't load!", Toast.LENGTH_SHORT).show();
            return;
        }

        switchAvailability.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                guideRef.child("status").setValue("Available");
                Toast.makeText(GuideDashboardActivity.this, "You are now Available", Toast.LENGTH_SHORT).show();
            } else {
                guideRef.child("status").setValue("Unavailable");
            }
        });

        btnEditProfile.setOnClickListener(v -> startActivity(new Intent(GuideDashboardActivity.this, GuideEditProfileActivity.class)));
        btnNotifications.setOnClickListener(v -> startActivity(new Intent(GuideDashboardActivity.this, GuideNotificationActivity.class)));

        // WhatsApp Support Button Click
        btnWhatsAppSupport.setOnClickListener(v -> openWhatsAppChat());

        // Email Support Button Click
        btnEmailSupport.setOnClickListener(v -> openEmailSupport());

    }

    private void openEmailSupport() {
        String adminEmail="jadhabhishek@gmail.com";
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // Only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{adminEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Support Request - Maharashtra Tourism");
        intent.putExtra(Intent.EXTRA_TEXT, "Hello Admin,\n\nI need help regarding my guide account.");
        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    private void openWhatsAppChat() {
        String adminPhoneNumber="9421045470";
        try {
            String message = "Hello Admin, I need support regarding Maharashtra Tourism App.";
            String url = "https://api.whatsapp.com/send?phone=" + adminPhoneNumber + "&text=" + Uri.encode(message);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.setPackage("com.whatsapp"); // Ensure it opens in WhatsApp
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + adminPhoneNumber));
            startActivity(intent);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.logout){
            showLogoutConfirmation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutConfirmation(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("Yes",((dialog, which) -> logoutUser()));
        builder.setNegativeButton("No",((dialog, which) -> dialog.dismiss()));
        builder.create().show();
    }

    public void logoutUser(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(GuideDashboardActivity.this,"Logout successfully",Toast.LENGTH_SHORT).show();

        Intent intent=new Intent(GuideDashboardActivity.this,TouristLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}