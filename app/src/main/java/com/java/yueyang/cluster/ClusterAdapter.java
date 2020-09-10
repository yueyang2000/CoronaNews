package com.java.yueyang.cluster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.java.yueyang.R;
import com.java.yueyang.data.COVIDInfo;

import java.util.ArrayList;
import java.util.List;


public class ClusterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_FOOTER = 2;

    private Context mContext;
    private List<String> mData = new ArrayList<>();
    private List<Integer> mCnt = new ArrayList<>();
    private boolean mIsShowFooter = true;
    private OnItemClickListener mOnItemClickListener;

    public ClusterAdapter(Context context) {
        mContext = context;
    }


    public void setData(List<String> data) {
        mData = new ArrayList<String>(data);
        this.notifyDataSetChanged();
    }

    public void setCnt(List<Integer> data){
        mCnt = new ArrayList<Integer>(data);
        this.notifyDataSetChanged();
    }


    public void setFooterVisible(boolean visible) {
        if (mIsShowFooter != visible) {
            mIsShowFooter = visible;
            if (mIsShowFooter)
                this.notifyItemInserted(mData.size());
            else
                this.notifyItemRemoved(mData.size());
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
            return new ClusterAdapter.ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer, parent, false);
            return new ClusterAdapter.FooterViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            String info = mData.get(position);
            int cnt = mCnt.get(position);
            final ItemViewHolder item = (ItemViewHolder) holder;
            item.mTitle.setText(info);
            item.mAuthor.setText("新闻条数："+cnt);
            item.setBackgroundColor(mContext.getResources().getColor(R.color.colorCard));
            item.mCurrentPosition = position;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mData.size() && mIsShowFooter)
            return TYPE_FOOTER;
        else
            return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mData.size() + (mIsShowFooter ? 1 : 0);
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mView;
        TextView mTitle, mAuthor, mDate;
        int mCurrentPosition = -1;
        public ItemViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = (TextView) view.findViewById(R.id.text_title);
            mAuthor = (TextView) view.findViewById(R.id.text_author);
            mDate = (TextView) view.findViewById(R.id.text_date);
            view.setOnClickListener(this);
        }

        public void setBackgroundColor(int color) {
            mView.setBackgroundColor(color);
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, this.getLayoutPosition());
            }
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View view) {
            super(view);
        }
    }

}
