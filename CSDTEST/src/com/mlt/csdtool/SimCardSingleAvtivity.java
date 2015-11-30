package com.mlt.csdtool;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.mlt.csdtool.R.string;

import android.os.SystemProperties;

//import com.android.settings.Utils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * @ClassName: SimCardSingleAvtivity
 * @Description: Mobile signal strength testing
 * @Function: 2G/3G/4G signal intensity can be detected
 * @author: peisaisai
 * @date: 20150821 pm3:26:47
 * @time: pm3:26:47 Copyright (c) 2015, Malata All Rights Reserved.
 */
/** 
* @ClassName: SimCardSingleAvtivity 
* @Description: add for VFOZBENQ-199
* @author:   peisaisai
* @date:     20151007 am10:33:13 
* @time: 10:33:13 
* Copyright (c) 2015,  Malata All Rights Reserved.
*/
public class SimCardSingleAvtivity extends Activity {
    private RadioGroup mrgSimSelector;
    private RadioButton mrbSim1, mrbSim2;
    private TextView mtvSimInfo, mtvSimSingle;
    private Intent mSimIntent;

    private TelephonyManager mTelephonyManager;
    private PhoneStateListener mPhoneStateListener;
    private Phone mPhone = null;

    private SubscriptionInfo mSirSim1;
    private SubscriptionInfo mSirSim2;
    private List<SubscriptionInfo> mSelectableSubInfos = new ArrayList<SubscriptionInfo>();
    private final int SIMONE_TYPE = 0;
    private final int SIMTWO_TYPE = 1;

    private final String GSMTWOG = "gsmtwo";
    private final String GSMTHREEG = "gsmthree";
    private final String GSMFOURG = "gsmfour";
    private final String GSMNAME = "simSingle";
    private String[] mSingleLevel;
    private final int DBMMIN = 0;
    private final int ASUMAX = 99;

    private MyHandler mHandler;

