package com.inveno.piflow.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.inveno.piflow.tools.bitmap.FlyshareBitmapManager;

/**
 * 展示大图的ViewPager的适配器
 * 
 * @author mingsong.zhang
 * @date 2012-09-07
 * 
 */
public class NewsDetailImgsPagerAdapter extends PagerAdapter {

	/** 布局集合 **/
	private ArrayList<? extends ImageView> mViewsList;

	private ArrayList<String> imgUlrs;

	/** 是美图类型还是news类型 */
	private int type;

	private int meiTuCount;
	
	int cleanCount;

	/** 位图管理 */
	private FlyshareBitmapManager flyshareBitmapManager;

	public NewsDetailImgsPagerAdapter(ArrayList<? extends ImageView> bitmaps,
			ArrayList<String> ulrs,
			FlyshareBitmapManager flyshareBitmapManager, int count) {
		if (bitmaps == null) {
			this.mViewsList = new ArrayList<ImageView>();
		} else {
			this.mViewsList = bitmaps;
			this.imgUlrs = ulrs;
			this.meiTuCount = count;
		}
		this.flyshareBitmapManager = flyshareBitmapManager;
	}

	public NewsDetailImgsPagerAdapter(ArrayList<? extends ImageView> bitmaps,
			FlyshareBitmapManager bitmapManager, int type, int meitus) {

		this.type = type;
		if (bitmaps == null) {
			this.mViewsList = new ArrayList<ImageView>();

		} else {
			this.mViewsList = bitmaps;
			this.meiTuCount = meitus;

		}

		this.flyshareBitmapManager = bitmapManager;

	}

	public void setMeiTuCount(int meiTuCount) {
		this.meiTuCount = meiTuCount;
	}

	@Override
	public int getCount() {

		// if (type == 0) {
		// return mViewsList.size();
		// } else {
		// return meiTuCount;
		// }
		return meiTuCount;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// container.removeView(mViewsList.get(position));
		// container.removeViewAt(position);
//		ImageView imageView=mViewsList.get(position);
//		imageView.setImageDrawable(null);
//		container.removeView(imageView);
		
//		Tools.showLog("meitu", "destroyItem item:"+position % mViewsList.size());
//		ImageView imageView = mViewsList.get(position % mViewsList.size());
//		imageView.setImageResource(R.drawable.little_icon);
	}

	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return POSITION_NONE;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
//		Tools.showLog("meitu", "instantiateItem item:"+position % mViewsList.size());
		ImageView imageView = mViewsList.get(position % mViewsList.size());
//		ImageView imageView = mViewsList.get(position);
		if (type == 0) {
			this.flyshareBitmapManager
					.display(imageView, imgUlrs.get(position));
		} else {
//			this.flyshareBitmapManager.display(imageView, MeituShowBiz
//					.getmMeiTuInfoList().get(position).getmImg());
		}
		try {
			container.addView(imageView, 0);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return imageView;
	}

}
