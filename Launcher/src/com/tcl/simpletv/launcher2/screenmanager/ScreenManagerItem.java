package com.tcl.simpletv.launcher2.screenmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.tcl.simpletv.launcher2.Launcher;
import com.tcl.simpletv.launcher2.LauncherApplication;
import com.tcl.simpletv.launcher2.R;

public class ScreenManagerItem extends FrameLayout {

	static final String TAG = "ScreenManagerItem";
	
	public ImageView mScreenAdd;	//添加图标
	public ImageView mScreenDelete;	//删除图标
	public ImageView mScreenHome;	//主页图标
//	public ImageView mScreenHomeIcon;
	public ImageView mScreenThumbnail;	//屏幕截图
	public View mScreenWhole;	//屏幕背景
	

	public ScreenManagerItem(Context paramContext) {
		this(paramContext, null);
	}

	public ScreenManagerItem(Context paramContext,
			AttributeSet paramAttributeSet) {
		this(paramContext, paramAttributeSet, 0);
	}

	public ScreenManagerItem(Context paramContext,
			AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	protected void onFinishInflate() {
		Log.d(TAG, "-----onFinishInflate-----");
		super.onFinishInflate();
		mScreenWhole = findViewById(R.id.screen_whole);
		mScreenThumbnail = ((ImageView) findViewById(R.id.screen_thumbnail));
		mScreenDelete = ((ImageView) findViewById(R.id.screen_delete));
		mScreenDelete.setTag(this);
		mScreenAdd = ((ImageView) findViewById(R.id.screen_add));
		mScreenHome = ((ImageView) findViewById(R.id.screen_home));
	}

	public void refreshData(Bitmap paramBitmap, Launcher launcher, int index)
	  {
		Log.d(TAG, "refreshData-----index = " + index);
		
	    int count = launcher.mWorkspace.getChildCount();
	    
	    mScreenThumbnail.setImageBitmap(paramBitmap);
	    mScreenWhole.setVisibility(View.VISIBLE);
	    mScreenHome.setVisibility(View.VISIBLE);
	    
	    if (ScreenManager.isAddScreenPostion(count, index)){

	    	mScreenDelete.setVisibility(View.GONE);
	      	mScreenHome.setVisibility(View.GONE);
	      	mScreenAdd.setVisibility(View.VISIBLE);
	      	mScreenWhole.setBackgroundResource(R.drawable.screen_mask_add);
	    }else{
	    	 mScreenDelete.setVisibility(View.VISIBLE);
	    	 mScreenHome.setVisibility(View.VISIBLE);
	    	 mScreenAdd.setVisibility(View.GONE);

	    	 if (index == LauncherApplication.getPreferenceUtils().getHomeScreen()){
	    		 mScreenHome.setImageResource(R.drawable.screen_home_current);
	    	 }else{
	    	 	mScreenHome.setImageResource(R.drawable.screen_home_normal);
	    	 }
	    	 
	    	 if(index == launcher.getCurrentWorkspaceScreen()){
	    		 mScreenWhole.setBackgroundResource(R.drawable.screen_mask_current);
	    	 }else{
	    	 	mScreenWhole.setBackgroundResource(R.drawable.screen_mask_normal);
	    	 }
	    }

	    
	  }

	public void setEmptyScreenView(Bitmap paramBitmap) {
		mScreenThumbnail.setImageBitmap(paramBitmap);
		mScreenWhole.setVisibility(View.INVISIBLE);
		mScreenHome.setVisibility(View.INVISIBLE);
		mScreenDelete.setVisibility(View.INVISIBLE);
	}


}