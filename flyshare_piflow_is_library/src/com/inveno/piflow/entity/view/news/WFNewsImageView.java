package com.inveno.piflow.entity.view.news;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.inveno.piflow.tools.commontools.Tools;

/**
 * 瀑布流详情内容图片
 * 
 * @author hongchang.liu
 * 
 */
public class WFNewsImageView extends ImageView {

	private int ivWidth;
	private int ivHeight;
	private Bitmap bitmap;
	private String imgUrl;

	/** 是否调用过下载图片 */
	private boolean hasSet;

	/** 是否设置margintop间隙 */
	private boolean marginTopSet;

	private int marginTop;

	private int oldW;
	private int oldH;
	private boolean fromWf;
	private int index;
	public WFNewsImageView(Context context, int w, int h, int ow, int oh,int index,
			String url, int margintop, boolean marginTopset, boolean fromWF) {
		super(context);
//		this.setAdjustViewBounds(true);
		this.ivWidth = w;
		this.ivHeight = h;
		this.imgUrl = url;
		this.marginTop = margintop;
		this.marginTopSet = marginTopset;
		this.index=index;
		Tools.showLog("meitu", "iv width :" + ivWidth + " iv height:"
				+ ivHeight + " OW:" + ow + " OH:" + oh);
		this.oldW = ow;
		this.oldH = oh;
		this.fromWf = fromWF;
		LayoutParams params;
		this.setScaleType(ScaleType.CENTER_CROP);
		if ((ow <= 50 && ow>0)|| (oh <= 50&&oh>0)) {
			params = new LayoutParams(ow, oh);
		} else {
			params = new LayoutParams(LayoutParams.MATCH_PARENT, ivHeight);
		}

		if (marginTopSet)
			params.setMargins(0, margintop, 0, 0);
		// params.setMargins(0, 0, 0, 30);

		this.setLayoutParams(params);
		
	}

	@Override
	public void setImageBitmap(Bitmap bm) {

//		if (bm != null && !fromWf) {
//
//			if ((oldW <= 50 && oldW>0)|| (oldH <= 50&&oldH>0)) {
//				return;
//			}
//			Tools.showLog("meitu", this + "bm width:" + bm.getWidth()
//					+ "  bm height:" + bm.getHeight());
//			int height = (int) ((((ivWidth * 1.0f) / bm.getWidth()) * bm
//					.getHeight()));
//			Tools.showLog("meitu", "计算后的高度："+height);
//			LayoutParams params = (LayoutParams) getLayoutParams();
//			params.height = height;
//			if (marginTopSet)
//				params.setMargins(0, marginTop, 0, 0);
////			this.setLayoutParams(params);
////			this.requestLayout();
//		}
		if (hasSet) {
			super.setImageBitmap(bm);

		}

		this.bitmap = bm;

	}

	/**
	 * 回收内存
	 */
	public void recycle() {
		setImageBitmap(null);
		this.hasSet=false;
		if ((this.bitmap == null) || (this.bitmap.isRecycled()))
			return;
		this.bitmap.recycle();
		this.bitmap = null;
		
	}

	public int getIvWidth() {
		return ivWidth;
	}

	public void setIvWidth(int ivWidth) {
		this.ivWidth = ivWidth;
	}

	public int getIvHeight() {
		return ivHeight;
	}

	public void setIvHeight(int ivHeight) {
		this.ivHeight = ivHeight;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public boolean isHasSet() {
		return hasSet;
	}

	public void setHasSet(boolean hasSet) {
		this.hasSet = hasSet;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	
	
}
