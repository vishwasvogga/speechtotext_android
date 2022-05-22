package com.coshel.commander;

import android.util.Log;

public class CLog {
    private static final CLog ourInstance = new CLog();

    public static CLog getInstance() {
        return ourInstance;
    }

    private CLog() {
    }

    void d(String tag,String msg){
        if(BuildConfig.DEBUG){
            Log.d(tag,msg);
        }
    }

    void e(String tag,String msg){
        if(BuildConfig.DEBUG){
            Log.e(tag,msg);
        }
    }
    void v(String tag,String msg){
        if(BuildConfig.DEBUG){
            Log.v(tag,msg);
        }
    }
}
