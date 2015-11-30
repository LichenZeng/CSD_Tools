package com.mlt.csdtool;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

//yutianliang add for VFOZBENQ-371 begin
import java.text.SimpleDateFormat;
import java.util.Date;
//yutianliang add for VFOZBENQ-371 end

/**
 * @ClassName: BootCompleteReceiver
 * @Description: Service for receiving the time to calibrate the broadcast, and
 *               the time to start the computation
 * @Function: TODO ADD FUNCTION
 * @author: peisaisai
 * @date: 20150911 10:08:04
 * @time: 10:08:04 Copyright (c) 2015, Malata All Rights Reserved.
 */
/** 
* @ClassName: BootCompleteReceiver 
* @Description: modify for VFOZBENQ-140
* @Function: TODO ADD FUNCTION
* @author:   peisaisai
* @date:     20150922 am10:20:16 
* @time: 10:20:16 
* Copyright (c) 2015,  Malata All Rights Reserved.
*/
/** 
* @ClassName: BootCompleteReceiver 
* * @Description: modify for VFOZBENQ-140
* @Function: Add a manual adjustment time function.
* @author:   peisaisai
* @date:     20150928 pm 5:16:56 
* @time:5:16:56 
* Copyright (c) 2015,  Malata All Rights Reserved.
*/
public class BootCompleteReceiver extends BroadcastReceiver {

    private final String MODEM = "android.intent.action.CSDTEST_COUNTTIMESERVICE_MODEM";
    private final String NETWORK = "android.intent.action.CSDTEST_COUNTTIMESERVICE";
    private final String NO_NETWORK = "android.intent.action.CSDTEST_COUNTTIMESERVICE_NO_NETWORK";
    private final String SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN";
    private final String COUNTTIME_RECEIVER = "BootCompleteReceiver";
    
    private final String SET_TIME = "android.intent.action.CSDTEST_COUNTTIMESERVICE_SET_TIME";
    private final String REBOOT = "android.intent.action.BOOT_COMPLETED";
    private SharedPreferences share;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.i("pss", "receive a message :" + intent.getAction().toString());
        Log.i("pss",
                "CountTimeService.judgeTimeExist() = "
                        + CountTimeService.judgeTimeExist());
        Log.i("pss", "CountTimeService.mboolCountTimeService = "
                + CountTimeService.mboolCountTimeService);
        
        share = context.getSharedPreferences(COUNTTIME_RECEIVER,Context.MODE_PRIVATE);
        Log.i("pss", "share.getBoolean(calibration, false) = " + share.getBoolean("calibration", false));
        
        boolean autoTimeEnabled = getAutoState(Settings.Global.AUTO_TIME,context);
        boolean autoTimeGpsEnabled = getAutoState(Settings.System.AUTO_TIME_GPS,context);
        
        if (intent.getAction().toString().equals(MODEM)) {
            if (CountTimeService.judgeTimeExist()
                    && !CountTimeService.mboolCountTimeService) {
                startCountTimeService(context);
            }
        } else if (intent.getAction().toString().equals(NETWORK)) {
            if (CountTimeService.judgeTimeExist()
                    && !CountTimeService.mboolCountTimeService) {
                startCountTimeService(context);
            }
        } else if (intent.getAction().toString().equals(NO_NETWORK)) {
            if (CountTimeService.judgeTimeExist()
                    && !CountTimeService.mboolCountTimeService
                    && share.getBoolean("calibration", false)) {
                startCountTimeService(context);
            }
        }else if(intent.getAction().toString().equals(SHUTDOWN)){
            if(CountTimeService.mboolCountTimeService){
                stopCountTimeService(context);
            }
        }else if(intent.getAction().toString().equals(SET_TIME)){            	
            if (CountTimeService.judgeTimeExist()
                    && !CountTimeService.mboolCountTimeService
                    && isValidTime()//yutianiang add for VFOZBENQ-371
            		) {
                startCountTimeService(context);
            }
        }else if(intent.getAction().toString().equals(REBOOT)){
            if (CountTimeService.judgeTimeExist()
                    && !CountTimeService.mboolCountTimeService && !autoTimeEnabled && !autoTimeGpsEnabled
                    && share.getBoolean("calibration", false)) {
                startCountTimeService(context);
            }
        }
    }
    
  //yutianliang add for VFOZBENQ-371 begin
    String m_sValidTime = "2015-11-10 00:00:00";
    private boolean isValidTime(){
		try {
			SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = sDate.parse(m_sValidTime);

			long validTime = date.getTime();
			long currentTime = System.currentTimeMillis();
			Log.d("yutianliang", "validTime = " + validTime
					+ "---currentTime = " + currentTime);

			return validTime <= currentTime;
		} catch (Exception e) {

		}
		return false;

    }
  //yutianliang add for VFOZBENQ-371 end
    
    private boolean getAutoState(String name,Context context) {
        try {
            return Settings.Global.getInt(context.getContentResolver(), name) > 0;
        } catch (SettingNotFoundException snfe) {
            return false;
        }
    }
    
    
    /** 
    * @MethodName: stopCountTimeService 
    * @Description:stop CountTimeService
    * @param context  
    * @return void   
    * @throws 
    * Copyright (c) 2015,  Malata All Rights Reserved.
    */
    private void stopCountTimeService(Context context){
        context.stopService(new Intent("com.mlt.csdtool.service"));
    }
    
    /** 
    * @MethodName: startCountTimeService 
    * @Description:Start CountTimeService
    * @param context  
    * @return void   
    * @throws 
    * Copyright (c) 2015,  Malata All Rights Reserved.
    */
    private void startCountTimeService(Context context){
        
        if (!share.getBoolean("calibration", false)) {
            Editor editor = share.edit();
            editor.putBoolean("calibration", true);
            editor.commit();
        }
        context.startService(new Intent("com.mlt.csdtool.service"));
    }

}
