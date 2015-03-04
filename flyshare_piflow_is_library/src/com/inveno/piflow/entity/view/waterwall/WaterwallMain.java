package com.inveno.piflow.entity.view.waterwall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.inveno.piflow.R;
import com.inveno.piflow.activity.FlyShareApplication;

public class WaterwallMain extends RelativeLayout {
	
	private String TAG = "WaterwallMain";
	
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	
	private LinearLayout mWaterwallScrollLayout;
	private LinearLayout mDetailLayout;
	private LinearLayout mDialogLayout;
	
	private WaterwallScrollView mWaterwallScrollView;

	public WaterwallMain(Context context,int width,int height , FlyShareApplication app) {
		super(context);
		init(context, width, height, app);
	}
	
	public WaterwallMain(Context context, AttributeSet attrs,int width,int height , FlyShareApplication app) {
		super(context, attrs);
		init(context, width, height, app);
	}
	
	private void init(Context context,int width,int height , FlyShareApplication app){
		mContext = context;
		this.mLayoutInflater = (LayoutInflater) context
		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mLayoutInflater.inflate(R.layout.wph_waterwall_main_tcl, this);
		
		mWaterwallScrollLayout = (LinearLayout) findViewById(R.id.waterwallscrollview_layout);				
		mDetailLayout = (LinearLayout) findViewById(R.id.detail_layout);
		mDialogLayout = (LinearLayout) findViewById(R.id.dialog_layout);
		
		mWaterwallScrollView = new WaterwallScrollView(context, width, height,mDetailLayout,mDialogLayout,this);//实例化瀑布流		
		mWaterwallScrollView.setmApp(app);//给瀑布流初始化必要的参数
		mWaterwallScrollLayout.addView(mWaterwallScrollView);
		
		Receiver receiver = new Receiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SHUTDOWN);
		context.registerReceiver(receiver, filter);
		
//		setOnLongClickListener(new OnLongClickListener() {
//			
//			@Override
//			public boolean onLongClick(View v) {
//				Toast.makeText(mContext, "WaterwallMain 长按", 1000).show();
//				return false;
//			}
//		});
		
	}
	
	/**
	 * 第一次添加后加载数据
	 */
	public void firstLoad(){
		if(mWaterwallScrollView!=null){
			mWaterwallScrollView.load();
		}
	}
	
	/**
	 * 保存最后一页
	 */
	public void saveLastNews(){
		if(mWaterwallScrollView!=null){
			mWaterwallScrollView.saveLastShowFlowNews();
		}
	}
	
	private class Receiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			
			saveLastNews();
			
		}
		
	}

	public boolean clickKeyBack(){
		if(this.mDialogLayout.getChildCount()==0 && this.mDetailLayout.getChildCount()>0){
			this.mDetailLayout.removeAllViews();
			return true;
		}else if(this.mDetailLayout.getChildCount()>0){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		Toast.makeText(mContext, "mian onTouchEvent了", 1000).show();
		((View)getParent()).onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		Toast.makeText(mContext, "mian onInterceptTouchEvent了", 1000).show();
		onTouchEvent(ev);
		return super.onInterceptTouchEvent(ev);
	}
	
}
