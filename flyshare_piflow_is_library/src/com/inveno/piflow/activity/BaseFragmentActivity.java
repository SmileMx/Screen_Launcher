package com.inveno.piflow.activity;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
 * 用于所有Fragment的基类Activity
 * 
 * @author hongchang.liu
 * 
 */
public abstract class BaseFragmentActivity extends FragmentActivity {

	/**底部菜单持续时间**/
    protected final int PopDurTime=5000;
	
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


	private ImageButton imageButton[];

	private int drawable[];

	/** 语音是否在朗读标示，已改变底部栏的点击按钮 **/
	public boolean isSpeaking;

	public void setSpeaking(boolean isSpeaking) {
		this.isSpeaking = isSpeaking;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		myApp = (FlyShareApplication) getApplication();
		mContext = this;
		baseReceiver = new BaseReceiver();
		filter = new IntentFilter();
		addAction(filter);// 调用子类的具体实现来注册广播种类
		registerReceiver(baseReceiver, filter);
		toast = new Toast(getApplicationContext());
		initToast();

		super.onCreate(savedInstanceState);
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
		if (listener != null)
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

	/** 更换底部菜单的图标 **/
	public void resetMenuBtnImg(final int... drawables) {

		this.drawable = drawables;
		if (imageButton != null) {
			for (int i = 0; i < drawables.length; i++) {
				if (i < imageButton.length) {
					imageButton[i].setBackgroundResource(drawables[i]);
				}
			}
		}

	}

	/**
	 * 初始化menu键弹出菜单
	 */
	private LinearLayout initMenuPopup2(final MenuPopupListener listener,
			final int... drawables) {
		LinearLayout contentView = null;
		this.drawable = drawables;
		imageButton = new ImageButton[drawables.length];
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
					tag = drawable[0];
			}else if(id== R.id.popup_menu_btn2){
					listener.clickBtn2();
					tag = drawable[1];
			}else if(id== R.id.popup_menu_btn3){
					tag = drawable[2];
					listener.clickBtn3();
			}else if(id== R.id.popup_menu_btn4){
					tag = drawable[3];
					listener.clickBtn4();
					
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
			imageButton[0] = (ImageButton) contentView
					.findViewById(R.id.popup_menu_btn1);
			imageButton[0].setBackgroundResource(drawables[0]);
			imageButton[0].setOnClickListener(lis);
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
			imageButton[0].setOnTouchListener(lis1);
			imageButton[0].setOnClickListener(lis);
			break;
		case 2:
			contentView = (LinearLayout) LayoutInflater.from(mContext).inflate(
					R.layout.popup_menu_common_style2, null);
			popupLayout = (LinearLayout) contentView
					.findViewById(R.id.popup_linerlayout);
			view = (View) contentView.findViewById(R.id.popup_view);
			imageButton[0] = (ImageButton) contentView
					.findViewById(R.id.popup_menu_btn1);
			imageButton[1] = (ImageButton) contentView
					.findViewById(R.id.popup_menu_btn2);
			imageButton[0].setBackgroundResource(drawables[0]);
			imageButton[1].setBackgroundResource(drawables[1]);
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
			imageButton[0].setOnTouchListener(lis2);
			imageButton[1].setOnTouchListener(lis2);
			imageButton[0].setOnClickListener(lis);
			imageButton[1].setOnClickListener(lis);
			break;
		case 3:
			contentView = (LinearLayout) LayoutInflater.from(mContext).inflate(
					R.layout.popup_menu_common_style3, null);
			popupLayout = (LinearLayout) contentView
					.findViewById(R.id.popup_linerlayout);
			view = (View) contentView.findViewById(R.id.popup_view);
			imageButton[0] = (ImageButton) contentView
					.findViewById(R.id.popup_menu_btn1);
			imageButton[1] = (ImageButton) contentView
					.findViewById(R.id.popup_menu_btn2);
			imageButton[2] = (ImageButton) contentView
					.findViewById(R.id.popup_menu_btn3);
			imageButton[0].setBackgroundResource(drawables[0]);
			imageButton[1].setBackgroundResource(drawables[1]);
			imageButton[2].setBackgroundResource(drawables[2]);
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
			imageButton[0].setOnTouchListener(lis3);
			imageButton[1].setOnTouchListener(lis3);
			imageButton[2].setOnTouchListener(lis3);
			imageButton[0].setOnClickListener(lis);
			imageButton[1].setOnClickListener(lis);
			imageButton[2].setOnClickListener(lis);
			break;
		case 4:
			contentView = (LinearLayout) LayoutInflater.from(mContext).inflate(
					R.layout.popup_menu_common_style4, null);
			popupLayout = (LinearLayout) contentView
					.findViewById(R.id.popup_linerlayout);
			view = (View) contentView.findViewById(R.id.popup_view);
			imageButton[0] = (ImageButton) contentView
					.findViewById(R.id.popup_menu_btn1);
			imageButton[1] = (ImageButton) contentView
					.findViewById(R.id.popup_menu_btn2);
			imageButton[2] = (ImageButton) contentView
					.findViewById(R.id.popup_menu_btn3);
			imageButton[3] = (ImageButton) contentView
					.findViewById(R.id.popup_menu_btn4);
			imageButton[0].setBackgroundResource(drawables[0]);
			imageButton[1].setBackgroundResource(drawables[1]);
			imageButton[2].setBackgroundResource(drawables[2]);
			imageButton[3].setBackgroundResource(drawables[3]);
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
			imageButton[0].setOnTouchListener(lis4);
			imageButton[1].setOnTouchListener(lis4);
			imageButton[2].setOnTouchListener(lis4);
			imageButton[3].setOnTouchListener(lis4);
			imageButton[0].setOnClickListener(lis);
			imageButton[1].setOnClickListener(lis);
			imageButton[2].setOnClickListener(lis);
			imageButton[3].setOnClickListener(lis);
			break;
		}
		;

		initMenuPoup(contentView);
		return contentView;
	}

	/**
	 * 初始化底部，设置不同的View
	 * 
	 * @param contentView
	 */
	private void initMenuPoup(LinearLayout contentView) {
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
		basicStateDataBiz.executeSaveBaseData(this, getTopAct(),
				PvBasicStateData.STARTUP_BY_UNKNOWN);
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

		super.onStop();
		PvBasicStateDataBiz.BASEFLAG = true;
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
