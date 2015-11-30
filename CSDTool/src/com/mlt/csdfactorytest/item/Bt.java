package com.mlt.csdfactorytest.item;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set; 

import android.R.bool;
import android.R.color;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mlt.csdfactorytest.R;
import com.mlt.csdfactorytest.ItemTestActivity;
import com.mlt.csdfactorytest.item.thread.WifiThread;
import com.mlt.csdfactorytest.item.tools.BTAdmin;
import com.mlt.csdfactorytest.item.tools.GPSAdmin;
import com.mlt.csdfactorytest.item.tools.ListViewMethod;
import com.mlt.csdfactorytest.item.tools.WifiAdmin;


import java.lang.reflect.Method;

/**
 * @ClassName: WIFI_BT_GPS
 * @Description: test wifi/BT/GPS and show the result of test
 * @Function: Wifi, bluetooth, GPS star search positioning.The list of the three
 *            test results are, shows the result is divided into two: ssid and
 *            strength.At the same time the wifi is sorted according to the
 *            signal strength size sequence
 * @author: peisaisai
 * @date: 2015-01-15 2:10:04 Copyright (c) 2015, Malata All Rights Reserved.
 */
 
 /** 
* @ClassName: Bt 
* @Description: modify for VFOZBENQ-209
* @Function: TODO ADD FUNCTION
* @author:   peisaisai
* @date:     20151008 am11:33:58 
* @time: 11:33:58 
* Copyright (c) 2015,  Malata All Rights Reserved.
*/

 /** 
* @ClassName: Bt 
* @Description: modify for VFOZBENQ-209
* @Function: clear the info of matched BT
* @author:   peisaisai
* @date:     20151008 am11:33:58 
* @time: 11:33:58 
* Copyright (c) 2015,  Malata All Rights Reserved.
*/
 
public class Bt extends AbsHardware {
    public static Bt mWifiBtGps;

    // Some of the arg0 handler message queue
    private final int MSG_UPDATE_INITFAIL_BT = 6;
    private final int MSG_UPDATE_INIT_BT = 7;
    private final int MSG_UPDATE_BT_LISTVIEW = 8;
    private final int MSG_UPDATE_BT_FINISH = 9;
    private final int MSG_UPDATE_BT_SEARCHING = 10;
	
    private final int MSG_UPDATE_BT_REFRESH = 11; 	
    
    private final int BTTEST_MAX_TIME = 30;

    private final int BT_MAX_NUM = 3;

    private final String SHAREPREFERCES_NAME = "TestState";

	private Context mContext;

	// Out of the control switch threads
	private boolean mIsBtExit;
	
	private boolean mIsBtTestExit;
	private boolean mIsClick;
	
	
    // the switch to control the threads of timer
    private boolean mIsBtTimeThreadExit;

    // ID is textview display text
    private TextView mbtnBtConnect;

    // private WifiManager wifiManager;
    private BTAdmin mBtAdmin;
    private ListView mlvBt;
    private SimpleAdapter mAdapterBt;
    private List<Map<String, Object>> mListBt;
    private List<Map<String, Object>> mListBtState;
	
	private Set<BluetoothDevice> devices;
	
