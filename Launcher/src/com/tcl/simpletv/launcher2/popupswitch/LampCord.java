package com.tcl.simpletv.launcher2.popupswitch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.tcl.simpletv.launcher2.R;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

public class LampCord extends View implements OnClickListener, OnTouchListener {
	private final String TAG = "LampCord";
	private Bitmap ringIcon;
	private Bitmap lineIcon;
	private Bitmap bgIcon;
	private Bitmap tipIcon;
	private Paint mPaint = new Paint();
	private WindowManager.LayoutParams wParams = new WindowManager.LayoutParams();
	private Context mContext;
	private int screenWidth;
	private Curtain curtain;
	private ArrayList<Integer> mImages;
	private int lampCordHeight,lampCordWidth;
	private int promptBgHeight,promptIconHeight;
	boolean mCycleFlip = false;
	boolean mFillBefore = true;

	/**
	 * Indicates whether the animation transformation should be applied after
	 * the animation ends.
	 */
	boolean mFillAfter = false;

	/**
	 * Indicates whether fillAfter should be taken into account.
	 */
	boolean mFillEnabled = false;

	/**
	 * The time in milliseconds at which the animation must start;
	 */
	long mStartTime = -1;

	/**
	 * The delay in milliseconds after which the animation must start. When the
	 * start offset is > 0, the start time of the animation is startTime +
	 * startOffset.
	 */
	private long mStartOffset;
	private long mDuration;
	private int mRepeatCount = 0;
	private boolean mMore = true;
	private int mRepeated = 0;
	public static final int REVERSE = 2;
	private int mRepeatMode = REVERSE;
	private boolean mOneMoreTime = true;
	private double radian ;
	
