package com.inveno.piflow.entity.view.waterwall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inveno.piflow.R;
import com.inveno.piflow.activity.FlyShareApplication;
import com.inveno.piflow.biz.waterwall.WaterwallBiz;
import com.inveno.piflow.entity.model.showflow.ShowFlowNewinfo;
import com.inveno.piflow.entity.model.showflow.ShowFlowStyleConfigs;
import com.inveno.piflow.entity.model.showflow.ShowFlowStyles;
import com.inveno.piflow.tools.bitmap.FlyshareBitmapManager;
import com.inveno.piflow.tools.commontools.DeviceConfig;
import com.inveno.piflow.tools.commontools.StartModuleUtil;

/**
 * 瀑布流父容器，自动按左右左右顺序放置item
 * 
 * @author mingsong.zhang
 * @date 2013-06-18
 * 
 */
@SuppressLint("NewApi")
public class WaterwallMainItem extends LinearLayout{

	/** 通廊标题在图外面 **/
	private final static int BANNER_STYLE_TITLE_BLOW = 0;
	/** 通廊标题在图上面 **/
	private final static int BANNER_STYLE_TITLE_ABOVE = 1;

	/** 3 7 **/
	// private final static int THREE_SEVEN = 0;
	//
	// /** 7 3 **/
	// private final static int SEVEN_THREE = 1;

	private LinearLayout mLayoutGroup;

	/** 通廊 **/
	// private WaterwallBannerItem mHorizontalLayout;
	// private ImageView mHorizontalIv;
	// private TextView mHorizontalTv;
	// private TextView mHorizontalSource;
	// private TextView mHorizontalTime;

	// private WaterwallThreeSeven mWaterwallThreeOrSeven;
	// private ImageView mWaterwallThreeIv;
	// private TextView mWaterwallThreeTv;
	// private ImageView mWaterwallSevenIv;
	// private TextView mWaterwallSevenTv;
	// private ImageView mWaterwallThreeVideo;
	// private ImageView mWaterwallSevenVideo;
	// private TextView mWaterwallSevenTime;
	// private TextView mWaterwallSevenSource;
	// private TextView mWaterwallThreeSource;
	// private RelativeLayout WaterwallThreeOrSevenLayout;

	// private ShowFlowNewinfo mShowFlowNewinfoThree;
	// private ShowFlowNewinfo mShowFlowNewinfoSeven;

	private WaterwallFFItem mWaterwallFFItem;
	/** 左右两列 **/
	private LinearLayout mLeftLayout;
	private LinearLayout mRightLayout;

	/** 此view **/
	private LinearLayout mConvertView;

	/** 对应下面2个值 **/
	private int mSB;
	private final static int SB_LEFT = 0;
	private final static int SB_RIGHT = 1;
	/** 是否多加了一块 **/
	private boolean mIsSB = false;
	private WaterwallBaseItem mSBView;

	private TextView mBottom;

	private Context mContext;
	private LayoutInflater mLayoutInflater;

	private ShowFlowNewinfo mShowFlowNewinfo;

	private FlyshareBitmapManager mFlyshareBitmapManager;

	private int mBannerWidth;
	private int mBannerHeight;

	/** 子view单击监听器 */
	private OnItemTouchDonwListener mTouchDonwListener;

	private DeviceConfig mDeviceConfig;

	private ShowFlowStyleConfigs mShowFlowStyleConfigs;

	private ShowFlowStyles mShowFlowStyles;
	
	private LinearLayout mLayout;
	private LinearLayout mDialogLayout;
	
	private FlyShareApplication mApp;

	public WaterwallMainItem(Context context, int bannerWidth,
			FlyshareBitmapManager flyshareBitmapManage,
			WaterwallBiz waterwallBiz, ShowFlowStyles styles , LinearLayout view, LinearLayout dialogLayout, FlyShareApplication app) {
		super(context);

		init(context, bannerWidth, flyshareBitmapManage, waterwallBiz, styles,view,dialogLayout,app);

	}

