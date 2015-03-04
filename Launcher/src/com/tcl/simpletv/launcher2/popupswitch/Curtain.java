package com.tcl.simpletv.launcher2.popupswitch;

import com.tcl.simpletv.launcher2.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Curtain extends RelativeLayout {
	private WindowManager.LayoutParams wParams = new WindowManager.LayoutParams();
	private ImageView bg;
	private ImageView tip;
	private Bitmap bgIcon;
	private Bitmap tipIcon;
	private Context mContext;
	private int screenWidth;
	
	public Curtain(Context context,int screenWidth) {
		super(context);
		mContext = context;
		this.screenWidth = screenWidth;
		bgIcon = BitmapFactory.decodeResource(getResources(), R.drawable.popup_switcher_curtain_bg_unit);
		tipIcon = BitmapFactory.decodeResource(getResources(), R.drawable.popup_switcher_curtain_tip);
		bg = new ImageView(context);
		bg.setBackgroundResource(R.drawable.popup_switcher_curtain_bg_unit);
		addView(bg,new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		tip = new ImageView(context);
		tip.setBackgroundResource(R.drawable.popup_switcher_curtain_tip);
		tip.setLeft((screenWidth-tipIcon.getWidth())/2);
	    tip.setTop((bgIcon.getHeight()-tipIcon.getHeight())/2);
	    /*
	     * 避免在横竖屏切换时，图片有个旋转显示；
	     */
	    setIconVisble(false);
		addView(tip,new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		initParams();
//		setBackgroundColor(Color.BLUE);
		
	}
	
	public void setViewVisible(boolean visible){
		if(visible){
			 WindowViewManager.addView(mContext, this, wParams);
		}else{
			 WindowViewManager.removeView(mContext, this);
		}
	}
	/*
	 * 设置提示语图片是否可见
	 */
	public void setIconVisble(boolean visible) {
		if (visible) {
			tip.setVisibility(View.VISIBLE);
			bg.setVisibility(View.VISIBLE);
		} else {
			tip.setVisibility(View.INVISIBLE);
			bg.setVisibility(View.INVISIBLE);
		}
	}

	public void updatePosition(float x,float y){
		wParams.x = Math.round(wParams.x+x);
		wParams.y = Math.round(wParams.y+y);
		if(wParams.y>=0){
			wParams.y = 0;
			return;
		}
		WindowViewManager.updateViewLayout(mContext, this, wParams);
	}
	
	public void resetPosition(float x,float y){
		wParams.y = Math.round(y);
		WindowViewManager.updateViewLayout(mContext, this, wParams);
	}
	
	public void initParams(){
		wParams = new WindowManager.LayoutParams();
		wParams.type = WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION/*WindowManager.LayoutParams.TYPE_PHONE*/;// 该类型提供与用户交互，置于所有应用程序上方，但是在状态栏后面
		wParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 不接受任何按键事件
		wParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		wParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
		wParams.width = screenWidth;
		wParams.height = bgIcon.getHeight();
		wParams.x = 0;
		wParams.y = -bgIcon.getHeight();
		wParams.format = PixelFormat.RGBA_8888;
	}
	
	public Curtain(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
//		super.onLayout(changed, l, t, r, b);
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			 int w = child.getMeasuredWidth();
	         int h = child.getMeasuredHeight();
			 int left = child.getLeft();
	         int top = child.getTop();
	         child.layout(left, top, left+w, top+h);
		}
	}

}