package com.inveno.piflow.tools.bitmap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

import com.inveno.piflow.tools.android.BitmapTools;
import com.inveno.piflow.tools.commontools.StringTools;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 综合处理SD和内存的缓存，处理Bitmap内存缓存机制思想：指定一块自定义的内存空间大小，以LinkedMap的数据
 * 结构存储，当每次查询或者使用时就吧这个缓存的Bitmap移动到头部，这样可以保证使用次数最多的永远处在数据
 * 结构的前部分，当每次下载一个新对象时吧它存入缓存中，每次存入都回去判断缓存大小是否超出预计值，如果超出 则删除最后一个位置的值
 * 
 * @author blueming.wu
 * @date 2012-12-26
 */
public class ImageCache {

	private static final String TAG = "ImageCache";

	// 默认的内存缓存大小
	private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 1024 * 7; // 8MB

	// 默认的磁盘缓存大小
	private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 50; // 50MB

	private static final int DEFAULT_COMPRESS_QUALITY = 70;
	private static final int DISK_CACHE_INDEX = 0;

	// 各种开关
	private static final boolean DEFAULT_MEM_CACHE_ENABLED = true;
	private static final boolean DEFAULT_DISK_CACHE_ENABLED = true;
	private static final boolean DEFAULT_CLEAR_DISK_CACHE_ON_START = true;
	private static final boolean DEFAULT_INIT_DISK_CACHE_ON_CREATE = false;

	/** Sd卡缓存图片的机制 */
	private LruSdCache mDiskLruCache;

	/** 内存缓存图片机制 */
	private LruCache<String, Bitmap> mMemoryCache;
	private ImageCacheParams mCacheParams;
	private final Object mDiskCacheLock = new Object();
	private boolean mDiskCacheStarting = true;

	/**
	 * 创建一个自定义管理缓存的对象，以指定的各种配置信息
	 * 
	 * @param cacheParams
	 */
	public ImageCache(ImageCacheParams cacheParams) {
		init(cacheParams);
	}

	/**
	 * 初始化给定的配置信息
	 * 
	 * @param cacheParams
	 */
	private void init(ImageCacheParams cacheParams) {
		mCacheParams = cacheParams;

		if (mCacheParams.memoryCacheEnabled) {
			mMemoryCache = new LruCache<String, Bitmap>(
					mCacheParams.memCacheSize) {
				/**
				 * 估计的字节值与实际大小差距暂未测试，重写此sizeOf方法
				 */
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					return BitmapTools.getBitmapSize(bitmap);
				}

				@Override
				protected void entryRemoved(boolean evicted, String key,
						Bitmap oldValue, Bitmap newValue) {
					super.entryRemoved(evicted, key, oldValue, newValue);
//					Tools.showLogA("entry remove value：" + key + " evicted:"
//							+ evicted);
					if (newValue != null)
						newValue.recycle();

				}

			};
		}

