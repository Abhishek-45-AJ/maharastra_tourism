package com.abhishek.maharashtratourism;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllHotelsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAllHotels;
    private HotelAdapter hotelAdapter;
    private List<Hotel> hotelList;
    private DatabaseReference hotelsRef;
    String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_hotels);

        recyclerViewAllHotels = findViewById(R.id.recyclerViewAllHotels);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);

        Intent intent = getIntent();
        if (intent != null) {
            city = intent.getStringExtra("city");
            tvHeaderTitle.setText("Stays in " + (city != null ? city : "Maharashtra"));
        }

        hotelList = new ArrayList<>();
        hotelAdapter = new HotelAdapter(this, hotelList);
        recyclerViewAllHotels.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAllHotels.setAdapter(hotelAdapter);

        hotelsRef = FirebaseDatabase.getInstance().getReference("hotels");
        loadAllHotels();

    }

    private void loadAllHotels() {
        Query query;
        if (city != null && !city.isEmpty()) {
            query = hotelsRef.orderByChild("city").startAt(city).endAt(city + "\uf8ff");
        } else {
            query = hotelsRef;
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hotelList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Hotel hotel = ds.getValue(Hotel.class);
                    if (hotel != null) {
                        hotel.setHotelId(ds.getKey());
                        hotelList.add(hotel);
                    }
                }
                hotelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}