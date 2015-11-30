package com.mlt.csdfactorytest.item;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mlt.csdfactorytest.ItemTestActivity;
import com.mlt.csdfactorytest.R;

/**
 * @ClassName: KeyAndMotor
 * @Description: This class is for keys and vibrator test
 * @Function: Mainly for access to the module test vibrator vibration, as well
 *            as the key block, when press a keystroke, displays the effect on
 *            the interface
 * @author: peisaisai
 * @date: 2015-01-15 13:44:32 Copyright (c) 2015, Malata All Rights Reserved.
 */
public class KeyTest extends AbsHardware {

    // HOME_KEY Block of code
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

   //chb modify for factorytest :delete power_key test begin
     // /PSS: power-off FactoryTest @{
    // private static final String FACTORYTEST_NORMAL_POWERKEY_SHUTDOWN_ACTION = "android.intent.action.factoryTestshutdown";
    // private static final String FACTORYTEST_NORMAL_POWERKEY_RENEW_ACTION = "android.intent.action.factoryTestrenew";
    // private static final String FACTORYTEST_NORMAL_POWERKEY_DISABLE_ACTION = "android.intent.action.factoryTestdisable";
    // /@}
    //chb modify for factorytest :delete power_key test end

    // pss FactoryTest 20150131 begin
    private static final String FACTORYTEST_CLOSERECENTAPPS_ACTION = "com.android.factorytest.closerecentapps";
    private static final String FACTORYTEST_MENU_DISABLE_ACTION = "com.android.factorytest.menudisable";
    private static final String FACTORYTEST_HOME_DISABLE_ACTION = "com.android.factorytest.homedisable";
    private static final String FACTORYTEST_STARTRECENTAPPS_ACTION = "com.android.factorytest.startrecentapps";
    // end
    
    private final int BACK = 0;
    private final int MENU = 1;
    private final int RECENTAPPS = 1;
    private final int VOLUME_UP = 2;
    private final int VOLUME_DOWN = 3;
    private final int POWER = 4;
    private final int HOME = 5;
    private final int MOTOR = 6;
    
    // the number of test cases
    // before  private final int TESTCASE_MAX_NUM = 6; ////chb modify for factorytest :delete power_key test 
	private final int TESTCASE_MAX_NUM = 5;//pss modify for VFOZBENQ-96 20150911
    private Context mContext;
    
    // The ID of key button
    private TextView mtvVolumeUp,
                     //mtvPower,  //chb modify for factorytest :delete power_key test 
                     mtvVolumeDown, 
                     mtvMenu,
                     mtvHome,
                     mtvBack;

    private int mKeyTestCount;
    private int []mKeyFlag;

    public KeyTest(String text, Boolean visible) {
        super(text, visible);
    }

