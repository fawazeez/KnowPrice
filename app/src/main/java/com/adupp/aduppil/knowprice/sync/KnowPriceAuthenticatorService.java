package com.adupp.aduppil.knowprice.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by fawaz on 11/15/2016.
 */

public class KnowPriceAuthenticatorService extends Service {
    private KnowPriceAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
       mAuthenticator = new KnowPriceAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
