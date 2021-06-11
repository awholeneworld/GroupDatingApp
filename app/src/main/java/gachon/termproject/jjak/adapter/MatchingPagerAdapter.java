package gachon.termproject.jjak.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

import gachon.termproject.jjak.fragment.MatchingTabProgressFragment;
import gachon.termproject.jjak.fragment.MatchingTabPeopleListFragment;
import gachon.termproject.jjak.fragment.MatchingTabMeetingFragment;
import gachon.termproject.jjak.fragment.MatchingTabTeammateFragment;

public class MatchingPagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    public static Fragment tab1;
    public static Fragment tab2;
    public static Fragment tab3;
    public static Fragment tab4;

    public MatchingPagerAdapter(@NonNull @NotNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.mNumOfTabs = behavior;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (tab1 == null) tab1 = new MatchingTabTeammateFragment();
                return tab1;

            case 1:
                if (tab2 == null)  tab2 = new MatchingTabMeetingFragment();
                return tab2;

            case 2:
                if (tab3 == null)  tab3 = new MatchingTabProgressFragment();
                return tab3;

            case 3:
                if (tab4 == null) tab4 = new MatchingTabPeopleListFragment();
                return tab4;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
