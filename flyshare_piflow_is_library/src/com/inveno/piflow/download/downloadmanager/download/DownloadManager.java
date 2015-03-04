package com.inveno.piflow.download.downloadmanager.download;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.inveno.piflow.R;
import com.inveno.piflow.download.downloadmanager.http.AsyncHttpClient;
import com.inveno.piflow.download.downloadmanager.http.AsyncHttpResponseHandler;
import com.inveno.piflow.download.downloadmanager.http.FileHttpResponseHandler;
import com.inveno.piflow.tools.commontools.MkdirFileTools;
import com.inveno.piflow.tools.commontools.NotificationUtil;
import com.inveno.piflow.tools.commontools.StringTools;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 管理下载
 * 
 * @author blueming.wu
 * 
 * @date 2013-6-4
 */
public class DownloadManager extends Thread {

	/** 最大能处理的预下载个数 */
	private static final int MAX_handler_COUNT = 10;

	/** 同时下载的线程数 */
	private static final int MAX_DOWNLOAD_THREAD_COUNT = 1;

	/** 异步handler队列 */
	private handlerQueue mhandlerQueue;

	/** 下载中的handler集合 */
	private List<AsyncHttpResponseHandler> mDownloadinghandlers;

	/** 等待中的handler集合 */
	private List<AsyncHttpResponseHandler> mPausinghandlers;

	/** 网络交互 */
	private AsyncHttpClient syncHttpClient;

	/** 是否在运行 */
	private Boolean isRunning = false;

	public static final String FILE_ROOT = MkdirFileTools.APPS_PATH
			+ File.separator;
	private DownLoadCallback mDownLoadCallback;
	private String rootPath = "";

	/** 单例 */
	private static DownloadManager downloadManager;

	private Context mContext;

	/** 通知管理 */
	private NotificationManager notificationManager;

	public static DownloadManager getDownloadManager(Context context) {
		if (downloadManager == null) {
			downloadManager = new DownloadManager(FILE_ROOT, context);
		}
		return downloadManager;
	}

	public static DownloadManager getDownloadManager(String rootPath,
			Context context) {

		if (downloadManager == null) {
			downloadManager = new DownloadManager(rootPath, context);
		}
		return downloadManager;
	}

	/** 此构造函数调用下面的扩展构造函数 */
	private DownloadManager(Context context) {
		this(FILE_ROOT, context);
	}

	private DownloadManager(String rootPath, Context context) {
		this.rootPath = rootPath;
		this.mContext = context;
		this.notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mhandlerQueue = new handlerQueue();
		mDownloadinghandlers = new ArrayList<AsyncHttpResponseHandler>();
		mPausinghandlers = new ArrayList<AsyncHttpResponseHandler>();
		syncHttpClient = new AsyncHttpClient();
		if (!StringTools.isEmpty(rootPath)) {
			File rootFile = new File(rootPath);
			if (!rootFile.exists()) {
				rootFile.mkdir();
			}
		}
	}

	/**
	 * 获取保存下载文件的根目录
	 * 
	 * @return
	 */
	public String getRootPath() {
		if (StringTools.isEmpty(rootPath)) {
			rootPath = FILE_ROOT;
		}
		return rootPath;
	}

	public void setDownLoadCallback(DownLoadCallback downLoadCallback) {
		this.mDownLoadCallback = downLoadCallback;
	}

	/**
	 * 开启下载
	 */
	public void startManage() {

		isRunning = true;
		this.start();
		if (mDownLoadCallback != null) {
			mDownLoadCallback.sendStartMessage();
		}
		// checkUncompletehandlers();
	}

	/**
	 * 停止
	 */
	// @SuppressWarnings("deprecation")
	// public void close() {
	//
	// isRunning = false;
	// pauseAllHandler();
	// if (mDownLoadCallback != null) {
	// mDownLoadCallback.sendStopMessage();
	// }
	// this.stop();
	// }

	public boolean isRunning() {

		return isRunning;
	}

	@Override
	public void run() {

		super.run();
		while (isRunning) {
			/** 如果条件满足能产生下载，则返回的不是null，一直取到位置 */
			FileHttpResponseHandler handler = (FileHttpResponseHandler) mhandlerQueue
					.poll();
			if (handler != null) {
				mDownloadinghandlers.add(handler);
				handler.setInterrupt(false);
				syncHttpClient.download(handler.getUrl(), handler);
			}
		}
	}

	/**
	 * 添加一个下载任务
	 * 
	 * @param url
	 */
	public void addHandler(String url) {
		addHandler(url, null, 0, null);
	}

