package com.inveno.piflow.adapter;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * 新闻详细内容样式的ViewPager的适配器
 * 
 * @author hongchang.Liu
 * @date 2012-07-19
 * 
 */
public class NewsCommonAdapter extends PagerAdapter {

	private List<View> views;
	//资讯总条数，即表示ViewPager可以循环多少页
	private int mCount;

	public NewsCommonAdapter(List<View> views,int count) {
		this.views = views;
		this.mCount = count;
	}

	@Override
	public void destroyItem(View collection, int position, Object arg2) {
		if (position >= views.size()) {
			int newPosition = position % views.size();
			position = newPosition;
			// ((ViewPager) collection).removeView(views.get(position));
		}
		if (position < 0) {
			position = -position;
			// ((ViewPager) collection).removeView(views.get(position));
		}
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public int getCount() {
		return mCount;// // 此处+1才能向右连续滚动
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		try {
			((ViewPager) collection).addView(
					views.get(position % views.size()), 0);
		} catch (Exception e) {
		}
		return views.get(position % views.size());
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (object);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

	public int getmCount() {
		return mCount;
	}

	public void setmCount(int mCount) {
		this.mCount = mCount;
	}

	
}
