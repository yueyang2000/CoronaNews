package com.java.yueyang.cluster;

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

public class ClusterFragment extends Fragment implements ClusterContract.View {

    private ClusterContract.Presenter mPresenter;

    private RecyclerView mRecyclerView;
    private ClusterAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private int mLastClickPosition = -1;

    public ClusterFragment() {
        // Required empty public constructor
    }


    public static ClusterFragment newInstance() {
        ClusterFragment fragment = new ClusterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLastClickPosition = -1;

        mAdapter = new ClusterAdapter(getContext());
        mAdapter.setFooterVisible(false);
        mAdapter.setOnItemClickListener((View itemView, int position) -> {

            this.mLastClickPosition = position;

            this.mPresenter.openClusterDetailUI(position, new Bundle());
        });

        mPresenter = new ClusterPresenter(this);
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
    public void setPresenter(ClusterContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void setCluster(List<String> list) {
        mAdapter.setData(list);
    }
    @Override
    public void setCnt(List<Integer> list) {
        mAdapter.setCnt(list);
    }

    @Override
    public Context context() {
        return getContext();
    }

    @Override
    public void start(Intent intent, Bundle options) {
        startActivityForResult(intent, 0, options);
    }


}
