package com.mlt.csdtool;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;
import android.os.ServiceManager;
/** 
* @ClassName: CountTimeService 
* @Function: A service for calculating the starting time and writing the boot time to NVRAM
* @author:   peisaisai
* @date:     20150831 pm1:47:10 
* @time: pm1:47:10 
* Copyright (c) 2015,  Malata All Rights Reserved.
*/


/** 
* @ClassName: CountTimeService 
* @Description: modify for VFOZBENQ-15
* @Function: Storage mode of modification time
* @author:   peisaisai
* @date:     20150911 10:16:08 
* @time: 10:16:08 
* Copyright (c) 2015,  Malata All Rights Reserved.
*/
/** 
* @ClassName: CountTimeService 
* @Description: modify for VFOZBENQ-140
* @author:   peisaisai
* @date:     20150922 10:22:47 
* @time: 涓婂崍10:22:47 
* Copyright (c) 2015,  Malata All Rights Reserved.
*/
public class CountTimeService extends Service{
    
    //To confirm whether the service is turned on.
    public static boolean mboolCountTimeService = false;
    
    private final static String TAG = "pss";
    private final int TIME_VALUE_MAX = 18000;
    private final int MSG_ONE_SECONDE_FLAG = 0;
    private final int MSG_SEND_FLAG = 1;

    //Save network for the first time to correct time
    public static final String SHAREPREFERENCE_NAME = "countservice";
    
    // Data location stored in NVRAM
    private final static int NVRAM_START_POSTION = 1024-7-1;//pss modify for VFOZBENQ-140//yutianliang modify for VFOZBENQ-370 old:40 + 64 + 12

    private final static int NVRAM_DATA_LENGTH = 7;

    // This is the NVRAM file name, according to which you can know lid NVRAM
    final static String PRODUCT_INFO_FILENAME = "/data/nvram/APCFG/APRDEB/PRODUCT_INFO";

