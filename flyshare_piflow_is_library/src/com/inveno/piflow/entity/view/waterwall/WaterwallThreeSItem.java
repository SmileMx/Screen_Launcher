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
 * 3个，"旧"字形布局
 * 
 * @author mingsong.zhang
 * @date 2013-08-16
 * 
 */
public class WaterwallThreeSItem extends WaterwallBaseItem {

	/** 横的样式是字 **/
	public static final int HAS_TEXT_01_TOP = 0;
	public static final int HAS_TEXT_01_BOTTOM = 1;
	public static final int HAS_TEXT_02_TOP = 2;
	public static final int HAS_TEXT_02_BOTTOM = 3;
	/** 竖的样式是字 **/
	public static final int HAS_TEXT_05_VERTICAL = 4;
	public static final int HAS_TEXT_06_VERTICAL = 5;
	/** 带有字块的样式个数，与上面个数相同 **/
	public static final int HAS_TEXT_STYLES_COUNT = 6;

	public static final int ALL_IMG_01 = 6;
	public static final int ALL_IMG_02 = 7;
	/** 全是图片的样式个数，与上面个数相同 **/
	public static final int ALL_IMG_STYLES_COUNT = 2;

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private ShowFlowStyleConfigs mShowFlowStyleConfigs;
	private DeviceConfig mDeviceConfig;
	private FlyshareBitmapManager mFlyshareBitmapManager;
	private ShowFlowStyles mShowFlowStyles;
	private HashMap<String, ShowFlowNewinfo> mShowFlowNewinfoMap;

	/** groupview **/
	private LinearLayout mLayoutView;
	/** 竖 (在样式HAS_TEXT_05_VERTICAL和HAS_TEXT_06_VERTICAL中是横) **/
	private View mVerticalView;
	/** 横1 是图 也可能是字(在样式HAS_TEXT_05_VERTICAL和HAS_TEXT_06_VERTICAL中是竖) **/
	private View mHorizontalViewImgOrText;
	/** 横2 **/
	private View mHorizontalViewImg;

	/** 竖的子view **/
	private ImageView mVerticalViewImageView;
	private ImageView mVerticalViewVideo;
	private TextView mVerticalViewTitle;
	private TextView mVerticalViewSource;
	private int mVerticalViewDefaultImg;

	/** 横1的子view **/
	private ImageView mHorizontalViewImgOrTextImageView;// 如果是字块，则该view是null
	private ImageView mHorizontalViewImgOrTextVideo;// 如果是字块，则该view是null
	private TextView mHorizontalViewImgOrTextTitle;
	private TextView mHorizontalViewImgOrTextSource;
	private int mHorizontalViewImgOrTextDefaultImg;

	/** 横2的子view **/
	private ImageView mHorizontalViewImgImageView;
	private ImageView mHorizontalViewImgVideo;
	private TextView mHorizontalViewImgTitle;
	private TextView mHorizontalViewImgSource;
	private int mHorizontalViewImgDefaultImg;

	/** 随机数 **/
	private int mR;
	
	private LinearLayout mLayout;
	private LinearLayout mDialogLayout;
	
	private FlyShareApplication mApp;

	public WaterwallThreeSItem(Context context,
			FlyshareBitmapManager flyshareBitmapManage, ShowFlowStyles styles,
			WaterwallBiz waterwallBiz , LinearLayout view, LinearLayout dialogLayout, FlyShareApplication app) {
		super(context);
		init(context, flyshareBitmapManage, styles, waterwallBiz,view,dialogLayout,app);
	}

	public WaterwallThreeSItem(Context context, AttributeSet attrs,
			FlyshareBitmapManager flyshareBitmapManage, ShowFlowStyles styles,
			WaterwallBiz waterwallBiz , LinearLayout view, LinearLayout dialogLayout, FlyShareApplication app) {
		super(context, attrs);
		init(context, flyshareBitmapManage, styles, waterwallBiz,view,dialogLayout,app);
	}

