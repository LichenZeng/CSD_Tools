package com.mlt.csdfactorytest.item;

import java.lang.ref.WeakReference;

import android.R.raw;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mlt.csdfactorytest.ItemTestActivity;
import com.mlt.csdfactorytest.R;


/** 
* @ClassName: VersionAndBatteryTest 
* @PackageName:com.malata.factorytest.item
* @Description: get the phone Version infomation and battery charging infomation.
* @author:   chehongbin
* @date:     2015年1月27日 上午11:12:27  
* Copyright (c) 2015 MALATA,All Rights Reserved.
*/

public class BatteryTest extends AbsHardware {
	private static final String TAG = "BatteryTest";
	private Context mContext;
    private Intent mIntentBatteryState;// battery broadcast intent 
    private static final int EVENT_BOOT_TIME_UPDATE = 500;// time update
    
    private TextView mtvBatteryStatus,mtvBatteryLevels,mtvBatteryRange,mtvBatteryRunstatus,mtvBatteryVoltage,mtvBatteryTemp,mtvBatteryTech,mtvBatteryTime;
      
    @Override
    public void onCreate() {
    	super.onCreate();
		Log.i(TAG, "oncreat");
		/**set the pass button can't click*/
		//ItemTestActivity.itemActivity.handler.sendEmptyMessage(ItemTestActivity.MSG_BTN_PASS_UNCLICKABLE);
		Handler mHandler = new MyHandler(this);
		mHandler.sendEmptyMessage(EVENT_BOOT_TIME_UPDATE);
    }
    
	public BatteryTest(String text, Boolean visible) {
		super(text, visible);
	}
	  
	@Override
	public View getView(Context context) {
		this.mContext = context;
		View view = LayoutInflater.from(context).inflate(R.layout.item_batterytest, null);
		mtvBatteryStatus = (TextView) view.findViewById(R.id.battery_status_tv);
		mtvBatteryLevels = (TextView) view.findViewById(R.id.battery_levels_tv);
		mtvBatteryRange = (TextView) view.findViewById(R.id.battery_range_tv);
		mtvBatteryStatus = (TextView) view.findViewById(R.id.battery_status_tv);
		mtvBatteryVoltage = (TextView) view.findViewById(R.id.battery_voltage_tv);
		mtvBatteryTemp = (TextView) view.findViewById(R.id.battery_temp_tv);
		mtvBatteryTech = (TextView) view.findViewById(R.id.battery_tech_tv);
		mtvBatteryTime = (TextView) view.findViewById(R.id.battery_time_tv);
		mtvBatteryRunstatus = (TextView) view.findViewById(R.id.battery_runstatus_tv);
		return view;
		
	}
		
