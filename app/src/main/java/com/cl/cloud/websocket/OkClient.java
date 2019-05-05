package com.cl.cloud.websocket;

import okhttp3.OkHttpClient;

/**
 * Created by work2 on 2019/5/5.
 */

public class OkClient {

    private static OkClient instance;

    private OkHttpClient mOkHttpClient;

    private OkClient(){
        mOkHttpClient = new OkHttpClient();
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
