package com.example.hrv.viewholder;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by xushuzhan on 2017/2/8.
 * 通用的ViewHolder
 */

public class CommonViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;

    public CommonViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<View>();
    }

    public static CommonViewHolder newInstance(Context context, ViewGroup parent, int layoutId) {  //创建一个ViewHolder
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false); //得到每个item的view
        return new CommonViewHolder(itemView);//创建ViewHolder
    }

    /**
     * 通过viewId获取控件
     * @param viewId viewId
     * @return View
     */

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }
    /**
     * 常用的绑定数据的方法
     */
    public CommonViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    public CommonViewHolder setText(int viewId, String text, @NonNull Typeface typeface) {
        TextView tv = getView(viewId);
        tv.setTypeface(typeface);
        tv.setText(text);
        return this;
    }

    public CommonViewHolder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    public CommonViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }
    public CommonViewHolder setItemOnClickListener(View.OnClickListener listener) {
        itemView.setOnClickListener(listener);
        return this;
    }
    public Context getContext() {
        return itemView.getContext();
    }
}
