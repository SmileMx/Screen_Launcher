package com.inveno.piflow.tools.bitmap;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

import android.graphics.Bitmap;

import com.inveno.piflow.tools.android.BitmapTools;
import com.inveno.piflow.tools.commontools.StringTools;
import com.inveno.piflow.tools.commontools.Tools;

public class BitmapProcess {

	private boolean mHttpDiskCacheStarting = true;
	private int cacheSize;
	private static final int DEFAULT_CACHE_SIZE = 100 * 1024 * 1024; // 默认大小控制在
																		// 20MB

	/** 原始图片的路径，不进行任何的压缩操作 */
	private LruSdCache mOriginalDiskCache;

	/** 线程锁 */
	private final Object mHttpDiskCacheLock = new Object();
	private static final int DISK_CACHE_INDEX = 0;

	private File mOriginalCacheDir;
	private IDownloader downloader;

	public BitmapProcess(IDownloader downloader, String filePath, int cacheSize) {
		this.mOriginalCacheDir = new File(filePath + "/original");
		this.downloader = downloader;
		if (cacheSize <= 0)
			cacheSize = DEFAULT_CACHE_SIZE;
		this.cacheSize = cacheSize;
	}

	/**
	 * 根据配置的config宽高等下载图片，应该保存原图，显示时压缩
	 * 
	 * @param data
	 * @param config
	 * @return
	 */
	public Bitmap processBitmap(String data, BitmapDisplayConfig config) {
		final String key = StringTools.generator(data);
		
		Tools.showLogB("下载url:"+data+"  下载图片的key："+key );
		FileDescriptor fileDescriptor = null;
		FileInputStream fileInputStream = null;
		LruSdCache.Snapshot snapshot;
		Bitmap bitmap = null;

		if (config.isSaveToSdcard()) {
			synchronized (mHttpDiskCacheLock) {
				// Wait for disk cache to initialize
				while (mHttpDiskCacheStarting) {
					try {
						mHttpDiskCacheLock.wait();
					} catch (InterruptedException e) {
					}
				}
				if (mOriginalDiskCache != null) {
					try {
						snapshot = mOriginalDiskCache.get(key);
						if (snapshot == null) {// 如果为null则去下载
							LruSdCache.Editor editor = mOriginalDiskCache
									.edit(key);
							if (editor != null) {
								if (downloader
										.downloadToLocalStreamByUrl(
												data,
												editor.newOutputStream(DISK_CACHE_INDEX))) {
									editor.commit();
								} else {
									editor.abort();
								}
							}
							snapshot = mOriginalDiskCache.get(key);
						}
						// 如果不为null则直接解析图片返回
						if (snapshot != null) {
							
							fileInputStream = (FileInputStream) snapshot
									.getInputStream(DISK_CACHE_INDEX);
							fileDescriptor = fileInputStream.getFD();
						}
					} catch (IOException e) {

					} catch (IllegalStateException e) {

					} finally {
						if (fileDescriptor == null && fileInputStream != null) {
							try {
								fileInputStream.close();
							} catch (IOException e) {
							}
						}
					}
				}
			}
			if (fileDescriptor != null) {
				bitmap = BitmapTools.decodeSampledBitmapFromDescriptor(
						fileDescriptor, config.getBitmapWidth(),
						config.getBitmapHeight());

			}
		} else {
			// 如果不用保存到sd卡，则直接把返回的流压缩返回
			bitmap = downloader.downloadAndCompressBitmap(data,
					config.getBitmapWidth(), config.getBitmapHeight());
		}

		if (fileInputStream != null) {
			try {
				fileInputStream.close();
			} catch (IOException e) {
			}
		}
		return bitmap;
	}

	/**
	 * 初始化保存原图的路径
	 */
	public void initHttpDiskCache() {
		if (!mOriginalCacheDir.exists()) {
			mOriginalCacheDir.mkdirs();
		}
		synchronized (mHttpDiskCacheLock) {
			if (BitmapTools.getUsableSpace(mOriginalCacheDir) > cacheSize) {
				try {
					mOriginalDiskCache = LruSdCache.open(mOriginalCacheDir, 1,
							1, cacheSize);
				} catch (IOException e) {
					mOriginalDiskCache = null;
				}
			}
			mHttpDiskCacheStarting = false;
			mHttpDiskCacheLock.notifyAll();
		}
	}

	public void clearCacheInternal() {
		synchronized (mHttpDiskCacheLock) {
			if (mOriginalDiskCache != null && !mOriginalDiskCache.isClosed()) {
				try {
					mOriginalDiskCache.delete();
				} catch (IOException e) {
					Tools.showLogB("clear cache" + e);
				}
				mOriginalDiskCache = null;
				mHttpDiskCacheStarting = true;
				initHttpDiskCache();
			}
		}
	}

	public void flushCacheInternal() {
		synchronized (mHttpDiskCacheLock) {
			if (mOriginalDiskCache != null) {
				try {
					mOriginalDiskCache.flush();
				} catch (IOException e) {
					Tools.showLogB("flush - " + e);
				}
			}
		}
	}

	public void closeCacheInternal() {
		synchronized (mHttpDiskCacheLock) {
			if (mOriginalDiskCache != null) {
				try {
					if (!mOriginalDiskCache.isClosed()) {
						mOriginalDiskCache.close();
						mOriginalDiskCache = null;
					}
				} catch (IOException e) {
					Tools.showLogB("close:" + e);
				}
			}
		}
	}
}
