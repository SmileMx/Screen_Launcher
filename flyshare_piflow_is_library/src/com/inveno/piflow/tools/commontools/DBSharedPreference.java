package com.inveno.piflow.tools.commontools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 用来存储常用的本地信息的基本操作
 *
 * 从飞秘v3.20移过来的
 * @author mingsong.zhang
 * @date 2012-09-18
 */

public class DBSharedPreference {

	public SharedPreferences settings = null;
	public SharedPreferences.Editor editor = null;

	/**
	 * 本地信息存储文件名
	 */
	public static String SHAREDPREFERENCE_DB = "SHAREPREFERENCE_DB";

	public static String USERSTATE_READ = "USERSTATE_READ";

	public static String ADDDESKSTATE_INFO = "ADDDESKSTATE_INFO";

	public static String MSGSET_EXIT = "MSGSET_EXIT";

	public static String WEIBO_USERNAME = "WEIBO_USERNAME";

	public static String WEIBO_PASS = "WEIBO_PASS";

	public static DBSharedPreference dbSharedPreference;

	public static String ADDDESKEXIT = "ADDDESKEXIT";
	
	public static String SHOWOPEREXIT="SHOWOPEREXIT";
	
	public static String FIRST_OPEN_HUANGLI = "first_open_huangli";
	
	public static String FIRST_OPEN_MESSAGE = "first_open_message";
	
	public static String FIRST_OPEN_WEATHER = "first_open_weather";
	
	public static String LOAD_DEFAULT_WEA = "LOAD_DEFAULT_WEA";
	
	public static String FIRST_REQUESTAD_ONEDAY = "FIRST_REQUESTAD_ONEDAY";

	/**
	 * 实例化DBSharedPreference
	 * 
	 * @return
	 */
	public static DBSharedPreference getDBSharedPreference() {
		if (dbSharedPreference == null)
			dbSharedPreference = new DBSharedPreference();
		return dbSharedPreference;
	}

	/**
	 * 存储SharedPreference信息
	 * 
	 * @param context
	 */
	public void saveUserStatePre(Context context, String sharedName,
			String infoName, Boolean infock) {
		settings = context.getSharedPreferences(sharedName, 0);
		editor = settings.edit();
		editor.putBoolean(infoName, infock);
		editor.commit();
		if (settings != null)
			settings = null;
	}


	public boolean getUserStatePre(Context context, String sharedName,
			String infoName) {
		boolean ckreadState = false;
		settings = context.getSharedPreferences(sharedName, 0);
		if (settings != null) {
			ckreadState = settings.getBoolean(infoName, false);
		}
		return ckreadState;
	}

	public void saveShareInfo(Context context, String sharedName,
			String infoName, String infock) {
		settings = context.getSharedPreferences(sharedName, 0);
		editor = settings.edit();
		editor.putString(infoName, infock);
		editor.commit();
		if (settings != null)
			settings = null;
	}

	public String getShareInfo(Context context, String sharedName,
			String infoName) {
		String ckreadState = null;
		settings = context.getSharedPreferences(sharedName, 0);
		if (settings != null) {
			ckreadState = settings.getString(infoName, "");
		}
		return ckreadState;
	}
}