	/** 
	* @ClassName: MyHandler 
	* @PackageName:com.malata.factorytest.item
	* @Description: Receives the message, Update on every 0.5 seconds,to updata boot time.
	* @Function: TODO ADD FUNCTION
	* @author:   chehongbin
	* @date:     2015年1月14日 下午5:45:09  
	* Copyright (c) 2015,  Malata All Rights Reserved.
	*/
	private  class MyHandler extends Handler {
	        public MyHandler(BatteryTest batteryTest) {
	            new WeakReference<BatteryTest>(batteryTest);
	        }
	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	                case EVENT_BOOT_TIME_UPDATE:
	                   updateTimes();
	                   sendEmptyMessageDelayed(EVENT_BOOT_TIME_UPDATE, 1000);//循环
	                   break;
	            }
	        }
	 }	
	
	/** 
	* @MethodName: updateTimes 
	* @Description: TODO  
	* @return void   
	* @throws 
	* Copyright (c) 2015,  Malata All Rights Reserved.
	*/
	void updateTimes() {
	       // long at = SystemClock.uptimeMillis() / 1000;
	        long ut = SystemClock.elapsedRealtime() / 1000;
	        if (ut == 0) {
	            ut = 1;
	        }
	        mtvBatteryTime.setText(convert(ut));
	 }
	
	/** 
	* @MethodName: convert 
	* @Description: 计算开机时间
	* @param t
	* @return  String   
	* @throws 
	* Copyright (c) 2015,  Malata All Rights Reserved.
	*/
	private String convert(long t) {
	        int s = (int)(t % 60);
	        int m = (int)((t / 60) % 60);
	        int h = (int)((t / 3600));

	        return h + ":" + pad(m) + ":" + pad(s);
	 }
	
	/** 
	* @MethodName: pad 
	* @Description:在个位数前+0，大于10的直接返回
	* @param n 
	* @return String   
	* @throws 
	* Copyright (c) 2015,  Malata All Rights Reserved.
	*/
	private String pad(int n) {
	        if (n >= 10) {
	            return String.valueOf(n);
	        } else {
	            return "0" + String.valueOf(n);
	        }
	 }
	/** 
	* @Fields: mReceiver 
	* TODO：battert BroadcastReceiver,
	* to get thebattery info :battery health,battery temp,battery states,battery level,
	* Battery charging type,battery technology...
	*/
	BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
					status == BatteryManager.BATTERY_STATUS_FULL;
			int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
			boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
			boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
			Log.i(TAG, "usbCharge："+usbCharge);
			Log.i(TAG, "acCharge："+acCharge);
			Log.i(TAG, "isCharging："+isCharging);
			
			int current = intent.getExtras().getInt("level");//Get the current power
	        int total = intent.getExtras().getInt("scale");//Get the total power
	        int percent = current*100 / total;
	        int voltage = intent.getIntExtra( "voltage" , 0 );
	        int temperature = intent.getIntExtra( "temperature" , 0 ); // 温度的单位是10℃
	        long temp = temperature/10;
	        String technology = intent.getStringExtra( "technology" );
	        int health = intent.getIntExtra("health", 0); 
	        
	        mtvBatteryLevels.setText(""+percent);
	        mtvBatteryRange.setText(""+total);
	        
	        if (temp >= 50) {
	        	mtvBatteryTemp.setText(""+temp+"℃"+" (too hot)");
	        	mtvBatteryTemp.setTextColor(Color.RED);
			} else if (temp <= 5) {
				mtvBatteryTemp.setText(""+temp+"℃"+" (too cold)");
	        	mtvBatteryTemp.setTextColor(Color.RED);
			}else {
				mtvBatteryTemp.setText(""+temp+"℃"+" (Normal)");
	        	mtvBatteryTemp.setTextColor(Color.GREEN);
			}
	        
	        if ((voltage >= 3450) && (voltage <= 43500)) {
	        	mtvBatteryVoltage.setText(""+voltage+"mV"+" (Normal)");
	        	//mtvBatteryVoltage.append(R.string.battery_voltage_status1);
	        	mtvBatteryVoltage.setTextColor(Color.GREEN);
			}else if ( voltage < 3450) {
				mtvBatteryVoltage.setText(""+voltage+"mV"+" (Low Voltage)");
	        	mtvBatteryVoltage.setTextColor(Color.RED);
			} else if (voltage > 43500) {
				mtvBatteryVoltage.setText(""+voltage+"mV"+" (Over Voltage)");
	        	mtvBatteryVoltage.setTextColor(Color.RED);
			}
	        
	        
	        
	        
	        
	        mtvBatteryTech.setText(""+technology);
	        
			if(isCharging && usbCharge) {
				mtvBatteryStatus.setText(R.string.battery_status2);
			
			}else if (isCharging && acCharge) {
				mtvBatteryStatus.setText(R.string.battery_status3);
			}			
			else {
				mtvBatteryStatus.setText(R.string.battery_status1);
			}	
			
			switch (status) {  
				case BatteryManager.BATTERY_STATUS_UNKNOWN:  
					mtvBatteryStatus.setText(R.string.battery_status5);
					break;  
				case BatteryManager.BATTERY_STATUS_NOT_CHARGING:  
					mtvBatteryStatus.setText(R.string.battery_status1);
					break;  
				case BatteryManager.BATTERY_STATUS_FULL:  
					mtvBatteryStatus.setText(R.string.battery_status4);
					break;  
			}  
			
			
			switch (health) {  
				case BatteryManager.BATTERY_HEALTH_UNKNOWN: 
					mtvBatteryRunstatus.setText(R.string.battery_runstatus1);
					break;  
				case BatteryManager.BATTERY_HEALTH_GOOD:  
					mtvBatteryRunstatus.setText(R.string.battery_runstatus2);
					break;  
				case BatteryManager.BATTERY_HEALTH_OVERHEAT:  
					mtvBatteryRunstatus.setText(R.string.battery_runstatus3);
					break;  
				case BatteryManager.BATTERY_HEALTH_DEAD:  
					mtvBatteryRunstatus.setText(R.string.battery_runstatus4);
					break;  
				case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:  
					mtvBatteryRunstatus.setText(R.string.battery_runstatus5);
					break;  
				case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:  
					mtvBatteryRunstatus.setText(R.string.battery_runstatus6);
					break;  
			}  
		}
	};
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(null != mBatteryReceiver) {
			mContext.unregisterReceiver(mBatteryReceiver); 
		} else {
			
		}
	}

	@Override
    public void onPause() {
        super.onPause();
    }
	@Override
	public void onResume() {
		super.onResume();
		   /**Create the battery status changed event listeners of the filter*/
    	IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		mContext.registerReceiver(mBatteryReceiver, ifilter);
		
		mIntentBatteryState = mContext.registerReceiver(mBatteryReceiver, ifilter);
		
	}
	
}
	 
	 

