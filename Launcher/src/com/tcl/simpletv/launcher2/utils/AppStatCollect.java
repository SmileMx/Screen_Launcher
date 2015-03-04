package com.tcl.simpletv.launcher2.utils;

import java.net.URISyntaxException;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

public class AppStatCollect {
	private static final String TAG = AppStatCollect.class.getName();
	private Context mContext;
	private ArrayList<String> appList = new ArrayList<String>();
	public static final String AUTHORITY = "com.tcl.simpletv.appstat";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/appstat");
	public static final String FREQUENT_APP_NAME = "FrequentAppName";
	public static final String FREQUENT_APP_ICON_PATH = "FrequentAppIconPath";
	public static final String FREQUENT_APP_ICON_ID = "FrequentAppIconId";
	public static final String KEY_STATNUM = "statnum";
	public static final String KEY_INTENT = "intent";

	public AppStatCollect(Context context) {
		super();
		mContext = context;
	}

	private Drawable getAppIcon(Intent intent) {
		String path;
		int id;
		Drawable icon = null;
		Context slaveContext = null;
		path = intent.getStringExtra(FREQUENT_APP_ICON_PATH);
		if (path != null) {
			Log.d(TAG, "getAppIcon---path= " + path);

		} else {
			id = intent.getIntExtra(FREQUENT_APP_ICON_ID, 0);
			Log.d(TAG, "getAppIcon---id= " + id);
			if (id > 0) {
				try {
					slaveContext = mContext.createPackageContext(intent
							.getComponent().getPackageName(),
							Context.CONTEXT_IGNORE_SECURITY);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				icon = slaveContext.getResources().getDrawable(id);
			}

		}

		return icon;
	}

	public ArrayList<String> getStatAppsList() {
		String strIntent = null;
		Intent appIntent = null;
		String packageName = null;
		String appName = null;
		String activityName = null;
		Drawable appIcon = null;
		ApplicationInfo appInfo = null;
		String mAppInfo = null;
		PackageManager pm = mContext.getPackageManager();
		String sortOrder = KEY_STATNUM + " desc";
		ContentResolver contentResolver = mContext.getContentResolver();
		Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null,
				sortOrder);
		appList.clear();
		if (cursor != null && cursor.moveToFirst()) {
			Log.i(TAG, "cursor.getColumnCount()= " + cursor.getColumnCount()
					+ " getCount=" + cursor.getCount());
			for (int i = 0; i < cursor.getCount(); i++) {
				strIntent = cursor.getString(cursor.getColumnIndex(KEY_INTENT));
				cursor.moveToNext();
				if (strIntent != null) {
					// Log.i(TAG, "getAppList------strIntent= " + strIntent);
					/*
					 * 举例如下 strIntent=
					 * #Intent;action=android.intent.action.MAIN;
					 * category=android.intent.category.LAUNCHER;
					 * component=com.tcl.simpletv.myapps/.StartActivity;end
					 */
					try {
						appIntent = Intent.parseUri(strIntent, 0); // 把以上自符串传化成真正的Intent
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					packageName = appIntent.getComponent().getPackageName();
					activityName = appIntent.getComponent().getClassName();

					try {
						appInfo = pm.getApplicationInfo(packageName,
								PackageManager.GET_META_DATA);
					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if ((appIntent == null) || (appInfo == null)) {
						Log.e(TAG, "appintent or appInfo is null!");
						continue;
					}
					appName = appIntent.getStringExtra(FREQUENT_APP_NAME);
					if (appName == null) {
						Log.i(TAG, "appname---->");
						appName = pm.getApplicationLabel(appInfo).toString();
					}
					Log.i(TAG, "getAppList------appName=" + appName);
					appIcon = getAppIcon(appIntent);
					if (appIcon == null) {
						Log.i(TAG, "getAppList------get defaule appIcon.");
						appIcon = pm.getApplicationIcon(appInfo);
					}

//					mAppInfo = new MoreAppInfo();
//					mAppInfo.setAppName(appName);
//					mAppInfo.setAppIcon(appIcon);
//					mAppInfo.setAppIntent(appIntent);
//					mAppInfo.setPkgName(packageName);
//					mAppInfo.setActivityNam(activityName);
					appList.add(packageName);
				}
			}
		} else {
			Log.e(TAG, "cursor is null!");
		}
		cursor.close();
		return appList;
	}

}