    /** Unknown network class. {@hide} */
    private final int NETWORK_CLASS_UNKNOWN = 0;
    /** Class of broadly defined "2G" networks. {@hide} */
    private final int NETWORK_CLASS_2_G = 1;
    /** Class of broadly defined "3G" networks. {@hide} */
    private final int NETWORK_CLASS_3_G = 2;
    /** Class of broadly defined "4G" networks. {@hide} */
    private final int NETWORK_CLASS_4_G = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.item_simcardsingle_activity);
        InitComponent();
    }
    
    
    final int preferredNetworkMode = Phone.PREFERRED_NT_MODE;
    final String SHAREPREFERCES_NAME = "siminfo";
    private int getPhoneInfo(final SubscriptionInfo sir){
        if (sir != null) {
            final Phone phone = PhoneFactory.getPhone(SubscriptionManager
                    .getPhoneId(sir.getSubscriptionId()));
            mPhone = phone;
            int phoneSubId = mPhone.getSubId();
            int settingsNetworkMode = android.provider.Settings.Global.getInt(mPhone.getContext().
                    getContentResolver(),
                    android.provider.Settings.Global.PREFERRED_NETWORK_MODE + phoneSubId,
                    preferredNetworkMode);
            return settingsNetworkMode;
        }
        return preferredNetworkMode;
    }
    
    private void saveSimInfo(SubscriptionInfo mSim1Info,SubscriptionInfo mSim2Info){
        
        
        SharedPreferences sp = this.getSharedPreferences(
                SHAREPREFERCES_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        
        
        if (mSim1Info == null) {
            editor.putInt("sim1net", -1);
        }else{
            editor.putInt("sim1net", getPhoneInfo(mSim1Info));
        }
        
        if(mSim2Info == null){
            editor.putInt("sim2net", -1);
        }else{
            editor.putInt("sim2net", getPhoneInfo(mSim2Info));
        }
        editor.commit();
            
    }
    
    private void recoveryPhoneInfo(final SubscriptionInfo sir,int networkMode){
        if (sir != null) {
            final Phone phone = PhoneFactory.getPhone(SubscriptionManager
                    .getPhoneId(sir.getSubscriptionId()));
            mPhone = phone;
            int phoneSubId = mPhone.getSubId();
            
            android.provider.Settings.Global.putInt(mPhone.getContext()
                    .getContentResolver(),
                    android.provider.Settings.Global.PREFERRED_NETWORK_MODE
                            + phoneSubId, networkMode);
            mPhone.setPreferredNetworkType(
                    networkMode,
                    mHandler.obtainMessage(MyHandler.MESSAGE_SET_PREFERRED_NETWORK_TYPE));
            
        }
    }
    
    private void  recoverySimInfo(SubscriptionInfo mSim1Info,SubscriptionInfo mSim2Info){
        SharedPreferences sp = this.getSharedPreferences(
                SHAREPREFERCES_NAME, Context.MODE_PRIVATE);
        if ( mSim1Info != null) {
            int netWorkType = sp.getInt("sim1net", -1);
            recoveryPhoneInfo(mSim1Info, netWorkType);
        }
        if (mSim2Info != null) {
            int netWorkType = sp.getInt("sim2net", -1);
            recoveryPhoneInfo(mSim2Info, netWorkType);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        setLogInfo();
        mTelephonyManager.listen(mPhoneStateListener,
                PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                        | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                        | PhoneStateListener.LISTEN_SERVICE_STATE);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        recoverySimInfo(mSirSim1, mSirSim2);
    }

    private void setSimTitle() {
        String temp = "";
        if (mSimIntent.getStringExtra(GSMNAME).equals(GSMTWOG)) {
            temp = this.getString(R.string.rssi);
        } else if (mSimIntent.getStringExtra(GSMNAME).equals(GSMTHREEG)) {
            temp = this.getString(R.string.rscp);
        } else if (mSimIntent.getStringExtra(GSMNAME).equals(GSMFOURG)) {
            temp = this.getString(R.string.rsrp);
        }
        mtvSimInfo.setText(temp);
    }

    /**
     * @MethodName: setSimSingle
     * @Description:Signal information displayed on mobile phone cards
     * @param type
     * @param dbm
     * @param asu
     * @param hasSim
     * @return void
     * @throws Copyright
     *             (c) 2015, Malata All Rights Reserved.
     */
    private void setSimSingle(int type, int dbm, int asu, boolean hasSim) {
        if (hasSim) {
            String level;
            if (dbm < -110) {
                level = mSingleLevel[mSingleLevel.length - 1];
            } else if (dbm >= -92) {
                level = mSingleLevel[0];
            } else if (dbm >= -98) {
                level = mSingleLevel[1];
            } else if (dbm >= -104) {
                level = mSingleLevel[2];
            } else {
                level = mSingleLevel[3];
            }
            if (SIMONE_TYPE == type) {
                mtvSimSingle.setText(this.getString(R.string.sim1)
                        + this.getString(R.string.colon) + dbm
                        + this.getString(R.string.dbm) + " " + asu
                        + this.getString(R.string.asu) + " (" + level + ")");
            } else if (SIMTWO_TYPE == type) {
                mtvSimSingle.setText(this.getString(R.string.sim2)
                        + this.getString(R.string.colon) + dbm
                        + this.getString(R.string.dbm) + " " + asu
                        + this.getString(R.string.asu) + " (" + level + ")");
            }
        } else {
            if (SIMONE_TYPE == type) {
                mtvSimSingle.setText(this.getString(R.string.sim1)
                        + this.getString(R.string.colon) + dbm
                        + this.getString(R.string.dbm) + " " + asu
                        + this.getString(R.string.asu)
                        + this.getString(R.string.nosim));
            } else if (SIMTWO_TYPE == type) {
                mtvSimSingle.setText(this.getString(R.string.sim2)
                        + this.getString(R.string.colon) + dbm
                        + this.getString(R.string.dbm) + " " + asu
                        + this.getString(R.string.asu)
                        + this.getString(R.string.nosim));
            }
        }
    }

    /**
     * @MethodName: InitComponent
     * @Description:Initialization of control and data
     * @return void
     * @throws Copyright
     *             (c) 2015, Malata All Rights Reserved.
     */
    private void InitComponent() {
        // TODO Auto-generated method stub
        mHandler = new MyHandler();
        mSimIntent = getIntent();
        mSingleLevel = getResources().getStringArray(R.array.singlelevel);
        mtvSimInfo = (TextView) findViewById(R.id.tvsiminfo);
        mtvSimSingle = (TextView) findViewById(R.id.tvsimsingle);

        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        mrgSimSelector = (RadioGroup) findViewById(R.id.radiosim);
        mrgSimSelector.setOnCheckedChangeListener(mSimCardSelector);
        mrbSim1 = (RadioButton) findViewById(R.id.rbtsim1);
        mrbSim1.setChecked(true);
        mrbSim2 = (RadioButton) findViewById(R.id.rbtsim2);

        mSirSim1 = findRecordBySlotId(this, SIMONE_TYPE);
        mSirSim2 = findRecordBySlotId(this, SIMTWO_TYPE);
        
        saveSimInfo(mSirSim1, mSirSim2);
        
        if (!isGeminiEnabled()) {
            mrbSim2.setVisibility(View.GONE);
        }
        if (mSirSim1 == null) {
            setSimSingle(SIMONE_TYPE, DBMMIN, ASUMAX, false);
        } else {
            updatePhoneInfo(mSirSim1, SIMONE_TYPE);
        }
        setSimTitle();

    }

    /**
     * @MethodName: findRecordBySlotId
     * @Description:Mobile phone cards to detect all cards in the slot
     * @param context
     * @param slotId
     * @return
     * @return SubscriptionInfo
     * @throws Copyright
     *             (c) 2015, Malata All Rights Reserved.
     */
    public SubscriptionInfo findRecordBySlotId(Context context, final int slotId) {
        final List<SubscriptionInfo> subInfoList = SubscriptionManager.from(
                context).getActiveSubscriptionInfoList();
        if (subInfoList != null) {
            final int subInfoLength = subInfoList.size();

            for (int i = 0; i < subInfoLength; ++i) {
                final SubscriptionInfo sir = subInfoList.get(i);
                if (sir.getSimSlotIndex() == slotId) {
                    // Right now we take the first subscription on a SIM.
                    return sir;
                }
            }
        }
        return null;
    }

    /**
     * @MethodName: updatePhoneInfo
     * @Description:Update the information of the mobile phone card and monitor
     *                     the mobile phone card information
     * @param sir
     * @param type
     * @return void
     * @throws Copyright
     *             (c) 2015, Malata All Rights Reserved.
     */
    private void updatePhoneInfo(final SubscriptionInfo sir, final int type) {
        if (sir != null) {
            final Phone phone = PhoneFactory.getPhone(SubscriptionManager
                    .getPhoneId(sir.getSubscriptionId()));
            mPhone = phone;
            int phoneSubId = mPhone.getSubId();

            int networkMode = -1;
            if (mSimIntent.getStringExtra(GSMNAME).equals(GSMTWOG)) {
                networkMode = mPhone.NT_MODE_GSM_ONLY;
            } else if (mSimIntent.getStringExtra(GSMNAME).equals(GSMTHREEG)) {
                networkMode = mPhone.NT_MODE_WCDMA_PREF;
            } else if (mSimIntent.getStringExtra(GSMNAME).equals(GSMFOURG)) {
                networkMode = mPhone.NT_MODE_LTE_GSM_WCDMA;
            }
            android.provider.Settings.Global.putInt(mPhone.getContext()
                    .getContentResolver(),
                    android.provider.Settings.Global.PREFERRED_NETWORK_MODE
                            + phoneSubId, networkMode);
            mPhone.setPreferredNetworkType(
                    networkMode,
                    mHandler.obtainMessage(MyHandler.MESSAGE_SET_PREFERRED_NETWORK_TYPE));

            if (mPhoneStateListener != null) {
                mTelephonyManager.listen(mPhoneStateListener,
                        PhoneStateListener.LISTEN_NONE);
            }

            mPhoneStateListener = new PhoneStateListener(
                    sir.getSubscriptionId()) {
                @Override
                public void onDataConnectionStateChanged(int state) {

                }

                @Override
                public void onSignalStrengthsChanged(
                        SignalStrength signalStrength) {
                    if (getNetWorkIntentString(
                            mTelephonyManager.getNetworkClass(mTelephonyManager
                                    .getNetworkType(sir.getSubscriptionId())))
                            .equals(mSimIntent.getStringExtra(GSMNAME))) {
                        setSimSingle(type, signalStrength.getDbm(),
                                signalStrength.getAsuLevel(), true);
                    } else {
                        mtvSimSingle.setText(getIntentString(mSimIntent
                                .getStringExtra(GSMNAME))
                                + SimCardSingleAvtivity.this
                                        .getString(R.string.nosingle));
                    }
                }

                @Override
                public void onServiceStateChanged(ServiceState serviceState) {

                }
            };
            mTelephonyManager.listen(mPhoneStateListener,
                    PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                            | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                            | PhoneStateListener.LISTEN_SERVICE_STATE);
        }

    }

    /**
     * @Fields: mSimCardSelector TODO：Dual card mobile phone can't monitor the
     *          signal intensity of the dual card, therefore, a selector is
     *          added to make a choice.
     */
    private OnCheckedChangeListener mSimCardSelector = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup arg0, int id) {
            // TODO Auto-generated method stub
            if (id == R.id.rbtsim1) {
                if (mSirSim1 != null) {
                    updatePhoneInfo(mSirSim1, SIMONE_TYPE);
                } else {
                    setSimSingle(SIMONE_TYPE, DBMMIN, ASUMAX, false);
                }
            } else if (id == R.id.rbtsim2) {
                if (mSirSim2 != null) {
                    updatePhoneInfo(mSirSim2, SIMTWO_TYPE);
                } else {
                    setSimSingle(SIMTWO_TYPE, DBMMIN, ASUMAX, false);
                }
            }
        }
    };

    /**
     * @MethodName: isGeminiEnabled
     * @Description:Detection of mobile phone is single or double card,return
     *                        true is single,or double
     * @return
     * @return boolean
     * @throws Copyright
     *             (c) 2015, Malata All Rights Reserved.
     */
    public static boolean isGeminiEnabled() {
        // return FeatureOption.MTK_GEMINI_SUPPORT;
        return getValue("ro.mtk_gemini_support");
    }

    private static boolean getValue(String key) {
        return SystemProperties.get(key).equals("1");
    }

    private String getIntentString(String intent) {
        switch (intent) {
        case GSMTWOG:
            return "2G";
        case GSMTHREEG:
            return "3G";
        case GSMFOURG:
            return "4G";
        default:
            break;
        }
        return "none";
    }

    private String getNetWorkIntentString(int networkClass) {
        switch (networkClass) {
        case NETWORK_CLASS_2_G:
            return GSMTWOG;
        case NETWORK_CLASS_3_G:
            return GSMTHREEG;
        case NETWORK_CLASS_4_G:
            return GSMFOURG;
        default:
            break;
        }
        return "none";
    }

    private void setLogInfo() {
        Log.i("pss", "Phone.NT_MODE_WCDMA_PREF : " + Phone.NT_MODE_WCDMA_PREF
                + "\nPhone.NT_MODE_GSM_ONLY : " + Phone.NT_MODE_GSM_ONLY
                + "\nPhone.NT_MODE_LTE_GSM_WCDMA : "
                + Phone.NT_MODE_LTE_GSM_WCDMA
                + "\nPhone.NT_MODE_LTE_CDMA_EVDO_GSM_WCDMA : "
                + Phone.NT_MODE_LTE_CDMA_EVDO_GSM_WCDMA
                + "\nPhone.NT_MODE_CDMA : " + Phone.NT_MODE_CDMA
                + "\nPhone.NT_MODE_CDMA_NO_EVDO : "
                + Phone.NT_MODE_CDMA_NO_EVDO
                + "\nPhone.NT_MODE_LTE_CDMA_AND_EVDO : "
                + Phone.NT_MODE_LTE_CDMA_AND_EVDO
                + "\nPhone.NT_MODE_GSM_UMTS : " + Phone.NT_MODE_GSM_UMTS);
    }

    private class MyHandler extends Handler {

        static final int MESSAGE_GET_PREFERRED_NETWORK_TYPE = 0;
        static final int MESSAGE_SET_PREFERRED_NETWORK_TYPE = 1;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_GET_PREFERRED_NETWORK_TYPE:
                Log.i("pss", "get networkType");
                break;

            case MESSAGE_SET_PREFERRED_NETWORK_TYPE:
                Log.i("pss", "put networkType");
                break;
            }
        }
    }
}
