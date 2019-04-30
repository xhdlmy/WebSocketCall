package com.xhd.base.util.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by computer on 2018/5/29.
 */

public class BitmapDecodeBytes implements BitmapDecode {

    private byte[] data;

    public BitmapDecodeBytes(byte[] data) {
        this.data = data;
    }

    @Override
    public Bitmap decode(BitmapFactory.Options options) {
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

}