	/**
	 * 开始一个下载任务，并申请维护一个通知栏下载进度条
	 * 
	 * @param url
	 *            下载地址
	 * @param title
	 *            通知栏显示的标题
	 * @param bmp
	 *            通知栏显示的下载应用的icon
	 */
	public void addHandler(String url, String title, int appId, Bitmap bmp) {
		Tools.showLogA("添加了一个下载任务url："+url);
		if (getTotalhandlerCount() >= MAX_handler_COUNT) {
			if (mDownLoadCallback != null) {
				mDownLoadCallback.sendFailureMessage(url, "任务列表已满");
			}
			return;
		}
		if (TextUtils.isEmpty(url) || hasHandler(url)) {
			// 任务中存在这个任务,或者任务不满足要求
			return;
		}
		try {
			addHandler(newAsyncHttpResponseHandler(url, title, appId, bmp));
		} catch (MalformedURLException e) {
			Tools.showLogA("添加url异常");
		}
	}

	/**
	 * 增加一个任务到队列中
	 * 
	 * @param handler
	 */
	private void addHandler(AsyncHttpResponseHandler handler) {
		broadcastAddHandler(((FileHttpResponseHandler) handler).getUrl());
		mhandlerQueue.offer(handler);

		if (!this.isAlive()) {
			this.startManage();
		}
	}

	private void broadcastAddHandler(String url)

	{
		broadcastAddHandler(url, false);
	}

	/**
	 * 发送添加成功的消息出去，发送完成后会回调handler的onAdd方法
	 * 
	 * @param url
	 * @param isInterrupt
	 */
	private void broadcastAddHandler(String url, boolean isInterrupt) {

		if (mDownLoadCallback != null) {
			mDownLoadCallback.sendAddMessage(url, false);
		}

	}

	/**
	 * 重新发送所有的已添加任务的消息
	 */
	public void reBroadcastAddAllhandler() {

		FileHttpResponseHandler handler;
		for (int i = 0; i < mDownloadinghandlers.size(); i++) {
			handler = (FileHttpResponseHandler) mDownloadinghandlers.get(i);
			broadcastAddHandler(handler.getUrl(), handler.isInterrupt());
		}
		for (int i = 0; i < mhandlerQueue.size(); i++) {
			handler = (FileHttpResponseHandler) mhandlerQueue.get(i);
			broadcastAddHandler(handler.getUrl());
		}
		for (int i = 0; i < mPausinghandlers.size(); i++) {
			handler = (FileHttpResponseHandler) mPausinghandlers.get(i);
			broadcastAddHandler(handler.getUrl());
		}
	}

	/**
	 * 当前正在下载的任务中是否包含此任务
	 * 
	 * @param url
	 * @return
	 */
	public boolean hasHandler(String url) {

		FileHttpResponseHandler handler;
		for (int i = 0; i < mDownloadinghandlers.size(); i++) {
			handler = (FileHttpResponseHandler) mDownloadinghandlers.get(i);
			if (handler.getUrl().equals(url)) {
				return true;
			}
		}
		for (int i = 0; i < mhandlerQueue.size(); i++) {
			handler = (FileHttpResponseHandler) mhandlerQueue.get(i);
			if (handler.getUrl().equals(url)) {
				return true;
			}
		}
		return false;
	}

	public FileHttpResponseHandler getHandler(String url) {
		FileHttpResponseHandler handler = null;
		for (int i = 0; i < mDownloadinghandlers.size(); i++) {
			handler = (FileHttpResponseHandler) mDownloadinghandlers.get(i);

		}
		for (int i = 0; i < mhandlerQueue.size(); i++) {
			handler = (FileHttpResponseHandler) mhandlerQueue.get(i);
		}
		return handler;
	}

	public AsyncHttpResponseHandler gethandler(int position) {

		if (position >= mDownloadinghandlers.size()) {
			return mhandlerQueue.get(position - mDownloadinghandlers.size());
		} else {
			return mDownloadinghandlers.get(position);
		}
	}

	/**
	 * 获取队列中的排队的数目
	 * 
	 * @return
	 */
	public int getQueuehandlerCount() {

		return mhandlerQueue.size();
	}

	/**
	 * 正在下载的数目
	 * 
	 * @return
	 */
	public int getDownloadinghandlerCount() {

		return mDownloadinghandlers.size();
	}

	/**
	 * 暂停状态的数目
	 * 
	 * @return
	 */
	public int getPausinghandlerCount() {

		return mPausinghandlers.size();
	}

	/**
	 * 所有任务数目
	 * 
	 * @return
	 */
	public int getTotalhandlerCount() {

		return getQueuehandlerCount() + getDownloadinghandlerCount()
				+ getPausinghandlerCount();
	}

