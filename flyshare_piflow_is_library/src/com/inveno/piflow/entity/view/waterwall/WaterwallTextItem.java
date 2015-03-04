package com.inveno.piflow.entity.view.waterwall;

import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inveno.piflow.R;
import com.inveno.piflow.activity.FlyShareApplication;
import com.inveno.piflow.biz.waterwall.WaterwallBiz;
import com.inveno.piflow.entity.model.showflow.ShowFlowHardAd;
import com.inveno.piflow.entity.model.showflow.ShowFlowNewinfo;
import com.inveno.piflow.entity.model.showflow.ShowFlowStyleConfigs;
import com.inveno.piflow.tools.commontools.Const;
import com.inveno.piflow.tools.commontools.StartModuleUtil;
import com.inveno.piflow.tools.commontools.StringTools;

/**
 * 资讯item
 * 
 * @author mingsong.zhang
 * @date 2013-06-18
 * 
 */
public class WaterwallTextItem extends WaterwallBaseItem implements
		OnClickListener {

	private static final String TAG = "WaterwallTextItem";

	/** 样式1 **/
	public static final int STYLE01 = 0;
	/** 样式2 **/
	public static final int STYLE02 = 1;
	/** 样式3 **/
	public static final int STYLE03 = 2;

	/** 标题第一个字符是汉字，内容第一个字符不是汉字 **/
	public static final int[] TITLE_ONLY = { STYLE01, STYLE02 };

	/** 标题第一个字符不是汉字，内容第一个字符是汉字 **/
	public static final int[] CONTENT_ONLY = { STYLE02, STYLE03 };

	/** 所有样式 **/
	public static final int[] ALL_STYLES = { STYLE01, STYLE02, STYLE03 };

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private LinearLayout layoutTextItem;
	private TextView mTitle;
	private TextView mContent;
	private TextView mSourceTime;
	private Random mR;

	/** 第一个字体大小 **/
	private int mFirstTextSize = 50;

	private ShowFlowNewinfo mShowFlowNewinfo;

	private WaterwallBiz mWaterwallBiz;

	private ShowFlowStyleConfigs mShowFlowStyleConfigs;
	
	private LinearLayout mLayout;
	private LinearLayout mDialogLayout;
	
	private FlyShareApplication mApp;

	public WaterwallTextItem(Context context, int ri,
			ShowFlowNewinfo showFlowNewinfo, int index, int actualWidth,
			WaterwallBiz waterwallBiz, LinearLayout view, LinearLayout dialogLayout, FlyShareApplication app) {
		super(context);
		init(context, ri, showFlowNewinfo, index, actualWidth, waterwallBiz,view,dialogLayout,app);
	}

	public WaterwallTextItem(Context context, AttributeSet attrs, int ri,
			ShowFlowNewinfo showFlowNewinfo, int index, int actualWidth,
			WaterwallBiz waterwallBiz, LinearLayout view, LinearLayout dialogLayout, FlyShareApplication app) {
		super(context, attrs);
		init(context, ri, showFlowNewinfo, index, actualWidth, waterwallBiz,view,dialogLayout,app);
	}

	private void init(Context context, int ri, ShowFlowNewinfo showFlowNewinfo,
			int index, int actualWidth, WaterwallBiz waterwallBiz, LinearLayout view, LinearLayout dialogLayout, FlyShareApplication app) {
		this.mMyStyle = MY_STYLE_WATERWALL_TEXT_ITEM;
		mLayout = view;
		mApp = app;
		this.mDialogLayout = dialogLayout;
		this.mIndex = index;
		this.mContext = context;
		this.mWaterwallBiz = waterwallBiz;
		this.mR = new Random();

		if (mWaterwallBiz != null) {
			mShowFlowStyleConfigs = mWaterwallBiz.getShowFlowStyleConfigs();
			if (mShowFlowStyleConfigs != null)
				this.mFirstTextSize = mWaterwallBiz.getShowFlowStyleConfigs().FIRST_TEXT_SIZE;
		} else {
			Resources res = context.getResources();
			this.mFirstTextSize = res
					.getInteger(R.integer.waterwall_first_text_size);
		}

		this.mShowFlowNewinfo = showFlowNewinfo;
		this.mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (showFlowNewinfo != null) {
			creatView(ri, showFlowNewinfo, actualWidth);
		}

		setOnClickListener(this);
	}

	private void creatView(int ri, ShowFlowNewinfo showFlowNewinfo,
			int actualWidth) {

		LinearLayout layout;
		SpannableString s;

		switch (ri) {
		case STYLE01:
			this.mLayoutInflater.inflate(R.layout.wpd_waterwall_text_item01,
					this);
			this.layoutTextItem = (LinearLayout) findViewById(R.id.waterwall_item_01_layout);

			this.mTitle = (TextView) findViewById(R.id.waterwall_text_item01_tv);
			this.mSourceTime = (TextView) findViewById(R.id.waterwall_text_item01_tv_source_time);

			LayoutParams lp = (LayoutParams) this.mTitle.getLayoutParams();

			if (mShowFlowStyleConfigs != null) {
				this.mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						mShowFlowStyleConfigs.TEXT_SIZE01);
				lp.height = mShowFlowStyleConfigs.SYTLE_01_TEXT_HEIGHT;

				// LayoutParams lpl = (LayoutParams) getLayoutParams();
				// lpl.setMargins(0, 0, 0, 0);
				// setLayoutParams(lpl);

			}

			s = new SpannableString(showFlowNewinfo.getTitle());
			s.setSpan(new AbsoluteSizeSpan(this.mFirstTextSize), 0, 1,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			this.mTitle.setText(s);

			layout = (LinearLayout) findViewById(R.id.waterwall_text_item01_layout);
			layout.setBackgroundResource(Const.WATERWALL_ITEM_BG_COLORS[mR
					.nextInt(Const.WATERWALL_ITEM_BG_COLORS.length)]);
//			this.mTitle.setBackgroundResource(Const.WATERWALL_ITEM_BG_COLORS[mR
//					.nextInt(Const.WATERWALL_ITEM_BG_COLORS.length)]);

//			lp.width = actualWidth;
			this.mTitle.setLayoutParams(lp);

			break;

		case STYLE02:

			this.mLayoutInflater.inflate(R.layout.wpd_waterwall_text_item02,
					this);
			this.layoutTextItem = (LinearLayout) findViewById(R.id.waterwall_item_02_layout);
			this.mTitle = (TextView) findViewById(R.id.waterwall_text_item02_title);
			this.mContent = (TextView) findViewById(R.id.waterwall_text_item02_content);
			this.mSourceTime = (TextView) findViewById(R.id.waterwall_text_item02_tv_source_time);

			this.mTitle.setText(showFlowNewinfo.getTitle());
			this.mContent.setText(thisContent(showFlowNewinfo));

			layout = (LinearLayout) findViewById(R.id.waterwall_text_item02_bg);
			layout.setBackgroundResource(Const.WATERWALL_ITEM_BG_COLORS[mR
					.nextInt(Const.WATERWALL_ITEM_BG_COLORS.length)]);
			LayoutParams lpLayout = (LayoutParams) layout.getLayoutParams();
//			lpLayout.width = actualWidth;
			layout.setLayoutParams(lpLayout);

			if (mShowFlowStyleConfigs != null) {
				this.mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						mShowFlowStyleConfigs.TEXT_SIZE01);
				LayoutParams lpTitle = (LayoutParams) this.mTitle
						.getLayoutParams();
				lpTitle.height = mShowFlowStyleConfigs.SYTLE_02_TITLE_HEIGHT;
//				lpTitle.width = actualWidth;
				this.mTitle.setLayoutParams(lpTitle);

				this.mContent.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						mShowFlowStyleConfigs.TEXT_SIZE02);
				LayoutParams lpContent = (LayoutParams) this.mContent
						.getLayoutParams();
				lpContent.height = mShowFlowStyleConfigs.SYTLE_02_CONTENT_HEIGHT;
//				lpContent.width = actualWidth;
				this.mContent.setLayoutParams(lpContent);

				// LayoutParams lpl = (LayoutParams) getLayoutParams();
				// lpl.setMargins(0, 0, 0, 0);
				// setLayoutParams(lpl);
			}

			break;

		case STYLE03:

			this.mLayoutInflater.inflate(R.layout.wpd_waterwall_text_item03,
					this);
			this.layoutTextItem = (LinearLayout) findViewById(R.id.waterwall_item_03_layout);
			this.mTitle = (TextView) findViewById(R.id.waterwall_text_item03_title);
			this.mContent = (TextView) findViewById(R.id.waterwall_text_item03_content);
			this.mSourceTime = (TextView) findViewById(R.id.waterwall_text_item03_tv_source_time);

			if (mShowFlowStyleConfigs != null) {
				this.mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						mShowFlowStyleConfigs.TEXT_SIZE02);
				LayoutParams lpTitle = (LayoutParams) this.mTitle
						.getLayoutParams();
				lpTitle.height = mShowFlowStyleConfigs.SYTLE_03_TITLE_HEIGHT;
//				lpTitle.width = actualWidth;
				this.mTitle.setLayoutParams(lpTitle);

				this.mContent.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						mShowFlowStyleConfigs.TEXT_SIZE01);
				LayoutParams lpContent = (LayoutParams) this.mContent
						.getLayoutParams();
				lpContent.height = mShowFlowStyleConfigs.SYTLE_03_CONTENT_HEIGHT;
