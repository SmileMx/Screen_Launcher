package com.tcl.simpletv.launcher2.localwidget;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public abstract class LocalWidgetView extends ViewGroup {

	public AppWidgetProviderInfo mProviderInfo;
	public LocalWidgetInfo mLocalWidgetInfo;
	
	public LocalWidgetView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initBasicData();
	}
	
	public LocalWidgetView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initBasicData();
	}

	public LocalWidgetView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initBasicData();
	}
	
	public void initBasicData(){
		
		mProviderInfo = new AppWidgetProviderInfo();
		
		mLocalWidgetInfo = new LocalWidgetInfo();
		
		mProviderInfo.provider = new ComponentName("com.tcl.simpletv.launcher2",
				"com.tcl.simpletv.launcher2.localwidget"); 
	}
	
	public abstract void show();
	
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		final View child = getChildAt(0);
		child.setVisibility(View.VISIBLE);
		child.measure(r - l, b - t);
		child.layout(0, 0, child.getMeasuredWidth(),
				child.getMeasuredHeight());
		System.out.println(child.getMeasuredWidth() + ":"
				+ child.getMeasuredHeight());

	}

}
