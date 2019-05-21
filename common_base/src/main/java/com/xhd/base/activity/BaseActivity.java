package com.xhd.base.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.xhd.base.permission.PermissionActivity;
import com.xhd.base.presenter.BasePresenter;
import com.xhd.base.util.ScreenUtils;

import java.util.ArrayList;

public abstract class BaseActivity extends PermissionActivity {

    protected Context mContext;
    protected BaseActivity mActivity;
    protected String TAG;
    protected Handler mHandler; // 懒加载（子类去初始化；如果使用 RxJava 操作符，一般也用不到）
    protected Intent mIntent;
    protected Bundle mBundle;

    protected ArrayList<BasePresenter> mPresenters = new ArrayList<>();
    protected Dialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;
        initScreenInfo();
        setContentView(getResId());
//        ButterKnife.bind(this);
        TAG = this.getClass().getSimpleName();
        mIntent = getIntent();
        mBundle = mIntent.getExtras();
        // 在 initData 之前 Presenter 注册管理
        addPresenter();
        initView();
        initData();
    }

    public void addPresenter(){};

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mDialog != null){
            if(mDialog.isShowing()) mDialog.dismiss();
        }
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        for(BasePresenter presenter : mPresenters){
            presenter.detachView();
        }
        mContext = null;
        mActivity = null;
        mHandler = null;
    }

    protected void initScreenInfo(){
        ScreenUtils.setPortrait(this);
    }

    protected abstract int getResId();
    public abstract void initView();
    public abstract void initData();

    public void setMProgressDialog(Dialog dialog) {
        mDialog = dialog;
    }

    /*================= gotoActivity ===================*/

    public <T extends Activity> void goToActivity(Class<T> cls) {
        goToActivity(cls, true, null);
    }

    public <T extends Activity> void goToActivity(Class<T> cls, boolean isFinish) {
        goToActivity(cls, isFinish, null);
    }

    public <T extends Activity> void goToActivity(Class<T> cls, Bundle bundle) {
        goToActivity(cls, true, bundle);
    }

    public <T extends Activity> void goToActivity(Class<T> cls, boolean isFinish, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    public <T extends Activity> void goToActivityForResult(Class<T> clazz, int requestCode){
        Intent intent = new Intent(mActivity, clazz);
        mActivity.startActivityForResult(intent, requestCode);
    }

}
