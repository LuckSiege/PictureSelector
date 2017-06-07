package com.luck.picture.lib.tools;

import android.util.Log;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.tool
 * email：893855882@qq.com
 * data：2017/3/14
 */

public class DebugUtil {
    public static final String TAG = "com.luck.picture.lib";

    public static final boolean DEBUG = false;

    public static void debug(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void debug(String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public static void v(String msg) {
        if (DEBUG) {
            Log.v(TAG, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (DEBUG) {
            Log.v(tag, msg);
        }
    }

    public static void log(String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public static void log(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void i(String msg) {
        if (DEBUG) {
            Log.i(TAG, msg);
        }
    }


    public static void error(String tag, String error) {

        if (DEBUG) {

            Log.e(tag, error);
        }
    }

    public static void error(String error) {

        if (DEBUG) {

            Log.e(TAG, error);
        }
    }

}
