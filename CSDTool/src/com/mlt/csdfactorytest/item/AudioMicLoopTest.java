package com.mlt.csdfactorytest.item;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mlt.csdfactorytest.ItemTestActivity;
import com.mlt.csdfactorytest.R;

/** 
* @ClassName: Ringtone 
* @Description: Ringtone and Mic Test
* @author:   chehongbin
* @date:     2015年1月13日  下午7:05:22  
* Copyright (c) 2015,  Malata All Rights Reserved.
*/
public class AudioMicLoopTest  extends AbsHardware{
	private Context context;
	private Button mStartButton;
	private MediaRecorder recorder;
	private MediaPlayer   media;
	private AudioManager audioManager;
	private File file;
	private boolean isrecorder=false;
	private boolean isplaying = false;
	private boolean isTestbegin = true;
	private int max; //ringtone max voice
	private int current;// ringtone current voice
	private int musicMax;// ringtone current voice
	private int musicCurrent;// ringtone current voice
	/*chb add for VFOZBENQ-200 begin 20150930*/
	private int sysMax;
	private int sysCurrent;
	/*chb add for VFOZBENQ-200 end 20150930*/
	
	public AudioMicLoopTest(String text, Boolean visible) {
		super(text, visible);
	}
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		super.onCreate();
		// get AUDIO_SERVICE
		audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setMode(AudioManager.MODE_IN_CALL);// 把模式调成听筒放音模式 
		//Whether the headset pluged
		//isHeadsetConnect = audioManager.isWiredHeadsetOn();
		
		//获得music最大音量
		musicMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); 
		musicCurrent = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC ); 
		
		//获得听筒的音量最大值
		max = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL ); 
		current = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL ); 
		
		/*chb add for VFOZBENQ-200 begin 20150930*/
		//get system max volume
		sysMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM ); 
		sysCurrent = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM );
		audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, sysMax,AudioManager.USE_DEFAULT_STREAM_TYPE); 
		/*chb add for VFOZBENQ-200 end 20150930*/
		//设置为最大音量
		audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, max,AudioManager.USE_DEFAULT_STREAM_TYPE);  
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, musicMax,AudioManager.USE_DEFAULT_STREAM_TYPE);  
		
	}
	
	@Override
	public View getView(Context context) {
		this.context = context;
		View view = LayoutInflater.from(context).inflate(R.layout.item_mic, null);
		mStartButton = (Button) view.findViewById(R.id.startButton);
		//listener
		mStartButton.setText(R.string.Start_recording);
		//Toast.makeText(context, R.string.beginTest, Toast.LENGTH_LONG).show();
		ItemTestActivity.itemActivity.handler.sendEmptyMessage(ItemTestActivity.MSG_BTN_PASS_UNCLICKABLE);
		
		mStartButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isTestbegin) {
					Toast.makeText(AudioMicLoopTest.this.context, R.string.MAIN_Please_speaking, Toast.LENGTH_SHORT).show();
					startRecord();
					return;
				}
				if (isrecorder)	{
					stopRecord();
					return ;
				}
				if(isplaying) {	
					stopPlay();
					Toast.makeText(AudioMicLoopTest.this.context, R.string.playing_over, Toast.LENGTH_SHORT).show();
					return ;
				}
			}
		});
		return view;
	}
	
	/** 
	* @MethodName: startRecord 
	* @Description:begin record  by mic,
	* @return void   
	* @throws 
	*/
	/*chb modify for VFOZBENQ-200 begin 20150930*/
	void startRecord() {
		try {
			if(recorder==null) {
				recorder=new MediaRecorder();
			}
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(1);
			recorder.getAudioSourceMax();
			file=new File(Environment.getExternalStorageDirectory()+"/luyin.mp3");
			if(file.exists()) {
				file.delete();
			}
			recorder.setOutputFile(Environment.getExternalStorageDirectory()+"/luyin.mp3");
			recorder.setAudioEncoder(3);
			recorder.setAudioChannels(2);
			recorder.setAudioEncodingBitRate(128000);
			recorder.setAudioSamplingRate(48000);
			recorder.prepare();
			recorder.start();
			mStartButton.setText(R.string.Stop_talking_and_playing);
			isrecorder =true;
			isTestbegin = false;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/*chb modify for VFOZBENQ-200 end 20150930*/
	
	/** 
	* @MethodName: stopRecord 
	* @Description:stop record and playing record
	* @return void   
	* @throws 
	*/
	void stopRecord() {
		isrecorder = false;
		if (file != null) {
			recorder.stop();
			recorder.release();
			recorder = null;
		}
		Toast.makeText(context, R.string.hear_From_ringtone, Toast.LENGTH_LONG).show();
		try {
			Thread.sleep(1*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		synchronized (file) {
			if(media==null) {
				media=new MediaPlayer();
			}
			try {
				//loop playing record
				media.setLooping(true);
				media.setDataSource(Environment.getExternalStorageDirectory()+"/luyin.mp3");
				media.prepare();
				media.setVolume(15, 15);/*chb modify for VFOZBENQ-200 end 20150930*/
				media.start();
				mStartButton.setText(R.string.Stop_playing);
				isplaying = true;
				ItemTestActivity.itemActivity.handler.sendEmptyMessage(ItemTestActivity.MSG_BTN_PASS_CLICKABLE);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	/** 
	* @MethodName: stopPlay 
	* @Description:stop playing records 
	* @return void   
	* @throws 
	*/
	void stopPlay() {
		isplaying = false;
		isTestbegin = true;
		media.stop();
		media.release();
		media = null;
	    mStartButton.setText(R.string.Start_recording);
	}
	
	@Override
	public void onPause() {
        //设置为之前的音量
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,current,AudioManager.USE_DEFAULT_STREAM_TYPE);  
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,musicCurrent,AudioManager.USE_DEFAULT_STREAM_TYPE);
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,sysCurrent,AudioManager.USE_DEFAULT_STREAM_TYPE);
       //chb add for  VFOZBENQ-300 20151015 begin
        if (media!=null) {
            media.stop();
            media.release();
            media = null;
        }
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
        if (file != null) {
            if(file.exists()) {
                file.delete();
            }
        }
        //chb add for  VFOZBENQ-300 20151015 end
        super.onPause();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		//设置为之前的音量
		audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,current,AudioManager.USE_DEFAULT_STREAM_TYPE);  
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,musicCurrent,AudioManager.USE_DEFAULT_STREAM_TYPE);
		audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,sysCurrent,AudioManager.USE_DEFAULT_STREAM_TYPE);
		if (media!=null) {
			media.stop();
			media.release();
			media = null;
		}
		if (recorder != null) {
			recorder.stop();
			recorder.release();
			recorder = null;
		}
		if (file != null) {
			if(file.exists()) {
				file.delete();
			}
		}
	}
	
}



