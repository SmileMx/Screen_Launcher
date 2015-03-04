package com.inveno.piflow.entity.view.news;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.inveno.piflow.tools.commontools.Tools;

public class NewsDetailImageView extends ImageView implements
		OnDoubleTapListener, OnGestureListener {

	private static int viewWidth = -1;
	private static int viewHeight = -1;

	/** matrix图形的状态 **/
	Matrix matrix = new Matrix();
	/** savedMatrix上一次运算保留结果 **/
	Matrix savedMatrix = new Matrix();
	/** 无(无任何行为的情况下) **/
	static final int NONE = 0;
	/** 拖动 **/
	static final int DRAG = 1;
	/** 缩放 **/
	static final int ZOOM = 2;
	/** mode模式 **/
	int mode = NONE;

	// 记录第一次触的点
	PointF start = new PointF();
	// 缩放时的中间点
	PointF mid = new PointF();
	float oldDist = 1f;

	/** 指定格式保存图像文件 **/
	private Bitmap bitmap;
	/** 画笔 **/
	private Paint paint;
	/** 缩放状态（最小能缩到多少） **/
	private float minScale;
	/** 状态坐标 **/
	private int startX;
	private int startY;
	/** 当前缩放的状态 **/
	private float currentScale;

	private boolean layouted;
	/** 手势 **/
	private GestureDetector gd;

	private ViewPager mViewPager;
	
	private ImageView mBg;

	public void setParentView(ViewPager viewPager) {
		this.mViewPager = viewPager;
	}

	/** 整个页面中的第几张图片 **/
	// private int imageIndex;

	public NewsDetailImageView(Context context) {
		super(context);
		paint = new Paint();
		matrix = new Matrix();
		currentScale = 0;
		layouted = false;
		// imageIndex = -1;
		gd = new GestureDetector(this);
		gd.setOnDoubleTapListener(this);
	}

	// private void setImageIndex(int index) {
	// imageIndex = index;
	// }
	//
	// /**
	// * 防止重复加载
	// */
	// private void loadImage() {
	// if (imageIndex != -1 && this.bitmap == null) {// 保险判断（如果未加载图片才加载）
	// // this.setBitmap(getBitmaoWitgPageIndex(imageIndex));
	// // this.setTag(this.bitmap);
	// }
	// }

	public NewsDetailImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		paint = new Paint();
		matrix = new Matrix();
		currentScale = 0;
		layouted = false;
		// imageIndex = -1;
		gd = new GestureDetector(this);
		gd.setOnDoubleTapListener(this);
	}

	/**
	 * 
	 * 释放图片
	 */
	private void destroyImage() {
		if (this.bitmap != null) {
			synchronized (this.bitmap) {
				this.bitmap.recycle();
				this.bitmap = null;
				this.setTag(null);
			}
		}
	}

	/**
	 * 释放图片，加载图片，目的防止有遗漏
	 */
	protected void tryLoadImage() {
		if (this.bitmap == null) {
			destroyImage();
			// loadImage();
		}
	}

	/**
	 * 计算2点之间的距离
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * 计算2点之间的中间点
	 */

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	// 单击事件
	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {

		if (mViewPager.getChildCount() == 1) {
			((Activity) mViewPager.getContext()).finish();
		}

		return false;
	}

	// 双击间隔中还发生其他的动作
	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}

	// 双击事件
	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// Log.i("double tap");
		Tools.showLog("chenxu", "double tap");
		if (currentScale == minScale) {

			float scale = 3;
			// if(this.bitmap.getWidth() > this.bitmap.getHeight()){
			// scale = (float)this.getHeight() /
			// ((float)this.bitmap.getHeight() * currentScale);
			// }else{
			// scale = (float)this.getWidth() /
			// ((float)this.bitmap.getWidth() * currentScale);
			// }

			// currentScale += 1.3f;
			currentScale += scale;
			matrix.set(savedMatrix);
			matrix.postScale(scale, scale, e.getX(), e.getY());
			savedMatrix.set(matrix);

//			if (mViewPager != null) {
//				mViewPager.disableFilp();
//			}

		} else {
			reset();
//			if (mViewPager != null) {
//				mViewPager.enableFilp();
//			}

		}
		return true;
	}

	/**
	 * chenxu 2012-12-04 (non-Javadoc) 触摸事件
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (bitmap == null)
			return false;
		// _matrixValue 获取矩阵内的值,Matrix 本身就是 9个 float数组封装出来的
		float _matrixValue[] = new float[9];
		matrix.getValues(_matrixValue);

		// 获得本次触摸的事件
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		// CANCEL 也当 UP处理
		switch (event.getAction()) {
		case MotionEvent.ACTION_CANCEL:
			action = MotionEvent.ACTION_UP;
			break;

		}

		switch (action) {
		case MotionEvent.ACTION_DOWN:// 屏幕检测到有手指按下之后就触发到这个事件
			matrix.set(matrix);
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			mode = DRAG;

			break;
		case MotionEvent.ACTION_POINTER_DOWN:// 当屏幕检测到有多个手指同时按下之后，就触发了这个事件。
			// oldDist 以前的距离
			oldDist = spacing(event);
			if (oldDist > 10f) {// 手指的距离超过10个像素，
				savedMatrix.set(matrix); // 保持矩阵状态
				midPoint(mid, event); // 保持2点中间点
				mode = ZOOM; // 进入缩放模式
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP: // 手指离开
			mode = NONE; // 更改操作模式为无

			if (_matrixValue[Matrix.MSCALE_X] < minScale) { // 如果Scale
															// 小于最小值，则重置矩阵，并重绘，
															// 并启用翻页
				reset();
				invalidate();
//				if (mViewPager != null) {
//					mViewPager.enableFilp();
//				}

				break;
			}
			if (_matrixValue[Matrix.MSCALE_X] > (minScale * 5)) { // 如果大于缩放最大值

				savedMatrix.getValues(_matrixValue); // 得到保存的矩阵状态

				float maxScale = (minScale * 5); // 设置缩放值
				matrix = new Matrix(); // 建立新的矩阵
				matrix.setScale(minScale * 5, minScale * 5); // 设置最大缩放值
				matrix.postTranslate((mid.x / 2) // 设置矩阵偏移转换
						- (this.getBitmap().getWidth() * maxScale) / 2,
						(mid.y / 2) - (this.getBitmap().getHeight() * maxScale)
								/ 2);
				break;
			}

			break;
		case MotionEvent.ACTION_MOVE: // 移动
			if (mode == DRAG) { // 如果正在拖拽
				matrix.set(savedMatrix); // 先还原为保存时的状态
				matrix.postTranslate(event.getX() - start.x, event.getY() // 设置偏移，
																			// 因为postTranslate是在基础上偏移，所以必须先还原为保存时的状态
						- start.y);
				// 解决图片移到最后一张放大出现黑色背景的问题
				matrix.getValues(_matrixValue);
				float mtrans_x = _matrixValue[Matrix.MTRANS_X]; // 偏移值
				float mtrans_y = _matrixValue[Matrix.MTRANS_Y];
				float mscale_x = _matrixValue[Matrix.MSCALE_X]; // 缩放值
				float mscale_y = _matrixValue[Matrix.MSCALE_Y];

				float newX = Float.NaN;
				float newY = Float.NaN;

				// 将图片限制在屏幕4个顶点
				if (mtrans_x > 0
						&& (bitmap.getWidth() * mscale_x) > this.getWidth()) {
					newX = 0;
				}
				if (mtrans_y > 0
						&& (bitmap.getHeight() * mscale_y) > this.getHeight()) {
					newY = 0;
				}
				// 图片右下角
				if (mtrans_x < -((bitmap.getWidth() * mscale_x) - this
						.getWidth())) {
					newX = -((bitmap.getWidth() * mscale_x) - this.getWidth());
				}
				if (mtrans_y < -((bitmap.getHeight() * mscale_y) - this
						.getHeight())) {

					newY = -((bitmap.getHeight() * mscale_y) - this.getHeight());
				}

				// 如果图片没有屏幕大
				if ((bitmap.getHeight() * mscale_y) < this.getHeight()) {
					newY = (this.getHeight() / 2)
							- ((bitmap.getHeight() * mscale_y) / 2);
				}

				if ((bitmap.getWidth() * mscale_x) < this.getWidth()) {
					newX = (this.getWidth() / 2)
							- ((bitmap.getWidth() * mscale_x) / 2);
				}

				// 先得到操作前的矩阵值
				matrix.getValues(_matrixValue);

				// 设置新的矩阵状态
				if (Float.compare(newX, Float.NaN) != 0) {
					_matrixValue[Matrix.MTRANS_X] = newX;
				}
				if (Float.compare(newY, Float.NaN) != 0) {
					_matrixValue[Matrix.MTRANS_Y] = newY;
				}
				matrix.setValues(_matrixValue);

				// 如果缩放值为最小，还原一下坐标
				if (currentScale == minScale) {
					reset();
				}
			} else if (mode == ZOOM) { // 如果是缩放模式
				float newDist = spacing(event); // 得到点距离
				if (newDist > 10f) { // 2点距离大于10像素
					matrix.set(savedMatrix); // 先还原状态
					float scale = newDist / oldDist;
					// if (scale<0.8f&&oldDist!=0) {
					// scale=0.8f;
					// //
					// }else if(scale>2f){
					// scale=2f;
					// }
					Tools.showLog("pf", "scale :" + scale);
					// currentScale += scale;
					Tools.showLog("pf", "currentScale :" + currentScale);
					currentScale += scale;
					matrix.postScale(scale, scale, mid.x, mid.y); // postScale
																	// 新的值
//					if (mViewPager != null) {
//						mViewPager.disableFilp();
//					}

				}
			}
			break;
		}
		gd.onTouchEvent(event);
		invalidate();
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);
		if (this.bitmap != null) {
			synchronized (this.bitmap) {
				// 使用 matrix 绘制 bitmap
				canvas.drawBitmap(this.bitmap, matrix, paint);
			}

		}
	}

	/**
	 * 恢复原图
	 */
	private void reset() {

		matrix = new Matrix();
		matrix.setScale(minScale, minScale);
		matrix.postTranslate(startX, startY);
		currentScale = minScale;

	}

	public void setImageBitmap(Bitmap b) {
		Tools.showLog("chenxu", "setImageBitmap" + (b == null));
		this.setBitmap(b);
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (viewWidth == -1 && viewHeight == -1) {
			viewWidth = this.getWidth();
			viewHeight = this.getHeight();
		}
		if (!layouted) {
			this.setBitmap(this.bitmap);
			layouted = true;
		}

	}

	private void setBitmap(Bitmap bitmap) {

		if (bitmap == null)
			return;
		
		if(mBg!=null){
			mBg.setVisibility(View.GONE);
		}
		
		this.bitmap = bitmap;

		// 图片处于加载中的时候就显示上一张图片，
		setImageResource(0);
		float scaleRate = 1.0f;
		int x = 0;
		int y = 0;

		// 计算自适配的大小，确定初始矩阵状态
		int width = this.getWidth() == 0 ? viewWidth : this.getWidth();
		int height = this.getHeight() == 0 ? viewHeight : this.getHeight();

		float scaleX = ((float) width / (float) bitmap.getWidth());
		float scaleY = ((float) height / (float) bitmap.getHeight());
		scaleRate = Math.min(scaleX, scaleY);

		if (scaleX < scaleY) {
			x = 0;
			y = (this.getHeight() / 2)
					- (int) ((bitmap.getHeight() * scaleRate) / 2);
		} else {
			x = (this.getWidth() / 2)
					- (int) ((bitmap.getWidth() * scaleRate) / 2);
			y = 0;
		}

		startX = x;
		startY = y;

		// 记录最小缩放值
		minScale = scaleRate;
		// 设置矩阵状态
		matrix.setScale(scaleRate, scaleRate);
		matrix.postTranslate(x, y);
		// 当前缩放值
		currentScale = minScale;

//		FrameLayout.LayoutParams lp = new LayoutParams(
//				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//
//		setLayoutParams(lp);

		// invalidate();
		postInvalidate();// 不是即时重绘，他是发送一条重绘的消息给UI线程。

	}

	// GestureDetector
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub

		return false;
	}
	
	public void setBg(ImageView bg){
		this.mBg = bg;
	}

}
