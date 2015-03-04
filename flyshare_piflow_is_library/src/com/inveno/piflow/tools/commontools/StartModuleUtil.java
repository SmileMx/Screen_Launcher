package com.inveno.piflow.tools.commontools;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.LinearLayout;

import com.inveno.piflow.activity.FlyShareApplication;
import com.inveno.piflow.activity.ext.WFNewsDetailActivity;
import com.inveno.piflow.biz.upload.PvBasicStateDataBiz;
import com.inveno.piflow.entity.model.showflow.ShowFlowHardAd;
import com.inveno.piflow.entity.model.showflow.ShowFlowNewinfo;
import com.inveno.piflow.entity.view.waterwall.WaterwallAudioView;
import com.inveno.piflow.entity.view.waterwall.WaterwallDetailView;

/**
 * 跳转四大组件工具类
 * 
 * @author hongchang.liu
 * 
 */
public class StartModuleUtil {

	/** 传给下载app的service的key **/
	public static final String APP_URL_KEY = "appurlkey";

	/** 应用名字 **/
	public static final String APP_NAME = "appName";

	/** app model Key **/
	public static final String APP_MODEL = "app_model";

	/** app category Key **/
	public static final String APP_CATEGORY_POSITION = "app_category_POSITION";
	
	/** 瀑布流资讯 Key **/
	public static final String WF_NEWS_BEAN = "wf_news_bean";
	
	/** 发送应用安装广播action **/
	public static final String APP_INSTALL_BROADCAST = "app_install_broadcast";
	/** 发送应用安装广播应用包名Key **/
	public static final String APP_INSTALL_PACKAGENAME = "app_install_packageName";
	/**
	 * 跳转系统安装app界面
	 * 
	 * @param context
	 * @param appPath
	 *            本地app包地址
	 */
	public static void startSysInstall(Context context, String appPath) {

		Intent install = new Intent();
		install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		install.setAction(android.content.Intent.ACTION_VIEW);
		install.setDataAndType(Uri.fromFile(new File(appPath)),
				"application/vnd.android.package-archive");
		context.startActivity(install);
	}


	
	
	/**
	 * 跳转瀑布点点击的资讯页
	 * @param context
	 * @param showFlowNewinfo
	 */
	public static void startWFNewsDetailAct(Context context,ShowFlowNewinfo showFlowNewinfo){
		Intent intent = new Intent(context, WFNewsDetailActivity.class);
		intent.putExtra(WF_NEWS_BEAN, showFlowNewinfo);		
		PvBasicStateDataBiz.BASEFLAG = false;
		context.startActivity(intent);
	}
	
	
	/**
	 * 发送应用安装广播
	 * @param context
	 * @param packageName
	 */
	public static void sendAPPInstallBroadcast(Context context,String packageName){
		Intent intent=new Intent(APP_INSTALL_BROADCAST);
		intent.putExtra(APP_INSTALL_PACKAGENAME, packageName);
		context.sendBroadcast(intent);
	}
	
	public static long time = 0;
	
	/**
	 * TCL用 打开详情页面
	 * @param context
	 * @param showFlowNewinfo
	 * @param layout
	 */
	public static void startWaterwallDetailView(Context context , ShowFlowNewinfo showFlowNewinfo , LinearLayout layout , LinearLayout dialogLayout, FlyShareApplication app){
		if(layout!=null){
			
			long now = System.currentTimeMillis();
			
			if(now - time > 1000){
			
				ShowFlowHardAd flowHardAd = showFlowNewinfo.getShowFlowHardAd();
				if(flowHardAd != null && "6".equals(flowHardAd.getCtype())){
					WaterwallAudioView wav = new WaterwallAudioView(context, showFlowNewinfo,layout,app);
					layout.addView(wav);
				}else{
					WaterwallDetailView wdv = new WaterwallDetailView(context, showFlowNewinfo, layout,dialogLayout,app);
					layout.addView(wdv);
				}
				
				time = now;
			
			}
		
		}
	}
}
