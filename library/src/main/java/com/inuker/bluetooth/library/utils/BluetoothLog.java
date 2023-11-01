package com.inuker.bluetooth.library.utils;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by dingjikerbo on 2015/12/16.
 */
public class BluetoothLog {
    public volatile static boolean isShowLog = true;
    private static final String LOG_TAG = "miio-bluetooth";

    public static void i(String msg) {
        if (isShowLog) Log.i(LOG_TAG, msg);
    }

    public static void e(String msg) {
        if (isShowLog) Log.e(LOG_TAG, msg);
    }

    public static void v(String msg) {
        if (isShowLog) Log.v(LOG_TAG, msg);
    }

    public static void d(String msg) {
        if (isShowLog) Log.d(LOG_TAG, msg);
    }

    public static void w(String msg) {
        if (isShowLog) Log.w(LOG_TAG, msg);
    }

    public static void e(Throwable e) {
        if (isShowLog) e(getThrowableString(e));
    }

    public static void w(Throwable e) {
        if (isShowLog) w(getThrowableString(e));
    }

    private static String getThrowableString(Throwable e) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        while (e != null) {
            e.printStackTrace(printWriter);
            e = e.getCause();
        }
        String text = writer.toString();
        printWriter.close();
        return text;
    }
}
