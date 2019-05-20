package com.cl.cloud.websocket;

import com.cl.cloud.app.Constant;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by work2 on 2019/5/5.
 */

public class OkClient {

    private static OkClient instance;

    private OkHttpClient mOkHttpClient;

    private OkClient(){
        mOkHttpClient = new OkHttpClient.Builder()
                        // pingInterval != 0 则会发送心跳检测包
                        .pingInterval(Constant.PING_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
                        .build();
    }

    public static OkClient getInstance(){
        if(instance == null){
            synchronized (OkClient.class){
                if(instance == null) instance = new OkClient();
            }
        }
        return instance;
    }

    public OkHttpClient getOkHttpClient(){
        return mOkHttpClient;
    }

}
