package com.inveno.piflow.download.downloadmanager.download;

import java.io.File;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;

import com.inveno.piflow.R;
import com.inveno.piflow.tools.android.ToastTools;
import com.inveno.piflow.tools.commontools.Const;
import com.inveno.piflow.tools.commontools.MkdirFileTools;
import com.inveno.piflow.tools.commontools.NotificationUtil;
import com.inveno.piflow.tools.commontools.StringTools;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 新版后台下载服务，包含downLoadManager对象以操作各类大数据下载
 * 
 * @author blueming.wu
 * 
 * @date 2013-6-13
 */
public class DownloadService extends Service {

	/** 下载任务的key */
	public static final String DOWNLOAD_TASK_KEY = "download_task";

	/** 下载任务为语音引擎 */
	public static final int TASK_DOWNLOAD_TTS = 1;

	/** 下载任务为版本更新 */
	public static final int TASK_DOWNLOAD_NEW_VERSION = 2;

	/** 下载任务为推送应用 */
	public static final int TASK_DOWNLOAD_PUSHAPP = 3;
	
	/** 瀑布流硬广告 */
	public static final int TASK_DOWNLOAD_WF_HARD_AD = 4;

	/** 下载发送通知的ID */
	private static final int TTS_NOTI_ID = 63;

	private static final int NEW_VERSION_NOTI_ID = 64;

	private static final int  WF_HARD_AD_ID=999;
	
	
	/** 下载管理类 */
	private DownloadManager mDownloadManager;

	/** 通知管理 */
	private NotificationManager manager;

	@Override
	public IBinder onBind(Intent intent) {

		return new DownloadServiceImpl();
	}

