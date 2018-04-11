package com.sange.dragalbum.util;

import android.util.Log;

/**
 * Log工具
 * Created by we on 2017/2/23.
 */

public class LogUtil {
    private static Boolean isPrintD = true;// 是否打印所有的d级别日志
    private static Boolean isPrintI = true;// 是否打印所有的i级别日志
    private static Boolean isPrintE = true;// 是否打印所有的e级别日志

    public static void d(Class c, String msg){
        if (isPrintD){
            Log.d(c.getSimpleName(),msg);
        }
    }
    public static void i(Class c, String msg){
        if (isPrintI){
            Log.i(c.getSimpleName(),msg);
        }
    }
    public static void e(Class c, String msg){
        if (isPrintE){
            Log.e(c.getSimpleName(),msg);
        }
    }
}
