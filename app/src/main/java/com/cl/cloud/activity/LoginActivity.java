package com.cl.cloud.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cl.cloud.R;
import com.cl.cloud.protocol.Md5Encrypt;
import com.cl.cloud.util.LoginRequestHelper;
import com.cl.cloud.util.SpUtils;
import com.xhd.base.activity.BaseActivity;
import com.xhd.base.widget.MCircularProgressBar;
import com.xhd.base.widget.dialog.MProgressDialog;
import com.xhd.base.widget.dialog.MToast;
import com.xhd.base.widget.dialog.config.MDialogConfig;

/**
 * Created by work2 on 2019/5/10.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEtName;
    private EditText mEtPwd;

    private MProgressDialog mProgressDialog;

    @Override
    protected int getResId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        mEtName = findViewById(R.id.et_name);
        mEtPwd = findViewById(R.id.et_pwd);
        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void initData() {
        MDialogConfig config = new MDialogConfig.Builder()
                .setCanceledOnBackKeyPressed(false).build();
        mProgressDialog = new MProgressDialog(mActivity, config);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        mIntent = intent;
        if(mProgressDialog.isShowing()) mProgressDialog.dismissProgress();
        mEtName.setText("");
        mEtPwd.setText("");
        MToast.makeShort(mContext, R.string.login_failed);
        super.onNewIntent(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                String name = mEtName.getText().toString();
                String pwd = mEtPwd.getText().toString();
                if(TextUtils.isEmpty(name)){
                    MToast.makeShort(mContext, R.string.empty_name);
                    break;
                }
                if(TextUtils.isEmpty(pwd)){
                    MToast.makeShort(mContext, R.string.empty_pwd);
                    break;
                }
                String md5Pwd = Md5Encrypt.MD5_2(pwd);
                SpUtils.getInstances().putUserName(name).putUserPwd(md5Pwd).commit();
                mProgressDialog.showProgress(mContext.getString(R.string.login_ing));
                LoginRequestHelper.login(mContext, name, md5Pwd);
                break;
        }
    }
}
