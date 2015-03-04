package com.tcl.simpletv.launcher2.localwidget;

import com.inveno.piflow.activity.FlyShareApplication;
import com.inveno.piflow.entity.view.waterwall.WaterwallMain;
import com.inveno.piflow.entity.view.waterwall.WaterwallScrollView;
import com.tcl.simpletv.launcher2.LauncherApplication;
import com.tcl.simpletv.launcher2.R;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class LocalWidgetFallsStreamNews extends LocalWidgetView{

	private static final String TAG = "LocalWidgetFallsStreamNews";
	
	private int WIDGET_WIDTH_LAND = 1050;
	private int WIDGET_HEIGHT_LAND = 750;
	
	private int WIDGET_WIDTH_PORT = 550;
	private int WIDGET_HEIGHT_PORT = 1200;
	
	private int mWidgetWidth;
	private int mWidgetHeight;
	
	private WaterwallMain mWaterwallMain;
	private FlyShareApplication myApp;
	private Context mContext;
	
	public LocalWidgetFallsStreamNews(Context context){
		super(context);
		
//		setLayoutParams(new LayoutParams(mWidgetWidth, mWidgetHeight));
//		setBackgroundColor(0x550000ff);
		
		mContext = context;
		
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			mWidgetWidth = WIDGET_WIDTH_LAND;
			mWidgetHeight = WIDGET_HEIGHT_LAND;
		}else{
			mWidgetWidth = WIDGET_WIDTH_PORT;
			mWidgetHeight = WIDGET_HEIGHT_PORT;
		}
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.local_widget_falls_stream_news, null);
		
		addView(view, mWidgetWidth, mWidgetHeight);
		
		LinearLayout widgetLayout = (LinearLayout) view.findViewById(R.id.widget_layout);
		
		myApp = (FlyShareApplication) LauncherApplication.getInstance();
		mWaterwallMain = new WaterwallMain(context, mWidgetWidth - 40, mWidgetHeight - 40, myApp);
//		mWaterwallScrollView = new WaterwallScrollView(context, mWidgetWidth - 40, mWidgetHeight - 40);//实例化瀑布流
		widgetLayout.addView(mWaterwallMain, mWidgetWidth, mWidgetHeight);
		
//		mWaterwallScrollView.setmApp(myApp);//给瀑布流初始化必要的参数

//		mWaterwallScrollView.load();//调用此方法 加载资讯
		
		
//		setLayoutParams(new LayoutParams(mWidgetWidth, mWidgetHeight));
		setBackgroundColor(0x050000ff);
//		
		initData();
//		
//		mWaterwallScrollView = new WaterwallScrollView(context);//实例化瀑布流
//		addView(mWaterwallScrollView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//				
//		myApp = (FlyShareApplication)LauncherApplication.getInstance();
//		
//		mWaterwallScrollView.setmApp(myApp);//给瀑布流初始化必要的参数
//		
//		mWaterwallScrollView.load();//调用此方法 加载资讯
	}
	
	public LocalWidgetFallsStreamNews(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		initData();
	}
	
	public LocalWidgetFallsStreamNews(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
		initData();
	}
	View view;
	private void initView(Context context){
		mContext = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.local_widget_falls_stream_news, null);
//		setBackgroundColor(0x880000ff);
		addView(view, new LayoutParams(mWidgetWidth, mWidgetHeight));
		
		LinearLayout widgetLayout = (LinearLayout) view.findViewById(R.id.widget_layout);//父容器
	}
	private void initData(){
		
        	mLocalWidgetInfo.localWidgetName = "newsWidget";
        	mLocalWidgetInfo.localWidgetId = -1000;
        	
        	mLocalWidgetInfo.provider = null;
        	
        	mLocalWidgetInfo.minWidth = mWidgetWidth - 400;
            mLocalWidgetInfo.minHeight = mWidgetHeight;
            mLocalWidgetInfo.minResizeWidth = mWidgetWidth - 400;
            mLocalWidgetInfo.minResizeHeight = mWidgetHeight;
            mLocalWidgetInfo.updatePeriodMillis = 1000;
            mLocalWidgetInfo.initialLayout = 300;
            mLocalWidgetInfo.initialKeyguardLayout = 100;
           
            mLocalWidgetInfo.configure = new ComponentName("test", "widget");
            
            mLocalWidgetInfo.label = mContext.getString(R.string.local_widget_falls_stream_news);
            mLocalWidgetInfo.icon = R.drawable.falls_stream_news_preview;
            mLocalWidgetInfo.previewImage = R.drawable.falls_stream_news_preview;
            mLocalWidgetInfo.autoAdvanceViewId = 0;//new LandWidgetLocalPhoto(mLauncher).getId();
            mLocalWidgetInfo.resizeMode = LocalWidgetInfo.RESIZE_BOTH;
            mLocalWidgetInfo.widgetCategory = 1;

    	


	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		Log.d(TAG, "----show-----");
//		mWaterwallScrollView.load();//调用此方法 加载资讯
		mWaterwallMain.firstLoad();
	}
}
