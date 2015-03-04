package com.inveno.piflow.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.inveno.piflow.dao.SdcardDao;
import com.inveno.piflow.download.ParseDownloadJson;
import com.inveno.piflow.entity.URLs;
import com.inveno.piflow.entity.model.showflow.ShowFlowNews;
import com.inveno.piflow.tools.bitmap.BitmapDisplayConfig;
import com.inveno.piflow.tools.bitmap.FlyshareBitmapManager;
import com.inveno.piflow.tools.commontools.DeviceConfig;
import com.inveno.piflow.tools.commontools.MkdirFileTools;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * App
 * 
 * @author hongchang.Liu
 * @date 2012-08-15
 */
public class FlyShareApplication extends Application {

	private FlyshareBitmapManager bitmapManager;

	private List<Activity> acts;


	
	/**瀑布流 每页为一项 <"reco_id" , ShowFlowNews> 点击上传的时候用**/
	private HashMap<String, ShowFlowNews> mShowFlowNewsMap;

	@Override
	public void onCreate() {
//		CrashHandler crashHandler = CrashHandler.getInstance();
//		// 注册crashHandler
//		crashHandler.init(getApplicationContext());
		acts = new ArrayList<Activity>();
		mShowFlowNewsMap = new HashMap<String, ShowFlowNews>(10);

		bitmapManager = FlyshareBitmapManager.create(getApplicationContext(),
				MkdirFileTools.IMAGS_PATH);
		bitmapManager.configAnim(BitmapDisplayConfig.AnimationType.fadeIn);

		ParseDownloadJson.clientId = SdcardDao.getInstance(
				getApplicationContext()).getClientId();
		Tools.showLog("newWidget", "接口action大小：" + URLs.urlActions.size());
		super.onCreate();
		SaveScreenSize();
	}

	/**
	 * 得到屏幕尺寸数据存入DeviceConfig
	 */
	private void SaveScreenSize() {
		DisplayMetrics size = new DisplayMetrics();
		WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(size);
		DeviceConfig config = DeviceConfig.getInstance(this);
		config.setHeight(size.heightPixels);
		config.setWidth(size.widthPixels);
		config.setModel(android.os.Build.MODEL);
		config.setSysVersion(android.os.Build.VERSION.RELEASE);
		// config.setMemory(manager.getTotalMemorySize() / 1048576);
		Tools.showLogB("手机屏幕:" + config.w + "X" + config.h);
		Tools.showLogB("手机内存大小：" + config.totalMemory);
	}

	



	public List<Activity> getActs() {
		return acts;
	}

	public void setActs(List<Activity> acts) {
		this.acts = acts;
	}

	public HashMap<String, ShowFlowNews> getmShowFlowNewsMap() {
		return mShowFlowNewsMap;
	}

//	public void setmShowFlowNewsMap(HashMap<String, ShowFlowNews> mShowFlowNewsMap) {
//		this.mShowFlowNewsMap = mShowFlowNewsMap;
//	}

	public void putToShowFlowNewsMap(String key,ShowFlowNews value){
		Tools.showLogA("加入集合："+value.size());
		this.mShowFlowNewsMap.put(key, value);
	}
}
