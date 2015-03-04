package com.inveno.piflow.entity.view.waterwall;

import java.util.HashMap;
import java.util.Random;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inveno.piflow.R;
import com.inveno.piflow.activity.FlyShareApplication;
import com.inveno.piflow.biz.waterwall.WaterwallBiz;
import com.inveno.piflow.entity.model.showflow.ShowFlowHardAd;
import com.inveno.piflow.entity.model.showflow.ShowFlowNewinfo;
import com.inveno.piflow.entity.model.showflow.ShowFlowStyleConfigs;
import com.inveno.piflow.entity.model.showflow.ShowFlowStyles;
import com.inveno.piflow.tools.bitmap.FlyshareBitmapManager;
import com.inveno.piflow.tools.commontools.Const;
import com.inveno.piflow.tools.commontools.DeviceConfig;
import com.inveno.piflow.tools.commontools.StartModuleUtil;
import com.inveno.piflow.tools.commontools.StringTools;

/**
 * 3 7样式，你懂的
 * 
 * @author mingsong.zhang
 * @date 2013-07-23
 * 
 */
public class WaterwallThreeSeven extends WaterwallBaseItem {

	/** 有一个是字 **/
	public static final int STYLE_HAS_TEXT_01 = 0;
	public static final int STYLE_HAS_TEXT_02 = 1;
	public static final int HAS_TEXT_COUNT = 2;

	/** 全图 **/
	public static final int STYLE_ALL_IMG_01 = 2;
	public static final int STYLE_ALL_IMG_02 = 3;
	public static final int ALL_IMG_COUNT = 2;

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private ShowFlowStyleConfigs mShowFlowStyleConfigs;
	private DeviceConfig mDeviceConfig;
	private FlyshareBitmapManager mFlyshareBitmapManager;
	private ShowFlowStyles mShowFlowStyles;
	private HashMap<String, ShowFlowNewinfo> mShowFlowNewinfoMap;

	/** groupview **/
	private LinearLayout mLayoutView;
	/** 竖 1 , 宽度小的那个 **/
	private View mVerticalViewImgOrText;
	/** 竖 2 是图 ,宽度大的那个 **/
	private View mVerticalView;

	/** 竖1的子view **/
	private ImageView mVerticalViewImgOrTextImageView;
	private ImageView mVerticalViewImgOrTextVideo;
	private TextView mVerticalViewImgOrTextTitle;
	private TextView mVerticalViewImgOrTextSource;
	private int mVerticalViewImgOrTextDefaultImg;

	/** 竖2的子view **/
	private ImageView mVerticalViewImageView;
	private ImageView mVerticalViewVideo;
	private TextView mVerticalViewTitle;
	private TextView mVerticalViewSource;
	private int mVerticalViewDefaultImg;

	/** 一个随机数 **/
	private int mR;

	private LinearLayout mLayout;
	private LinearLayout mDialogLayout;

	private FlyShareApplication mApp;

	public WaterwallThreeSeven(Context context,
			FlyshareBitmapManager flyshareBitmapManage, ShowFlowStyles styles,
			WaterwallBiz waterwallBiz, LinearLayout view,
			LinearLayout dialogLayout, FlyShareApplication app) {
		super(context);
		init(context, flyshareBitmapManage, styles, waterwallBiz, view,
				dialogLayout, app);
	}

	public WaterwallThreeSeven(Context context, AttributeSet attrs,
			FlyshareBitmapManager flyshareBitmapManage, ShowFlowStyles styles,
			WaterwallBiz waterwallBiz, LinearLayout view,
			LinearLayout dialogLayout, FlyShareApplication app) {
		super(context, attrs);
		init(context, flyshareBitmapManage, styles, waterwallBiz, view,
				dialogLayout, app);
	}

