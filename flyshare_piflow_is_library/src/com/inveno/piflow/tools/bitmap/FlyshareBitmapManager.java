package com.inveno.piflow.tools.bitmap;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.inveno.piflow.R;
import com.inveno.piflow.tools.android.BitmapTools;
import com.inveno.piflow.tools.bitmap.BitmapDisplayConfig.AnimationType;
import com.inveno.piflow.tools.commontools.MkdirFileTools;
import com.inveno.piflow.tools.commontools.StringTools;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 封装Bitmap的异步加载，根据最新4.0google文档改正之前的弱引用软引用
 * 
 * @author blueming.wu
 * @date 2012-12-25
 */
public class FlyshareBitmapManager {

	/** 标识图片是否下载成功 */
	public static final String LOAD_BMP_OK = "ok";
	public static final String LOAD_BMP_FAIL = "fail";

	/** 跟imageView各项参数绑定的配置信息 */
	private HashMap<String, BitmapDisplayConfig> configMap = new HashMap<String, BitmapDisplayConfig>();

	/** 公共配置信息，可自定义 */
	private BitmapConfig mConfig;

	/** 缓存区域，包含SD卡和内存 */
	private static ImageCache mImageCache;

	/** 让任务暂停 */
	private boolean mExitTasksEarly = false;
	private boolean mPauseWork = false;

	/** 锁 */
	private final Object mPauseWorkLock = new Object();
	private Context mContext;

	/** 线程池 */
	private static ExecutorService bitmapLoadAndDisplayExecutor;

	private static FlyshareBitmapManager mFlyshareBitmap;

	// 配置信息加载。。。。从这里开始
	private FlyshareBitmapManager(Context context) {
		mContext = context;
		mConfig = new BitmapConfig(context);

		// configDiskCachePath(BitmapTools.getDiskCacheDir(context, "flyCache")
		// .getAbsolutePath());// 配置缓存路径
		configIBitmapDisplay(new BitmapDisplayImpl());// 配置显示器
		configDownlader(new HttpDownloadImpl());// 配置下载器
	}

	/**
	 * 创建FlyshareBitmap
	 * 
	 * @param ctx
	 * @return
	 */
	public static FlyshareBitmapManager create(Context ctx) {
		Tools.showLogB("初始化FlyshareBitmapManager Context:11111111111111111111"
				+ ctx);
		if (mFlyshareBitmap == null) {
			mFlyshareBitmap = new FlyshareBitmapManager(
					ctx.getApplicationContext());
			mFlyshareBitmap.init();
		}
		return mFlyshareBitmap;
	}

	/**
	 * 创建FlyshareBitmap，此构造函数跟其他构造函数不同，为重新初始化所有配置，
	 * 每个界面或者服务的主入口才需要用到此构造，其余二层入口可直接使用create（context ctx） 构造函数
	 * 
	 * 此方法会初始化sd存储路径，一般不使用
	 * 
	 * @param ctx
	 * @param diskCachePath
	 *            SD卡缓存路径
	 * @return
	 */
	public static FlyshareBitmapManager create(Context ctx, String diskCachePath) {
		// 如果带了文件夹配置，就要重新配置文件夹信息
		Tools.showLogB("初始化FlyshareBitmapManager Context:222222222222222222"
				+ ctx);
		if (mFlyshareBitmap == null) {
			mFlyshareBitmap = new FlyshareBitmapManager(ctx);

		}
		mFlyshareBitmap.configDiskCachePath(diskCachePath);
		return mFlyshareBitmap;
	}

	/**
	 * 设置默认的图片的高度
	 * 
	 * @param bitmapHeight
	 */
	public FlyshareBitmapManager configBitmapMaxHeight(int bitmapHeight) {
		Tools.showLogB("设置的高为：" + bitmapHeight);
		mConfig.defaultDisplayConfig.setBitmapHeight(bitmapHeight);
		return this;
	}

	/**
	 * 配置默认图片的的宽度
	 * 
	 * @param bitmapHeight
	 */
	public FlyshareBitmapManager configBitmapMaxWidth(int bitmapWidth) {
		mConfig.defaultDisplayConfig.setBitmapWidth(bitmapWidth);
		return this;
	}

