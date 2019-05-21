package com.xhd.base.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xhd.base.R;
import com.xhd.base.activity.BaseActivity;
import com.xhd.base.util.DpUtils;
import com.xhd.base.util.ScreenUtils;
import com.xhd.base.widget.MProgressWheel;
import com.xhd.base.widget.dialog.config.MDialogConfig;


/**
 * 网络加载框架的加载进度框
 */
public class MProgressDialog {

    private Context mContext;
    private Dialog mDialog;
    private MDialogConfig mDialogConfig;

    //布局
    private RelativeLayout dialog_window_background;
    private RelativeLayout dialog_view_bg;
    private MProgressWheel progress_wheel;
    private TextView tv_show;

    public MProgressDialog(Context context) {
        this(context, new MDialogConfig.Builder().build());
    }

    public MProgressDialog(Context context, MDialogConfig dialogConfig) {
        this.mContext = context;
        this.mDialogConfig = dialogConfig;
        if (mDialogConfig == null) {
            mDialogConfig = new MDialogConfig.Builder().build();
        }

        View mProgressDialogView = LayoutInflater.from(context)
                .inflate(R.layout.mn_progress_dialog_layout, null);
        mDialog = new Dialog(context, R.style.MNCustomDialog);
        mDialog.setContentView(mProgressDialogView);

        WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
        layoutParams.width = ScreenUtils.getScreenWidth(context);
        layoutParams.height = ScreenUtils.getScreenHeight(context);
        mDialog.getWindow().setAttributes(layoutParams);

        dialog_window_background = (RelativeLayout) mProgressDialogView.findViewById(R.id.dialog_window_background);
        dialog_view_bg = (RelativeLayout) mProgressDialogView.findViewById(R.id.dialog_view_bg);
        progress_wheel = (MProgressWheel) mProgressDialogView.findViewById(R.id.progress_wheel);
        tv_show = (TextView) mProgressDialogView.findViewById(R.id.tv_show);
        progress_wheel.spin();

        configView();

        if(context instanceof BaseActivity){
            BaseActivity activity = (BaseActivity) context;
            activity.setMProgressDialog(mDialog);
        }
    }

    private void configView() {
        mDialog.setCancelable(mDialogConfig.canceledOnBackKeyPressed);
        if(mDialogConfig.canceledOnBackKeyPressed
                && mDialogConfig.onDialogCancelListener != null) {
            mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mDialogConfig.onDialogCancelListener.onCancel(dialog);
                }
            });
        }
        mDialog.setCanceledOnTouchOutside(mDialogConfig.canceledOnTouchOutside);
        dialog_window_background.setBackgroundColor(mDialogConfig.backgroundWindowColor);
        dialog_window_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogConfig != null && mDialogConfig.canceledOnTouchOutside) {
                    dismissProgress();
                }
            }
        });

        GradientDrawable myGrad = new GradientDrawable();
        myGrad.setColor(mDialogConfig.backgroundViewColor);
        myGrad.setStroke(DpUtils.dp2px(mContext, mDialogConfig.strokeWidth), mDialogConfig.strokeColor);
        myGrad.setCornerRadius(DpUtils.dp2px(mContext, mDialogConfig.cornerRadius));
        tv_show.setTextColor(mDialogConfig.textColor);
        dialog_view_bg.setBackground(myGrad);

        progress_wheel.setBarColor(mDialogConfig.progressColor);
        progress_wheel.setBarWidth(DpUtils.dp2px(mContext, mDialogConfig.progressWidth));
        progress_wheel.setRimColor(mDialogConfig.progressRimColor);
        progress_wheel.setRimWidth(mDialogConfig.progressRimWidth);

    }

    public void showProgress() {
        showProgress(mContext.getString(R.string.loading));
    }

    public void showProgress(String msg) {
        if (mContext != null && mDialog != null && !mDialog.isShowing()) {
            if (TextUtils.isEmpty(msg)) {
                tv_show.setVisibility(View.GONE);
            } else {
                tv_show.setVisibility(View.VISIBLE);
                tv_show.setText(msg);
            }
            mDialog.show();
        }
    }

    public void dismissProgress() {
        if (mContext != null && mDialog != null && mDialog.isShowing()) {
            // 消失监听
            if(mDialogConfig.onDialogDismissListener != null){
                mDialogConfig.onDialogDismissListener.onDismiss();
            }
            // 处理一定几率会崩溃
            try {
                Context context = ((ContextWrapper) mDialog.getContext()).getBaseContext();
                if(context instanceof Activity) {
                    if(!((Activity)context).isFinishing()){
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            if(!((Activity) context).isDestroyed()){
                                mDialog.dismiss();
                            }
                        }else{
                            mDialog.dismiss();
                        }
                    }
                } else {
                    mDialog.dismiss();
                }
            } catch (IllegalArgumentException e) {
            }

        }
    }

    public boolean isShowing() {
        if (mContext != null && mDialog != null) {
            return mDialog.isShowing();
        }
        return false;
    }

}
