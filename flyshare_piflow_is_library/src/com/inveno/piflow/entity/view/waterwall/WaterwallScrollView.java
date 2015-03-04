package com.inveno.piflow.entity.view.waterwall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.inveno.piflow.R;
import com.inveno.piflow.activity.FlyShareApplication;
import com.inveno.piflow.biz.waterwall.WaterwallBiz;
import com.inveno.piflow.download.downloadmanager.netstate.NetWorkUtil;
import com.inveno.piflow.entity.model.showflow.ShowFlowNewinfo;
import com.inveno.piflow.entity.model.showflow.ShowFlowNews;
import com.inveno.piflow.entity.model.showflow.ShowFlowStyleConfigs;
import com.inveno.piflow.entity.model.showflow.ShowFlowStyles;
import com.inveno.piflow.tools.android.ToastTools;
import com.inveno.piflow.tools.bitmap.FlyshareBitmapManager;
import com.inveno.piflow.tools.commontools.Const.ConnectInternetLoadResult;
import com.inveno.piflow.tools.commontools.Const;
import com.inveno.piflow.tools.commontools.DeviceConfig;
import com.inveno.piflow.tools.commontools.StringTools;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 瀑布流自定义scrollview
 * 
 * @author mingsong.zhang
 * @date 2013-06-17
 * 
 */
