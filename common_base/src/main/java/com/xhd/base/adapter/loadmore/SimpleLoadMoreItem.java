package com.xhd.base.adapter.loadmore;


import com.xhd.base.R;

/**
 * Created by BlingBling on 2016/11/11.
 */

public class SimpleLoadMoreItem extends LoadMoreItem {

    @Override
    public int getLayoutId() {
        return R.layout.item_recycler_load_more;
    }

    @Override
    public int getLoadingViewId() {
        return R.id.loading;
    }

    @Override
    public int getLoadFailViewId() {
        return R.id.load_fail;
    }

    @Override
    public int getLoadEndViewId() {
        return R.id.load_end;
    }

}
