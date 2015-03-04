package com.inveno.piflow.entity.view.waterwall;

import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inveno.piflow.R;
import com.inveno.piflow.activity.FlyShareApplication;
import com.inveno.piflow.biz.upload.WFPvUploadBiz;
import com.inveno.piflow.download.downloadmanager.download.DownloadManager;
import com.inveno.piflow.download.downloadmanager.download.DownloadService;
import com.inveno.piflow.entity.model.showflow.ShowFlowHardAd;
import com.inveno.piflow.entity.model.showflow.ShowFlowNewinfo;
import com.inveno.piflow.entity.model.showflow.ShowFlowNews;
import com.inveno.piflow.entity.model.showflow.ShowFlowP;
import com.inveno.piflow.entity.model.upload.WFPvOperation;
import com.inveno.piflow.entity.view.FsDialog;
import com.inveno.piflow.tools.bitmap.FlyshareBitmapManager;
import com.inveno.piflow.tools.commontools.DeviceConfig;
import com.inveno.piflow.tools.commontools.StringTools;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 你懂的，不解释
 * 
 * @author mingsong.zhang
 * @date 2013-09-11
 * 
 */
public class WaterwallAudioView extends RelativeLayout implements OnClickListener {

	private LayoutInflater mLayoutInflater;

	private Context mContext;
	private FlyshareBitmapManager bitmapManager;
	private DownloadManager downloadManager;
	private ShowFlowNewinfo mShowFlowNewinfo;
	private ShowFlowHardAd showFlowHardAd;

	private ImageView iv;
	private TextView titleTv;
	private TextView contentTv;
	private TextView summeryTv;
	private RelativeLayout imgLy;

	private String ctype;
	private int second;

	private String content;
	private String imgUrl;

	private int displayWidth;

	private int txtSpaceWidth;

	private Dialog downloadDialog;

	private LinearLayout mLayout;

	private ImageView mBack;
	private WFPvOperation pvOperation;
	private long srartTime;
	private String recoId;
	private HashMap<String, ShowFlowNews> map;
	
	private ShowFlowNews showFlowNews;

	public WaterwallAudioView(Context context, ShowFlowNewinfo showFlowNewinfo,
			LinearLayout view, FlyShareApplication app) {
		super(context);

		init(context, showFlowNewinfo, view,app);
	}

	public WaterwallAudioView(Context context, AttributeSet attrs,
			ShowFlowNewinfo showFlowNewinfo, LinearLayout view, FlyShareApplication app) {
		super(context, attrs);

		init(context, showFlowNewinfo, view,app);
	}

	private void init(Context context, ShowFlowNewinfo showFlowNewinfo,
			LinearLayout view, FlyShareApplication app) {
		this.mContext = context;
		mLayout = view;
		bitmapManager = FlyshareBitmapManager.create(context);
		mShowFlowNewinfo = showFlowNewinfo;
		if (showFlowNewinfo != null) {
			showFlowHardAd = showFlowNewinfo.getShowFlowHardAd();
			second = showFlowHardAd.getSecond();
			ctype = showFlowHardAd.getCtype();
		}
		displayWidth = DeviceConfig.getInstance(context).w;
		txtSpaceWidth = context.getResources().getInteger(
				R.integer.waterfall_detail_content_space);
		this.mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mLayoutInflater.inflate(
				R.layout.wpe_waterfall_newsdetail_audio_ad, this);
		
		pvOperation = new WFPvOperation();
		map = app.getmShowFlowNewsMap();
		srartTime = System.currentTimeMillis();
		recoId = showFlowNewinfo.getReco_id();
		showFlowNews = map.get(recoId);
		pvOperation.setClick_timestamp("" + srartTime);
		pvOperation.setReco_id(recoId);
		pvOperation.setClick_item_id(showFlowNewinfo.getId());
		pvOperation.setClick_item_type(showFlowNewinfo.getType());

		initView();

		setData(showFlowHardAd);
		
		

	}

