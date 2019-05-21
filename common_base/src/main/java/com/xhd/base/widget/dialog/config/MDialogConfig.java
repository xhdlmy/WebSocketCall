package com.xhd.base.widget.dialog.config;

import android.graphics.Color;

/**
 * Created by maning on 2017/8/11.
 */

public class MDialogConfig {

    public boolean canceledOnBackKeyPressed = false; // 回退可以取消
    public boolean canceledOnTouchOutside = false; // 点击外部可以取消
    public OnDialogCancelListener onDialogCancelListener; // 取消的监听
    public OnDialogDismissListener onDialogDismissListener; // 消失的监听

    public int backgroundWindowColor = Color.TRANSPARENT; // 弹窗背景色
    public int backgroundViewColor = Color.parseColor("#b2000000"); // View 背景色
    public int strokeColor = Color.TRANSPARENT; // View 边框的颜色
    public float strokeWidth = 0; // View 边框的宽度
    public float cornerRadius = 8; // View 背景圆角
    public int textColor = Color.WHITE; // 文字颜色

    public int progressColor = Color.WHITE; // 进度环颜色
    public float progressWidth = 2; // Progress 进度环宽度
    public int progressRimColor = Color.TRANSPARENT; // 背景环 颜色
    public int progressRimWidth = 0; // 背景环 宽度

    private MDialogConfig() {
    }

    /**
     * 构建者
     */
    public static class Builder {
        
        private MDialogConfig mConfig;

        public Builder() {
            mConfig = new MDialogConfig();
        }

        public MDialogConfig build() {
            return mConfig;
        }

        public MDialogConfig.Builder setCanceledOnBackKeyPressed (boolean canceledOnBackKeyPressed) {
            mConfig.canceledOnBackKeyPressed = canceledOnBackKeyPressed;
            return this;
        }

        public MDialogConfig.Builder setOnDialogCancelListener(OnDialogCancelListener onDialogCancelListener) {
            mConfig.onDialogCancelListener = onDialogCancelListener;
            return this;
        }

        public MDialogConfig.Builder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            mConfig.canceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public MDialogConfig.Builder setOnDialogDismissListener(OnDialogDismissListener onDialogDismissListener) {
            mConfig.onDialogDismissListener = onDialogDismissListener;
            return this;
        }

        public MDialogConfig.Builder setBackgroundWindowColor(int backgroundWindowColor) {
            mConfig.backgroundWindowColor = backgroundWindowColor;
            return this;
        }

        public MDialogConfig.Builder setBackgroundViewColor(int backgroundViewColor) {
            mConfig.backgroundViewColor = backgroundViewColor;
            return this;
        }

        public MDialogConfig.Builder setStrokeColor(int strokeColor) {
            mConfig.strokeColor = strokeColor;
            return this;
        }

        public MDialogConfig.Builder setStrokeWidth(float strokeWidth) {
            mConfig.strokeWidth = strokeWidth;
            return this;
        }

        public MDialogConfig.Builder setCornerRadius(float cornerRadius) {
            mConfig.cornerRadius = cornerRadius;
            return this;
        }

        public MDialogConfig.Builder setTextColor(int textColor) {
            mConfig.textColor = textColor;
            return this;
        }

        public MDialogConfig.Builder setProgressColor(int progressColor) {
            mConfig.progressColor = progressColor;
            return this;
        }

        public MDialogConfig.Builder setProgressWidth(float progressWidth) {
            mConfig.progressWidth = progressWidth;
            return this;
        }

        public MDialogConfig.Builder setProgressRimColor(int progressRimColor) {
            mConfig.progressRimColor = progressRimColor;
            return this;
        }

        public MDialogConfig.Builder setProgressRimWidth(int progressRimWidth) {
            mConfig.progressRimWidth = progressRimWidth;
            return this;
        }

    }

}
