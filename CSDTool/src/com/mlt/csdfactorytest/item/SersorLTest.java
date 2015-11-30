package com.mlt.csdfactorytest.item;

import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mlt.csdfactorytest.ItemTestActivity;
import com.mlt.csdfactorytest.customview.GSensorOriView;
import com.mlt.csdfactorytest.item.tools.SensorTool;
import com.mlt.csdfactorytest.R;
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
public class SersorLTest extends AbsHardware implements SensorEventListener{
	// ori change value
	private static final float ORI_CHANGE_LEN;
	static {
		ORI_CHANGE_LEN = (float) (9.8f * Math.sin(45*Math.PI/180));
	}
	// Handler's Message : update lsensor
	private final int MSG_UPDATE_LSSOR = 1004;
	// SensorManager
	public SensorManager mSsorManager;
	// sensor: lsensor
	public Sensor lssor;
	// Context of Application
	public Context mContext;
	// layout of Lsensor
	private RelativeLayout mLSsorLayout;
	private LinearLayout mLSsorLineLayout;
	// Gsensor's TextView,show values of GSensor
	public TextView mtvLssor;
	// access Message's of Sensors
	private Handler mHandler;
	// access Message's of Sensors
	private View mLayout;
	// custom view ,show ori of GSensor
	private int mColorWhite;
	// custom view ,show ori of GSensor
	private float mValueLSensor;
	// if true,the device has Lsensor,or has no Lsensor
	private boolean hasLSsor;
	/**
	 * construction of class SensorShow 
	 * @param text test item's name
	 * @param visible
	 */
	public SersorLTest(String text, Boolean visible) {
		super(text, visible);
	}
	
	@Override
	public View getView(Context context) {
		
		mContext = context;
		
		// init views
		initView();
		// set Title of this Item
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
		
		
		if(!(hasLSsor = SensorTool.hasSensor(mSsorManager, Sensor.TYPE_LIGHT))) {
			mtvLssor.setText(mContext.getString(R.string.tv_lssor_not_available));
		} else {
			lssor = mSsorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
			// set Pass button unClickable
		}
		
	}
	/**
	 * init all views
	 * @date 2015-1-31 pm 3:53:28
	 */
	private void initView() {
		mLayout = LayoutInflater.from(mContext).inflate(R.layout.item_sersor_l_test, null);
		
		mtvLssor = (TextView) mLayout.findViewById(R.id.tv_lssor_value);
		mLSsorLayout = (RelativeLayout) mLayout.findViewById(R.id.rl_lssor);
		mLSsorLayout = (RelativeLayout) mLayout.findViewById(R.id.rl_lssor);
		mLSsorLineLayout = (LinearLayout) mLayout.findViewById(R.id.lsersor_blackground);
	}

	/**
	 * registerListener of SensorManager
	 * if not have sensor,no register the listener
	 */
	@Override
	public void onResume() {
		if(hasLSsor) {
			mSsorManager.registerListener(this, lssor, SensorManager.SENSOR_DELAY_NORMAL);
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
			/** 4.LIGHT:SensorEvent type is TYPE_LIGHT */
			case Sensor.TYPE_LIGHT:
				sendLSensorChangedMSG(values, msg);
				break;
			default:
				break;
			} // end of switch(event.sensor.getType())
		} // end of synchronized (this)
	} // end of onSensorChanged(SensorEvent event)

	/**
	 * Lsensor changed ,send message to update view
	 * @date 2015-2-11 pm 3:31:15
	 * @param values
	 * @param msg
	 */
	private void sendLSensorChangedMSG(float[] values, Message msg) {
		msg = mHandler.obtainMessage(MSG_UPDATE_LSSOR, values[0]);
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
			/** 4.LSensor changed */
			case MSG_UPDATE_LSSOR:
				float valuel = ((Float)msg.obj).floatValue();
				if(valuel > 10 /* laiyang change value 20150926, old:30 */) {
					//mLSsorLayout.setBackgroundColor(mColorWhite);
					mLSsorLineLayout.setBackgroundColor(mColorWhite);
					ItemTestActivity.itemActivity.bar.setBackgroundColor(mColorWhite);
				} else {
					//mLSsorLayout.setBackgroundColor(Color.GREEN);
					mLSsorLineLayout.setBackgroundColor(Color.GREEN);
					ItemTestActivity.itemActivity.bar.setBackgroundColor(Color.GREEN);
					ItemTestActivity.itemActivity.handler.
						sendEmptyMessage(ItemTestActivity.MSG_BTN_PASS_CLICKABLE);
				}
				mtvLssor.setText(""+msg.obj);
				break;
			default:
				break;
			}
		}
	}
}
