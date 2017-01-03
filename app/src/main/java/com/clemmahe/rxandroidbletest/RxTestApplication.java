package com.clemmahe.rxandroidbletest;

import android.app.Application;

import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.internal.RxBleLog;

/**
 * Created by CMA10935 on 03/01/2017.
 */

public class RxTestApplication extends Application {

    private RxBleClient rxBleClient;

    @Override
    public void onCreate() {
        super.onCreate();
        rxBleClient= RxBleClient.create(getApplicationContext());
        rxBleClient.setLogLevel(RxBleLog.DEBUG);
    }

    public RxBleClient getRxBleClient() {
        return rxBleClient;
    }

}
