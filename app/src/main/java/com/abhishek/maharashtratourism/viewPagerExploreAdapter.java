package com.abhishek.maharashtratourism;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class viewPagerExploreAdapter extends FragmentStateAdapter {
    String userId;
    public viewPagerExploreAdapter(Fragment fragment,String userId) {

        super(fragment);
        this.userId=userId;
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new ExploreHillstation(userId);
            case 1:
                return new ExploreReligion(userId);
            case 2:
                return new ExploreBeaches(userId);
            case 3:
                return new ExploreHistoricPlaces(userId);
            case 4:
                return new ExploreForest(userId);
            default:
                return new ExploreHillstation(userId);
        }
    }
}