	public WaterwallMainItem(Context context, AttributeSet attrs,
			int bannerWidth, int bannerHeight,
			FlyshareBitmapManager flyshareBitmapManage,
			WaterwallBiz waterwallBiz, ShowFlowStyles styles , LinearLayout view, LinearLayout dialogLayout, FlyShareApplication app) {
		super(context, attrs);
		init(context, bannerHeight, flyshareBitmapManage, waterwallBiz, styles,view,dialogLayout,app);
	}

	private void init(Context context, int bannerWidth,
			FlyshareBitmapManager flyshareBitmapManage,
			WaterwallBiz waterwallBiz, ShowFlowStyles styles , LinearLayout view, LinearLayout dialogLayout, FlyShareApplication app) {
		this.mContext = context;
		mLayout = view;
		mApp = app;
		this.mDialogLayout = dialogLayout;
		this.mDeviceConfig = DeviceConfig.getInstance(context);
		this.mFlyshareBitmapManager = flyshareBitmapManage;

		this.mBannerWidth = bannerWidth;

		this.mShowFlowStyles = styles;

		if (waterwallBiz != null) {
			this.mShowFlowStyleConfigs = waterwallBiz.getShowFlowStyleConfigs();
		}

		this.mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mConvertView = (LinearLayout) this.mLayoutInflater.inflate(
				R.layout.wpd_waterwall_main_item, this);

		this.mLayoutGroup = (LinearLayout) findViewById(R.id.waterwall_main_item_group);
		this.mWaterwallFFItem = (WaterwallFFItem) findViewById(R.id.waterwall_main_item_ff);

		

		this.mLeftLayout = (LinearLayout) findViewById(R.id.waterwall_main_item_left);
		this.mRightLayout = (LinearLayout) findViewById(R.id.waterwall_main_item_right);

		if (this.mShowFlowStyleConfigs != null) {

			

			LayoutParams l = (LayoutParams) this.mLeftLayout.getLayoutParams();
			l.setMargins(0, 0, mShowFlowStyleConfigs.GAP / 2, 0);
			this.mLeftLayout.setLayoutParams(l);

			LayoutParams r = (LayoutParams) this.mRightLayout.getLayoutParams();
			r.setMargins(mShowFlowStyleConfigs.GAP / 2, 0, 0, 0);
			this.mRightLayout.setLayoutParams(r);
		}

		

		this.mBottom = (TextView) findViewById(R.id.waterwall_main_item_bottom);

		if (styles != null) {

			WaterwallBaseItem viewItem = null;

			switch (styles.getThisStyle()) {
			case ShowFlowStyles.STYLE_THREE_S:

				if (styles.getShowFlowNewinfoMap().get(
						ShowFlowStyles.ThreeSItemKeyS.VERTICAL_KEY) != null) {

					viewItem = new WaterwallThreeSItem(mContext,
							mFlyshareBitmapManager, mShowFlowStyles,
							waterwallBiz, mLayout , mDialogLayout,mApp);

				}

				break;

			case ShowFlowStyles.STYLE_THREE_SEVEN:

				if (styles.getShowFlowNewinfoMap().get(
						ShowFlowStyles.ThreeSevenItemKeyS.SEVEN_KEY) != null) {

					viewItem = new WaterwallThreeSeven(mContext,
							mFlyshareBitmapManager, mShowFlowStyles,
							waterwallBiz , mLayout , mDialogLayout,mApp);

				}

				break;

			case ShowFlowStyles.STYLE_BANNER:

				if (styles.getShowFlowNewinfoMap().get(
						ShowFlowStyles.BannerItemKeyS.BANNER_KEY) != null) {

					viewItem = new WaterwallBannerItem(mContext,
							mFlyshareBitmapManager, mShowFlowStyles,
							waterwallBiz , mLayout , mDialogLayout,mApp);

				}

				break;
			}

			if (viewItem != null) {

				this.mLayoutGroup.addView(viewItem);
			}
		}

	}

	
	public void addViewToLeft(WaterwallBaseItem view) {
		this.mLeftLayout.addView(view);
		mSB = SB_LEFT;
	}

