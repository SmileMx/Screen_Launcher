package com.inveno.piflow.entity.view.news;

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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inveno.piflow.R;
import com.inveno.piflow.download.downloadmanager.download.DownloadManager;
import com.inveno.piflow.download.downloadmanager.download.DownloadService;
import com.inveno.piflow.entity.model.showflow.ShowFlowHardAd;
import com.inveno.piflow.entity.model.showflow.ShowFlowNewinfo;
import com.inveno.piflow.entity.model.showflow.ShowFlowP;
import com.inveno.piflow.entity.view.FsDialog;
import com.inveno.piflow.tools.bitmap.FlyshareBitmapManager;
import com.inveno.piflow.tools.commontools.DeviceConfig;
import com.inveno.piflow.tools.commontools.StringTools;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 瀑布流视频详情碎片
 * 
 * @author Administrator
 * 
 */
public class WFAudioFragment extends Fragment implements OnClickListener {

	private Context context;
	private FlyshareBitmapManager bitmapManager;
	private DownloadManager downloadManager;
	private ShowFlowNewinfo showFlowNewinfo;
	private ShowFlowHardAd showFlowHardAd;

	private View view;
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

	public WFAudioFragment() {
		super();
	}

	public static WFAudioFragment getInstance(ShowFlowNewinfo info) {

		WFAudioFragment fragment = new WFAudioFragment();
		Bundle b = new Bundle();
		b.putSerializable("data", info);
		fragment.setArguments(b);
		return fragment;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = getActivity();
		bitmapManager = FlyshareBitmapManager.create(context);
		showFlowNewinfo = (ShowFlowNewinfo) (getArguments() != null ? getArguments()
				.getSerializable("data") : null);
		if (showFlowNewinfo != null) {
			showFlowHardAd = showFlowNewinfo.getShowFlowHardAd();
			second = showFlowHardAd.getSecond();
			ctype = showFlowHardAd.getCtype();
		}
		displayWidth = DeviceConfig.getInstance(context).w;
		txtSpaceWidth = context.getResources()
		.getInteger(R.integer.waterfall_detail_content_space);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.wpe_waterfall_newsdetail_audio_ad,
				container, false);
		initView(view);
		return view;
	}

	private void initView(View v) {
		iv = (ImageView) view.findViewById(R.id.waterfall_audio_ad_img);
		titleTv = (TextView) view
				.findViewById(R.id.waterfall_audio_ad_title_tv);
		contentTv = (TextView) view
				.findViewById(R.id.waterfall_audio_ad_content_tv);
		contentTv.setLineSpacing(txtSpaceWidth, 1);
		summeryTv=(TextView) v.findViewById(R.id.waterfall_audio_ad_summery_tv);
		if ("7".equals(ctype)) {
			summeryTv.setText("音乐介绍");
			
		}else{
			summeryTv.setText("视频介绍");
		}
		imgLy = (RelativeLayout) view
				.findViewById(R.id.waterfall_audio_ad_img_ly);
		imgLy.setOnClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setData(showFlowHardAd);

	}

	private void setData(ShowFlowHardAd showFlowHardAd) {
		if (showFlowHardAd != null) {
			List<ShowFlowP> showFlowPs = showFlowNewinfo.getContent();
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
								+ displayWidth,
								0,
								0,
								displayWidth, height, 150, false);

						break;
					}

				}
			}

			titleTv.setText(showFlowNewinfo.getTitle());
			String content=showFlowHardAd.getContent();
			Tools.showLog("wf", "视频内容:"+content);
			if (StringTools.isNotEmpty(content)) {
				contentTv.setText(Html.fromHtml(content));
			}
			

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// 表示是视频类型
		if ("6".equals(ctype)||"7".equals(ctype)) {
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
						context.startActivity(it);
					} else {
//						ToastTools.showToast(context, "链接异常!!");
					}
				} catch (ActivityNotFoundException e) {
					try {
						Tools.showLog("lhc", "打开连接2：" + linkUrl);
						if (StringTools.httpJudge(linkUrl)) {
							Intent intent = new Intent(Intent.ACTION_VIEW,
									Uri.parse(linkUrl));
							context.startActivity(intent);
						} else {
//							ToastTools.showToast(context, "链接异常!!");
						}
					} catch (ActivityNotFoundException e2) {
//						ToastTools.showToast(context, "无法找到可用浏览器!!");
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
							startActivity(i);
						} catch (ActivityNotFoundException e) {
							Tools.showLog("wf", "activityNotFound ：" + e.getMessage());
							intent.setComponent(new ComponentName(
									resolveInfo.activityInfo.packageName,
									resolveInfo.activityInfo.name));
							startActivity(intent);
						}
						
					} else {
						intent.setComponent(new ComponentName(
								resolveInfo.activityInfo.packageName,
								resolveInfo.activityInfo.name));
						startActivity(intent);
					}
					return;

				}

				if (downloadDialog == null) {
					downloadDialog = createDownLoadDialog(context, cpName, apk);
				}
				downloadDialog.show();
			}
		}

	}

	/**
	 * 打开与参数包名相同的第三方应用
	 * 
	 * @param packageName
	 */
	private ResolveInfo checkApp(String packageName, Intent intent) {

		List<ResolveInfo> list = getActivity().getPackageManager()
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

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
