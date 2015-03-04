package com.inveno.piflow.download.downloadmanager.download;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.inveno.piflow.tools.commontools.StringTools;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 下载的一些配置信息
 * 
 * @author blueming.wu
 * 
 * @date 2013-6-4
 */
public class DownLoadConfigUtil {

	/** 下载时保存的文件夹名字 */
	public static final String PREFERENCE_NAME = "piwindow_download";

	/** 通知下载的个数 */
	public static final int URL_COUNT = 3;

	/** 取url时用的key */
	public static final String KEY_URL = "url";

	/**
	 * 保存一个url到sp中
	 * 
	 * @param index
	 * @param url
	 */
	public static void storeURL(int index, String url, Context context) {
		Tools.setInformain(KEY_URL + index, url, context);
	}

	public static void clearURL(int index, Context context) {
		Tools.setInformain(KEY_URL + index, "", context);
	}

	public static String getURL(int index, Context context) {
		return Tools.getInformain(KEY_URL + index, "", context);
	}

	public static List<String> getURLArray(Context context) {
		List<String> urlList = new ArrayList<String>();
		for (int i = 0; i < URL_COUNT; i++) {
			if (!StringTools.isEmpty(getURL(i, context))) {
				urlList.add(getString(KEY_URL + i, context));
			}
		}
		return urlList;
	}

	private static String getString(String key, Context context) {
		return Tools.getInformain(key, "", context);
	}

}
