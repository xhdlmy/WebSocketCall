package com.cl.cloud.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.cl.cloud.R;
import com.cl.cloud.activity.LoginActivity;
import com.cl.cloud.app.App;
import com.cl.cloud.app.Constant;
import com.cl.cloud.util.SpUtils;
import com.cl.cloud.websocket.WebSocketManager;
import com.xhd.base.fragment.BaseFragment;

/**
 * Created by work2 on 2019/5/20.
 */

public class MyFragment extends BaseFragment {
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initView() {
        LinearLayout llLoginout = mView.findViewById(R.id.ll_login_out);
        llLoginout.setOnClickListener(v -> loginOut());
    }

    private void loginOut() {
        new AlertDialog.Builder(mActivity)
                .setTitle(mActivity.getString(R.string.login_out))
                .setMessage(mActivity.getString(R.string.comfirm_login_out))
                .setPositiveButton(mActivity.getString(R.string.sure), (dialog, which) -> {
                    SpUtils.getInstances().putUserName("").putUserPwd("").commit();
                    WebSocketManager webSocketManager = App.getWebSocketManagerMap().get(Constant.WS_URL);
                    webSocketManager.closeWebSocket(mActivity.getString(R.string.login_out));
                    dialog.dismiss();
                })
                .setNegativeButton(mActivity.getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void initData() {

    }
}