@TargetApi(11)
public class WaterwallScrollView extends ScrollView implements OnClickListener,
		android.widget.AbsListView.OnScrollListener {

	public final static String TAG = "WaterwallScrollView";

	/** 加载运行内存或者sd卡中的数据 **/
	public final static int LOAD_CACHE_DATA = 101;
	/** 手离开后 触发 加载当屏图片 **/
	public final static int LOAD_IMG_ON_THE_SCREEN = 100;

	public final static int LOAD_WEATHER_RESULT = 102;

	/** 自动滚动后延迟1秒看是否需要回收items **/
	public final static int NEED_REMOVE_ITEMS = 103;

	/** 延迟刷新天气 **/
	public final static int REFRESH_WEATHER = 104;

	/** 离开屏幕10秒无操作且亮屏，开始发送消息自动滚动 */
	public final static int AUTO_SCROLL = 888;

	private Context mContext;
	private Resources mResources;
	private LinearLayout mWaterwallLayoutMain;
	private FlyshareBitmapManager mFlyshareBitmapManager;
	private FlyShareApplication mApp;
	private LinearLayout mDetailLayout;
	private LinearLayout mDialogLayout;

	/** 点击重新加载 **/
	private LinearLayout mWaterwallSetLy;
	/** 中间的loading圈圈 **/
	// private ProgressBar mPb;

	private WaterwallBiz mWaterwallBiz;
	private ShowFlowStyleConfigs mShowFlowStyleConfigs;

	private ShowFlowNews mShowFlowNews;

	private WaterwallMainItem mWaterwallMainItem;

	private int mLeftHeight;
	private int mRightHeight;
	private Random mR;
	/** 左右两边相差小于这个值就显示通廊 **/
	private int E = 30;

	/** 当前资讯条数 **/
	private int mSize = 0;

	/** 当滑动还有这么多像素就到底部的时候 会加载下一页 **/
	private int SUB = 700;

	private int MAY_REMOVE_GAP = 1500;

	private WaterwallBaseItem mViewLeft;
	private WaterwallBaseItem mViewRight;
	/** 到底部时没有对齐单独多出来的一条资讯 **/
	private ShowFlowNewinfo mShowFlowNewinfo;

	/** 通廊集合 **/
	private ArrayList<ShowFlowNewinfo> mBannerInfos;

	/** 每个图片字块 = 高度 - 图片放到控件上的高度 **/
	private int mImgExtraGap = 96;
	/** 每个字块该分辨率下的图片宽度 **/
	private int mItemWidth = 218;
	/** 通廊该分辨率下的图片宽度 **/
	private int mBannerWidth = 458;

	private DeviceConfig mDeviceConfig;

	private int mGap = 20;

	/** 当当前单元超过这个数 就会remove掉最早的单元块 **/
	private final static int PAGE_COUNT = 5;

	/** 最新的单元块包含了几页的数据 **/
	private int mPage = 0;
	/** 最新的单元块最多尽量只包含这么多页的数据(当大于这个页数时会尝试对齐以便回收资源) **/
	private final static int MAIN_ITEM_MAX_PAGE = 2;

	private int mB;

	/** 补空白 **/
	private WaterwallBaseItem mSBView;

	/** 最顶端弹出的快捷方式 */
	private boolean menuCanShow;

	/** 一个3 7样式下标随机数，在0和1之间切换 **/
	private int mThreeSevenRa = 0;

	// private WeatherMainBiz weatherDataBiz;
	// private WeatherViewControl weatherViewControl;
	// private View weatherView;

	/** 顶部菜单 */
	// private LinearLayout mShortCutLayout;

	/** 查看是否停止了滚动 **/
	private AsyncTask<Void, Void, Integer> mSeeIsStopScrollTask;

	private Animator animatorTranslate, animatorTranslateTop, animatordis,
			animatordisTop;

	private boolean mIsSeeIsStopScrolling;

	private LayoutTransition mLayoutTransition;

	/** 记录加载和回收item的时间 **/
	private long timeT = 0;

	private Scroller mScroller;
	
	private WaterwallMain mWaterwallMain;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ConnectInternetLoadResult.HANDLER_WHAT_LOAD_OK:
				Tools.showLogA("取到数据了");

				mShowFlowNews = mWaterwallBiz.getShowFlowNews();

				ShowFlowNews show = new ShowFlowNews(10);
				show.addAll(mShowFlowNews);
				if (mShowFlowNews.getBannerInfo() != null) {
					show.add(mShowFlowNews.getBannerInfo());
				}
				if (mApp != null && show.size() > 0) {
					mApp.putToShowFlowNewsMap(show.get(0).getReco_id(), show);
				}

				if (mShowFlowNewinfo != null) {
					mShowFlowNews.add(0, mShowFlowNewinfo);
					mShowFlowNewinfo = null;
				}
				mSize += mShowFlowNews.size();

				seeIsStopScroll(0, mShowFlowNews);
				// add(0, mShowFlowNews);

				// hidePb();
				hideSetLy();
				// mWaterwallBiz.setLoading(false);

				// if (Math.abs(mLeftHeight - mRightHeight) > mDeviceConfig.h) {
				// mWaterwallBiz.loadNews();
				// if (mWaterwallMainItem != null) {
				// mWaterwallMainItem.showLoading();
				// }
				// time = System.currentTimeMillis();
				// }
				if (mWaterwallLayoutMain != null)
					mWaterwallLayoutMain.setVisibility(VISIBLE);

				mLayoutTransition.setAnimator(LayoutTransition.APPEARING,
						animatorTranslateTop);
				mLayoutTransition.setAnimator(LayoutTransition.DISAPPEARING,
						animatordisTop);
				mLayoutTransition.setDuration(300);
				break;
			case ConnectInternetLoadResult.HANDLER_WHAT_LINK_ERROR:
			case ConnectInternetLoadResult.HANDLER_WHAT_LINK_ERROR_2:
			case ConnectInternetLoadResult.HANDLER_WHAT_NO_MORE_DATA:
				if (mSize == 0) {
					showSetLy();
					// hidePb();
				}

				mWaterwallBiz.setLoading(false);
				if (mWaterwallMainItem != null) {
					mWaterwallMainItem.loadFail();
				}
				break;

			case ConnectInternetLoadResult.HANDLER_WHAT_NO_NET:
				if (mSize == 0) {
					showSetLy();
					// hidePb();
				}
				mWaterwallBiz.setLoading(false);
				if (mWaterwallMainItem != null) {
					mWaterwallMainItem.loadFail();
				}
				// ToastTools.showToast(mContext,
				// mResources.getString(R.string.net_error));
				break;
			case LOAD_CACHE_DATA:

				// 加载运行内存或者sd卡中的数据

				if (mShowFlowNews != null) {

					mSize += mShowFlowNews.size();
					add(0, mShowFlowNews);

					// hidePb();
					hideSetLy();

				}

				// mWaterwallLayoutMain.setVisibility(VISIBLE);

				break;

			case LOAD_IMG_ON_THE_SCREEN:

				// 手离开后 触发 加载当屏图片

				for (int i = 0; i < mWaterwallLayoutMain.getChildCount(); i++) {
					WaterwallMainItem mainItem = ((WaterwallMainItem) mWaterwallLayoutMain
							.getChildAt(i));
					if (mainItem != null) {
						mainItem.take(getScrollY(), mDeviceConfig.h);

					}
				}

				// 加载当屏之后的图片
				// for (int i = 0; i < mWaterwallLayoutMain.getChildCount();
				// i++) {
				// WaterwallMainItem mainItem = ((WaterwallMainItem)
				// mWaterwallLayoutMain
				// .getChildAt(i));
				// if (mainItem != null) {
				// mainItem.showTheImgBelowTheScreen(getScrollY(),
				// mDeviceConfig.h);
				// }
				// }

				break;

			case LOAD_WEATHER_RESULT:

				// WeatherCity weatherCity = (WeatherCity) msg.obj;
				// if (weatherCity != null) {
				// weatherViewControl.setUi(weatherCity);
				// } else {
				//
				// }

				break;
			case AUTO_SCROLL:
				if (canAutoScroll) {
					// mLayout.scrollBy(0, scrollS);
					smoothScrollBy(0, scrollS);

					for (int j = 0; j < 10; j++) {
						removeMessages(AUTO_SCROLL);
					}
					sendEmptyMessageDelayed(AUTO_SCROLL, autoScrollDelayedTime);
					if (NetWorkUtil.isWifiConnected(mContext)) {
						canLoadNextPage = true;
					} else {
						canLoadNextPage = false;
					}
					sendEmptyMessage(NEED_REMOVE_ITEMS);
				} else {
					canLoadNextPage = true;
				}
				break;

			case NEED_REMOVE_ITEMS:
				needRemoveTheViews();
				break;

			case REFRESH_WEATHER:

				setWeatherUi();
				break;
			}
		}
	};

	/** 标识是否可以自动滚动(条件为亮屏且未触摸屏幕超过10秒) */
	private boolean canAutoScroll = false;
	/** 自动滚动时间间隔 **/
	private static int autoScrollDelayedTime = 10;
	/** 是否能加载下一页 自动滚动时 如果是wifi 则可以自动滚动到差不多底部时加载下一页 **/
	private boolean canLoadNextPage = true;

	/** 移动的距离 **/
	public int scrollS = 1;

	/**
	 * 默认构造函数，此时默认展示的宽高为屏幕的宽高
	 * 
	 * @param context
	 */
	public WaterwallScrollView(Context context , LinearLayout detailLayout , LinearLayout dialogLayout,WaterwallMain  waterwallMain ) {
		this(context, 0, 0,detailLayout,dialogLayout,waterwallMain);
	}

	/**
	 * 当不需要全屏显示时，传入父容器的宽和高，已进行适配
	 * 
	 * @param context
	 * @param width
	 * @param height
	 */
	public WaterwallScrollView(Context context, int width, int height , LinearLayout detailLayout , LinearLayout dialogLayout,WaterwallMain  waterwallMain ) {
		super(context);
		this.mDeviceConfig = DeviceConfig.getInstance(context);
		if (width != 0)
			mDeviceConfig.setWidth(width);
		if (height != 0)
			mDeviceConfig.setHeight(height);
		init(context,detailLayout,dialogLayout,waterwallMain);
	}

	/** 头部快捷栏，天气，和瀑布流父容器 */
	private LinearLayout mLayout;

	private boolean dismissAnimEnd = true;

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		// Tools.showLogA("water onlayout");
		if (mWaterwallLayoutMain == null) {
			mWaterwallLayoutMain = (LinearLayout) findViewById(R.id.layout_main);

		}

		// if (mLayout == null) {
		// mLayout = (LinearLayout) findViewById(R.id.layout1);
		// mLayout.setLayoutTransition(mLayoutTransition);
		// }

		// if (this.mShortCutLayout == null) {
		// mShortCutLayout = (LinearLayout) findViewById(R.id.short_cut_layout);
		// initShortLayout(mShortCutLayout);
		// }
		if (mWaterwallSetLy == null) {
			mWaterwallSetLy = (LinearLayout) findViewById(R.id.waterwall_reset_ly);
			mWaterwallSetLy.setOnClickListener(this);
		}

		// if (weatherView == null) {
		// weatherView = findViewById(R.id.weather_ly);
		//
		// LinearLayout callMeLayout = (LinearLayout)
		// findViewById(R.id.call_me_layout);
		// LinearLayout weChatLayout = (LinearLayout)
		// findViewById(R.id.we_chat_layout);
		// callMeLayout.setOnClickListener(this);
		// weChatLayout.setOnClickListener(this);
		//
		// View
		// weatherContent=weatherView.findViewById(R.id.weather_content_ly);
		// weatherContent.setOnClickListener(this);
		// // weatherView.setOnClickListener(this);
		//
		// weatherViewControl = new WeatherViewControl(weatherView);
		// setWeatherUi();
		// // View view=findViewById(R.id.bangding_ly);
		// // BindViewControl bindViewControl=new BindViewControl(view,
		// mContext);
		// }
		
		super.onLayout(changed, l, t, r, b);

		// if (canAutoScroll) {
		// // 能自动滚动的时候全部在顶部不动
		// weatherView.bringToFront();
		// weatherView.layout(0, getScrollY(), mDeviceConfig.w, getScrollY()
		// + weatherView.getHeight());
		// }
	}

	private int c;

	/** 设置天气的Ui **/
	public void setWeatherUi() {
		// if (weatherViewControl != null) {
		// // weatherViewControl = new WeatherViewControl(weatherView);
		// WeatherCity weatherCity = WeatherDao.getInstance(mContext)
		// .getOneWeatherCity();
		// if (weatherCity != null) {
		// weatherViewControl.setUi(weatherCity);
		// }
		// }
	}

	private void init(Context context , LinearLayout detailLayout, LinearLayout dialogLayout,WaterwallMain  waterwallMain ) {
		this.mContext = context;
		this.mDetailLayout = detailLayout;
		this.mDialogLayout = dialogLayout;
		mWaterwallMain = waterwallMain;
		this.mResources = mContext.getResources();
		this.mScroller = new Scroller(context);
		this.mR = new Random();
		this.mWaterwallBiz = new WaterwallBiz(mContext, mHandler);
		this.mBannerInfos = new ArrayList<ShowFlowNewinfo>();
		this.mImgExtraGap = mResources
				.getInteger(R.integer.waterwall_img_item_extra_gap);

		this.mFlyshareBitmapManager = FlyshareBitmapManager.create(context);

		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.wpd_waterwall_main, this);

		if (WaterwallBiz.RESOLUTIONS.indexOf(mDeviceConfig.w + "") != -1) {

			this.mItemWidth = mResources
					.getInteger(R.integer.waterwall_item_width);
			this.mBannerWidth = mResources
					.getInteger(R.integer.waterwall_banner_width);
			this.mGap = mResources.getInteger(R.integer.waterwall_gap);
		} else {
			mShowFlowStyleConfigs = mWaterwallBiz.getShowFlowStyleConfigs();
			if (mShowFlowStyleConfigs != null) {
				this.mGap = mShowFlowStyleConfigs.GAP;
				this.mItemWidth = mDeviceConfig.w / 2
						- mShowFlowStyleConfigs.GAP / 2;
				this.mImgExtraGap = mShowFlowStyleConfigs.IMG_EXTRA_GAP;
				E = this.mImgExtraGap;

			} else {
				this.mItemWidth = mDeviceConfig.w / 2 - 9;
			}
			this.mBannerWidth = mDeviceConfig.w;

		}

		this.setOnTouchListener(onTouchListener);

		mSBView = new WaterwallSBItemView(mContext);
		LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(
				mItemWidth, 0);
		mSBView.setLayoutParams(itemParam);
