package com.inveno.piflow.entity.view.news;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Scroller;

import com.inveno.piflow.tools.commontools.Tools;

/**
 * 重写ViewPager实现左滑反弹效果
 * @author jianghaibo
 *
 */
public class NewsViewPager extends ViewPager {

	private static final String TAG = "NewsViewPager";
	private float mLastMotionX;// 最后点击的点
	private Scroller mScroller;
	private int mRrightMove = 0;// 往右多移的距离
	private int mLeftMove = 0;// 往左多移的距离
	private int mCountPage = 0;
	private int mDeltaX = 0;
	private boolean mIsLeft = true;
	private boolean mIsRight = false;
	private boolean mIsBounce = false;
	private static final int DURATION_TIME = 100;
	private static final int MAX_MOVE = 200;
	
	  /**是否启动翻页功能**/
	private boolean enableFilpPage = true;
	
	public void enableFilp(){
		enableFilpPage = true;
		
	}
	
	public void disableFilp(){
		enableFilpPage = false;
		
	}

	public NewsViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScroller = new Scroller(context);
	}

	public NewsViewPager(Context context) {
		super(context);
		mScroller = new Scroller(context);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		float x = ev.getX();
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				mScroller.forceFinished(true);
			}
			mLastMotionX = x;
			mDeltaX = 0;
			break;
		case MotionEvent.ACTION_MOVE:
			// 随手指 拖动的代码
			mDeltaX = (int) (mLastMotionX - x);
			mLastMotionX = x;
			Tools.showLog(TAG, " deltaX " + mDeltaX);
			if ( mDeltaX < 0 &&  mIsLeft) {
				if (mRrightMove <= MAX_MOVE) {
					mRrightMove = mRrightMove - mDeltaX / 2;// 记录下多往右滑动的值
					scrollBy(mDeltaX / 2, 0);
					mIsBounce = true;
					//回弹监听接口
					if (onReboundListener!=null) 
						onReboundListener.onRebound();
					
				} 
			} else if( mDeltaX > 0 && mIsRight ) {
				if (mLeftMove <= MAX_MOVE) {
					mLeftMove = mLeftMove +  mDeltaX / 2;// 记录下多往右滑动的值
					scrollBy(mDeltaX / 2, 0);
				} 
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			Tools.showLog(TAG, " mDeltaX " + mDeltaX + " mRrightMove " + mRrightMove
					+ " mIsLeft " + mIsLeft);
			if(mIsRight) {
				snapToScreen(mCountPage);
				invalidate();
			}else if (mIsLeft) {
				// 多滚了直接弹回第一页
				snapToScreen(0);
				invalidate();
			}else {
				try {
					Tools.showLog(TAG, " MotionEvent.ACTION_CANCEL retrun super.onTouchEvent  ");
					return super.onTouchEvent(ev);
				} catch (Exception e) {
					snapToScreen(0);
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			Tools.showLog(TAG, " mDeltaX " + mDeltaX + " mRrightMove " + mRrightMove
					+ " mIsLeft " + mIsLeft);
			if(mIsRight && mLeftMove > 0) {
				snapToScreen(getCountPage());
				invalidate();
			}else if (mDeltaX <= 0 && mRrightMove >= 0 && mIsLeft) {
				// 多滚了直接弹回第一页
				snapToScreen(0);
				invalidate();
			}else {
				try {
					Tools.showLog(TAG, " MotionEvent.ACTION_UP retrun super.onTouchEvent ");
					return super.onTouchEvent(ev);
				} catch (Exception e) {
					snapToScreen(0);
					invalidate();
				}
			}
			break;
		}
		if(mIsRight && mLeftMove > 0) {
			return true;
		}
		try {
			if (!mIsLeft || mDeltaX > 0) {
				Tools.showLog(TAG, " retrun super.onTouchEvent ");
				return super.onTouchEvent(ev);
			}
		} catch (Exception e) {
			// 有异常也直接回第一页
			snapToScreen(0);
		}
		return true;
	}

	public boolean onInterceptTouchEvent(MotionEvent ev) {

		if(!enableFilpPage)
			return false;
		
		final int action = ev.getAction();

		final float x = ev.getX();
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mDeltaX = 0;
			mRrightMove = 0;
			mLeftMove = 0;
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		try{
			return super.onInterceptTouchEvent(ev);
		}catch(Exception e){
			return false;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
	}

	private void snapToScreen(int whichScreen) {
		final int newX = whichScreen * getWidth();
		final int delta = newX - getScrollX();
		mScroller.startScroll(getScrollX(), 0, delta, 0, DURATION_TIME);
		mIsBounce = false;
		invalidate();
	}

	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	public boolean isBounce() {
		return mIsBounce;
	}

	public void setBounce(boolean mIsBounce) {
		this.mIsBounce = mIsBounce;
	}
	
	public boolean isIsLeft() {
		return mIsLeft;
	}

	public void setIsLeft(boolean mIsLeft) {
		this.mIsLeft = mIsLeft;
	}
	
	public boolean isRight() {
		return mIsRight;
	}

	public void setRight(boolean mIsRight) {
		this.mIsRight = mIsRight;
	}

	public int getCountPage() {
		return mCountPage;
	}

	public void setCountPage(int mCountPage) {
		this.mCountPage = mCountPage;
	}

	public interface OnReboundListener{
		void onRebound();
	}
	private OnReboundListener onReboundListener;

	public OnReboundListener getOnReboundListener() {
		return onReboundListener;
	}

	public void setOnReboundListener(OnReboundListener onReboundListener) {
		this.onReboundListener = onReboundListener;
	}
	
}
