package com.inveno.piflow.tools.bitmap;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 图片加载完成后，回调的接口，使用者可通过此接口来实现某些提示
 * 
 * @author blueming.wu
 * @date 2012-12-25
 */
public interface IBitmapDisplay {

	/**
	 * 图片加载完成 回调的函数
	 * 
	 * @param imageView
	 * @param bitmap
	 * @param config
	 */
	public void loadCompletedisplay(ImageView imageView, Bitmap bitmap,
			BitmapDisplayConfig config);

	/**
	 * 图片加载失败回调的函数
	 * 
	 * @param imageView
	 * @param bitmap
	 */
	public void loadFailDisplay(ImageView imageView, int bitmapRes);
}