//		Random r = new Random();
//		this.mSBView.setBackgroundResource(Const.WATERWALL_ITEM_BG_COLORS[r
//				.nextInt(Const.WATERWALL_ITEM_BG_COLORS.length)]);
//		mSBView.setBackgroundResource(R.color.waterwall_sb_item_background_color);

		SUB = mDeviceConfig.w ;
		MAY_REMOVE_GAP = mDeviceConfig.w * 4;

		animatorTranslate = ObjectAnimator.ofFloat(null, "translationY", 700f,
				0f).setDuration(800);
		animatorTranslate.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator anim) {
				View view = (View) ((ObjectAnimator) anim).getTarget();
				view.setTranslationY(0f);
			}
		});

		animatorTranslateTop = ObjectAnimator.ofFloat(null, "translationY",
				-120f, 0f);
		animatorTranslateTop.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator anim) {
				View view = (View) ((ObjectAnimator) anim).getTarget();
				view.setTranslationY(0f);

			}
		});

		animatordis = ObjectAnimator.ofFloat(null, "translationY", 0f, 900f);
		animatordis.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator anim) {
				View view = (View) ((ObjectAnimator) anim).getTarget();
				view.setTranslationY(0f);
				dismissAnimEnd = true;
			}
		});

		animatordisTop = ObjectAnimator
				.ofFloat(null, "translationY", 0f, -120f);
		animatordisTop.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator anim) {
				View view = (View) ((ObjectAnimator) anim).getTarget();
				view.setTranslationY(0f);
				Tools.showLogA("translationY 100 end:" + dismissAnimEnd);
				dismissAnimEnd = true;
			}
		});

		mLayoutTransition = new LayoutTransition();
		mLayoutTransition.setAnimator(LayoutTransition.APPEARING,
				animatorTranslate);
		mLayoutTransition.setAnimator(LayoutTransition.DISAPPEARING,
				animatordis);
		mLayoutTransition.setInterpolator(LayoutTransition.APPEARING,
				new AccelerateDecelerateInterpolator());
		mLayoutTransition.setDuration(500);
	}

	private void startApp(String packageName) {
		// 应用激活则打开答应应用
		if (packageName.contains(".")) {
			try {
				Intent intent = mContext.getPackageManager()
						.getLaunchIntentForPackage(packageName);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				mContext.startActivity(intent);
			} catch (Exception e) {
				ToastTools.showToast(mContext, "没有安装该应用！");
			}
		} else {
			ToastTools.showToast(mContext, "包名错误！");
		}
	}

	public int computeVerticalScrollOffset() {
		return super.computeVerticalScrollOffset();
	}

	/** 计算5 5个数，大于等于4的时候尽量下一次显示3 7布局 **/
	int mFFcount = 0;
	int mStyle = 0;

	/**
	 * 加载布局
	 * 
	 * @param k
	 * @param showFlowNews
	 */
	private void add(int k, final ShowFlowNews showFlowNews) {

		if (showFlowNews == null) {
			return;
		}

		// int size = showFlowNews.size();
		int styleIndex = 0;
		ShowFlowNewinfo bannerInfo = null;
		bannerInfo = showFlowNews.getBannerInfo();

		if (bannerInfo != null) {
			mBannerInfos.add(bannerInfo);
			showFlowNews.setBannerInfo(null);

		}

		if (mWaterwallMainItem != null) {
			mWaterwallMainItem.removeSBView();
		}

		if (Math.abs(mLeftHeight - mRightHeight) < 10) {

			// 开始一个新的单元块，有通廊时可以放入

			if (mWaterwallMainItem != null) {
				mWaterwallMainItem.hideBottom();
			}
			if (mBannerInfos.size() > 0) {
				bannerInfo = mBannerInfos.remove(0);
			}

			HashMap<String, ShowFlowNewinfo> showFlowNewinfoMap = new HashMap<String, ShowFlowNewinfo>();
			int style = 0;

			if (bannerInfo == null) {

				if (mStyle != ShowFlowStyles.STYLE_THREE_S) {

					ShowFlowNewinfo temp1 = null;
					ShowFlowNewinfo temp2 = null;
					ShowFlowNewinfo temp3 = null;

					int count = showFlowNews.size() - k;
					if (count >= 3) {

						for (int i = k; i < showFlowNews.size(); i++) {
							ShowFlowNewinfo temp = showFlowNews.get(i);
							if (StringTools.isNotEmpty(temp.getImgUrl())) {
								if (showFlowNewinfoMap
										.get(ShowFlowStyles.ThreeSItemKeyS.VERTICAL_KEY) == null) {

									if (temp.getIconHeight() >= temp
											.getIconWidht()) {
										showFlowNewinfoMap
												.put(ShowFlowStyles.ThreeSItemKeyS.VERTICAL_KEY,
														temp);
										temp1 = temp;
										continue;
									} else if (i == showFlowNews.size() - 1) {
										showFlowNewinfoMap
												.put(ShowFlowStyles.ThreeSItemKeyS.VERTICAL_KEY,
														temp);
										temp1 = temp;
										continue;
									}
								}
								if (showFlowNewinfoMap
										.get(ShowFlowStyles.ThreeSItemKeyS.HORIZONTAL_IMG_1_KEY) == null) {
									showFlowNewinfoMap
											.put(ShowFlowStyles.ThreeSItemKeyS.HORIZONTAL_IMG_1_KEY,
													temp);
									temp2 = temp;
									continue;
								}
								if (showFlowNewinfoMap
										.get(ShowFlowStyles.ThreeSItemKeyS.HORIZONTAL_IMG_2_KEY) == null
										&& temp3 == null) {
									showFlowNewinfoMap
											.put(ShowFlowStyles.ThreeSItemKeyS.HORIZONTAL_IMG_2_KEY,
													temp);
									temp3 = temp;
									continue;
								}
							} else {
								if (showFlowNewinfoMap
										.get(ShowFlowStyles.ThreeSItemKeyS.HORIZONTAL_TEXT_KEY) == null
										&& temp3 == null) {
									showFlowNewinfoMap
											.put(ShowFlowStyles.ThreeSItemKeyS.HORIZONTAL_TEXT_KEY,
													temp);
									temp3 = temp;

								}
							}

							if (temp1 != null && temp2 != null && temp3 != null) {
								break;
							}
						}

						if (temp1 != null && temp2 != null && temp3 != null) {
							mStyle = ShowFlowStyles.STYLE_THREE_S;
							style = ShowFlowStyles.STYLE_THREE_S;

							showFlowNews.remove(temp1);
							showFlowNews.remove(temp2);
							showFlowNews.remove(temp3);

							mFFcount = 0;
						}

					}

				} else if (mStyle != ShowFlowStyles.STYLE_THREE_SEVEN
						|| mFFcount > 1) {

					ShowFlowNewinfo showFlowNewinfo = null;
					ShowFlowNewinfo showFlowNewinfo2 = null;

					int count = showFlowNews.size() - k;
					if (count >= 2) {

						for (int i = k; i < showFlowNews.size(); i++) {
							ShowFlowNewinfo temp = showFlowNews.get(i);
							if (StringTools.isNotEmpty(temp.getImgUrl())) {

								if (showFlowNewinfoMap
										.get(ShowFlowStyles.ThreeSevenItemKeyS.THREE_KEY) == null
										&& showFlowNewinfoMap
												.get(ShowFlowStyles.ThreeSevenItemKeyS.THREE_TEXT_KEY) == null) {

									if (temp.getIconHeight() >= temp
											.getIconWidht()) {
										showFlowNewinfoMap
												.put(ShowFlowStyles.ThreeSevenItemKeyS.THREE_KEY,
														temp);
										showFlowNewinfo = temp;
										continue;
									} else if (i == showFlowNews.size() - 1) {
										showFlowNewinfoMap
												.put(ShowFlowStyles.ThreeSevenItemKeyS.THREE_KEY,
														temp);
										showFlowNewinfo = temp;
										continue;
									}

								}

								if (showFlowNewinfoMap
										.get(ShowFlowStyles.ThreeSevenItemKeyS.SEVEN_KEY) == null) {

									showFlowNewinfoMap
											.put(ShowFlowStyles.ThreeSevenItemKeyS.SEVEN_KEY,
													temp);
									showFlowNewinfo2 = temp;
									continue;
								}

							} else if (showFlowNewinfoMap
									.get(ShowFlowStyles.ThreeSevenItemKeyS.THREE_KEY) == null
									&& showFlowNewinfoMap
											.get(ShowFlowStyles.ThreeSevenItemKeyS.THREE_TEXT_KEY) == null) {

								showFlowNewinfoMap
										.put(ShowFlowStyles.ThreeSevenItemKeyS.THREE_TEXT_KEY,
												temp);
								showFlowNewinfo = temp;

							}

							if (showFlowNewinfo != null
									&& showFlowNewinfo2 != null) {
								break;
							}
						}

						if (showFlowNewinfo != null && showFlowNewinfo2 != null) {
							showFlowNews.remove(showFlowNewinfo);
							showFlowNews.remove(showFlowNewinfo2);

							mStyle = ShowFlowStyles.STYLE_THREE_SEVEN;
							style = ShowFlowStyles.STYLE_THREE_SEVEN;
							if (mThreeSevenRa == 0) {
								mThreeSevenRa = 1;
							} else {
								mThreeSevenRa = 0;
							}

							mFFcount = 0;
						}

					}
				}

			} else {
				showFlowNewinfoMap.put(
						ShowFlowStyles.BannerItemKeyS.BANNER_KEY, bannerInfo);
				mStyle = ShowFlowStyles.STYLE_BANNER;
				style = ShowFlowStyles.STYLE_BANNER;
				mFFcount = 0;
			}

			ShowFlowStyles styles = new ShowFlowStyles(style,
					showFlowNewinfoMap, mThreeSevenRa);

			mWaterwallMainItem = new WaterwallMainItem(mContext,
					this.mBannerWidth, this.mFlyshareBitmapManager,
					mWaterwallBiz, styles,mDetailLayout , mDialogLayout,mApp);

			// 设置子项被触发down监听，触发时得到此位置上面4个下面4个

			if (mWaterwallLayoutMain != null) {
				mWaterwallLayoutMain.addView(mWaterwallMainItem);
			}

			mLeftHeight = 0;
			mRightHeight = 0;
			mPage = 0;
		}

		mPage++; // 该单元块每加一页自加1

		// 加载每一个子块
		for (int i = k; i < showFlowNews.size(); i++) {

			int height = 0;
			ShowFlowNewinfo showFlowNewinfo = showFlowNews.get(i);

			if (k == showFlowNews.size() - 1) {
				mShowFlowNewinfo = showFlowNewinfo;
			} else {

				WaterwallBaseItem view = null;
				if (StringTools.isNotEmpty(showFlowNewinfo.getImgUrl())) {

					height = getActualHeight(showFlowNewinfo.getIconHeight(),
							showFlowNewinfo.getIconWidht());

					boolean flag = isTheStyleHeightBigger(mItemWidth, height);

					view = new WaterwallImgItem(mContext, showFlowNewinfo,
							mB++, mItemWidth, height,
							this.mFlyshareBitmapManager, mWaterwallBiz, flag,this.mDetailLayout , mDialogLayout,mApp);
					view.setActualHeight(height);

					if (flag) {
						if (mShowFlowStyleConfigs != null) {
							height += mShowFlowStyleConfigs.IMG_ABOVE_EXTRA_GAP;
						}

					} else {
						height += mImgExtraGap;
					}

				} else {

					styleIndex = getWaterwallTextItemStyleIndex(showFlowNewinfo);

					view = new WaterwallTextItem(mContext, styleIndex,
							showFlowNewinfo, mB++, mItemWidth, mWaterwallBiz ,mDetailLayout , mDialogLayout,mApp);
					height = mWaterwallBiz.getStyleHeights().get(styleIndex);
				}

				if (mLeftHeight <= mRightHeight) {
					mWaterwallMainItem.addViewToLeft(view);
					mLeftHeight += height;
					mViewLeft = view;

					mFFcount++;

				} else {
					mWaterwallMainItem.addViewToRight(view);
					mRightHeight += height;
					mViewRight = view;

					mFFcount++;

				}

				if (Math.abs(mLeftHeight - mRightHeight) < E * 3 / 2) {
					if (mLeftHeight <= mRightHeight) {
						if (mViewLeft != null) {
							mViewLeft.changeHeight(mRightHeight - mLeftHeight);
							mLeftHeight = mRightHeight;

							if (i != showFlowNews.size() - 1) {
								add(i + 1, showFlowNews);
							}
							break;
						}
					} else {
						if (mViewRight != null) {
							mViewRight.changeHeight(mLeftHeight - mRightHeight);
							mRightHeight = mLeftHeight;

							if (i != showFlowNews.size() - 1) {
								add(i + 1, showFlowNews);
							}
							break;
						}
					}

				}

				if (i == showFlowNews.size() - 1) {
					mViewLeft = null;
					mViewRight = null;
				}

				// 补空白
				if (i == showFlowNews.size() - 1) {
					if (mLeftHeight <= mRightHeight) {

						Random r = new Random();
						this.mSBView.setBackgroundResource(Const.WATERWALL_ITEM_BG_COLORS[r
								.nextInt(Const.WATERWALL_ITEM_BG_COLORS.length)]);
						
						LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mSBView
								.getLayoutParams();
						lp.height = mRightHeight - mLeftHeight - mGap;
						lp.width = mItemWidth;
						mSBView.setLayoutParams(lp);
						mWaterwallMainItem.addViewToLeft(mSBView);
						mWaterwallMainItem.setSBView(mSBView);

					} else {
						
						Random r = new Random();
						this.mSBView.setBackgroundResource(Const.WATERWALL_ITEM_BG_COLORS[r
								.nextInt(Const.WATERWALL_ITEM_BG_COLORS.length)]);
						
						LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mSBView
								.getLayoutParams();
						lp.height = mLeftHeight - mRightHeight - mGap;
						lp.width = mItemWidth;
						mSBView.setLayoutParams(lp);

						mWaterwallMainItem.addViewToRight(mSBView);
						mWaterwallMainItem.setSBView(mSBView);

					}
				}
			}

		}

	}

	/**
	 * 获得图片放到图片item控件上后的实际高度
	 * 
	 * @param height
	 * @param width
	 * @return
	 */
	private int getActualHeight(int height, int width) {
		int actualHeight = height;
		if (width != 0) {
			actualHeight = height * mItemWidth / width;
		}
		return actualHeight;
	}

	/**
	 * 获取字子块样式下标 , 为了保证第一个字符是汉字，放大之后才好看
	 * 
	 * @param showFlowNewinfo
	 * @return
	 */
	private int getWaterwallTextItemStyleIndex(ShowFlowNewinfo showFlowNewinfo) {
		int styleIndex;
		if (checkFirstText(showFlowNewinfo.getTitle())
				&& !checkFirstText(showFlowNewinfo.getSnippet())) {
			styleIndex = WaterwallTextItem.TITLE_ONLY[mR
					.nextInt(WaterwallTextItem.TITLE_ONLY.length)];
		} else if (!checkFirstText(showFlowNewinfo.getTitle())
				&& checkFirstText(showFlowNewinfo.getSnippet())) {
			styleIndex = WaterwallTextItem.CONTENT_ONLY[mR
					.nextInt(WaterwallTextItem.CONTENT_ONLY.length)];
		} else if (!checkFirstText(showFlowNewinfo.getTitle())
				&& !checkFirstText(showFlowNewinfo.getSnippet())) {
			styleIndex = WaterwallTextItem.STYLE02;
		} else {
			styleIndex = WaterwallTextItem.ALL_STYLES[mR
					.nextInt(WaterwallTextItem.ALL_STYLES.length)];
		}
		return styleIndex;
	}

	private PaintFlagsDrawFilter drawFilter = new PaintFlagsDrawFilter(0,
			Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.setDrawFilter(drawFilter);
		super.onDraw(canvas);
	}

	long time = 0;

	long timeOld = 0;

	/** 上次滑动的距离 */
	private int oldH;

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {

		long now = System.currentTimeMillis();

		if (mSize > 0
				&& mWaterwallLayoutMain != null
				&& (mWaterwallLayoutMain.getMeasuredHeight() - (getScrollY() + getHeight())) <= SUB
				&& now - time >= 1000 && !mWaterwallBiz.isLoading()
				&& canLoadNextPage) {

			mWaterwallBiz.loadNews();
			if (mWaterwallMainItem != null) {
				mWaterwallMainItem.showLoading();
			}

			time = now;

		}

		// if (canAutoScroll) {
		// // 能自动滚动的时候全部在顶部不动
		// weatherView.bringToFront();
		// weatherView.layout(0, getScrollY(), mDeviceConfig.w, getScrollY()
		// + weatherView.getHeight());
		// } else {
		// // 手动滚动分两种状态，往上滑动时，天气模块随着手势往上
		// if ((t - oldt) > 0) {
		//
		// } else {
		// // 往下滑动时，当滑到屏幕顶部时就不动了
		// int[] location = new int[2];
		// weatherView.getLocationInWindow(location);
		// if (location[1] > 30) {
		// Tools.showLogA("手动往下到了顶部");
		// int yy = getScrollY();
		// if (yy < 10)
		// yy = 0;
		// weatherView.layout(0, yy, mDeviceConfig.w,
		// yy + weatherView.getHeight());
		// } else {
		//
		// }
		// }
		// }
		super.onScrollChanged(l, t, oldl, oldt);
	}

	/**
	 * 移除单元块
	 * 
	 * @param i
	 */
	private boolean removeWaterwallMainItem(int i) {

		boolean flag = false;

		if (mWaterwallLayoutMain != null) {

			WaterwallMainItem wmi = (WaterwallMainItem) mWaterwallLayoutMain
					.getChildAt(i);

			if (wmi != null) {

				int height = wmi.getHeight();
				int y = getScrollY();
				int t = y - height;

				if (t > 300) {
					wmi.cancelAllLoadImg();
					wmi.removeItsAllViews();

					mWaterwallLayoutMain.removeViewAt(i);

					scrollTo(0, t);

					return true;
				} else {
					return false;
				}
			}

		}

		this.mFlyshareBitmapManager.clearMemeoryCache();
		return flag;
	}

	float oY;
	float nY;
	int n;
	float firstY;
	float mEx = 2.5f;

	/** 点击以下的4个item */
	private List<WaterwallBaseItem> baseItems;

	/** 通廊 */
	private WaterwallBannerItem tongLan;

	/** 点击以上的4个item */
	private List<WaterwallBaseItem> baseItemsTop;

	/** 记录第一次move的位置 */
	private int firstMoveY;

