package com.java.coronanews.scolar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
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
import com.java.coronanews.data.Manager;
import com.java.coronanews.data.NewsItem;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;


public class ScholarDetailActivity extends AppCompatActivity {

    private String news_title;
    private String news_content;
    private String news_info;
    private String news_type;


    // private NewsDetailContract.Presenter mPresenter;
    private NewsItem mNews;
    private boolean mError;

    private TextView mTag, mDetail, mContent;
    private ImageView mImage;
    private FloatingActionButton mFab;
    private NestedScrollView mScrollView;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    // private View mBottomView, mFavoriteBtn, mSpeechBtn, mShareBtn;


    private void onShare() {
        Manager.I.shareNews(this, mNews.title,
                    "", "www.example.com", "");
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        news_title = getIntent().getStringExtra("NEWS_TITLE");
        news_content = getIntent().getStringExtra("NEWS_CONTENT");
        news_info = getIntent().getStringExtra("NEWS_INFO");
        news_type = getIntent().getStringExtra("NEWS_TYPE");

        mError = false;

        setContentView(R.layout.activity_news_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        // mFab.setOnClickListener((View view) -> onFavoite());

        //mBottomView = findViewById(R.id.bottom_view);
        mScrollView = (NestedScrollView) findViewById(R.id.scroll_view);
//        mScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            private boolean isBottomShow = true;
//
//            @Override
//            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if (scrollY - oldScrollY > 0 && isBottomShow) { // 下移隐藏
//                    isBottomShow = false;
//                    mBottomView.animate().translationY(mBottomView.getHeight());
//                } else if (scrollY - oldScrollY < 0 && !isBottomShow) { // 上移出现
//                    isBottomShow = true;
//                    mBottomView.animate().translationY(0);
//                }
//            }
//        });

//        mFavoriteBtn = findViewById(R.id.bottom_favorite);
//        mSpeechBtn = findViewById(R.id.bottom_speech);
//        mShareBtn = findViewById(R.id.bottom_share);
//        mFavoriteBtn.setOnClickListener((View view) -> onFavoite());
//        mSpeechBtn.setOnClickListener((View view) -> onSpeech());
//        mShareBtn.setOnClickListener((View view) -> onShare());

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbarLayout.setTitle(news_title);

        mTag = (TextView) findViewById(R.id.text_tag);
        mDetail = (TextView) findViewById(R.id.text_detail);
        mContent = (TextView) findViewById(R.id.text_content);
        mImage = (ImageView) findViewById(R.id.image_view);

//        mFab.setSelected(news_is_favorited);
//        mFavoriteBtn.setSelected(news_is_favorited);
//        if (news_picture_url != null) {
//            ImageLoader.displayImage(news_picture_url, mImage);
//        }

        mTag.setText(news_type);
        mDetail.setText(news_info);

        String content = TextUtils.join("\n\n　　", news_content.trim().split("　　"));
        mContent.setText(content);

        mFab.setClickable(true);
        findViewById(R.id.layout_content).setVisibility(View.VISIBLE);

    }



    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_news, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_share:
                onShare();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @SuppressLint("RestrictedApi")
    public void onBackPressed() {
        mFab.setVisibility(View.INVISIBLE);

        if (mNews != null) {
            Intent intent = new Intent();
            // intent.putExtra("IS_FAVORITED", mNews.is_favorite);
            setResult(RESULT_OK, intent);
        }

        super.onBackPressed();
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
