package com.inveno.piflow.entity.view.waterwall;

import java.util.Random;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.inveno.piflow.tools.bitmap.FlyshareBitmapManager;
import com.inveno.piflow.tools.commontools.Const;
import com.inveno.piflow.tools.commontools.DeviceConfig;
import com.inveno.piflow.tools.commontools.StartModuleUtil;
import com.inveno.piflow.tools.commontools.StringTools;

/**
 * 瀑布流 图片item
 * 
 * @author mingsong.zhang
 * @date 2013-06-18
 */
public class WaterwallImgItem extends WaterwallBaseItem implements
		OnClickListener {

	public static final String TAG = "WaterwallImgItem";

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private ImageView mIv;
	private FlyshareBitmapManager mFlyshareBitmapManager;
	private ShowFlowNewinfo mShowFlowNewinfo;
	private TextView mTv;
	private TextView mSource;
	private RelativeLayout mRelativeLayout;
	private ImageView mTitleBackground;
	private boolean isHeightBigger;
	private LinearLayout mTitleBg;

	private String mStrSource;
	private String mStrTime;

	private WaterwallBiz mWaterwallBiz;

	private ShowFlowStyleConfigs mShowFlowStyleConfigs;

	private DeviceConfig mDeviceConfig;

	private LinearLayout mLayout;
	private LinearLayout mDialogLayout;

	private FlyShareApplication mApp;

	public WaterwallImgItem(Context context, ShowFlowNewinfo showFlowNewinfo,
			int index, int actualWidth, int actualHeight,
			FlyshareBitmapManager flyshareBitmapManage,
			WaterwallBiz waterwallBiz, boolean isHeightBigger,
			LinearLayout view, LinearLayout dialogLayout,
			FlyShareApplication app) {
		super(context);
		init(context, showFlowNewinfo, index, actualWidth, actualHeight,
				flyshareBitmapManage, waterwallBiz, isHeightBigger, view,
				dialogLayout, app);
	}

	public WaterwallImgItem(Context context, AttributeSet attrs,
			ShowFlowNewinfo showFlowNewinfo, int index, int actualWidth,
			int actualHeight, FlyshareBitmapManager flyshareBitmapManage,
			WaterwallBiz waterwallBiz, boolean isHeightBigger,
			LinearLayout view, LinearLayout dialogLayout,
			FlyShareApplication app) {
		super(context, attrs);
		init(context, showFlowNewinfo, index, actualWidth, actualHeight,
				flyshareBitmapManage, waterwallBiz, isHeightBigger, view,
				dialogLayout, app);
	}

	int width;
	int mActualHeight;
	int mDefaultImg = 0;

	private void init(Context context, ShowFlowNewinfo showFlowNewinfo,
			int index, int actualWidth, int actualHeight,
			FlyshareBitmapManager flyshareBitmapManager,
			WaterwallBiz waterwallBiz, boolean isHeightBigger,
			LinearLayout view, LinearLayout dialogLayout,
			FlyShareApplication app) {
		this.mMyStyle = MY_STYLE_WATERWALL_IMG_ITEM;
		this.mIndex = index;
		this.mContext = context;
		this.mDeviceConfig = DeviceConfig.getInstance(context);
		this.mWaterwallBiz = waterwallBiz;
		this.isHeightBigger = isHeightBigger;
		this.mLayout = view;
		this.mDialogLayout = dialogLayout;
		this.mApp = app;
		this.mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mLayoutInflater.inflate(R.layout.wpd_waterwall_img_item, this);
		this.mIv = (ImageView) findViewById(R.id.waterwall_img_item_iv);
		this.mTitleBackground = (ImageView) findViewById(R.id.waterwall_img_item_title_background);
		this.mTitleBg = (LinearLayout) findViewById(R.id.waterwall_img_item_title_LinearLayout);
		Random r = new Random();
		this.mTitleBg.setBackgroundResource(Const.WATERWALL_ITEM_BG_COLORS[r
				.nextInt(Const.WATERWALL_ITEM_BG_COLORS.length)]);

		if (showFlowNewinfo != null) {

			mStrSource = showFlowNewinfo.getSrc();
			if (StringTools.isEmpty(mStrSource)) {
				ShowFlowHardAd ad = showFlowNewinfo.getShowFlowHardAd();
				if (ad != null) {
					mStrSource = ad.getCpname();
				}
			}

			if (StringTools.isNotEmpty(mStrSource)) {
				mStrSource = mStrSource + "    ";
			}

			mStrTime = StringTools.waterwallTime(showFlowNewinfo.getRtime());

		}

		if (isHeightBigger) {
			this.mTv = (TextView) findViewById(R.id.waterwall_img_item_title_above);
			this.mSource = (TextView) findViewById(R.id.waterwall_img_item_source_above);

			ifHideSourceAndTime();

		} else {
			this.mTv = (TextView) findViewById(R.id.waterwall_img_item_title);
			this.mSource = (TextView) findViewById(R.id.waterwall_img_item_source);
			this.mTitleBackground.setVisibility(View.GONE);
		}
		this.mTv.setVisibility(View.VISIBLE);
		this.mSource.setVisibility(View.VISIBLE);

		this.mRelativeLayout = (RelativeLayout) findViewById(R.id.waterwall_img_item_relativelayout);

		LayoutParams lpRL = (LayoutParams) this.mRelativeLayout
				.getLayoutParams();
		// lpRL.width = actualWidth;
		lpRL.height = actualHeight;
		this.mRelativeLayout.setLayoutParams(lpRL);

		this.mSource.setText(mStrSource + mStrTime);

		if (this.mWaterwallBiz != null) {
			mShowFlowStyleConfigs = mWaterwallBiz.getShowFlowStyleConfigs();
			if (mShowFlowStyleConfigs != null) {
				this.mTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						mShowFlowStyleConfigs.TEXT_SIZE02);
				this.mTv.setLineSpacing(
						mShowFlowStyleConfigs.IMG_TITLE_LINE_SPACING, 1.0f);
				if (!isHeightBigger) {
					LayoutParams lp = (LayoutParams) this.mTv.getLayoutParams();
					lp.height = mShowFlowStyleConfigs.IMG_TEXT_HEIGHT;
					this.mTv.setLayoutParams(lp);
				}

				LinearLayout layout = (LinearLayout) findViewById(R.id.waterwall_img_item_layout);

				LayoutParams l = (LayoutParams) layout.getLayoutParams();
				l.setMargins(0, 0, 0, mShowFlowStyleConfigs.GAP);
				layout.setLayoutParams(l);

			}
		}

		this.mFlyshareBitmapManager = flyshareBitmapManager;

		width = actualWidth;
		mActualHeight = actualHeight;

		mDefaultImg = R.drawable.waterwall_list_default;

		ShowFlowHardAd ad = showFlowNewinfo.getShowFlowHardAd();
		if (ad != null) {
			if (ShowFlowHardAd.VIDEO.equals(ad.getCtype())) {
				mDefaultImg = 0;
				ImageView iv = (ImageView) findViewById(R.id.waterwall_img_item_video);
				iv.setImageResource(R.drawable.water_video_icon);
				iv.setVisibility(View.VISIBLE);
			} else if (ShowFlowHardAd.VOICE.equals(ad.getCtype())) {
				mDefaultImg = 0;
				ImageView iv = (ImageView) findViewById(R.id.waterwall_img_item_video);
				iv.setImageResource(R.drawable.water_voice_icon);
				iv.setVisibility(View.VISIBLE);
			}
		}

		this.mFlyshareBitmapManager.displayForFlow(mIv,
				showFlowNewinfo.getImgUrl() + "&width=" + width * 21 / 10,
				mDefaultImg, mDefaultImg, width, actualHeight,
				mDeviceConfig.w / 6, false);

		this.mTv.setText(StringTools.removeHtmlTag(showFlowNewinfo.getTitle()
				.trim()));

		setImgActualWidth(actualWidth);
		this.mShowFlowNewinfo = showFlowNewinfo;
		setOnClickListener(this);

		mTitle = showFlowNewinfo.getTitle();
		mType = showFlowNewinfo.getType();
	}

	private void ifHideSourceAndTime() {
		if (StringTools.isEmpty(mStrSource) && StringTools.isEmpty(mStrTime)) {
			RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) this.mSource
					.getLayoutParams();
			lp1.height = 0;
			this.mSource.setLayoutParams(lp1);

			// RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams)
			// this.mTime
			// .getLayoutParams();
			// lp2.height = 0;
			// this.mTime.setLayoutParams(lp2);
		}
	}

	String mTitle;
	String mType;

	@Override
	public void changeHeight(int height) {

		// if (isHeightBigger) {
		// RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)
		// this.mIv
		// .getLayoutParams();
		// lp.height = lp.height + height;
		// this.mIv.setLayoutParams(lp);
		//
		// LayoutParams lpRL = (LayoutParams) this.mRelativeLayout
		// .getLayoutParams();
		// lpRL.height = lpRL.height + height;
		// this.mRelativeLayout.setLayoutParams(lpRL);
		//
		// } else {

		int gap = 0;

		if (!isHeightBigger) {
			this.mTv.setVisibility(View.GONE);
			this.mSource.setVisibility(View.GONE);

			this.mTv = (TextView) findViewById(R.id.waterwall_img_item_title_above);
			this.mSource = (TextView) findViewById(R.id.waterwall_img_item_source_above);

			if (this.mShowFlowNewinfo != null) {

				this.mTitleBackground.setVisibility(View.VISIBLE);
				this.mTv.setVisibility(View.VISIBLE);
				this.mSource.setVisibility(View.VISIBLE);

				this.mSource.setText(mStrSource + mStrTime);

				ifHideSourceAndTime();

				if (mShowFlowStyleConfigs != null) {
					this.mTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
							mShowFlowStyleConfigs.TEXT_SIZE02);
					this.mTv.setLineSpacing(
							mShowFlowStyleConfigs.IMG_TITLE_LINE_SPACING, 1.0f);
				}
				this.mTv.setText(StringTools.removeHtmlTag(mShowFlowNewinfo
						.getTitle().trim()));

			}

			if (mShowFlowStyleConfigs != null) {
				gap = mShowFlowStyleConfigs.IMG_EXTRA_GAP
						- mShowFlowStyleConfigs.IMG_ABOVE_EXTRA_GAP;
			}
		}

		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) this.mIv
				.getLayoutParams();
		lp.height = lp.height + height + gap;
		this.mIv.setLayoutParams(lp);

		LayoutParams lpRL = (LayoutParams) this.mRelativeLayout
				.getLayoutParams();
		lpRL.height = lpRL.height + height + gap;
		this.mRelativeLayout.setLayoutParams(lpRL);

		// LayoutParams lp = (LayoutParams) this.mTv.getLayoutParams();
		// lp.height = lp.height + height;
		// this.mTv.setLayoutParams(lp);
		// }

	}

	public void setSBheight(int height) {
		LayoutParams lp = (LayoutParams) this.mTv.getLayoutParams();
		lp.height = height;
		this.mTv.setLayoutParams(lp);
	}

	/**
	 * 设置图片实际高度
	 */
	public void setActualHeight(int height) {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) this.mIv
				.getLayoutParams();
		lp.height = height;
		this.mIv.setLayoutParams(lp);

		LayoutParams lpRL = (LayoutParams) this.mRelativeLayout
				.getLayoutParams();
		lpRL.height = height;
		this.mRelativeLayout.setLayoutParams(lpRL);
	}

	public void setImgActualWidth(int width) {
		// RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)
		// this.mIv
		// .getLayoutParams();
		// lp.width = width;
		// this.mIv.setLayoutParams(lp);
	}

	@Override
	public void changeMargin(int left, int top, int right, int bottom) {
		// TODO Auto-generated method stub

	}

	/** 限制多次点击 */
	private long lastClickTime;

	@Override
	public void onClick(View v) {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 500) { // 500毫秒内按钮无效，这样可以控制快速点击，自己调整频率
			return;
		}
		lastClickTime = time;

		if (mContext != null && mShowFlowNewinfo != null) {
			ShowFlowHardAd ad = mShowFlowNewinfo.getShowFlowHardAd();
			if (ad != null && ad.getFirst() == 5) {
				openFirstAD(ad);
			} else {
				// StartModuleUtil
				// .startWFNewsDetailAct(mContext, mShowFlowNewinfo);
				// if(mLayout!=null){
				// ShowFlowHardAd flowHardAd =
				// mShowFlowNewinfo.getShowFlowHardAd();
				// if(flowHardAd != null && "6".equals(flowHardAd.getCtype())){
				// WaterwallAudioView wav = new WaterwallAudioView(mContext,
				// mShowFlowNewinfo,mLayout);
				// mLayout.addView(wav);
				// }
				//
				// }

				StartModuleUtil.startWaterwallDetailView(mContext,
						mShowFlowNewinfo, mLayout, mDialogLayout, mApp);

			}

		}

		// Toast.makeText(mContext, "差值：" + (getHeight() - mIv.getHeight()),
		// 2000)
		// .show();

	}

	// @Override
	// public boolean onInterceptTouchEvent(MotionEvent ev) {
	// // 拦截dwon事件，知道此view属于父容器的位置
	//
	// // switch (ev.getAction()) {
	// // case MotionEvent.ACTION_DOWN:
	// // WaterwallMainItem mainItem = (WaterwallMainItem)
	// // getParent().getParent();
	// // mainItem.itemTouchDown(this);
	// // break;
	// // }
	//
	// return super.onInterceptTouchEvent(ev);
	// }

	@Override
	public void getTheViewOnTheScreen(int sub, int windowHeight) {

		if ((getTop() + sub < windowHeight && getTop() + sub >= 0)
				|| (getTop() + sub <= 0 && getTop() + sub >= -getHeight() * 3 / 4)) {

			// Tools.showLog("WaterwallScrollView", " title : " + mTitle
			// + " getTag :" + mIv.getTag(R.string.load_bitmap_key)
			// + " getTop()+ sub :" + (getTop() + sub) + " H:"
			// + getHeight() + " 类型：" + mType);

			loadImgs();

		} else if ((getTop() + sub < 0)) {
			FlyshareBitmapManager.cancelWork(this.mIv);
		} else {

		}

		super.getTheViewOnTheScreen(sub, windowHeight);
	}

	private void loadImgs() {
		if (!FlyshareBitmapManager.LOAD_BMP_OK.equals(this.mIv
				.getTag(R.string.load_bitmap_key))) {
			this.mFlyshareBitmapManager.displayForFlow(this.mIv,
					this.mShowFlowNewinfo.getImgUrl() + "&width=" + width * 21
							/ 10, mDefaultImg, mDefaultImg, width,
					mActualHeight, mDeviceConfig.w / 6, false);

			// Tools.showLog("WaterwallScrollView",
			// " title : " + mTitle + " : " + getTop() + " getTag :"
			// + mIv.getTag(R.string.load_bitmap_key));
		}
	}

	// @Override
	// public void startStretchingAnim(int fx, int fy) {
	// super.startStretchingAnim(fx, fy);
	// Tools.showLogA("拉伸的title:"+mTitle);
	//
	// }

	@Override
	public void loadImg() {
		loadImgs();
	}

	@Override
	public void cancelLoadImg() {

		FlyshareBitmapManager.cancelWork(this.mIv);

		super.cancelLoadImg();
	}

	@Override
	public void loadImgBelowTheScreen(int sub, int windowHeight) {
		if (getTop() + sub > windowHeight) {

			loadImgs();

		}
	}

}
