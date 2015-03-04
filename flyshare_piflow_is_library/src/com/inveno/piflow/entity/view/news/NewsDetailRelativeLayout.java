package com.inveno.piflow.entity.view.news;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 自定义 用于监听软键盘的弹出和收回
 * @author mingsong.zhang
 * @date 2013-02-26
 */
public class NewsDetailRelativeLayout extends RelativeLayout {

	public NewsDetailRelativeLayout(Context context) {
		super(context);
	}
	
	public NewsDetailRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public NewsDetailRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
	
	private Changel mChangel;
	
	public interface Changel{
		void change(int w, int h, int oldw, int oldh);
	}
	
	public void setOnchangel(Changel c){
		mChangel = c;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		
		if(mChangel != null){
			mChangel.change(w, h, oldw, oldh);
		}
		
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
}
