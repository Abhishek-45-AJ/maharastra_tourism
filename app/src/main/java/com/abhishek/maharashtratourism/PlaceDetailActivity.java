package com.abhishek.maharashtratourism;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PlaceDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageView placeImage;
    private TextView placeName,placeCity,placeEntryFee,placeDescription,placeWebsite,placeContact;
    String name,city,entryFee,description,website,contact,image,lat,longi;
    private double latitude,longitude;
    private ImageButton likeUnlikePlace;
    private DatabaseReference favoritesRef,guideRef;
    private RecyclerView guideInfoRecyclerView;
    private List<Guide> guideList;
    private serviceProviderInfoCardAdapter guideAdapter;
    private Button btnGetDirections;
    String hotelId;
    private boolean isHotel = false;
    private DatabaseReference hotelsRef, reviewsRef;
    private RecyclerView recyclerViewHotelsHorizontal, recyclerViewReviews;
    private List<Hotel> hotelList;
    private List<Review> reviewList;
    private HotelAdapter hotelAdapter;
    private ReviewAdapter reviewAdapter;
    private Button btnSeeAllHotels, btnAddReview;

    // Multiple photo picker for reviews
    private List<Uri> selectedPhotoUris = new ArrayList<>();
    private TextView tvSelectedPhotosCount;
    private ActivityResultLauncher<String> multiPhotoLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        placeName=findViewById(R.id.placeName);
        placeCity=findViewById(R.id.placeCity);
        placeEntryFee=findViewById(R.id.placeEntryFee);
        placeDescription=findViewById(R.id.placeDescription);
        placeWebsite=findViewById(R.id.placeWebsite);
        placeContact=findViewById(R.id.placeContact);
        placeImage=findViewById(R.id.placeImage);
        likeUnlikePlace=findViewById(R.id.likeUnlikePlace);
        guideInfoRecyclerView=findViewById(R.id.guideInfoRecyclerView);
        btnGetDirections=findViewById(R.id.btnGetDirections);

        recyclerViewHotelsHorizontal = findViewById(R.id.recyclerViewHotelsHorizontal);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        btnSeeAllHotels = findViewById(R.id.btnSeeAllHotels);
        btnAddReview = findViewById(R.id.btnAddReview);

        SharedPreferences sharedPreferences=getSharedPreferences("MyAppPrefs",MODE_PRIVATE);
        String userId=sharedPreferences.getString("userId",null);
        String userName = sharedPreferences.getString("userName", "Traveler");

        Intent intent=getIntent();
        if(intent != null){
            isHotel = intent.getBooleanExtra("isHotel", false);
            hotelId = intent.getStringExtra("hotelId");
            name=intent.getStringExtra("placeName");
            city=intent.getStringExtra("placeCity");
            description=intent.getStringExtra("placeDescription");
            entryFee=intent.getStringExtra("placeEntryFee");
            website=intent.getStringExtra("placeWebsite");
            contact=intent.getStringExtra("placeContact");
            image=intent.getStringExtra("placeImage");
            lat=intent.getStringExtra("placeLatitude");
            longi=intent.getStringExtra("placeLongitude");
            try {
                latitude = lat != null ? Double.parseDouble(lat) : 19.7515;
                longitude = longi != null ? Double.parseDouble(longi) : 75.7139;
            } catch (Exception e) {
                latitude = 19.7515;
                longitude = 75.7139;
            }

            placeName.setText(name);
            placeCity.setText(city);
            placeDescription.setText(description);
            placeEntryFee.setText(entryFee);
            placeWebsite.setText(website);
            placeContact.setText(contact);

            if (isHotel) {
                placeEntryFee.setText("Price: " + entryFee);
                btnAddReview.setVisibility(View.VISIBLE); // Show review button only for hotels/stays
            } else {
                placeEntryFee.setText("Entry Fee: " + entryFee);
                btnAddReview.setVisibility(View.GONE);
            }

            Picasso.get().load(image).placeholder(R.drawable.logo_image).into(placeImage);
        }

        assert userId != null;
        favoritesRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites");
        guideRef=FirebaseDatabase.getInstance().getReference("guides");

        hotelsRef = FirebaseDatabase.getInstance().getReference("hotels");
        reviewsRef = FirebaseDatabase.getInstance().getReference("hotel_reviews");

        favoritesRef.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    likeUnlikePlace.setImageResource(R.drawable.baseline_favorite);
                    likeUnlikePlace.setTag("liked");
                } else {
                    likeUnlikePlace.setImageResource(R.drawable.baseline_favorite_border);
                    likeUnlikePlace.setTag("unliked");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        likeUnlikePlace.setOnClickListener(v -> {
            if ("unliked".equals(likeUnlikePlace.getTag())) {
                // Add to favorites
                favoritesRef.child(name).setValue(true);
                likeUnlikePlace.setImageResource(R.drawable.baseline_favorite);
                likeUnlikePlace.setTag("liked");
            } else {
                // Remove from favorites
                favoritesRef.child(name).removeValue();
                likeUnlikePlace.setImageResource(R.drawable.baseline_favorite_border);
                likeUnlikePlace.setTag("unliked");
            }
        });


        placeWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(placeWebsite != null && !placeWebsite.getText().toString().isEmpty()){
                    Intent browserIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(placeWebsite.getText().toString()));
                    startActivity(browserIntent);
                }
            }
        });

        //Moving to navigation activity
        btnGetDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent navIntent=new Intent(PlaceDetailActivity.this, NavigationActivity.class);
                navIntent.putExtra("destName", name);
                navIntent.putExtra("destLat", latitude);
                navIntent.putExtra("destLng", longitude);
                startActivity(navIntent);
            }
        });

        SupportMapFragment mapFragment=new SupportMapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mapContainer,mapFragment).commit();
        mapFragment.getMapAsync(this);

        guideList=new ArrayList<>();
        guideAdapter=new serviceProviderInfoCardAdapter(guideList,userId);
        guideInfoRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        guideInfoRecyclerView.setAdapter(guideAdapter);
        loadGuides();

        
        btnSeeAllHotels.setOnClickListener(v -> {
            Intent allHotelsIntent = new Intent(PlaceDetailActivity.this, AllHotelsActivity.class);
            allHotelsIntent.putExtra("city", city);
            startActivity(allHotelsIntent);
        });
        
        // Setup Horizontal Hotels RecyclerView
        hotelList = new ArrayList<>();
        hotelAdapter = new HotelAdapter(this, hotelList);
        recyclerViewHotelsHorizontal.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewHotelsHorizontal.setAdapter(hotelAdapter);
        loadHorizontalHotels();

        // Setup Reviews RecyclerView & Button
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(this, reviewList);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setAdapter(reviewAdapter);

        if (isHotel && hotelId != null) {
            loadHotelReviews();
        }

        // Initialize Multiple Photo Picker Result Launcher
        multiPhotoLauncher = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), uris -> {
            if (uris != null && !uris.isEmpty()) {
                selectedPhotoUris.clear();
                selectedPhotoUris.addAll(uris);
                if (tvSelectedPhotosCount != null) {
                    tvSelectedPhotosCount.setText(selectedPhotoUris.size() + " photos selected");
                }
            }
        });

        btnAddReview.setOnClickListener(v -> showAddReviewDialog(userId, userName));
        

    }

    private void showAddReviewDialog(String userId, String userName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_review, null);
        builder.setView(dialogView);

        RatingBar ratingBarInput = dialogView.findViewById(R.id.ratingBarInput);
        EditText etReviewComment = dialogView.findViewById(R.id.etReviewComment);
        Button btnSelectPhotos = dialogView.findViewById(R.id.btnSelectPhotos);
        tvSelectedPhotosCount = dialogView.findViewById(R.id.tvSelectedPhotosCount);
        Button btnSubmitReview = dialogView.findViewById(R.id.btnSubmitReview);

        selectedPhotoUris.clear();
        tvSelectedPhotosCount.setText("0 photos selected");

        AlertDialog dialog = builder.create();

        btnSelectPhotos.setOnClickListener(v -> multiPhotoLauncher.launch("image/*"));

        btnSubmitReview.setOnClickListener(v -> {
            float rating = ratingBarInput.getRating();
            String comment = etReviewComment.getText().toString().trim();

            if (comment.isEmpty()) {
                etReviewComment.setError("Please write a comment");
                return;
            }

            btnSubmitReview.setEnabled(false);
            btnSubmitReview.setText("Uploading...");

            uploadReviewWithPhotos(userId, userName, rating, comment, dialog);
        });

        dialog.show();
    }

    private void uploadReviewWithPhotos(String userId, String userName, float rating, String comment, AlertDialog dialog) {
        String reviewId = reviewsRef.child(hotelId).push().getKey();
        if (reviewId == null) return;

        List<String> uploadedPhotoUrls = new ArrayList<>();
        if (selectedPhotoUris.isEmpty()) {
            saveReviewToDatabase(reviewId, userId, userName, rating, comment, uploadedPhotoUrls, dialog);
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("hotel_review_photos").child(hotelId).child(reviewId);

        final int totalPhotos = selectedPhotoUris.size();
        final int[] uploadedCount = {0};

        for (int i = 0; i < totalPhotos; i++) {
            Uri imageUri = selectedPhotoUris.get(i);
            StorageReference fileRef = storageRef.child("photo_" + i + ".jpg");

            int finalI = i;
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                uploadedPhotoUrls.add(uri.toString());
                uploadedCount[0]++;

                if (uploadedCount[0] == totalPhotos) {
                    saveReviewToDatabase(reviewId, userId, userName, rating, comment, uploadedPhotoUrls, dialog);
                }
            })).addOnFailureListener(e -> {
                uploadedCount[0]++;
                if (uploadedCount[0] == totalPhotos && !uploadedPhotoUrls.isEmpty()) {
                    saveReviewToDatabase(reviewId, userId, userName, rating, comment, uploadedPhotoUrls, dialog);
                } else if (uploadedCount[0] == totalPhotos) {
                    Toast.makeText(PlaceDetailActivity.this, "Failed to upload photos", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        }
    }

    private void saveReviewToDatabase(String reviewId, String userId, String userName, float rating, String comment, List<String> photoUrls, AlertDialog dialog) {
        Review review = new Review();
        review.setReviewId(reviewId);
        review.setUserId(userId);
        review.setUserName(userName);
        review.setRating(rating);
        review.setComment(comment);
        review.setPhotoUrls(photoUrls);
        review.setTimestamp(System.currentTimeMillis());

        reviewsRef.child(hotelId).child(reviewId).setValue(review).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updateHotelAverageRating();
                Toast.makeText(PlaceDetailActivity.this, "Review posted successfully!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(PlaceDetailActivity.this, "Failed to post review", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void updateHotelAverageRating() {
        reviewsRef.child(hotelId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalRating = 0;
                int count = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Review rev = ds.getValue(Review.class);
                    if (rev != null) {
                        totalRating += rev.getRating();
                        count++;
                    }
                }
                if (count > 0) {
                    double avg = totalRating / count;
                    hotelsRef.child(hotelId).child("averageRating").setValue(avg);
                    hotelsRef.child(hotelId).child("totalReviews").setValue(count);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadHotelReviews() {
        reviewsRef.child(hotelId).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Review review = ds.getValue(Review.class);
                    if (review != null) {
                        reviewList.add(review);
                    }
                }
                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadHorizontalHotels() {
        hotelList.clear();
        Query hotelQuery = hotelsRef.orderByChild("city").startAt(city).endAt(city + "\uf8ff");
        hotelQuery.limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Hotel hotel = ds.getValue(Hotel.class);
                    if (hotel != null) {
                        hotel.setHotelId(ds.getKey());
                        // If viewing this specific hotel, optionally skip or include it
                        hotelList.add(hotel);
                    }
                }
                hotelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadGuides() {
        guideList.clear();
        Query guideQuery=guideRef.orderByChild("city").startAt(city).endAt(city+"\uf8ff");
        guideQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    Guide guide=ds.getValue(Guide.class);
                    if(guide != null && !guideList.contains(guide)){
                        if (guide.isVerified() && guide.getStatus().equals("Available")) {
                            guide.setId(ds.getKey());
                            guideList.add(guide);
                        }
                    }
                }
                guideAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap=googleMap;
        LatLng placeLocation=new LatLng(latitude,longitude);
        mMap.addMarker(new MarkerOptions().position(placeLocation).title(name));

        if(latitude==19.7515){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLocation,5));
        }else{
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLocation,15));
        }

    }
}