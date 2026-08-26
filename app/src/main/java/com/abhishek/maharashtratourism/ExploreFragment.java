package com.abhishek.maharashtratourism;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ExploreFragment extends Fragment {

    private TabLayout exploreTabs;
    private ViewPager2 exploreViewpager;
    private String userId;

    public ExploreFragment(String userId) {
        this.userId=userId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_explore, container, false);

        exploreTabs=view.findViewById(R.id.exploreTabs);
        exploreViewpager=view.findViewById(R.id.exploreViewpager);

        viewPagerExploreAdapter adapter=new viewPagerExploreAdapter(this,userId);
        exploreViewpager.setAdapter(adapter);

        new TabLayoutMediator(exploreTabs,exploreViewpager,(tab, position) -> {
            switch (position){
                case 0:
                    tab.setText("Hill Stations");
                    break;
                case 1:
                    tab.setText("Religious Places");
                    break;
                case 2:
                    tab.setText("Beaches");
                    break;
                case 3:
                    tab.setText("Historic Places");
                    break;
                case 4:
                    tab.setText("Forests & Parks");
            }
        }).attach();

        return view;
    }
}