package com.cl.cloud.util;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import com.cl.cloud.app.App;


public class SpUtils {

    private static SharedPreferences sSp;
    private static SharedPreferences.Editor sEditor;

    private static SpUtils instance;

    private SpUtils() {
        sSp = App.getSharedPreferences();
        sEditor = sSp.edit();
    }

    public synchronized static SpUtils getInstances() {
        if(instance == null){
            instance = new SpUtils();
        }
        return instance;
    }

    public void commit(){
        sEditor.commit();
    }

    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_PWD = "userPwd";


    public String getUserName() {
        return sSp.getString(KEY_USER_NAME, "");
    }

    public SpUtils putUserName(String userName){
        sEditor.putString(KEY_USER_NAME, userName);
        return instance;
    }

    public String getUserPwd() {
        return sSp.getString(KEY_USER_PWD, "");
    }

    public SpUtils putUserPwd(String userPwd){
        sEditor.putString(KEY_USER_PWD, userPwd);
        return instance;
    }

}
