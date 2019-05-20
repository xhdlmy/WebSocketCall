package com.cl.cloud.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.cl.cloud.R;
import com.cl.cloud.app.Constant;
import com.cl.cloud.fragment.MyFragment;
import com.cl.cloud.fragment.PhoneFragment;
import com.cl.cloud.fragment.SmsFragment;
import com.cl.cloud.service.NetworkStateService;
import com.cl.cloud.util.MainBottomTab;
import com.cl.cloud.util.PermissionHelper;
import com.xhd.base.activity.BaseActivity;
import com.xhd.base.widget.FragmentTabHost;
import com.xhd.base.widget.dialog.MToast;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    FrameLayout mFlFragment;
    FrameLayout mTabcontent;
    FragmentTabHost mTabhost;

    private long mExitAppMoment;

    @Override
    protected int getResId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        mFlFragment = (FrameLayout) findViewById(R.id.fl_fragment);
        mTabhost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabcontent = (FrameLayout) findViewById(android.R.id.tabcontent);
    }

    @Override
    public void initData() {
        PermissionHelper.requestPermission(mActivity);
        initTabHostFragment();
    }

    private void initTabHostFragment() {
        mTabhost.setup(mContext, getSupportFragmentManager(), R.id.fl_fragment);
        MainBottomTab loanTab = new MainBottomTab(R.drawable.main_btn_phone_selector, R.string.phone, PhoneFragment.class);
        MainBottomTab repayTab = new MainBottomTab(R.drawable.main_btn_sms_selector, R.string.sms, SmsFragment.class);
        MainBottomTab myTab = new MainBottomTab(R.drawable.main_btn_my_selector, R.string.my, MyFragment.class);
        ArrayList<MainBottomTab> tabs = new ArrayList<>();
        tabs.add(loanTab);
        tabs.add(repayTab);
        tabs.add(myTab);

        mTabhost.clearAllTabs();
        for (MainBottomTab tab : tabs){
            TabHost.TabSpec tabSpec = mTabhost.newTabSpec(getString(tab.textStringId));
            tabSpec.setIndicator(tab.getBottomTab(mContext));
            mTabhost.addTab(tabSpec, tab.fragmentCls, null);
        }

        mTabhost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitAppMoment) > Constant.TWO_SECOND_MILLIS) {
                MToast.makeShort(mContext, R.string.exit_app);
                mExitAppMoment = System.currentTimeMillis();
                return true;
            } else {
                mContext.stopService(new Intent(mContext, NetworkStateService.class));
                finish();
                System.exit(0);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
