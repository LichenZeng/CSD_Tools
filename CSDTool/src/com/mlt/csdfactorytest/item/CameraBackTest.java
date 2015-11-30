package com.mlt.csdfactorytest.item;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mlt.csdfactorytest.ItemTestActivity;
import com.mlt.csdfactorytest.R;

public class CameraBackTest extends AbsHardware {
	private String TAG = "CAMERA";
	private Context mContext;
	private TextView mtvBackCameraOpen,mtvBackCameraTakePicture;
	private Intent mIntent = null;
	private int mCameraPosition;  
	
	private boolean mBackCameraOK = false;
	private AlertDialog.Builder mBuilder;
	public static final int  PICK_BACK_CAMERA = 100;
	String mTestString = "malata_camera_intent"; //chb add for VFOZBENQ-201 20150930
	
	public void onCreate() {
		mBackCameraOK = false;
		/**set the pass button can't click*/
		ItemTestActivity.itemActivity.handler.sendEmptyMessage(ItemTestActivity.MSG_BTN_PASS_UNCLICKABLE);
		
		if (checkCameraHardware()) { // if camera exited
			if (FindBackCameraExist()) {
				mIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				mIntent.putExtra("autofocus", true); //  autofocus
				mIntent.putExtra("fullScreen", false); // no fullScreen
				mIntent.putExtra("malata_intent", mTestString); //chb add for VFOZBENQ-201 20150930
				mIntent.putExtra("android.intent.extras.CAMERA_FACING", 0);// open back Camera
				/**open camera by Method Call startActivityForResult*/
				ItemTestActivity.itemActivity.startActivityForResult(mIntent, PICK_BACK_CAMERA);
			}else {
				mCameraExitDialog();
				mtvBackCameraTakePicture.setText(R.string.noprocess);
				mtvBackCameraTakePicture.setBackgroundColor(Color.RED);
			}
			
		}else {
			mCameraExitDialog(); //tips
		}
	}
	
	public CameraBackTest(String text, Boolean visible) {
		super(text, visible);
	}
	
	@Override
	public View getView(Context context) {
		this.mContext = context;
		View view = LayoutInflater.from(context).inflate(R.layout.item_cameraback, null);
		mtvBackCameraOpen = (TextView) view.findViewById(R.id.back_camera_open_tag);
		mtvBackCameraTakePicture = (TextView) view.findViewById(R.id.back_camera_takepicture_tag);
		
		return view;
	}
	
	
	/** 
	* @MethodName: checkCameraHardware 
	* @Functions:Check whether the phone has a camera
	* @return	:boolean   
	*/
	private boolean checkCameraHardware () {
	    if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
	
	/* (non-Javadoc)
	 * @see com.malata.factorytest.item.AbsHardware#onActivityResult(int, int, android.content.Intent)
	 */
	public void onActivityResult (int requestCode, int resultCode,	Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == PICK_BACK_CAMERA) { //open back camera
			mtvBackCameraOpen.setBackgroundColor(Color.GREEN);
			if (resultCode == Activity.RESULT_OK) {
				mBackCameraOK = true;
				mtvBackCameraTakePicture.setText(R.string.success);
				mtvBackCameraTakePicture.setBackgroundColor(Color.GREEN);
				isTestSuccess();
			}
			else {
				mtvBackCameraTakePicture.setText(R.string.fail);
				mtvBackCameraTakePicture.setBackgroundColor(Color.RED);
			}
		}
	}
	
	/** 
	* @MethodName: FindFrontCamera 
	* @Functions:Get front-facing camera.
	* @return	:int   
	*/
	@SuppressLint("NewApi")
	private boolean FindFrontCameraExist(){
	    int cameraCount = 0;
	    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
	    cameraCount = Camera.getNumberOfCameras(); // get cameras number
	    for (mCameraPosition = 0; mCameraPosition < cameraCount;mCameraPosition++ ) {
	    	Camera.getCameraInfo( mCameraPosition, cameraInfo ); // get camerainfo
	        if ( cameraInfo.facing ==Camera.CameraInfo.CAMERA_FACING_FRONT ) { // front camera
	           return  true;//camera exited
	          
	        }
	    }
		return false;
	}
		
	/** 
	* @MethodName: FindBackCamera 
	* @Functions:Get back-facing camera.
	* @return	:int   
	*/
	@SuppressLint("NewApi")
	private boolean FindBackCameraExist(){
	        int cameraCount = 0;
	        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
	        cameraCount = Camera.getNumberOfCameras(); // get cameras number
	        for (mCameraPosition = 0; mCameraPosition < cameraCount;mCameraPosition++ ) {
	            Camera.getCameraInfo( mCameraPosition, cameraInfo ); // get camerainfo
	            if ( cameraInfo.facing ==Camera.CameraInfo.CAMERA_FACING_BACK ) {  // back camera
	               return  true; //camera exited
	            }
	        }
	    	return false;
	}	
	
	private void mCameraExitDialog() {
	    mBuilder = new Builder(mContext);
	    if (!FindBackCameraExist()) {
			mBuilder.setMessage(R.string.bcamera_no_exits);
		} 
	    else if(!FindFrontCameraExist()){
			mBuilder.setMessage(R.string.fcamera_no_exits);
		}
		mBuilder.setTitle(R.string.tip);
		mBuilder.setPositiveButton(R.string.tip_ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//do something , When you click on the Dialog button
			}
		});
		mBuilder.create().show();
	}
	
	/** 
	* @MethodName: isTestSuccess 
	* @Functions: Whether the test pass
	* @return	:void   
	* @throws 
	*/
	private void isTestSuccess() {
		if (mBackCameraOK ) {
			ItemTestActivity.itemActivity.handler.sendEmptyMessage(ItemTestActivity.MSG_BTN_PASS_CLICKABLE);
		}
	}	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
}
