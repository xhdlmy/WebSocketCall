package com.xhd.base.util.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.HONEYCOMB_MR1;

/**
 * 图片
 *  内存 Bitmap
 *  网络 Byte Stream
 *  硬盘 File
 *
 * Bitmap
 *  load        根据 ImageView w h 计算 inSampleSize 防止加载 OOM
 *  compress    上传服务器 质量压缩
 *  lru cache  Glide Picasso 都实现了
 */
public class BitmapUtils {

    private static final String TAG = BitmapUtils.class.getSimpleName();

    // 获取缩小尺寸后的 Bitmap，防止加载过大的 Bitmap 出现 OOM
    public static Bitmap getScaleBitmap(BitmapDecode decode, double reqWidth, double reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        decode.decode(options); // 目的 获取 bitmap w h 与 reqW reqH 对比，计算采样率
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return decode.decode(options);
    }

    // 计算采样率(reqWidth reqHeight 为 ImageView 的宽高)
    private static int calculateInSampleSize(BitmapFactory.Options options, double reqWidth, double reqHeight) {
        int inSampleSize = 1;
        final int width = options.outWidth;
        final int height = options.outHeight;
        if(width > reqWidth || height > reqHeight){
            final int halfWidth = width / 2;
            final int halfHeight = height / 2;
            if(reqWidth == 0 || reqHeight == 0) return inSampleSize;
            while (( halfWidth / inSampleSize ) >= reqWidth
                    && ( halfHeight / inSampleSize ) >= reqHeight){
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * 质量压缩 Bitmap -> File File 变小了，但如果加载到内存中 Bitmap 占用内存不会变小，因为 像素大小不变
     * 质量压缩一般用于将图片上传至服务器
     * 但是如果是 PNG 图片，质量压缩无效；此时只能使用尺寸压缩 （一般尺寸压缩用来生成缩略图）
     */
    private static byte[] compress(Bitmap bitmap, Bitmap.CompressFormat format, double uploadLimitBytes) {
        int quality = 100;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, quality, baos); // 将原始 bitmap 写入 baos
        if(baos.toByteArray().length > uploadLimitBytes){
            if(format != Bitmap.CompressFormat.PNG){
                // 质量压缩
                while (baos.toByteArray().length > uploadLimitBytes) {
                    baos.reset();
                    quality -= 20; // 重新质量压缩 策略：20% 递减
                    bitmap.compress(format, quality, baos);
                }
            }else{
                BitmapDecodeBytes decode = new BitmapDecodeBytes(baos.toByteArray());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inSampleSize = 1;
                while (baos.toByteArray().length > uploadLimitBytes) {
                    // 尺寸压缩 (无法质量压缩)
                    options.inSampleSize *= 2;
                    bitmap = decode.decode(options);
                    baos.reset();
                    bitmap.compress(format, 100, baos);
                }
            }
        }
        return baos.toByteArray();
    }

    /**
     * Bitmap 转 File (阻塞方法)  上传大 Bitmap 时先质量压缩
     */
    public static File writeToFile(@NonNull Bitmap bitmap, @NonNull File file, double uploadLimitBytes) throws IOException {
        return writeToFile(bitmap, file, Bitmap.CompressFormat.JPEG, uploadLimitBytes);
    }

    public static File writeToFile(@NonNull Bitmap bitmap, @NonNull File file, Bitmap.CompressFormat format, double uploadLimitBytes) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        byte[] bytes = compress(bitmap, format, uploadLimitBytes);
        bos.write(bytes);
        bos.flush();
        bos.close();
        return file;
    }

    /**
     * 计算 Bitmap 所占内存大小 bytes 字节数
     */
    public static int getBitmapBytes(@NonNull Bitmap bitmap) {
        int result;
        if (SDK_INT >= HONEYCOMB_MR1) {
            result = bitmap.getByteCount();
        } else {
            result = bitmap.getRowBytes() * bitmap.getHeight();
        }
        if (result <= 0) {
            throw new IllegalStateException("Bitmap size can't <= 0");
        }
        return result;
    }

    // 计算 Bitmap 所占内存大小 单位 M
    public static String getBitmapMemorySize(@NonNull Bitmap bitmap){
        double size = getBitmapBytes(bitmap) / 1024 / 1024;
        return new DecimalFormat("0.00").format(size)  + "M";
    }

    /**
     * Drawable 转 Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 将 Activity ViewTree 生成 Bitmap
     * @param viewGroup 子View 无 MapView
     */
    public static Bitmap generatePagerBitmap(ViewGroup viewGroup) {
        // 确定 bitmap 高度
        int h = 0;
        Bitmap bitmap;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            h += childAt.getHeight();
        }
        // 创建相应大小的bitmap
        bitmap = Bitmap.createBitmap(viewGroup.getMeasuredWidth(), h, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        // 获取当前主题背景颜色，设置canvas背景
        canvas.drawColor(Color.WHITE);
        // 绘制viewGroup内容
        viewGroup.draw(canvas);
        return bitmap;
    }

    /**
     * 将 Activity ViewTree （MapView） 生成 Bitmap
     * @param viewContainer MapView
     */
//    public static Bitmap generatePagerBitmap(ViewGroup viewContainer, MapView mapView){
//        // 确定 bitmap 高度
//        Bitmap bm;
//        // 创建相应大小的bitmap
//        bm = Bitmap.createBitmap(viewContainer.getMeasuredWidth(), viewContainer.getHeight() - mapView.getHeight(), Bitmap.Config.ARGB_4444);
//        Canvas canvas = new Canvas(bm);
//        canvas.drawColor(Color.WHITE); // 画背景为 white
//        boolean isAfterMapView = false;
//        for (int i = 0; i < viewContainer.getChildCount(); i++) {
//            View view = viewContainer.getChildAt(i);
//            if(view.getVisibility() == View.VISIBLE){
//                // 关键就是 MapView 要是 viewContainer 的直接子 View
//                if(view instanceof MapView){
////                    canvas.drawBitmap(baiduBitmap, mapView.getLeft(), mapView.getTop(), null); // 如果需要显示百度地图的话
//                    isAfterMapView = true;
//                }else{
//                    // view.getDrawingCache() 返回 null, 那么使用另一个Canvas来画Bitmap
//                    Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_4444);
//                    Canvas c = new Canvas(bitmap);
//                    view.draw(c);
//                    if(isAfterMapView){
//                        canvas.drawBitmap(bitmap, view.getLeft(), view.getTop() - mapView.getHeight(), null);
//                    }else{
//                        canvas.drawBitmap(bitmap, view.getLeft(), view.getTop(), null);
//                    }
//                }
//            }
//        }
//        return bm;
//    }

}
