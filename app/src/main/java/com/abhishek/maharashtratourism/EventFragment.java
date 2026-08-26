package com.abhishek.maharashtratourism;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class EventFragment extends Fragment {

    private TabLayout eventTabs;
    private ViewPager2 eventViewpager;
    private String userId;

    public EventFragment(String userId) {
        this.userId=userId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_event, container, false);

        eventTabs=view.findViewById(R.id.eventTabs);
        eventViewpager=view.findViewById(R.id.eventViewpager);

        viewPagerEventAdapter adapter=new viewPagerEventAdapter(this);
        eventViewpager.setAdapter(adapter);

        new TabLayoutMediator(eventTabs,eventViewpager,(tab, position) -> {
            switch (position){
                case 0:
                    tab.setText("Upcoming Events");
                    break;
                case 1:
                    tab.setText("Ongoing Event");
                    break;
                case 2:
                    tab.setText("Completed");
                    break;
            }
        }).attach();


        return view;
    }
}