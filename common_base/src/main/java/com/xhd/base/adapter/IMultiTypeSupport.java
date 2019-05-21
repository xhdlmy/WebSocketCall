package com.xhd.base.adapter;

import android.support.annotation.LayoutRes;

/**
 * Created by computer on 2018/1/18.
 */

public interface IMultiTypeSupport<T> {

    // 根据 position or data 字段来判断应该属于哪种自定义的 ViewType
    int getItemViewType(int position, T data);


    @LayoutRes
    int getLayoutId(int viewType);

}
