package com.cl.cloud.util;

import com.cl.cloud.R;
import com.xhd.base.activity.BaseActivity;
import com.xhd.base.permission.PermissionListener;
import com.xhd.base.util.ToastUtils;
import com.xhd.base.util.manufacturer.PermissionsPageManager;

import java.util.List;

import static com.cl.cloud.app.Constant.PERMISSION_CALL_PHONE;
import static com.cl.cloud.app.Constant.PERMISSION_READ_PHONE_STATE;
import static com.cl.cloud.app.Constant.PERMISSION_SEND_SMS;

public class PermissionHelper {

    private static final String[] PERMISSIONS = {PERMISSION_READ_PHONE_STATE, PERMISSION_CALL_PHONE, PERMISSION_SEND_SMS};

    public static void requestPermission(BaseActivity activity){
        activity.requestPermission(PERMISSIONS, new PermissionListener() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(List<String> deniedPermission) {
                for (String permission : deniedPermission){
                    if(PERMISSION_READ_PHONE_STATE.equalsIgnoreCase(permission)){
                        ToastUtils.makeShort(activity, R.string.permission_read_phone_state);
                        PermissionsPageManager.getInstances().getIntent(activity);
                    }
                    if(PERMISSION_CALL_PHONE.equalsIgnoreCase(permission)){
                        ToastUtils.makeShort(activity, R.string.permission_call_phone);
                        PermissionsPageManager.getInstances().getIntent(activity);
                    }
                    if(PERMISSION_SEND_SMS.equalsIgnoreCase(permission)){
                        ToastUtils.makeShort(activity, R.string.permission_send_sms);
                        PermissionsPageManager.getInstances().getIntent(activity);
                    }
                }
            }
        });
    }

}
