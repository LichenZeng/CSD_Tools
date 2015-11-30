package com.mlt.csdfactorytest.item;

import java.util.Locale;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
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
public class SersorMTest extends AbsHardware implements SensorEventListener{
	// ori change value
	private static final float ORI_CHANGE_LEN;
	static {
		ORI_CHANGE_LEN = (float) (9.8f * Math.sin(45*Math.PI/180));
	}
	// Handler's Message : update gsensor
	private final int MSG_UPDATE_MSSOR = 1003;
	
	// SensorManager
	public SensorManager mSsorManager;
	// sensor: msensor 
	public Sensor mssor;
	// Context of Application
	public Context mContext;
	// Gsensor's TextView,show values of GSensor
	public TextView mtvMssor;
	// Gsensor's hint
	private TextView mtvGSensorHint;
	// access Message's of Sensors
	private Handler mHandler;
	// access Message's of Sensors
	private View mLayout;
	// custom view ,show ori of GSensor
	private int mColorWhite;
	// if true,the device has Lsensor,or has no Lsensor
	private boolean hasMSsor;
	/**
	 * construction of class SensorShow 
	 * @param text test item's name
	 * @param visible
	 */
	public SersorMTest(String text, Boolean visible) {
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
		if(!(hasMSsor = SensorTool.hasSensor(mSsorManager, Sensor.TYPE_MAGNETIC_FIELD))) {
			mtvMssor.setText(mContext.getString(R.string.tv_mssor_not_available));
		} else {
			mssor = mSsorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		}
	}
	/**
	 * init all views
	 * @date 2015-1-31 pm 3:53:28
	 */
	private void initView() {
		mLayout = LayoutInflater.from(mContext).inflate(R.layout.item_sensor_m_test, null);
		mtvMssor = (TextView) mLayout.findViewById(R.id.tv_mssor);
		mtvGSensorHint  = (TextView) mLayout.findViewById(R.id.tv_gsensor_hint);
	}

	/**
	 * registerListener of SensorManager
	 * if not have sensor,no register the listener
	 */
	@Override
	public void onResume() {
		if(hasMSsor) {
			mSsorManager.registerListener(this, mssor, SensorManager.SENSOR_DELAY_NORMAL);
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
			/** 3.MAGNETIC_FIELD:SensorEvent type is TYPE_MAGNETIC_FIELD */
			case Sensor.TYPE_MAGNETIC_FIELD:
				sendMSensorChangedMSG(values, msg);
				break;
			default:
				break;
			} // end of switch(event.sensor.getType())
		} // end of synchronized (this)
	} // end of onSensorChanged(SensorEvent event)

	/**
	 * Msensor changed ,send message to update view
	 * @date 2015-2-11 pm 3:31:15
	 * @param values
	 * @param msg
	 */
	private void sendMSensorChangedMSG(float[] values, Message msg) {
		msg = mHandler.obtainMessage(MSG_UPDATE_MSSOR, 
				String.format(Locale.ENGLISH, "X:%+8.4f\nY:%+8.4f\nZ:%+8.4f", 
				values[0], values[1], values[2]));
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
			
			/** 3.MSensor changed */
			case MSG_UPDATE_MSSOR:
				mtvMssor.setText((String)msg.obj);
				ItemTestActivity.itemActivity.handler.
					sendEmptyMessage(ItemTestActivity.MSG_BTN_PASS_CLICKABLE);
				break;
			default:
				break;
			}
		}
	}
}
