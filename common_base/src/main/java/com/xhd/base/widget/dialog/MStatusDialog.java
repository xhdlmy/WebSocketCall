package com.xhd.base.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xhd.base.R;
import com.xhd.base.util.DpUtils;
import com.xhd.base.widget.dialog.config.MDialogConfig;
import com.xhd.base.util.ScreenUtils;

/**
 * Created by maning on 2017/8/10.
 * 提示Dialog
 */

public class MStatusDialog {

    private Context mContext;
    private Handler mHandler = new Handler();
    public static final long DELAY_MILLIS = 2000L;
    private Dialog mDialog;
    private MDialogConfig mDialogConfig;

    private RelativeLayout dialog_window_background;
    private RelativeLayout dialog_view_bg;
    private ImageView imageStatus;
    private TextView tvShow;

    public MStatusDialog(Context context) {
        this(context, new MDialogConfig.Builder().build());
    }

    public MStatusDialog(Context context, MDialogConfig dialogConfig) {
        mContext = context;
        this.mDialogConfig = dialogConfig;
        if (mDialogConfig == null) {
            mDialogConfig = new MDialogConfig.Builder().build();
        }

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View mProgressDialogView = inflater.inflate(R.layout.mn_status_dialog_layout, null);// 得到加载view
        mDialog = new Dialog(mContext, R.style.MNCustomDialog);// 创建自定义样式dialog
        mDialog.setCancelable(false);// 不可以用“返回键”取消
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setContentView(mProgressDialogView);// 设置布局

        WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
        layoutParams.width = ScreenUtils.getScreenWidth(mContext);
        layoutParams.height = ScreenUtils.getScreenHeight(mContext);
        mDialog.getWindow().setAttributes(layoutParams);

        dialog_window_background = (RelativeLayout) mProgressDialogView.findViewById(R.id.dialog_window_background);
        dialog_view_bg = (RelativeLayout) mProgressDialogView.findViewById(R.id.dialog_view_bg);
        imageStatus = (ImageView) mProgressDialogView.findViewById(R.id.imageStatus);
        tvShow = (TextView) mProgressDialogView.findViewById(R.id.tvShow);

        configView();
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
        dialog_window_background.setBackgroundColor(mDialogConfig.backgroundWindowColor);
        tvShow.setTextColor(mDialogConfig.textColor);

        GradientDrawable myGrad = new GradientDrawable();
        myGrad.setColor(mDialogConfig.backgroundViewColor);
        myGrad.setStroke(DpUtils.dp2px(mContext, mDialogConfig.strokeWidth), mDialogConfig.strokeColor);
        myGrad.setCornerRadius(DpUtils.dp2px(mContext, mDialogConfig.cornerRadius));
        dialog_view_bg.setBackground(myGrad);
    }

    public void show(String msg, Drawable drawable) {
        show(msg, drawable, DELAY_MILLIS);
    }

    public void show(String msg, Drawable drawable, long delayMillis) {
        imageStatus.setImageDrawable(drawable);
        tvShow.setText(msg);
        mDialog.show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDialog.dismiss();
                mHandler.removeCallbacksAndMessages(null);
                if(mDialogConfig.onDialogDismissListener != null){
                    mDialogConfig.onDialogDismissListener.onDismiss();
                }
            }
        }, delayMillis);
    }
}
