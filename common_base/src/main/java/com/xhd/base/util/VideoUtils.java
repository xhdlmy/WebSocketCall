package com.xhd.base.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VideoUtils {

    private static final String TAG = "VideoUtils";
    private static VideoUtils instance = null;

    private Context mContext;
    private Camera mCamera;
    // 相机默认宽高（像素），相机的宽度和高度跟屏幕坐标相反（xy互换）
    public static final int DEFAULT_WIDTH = 1280;
    public static final int DEFAULT_HEIGHT = 720;
    // FPS 每秒传输的帧数
    public static final int DESIRED_PREVIEW_FPS = 30;
    // 摄像头ID 默认后置摄像头
    private int mCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    // 拍摄方向
    private int mOrientation;
    // 是否对焦
    private boolean mIsFocus;

    private VideoUtils(Context context) {
        mContext = context;
    }

    public static final VideoUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (VideoUtils.class) {
                if (instance == null) {
                    instance = new VideoUtils(context);
                }
            }
        }
        return instance;
    }

    // 检测硬件
    public boolean checkHardware() {
        if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    // 打开前置摄像头
    /*public void openFrontCamera(Activity activity, int width, int height) {
        openFrontCamera(activity, width, height, DESIRED_PREVIEW_FPS);
    }*/

    public void openFrontCamera(Activity activity, int width, int height) {
        if (mCamera != null) {
            throw new RuntimeException("camera already initialized!");
        }
        Camera.CameraInfo info = new Camera.CameraInfo();
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mCamera = Camera.open(i);
                mCameraID = info.facing;
                break;
            }
        }
        // 如果没有前置摄像头，则打开默认的后置摄像头
        if (mCamera == null) {
            mCamera = Camera.open();
            mCameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        // 没有摄像头时，抛出异常
        if (mCamera == null) {
            throw new RuntimeException("Unable to open camera");
        }
        // 设置参数
        setPreviewSize(mCamera, width, height);
        setPreviewOrientation(activity);
    }

    // 设置 SurfaceHolder 并打开预览界面
    public void startPreviewDisplay(SurfaceHolder holder) {
        if (mCamera == null) {
            throw new IllegalStateException("Camera must be set when start preview");
        }
        try {
            // 摄像头画面显示在Surface上
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 停止预览界面
    public void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }
    
    // 自动对焦
    public void autoFocus(final FocusCallback focusCallback){
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                mIsFocus = success;
                if (success) {
                    mCamera.cancelAutoFocus();
                    focusCallback.onFocus();
                }else{
                    focusCallback.onUnFocus();
                }
            }
        });
    }

    // 拍照
    public void takePhoto(Camera.ShutterCallback shutterCallback,
                          Camera.PictureCallback rawCallback,
                          Camera.PictureCallback pictureCallback) {
        if (mCamera != null) {
            mCamera.takePicture(shutterCallback, rawCallback, pictureCallback);
        }
    }

    public void autoFocus(final CameraUtils.FocusCallback focusCallback){
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                mIsFocus = success;
                if (success) {
                    mCamera.cancelAutoFocus();
                    focusCallback.onFocus();
                }else{
                    focusCallback.onUnFocus();
                }
            }
        });
    }

    // 释放相机
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void lockCamera(){
        if(mCamera != null){
            mCamera.lock();
        }
    }

    public void unlockCamera(){
        if(mCamera != null){
            mCamera.unlock();
        }
    }

    // 设置相机预览界面像素大小
    public void setPreviewSize(Camera camera, int expectWidth, int expectHeight) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = calcSize(parameters.getSupportedPreviewSizes(),
                expectWidth, expectHeight);
        parameters.setPreviewSize(size.width, size.height);
        camera.setParameters(parameters);
    }

    // 获取相机预览界面像素大小
    public Camera.Size getPreviewSize() {
        if (mCamera != null) {
            return mCamera.getParameters().getPreviewSize();
        }
        return null;
    }

    // 计算 Size 既宽高尺寸 Size(int w, int h)
    public Camera.Size calcSize(List<Camera.Size> sizes, int expectWidth, int expectHeight) {
        sortList(sizes); // 根据宽度进行排序
        Camera.Size result = sizes.get(0);
        boolean widthOrHeight = false; // 判断存在宽或高相等的Size
        // 辗转计算宽高最接近的值
        for (Camera.Size size : sizes) {
            // 如果宽高相等，则直接返回
            if (size.width == expectWidth && size.height == expectHeight) {
                result = size;
                break;
            }
            // 仅仅是宽度相等，计算高度最接近的size
            if (size.width == expectWidth) {
                widthOrHeight = true;
                if (Math.abs(result.height - expectHeight)
                        > Math.abs(size.height - expectHeight)) {
                    result = size;
                }
            }
            // 高度相等，则计算宽度最接近的Size
            else if (size.height == expectHeight) {
                widthOrHeight = true;
                if (Math.abs(result.width - expectWidth)
                        > Math.abs(size.width - expectWidth)) {
                    result = size;
                }
            }
            // 如果之前的查找不存在宽或高相等的情况，则计算宽度和高度都最接近的期望值的Size
            else if (!widthOrHeight) {
                if (Math.abs(result.width - expectWidth)
                        > Math.abs(size.width - expectWidth)
                        && Math.abs(result.height - expectHeight)
                        > Math.abs(size.height - expectHeight)) {
                    result = size;
                }
            }
        }
        return result;
    }

    // 排序
    private void sortList(List<Camera.Size> list) {
        Collections.sort(list, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size pre, Camera.Size after) {
                if (pre.width > after.width) {
                    return 1;
                } else if (pre.width < after.width) {
                    return -1;
                }
                return 0;
            }
        });
    }

    // 设置预览方向 （重要，显示图片，保证方向正确）
    public void setPreviewOrientation(Activity activity) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraID, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        LogUtils.i(TAG, "rotation:" + rotation);
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        LogUtils.i(TAG, "Camera Orientation:" + info.orientation);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        mOrientation = result;
        LogUtils.i(TAG, "Orientation:" + mOrientation);
        mCamera.setDisplayOrientation(mOrientation);
    }

    public Camera getCamera() {
        return mCamera;
    }

    public int getCameraID() {
        return mCameraID;
    }

    public int getPreviewOrientation() {
        return mOrientation;
    }

    public interface FocusCallback {
        void onFocus();
        void onUnFocus();
    }

}
