package com.inveno.piflow.tools.commontools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

import com.inveno.piflow.R;

/**
 * 手机通知栏推送通知工具类，主要用于推送重大新闻，版本升级推送等
 * 
 * @author blueming.wu
 * @date 2012-8-17
 */
public class NotificationUtil {

	public static Notification createNotification(Context context,
			Intent intent, String rollTitle, String title, String intro,
			int notiId, int type, int icon) {
		Notification noti = new Notification();
//		noti.icon = R.drawable.icon;
		noti.tickerText = rollTitle;

		if (icon != -1)
			noti.icon = icon;

		NotificationManager notiManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		noti.defaults = Notification.DEFAULT_SOUND;
		noti.flags = Notification.FLAG_AUTO_CANCEL;
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, type);
		noti.setLatestEventInfo(context, title, intro, contentIntent);
		notiManager.notify(notiId, noti);
		return noti;
	}

	/**
	 * 下载进度条通知栏
	 * 
	 * @param pIntent
	 * @param title
	 * @param packageName
	 * @param manager
	 * @return
	 */
	public static Notification createNotiHasProgress(PendingIntent pIntent,
			String title, String packageName, NotificationManager manager,
			Bitmap bmp) {
		// 发通知
		Notification notif = new Notification();
		notif.icon = R.drawable.allset_download;
		notif.tickerText = title;

		notif.flags = Notification.FLAG_NO_CLEAR;
		// 通知栏显示所用到的布局文件
		notif.contentView = new RemoteViews(packageName,
				R.layout.donwload_progress_notify_layout);
		notif.contentView.setTextViewText(R.id.download_progress_tv, title);
		if (bmp != null)
			notif.contentView
					.setImageViewBitmap(R.id.download_progress_iv, bmp);
		if (pIntent != null)
			notif.contentIntent = pIntent;
		return notif;
	}

	/**
	 * 创建广告的通知栏
	 * 
	 * @param context
	 * @param intent
	 * @param title
	 * @param intro
	 * @param imgUrl
	 * @param notiId
	 * @return
	 */
//	public static Notification createGuangGaoNotification(Context context,
//			Intent intent, String title, String intro, Bitmap bmp, int notiId) {
//		NotificationManager notiManager = (NotificationManager) context
//				.getSystemService(Context.NOTIFICATION_SERVICE);
//		Notification notification = new Notification();
//		notification.icon = R.drawable.allset_download;
//		notification.tickerText = title;
//
//		// Notification notification = new Builder(context).getNotification();
//		notification.icon = R.drawable.icon;
//		RemoteViews remoteView = new RemoteViews(context.getPackageName(),
//				R.layout.notification_guanggao);
//		if (null != bmp) {
//			remoteView.setImageViewBitmap(R.id.img, bmp);
//		}
//		remoteView.setTextViewText(R.id.tv, intro);
//		notification.contentView = remoteView;
//		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
//				intent, 0);
//		notification.contentIntent = pendingIntent;
//		notification.flags = Notification.FLAG_AUTO_CANCEL;
//		notification.defaults = Notification.DEFAULT_SOUND;
//		notiManager.notify(notiId, notification);
//
//		return notification;
//	}

	/**
	 * 简单下载进度条
	 * 
	 * @param context
	 * @param title
	 * @param notiId
	 * @return
	 */
	public static Notification createNotification(String packageNam,
			NotificationManager notiManager, String title, int notiId) {
		Notification noti = new Notification();
		noti.icon = R.drawable.allset_download;
		noti.tickerText = title;

		RemoteViews remoteView = new RemoteViews(packageNam,
				R.layout.w_simple_noti_download_progress);

		noti.contentView = remoteView;
		remoteView.setProgressBar(R.id.simple_download_progressbar, 100, 0,
				false);
		remoteView.setTextViewText(R.id.tv_title, title);

		noti.flags = Notification.FLAG_AUTO_CANCEL;
		notiManager.notify(notiId, noti);

		return noti;
	}

}
