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
 
 /**
 * @modify for VFOZBENQ-140
 * @ClassName: MotorTest
 * @Description: This modification is for the test item after you press the HOME key out, and then come restarts Vibrator,
 * resulting in abnormal test
 * @author: peisaisai
 * @date: 2015-09-21  Copyright (c) 2015, Malata All Rights Reserved.
 */
public class MotorTest extends AbsHardware {

    // HOME_KEY Block of code
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

    private Button mbtMotorStop;

    // Frequency of the vibration of the vibrator
    private long[] mPattern = new long[] { 500, 500, 500, 500 };

    // Vibrator class instantiation
    private TipHelper mTipHelper;
    
    private boolean mClickButton = false;//chb add for VFOZBENQ-130 20150917

    public MotorTest(String text, Boolean visible) {
        super(text, visible);
    }

    @Override
    public void onCreate() {
        ItemTestActivity.itemActivity.getWindow().setFlags(
                FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
        //motorTest(); //chb modify for factorytest :delete power_key test 
        super.onCreate();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if(!mClickButton){
			motorTest();
		}
    }
	
	@Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
		mTipHelper.cancel();
    }

    /**
     * @MethodName: MotorStop_OnClick
     * @Description:Stop button events of vibrator
     * @param v
     * @return void
     * @throws Copyright
     *             (c) 2015, Malata All Rights Reserved.
     */
    public void onClickMotorStop(View v) {
       //chb modify for VFOZBENQ-130 begin 20150917
       //mbtMotorStop.setVisibility(View.INVISIBLE);
        if (!mClickButton) {
        	 mbtMotorStop.setText(R.string.mbtmotorStart);
        	 mClickButton = true;
        	 mTipHelper.cancel();
		}else if (mClickButton) {
			 mbtMotorStop.setText(R.string.mbtmotorStop);
			 mClickButton = false;
			 motorTest();
		}
        //chb modify for VFOZBENQ-130 end 20150917
        ItemTestActivity.itemActivity.handler.sendEmptyMessage(ItemTestActivity.MSG_BTN_PASS_CLICKABLE);
    }

    /**
     * @MethodName: motorTest
     * @Description:start MotorTest
     * @return void
     * @throws Copyright
     *             (c) 2015, Malata All Rights Reserved.
     */
    private void motorTest() {
    	mTipHelper = new TipHelper(ItemTestActivity.itemActivity);
    	mTipHelper.vibrate(mPattern, true);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        mTipHelper.cancel();
        super.onDestroy();
    }

    @Override
    public View getView(Context context) {
        // TODO Auto-generated method stub\
        LayoutInflater factory = LayoutInflater.from(context);
        View view = factory.inflate(R.layout.item_motor, null);
        mbtMotorStop = (Button) view.findViewById(R.id.mbtmotorStop);
		
		mbtMotorStop.setOnClickListener(new View.OnClickListener() {
    
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                onClickMotorStop(arg0);
            }
        });
		
        // send a message to stop the function of the pass button
    	ItemTestActivity.itemActivity.handler.sendEmptyMessage(ItemTestActivity.MSG_BTN_PASS_UNCLICKABLE);
        return view;
    }

    /**
     * @ClassName: TipHelper
     * @Description: Vibrator class mainly realize the two vibration modes, one
     *               is a vibration only once, the other is a continuous
     *               vibration
     * @Function: TODO ADD FUNCTION
     * @author: peisaisai
     * @date: 2015-01-15 11:45:33 Copyright (c) 2015, Malata All Rights
     *        Reserved.
     */
    private class TipHelper {
        Activity activity;
        Vibrator vib;
        public TipHelper(ItemTestActivity itemActivity) {
            // TODO Auto-generated constructor stub
            this.activity = itemActivity;
            vib = (Vibrator) activity
                    .getSystemService(Service.VIBRATOR_SERVICE);
        }

        //
        public void vibrate(long milliseconds) {
            vib.vibrate(milliseconds);
        }
        public void vibrate(long[] pattern, boolean isRepeat) {
            vib.vibrate(pattern, isRepeat ? 1 : -1);
        }
        public void cancel(){
            vib.cancel();
        }
    }

}
