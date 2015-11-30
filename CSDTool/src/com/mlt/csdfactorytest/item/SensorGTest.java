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
public class SensorGTest extends AbsHardware implements SensorEventListener{
	// ori change value
	private static final float ORI_CHANGE_LEN;
	static {
		ORI_CHANGE_LEN = (float) (9.8f * Math.sin(45*Math.PI/180));
	}
	// Handler's Message : update gsensor
	private final int MSG_UPDATE_GSSOR = 1002;
	// SensorManager
	public SensorManager mSsorManager;
	// sensor: gyroscope
	public Sensor gssor;
	// Context of Application
	public Context mContext;
	// Context of Application
	public TextView mtvGssor;
	// access Message's of Sensors
	private Handler mHandler;
	// access Message's of Sensors
	private View mLayout;
	// custom view ,show ori of GSensor
	private GSensorOriView mGSsorView;
	// custom view ,show ori of GSensor
	private int mColorWhite;
	// if true,the device has Gsensor,or has no Gsensor
	private boolean hasGSsor;
	/**
	 * construction of class SensorShow 
	 * @param text test item's name
	 * @param visible
	 */
	public SensorGTest(String text, Boolean visible) {
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
		
		if(hasGSsor = SensorTool.hasSensor(mSsorManager, Sensor.TYPE_ACCELEROMETER)){
			gssor = mSsorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			//mtvGSensorHint.setVisibility(View.GONE);
		} else {
			mtvGssor.setText(mContext.getString(R.string.tv_gssor_not_available));
		} 
	}
	/**
	 * init all views
	 * @date 2015-1-31 pm 3:53:28
	 */
	private void initView() {
		mLayout = LayoutInflater.from(mContext).inflate(R.layout.item_sersor_g_test, null);
		mGSsorView = (GSensorOriView) mLayout.findViewById(R.id.gssor_ori);
		mtvGssor = (TextView) mLayout.findViewById(R.id.tv_gssor);
	}

	/**
	 * registerListener of SensorManager
	 * if not have sensor,no register the listener
	 */
	@Override
	public void onResume() {
		if(hasGSsor) {
			mSsorManager.registerListener(this, gssor, SensorManager.SENSOR_DELAY_NORMAL);
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
			/** 1.ACCELEROMETER:SensorEvent type is TYPE_ACCELEROMETER */
			case Sensor.TYPE_ACCELEROMETER:
				sendGSensorChangedMSG(values, msg);
				break;
			default:
				break;
			} // end of switch(event.sensor.getType())
		} // end of synchronized (this)
	} // end of onSensorChanged(SensorEvent event)


	/**
	 * Gsensor changed ,send message to update view
	 * @date 2015-2-11 pm 3:31:15
	 * @param values
	 * @param msg
	 */
	private void sendGSensorChangedMSG(float[] values, Message msg) {
		msg = mHandler.obtainMessage(MSG_UPDATE_GSSOR, 
				String.format(Locale.ENGLISH, "X:%+8.4f\nY:%+8.4f\nZ:%+8.4f", 
			values[0], values[1], values[2]));
		mHandler.sendMessage(msg);
		if(values[0] > ORI_CHANGE_LEN || values[0] < -ORI_CHANGE_LEN ||
			values[1] > ORI_CHANGE_LEN || values[1] < -ORI_CHANGE_LEN ||
					values[2] > ORI_CHANGE_LEN || values[2] < -ORI_CHANGE_LEN ) {
			updateGssorView(values[0], values[1], values[2]);
		}
		
		if(mGSsorView.getmOriTestSum() == GSensorOriView.ORI_SUM) {
			ItemTestActivity.itemActivity.handler.
				sendEmptyMessage(ItemTestActivity.MSG_BTN_PASS_CLICKABLE);
		}
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
		mGSsorView.clearViewState();
		super.onPause();
	}
	/**
	 * 
	 * @date 2015-2-11 pm 3:29:36
	 * @param x Gx 
	 * @param y Gy
	 * @param z Gz
	 */
	public void updateGssorView(float x, float y, float z) {
		if(x > ORI_CHANGE_LEN && !mGSsorView.isRight) {
			mGSsorView.isRight = true;
			mGSsorView.addmOriTestSum();
		} 
		if(x < -ORI_CHANGE_LEN && !mGSsorView.isLeft) {
			mGSsorView.isLeft = true;
			mGSsorView.addmOriTestSum();
		}
		if(y < -ORI_CHANGE_LEN && !mGSsorView.isDown) {
			mGSsorView.isDown = true;
			mGSsorView.addmOriTestSum();
		}
		if(y > ORI_CHANGE_LEN && !mGSsorView.isUp) {
			mGSsorView.isUp = true;
			mGSsorView.addmOriTestSum();
		}
		if(z > ORI_CHANGE_LEN && !mGSsorView.isPositive) {
			mGSsorView.isPositive = true;
			mGSsorView.addmOriTestSum();
		}
		if(z < -ORI_CHANGE_LEN && !mGSsorView.isNegative) {
			mGSsorView.isNegative = true;
			mGSsorView.addmOriTestSum();
			Log.i("hah", ""+z);
		}
		mGSsorView.invalidate();
	}
	
	class SensorUpdateHandler extends Handler {

		public SensorUpdateHandler(Looper looper) {
			super(looper);
		}
	
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
			/** 2.GSensor changed */
			case MSG_UPDATE_GSSOR:
				mtvGssor.setText((String)msg.obj);
				break;
			default:
				break;
			}
		}
	}
}