	public void addViewToRight(WaterwallBaseItem view) {
		this.mRightLayout.addView(view);
		mSB = SB_RIGHT;
	}

	public void setSBView(WaterwallBaseItem view) {

		this.mSBView = view;

		mIsSB = true;
	}

	public void removeSBView() {

		if (mIsSB) {
			switch (mSB) {
			case SB_LEFT:

				if (this.mSBView != null) {
					this.mLeftLayout.removeView(mSBView);
				}

				break;

			case SB_RIGHT:

				if (this.mSBView != null) {
					this.mRightLayout.removeView(mSBView);
				}

				break;
			}
		}

		mIsSB = false;

	}

	

	/**
	 * 隐藏底部
	 */
	public void hideBottom() {
		this.mBottom.setVisibility(View.GONE);
	}

	/**
	 * 显示底部
	 */
	public void showBottom() {
		this.mBottom.setVisibility(View.VISIBLE);

	}

	public void showLoading() {
		this.mBottom.setText("");
	}

	public void loadFail() {
		this.mBottom.setText("");
	}	

	/**
	 * 加载当屏图片入口
	 * 
	 * @param scrollY
	 * @param windowHeight
	 */
	public void take(int scrollY, int windowHeight) {

		// Tools.showLog("WaterwallScrollView",
		// "_______________  " + getTop() + " _________________"
		// + " 通廊高度 :" + mHorizontalLayout.getHeight() +
		// " 实际的："+((LinearLayout)getParent()).getTop());

		int top = getTop() + ((LinearLayout) getParent().getParent()).getTop();

		

		if (mLayoutGroup != null && mLayoutGroup.getChildCount() > 0) {
			if (top - scrollY < windowHeight
					&& top - scrollY >= -mLayoutGroup.getHeight() * 3 / 4) {
				((WaterwallBaseItem) mLayoutGroup.getChildAt(0))
						.getTheViewOnTheScreen(0, 0);
				// } else {
				// ((WaterwallBaseItem) mLayoutGroup.getChildAt(0))
				// .cancelLoadImg();
			}
		}

		// int dX = top + mHorizontalLayout.getHeight()
		// + mWaterwallThreeOrSeven.getHeight() + mLayoutGroup.getHeight() -
		// scrollY;
		int dX = top + mLayoutGroup.getHeight() - scrollY;

		for (int i = 0; i < this.mLeftLayout.getChildCount(); i++) {
			WaterwallBaseItem item = (WaterwallBaseItem) this.mLeftLayout
					.getChildAt(i);
			item.getTheViewOnTheScreen(dX, windowHeight);
		}
		for (int i = 0; i < this.mRightLayout.getChildCount(); i++) {
			WaterwallBaseItem item = (WaterwallBaseItem) this.mRightLayout
					.getChildAt(i);
			item.getTheViewOnTheScreen(dX, windowHeight);
		}

	}

	/**
	 * 加载当屏之后的图片
	 * 
	 * @param scrollY
	 * @param windowHeight
	 */
	public void showTheImgBelowTheScreen(int scrollY, int windowHeight) {

		int top = getTop() + ((LinearLayout) getParent().getParent()).getTop();

		if (mLayoutGroup != null && mLayoutGroup.getChildCount() > 0) {
			if (top - scrollY > windowHeight) {
				((WaterwallBaseItem) mLayoutGroup.getChildAt(0)).loadImg();
			}
		}

		// int dX = top + mLayoutGroup.getHeight() - scrollY;
		//
		// for (int i = 0; i < this.mLeftLayout.getChildCount(); i++) {
		// WaterwallBaseItem item = (WaterwallBaseItem) this.mLeftLayout
		// .getChildAt(i);
		// item.loadImgBelowTheScreen(dX, windowHeight);
		// }
		// for (int i = 0; i < this.mRightLayout.getChildCount(); i++) {
		// WaterwallBaseItem item = (WaterwallBaseItem) this.mRightLayout
		// .getChildAt(i);
		// item.loadImgBelowTheScreen(dX, windowHeight);
		// }

	}

	

