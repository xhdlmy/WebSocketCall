package com.xhd.base.util.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapDecodeFile implements BitmapDecode {

    private String filePath;

    public BitmapDecodeFile(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Bitmap decode(BitmapFactory.Options options) {
        return BitmapFactory.decodeFile(filePath, options);
    }

}
