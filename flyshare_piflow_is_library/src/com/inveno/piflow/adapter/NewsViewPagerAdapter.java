package com.inveno.piflow.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * 新闻频道样式的ViewPager的适配器
 * 
 * @author hongchang.Liu
 * @date 2012-07-19
 * 
 */
public class NewsViewPagerAdapter extends PagerAdapter {

	/** 新闻资讯样式ArrayList */
	private ArrayList<View> mViewsArrayList;
	//资讯总条数，即表示ViewPager可以循环多少页
	private int mCount;

	public NewsViewPagerAdapter(ArrayList<View> mViewsArrayList,int count) {
		this.mViewsArrayList = mViewsArrayList;
		
		this.mCount=count;

	}


	// 设置适配器的集合
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return  mCount;
	}

	// 判断对象是否为View
	// 这里需要返回 return arg0 == arg1; 否则将不会显示任何东西
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	// 这里是删除Item优化处理。
	// container代表viewPager,也就是第一个参数ViewGroup container
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		View destroyView = (View) object;
		container.removeView(destroyView);
		destroyView = null;

	}

	// 这里是装载区域
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		

		
		container.addView(mViewsArrayList.get(position));

		return mViewsArrayList.get(position);
	}

	// 返回POSITION_NONE,这样notifyDataChange才有效.
	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return POSITION_NONE;
	}
	
//	@Override
//	public Object instantiateItem(ViewGroup container, int position) {
//		// TODO Auto-generated method stub
//		
//		
//		
//		
//		return super.instantiateItem(container, position);
//	}


	public int getmCount() {
		return mCount;
	}

	public void setmCount(int mCount) {
		this.mCount = mCount;
		notifyDataSetChanged();
	}

}