	private void init(Context context,
			FlyshareBitmapManager flyshareBitmapManage, ShowFlowStyles styles,
			WaterwallBiz waterwallBiz, LinearLayout view,
			LinearLayout dialogLayout, FlyShareApplication app) {
		this.mContext = context;
		this.mLayout = view;
		this.mDialogLayout = dialogLayout;
		this.mApp = app;
		if (waterwallBiz != null) {
			this.mShowFlowStyleConfigs = waterwallBiz.getShowFlowStyleConfigs();
		}
		this.mDeviceConfig = DeviceConfig.getInstance(context);
		this.mFlyshareBitmapManager = flyshareBitmapManage;
		this.mShowFlowStyles = styles;
		this.mShowFlowNewinfoMap = styles.getShowFlowNewinfoMap();

		this.mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		createViews();
	}

	private void createViews() {
		Random r = new Random();

		if (this.mShowFlowStyles.getShowFlowNewinfoMap().get(
				ShowFlowStyles.ThreeSevenItemKeyS.THREE_TEXT_KEY) != null) {
			mR = r.nextInt(HAS_TEXT_COUNT);

		} else {
			mR = r.nextInt(ALL_IMG_COUNT) + HAS_TEXT_COUNT;
		}

		switch (mR) {
		case STYLE_HAS_TEXT_01:

			this.mLayoutInflater.inflate(
					R.layout.wpg_waterwall_three_seven_has_text, this);
			this.mLayoutView = (LinearLayout) findViewById(R.id.waterwall_three_seven_has_text);
			this.mVerticalViewImgOrText = findViewById(R.id.waterwall_three_seven_has_text_text);
			this.mVerticalView = findViewById(R.id.waterwall_three_seven_has_text_img);

			LinearLayout.LayoutParams lp1 = (LayoutParams) this.mVerticalViewImgOrText
					.getLayoutParams();
			lp1.setMargins(0, 0, mShowFlowStyleConfigs.GAP, 0);
			this.mVerticalViewImgOrText.setLayoutParams(lp1);

			break;

		case STYLE_HAS_TEXT_02:

			this.mLayoutInflater.inflate(
					R.layout.wpg_waterwall_seven_three_has_text, this);
			this.mLayoutView = (LinearLayout) findViewById(R.id.waterwall_seven_three_has_text);
			this.mVerticalViewImgOrText = findViewById(R.id.waterwall_seven_three_has_text_text);
			this.mVerticalView = findViewById(R.id.waterwall_seven_three_has_text_img);

			LinearLayout.LayoutParams lp2 = (LayoutParams) this.mVerticalView
					.getLayoutParams();
			lp2.setMargins(0, 0, mShowFlowStyleConfigs.GAP, 0);
			this.mVerticalView.setLayoutParams(lp2);

			break;

		case STYLE_ALL_IMG_01:

			this.mLayoutInflater.inflate(
					R.layout.wpe_waterwall_three_seven_item, this);
			this.mLayoutView = (LinearLayout) findViewById(R.id.waterwall_three_seven_item);
			this.mVerticalViewImgOrText = findViewById(R.id.waterwall_three_seven_item_three);
			this.mVerticalView = findViewById(R.id.waterwall_three_seven_item_three_seven);

			this.mVerticalViewImgOrTextImageView = (ImageView) mVerticalViewImgOrText
					.findViewById(R.id.waterwall_the_img_item_img);
			this.mVerticalViewImgOrTextVideo = (ImageView) mVerticalViewImgOrText
					.findViewById(R.id.waterwall_the_img_item_video);

			LinearLayout.LayoutParams lp3 = (LayoutParams) this.mVerticalViewImgOrText
					.getLayoutParams();
			lp3.setMargins(0, 0, mShowFlowStyleConfigs.GAP, 0);
			this.mVerticalViewImgOrText.setLayoutParams(lp3);

			break;

		case STYLE_ALL_IMG_02:

			this.mLayoutInflater.inflate(
					R.layout.wpe_waterwal_lseven_three_item, this);
			this.mLayoutView = (LinearLayout) findViewById(R.id.waterwall_seven_three_item);
			this.mVerticalViewImgOrText = findViewById(R.id.waterwall_seven_three_item_three);
			this.mVerticalView = findViewById(R.id.waterwall_seven_three_item_seven);

			this.mVerticalViewImgOrTextImageView = (ImageView) mVerticalViewImgOrText
					.findViewById(R.id.waterwall_the_img_item_img);
			this.mVerticalViewImgOrTextVideo = (ImageView) mVerticalViewImgOrText
					.findViewById(R.id.waterwall_the_img_item_video);

			LinearLayout.LayoutParams lp4 = (LayoutParams) this.mVerticalView
					.getLayoutParams();
			lp4.setMargins(0, 0, mShowFlowStyleConfigs.GAP, 0);
			this.mVerticalView.setLayoutParams(lp4);

			break;
		}

		// 设置总高度
		LinearLayout.LayoutParams layoutViewLp = (LinearLayout.LayoutParams) mLayoutView
				.getLayoutParams();
		layoutViewLp.height = (int) (this.mDeviceConfig.w * 0.618);
		layoutViewLp.setMargins(0, 0, 0, mShowFlowStyleConfigs.GAP);
		mLayoutView.setLayoutParams(layoutViewLp);

		this.mVerticalViewImgOrTextTitle = (TextView) mVerticalViewImgOrText
				.findViewById(R.id.waterwall_the_img_item_title);
		this.mVerticalViewImgOrTextSource = (TextView) mVerticalViewImgOrText
				.findViewById(R.id.waterwall_the_img_item_source);

		this.mVerticalViewImageView = (ImageView) mVerticalView
				.findViewById(R.id.waterwall_the_img_item_img);
		this.mVerticalViewVideo = (ImageView) mVerticalView
				.findViewById(R.id.waterwall_the_img_item_video);
		this.mVerticalViewTitle = (TextView) mVerticalView
				.findViewById(R.id.waterwall_the_img_item_title);
		this.mVerticalViewSource = (TextView) mVerticalView
				.findViewById(R.id.waterwall_the_img_item_source);

		if (mR == STYLE_ALL_IMG_01 || mR == STYLE_ALL_IMG_02) {

			final ShowFlowNewinfo verticalNews = this.mShowFlowNewinfoMap
					.get(ShowFlowStyles.ThreeSevenItemKeyS.THREE_KEY);
			if (verticalNews != null) {

				final ShowFlowHardAd ad = verticalNews.getShowFlowHardAd();
				mVerticalViewImgOrTextDefaultImg = R.drawable.waterwall_list_default;
				if (ad != null) {
					if (ShowFlowHardAd.VIDEO.equals(ad.getCtype())) {
						mVerticalViewImgOrTextDefaultImg = 0;
						mVerticalViewImgOrTextVideo
								.setImageResource(R.drawable.water_video_icon);
						mVerticalViewImgOrTextVideo.setVisibility(View.VISIBLE);
					} else if (ShowFlowHardAd.VOICE.equals(ad.getCtype())) {
						mVerticalViewImgOrTextDefaultImg = 0;
						mVerticalViewImgOrTextVideo
								.setImageResource(R.drawable.water_voice_icon);
						mVerticalViewImgOrTextVideo.setVisibility(View.VISIBLE);
					}
				}

				this.mVerticalViewImgOrTextTitle.setTextSize(
						TypedValue.COMPLEX_UNIT_PX,
						mShowFlowStyleConfigs.TEXT_SIZE02);
				this.mVerticalViewImgOrTextTitle.setLineSpacing(
						mShowFlowStyleConfigs.IMG_TITLE_LINE_SPACING, 1.0f);

				this.mVerticalViewImgOrTextTitle.setText(verticalNews
						.getTitle());

				this.mVerticalViewImgOrTextSource
						.setText(getSourceAndTimeForVertical(verticalNews));

				this.mVerticalViewImgOrText
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								if (mContext != null) {
									if (ad != null && ad.getFirst() == 5) {
										openFirstAD(ad);
									} else {
										// StartModuleUtil.startWFNewsDetailAct(
										// mContext, verticalNews);

										StartModuleUtil
												.startWaterwallDetailView(
														mContext, verticalNews,
														mLayout, mDialogLayout,
														mApp);

									}
								}
							}
						});

			}

		} else {
			final ShowFlowNewinfo news = this.mShowFlowNewinfoMap
					.get(ShowFlowStyles.ThreeSevenItemKeyS.THREE_TEXT_KEY);

			if (news != null) {
				this.mVerticalViewImgOrTextTitle.setTextSize(
						TypedValue.COMPLEX_UNIT_PX,
						mShowFlowStyleConfigs.TEXT_SIZE01);
				this.mVerticalViewImgOrTextTitle.setLineSpacing(
						mShowFlowStyleConfigs.IMG_TITLE_LINE_SPACING, 1.0f);
				this.mVerticalViewImgOrTextTitle.setMaxLines(5);

				this.mVerticalViewImgOrTextTitle.setText(news.getTitle());

				this.mVerticalViewImgOrTextSource
						.setText(getSourceAndTimeForVertical(news));

				this.mVerticalViewImgOrText
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								if (mContext != null) {
									// StartModuleUtil.startWFNewsDetailAct(
									// mContext, news);
									StartModuleUtil.startWaterwallDetailView(
											mContext, news, mLayout,
											mDialogLayout, mApp);
								}
							}
						});

