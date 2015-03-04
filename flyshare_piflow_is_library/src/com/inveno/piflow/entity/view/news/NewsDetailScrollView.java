package com.inveno.piflow.entity.view.news;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.inveno.piflow.R;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 新闻滚动 资讯详情页 2112-11-19
 * 
 * @author Chenxu
 * 
 */
public class NewsDetailScrollView extends ScrollView {
	/** 新闻标题ID的引用 **/
	private View pTopView;
	/** 标题文字坐标 **/
	private int tempTop;
	/** 是否允许整个ScrollView滚动 **/
	private boolean enableScroll = false;

	/** 是否启用头部持续显示 **/
	private static boolean enableTop = true;
	/** 左 **/
	private int left;
	private int top;
	private int right;
	private int bottom;
	public boolean firstLayout = false;

	/**
	 * 构造函数
	 * **/

	public NewsDetailScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		tempTop = -1;
		enableScroll = false;
	}

	public void diableScroll() {
		enableScroll = false;
	}

	public void enableScroll() {
		enableScroll = true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		try {
			return enableScroll && super.onTouchEvent(ev);
		} catch (Exception e) {
			return enableScroll;
		}
	}

	/**
	 * ScrollView滚动时的方法
	 * 
	 * @param l
	 *            类似于 X坐标 X的值
	 * @param t
	 *            类似于 Y坐标 Y值
	 * @param oldl
	 *            滚动前X的值
	 * @param oldt
	 *            滚动前Y的值
	 * 
	 */
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if (!enableScroll)
			return;

		// Tools.showLog("sb",
		// "t:"+t+" oldt:"+oldt+" ScrollY():"+getScrollY()+" height:"+getHeight()+" top:"+getTop()+" bottom:"+getBottom());
		//
		// if (Math.abs(t-oldt)==0) {
		// Tools.showLog("sb", "滑动停止了！！");
		// }
		// 是否启用滚动的方法
		// View v=findViewById(R.id.news_detail_ads_ly);
		// Tools.showLog("sb",
		// "t:"+v.getTop()+" b:"+v.getBottom()+" l:"+v.getLeft()+" r:"+v.getRight());
		if (!enableTop) {
			super.onScrollChanged(l, t, oldl, oldt);
			return;
		}
		firstLayout = true;
		pTopView = findViewById(R.id.news_detail_data);

		if (tempTop == -1) {
			tempTop = pTopView.getTop();
		}
		// 获取当前位置
		int[] location = new int[2];

		if (pTopView == null)
			pTopView.getLocationInWindow(location);

		if (t > tempTop) {
			pTopView.layout(pTopView.getLeft(), t, pTopView.getRight(), t
					+ pTopView.getHeight());
			left = pTopView.getLeft();
			top = t;
			right = pTopView.getRight();
			bottom = t + pTopView.getHeight();

		} else {
			pTopView.layout(pTopView.getLeft(), tempTop, pTopView.getRight(),
					tempTop + pTopView.getHeight());
			left = pTopView.getLeft();
			top = tempTop;
			right = pTopView.getRight();
			bottom = tempTop + pTopView.getHeight();
		}
		if (t >= 0 && onScrollListener != null) {
			onScrollListener.onAutoScroll(l, t, oldl, pTopView.getHeight());
		}

		super.onScrollChanged(l, t, oldl, oldt);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// int expandSpec =
		// MeasureSpec.makeMeasureSpec(NewsComomFragment.webViewHeight,
		// MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		Tools.showLog("fff", "onLayout");
		if (firstLayout) {
			pTopView = findViewById(R.id.news_detail_data);
			// 获取当前位置设置
			int[] location = new int[2];
			pTopView.getLocationInWindow(location);
			pTopView.layout(left, top, right, bottom);
		}
	}

	public void setFirstLayout(boolean bl) {
		firstLayout = bl;
	}

	/**
	 * 定义接口
	 * 
	 * @author admin
	 * 
	 */
	public interface OnScrollListener {
		void onBottom();

		void onTop();

		void onScroll();

		void onAutoScroll(int l, int t, int oldl, int oldt);
	}

	private OnScrollListener onScrollListener;

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}

	// 滑动速率
	@Override
	public void fling(int velocityY) {
		// TODO Auto-generated method stub
		super.fling(velocityY * 2);
	}

}
