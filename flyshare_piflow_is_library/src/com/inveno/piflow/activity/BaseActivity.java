package com.inveno.piflow.activity;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.inveno.piflow.R;
import com.inveno.piflow.biz.upload.PvBasicStateDataBiz;
import com.inveno.piflow.entity.model.upload.PvBasicStateData;
import com.inveno.piflow.tools.commontools.DeviceConfig;

/**
 * activity基类，抽象一些共用方法，无标题显示，添加当前Activity到全局
 * 抽象接收广播改变界面的操作，统一toast显示风格，后续可扩展更多公共内容
 * 
 * @date 2012-12-05 1.新增两个PopupWindow初始化，功能为应用内弹窗推送和应用内MENU键弹出菜单统一
 *       当子类ACT需要这两个功能时，可调用注册方法注册使用这两个功能 只有子类注册时，此子类才会实例化这两个控件
 * 
 *       使用指南： 当子类Act需要弹出菜单时，可调用registMenuPopup（）方法进行注册
 * 
 * @date 2013-04-08 重新startActivity方法，在其中增加改变计算启动次数的flag
 * 
 * @author blueming.wu
 * @date 2012-07-16
 */
public abstract class BaseActivity extends Activity {

	protected Context mContext;

	/** 内部广播接收者对象，不做任何事情，具体事件由子类实现 */
	private BaseReceiver baseReceiver;
	private IntentFilter filter;

	/** 统一toast提示风格 */
	public Toast toast;
	public TextView toastTV;

	/** toast显示时距离底部的距离，可定制 */
	public int INSTANCE_FROM_BOTTOM = 130;

	/** 全局缓存 */
	protected FlyShareApplication myApp;

	/** 全局下部弹窗提醒推送 */
	protected PopupWindow pushPopup;
	protected ImageView pushPopupIv;
	protected TextView pushPopupTv;
	protected int pushType;

