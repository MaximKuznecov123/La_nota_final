package com.example.myapplication.Adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.Activities.MainActivity;
import com.example.myapplication.Models.VPfragment;


public class VPadapter extends FragmentStateAdapter {
    private final MainActivity activity;
    public static final int preFilledPages = 14;
    public static final int defaultpage = 7;

    public VPadapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.activity = (MainActivity) fragmentActivity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int page) {
        return new VPfragment(page, activity);
    }

    @Override
    public int getItemCount() {
        return preFilledPages;
    }

//Пусть пока лежит - пригодится)))
//public static class PageListener extends ViewPager2.OnPageChangeCallback {
//        @Override
//        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//
//            if (position == 1 ) {
//                pagesAmount++;
//                Log.d("MYLOG", pagesAmount + "");
//            }else if (positionOffset == 0 & position == 0){
//                pagesAmount--;
//                Log.d("MYLOG", pagesAmount + "");
//            }
//        }
//    }

}