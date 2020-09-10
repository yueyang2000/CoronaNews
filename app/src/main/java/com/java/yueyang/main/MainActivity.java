package com.java.yueyang.main;

import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.java.yueyang.R;
import com.java.yueyang.cluster.ClusterFragment;
import com.java.yueyang.settings.SettingsFragment;
import com.java.yueyang.chart.PaintChartFragment;
import com.java.yueyang.history.HistoryFragment;
import com.java.yueyang.news.NewsFragment;
import com.java.yueyang.info.InfoFragment;
import com.java.yueyang.scholar.ScholarFragment;

/**
 * Created by equation on 9/7/17.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainContract.View {

    private static final String RESTART_BY_MODE = "RESTART_BY_MODE";

    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private MainContract.Presenter mPresenter;
    private Fragment mNews, mHistory, mChart, mSetting, mInfo, mScholar, mCluster;
    private MenuItem mSearchItem;
    private SearchView mSearchView;
    private String mKeyword = "";
    int cur_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MainPresenter(this, getIntent().getBooleanExtra(RESTART_BY_MODE, false));

        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        mSearchItem = menu.findItem(R.id.action_search);
        mSearchItem.setVisible(R.id.nav_news == mPresenter.getCurrentNavigation() || R.id.nav_info == mPresenter.getCurrentNavigation());
        mSearchView = (SearchView) mSearchItem.getActionView();
        mSearchView.setOnCloseListener(() -> {
            if (!mKeyword.isEmpty()) {
                mKeyword = "";
                if(R.id.nav_news == mPresenter.getCurrentNavigation())
                    ((NewsFragment) mNews).setKeyword("");
            }
            return false;
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                mKeyword = query;
                System.out.println(mPresenter.getCurrentNavigation() + "  " + R.id.nav_news);
                if (mPresenter.getCurrentNavigation() == R.id.nav_news && mNews != null)
                    ((NewsFragment) mNews).setKeyword(query);
                if(mPresenter.getCurrentNavigation() == R.id.nav_info && mInfo != null){
                    ((InfoFragment) mInfo).setKeyword(query);
                }
                mSearchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        mPresenter.switchNavigation(item.getItemId());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public Context context() {
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void start(Intent intent, Bundle options) {
        startActivity(intent, options);
    }

    public void switchTo(int id, String title) {
        cur_id = id;
        mToolbar.setTitle(title);
        if (mSearchItem != null) {
            mSearchItem.setVisible(R.id.nav_news == id || R.id.nav_info == id);
        }
        if (mSearchView != null) {
            mSearchView.clearFocus();
            mSearchView.setQuery(mKeyword, false);
        }
        mNavigationView.setCheckedItem(id);
    }




    @Override
    public void switchToNews() {
        switchTo(R.id.nav_news, "新闻");
        if (mNews == null)
            mNews = NewsFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mNews).commit();
    }


    @Override
    public void switchToSetting() {
        switchTo(R.id.nav_setting, "设置");
        if (mSetting == null)
            mSetting = new SettingsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mSetting).commit();
    }

    @Override
    public void switchToChart() {
        switchTo(R.id.nav_data, "疫情数据");
        if (mChart == null)
            mChart = PaintChartFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mChart).commit();
    }

    public void switchToHistory() {
        switchTo(R.id.nav_history, "历史");
        if (mHistory == null)
            mHistory = HistoryFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mHistory).commit();
    }

    @Override
    public void switchToInfo() {
        switchTo(R.id.nav_info, "疫情图谱");
        if(mInfo == null){
            mInfo = InfoFragment.newInstance();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mInfo).commit();
    }

    @Override
    public void switchToScholar(){
        switchTo(R.id.nav_scholar, "知疫学者");
        if(mScholar == null){
            mScholar = ScholarFragment.newInstance();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mScholar).commit();
    }

    @Override
    public void switchToCluster(){
        switchTo(R.id.nav_scholar, "新闻聚类");
        if(mCluster == null){
            mCluster = ClusterFragment.newInstance();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mCluster).commit();
    }

}
