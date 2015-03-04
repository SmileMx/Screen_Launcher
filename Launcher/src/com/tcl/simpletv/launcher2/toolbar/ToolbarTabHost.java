package com.tcl.simpletv.launcher2.toolbar;

import android.content.Context;
import android.location.GpsStatus.Listener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TabHost;

public class ToolbarTabHost extends TabHost  {

	private BackKeyDown mBackKeyDown;
	private String TAG = "ToolbarTabHost";

	public ToolbarTabHost(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ToolbarTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		Log.d(TAG, "ToolbarTabHost:::" + event.getKeyCode());
		if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			mBackKeyDown.backorTouchOutside();
			return true;
		}else if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_MENU){
			mBackKeyDown.hideToolbars();
			return false;
		}
		return super.dispatchKeyEvent(event);
	}

//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev) {
//		// TODO Auto-generated method stub
//		Log.d(TAG, "dispatchTouchEvent---event.getX()="+ev.getX()+",event.getY()="+ev.getY());
//		return super.dispatchTouchEvent(ev);
//	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		Log.d(TAG, "event.getX()="+event.getX()+",event.getY()="+event.getY());
		if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
			Log.d(TAG, "MotionEvent.ACTION_OUTSIDE");
			mBackKeyDown.backorTouchOutside();
			return true;
		}
//		else if(event.getAction() == MotionEvent.ACTION_DOWN){
//			
//		}
		return super.onTouchEvent(event);
	}

	public void setInterface(BackKeyDown backKeyDown) {
		mBackKeyDown = backKeyDown;
	}

	public interface BackKeyDown {
//		void startBack();
//		void startTouch();
		void backorTouchOutside();
		void hideToolbars();
	}
}
