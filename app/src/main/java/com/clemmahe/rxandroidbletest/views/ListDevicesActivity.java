package com.clemmahe.rxandroidbletest.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.clemmahe.rxandroidbletest.R;
import com.clemmahe.rxandroidbletest.utils.BleLogger;
import com.polidea.rxandroidble.RxBleDevice;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

public class ListDevicesActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    public static final String EXTRA_RXBLEDEVICE = "EXTRA_RXBLEDEVICE";

    @BindView(R.id.listdevices_listview)
    ListView listdevicesListview;
    @BindView(R.id.activity_list_device)
    RelativeLayout activityListDevice;

    private ArrayList<RxBleDevice> listDevicesFound;
    private ArrayAdapter<RxBleDevice> adapter;

    private Subscription scanSubscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_device);
        ButterKnife.bind(this);

        //PrepareAdapter
        listDevicesFound = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listDevicesFound);
        listdevicesListview.setAdapter(adapter);
        listdevicesListview.setOnItemClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // When done, just unsubscribe.
        //if(scanSubscription!=null) scanSubscription.unsubscribe();
    }


    private void addDeviceIfNeeded(RxBleDevice bleDevice) {
        if (!listDevicesFound.contains(bleDevice)) {
            listDevicesFound.add(bleDevice);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        //Selected item
        RxBleDevice selected = listDevicesFound.get(pos);
        Intent func = new Intent(ListDevicesActivity.this,FunctionsDeviceActivity.class);
        func.putExtra(EXTRA_RXBLEDEVICE, selected.getMacAddress());
        startActivity(func);
    }



    @Override
    protected void appBluetoothReady(boolean ready, int status) {
        if(!ready){
            handleSnackMessage("Bluetooth not ready");
        }else{

            listDevicesFound.clear();
            adapter.notifyDataSetChanged();

            //Start scan
            scanSubscription = getAppClient().scanBleDevices()
                    .subscribe(
                            rxBleScanResult -> {
                                addDeviceIfNeeded(rxBleScanResult.getBleDevice());
                            },
                            throwable -> {
                                // Handle an error here.
                                handleSnackMessage("Error state connection : " + throwable.getMessage());
                            }
                    );
        }
    }

}