	@Override
	public void onCreate() {

		super.onCreate();
		// 这样能在后台运行么？
		manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mDownloadManager = DownloadManager.getDownloadManager(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Tools.showLogA("下载服务执行了onstartCommand");
		// 取intent中的数据
		int task = 0;
		if (intent != null) {
			task = intent.getIntExtra(DOWNLOAD_TASK_KEY, 0);
			switch (task) {
			case TASK_DOWNLOAD_TTS:
				// 如果正在下载则，提示用户
				final String ttsUrl = Const.TTS_DOWNLOAD_URL;

				if (mDownloadManager.hasHandler(ttsUrl)) {
					ToastTools.showToast(this, "正在下载中...");
				} else {
					DownLoadCallback callback = getDownloadCallback(ttsUrl,
							true, "语音引擎", TTS_NOTI_ID);
					mDownloadManager.setDownLoadCallback(callback);
					mDownloadManager.addHandler(ttsUrl);
				}

				break;
			case TASK_DOWNLOAD_NEW_VERSION:
				final String updateVersionUrl = intent
						.getStringExtra("updateUrl");

				if (mDownloadManager.hasHandler(updateVersionUrl)) {
					ToastTools.showToast(this, "正在下载中...");
				} else {
					boolean notify = intent.getBooleanExtra("notify", true);
					DownLoadCallback callback = getDownloadCallback(
							updateVersionUrl, notify, "版本更新",
							NEW_VERSION_NOTI_ID);
					Tools.showLogA("下载版本的url：" + updateVersionUrl);
					mDownloadManager.setDownLoadCallback(callback);
					mDownloadManager.addHandler(updateVersionUrl);
				}
				break;
			case TASK_DOWNLOAD_PUSHAPP:
				final String pushAppUrl = intent.getStringExtra("appUrl");
				if (mDownloadManager.hasHandler(pushAppUrl)) {
					ToastTools.showToast(this, "正在下载中...");
				} else {
					DownLoadCallback callback = getDownloadCallback(pushAppUrl,
							true, "", NEW_VERSION_NOTI_ID);
					Tools.showLogA("下载app的url：" + pushAppUrl);
					mDownloadManager.setDownLoadCallback(callback);
					mDownloadManager.addHandler(pushAppUrl);
				}
				break;

			case TASK_DOWNLOAD_WF_HARD_AD:
				final String hardAppUrl=intent.getStringExtra("hardAdUrl");
				final String appName=intent.getStringExtra("appName");
				if (mDownloadManager.hasHandler(hardAppUrl)) {
					ToastTools.showToast(this, "正在下载中...");
				} else {
					DownLoadCallback callback = getDownloadCallback(hardAppUrl,
							true, appName, WF_HARD_AD_ID);
					Tools.showLogA("下载app的url：" + hardAppUrl);
					mDownloadManager.setDownLoadCallback(callback);
					mDownloadManager.addHandler(hardAppUrl);
				}
				
				break;
			default:
				break;
			}
		}

		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 获取回调对象
	 * 
	 * @param taskUrl
	 * @return
	 */
	private DownLoadCallback getDownloadCallback(final String taskUrl,
			final boolean notify, final String title, final int notiId) {
		DownLoadCallback callback = new DownLoadCallback() {

			private Notification noti;

			@Override
			public void onAdd(String url, Boolean isInterrupt) {
				super.onAdd(url, isInterrupt);
				if (taskUrl.equals(url) && notify) {
					noti = NotificationUtil.createNotification(
							getPackageName(), manager, title, notiId);
				}
			}

			@Override
			public void onSuccess(String url) {
				super.onSuccess(url);
				Tools.showLogA("下载成功");
				if (taskUrl.equals(url)) {
					// 存在，且是完整的
					String appName = StringTools.getFileNameFromUrl(taskUrl);
					String savePathOld = MkdirFileTools.APPS_PATH
							+ File.separator + appName;
					File ttsFile = new File(savePathOld);

					if (ttsFile.exists()) {
						Intent install = new Intent();
						install.setAction(android.content.Intent.ACTION_VIEW);
						install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						install.setDataAndType(Uri.fromFile(ttsFile),
								"application/vnd.android.package-archive");
						startActivity(install);
						return;
					}
				}

				// stopSelf();

			}

			@Override
			public void onFinish(String url) {
				Tools.showLogA("下载结束");
				manager.cancel(notiId);
				super.onFinish(url);
			}

			@Override
			public void onFailure(String url, String strMsg) {
				Tools.showLogA("下载失败");
				ToastTools.showToast(DownloadService.this, "下载失败");
				manager.cancel(notiId);
				super.onFailure(url, strMsg);
			}

			@Override
			public void onLoading(String url, String speed, long download,
					int progress) {
				Tools.showLogA("更新进度");
				if (taskUrl.equals(url) && notify) {
					int intProgress = progress;

					if (intProgress < 100) {
						noti.contentView.setProgressBar(
								R.id.simple_download_progressbar, 100,
								intProgress, false);
						manager.notify(notiId, noti);
					}

				}
				super.onLoading(url, speed, download, progress);
			}
		};
		return callback;
	}

	/**
	 * 给外部帮顶service使用的类
	 * 
	 * @author blueming.wu
	 * 
	 * @date 2013-6-13
	 */
	public class DownloadServiceImpl extends IDownloadService.Stub {

		@Override
		public void startManage() throws RemoteException {

			mDownloadManager.startManage();
		}

		public DownloadManager getDownloadManager() {
			return mDownloadManager;
		}

		@Override
		public void addTask(String url) throws RemoteException {
			if (!StringTools.isEmpty(url)) {
				mDownloadManager.addHandler(url);
			}

		}

		@Override
		public void pauseTask(String url) throws RemoteException {
			if (!StringTools.isEmpty(url)) {
				mDownloadManager.pauseHandler(url);
			}
		}

		@Override
		public void deleteTask(String url) throws RemoteException {
			if (!StringTools.isEmpty(url)) {
				mDownloadManager.deleteHandler(url);
			}
		}

		@Override
		public void continueTask(String url) throws RemoteException {
			if (!StringTools.isEmpty(url)) {
				mDownloadManager.continueHandler(url);
			}
		}

		@Override
		public void pauseAll(String url) throws RemoteException {
			if (!StringTools.isEmpty(url)) {
				mDownloadManager.pauseAllHandler();
			}
		}

		@Override
		public void stopManage() throws RemoteException {
			// mDownloadManager.close();
		}

	}

}