	private void init(Context context,
			FlyshareBitmapManager flyshareBitmapManage, ShowFlowStyles styles,
			WaterwallBiz waterwallBiz , LinearLayout view, LinearLayout dialogLayout, FlyShareApplication app) {
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

	/**
	 * 初始化views
	 */
	private void createViews() {
		Random r = new Random();

		if (this.mShowFlowStyles.getShowFlowNewinfoMap().get(
				ShowFlowStyles.ThreeSItemKeyS.HORIZONTAL_TEXT_KEY) != null) {
			mR = r.nextInt(HAS_TEXT_STYLES_COUNT);

		} else {
			mR = r.nextInt(ALL_IMG_STYLES_COUNT) + HAS_TEXT_STYLES_COUNT;
		}

		switch (mR) {
		case HAS_TEXT_01_TOP:

			this.mLayoutInflater.inflate(
					R.layout.wpg_waterwall_three_has_text_01, this);
			this.mLayoutView = (LinearLayout) findViewById(R.id.waterwall_three_has_text_layout_01);
			this.mVerticalView = findViewById(R.id.waterwall_three_has_text_01_vertical);
			this.mHorizontalViewImgOrText = findViewById(R.id.waterwall_three_has_text_01_horizontal_text);
			this.mHorizontalViewImg = findViewById(R.id.waterwall_three_has_text_01_horizontal_img);

			LinearLayout.LayoutParams vlp = (LinearLayout.LayoutParams) this.mVerticalView
					.getLayoutParams();
			vlp.setMargins(0, 0, this.mShowFlowStyleConfigs.GAP, 0);
			this.mVerticalView.setLayoutParams(vlp);

			LinearLayout.LayoutParams hTLp = (LinearLayout.LayoutParams) this.mHorizontalViewImgOrText
					.getLayoutParams();
			hTLp.setMargins(0, 0, 0, this.mShowFlowStyleConfigs.GAP / 2);
			this.mHorizontalViewImgOrText.setLayoutParams(hTLp);

			LinearLayout.LayoutParams hILp = (LinearLayout.LayoutParams) this.mHorizontalViewImg
					.getLayoutParams();
			hILp.setMargins(0, this.mShowFlowStyleConfigs.GAP / 2, 0, 0);
			this.mHorizontalViewImg.setLayoutParams(hILp);

			break;

		case HAS_TEXT_01_BOTTOM:

			this.mLayoutInflater.inflate(
					R.layout.wpg_waterwall_three_has_text_02, this);
			this.mLayoutView = (LinearLayout) findViewById(R.id.waterwall_three_has_text_layout_02);
			this.mVerticalView = findViewById(R.id.waterwall_three_has_text_02_vertical);
			this.mHorizontalViewImgOrText = findViewById(R.id.waterwall_three_has_text_02_horizontal_text);
			this.mHorizontalViewImg = findViewById(R.id.waterwall_three_has_text_02_horizontal_img);

			LinearLayout.LayoutParams vlp2 = (LinearLayout.LayoutParams) this.mVerticalView
					.getLayoutParams();
			vlp2.setMargins(0, 0, this.mShowFlowStyleConfigs.GAP, 0);
			this.mVerticalView.setLayoutParams(vlp2);

			LinearLayout.LayoutParams hTLp2 = (LinearLayout.LayoutParams) this.mHorizontalViewImgOrText
					.getLayoutParams();
			hTLp2.setMargins(0, this.mShowFlowStyleConfigs.GAP / 2, 0, 0);
			this.mHorizontalViewImgOrText.setLayoutParams(hTLp2);

			LinearLayout.LayoutParams hILp2 = (LinearLayout.LayoutParams) this.mHorizontalViewImg
					.getLayoutParams();
			hILp2.setMargins(0, 0, 0, this.mShowFlowStyleConfigs.GAP / 2);
			this.mHorizontalViewImg.setLayoutParams(hILp2);

			break;

		case HAS_TEXT_02_TOP:

			this.mLayoutInflater.inflate(
					R.layout.wpg_waterwall_three_has_text_03, this);
			this.mLayoutView = (LinearLayout) findViewById(R.id.waterwall_three_has_text_layout_03);
			this.mVerticalView = findViewById(R.id.waterwall_three_has_text_03_vertical);
			this.mHorizontalViewImgOrText = findViewById(R.id.waterwall_three_has_text_03_horizontal_text);
			this.mHorizontalViewImg = findViewById(R.id.waterwall_three_has_text_03_horizontal_img);

			LinearLayout.LayoutParams hTLp3 = (LinearLayout.LayoutParams) this.mHorizontalViewImgOrText
					.getLayoutParams();
			hTLp3.setMargins(0, 0, this.mShowFlowStyleConfigs.GAP,
					this.mShowFlowStyleConfigs.GAP / 2);
			this.mHorizontalViewImgOrText.setLayoutParams(hTLp3);

			LinearLayout.LayoutParams hILp3 = (LinearLayout.LayoutParams) this.mHorizontalViewImg
					.getLayoutParams();
			hILp3.setMargins(0, this.mShowFlowStyleConfigs.GAP / 2,
					this.mShowFlowStyleConfigs.GAP, 0);
			this.mHorizontalViewImg.setLayoutParams(hILp3);

			break;

		case HAS_TEXT_02_BOTTOM:

			this.mLayoutInflater.inflate(
					R.layout.wpg_waterwall_three_has_text_04, this);
			this.mLayoutView = (LinearLayout) findViewById(R.id.waterwall_three_has_text_layout_04);
			this.mVerticalView = findViewById(R.id.waterwall_three_has_text_04_vertical);
			this.mHorizontalViewImgOrText = findViewById(R.id.waterwall_three_has_text_04_horizontal_text);
			this.mHorizontalViewImg = findViewById(R.id.waterwall_three_has_text_04_horizontal_img);

			LinearLayout.LayoutParams hTLp4 = (LinearLayout.LayoutParams) this.mHorizontalViewImgOrText
					.getLayoutParams();
			hTLp4.setMargins(0, this.mShowFlowStyleConfigs.GAP / 2,
					this.mShowFlowStyleConfigs.GAP, 0);
			this.mHorizontalViewImgOrText.setLayoutParams(hTLp4);

			LinearLayout.LayoutParams hILp4 = (LinearLayout.LayoutParams) this.mHorizontalViewImg
					.getLayoutParams();
			hILp4.setMargins(0, 0, this.mShowFlowStyleConfigs.GAP,
					this.mShowFlowStyleConfigs.GAP / 2);
			this.mHorizontalViewImg.setLayoutParams(hILp4);

			break;

		case HAS_TEXT_05_VERTICAL:

			this.mLayoutInflater.inflate(
					R.layout.wpg_waterwall_three_has_text_05, this);
			this.mLayoutView = (LinearLayout) findViewById(R.id.waterwall_three_has_text_layout_05);
			this.mVerticalView = findViewById(R.id.waterwall_three_has_text_05_horizontal_img_top);
			this.mHorizontalViewImgOrText = findViewById(R.id.waterwall_three_has_text_05_vertical_text);
			this.mHorizontalViewImg = findViewById(R.id.waterwall_three_has_text_05_horizontal_img_bottom);

			LinearLayout.LayoutParams vlp5 = (LinearLayout.LayoutParams) this.mVerticalView
					.getLayoutParams();
			vlp5.setMargins(0, 0, 0, this.mShowFlowStyleConfigs.GAP / 2);
			this.mVerticalView.setLayoutParams(vlp5);

			LinearLayout.LayoutParams hTLp5 = (LinearLayout.LayoutParams) this.mHorizontalViewImgOrText
					.getLayoutParams();
			hTLp5.setMargins(0, 0, this.mShowFlowStyleConfigs.GAP, 0);
			this.mHorizontalViewImgOrText.setLayoutParams(hTLp5);

			LinearLayout.LayoutParams hILp5 = (LinearLayout.LayoutParams) this.mHorizontalViewImg
					.getLayoutParams();
			hILp5.setMargins(0, this.mShowFlowStyleConfigs.GAP / 2, 0, 0);
			this.mHorizontalViewImg.setLayoutParams(hILp5);

			break;

		case HAS_TEXT_06_VERTICAL:

			this.mLayoutInflater.inflate(
					R.layout.wpg_waterwall_three_has_text_06, this);
			this.mLayoutView = (LinearLayout) findViewById(R.id.waterwall_three_has_text_layout_06);
			this.mVerticalView = findViewById(R.id.waterwall_three_has_text_06_horizontal_img_top);
			this.mHorizontalViewImgOrText = findViewById(R.id.waterwall_three_has_text_06_vertical_text);
			this.mHorizontalViewImg = findViewById(R.id.waterwall_three_has_text_06_horizontal_img_bottom);

			LinearLayout.LayoutParams vlp6 = (LinearLayout.LayoutParams) this.mVerticalView
					.getLayoutParams();
			vlp6.setMargins(0, 0, this.mShowFlowStyleConfigs.GAP,
					this.mShowFlowStyleConfigs.GAP / 2);
			this.mVerticalView.setLayoutParams(vlp6);

			LinearLayout.LayoutParams hILp6 = (LinearLayout.LayoutParams) this.mHorizontalViewImg
					.getLayoutParams();
			hILp6.setMargins(0, this.mShowFlowStyleConfigs.GAP / 2,
					this.mShowFlowStyleConfigs.GAP, 0);
			this.mHorizontalViewImg.setLayoutParams(hILp6);

			break;

		case ALL_IMG_01:
			this.mLayoutInflater.inflate(
					R.layout.wpg_waterwall_three_all_img_01, this);
			this.mLayoutView = (LinearLayout) findViewById(R.id.waterwall_three_all_img_01_layout);
			this.mVerticalView = findViewById(R.id.waterwall_three_all_img_01_vertical);
			this.mHorizontalViewImgOrText = findViewById(R.id.waterwall_three_all_img_01_top_horizontal);
			this.mHorizontalViewImg = findViewById(R.id.waterwall_three_all_img_01_bottom_horizontal);

			this.mHorizontalViewImgOrTextImageView = (ImageView) this.mHorizontalViewImgOrText
					.findViewById(R.id.waterwall_the_img_item_img);
			this.mHorizontalViewImgOrTextVideo = (ImageView) this.mHorizontalViewImgOrText
					.findViewById(R.id.waterwall_the_img_item_video);

			LinearLayout.LayoutParams verticalViewLp = (LinearLayout.LayoutParams) this.mVerticalView
					.getLayoutParams();
			verticalViewLp.setMargins(0, 0, this.mShowFlowStyleConfigs.GAP, 0);
			this.mVerticalView.setLayoutParams(verticalViewLp);

			LinearLayout.LayoutParams horizontalViewImgOrTextLp = (LinearLayout.LayoutParams) this.mHorizontalViewImgOrText
					.getLayoutParams();
			horizontalViewImgOrTextLp.setMargins(0, 0, 0,
					this.mShowFlowStyleConfigs.GAP / 2);
			this.mHorizontalViewImgOrText
					.setLayoutParams(horizontalViewImgOrTextLp);

			LinearLayout.LayoutParams horizontalViewImgLp = (LinearLayout.LayoutParams) this.mHorizontalViewImg
					.getLayoutParams();
			horizontalViewImgLp.setMargins(0,
					this.mShowFlowStyleConfigs.GAP / 2, 0, 0);
			this.mHorizontalViewImg.setLayoutParams(horizontalViewImgLp);

			break;

		case ALL_IMG_02:

			this.mLayoutInflater.inflate(
					R.layout.wpg_waterwall_three_all_img_02, this);
			this.mLayoutView = (LinearLayout) findViewById(R.id.waterwall_three_all_img_02_layout);
			this.mVerticalView = findViewById(R.id.waterwall_three_all_img_02_vertical);
			this.mHorizontalViewImgOrText = findViewById(R.id.waterwall_three_all_img_02_top_horizontal);
			this.mHorizontalViewImg = findViewById(R.id.waterwall_three_all_img_02_bottom_horizontal);

			this.mHorizontalViewImgOrTextImageView = (ImageView) this.mHorizontalViewImgOrText
					.findViewById(R.id.waterwall_the_img_item_img);
			this.mHorizontalViewImgOrTextVideo = (ImageView) this.mHorizontalViewImgOrText
					.findViewById(R.id.waterwall_the_img_item_video);

			LinearLayout.LayoutParams horizontalViewImgOrTextLp2 = (LinearLayout.LayoutParams) this.mHorizontalViewImgOrText
					.getLayoutParams();
			horizontalViewImgOrTextLp2.setMargins(0, 0,
					this.mShowFlowStyleConfigs.GAP,
					this.mShowFlowStyleConfigs.GAP / 2);
			this.mHorizontalViewImgOrText
					.setLayoutParams(horizontalViewImgOrTextLp2);

			LinearLayout.LayoutParams horizontalViewImgLp2 = (LinearLayout.LayoutParams) this.mHorizontalViewImg
					.getLayoutParams();
			horizontalViewImgLp2.setMargins(0,
					this.mShowFlowStyleConfigs.GAP / 2,
					this.mShowFlowStyleConfigs.GAP, 0);
			this.mHorizontalViewImg.setLayoutParams(horizontalViewImgLp2);

			break;
		}

		// 设置总高度
		LinearLayout.LayoutParams layoutViewLp = (LinearLayout.LayoutParams) mLayoutView
				.getLayoutParams();
		layoutViewLp.height = (int) (this.mDeviceConfig.w * 0.77);
		layoutViewLp.setMargins(0, 0, 0, mShowFlowStyleConfigs.GAP);
		mLayoutView.setLayoutParams(layoutViewLp);

		this.mVerticalViewImageView = (ImageView) mVerticalView
				.findViewById(R.id.waterwall_the_img_item_img);
		this.mVerticalViewVideo = (ImageView) mVerticalView
				.findViewById(R.id.waterwall_the_img_item_video);
		this.mVerticalViewTitle = (TextView) mVerticalView
				.findViewById(R.id.waterwall_the_img_item_title);
		this.mVerticalViewSource = (TextView) mVerticalView
				.findViewById(R.id.waterwall_the_img_item_source);

		this.mHorizontalViewImgOrTextTitle = (TextView) mHorizontalViewImgOrText
				.findViewById(R.id.waterwall_the_img_item_title);
		this.mHorizontalViewImgOrTextSource = (TextView) mHorizontalViewImgOrText
				.findViewById(R.id.waterwall_the_img_item_source);

		this.mHorizontalViewImgImageView = (ImageView) mHorizontalViewImg
				.findViewById(R.id.waterwall_the_img_item_img);
		this.mHorizontalViewImgVideo = (ImageView) mHorizontalViewImg
				.findViewById(R.id.waterwall_the_img_item_video);
		this.mHorizontalViewImgTitle = (TextView) mHorizontalViewImg
				.findViewById(R.id.waterwall_the_img_item_title);
		this.mHorizontalViewImgSource = (TextView) mHorizontalViewImg
				.findViewById(R.id.waterwall_the_img_item_source);

		// 竖
		final ShowFlowNewinfo verticalNews = this.mShowFlowNewinfoMap
				.get(ShowFlowStyles.ThreeSItemKeyS.VERTICAL_KEY);
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

			this.mVerticalViewSource
					.setText(getSourceAndTimeForVertical(verticalNews));

			this.mVerticalView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (mContext != null) {
						if (ad != null && ad.getFirst() == 5) {
							openFirstAD(ad);
						} else {
//							StartModuleUtil.startWFNewsDetailAct(mContext,
//									verticalNews);
							
							StartModuleUtil.startWaterwallDetailView(mContext, verticalNews, mLayout,mDialogLayout,mApp);
						}
					}
				}
			});

		}

		// 横1
		if (mR == ALL_IMG_01 || mR == ALL_IMG_02) {
			final ShowFlowNewinfo horizontalNews = this.mShowFlowNewinfoMap
					.get(ShowFlowStyles.ThreeSItemKeyS.HORIZONTAL_IMG_2_KEY);
			if (horizontalNews != null) {

				final ShowFlowHardAd ad = horizontalNews.getShowFlowHardAd();
				mHorizontalViewImgOrTextDefaultImg = R.drawable.waterwall_list_default;
				if (ad != null) {
					if (ShowFlowHardAd.VIDEO.equals(ad.getCtype())) {
						mHorizontalViewImgOrTextDefaultImg = 0;
						mHorizontalViewImgOrTextVideo
								.setImageResource(R.drawable.water_video_icon);
						mHorizontalViewImgOrTextVideo
								.setVisibility(View.VISIBLE);
					} else if (ShowFlowHardAd.VOICE.equals(ad.getCtype())) {
						mHorizontalViewImgOrTextDefaultImg = 0;
						mHorizontalViewImgOrTextVideo
								.setImageResource(R.drawable.water_voice_icon);
						mHorizontalViewImgOrTextVideo
								.setVisibility(View.VISIBLE);
					}
				}

				this.mHorizontalViewImgOrTextTitle.setTextSize(
						TypedValue.COMPLEX_UNIT_PX,
						mShowFlowStyleConfigs.TEXT_SIZE02);
				this.mHorizontalViewImgOrTextTitle.setLineSpacing(
						mShowFlowStyleConfigs.IMG_TITLE_LINE_SPACING, 1.0f);

				this.mHorizontalViewImgOrTextTitle.setText(horizontalNews
						.getTitle());

				this.mHorizontalViewImgOrTextSource
						.setText(getSourceAndTime(horizontalNews));

				this.mHorizontalViewImgOrText
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								if (mContext != null) {
									if (ad != null && ad.getFirst() == 5) {
										openFirstAD(ad);
									} else {
//										StartModuleUtil.startWFNewsDetailAct(
//												mContext, horizontalNews);
										
										StartModuleUtil.startWaterwallDetailView(mContext, horizontalNews, mLayout,mDialogLayout,mApp);
									}

								}
							}
						});
			}

		} else {

			final ShowFlowNewinfo horizontalNews = this.mShowFlowNewinfoMap
					.get(ShowFlowStyles.ThreeSItemKeyS.HORIZONTAL_TEXT_KEY);

			if (horizontalNews != null) {
				this.mHorizontalViewImgOrTextTitle.setTextSize(
						TypedValue.COMPLEX_UNIT_PX,
						mShowFlowStyleConfigs.TEXT_SIZE01);
				this.mHorizontalViewImgOrTextTitle.setLineSpacing(
						mShowFlowStyleConfigs.IMG_TITLE_LINE_SPACING, 1.0f);

				this.mHorizontalViewImgOrTextTitle.setText(horizontalNews
						.getTitle());

				this.mHorizontalViewImgOrTextSource
						.setText(getSourceAndTime(horizontalNews));

				this.mHorizontalViewImgOrText
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								if (mContext != null) {
//									StartModuleUtil.startWFNewsDetailAct(
//											mContext, horizontalNews);
									
									StartModuleUtil.startWaterwallDetailView(mContext, horizontalNews, mLayout,mDialogLayout,mApp);
								}
							}
						});
				
