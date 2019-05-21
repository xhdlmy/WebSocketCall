package com.xhd.base.adapter.loadmore;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;

import com.xhd.base.adapter.BaseViewHolder;


/**
 * Created by BlingBling on 2016/11/11.
 */

public abstract class LoadMoreItem {

    public static final int STATUS_DEFAULT = 1;
    public static final int STATUS_LOADING = 2;
    public static final int STATUS_FAIL = 3;
    public static final int STATUS_END = 4;

    private int mLoadMoreStatus = STATUS_DEFAULT;

    public void setStatus(int loadMoreStatus) {
        this.mLoadMoreStatus = loadMoreStatus;
    }

    public int getStatus() {
        return mLoadMoreStatus;
    }

    public void convert(BaseViewHolder holder) {
        switch (mLoadMoreStatus) {
            case STATUS_LOADING:
                loadingStatus(holder);
                break;
            case STATUS_FAIL:
                failStatus(holder);
                break;
            case STATUS_END:
                endStatus(holder);
                break;
            case STATUS_DEFAULT:
                defaultStatus(holder);
                break;
        }
    }

    private void loadingStatus(BaseViewHolder holder) {
        holder.setVisiable(getLoadingViewId(), true);
        holder.setVisiable(getLoadFailViewId(), false);
        holder.setVisiable(getLoadEndViewId(), false);
    }

    private void failStatus(BaseViewHolder holder) {
        holder.setVisiable(getLoadingViewId(), false);
        holder.setVisiable(getLoadFailViewId(), true);
        holder.setVisiable(getLoadEndViewId(), false);
    }

    private void endStatus(BaseViewHolder holder) {
        holder.setVisiable(getLoadingViewId(), false);
        holder.setVisiable(getLoadFailViewId(), false);
        holder.setVisiable(getLoadEndViewId(), true);
    }

    private void defaultStatus(BaseViewHolder holder) {
        holder.setVisiable(getLoadingViewId(), false);
        holder.setVisiable(getLoadFailViewId(), false);
        holder.setVisiable(getLoadEndViewId(), false);
    }

    public abstract @LayoutRes
    int getLayoutId();
    public abstract @IdRes
    int getLoadingViewId();
    public abstract @IdRes
    int getLoadFailViewId();
    public abstract @IdRes
    int getLoadEndViewId();

}
