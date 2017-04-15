package com.example.wangxi.keepliveprocess;

import android.os.Process;
import android.util.Log;

/**
 * Created by wangxi on 2017/4/15.
 */

public class Logger {
    String processName;
    public Logger(String processName){
        this.processName=processName;
    }

    public void print(){
        Log.e("tag2","process="+ Process.myPid()+" Process uid="+Process.myUid());
        Log.e("tag2","processName="+processName);
    }
}