	/** 菜单弹出框 */
	protected PopupWindow menuPopup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getWindow().setBackgroundDrawable(null);
		myApp = (FlyShareApplication) getApplication();
		myApp.getActs().add(this);
		mContext = this;
		baseReceiver = new BaseReceiver();
		filter = new IntentFilter();
		addAction(filter);// 调用子类的具体实现来注册广播种类
		registerReceiver(baseReceiver, filter);
		toast = new Toast(getApplicationContext());
		initToast();

	}

	/**
	 * 子类实现此方法，为自己需要监听的广播添加action
	 * 
	 * @param filter
	 */
	protected abstract void addAction(IntentFilter filter);

	/**
	 * 子类实现此方法，接收到特定广播，改变特变UI界面
	 * 
	 * @param context
	 * @param intent
	 */
	protected abstract void changeUI(Context context, Intent intent);

	/**
	 * 初始化界面控件
	 */
	protected abstract void initViews();

	/**
	 * 初始化显示需要的数据
	 */
	protected abstract void initData();

	/**
	 * 初始化其他成员变量
	 */
	protected abstract void initMembers();

	/**
	 * 按具体逻辑顺序调用三个具体初始化方法
	 */
	protected abstract void init();

	/**
	 * 当子类需要统一风格的MENU弹窗时，调用此方法注册，调用之后可使用MENU弹窗
	 */
	protected void registMenuPopup2(MenuPopupListener listener,
			int... drawableRes) {
		initMenuPopup2(listener, drawableRes);
	}

	/**
	 * 当子类注册了菜单时，需要传入一个此接口的子类
	 * 
	 * @author blueming.wu
	 * 
	 */
	protected interface MenuPopupListener {

		/** 点击按钮1的事件 */
		void clickBtn1();

		/** 点击按钮2的事件 */
		void clickBtn2();

		/** 点击按钮3的事件 */
		void clickBtn3();

		/** 点击按钮4的事件 */
		void clickBtn4();
	}

	/**
	 * 内部广播接收类，调用具体子类重写的改变界面方法来实现具体界面更改
	 * 
	 * @author blueming.wu
	 */
	private class BaseReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			changeUI(context, intent);
		}

	}

	/**
	 * 初始化menu键弹出菜单
	 */
	private void initMenuPopup2(final MenuPopupListener listener,
			final int... drawables) {
		LinearLayout contentView = null;
		ImageButton imageButton1 = null;
		ImageButton imageButton2 = null;
		ImageButton imageButton3 = null;
		ImageButton imageButton4 = null;
		final LinearLayout popupLayout;
		final View view;
		int length = drawables.length;
		OnClickListener lis = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 标记点击了哪个按钮
				int tag = 0;
				int id = v.getId();
				if(id== R.id.popup_menu_btn1){
					listener.clickBtn1();
					tag = drawables[0];
				}else if(id==R.id.popup_menu_btn2){
					listener.clickBtn2();
					tag = drawables[1];
				}else if(id== R.id.popup_menu_btn3){
					tag = drawables[2];
					listener.clickBtn3();
				}else if(id== R.id.popup_menu_btn4){
					tag = drawables[3];
					listener.clickBtn4();
					
				}
				if (tag != R.drawable.popup_btn_text
						&& tag != R.drawable.popup_btn_light
						&& tag != R.drawable.popup_btn_ri
						&& tag != R.drawable.popup_btn_ye) {
					if (menuPopup.isShowing()) {
						Handler handler = new Handler(getMainLooper());
						handler.postDelayed(new Runnable() {

							@Override
							public void run() {
								menuPopup.dismiss();
							}
						}, 100);

					}
				}
			}
		};

		switch (length) {
		case 1:

			contentView = (LinearLayout) LayoutInflater.from(mContext).inflate(
					R.layout.popup_menu_common_style1, null);
			popupLayout = (LinearLayout) contentView
					.findViewById(R.id.popup_linerlayout);
			view = (View) contentView.findViewById(R.id.popup_view);
			imageButton1 = (ImageButton) contentView
					.findViewById(R.id.popup_menu_btn1);
			imageButton1.setBackgroundResource(drawables[0]);
			imageButton1.setOnClickListener(lis);
			OnTouchListener lis1 = new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN
							|| event.getAction() == MotionEvent.ACTION_MOVE) {
						popupLayout.setBackgroundColor(getResources().getColor(
								R.color.popubackgroud_o));
						view.setBackgroundColor(getResources().getColor(
								R.color.popuMenuview));
					} else {
						popupLayout.setBackgroundColor(getResources().getColor(
								R.color.popubackgroud));
						view.setBackgroundColor(getResources().getColor(
								R.color.black));
					}
					return false;
				}
			};
			popupLayout.setOnTouchListener(lis1);
			imageButton1.setOnTouchListener(lis1);
			break;
		case 2:
			contentView = (LinearLayout) LayoutInflater.from(mContext).inflate(
					R.layout.popup_menu_common_style2, null);
			popupLayout = (LinearLayout) contentView
					.findViewById(R.id.popup_linerlayout);
			view = (View) contentView.findViewById(R.id.popup_view);
			imageButton1 = (ImageButton) contentView
					.findViewById(R.id.popup_menu_btn1);
			imageButton2 = (ImageButton) contentView
					.findViewById(R.id.popup_menu_btn2);
			imageButton1.setBackgroundResource(drawables[0]);
			imageButton2.setBackgroundResource(drawables[1]);
			OnTouchListener lis2 = new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN
							|| event.getAction() == MotionEvent.ACTION_MOVE) {
						popupLayout.setBackgroundColor(getResources().getColor(
								R.color.popubackgroud_o));
						view.setBackgroundColor(getResources().getColor(
								R.color.popuMenuview));
					} else {
						popupLayout.setBackgroundColor(getResources().getColor(
								R.color.popubackgroud));
						view.setBackgroundColor(getResources().getColor(
								R.color.black));
					}
					return false;
				}
			};
			popupLayout.setOnTouchListener(lis2);
			imageButton1.setOnTouchListener(lis2);
			imageButton2.setOnTouchListener(lis2);
			imageButton1.setOnClickListener(lis);
			imageButton2.setOnClickListener(lis);
			break;
		case 3:
			contentView = (LinearLayout) LayoutInflater.from(mContext).inflate(
					R.layout.popup_menu_common_style3, null);
			popupLayout = (LinearLayout) contentView
					.findViewById(R.id.popup_linerlayout);
			view = (View) contentView.findViewById(R.id.popup_view);
			imageButton1 = (ImageButton) contentView
					.findViewById(R.id.popup_menu_btn1);
			imageButton2 = (ImageButton) contentView
					.findViewById(R.id.popup_menu_btn2);
			imageButton3 = (ImageButton) contentView
					.findViewById(R.id.popup_menu_btn3);
			imageButton1.setBackgroundResource(drawables[0]);
			imageButton2.setBackgroundResource(drawables[1]);
			imageButton3.setBackgroundResource(drawables[2]);
			OnTouchListener lis3 = new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN
							|| event.getAction() == MotionEvent.ACTION_MOVE) {
						popupLayout.setBackgroundColor(getResources().getColor(
								R.color.popubackgroud_o));
						view.setBackgroundColor(getResources().getColor(
								R.color.popuMenuview));
					} else {
						popupLayout.setBackgroundColor(getResources().getColor(
								R.color.popubackgroud));
						view.setBackgroundColor(getResources().getColor(
								R.color.black));
					}
					return false;
				}
			};
			popupLayout.setOnTouchListener(lis3);
			imageButton1.setOnTouchListener(lis3);
			imageButton2.setOnTouchListener(lis3);
			imageButton3.setOnTouchListener(lis3);
			imageButton1.setOnClickListener(lis);
			imageButton2.setOnClickListener(lis);
			imageButton3.setOnClickListener(lis);
			break;
		case 4:
			contentView = (LinearLayout) LayoutInflater.from(mContext).inflate(
					R.layout.popup_menu_common_style4, null);
			popupLayout = (LinearLayout) contentView
					.findViewById(R.id.popup_linerlayout);
			view = (View) contentView.findViewById(R.id.popup_view);
			imageButton1 = (ImageButton) contentView
					.findViewById(R.id.popup_menu_btn1);
			imageButton2 = (ImageButton) contentView
					.findViewById(R.id.popup_menu_btn2);
			imageButton3 = (ImageButton) contentView
					.findViewById(R.id.popup_menu_btn3);
			imageButton4 = (ImageButton) contentView
					.findViewById(R.id.popup_menu_btn4);
			imageButton1.setBackgroundResource(drawables[0]);
			imageButton2.setBackgroundResource(drawables[1]);
			imageButton3.setBackgroundResource(drawables[2]);
			imageButton4.setBackgroundResource(drawables[3]);
			OnTouchListener lis4 = new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN
							|| event.getAction() == MotionEvent.ACTION_MOVE) {
						popupLayout.setBackgroundColor(getResources().getColor(
								R.color.popubackgroud_o));
						view.setBackgroundColor(getResources().getColor(
								R.color.popuMenuview));
					} else {
						popupLayout.setBackgroundColor(getResources().getColor(
								R.color.popubackgroud));
						view.setBackgroundColor(getResources().getColor(
								R.color.black));
					}
					return false;
				}
			};
			popupLayout.setOnTouchListener(lis4);
			imageButton1.setOnTouchListener(lis4);
			imageButton2.setOnTouchListener(lis4);
			imageButton3.setOnTouchListener(lis4);
			imageButton4.setOnTouchListener(lis4);
			imageButton1.setOnClickListener(lis);
			imageButton2.setOnClickListener(lis);
			imageButton3.setOnClickListener(lis);
			imageButton4.setOnClickListener(lis);
			break;
		}
		int popuWidth = DeviceConfig.getInstance(mContext).w;
		if (popuWidth == 0) {
			DisplayMetrics size = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(size);
			popuWidth = size.widthPixels;
		}
		menuPopup = new PopupWindow(contentView, popuWidth, getResources()
				.getInteger(R.integer.menu_popupwindow_height));
		menuPopup.setOutsideTouchable(true);
		menuPopup.setBackgroundDrawable(new BitmapDrawable(getResources()));
		menuPopup.setAnimationStyle(R.style.PopupAnimation);
	}

	/**
	 * 初始化toast风格，具体可定制
	 */
	private void initToast() {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.toast_layout, null);
		toastTV = (TextView) view.findViewById(R.id.toast_tv);
		toast.setView(view);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0,
				INSTANCE_FROM_BOTTOM);
	}


	

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		addAction(filter);// 调用子类的具体实现来注册广播种类
		registerReceiver(baseReceiver, filter);
		PvBasicStateDataBiz basicStateDataBiz = PvBasicStateDataBiz
				.getInstance();
		basicStateDataBiz.executeSaveBaseData(this,getTopAct(),PvBasicStateData.STARTUP_BY_UNKNOWN);
	}
	
	/**
	 * 获取当前前台activity的包名
	 * 
	 * @return
	 */
	private String getTopAct() {
		String packAgeName = null;
		ActivityManager am = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfos = am.getRunningTasks(1);
		if (taskInfos.size() > 0) {
			ComponentName name = taskInfos.get(0).topActivity;
			packAgeName = name.getPackageName();
		}
		return packAgeName;
	}

	@Override
	protected void onStop() {
//		if (WidgetDataBiz.getInstance(mContext).hasWidget(mContext))
//			sendStickyBroadcast(new Intent(Const.Widget.REFRESH_WIDGET_LIVE));
		
		PvBasicStateDataBiz.BASEFLAG = true;
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (this.menuPopup != null)
			menuPopup.dismiss();
		unregisterReceiver(baseReceiver);
	}

	/**
	 * 回退建事件
	 * 
	 * @param view
	 */
	protected void goback(View view) {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			PvBasicStateDataBiz.BASEFLAG = false;
			if (null != menuPopup && menuPopup.isShowing()) {
				menuPopup.dismiss();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("menu");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (menuPopup != null) {
			if (!menuPopup.isShowing()) {
				/* 最重要的一步：弹出显示 在指定的位置(parent) 最后两个参数 是相对于 x / y 轴的坐标 */
				Log.i("fei", "------------------------------------------");
				menuPopup.showAtLocation(getWindow().getDecorView(),
						Gravity.BOTTOM, 0, 0);
			} else {
				menuPopup.dismiss();
			}
		}
		return false;// 返回为true 则显示系统menu
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		PvBasicStateDataBiz.BASEFLAG = false;
	}
}
