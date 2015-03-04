package com.tcl.simpletv.launcher2.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.tcl.simpletv.launcher2.R;

public class FlatwiseListener {	
	private static final String TAG = "FlatwiseListener";
	
	private SensorManager mGSensorManager;
	private Sensor mGSensor;
	private GSensorListener mGSensorListener;
	private AlertDialog mAlertDialog;
	private boolean isCircledeskState = false;
	
	private Context mContext;
	
	public FlatwiseListener(Context context){
		mContext = context;
	}
	
	/**
	 * 注册加速器监听
	 */
	public void registerGSensorListener(){
		if(mGSensorManager == null){
			mGSensorManager = (SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
			mGSensor = mGSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			mGSensorListener = new GSensorListener(mContext);
		}
		mGSensorManager.registerListener(mGSensorListener, mGSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	/**
	 * 注销加速器监听
	 */
	public void unregisterGSensorListener(){
		if(mGSensorManager != null){
			mGSensorManager.unregisterListener(mGSensorListener);
		}
	}
	
	/**
	 * 显示切换对话框
	 */
	private void showGotoCircleDeskDialog(Context context){
		Log.d(TAG, "------showGotoCircleDeskDialog------");
		if (null == mAlertDialog){
            mAlertDialog = new AlertDialog.Builder(context)
                    .setTitle(
                    		mContext.getString(R.string.goto_circledesk_confirm))
                    .setPositiveButton(
                    		mContext.getString(R.string.dialog_confirm),
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(
                                        DialogInterface dialog,
                                        int which){
                                	startCircledeskAction();
                                	mAlertDialog.dismiss();
                                }
                            })
                    .setNegativeButton(
                    		mContext.getString(R.string.dialog_cancel),
                            new DialogInterface.OnClickListener(){                            
                                public void onClick(
                                        DialogInterface dialog,
                                        int which){
                                	mAlertDialog.dismiss();
                                }
                            })
                        .setCancelable(false)
                        .show();
        }else{
        	mAlertDialog.show();
        }
	}
	
	/**
	 * 隐藏切换对话框
	 */
	private void dismissGotoCircleDeskDialog(){
//		Log.d(TAG, "------dismissGotoCircleDeskDialog------");
		if ((null != mAlertDialog) && (mAlertDialog.isShowing() == true)){			
			mAlertDialog.dismiss();
			Log.d(TAG, "dismissGotoCircleDeskDialog------dismiss");
		}
	}
	
	/**
	 * 启动平放模式桌面
	 */
	public void startCircledeskAction() {				
		Intent intent = new Intent();
		try {
			Log.d(TAG, "------startCircledeskAction----");
			intent.setClassName("com.tctmobile.circledesktop", "com.tctmobile.circledesktop.MainActivity");		
			mContext.startActivity(intent);			
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
		
		isCircledeskState = true;
	}

	public boolean isCircledeskState(){
		return isCircledeskState;
	}
	
	public void setCircledeskState(boolean circledeskState){
		isCircledeskState = circledeskState;
	}
	
	/**
	 * 角度监听
	 */
    public class GSensorListener implements SensorEventListener
    {
    	private boolean isFlatwiseState = false;
    	
        private float x = 0;
        private float y = 0;
        private float z = 0;
        private final float MAX_Z = 10;//10.5f;
        private final int FLATWISE_DEGREE = 150;
                
        Context mContext;
        
        public GSensorListener(Context context){
            super();            
            mContext = context;
        }
        
        public void onSensorChanged(SensorEvent event){            
            synchronized (this){
//                Log.d(TAG, "onSensorChanged sensortype = " + event.sensor.getType());
                
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)//SENSOR_TYPE)
                {
                    x = event.values[0];
                    y = event.values[1];
                    z = event.values[2];
                    
                    double degree = Math.toDegrees(Math.acos(z / MAX_Z));
                    
//                    Log.d(TAG, "x:"+event.values[0]+" y:"+event.values[1]+" z:"+event.values[2]+" degree= " + degree);
                                        
                    if((z < 0) && (Math.abs(z) < MAX_Z)){
                    	if(degree > FLATWISE_DEGREE){
	                    	if(isFlatwiseState == false){
	                    		isFlatwiseState = true;
	                    		showGotoCircleDeskDialog(mContext);
	                    	}
	                    }else{
	                    	isFlatwiseState = false;
	                    	dismissGotoCircleDeskDialog();
	                    }
                    }
                }
            }
        }
        
        public void onAccuracyChanged(Sensor arg0, int arg1)
        {
        }
    }    

}
