package com.mlt.csdfactorytest.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mlt.csdfactorytest.R;
import com.mlt.csdfactorytest.item.tools.ListViewMethod;
import com.mlt.csdfactorytest.item.tools.WifiAdmin;

import android.widget.AdapterView.OnItemClickListener;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

/** 
* @ClassName: WifiTest 
* @Description: add for VFOZBENQ-179
* @Function: TODO ADD FUNCTION
* @author:   peisaisai
* @date:     20150930 am11:21:04 
* @time: 11:21:04 
* Copyright (c) 2015,  Malata All Rights Reserved.
*/
/** 
* @ClassName: WifiTest 
* @Description: add for VFOZBENQ-179
* @Function: add a funtion for connecting wifi
* @author:   peisaisai
* @date:     20151013 am11:21:04 
* @time: 11:21:04 
* Copyright (c) 2015,  Malata All Rights Reserved.
*/

public class WifiTest extends AbsHardware {

    private Context mContext;
    private WifiAdmin mWifiAdmin;
    private ListView mlvWifi;
    private TextView mbtnWifiConnect;
	private EditText metwifiPassword;
    private SimpleAdapter mAdapterWifi;
    private List<Map<String, Object>> mListWifi;
    private final String SHAREPREFERCES_NAME = "TestState";

    private final int SCAN_NUM = 5;

    private final int WIFI_REFRESH = 0;
    private final int WIFI_REFRESH_FINISH = 1;
    private final int WIFI_START = 2;
    private final int WIFI_START_SUCCESS = 3;
    
    private boolean mboolWifiTest = false;

    public WifiTest(String text, Boolean visible) {
        super(text, visible);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        initData();
    }

