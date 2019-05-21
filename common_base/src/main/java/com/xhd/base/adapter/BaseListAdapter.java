package com.xhd.base.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.xhd.base.activity.BaseActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 通用 BaseAdapter
 * 1、绑定的数据格式为：List<T>， T 代表具体 Bean 对象
 * 2、子类 Adapter 需要做的事:
 *      第一步：只需要在构造中传入 Data 与 ItemLayoutId；
 *      第二步：实现该类暴露出去的 bindData 方法，将 View 与 Data 进行绑定即可。
 * 3、选择性实现事件点击、长按
 */

public abstract class BaseListAdapter<T> extends BaseAdapter {

    protected BaseActivity mActivity;
    protected LayoutInflater mInflater;
    protected List<T> mDatas;
    // Item 绑定的视图
    protected final int mItemLayoutId;

    protected BaseListAdapter(Activity activity, ArrayList<T> datas, int itemLayoutId) {
        mActivity = (BaseActivity) activity;
        mInflater = LayoutInflater.from(activity);
        mDatas = datas;
        mItemLayoutId = itemLayoutId;
    }

    protected BaseListAdapter(Activity activity, T[] datas, int itemLayoutId) {
        mActivity = (BaseActivity) activity;
        mInflater = LayoutInflater.from(activity);
        mDatas = Arrays.asList(datas);
        mItemLayoutId = itemLayoutId;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder
                = ViewHolder.get(mActivity, convertView, parent, mItemLayoutId, position);
        bindData(viewHolder, getItem(position), position);
        // convertView 的点击事件
        viewHolder.getConvertView().setOnClickListener(v -> onConvertViewClick(v, position));
        return viewHolder.getConvertView();
    }

    // Item 点击事件
    protected void onConvertViewClick(View v, int position) {
        // 子类选择性实现
    }

    /**
     * 暴露 Item 视图（View） 与 数据（Data） 绑定的方法
     * @param viewHolder 包含 Item 的各个子控件
     * @param item 数据集合中每个 Item 对应的数据
     */
    protected abstract void bindData(ViewHolder viewHolder, T item, int position);

}
