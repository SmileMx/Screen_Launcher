package com.inveno.piflow.entity.view.waterwall;

import java.util.List;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.inveno.piflow.download.downloadmanager.download.DownloadService;
import com.inveno.piflow.entity.model.showflow.ShowFlowHardAd;
import com.inveno.piflow.entity.view.FsDialog;
import com.inveno.piflow.tools.commontools.StringTools;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 瀑布流子左右两列子item的
 * 
 * @author mingsong.zhang
 * @date 2013-06-20
 */
public abstract class WaterwallBaseItem extends LinearLayout {

	/** 样式属于字字块 **/
	public static final int MY_STYLE_WATERWALL_TEXT_ITEM = 1;
	/** 样式属于图字块 **/
	public static final int MY_STYLE_WATERWALL_IMG_ITEM = 2;
	/** 自己的样式 **/
	public int mMyStyle = 0;

	/** 我的下标 **/
	public int mIndex;

	/** 控制拉伸滑动 */
	protected Scroller mScroller;
	
	private Context mContext;
	
	private Dialog downloadDialog;

	public WaterwallBaseItem(Context context) {
		super(context);
		mContext = context;
		this.mScroller = new Scroller(context,
				new AccelerateInterpolator());
	}

	public WaterwallBaseItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		this.mScroller = new Scroller(context,
				new AccelerateInterpolator());
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		super.onLayout(changed, l, t, r, b);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
	}

//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//
//		return super.onInterceptTouchEvent(ev);
//	}

	public void startStretchingAnim(int fx, int fy) {
//		Tools.showLogA("传进来的距离：" + fy);

		if(fy<-100)
			fy=-100;
		else if(fy>-40&&fy<40)
			return;
		else if(fy>100)
			fy = 100;
		
		start(fy);

	}

	private void start(int fy) {
		if (!mScroller.isFinished())
			return;
//		Tools.showLogA("开始拉伸,距离：" + fy);

		scrollTo(0, -fy);
		mScroller.startScroll(0, -fy, 0, fy, 300);
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}

	}

	/**
	 * 改变该item高度
	 * 
	 * @param height
	 */
	public abstract void changeHeight(int height);

	public abstract void setActualHeight(int height);

	public int myStyle() {
		return this.mMyStyle;
	}

	public int getmIndex() {
		return mIndex;
	}

	public abstract void changeMargin(int left, int top, int right, int bottom);

	/**
	 * 加载当屏图片
	 * 
	 * @param sub
	 * @param windowHeight
	 */
	public void getTheViewOnTheScreen(int sub, int windowHeight) {

	}

	// /**
	// * 开启动画
	// * @param sub
	// * @param windowHeight
	// */
	// public void startAnim(int sub , int windowHeight){
	// if ((getTop() + sub < windowHeight && getTop() + sub >= 0)
	// || (getTop() + sub <= 0 && getTop() + sub >= -getHeight() * 3 / 4)) {
	// startStretchingAnim(0, fy);
	// Tools.showLogA("有几个需要动画");
	//
	// }
	// }
	
	/**
	 * 加载图片
	 */
	public void loadImg(){
		
	}

	/**
	 * 取消下载该块的图片
	 */
	public void cancelLoadImg() {
	};
	
	/**
	 * 加载当屏之后的图片
	 * @param sub
	 * @param windowHeight
	 */
	public void loadImgBelowTheScreen(int sub, int windowHeight){
		
	}
	
	public void openFirstAD(ShowFlowHardAd showFlowHardAd){

		String apk = showFlowHardAd.getCpapk();
		String cpName = showFlowHardAd.getCpname();
		String packageName = showFlowHardAd.getCppackage();
		String clsName = null;
		String linkUrl = showFlowHardAd.getLinkurl();
		if (showFlowHardAd != null) {
			clsName = packageName.substring(
					packageName.indexOf("/") + 1, packageName.length());
			packageName = packageName.substring(0,
					packageName.indexOf("/"));

		}
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);

		ResolveInfo resolveInfo = checkApp(packageName, intent);

		if (resolveInfo != null) {

			if (StringTools.isNotEmpty(clsName)) {
				Tools.showLog("wf", "广告包名：" + packageName + "  act名:"
						+ clsName);
				Tools.showLog("wf", "广告linkUrl：" + linkUrl);
				try {
					Intent i = new Intent("android.intent.action.VIEW");
					i.setComponent(new ComponentName(packageName, clsName));
					i.putExtra("url", linkUrl);
					mContext.startActivity(i);
				} catch (ActivityNotFoundException e) {
					Tools.showLog("wf", "activityNotFound ：" + e.getMessage());
					intent.setComponent(new ComponentName(
							resolveInfo.activityInfo.packageName,
							resolveInfo.activityInfo.name));
					mContext.startActivity(intent);
				}
				
			} else {
				intent.setComponent(new ComponentName(
						resolveInfo.activityInfo.packageName,
						resolveInfo.activityInfo.name));
				mContext.startActivity(intent);
			}
			return;

		}

		if (downloadDialog == null) {
			downloadDialog = createDownLoadDialog(mContext, cpName, apk);
		}
		downloadDialog.show();
	}
	
	
	public Dialog createDownLoadDialog(final Context context,
			final String name, final String url) {

		Dialog dialog = null;
		FsDialog.Builder customBuilder = new FsDialog.Builder(context);
		customBuilder
				.setTitle("下载提示")
				.setMessage(
						"您还未安装" + name + ",点击下载")
				.setPositiveButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.setNegativeButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 启动服务下载更新
//						DownloadManager downloadManager = ((WFNewsDetailActivity) context)
//								.getDownloadManager();
//						if (downloadManager != null
//								&& StringTools.isNotEmpty(url)) {
//
//							downloadManager.addHandler(url, name, 99999, null);
//						}
						
						Intent i=new Intent(context,DownloadService.class);
						i.putExtra(DownloadService.DOWNLOAD_TASK_KEY,
								DownloadService.TASK_DOWNLOAD_WF_HARD_AD);
						i.putExtra("appName", name);						
						i.putExtra("hardAdUrl", url);
						context.startService(i);
						
						dialog.dismiss();
					}
				});
		dialog = customBuilder.create();
		return dialog;

	}
	
	/**
	 * 打开与参数包名相同的第三方应用
	 * 
	 * @param packageName
	 */
	public ResolveInfo checkApp(String packageName, Intent intent) {

		List<ResolveInfo> list = mContext.getPackageManager()
				.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
		int size = list.size();
		ResolveInfo resolveInfo = null;
		for (int i = 0; i < size; i++) {

			if (list.get(i).activityInfo.packageName
					.equalsIgnoreCase(packageName)) {

				resolveInfo = list.get(i);
				break;
			}
		}

		return resolveInfo;
	}
}
