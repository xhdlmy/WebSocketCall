package com.xhd.base.permission;

import java.util.List;

public interface PermissionListener {
    void onGranted();
    void onDenied(List<String> deniedPermission);
}
