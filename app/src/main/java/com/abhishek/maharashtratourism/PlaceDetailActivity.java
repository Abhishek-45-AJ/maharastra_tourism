package com.abhishek.maharashtratourism;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

        SharedPreferences sharedPreferences=getSharedPreferences("MyAppPrefs",MODE_PRIVATE);
        String userId=sharedPreferences.getString("userId",null);

        Intent intent=getIntent();
        if(intent != null){
            name=intent.getStringExtra("placeName");
            city=intent.getStringExtra("placeCity");
            description=intent.getStringExtra("placeDescription");
            entryFee=intent.getStringExtra("placeEntryFee");
            website=intent.getStringExtra("placeWebsite");
            contact=intent.getStringExtra("placeContact");
            image=intent.getStringExtra("placeImage");
            lat=intent.getStringExtra("placeLatitude");
            latitude=Double.parseDouble(lat);
            longi=intent.getStringExtra("placeLongitude");
            longitude=Double.parseDouble(longi);

            placeName.setText(name);
            placeCity.setText(city);
            placeDescription.setText(description);
            placeEntryFee.setText(entryFee);
            placeWebsite.setText(website);
            placeContact.setText(contact);

            Picasso.get().load(image).placeholder(R.drawable.logo_image).into(placeImage);
        }

        assert userId != null;
        favoritesRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites");
        guideRef=FirebaseDatabase.getInstance().getReference("guides");

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

        SupportMapFragment mapFragment=new SupportMapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mapContainer,mapFragment).commit();
        mapFragment.getMapAsync(this);

        guideList=new ArrayList<>();
        guideAdapter=new serviceProviderInfoCardAdapter(guideList,userId);
        guideInfoRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        guideInfoRecyclerView.setAdapter(guideAdapter);

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