	/**
	 * 设置下载器，比如通过其他协议去网络读取图片的时候可以设置这项 只要你提供的下载工具类提供了此接口的方法
	 * 
	 * @param downlader
	 * @return
	 */
	public FlyshareBitmapManager configDownlader(IDownloader downlader) {
		mConfig.downloader = downlader;
		return this;
	}

	/**
	 * 设置显示器，比如在显示的过程中显示动画等
	 * 
	 * @param displayer
	 * @return
	 */
	public FlyshareBitmapManager configIBitmapDisplay(IBitmapDisplay displayer) {
		mConfig.displayer = displayer;
		return this;
	}

	/**
	 * 设定图片展示时间，当为负或者0时，无淡入淡出动画效果（单位为毫秒） （！！！！暂时未实现）
	 * 
	 * @param duration
	 */
	public void configAnimDuration(int duration) {

	}

	/**
	 * 设定图片展示时间，当为负或者0时，无淡入淡出动画效果（单位为毫秒） （！！！！暂时未实现）
	 * 
	 * @param 0 淡入淡出,1 用户自定义(必须自定义动画),2默认无动画
	 */
	public void configAnim(int animType) {
		mConfig.defaultDisplayConfig
				.setAnimationType(BitmapDisplayConfig.AnimationType.fadeIn);
	}

	/**
	 * 配置SD卡缓存路径
	 * 
	 * @param strPath
	 * @return
	 */
	public FlyshareBitmapManager configDiskCachePath(String strPath) {
		if (!MkdirFileTools.sdCardExist()) {
			strPath = BitmapTools.getDiskCacheDir(mContext, "flyCache")
					.getAbsolutePath();
			mFlyshareBitmap.configDiskCacheSize(1024 * 1024 * 10);
			Tools.showLogA("无sd卡，去data路径：" + strPath);
		}
		if (!TextUtils.isEmpty(strPath)) {
			mConfig.cachePath = strPath;
			mFlyshareBitmap.init();
		}
		return this;
	}

	// /**
	// * 配置内存缓存大小 大于2MB以上有效
	// *
	// * @param size
	// * 缓存大小
	// */
	// private FlyshareBitmapManager configMemoryCacheSize(int size) {
	// mConfig.memCacheSize = size;
	// return this;
	// }
	//
	// /**
	// * 设置应缓存的在APK总内存的百分比，优先级大于configMemoryCacheSize
	// *
	// * @param percent
	// * 百分比，值的范围是在 0.05 到 0.8之间
	// */
	// private FlyshareBitmapManager configMemoryCachePercent(float percent) {
	// mConfig.memCacheSizePercent = percent;
	// return this;
	// }

	/**
	 * 设置SD卡缓存大小 5MB 以上有效
	 * 
	 * @param size
	 */
	private FlyshareBitmapManager configDiskCacheSize(int size) {
		mConfig.diskCacheSize = size;
		return this;
	}

	// /**
	// * 设置加载图片的线程并发数量
	// *
	// * @param size
	// */
	// private FlyshareBitmapManager configBitmapLoadThreadSize(int size) {
	// if (size >= 1)
	// mConfig.poolSize = size;
	// return this;
	// }

	/**
	 * 这个方法必须被调用后 FlyshareBitmapManager 配置才能有效
	 * 
	 * @return
	 */
	private FlyshareBitmapManager init() {

		mConfig.init();

		ImageCache.ImageCacheParams imageCacheParams = new ImageCache.ImageCacheParams();
		if (mConfig.memCacheSizePercent > 0.05
				&& mConfig.memCacheSizePercent < 0.8) {
			imageCacheParams.setMemCacheSizePercent(mContext,
					mConfig.memCacheSizePercent);
		} else {
			if (mConfig.memCacheSize > 1024 * 1024 * 2) {
				imageCacheParams.setMemCacheSize(mConfig.memCacheSize);
			} else {
				// 设置默认的内存缓存大小
				imageCacheParams.setMemCacheSizePercent(mContext, 0.3f);
			}
		}
		if (mConfig.diskCacheSize > 1024 * 1024 * 5)
			imageCacheParams.setDiskCacheSize(mConfig.diskCacheSize);

		mImageCache = new ImageCache(imageCacheParams);
		if (StringTools.isNotEmpty(mConfig.cachePath))
			mImageCache.setDiskCacheDir(mConfig.cachePath);

		bitmapLoadAndDisplayExecutor = Executors.newFixedThreadPool(
				mConfig.poolSize, new ThreadFactory() {

					@Override
					public Thread newThread(Runnable r) {
						Thread t = new Thread(r);
						// 设置线程的优先级别，让线程先后顺序执行（级别越高，抢到cpu执行的时间越多）
						t.setPriority(Thread.NORM_PRIORITY);
						return t;
					}
				});

		new CacheExecutecTask()
				.execute(CacheExecutecTask.MESSAGE_INIT_DISK_CACHE);

		return this;
	}

