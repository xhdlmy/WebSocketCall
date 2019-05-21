package com.xhd.base.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by computer on 2018/1/18.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {

    private View mConvertView;
    private SparseArray<View> mViews;

    private BaseViewHolder(@NonNull View itemView) {
        super(itemView);
        mConvertView = itemView;
        mViews = new SparseArray<>();
    }

    @NonNull
    public static BaseViewHolder create(Context context, int layoutId, ViewGroup parent) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        BaseViewHolder holder = new BaseViewHolder(itemView);
        return holder;
    }

    public static BaseViewHolder create(View itemView) {
        return new BaseViewHolder(itemView);
    }

    public View getConvertView() {
        return mConvertView;
    }

    @NonNull
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public void setVisiable(int viewId, boolean visiable){
        View view = getView(viewId);
        view.setVisibility(visiable ? View.VISIBLE : View.GONE);
    }

}