	public LampCord(Context context, int screenWidth) {
		super(context);
		mContext = context;
		this.screenWidth = screenWidth;
		this.mPaint.setAntiAlias(true);
		this.mPaint.setDither(true);
		ringIcon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.popup_switcher_handle);
		lineIcon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.popup_switcher_line);
		bgIcon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.popup_switcher_curtain_bg_unit);
		tipIcon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.popup_switcher_curtain_tip);
		lampCordHeight = ringIcon.getHeight()+lineIcon.getHeight();
		lampCordWidth = ringIcon.getWidth();
		promptBgHeight = bgIcon.getHeight();
		promptIconHeight = tipIcon.getHeight();
		initParam();
		setOnTouchListener(this);
		findWallpapers();
	}

	private void findWallpapers() {
		mImages = new ArrayList<Integer>(24);
		final Resources resources = getResources();
		final String packageName = resources
				.getResourcePackageName(R.array.wallpapers);
		addWallpapers(resources, packageName, R.array.wallpapers);
	}

	private void addWallpapers(Resources resources, String packageName, int list) {
		final String[] extras = resources.getStringArray(list);
		for (String extra : extras) {
			int res = resources.getIdentifier(extra, "drawable", packageName);
			if (res != 0) {
				final int thumbRes = resources.getIdentifier(extra + "_small",
						"drawable", packageName);

				if (thumbRes != 0) {
					mImages.add(res);
				}
			}
		}
	}

	private void selectWallpaper(int position) {
		try {
			WallpaperManager wpm = (WallpaperManager) mContext
					.getSystemService(Context.WALLPAPER_SERVICE);
			wpm.setResource(mImages.get(position));
		} catch (IOException e) {
			Log.e(TAG, "Failed to set wallpaper: " + e);
		}
	}

	public void setCurtain(Curtain curtain) {
		this.curtain = curtain;
	}

	public void setViewVisible(boolean visible) {
		if (visible) {
			WindowViewManager.addView(mContext, this, wParams);
		} else {
			WindowViewManager.removeView(mContext, this);
		}
	}

	public void initParam() {
		wParams = new WindowManager.LayoutParams();
		wParams.type = WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION;// 该类型提供与用户交互，置于所有应用程序上方，但是在状态栏后面
		wParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 不接受任何按键事件
		wParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		wParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
		wParams.width = lampCordWidth;
		wParams.height = lampCordHeight;
		wParams.x = screenWidth-(3*lampCordWidth);
		wParams.y = -promptBgHeight;
		wParams.format = PixelFormat.RGBA_8888;
	}

	
	public void updatePosition(float x, float y) {
		Log.d(TAG, "x = " + x + " y = " + y);
		if (Math.round(wParams.y + y) >= 0 || Math.round(wParams.y + y) < -promptBgHeight 
				|| Math.round(wParams.x + x) <= 0
				|| Math.round(wParams.x + x) >= (screenWidth - lampCordWidth)) {
			return;
		}
		wParams.x = Math.round(wParams.x + x);
		wParams.y = Math.round(wParams.y + y);
		WindowViewManager.updateViewLayout(mContext, this, wParams);
		if (curtain != null) {
			curtain.updatePosition(0, y);
		}
	}
	
	float lastX = 0;
	float lastY = 0;
	long endTime;  
	private Calendar now;  
	private boolean isDown = false;
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		int action = arg1.getAction() & MotionEvent.ACTION_MASK;
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			Log.d(TAG, "MotionEvent.ACTION_DOWN ===============");
			curtain.setIconVisble(true);
			lastX = arg1.getRawX();
			lastY = arg1.getRawY();
			isDown = true;
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d(TAG, "MotionEvent.ACTION_MOVE ===============");
			updatePosition(arg1.getRawX()-lastX, arg1.getRawY()-lastY);
			lastX = arg1.getRawX();
			lastY = arg1.getRawY();
			break;
		case MotionEvent.ACTION_UP:
			Log.d(TAG, "MotionEvent.ACTION_UP ===============");
			curtain.setIconVisble(false);
		    isDown = false;
			now = Calendar.getInstance();
			if(wParams.y>=-promptIconHeight){
				if(now.getTimeInMillis()-endTime < 4000){
					showToast(mContext.getResources().getString(R.string.popup_switcher_wallpaper_change_local_toast_ongoing_1));
				}else{
					changeWallpaper();
					endTime = now.getTimeInMillis();
					showToast(mContext.getResources().getString(R.string.popup_switcher_wallpaper_change_local_toast));
				}	
			}
			mDuration = 3000;
			if((arg1.getRawY()-lampCordHeight)>0){
				mFromYDelta = 0;
			}else{
				mFromYDelta = arg1.getRawY()-lampCordHeight;
			}
			if(arg1.getRawY() > (lampCordHeight-promptBgHeight)){
				mToYDelta = -promptBgHeight;
				mStartTime = -1;
				radian = (-Math.PI/2);
				LampCord.this.post(new BounceRunnable());
			}
			break;
		}
		return true;
	}
	
	
	public void changeWallpaper(){
		int position = (int) (Math.random()*mImages.size());
		selectWallpaper(position);
	}
	
	public void showToast(String tip){	
		Toast.makeText(mContext, tip, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onClick(View v) {
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		setMeasuredDimension(measuredWidth, measuredHeight); 
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(lineIcon, 0.0f, 0.0f, mPaint);
		canvas.save();
        canvas.translate(0.0f, lineIcon.getHeight());
        canvas.drawBitmap(ringIcon, 0.0F, 0.0F, mPaint);
        canvas.restore();
	}
	
	private class BounceRunnable implements Runnable {
		public BounceRunnable() {			
		}

		@Override
		public void run() {
			try {
				if(!getTransformation(System.currentTimeMillis())|| isDown){
					return;
				}
				Thread.sleep(20);
				LampCord.this.post(this);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	
	public boolean getTransformation(long currentTime) {
		if (mStartTime == -1) {
			mStartTime = currentTime;
		}

		final long startOffset = 0;
		final long duration = mDuration;
		float normalizedTime;
		if (duration != 0) {
			normalizedTime = ((float) (currentTime - (mStartTime + startOffset)))
					/ (float) duration;
		} else {
			// time is a step-change with a zero duration
			normalizedTime = currentTime < mStartTime ? 0.0f : 1.0f;
		}

		final boolean expired = normalizedTime >= 1.0f;
		mMore = !expired;

		if (!mFillEnabled)
			normalizedTime = Math.max(Math.min(normalizedTime, 1.0f), 0.0f);

		if ((normalizedTime >= 0.0f || mFillBefore)
				&& (normalizedTime <= 1.0f || mFillAfter)) {

			if (mFillEnabled)
				normalizedTime = Math.max(Math.min(normalizedTime, 1.0f), 0.0f);

			if (mCycleFlip) {
				normalizedTime = 1.0f - normalizedTime;
			}

			final float interpolatedTime = getInterpolation(normalizedTime);
			Log.d(TAG, "t = "+normalizedTime);
			Log.d(TAG, "interpolatedTime = "+interpolatedTime);
			applyTransformation(interpolatedTime,normalizedTime);
		}

		if (expired) {
			if (mRepeatCount == mRepeated) {

			} else {
				if (mRepeatCount > 0) {
					mRepeated++;
				}
				if (mRepeatMode == REVERSE) {
					mCycleFlip = !mCycleFlip;
				}
				mStartTime = -1;
				mMore = true;
			}
		}
		if (!mMore && mOneMoreTime) {
			mOneMoreTime = false;
			return true;
		}
		return mMore;
	}

	public void cancel() {
		mStartTime = Long.MIN_VALUE;
		mMore = mOneMoreTime = false;
	}

	private static float bounce(float t) {
		return t * t * 8.0f;
	}
	public float getInterpolation(float t) {
		if(t<0.3535f){
			radian = (2*Math.PI*t/0.3535f)-Math.PI/2;
		    return (float) (Math.sin(radian)+1.0f);
		}
		else if (t < 0.7408f){
			radian = 2*Math.PI*(t-0.3535)/0.3873f-Math.PI/2;
		    return (float) (Math.sin(radian)+1.0f);
		}
		else if (t < 0.9644f){
			radian = 2*Math.PI*(t-0.7408)/0.2236f -Math.PI/2;
			return (float) (Math.sin(radian)+1.0f);
		}
		else
		{			
			return bounce(t *1.1226f - 1.0435f) + 0.95f;
		}
		
	}


	private float mFromYDelta;
	private float mToYDelta;
	/*
	 * 这个算法比较坑爹，原谅我吧^_^!
	 */
	protected void applyTransformation(float interpolatedTime, float t) {
		float dy = mFromYDelta;
		if (mFromYDelta != mToYDelta) {
			if (t < (0.3535f / 4)) {
				dy = mFromYDelta
						+ ((mToYDelta - mFromYDelta) * interpolatedTime);
			} else if (t < (0.2651f)) {
				dy = mFromYDelta
						+ ((mToYDelta - mFromYDelta) * 0.22f)
						+ ((mToYDelta - (mFromYDelta + ((mToYDelta - mFromYDelta) * 0.22f))) * interpolatedTime);
			} else if (t < (0.4404f)) {
				dy = mFromYDelta
						+ ((mToYDelta - mFromYDelta) * 0.44f)
						+ ((mToYDelta - (mFromYDelta + ((mToYDelta - mFromYDelta) * 0.44f))) * interpolatedTime);
			} else if (t < 0.6440f) {
				dy = mFromYDelta
						+ ((mToYDelta - mFromYDelta) * 0.64f)
						+ ((mToYDelta - (mFromYDelta + ((mToYDelta - mFromYDelta) * 0.66f))) * interpolatedTime);
			} else if (t < 0.7967f) {
				dy = mFromYDelta
						+ ((mToYDelta - mFromYDelta) * 0.80f)
						+ ((mToYDelta - (mFromYDelta + ((mToYDelta - mFromYDelta) * 0.80f))) * interpolatedTime);
			} else if (t < 0.9644f) {
				dy = mFromYDelta
						+ ((mToYDelta - mFromYDelta) * 0.97f)
						+ ((mToYDelta - (mFromYDelta + ((mToYDelta - mFromYDelta) * 0.97f))) * interpolatedTime);
			} else {
				dy = mFromYDelta
						+ ((mToYDelta - mFromYDelta) * interpolatedTime);
			}
		}
		wParams.y = (int) dy;
		WindowViewManager.updateViewLayout(mContext, this, wParams);
		if (curtain != null) {
			curtain.resetPosition(0, -bgIcon.getHeight());
		}
	}
	
	
	
}
