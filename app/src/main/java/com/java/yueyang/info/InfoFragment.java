package com.java.yueyang.info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.java.yueyang.R;
import com.java.yueyang.data.COVIDInfo;

import java.util.List;


public class InfoFragment extends Fragment implements InfoContract.View {

    private InfoContract.Presenter mPresenter;
    private String mKeyword;
    private RecyclerView mRecyclerView;
    private InfoAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private int mLastClickPosition = -1;

    public InfoFragment() {
        // Required empty public constructor
    }
    public static InfoFragment newInstance() {
        InfoFragment fragment = new InfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLastClickPosition = -1;
        mKeyword = "";
        mAdapter = new InfoAdapter(getContext());
        mAdapter.setOnItemClickListener((View itemView, int position) -> {
            COVIDInfo info = mAdapter.getInfo(position);
            this.mLastClickPosition = position;

            this.mPresenter.openNewsDetailUI(info, new Bundle());
        });

        mPresenter = new InfoPresenter(this);
        mPresenter.subscribe();
    }

    public void setKeyword(String keyword){
        mKeyword = keyword;
        mPresenter.setKeyword(keyword);
        if(keyword.equals(""))
            setFooterVisible(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }


    @Override
    public void setPresenter(InfoContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void setInfo(List<COVIDInfo> list) {
        mAdapter.setData(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setFooterVisible(boolean flag)
    {
        mAdapter.setFooterVisible(flag);
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
