package com.La_nota.ALLA.Adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.La_nota.ALLA.Activities.MainActivity;
import com.La_nota.ALLA.Models.VPfragment;


public class VPadapter extends FragmentStateAdapter {
    private static MainActivity activity;
    public static final int preFilledPages = 14;
    public static final int defaultpage = 7;

    public VPadapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        activity = (MainActivity) fragmentActivity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int page) {
        return new VPfragment(page);
    }

    @Override
    public int getItemCount() {
        return preFilledPages;
    }

    public static MainActivity getActivity() {
        return activity;
    }
}