package com.mlt.csdtool;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.SystemProperties;
import android.provider.Settings;

/** 
* @ClassName: RootedActivity 
* @Description: Check whether the phone is root
* @Function: By detecting ADB and Su to determine whether the phone is rooted
* @author:   peisaisai
* @date:     20150821 pm3:24:29 
* @time: pm3:24:29 
* Copyright (c) 2015,  Malata All Rights Reserved.
*/
/** 
* @ClassName: RootedActivity 
* @Description: modify for the judgment of root
* @author:   peisaisai
* @date:     20151009 pm2:56:20 
* @time: 2:56:20 
* Copyright (c) 2015,  Malata All Rights Reserved.
*/
public class RootedActivity extends Activity {
    private Intent mIntent;

 // the view of rootitem
    private TextView mtvAdb, mtvSu, mtvResult;
    private LinearLayout mlinResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);      
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,      
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setTitle("ROOT INFO");
        setView();
        initComponent();
        setResult();
    }

    private void setResult() {
        // TODO Auto-generated method stub
        if (mIntent.getStringExtra("csditemname").equals("root")) {
            setRootResult();
        }
    }

    /** 
    * @MethodName: setRootResult 
    * @Description:Show the results of detecting root
    * @return void   
    * @throws 
    * Copyright (c) 2015,  Malata All Rights Reserved.
    */
    private void setRootResult() {
        // TODO Auto-generated method stub
//        boolean adbRooted = SystemProperties.get("ro.secure").equals("1");
        //boolean adbRooted = (Settings.Global.getInt(this.getContentResolver(), Settings.Global.ADB_ENABLED, 0) != 0);//yutianliang delete for VFOZBENQ-343 
		boolean adbRooted = SystemProperties.get("ro.secure").equals("0");//yutianliang add for VFOZBENQ-343 
        boolean suRooted = false;
        if (!adbRooted) {
            mtvAdb.setText(R.string.pass);
        } else {
            mtvAdb.setText(R.string.rooted);
        }
//        File file = new File("system/bin/su");
//        if (file.exists()) {
//            suRooted = true;
//        }
        
        File file1 = new File("system/xbin/su");
        if (file1.exists()) {
            suRooted = true;
        }
        
        
        if (suRooted) {
            mtvSu.setText(R.string.rooted);
        }else{
            mtvSu.setText(R.string.pass);
        }
        if (suRooted || adbRooted) {  //pss modify for the bug of customer 8143
            mtvResult.setText(R.string.rooted);
        }else{
            mtvResult.setText(R.string.pass);
        }
    }

    private void initComponent() {
        // TODO Auto-generated method stub
        if (mIntent.getStringExtra("csditemname").equals("root")) {
            initRootComPonent();
        }
    }

    private void initRootComPonent() {
        // TODO Auto-generated method stub
        mtvAdb = (TextView) findViewById(R.id.mtvadb);
        mtvSu = (TextView) findViewById(R.id.mtvsu);
        mtvResult = (TextView) findViewById(R.id.mtvresult);
        mlinResult = (LinearLayout) findViewById(R.id.linerresult);
    }
    
    // set item view
    private void setView() {
        mIntent = getIntent();
        if (mIntent.getStringExtra("csditemname").equals("root")) {
            setContentView(R.layout.item_root_activity);
        }
    }
    
    public void onRootItemClick(View view){
        switch (view.getId()) {
        case R.id.verifynow:
            mlinResult.setVisibility(View.VISIBLE);
            break;
        case R.id.rootfail:
            finish();
            break;
        default:
            break;
        }
    }
}