    private void initData() {
        // TODO Auto-generated method stub
        mListWifi = new ArrayList<Map<String, Object>>();
        mWifiAdmin = new WifiAdmin(mContext);
		mWifiAdmin.openWifi();
		
        mboolWifiTest = false;
        
        // Used to save the current state of mobile test item
        SharedPreferences sp = mContext.getSharedPreferences(
                SHAREPREFERCES_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean("wifistate", mWifiAdmin.mWifiManager.isWifiEnabled());
        editor.commit();
    }

    private void refreshWifi() {
		mWifiAdmin = new WifiAdmin(mContext);
        mWifiAdmin.startScan();
        List<ScanResult> listResults = mWifiAdmin.getWifiList();
		if(listResults.size() <= 0){
			mWifiAdmin.startScan();
			listResults = mWifiAdmin.getWifiList();
		}
        mListWifi.clear();
        Map<String, Object> map;
        if (listResults != null) {

            for (ScanResult scanResult : listResults) {

                // Filter out the WIFI password, keep no WIFI password
                // if (mWifiAdmin.getSecurity(scanResult) !=
                // mWifiAdmin.SECURITY_NONE) {
                // continue;
                // }
                map = new HashMap<String, Object>();
                Log.i("pss", "wifissid : " + scanResult.SSID);
                Log.i("pss", "getSecurity :" + mWifiAdmin.getSecurity(scanResult));
                map.put("ssid", scanResult.SSID);
                map.put("strength", "" + scanResult.level + "dbm");
                map.put("rate", "" + scanResult.frequency + "MHz");
                map.put("mac", "" + scanResult.BSSID);
                map.put("security", "" + scanResult.capabilities);
				if (mWifiAdmin.getSecurity(scanResult) != mWifiAdmin.SECURITY_NONE) {
                    map.put("password",true);
                }else{
					map.put("password",false);
				}
                mListWifi.add(map);
            }
            Log.i("pss", "mListWifi.size() = " + mListWifi.size());
			Message msg = new Message();
			msg.what = WIFI_REFRESH;
			mwifiHandler.sendMessage(msg);
        } else {
            Log.i("pss", "list Results == null");
        }

    }

    private Handler mwifiHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
			case WIFI_START:
				mbtnWifiConnect.setText(mContext.getString(R.string.mtvinitwifi));
				if(mWifiAdmin.mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED){
					startWifiTest();
				}else{
					Message msgsuc = new Message();
					msgsuc.what = WIFI_START_SUCCESS;
					mwifiHandler.sendMessage(msgsuc);
				}
				break;
			case WIFI_START_SUCCESS:
				mbtnWifiConnect.setText(mContext.getString(R.string.mtvinitwifi));
				if(mWifiAdmin.mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED){
					startWifiTest();
				}else{
					Message msgstart = new Message();
					msgstart.what = WIFI_START;
					mwifiHandler.sendMessage(msgstart);
				}
				break;
            case WIFI_REFRESH:
                mbtnWifiConnect.setText(mContext.getString(R.string.mtvwifiscanning));
                updateListview(mListWifi, mAdapterWifi, mlvWifi);
                break;
			case WIFI_REFRESH_FINISH:
				mbtnWifiConnect.setText(mContext.getString(R.string.mtvwifilength)+mListWifi.size());
            default:
                break;
            }
        };
    };

    private void updateListview(List<Map<String, Object>> list,
            SimpleAdapter sAdapter, ListView listview) {
        sAdapter = new SimpleAdapter(mContext, list,
                R.layout.item_wifi_list_item, new String[] { "ssid",
        "strength", "rate", "mac", "security"}, new int[] { R.id.mtvwifissid,
        R.id.mtvwifistrength, R.id.mtvwifirate, R.id.mtvwifimac,R.id.mtvwifisecurity});
        listview.setAdapter(sAdapter);
        ListViewMethod.setListViewHeightBasedOnChildren(listview);
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
		
    }

	private void startWifiTest(){
		if(!mboolWifiTest){
			mboolWifiTest = true;
			new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                int i = SCAN_NUM;
				Message msg1 = new Message();
				msg1.what = WIFI_REFRESH;
				mwifiHandler.sendMessage(msg1);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                refreshWifi();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg = new Message();
				msg.what = WIFI_REFRESH_FINISH;
				mwifiHandler.sendMessage(msg);
            }
			}).start();
		}
	}
	
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
		
		Message msg = new Message();
		msg.what = WIFI_START;
		mwifiHandler.sendMessage(msg);  
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
		mboolWifiTest = true;
		
		Log.i("pss","ondestroy!");
		List<WifiConfiguration> listconfig = mWifiAdmin.getConfiguration();
		if(listconfig != null){
			for(int i = 0; i < listconfig.size();i++){
				mWifiAdmin.forget(listconfig.get(i).networkId);
			}
		}
		
		SharedPreferences sp = mContext.getSharedPreferences(
                SHAREPREFERCES_NAME, Context.MODE_PRIVATE);
		if (!sp.getBoolean("wifistate", false)) {
			mWifiAdmin.closeWifi();
		}else{
			mWifiAdmin.openWifi();
		}
    }

    @Override
    public View getView(Context context) {
        // TODO Auto-generated method stub
        this.mContext = context;
        LayoutInflater factory = LayoutInflater.from(context);
        View view = factory.inflate(R.layout.item_wifi, null);
        mlvWifi = (ListView) view.findViewById(R.id.mlswifi);
        mbtnWifiConnect = (TextView) view.findViewById(R.id.mtvwificon);
		listOnItemLongclick();
        return view;
    }
	
	private void listOnItemLongclick() {
		// TODO Auto-generated method stub
		mlvWifi.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				final String ssid = mListWifi.get(arg2).get("ssid").toString();
				//final boolean password = mListWifi.get(arg2).get("password");
				final boolean password = Boolean.valueOf(mListWifi.get(arg2).get("password").toString());
				Log.i("pss","me click item");
				createDialog(ssid,password);
			}
		});
	}
	
	private void createDialog(final String ssid,final boolean password) {

        LayoutInflater factory = LayoutInflater
                .from(mContext);
        View view = null;
		if(password){
			view = factory.inflate(R.layout.item_wifi_dialog_yes, null);
			metwifiPassword = (EditText) view
                .findViewById(R.id.mtvwifipass);
		}else{
			view = factory.inflate(R.layout.item_wifi_dialog_no, null);
		}

        new AlertDialog.Builder(mContext)
                .setTitle("connect  " + ssid + "?")
                .setView(view)
                .setNegativeButton(mContext.getString(R.string.no),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {
                                // TODO Auto-generated method stub
								dialog.cancel();
                            }
                        })
                .setPositiveButton(
                        mContext.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {
                                // TODO Auto-generated method stub
                                
								Log.i("pss","password = "+password);
								 if (!password) {
                                    mWifiAdmin.addNetwork(mWifiAdmin.createWifiInfo(ssid,"", 2));
                                } else {
                                   mWifiAdmin.addNetwork(mWifiAdmin.createWifiInfo(ssid,metwifiPassword.getText().toString(), 3));
                                }
                            }
                        }).create().show();

    }

}
