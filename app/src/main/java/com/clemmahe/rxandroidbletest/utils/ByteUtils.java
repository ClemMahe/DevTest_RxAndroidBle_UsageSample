package com.clemmahe.rxandroidbletest.utils;

/**
 * ByteUtils
 * Created by CMA10935 on 03/01/2017.
 */

public class ByteUtils {

    private static final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String byteArrayToHexString(byte[] bytes, boolean separate) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        StringBuilder returnStr = new StringBuilder(new String(hexChars));
        if (separate) {
            int idx = returnStr.length() - 2;
            while (idx > 0) {
                returnStr.insert(idx, " ");
                idx -= 2;
            }
        }
        return returnStr.toString();
    }

    /**
     * To know if it is a command indicate the end of data
     *
     * @param array
     * @return
     */
    public static boolean isCommandFinish(byte[] array) {
        if (array.length != 20) {
            return false;
        }
        for (int i = 0; i < array.length; i++) {
            int tmp = UByte(array[i]);
            if (tmp != 0xff) {
                return false;
            }
        }
        return true;
    }

    /**
     * To know if it is a command indicate the data is ready
     *
     * @param array
     * @return
     */
    public static boolean isDataReady(byte[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            int tmp = UByte(array[i]);
            if (tmp != 0xff) {
                return true;
            }
        }

        if (array.length == 20) {
            int tmp = UByte(array[19]);
            if (tmp == 0xee) {
                return false;
            }
        }
        return true;
    }


    private static int UByte(byte b) {
        if (b < 0)
            return (b & 0xff);
        else
            return b;
    }

}
