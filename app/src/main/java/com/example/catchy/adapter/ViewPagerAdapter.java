package com.example.catchy.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.catchy.fragments.Add;
import com.example.catchy.fragments.Home;
import com.example.catchy.fragments.Notification;
import com.example.catchy.fragments.Profile;
import com.example.catchy.fragments.Search;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    int noOfTabs;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int noOfTabs) {
        super(fm);
        this.noOfTabs = noOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
        return new Home();

            case 1:
               return new Search();

            case 2:
                return new Add();

            case 3:
                return new Notification();

            case 4:
                return new Profile();

            default:
                return null;


        }


    }

    @Override
    public int getCount() {
        return noOfTabs;
    }
}
