package com.inveno.piflow.entity.view.waterwall;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inveno.piflow.R;
import com.inveno.piflow.activity.FlyShareApplication;
import com.inveno.piflow.activity.PhotoShowActivity;
import com.inveno.piflow.activity.ext.WFNewsDetailActivity;
import com.inveno.piflow.biz.DayNightModeBiz;
import com.inveno.piflow.biz.NewsDetailBiz;
import com.inveno.piflow.biz.upload.PvBasicStateDataBiz;
import com.inveno.piflow.biz.upload.PvOperationBiz;
import com.inveno.piflow.biz.upload.WFPvUploadBiz;
import com.inveno.piflow.download.downloadmanager.download.DownLoadCallback;
import com.inveno.piflow.download.downloadmanager.download.DownloadManager;
import com.inveno.piflow.download.downloadmanager.download.DownloadService;
import com.inveno.piflow.download.downloadmanager.download.DownloadService.DownloadServiceImpl;
import com.inveno.piflow.download.downloadmanager.download.IDownloadService;
import com.inveno.piflow.entity.model.acount.Comment;
import com.inveno.piflow.entity.model.showflow.ShowFlowAd;
import com.inveno.piflow.entity.model.showflow.ShowFlowHardAd;
import com.inveno.piflow.entity.model.showflow.ShowFlowNewinfo;
import com.inveno.piflow.entity.model.showflow.ShowFlowNews;
import com.inveno.piflow.entity.model.showflow.ShowFlowP;
import com.inveno.piflow.entity.model.upload.WFPvOperation;
import com.inveno.piflow.entity.view.news.NewsDetailRelativeLayout;
import com.inveno.piflow.entity.view.news.NewsDetailScrollView;
import com.inveno.piflow.entity.view.news.NewsDetailScrollView.OnScrollListener;
import com.inveno.piflow.entity.view.news.ShrareGirdFragment;
import com.inveno.piflow.entity.view.news.WFNewsImageView;
import com.inveno.piflow.entity.view.news.WfNewsListView;
import com.inveno.piflow.tools.android.ToastTools;
import com.inveno.piflow.tools.bitmap.FlyshareBitmapManager;
import com.inveno.piflow.tools.commontools.Const;
import com.inveno.piflow.tools.commontools.DeviceConfig;
import com.inveno.piflow.tools.commontools.MkdirFileTools;
import com.inveno.piflow.tools.commontools.OperDialog;
import com.inveno.piflow.tools.commontools.StringTools;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 不解释，大家都懂
 * 
 * @author mingsong.zhang
 * @date 2013-09-11
 * 
 */
