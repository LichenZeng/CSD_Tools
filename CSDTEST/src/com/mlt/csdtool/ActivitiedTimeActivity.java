package com.mlt.csdtool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent; //pss add for the bug for customer 20151020
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @ClassName: ActivitiedTimeActivity
 * @Function: Used to detect the activity of the mobile phone, more than five
 *            hours after the phone manually or set time by the network to
 *            record time. Record time can not be formatted
 * @author: peisaisai
 * @date: 20150821 pm3:45:55
 * @time: pm3:45:55 Copyright (c) 2015, Malata All Rights Reserved.
 */
public class ActivitiedTimeActivity extends Activity {
    private TextView mtvActivatedTime;
    private EditText metActivatedTimePassword;

    // Data location stored in NVRAM
    private final int NVRAM_START_POSTION = 1024-7-1;//pss modify for VFOZBENQ-140//yutianliang modify for VFOZBENQ-370 old:40 + 64 + 12

    private final static int NVRAM_DATA_LENGTH = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.item_activitedtime_activity);
        mtvActivatedTime = (TextView) this.findViewById(R.id.activitedTime);
        setProductInfo();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    /** 
    * @MethodName: setProductInfo 
    * @Description:Update time, if there is time in the NVRAM, the display time, or display invalid time 
    * @return void   
    * @throws 
    * Copyright (c) 2015,  Malata All Rights Reserved.
    */
    private void setProductInfo() {

        final String PRODUCT_INFO_FILENAME = "/data/nvram/APCFG/APRDEB/PRODUCT_INFO";
        byte[] buff = CountTimeService.readProductInfo(NVRAM_START_POSTION,
                PRODUCT_INFO_FILENAME);

        if (buff != null && !CountTimeService.judgeTimeExist()) {
            Log.i("pss", "read success锛欳SDINFO锛�");

            byte[] b = new byte[] { 0, 0, 0, 0, 0, 0, 0 };
            Log.i("pss", "buff.length = " + buff.length);
            for (int i = 0; i < buff.length; i++) {
                Log.i("pss", "buff[" + i + "] : " + buff[i]);
            }
            int j = 0;
            for (int i = NVRAM_START_POSTION; i < NVRAM_START_POSTION
                    + NVRAM_DATA_LENGTH; i++) {
                Log.i("pss", "" + buff[i]);
                b[j++] = buff[i];
            }
            mtvActivatedTime.setText(String.format("%02d", b[0]) + "/"
                    + String.format("%02d", b[1]) + "/"
                    + String.format("%02d", b[2]) + String.format("%02d", b[3])
                    + "  " + String.format("%02d", b[4]) + ":"
                    + String.format("%02d", b[5]) + ":"
                    + String.format("%02d", b[6]));

        } else {
            mtvActivatedTime.setText("Time Invalid");
        }

    }

    public void ActivatedTimeOnclick(View view) {
        switch (view.getId()) {
        case R.id.activitedreset:
            // mtvActivatedTime.setText(getNvramInfo());
            createDialog();
            break;
        case R.id.activitedexit:
            finish();
            break;
        default:
            break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void createDialog() {

        LayoutInflater factory = LayoutInflater
                .from(ActivitiedTimeActivity.this);
        View view = factory.inflate(R.layout.item_ativatedtime_dialog, null);
        metActivatedTimePassword = (EditText) view
                .findViewById(R.id.activatedtimedialogpass);

        new AlertDialog.Builder(this)
                .setTitle(this.getString(R.string.activatedinputpassword))
                .setView(view)
                .setNegativeButton(this.getString(R.string.activatedreset),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {
                                // TODO Auto-generated method stub
                                if (metActivatedTimePassword
                                        .getText()
                                        .toString()
                                        .equals(CsdApplication.ACTIVATEDTIME_PASSWORD)) {
                                    ActivitiedTimeActivity.this.findViewById(
                                            R.id.activitedtitle).setVisibility(
                                            View.GONE);
                                    ActivitiedTimeActivity.this.findViewById(
                                            R.id.activitedTime).setVisibility(
                                            View.GONE);
                                    byte[] b = new byte[] { 0, 0, 0, 0, 0, 0, 0 };
                                    CountTimeService.writeProduct(b, NVRAM_START_POSTION);
                                    ActivitiedTimeActivity.this.findViewById(
                                            R.id.activatedtimeresetsuccess)
                                            .setVisibility(View.VISIBLE);
									//pss add for the bug for customer 20151020 start
                                    ActivitiedTimeActivity.this.stopService(new Intent("com.mlt.csdtool.service"));//pss add 20151020
                                    Log.i("pss", "stopService  countTimeService!");
                                    new Handler().postDelayed(new Runnable() {
                                        
                                        @Override
                                        public void run() {
                                            // TODO Auto-generated method stub
                                            Log.i("pss", "startService  countTimeService!");
                                            ActivitiedTimeActivity.this.startService(new Intent("com.mlt.csdtool.service"));
                                        }
                                    }, 10000);
									//pss add for the bug for customer 20151020 end
                                } else {
                                    Toast.makeText(ActivitiedTimeActivity.this,
                                            "password error!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                .setPositiveButton(
                        this.getString(R.string.no),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {
                                // TODO Auto-generated method stub
                                dialog.cancel();
                            }
                        }).create().show();

    }
}
