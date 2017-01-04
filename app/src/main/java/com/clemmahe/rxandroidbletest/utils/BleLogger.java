package com.clemmahe.rxandroidbletest.utils;


import com.clemmahe.rxandroidbletest.BuildConfig;
import com.orhanobut.logger.Logger;

/**
 * SdkLogger
 * Created by CMA10935 on 17/11/2016.
 */

public abstract class BleLogger {

    private static final String TAG = BleLogger.class.getSimpleName();

    /**
     * Log Data if LOG_ACTIVATED
     * @param textToLog String
     */
    public static void logData(final String textToLog) {
        if(BuildConfig.DEBUG) {
            Logger.d(textToLog);
        }
    }


    /**
     * Log Debug if LOG_ACTIVATED
     * @param textToLog String
     */
    public static void logDebug(final String textToLog) {
        if(BuildConfig.DEBUG) {
            Logger.d(textToLog);
        }
    }


    /**
     * Log Warning if LOG_ACTIVATED
     * @param textToLog String
     */
    public static void logWarning(final String textToLog) {
        if(BuildConfig.DEBUG) {
            Logger.w(textToLog);
        }
    }

    /**
     * Log Error if LOG_ERROR_ACTIVATED
     * @param textToLog String
     */
    public static void logError(final String textToLog) {
        if(BuildConfig.DEBUG) {
            Logger.e(textToLog);
        }
    }
}
