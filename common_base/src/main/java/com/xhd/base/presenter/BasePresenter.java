package com.xhd.base.presenter;

import com.xhd.base.activity.BaseActivity;
import com.xhd.base.fragment.BaseFragment;
import com.xhd.base.util.LogUtils;

public class BasePresenter<V> {

    protected String TAG;

    // View 层回调 success failed 使用
    public V mView;
    // Model 层 网络回调生成 Model 使用
    public BaseActivity mActivity;
    public BaseFragment mFragment;

    public BasePresenter(V view) {
        attachView(view);
        if(view instanceof BaseActivity) mActivity = (BaseActivity) view;
        if(view instanceof BaseFragment){
            mFragment = (BaseFragment) view;
            mActivity = (BaseActivity) mFragment.getActivity();
        }

        TAG = this.getClass().getSimpleName();
        LogUtils.i(TAG, "mActivity:" + mActivity);
    }

    public void attachView(V mView) {
        this.mView = mView;
    }

    public void detachView() {
        this.mView = null;
    }

}
