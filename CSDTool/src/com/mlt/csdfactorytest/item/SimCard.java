package com.mlt.csdfactorytest.item;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mlt.csdfactorytest.R;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.sax.StartElementListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;


//simCard
import com.android.internal.telephony.PhoneConstants;
import com.mediatek.telephony.TelephonyManagerEx;
import com.mlt.csdfactorytest.ItemTestActivity;

//import com.mediatek.common.featureoption.FeatureOption;
//import com.mediatek.gemini.GeminiUtils;
//import com.mediatek.gemini.SimInfoRecord;
import android.telephony.SubscriptionManager;
//import android.telephony.SubInfoRecord;//yutianliang delete
import android.telephony.SubscriptionInfo;//yutianliang add 

////memoryCard
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.Settings;
import android.os.SystemProperties;
import android.content.res.Resources;

/**
 * @ClassName: SimCardAndSingle
 * @Description: This kind of mobile phone CARDS, memory CARDS and call test
 * @Function: 1, read the sim card information, to determine whether a sim card
 *            2, read the memory card information, to determine whether a memory
 *            card 3, set up the call button, click to dial the phone number of
 *            the corresponding
 * @author: peisaisai
 * @date: 2015-01-15 13:47:25 Copyright (c) 2015, malata All Rights Reserved.
 */
public class SimCard extends AbsHardware {
    private Context context;

    // Four buttons respectively unicom
    // calls button and dial the mobile phone button
    private Button mbtnCallSim1, mbtnCallSim2;

    // Used to display the memory card information, sim1 test results, sim2 test
    // results,
    private TextView mtvSim1Card,
                     mtvSim2Card;
                     

    // If the mode is single card mode, then display only mRelSim2,or all can
    // disappear
    private RelativeLayout mRelSim1, mRelSim2;

    // the message to update the view
    private final int UPDATE_SIMCARDANDSIGLE = 0;

    // Sim card information management instance
    private TelephonyManagerEx mTelephonyManagerEx;

    // the flag to judge the EMMC,SDcard,sim1,sim2 and usb etc
    private boolean mIsMountedSim1,
                    mIsMountedSim2;
    
    private long mSim1CardInfoId, mSim2CardInfoId;

    // a list store the siminfo variable
    //private List<SubInfoRecord> mSimInfoList = new ArrayList<SubInfoRecord>();//yutianliang delete
    private List<SubscriptionInfo> mSimInfoList = new ArrayList<SubscriptionInfo>();//yutianliang add

    public SimCard(String text, Boolean visible) {
        super(text, visible);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        /** set the acitivty title */
        // ItemTestActivity.itemActivity.setTitle(R.string.item_SIM);
        getSimInfo();
    }

    private void getSimInfo() {
        Log.d("pss", "getSimInfo()");
        //mSimInfoList = SubscriptionManager.getAllSubInfoList();//yutianliang delete
        mSimInfoList = SubscriptionManager.from(context).getAllSubscriptionInfoList();//yutianliang add
        int mSimNum = mSimInfoList.size();
        Log.d("pss", "total inserted sim card =" + mSimNum);
        // Collections.sort(mSimInfoList, new GeminiUtils.SIMInfoComparable());
        // for debug purpose to show the actual sim information
        int slot;
        for (int i = 0; i < mSimInfoList.size(); i++) {
            //slot = mSimInfoList.get(i).slotId;//yutialiang delete
        	slot = mSimInfoList.get(i).getSimSlotIndex();//yutialiang add
            if (slot == 0) {
                //mSim1CardInfoId = mSimInfoList.get(i).subId;//yutialiang delete
            	mSim1CardInfoId = mSimInfoList.get(i).getSubscriptionId();//yutialiang add
            } else if (slot == 1) {
                //mSim2CardInfoId = mSimInfoList.get(i).subId;//yutialiang delete
                mSim2CardInfoId = mSimInfoList.get(i).getSubscriptionId();//yutialiang add
            }
            //Log.i("pss", "siminfo.mSimSlotId = " + slot + "subid = "
                    //+ mSimInfoList.get(i).subId);
        }
    }

    private void loadSimFlashInfoThread() {
        // TODO Auto-generated method stub
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                // check Sim Card
                simCardTest(); 
                
                Message msg = new Message();
                msg.what = UPDATE_SIMCARDANDSIGLE;
                handler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSimFlashInfoThread();
    }

    /**
     * @MethodName: simCardTeXst
     * @Description:Is used to detect the sim card, if it is a single card,
     *                 displays sim1, if it is a dual sim card, display sim1 and
     *                 sim2.If you can detect the sim card, the corresponding
     *                 button will turn green, at the same time shows through.If
     *                 you don't read the sim card, the following two dial
     *                 button will not show
     * @return void
     * @throws Copyright
     *             (c) 2015, Malata All Rights Reserved.
     */
    private void simCardTest() {
        // TODO Auto-generated method stub
        String imsi_1, imsi_2;
        if (!isGeminiEnabled()) {
            mRelSim2.setVisibility(View.INVISIBLE);
            TelephonyManager telManager = (TelephonyManager) context
                    .getSystemService(context.TELEPHONY_SERVICE);
            if (telManager.getSubscriberId() != null) {
                mIsMountedSim1 = true;
            } else {
                mIsMountedSim1 = false;
            }
        } else {
            mTelephonyManagerEx = TelephonyManagerEx.getDefault();
            imsi_1 = mTelephonyManagerEx
                    .getSubscriberId(PhoneConstants.SIM_ID_1);// GEMINI_SIM_1
            imsi_2 = mTelephonyManagerEx
                    .getSubscriberId(PhoneConstants.SIM_ID_2);// GEMINI_SIM_2
            Log.i("pss", "imsi_1:" + imsi_1);
            Log.i("pss", "imsi_2:" + imsi_2);
            if (imsi_1 != null) {
                mIsMountedSim1 = true;
            } else {
                mIsMountedSim1 = false;
            }
            if (imsi_2 != null) {
                mIsMountedSim2 = true;
            } else {
                mIsMountedSim2 = false;
            }
        }
    }

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
    
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (UPDATE_SIMCARDANDSIGLE == msg.what) {
                
                if (mIsMountedSim1) {
                    mtvSim1Card.setBackgroundColor(Color.GREEN);
                    mtvSim1Card.setText(context.getText(R.string.mtvsimcardinserted));
                } else {
                    mtvSim1Card.setBackgroundColor(Color.RED);
                    mtvSim1Card.setText(context.getText(R.string.mtvsimcardnoinserted));
                }
                if (mIsMountedSim2) {
                    mtvSim2Card.setText(context.getText(R.string.mtvsimcardinserted));
                    mtvSim2Card.setBackgroundColor(Color.GREEN);
                } else {
                    mtvSim2Card.setText(context.getText(R.string.mtvsimcardnoinserted));
                    mtvSim2Card.setBackgroundColor(Color.RED);
                }
                
            }
        };
    };

    @Override
    public View getView(Context context) {
        // TODO Auto-generated method stub
        this.context = context;
        LayoutInflater factory = LayoutInflater.from(context);
        View view = factory.inflate(R.layout.item_sim, null);
        mtvSim1Card = (TextView) view.findViewById(R.id.mtvsim1card);
        mtvSim2Card = (TextView) view.findViewById(R.id.mtvsim2card);
        mRelSim1 = (RelativeLayout) view.findViewById(R.id.mrelsim1);
        mRelSim2 = (RelativeLayout) view.findViewById(R.id.mrelsim2);
        return view;
    }
}
