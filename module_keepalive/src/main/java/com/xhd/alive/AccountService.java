package com.xhd.alive;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Android 8.0 自动同步功能已经关闭，需要点击进行手动同步
 */

public class AccountService extends Service {

    public static String ACCOUNT_TYPE;
    public static String ACCOUNT_PROVIDER;
    public static final long SECOND_30 = 30;

    private Account mAccount;

    private void initConstant(Context context){
        String packageName = "com.cl.clound";
        ACCOUNT_TYPE = packageName + ".account.type";
        ACCOUNT_PROVIDER = packageName + ".account.provider";
    }

    private boolean addAccount(){
        AccountManager am = (AccountManager) this.getSystemService(Context.ACCOUNT_SERVICE);
        assert am != null;
        Account[] accounts = am.getAccountsByType(ACCOUNT_TYPE);
        if(accounts.length > 0){
            mAccount = accounts[0];
        }else{
            mAccount = new Account(this.getString(R.string.app_name), ACCOUNT_TYPE);
        }
        return am.addAccountExplicitly(mAccount, null, null);
    }

    private void syncAccount(Bundle bundle){
        if(addAccount()){
            ContentResolver.setIsSyncable(mAccount, ACCOUNT_PROVIDER, 1);
            ContentResolver.setSyncAutomatically(mAccount, ACCOUNT_PROVIDER,true);
            ContentResolver.addPeriodicSync(mAccount, ACCOUNT_PROVIDER, bundle, SECOND_30);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initConstant(this);
        syncAccount(new Bundle());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
