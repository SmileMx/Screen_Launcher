package com.inveno.piflow.tools.bitmap;

import android.graphics.Bitmap.CompressFormat;
import android.view.animation.Animation;

import com.inveno.piflow.R;

/**
 * Bitmap显示时可以配置的一些参数，可配置显示动画显示时的宽高等
 * 
 * @author blueming.wu
 * @date 2012-12-25
 */
public class BitmapDisplayConfig {

	// 显示时需压缩的宽高
	private int bitmapWidth;
	private int bitmapHeight;

	// 默认的屏幕宽高，即大图压缩
	public static int defaultWidth;
	public static int defaultHeight;

	// 是否显示原图
//	private boolean isOrignal;

	// 显示动画
	private Animation animation;

	private int animationType = AnimationType.defaultType;

	// 为下载时的默认图
	private int loadingRes;

	// 下载失败时的图片
	private int loadfailRes = R.drawable.transparent;

	// 下载显示时的压缩格式
	private CompressFormat format = CompressFormat.JPEG;

	// 是否需要保存到SD卡
	private boolean isSaveToSdcard = true;

	public int getBitmapWidth() {
		return bitmapWidth;
	}

	public void setBitmapWidth(int bitmapWidth) {
		this.bitmapWidth = bitmapWidth;
	}

	public int getBitmapHeight() {
		return bitmapHeight;
	}

	public void setBitmapHeight(int bitmapHeight) {
		this.bitmapHeight = bitmapHeight;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public int getAnimationType() {
		return animationType;
	}

	public void setAnimationType(int animationType) {
		this.animationType = animationType;
	}

//	public boolean isOrignal() {
//		return isOrignal;
//	}
//
//	public void setOrignal(boolean isOrignal) {
//		this.isOrignal = isOrignal;
//	}

	public CompressFormat getFormat() {
		return format;
	}

	public void setFormat(CompressFormat format) {
		this.format = format;
	}

	public int getLoadingRes() {
		return loadingRes;
	}

	public void setLoadingRes(int loadingRes) {
		this.loadingRes = loadingRes;
	}

	public int getLoadfailRes() {
		return loadfailRes;
	}

	public void setLoadfailRes(int loadfailRes) {
		this.loadfailRes = loadfailRes;
	}

	public boolean isSaveToSdcard() {
		return isSaveToSdcard;
	}

	public void setSaveToSdcard(boolean isSaveToSdcard) {
		this.isSaveToSdcard = isSaveToSdcard;
	}

	public class AnimationType {
		public static final int userDefined = 0;
		public static final int fadeIn = 1;
		public static final int defaultType = 2;
	}
}
