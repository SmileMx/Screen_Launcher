package com.inveno.piflow.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.inveno.piflow.R;
import com.inveno.piflow.entity.view.waterwall.WaterwallScrollView;
import com.inveno.piflow.services.HeartService;
import com.inveno.piflow.tools.bitmap.FlyshareBitmapManager;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 瀑布流界面
 * 
 * @author mingsong.zhang
 * @date 2013-07-31
 * 
 */
public class WaterwallActivity extends BaseActivity {

	private WaterwallScrollView mWaterwallScrollView;
	private FlyshareBitmapManager mFlyshareBitmapManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wpd_waterwall_main);

		mWaterwallScrollView = (WaterwallScrollView) findViewById(R.id.waterwall_scrollview);
		this.mFlyshareBitmapManager = FlyshareBitmapManager.create(this);
		mWaterwallScrollView.setmFlyshareBitmapManager(mFlyshareBitmapManager);
		mWaterwallScrollView.setmApp(myApp);
		mWaterwallScrollView.load();

		
		
		startService(new Intent(mContext, HeartService.class));
	}

	@Override
	protected void onResume() {
		if (mWaterwallScrollView != null) {
			mWaterwallScrollView.setWeatherUi();
		}
		super.onResume();
	}

	@Override
	protected void addAction(IntentFilter filter) {

	}

	@Override
	protected void changeUI(Context context, Intent intent) {

	}

	@Override
	protected void initViews() {

	}

	@Override
	protected void initData() {

	}

	@Override
	protected void initMembers() {

	}

	@Override
	protected void init() {

	}

	@Override
	protected void onPause() {
		new Thread() {
			public void run() {
				if (mWaterwallScrollView != null)
					mWaterwallScrollView.saveLastShowFlowNews();
			};
		}.start();
		super.onPause();
	}

	@Override
	protected void onDestroy() {

		if (mFlyshareBitmapManager != null) {
			mFlyshareBitmapManager.flushCache();
		}

		whetherCleanCache();
		
		mWaterwallScrollView.removeAllWaterwallViews();
		Tools.showLogA("推出");
		System.gc();

		System.exit(0);
		
		super.onDestroy();
	}

	/**
	 * 如果缓存大小大于30m，则清除文件
	 */
	private void whetherCleanCache() {
//		String a = Tools.getInformain(Tools.FILE_CACHE_SIZE, "0", mContext);
//		Tools.showLog("lhc", "是否删除文件，缓存大小：" + a);
//		if (Float.parseFloat(a) > 30) {
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					FileControler.getInstance().deleteAboutNewsFile();
//				}
//			}).start();
//
//		}
	}
}
