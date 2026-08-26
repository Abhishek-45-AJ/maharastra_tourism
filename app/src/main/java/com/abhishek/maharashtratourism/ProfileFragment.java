package com.abhishek.maharashtratourism;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ProfileFragment extends Fragment implements suggestedDestinationCardAdapter.OnItemClickListner{

    private ImageView profileImageView;
    private TextView userName,userNumber,userEmail;
    private ImageButton selectImage;
    private DatabaseReference userRef;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private Uri imageUri;
    private String userId;
    private RecyclerView recyclerViewFavorites;
    private suggestedDestinationCardAdapter placeAdapter;
    private List<Place> favoritePlaces;
    private DatabaseReference userFavoritesRef, placesRef;

    public ProfileFragment(String userId) {
        this.userId=userId;
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        profileImageView=view.findViewById(R.id.profileImageView);
        userName=view.findViewById(R.id.userName);
        userEmail=view.findViewById(R.id.userEmail);
        userNumber=view.findViewById(R.id.userNumber);
        selectImage=view.findViewById(R.id.selectImage);
        recyclerViewFavorites=view.findViewById(R.id.recyclerViewFavorites);
        favoritePlaces = new ArrayList<>();

        userFavoritesRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites");
        placesRef = FirebaseDatabase.getInstance().getReference("places");


        userRef= FirebaseDatabase.getInstance().getReference("users").child(userId);
        storageReference= FirebaseStorage.getInstance().getReference("profile_images");

        placeAdapter = new suggestedDestinationCardAdapter(favoritePlaces, this,userId);
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        recyclerViewFavorites.setAdapter(placeAdapter);


        loadFavoritePlaces(this);

        if (userId == null) {
            Toast.makeText(getContext(), "User not logged in!", Toast.LENGTH_SHORT).show();
            return view;  // Exit if no user ID is found (you could redirect to login here)
        }

        loadUserProfile();

        selectImage.setOnClickListener(v->selectProfileImage());

        return view;
    }

    private void loadFavoritePlaces(ProfileFragment profileFragment) {

        userFavoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoritePlaces.clear();
                List<String> favoritePlaceNames = new ArrayList<>();

                // Fetch place names from user's favorites
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String placeName = ds.getKey();  // Since favorites are stored with place names
                    favoritePlaceNames.add(placeName);
                }

                // Fetch place details based on place names
                placesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot placeSnapshot) {
                        for (DataSnapshot ds : placeSnapshot.getChildren()) {
                            Place place = ds.getValue(Place.class);
                            if (place != null && favoritePlaceNames.contains(place.getName())) {
                                favoritePlaces.add(place);
                            }
                        }
                        placeAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadUserProfile(){
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String name=snapshot.child("name").getValue(String.class);
                    String mobile=snapshot.child("phone").getValue(String.class);
                    String email=snapshot.child("email").getValue(String.class);
                    String profileImageUrl=snapshot.child("profileImage").getValue(String.class);

                    userName.setText(name);
                    userEmail.setText(email);
                    userNumber.setText(mobile);
                    if(profileImageUrl != null && !profileImageUrl.isEmpty()){
                        Picasso.get().load(profileImageUrl)
                                .transform(new CropCircleTransformation())
                                .placeholder(R.drawable.baseline_person_24)
                                .into(profileImageView);
                    }else{
                        profileImageView.setImageResource(R.drawable.baseline_person_24);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Failed to load data",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectProfileImage(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,100);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==100 && resultCode==getActivity().RESULT_OK && data != null){
            imageUri=data.getData();
            profileImageView.setImageURI(imageUri);
            uploadProfileImage();
        }
    }

    private void uploadProfileImage(){
        if(imageUri == null){
            Toast.makeText(getContext(),"Please select image",Toast.LENGTH_SHORT).show();
            return;
        }
        String imageId= UUID.randomUUID().toString();
        StorageReference imageRef=storageReference.child(imageId);

        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri->{
            userRef.child("profileImage").setValue(uri.toString()).addOnCompleteListener(task->{
                if(task.isSuccessful()){
                    Toast.makeText(getContext(),"Profile image uploaded",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(),"Failed to upload image",Toast.LENGTH_SHORT).show();
                }
            });
        })).addOnFailureListener(e->Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onItemClick(Place place) {

        Intent intent = new Intent(getActivity(), PlaceDetailActivity.class);
        intent.putExtra("placeName",place.getName());
        intent.putExtra("placeCity",place.getCity());
        intent.putExtra("placeDescription",place.getDescription());
        intent.putExtra("placeEntryFee",place.getEntryFee());
        intent.putExtra("placeWebsite",place.getWebsite());
        intent.putExtra("placeContact",place.getContact());
        intent.putExtra("placeImage",place.getImageUrl());
        intent.putExtra("placeLatitude",place.getLatitude());
        intent.putExtra("placeLongitude",place.getLongitude());
        startActivity(intent);

    }
}