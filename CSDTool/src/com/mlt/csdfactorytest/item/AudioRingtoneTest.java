package com.mlt.csdfactorytest.item;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.mlt.csdfactorytest.ItemTestActivity;
import com.mlt.csdfactorytest.R;

public class AudioRingtoneTest  extends AbsHardware{
	private Context mContext;
	private MediaPlayer   mMediaPlayer;
	private AudioManager audioManager;
	private int max; //ringtone max voice
	private int current;// ringtone current voice
	private int musicMax;// ringtone current voice
	private int musicCurrent;// ringtone current voice
	
	public AudioRingtoneTest(String text, Boolean visible) {
		super(text, visible);
	}
	@Override
	public void onCreate() {
		super.onCreate();
		// get AUDIO_SERVICE
		audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setMode(AudioManager.MODE_IN_CALL);// 
		Log.i("chehongbin", "AudioRingtoneTest_onCreate");
		
		//获得music最大音量
		musicMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); 
		musicCurrent = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC ); 
		
		//获得听筒的音量最大值
		max = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL ); 
		current = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL ); 
		
		//设置为最大音量
		audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, max,AudioManager.USE_DEFAULT_STREAM_TYPE);  
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, musicMax,AudioManager.USE_DEFAULT_STREAM_TYPE);  
		ItemTestActivity.itemActivity.handler.sendEmptyMessage(ItemTestActivity.MSG_BTN_PASS_UNCLICKABLE);
	}
	
	@Override
	public View getView(Context context) {
		this.mContext = context;
		View view = LayoutInflater.from(context).inflate(R.layout.item_ringtone, null);
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//chb add for  VFOZBENQ-300 20151015 begin
		audioManager.setMode(AudioManager.MODE_IN_CALL);
		Log.i("chehongbin", "AudioRingtoneTest_onResume");
		//chb add for  VFOZBENQ-300 20151015 end
		playRawMusic();
	}
	/** 
	* @MethodName: playmusic 
	* @Description:playing music ,music file at   
	* @return void   
	* @throws 
	*/
	public void playRawMusic() {
		try {
			 try {
				 mMediaPlayer = MediaPlayer.create(mContext, R.raw.testsong);
				 mMediaPlayer.setLooping(true); 
				 mMediaPlayer.setVolume(1f, 1f); // set play music sound size
				 mMediaPlayer.start();
				 
				 if (mMediaPlayer != null) {
					 ItemTestActivity.itemActivity.handler.sendEmptyMessage(ItemTestActivity.MSG_BTN_PASS_CLICKABLE);
				}
			 } catch (Exception e) {
             	e.printStackTrace();
             }
			 
		}  catch (Exception e) {
         	e.printStackTrace();
        } 
	} 
	
	@Override
	public void onStop() {
		super.onStop();
		if (mMediaPlayer!=null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		
	}
	//chb add for  VFOZBENQ-300 20151015 begin
	@Override
	public void onPause() {
		super.onPause();
		if (mMediaPlayer!=null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		audioManager.setMode(AudioManager.MODE_NORMAL);
		Log.i("chehongbin", "AudioRingtoneTest_onPause");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//设置为之前的音量
		audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,current,AudioManager.USE_DEFAULT_STREAM_TYPE);  
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,musicCurrent,AudioManager.USE_DEFAULT_STREAM_TYPE);
		if (mMediaPlayer!=null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		audioManager.setMode(AudioManager.MODE_NORMAL);
		Log.i("chehongbin", "AudioRingtoneTest_onDestroy");
	}
	//chb add for  VFOZBENQ-300 20151015 end
}
