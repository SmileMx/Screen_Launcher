package com.inveno.piflow.entity.view.waterwall;

import java.util.HashMap;

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
import com.inveno.piflow.tools.commontools.DeviceConfig;
import com.inveno.piflow.tools.commontools.StartModuleUtil;
import com.inveno.piflow.tools.commontools.StringTools;

/**
 * 通栏
 * 
 * @author mingsong.zhang
 * @date 2013-07-23
 * 
 */
public class WaterwallBannerItem extends WaterwallBaseItem {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private ShowFlowStyleConfigs mShowFlowStyleConfigs;
	private DeviceConfig mDeviceConfig;
	private FlyshareBitmapManager mFlyshareBitmapManager;
	private ShowFlowStyles mShowFlowStyles;
	private HashMap<String, ShowFlowNewinfo> mShowFlowNewinfoMap;

	private RelativeLayout mThisView;
	private ImageView mImageView;
	private ImageView mVideo;
	private TextView mTitle;
	private TextView mSource;
	private int mDefaultImg;
	
	private LinearLayout mLayout;
	private LinearLayout mDialogLayout;
	
	private FlyShareApplication mApp;

	public WaterwallBannerItem(Context context,
			FlyshareBitmapManager flyshareBitmapManage, ShowFlowStyles styles,
			WaterwallBiz waterwallBiz , LinearLayout view, LinearLayout dialogLayout, FlyShareApplication app) {
		super(context);
		init(context, flyshareBitmapManage, styles, waterwallBiz,view,dialogLayout,app);
	}

	public WaterwallBannerItem(Context context, AttributeSet attrs,
			FlyshareBitmapManager flyshareBitmapManage, ShowFlowStyles styles,
			WaterwallBiz waterwallBiz , LinearLayout view, LinearLayout dialogLayout, FlyShareApplication app) {
		super(context, attrs);
		init(context, flyshareBitmapManage, styles, waterwallBiz,view,dialogLayout,app);
	}

	private void init(Context context,
			FlyshareBitmapManager flyshareBitmapManage, ShowFlowStyles styles,
			WaterwallBiz waterwallBiz , LinearLayout view, LinearLayout dialogLayout, FlyShareApplication app) {
		this.mContext = context;
		mLayout = view;
		mApp = app;
		mDialogLayout = dialogLayout;
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

		this.mLayoutInflater.inflate(R.layout.wpg_waterwall_the_img_item, this);

		this.mThisView = (RelativeLayout) findViewById(R.id.waterwall_the_img_item);
		this.mImageView = (ImageView) findViewById(R.id.waterwall_the_img_item_img);
		this.mVideo = (ImageView) findViewById(R.id.waterwall_the_img_item_video);
		this.mTitle = (TextView) findViewById(R.id.waterwall_the_img_item_title);
		this.mSource = (TextView) findViewById(R.id.waterwall_the_img_item_source);

		final ShowFlowNewinfo news = this.mShowFlowNewinfoMap
				.get(ShowFlowStyles.BannerItemKeyS.BANNER_KEY);

		if (news != null) {

			// 设置总高度
			LinearLayout.LayoutParams layoutViewLp = (LinearLayout.LayoutParams) mThisView
					.getLayoutParams();
			layoutViewLp.setMargins(0, 0, 0, mShowFlowStyleConfigs.GAP);
			layoutViewLp.height = getActualHeight(news.getIconHeight(),
					news.getIconWidht(), this.mDeviceConfig.w);
			mThisView.setLayoutParams(layoutViewLp);

			final ShowFlowHardAd ad = news.getShowFlowHardAd();
			mDefaultImg = R.drawable.waterwall_list_default;
			if (ad != null) {
				if (ShowFlowHardAd.VIDEO.equals(ad.getCtype())) {
					mDefaultImg = 0;
					mVideo.setImageResource(R.drawable.water_video_icon);
					mVideo.setVisibility(View.VISIBLE);
				} else if (ShowFlowHardAd.VOICE.equals(ad.getCtype())) {
					mDefaultImg = 0;
					mVideo.setImageResource(R.drawable.water_voice_icon);
					mVideo.setVisibility(View.VISIBLE);
				}
			}

			this.mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					mShowFlowStyleConfigs.TEXT_SIZE02);
			this.mTitle.setLineSpacing(
					mShowFlowStyleConfigs.IMG_TITLE_LINE_SPACING, 1.0f);

			this.mTitle.setText(news.getTitle());

			this.mSource.setText(getSourceAndTime(news));

			this.mThisView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (mContext != null) {
						if (ad != null && ad.getFirst() == 5) {
							openFirstAD(ad);
						} else {
//							StartModuleUtil
//									.startWFNewsDetailAct(mContext, news);
							StartModuleUtil.startWaterwallDetailView(mContext, news, mLayout,mDialogLayout,mApp);

						}
					}
				}
			});

			loadImgs();
		}

	}

	@Override
	public void getTheViewOnTheScreen(int sub, int windowHeight) {

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
	 * 加载图片
	 */
	private void loadImgs() {
		ShowFlowNewinfo news = this.mShowFlowNewinfoMap
				.get(ShowFlowStyles.BannerItemKeyS.BANNER_KEY);
		if (news != null) {

			if (!FlyshareBitmapManager.LOAD_BMP_OK.equals(this.mImageView
					.getTag(R.string.load_bitmap_key))) {
				this.mFlyshareBitmapManager.displayForFlow(
						this.mImageView,
						news.getImgUrl() + "&width=" + this.mDeviceConfig.w * 3/2,
						mDefaultImg,
						mDefaultImg,
						mDeviceConfig.w,
						getActualHeight(news.getIconHeight(),
								news.getIconWidht(), this.mDeviceConfig.w),
						mDeviceConfig.w / 6, false);
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
	private int getActualHeight(int height, int width, int bannerWidth) {
		int actualHeight = height;
		if (width != 0) {
			actualHeight = height * bannerWidth / width;
		}
		return actualHeight;
	}

	@Override
	public void changeHeight(int height) {

	}

	@Override
	public void setActualHeight(int height) {

	}

	@Override
	public void changeMargin(int left, int top, int right, int bottom) {

	}

	@Override
	public void loadImg() {
		loadImgs();
	}

	@Override
	public void cancelLoadImg() {
		FlyshareBitmapManager.cancelWork(this.mImageView);
	}
}