//				lpContent.width = actualWidth;
				this.mContent.setLayoutParams(lpContent);

				// LayoutParams lpl = (LayoutParams) getLayoutParams();
				// lpl.setMargins(0, 0, 0, 0);
				// setLayoutParams(lpl);
			}

			s = new SpannableString(thisContent(showFlowNewinfo));
			s.setSpan(new AbsoluteSizeSpan(this.mFirstTextSize), 0, 1,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			this.mContent.setText(s);

			this.mTitle.setText(showFlowNewinfo.getTitle());

			layout = (LinearLayout) findViewById(R.id.waterwall_text_item03_bg);
			layout.setBackgroundResource(Const.WATERWALL_ITEM_BG_COLORS[mR
					.nextInt(Const.WATERWALL_ITEM_BG_COLORS.length)]);

			LayoutParams lpLayout2 = (LayoutParams) layout.getLayoutParams();
//			lpLayout2.width = actualWidth;
			layout.setLayoutParams(lpLayout2);

			break;
		}
		
		String source = showFlowNewinfo.getSrc();
		if(StringTools.isEmpty(source)){
			ShowFlowHardAd ad = showFlowNewinfo.getShowFlowHardAd();
			if(ad!=null){
				source = ad.getCpname();
			}
		}
		
		
		this.mSourceTime.setText(source+"    "+StringTools.waterwallTime(showFlowNewinfo.getRtime()));
		
		if (mShowFlowStyleConfigs != null && layoutTextItem!=null) {
			
			LayoutParams l = (LayoutParams) layoutTextItem.getLayoutParams();
			l.setMargins(0, 0, 0, mShowFlowStyleConfigs.GAP);
			layoutTextItem.setLayoutParams(l);
		}
	}

	/**
	 * 内容
	 * 
	 * @param showFlowNewinfo
	 * @return
	 */
	private String thisContent(ShowFlowNewinfo showFlowNewinfo) {
		return StringTools.removeHtmlTag(showFlowNewinfo.getSnippet().trim());
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
//			StartModuleUtil.startWFNewsDetailAct(mContext, mShowFlowNewinfo);
			StartModuleUtil.startWaterwallDetailView(mContext, mShowFlowNewinfo, mLayout,mDialogLayout,mApp);
		}
		
//		Toast.makeText(mContext, "高度："+getHeight(), 2000).show();
	}

	@Override
	public void changeHeight(int height) {
		LayoutParams lp = (LayoutParams) this.mTitle.getLayoutParams();
		lp.height = lp.height + height;
		this.mTitle.setLayoutParams(lp);

	}

	@Override
	public void changeMargin(int left, int top, int right, int bottom) {
		setPadding(left, top, right, bottom);
	}

	@Override
	public void setActualHeight(int height) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void getTheViewOnTheScreen(int sub , int windowHeight) {
		
		super.getTheViewOnTheScreen(sub , windowHeight);
	}
}