	/**
	 * 显示url对应的图片到控件
	 * 
	 * @param imageView
	 * @param uri
	 */
	public void display(ImageView imageView, String uri) {
		doDisplay(imageView, uri, null);
	}

	/**
	 * 
	 * @param imageView
	 * @param uri
	 * @param loadingRes
	 * @param loadingFailRes
	 * @param imageWidth
	 * @param imageHeight
	 */
	public void display(ImageView imageView, String uri, int loadingRes,
			int loadingFailRes, int imageWidth, int imageHeight) {
		display(imageView, uri, loadingRes, loadingFailRes, imageWidth,
				imageHeight, CompressFormat.JPEG, true);
	}

	/**
	 * 自定一些参数
	 * 
	 * @param imageView
	 * @param uri
	 * @param loadingRes
	 * @param loadingFailRes
	 * @param imageWidth
	 * @param imageHeight
	 * @param format
	 */
	public void display(ImageView imageView, String uri, int loadingRes,
			int loadingFailRes, int imageWidth, int imageHeight,
			CompressFormat format, boolean saveToSdCard) {
		String key = imageWidth + "_" + imageHeight + "_" + format.toString()
				+ loadingRes + saveToSdCard;
		BitmapDisplayConfig displayConfig = configMap.get(key);
		if (displayConfig == null) {
			displayConfig = getDisplayConfig();
			displayConfig.setBitmapHeight(imageHeight);
			displayConfig.setBitmapWidth(imageWidth);
			displayConfig.setLoadingRes(loadingRes);
			displayConfig.setLoadfailRes(loadingFailRes);
			displayConfig.setSaveToSdcard(saveToSdCard);
			if (saveToSdCard) {
				displayConfig.setAnimationType(AnimationType.defaultType);
			}
			
			displayConfig.setFormat(format);
			configMap.put(key, displayConfig);
		}

		doDisplay(imageView, uri, displayConfig);
	}

	/**
	 * 是否显示原图，如果为true，则直接显示下载的原始图片
	 * 
	 * @param imageView
	 * @param uri
	 * @param orignal
	 */
	public void display(ImageView imageView, String uri, boolean orignal) {
		BitmapDisplayConfig displayConfig = getDisplayConfig();
		// displayConfig.setOrignal(orignal);
		doDisplay(imageView, uri, displayConfig);
	}

	/**
	 * 扩展方法
	 * 
	 * @param imageView
	 * @param uri
	 * @param imageWidth
	 * @param imageHeight
	 */
	public void display(ImageView imageView, String uri, int imageWidth,
			int imageHeight) {
		display(imageView, uri, imageWidth, imageHeight, CompressFormat.JPEG);
	}

	/**
	 * 瀑布流专用图片显示，可以先设置默认图片的padding，图片下载成功则铺满 图片不保存到sd卡，直接显示
	 * 
	 * @param imageView
	 * @param uri
	 * @param imageWidth
	 * @param imageHeight
	 */
	public void displayForFlow(ImageView imageView, String uri, int loadingRes,
			int loadingFailRes, int imageWidth, int imageHeight,
			int defaultImgWidth, boolean saveToSdCard) {
//		imageView.setPadding((imageWidth - defaultImgWidth) / 2,
//				(imageHeight - defaultImgWidth) / 2,
//				(imageWidth - defaultImgWidth) / 2,
//				(imageHeight - defaultImgWidth) / 2);

		display(imageView, uri, loadingRes, loadingFailRes, imageWidth,
				imageHeight, CompressFormat.JPEG, saveToSdCard);
	}

