package com.zestsed.mobile.adapter;

/**
 * Created by mdugah on 12/19/2016.
 */


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.zestsed.mobile.ui.ContributeFragment;
import com.zestsed.mobile.ui.InvestFragment;

public class ContribPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public ContribPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ContributeFragment contributeFragment = new ContributeFragment();
                return contributeFragment;
            case 1:
                InvestFragment investFragment  = new InvestFragment();
                return investFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}