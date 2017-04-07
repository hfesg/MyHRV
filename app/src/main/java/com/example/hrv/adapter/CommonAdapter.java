package com.example.hrv.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import com.example.hrv.viewholder.CommonViewHolder;

import java.util.List;

/**
 * Created by xushuzhan on 2017/2/8.
 * CommonAdapter
 */

public abstract class CommonAdapter<T> extends RecyclerView.Adapter<CommonViewHolder> {
    protected int mLayoutId;
    protected List<T> mData; //bean文件的集合
    protected LayoutInflater mInflater;

    public abstract void convert(CommonViewHolder holder, T t);//用于绑定数据的方法，交给用户去控制

    public CommonAdapter(Context context, int layoutId, List<T> data) {
        mLayoutId = layoutId;
        mData = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return CommonViewHolder.newInstance(parent.getContext(), parent, mLayoutId);
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        convert(holder, mData.get(position));
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public Context getContext() {
        return mInflater.getContext();
    }

}