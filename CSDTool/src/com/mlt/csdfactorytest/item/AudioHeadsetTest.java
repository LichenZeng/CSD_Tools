package com.mlt.csdfactorytest.item;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mlt.csdfactorytest.ItemTestActivity;
import com.mlt.csdfactorytest.R;

/** 
* @ClassName: AudioHeadsetTest 
* @PackageName:com.mlt.csdtool.item
* @Description: TODO ADD Description
* @Function: TODO ADD FUNCTION
* @author:   chehongbin
* @date:     2015-7-27 下午2:36:09  
* Copyright (c) 2015 MALATA,All Rights Reserved.
*/
public class AudioHeadsetTest extends AbsHardware {
    private static final String TAG = "HEADSET";
    private Context mContext;
    private boolean mRunning = false;
    private TextView mtvHeadsetStates;
    private TextView mtvHeadsetKeyStates;
    private AudioRecord mAudioRecord;
    private AudioTrack mAudioTrack;
    private boolean mHeasetNormal = false;
    private boolean mHeadsetInside = false;
    private boolean mKeydown = false;
    private AudioManager mAudioManager;

    /** 
    * @Fields: headSetReceiver 
    * @Description�� TODO��
    */
    BroadcastReceiver headSetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "into headsetreceiver!");

                String action = intent.getAction();

                if (action.equals(Intent.ACTION_HEADSET_PLUG)) { // headset has pluged

                    if (intent.getIntExtra("state", 0) == 1) {
                        Log.d(TAG, "this is Headset plugged");
                        mtvHeadsetStates.setText(R.string.headset_connected);
                        mtvHeadsetStates.setBackgroundColor(Color.GREEN); //Headset insert UI changes
                        mHeasetNormal = true;
                        mHeadsetInside = true;
                        isHeadsetTestSuccess(); // Handler messages sent to the Itemactivity
                        mRunning = true;
                        new headsetRecord().start(); //Start headset loopback threads
                    } else {
                        Log.d(TAG, "this is headset unplugged");
                        mtvHeadsetStates.setText(R.string.headset_unconnected);
                        mtvHeadsetStates.setBackgroundColor(Color.RED); //The headset out, UI changes
                        mHeadsetInside = false;
                    }
                } else {
                    // mRecord = false;
                }
            }
        };

    public AudioHeadsetTest(String text, Boolean visible) {
        super(text, visible);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHeadsetInside = false;

        /** To obtain the audio services*/
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        /**set the pass button can't click*/
        ItemTestActivity.itemActivity.handler.sendEmptyMessage(ItemTestActivity.MSG_BTN_PASS_UNCLICKABLE);
    }

    /* (non-Javadoc)
     * @see com.malata.factorytest.item.AbsHardware#getView(android.content.Context)
     */
    @Override
    public View getView(Context context) {
        this.mContext = context;

        View view = LayoutInflater.from(context)
                                  .inflate(R.layout.item_headset, null);
        mtvHeadsetStates = (TextView) view.findViewById(R.id.headset_stae);
        mtvHeadsetKeyStates = (TextView) view.findViewById(R.id.press_key);

        return view;
    }


    /* (non-Javadoc)
     * @see com.malata.factorytest.item.AbsHardware#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_MEDIA_PREVIOUS: //vol up key;
        case KeyEvent.KEYCODE_MEDIA_PAUSE:
        case KeyEvent.KEYCODE_HEADSETHOOK: // both are the vol play key;
        case KeyEvent.KEYCODE_MEDIA_NEXT: //vol down key;
            mtvHeadsetKeyStates.setBackgroundColor(Color.GREEN);
            mtvHeadsetKeyStates.setText(R.string.press_key_yes);
            mKeydown = true;

            break;

        default:
            break;
        }
        isHeadsetTestSuccess(); // Handler messages sent to the Itemactivity;
                                //check this test item  whether can pass.
        return false;
    }

   

    /**
    * @MethodName: isTestSuccess
    * @Description:judge Whether the test is finished; whether the through  button able to  click
    * @return void
    * @throws
    */
    public void isHeadsetTestSuccess() {
        if (mHeasetNormal && mKeydown && mHeadsetInside) {
            ItemTestActivity.itemActivity.handler.sendEmptyMessage(ItemTestActivity.MSG_BTN_PASS_CLICKABLE);
        }
    }

	@Override
    public void onResume() {
        super.onResume();
		 /**Create the headset plug event listeners of the filter*/
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        mContext.registerReceiver(headSetReceiver, ifilter);
		
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mAudioManager.setParameters("SET_LOOPBACK_TYPE=0");
    }

    @Override
    public void onDestroy() { // In the end of the activity;
                              //Release resources and the end of the process
                              //Cancel the registration BroadcastReceiver receiver
        super.onDestroy();
        this.mRunning = false;

        //mRecord = false;
        if (mAudioRecord != null) {
            mAudioRecord.stop();
            mAudioRecord.release();
            ;
        }

        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
        }

        this.mAudioManager.setParameters("SET_LOOPBACK_TYPE=0");
        mContext.unregisterReceiver(headSetReceiver);
    }

    /**
    * @ClassName: headsetRecord
    * @PackageName:com.malata.factorytest.item
    * @Description:inner class;The headset loopback thread.
    * @Function: By setting the audio manager loopback type model of headset,
    *                          satat headset loopback test
    * @author:   chehongbin
    */
    class headsetRecord extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(1000L);

                if (mRunning) {
                    //Set the loopback to headset mode
                    AudioHeadsetTest.this.mAudioManager.setParameters(
                        "SET_LOOPBACK_TYPE=2");
                }

                return;
            } catch (InterruptedException localInterruptedException) {
            }
        }
    }
}
