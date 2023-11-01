package com.inuker.bluetooth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by dingjikerbo on 2016/9/1.
 */
public class StringUtils {
    public static String getTimeDisplayNameNormal(long ctimelong) {
        String r;
        Calendar currentTime = Calendar.getInstance();
        long currentTimelong = System.currentTimeMillis();
        Calendar publicCal = Calendar.getInstance();
        publicCal.setTimeInMillis(ctimelong);
        long timeDelta = currentTimelong - ctimelong;
        if (timeDelta < 60 * 1000L) {
            r = "刚刚";
        } else if (timeDelta < 60 * 60 * 1000L) {
            r = timeDelta / (60 * 1000L) + "分钟前";
        } else if (timeDelta < 24 * 60 * 60 * 1000L) {
            if (currentTime.get(Calendar.DAY_OF_YEAR) == publicCal.get(Calendar.DAY_OF_YEAR)) {
                r = timeDelta / (60 * 60 * 1000L) + "小时前";
            } else {
                r = "昨天";
            }
        } else if (timeDelta < 2 * 24 * 60 * 60 * 1000L) {
            if (currentTime.get(Calendar.DAY_OF_YEAR) == publicCal.get(Calendar.DAY_OF_YEAR) + 1) {
                r = "昨天";
            } else {
                r = "前天";
            }
        } else if (timeDelta < 3 * 24 * 60 * 60 * 1000L) {
            if (currentTime.get(Calendar.DAY_OF_YEAR) == publicCal.get(Calendar.DAY_OF_YEAR) + 2) {
                r = "前天";
            } else {
                r = new SimpleDateFormat("yy年MM月dd日", Locale.CHINA).format(ctimelong);
            }
        } else {
            r = new SimpleDateFormat("yy年MM月dd日", Locale.CHINA).format(ctimelong);
        }
        return r;
    }
}
