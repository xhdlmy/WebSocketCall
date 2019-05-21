package com.xhd.base.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.xhd.base.app.ActivityStackManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Android 6.0 以上运行时权限，并不适用国产 6.0 以下手机
 */
public class PermissionActivity extends AppCompatActivity {

	private static final int PERMISSION_REQUEST_CODE = 1;
	protected PermissionListener mListener;

	public void requestPermission(String[] permissions, PermissionListener listener) {
		
		Activity topActivity = ActivityStackManager.getInstance().getTopActivity();
		if (topActivity == null) {
			return;
		}

		mListener = listener;

		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			mListener.onGranted();
			return;
		}
		
		List<String> permissionList = new ArrayList<>();
		for (String permission : permissions) {
			if (ContextCompat.checkSelfPermission(topActivity, permission) != PackageManager.PERMISSION_GRANTED) {
				permissionList.add(permission);
			}
		}
		if (!permissionList.isEmpty()) {
			ActivityCompat.requestPermissions(topActivity,
					permissionList.toArray(new String[permissionList.size()]),
					PERMISSION_REQUEST_CODE);
		} else {
			mListener.onGranted();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case PERMISSION_REQUEST_CODE:
				if (grantResults.length > 0) {
					List<String> deniedPermission = new ArrayList<>();
					for (int i = 0; i < grantResults.length; i++) {
						int grantResult = grantResults[i];
						String permission = permissions[i];
						if (grantResult != PackageManager.PERMISSION_GRANTED) {
							deniedPermission.add(permission);
						}
					}
					if (deniedPermission.isEmpty()) {
						mListener.onGranted();
					} else {
						mListener.onDenied(deniedPermission);
					}
				}
			default:
				break;
		}
	}
}
