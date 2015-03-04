package com.inveno.piflow.tools.commontools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 手机硬件参数数据类，欢迎界面获取手机硬件参数存入SP中
 * 便于其他地方使用以及定时做上传
 * @author blueming.wu
 * @date 2012-10-22
 */
public class DeviceConfig {
	private static DeviceConfig config = null;
	private Context context = null;
	
	/**手机屏幕宽高（分辨率）*/
	public int w;
	public int h ;
	
	/**手机型号*/
	public String model;
	
	/**手机系统版本号（安卓版本）*/
	public String sysVersion;
	
	/**手机芯片型号*/
	public String hardWare;
	
	/**手机内存大小*/
	public long totalMemory;
	
	public static DeviceConfig getInstance(Context context){
		if(config == null)
			return new DeviceConfig(context);
		else
			return config;
	}
	
	public DeviceConfig(Context context){
		this.context = context;
		initHeight();
		initWidth();
		initMemory();
	}
	
	public void setHeight(int height){
		SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = pre.edit();
		editor.putInt("device_height", height);
		editor.commit();
		
	}
	
	public void setWidth(int width){		
		SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = pre.edit();
		editor.putInt("device_width", width);
		editor.commit();
	}
	
	public void setModel(String model){
		SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = pre.edit();
		editor.putString("device_model", model);
		editor.commit();
	}
	
	public void setSysVersion(String version){
		SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = pre.edit();
		editor.putString("device_sys_version", version);
		editor.commit();
	}
	
	public void setMemory(long memory){
		SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = pre.edit();
		editor.putLong("device_totalMemory", memory);
		editor.commit();
	}
	
	private void initWidth(){
		SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(context);
		w = pre.getInt("device_width", 0);
	}
	
	private void initHeight(){
		SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(context);
		h = pre.getInt("device_height", 0);
	}
	
	private void initMemory(){
		SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(context);
		totalMemory = pre.getLong("device_totalMemory", 0);
	}
	
	
}
