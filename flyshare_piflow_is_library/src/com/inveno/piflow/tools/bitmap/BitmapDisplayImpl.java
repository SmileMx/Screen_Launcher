package com.inveno.piflow.tools.bitmap;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.inveno.piflow.R;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 实现图片显示接口的实现类
 * 
 * @author blueming.wu
 * @date 2012-12-26
 */
public class BitmapDisplayImpl implements IBitmapDisplay {

	@Override
	public void loadCompletedisplay(ImageView imageView, Bitmap bitmap,
			BitmapDisplayConfig config) {
		imageView.setScaleType(ScaleType.CENTER_CROP);
		switch (config.getAnimationType()) {
		case BitmapDisplayConfig.AnimationType.fadeIn:
			Tools.showLog("test", "显示淡入淡出动画！！！！");
			fadeInDisplay(imageView, bitmap);
			break;
		case BitmapDisplayConfig.AnimationType.userDefined:
			Tools.showLog("test", "用户自定义动画！！！！");
			animationDisplay(imageView, bitmap, config.getAnimation());
			break;
		case BitmapDisplayConfig.AnimationType.defaultType:

			Tools.showLog("test", "显示默认动画！！！！");
			defaultDisplay(imageView, bitmap);
		default:
			break;
		}
	}

	@Override
	public void loadFailDisplay(ImageView imageView, int bitmapRes) {
		imageView.setImageResource(bitmapRes);
		imageView.setTag(R.string.load_bitmap_key,
				FlyshareBitmapManager.LOAD_BMP_FAIL);
	}

	/**
	 * 默认显示淡入淡出动画
	 * 
	 * @param imageView
	 * @param bitmap
	 */
	private void fadeInDisplay(final ImageView imageView, final Bitmap bitmap) {

		if (bitmap != null && !bitmap.isRecycled()) {
			final TransitionDrawable td = new TransitionDrawable(
					new Drawable[] {
							new ColorDrawable(android.R.color.transparent),
							new BitmapDrawable(imageView.getResources(), bitmap) });
			imageView.setImageDrawable(td);
			td.startTransition(300);
			imageView.setTag(R.string.load_bitmap_key,
					FlyshareBitmapManager.LOAD_BMP_OK);
		}

	}

	private void defaultDisplay(ImageView imageView, Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
//			LayoutParams params =  imageView.getLayoutParams();
//			params.height = bitmap.getHeight();
			
			imageView.setImageBitmap(bitmap);
			imageView.setTag(R.string.load_bitmap_key,
					FlyshareBitmapManager.LOAD_BMP_OK);
		}

	}

	/**
	 * 可自定义动画
	 * 
	 * @param imageView
	 * @param bitmap
	 * @param animation
	 */
	private void animationDisplay(ImageView imageView, Bitmap bitmap,
			Animation animation) {
		animation.setStartTime(AnimationUtils.currentAnimationTimeMillis());
		imageView.setImageBitmap(bitmap);
		imageView.startAnimation(animation);
	}

}
