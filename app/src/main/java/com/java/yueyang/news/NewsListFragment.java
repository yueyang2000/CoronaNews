package com.java.yueyang.news;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.java.yueyang.R;
import com.java.yueyang.data.NewsItem;

import java.util.List;


public class NewsListFragment extends Fragment implements NewsListContract.View {

    private NewsListContract.Presenter mPresenter;
    private int mCategory;
    private String mKeyword;

    private int mLastClickPosition = -1;
    private SwipeRefreshLayout mSwipeRefreshWidget;
    private RecyclerView mRecyclerView;
    private NewsAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private View mTextEmpty;

    public NewsListFragment() {
        // Required empty public constructor
    }

    public void setKeyword(String keyword) {
        mKeyword = keyword;
        Bundle args = this.getArguments();
        args.putString("keyword", keyword);
        this.setArguments(args);
    }

    public String getKeyword() {
        return mKeyword;
    }


    public static NewsListFragment newInstance(int category, String keyword) {
        Bundle args = new Bundle();
        NewsListFragment fragment = new NewsListFragment();
        args.putInt("category", category);
        args.putString("keyword", keyword);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLastClickPosition = -1;
        mCategory = getArguments().getInt("category");
        mKeyword = getArguments().getString("keyword");
        mPresenter = new NewsListPresenter(this, mCategory, mKeyword);

        mAdapter = new NewsAdapter(getContext());
        mAdapter.setOnItemClickListener((View itemView, int position) -> {
            NewsItem news = mAdapter.getNews(position);
            if (!news.has_read) {
                this.mLastClickPosition = position;
            } else
                this.mLastClickPosition = -1;

            this.mPresenter.openNewsDetailUI(news, new Bundle());
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLastClickPosition >= 0 && mPresenter != null)
            mPresenter.fetchNewsRead(mLastClickPosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TypedValue colorPrimary = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, colorPrimary, true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);

        mTextEmpty = view.findViewById(R.id.text_empty);
        mTextEmpty.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);

        mSwipeRefreshWidget = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);
        mSwipeRefreshWidget.setColorSchemeColors(getResources().getColor(colorPrimary.resourceId));
        mSwipeRefreshWidget.setOnRefreshListener(() -> {
            mSwipeRefreshWidget.setRefreshing(true);
            mPresenter.refreshNews();
        });

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastVisibleItem;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    if (lastVisibleItem == mAdapter.getItemCount() - 1 &&
                           mAdapter.isShowFooter()  && !mPresenter.isLoading()) { //delete ISFOOTER
                        mPresenter.requireMoreNews();
                    }
                }

            }
        });
        mPresenter.subscribe();
        return view;
    }

    @Override
    public void setPresenter(NewsListContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public Context context() {
        return getContext();
    }

    @Override
    public void start(Intent intent, Bundle options) {
        startActivity(intent, options);
    }

    @Override
    public void setNewsList(List<NewsItem> list) {
        mAdapter.setData(list);
        mTextEmpty.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void appendNewsList(List<NewsItem> list) {
        mAdapter.appendData(list);
    }

    @Override
    public void resetItemRead(int pos, boolean has_read) {
        if (mAdapter.getNews(pos).has_read != has_read) {
            mAdapter.setRead(pos, has_read);
            mAdapter.notifyItemChanged(pos);
        }
    }

    @Override
    public void onSuccess(boolean loadCompleted) {
        //mAdapter.setFooterVisible(!loadCompleted);
        mSwipeRefreshWidget.setRefreshing(false);
    }

    @Override
    public void onError() {
        mSwipeRefreshWidget.setRefreshing(false);
        Toast.makeText(getContext(), "获取新闻失败，请稍后再试", Toast.LENGTH_SHORT).show();
    }
}
