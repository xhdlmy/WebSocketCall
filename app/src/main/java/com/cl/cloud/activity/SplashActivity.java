package com.cl.cloud.activity;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.cl.cloud.R;
import com.cl.cloud.app.Constant;
import com.cl.cloud.util.PermissionHelper;
import com.cl.cloud.util.SpUtils;
import com.xhd.base.activity.BaseActivity;
import com.xhd.base.permission.PermissionListener;
import com.xhd.base.util.ToastUtils;
import com.xhd.base.util.manufacturer.PermissionsPageManager;

import java.util.List;

import static com.cl.cloud.app.Constant.PERMISSION_CALL_PHONE;
import static com.cl.cloud.app.Constant.PERMISSION_SEND_SMS;

/**
 * Created by work2 on 2019/5/10.
 */

public class SplashActivity extends BaseActivity {

    @Override
    protected int getResId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initView() {
        ImageView iv = findViewById(R.id.iv);
        Glide.with(mContext).load(R.drawable.pic_splash).apply(new RequestOptions().centerCrop()).into(iv);
    }

    @Override
    public void initData() {
        PermissionHelper.requestPermission(mActivity);
    }



}
