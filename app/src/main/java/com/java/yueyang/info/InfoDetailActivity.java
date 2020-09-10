package com.java.yueyang.info;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;

import com.java.yueyang.R;
import com.java.yueyang.data.COVIDInfo;
import com.java.yueyang.data.ImageLoader;
import com.java.yueyang.data.Manager;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

public class InfoDetailActivity extends AppCompatActivity {

    private String entity;;
    private COVIDInfo mInfo;

    // private NewsDetailContract.Presenter mPresenter;


    private ProgressBar mProgressBar;
    private View content;
    private ImageView mImage;
    private NestedScrollView mScrollView;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    // private View mBottomView, mFavoriteBtn, mSpeechBtn, mShareBtn;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        entity = getIntent().getStringExtra("INFO");

        setContentView(R.layout.activity_info_detail);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbarLayout.setTitle(entity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mImage = (ImageView) findViewById(R.id.image_view);
        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);
        content = findViewById(R.id.layout_content);
        mProgressBar.setVisibility(View.VISIBLE);
        content.setVisibility(View.INVISIBLE);
        loadData();
    }

    public void loadData(){
        Single<List<COVIDInfo>> single = Manager.I.fetchInfo(entity);
        single.subscribe(new Consumer<List<COVIDInfo>>() {
            @Override
            public void accept(List<COVIDInfo> covidInfos) throws Exception {
                mInfo = covidInfos.get(0);
                if(mInfo.image!=null)
                    ImageLoader.displayImage(mInfo.image, mImage);
                loadFinish();
            }
        });

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
    public void loadFinish(){
        TextView abstractInfo = findViewById(R.id.text_abstractInfo);
        abstractInfo.setText(mInfo.GetDescription());
        String prop = "";
        for(int i=0; i<mInfo.property.keys.size();i++){
            prop = prop + mInfo.property.keys.get(i) + ":" + mInfo.property.values.get(i) + "\n";
        }
        TextView property = findViewById(R.id.text_property);
        property.setText(prop);


        TextView v = null;
        CardView c = null;
        v = findViewById(R.id.related1);
        c = findViewById(R.id.card1);
        setItemView(v, c,0);
        v = findViewById(R.id.related2);
        c = findViewById(R.id.card2);
        setItemView(v, c,1);
        v = findViewById(R.id.related3);
        c = findViewById(R.id.card3);
        setItemView(v, c,2);
        v = findViewById(R.id.related4);
        c = findViewById(R.id.card4);
        setItemView(v, c,3);
        v = findViewById(R.id.related5);
        c = findViewById(R.id.card5);
        setItemView(v, c,4);

        mProgressBar.setVisibility(View.INVISIBLE);
        content.setVisibility(View.VISIBLE);
    }
    void setItemView(TextView t, CardView c, int idx){
        ArrayList<COVIDInfo.Relationship> relationships = mInfo.relationships;
        if(relationships.size()<=idx)
            return;

        String arrow = (relationships.get(idx).foward)? " --> ": " <-- ";
        t.setText(relationships.get(idx).relation + arrow + relationships.get(idx).label);
        c.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                startNew(idx);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startNew(int idx){
        Intent intent = new Intent(this, InfoDetailActivity.class);
        intent.putExtra("INFO", mInfo.relationships.get(idx).label);
        start(intent, new Bundle());
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