    @Override
    public void onCreate() {
        ItemTestActivity.itemActivity.getWindow().setFlags(
                FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
        mKeyFlag = new int[]{0, 0, 0, 0, 0, 0, 0};
        mKeyTestCount = 0;
        //motorTest(); //chb modify for factorytest :delete power_key test 
        super.onCreate();
    }

    /**
     * @MethodName: startKeyMotorThread
     * @Description:In order to determine whether the test over start a thread
     * @return void
     * @throws Copyright
     *             (c) 2015, Malata All Rights Reserved.
     */
    private void judgeKeyMotorExit(int count) {
        if (count == TESTCASE_MAX_NUM) {
            Message msg = new Message();
            msg.what = ItemTestActivity.itemActivity.MSG_BTN_PASS_CLICKABLE;
            ItemTestActivity.itemActivity.handler.sendMessage(msg);
        }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
       // intentFilter.addAction(FACTORYTEST_NORMAL_POWERKEY_DISABLE_ACTION); //chb modify for factorytest :delete power_key test 
        intentFilter.addAction(FACTORYTEST_MENU_DISABLE_ACTION);
        intentFilter.addAction(FACTORYTEST_HOME_DISABLE_ACTION);

        // send a broadcast to stop the function of the power button
        mContext.registerReceiver(mKeyReceiver, intentFilter);
      //chb modify for factorytest :delete power_key test 
       /* Intent powerIntent = new Intent();

        powerIntent.setAction(FACTORYTEST_NORMAL_POWERKEY_SHUTDOWN_ACTION);
        mContext.sendBroadcast(powerIntent);*/
      //chb modify for factorytest :delete power_key test end
        // send a broadcast to stop the function of the menu button
        Intent menuIntent = new Intent();
        menuIntent.setAction(FACTORYTEST_CLOSERECENTAPPS_ACTION);
        mContext.sendBroadcast(menuIntent);

        acquireWakeLock();
    }

    @Override
    public void onPause() {
        super.onPause();
        mContext.unregisterReceiver(mKeyReceiver);

        // send a broadcast to start the function of the menu button
        Intent menuIntent = new Intent();
        menuIntent.setAction(FACTORYTEST_STARTRECENTAPPS_ACTION);
        mContext.sendBroadcast(menuIntent);

        releaseWakeLock();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View getView(Context context) {
        // TODO Auto-generated method stub\
        this.mContext = context;
        LayoutInflater factory = LayoutInflater.from(context);
        View view = factory.inflate(R.layout.item_key, null);
        mtvBack = (TextView) view.findViewById(R.id.mtvback);
        mtvHome = (TextView) view.findViewById(R.id.mtvhome);
        mtvMenu = (TextView) view.findViewById(R.id.mtvmenu);
        //mtvPower = (TextView) view.findViewById(R.id.mtvpower); ////chb modify for factorytest :delete power_key test 
        mtvVolumeDown = (TextView) view.findViewById(R.id.mtvvolumedown);
        mtvVolumeUp = (TextView) view.findViewById(R.id.mtvvolumeup);

        // send a message to stop the function of the pass button
        Message msg = new Message();
        msg.what = ItemTestActivity.itemActivity.MSG_BTN_PASS_UNCLICKABLE;
        ItemTestActivity.itemActivity.handler.sendMessage(msg);
        return view;
    }

    /*
     * (non-Javadoc)
     * 
     * @Description: Six physical button on the screen, but no power rows block,
     * to modify the framework layer
     * 
     * @see com.malata.factorytest.item.AbsHardware#onKeyDown(int,
     * android.view.KeyEvent) Copyright (c) 2015, Malata All Rights Reserved.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mtvBack.setVisibility(View.INVISIBLE);
            if (0 == mKeyFlag[BACK]) {
                mKeyFlag[BACK] = 1;
                mKeyTestCount++;
                judgeKeyMotorExit(mKeyTestCount);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            mtvVolumeDown.setVisibility(View.INVISIBLE);
            if (0 == mKeyFlag[VOLUME_DOWN]) {
                mKeyFlag[VOLUME_DOWN] = 1;
                mKeyTestCount++;
                judgeKeyMotorExit(mKeyTestCount);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            mtvVolumeUp.setVisibility(View.INVISIBLE);
            if (0 == mKeyFlag[VOLUME_UP]) {
                mKeyFlag[VOLUME_UP] = 1;
                mKeyTestCount++;
                judgeKeyMotorExit(mKeyTestCount);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            mtvMenu.setVisibility(View.INVISIBLE);
            if (0 == mKeyFlag[MENU]) {
                mKeyFlag[MENU] = 1;
                mKeyTestCount++;
                judgeKeyMotorExit(mKeyTestCount);
            }
            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_HOME)
                && (event.getRepeatCount() == 0)) {
            mtvHome.setVisibility(View.INVISIBLE);
            if (0 == mKeyFlag[HOME]) {
                mKeyFlag[HOME] = 1;
                mKeyTestCount++;
                judgeKeyMotorExit(mKeyTestCount);
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    private WakeLock mWakeLock = null;

    /**
     * @MethodName: acquireWakeLock
     * @Description:Without operation to keep the screen in the awakened state
     * @return void
     * @throws Copyright
     *             (c) 2015, Malata All Rights Reserved.
     */
    private void acquireWakeLock() {
        if (mWakeLock == null) {
            // Log.i("pss", "Acquiring wake lock");
            PowerManager pm = (PowerManager) ItemTestActivity.itemActivity
                    .getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
                    mContext.getClass().getCanonicalName());
            mWakeLock.acquire();
        }
    }

    /**
     * @MethodName: releaseWakeLock
     * @Description:release the lock of screen
     * @return void
     * @throws Copyright
     *             (c) 2015, Malata All Rights Reserved.
     */
    private void releaseWakeLock() {

        // Log.i("pss", "Releasing wake lock");
        if ((mWakeLock != null) && mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
        }

    }

    /**
     * @Fields: mKeyReceiver TODO：receive two broadcasts to make the power
     *          button and the menu button disappear
     */
    public BroadcastReceiver mKeyReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context mContext, Intent mIntent) {
            String action = mIntent.getAction();
            if (action.equals(FACTORYTEST_MENU_DISABLE_ACTION)) {
                mtvMenu.setVisibility(View.INVISIBLE);
                if (0 == mKeyFlag[MENU]) {
                    mKeyFlag[MENU] = 1;
                    mKeyTestCount++;
                    judgeKeyMotorExit(mKeyTestCount);
                }
            }else if(action.equals(FACTORYTEST_HOME_DISABLE_ACTION)){
				mtvHome.setVisibility(View.INVISIBLE);
				if(0 == mKeyFlag[HOME]){
					mKeyFlag[HOME] = 1;
                    mKeyTestCount++;
                    judgeKeyMotorExit(mKeyTestCount);
				}
			}
        }
    };

}
