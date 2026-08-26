package com.abhishek.maharashtratourism;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements searchPlaceAdapter.OnItemClickListner{

    private AutoCompleteTextView searchPlaces;
    private RecyclerView searchRecyclerView;
    private List<Place> placeList;
    private List<String> suggetionList;
    private searchPlaceAdapter adapter;
    private ArrayAdapter<String> suggetionAdapter;
    private DatabaseReference placesRef;
    String userId;


    public SearchFragment(String userId) {
        this.userId=userId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_search, container, false);

        searchPlaces=view.findViewById(R.id.searchPlaces);
        searchRecyclerView=view.findViewById(R.id.searchRecyclerView);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        placeList=new ArrayList<>();
        suggetionList=new ArrayList<>();
        adapter=new searchPlaceAdapter(placeList, this);
        searchRecyclerView.setAdapter(adapter);

        placesRef= FirebaseDatabase.getInstance().getReference("places");

        loadSuggestions();

        searchPlaces.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void afterTextChanged(Editable s) {
                String searchText=s.toString().trim();
                if(!searchText.isEmpty()){
                    performSearch(searchText);
                }else{
                    placeList.clear();
                }
                adapter.notifyDataSetChanged();
            }
        });


        return view;
    }

    private void loadSuggestions(){
        placesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                suggetionList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    String placeName=ds.child("name").getValue(String.class);
                    String city=ds.child("city").getValue(String.class);
                    if(placeName != null && !suggetionList.contains(placeName)){
                        suggetionList.add(placeName);
                    }
                    if(city != null && !suggetionList.contains(city)){
                        suggetionList.add(city);
                    }
                }
                suggetionAdapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_dropdown_item_1line,suggetionList);
                searchPlaces.setAdapter(suggetionAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Failed to load suggestions!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSearch(String searchText){
        placeList.clear();
        Query nameQuery=placesRef.orderByChild("name").startAt(searchText).endAt(searchText+"\uf8ff");
        nameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for( DataSnapshot ds: snapshot.getChildren()){
                    Place place=ds.getValue(Place.class);
                    if(place != null && !placeList.contains(place)){
                        placeList.add(place);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Search failed!", Toast.LENGTH_SHORT).show();
            }
        });

        Query cityQuery=placesRef.orderByChild("city").startAt(searchText).endAt(searchText+"\uf8ff");
        cityQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    Place place=ds.getValue(Place.class);
                    if(place != null && !placeList.contains(place)){
                        placeList.add(place);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Search failed!", Toast.LENGTH_SHORT).show();
            }
        });
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