	public LinearLayout getmLeftLayout() {
		return mLeftLayout;
	}

	public LinearLayout getmRightLayout() {
		return mRightLayout;
	}

	// public WaterwallBannerItem getmHorizontalLayout() {
	// return mHorizontalLayout;
	// }

	/**
	 * 开始展示两列的拉伸动画
	 * 
	 * @param x
	 * @param y
	 */
	public void startChildAnim(int x, int y) {
		for (int i = 0; i < this.mLeftLayout.getChildCount(); i++) {
			WaterwallBaseItem item = (WaterwallBaseItem) this.mLeftLayout
					.getChildAt(i);
			item.startStretchingAnim(x, y);
		}
		for (int i = 0; i < this.mRightLayout.getChildCount(); i++) {
			WaterwallBaseItem item = (WaterwallBaseItem) this.mRightLayout
					.getChildAt(i);
			item.startStretchingAnim(x, y);
		}
	}

	/**
	 * 取消下载其所有子控件的图片
	 */
	public void cancelAllLoadImg() {

		// FlyshareBitmapManager.cancelWork(this.mHorizontalIv);

		for (int i = 0; i < this.mLeftLayout.getChildCount(); i++) {
			WaterwallBaseItem item = (WaterwallBaseItem) this.mLeftLayout
					.getChildAt(i);
			item.cancelLoadImg();
		}
		for (int i = 0; i < this.mRightLayout.getChildCount(); i++) {
			WaterwallBaseItem item = (WaterwallBaseItem) this.mRightLayout
					.getChildAt(i);
			item.cancelLoadImg();
		}

		for (int i = 0; i < this.mLayoutGroup.getChildCount(); i++) {
			WaterwallBaseItem item = (WaterwallBaseItem) this.mLayoutGroup
					.getChildAt(i);
			item.cancelLoadImg();
		}

	}

	/**
	 * 移除它的所有子控件
	 */
	public void removeItsAllViews() {
		this.mLayoutGroup.removeAllViews();
		this.mLeftLayout.removeAllViews();
		this.mRightLayout.removeAllViews();
		removeAllViews();
	}

	/**
	 * 子view被点击
	 * 
	 * @param child
	 */
	public void itemTouchDown(View child) {
		int index = indexOfChild(child);
		if (this.mTouchDonwListener != null)
			mTouchDonwListener.OnItemTouchDonw(index);
	}

	/**
	 * 某个子item被按下产生down事件
	 * 
	 * @author blueming.wu
	 * 
	 * @date 2013-7-23
	 */
	public interface OnItemTouchDonwListener {
		void OnItemTouchDonw(int item);
	}

	public void setTouchDonwListener(OnItemTouchDonwListener mTouchDonwListener) {
		this.mTouchDonwListener = mTouchDonwListener;
	}

	public int getFirstItemHeight() {
		if (this.mLayoutGroup.getChildCount() > 0) {
			return this.mLayoutGroup.getHeight();
		} else {
			return getHeight();
		}
	}

	
	/**
	 * 每块动画可以参考调用此方法 ,方法名和传参可以不必理会
	 * @param scrollY
	 * @param windowHeight
	 * @param left
	 * @param t
	 * @param right
	 * @param bottom
	 */
	public void changeMargin(int scrollY, int windowHeight, int left, int t,
			int right, int bottom) {
		int top = getTop() + ((LinearLayout) getParent().getParent()).getTop();

		if (mLayoutGroup != null && mLayoutGroup.getChildCount() > 0) {
			if (top - scrollY < windowHeight
					&& top - scrollY >= -mLayoutGroup.getHeight() * 3 / 4) {
				
//				WaterwallBaseItem item = ((WaterwallBaseItem) mLayoutGroup.getChildAt(0));
				//TODO item.开始动画

			}
		}

		if (mWaterwallFFItem != null) {
			if (top - scrollY < windowHeight
					&& top - scrollY >= -mWaterwallFFItem.getHeight() * 3 / 4) {
				
				//TODO

			}
		}
	}
	
}
