package com.mlt.csdfactorytest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.GridView;

import com.mlt.csdfactorytest.adapter.TestItemAdapter;
import com.mlt.csdfactorytest.item.MyApplication;
import com.mlt.csdfactorytest.item.tools.SaveStatusTool;
import com.mlt.csdfactorytest.R;
/**
 * 
 * file name:ManualTestActivity.java
 * Copyright MALATA ,ALL rights reserved
 * 
 * This class is an activity,show all test items view,
 * and you can click one item to start test,when test finish,
 * you will back to this activity,the result will be show.
 * 
 * 2015-1-26
 * author:laiyang   
 * Modification history
 * -------------------------------------
 * 
 * -------------------------------------
 */
public class ManualTestActivity extends Activity {
	// log's tag
	private static final String TAG = "MaunalTestAcitivty";
	// GridView is to show all test items
	private GridView mGridView;
	// GridView's adapter
	private TestItemAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_manual_test);
		
		MyApplication.clearResult();
		// init view and set Adapter
		mGridView = (GridView)findViewById(R.id.girdView);
		
		//pss del  for VFOZBENQ-211 20150917
		//mAdapter = new TestItemAdapter(this);
		//mGridView.setAdapter(mAdapter);
	}
	
	@Override
	protected void onResume() {
		// update GridView when current activity will be showed 
		
		//pss add  for VFOZBENQ-211 20150917
		mAdapter = new TestItemAdapter(this);
		mGridView.setAdapter(mAdapter);
		
		mGridView.invalidateViews();
		super.onResume();
	}
	
	
	@Override
	protected void onStop() {
		// when leave this activity,save current test result in SharedPreference
		Log.i(TAG, "is saving manual status");
		SaveStatusTool.saveTestResults(this);
		super.onStop();
	}
}
