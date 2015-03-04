package com.inveno.piflow.tools.commontools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * 网络状态工具类
 * 
 * @update mingsong.zhang 2012-10-22
 */
public class ConnectUtil {

	/** gprs **/
	public static final String CONNECT_UTIL_GPRS = "gprs";

	/** wifi **/
	public static final String CONNECT_UTIL_WIFI = "wifi";

	/** none **/
	public static final String CONNECT_UTIL_NONE = "none";

	/**
	 * 判断网络是否可用
	 */
	public static boolean isNetWorkUsed(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isAvailable()) {
			return true;
		} else {
		}
		return false;
	}

	/**
	 * 获取连接的网络类型
	 */
//	public static String getNetWorkType(Context context) {
//		ConnectivityManager cm = (ConnectivityManager) context
//				.getSystemService(Context.CONNECTIVITY_SERVICE);
//		if (State.CONNECTED == cm.getNetworkInfo(
//				ConnectivityManager.TYPE_MOBILE).getState()) {
//			return CONNECT_UTIL_GPRS;
//		}
//		if (State.CONNECTED == cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
//				.getState()) {
//			return CONNECT_UTIL_WIFI;
//		}
//		return "none";
//	}

	/**
	 * 获取当前wifi的实时速度
	 * @param context
	 * @return
	 */
	public static int getWifiInfo(Context context) {
		WifiManager wifi_service = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifi_service.getConnectionInfo();
		int speed = wifiInfo.getLinkSpeed();

		return speed;
	}
}
