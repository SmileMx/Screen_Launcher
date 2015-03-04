package com.inveno.piflow.tools.commontools;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.inveno.piflow.dao.SdcardDao;

/**
 * 获取手机SIM卡信息的工具类 需要权限：android.permission.READ_PHONE_STATE
 * 
 * @author mingsong.zhang
 * @date 20120627
 */
public class TelephonyManagerTools {
	/** 有插SIM，状态良好 **/
	public final static String SIM_STATE_GOOD = "simStateGood";

	/** 无SIM卡 **/
	public final static String SIM_STATE_NO = "simStateNo";

	/** SIM卡被锁定或未知的状态 **/
	public final static String SIM_STATE_OTHER = "simStateOther";

	/**
	 * 唯一的设备ID：<br/>
	 * 如果是GSM网络，返回IMEI；如果是CDMA网络，返回MEID<br/>
	 * 需要权限：android.permission.READ_PHONE_STATE
	 * 
	 * @return null if device ID is not available.
	 */
	public static String getDeviceId(Context context) {

		String imei = Tools.getInformain("imei", "", context);
		if (StringTools.isEmpty(imei)) {
			TelephonyManager telMgr = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			SdcardDao sdcardDao = SdcardDao.getInstance(context);

			imei = telMgr.getDeviceId();
			if (StringTools.isEmpty(imei)) {
				imei = wifiManager.getConnectionInfo().getMacAddress();
			}
			if (StringTools.isEmpty(imei))
				imei = "888888888888888";
			Tools.setInformain("imei", imei, context);

			sdcardDao.saveImeiToImei(imei);
		}
		return imei;
	}

	/**
	 * 获取设备制造商
	 * 
	 * @author fei.zhang
	 * @date 2012-11-21
	 * @return
	 */
	public static String getManufacturerName() {
		return Build.MANUFACTURER;
	}

	/**
	 * 获取手机型号
	 * 
	 * @author fei.zhang
	 * @date 2012-11-21
	 * @return
	 */
	public static String getModelName() {
		return Build.MODEL;
	}

	/**
	 * 获取手机型号
	 * 
	 * @author fei.zhang
	 * @date 2012-11-21
	 * @return
	 */
	public static String getSystemVersion() {
		return Build.VERSION.RELEASE;
	}

	public static void getTopActivity(Activity context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
		if (runningTaskInfos != null)
			runningTaskInfos.size();
		runningTaskInfos.get(0).topActivity.toShortString();
	}
	
	/**
	 * 销统专用 勿改 勿用 唯一的设备ID：<br/>
	 * 如果是GSM网络，返回IMEI；如果是CDMA网络，返回MEID<br/>
	 * 需要权限：android.permission.READ_PHONE_STATE
	 * 
	 * @author mingsong.zhang
	 * @date 2013-05-16
	 * @return 有imei则返回实际imei，没有则返回"999999999999999"
	 */
	public static String getDeviceIdSS(Context context) {

		String imei = "";
		TelephonyManager telMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		imei = telMgr.getDeviceId();
		if (StringTools.isEmpty(imei)) {
			imei = "999999999999999";
		}

		return imei;
	}
}