	/**
	 * 扩展方法
	 * 
	 * @param imageView
	 * @param uri
	 * @param imageWidth
	 * @param imageHeight
	 */
	public void display(ImageView imageView, String uri, int imageWidth,
			int imageHeight, CompressFormat format) {
		display(imageView, uri, 0, 0, imageWidth, imageHeight, format, true);
	}

	public void display(ImageView imageView, String uri,
			BitmapDisplayConfig config) {
		doDisplay(imageView, uri, config);
	}

	/**
	 * 只下载图片到本地，并不显示,没下载完成通知调用方法的handler已下载完
	 * 
	 * @param url
	 */
	public Bitmap downloadAndSave(String url, BitmapDisplayConfig config,
			Handler handler) {
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		if (config == null)
			config = mConfig.defaultDisplayConfig;
		// 如果缓存没取到，则去下载，下载完成后会发送消息100，此时再去缓存取
		final BitmapLoadTask task = new BitmapLoadTask(config, handler);

		int apiLevel = android.os.Build.VERSION.SDK_INT;
		if (apiLevel > 11) {
			task.executeOnExecutor(bitmapLoadAndDisplayExecutor, url);
		} else
			task.execute(url);
		return null;
	}

	/**
	 * 保存图片到默认路径，不在子线程执行，config传null时存在默认文件夹下
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap DownloadAndSaveUithread(String url, BitmapDisplayConfig config) {
		Tools.showLogB("开始下载图片保存");
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		if (config == null)
			config = mConfig.defaultDisplayConfig;

		Bitmap bitmap = null;
		// 如果内存或者SD缓存中存在，则直接去取存在的图片（当需要取原图时，此步骤不执行）
		if (mImageCache != null) {
			bitmap = mImageCache.getBitmapFromMemCache(url);
			if (bitmap == null)
				bitmap = mImageCache.getBitmapFromDiskCache(url);
			if (bitmap == null)
				bitmap = processBitmap(url, config);
		}

		if (mImageCache != null)
			mImageCache.addBitmapToCache(url, bitmap, config);
		return bitmap;
	}

	/**
	 * 保存默认图片到图片缓存
	 * 
	 * @param url
	 */
	public void saveBitmapToCache(String url, Bitmap bitmap,
			BitmapDisplayConfig config) {
		if (TextUtils.isEmpty(url)) {
			return;
		}
		if (config == null)
			config = mConfig.defaultDisplayConfig;
		if (mImageCache != null)
			mImageCache.addBitmapToCache(url, bitmap, config);
	}

	/**
	 * 从cache中取图片，取到返回并放入内存，取不到则返回null
	 * 
	 * @param url
	 * @param config
	 * @return
	 */
	public Bitmap getBitmapFromCache(String url, BitmapDisplayConfig config) {
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		if (config == null)
			config = mConfig.defaultDisplayConfig;

		Bitmap bitmap = null;
		// 如果内存或者SD缓存中存在，则直接去取存在的图片（当需要取原图时，此步骤不执行）
		if (mImageCache != null) {
			bitmap = mImageCache.getBitmapFromMemCache(url);
			if (bitmap == null)
				bitmap = mImageCache.getBitmapFromDiskCache(url);
		}

		if (mImageCache != null)
			mImageCache.addBitmapToCache(url, bitmap, config);
		return bitmap;
	}

