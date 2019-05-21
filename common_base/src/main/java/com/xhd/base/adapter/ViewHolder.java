package com.xhd.base.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 通用 ViewHolder
 * 1、与 convertView 进行绑定
 * 2、获取 Item 绑定的 Layout 中的各个子控件
 */
public class ViewHolder {

    // Item 关联的 Layout
    private View mConvertView;
    // Item 绑定的 Layout 中的 View 控件
    private final SparseArray<View> mViews; // 键为控件Id (Key:int类型)

    /**
     * 私有构造方法
     */
    private ViewHolder(View convertView) {
        mConvertView = convertView;
        mViews = new SparseArray<>();
        // 与 convertView 绑定：setTag
        mConvertView.setTag(this);
    }

    /**
     * 暴露 get 方法
     * @param convertView 绑定的 ListView 复用 ItemView
     * @return  ViewHolder 对象
     */
    public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
            return new ViewHolder(convertView);
        }
        return (ViewHolder) convertView.getTag();
    }

    /**
     * 获取绑定的 Layout
     */
    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 根据控件 Id(Key) 获取对应的控件（Value）
     * 第一次要将控件先存入 SparseArray 中，并返回；后可直接获取，并返回。
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

}
