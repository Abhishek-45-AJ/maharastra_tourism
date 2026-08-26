package com.abhishek.maharashtratourism;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExploreHistoricPlaces extends Fragment implements exploreCardAdapter.OnItemClickListner{

    private List<Place> explorelist;
    private RecyclerView exploreHistoricPlacesRecyclerView;
    private exploreCardAdapter adapter;
    private DatabaseReference databaseReference;
    private String userId;

    public ExploreHistoricPlaces(String userId) {
        this.userId=userId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.explore_historic_places_fragment, container, false);

        exploreHistoricPlacesRecyclerView=view.findViewById(R.id.exploreHistoricPlacesRecyclerView);

        explorelist =new ArrayList<>();

        adapter=new exploreCardAdapter(explorelist,this);
        exploreHistoricPlacesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        exploreHistoricPlacesRecyclerView.setAdapter(adapter);

        databaseReference= FirebaseDatabase.getInstance().getReference("places");
        explorelist.clear();

        Query query=databaseReference.orderByChild("category").startAt("Historical place").endAt("Historical place\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    Place place=ds.getValue(Place.class);
                    if(place != null && !explorelist.contains(place)){
                        explorelist.add(place);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Database error!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
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