@SuppressLint("NewApi")
public class WaterwallDetailView extends NewsDetailRelativeLayout implements
		OnScrollListener {

	private LayoutInflater mLayoutInflater;

	private Context mContext;

	private LinearLayout mLayout;
	private LinearLayout mDialogLayout;

	private static final String NEWSINFO_DATA = "newsinfo_data";
	private static final String NEWSINFO_FONTSIZE = "newsinfo_fontsize";
	private static final String NEWSINFO_IMGS_DATA = "newsinfo_imgs_data";
	private static final String FRAGMENT_POSITION = "fragment_position";
	private static final String FRAGMENT_FROMWHERE = "fragment_frowhere";
	private static final String REG_TO_WX = "reg_to_wx";

	/** 推广应用的状态 **/
	private static final String ADS_APP_STATE = "ads_app_state";

	/** 默认下载图片大小控制 */
	private int PADDING_IMG = 150;

	/*** 底部广告操作 ***/
	private FlyshareBitmapManager bitmapManager;
	private ShowFlowNewinfo mShowFlowNewinfo;

	/** 信息标题 */
	private String title;

	/** 信息时间 */
	private String time;

	/** 资讯原文链接 */
	private String originalUrl = "";

	/** 资讯来源 **/
	private String mSource;

	/** 资讯大类 **/
	private String mBigType = "";

	/** 资讯所属频道id */
	private int typeCode;

	private ArrayList<String> imgUrls;

	private TextView tvFrom;

	/** 最上面的线 **/
	private ImageView mImageViewTopLine;

	/** 资讯90%透明栏，包含时间，评论等图标 **/
	private RelativeLayout middleLayout;

	/** 信息标题控件 */
	private TextView titleText;

	/** 信息时间控件 */
	private TextView timeText;

	private ImageView midLine;

	private NewsDetailBiz mNewsDetailBiz;
	private DayNightModeBiz dayNightModeBiz;

	/** 解决widget跳转栈问题 **/
	private int mFromWhere;

	/** 用户点击数据处理类 **/
	private PvOperationBiz mPvOperationBiz;

	private long startTime;
	private long overTime;

	private boolean hasLoad;

	private int mFontSize = 18;

	private OnClickListener clickListener;

	private View v;

	private boolean isDestroy;
	private boolean isPause;
	/** 获取到资讯内容,用于记录已读未读 */
	private boolean loadOut;
	/** 标识曾经onpause过 */
	private boolean hasPause;

	// private NewsCommonMoreBiz mNewsCommonMoreBiz;

	private int fragmentId;

	private boolean scrollBottom;

	private int sWidth;

	/** 记录分享点击时间 短时间内不能连续点击多次 **/
	private long oldTime;

	private boolean isShare;

	private Comment currentComment;

	private Handler sonHandler;

	private ExecutorService executorService;

	private AsyncTask<Integer, String, String> contentTask;

	private Runnable savePvRunnable;

	/** 流量控制模式 **/
	private String flowControlMode;

	/** 推广应用状态 */
	private int adsAppState;
	/** 推广应用在该系统中的状态，默认0未下载， 1已经安装，2已下载完成，未安装 ***/
	public static final int NOT_DOWNLOADED = 0;
	public static final int HAS_INSTALL = 1;
	public static final int FINISH_DOWNLOAD = 2;

	/** 底部广告操作ui **/
	private TextView adTitleTv;
	private ProgressBar adBar;
	private ImageView adIconIv;
	private TextView adName;
	private RelativeLayout adBtn;
	private RelativeLayout adparentLy;

	private Button orginalBtn;

	private Dialog phoneCallDialog;

	// /** 内容父容器 */
	private LinearLayout contentLy;

	private List<ShowFlowP> content;
	private ShowFlowAd showFlowAd;

	private ShowFlowHardAd flowHardAd;

	/** 广告应用名 */
	private String cpName;

	private String adTitle;

	private String cpApk;

	private String cpPackage;

	private String linkUrl;

	/** 图片空间宽,高 */
	private int ivWidth;
	private int ivHeight;
	/** 字体行间距 */
	private int spaceWidth;

	private Resources resources;

	private WfNewsListView listView;

	private RelativeLayout headView;
	private LinearLayout footView;

	private NewsDetailScrollView scrollView;

	private ArrayList<TextView> tvs;
	private ArrayList<ImageView> ivs;

	private int tvSize;
	private int ivSize;

	/** 屏幕高 */
	private int scrollHeight;

	/** 需要下载 */
	private int imgWidth;

	/** 广告类型 */
	private int second;

	private int iconSize = 40;

	private Drawable shareIcon;

	private TextView shareTv;

	private boolean canShareWX;

	private Bitmap screenShot;

	private ShrareGirdFragment shareFragment;

	private DownloadManager downloadManager;

	private DownLoadCallback callback;
	private ServiceConnection serviceConnection;

	private ImageView mBack;

	private WFPvOperation pvOperation;
	private long srartTime;
	private String recoId;
	private HashMap<String, ShowFlowNews> map;

	private ShowFlowNews showFlowNews;

	private ArrayList<String> widths;
	private ArrayList<String> heights;

	public WaterwallDetailView(Context context,
			ShowFlowNewinfo showFlowNewinfo, LinearLayout view,
			LinearLayout dialogLayout, FlyShareApplication app) {
		super(context);
		init(context, showFlowNewinfo, view, dialogLayout, app);
	}

	public WaterwallDetailView(Context context, AttributeSet attrs,
			ShowFlowNewinfo showFlowNewinfo, LinearLayout view,
			LinearLayout dialogLayout, FlyShareApplication app) {
		super(context, attrs);
		init(context, showFlowNewinfo, view, dialogLayout, app);
	}

	private void init(Context context, ShowFlowNewinfo showFlowNewinfo,
			LinearLayout view, LinearLayout dialogLayout,
			FlyShareApplication app) {
		this.mContext = context;
		this.mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mLayoutInflater.inflate(R.layout.wpc_waterfall_newsdetail, this);
		this.mLayout = view;
		this.mDialogLayout = dialogLayout;
		this.mShowFlowNewinfo = showFlowNewinfo;
		this.widths = new ArrayList<String>();
		this.heights = new ArrayList<String>();

		pvOperation = new WFPvOperation();

		imgUrls = new ArrayList<String>();
		// 把图片和内容分开
		List<ShowFlowP> ps = showFlowNewinfo.getContent();
		if (ps != null) {
			int size = ps.size();
			for (int i = 0; i < size; i++) {
				ShowFlowP p = ps.get(i);
				if (p.isImg()) {
					imgUrls.add(p.getUrl());
				}

			}
		}

		mNewsDetailBiz = new NewsDetailBiz(mContext);
		// mNewsCommonMoreBiz = getNewsCommonMoreBiz();

		mPvOperationBiz = PvOperationBiz.getInstance();
		dayNightModeBiz = DayNightModeBiz.getInstance(mContext);

		bitmapManager = FlyshareBitmapManager.create(mContext);
		// bitmapManager.flushCache();
		// ((NewsCommonActivity) getActivity()).setOnPageChange(this);
		sWidth = mContext.getResources().getDimensionPixelSize(
				R.dimen.news_comment_s_userhead);
		// lp = new LinearLayout.LayoutParams(sWidth, sWidth);
		// lp.setMargins(
		// 0,
		// 0,
		// mContext.getResources().getInteger(
		// R.integer.comment_userimgs_margin), 0);

		flowControlMode = Tools.getInformain(Const.ALLSETTINGS,
				Tools.FLOW_CONTROL_MODE, Const.ALWAYS_LOAD_MODE, mContext);
		Tools.showLog("wf", " oncrete WfNewsFragment:" + fragmentId
				+ "  fontsize:" + mFontSize);
		resources = mContext.getResources();
		ivWidth = resources
				.getInteger(R.integer.waterfall_detail_imageview_width);

		spaceWidth = resources
				.getInteger(R.integer.waterfall_detail_content_space);
		startTime = System.currentTimeMillis();
		tvs = new ArrayList<TextView>(5);
		ivs = new ArrayList<ImageView>(5);

		scrollHeight = DeviceConfig.getInstance(mContext).h;
		imgWidth = DeviceConfig.getInstance(mContext).w;
		ivWidth = imgWidth;
		PADDING_IMG = imgWidth / 6;
		if (imgWidth > 540)
			imgWidth = 540;
		// currentBm=scrollHeight;

		// iconSize = resources.getInteger(
		// R.integer.comment_icon_size);
		iconSize = 40;

		shareIcon = resources.getDrawable(R.drawable.icon_share);
		shareIcon.setBounds(0, 0, iconSize, iconSize);

		serviceConnection = getServiceConnection();
		callback = getDownCallback();

		initAllChildView();
		setNewsInfo(showFlowNewinfo);
		initBitmap();

		map = app.getmShowFlowNewsMap();
		srartTime = System.currentTimeMillis();
		recoId = showFlowNewinfo.getReco_id();
		showFlowNews = map.get(recoId);
		pvOperation.setClick_timestamp("" + srartTime);
		pvOperation.setReco_id(recoId);
		pvOperation.setClick_item_id(showFlowNewinfo.getId());
		pvOperation.setClick_item_type(showFlowNewinfo.getType());
	}

	private void initBitmap() {

		// if (flowControlMode.equals(Const.SMART_LOAD_MODE)) {
		// Tools.showLog("pf",
		// "详情页智能模式开启了：" + NetWorkUtil.getAPNType(mContext));
		// if (!(NetWorkUtil.getAPNType(mContext) == netType.wifi || NetWorkUtil
		// .getAPNType(mContext) == netType.noneNet)) {
		// return;
		// }
		//
		// } else if (!flowControlMode.equals(Const.ALWAYS_LOAD_MODE)) {
		// Tools.showLog("pf", "详情全图模式开启了：");
		// return;
		//
		// }
		Tools.showLog("pf", "详情准备下载图片：" + flowControlMode);
		if (ivSize != 0 && bitmapManager != null) {
			WFNewsImageView iv = null;
			for (int i = 0; i < ivSize; i++) {
				iv = (WFNewsImageView) ivs.get(i);
				int height = iv.getIvHeight();
				int index = iv.getIndex();
				if (!iv.isHasSet()
						&& (!iv.isHasSet() && height * index < scrollHeight || i == 0)) {
					iv.setHasSet(true);
					Tools.showLog("pf", "iv.getImgUrl() ：" + iv.getImgUrl());
					bitmapManager.displayForFlow(iv, iv.getImgUrl(),
							R.drawable.waterwall_list_default,
							R.drawable.waterwall_list_default, ivWidth, height,
							150, true);
				}

			}

		}
	}

	public void setNewsInfo(ShowFlowNewinfo newsInfo) {
		if (newsInfo != null) {

			content = newsInfo.getContent();
			showFlowAd = newsInfo.getShowFlowAd();
			flowHardAd = newsInfo.getShowFlowHardAd();
			this.time = newsInfo.getRtime() + "";

			this.title = newsInfo.getTitle();
			this.originalUrl = newsInfo.getOriginUrl();
			this.mSource = newsInfo.getSrc();

			ShowFlowHardAd ad = newsInfo.getShowFlowHardAd();
			if (StringTools.isEmpty(mSource)) {
				if (ad != null) {
					mSource = ad.getCpname();
				}
			}

			this.titleText.setText(title);
			this.titleText.setTag(title);
			this.timeText.setText(time);
			this.tvFrom.setText(mSource);

			if (content != null) {
				int size = content.size();
				ShowFlowP p = null;
				String news = null;
				LayoutParams pp = new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);

				// 如果图片控件连续，则设置margintop间隙
				int count = 1;
				boolean imgMargin;

				for (int i = 0; i < size; i++) {
					p = content.get(i);

					boolean isImg = p.isImg();
					if (!isImg) {
						news = p.getContent();

						Spanned text = Html.fromHtml(news);
						Tools.showLog("abc",
								"文字 i:" + i + " 内容:" + text.toString()
										+ " news:" + news);
						if (StringTools.isNotEmpty(text.toString())) {
							TextView t = new TextView(mContext);
							t.setLayoutParams(pp);
							t.setText(text);
							t.setTag(text.toString());
							t.setLineSpacing(spaceWidth, 1);
							t.setTextSize(mFontSize);
							// if (dayNightModeBiz.getMode()==2)
							// t.setTextColor(dayNightModeBiz.getBlackNotRead());
							// else
							t.setTextColor(dayNightModeBiz.getBlack());

							tvs.add(t);
							contentLy.addView(t);
						} else {
							// 空内容，则下次如果为图片则哎在一起了
							count = i;
						}

					} else {
						Tools.showLog("abc", "图片 i:" + i + " count:" + count);
						// 是否设置顶部间隙
						if (i - count == 1) {

							imgMargin = true;
						} else {
							imgMargin = false;
						}

						final String url = p.getUrl();

						int height = (int) ((((ivWidth * 1.0f) / p.getWidth()) * p
								.getHeight()));
						widths.add(p.getWidth() + "");
						heights.add(p.getHeight() + "");

						WFNewsImageView iv = new WFNewsImageView(mContext,
								ivWidth, height, p.getWidth(), p.getHeight(),
								count, url, spaceWidth, imgMargin, true);

						iv.setOnClickListener(new OnClickListener() {
							private long lastClickTime;

							@Override
							public void onClick(View v) {
								long time = System.currentTimeMillis();
								long timeD = time - lastClickTime;
								if (0 < timeD && timeD < 500) { // 500毫秒内按钮无效，这样可以控制快速点击，自己调整频率
									return;
								}
								lastClickTime = time;

								Intent intent = new Intent(mContext,
										PhotoShowActivity.class);
								intent.putExtra("FromWhere",
										PhotoShowActivity.FROM_WATERFALL);
								intent.putExtra("currentPhoto", url);
								intent.putStringArrayListExtra(
										"NewsImgUrlList", imgUrls);
								intent.putStringArrayListExtra("widths", widths);
								intent.putStringArrayListExtra("heights", heights);
								
								PvBasicStateDataBiz.BASEFLAG = false;
								mContext.startActivity(intent);
							}
						});

						// imageViews.add(iv);
						ivs.add(iv);
						contentLy.addView(iv);
						// Tools.showLog("lhc",i+"add iv top:" +iv.getTop());
						//
						// if (height*count<scrollHeight) {
						// iv.setHasSet(true);
						// bitmapManager.displayForFlow(iv, url,
						// R.drawable.waterwall_list_default,
						// R.drawable.waterwall_list_default, ivWidth,
						// height, PADDING_IMG, false);
						//
						// }

						// bitampRunnbles.add(new BitampRunnble(imgUrl,
						// bitmapManager, ivWidth, height,iv));
						count = i;
					}
				}

				tvSize = tvs.size();
				ivSize = ivs.size();

				// 添加底部广告
				if (showFlowAd != null
						&& (second = showFlowAd.getSecond()) != 0) {
					cpName = showFlowAd.getCpName();
					cpApk = showFlowAd.getCpApk();
					cpPackage = showFlowAd.getCpPackage();
					linkUrl = showFlowAd.getLinkUrl();
					adsOperateUi(showFlowAd.getSecond(),
							showFlowAd.getAdTitle());
				}// 添加原文链接
				else if ((flowHardAd != null && (second = flowHardAd
						.getSecond()) != 0)) {
					cpName = flowHardAd.getCpname();
					cpApk = flowHardAd.getCpapk();
					cpPackage = flowHardAd.getCppackage();
					linkUrl = flowHardAd.getLinkurl();
					adsOperateUi(flowHardAd.getSecond(),
							flowHardAd.getAdTitle());
				} else if (StringTools.isNotEmpty(originalUrl)) {
					orginalBtn.setVisibility(View.VISIBLE);
				}

			}
		}

		// bitmapManager.flushCache();

	}

	private void initAllChildView() {

		initHeadView();
		initFootView();
	}

	private void initHeadView() {
		clickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				int id = v.getId();
				if (id == R.id.waterfall_appdetail_download_ly) {

					if (showFlowAd != null) {
						adsOperateClick(showFlowAd.getSecond());
					} else if (flowHardAd != null) {
						adsOperateClick(flowHardAd.getSecond());
					}

				} else if (id == R.id.news_detail_original_btn) {

					// // 打开原文链接

					try {
						Tools.showLog("lhc", "打开原文连接：" + originalUrl);
						if (StringTools.httpJudge(originalUrl)) {
							Intent it = new Intent(Intent.ACTION_VIEW,
									Uri.parse(originalUrl));
							// 调用系统的浏览器 如果不存在的话 会抛异常
							it.setClassName("com.android.browser",
									"com.android.browser.BrowserActivity");
							mContext.startActivity(it);
						} else {
							ToastTools.showToast(mContext, "链接异常!!");
						}
					} catch (ActivityNotFoundException e) {
						try {
							Tools.showLog("lhc", "打开原文连接：" + originalUrl);
							if (StringTools.httpJudge(originalUrl)) {
								Intent intent = new Intent(Intent.ACTION_VIEW,
										Uri.parse(originalUrl));
								mContext.startActivity(intent);
							} else {
								ToastTools.showToast(mContext, "链接异常!!");
							}
						} catch (ActivityNotFoundException e2) {
							ToastTools.showToast(mContext, "无法找到可用浏览器!!");
						}

					}
				} else if (id == R.id.content_share_TextView) {// 分享
				// if (shareFragment==null) {
				// screenShot=BitmapTools.takeScreenShot((Activity)mContext);
				// shareFragment=new ShrareGirdFragment(title,
				// null,screenShot,originalUrl);
				// }
				// shareFragment.show(((Activity)mContext).getSupportFragmentManager(),
				// "shareFragment");

					WaterwallShareGridView wsgv = new WaterwallShareGridView(
							mContext, title, originalUrl, mDialogLayout);
					mDialogLayout.addView(wsgv);

				} else if (id == R.id.news_detail_back) {
					if (shareIcon != null) {
						shareIcon.setCallback(null);
					}
					bitmapManager.flushCache();
					for (int i = 0; i < ivSize; i++) {
						((WFNewsImageView) ivs.get(i)).recycle();
					}
					contentLy.removeAllViews();
					contentLy = null;
					tvs.clear();
					ivs.clear();

					WFPvUploadBiz.getInstance().post(mContext, pvOperation,
							srartTime, showFlowNews, mShowFlowNewinfo);

					mLayout.removeAllViews();
				}
			}
		};
		titleText = (TextView) findViewById(R.id.news_datail_title_TextView);
		timeText = (TextView) findViewById(R.id.news_detail_time_TextView);
		shareTv = (TextView) findViewById(R.id.content_share_TextView);
		shareTv.setCompoundDrawables(shareIcon, null, null, null);
		shareTv.setOnClickListener(clickListener);
		middleLayout = (RelativeLayout) findViewById(R.id.news_detail_middle_RelativeLayout);

		tvFrom = (TextView) findViewById(R.id.news_datail_type_from_TextView);
		scrollView = (NewsDetailScrollView) findViewById(R.id.news_details_scroll);
		scrollView.setOnScrollListener(this);
		contentLy = (LinearLayout) findViewById(R.id.news_detail_content_ly);

		midLine = (ImageView) findViewById(R.id.news_detail_title_bottom_line);
		mImageViewTopLine = (ImageView) findViewById(R.id.news_detail_top_line_ImageView);

		mBack = (ImageView) findViewById(R.id.news_detail_back);
		mBack.setOnClickListener(clickListener);
	}

	private void initFootView() {
		orginalBtn = (Button) findViewById(R.id.news_detail_original_btn);
		orginalBtn.setOnClickListener(clickListener);

		/*************** 底部广告控件 ****************/
		adTitleTv = (TextView) findViewById(R.id.wf_newsdetail_ads_title);
		adBar = (ProgressBar) findViewById(R.id.waterfall_appdetail_download_pb);
		adIconIv = (ImageView) findViewById(R.id.waterfall_appdetail_download_icon);
		adName = (TextView) findViewById(R.id.waterfall_appdetail_download_tv);
		adBtn = (RelativeLayout) findViewById(R.id.waterfall_appdetail_download_ly);
		adparentLy = (RelativeLayout) findViewById(R.id.news_detail_ads);
		adBtn.setOnClickListener(clickListener);

		int random = new Random().nextInt(Const.skinColor.length);
		mImageViewTopLine.setBackgroundResource(Const.skinColor[random]);

		fontSizeSet(mFontSize);
		styleChange(dayNightModeBiz.getMode());
		scrollView.enableScroll();

	}

	/**
	 * 设置Webview的字体大小
	 * 
	 * @param size
	 */
	public void fontSizeSet(int size) {

		mNewsDetailBiz.changeFontSize(size, dayNightModeBiz.getMode());
		for (int i = 0; i < tvSize; i++) {
			TextView tv = tvs.get(i);
			tv.setTextSize(size);
		}
		// if (newsDetailAdapter != null) {
		// newsDetailAdapter.changeFontSizeUi(size);
		// }
	}

	/**
	 * 设置日夜模式
	 */
	public void styleChange(int mode) {

		Tools.showLog("cbd", "日夜模式为：" + mode);
		if (mode == 2) {
			mNewsDetailBiz.nightMode();
			// setBackgroundColor(dayNightModeBiz.getBlackbg());
			titleText.setBackgroundColor(dayNightModeBiz.getBlackbg());
			titleText.setTextColor(dayNightModeBiz.getBlackNotRead());
			middleLayout.setBackgroundColor(dayNightModeBiz
					.getTransparentBlack());
			midLine.setBackgroundColor(dayNightModeBiz.getNightLine());
			adTitleTv.setTextColor(dayNightModeBiz.getBlackNotRead());

			// for (int i = 0; i < tvSize; i++) {
			// TextView tv = tvs.get(i);
			// tv.setTextColor(dayNightModeBiz.getBlackNotRead());
			// }

			// for (int i = 0; i < ivSize; i++) {
			// ImageView iv=ivs.get(i);
			// iv.setAlpha(0.3f);
			// }
			// mHandler.sendEmptyMessage(NewsCommonVariable.NIGHT);
		}

		else {
			mNewsDetailBiz.dayMode();
			// for (int i = 0; i < tvSize; i++) {
			// TextView tv = tvs.get(i);
			// tv.setTextColor(Color.BLACK);
			// }
			// for (int i = 0; i < ivSize; i++) {
			// ImageView iv=ivs.get(i);
			// iv.setAlpha(1.0f);
			// }
			setBackgroundColor(dayNightModeBiz.getWhite());
			titleText.setBackgroundColor(dayNightModeBiz.getWhite());
			titleText.setTextColor(dayNightModeBiz.getBlack());
			middleLayout.setBackgroundColor(dayNightModeBiz
					.getTransparentWhite());
			midLine.setBackgroundColor(dayNightModeBiz.getDayLine());
			adTitleTv.setTextColor(dayNightModeBiz.getBlack());
			// mHandler.sendEmptyMessage(NewsCommonVariable.DAY);

		}

	}

	/**
	 * 广告二级操作显示的ui
	 * 
	 * @param second
	 */
	private void adsOperateUi(int second, String title) {

		Tools.showLog("wf", "资讯广告操作 second：" + second);
		adTitleTv.setText(title);
		switch (second) {
		case ShowFlowAd.CALL_PHONE:

			adIconIv.setImageResource(R.drawable.icon_wf_ads_call);
			adName.setText("点击通话");
			break;
		case ShowFlowAd.DOWN_APP:

			setAdsBottomBar();

			break;
		case ShowFlowAd.PLAY_MEDIA:
			Tools.showLog("wf", "广告为---视频----");
			break;
		case ShowFlowAd.OPEN_LINK:

			adIconIv.setImageResource(R.drawable.icon_wf_ads_sure);
			adName.setText("查看详情");
			break;

		default:
			break;
		}
		adparentLy.setVisibility(View.VISIBLE);
		// mHandler.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// if (!isDestroy) {
		// getActivity().runOnUiThread(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		// }
		//
		// }
		// }, 1000);
	}

	boolean isDownLoading;

	/** 广告操作 */
	private void adsOperateClick(int second) {

		Tools.showLog("wf", "title:" + title + "   second:" + second);
		switch (second) {
		case ShowFlowAd.CALL_PHONE:
			if (phoneCallDialog == null) {
				phoneCallDialog = OperDialog.getOperDialog().createCallDailog(
						mContext, showFlowAd.getPhone());
			}
			phoneCallDialog.show();
			break;
		case ShowFlowAd.DOWN_APP:
			// TODO 还需分情
			switch (adsAppState) {
			case WFNewsDetailActivity.FINISH_DOWNLOAD:
				installApk(cpApk);
				break;
			case WFNewsDetailActivity.HAS_INSTALL:
				startAppActivity(cpPackage);
				break;
			case WFNewsDetailActivity.NOT_DOWNLOADED:

				// String apkUrl = showFlowAd.getCpApk();
				if (StringTools.isNotEmpty(cpApk)) {

					Intent intent = new Intent(mContext, DownloadService.class);
					intent.putExtra(DownloadService.DOWNLOAD_TASK_KEY,
							DownloadService.TASK_DOWNLOAD_WF_HARD_AD);
					intent.putExtra("appName", cpName);
					intent.putExtra("hardAdUrl", cpApk);
					mContext.startService(intent);

					// downloadManager =
					// DownloadManager.getDownloadManager(mContext);
					//
					// if (downloadManager != null) {
					// //要下载的时候才设置callback
					// // setDownLoadMgCallback();
					// if (!isDownLoading) {
					// isDownLoading = true;
					// if (downloadManager.hasHandler(cpApk)) {
					// Tools.showLog("wf", "继续下载");
					// downloadManager.continueHandler(cpApk);
					// } else {
					// Tools.showLog("wf", "开始下载");
					// downloadManager.addHandler(cpApk, cpName,
					// 88888, null);
					// }
					//
					// adIconIv.setImageResource(R.drawable.icon_wf_cancel);
					// adName.setText("取消下载  ");
					// } else {
					// isDownLoading = false;
					// Tools.showLog("wf", "取消下载");
					// downloadManager.pauseHandler(cpApk);
					// adIconIv.setImageResource(R.drawable.allset_download);
					// adName.setText("下载" + cpName);
					// adBar.setProgress(0);
					// }
					//
					// }

				}

				break;

			default:

				break;
			}

			break;
		case ShowFlowAd.PLAY_MEDIA:
			Tools.showLog("wf", "点击广告为---视频----");
			break;
		case ShowFlowAd.OPEN_LINK:

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
					ToastTools.showToast(mContext, "链接异常!!");
				}
			} catch (ActivityNotFoundException e) {
				try {
					Tools.showLog("lhc", "打开连接2：" + linkUrl);
					if (StringTools.httpJudge(linkUrl)) {
						Intent intent = new Intent(Intent.ACTION_VIEW,
								Uri.parse(linkUrl));
						mContext.startActivity(intent);
					} else {
						ToastTools.showToast(mContext, "链接异常!!");
					}
				} catch (ActivityNotFoundException e2) {
					ToastTools.showToast(mContext, "无法找到可用浏览器!!");
				}

			}

			break;

		default:
			break;
		}

	}

	/** 根据推广应用状态设置UI */
	private void setAdsBottomBar() {

		if (second == 3) {
			switch (adsAppState) {
			case WFNewsDetailActivity.FINISH_DOWNLOAD:
				adIconIv.setImageResource(R.drawable.icon_wf_ads_sure);
				adName.setText("安装" + cpName);
				break;
			case WFNewsDetailActivity.HAS_INSTALL:

				adIconIv.setImageResource(R.drawable.icon_wf_ads_sure);
				adName.setText("点击运行" + cpName);
				break;
			case WFNewsDetailActivity.NOT_DOWNLOADED:

				adIconIv.setImageResource(R.drawable.allset_download);
				adName.setText("下载" + cpName);
				break;

			default:

				adIconIv.setImageResource(R.drawable.allset_download);
				adName.setText("下载" + cpName);
				break;
			}
		}

	}

	/** 外部activity调用改变底部app广告ui */
	public void changeAppAdsBottomBar(int state) {
		adsAppState = state;
		setAdsBottomBar();

	}

	/**
	 * 打开与参数包名相同的第三方应用
	 * 
	 * @param packageName
	 */
	private void startAppActivity(String packageName) {

		String actName = null;
		if (flowHardAd != null) {
			actName = packageName.substring(packageName.indexOf("/") + 1,
					packageName.length());
			packageName = packageName.substring(0, packageName.indexOf("/"));
		}

		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);

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

		if (resolveInfo != null) {

			if (StringTools.isNotEmpty(actName)) {
				Tools.showLog("wf", "广告包名：" + packageName + "  act名:" + actName);
				Tools.showLog("wf", "广告linkUrl：" + linkUrl);
				Intent i = new Intent();
				// i.setAction(Intent.ACTION_VIEW);
				// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setComponent(new ComponentName(packageName, actName));
				i.putExtra("url", linkUrl);
				mContext.startActivity(i);
			} else {
				// Intent i = new Intent(Intent.ACTION_MAIN);
				// i.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.setComponent(new ComponentName(
						resolveInfo.activityInfo.packageName,
						resolveInfo.activityInfo.name));
				mContext.startActivity(intent);
			}

		}
	}

	/**
	 * 更新应用下载进度
	 * 
	 * @param progress
	 */
	public void updateAppProgress(int progress) {
		if (hasLoad) {

		}
		Tools.showLog("wf", "更新瀑布流详情下载进度：" + progress);
		isDownLoading = true;
		adIconIv.setImageResource(R.drawable.icon_wf_cancel);
		adName.setText("取消下载  " + progress + "%");
		adBar.setProgress(progress);

	}

	public void installApk(String apkUrl) {

		String appName = StringTools.getFileNameFromUrl(apkUrl);
		// 如果已经下载过直接提示安装
		String savePathOld = MkdirFileTools.APPS_PATH + File.separator
				+ appName;

		Intent install = new Intent();
		install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		install.setAction(android.content.Intent.ACTION_VIEW);
		install.setDataAndType(Uri.fromFile(new File(savePathOld)),
				"application/vnd.android.package-archive");
		mContext.startActivity(install);

	}

	// private void recycleBitmap() {
	// int size = contentLy.getChildCount();
	// for (int i = 0; i < size; i++) {
	// View v = contentLy.getChildAt(i);
	// if (v instanceof ImageView) {
	// Tools.showLog("wf", "回收图片v:" + v);
	// ((ImageView) v).setImageDrawable(null);
	// }
	// }
	// }

	/** 更新listview 中tts阅读的tv */
	public void updateTtsUi(String content, boolean isRead) {

		// if (newsDetailAdapter != null) {
		// newsDetailAdapter.updateTtsTv(content, isRead);
		// }

		TextView tv;
		String str;
		if (StringTools.isNotEmpty(content)) {
			for (int i = 0; i < tvSize; i++) {
				tv = tvs.get(i);
				str = (String) tv.getTag();
				if (content.equals(str)) {
					if (isRead) {
						SpannableString msp = new SpannableString(str);
						msp.setSpan(new BackgroundColorSpan(Color.CYAN), 0,
								str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						tv.setText(msp);
					} else {
						tv.setText(str);
					}
				}
			}
		} else {
			for (int i = 0; i < tvSize; i++) {
				tv = tvs.get(i);
				str = (String) tv.getTag();
				tv.setText(str);
			}
		}

	}

	/** 更新tts阅读标题ui */
	public void updateTitleTtsUi(boolean isRead) {
		if (isRead) {
			SpannableString msp = new SpannableString(title);
			msp.setSpan(new BackgroundColorSpan(Color.CYAN), 0, title.length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			titleText.setText(msp);
		} else {
			titleText.setText(title);
		}

	}

	private void loadImg(int t, int b, int sonHeight) {
		int ivTop;
		int ivBm;
		for (int i = 0; i < ivSize; i++) {
			WFNewsImageView iv = (WFNewsImageView) ivs.get(i);
			ivTop = iv.getTop() + sonHeight;
			ivBm = iv.getBottom() + sonHeight;
			Tools.showLog("sb", i + "ivtop：" + ivTop + " t:" + t + "ivbottom:"
					+ ivBm + " b:" + b);
			if ((ivTop > t && ivTop < b) || (ivBm > t && ivBm < b)
					|| (ivTop < t && ivBm > b)) {
				// Tools.showLog("sb", i+"下载图片top："+ivTop+"bottom:"+ivBm);
				if (!iv.isHasSet()) {
					iv.setHasSet(true);
					bitmapManager.displayForFlow(iv, iv.getImgUrl(),
							R.drawable.waterwall_list_default,
							R.drawable.waterwall_list_default, ivWidth,
							iv.getHeight(), PADDING_IMG, false);
				}

			}

			else {
				Tools.showLog("sb", i + "回收iv图片");
				FlyshareBitmapManager.cancelWork(iv);
				iv.recycle();
				iv.setHasSet(false);
			}
		}
	}

	private ServiceConnection getServiceConnection() {
		ServiceConnection serviceConnection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				DownloadService.DownloadServiceImpl downloadServiceImpl = (DownloadServiceImpl) IDownloadService.Stub
						.asInterface(service);
				downloadManager = downloadServiceImpl.getDownloadManager();
				Tools.showLog("lhc", "onServiceConnected   downloadManager:"
						+ downloadManager);
				// downloadManager.setDownLoadCallback(callback);
			}
		};
		return serviceConnection;

	}

	public void setDownLoadMgCallback() {
		if (downloadManager != null && callback != null) {
			downloadManager.setDownLoadCallback(callback);
		}
	}

	private DownLoadCallback getDownCallback() {
		DownLoadCallback callback = new DownLoadCallback() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();

			}

			@Override
			public void onAdd(String url, Boolean isInterrupt) {
				// TODO Auto-generated method stub
				super.onAdd(url, isInterrupt);
			}

			@Override
			public void onLoading(String url, String speed, long download,
					int progress) {
				// TODO Auto-generated method stub
				super.onLoading(url, speed, download, progress);

				updateAppProgress(progress);
				if (progress == 100) {
					adsAppState = FINISH_DOWNLOAD;
					changeAppAdsBottomBar(adsAppState);
					if (showFlowAd != null) {
						installApk(showFlowAd.getCpApk());
					} else if (flowHardAd != null) {
						installApk(flowHardAd.getCpapk());
					}

				}

			}

			@Override
			public void onSuccess(String url) {
				// TODO Auto-generated method stub
				super.onSuccess(url);
				Tools.showLog("lhc", this + "onSuccess");

			}

			@Override
			public void onFailure(String url, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(url, strMsg);
				Tools.showLog("lhc", this + "onFailure");

			}

			@Override
			public void onFinish(String url) {
				// TODO Auto-generated method stub
				super.onFinish(url);
			}

			@Override
			public void onStop() {
				// TODO Auto-generated method stub
				super.onStop();
			}

		};

		return callback;
	}

	@Override
	public void onBottom() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAutoScroll(int l, int t, int oldl, int oldt) {
		int halfDisH = scrollHeight / 2;
		loadImg(t - halfDisH, (scrollHeight + t + halfDisH), oldt);
	}

}