	/**
	 * 加载图片逻辑
	 * 
	 * @param imageView
	 * @param uri
	 * @param displayConfig
	 */
	private void doDisplay(ImageView imageView, String uri,
			BitmapDisplayConfig displayConfig) {
		if (TextUtils.isEmpty(uri) || imageView == null) {
			return;
		}

		if (displayConfig == null)
			displayConfig = mConfig.defaultDisplayConfig;

		Bitmap bitmap = null;
		// 如果取原图则不去内存中取
		if (mImageCache != null) {
			bitmap = mImageCache.getBitmapFromMemCache(uri);
		}

		if (bitmap != null) {
			imageView.setBackgroundResource(R.color.waterwall_photoshow_img_bg);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageView.setImageBitmap(bitmap);
			imageView.setTag(R.string.load_bitmap_key, LOAD_BMP_OK);
		} else if (checkImageTask(uri, imageView)) {

			final BitmapLoadAndDisplayTask task = new BitmapLoadAndDisplayTask(
					imageView, displayConfig);
			// 如果默认加载图片有值，即值不为0 设置默认图片
			Resources res = mContext.getResources();

			Bitmap bmp = null;
			if (0 != displayConfig.getLoadingRes()) {
				bmp = BitmapFactory.decodeResource(res,
						displayConfig.getLoadingRes());
			}
			final AsyncDrawable asyncDrawable = new AsyncDrawable(res, bmp,
					task);
			if (asyncDrawable != null){
				imageView.setScaleType(ScaleType.CENTER);
				imageView.setImageDrawable(asyncDrawable);
			}
				

			int apiLevel = android.os.Build.VERSION.SDK_INT;
			if (apiLevel > 11) {

				task.executeOnExecutor(bitmapLoadAndDisplayExecutor, uri);

			} else
				task.execute(uri);
		}
	}

	/**
	 * Bitmap的配置信息
	 * 
	 * @return
	 */
	private BitmapDisplayConfig getDisplayConfig() {
		BitmapDisplayConfig config = new BitmapDisplayConfig();
		config.setAnimation(mConfig.defaultDisplayConfig.getAnimation());
		config.setAnimationType(mConfig.defaultDisplayConfig.getAnimationType());
		config.setBitmapHeight(mConfig.defaultDisplayConfig.getBitmapHeight());
		config.setBitmapWidth(mConfig.defaultDisplayConfig.getBitmapWidth());
		config.setLoadingRes(mConfig.defaultDisplayConfig.getLoadingRes());
		config.setLoadfailRes(mConfig.defaultDisplayConfig.getLoadfailRes());
		return config;
	}

	private void initDiskCacheInternal() {
		if (mImageCache != null) {
			mImageCache.initDiskCache();
		}
		if (mConfig != null && mConfig.bitmapProcess != null) {
			mConfig.bitmapProcess.initHttpDiskCache();
		}
	}

	private void clearCacheInternal() {
		if (mImageCache != null) {
			mImageCache.clearCache();
		}
		if (mConfig != null && mConfig.bitmapProcess != null) {
			mConfig.bitmapProcess.clearCacheInternal();
		}
	}

	private void clearMemoryCache() {
		if (mImageCache != null) {
			mImageCache.clearMemoryCache();
		}
	}

	private void flushCacheInternal() {
		if (mImageCache != null) {
			mImageCache.flush();
		}
		if (mConfig != null && mConfig.bitmapProcess != null) {
			mConfig.bitmapProcess.flushCacheInternal();
		}
	}

	private void closeCacheInternal() {
		if (mImageCache != null) {
			mImageCache.close();
			mImageCache = null;
		}
		if (mConfig != null && mConfig.bitmapProcess != null) {
			mConfig.bitmapProcess.clearCacheInternal();
		}
	}

	/**
	 * 网络加载bitmap
	 * 
	 * @param data
	 * @return
	 */
	private Bitmap processBitmap(String uri, BitmapDisplayConfig config) {
		if (mConfig != null && mConfig.bitmapProcess != null) {
			return mConfig.bitmapProcess.processBitmap(uri, config);
		}
		return null;
	}

	/**
	 * (！！！！！此方法暂时不能使用，未完善)
	 * 
	 * @param exitTasksEarly
	 */
	public void setExitTasksEarly(boolean exitTasksEarly) {
		mExitTasksEarly = exitTasksEarly;
	}

	/**
	 * activity onResume的时候调用这个方法，让加载图片线程继续 (！！！！！此方法暂时不能使用，未完善)
	 */
	public void onResume() {
		setExitTasksEarly(false);
	}

	/**
	 * activity onPause的时候调用这个方法，让线程暂停 (！！！！！此方法暂时不能使用，未完善)
	 */
	public void onPause() {
		setExitTasksEarly(true);
		flushCache();
	}

