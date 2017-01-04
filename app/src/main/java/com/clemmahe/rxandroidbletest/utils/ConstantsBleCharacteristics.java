package com.clemmahe.rxandroidbletest.utils;

import java.util.UUID;

/**
 * ConstantsBleCharacteristics
 * Created by CMA10935 on 03/01/2017.
 */

public class ConstantsBleCharacteristics {

    public static String END_BLUETOOTH_CHARACTERISTIC = "-0000-1000-8000-00805f9b34fb";
    public static UUID UUID_CHARACTERISTIC_WRITE =  UUID.fromString("0000ff31" + END_BLUETOOTH_CHARACTERISTIC);
    public static UUID UUID_CHARACTERISTIC_READ =  UUID.fromString("0000ff32" + END_BLUETOOTH_CHARACTERISTIC);

}
