package com.inveno.piflow.entity.view.mainfrag;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inveno.piflow.R;
import com.inveno.piflow.activity.FlyShareApplication;
import com.inveno.piflow.entity.view.BaseFragment;
import com.inveno.piflow.entity.view.waterwall.WaterwallScrollView;
import com.inveno.piflow.tools.bitmap.FlyshareBitmapManager;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 主界面右边的瀑布流碎片，封装瀑布流逻辑 几点重要问题
 * 1.考虑主界面左右两边是viewPager实现，当未滑到瀑布流页面时，不去加载瀑布流的图片，可以加载瀑布流框架
 * 2.瀑布流是可以无限往下拉数据的，所以实现控件复用以及资源释放时最重要的一环
 * 
 * @author mingsong.zhang
 * 
 * @date 2013-6-4
 */
@SuppressLint("ValidFragment")
public class ShowFlowFragment extends BaseFragment {

	private WaterwallScrollView mWaterwallScrollView;

	private FlyshareBitmapManager mFlyshareBitmapManager;

	private FlyShareApplication mApp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Tools.showLogB("ShowFlowFragment on activity create!");

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Tools.showLogB("ShowFlowFragment on create view!");
		mWaterwallScrollView = (WaterwallScrollView) inflater.inflate(
				R.layout.wpd_waterwall_main, container, false);
		this.mFlyshareBitmapManager = FlyshareBitmapManager
				.create(getActivity());
		mWaterwallScrollView.setmFlyshareBitmapManager(mFlyshareBitmapManager);
		mWaterwallScrollView.setmApp(mApp);
		return mWaterwallScrollView;
	}

	
	public ShowFlowFragment() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ShowFlowFragment(FlyShareApplication app) {
		Tools.showLogB("生成了一个新的ShowFlowFragment");
		this.mApp = app;
	}

	/**
	 * 构造器
	 * 
	 * @return
	 */
	public static ShowFlowFragment newInstance(FlyShareApplication app) {
		ShowFlowFragment f = new ShowFlowFragment(app);
		return f;
	}

	public void load() {
		if (mWaterwallScrollView != null) {
			mWaterwallScrollView.load();
		}
	}

	@Override
	public void onPause() {
		if (mFlyshareBitmapManager != null) {
			mFlyshareBitmapManager.flushCache();
		}
		new Thread() {
			public void run() {
				mWaterwallScrollView.saveLastShowFlowNews();
			};
		}.start();
		super.onPause();
	}

	public void release() {
		mWaterwallScrollView.removeAllWaterwallViews();
//		mWaterwallScrollView.hidenWaterFlow();
//		mWaterwallScrollView.removeAllWaterwallViewsStayLast();
	}

	@Override
	public void onDestroy() {
		Tools.showLogA("showflow ondestroy");
		release();
		
		super.onDestroy();
	}

}
