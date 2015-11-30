package com.mlt.csdfactorytest.item;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mlt.csdfactorytest.R;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.sax.StartElementListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;


//simCard
import com.android.internal.telephony.PhoneConstants;
import com.mediatek.telephony.TelephonyManagerEx;
import com.mlt.csdfactorytest.ItemTestActivity;

import com.mlt.csdfactorytest.R.string;

//import com.mediatek.common.featureoption.FeatureOption;
//import com.mediatek.gemini.GeminiUtils;
//import com.mediatek.gemini.SimInfoRecord;
import android.telephony.SubscriptionManager;
//import android.telephony.SubInfoRecord;//yutianliang delete
import android.telephony.SubscriptionInfo;//yutianliang add 

////memoryCard
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.Settings;
import android.os.SystemProperties;
import android.content.res.Resources;

/**
 * @ClassName: SimCardAndSingle
 * @Description: This kind of mobile phone CARDS, memory CARDS and call test
 * @Function: 1, read the sim card information, to determine whether a sim card
 *            2, read the memory card information, to determine whether a memory
 *            card 3, set up the call button, click to dial the phone number of
 *            the corresponding
 * @author: peisaisai
 * @date: 2015-01-15 13:47:25 Copyright (c) 2015, malata All Rights Reserved.
 */
public class MemoryCard extends AbsHardware {
    private Context context;

    // Used to display the memory card information, sim1 test results, sim2 test
    // results,
    private TextView mtvSDState, mtvSDTotalSize, mtvSDUsedSize,
            mtvSDAvailbleSize, mtvSD2State, mtvSD2TotalSize, mtvSD2UsedSize,
            mtvSD2AvailbleSize, mtvOTGState, mtvOTGTotalSize, mtvOTGUsedSize,
            mtvOTGAvailbleSize;

    private  String TNAME = "";
    private  String FLASHNAME = "";
    private  String USBNAME = "";
    private  String MB = "";
    // the message to update the view
    private final int UPDATE_SIMCARDANDSIGLE = 0;

    // Memory card information management class instances
    private StorageManager mStorageManager = null;

    // the flag to judge the EMMC,SDcard,sim1,sim2 and usb etc
    private boolean mboolMountedSD, mboolMountedSD2, mboolMountedOTG;

    private float mSDTotalSize, mSDUsedSize, mSDAvailbleSize, mSD2TotalSize,
            mSD2UsedSize, mSD2AvailbleSize, mOTGTotalSize, mOTGUsedSize,
            mOTGAvailbleSize;

