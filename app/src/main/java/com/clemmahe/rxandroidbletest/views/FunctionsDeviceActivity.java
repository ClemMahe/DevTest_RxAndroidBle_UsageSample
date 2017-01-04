package com.clemmahe.rxandroidbletest.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.clemmahe.rxandroidbletest.R;
import com.clemmahe.rxandroidbletest.utils.BleLogger;
import com.clemmahe.rxandroidbletest.utils.ByteUtils;
import com.clemmahe.rxandroidbletest.utils.ConstantsBleCharacteristics;
import com.clemmahe.rxandroidbletest.utils.ConstantsCommands;
import com.polidea.rxandroidble.RxBleConnection;
import com.polidea.rxandroidble.RxBleDevice;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.subjects.PublishSubject;

import static com.trello.rxlifecycle.android.ActivityEvent.PAUSE;

public class FunctionsDeviceActivity extends BaseActivity {

    @BindView(R.id.functions_textview_state)
    TextView functionsTextviewState;
    @BindView(R.id.functions_button_connect)
    Button functionsTextButtonConnect;
    @BindView(R.id.functions_button_disconnect)
    Button functionsTextButtonDisconnect;
    @BindView(R.id.functions_button_getsettings)
    Button functionsButtonGetSettings;


    private RxBleDevice device;
    private RxBleConnection connection;

    private PublishSubject<Void> disconnectTriggerSubject = PublishSubject.create();
    private Observable<RxBleConnection> connectionObservable;

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

                device.observeConnectionStateChanges()
                        .subscribe(
                                connectionState -> {
                                    handleConnectionState(connectionState);

                                    //Get settings
                                    connection.setupNotification(ConstantsBleCharacteristics.UUID_CHARACTERISTIC_READ)
                                            .doOnNext(notificationObservable -> {
                                                //Read
                                            })
                                            .flatMap(notificationObservable -> notificationObservable) // <-- Notification has been set up, now observe value changes.
                                            .subscribe(
                                                    bytes -> {
                                                        //handleNotification();
                                                        String hexByte = ByteUtils.byteArrayToHexString(bytes, true);
                                                        BleLogger.logData("GetSettings : " + hexByte);
                                                    },
                                                    throwable -> {
                                                        //Handle notification
                                                    }
                                            );
                                },
                                throwable -> {
                                    handleSnackMessage("Error state connection : " + throwable.getMessage());
                                }
                        );
            }
        }
    }

    @OnClick({R.id.functions_button_connect, R.id.functions_button_disconnect, R.id.functions_button_getsettings})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.functions_button_connect:

                if (device != null && !isConnected()) {

                    //Prepare Connection
                    connectionObservable = device.establishConnection(getApplicationContext(), false)
                            .takeUntil(disconnectTriggerSubject)
                            .compose(bindUntilEvent(PAUSE))
                            .doOnUnsubscribe(this::clearSubscription);

                    //Connection & Services
                    connectionObservable.subscribe(rxBleConnection -> {
                        connection = rxBleConnection;
                        connection.discoverServices().subscribe(rxBleDeviceServices -> {
                            handleSnackMessage("Service discovered");
                        }, throwable -> {
                            handleSnackMessage("Cannot discover services: " + throwable.toString());
                        });
                    }, throwable -> {
                        //Error
                        handleSnackMessage("Cannot connect: " + throwable.toString());
                    });


                }
                break;

            case R.id.functions_button_disconnect:

                if (device != null && isConnected()) {
                    triggerDisconnect();
                }
                break;

            case R.id.functions_button_getsettings:

                if (isConnected()) {

                    String commandString = ConstantsCommands.COMMAND_GET_SETTINGS;
                    byte[] arrayGetSettings = ByteUtils.hexStringToByteArray(commandString);

                    connection.writeCharacteristic(ConstantsBleCharacteristics.UUID_CHARACTERISTIC_WRITE, arrayGetSettings)
                            .subscribe(bytes -> {
                                String hexByte = ByteUtils.byteArrayToHexString(bytes, true);
                                BleLogger.logData("GetSettings Data written : " + hexByte);
                            },throwable ->  {
                                BleLogger.logData("GetSettings ErrorWrite : " + throwable.toString());
                            });
                }
                break;

        }
    }

    private void clearSubscription() {
        //Disconnected
    }

    private void triggerDisconnect() {
        disconnectTriggerSubject.onNext(null);
    }

    private boolean isConnected() {
        return device.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;
    }

    /**
     * Handle connection state
     * @param connectionState RxBleConnectionState
     */
    private void handleConnectionState(RxBleConnection.RxBleConnectionState connectionState) {
        if (connectionState == RxBleConnection.RxBleConnectionState.DISCONNECTED) {
            connection = null;
        }
        runOnUiThread(() -> functionsTextviewState.setText(connectionState.toString()));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void appBluetoothReady(boolean ready, int status) {
        if(!ready){
            handleSnackMessage("Bluetooth not ready");
        }
    }

}
