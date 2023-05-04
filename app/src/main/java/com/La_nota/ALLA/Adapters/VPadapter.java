package com.La_nota.ALLA.Adapters;



import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.La_nota.ALLA.Activities.MainActivity;
import com.La_nota.ALLA.Models.VPfragment;


public class VPadapter extends FragmentStateAdapter {
    private static MainActivity activity;

    public final VPfragment[] fragments = new VPfragment[preFilledPages];
    public static final int preFilledPages = 14;
    public static final int defaultpage = 7;

    public VPadapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        activity = (MainActivity) fragmentActivity;
        initFragments();
    }

    @NonNull
    @Override
    public Fragment createFragment(int page) {
        return fragments[page];
    }

    @Override
    public int getItemCount() {
        return preFilledPages;
    }

    public static MainActivity getActivity() {
        return activity;
    }

    public void initFragments(){
        for (int i = 0; i < preFilledPages; i++) {
            VPfragment fr = new VPfragment(i, this);
            fragments[i] = fr;
        }
    }

    public void refreshSHRVs() {
        for (int i = 0; i < preFilledPages; i++) {
            fragments[i].refreshSHRV();
        }
    }
}