//	@Override
//	public boolean onTouchEvent(MotionEvent arg0) {
//		// int moveNowY = (int) arg0.getY();
//
//		switch (arg0.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//
//			canAutoScroll = false;
//			canLoadNextPage = true;
//			mHandler.removeMessages(AUTO_SCROLL);
//			break;
//		case MotionEvent.ACTION_UP:
//
//			break;
//		case MotionEvent.ACTION_CANCEL:
//			break;
//		case MotionEvent.ACTION_MOVE:
//			// Tools.showLogA("moveNowY:" + moveNowY + " firstMoveY:" +
//			// firstMoveY);
//			// int dis = moveNowY - firstMoveY;
//			// if (dis < -100) {
//			// if (VISIBLE == this.mShortCutLayout.getVisibility())
//			// this.mShortCutLayout.setVisibility(GONE);
//			// } else if (dis > 100) {
//			// if (menuCanShow && GONE == this.mShortCutLayout.getVisibility())
//			// {
//			// this.mShortCutLayout.setVisibility(VISIBLE);
//			// this.dismissAnimEnd = false;
//			// }
//			// }
//			//
//			canAutoScroll = false;
//			canLoadNextPage = true;
//			mHandler.removeMessages(AUTO_SCROLL);
//			break;
//		}
//		//
//		// if (!dismissAnimEnd)
//		// return false;
//		return super.onTouchEvent(arg0);
//	}

	

	// /**
	// * 获取点击位置上面四个和下面四个item
	// *
	// * @param clickY
	// */
	// private void getClickUpAndDownItem(int clickY) {
	// for (int i = 0; i < mWaterwallLayoutMain.getChildCount(); i++) {
	// WaterwallMainItem mainItem = (WaterwallMainItem) mWaterwallLayoutMain
	// .getChildAt(i);
	// LinearLayout left = mainItem.getmLeftLayout();
	// LinearLayout right = mainItem.getmRightLayout();
	// WaterwallBannerItem tongLan = mainItem.getmHorizontalLayout();
	//
	// if (mainItem.getTop() > getScrollY())
	// this.tongLan = tongLan;
	//
	// for (int a = 0; a < left.getChildCount(); a++) {
	// WaterwallBaseItem item1 = (WaterwallBaseItem) left
	// .getChildAt(a);
	// int itemTop = item1.getTop() + mainItem.getTop();
	// if (tongLan != null) {
	// itemTop += tongLan.getHeight();
	// }
	// // Tools.showLogA("左边itemTop："+itemTop+" clickY:"+clickY);
	// // 当前屏幕可见的
	// if (itemTop + item1.getHeight() + 400 >= getScrollY()) {
	// if (itemTop > clickY) {
	// if (baseItems.size() < 3)
	// baseItems.add(item1);
	// } else if (itemTop + 400 < clickY + item1.getHeight()) {
	// if (baseItemsTop.size() < 3) {
	// baseItemsTop.add(item1);
	// }
	// }
	// }
	//
	// if (baseItems.size() >= 3 && baseItemsTop.size() >= 3)
	// break;
	// // Tools.showLogA("左边循环了几次："+baseItems.size());
	// }
	// for (int b = 0; b < right.getChildCount(); b++) {
	//
	// WaterwallBaseItem item2 = (WaterwallBaseItem) right
	// .getChildAt(b);
	// int itemTop = item2.getTop() + mainItem.getTop();
	// if (tongLan != null) {
	// itemTop += tongLan.getHeight();
	// }
	// // Tools.showLogA("右边itemTop："+itemTop+" clickY:"+clickY);
	// if (itemTop + item2.getHeight() + 400 >= getScrollY()) {
	// if (itemTop > clickY) {
	// if (baseItems.size() < 6)
	// baseItems.add(item2);
	// } else if (itemTop + 400 < clickY + item2.getHeight()) {
	// if (baseItemsTop.size() < 6)
	// baseItemsTop.add(item2);
	// }
	// }
	//
	// if (baseItems.size() >= 6 && baseItemsTop.size() >= 6)
	// break;
	// // Tools.showLogA("右边循环了几次："+baseItems.size());
	// }
	// }
	// }

	OnTouchListener onTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch (event.getAction()) {
//			case MotionEvent.ACTION_DOWN:
//
//				break;
			case MotionEvent.ACTION_UP:

				if (NetWorkUtil.isNetworkAvailable(mContext)) {
					mHandler.sendEmptyMessageDelayed(LOAD_IMG_ON_THE_SCREEN,
							1000);
				}

				needRemoveTheViews();

				break;
			}
			return false;
		}

	};

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.waterwall_reset_ly) {
			if (mWaterwallBiz != null)
				mWaterwallBiz.loadLocalNews();
			// showPb();
			hideSetLy();
		}
		// break;
		// case R.id.weather_content_ly:
		// mContext.startActivity(new Intent(mContext, WeatherActivity.class));
		// break;
		// case R.id.call_me_layout:
		// startApp("com.dianhua.callme");
		// // Intent intent = new Intent(Intent.ACTION_CALL_BUTTON);
		// // mContext.startActivity(intent);
		// break;
		// case R.id.we_chat_layout:
		// startApp("com.tencent.mm");
		// break;
		// default:
		// break;
		// }
	}

	// private void showPb() {
	// if (mPb != null) {
	// mPb.setVisibility(View.VISIBLE);
	// }
	// }
	//
	// private void hidePb() {
	// if (mPb != null) {
	// mPb.setVisibility(View.GONE);
	// }
	// }

	private void showSetLy() {
		if (mWaterwallSetLy != null)
			mWaterwallSetLy.setVisibility(View.VISIBLE);
	}

	private void hideSetLy() {
		if (mWaterwallSetLy != null)
			mWaterwallSetLy.setVisibility(View.GONE);
	}

	/** 当主界面滑到瀑布流界面时会调用此方法加载数据,用于初始化第一次的数据 **/
	public void load() {
		if (mSize == 0) {
			if (mShowFlowNews != null && mShowFlowNews.size() > 0) {
				mHandler.sendEmptyMessageDelayed(LOAD_CACHE_DATA, 500);
			} else {
				if (mWaterwallBiz != null) {
					 mWaterwallBiz.loadLocalNews();
//					mWaterwallBiz.loadNews();
				}

			}
		}
	}

	/**
	 * 判断第一个字符是不是汉字
	 * 
	 * @param content
	 * @return 是返回ture
	 */
	private boolean checkFirstText(String content) {
		boolean flag = false;
		try {
			content = StringTools.removeHtmlTag(content).trim();

			if (StringTools.isNotEmpty(content)) {
				Pattern p = Pattern.compile("[\u4e00-\u9fa5]+$");
				Matcher m = p.matcher(content.substring(0, 1));
				flag = m.matches();
			}
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	@Override
	public void scrollTo(int x, int y) {
		if (Build.VERSION.SDK_INT > 16 && 0 == y) {
			y = 1;
		}
		super.scrollTo(x, y);
	}

	public void setmFlyshareBitmapManager(
			FlyshareBitmapManager flyshareBitmapManager) {
		if (flyshareBitmapManager != null)
			this.mFlyshareBitmapManager = flyshareBitmapManager;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		Tools.showLog(TAG, " scrollState:" + scrollState);

	}

	// public void dismissTopMenu() {
	// if (topMenu != null && topMenu.isShowing()) {
	// topMenu.dismiss();
	// }
	// }

	// public void setMenuCanShow(boolean menuCanShow) {
	// this.menuCanShow = menuCanShow;
	// }

	/**
	 * 
	 */
	public void startAnim(int fx, int fy) {
		Tools.showLogA("下面的大小：" + baseItems.size() + " 上面的大小："
				+ baseItemsTop.size() + " fy:" + fy);
		if (fy > 0) {
			if (baseItems != null) {
				for (WaterwallBaseItem baseItem : baseItems) {
					baseItem.startStretchingAnim(fx, fy);
				}
			}

		} else {
			if (baseItemsTop != null) {
				for (WaterwallBaseItem baseItem : baseItemsTop) {
					baseItem.startStretchingAnim(fx, fy);
				}
			}
		}

		if (this.tongLan != null)
			tongLan.startStretchingAnim(fx, fy);
	}

	/**
	 * 移除所有子view并回到初始化状态
	 */
	public void removeAllWaterwallViews() {
		if (this.mWaterwallLayoutMain != null) {
			for (int i = 0; i < mWaterwallLayoutMain.getChildCount(); i++) {
				WaterwallMainItem wmainitem = ((WaterwallMainItem) mWaterwallLayoutMain
						.getChildAt(i));
				wmainitem.cancelAllLoadImg();
				wmainitem.removeAllViews();
			}

			mWaterwallLayoutMain.removeAllViews();
			// showPb();
			hideSetLy();
			mLeftHeight = 0;
			mRightHeight = 0;
			mPage = 0;
			mSize = 0;

		}

		clearBitmapCache();
	}

	/**
	 * 移除所有子view并回到初始化状态,留下最后一页
	 */
	public void removeAllWaterwallViewsStayLast() {
		if (this.mWaterwallLayoutMain != null) {
			for (int i = 0; i < mWaterwallLayoutMain.getChildCount() - 1; i++) {
				WaterwallMainItem wmainitem = ((WaterwallMainItem) mWaterwallLayoutMain
						.getChildAt(i));
				wmainitem.cancelAllLoadImg();
				wmainitem.removeAllViews();
			}

			// mWaterwallLayoutMain.removeAllViews();
			// showPb();
			hideSetLy();
			// mLeftHeight = 0;
			// mRightHeight = 0;
			mPage = 0;
			mSize = 0;

		}

		clearBitmapCache();
	}

	public void clearBitmapCache() {
		if (mFlyshareBitmapManager != null)
			mFlyshareBitmapManager.clearMemeoryCache();
	}

	public void saveLastShowFlowNews() {
		try {
			mWaterwallBiz.saveLasrNews();
		} catch (IOException e) {
			// 保存最后一页失败
		}
	}

	@Override
	public void fling(int velocityY) {

		Tools.showLog(TAG, "fling velocityY: " + velocityY);

		super.fling(velocityY);
	}

	public void setmApp(FlyShareApplication mApp) {
		this.mApp = mApp;
	}

	/**
	 * 回收最早的views
	 */
	private void needRemoveTheViews() {

		long now = System.currentTimeMillis();

		if (now - timeT >= 1000
				&& NetWorkUtil.isNetworkAvailable(mContext)
				&& mWaterwallLayoutMain != null
				&& mWaterwallLayoutMain.getMeasuredHeight()
						- (getScrollY() + getHeight()) <= MAY_REMOVE_GAP) {

			int count = mWaterwallLayoutMain.getChildCount();
			if (count > PAGE_COUNT) {
				int j = 0;
				// 最新两块的高度和如果小于2500px，且倒数第3块的高度不大于3000px，则不回收掉倒数第三块
				// if (mWaterwallLayoutMain.getChildAt(count - 1).getHeight()
				// + mWaterwallLayoutMain.getChildAt(count - 2)
				// .getHeight() < 2500
				// && mWaterwallLayoutMain.getChildAt(count - 3)
				// .getHeight() < 3000) {
				// j = 1;
				// } else if (mWaterwallLayoutMain.getChildAt(count - 1)
				// .getHeight() > 4000) {
				// // 最新一个块过长 回收多一块
				// j = -1;
				// }
				for (int i = 0; i < mWaterwallLayoutMain.getChildCount()
						- PAGE_COUNT - j; i++) {
					boolean flag = removeWaterwallMainItem(0);
					timeT = now;
					if (!flag) {
						break;
					}
				}
			} else if (count == 2
					&& mWaterwallLayoutMain.getChildAt(1).getHeight() > 5000) {
				removeWaterwallMainItem(0);
				timeT = now;
			}

		}
	}

	public void hidenWaterFlow() {
		scrollTo(0, 0);
		mLayoutTransition.setAnimator(LayoutTransition.DISAPPEARING,
				animatordis);
		mLayoutTransition.setDuration(500);
		if (mWaterwallLayoutMain != null) {
			mWaterwallLayoutMain.setVisibility(GONE);
		}
		// if (mShortCutLayout != null)
		// mShortCutLayout.setVisibility(GONE);
	}

	public void showWaterFlow() {
		if (mWaterwallLayoutMain != null) {
			mLayoutTransition.setAnimator(LayoutTransition.APPEARING,
					animatorTranslate);
			mLayoutTransition.setDuration(500);
			mWaterwallLayoutMain.setVisibility(VISIBLE);
			// load();

			mLayoutTransition.setAnimator(LayoutTransition.APPEARING,
					animatorTranslateTop);
			mLayoutTransition.setAnimator(LayoutTransition.DISAPPEARING,
					animatordisTop);
			mLayoutTransition.setDuration(300);
		}
	}

	/**
	 * 判断滑动停止才addview
	 * 
	 * @param k
	 * @param showFlowNews
	 */
	private synchronized void seeIsStopScroll(int k,
			final ShowFlowNews showFlowNews) {

		if (mIsSeeIsStopScrolling) {
			return;
		}

		if (mSeeIsStopScrollTask != null) {
			mSeeIsStopScrollTask.cancel(true);
			mSeeIsStopScrollTask = null;
		}
		mSeeIsStopScrollTask = new AsyncTask<Void, Void, Integer>() {

			@Override
			protected Integer doInBackground(Void... params) {
				mIsSeeIsStopScrolling = true;

				int n = 0;
				int sy = getScrollY();

				while (n < 4) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					long now = System.currentTimeMillis();
					if (sy == getScrollY() && now - timeT >= 200) {

						n++;
						Tools.showLog(TAG,
								"return 0 ##########################  n :" + n
										+ " sy:" + sy + " getScrollY:"
										+ getScrollY());
						sy = getScrollY();
					} else {
						n = 0;
						sy = getScrollY();
					}

				}

				Tools.showLog(TAG, "return 0 ##########################");
				return 0;
			}

			@Override
			protected void onPostExecute(Integer result) {

				switch (result) {
				case 0:
					add(0, showFlowNews);
					mWaterwallBiz.setLoading(false);

					if (Math.abs(mLeftHeight - mRightHeight) > mDeviceConfig.h) {
						mWaterwallBiz.loadNews();
						if (mWaterwallMainItem != null) {
							mWaterwallMainItem.showLoading();
						}
						time = System.currentTimeMillis();
					}
					timeT = System.currentTimeMillis();
					break;

				default:
					break;
				}

				mIsSeeIsStopScrolling = false;

				super.onPostExecute(result);
			}

		};

		mSeeIsStopScrollTask.execute();
	}

	public boolean isMenuCanShow() {
		return menuCanShow;
	}

	public void setMenuCanShow(boolean menuCanShow) {
		this.menuCanShow = menuCanShow;
	}

	/**
	 * 统一决定字是否在图上面的计算方法
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public boolean isTheStyleHeightBigger(int width, int height) {
		boolean flag = false;

		if (height >= width) {
			flag = true;
		}

		return flag;
	}

	/**
	 * 暂停自动滚动
	 */
	public void pauseAutoScroll() {
		canAutoScroll = false;
		mHandler.removeMessages(AUTO_SCROLL);
	}

	/**
	 * 开始自动滚动
	 */
	public void startAutoScroll() {
		canAutoScroll = true;
		mHandler.sendEmptyMessageDelayed(AUTO_SCROLL, 5000);
		mHandler.sendEmptyMessageDelayed(REFRESH_WEATHER, 1000);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		mWaterwallMain.onTouchEvent(ev);
		return super.onTouchEvent(ev);
	}
	
}
