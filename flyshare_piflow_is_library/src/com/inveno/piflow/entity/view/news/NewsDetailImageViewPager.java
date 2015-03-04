package com.inveno.piflow.entity.view.news;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
/**
 * 自定义控件类   目的禁止翻页滚动
 * @author chenxu 2012 -12-06
 *
 */
public class NewsDetailImageViewPager extends ViewPager {
    /**是否启动翻页功能**/
	private boolean enableFilpPage = true;
	
	public NewsDetailImageViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void enableFilp(){
		enableFilpPage = true;
		
	}
	
	public void disableFilp(){
		enableFilpPage = false;
		
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {

		if(!enableFilpPage)
			return false;
		try{
			return super.onInterceptTouchEvent(arg0);
		}catch(Exception e){
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		try {
			return super.onTouchEvent(arg0);
		} catch (Exception e) {
			return false;
		}
		
		
		
	}
	
	

}
