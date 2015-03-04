package com.inveno.piflow.tools.commontools;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 工具类
 * 
 * @author mingsong.zhang
 * @date 2012-07-23
 */
public class Tools {


	private static boolean OPENLOG = true ; // 是否开启打印Log true：打印, false：不打印


	private static boolean OPENMSG = false;// 是否发送短信 ture 发送，false 不打印

	/** 手机加油站的精选图KEY **/
	public static final String APP_MARROW_IMG = "app_marrow_img";

	/** 抽奖用户号码KEY **/
	public static final String LOTTERY_USER_PHONENUM = "lottery_user_phonenum";

	/** 提示设置语音的对话框是否显示的KEY **/
	public static final String TTS_NOMORE_ASK = "tts_nomore_ask";

	/** 语速KEY **/
	public static final String TTS_SPEECH = "tts_speech";

	/** 语音KEY **/
	public static final String TTS_VOLUME = "tts_volume";

	/** 读语音的模式KEY **/
	public static final String TTS_MODE = "tts_mode";

	/** 资讯内容图片加载模式的控制KEY **/
	public static final String FLOW_CONTROL_MODE = "flow_control_mode";
	
	/** 飞享缓存文件大小KEY **/
	public static final String FILE_CACHE_SIZE = "file_cache_size";

	/**
	 * 打印信息
	 * 
	 * @param TAG
	 *            标签
	 * @param tmp
	 *            内容
	 */
	public static void showLog(String TAG, String tmp) {
		if (OPENLOG)
			Log.i(TAG, "======" + tmp + "======");
	}

	/**
	 * 默认Log标签的输出
	 * 
	 * @param tmp
	 */
	public static void showLog(String tmp) {
		if (OPENLOG)
			showLog("info", tmp);
	}

	public static void showLogB(String tmp) {
		if (OPENLOG)
			showLog("blueming.wu", tmp);
	}

	public static void showLogA(String tmp) {
		if (OPENLOG)
			showLog("blueming.liu", tmp);
	}

	/**
	 * 设置信息long
	 */
	public static void setInformain(String Key, long Value, Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				Const.SETTINGS, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(Key, Value);
		editor.commit();
	}

	/**
	 * 取设置信息String
	 */
	public static long getLongInformain(String Key, long defValue,
			Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				Const.SETTINGS, 0);
		return settings.getLong(Key, defValue);
	}

	/**
	 * 设置信息String
	 */
	public static void setInformain(String Key, String Value, Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				Const.SETTINGS, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(Key, Value);
		editor.commit();
	}

	/**
	 * 取设置信息String
	 */
	public static String getInformain(String Key, String defValue,
			Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				Const.SETTINGS, 0);
		return settings.getString(Key, defValue);
	}

	/**
	 * 取设置信息
	 */
	public static int getInformain(String key, Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				Const.SETTINGS, 0);

		return settings.getInt(key, 0);
	}

	/**
	 * 取设置信息
	 */
	public static int getInformain(String key, int value, Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				Const.SETTINGS, 0);
		return settings.getInt(key, value);
	}

	/**
	 * 设置信息
	 */
	public static void setInformain(String key, int value, Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				Const.SETTINGS, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * 设置信息
	 */
	public static void setBooleaninformain(String key, boolean value,
			Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				Const.SETTINGS, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * 取设置信息
	 */
	public static boolean getBooleanInformain(String key, Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				Const.SETTINGS, 0);
		return settings.getBoolean(key, true);
	}

	/**
	 * 取设置信息
	 */
	public static boolean getBooleanInformain(String key, boolean defaultValue,
			Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				Const.SETTINGS, 0);
		return settings.getBoolean(key, defaultValue);
	}

	/**
	 * 取设置信息
	 */
	public static int getInformain(String name, String key, int value,
			Context context) {
		SharedPreferences settings = context.getSharedPreferences(name, 0);
		return settings.getInt(key, value);
	}

	/**
	 * 设置信息
	 */
	public static void setInformain(String name, String key, String value,
			Context context) {
		SharedPreferences settings = context.getSharedPreferences(name, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * 取设置信息
	 */
	public static String getInformain(String name, String key, String defValue,
			Context context) {
		SharedPreferences settings = context.getSharedPreferences(name, 0);
		return settings.getString(key, defValue);
	}

	/**
	 * 设置信息
	 */
	public static void setInformain(String name, String key, int value,
			Context context) {
		SharedPreferences settings = context.getSharedPreferences(name, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * 清除settings.xml某个键值对
	 * 
	 * @author fei.zhang
	 * @date 2012-11-16
	 */
	public static void remove(String key, Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				Const.SETTINGS, 0);
		Editor editor = settings.edit().remove(key);
		editor.commit();
	}

	/**
	 * 根据service类名来判断此服务是否启动
	 * 
	 * @author blueming.wu
	 * @param serviceName
	 *            服务类名
	 * @return
	 */
	public static boolean isServiceStart(String serviceName, Context context) {
		// 获得所有开启服务的集合
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> serviceInfos = am.getRunningServices(30);
		for (RunningServiceInfo runningServiceInfo : serviceInfos) {
			if (serviceName.equals(runningServiceInfo.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 发送临时clientId短信的方法
	 * 
	 * @param mContext
	 *            上下文
	 * @param receiverNumber
	 *            接收方号码
	 * @param msg
	 *            短信内容
	 * @param flag
	 *            值0为临时clientId发送，有回执广播，其他值没有回执广播
	 */
	public static void sendMsg(Context mContext, String receiverNumber,
			String msg, int flag) {
		if (OPENMSG) {
			TelephonyManager telMgr = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);

			if (telMgr.getSimState() == TelephonyManager.SIM_STATE_READY) {
				Tools.showLog("fei.zhang", "2、sim卡良好");
				SmsManager smsManager = SmsManager.getDefault();
				Intent intent = null;
				if (flag == 0) {
					intent = new Intent(Const.SENT_SMS_ACTION);
				} else {
					intent = new Intent();
				}
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						mContext, 0, intent, 0);
				// 内容不超过70个字符
				if (msg.length() < 70) {
					// 发送短信(没有权限捕获异常)
					try {
						smsManager.sendTextMessage(receiverNumber, null, msg,
								pendingIntent, null);
					} catch (SecurityException exception) {
						exception.printStackTrace();
						return;
					}
					Tools.showLog("fei.zhang", "3、发送的短信内容：" + msg);
				}
			} else if (telMgr.getSimState() == TelephonyManager.SIM_STATE_ABSENT) {
				Tools.showLog("fei.zhang", "无SIM卡");
			} else {
				Tools.showLog("fei.zhang", "SIM卡被锁定或未知的状态");
			}
		}

	}
     
}
