package com.clemmahe.rxandroidbletest.views;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.clemmahe.rxandroidbletest.RxTestApplication;
import com.polidea.rxandroidble.RxBleClient;

/**
 * Created by CMA10935 on 03/01/2017.
 */

public class BaseActivity extends AppCompatActivity {



    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    protected RxBleClient getAppClient(){
        return ((RxTestApplication)getApplication()).getRxBleClient();
    }

    /**
     * Handle error on UiThread
     * @param message String
     */
    protected void handleErrorOnUi(String message){
        runOnUiThread(() -> Snackbar.make(findViewById(android.R.id.content), "Error: " + message, Snackbar.LENGTH_SHORT).show());
    }

}
