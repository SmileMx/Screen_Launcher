package com.inveno.piflow.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * 展示大图的ViewPager的适配器
 * 
 * @author mingsong.zhang
 * @date 2012-09-07
 * 
 */
public class ShowBigImgPagerAdapter extends PagerAdapter {

	/** 布局集合 **/
	private ArrayList<View> mViewsList;

	/** 资讯总条数，即表示ViewPager可以循环多少页 **/
	private int mCount;

	public ShowBigImgPagerAdapter(ArrayList<View> mViewsList,
			int count) {
		this.mViewsList = mViewsList;
		this.mCount = count;
	}

	public void addView(View mView) {
		mViewsList.add(mView);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {

	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		try {
			((ViewPager) container).addView(
					mViewsList.get(position % mViewsList.size()), 0);
		} catch (Exception e) {
		}
		return mViewsList.get(position % mViewsList.size());
	}

	public int getmCount() {
		return mCount;
	}

	public void setmCount(int mCount) {
		this.mCount = mCount;
	}

}