//				RelativeLayout textBg = (RelativeLayout) mHorizontalViewImgOrText.findViewById(R.id.waterwall_the_text_item);
				Random rColor = new Random();
				mHorizontalViewImgOrText.setBackgroundResource(Const.WATERWALL_ITEM_BG_COLORS[rColor
				                                        					.nextInt(Const.WATERWALL_ITEM_BG_COLORS.length)]);
				
			}
		}

		// 横2
		final ShowFlowNewinfo horizontalNewsImg = this.mShowFlowNewinfoMap
				.get(ShowFlowStyles.ThreeSItemKeyS.HORIZONTAL_IMG_1_KEY);
		if (horizontalNewsImg != null) {

			final ShowFlowHardAd ad = horizontalNewsImg.getShowFlowHardAd();
			mHorizontalViewImgDefaultImg = R.drawable.waterwall_list_default;
			if (ad != null) {
				if (ShowFlowHardAd.VIDEO.equals(ad.getCtype())) {
					mHorizontalViewImgDefaultImg = 0;
					mHorizontalViewImgVideo
							.setImageResource(R.drawable.water_video_icon);
					mHorizontalViewImgVideo.setVisibility(View.VISIBLE);
				} else if (ShowFlowHardAd.VOICE.equals(ad.getCtype())) {
					mHorizontalViewImgDefaultImg = 0;
					mHorizontalViewImgVideo
							.setImageResource(R.drawable.water_voice_icon);
					mHorizontalViewImgVideo.setVisibility(View.VISIBLE);
				}
			}

			this.mHorizontalViewImgTitle.setTextSize(
					TypedValue.COMPLEX_UNIT_PX,
					mShowFlowStyleConfigs.TEXT_SIZE02);
			this.mHorizontalViewImgTitle.setLineSpacing(
					mShowFlowStyleConfigs.IMG_TITLE_LINE_SPACING, 1.0f);

			this.mHorizontalViewImgTitle.setText(horizontalNewsImg.getTitle());

			this.mHorizontalViewImgSource
					.setText(getSourceAndTime(horizontalNewsImg));

			this.mHorizontalViewImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (mContext != null) {
						if (ad != null && ad.getFirst() == 5) {
							openFirstAD(ad);
						} else {
//							StartModuleUtil.startWFNewsDetailAct(mContext,
//									horizontalNewsImg);
							
							StartModuleUtil.startWaterwallDetailView(mContext, horizontalNewsImg, mLayout,mDialogLayout,mApp);
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
		// layoutViewLp.setMargins(0, 0, 0, mShowFlowStyleConfigs.GAP + bottom);
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
				.get(ShowFlowStyles.ThreeSItemKeyS.VERTICAL_KEY);
		if (verticalNews != null) {

			if (!FlyshareBitmapManager.LOAD_BMP_OK
					.equals(this.mVerticalViewImageView
							.getTag(R.string.load_bitmap_key))) {
				this.mFlyshareBitmapManager.displayForFlow(
						this.mVerticalViewImageView, verticalNews.getImgUrl()
								+ "&width=" + this.mDeviceConfig.w / 3 * 3/2
								+ "&height=" + mDeviceConfig.w * 2 / 3 * 3/2,
						mVerticalViewDefaultImg, mVerticalViewDefaultImg,
						mDeviceConfig.w / 3, mDeviceConfig.w * 2 / 3,
						mDeviceConfig.w / 6, false);
			}

			// Tools.showLog("WaterwallScrollView",
			// " title : " + verticalNews.getTitle());

		}

		if (mR == ALL_IMG_01 || mR == ALL_IMG_02) {
			ShowFlowNewinfo horizontalNews = this.mShowFlowNewinfoMap
					.get(ShowFlowStyles.ThreeSItemKeyS.HORIZONTAL_IMG_2_KEY);
			if (horizontalNews != null) {

				if (!FlyshareBitmapManager.LOAD_BMP_OK
						.equals(this.mHorizontalViewImgOrTextImageView
								.getTag(R.string.load_bitmap_key))) {
					this.mFlyshareBitmapManager.displayForFlow(
							this.mHorizontalViewImgOrTextImageView,
							horizontalNews.getImgUrl() + "&width="
									+ this.mDeviceConfig.w * 2 / 3 * 3/2,
							mHorizontalViewImgOrTextDefaultImg,
							mHorizontalViewImgOrTextDefaultImg,
							mDeviceConfig.w * 2 / 3, mDeviceConfig.w / 3,
							mDeviceConfig.w / 6, false);
				}

				// Tools.showLog("WaterwallScrollView", " title : "
				// + horizontalNews.getTitle());
			}
		}

		ShowFlowNewinfo horizontalNewsImg = this.mShowFlowNewinfoMap
				.get(ShowFlowStyles.ThreeSItemKeyS.HORIZONTAL_IMG_1_KEY);
		if (horizontalNewsImg != null) {

			if (!FlyshareBitmapManager.LOAD_BMP_OK
					.equals(this.mHorizontalViewImgImageView
							.getTag(R.string.load_bitmap_key))) {
				this.mFlyshareBitmapManager.displayForFlow(
						this.mHorizontalViewImgImageView,
						horizontalNewsImg.getImgUrl() + "&width="
								+ this.mDeviceConfig.w * 2 / 3 * 3/2,
						mHorizontalViewImgDefaultImg,
						mHorizontalViewImgDefaultImg, mDeviceConfig.w * 2 / 3,
						mDeviceConfig.w / 3, mDeviceConfig.w / 6, false);
			}

			// Tools.showLog("WaterwallScrollView", " title : "
			// + horizontalNewsImg.getTitle());

		}
	}

	@Override
	public void loadImg() {
		loadImgs();
	}

	@Override
	public void cancelLoadImg() {

		FlyshareBitmapManager.cancelWork(this.mVerticalViewImageView);
		FlyshareBitmapManager
				.cancelWork(this.mHorizontalViewImgOrTextImageView);
		FlyshareBitmapManager.cancelWork(this.mHorizontalViewImgImageView);

		super.cancelLoadImg();
	}
}
