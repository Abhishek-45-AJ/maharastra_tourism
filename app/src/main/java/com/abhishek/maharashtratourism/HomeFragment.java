package com.abhishek.maharashtratourism;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements popularFestivalCardAdapter.OnItemClickListner, suggestedDestinationCardAdapter.OnItemClickListner, exploreMoreCardAdapter.OnItemClickListner {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private List<Integer> imageList;
    private Handler slideHandler;
    private RecyclerView recyclerviewSugested, recyclerviewFestival, recyclerviewExplore;
    private ImageButton suggestDestMoreBtn,popularFestivalMoreBtn,exploreMoreBtn;
    private suggestedDestinationCardAdapter suggestdestadapter;
    private popularFestivalCardAdapter popularFestAdapter;
    private exploreMoreCardAdapter exploreMoreAdapter;
    private List<Place> destinationList;
    private List<Event> festivalList;
    private List<exploreMoreCard> exploreList;
    private DatabaseReference databasePlaceReference,databaseEventReference;
    private String userId;


    public HomeFragment(String userId) {
        this.userId=userId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);


        viewPager=view.findViewById(R.id.viewPager);
        tabLayout=view.findViewById(R.id.tabLayout);
        suggestDestMoreBtn=view.findViewById(R.id.suggestDestMoreBtn);
        popularFestivalMoreBtn=view.findViewById(R.id.popularFestivalMoreBtn);
        exploreMoreBtn=view.findViewById(R.id.exploreMoreBtn);

        databasePlaceReference= FirebaseDatabase.getInstance().getReference("places");
        databaseEventReference= FirebaseDatabase.getInstance().getReference("events");

        imageList=new ArrayList<>();
        imageList.add(R.drawable.slider1);
        imageList.add(R.drawable.slider2);
        imageList.add(R.drawable.slider3);
        imageList.add(R.drawable.slider4);

        sliderImageAdapter adapter=new sliderImageAdapter(imageList);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout,viewPager,(tab, position)->{}).attach();

        slideHandler = new Handler(Looper.getMainLooper());
        Runnable sliderRunnable =new Runnable() {
            @Override
            public void run() {
                int nextPosition = (viewPager.getCurrentItem()+1)%imageList.size();

                viewPager.setCurrentItem(nextPosition, true);
                slideHandler.postDelayed(this,3000);
            }
        };
        slideHandler.postDelayed(sliderRunnable,3000);

        //suggested destination card view
        recyclerviewSugested = view.findViewById(R.id.sugest_destination_home);
        destinationList = new ArrayList<>();

        suggestdestadapter = new suggestedDestinationCardAdapter(destinationList,this,userId);
        recyclerviewSugested.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        recyclerviewSugested.setAdapter(suggestdestadapter);

        destinationList.clear();
        Query placeQuery=databasePlaceReference.orderByChild("city").startAt("Mumbai").endAt("Mumbai\uf8ff");
        placeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    Place place=ds.getValue(Place.class);
                    if(place != null && !destinationList.contains(place)){
                        destinationList.add(place);
                    }
                }
                suggestdestadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        suggestDestMoreBtn.setOnClickListener(v->{
            if(getActivity() instanceof MainActivity){
                ((MainActivity)getActivity()).loadFragment(new SearchFragment(userId));

                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav);
                bottomNavigationView.setSelectedItemId(R.id.search);
            }
        });

        //pupular festival card view
        recyclerviewFestival=view.findViewById(R.id.popular_festival_home);
        festivalList=new ArrayList<>();

        popularFestAdapter=new popularFestivalCardAdapter(festivalList,this);
        recyclerviewFestival.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        recyclerviewFestival.setAdapter(popularFestAdapter);

        festivalList.clear();
        Query eventQuery=databaseEventReference.orderByChild("status").startAt("Upcoming").endAt("Upcoming\uf8ff");
        eventQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    Event event=ds.getValue(Event.class);
                    if(event != null && !festivalList.contains(event)){
                        festivalList.add(event);
                    }
                }
                popularFestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        popularFestivalMoreBtn.setOnClickListener(v->{
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new EventFragment(userId));

                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav);
                bottomNavigationView.setSelectedItemId(R.id.event);
            }
        });

        //explore more card view
        recyclerviewExplore=view.findViewById(R.id.exploreMoreHome);
        exploreList=new ArrayList<>();
        exploreList.add(new exploreMoreCard("Events",R.drawable.event));
        exploreList.add(new exploreMoreCard("Historical Places",R.drawable.historic));
        exploreList.add(new exploreMoreCard("Hill Stations",R.drawable.hillstation));
        exploreList.add(new exploreMoreCard("Religious Places",R.drawable.riligious));
        exploreList.add(new exploreMoreCard("Beaches",R.drawable.beach));

        exploreMoreAdapter =new exploreMoreCardAdapter(exploreList,this);
        recyclerviewExplore.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        recyclerviewExplore.setAdapter(exploreMoreAdapter);

        exploreMoreBtn.setOnClickListener(v -> {
            // Using getActivity() to get the MainActivity and calling loadFragment
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new ExploreFragment(userId));

                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav);
                bottomNavigationView.setSelectedItemId(R.id.explore);
            }
        });

        return view;
    }

    //places near you on click event
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

    //upcoming events,festivals onclick listner
    @Override
    public void onItemClick(Event e) {
        Intent intent = new Intent(getActivity(), PlaceDetailActivity.class);
        intent.putExtra("placeName",e.getName());
        intent.putExtra("placeCity",e.getAddress());
        intent.putExtra("placeDescription",e.getDescription());
        intent.putExtra("placeEntryFee",e.getEntryFee());
        intent.putExtra("placeWebsite",e.getWebsite());
        intent.putExtra("placeContact",e.getContact());
        intent.putExtra("placeImage",e.getImageUrl());
        intent.putExtra("placeLatitude",e.getLatitude());
        intent.putExtra("placeLongitude",e.getLongitude());
        startActivity(intent);

    }

    //Explore more event listner
    @Override
    public void onItemClick(String n) {
        if(n.equals("Events")){
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new EventFragment(userId));

                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav);
                bottomNavigationView.setSelectedItemId(R.id.event);
            }
        }else{
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new ExploreFragment(userId));

                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav);
                bottomNavigationView.setSelectedItemId(R.id.explore);
            }
        }
    }
}