package com.java.yueyang.history;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.java.yueyang.R;
import com.java.yueyang.data.NewsItem;
import com.java.yueyang.news.NewsAdapter;

import java.util.List;

/**
 * 收藏列表
 * Created by equation on 9/12/17.
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class HistoryFragment extends Fragment implements HistoryContract.View {

    private HistoryContract.Presenter mPresenter;

    private RecyclerView mRecyclerView;
    private NewsAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private int mLastClickPosition = -1;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewsListFragment.
     */
    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLastClickPosition = -1;

        mAdapter = new NewsAdapter(getContext());
        mAdapter.setFooterVisible(false);
        mAdapter.setOnItemClickListener((View itemView, int position) -> {
            NewsItem news = mAdapter.getNews(position);
            this.mLastClickPosition = position;

            this.mPresenter.openNewsDetailUI(news, new Bundle());
        });

        mPresenter = new HistoryPresenter(this);
        mPresenter.subscribe();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }


    @Override
    public void setPresenter(HistoryContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void setHistory(List<NewsItem> list) {
        mAdapter.setData(list);
    }

    @Override
    public Context context() {
        return getContext();
    }

    @Override
    public void start(Intent intent, Bundle options) {
        startActivityForResult(intent, 0, options);
    }

    public void clean_history(){
        Toast.makeText(this.getActivity(), "清除历史", Toast.LENGTH_SHORT);
    }

}
