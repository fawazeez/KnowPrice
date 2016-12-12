package com.adupp.aduppil.knowprice.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by fawaz on 11/14/2016.
 */

public class KnowPriceSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static KnowPriceSyncAdapter sKnowPriceSyncAdapter= null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sKnowPriceSyncAdapter == null) {
                sKnowPriceSyncAdapter = new KnowPriceSyncAdapter(getApplicationContext(), true);
            }
        }
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sKnowPriceSyncAdapter.getSyncAdapterBinder();
    }
}
