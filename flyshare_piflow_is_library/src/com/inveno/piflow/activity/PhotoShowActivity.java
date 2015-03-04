package com.inveno.piflow.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.inveno.piflow.R;
import com.inveno.piflow.biz.DayNightModeBiz;
import com.inveno.piflow.biz.MeituShowBiz;
import com.inveno.piflow.biz.upload.PvOperationBiz;
import com.inveno.piflow.entity.model.MeiTuInfo;
import com.inveno.piflow.entity.view.PhotoShowFragment;
import com.inveno.piflow.entity.view.news.NewsViewPager.OnReboundListener;
import com.inveno.piflow.tools.android.ToastTools;
import com.inveno.piflow.tools.bitmap.BitmapDisplayImpl;
import com.inveno.piflow.tools.bitmap.FlyshareBitmapManager;
import com.inveno.piflow.tools.commontools.ConnectUtil;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 看大图FragmentActivity,有缩放功能.
 * 
 * @author hongchang.liu
 * 
 */
public class PhotoShowActivity extends BaseFragmentActivity implements
		OnReboundListener {

	private FlyshareBitmapManager bitmapManager;
	private ViewPager viewPager;
	// 自定义控件END

	public static final int FROM_NEWS = 0;
	public static final int FROM_MEITU = 1;
	public static final int FROM_APPSTORE = 2;
	public static final int FROM_WATERFALL = 3;
	private int from;

	/** 传递过来的集合的size */
	private int size;

	// /** 传递过来的频道大标题 */
	// private int infoId;

	/** 传递过来的图片url集合 */
	private ArrayList<String> imgUrls;

	private String currentPhoto;

	private MeituShowBiz meituShowBiz;

	private int currentIndex;

	private int resultCode;

	private Context mContext;

	private PhotoShowPagerAdapter pagerAdapter;

	private boolean netError;
	private boolean noData;

	private DayNightModeBiz dayNightModeBiz;

	private ArrayList<OnPageChange> pageChanges;

	private int typeId;
	
	private ArrayList<String> widths;
	private ArrayList<String> heights;

	public FlyshareBitmapManager getBitmapManager() {
		return bitmapManager;
	}

	public ViewPager getmViewPager() {
		return viewPager;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:

				Tools.showLog("meitu", "收到消息改变适配器的 count:" + size);
				size = MeituShowBiz.getmMeiTuInfoList().size();
				meituShowBiz.setNotLoading(true);
//				viewPager.setRight(false);
				pagerAdapter.setmSize(size);

				break;
			case 1:
				meituShowBiz.setNotLoading(true);
				netError = true;
				break;
			case 2:
				noData = true;

				break;

			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTheme(R.style.Transparent);
		
		super.onCreate(savedInstanceState);

		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		final View v = inflater.inflate(R.layout.news_detail_imgs, null);

		dayNightModeBiz = DayNightModeBiz.getInstance(PhotoShowActivity.this);
//		if (dayNightModeBiz.getMode() == 1)
//			v.setBackgroundColor(dayNightModeBiz.getWhite());
//		else
//			v.setBackgroundColor(dayNightModeBiz.getBlackbg());
		setContentView(v);

		mContext = PhotoShowActivity.this;
		// Set up ViewPager and backing adapter

		viewPager = (ViewPager) this
				.findViewById(R.id.news_detail_imgs_ViewPager);
		pageChanges = new ArrayList<OnPageChange>();
		init();
		// Set the current item based on the extra passed in to this activity
		// final int extraCurrentItem = getIntent().getIntExtra(EXTRA_IMAGE,
		// -1);
		// if (extraCurrentItem != -1) {
		// mViewPager.setCurrentItem(extraCurrentItem);
		// }

	}

	@Override
	protected void init() {

		Intent intent = this.getIntent();

		from = intent.getIntExtra("FromWhere", -1);
		Tools.showLog("meitu", "初始化FromWhere:" + from);
		this.bitmapManager = FlyshareBitmapManager.create(mContext);
		if (from == FROM_NEWS || from == FROM_WATERFALL) {
			Tools.showLog("meitu", "初始化来自new的BitmapManage");
			currentPhoto = intent.getExtras().getString("currentPhoto");
			imgUrls = intent.getStringArrayListExtra("NewsImgUrlList");
			widths = intent.getStringArrayListExtra("widths");
			heights = intent.getStringArrayListExtra("heights");
			// infoId = intent.getIntExtra("infoId", -1);
			size = imgUrls.size();
			Tools.showLog("meitu", "news url size:" + size);
			pagerAdapter = new PhotoShowPagerAdapter(
					getSupportFragmentManager(), size);
			currentIndex = imgUrls.indexOf(currentPhoto);
			Tools.showLog("meitu", "news url currentIndex:" + currentIndex);
		} else if (from == FROM_MEITU) {
			 Tools.showLog("meitu", "初始化来自Meitu的BitmapManage");
			 meituShowBiz = MeituShowBiz.getInstance(mContext);
			 size = MeituShowBiz.getmMeiTuInfoList().size();
			 pagerAdapter = new PhotoShowPagerAdapter(
			 getSupportFragmentManager(), size);
			 currentIndex = intent.getIntExtra("currentIndex", 0);
			 typeId = intent.getIntExtra("typeCode", 0);
			 Tools.showLog("meitu", "初始化来自Meitu typeId:" + typeId);

		} else if (from == FROM_APPSTORE) {
			Tools.showLog("meitu", "初始化来自APP Store的BitmapManage");
			imgUrls = intent.getStringArrayListExtra("imageUrls");
			currentIndex = intent.getIntExtra("index", 0);
			size = imgUrls.size();
			pagerAdapter = new PhotoShowPagerAdapter(
					getSupportFragmentManager(), size);
		}

		else if (from == -1) {
			Tools.showLog("meitu", "不是来自new、meitu、App store,finish!");
			this.finish();
		}
		viewPager.setOnPageChangeListener(pageChangeListener);
//		viewPager.setOnReboundListener(this);
		viewPager.setAdapter(pagerAdapter);
		if (currentIndex != 0)
			viewPager.setCurrentItem(currentIndex);

		resultCode = currentIndex;
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		int index;

		@Override
		public void onPageSelected(int position) {
			// Tools.showLog("meitu",
			// " OnPageChangeListener position"+position);

//			if (position == size - 1) {
//				viewPager.setRight(true);
//				viewPager.setCountPage(size - 1);
//			} else {
//				viewPager.setRight(false);
//			}
//
//			if (position == 0)
//				viewPager.setIsLeft(true);
//			else
//				viewPager.setIsLeft(false);

			if (from == FROM_MEITU) {
				index = position;
				// bitmapManager.clearMemeoryCache();
				resultCode = position;
				if (position == size - 2) {
					Tools.showLog("meitu", "滑动倒数第二页,加载新数据 position:" + position);
					meituShowBiz.downLoadPre(mHandler);
				}
			}

			int changeSize = pageChanges.size();
			Tools.showLog("meitu", "滑动监听size：" + changeSize);
			for (int i = 0; i < changeSize; i++) {
				pageChanges.get(i).onPageChangeListener(position);
			}

		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrollStateChanged(int state) {
			// if (from == FROM_MEITU) {
			// if (index == size - 1 && state == 0) {
			// if (netError) {
			//
			// ToastTools.showToast(mContext, "网络不给力,请稍候再试!");
			// netError = false;
			//
			// } else if (noData) {
			//
			// ToastTools.showToast(mContext, "已经是最后一页了!");
			// } else {
			//
			// ToastTools.showToast(mContext, "数据读取中,请稍候!");
			// }
			//
			// }
			// else if (index == 0 && state == 0) {
			//
			// ToastTools.showToast(mContext, "第一页!");
			// }
			// }

		}
	};

	@Override
	public void onResume() {
		// **记录阅读时间点，可能home返回继续阅读，重新记录pv startTime**/
		// NewsCommonActivity.mNeedFinish = 1;

		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		uploadPv();
		// NewsCommonActivity.mNeedFinish = 0;
		viewPager.removeAllViews();
		if (!(from == FROM_WATERFALL || from == FROM_NEWS))
			bitmapManager.flushCache();

		if (bitmapManager != null)
			bitmapManager.configIBitmapDisplay(new BitmapDisplayImpl());

		super.onDestroy();

	}

	/**
	 * 上传PV点击资讯
	 */
	private void uploadPv() {

		if (from == FROM_MEITU) {
			final PvOperationBiz mPvOperationBiz = PvOperationBiz.getInstance();
			// 退出资讯详细,上传pv

			if (ConnectUtil.isNetWorkUsed(mContext)) {
				new Thread(new Runnable() {

					@Override
					public void run() {

						String backCode = mPvOperationBiz
								.postPvOperation(mContext);

						Tools.showLog("test", "上传PV返回码:" + backCode);

					}
				}).start();
			}

		}

	}

	/**
	 * The main adapter that backs the ViewPager. A subclass of
	 * FragmentStatePagerAdapter as there could be a large number of items in
	 * the ViewPager and we don't want to retain them all in memory at once but
	 * create/destroy them on the fly.
	 */
	private class PhotoShowPagerAdapter extends FragmentStatePagerAdapter {
		private int mSize;

		public PhotoShowPagerAdapter(FragmentManager fm, int size) {
			super(fm);
			mSize = size;
		}

		@Override
		public int getCount() {
			return mSize;
		}

		@Override
		public Fragment getItem(int position) {
			int width = Integer.parseInt(widths.get(position));
			int height = Integer.parseInt(heights.get(position));
			
			if (from == FROM_MEITU) {

				MeiTuInfo meiTuInfo = MeituShowBiz.getmMeiTuInfoList().get(
						position);
				
				return PhotoShowFragment.newInstance(meiTuInfo.getmImg(),
						dayNightModeBiz.getMode(), from, meiTuInfo.getmId(),
						position, typeId);
			}
			return PhotoShowFragment.newInstance(imgUrls.get(position),
					dayNightModeBiz.getMode(), from,width,height);
		}

		public void setmSize(int mSize) {
			this.mSize = mSize;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Tools.showLog("meitu", "准备返回页数时,当前item为:" + resultCode);
			setResult(resultCode / 7);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// Tools.showLog("widget", " home了"+getIntent().getIntExtra(
		// NewsCommonActivity.GET_FROMWHERE_KEY, -1));
		// if (NewsCommonActivity.FROM_WIDGET_NEWS == getIntent().getIntExtra(
		// NewsCommonActivity.GET_FROMWHERE_KEY, -1)) {
		// Tools.showLog("widget", " home了");
		// NewsCommonActivity.mNeedFinish = 2;
		// sendBroadcast(new Intent(NewsCommonActivity.NEED_FINISH));
		// finish();
		// }
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void addAction(IntentFilter filter) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void changeUI(Context context, Intent intent) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initViews() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initMembers() {
		// TODO Auto-generated method stub

	}

	/**
	 * 页面改变监听接口
	 * 
	 * @author hongchang.liu
	 * 
	 */
	public interface OnPageChange {
		void onPageChangeListener(int page);
	}

	public void setOnPageChange(OnPageChange pageChange) {

		if (pageChange != null && pageChanges != null) {
			pageChanges.add(pageChange);
		}

	}

	public void removeOnPageChange(OnPageChange pageChange) {

		if (pageChange != null && pageChanges != null) {
			pageChanges.remove(pageChange);
		}

	}

	@Override
	public void onRebound() {
		// TODO Auto-generated method stub
		Tools.showLog("pf",
				"回弹动画时,当前页数" + size + "   当前页：" + viewPager.getCurrentItem());
		if (viewPager.getCurrentItem() == size - 1 && from == FROM_MEITU) {
			if (netError) {

				ToastTools.showToast(mContext, "网络不给力,请稍候再试!");
				netError = false;

			} else if (noData) {

				ToastTools.showToast(mContext, "已经是最后一页了!");
			} else {

				ToastTools.showToast(mContext, "数据读取中,请稍候!");
			}
		}
	}

}
