package com.java.yueyang.cluster;
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
import com.java.yueyang.R;
import com.java.yueyang.data.ImageLoader;
import com.java.yueyang.data.Manager;
import com.java.yueyang.data.NewsItem;
import com.java.yueyang.data.Scholar;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import org.json.JSONException;
import org.json.JSONObject;


public class ClusterDetailActivity extends AppCompatActivity {

    // private NewsDetailContract.Presenter mPresenter;
    private boolean mError;

    private NestedScrollView mScrollView;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String msg = getIntent().getStringExtra("MSG");
        String title = getIntent().getStringExtra("TITLE");
        setContentView(R.layout.activity_cluster_detail);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.cluster_collapsing_toolbar);
        mCollapsingToolbarLayout.setTitle(title);
        mError = false;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        mScrollView = (NestedScrollView) findViewById(R.id.scroll_view);


        TextView text = findViewById(R.id.cluster_text_detail);
        text.setText(msg);
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
}
