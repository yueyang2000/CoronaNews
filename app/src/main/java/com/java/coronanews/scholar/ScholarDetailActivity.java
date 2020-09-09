package com.java.coronanews.scholar;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.java.coronanews.R;
import com.java.coronanews.data.ImageLoader;
import com.java.coronanews.data.Manager;
import com.java.coronanews.data.NewsItem;
import com.java.coronanews.data.Scholar;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class ScholarDetailActivity extends AppCompatActivity {

    private Scholar mScholar;


    // private NewsDetailContract.Presenter mPresenter;
    private NewsItem mNews;
    private boolean mError;

    private TextView mTag, mDetail, mContent;
    private ImageView mImage;
    private FloatingActionButton mFab;
    private NestedScrollView mScrollView;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;


    private void onShare() {
        Manager.I.shareNews(this, mNews.title,
                    "", "www.example.com", "");
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mScholar = Scholar.parseJSON(new JSONObject(getIntent().getStringExtra("SCHOLAR")));
        } catch (JSONException e) {
            e.printStackTrace();
        }



        setContentView(R.layout.activity_scholar_detail);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.scholar_collapsing_toolbar);
        mCollapsingToolbarLayout.setTitle(mScholar.getName());
        mError = false;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        mScrollView = (NestedScrollView) findViewById(R.id.scroll_view);

        mImage = (ImageView) findViewById(R.id.image_view);
        ImageLoader.displayImage(mScholar.avatar, mImage);

        TextView indices, affiliation, bio, edu, work, field;
        indices = findViewById(R.id.scholar_indices);
        affiliation = findViewById(R.id.scholar_affiliation);
        bio = findViewById(R.id.scholar_bio);
        edu = findViewById(R.id.scholar_edu);
        work = findViewById(R.id.scholar_work);
        field = findViewById(R.id.scholar_field);
        indices.setText(mScholar.indices);
        affiliation.setText(mScholar.affiliation);
        bio.setText(mScholar.bio);
        edu.setText(mScholar.edu);
        work.setText(mScholar.work);
        field.setText(mScholar.field);

        findViewById(R.id.layout_content).setVisibility(View.VISIBLE);
    }


    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void start(Intent intent, Bundle options) {
        startActivity(intent, options);
    }

    public Context context() {
        return this;
    }


    public void onShowToast(String title) {
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
    }


}