		// By default the disk cache is not initialized here as it should be
		// initialized
		// on a separate thread due to disk access.
		if (cacheParams.initDiskCacheOnCreate) {
			// Set up disk cache
			initDiskCache();
		}
	}

	/**
	 * Initializes the disk cache. Note that this includes disk access so this
	 * should not be executed on the main/UI thread. By default an ImageCache
	 * does not initialize the disk cache when it is created, instead you should
	 * call initDiskCache() to initialize it on a background thread.
	 */
	public void initDiskCache() {
		// Set up disk cache
		synchronized (mDiskCacheLock) {
			if (mDiskLruCache == null || mDiskLruCache.isClosed()) {
				if (mCacheParams.diskCacheEnabled && diskCacheDir != null) {
					if (!diskCacheDir.exists()) {
						diskCacheDir.mkdirs();
					}
					if (BitmapTools.getUsableSpace(diskCacheDir) > mCacheParams.diskCacheSize) {
						try {
							mDiskLruCache = LruSdCache.open(diskCacheDir, 1, 1,
									mCacheParams.diskCacheSize);
						} catch (final IOException e) {
							diskCacheDir = null;
							Tools.showLogB("initDiskCache - " + e);
						}
					}
				}
			}
			mDiskCacheStarting = false;
			mDiskCacheLock.notifyAll();
		}
	}

	/**
	 * Adds a bitmap to both memory and disk cache.
	 * 
	 * @param data
	 *            Unique identifier for the bitmap to store
	 * @param bitmap
	 *            The bitmap to store
	 */
	public void addBitmapToCache(String data, Bitmap bitmap,
			BitmapDisplayConfig config) {
		if (data == null || bitmap == null) {
			return;
		}

		// Add to memory cache
		if (mMemoryCache != null && mMemoryCache.get(data) == null) {
//			Tools.showLogA("放入内存中：name" + data);
			mMemoryCache.put(data, bitmap);
		}

		if (config.isSaveToSdcard()) {
//			Tools.showLogA("放入SD中：name" + data);
			addToSdcache(data, bitmap, config);
		}

	}

	/**
	 * 添加到SD卡缓冲区
	 * 
	 * @param data
	 * @param bitmap
	 */
	public void addToSdcache(String data, Bitmap bitmap,
			BitmapDisplayConfig config) {
		synchronized (mDiskCacheLock) {
			// 添加到硬盘缓存
			if (mDiskLruCache != null) {
				final String key = StringTools.generator(data);
				OutputStream out = null;
				try {
					LruSdCache.Snapshot snapshot = mDiskLruCache.get(key);
					if (snapshot == null) {
						final LruSdCache.Editor editor = mDiskLruCache
								.edit(key);
						if (editor != null) {
							out = editor.newOutputStream(DISK_CACHE_INDEX);
							Tools.showLogB("now compressformat is:"
									+ config.getFormat());
							bitmap.compress(config.getFormat(),
									mCacheParams.compressQuality, out);
							editor.commit();
							out.close();
						}
					} else {
						snapshot.getInputStream(DISK_CACHE_INDEX).close();
					}
				} catch (final IOException e) {
					Tools.showLogB("addBitmapToCache - " + e);
				} catch (Exception e) {
					Tools.showLogB("addBitmapToCache - " + e);
				} finally {
					try {
						if (out != null) {
							out.close();
						}
					} catch (IOException e) {
					}
				}
			}
		}
	}

	/**
	 * Get from memory cache.
	 * 
	 * @param data
	 *            Unique identifier for which item to get
	 * @return The bitmap if found in cache, null otherwise
	 */
	public Bitmap getBitmapFromMemCache(String data) {
		if (mMemoryCache != null) {
			Tools.showLogA("内存中取图片:"+data);
			final Bitmap memBitmap = mMemoryCache.get(data);
			if (memBitmap != null) {
				Tools.showLogA("内存中取图片返回的值不为null");
				return memBitmap;
			}
		}
		
		return null;
	}

	public Bitmap getBitmapFromDiskCache(String data) {

		final String key = StringTools.generator(data);
		Tools.showLogB("本地SD卡取图片数据name:" + data + "key:" + key);

		synchronized (mDiskCacheLock) {
			while (mDiskCacheStarting) {
				try {
					mDiskCacheLock.wait();
				} catch (InterruptedException e) {
				}
			}
			if (mDiskLruCache != null) {
				InputStream inputStream = null;
				try {
					final LruSdCache.Snapshot snapshot = mDiskLruCache.get(key);

					if (snapshot != null) {
						inputStream = snapshot.getInputStream(DISK_CACHE_INDEX);
						if (inputStream != null) {
							try {
								final Bitmap bitmap = BitmapFactory
										.decodeStream(inputStream);
								return bitmap;
							} catch (OutOfMemoryError e) {
								mMemoryCache.evictAll();
								System.gc();
							}

						}
					}
				} catch (final IOException e) {
					Tools.showLogB("getBitmapFromDiskCache - " + e);
				} finally {
					try {
						if (inputStream != null) {
							inputStream.close();
						}
					} catch (IOException e) {
					}
				}
			}
			return null;
		}
	}

	/**
	 * Clears both the memory and disk cache associated with this ImageCache
	 * object. Note that this includes disk access so this should not be
	 * executed on the main/UI thread.
	 */
	public void clearCache() {
		clearMemoryCache();
		synchronized (mDiskCacheLock) {
			mDiskCacheStarting = true;
			if (mDiskLruCache != null && !mDiskLruCache.isClosed()) {
				try {
					mDiskLruCache.delete();
				} catch (IOException e) {
					Tools.showLogB("clearCache - " + e);
				}
				mDiskLruCache = null;
				initDiskCache();
			}
		}
	}

	public void clearMemoryCache() {
		if (mMemoryCache != null) {
			mMemoryCache.evictAll();
		}
	}

	/**
	 * Flushes the disk cache associated with this ImageCache object. Note that
	 * this includes disk access so this should not be executed on the main/UI
	 * thread.
	 */
	public void flush() {
		synchronized (mDiskCacheLock) {
			if (mDiskLruCache != null) {
				try {
					mDiskLruCache.flush();
				} catch (IOException e) {
					Tools.showLogB("flush - " + e);
				}
			}
		}
	}

	/**
	 * Closes the disk cache associated with this ImageCache object. Note that
	 * this includes disk access so this should not be executed on the main/UI
	 * thread.
	 */
	public void close() {
		synchronized (mDiskCacheLock) {
			if (mDiskLruCache != null) {
				try {
					if (!mDiskLruCache.isClosed()) {
						mDiskLruCache.close();
						mDiskLruCache = null;
					}
				} catch (IOException e) {
					Tools.showLogB("close - " + e);
				}
			}
		}
	}

	/** 文件路径属性，可能为多个目录，不能是静态 */
	private File diskCacheDir;

	public void setDiskCacheDir(String diskCacheDir) {
		this.diskCacheDir = new File(diskCacheDir);
	}

	/**
	 * 定义缓存对象的各种配置信息
	 * 
	 * @author blueming.wu
	 * @date 2012-12-26
	 */
	public static class ImageCacheParams {
		/***/
		public int memCacheSize = DEFAULT_MEM_CACHE_SIZE;
		public int diskCacheSize = DEFAULT_DISK_CACHE_SIZE;

		/** 文件路径不能静态公共属性，2012年1月11日修改 */
		// public File diskCacheDir;
		// public CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
		public int compressQuality = DEFAULT_COMPRESS_QUALITY;
		public boolean memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED;
		public boolean diskCacheEnabled = DEFAULT_DISK_CACHE_ENABLED;
		public boolean clearDiskCacheOnStart = DEFAULT_CLEAR_DISK_CACHE_ON_START;
		public boolean initDiskCacheOnCreate = DEFAULT_INIT_DISK_CACHE_ON_CREATE;

		public ImageCacheParams() {

		}

		/**
		 * 设置缓存大小
		 * 
		 * @param context
		 * @param percent
		 *            百分比，值的范围是在 0.05 到 0.8之间
		 */
		public void setMemCacheSizePercent(Context context, float percent) {
			if (percent < 0.05f || percent > 0.8f) {
				throw new IllegalArgumentException(
						"setMemCacheSizePercent - percent must be "
								+ "between 0.05 and 0.8 (inclusive)");
			}
			memCacheSize = Math.round(percent * getMemoryClass(context) * 1024
					* 1024);
		}

		public void setMemCacheSize(int memCacheSize) {
			this.memCacheSize = memCacheSize;
		}

		public void setDiskCacheSize(int diskCacheSize) {
			this.diskCacheSize = diskCacheSize;
		}

		private static int getMemoryClass(Context context) {
			return ((ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE))
					.getMemoryClass();
		}
	}

}