	/**
	 * 检查缓存中没有完成的任务继续添加
	 */
	public void checkUncompletehandlers() {

		List<String> urlList = DownLoadConfigUtil.getURLArray(mContext);
		if (urlList.size() >= 0) {
			for (int i = 0; i < urlList.size(); i++) {
				addHandler(urlList.get(i));
			}
		}
	}

	/**
	 * 暂停某个任务
	 * 
	 * @param url
	 */
	public synchronized void pauseHandler(String url) {

		FileHttpResponseHandler removeHandler = null;
		for (AsyncHttpResponseHandler responseHandler : mDownloadinghandlers) {
			FileHttpResponseHandler handler = (FileHttpResponseHandler) responseHandler;
			if (handler != null && handler.getUrl().equals(url)) {
				removeHandler = handler;
				break;
			}
		}

		if (removeHandler != null)
			pausehandler(removeHandler);
	}

	/**
	 * 暂停所有排队和正在下载的任务
	 */
	public synchronized void pauseAllHandler() {

		AsyncHttpResponseHandler handler;

		for (int i = 0; i < mhandlerQueue.size(); i++) {
			handler = mhandlerQueue.get(i);
			mhandlerQueue.remove(handler);
			mPausinghandlers.add(handler);
		}

		for (int i = 0; i < mDownloadinghandlers.size(); i++) {
			handler = mDownloadinghandlers.get(i);
			if (handler != null) {
				pausehandler(handler);
			}
		}
	}

	/**
	 * 删除任务
	 * 
	 * @param url
	 */
	public synchronized void deleteHandler(String url) {

		FileHttpResponseHandler handler;
		for (int i = 0; i < mDownloadinghandlers.size(); i++) {
			handler = (FileHttpResponseHandler) mDownloadinghandlers.get(i);
			if (handler != null && handler.getUrl().equals(url)) {
				File file = handler.getFile();
				if (file.exists())
					file.delete();
				File tempFile = handler.getTempFile();
				if (tempFile.exists()) {
					tempFile.delete();
				}
				handler.setInterrupt(true);
				completehandler(handler);
				return;
			}
		}
		for (int i = 0; i < mhandlerQueue.size(); i++) {
			handler = (FileHttpResponseHandler) mhandlerQueue.get(i);
			if (handler != null && handler.getUrl().equals(url)) {
				mhandlerQueue.remove(handler);
			}
		}
		for (int i = 0; i < mPausinghandlers.size(); i++) {
			handler = (FileHttpResponseHandler) mPausinghandlers.get(i);
			if (handler != null && handler.getUrl().equals(url)) {
				mPausinghandlers.remove(handler);
			}
		}
	}

	/**
	 * 继续任务
	 * 
	 * @param url
	 */
	public synchronized void continueHandler(String url) {

		FileHttpResponseHandler handler;
		for (int i = 0; i < mPausinghandlers.size(); i++) {
			handler = (FileHttpResponseHandler) mPausinghandlers.get(i);
			if (handler != null && handler.getUrl().equals(url)) {
				continuehandler(handler);
			}

		}
	}

	/**
	 * 暂停重载
	 * 
	 * @param handler
	 */
	public synchronized void pausehandler(AsyncHttpResponseHandler handler) {

		FileHttpResponseHandler fileHttpResponseHandler = (FileHttpResponseHandler) handler;
		if (handler != null) {
			fileHttpResponseHandler.setInterrupt(true);

			String url = fileHttpResponseHandler.getUrl();
			fileHttpResponseHandler.onPause(url);
			try {
				mDownloadinghandlers.remove(handler);
				handler = newAsyncHttpResponseHandler(url, "", 0, null);
				mPausinghandlers.add(handler);
			} catch (MalformedURLException e) {
				Tools.showLogA("暂停任务url出错");
			}

		}
	}

	/**
	 * 继续 重载
	 * 
	 * @param handler
	 */
	public synchronized void continuehandler(AsyncHttpResponseHandler handler) {

		if (handler != null) {
			mPausinghandlers.remove(handler);
			mhandlerQueue.offer(handler);
		}
	}

	/**
	 * 完成一个任务，完成任务后清楚缓存中的此条Url的记录
	 * 
	 * @param handler
	 */
	public synchronized void completehandler(AsyncHttpResponseHandler handler) {

		if (mDownloadinghandlers.contains(handler)) {
			DownLoadConfigUtil.clearURL(mDownloadinghandlers.indexOf(handler),
					mContext);
			mDownloadinghandlers.remove(handler);

			if (mDownLoadCallback != null) {
				mDownLoadCallback
						.sendFinishMessage(((FileHttpResponseHandler) handler)
								.getUrl());
			}
		}
	}

