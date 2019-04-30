package com.xhd.base.util.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;

public class BitmapDecodeRes implements BitmapDecode {

    private Context context;

    @DrawableRes
    private int resId;

    public BitmapDecodeRes(Context context, int resId) {
        this.context = context;
        this.resId = resId;
    }

    @Override
    public Bitmap decode(BitmapFactory.Options options) {
        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }

}
