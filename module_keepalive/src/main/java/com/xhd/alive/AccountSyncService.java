package com.xhd.alive;

import android.accounts.Account;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by work2 on 2019/4/25.
 */

public class AccountSyncService extends Service {

    private SyncAdapter mSyncAdapter;

    @Override
    public void onCreate() {
        synchronized (AccountSyncService.class) {
            if(mSyncAdapter == null) {
                mSyncAdapter = new SyncAdapter(this, true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mSyncAdapter.getSyncAdapterBinder();
    }

    class SyncAdapter extends AbstractThreadedSyncAdapter {

        public SyncAdapter(Context context, boolean autoInitialize) {
            super(context, autoInitialize);
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
            // 在这里实现数据的同步（但我们不同步具体数据）
        }
    }
    
}
