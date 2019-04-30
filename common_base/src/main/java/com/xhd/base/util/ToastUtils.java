package com.xhd.base.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

public final class ToastUtils {

	public static void makeShort(@NonNull Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	
	public static void makeShort(@NonNull Context context, int resId) {
		Toast.makeText(context, context.getResources().getString(resId), Toast.LENGTH_SHORT).show();
	}
	
	public static void makeLong(@NonNull Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
	
	public static void makeLong(@NonNull Context context, int resId) {
		Toast.makeText(context, context.getResources().getString(resId), Toast.LENGTH_LONG).show();
	}
	
}