	private void initView() {
		iv = (ImageView) findViewById(R.id.waterfall_audio_ad_img);
		titleTv = (TextView) findViewById(R.id.waterfall_audio_ad_title_tv);
		contentTv = (TextView) findViewById(R.id.waterfall_audio_ad_content_tv);
		contentTv.setLineSpacing(txtSpaceWidth, 1);
		summeryTv = (TextView) findViewById(R.id.waterfall_audio_ad_summery_tv);
		if ("7".equals(ctype)) {
			summeryTv.setText("音乐介绍");

		} else {
			summeryTv.setText("视频介绍");
		}
		imgLy = (RelativeLayout) findViewById(R.id.waterfall_audio_ad_img_ly);
		imgLy.setOnClickListener(this);

		mBack = (ImageView) findViewById(R.id.waterfall_audio_back);
		mBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.waterfall_audio_ad_img_ly) {

			// 表示是视频类型
			if ("6".equals(ctype) || "7".equals(ctype)) {
				// 打开链接
				if (second == 5) {

					String linkUrl = showFlowHardAd.getLinkurl();
					try {
						Tools.showLog("lhc", "打开连接1：" + linkUrl);
						if (StringTools.httpJudge(linkUrl)) {
							Intent it = new Intent(Intent.ACTION_VIEW,
									Uri.parse(linkUrl));
							// 调用系统的浏览器 如果不存在的话 会抛异常
							it.setClassName("com.android.browser",
									"com.android.browser.BrowserActivity");
							mContext.startActivity(it);
						} else {
							// ToastTools.showToast(context, "链接异常!!");
						}
					} catch (ActivityNotFoundException e) {
						try {
							Tools.showLog("lhc", "打开连接2：" + linkUrl);
							if (StringTools.httpJudge(linkUrl)) {
								Intent intent = new Intent(Intent.ACTION_VIEW,
										Uri.parse(linkUrl));
								mContext.startActivity(intent);
							} else {
								// ToastTools.showToast(context, "链接异常!!");
							}
						} catch (ActivityNotFoundException e2) {
							// ToastTools.showToast(context, "无法找到可用浏览器!!");
						}

					}
				}
				// 下载视频客户端
				else if (second == 3) {

					String apk = showFlowHardAd.getCpapk();
					String cpName = showFlowHardAd.getCpname();
					String packageName = showFlowHardAd.getCppackage();
					String clsName = null;
					String linkUrl = showFlowHardAd.getLinkurl();
					if (showFlowHardAd != null) {
						clsName = packageName.substring(
								packageName.indexOf("/") + 1,
								packageName.length());
						packageName = packageName.substring(0,
								packageName.indexOf("/"));

					}
					Intent intent = new Intent(Intent.ACTION_MAIN, null);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);

					ResolveInfo resolveInfo = checkApp(packageName, intent);

					if (resolveInfo != null) {

						if (StringTools.isNotEmpty(clsName)) {
							Tools.showLog("wf", "广告包名：" + packageName
									+ "  act名:" + clsName);
							Tools.showLog("wf", "广告linkUrl：" + linkUrl);
							try {
								Intent i = new Intent(
										"android.intent.action.VIEW");
								i.setComponent(new ComponentName(packageName,
										clsName));
								i.putExtra("url", linkUrl);
								mContext.startActivity(i);
							} catch (ActivityNotFoundException e) {
								Tools.showLog("wf",
										"activityNotFound ：" + e.getMessage());
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
						downloadDialog = createDownLoadDialog(mContext, cpName,
								apk);
					}
					downloadDialog.show();
				}
			}

		}else if(v.getId() == R.id.waterfall_audio_back){
			if(mLayout!=null){
				
				WFPvUploadBiz.getInstance().post(mContext, pvOperation, srartTime,
						showFlowNews, mShowFlowNewinfo );
				
				mLayout.removeAllViews();
			}
				
				
			
		}
	}

	private void setData(ShowFlowHardAd showFlowHardAd) {
		if (showFlowHardAd != null) {
			List<ShowFlowP> showFlowPs = mShowFlowNewinfo.getContent();
			if (showFlowPs != null) {
				int size = showFlowPs.size();
				ShowFlowP showFlowP = null;
				for (int i = 0; i < size; i++) {
					showFlowP = showFlowPs.get(i);
					boolean isImg = showFlowP.isImg();
					if (isImg) {
						imgUrl = showFlowP.getUrl();

						int height = (int) ((((displayWidth * 1.0f) / showFlowP
								.getWidth()) * showFlowP.getHeight()));
						bitmapManager.displayForFlow(iv, imgUrl + "&width="
								+ displayWidth, 0, 0, displayWidth, height,
								150, false);

						break;
					}

				}
			}

			titleTv.setText(mShowFlowNewinfo.getTitle());
			String content = showFlowHardAd.getContent();
			Tools.showLog("wf", "视频内容:" + content);
			if (StringTools.isNotEmpty(content)) {
				contentTv.setText(Html.fromHtml(content));
			}

		}
	}

	/**
	 * 打开与参数包名相同的第三方应用
	 * 
	 * @param packageName
	 */
	private ResolveInfo checkApp(String packageName, Intent intent) {

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

	private Dialog createDownLoadDialog(final Context context,
			final String name, final String url) {

		Dialog dialog = null;
		FsDialog.Builder customBuilder = new FsDialog.Builder(context);
		customBuilder.setTitle("下载提示").setMessage("您还未安装" + name + ",点击下载")
				.setPositiveButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.setNegativeButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 启动服务下载更新
						// DownloadManager downloadManager =
						// ((WFNewsDetailActivity) context)
						// .getDownloadManager();
						// if (downloadManager != null
						// && StringTools.isNotEmpty(url)) {
						//
						// downloadManager.addHandler(url, name, 99999, null);
						// }

						Intent i = new Intent(context, DownloadService.class);
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

}
