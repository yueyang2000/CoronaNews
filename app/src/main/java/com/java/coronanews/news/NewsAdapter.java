package com.java.coronanews.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.java.coronanews.R;
import com.java.coronanews.data.NewsItem;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * 新闻列表适配器
 * Created by equation on 9/8/17.
 */

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_FOOTER = 2;

    private Context mContext;
    private List<NewsItem> mData = new ArrayList<NewsItem>();
    private boolean mIsShowFooter = true;
    private OnItemClickListener mOnItemClickListener;

    public NewsAdapter(Context context) {
        mContext = context;
    }

    public NewsItem getNews(int position) {
        return mData.get(position);
    }

    public void setData(List<NewsItem> data) {
        mData = new ArrayList<NewsItem>(data);
        this.notifyDataSetChanged();
    }

    public void appendData(List<NewsItem> data) {
        int pos = mData.size();
        mData.addAll(data);
        this.notifyItemRangeChanged(pos, getItemCount());
    }

    public void removeItem(int position) {
        mData.remove(position);
        this.notifyItemRemoved(position);
    }

    public void setRead(int position, boolean has_read) {
        NewsItem news = getNews(position);
        news.has_read = has_read;
        mData.set(position, news);
    }

    public boolean isShowFooter() {
        return mIsShowFooter;
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
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer, parent, false);
            return new FooterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            NewsItem news = mData.get(position);
            final ItemViewHolder item = (ItemViewHolder) holder;
            item.mTitle.setText(news.title);
            item.mAuthor.setText(news.author.isEmpty() ? news.source : news.author);
            item.mDate.setText(news.time);
            item.setBackgroundColor(mContext.getResources().getColor(news.has_read ? R.color.colorCardRead : R.color.colorCard));
            item.mCurrentPosition = position;
            final long start = System.currentTimeMillis();
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

    /**
     * 新闻点击 Listener
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * 新闻单元格
     */
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

    /**
     * 列表底部
     */
    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View view) {
            super(view);
        }
    }
}