	/**
	 * 新产生一个异步任务
	 * 
	 * @param url
	 * @param title
	 *            为此下载任务维护一个通知栏进度通知的标题
	 * @return
	 * @throws MalformedURLException
	 */
	private AsyncHttpResponseHandler newAsyncHttpResponseHandler(String url,
			final String title, final int appId, final Bitmap bmp)
			throws MalformedURLException {
		FileHttpResponseHandler handler = new FileHttpResponseHandler(url,
				rootPath, StringTools.getFileNameFromUrl(url)) {

			private Notification notification;

			@Override
			public void onPause(String url) {
				super.onPause(url);
				notificationManager.cancel(appId);
			}

			@Override
			protected void sendProgressMessage(String speed, long download,
					int progress) {
				Tools.showLogA("speed" + speed + "progress" + progress);
				if (mDownLoadCallback != null) {
					mDownLoadCallback.sendLoadMessage(this.getUrl(), speed,
							download, progress);
				}
				if (notification != null && progress < 100) {
					notification.contentView.setProgressBar(
							R.id.download_progress_pb, 100, progress, false);
					notificationManager.notify(appId, notification);
				}
				super.sendProgressMessage(speed, download, progress);
			}

			@Override
			public void onSuccess(String content) {
				Tools.showLogA("下载成功:" + content);
				if (mDownLoadCallback != null) {
					mDownLoadCallback.sendSuccessMessage(this.getUrl());
				}
			}

			@Override
			public void onSuccess(byte[] binaryData) {
				Tools.showLogA("下载成功byte:" + binaryData);
				if (mDownLoadCallback != null) {
					mDownLoadCallback.sendSuccessMessage(this.getUrl());
				}
				notificationManager.cancel(appId);
				super.onSuccess(binaryData);
			}

			@Override
			public void onFinish() {
				Tools.showLogA("下载finish:");
				completehandler(this);
			}

			/**
			 * 开始下载时，添加一个url到文件缓存中
			 */
			@Override
			public void onStart() {
				Tools.showLogA("开始一个下载title：" + title);
				if (StringTools.isNotEmpty(title))
					notification = NotificationUtil.createNotiHasProgress(null,
							title, mContext.getPackageName(),
							notificationManager, bmp);
				DownLoadConfigUtil.storeURL(mDownloadinghandlers.indexOf(this),
						getUrl(), mContext);
			}

			@Override
			public void onFailure(Throwable error) {
				notificationManager.cancel(appId);
				Tools.showLogA("下载失败了:" + error);
				if (error != null) {

					if (mDownLoadCallback != null) {
						mDownLoadCallback.sendFailureMessage(this.getUrl(),
								error.getMessage());
					}
				}
				// completehandler(this);
			}
		};

		return handler;
	}

	/**
	 * handler队列
	 * 
	 * @author blueming.wu
	 * 
	 * @date 2013-6-5
	 */
	private class handlerQueue {
		/** 异步响应的队列 */
		private Queue<AsyncHttpResponseHandler> handlerQueue;

		public handlerQueue() {
			handlerQueue = new LinkedList<AsyncHttpResponseHandler>();
		}

		public void offer(AsyncHttpResponseHandler handler) {

			handlerQueue.offer(handler);
		}

		/**
		 * downLoad 每执行一次run方法，就会问此方法来提取异步responseHandler，
		 * 如果队列中的正在下载数的responseHandler大于三个，或者从队列中去responseHandler的头取出来为null，
		 * 都会在一秒之后继续去取，此时run方法被阻塞等待poll方法取到为止
		 * 
		 * @return
		 */
		public AsyncHttpResponseHandler poll() {

			AsyncHttpResponseHandler handler = null;
			while (mDownloadinghandlers.size() >= MAX_DOWNLOAD_THREAD_COUNT
					|| (handler = handlerQueue.poll()) == null) {
				try {
					Thread.sleep(1000); // sleep
				} catch (InterruptedException e) {
					Tools.showLogB("下载app线程异常！");
				}
			}
			return handler;
		}

		public AsyncHttpResponseHandler get(int position) {

			if (position >= size()) {
				return null;
			}
			return ((LinkedList<AsyncHttpResponseHandler>) handlerQueue)
					.get(position);
		}

		public int size() {

			return handlerQueue.size();
		}

		@SuppressWarnings("unused")
		public boolean remove(int position) {

			return handlerQueue.remove(get(position));
		}

		public boolean remove(AsyncHttpResponseHandler handler) {

			return handlerQueue.remove(handler);
		}
	}

}