    private long mTimeCount;
    
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.i("pss","onCreate");
        mboolCountTimeService = true;
        Log.i("pss", "mboolCountTimeService = " + mboolCountTimeService);
        Message msgCycle = new Message();
        msgCycle.what = MSG_ONE_SECONDE_FLAG;
        mTimeHandler.sendMessageDelayed(msgCycle, 1000);
    }
    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        Log.i("pss","onStart");
        savePreTime();
        Log.i("pss", "getPreTime() = " + getPreTime());
    }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
		Log.i("pss","onDestroy");
		clearPreTime();
		mboolCountTimeService = false;
        super.onDestroy();
    }

    /** 
    * @MethodName: judgeTimeExist 
    * @Description:To determine whether the phone has been recorded over time
    * @return  
    * @return boolean   
    * @throws 
    * Copyright (c) 2015,  Malata All Rights Reserved.
    */
    public static boolean judgeTimeExist(){
        byte[] buff = readProductInfo(
                NVRAM_START_POSTION, PRODUCT_INFO_FILENAME);
        int flag = 0;

        if (buff != null) {
            for (flag = NVRAM_START_POSTION; flag < NVRAM_START_POSTION + NVRAM_DATA_LENGTH; flag++) {
                if (buff[flag] != 0) {
                    break;
                }
            }
            if (flag >= NVRAM_START_POSTION + NVRAM_DATA_LENGTH) {
                return true;
            } 
        }
        return false;
    }

    /** 
    * @Fields: mTimeHandler 
    * TODO锛歊eceive the message, determine whether the current boot time is more than 5 hours, more than 5 hours to record the current time
    */
    public Handler mTimeHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
            case MSG_ONE_SECONDE_FLAG:
                Log.i("pss","MSG_ONE_SECONDE_FLAG");
                mTimeCount = (SystemClock.elapsedRealtime() - getPreTime())/1000;
                Log.i("pss","getPreTime() = " + getPreTime());
                Log.i(TAG, "mTimeCount = " + mTimeCount);
                if (mTimeCount >= TIME_VALUE_MAX) {

                    byte[] b = acquireTime();
                    Log.i(TAG, "b:");
                    for (int i = 0; i < b.length; i++) {
                        Log.i(TAG, "byte[" + i + "] = " + b[i]);
                    }
                    if (b != null && judgeTimeExist()) {
                        writeProduct(b, NVRAM_START_POSTION);
                        onDestroy();
                    }
                } else {
                    Message msgcCycle = new Message();
                    msgcCycle.what = MSG_SEND_FLAG; 
                    mCycleHander.sendMessage(msgcCycle);
                }
                break;

            default:
                break;
            }
            ;
        };
    };

    /**
     * @MethodName: acquireTime
     * @Description:Acquisition system current time
     * @return
     * @return byte[]
     * @throws Copyright
     *             (c) 2015, Malata All Rights Reserved.
     */
    private byte[] acquireTime() {
        Time time = new Time();
        time.setToNow();
        byte[] b = new byte[] {0,0,0,0,0,0,0};
        b[0] = (byte) time.monthDay;
        b[1] = (byte) (time.month + 1);
        b[2] = (byte) (time.year / 100);
        b[3] = (byte) (time.year % 100);
        b[4] = (byte) time.hour;
        b[5] = (byte) time.minute;
        b[6] = (byte) time.second;
        return b;
    }

    public Handler mCycleHander = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_SEND_FLAG:
                Log.i("pss", "MSG_SEND_FLAG");
                Message msgCycle = new Message();
                msgCycle.what = MSG_ONE_SECONDE_FLAG;
                mTimeHandler.sendMessageDelayed(msgCycle, 1000);
                break;

            default:
                break;
            }
            
        };
    };

    /** 
    * @MethodName: writeProduct 
    * @Description:Write NVRAM data
    * @param buff
    * @param postion  
    * @return void   
    * @throws 
    * Copyright (c) 2015,  Malata All Rights Reserved.
    */
    public static void writeProduct(byte[] buff, int postion) {
        final String NVRAM_AGENT_SERVICE = "NvRAMAgent";

        NvRAMAgent agent = NvRAMAgent.Stub.asInterface(ServiceManager
                .getService(NVRAM_AGENT_SERVICE));
        byte[] buffWirte = null;
        buffWirte = readProductInfo(NVRAM_START_POSTION, PRODUCT_INFO_FILENAME);
        for (int i = 0; i < buff.length; i++) {
            buffWirte[i + postion] = buff[i];
        }
        int flag = 0;

        try {
            flag = agent.writeFileByName(PRODUCT_INFO_FILENAME, buffWirte);
        } catch (Exception e) {
            Log.i(TAG, "write exception");
            e.printStackTrace();
        }
        if (flag > 0) {
            Log.i(TAG, "write success!");
        } else {
            Log.i(TAG, "write failed!");
        }
    }
    
    /** 
     * @MethodName: readProductInfo 
     * @Description:Read NVRAM data
     * @param postion
     * @param nvramName
     * @return  
     * @return byte[]   
     * @throws 
     * Copyright (c) 2015,  Malata All Rights Reserved.
     */
     public static byte[] readProductInfo(int postion, String nvramName) {

         final String NVRAM_AGENT_SERVICE = "NvRAMAgent";
         NvRAMAgent agent = NvRAMAgent.Stub.asInterface(ServiceManager
                 .getService(NVRAM_AGENT_SERVICE));
         Log.i("pss", "agent = "+agent);
         byte[] buff = null;
         try {
             buff = agent.readFileByName(nvramName);
         } catch (Exception e) {
             Log.i("pss", "read exception!");
             e.printStackTrace();
         }
         if (buff != null) {
             Log.i("pss", "read success锛歮acaddress锛�");
             for (int i = postion; i < postion + NVRAM_DATA_LENGTH; i++) {
                 Log.i("pss", "" + buff[i]);
             }
             return buff;
         } else {
             Log.i("pss", "null");
         }
         return null;
     }
     
     
     
     /** 
    * @MethodName: savePreTime 
    * @Description:The time of the first calibration time has been saved in sharepreference 
    * @return void   
    * @throws 
    * Copyright (c) 2015,  Malata All Rights Reserved.
    */
    public void savePreTime(){
         Log.i("pss","savePreTime");
         SharedPreferences share = this.getSharedPreferences(SHAREPREFERENCE_NAME,Context.MODE_PRIVATE);
         if (!share.getBoolean("exist", false)) {
             Log.i("pss", "!share.getBoolean(exist, false)");
             long mPreTime = SystemClock.elapsedRealtime();
             Editor editor = share.edit();
             editor.putLong("mPreTime", mPreTime);
             editor.putBoolean("exist", true);
             editor.commit();
         }
     }
     
    /** 
    * @MethodName: clearPreTime 
    * @Description:clear data 
    * @return void   
    * @throws 
    * Copyright (c) 2015,  Malata All Rights Reserved.
    */
    public void clearPreTime(){
        Log.i("pss", "clearPreTime");
        SharedPreferences share = this.getSharedPreferences(SHAREPREFERENCE_NAME,Context.MODE_PRIVATE);
        Editor editor = share.edit();
        editor.putLong("mPreTime", 0);
        editor.putBoolean("exist", false);
        editor.commit();
        Log.i("pss", "share.getLong(mPreTime, 0) = " + share.getLong("mPreTime", 0));
    }
    
     /** 
    * @MethodName: getPreTime 
    * @Description:get the time saved in sharepreference
    * @return  
    * @return long   
    * @throws 
    * Copyright (c) 2015,  Malata All Rights Reserved.
    */
    public long getPreTime(){
         Log.i("pss","getPreTime");
         SharedPreferences share;
         share = this.getSharedPreferences(SHAREPREFERENCE_NAME,
                    Context.MODE_PRIVATE);
         long preTime = share.getLong("mPreTime", 0);
         return preTime;
     }
     
}
