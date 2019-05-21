package com.xhd.base.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xhd.base.activity.BaseActivity;
import com.xhd.base.presenter.BasePresenter;
import com.xhd.base.util.LogUtils;
import com.xhd.base.util.ScreenUtils;

import java.util.ArrayList;

/**
 * Fragment Tab 切换太快，还是会出现空指针
 */
public abstract class BaseFragment extends Fragment {

    protected String TAG;

    protected BaseActivity mActivity;
    protected View mView;
    //Fragment 替换 View视图时 需要解绑；unbinder 通过 ButterKnife.bind(this, root); 中赋值
//    Unbinder unbinder;

    protected ArrayList<BasePresenter> mPresenters = new ArrayList<>();

    // 手动实现 fitsSystemWindows="true" 自己加 Padding
    protected void initStatusBarPadding(Context context){
        mView.setPadding(0, ScreenUtils.getStatusHeight(context), 0, 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TAG = this.getClass().getSimpleName();
        LogUtils.i(TAG, "onCreateView");
        mActivity = (BaseActivity) getActivity();
        mView = inflater.inflate(getLayoutResId(), null);
        initView();
        addPresenter();
        initData();
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        LogUtils.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        LogUtils.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        LogUtils.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        LogUtils.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        LogUtils.i(TAG, "onDestroyView");
        super.onDestroyView();
        for(BasePresenter presenter : mPresenters){
            presenter.detachView();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        LogUtils.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(hidden){
            onFragmentPause();
        }else{
            onFragmentResume();
        }
        super.onHiddenChanged(hidden);
    }

    public void onFragmentResume(){}
    public void onFragmentPause(){}

    public void addPresenter(){}

    protected abstract int getLayoutResId();
    protected abstract void initView();
    public abstract void initData();

}
