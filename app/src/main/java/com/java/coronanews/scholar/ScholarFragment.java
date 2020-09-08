package com.java.coronanews.scholar;

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
import com.java.coronanews.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻主页面
 * Created by equation on 9/8/17.
 * A simple {@link Fragment} subclass.
 * Use the {@link ScholarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class ScholarFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MyPagerAdapter mPagerAdapter;
    private List<String> mCategories = new ArrayList<String>();

    public ScholarFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewsListFragment.
     */
    public static ScholarFragment newInstance() {
        ScholarFragment fragment = new ScholarFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategories.clear();
        //mCategories = Manager.I.getConfig().availableCategories(true);
        mCategories.add("知疫学者");
        mCategories.add("追忆学者");
        mPagerAdapter = new MyPagerAdapter(getChildFragmentManager(), mCategories);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scholar_page, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.scholar_view_pager);
        mViewPager.setOffscreenPageLimit(3);

        mTabLayout = (TabLayout) view.findViewById(R.id.scholar_tab_layout);
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
            return ScholarListFragment.newInstance(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ScholarListFragment f = (ScholarListFragment) super.instantiateItem(container, position);
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
