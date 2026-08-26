package com.abhishek.maharashtratourism;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class viewPagerEventAdapter extends FragmentStateAdapter {

    public viewPagerEventAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new EventUpcoming();
            case 1:
                return new EventPresent();
            case 2:
                return new EventPast();
            default:
                return new EventUpcoming();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