	/**
	 * activity onDestroy的时候调用这个方法，释放缓存
	 */
	public void onDestroy() {
		closeCache();
		// 释放单例
		// mFlyshareBitmap = null;
	}

	/**
	 * 清除缓存 (！！！！！此方法暂时不能使用，未完善)
	 */
	public void clearAllCache() {
		new CacheExecutecTask().execute(CacheExecutecTask.MESSAGE_CLEAR);
	}

	/**
	 * 清除内存缓存
	 */
	public void clearMemeoryCache() {
		new CacheExecutecTask().execute(CacheExecutecTask.MESSAGE_CLEAR_MEMORY);
	}

	/**
	 * 刷新缓存 (！！！！！此方法暂时不能使用，未完善)
	 */
	public void flushCache() {
		new CacheExecutecTask().execute(CacheExecutecTask.MESSAGE_FLUSH);
	}

	/**
	 * 关闭缓存 (！！！！！此方法暂时不能使用，未完善)
	 */
	public void closeCache() {
		new CacheExecutecTask().execute(CacheExecutecTask.MESSAGE_CLOSE);
	}

	/**
	 * 退出正在加载的线程，程序退出的时候调用该方法 (！！！！！此方法暂时不能使用，未完善)
	 * 
	 * @param exitTasksEarly
	 */
	public void exitTasksEarly(boolean exitTasksEarly) {
		mExitTasksEarly = exitTasksEarly;
		if (exitTasksEarly)
			pauseWork(false);// 让暂停的线程结束
	}

	/**
	 * 暂停正在加载的线程，监听listview或者gridview正在滑动的时候条用词方法
	 * 
	 * @param pauseWork
	 *            true停止暂停线程，false继续线程
	 */
	public void pauseWork(boolean pauseWork) {
		synchronized (mPauseWorkLock) {
			mPauseWork = pauseWork;
			if (!mPauseWork) {
				mPauseWorkLock.notifyAll();
			}
		}
	}

	private static BitmapLoadAndDisplayTask getBitmapTaskFromImageView(
			ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}

	/**
	 * 检测 imageView中是否已经有线程在运行
	 * 
	 * @param data
	 * @param imageView
	 * @return true 没有 false 有线程在运行了
	 */
	public static boolean checkImageTask(Object data, ImageView imageView) {
		final BitmapLoadAndDisplayTask bitmapWorkerTask = getBitmapTaskFromImageView(imageView);

		if (bitmapWorkerTask != null) {
			final Object bitmapData = bitmapWorkerTask.data;
			if (bitmapData == null || !bitmapData.equals(data)) {
				bitmapWorkerTask.cancel(true);

			} else {
				// 同一个线程已经在执行
				return false;
			}
		}
		return true;
	}

	/**
	 * 检测 imageView中是否已经有线程在运行
	 * 
	 * 
	 * @param imageView
	 * @return true 没有 false 有线程在运行了
	 */
	public static boolean cancelWork(ImageView imageView) {
		final BitmapLoadAndDisplayTask bitmapWorkerTask = getBitmapTaskFromImageView(imageView);

		if (bitmapWorkerTask != null) {
			bitmapWorkerTask.cancel(true);
			Tools.showLog("meitu", imageView + "取消了一个线程");
		}
		return true;
	}

	/**
	 * 释放资源
	 */
	public void release() {
		mFlyshareBitmap = null;
		bitmapLoadAndDisplayExecutor.shutdown();
	}

	/**
	 * 自定义图片缓存类，对应一个下载此位图的线程，保证显示不错乱
	 * 
	 * @author blueming.wu
	 * 
	 */
	private static class AsyncDrawable extends BitmapDrawable {
		private final WeakReference<BitmapLoadAndDisplayTask> bitmapWorkerTaskReference;

		public AsyncDrawable(Resources res, Bitmap bitmap,
				BitmapLoadAndDisplayTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapWorkerTaskReference = new WeakReference<BitmapLoadAndDisplayTask>(
					bitmapWorkerTask);
		}

		public BitmapLoadAndDisplayTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
	}

