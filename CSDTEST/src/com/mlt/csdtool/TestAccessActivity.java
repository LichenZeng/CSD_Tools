package com.mlt.csdtool;

import java.text.BreakIterator;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * @ClassName: TestAccessActivity
 * @Description: access to all TestItem;
 * @Function: This class is an interface that displays all tests.Through this
 *            class you can enter other test items.
 * @author: peisaisai
 * @date: 20150821 pm3:13:51
 * @time: pm3:13:51 Copyright (c) 2015, Malata All Rights Reserved.
 */
public class TestAccessActivity extends Activity {
    private Context mContext;

    private boolean mboolRFStatus, mboolMMIStatus, mboolRootStatus,
            mboolStatisticStatus, mboolActivationStatus;
    private ImageView mimgMMI, mimgRF, mimgRoot, mimgStatisticInfo,
            mimgActivationTime, mimgResetPSenor;

    private Animation mAnimation;
    private Animation mAnimationRF;
    private Animation mAnimation2;

    //Message to all test.
    private final int MSG_DISPATH_MMI_DELAY = 0;
    private final int MSG_DISPATH_MMI_SINGLETEST_DELAY = 1;
    private final int MSG_DISPATH_MMI_CONTINUETEST_DELAY = 2;
    private final int MSG_DISPATH_ROOT_DELAY = 3;
    private final int MSG_DISPATH_STATISTIC_DELAY = 4;
    private final int MSG_DISPATH_ACTIVATION_DELAY = 5;
    private final int MSG_RF_CARDSINGLETWOG = 6;
    private final int MSG_RF_CARDSINGLETHREEG = 7;
    private final int MSG_RF_CARDSINGLEFOURG = 8;
    private final int MSG_RF_GPS = 9;
    private final int MSG_DISPATH_RESETPSENSOR_DELAY = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_test_access);
        mContext = this;
        initComponentAndData();
    }

    /** 
    * @MethodName: initComponentAndData 
    * @Description:Initialize all the controls and properties of the current activity
    * @return void   
    * @throws 
    * Copyright (c) 2015,  Malata All Rights Reserved.
    */
    private void initComponentAndData() {
        mimgMMI = (ImageView) findViewById(R.id.imgmmi);
        mimgRF = (ImageView) findViewById(R.id.imgrf);
        mimgRoot = (ImageView) findViewById(R.id.imgroot);
        mimgStatisticInfo = (ImageView) findViewById(R.id.imgstatistic);
        mimgActivationTime = (ImageView) findViewById(R.id.imgactivation);
        mimgResetPSenor = (ImageView) findViewById(R.id.imgresetpsensor);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        mAnimationRF = AnimationUtils.loadAnimation(this, R.anim.rotate);
        mAnimation2 = AnimationUtils.loadAnimation(this, R.anim.rotatex);
        mAnimationRF.setFillAfter(true);
        mboolActivationStatus = false;
        mboolMMIStatus = false;
        mboolRFStatus = false;
        mboolRootStatus = false;
        mboolStatisticStatus = false;
    }

    /** 
    * @MethodName: onClick 
    * @Description:Current view click event for all of the current
    * @param view  
    * @return void   
    * @throws 
    * Copyright (c) 2015,  Malata All Rights Reserved.
    */
    public void onClick(View view) {
        startAnimation(view);
        Message msgDelay = new Message();
        Message msg = new Message();
        switch (view.getId()) {
        case R.id.mlinmmitest: // MMItest
            if (mboolMMIStatus) {
                this.findViewById(R.id.mtvsingletest).setVisibility(view.GONE);
                this.findViewById(R.id.mimgsingletest).setVisibility(View.GONE);
                this.findViewById(R.id.mtvcontinuetest)
                        .setVisibility(View.GONE);
                this.findViewById(R.id.mimgcontinuetest).setVisibility(View.GONE);
                mboolMMIStatus = false;
            } else {
                this.findViewById(R.id.mtvsingletest).setVisibility(
                        view.VISIBLE);
                this.findViewById(R.id.mtvcontinuetest).setVisibility(
                        View.VISIBLE);
                this.findViewById(R.id.mimgsingletest).setVisibility(
                        view.VISIBLE);
                this.findViewById(R.id.mimgcontinuetest).setVisibility(
                        View.VISIBLE);
                mboolMMIStatus = true;
            }
            msgDelay.what = MSG_DISPATH_MMI_DELAY;
            mHandler.sendMessageDelayed(msgDelay,
                    mAnimation.getDuration() + 100);
            break;
        case R.id.mtvsingletest:
            msg.what = MSG_DISPATH_MMI_SINGLETEST_DELAY;
            mHandler.sendMessage(msg);
            break;
        case R.id.mtvcontinuetest:
            msg.what = MSG_DISPATH_MMI_CONTINUETEST_DELAY;
            mHandler.sendMessage(msg);
            break;
        case R.id.mlinrffuction: // rf choice
            if (mboolRFStatus) {
                this.findViewById(R.id.mtvgps).setVisibility(View.GONE);
                this.findViewById(R.id.mtvcardsingletwoG).setVisibility(
                        View.GONE);
                this.findViewById(R.id.mtvcardsinglethreeG).setVisibility(
                        View.GONE);
                this.findViewById(R.id.mtvcardsinglefourG).setVisibility(
                        View.GONE);
                this.findViewById(R.id.mimggps).setVisibility(View.GONE);
                this.findViewById(R.id.mimgcardsingletwoG).setVisibility(
                        View.GONE);
                this.findViewById(R.id.mimgcardsinglethreeG).setVisibility(
                        View.GONE);
                this.findViewById(R.id.mimgcardsinglefourG).setVisibility(
                        View.GONE);
                mboolRFStatus = false;
            } else {
                this.findViewById(R.id.mtvgps).setVisibility(View.VISIBLE);
                this.findViewById(R.id.mtvcardsingletwoG).setVisibility(
                        View.VISIBLE);
                this.findViewById(R.id.mtvcardsinglethreeG).setVisibility(
                        View.VISIBLE);
                this.findViewById(R.id.mtvcardsinglefourG).setVisibility(
                        View.VISIBLE);
                this.findViewById(R.id.mimggps).setVisibility(View.VISIBLE);
                this.findViewById(R.id.mimgcardsingletwoG).setVisibility(
                        View.VISIBLE);
                this.findViewById(R.id.mimgcardsinglethreeG).setVisibility(
                        View.VISIBLE);
                this.findViewById(R.id.mimgcardsinglefourG).setVisibility(
                        View.VISIBLE);
                mboolRFStatus = true;
            }
            break;
        case R.id.mlinrootinfo:
            msgDelay.what = MSG_DISPATH_ROOT_DELAY;
            mHandler.sendMessageDelayed(msgDelay,
                    mAnimation.getDuration() + 100);
            break;
        case R.id.mlinstatisticinfo:
            msgDelay.what = MSG_DISPATH_STATISTIC_DELAY;
            mHandler.sendMessageDelayed(msgDelay,
                    mAnimation.getDuration() + 100);
            break;
        case R.id.mlinactivationtime:
            msgDelay.what = MSG_DISPATH_ACTIVATION_DELAY;
            mHandler.sendMessageDelayed(msgDelay,
                    mAnimation.getDuration() + 100);
            break;
        case R.id.mtvcardsingletwoG:
            msg.what = MSG_RF_CARDSINGLETWOG;
            mHandler.sendMessage(msg);
            break;
        case R.id.mtvcardsinglethreeG:
            msg.what = MSG_RF_CARDSINGLETHREEG;
            mHandler.sendMessage(msg);
            break;
        case R.id.mtvcardsinglefourG:
            msg.what = MSG_RF_CARDSINGLEFOURG;
            mHandler.sendMessage(msg);
            break;
        case R.id.mtvgps:
            msg.what = MSG_RF_GPS;
            mHandler.sendMessage(msg);
            break;
        case R.id.mlinresetpsensor:
            msg.what = MSG_DISPATH_RESETPSENSOR_DELAY;
            mHandler.sendMessage(msg);
            break;
        default:
            break;
        }
    }

    private void startAnimation(View view) {
        // mAnimation.setDuration(3000);
        switch (view.getId()) {
        case R.id.mlinmmitest: // MMItest
            if (!mboolMMIStatus) {
                mimgMMI.startAnimation(mAnimationRF);
            } else {
                mimgMMI.startAnimation(mAnimation2);
            }
            break;
        case R.id.mlinrffuction: // rf choice
            if (!mboolRFStatus) {
                mimgRF.startAnimation(mAnimationRF);
            } else {
                mimgRF.startAnimation(mAnimation2);
            }
            break;
        case R.id.mlinrootinfo:
            mimgRoot.startAnimation(mAnimation);
            break;
        case R.id.mlinstatisticinfo:
            mimgStatisticInfo.startAnimation(mAnimation);
            break;
        case R.id.mlinactivationtime:
            mimgActivationTime.startAnimation(mAnimation);
            break;
        case R.id.mlinresetpsensor:
            mimgResetPSenor.startAnimation(mAnimation);
            break;
        default:
            break;
        }
    }

    /** 
    * @Fields: mHandler 
    * TODO：Receive messages and perform their actions
    */
    private Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
            case MSG_DISPATH_MMI_DELAY:
                break;
			//chb modify for VFOZBAUBQ-55 change "factorytest" -> "csdfactorytest" begin 2015-9-8 
            case MSG_DISPATH_MMI_SINGLETEST_DELAY:
                Intent intentManual = new Intent(
                        "com.mlt.csdfactorytest.action.TEST_LIST");
                ComponentName mComponentNameManual = new ComponentName(
                        "com.mlt.csdfactorytest",
                        "com.mlt.csdfactorytest.ManualTestActivity");
                intentManual.setComponent(mComponentNameManual);
                intentManual.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intentManual);
                break;
            case MSG_DISPATH_MMI_CONTINUETEST_DELAY:
                Intent intentAuto = new Intent(
                        "com.mlt.csdfactorytest.action.TEST_LIST");
                ComponentName mComponentName = new ComponentName(
                        "com.mlt.csdfactorytest",
                        "com.mlt.csdfactorytest.AutoTestActivity");
                intentAuto.putExtra("intentname", "csdtool");
                intentAuto.setComponent(mComponentName);
                intentAuto.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intentAuto);
                // startActivity("com.mlt.csdfactorytest",
                // "com.mlt.csdfactorytest.AutoTestActivity");
                break;
			//chb modify for VFOZBAUBQ-55 change "factorytest" -> "csdfactorytest" end 2015-9-8 
            case MSG_DISPATH_ROOT_DELAY:
                Intent mRootIntent = new Intent(TestAccessActivity.this,
                        RootedActivity.class);
                mRootIntent.putExtra("csditemname", "root");
                startActivity(mRootIntent);
                break;
            case MSG_DISPATH_STATISTIC_DELAY:

                break;
            case MSG_DISPATH_ACTIVATION_DELAY:
                Intent activatedIntent = new Intent(TestAccessActivity.this,
                        ActivitiedTimeActivity.class);
                startActivity(activatedIntent);
                break;
            case MSG_RF_CARDSINGLETWOG:
                Intent mTwoGIntent = new Intent(TestAccessActivity.this,
                        SimCardSingleAvtivity.class);
                mTwoGIntent.putExtra("simSingle", "gsmtwo");
                startActivity(mTwoGIntent);
                break;
            case MSG_RF_CARDSINGLETHREEG:
                Intent mThreeGIntent = new Intent(TestAccessActivity.this,
                        SimCardSingleAvtivity.class);
                mThreeGIntent.putExtra("simSingle", "gsmthree");
                startActivity(mThreeGIntent);
                break;
            case MSG_RF_CARDSINGLEFOURG:
                Intent mFourGIntent = new Intent(TestAccessActivity.this,
                        SimCardSingleAvtivity.class);
                mFourGIntent.putExtra("simSingle", "gsmfour");
                startActivity(mFourGIntent);
                break;
            case MSG_RF_GPS:
                startActivity("com.mediatek.ygps",
                        "com.mediatek.ygps.YgpsActivity");
                break;
            case MSG_DISPATH_RESETPSENSOR_DELAY:

                break;
            default:
                break;
            }
        };
    };

    private void startActivity(String componentName, String activityName) {
        Intent mIntent = new Intent();
        ComponentName mComponentName = new ComponentName(componentName,
                activityName);
        mIntent.setComponent(mComponentName);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mIntent);
    }
}
