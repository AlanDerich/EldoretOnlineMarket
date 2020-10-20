package com.rayson.eldoretonlinemarket;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class ProductPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    public ProductPagerAdapter(@NonNull FragmentManager fm, int behavior,ArrayList<Fragment> fragments) {
        super(fm, behavior);
        mFragments=fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
