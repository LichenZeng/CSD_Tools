package com.mlt.csdfactorytest.item;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.mlt.csdfactorytest.ItemTestActivity;
import com.mlt.csdfactorytest.R;

/**
 * @ClassName: LCD
 * @Description: This class by switching the background image to test the screen
 * @Function: Test the screen display
 * @author: huangguoxiong
 * @date: 2015-2-11 am10:09:07 Copyright (c) 2015, Malata All Rights Reserved.
 */
public class LCD extends AbsHardware implements OnClickListener {
    // The background image
    private ImageView mivPicture;
    // The main UI
    private View mView;
    // The number of the picture
    private static int mImageNum = 5;
    // When displayed at the end of the picture to set the text.
    private TextView mtvPrompt, mtvLcdTitle;
    private long mlastClick;

    public LCD(String text, Boolean visible) {
        super(text, visible);
    }
 
    @Override
    public View getView(Context context) {
        setVisible();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        mView = layoutInflater.inflate(R.layout.item_lcd, null);
        mivPicture = (ImageView) mView.findViewById(R.id.img_lcd);
        mtvPrompt = (TextView) mView.findViewById(R.id.tv_lcd);
        mtvLcdTitle = (TextView) mView.findViewById(R.id.lcd_tietle);

        // mView.setOnTouchListener(this);
        return mView;
    }

    public void setVisible() {

        /**
         * Start test Settings screen brightness is the largest
         * */
        WindowManager.LayoutParams lp = ItemTestActivity.itemActivity
                .getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(255) * (1f / 255f);
        ItemTestActivity.itemActivity.getWindow().setAttributes(lp);

        /**
         * Hide button
         **/

        ItemTestActivity.itemActivity.handler
                .sendEmptyMessage(ItemTestActivity.MSG_BTNBAR_INVISIBLE);
        /**
         * Set no title, full screen
         * */
        ItemTestActivity.itemActivity
                .requestWindowFeature(Window.FEATURE_NO_TITLE);
        ItemTestActivity.itemActivity.getWindow().requestFeature(
                Window.FEATURE_NO_TITLE);
        ItemTestActivity.itemActivity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mivPicture.setBackgroundResource(R.drawable.lcd_test_03);
        mImageNum = 5; //chb add
        // mView.setOnTouchListener(this);
        mivPicture.setOnClickListener(this);
    }

    /**
     * Set the click frequency
     * */
    public boolean isDouldClick() {

        long time = System.currentTimeMillis();
        long timeD = time - mlastClick;
        if (0 < timeD && timeD < 300) {
            return true;
        }
        mlastClick = time;
        return false;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (isDouldClick()) {
            return;
        }
        switch (v.getId()) {
        case R.id.img_lcd:
            if (mImageNum == 5) {
            	 mivPicture.setBackgroundColor(Color.RED);
                mImageNum--;
            } else if (mImageNum == 4) {
            	mivPicture.setBackgroundColor(Color.GREEN);
                mImageNum--;
            } else if (mImageNum == 3) {
            	 mivPicture.setBackgroundColor(Color.BLUE);
                mImageNum--;
            } else if (mImageNum == 2) {
            	mivPicture.setBackgroundColor(Color.WHITE);
                mImageNum--;
            } else if (mImageNum == 1) {
            	mivPicture.setBackgroundColor(Color.BLACK);
                mImageNum--;
            } else if (mImageNum == 0) {
            	 // mView.setBackgroundColor(color.floralwhite);
                mivPicture.setVisibility(View.INVISIBLE);
                mView.setBackgroundColor(0xFFFAF0);
                ItemTestActivity.itemActivity.handler
                        .sendEmptyMessage(ItemTestActivity.MSG_BTNBAR_VISIBLE);
                mtvPrompt.setVisibility(View.VISIBLE);
                mtvLcdTitle.setVisibility(View.VISIBLE);
            } 
            break;

        default:
            break;
        }
    }

    // @Override
    // public boolean onTouch(View v, MotionEvent event) {
    //
    // switch (event.getAction()) {
    // case MotionEvent.ACTION_DOWN:
    // if (mImageNum == 7) {
    // mivPicture.setVisibility(View.VISIBLE);
    // mivPicture.setBackgroundResource(R.drawable.lcd_test_01);
    // mImageNum--;
    // } else if (mImageNum == 6) {
    // mivPicture.setBackgroundResource(R.drawable.lcd_test_02);
    // mImageNum--;
    // } else if (mImageNum == 5) {
    // mivPicture.setVisibility(View.INVISIBLE);
    // mView.setBackgroundColor(Color.RED);
    // mImageNum--;
    // } else if (mImageNum == 4) {
    // mView.setBackgroundColor(Color.GREEN);
    // mImageNum--;
    // } else if (mImageNum == 3) {
    // mView.setBackgroundColor(Color.BLUE);
    // mImageNum--;
    // } else if (mImageNum == 2) {
    // mView.setBackgroundColor(Color.WHITE);
    // mImageNum--;
    // } else if (mImageNum == 1) {
    // mView.setBackgroundColor(Color.BLACK);
    // mImageNum--;
    // } else if (mImageNum == 0) {
    // //mView.setBackgroundColor(color.floralwhite);
    // mView.setBackgroundColor(0xFFFAF0);
    // ItemTestActivity.itemActivity.handler
    // .sendEmptyMessage(ItemTestActivity.MSG_BTNBAR_VISIBLE);
    // mtvPrompt.setVisibility(View.VISIBLE);
    // mtvLcdTietle.setVisibility(View.VISIBLE);
    // }
    // break;
    // default:
    // break;
    // }
    // return true;
    // }

}