	/**
	 * @description 缓存操作的异步任务
	 */
	private class CacheExecutecTask extends AsyncTask<Object, Void, Void> {
		public static final int MESSAGE_CLEAR = 0;
		public static final int MESSAGE_INIT_DISK_CACHE = 1;
		public static final int MESSAGE_FLUSH = 2;
		public static final int MESSAGE_CLOSE = 3;
		public static final int MESSAGE_CLEAR_MEMORY = 4;

		@Override
		protected Void doInBackground(Object... params) {
			switch ((Integer) params[0]) {
			case MESSAGE_CLEAR:
				clearCacheInternal();
				break;
			case MESSAGE_INIT_DISK_CACHE:
				initDiskCacheInternal();
				break;
			case MESSAGE_FLUSH:
				clearMemoryCache();
				flushCacheInternal();
				break;
			case MESSAGE_CLOSE:
				clearMemoryCache();
				closeCacheInternal();
				break;
			case MESSAGE_CLEAR_MEMORY:
				clearMemoryCache();
				break;
			}
			return null;
		}
	}

	/**
	 * 只下载保存，并不显示的线程
	 */
	private class BitmapLoadTask extends AsyncTask<Object, Void, Void> {

		private Handler handler;
		private Object data;
		private final BitmapDisplayConfig displayConfig;

		public BitmapLoadTask(BitmapDisplayConfig config, Handler handler) {
			displayConfig = config;
			this.handler = handler;
		}

		@Override
		protected void onCancelled() {
			// API 11以上才有onCancelled（里面带参数）的方法
			super.onCancelled();
			synchronized (mPauseWorkLock) {
				mPauseWorkLock.notifyAll();
			}
		}

		@Override
		protected Void doInBackground(Object... params) {
			data = params[0];
			final String dataString = String.valueOf(data);
			Bitmap bitmap = null;

			synchronized (mPauseWorkLock) {
				while (mPauseWork && !isCancelled()) {
					try {
						mPauseWorkLock.wait();
					} catch (InterruptedException e) {
					}
				}
			}

			// 如果SD中存在，则直接去取存在的图片
			if (mImageCache != null && !isCancelled() && !mExitTasksEarly) {
				bitmap = mImageCache.getBitmapFromDiskCache(dataString);
			}

			// 如果内存和本地没有取到，则开始去url地址去下载
			if (bitmap == null && !isCancelled() && !mExitTasksEarly) {
				bitmap = processBitmap(dataString, displayConfig);
			}

			if (bitmap != null && mImageCache != null) {
				mImageCache.addToSdcache(dataString, bitmap, displayConfig);
				if (handler != null) {
					Message msg = handler.obtainMessage(100);
					msg.obj = data;
					msg.sendToTarget();
				}
			} else if (bitmap == null) {
				if (handler != null) {
					// 发送下载图片失败消息
					Message msg = handler.obtainMessage(101);
					msg.obj = data;
					msg.sendToTarget();
				}
			}

			return null;
		}

	}

