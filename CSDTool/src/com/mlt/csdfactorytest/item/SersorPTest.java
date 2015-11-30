package com.mlt.csdfactorytest.item;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mlt.csdfactorytest.ItemTestActivity;
import com.mlt.csdfactorytest.R;
import com.mlt.csdfactorytest.item.tools.SensorTool;
/**
 * 
 * file name:SensorShow.java
 * Copyright MALATA ,ALL rights reserved
 * 2015-2-10
 * author:laiyang
 * 
 * show all sensors(Gyroscope,Gsensor,Psensor,Lsensor,Msensor) data,
 * in Gsensor, show custom view to device direction test
 * some device has no one or more sensors,so if device has no sensor(like Gyroscope，Msensor),
 * hint the device doesn't have the sensor.
 * 
 * Modification history
 * -------------------------------------
 * 
 * -------------------------------------
 */
public class SersorPTest extends AbsHardware implements SensorEventListener{
	// ori change value
	private static final float ORI_CHANGE_LEN;
	static {
		ORI_CHANGE_LEN = (float) (9.8f * Math.sin(45*Math.PI/180));
	}
	// Handler's Message : update psensor
	private final int MSG_UPDATE_PSSOR = 1005;
	// SensorManager
	public SensorManager mSsorManager;
	// sensor: psensor
	public Sensor pssor;
	// Context of Application
	public Context mContext;
	// Context of Application
	private RelativeLayout mPSsorLayout;
	private LinearLayout mPSsorLinearLayout;
	// PSensor's TextView,show value of PSensor
	public TextView mtvPssor;
	// access Message's of Sensors
	private Handler mHandler;
	// access Message's of Sensors
	private View mLayout;
	// custom view ,show ori of GSensor
	private int mColorWhite;
	// custom view ,show ori of GSensor
	private float mValuePSensor;
	// if true,the device has Lsensor,or has no Lsensor
	private boolean hasPSsor;
	/**
	 * construction of class SensorShow 
	 * @param text test item's name
	 * @param visible
	 */
	public SersorPTest(String text, Boolean visible) {
		super(text, visible);
	}
	
	@Override
	public View getView(Context context) {
		
		mContext = context;
		
		// init views
		initView();
		mHandler = new SensorUpdateHandler(ItemTestActivity.itemActivity.getMainLooper());
		
		// initialize all of sensors
		initSensors();
		
		// set Pass button unClickable
		ItemTestActivity.itemActivity.handler.
			sendEmptyMessage(ItemTestActivity.MSG_BTN_PASS_UNCLICKABLE);
		mColorWhite = mContext.getResources().getColor(R.color.floralwhite);
		
		return mLayout;
	}
	/**
	 * init all sensors
	 * if the device not available the sensor,will not init the sensor
	 * @date 2015-1-31 pm 3:49:53
	 */
	private void initSensors() {
		
		mSsorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		if(!(hasPSsor = SensorTool.hasSensor(mSsorManager, Sensor.TYPE_PROXIMITY))) {
			mtvPssor.setText(mContext.getString(R.string.tv_pssor_not_available));
		} else {
			pssor = mSsorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
			
		}
		
	}
	/**
	 * init all views
	 * @date 2015-1-31 pm 3:53:28
	 */
	private void initView() {
		mLayout = LayoutInflater.from(mContext).inflate(R.layout.item_sersor_p_test, null);
		
		mtvPssor = (TextView) mLayout.findViewById(R.id.tv_pssor_value);
		
		mPSsorLayout = (RelativeLayout) mLayout.findViewById(R.id.rl_pssor);
		mPSsorLinearLayout = (LinearLayout) mLayout.findViewById(R.id.p_sersor_blackground);
		
	}

	/**
	 * registerListener of SensorManager
	 * if not have sensor,no register the listener
	 */
	@Override
	public void onResume() {
		if(hasPSsor) {
			mSsorManager.registerListener(this, pssor, SensorManager.SENSOR_DELAY_NORMAL);
		}
		super.onResume();
	}
	/**
	 * when sensor data changed,send handler message to update view
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		Message msg = null;
		float values[] = event.values;
		synchronized (this) { 
			switch(event.sensor.getType()) {
			/** 5.PROXIMITY:SensorEvent type is TYPE_PROXIMITY */
			case Sensor.TYPE_PROXIMITY:
				sendPSensorChangedMSG(values, msg);
				break;
			
			default:
				break;
			} // end of switch(event.sensor.getType())
		} // end of synchronized (this)
	} // end of onSensorChanged(SensorEvent event)
	/**
	 * Psensor changed ,send message to update view
	 * @date 2015-2-11 pm 3:31:15
	 * @param values
	 * @param msg
	 */
	private void sendPSensorChangedMSG(float[] values, Message msg) {
		msg = mHandler.obtainMessage(MSG_UPDATE_PSSOR, values[0]);
		mHandler.sendMessage(msg);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// not used
	}
	/**
	 * when leave this Activity, unregisterListener of SensorManager
	 */
	@Override
	public void onPause() {
		mSsorManager.unregisterListener(this);
		super.onPause();
	}
	
	class SensorUpdateHandler extends Handler {

		public SensorUpdateHandler(Looper looper) {
			super(looper);
		}
	
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
			/** 5.PSensor changed */
			case MSG_UPDATE_PSSOR:
				float valuep = ((Float)msg.obj).floatValue();
				if(valuep < 1) {
					//mPSsorLayout.setBackgroundColor(Color.BLUE);
					mPSsorLinearLayout.setBackgroundColor(Color.BLUE);
					ItemTestActivity.itemActivity.bar.setBackgroundColor(Color.BLUE);
					// set Pass button Clickable
					ItemTestActivity.itemActivity.handler.
						sendEmptyMessage(ItemTestActivity.MSG_BTN_PASS_CLICKABLE);
				} else {
					//mPSsorLayout.setBackgroundColor(mColorWhite);					
					mPSsorLinearLayout.setBackgroundColor(mColorWhite);	
					ItemTestActivity.itemActivity.bar.setBackgroundColor(mColorWhite);
				}
				mtvPssor.setText(""+msg.obj);
				break;
			default:
				break;
			}
		}
	}
}