//				RelativeLayout textBg = (RelativeLayout) mVerticalViewImgOrText
//						.findViewById(R.id.waterwall_the_text_item);
				Random rColor = new Random();
				mVerticalViewImgOrText.setBackgroundResource(Const.WATERWALL_ITEM_BG_COLORS[rColor
						.nextInt(Const.WATERWALL_ITEM_BG_COLORS.length)]);
			}
		}

		final ShowFlowNewinfo verticalNews = this.mShowFlowNewinfoMap
				.get(ShowFlowStyles.ThreeSevenItemKeyS.SEVEN_KEY);
		if (verticalNews != null) {

			final ShowFlowHardAd ad = verticalNews.getShowFlowHardAd();
			mVerticalViewDefaultImg = R.drawable.waterwall_list_default;
			if (ad != null) {
				if (ShowFlowHardAd.VIDEO.equals(ad.getCtype())) {
					mVerticalViewDefaultImg = 0;
					mVerticalViewVideo
							.setImageResource(R.drawable.water_video_icon);
					mVerticalViewVideo.setVisibility(View.VISIBLE);
				} else if (ShowFlowHardAd.VOICE.equals(ad.getCtype())) {
					mVerticalViewDefaultImg = 0;
					mVerticalViewVideo
							.setImageResource(R.drawable.water_voice_icon);
					mVerticalViewVideo.setVisibility(View.VISIBLE);
				}
			}

			this.mVerticalViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					mShowFlowStyleConfigs.TEXT_SIZE02);
			this.mVerticalViewTitle.setLineSpacing(
					mShowFlowStyleConfigs.IMG_TITLE_LINE_SPACING, 1.0f);

			this.mVerticalViewTitle.setText(verticalNews.getTitle());

			this.mVerticalViewSource.setText(getSourceAndTime(verticalNews));

			this.mVerticalView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (mContext != null) {
						if (ad != null && ad.getFirst() == 5) {
							openFirstAD(ad);
						} else {
							// StartModuleUtil.startWFNewsDetailAct(mContext,
							// verticalNews);
							StartModuleUtil.startWaterwallDetailView(mContext,
									verticalNews, mLayout, mDialogLayout, mApp);

						}
					}
				}
			});

		}

		loadImgs();

	}

	/**
	 * 获取来源和时间组成的字符串
	 * 
	 * @param showFlowNewinfo
	 * @return
	 */
	private String getSourceAndTime(ShowFlowNewinfo showFlowNewinfo) {
		String strSource = showFlowNewinfo.getSrc();
		if (StringTools.isEmpty(strSource)) {
			ShowFlowHardAd ad = showFlowNewinfo.getShowFlowHardAd();
			if (ad != null) {
				strSource = ad.getCpname();
			}
		}

		if (StringTools.isNotEmpty(strSource)) {
			strSource = strSource + "    ";
		}

		return strSource
				+ StringTools.waterwallTime(showFlowNewinfo.getRtime());
	}

	/**
	 * 获取来源和时间组成的字符串
	 * 
	 * @param showFlowNewinfo
	 * @return
	 */
	private String getSourceAndTimeForVertical(ShowFlowNewinfo showFlowNewinfo) {
		String strSource = showFlowNewinfo.getSrc();
		String time = StringTools.waterwallTime(showFlowNewinfo.getRtime());
		if (StringTools.isEmpty(strSource)) {
			ShowFlowHardAd ad = showFlowNewinfo.getShowFlowHardAd();
			if (ad != null) {
				strSource = ad.getCpname();
			}
		}

		if (StringTools.isNotEmpty(strSource)) {

			if ((strSource + time).length() >= 9) {
				strSource = strSource + "\n";
			} else {
				strSource = strSource + "    ";
			}
		}

		return strSource + time;
	}

	@Override
	public void changeHeight(int height) {

	}

	@Override
	public void setActualHeight(int height) {

	}

	@Override
	public void changeMargin(int left, int top, int right, int bottom) {
		// LinearLayout.LayoutParams layoutViewLp = (LinearLayout.LayoutParams)
		// mLayoutView
		// .getLayoutParams();
		// layoutViewLp.setMargins(0, 0, 0, mShowFlowStyleConfigs.GAP+bottom);
		// mLayoutView.setLayoutParams(layoutViewLp);
	}

	@Override
	public void getTheViewOnTheScreen(int sub, int windowHeight) {
		loadImgs();

	}

	/**
	 * 加载图片
	 */
	private void loadImgs() {
		ShowFlowNewinfo verticalNews = this.mShowFlowNewinfoMap
				.get(ShowFlowStyles.ThreeSevenItemKeyS.SEVEN_KEY);
		if (verticalNews != null) {

			if (!FlyshareBitmapManager.LOAD_BMP_OK
					.equals(this.mVerticalViewImageView
							.getTag(R.string.load_bitmap_key))) {
				this.mFlyshareBitmapManager.displayForFlow(
						this.mVerticalViewImageView, verticalNews.getImgUrl()
								+ "&width=" + this.mDeviceConfig.w * 2 / 3 * 3
								/ 2, mVerticalViewDefaultImg,
						mVerticalViewDefaultImg, mDeviceConfig.w * 2 / 3,
						mDeviceConfig.w * 2 / 3, mDeviceConfig.w / 6, false);
			}

			// Tools.showLog("WaterwallScrollView",
			// " title : " + verticalNews.getTitle());

		}

		if (mR == STYLE_ALL_IMG_01 || mR == STYLE_ALL_IMG_02) {
			ShowFlowNewinfo verticalNewsImgOrText = this.mShowFlowNewinfoMap
					.get(ShowFlowStyles.ThreeSevenItemKeyS.THREE_KEY);
			if (verticalNewsImgOrText != null) {

				if (!FlyshareBitmapManager.LOAD_BMP_OK
						.equals(this.mVerticalViewImgOrTextImageView
								.getTag(R.string.load_bitmap_key))) {
					this.mFlyshareBitmapManager.displayForFlow(
							this.mVerticalViewImgOrTextImageView,
							verticalNewsImgOrText.getImgUrl() + "&width="
									+ this.mDeviceConfig.w / 3 * 3 / 2
									+ "&height=" + mDeviceConfig.w * 2 / 3 * 3
									/ 2, mVerticalViewImgOrTextDefaultImg,
							mVerticalViewImgOrTextDefaultImg,
							mDeviceConfig.w / 3, mDeviceConfig.w * 2 / 3,
							mDeviceConfig.w / 6, false);

					// Tools.showLog(
					// "WaterwallScrollView",
					// " title : "
					// + verticalNewsImgOrText.getTitle()
					// + " tag : "
					// + this.mVerticalViewImgOrTextImageView
					// .getTag(R.string.load_bitmap_key));
				}

				// Tools.showLog(
				// "WaterwallScrollView",
				// " title : "
				// + verticalNewsImgOrText.getTitle()
				// + " tag : "
				// + this.mVerticalViewImgOrTextImageView
				// .getTag(R.string.load_bitmap_key));

			}
		}

	}

	@Override
	public void loadImg() {
		loadImgs();
	}

	@Override
	public void cancelLoadImg() {

		FlyshareBitmapManager.cancelWork(this.mVerticalViewImageView);
		FlyshareBitmapManager.cancelWork(this.mVerticalViewImgOrTextImageView);

	}

}