	/**
	 * bitmap下载显示的线程，修改自 google官方的最新关于bitmap的文档
	 * 
	 * @author blueming.wu
	 * @date 2012-12-25
	 */
	private class BitmapLoadAndDisplayTask extends
			AsyncTask<Object, Void, Bitmap> {
		private Object data;
		private final WeakReference<ImageView> imageViewReference;
		private final BitmapDisplayConfig displayConfig;

		public BitmapLoadAndDisplayTask(ImageView imageView,
				BitmapDisplayConfig config) {
			imageViewReference = new WeakReference<ImageView>(imageView);
			displayConfig = config;
		}

		@Override
		protected Bitmap doInBackground(Object... params) {
			data = params[0];
			final String dataString = String.valueOf(data);
			Bitmap bitmap = null;

			synchronized (mPauseWorkLock) {
				while (mPauseWork && !isCancelled()) {
					try {
						mPauseWorkLock.wait();
					} catch (InterruptedException e) {
					}
				}
			}

			// 如果内存或者SD缓存中存在，则直接去取存在的图片（当需要取原图时，此步骤不执行）

			if (displayConfig.isSaveToSdcard() && mImageCache != null
					&& !isCancelled() && getAttachedImageView() != null
					&& !mExitTasksEarly) {

				bitmap = mImageCache.getBitmapFromDiskCache(dataString);
				Tools.showLogB("本地获取到的图片:"+bitmap);
			}

			// 如果内存和本地没有取到，则开始去url地址去下载
			if (bitmap == null && !isCancelled()
					&& getAttachedImageView() != null && !mExitTasksEarly) {
				bitmap = processBitmap(dataString, displayConfig);
			}

			// 下载成功后放入cache中
			if (bitmap != null && mImageCache != null) {
				mImageCache.addBitmapToCache(dataString, bitmap, displayConfig);
			}

			// if (bitmap == null) {
			// mImageCache.clearMemoryCache();
			// mImageCache.flush();
			// }

			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			
			if (isCancelled() || mExitTasksEarly) {
				bitmap = null;
			}
			
			// 判断线程和当前的imageview是否是匹配
			final ImageView imageView = getAttachedImageView();
			Tools.showLogB("isCancelled()："+isCancelled()+"  imageView :"+imageView +" bitmap :"+bitmap );
			if (bitmap != null && imageView != null) {
				mConfig.displayer.loadCompletedisplay(imageView, bitmap,
						displayConfig);
			} else if (bitmap == null && imageView != null) {
				mConfig.displayer.loadFailDisplay(imageView,
						displayConfig.getLoadfailRes());
			}
		}

		@Override
		protected void onCancelled() {
			// API 11以上才有onCancelled（里面带参数）的方法
			super.onCancelled();
			Tools.showLogA("执行了cancle方法！");
			if (android.os.Build.VERSION.SDK_INT > 11)
				mConfig.downloader.disConnection();
			synchronized (mPauseWorkLock) {
				mPauseWorkLock.notifyAll();
			}
		}

		/**
		 * 获取线程匹配的imageView,防止出现闪动的现象
		 * 
		 * @return
		 */
		private ImageView getAttachedImageView() {
			final ImageView imageView = imageViewReference.get();
			final BitmapLoadAndDisplayTask bitmapWorkerTask = getBitmapTaskFromImageView(imageView);

			if (this == bitmapWorkerTask) {
				return imageView;
			}

			return null;
		}

	}

	/**
	 * @title 配置信息
	 */
	private class BitmapConfig {

		public String cachePath;

		/** 配置显示监听 */
		public IBitmapDisplay displayer;

		/** 图片下载器 */
		public IDownloader downloader;
		public BitmapProcess bitmapProcess;
		/** 图片显示的一些配置信息 */
		public BitmapDisplayConfig defaultDisplayConfig;
		public float memCacheSizePercent;// 缓存百分比，android系统分配给每个apk内存的大小
		public int memCacheSize;// 内存缓存百分比
		public int diskCacheSize;// SD卡百分比
		public int poolSize = 3;// 默认的线程池线程并发数量
		public int originalDiskCacheSize = 30 * 1024 * 1024;// 20MB

		public BitmapConfig(Context context) {
			defaultDisplayConfig = new BitmapDisplayConfig();

			defaultDisplayConfig.setAnimation(null);
			defaultDisplayConfig
					.setAnimationType(BitmapDisplayConfig.AnimationType.defaultType);

			// 设置图片的显示最大尺寸（为屏幕的大小）
			DisplayMetrics displayMetrics = context.getResources()
					.getDisplayMetrics();
			int defaultWidth = (int) Math.floor(displayMetrics.widthPixels);
			int defaultHeight = (int) Math.floor(displayMetrics.heightPixels);
			defaultDisplayConfig.setBitmapHeight(2 * defaultWidth);
			defaultDisplayConfig.setBitmapWidth(2 * defaultHeight);

			BitmapDisplayConfig.defaultWidth = defaultWidth;
			BitmapDisplayConfig.defaultHeight = defaultHeight;
		}

		public void init() {
			if (downloader == null)
				downloader = new HttpDownloadImpl();

			if (displayer == null) {
				displayer = new BitmapDisplayImpl();
			}

			bitmapProcess = new BitmapProcess(downloader, cachePath,
					originalDiskCacheSize);
		}

	}
}
