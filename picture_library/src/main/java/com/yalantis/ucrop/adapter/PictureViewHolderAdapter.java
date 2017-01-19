package com.yalantis.ucrop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.adapter
 * email：893855882@qq.com
 * data：16/12/31
 */

public abstract class PictureViewHolderAdapter<VH extends PicturePhotoGalleryAdapter.ViewHolder, T> extends BaseAdapter {
    private Context mContext;
    private List<T> mList;
    private LayoutInflater mInflater;

    public PictureViewHolderAdapter(Context context, List<T> list) {
        this.mContext = context;
        this.mList= list;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return this.mList.size();
    }

    @Override
    public T getItem(int position) {
        return this.mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        VH holder;
        if ( view == null ) {
            holder = onCreateViewHolder(viewGroup, i);
            holder.view.setTag(holder);
        } else {
            holder = (VH) view.getTag();
        }

        onBindViewHolder(holder, i);
        return holder.view;
    }

    public abstract VH onCreateViewHolder(ViewGroup parent, int position);
    public abstract void onBindViewHolder(VH holder, int position);

    public View inflate(int resLayout, ViewGroup parent) {
        return mInflater.inflate(resLayout, parent, false);
    }

    /**
     * 返回列表数据
     * @return
     */
    public List<T> getDatas() {
        return this.mList;
    }

    public Context getContext() {
        return this.mContext;
    }

    public LayoutInflater getLayoutInflater(){
        return this.mInflater;
    }

    public static class ViewHolder {
        View view;
        public ViewHolder(View view) {
            this.view = view;
        }
    }
}
