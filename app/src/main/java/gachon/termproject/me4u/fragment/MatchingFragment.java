package gachon.termproject.me4u.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import gachon.termproject.me4u.R;
import gachon.termproject.me4u.activity.MatchingSearchActivity;
import gachon.termproject.me4u.activity.SearchPeopleActivity;
import gachon.termproject.me4u.adapter.MatchingPagerAdapter;

public class MatchingFragment extends Fragment {
    private TabLayout tabs;
    private View view;
    private int status;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_matching, container, false);

        // 액션바 메뉴
        setHasOptionsMenu(true);

        tabs = view.findViewById(R.id.tabs);

        tabs.addTab(tabs.newTab().setText("팀원 구해요")); // 본인이 정한 수 채워지면
        tabs.addTab(tabs.newTab().setText("이성 게시물")); // 이성의 게시물 게시판에 뜸
        tabs.addTab(tabs.newTab().setText("연결중")); // 내가 참여한 미팅 글이 뜸
        tabs.addTab(tabs.newTab().setText("이성 목록"));
        tabs.setTabGravity(tabs.GRAVITY_FILL);

        //어답터설정
        final ViewPager viewPager = view.findViewById(R.id.matching_frame);
        final MatchingPagerAdapter myPagerAdapter = new MatchingPagerAdapter(getChildFragmentManager(), 4);
        viewPager.setAdapter(myPagerAdapter);

        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                status = tab.getPosition();
                if (status == 2)
                    setHasOptionsMenu(false);
                else
                    setHasOptionsMenu(true);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.top_search_app_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (status == 0)
            getActivity().startActivity(new Intent(getContext(), MatchingSearchActivity.class));
        else if (status == 1)
            getActivity().startActivity(new Intent(getContext(), MatchingSearchActivity.class));
        else if (status == 3)
            getActivity().startActivity(new Intent(getContext(), SearchPeopleActivity.class));

        return super.onOptionsItemSelected(item);
    }
}