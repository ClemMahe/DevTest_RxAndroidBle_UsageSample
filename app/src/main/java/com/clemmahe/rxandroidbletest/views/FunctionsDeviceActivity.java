package com.clemmahe.rxandroidbletest.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.clemmahe.rxandroidbletest.R;
import com.clemmahe.rxandroidbletest.utils.ByteUtils;
import com.clemmahe.rxandroidbletest.utils.ConstantsBleCharacteristics;
import com.clemmahe.rxandroidbletest.utils.ConstantsCommands;
import com.polidea.rxandroidble.RxBleConnection;
import com.polidea.rxandroidble.RxBleDevice;
import com.polidea.rxandroidble.RxBleDeviceServices;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

public class FunctionsDeviceActivity extends BaseActivity {

    @BindView(R.id.functions_button_connect)
    Button functionsTextButtonConnect;
    @BindView(R.id.functions_button_disconnect)
    Button functionsTextButtonDisconnect;
    @BindView(R.id.functions_button_getsettings)
    Button functionsButtonGetSettings;
    @BindView(R.id.functions_textview_state)
    TextView functionsTextviewState;


    private RxBleDevice device;
    private RxBleConnection connection;
    private RxBleDeviceServices services;

    private Subscription connectionSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functions_device);
        ButterKnife.bind(this);

        checkBundle(getIntent().getExtras());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkBundle(intent.getExtras());
    }

    private void checkBundle(Bundle bdl) {
        if (bdl != null) {
            String macAddress = bdl.getString(ListDevicesActivity.EXTRA_RXBLEDEVICE);
            if (macAddress != null) {
                device = getAppClient().getBleDevice(macAddress);
            }
        }
    }

    @OnClick({R.id.functions_button_connect, R.id.functions_button_disconnect, R.id.functions_button_getsettings})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.functions_button_connect:
                if (device != null) {

                    device.observeConnectionStateChanges()
                            .subscribe(
                                    connectionState -> {
                                        handleConnectionState(connectionState);
                                    },
                                    throwable -> {
                                        handleErrorOnUi("Error state connection : " + throwable.getMessage());
                                    }
                            );

                    //Connection
                    connectionSubscription = device.establishConnection(getApplicationContext(), false) // <-- autoConnect flag
                        .subscribe(
                            rxBleConnection -> {
                                // All GATT operations are done through the rxBleConnection.
                                connection = rxBleConnection;
                            },
                            throwable -> {
                                handleErrorOnUi("Error connection : " + throwable.getMessage());
                            }
                        );
                }
                break;
            case R.id.functions_button_disconnect:
                if (device != null) {
                    connectionSubscription.unsubscribe();
                }
                break;
            case R.id.functions_button_getsettings:
                if(connection!=null){

                    String commandString = ConstantsCommands.COMMAND_GET_SETTINGS;
                    byte[] arrayGetSettings = ByteUtils.hexStringToByteArray(commandString);

                    device.establishConnection(getApplicationContext(), false)
                            .flatMap(rxBleConnection -> rxBleConnection.readCharacteristic(ConstantsBleCharacteristics.UUID_CHARACTERISTIC_READ)
                                    .doOnNext(bytes -> {
                                        // Process read data.
                                        handleErrorOnUi("READ DATA");
                                    })
                                    .flatMap(bytes -> rxBleConnection.writeCharacteristic(ConstantsBleCharacteristics.UUID_CHARACTERISTIC_WRITE, arrayGetSettings)))
                            .subscribe(
                                    writeBytes -> {
                                        // Written data.
                                        handleErrorOnUi("WRITTEN DATA");
                                    },
                                    throwable -> {
                                        // Handle an error here.
                                        handleErrorOnUi("ERROR DATA: "+throwable.getMessage());
                                    }
                            );
                }
                break;
        }
    }

    /**
     * Handle connection state
     *
     * @param connectionState RxBleConnectionState
     */
    private void handleConnectionState(RxBleConnection.RxBleConnectionState connectionState) {
        if(connectionState== RxBleConnection.RxBleConnectionState.DISCONNECTED){
            connection = null;
        }
        runOnUiThread(() -> functionsTextviewState.setText(connectionState.toString()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(connectionSubscription!=null && !connectionSubscription.isUnsubscribed()) {
            connectionSubscription.unsubscribe();
        }
    }


}
