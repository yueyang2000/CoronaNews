package com.java.yueyang.news;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.java.yueyang.R;
import com.java.yueyang.data.Config;

import java.util.ArrayList;
import java.util.List;


public class NewsFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private String mKeyword = "";
    private MyPagerAdapter mPagerAdapter;
    private List<String> mCategories = new ArrayList<String>(){};

    public NewsFragment() {
        // Required empty public constructor
    }

    public void setKeyword(String keyword) {
        mKeyword = keyword;
        mPagerAdapter.notifyDataSetChanged();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewsListFragment.
     */
    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategories.clear();
        List<String> available = Config.availableList();
        for(int i = 0; i<available.size(); i++)
        {
            mCategories.add(available.get(i));
        }
        mPagerAdapter = new MyPagerAdapter(getChildFragmentManager(), mCategories);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_page, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(3);

        mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.removeAllTabs();
        for (int i = 0; i < mCategories.size(); i++)
            mTabLayout.addTab(mTabLayout.newTab().setText(mCategories.get(i)));
        mViewPager.setAdapter(mPagerAdapter);


        return view;
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        private List<String> mCategories;

        public MyPagerAdapter(FragmentManager fm, List<String> list) {
            super(fm);
            mCategories = list;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mCategories.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return NewsListFragment.newInstance(position, mKeyword);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            NewsListFragment f = (NewsListFragment) super.instantiateItem(container, position);
            f.setKeyword(mKeyword);
            return f;
        }

        @Override
        public int getCount() {
            return mCategories.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}