    public Bt(String text, Boolean visible) {
        super(text, visible);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mBtReceiver, filter);
        mWifiBtGps = Bt.this;
        Log.i("pss", "onResume");
    }

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i("pss", "onPause");
		mContext.unregisterReceiver(mBtReceiver);
		if (mBtAdmin.mAdapter.isDiscovering()) {
//			mBtAdmin.mAdapter.cancelDiscovery();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @Description:The data clean up and close the wifi, bt, GPS
	 * 
	 * @see com.malata.factorytest.item.AbsHardware#onDestory() Copyright (c)
	 * 2015, Malata All Rights Reserved.
	 */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i("pss", "onDestroy");
		closeWifiBtGpsTest();
	}

	public void closeWifiBtGpsTest() {
		Log.i("pss", "colse start");
		
		if (mListBt != null) { 
            for(int i =0; i < mListBt.size(); i++){
				BluetoothDevice device = (BluetoothDevice) mListBt.get(i).get("device");
                if(BluetoothDevice.BOND_BONDED == device.getBondState() && !devices.contains(device)){
					
					try{
						Method removeBondMethod = device.getClass().getMethod("removeBond");
						Boolean returnValue = (Boolean) removeBondMethod.invoke(device);
					} catch (Exception e) {
                    // TODO: handle exception
					}
                }
			}
        }
		
		mIsClick = true;
		mIsBtTestExit = true;
		mIsBtExit = true;
		mIsBtTimeThreadExit = true;
		if (mBtAdmin.mAdapter.isDiscovering()) {
			mBtAdmin.mAdapter.cancelDiscovery();
		}
		SharedPreferences sp = mContext.getSharedPreferences(
				SHAREPREFERCES_NAME, Context.MODE_PRIVATE);
		if (sp.getInt("btstate", 0) == BluetoothAdapter.STATE_OFF) {
			mBtAdmin.closeBT();
		}else{
			mBtAdmin.openBT();
		}
		mListBt.clear();
		Log.i("pss", "colse end");
	}

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.i("pss", "oncreate");
        startWifiBtGpsTest();
    }

    /**
     * @MethodName: startWifiBtGpsTest
     * @Description: start wifi-bt-gps Testing
     * @return void
     * @throws Copyright
     *             (c) 2015, Malata All Rights Reserved.
     */
    public void startWifiBtGpsTest() {
		mIsClick = false;
		mIsBtTestExit  = false;
        mIsBtExit = false;
        mIsBtTimeThreadExit = false;
        mBtAdmin = new BTAdmin(mContext);
        // Used to save the current state of mobile test item
        SharedPreferences sp = mContext.getSharedPreferences(
                SHAREPREFERCES_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        Log.i("pss", "mBtAdmin:" + mBtAdmin + "MAdapter:" + mAdapterBt);
        editor.putInt("btstate", mBtAdmin.mAdapter.getState());
        editor.commit();
        
        startBtTest();
        
    }

	/**
	 * @Fields: bReceiver TODO��Receiving bluetooth search information, when the
	 *          search to a device will be added to the list, and determine
	 *          whether the number more than three, if more than three, it will
	 *          send search success.If the search is complete, the judge has at
	 *          least one device in the list, only to send information search
	 *          success.
	 */
	private BroadcastReceiver mBtReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Map<String, Object> map = new HashMap<String, Object>();
				 
                if (BluetoothDevice.BOND_NONE == device.getBondState()) {
                    map.put("ssid", "No Match " + device.getName()); 
                }else if(BluetoothDevice.BOND_BONDED == device.getBondState()){
                    map.put("ssid", "Match   " + device.getName()); 
                }
                map.put("strength",
                        ""
                                + intent.getExtras().getShort(
                                        BluetoothDevice.EXTRA_RSSI));
				map.put("device", device); 
				
                /* before if ((mListBt.size() < BT_MAX_NUM) && !mIsBtTimeThreadExit) {
                    mListBt.add(map);
                    sendMsg(MSG_UPDATE_BT_LISTVIEW);
                } else if (mListBt.size() >= BT_MAX_NUM) {
                    mIsBtExit = true;
                    sendMsg(MSG_UPDATE_BT_FINISH);
                }*/
				if (!mIsBtTimeThreadExit) {
                    mListBt.add(map);
                    sendMsg(MSG_UPDATE_BT_LISTVIEW);
                } 

            } else if (action
                    .equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                if (mListBt.size() != 0) {
                    mIsBtExit = true;
                }
            }
        }

	};

	/** 
	* @MethodName: closeBtTest 
	* @Description:After the BT test, have to do operation  
	* @return void   
	* @throws 
	* Copyright (c) 2015,  Malata All Rights Reserved.
	*/
	private void closeBtTest() {
	
		//pss del for VFOZBENQ-209 20151007
		//SharedPreferences sp1 = mContext.getSharedPreferences(
		//		SHAREPREFERCES_NAME, Context.MODE_PRIVATE);

		//if (sp1.getInt("btstate", 0) == BluetoothAdapter.STATE_OFF) {
		//	mBtAdmin.closeBT();
		//}

		if (mBtAdmin.mAdapter.isDiscovering()) {
			mBtAdmin.mAdapter.cancelDiscovery();
		}
		mIsBtTimeThreadExit = true;
		mIsBtExit = true;
	}

	/**
	 * @MethodName: BT_Test
	 * @Description:Bluetooth test, first of all empty list, whether the
	 *                        bluetooth open, then to search in the search
	 *                        process, will not be repeated search
	 * @return void
	 * @throws Copyright
	 *             (c) 2015, Malata All Rights Reserved.
	 */
	private void startBtTest() {
		// TODO Auto-generated method stub
		mBtAdmin.openBT();
		sendMsg(MSG_UPDATE_INIT_BT);
		mListBt = new ArrayList<Map<String, Object>>();
		mListBtState = new ArrayList<Map<String, Object>>();
		final long preTime = System.currentTimeMillis();
		new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                long lasTime;
                while (true) {
                    if (unexpectedShutdown()) {
                        break;
                    }
                    if (mIsBtExit) {
                        break;
                    }
                    lasTime = System.currentTimeMillis();
                    if (BTTEST_MAX_TIME <= (lasTime - preTime) / 1000) {
                        if ((!mBtAdmin.mAdapter.isEnabled()) && (!mIsBtExit)) {
                            sendMsg(MSG_UPDATE_INITFAIL_BT);
                            Log.i("pss",
                                    "send a message:MSG_UPDATE_INITFAIL_BT");
                        } else {
                            mIsBtExit = true;
                        }
                        break;
                    }
                    if (mIsBtExit) {
                        break;
                    }
                    if (mIsBtTimeThreadExit) {
                        break;
                    }
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    if (unexpectedShutdown()) {
                        break;
                    }
                    if (mIsBtExit) {
                        sendMsg(MSG_UPDATE_BT_FINISH);
                        break;
                    }
                    if (mBtAdmin.mAdapter.isEnabled()) {
                        if (!mBtAdmin.mAdapter.isDiscovering()) {
                            mListBt.clear();
                            mBtAdmin.searchBT();
                            if (!mIsBtExit) {
                                sendMsg(MSG_UPDATE_BT_SEARCHING);
                            }
                            // break;
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * @Fields: handler Technique of information update picture.
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.arg1) {
            case MSG_UPDATE_INIT_BT:
                mbtnBtConnect.setText(mContext.getString(R.string.mtvinitbt));
                break;
            case MSG_UPDATE_INITFAIL_BT:
                Log.i("pss", "receive a message:" + MSG_UPDATE_INITFAIL_BT);
                mbtnBtConnect.setText(mContext.getString(R.string.mtvinitbtfail));
                mbtnBtConnect.setBackgroundColor(Color.RED);
                closeBtTest();
                break;

            // update the listview of BT
            case MSG_UPDATE_BT_LISTVIEW:
                Collections.sort(mListBt, new ListComparator());
                updateListview(mListBt, mAdapterBt, mlvBt);
                break;

            case MSG_UPDATE_BT_SEARCHING:
                mbtnBtConnect.setText(mContext.getString(R.string.mtvbtscanning));
                break;
			
			case MSG_UPDATE_BT_REFRESH:
				Log.i("pss","receive time handle");
				if (mListBt != null) {
                    for(int i =0; i < mListBt.size(); i++){
                        BluetoothDevice device = (BluetoothDevice) mListBt.get(i).get("device");
                        if (BluetoothDevice.BOND_NONE == device.getBondState()) {
                            mListBt.get(i).put("ssid", "No Match " + device.getName()); 
                        }else if(BluetoothDevice.BOND_BONDED == device.getBondState()){
                            mListBt.get(i).put("ssid", "Match   " + device.getName()); 
                        }
                    }
                    updateListview(mListBt, mAdapterBt, mlvBt);
                }
				if(!mIsBtTestExit){
					mTimeHandler.sendEmptyMessage(0);
				}
                break;
			
            // searching bt finshed
            case MSG_UPDATE_BT_FINISH:
				devices = mBtAdmin.mAdapter.getBondedDevices(); 
                if (mListBt.size() > 0) {
                    mbtnBtConnect.setText(mContext
                            .getString(R.string.mtvbtsearsuccess));
                } else {
                    mbtnBtConnect.setText(mContext
                            .getString(R.string.mtvbtsearfail));
                }
                closeBtTest();
				mlvBt.setEnabled(true);
                break;
            default:
                break;
            }
        }

	};
	
	private Handler mTimeHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            Message msg1 = new Message();
            msg1.arg1 = MSG_UPDATE_BT_REFRESH;
            handler.sendMessageDelayed(msg1, 500);
			Log.i("pss","send time handle");
        };
    };

    /**
     * @MethodName: updateListview
     * @Description:TODO
     * @param list
     * @param sAdapter
     * @param listview
     * @return void
     * @throws Copyright
     *             (c) 2015, Malata All Rights Reserved.
     */
    private void updateListview(List<Map<String, Object>> list,
            SimpleAdapter sAdapter, ListView listview) {
        sAdapter = new SimpleAdapter(mContext, list,
                R.layout.item_bt_list_item, new String[] { "ssid",
                        "strength" }, new int[] { R.id.mtvbtnum,
                        R.id.mtvbtstrength });
        listview.setAdapter(sAdapter);
        ListViewMethod.setListViewHeightBasedOnChildren(listview);
    }

	private void sendMsg(int msgArg1) {
		Message msg = new Message();
		msg.arg1 = msgArg1;
		handler.sendMessage(msg);
	}

	@Override
	public View getView(Context context) {
		// TODO Auto-generated method stub
		this.mContext = context;
		LayoutInflater factory = LayoutInflater.from(context);
		View view = factory.inflate(R.layout.item_bt, null);
		initList();
		initModule(view);
		return view;
	}

	private void initList() {
		// TODO Auto-generated method stub
		mListBt = new ArrayList<Map<String, Object>>();
	}

    private void initModule(View view) {
        // TODO Auto-generated method stub
        mbtnBtConnect = (TextView) view.findViewById(R.id.mtvbtcon);
        mlvBt = (ListView) view.findViewById(R.id.mlsbt);
		clickListItem(); 
	}
    
	
	private void clickListItem() {
        mlvBt.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position,
                    long arg3) {
                // TODO Auto-generated method stub
                
                BluetoothDevice device = (BluetoothDevice) mListBt.get(position).get("device");
                try {
                    Method createBondMethod = device.getClass().getMethod(
                            "createBond");
                    Boolean returnValue = (Boolean) createBondMethod
                            .invoke(device);
                } catch (Exception e) {
                    // TODO: handle exception
                }
				
				if(!mIsClick){
					mIsClick = true;
					mTimeHandler.sendEmptyMessage(0);
				}
				
				
            }
        });
		mlvBt.setEnabled(false);
	}
    
	/**
	 * @ClassName: ListComparator
	 * @Description: To sort the list
	 * @Function: According to the growing up of sequence order
	 * @author: peisaisai
	 * @date: 2015-01-15 14:38:11 Copyright (c) 2015, Malata All Rights
	 *        Reserved.
	 */
	private class ListComparator implements Comparator<Map<String, Object>> {

		@Override
		public int compare(Map<String, Object> map1, Map<String, Object> map2) {
			// TODO Auto-generated method stub

			return (Integer.parseInt(map2.get("strength").toString()))
					- (Integer.parseInt(map1.get("strength").toString()));

		}

    }
    
    /** 
    * @MethodName: unexpectedShutdown 
    * @Description:If the program closed unexpectedly, return true
    *              else return false;
    * @return  
    * @return boolean   
    * @throws 
    * Copyright (c) 2015,  mlt All Rights Reserved.
    */
    private boolean unexpectedShutdown(){
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        //Log.i("pss", ""+activityManager.getRunningTasks(1).get(0).topActivity
        //        .getClassName());
        try {
            if (activityManager.getRunningTasks(1).get(0).topActivity
                    .getClassName().equals("com.mlt.csdfactorytest.ItemTestActivity")) {
                return false;
            }
        } catch (NullPointerException e) {
        }
        onDestroy();
        return true;
    }
    

}