    public MemoryCard(String text, Boolean visible) {
        super(text, visible);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    private void loadSimFlashInfoThread() {
        // TODO Auto-generated method stub
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                // check memory Card
                memoryCardTest();

                Message msg = new Message();
                msg.what = UPDATE_SIMCARDANDSIGLE;
                handler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        mboolMountedOTG = false;
        mboolMountedSD = false;
        mboolMountedSD2 = false;
        mOTGAvailbleSize = 0;
        mOTGTotalSize = 0;
        mOTGUsedSize = 0;
        Resources r = Resources.getSystem();
        TNAME = Resources.getSystem()
                .getText(com.android.internal.R.string.storage_sd_card)
                .toString();
        FLASHNAME = Resources.getSystem().getText(com.android.internal.R.string.storage_phone).toString();
        USBNAME = Resources.getSystem().getString(
                com.android.internal.R.string.storage_external_usb);
        MB = Resources.getSystem().getText(com.android.internal.R.string.megabyteShort).toString();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSimFlashInfoThread();
    }

    /**
     * @MethodName: memoryCardTest
     * @Description: Through getVolumeList () to obtain memory card information,
     *               information obtained through the custom class mountPoint
     *               memory card, through tv_TFlash test results displayed.
     * @return void
     * @throws Copyright
     *             (c) 2015, Malata All Rights Reserved.
     */
    private void memoryCardTest() {
        mStorageManager = (StorageManager) context
                .getSystemService(Context.STORAGE_SERVICE);
        StorageVolume[] storageVolumeList = mStorageManager.getVolumeList();
        if (storageVolumeList != null) {
            for (StorageVolume volume : storageVolumeList) {
                MountPoint mountPoint = new MountPoint();
                mountPoint.mDescription = volume.getDescription(context);
                mountPoint.mPath = volume.getPath();
                try {
                    StatFs stat = new StatFs(volume.getPath());
                    long blockSize = stat.getBlockSize();
                    long availableBlocks = stat.getAvailableBlocks();
                    long totalSize = (stat.getTotalBytes() / 1024 / 1024);
                    mountPoint.mIsMounted = isMounted(volume.getPath());
                    mountPoint.mMaxFileSize = (blockSize * availableBlocks
                            / 1024 / 1024);// volume.getMaxFileSize()
                    Log.i("pss", "name:" + mountPoint.mDescription + "size:"
                            + totalSize + "IsMounted:" + mountPoint.mIsMounted);
                    Log.i("pss", "TNAME = " + TNAME);
                    Log.i("pss", "USBNAME = " + USBNAME);
                    Log.i("pss", "FLASHNAME = " + FLASHNAME);
                    Log.i("pss", "MB = " + MB);
                    if (mountPoint.mDescription.contains(TNAME)) {
                        mboolMountedSD2 = mountPoint.mIsMounted;
                        mSD2TotalSize = totalSize;
                        mSD2AvailbleSize = totalSize - mountPoint.mMaxFileSize;
                        mSD2UsedSize = mSD2TotalSize - mSD2AvailbleSize;
                    } else if (mountPoint.mDescription.equals(USBNAME)) {
                        mboolMountedOTG = mountPoint.mIsMounted;
                        mOTGTotalSize = totalSize;
                        mOTGAvailbleSize = totalSize - mountPoint.mMaxFileSize;
                        mOTGUsedSize = mOTGTotalSize - mOTGAvailbleSize;
                    }else if(mountPoint.mDescription.equals(FLASHNAME)){
                        mboolMountedSD = mountPoint.mIsMounted;
                        mSDTotalSize = totalSize;
                        mSDAvailbleSize = totalSize - mountPoint.mMaxFileSize;
                        mSDUsedSize = mSDTotalSize - mSDAvailbleSize;
                    }
                } catch (Exception e) {

                }
            }
        }
    }

    //
    /**
     * @ClassName: MountPoint
     * @Description: Class used to temporarily save memory card information
     * @Function: TODO ADD FUNCTION
     * @author: peisaisai
     * @date: 2015-01-15 14:01:26 Copyright (c) 2015, Malata All Rights
     *        Reserved.
     */
    private static class MountPoint {
        String mDescription;
        String mPath;
        boolean mIsExternal;
        boolean mIsMounted;
        long mMaxFileSize;
    }

    /**
     * This method checks whether SDcard is mounted or not
     * 
     * @param mountPoint
     *            the mount point that should be checked
     * @return true if SDcard is mounted, false otherwise
     */
    protected boolean isMounted(String mountPoint) {

        String state = null;
        state = mStorageManager.getVolumeState(mountPoint);
        // LogUtils.d(TAG, "state = " + state);
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (UPDATE_SIMCARDANDSIGLE == msg.what) {
                DecimalFormat decimalFormat = new DecimalFormat(".00");
                String hasCard = context.getString(R.string.mtvmemorycardhascard);
                String noCard = context.getString(R.string.mtvmemorycardnocard);
                String state = context.getString(R.string.mtvmemorycardstate);
                String totalSize = context.getString(R.string.mtvmemorycardtotalsize);
                String usedSize = context.getString(R.string.mtvmemorycardusedspace);
                String availbleSize = context.getString(R.string.mtvmemorycardavailablesize);
				
				//pss mofify for VFOZBENQ-140 20150917 start
				/*before
				mtvSDState.setText( state + (mboolMountedSD? hasCard : noCard));
                mtvSDTotalSize.setText(totalSize + mSDTotalSize + MB);
                mtvSDUsedSize.setText(usedSize + mSDUsedSize+MB);
                mtvSDAvailbleSize.setText(availbleSize + mSDAvailbleSize + MB);
				*/
                mtvSDState.setText( state + (mboolMountedSD2? hasCard : noCard));
                mtvSDTotalSize.setText(totalSize + mSD2TotalSize + MB);
                mtvSDUsedSize.setText(usedSize + mSD2UsedSize+MB);
                mtvSDAvailbleSize.setText(availbleSize + mSD2AvailbleSize + MB);
                //pss mofify for VFOZBENQ-140 20150917 end
				
                /* pss del for VFOZBENQ-140 20150917
				mtvSD2State.setText( state + (mboolMountedSD2? hasCard : noCard));
                mtvSD2TotalSize.setText(totalSize + mSD2TotalSize + MB);
                mtvSD2UsedSize.setText(usedSize + mSD2UsedSize+MB);
                mtvSD2AvailbleSize.setText(availbleSize + mSD2AvailbleSize + MB);*/
                
                /*pss del for VFOZBENQ-140 20150917
				mtvOTGState.setText( state + (mboolMountedOTG? hasCard : noCard));
                mtvOTGTotalSize.setText(totalSize + mOTGTotalSize + MB);
                mtvOTGUsedSize.setText(usedSize + mOTGUsedSize+MB);
                mtvOTGAvailbleSize.setText(availbleSize + mOTGAvailbleSize + MB);*/
            }
        };
    };

    @Override
    public View getView(Context context) {
        // TODO Auto-generated method stub
        this.context = context;
        LayoutInflater factory = LayoutInflater.from(context);
        View view = factory.inflate(R.layout.item_flash, null);
        /*pss del for VFOZBENQ-140 20150917
		mtvOTGAvailbleSize = (TextView) view.findViewById(R.id.mtvotgavilablesize);
        mtvOTGState = (TextView) view.findViewById(R.id.mtvotgstate);
        mtvOTGUsedSize = (TextView) view.findViewById(R.id.mtvotgusedsize);
        mtvOTGTotalSize = (TextView) view.findViewById(R.id.mtvotgtotalsize);*/
        
        mtvSDAvailbleSize = (TextView) view.findViewById(R.id.mtvsdavilablesize);
        mtvSDState = (TextView) view.findViewById(R.id.mtvsdstate);
        mtvSDUsedSize = (TextView) view.findViewById(R.id.mtvsdusedsize);
        mtvSDTotalSize = (TextView) view.findViewById(R.id.mtvsdtotalsize);
        
        /*pss del for VFOZBENQ-140 20150917
		mtvSD2AvailbleSize = (TextView) view.findViewById(R.id.mtvsd2avilablesize);
        mtvSD2State = (TextView) view.findViewById(R.id.mtvsd2state);
        mtvSD2UsedSize = (TextView) view.findViewById(R.id.mtvsd2usedsize);
        mtvSD2TotalSize = (TextView) view.findViewById(R.id.mtvsd2totalsize);*/
        return view;
    }
}
