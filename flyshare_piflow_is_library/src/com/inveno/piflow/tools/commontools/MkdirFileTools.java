package com.inveno.piflow.tools.commontools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.inveno.piflow.entity.model.MeiTuInfo;
import com.inveno.piflow.entity.model.NewsInfo;
import com.inveno.piflow.tools.android.BitmapTools;

/**
 * 创建文件夹和文件保存，读取，删除，更新的工具类
 * 
 * @author mingsong.zhang
 * @date 2012-08-11
 */
public class MkdirFileTools implements Const {

	/** 保存类型 */
	public static final int TYPE_PNG = 1;
	public static final int TYPE_JPEG = 2;

	/** content保存详细资讯json格式的key **/
	public static final String CONTENT = "content";

	// ----------名字---------

	/** 根目录 **/
	public static final String FLYSHARE = "TCL_waterflow";

	/** 存放用户下载的图片的目录FlyShare/flyshare **/
	public static final String DOWNLOAD_IMGS = "flyshare";

	/** 缓存目录FlyShare/cache **/
	public static final String CACHE = "cache";

	/** 存放飞享所有图片的根目录 **/
	public static final String IMAGES = "aimages";

	/** 存放广告页图片和资讯图片的目录FlyShare/cache/datainfoimg **/
	public static final String DATA_INFO_IMG = "datainfoimg";

	/** 存放资讯的文本文件目录FlyShare/datatxt **/
	public static final String DATA_TXT = "datatxt";

	/** 存放获取频道资讯列表的目录FlyShare/datatxt/channelinfo **/
	public static final String CHANNEL_INFO = "channelinfo";

	/** 存放请求产生clientId接口, 手机号接口获取的数据的文件flyshare.txt **/
	public static final String FLYSHARE_TXT = "flyshare.txt";

	/** 存放客户端配置数据的文件configuration.txt **/
	public static final String CONFIGURATION_TXT = "configuration.txt";

	/** 存放存放频道列表的文件channellist.txt **/
	public static final String CHANNELLIST_TXT = "channellist.txt";

	/** 存放频道设置为离线下载的文件outlinedownchannels.txt **/
	public static final String OUTLINE_DOWN_CHANNELS_TXT = "outlinedownchannels.txt";

	/** 存放最新从广告接口获取的图片名字cover.+ **/
	public static final String COVER = "cover.+";

	/** 保存最新从广告接口获取的图片名字文件 cover.txt 文件格式{"cover":"xxx"} **/
	public static final String COVER_TXT = "cover.txt";

	/** 保存最新从广告接口获取的图片名字文件 cover.txt的key 文件格式{"cover":"xxx"} **/
	public static final String COVER_TXT_KEY = "cover";

	/** 存放该频道当前已存的最大index，maxindex.txt **/
	public static final String MAX_INDEX_TXT = "maxindex.txt";

	/** 存放保存频道对应的图片目录名channelimgs **/
	public static final String CHANNEL_IMGS = "channelimgs";

	/** 存放详细资讯的目录名FlyShare/cache/fullcontent **/
	public static String FULL_CONTENT = "fullcontent";

	/** 存放详细资讯的目录名FlyShare/cache/apps **/
	public static String APPS = "apps";

	/** 存放手机服务区的目录名FlyShare/flyshare/mobileService **/
	public static String MOBILESERVICE = "mobileService";

	/** 存放手机服务区txt文件的目录名FlyShare/flyshare/mobileService/Adstxt **/
	public static String ADSTXT = "Adstxt";

	/** 存放手机服务区图片的目录名FlyShare/flyshare/mobileService/AdsImg **/
	public static String ADSIMG = "AdsImg";

	/**
	 * 存放手机服务区最近的相应厂商活动已经在widget上展示的天数的文件名FlyShare/flyshare/mobileService/
	 * adsshowdays.txt
	 **/
	public static String ADS_SHOW_DAYS_TXT = "adsshowdays.txt";

	/** 存放updatetime的文件flyshare.txt **/
	public static final String UPDATETIME_TXT = "updatetime.txt";

	/** 存放频道设置是否展开的文件FlyShare/datatxt/expandGroupIds.txt **/
	public static final String EXPAND_GROUP_IDS_TXT = "expandGroupIds.txt";

	/** 存放widget新闻资讯文件FlyShare/datatxt/widgetNews.txt **/
	public static final String WIDGET_NEWS_TXT = "widgetNews.txt";

	/** 存放widget新闻资讯上次更新到最大的index的文件FlyShare/datatxt/widgetNewsIndex.txt **/
	public static String WIDGET_NEWS_INDEX_TXT = "widgetNewsIndex.txt";

	/** 存放widget有图片的新闻的目录FlyShare/datatxt/widgetNews **/
	public static final String WIDGET_NEWS = "widgetNews";

	/** 存放已读资讯文件FlyShare/datatxt/articleRead.txt **/
	public static final String ARTICLE_READ_TXT = "articleRead.txt";

	/** 存放短信发送次数的文件FlyShare/datatxt/sendMsgTimes.txt */
	public static final String SENDMSG_TIMES_TXT = "sendMsgTimes.txt";

	/** 存放手机IMEI的文件FlyShare/datatxt/imei.txt */
	public static final String IMEI_TXT = "imei.txt";
	/** 存放用户安装卸载第三方应用的目录FlyShare/flayshare/thirdApp */
	public static final String THIRD_APP = "thirdApp";

	/** 存放用户安装卸载第三方应用的文件FlyShare/flayshare/thirdApp/thirdApp.txt */
	public static final String THIRD_APP_TXT = "thirdApp.txt";

	/** 存放销统相关数据的文件名FlyShare/datatxt/salesStatistics.txt **/
	public static final String SALES_STATISTICS_TXT = "salesStatistics.txt";

	/** 存放挖掘用户数据的目录FlyShare/flayshare/.android_pvdata */
	public static final String PV_DATA = ".android_pvdata";
	/** 存放挖掘用户手机基本功能目录文件FlyShare/flayshare/.android_pvdata/basicOperation.txt */
	public static final String BASIC_OPERATION_TXT = "basicOperation.txt";

	/** 存放挖掘流量信息的文件 ,它的路径：FlyShare/flyshare/.android_pvdata/netflow.txt **/
	public static final String NET_FLOW_TXT = "netflow.txt";

	/** 存放飞享版本号的文件FlyShare/datatxt/version.txt */
	public static final String VERSION_TXT = "version.txt";
	/** 存放飞享请求接口,FlyShare/datatxt/request.txt */
	public static final String REQUEST_TXT = "request.txt";

	/** 存放瀑布流最后一页数据的文件名字 */
	public static final String FLOW_DATA_TXT = "flownews.txt";
	// ----------------

	// -------路径------

	/** sdcard根目录 **/
	public static String SDCARDPATH;

	/** 存放请求产生clientId接口, 手机号接口获取的数据的文件FlyShare/datatxt/flyshare.txt路径 **/
	public static String CAMN_TXT_PATH;

	/** 存放客户端配置数据的文件FlyShare/datatxt/configuration.txt路径 **/
	public static String CONFIGURATION_PATH;

	/** 存放存放频道列表的文件FlyShare/datatxt/channellist.txt路径 **/
	public static String CHANNELLIST_PATH;

	/** 存放资讯频道列表的目录路径FlyShare/datatxt/channelinfo **/
	public static String CHANNELINFO_PATH;

	/** 存放设置为离线下载的文件路径FlyShare/datatxt/outlinedownchannels.txt **/
	public static String OUTLINE_DOWN_CHANNELS_PATH;

	/** 存放保存频道对应的图片目录路径FlyShare/datatxt/channelimgs **/
	public static String CHANNEL_IMGS_PATH;

	/** 存放详细资讯的目录路径FlyShare/cache/fullcontent **/
	public static String FULL_CONTENT_PATH;

	// 飞享所有图片缓存，分为多个部分start
	/** 存放飞享所有图片的全目录 **/
	public static String IMAGS_PATH;

	// 飞享所有图片缓存目录，end

	/** 存放广告页图片和资讯图片的目录路径FlyShare/cache/datainfoimg **/
	public static String DATA_INFO_IMG_PATH;

	/** 存放手机加油站应用记录目录路径FlyShare/cache/apps **/
	public static String APPS_PATH;

	/** 存放手机服务区录路径FlyShare/flyshare/mobileService/Adstxt **/
	public static String ADSTXT_PATH;

	/** 存放手机服务区录路径FlyShare/flyshare/mobileService/AdsImg **/
	public static String ADSIMG_PATH;

	/**
	 * 存放手机服务区最近的相应厂商活动已经在widget上展示的天数的文件路径FlyShare/flyshare/mobileService/
	 * adsshowdays.txt
	 **/
	public static String ADS_SHOW_DAYS_PATH;

	/** 存放updatetime的文件FlyShare/datatxt/updatetime.txt路径 **/
	public static String UPDATETIME_TXT_PATH;

	/** 存放widget的文件FlyShare/datatxt/widget.txt路径 **/
	public static String WIDGET_TXT_PATH;

	/** 存放频道设置是否展开的文件路径FlyShare/datatxt/expandGroupIds.txt **/
	public static String EXPAND_GROUP_IDS_TXT_PATH;

	/** 存放widget新闻目录路径FlyShare/datatxt/widgetNews **/
	public static String WIDGET_NEWS_PATH;

	public static String ARTICLE_READ_PATH;

	/** 存放短信发送times次数的路径 FlyShare/datatxt/sendMsgTimes.txt */
	public static String SENDMSG_TIMES_PATH;

	/** 存放手机IMEI的文件FlyShare/datatxt/imei.txt */
	public static String IMEI_PATH;

	/** 存放用户安装卸载第三方应用的文件FlyShare/flayshare/thirdApp/thirdApp.txt */
	public static String THIRD_APP_PATH;

	/** 存放挖掘用户手机基本功能目录文件FlyShare/flayshare/.android_pvdata/basicOperation.txt */
	public static String BASIC_OPERATION_PATH;

	/** 存放挖掘流量信息的文件的路径：FlyShare/flyshare/.android_pvdata/netflow.txt **/
	public static String NET_FLOW_TXT_PATH;

	/** 存放销统相关数据的文件路径：FlyShare/datatxt/salesStatistics.txt **/
	public static String SALES_STATISTICS_TXT_PATH;

	/** 存放飞享版本号的文件FlyShare/datatxt/version.txt */
	public static String VERSION_PATH;
	/** 存放飞享请求接口及流量 消耗FlyShare/datatxt/request.txt */
	public static String REQUEST_PATH;

	/** 存放瀑布流最后一页数据的文件路径 */
	public static String FLOW_DATA_PATH;

	// --------------------

	/** 静态块，创建文件夹，只会执行一次 **/
	static {

		mkdirFileAndPath();
	}

	private static void mkdirFileAndPath() {
		// 获取SD卡根目录
		SDCARDPATH = getSDPath();
		// 根目录路径
		String flyshare = SDCARDPATH + File.separator + FLYSHARE
				+ File.separator;
		Tools.showLogA("创建目录:" + flyshare);
		// 有SD卡才会执行
		if (sdCardExist()) {

			// 存放飞享所有图片的目录
			IMAGS_PATH = flyshare + CACHE + File.separator + IMAGES;
			File fileImages = new File(IMAGS_PATH);

			// 存放广告页图片和资讯图片的目录
			DATA_INFO_IMG_PATH = flyshare + CACHE + File.separator
					+ DATA_INFO_IMG;
			File fileDataInfoImg = new File(DATA_INFO_IMG_PATH);

			// 存放详细资讯的目录
			FULL_CONTENT_PATH = flyshare + CACHE + File.separator
					+ FULL_CONTENT;
			File fileFullContent = new File(FULL_CONTENT_PATH);

			// 存放获取频道资讯列表的目录
			CHANNELINFO_PATH = flyshare + DATA_TXT + File.separator
					+ CHANNEL_INFO;
			File fileChannelInfo = new File(CHANNELINFO_PATH);

			// 存放用户下载的图片的目录
			File fileFlyShare = new File(flyshare + DOWNLOAD_IMGS);

			// 存放保存频道对应的图片目录
			CHANNEL_IMGS_PATH = flyshare + DATA_TXT + File.separator
					+ CHANNEL_IMGS;
			File fileChannelImgs = new File(CHANNEL_IMGS_PATH);

			// 存放手机加油站应用icon的目录
			APPS_PATH = flyshare + CACHE + File.separator + APPS;
			File fileApps = new File(APPS_PATH);

			// 存放手机服务区产品的目录

			ADSTXT_PATH = flyshare + DOWNLOAD_IMGS + File.separator
					+ MOBILESERVICE + File.separator + ADSTXT;

			File fileAdstxt = new File(ADSTXT_PATH);
			// 存放手机服务区图片的目录
			ADSIMG_PATH = flyshare + DOWNLOAD_IMGS + File.separator
					+ MOBILESERVICE + File.separator + ADSIMG;

			File fileAdsImg = new File(ADSIMG_PATH);

			// 存放widget新闻的目录
			WIDGET_NEWS_PATH = flyshare + DATA_TXT + File.separator
					+ WIDGET_NEWS;
			File fileWidgetNews = new File(WIDGET_NEWS_PATH);

			// 创建存放美图秀秀图片的目录
			if (!fileImages.exists()) {
				fileImages.mkdirs();
			}

			// 创建存放广告页图片和资讯图片的目录
			if (!fileDataInfoImg.exists()) {
				fileDataInfoImg.mkdirs();
			}

			// 创建存放获取频道资讯列表的目录
			if (!fileChannelInfo.exists()) {
				fileChannelInfo.mkdirs();
			}

			// 创建存放用户下载的图片的目录
			if (!fileFlyShare.exists()) {
				fileFlyShare.mkdirs();
			}

			// 创建存放保存频道对应的图片目录
			if (!fileChannelImgs.exists()) {
				fileChannelImgs.mkdirs();
			}

			// 创建存放详细资讯的目录
			if (!fileFullContent.exists()) {
				fileFullContent.mkdirs();
			}

			// 创建存放手机加油站应用icon的目录
			if (!fileApps.exists()) {
				fileApps.mkdirs();
			}

			// 创建存放手机服务区txt文件的目录
			if (!fileAdstxt.exists()) {
				fileAdstxt.mkdirs();
			}
			// 创建存放手机服务区img文件的目录
			if (!fileAdsImg.exists()) {
				fileAdsImg.mkdirs();
			}

			// 创建widget新闻文件的目录
			if (!fileWidgetNews.exists()) {
				fileWidgetNews.mkdirs();
			}

		}
		try {

			// 创建请求接口及流量消耗 FlyShare/datatxt/request.txt
			REQUEST_PATH = flyshare + DATA_TXT + File.separator + REQUEST_TXT;
			mkdirFile(REQUEST_PATH);

			// 创建存放请求产生clientId接口, 手机号接口获取的数据的文件FlyShare/datatxt/flyshare.txt
			CAMN_TXT_PATH = flyshare + DATA_TXT + File.separator + FLYSHARE_TXT;
			mkdirFile(CAMN_TXT_PATH);

			// 创建存放客户端配置数据的文件FlyShare/datatxt/configuration.txt
			CONFIGURATION_PATH = flyshare + DATA_TXT + File.separator
					+ CONFIGURATION_TXT;
			mkdirFile(CONFIGURATION_PATH);

			// 创建存放频道列表的文件FlyShare/datatxt/channellist.txt
			CHANNELLIST_PATH = flyshare + DATA_TXT + File.separator
					+ CHANNELLIST_TXT;
			mkdirFile(CHANNELLIST_PATH);

			// 存放设置为离线下载的文件路径FlyShare/datatxt/outlinedownchannels.txt
			OUTLINE_DOWN_CHANNELS_PATH = flyshare + DATA_TXT + File.separator
					+ OUTLINE_DOWN_CHANNELS_TXT;
			mkdirFile(OUTLINE_DOWN_CHANNELS_PATH);

			// 创建updatetime的文件FlyShare/datatxt/updatetime.txt
			UPDATETIME_TXT_PATH = flyshare + DATA_TXT + File.separator
					+ UPDATETIME_TXT;
			mkdirFile(UPDATETIME_TXT_PATH);

			// 存放频道设置是否展开的文件路径FlyShare/datatxt/expandGroupIds.txt
			EXPAND_GROUP_IDS_TXT_PATH = flyshare + DATA_TXT + File.separator
					+ EXPAND_GROUP_IDS_TXT;
			mkdirFile(EXPAND_GROUP_IDS_TXT_PATH);

			// 存放已读资讯的文件
			ARTICLE_READ_PATH = flyshare + DATA_TXT + File.separator
					+ ARTICLE_READ_TXT;
			mkdirFile(ARTICLE_READ_PATH);

			// 存放widget新闻资讯上展示的活动已经展示了的天数
			ADS_SHOW_DAYS_PATH = flyshare + DOWNLOAD_IMGS + File.separator
					+ MOBILESERVICE + File.separator + ADS_SHOW_DAYS_TXT;
			mkdirFile(ADS_SHOW_DAYS_PATH);

			// 创建存放短信发送次数的数据文件 FlyShare/datatxt/sendMsgTimes.txt
			SENDMSG_TIMES_PATH = flyshare + DATA_TXT + File.separator
					+ SENDMSG_TIMES_TXT;
			mkdirFile(SENDMSG_TIMES_PATH);

			// 创建存放手机IMEI的数据文件 FlyShare/datatxt/imei.txt
			IMEI_PATH = flyshare + DATA_TXT + File.separator + IMEI_TXT;
			mkdirFile(IMEI_PATH);

			// 创建用户安装卸载第三方应用的文件FlyShare/flayshare/thirdApp/thirdApp.txt */
			THIRD_APP_PATH = flyshare + DOWNLOAD_IMGS + File.separator
					+ THIRD_APP + File.separator + THIRD_APP_TXT;

			mkdirFile(THIRD_APP_PATH);

			// 存放挖掘用户手机基本功能目录文件FlyShare/flayshare/.android_pvdata/basicOperation.txt
			// */
			BASIC_OPERATION_PATH = flyshare + DOWNLOAD_IMGS + File.separator
					+ PV_DATA + File.separator + BASIC_OPERATION_TXT;

			mkdirFile(BASIC_OPERATION_PATH);

			// 创建挖掘流量信息的文件FlyShare/flyshare/.android_pvdata/netflow.txt
			NET_FLOW_TXT_PATH = flyshare + DOWNLOAD_IMGS + File.separator
					+ PV_DATA + File.separator + NET_FLOW_TXT;
			mkdirFile(NET_FLOW_TXT_PATH);

			// 创建销统相关数据的文件FlyShare/datatxt/salesStatistics.txt
			SALES_STATISTICS_TXT_PATH = flyshare + DATA_TXT + File.separator
					+ SALES_STATISTICS_TXT;
			mkdirFile(SALES_STATISTICS_TXT_PATH);

			// 创建存放手机IMEI的数据文件 FlyShare/datatxt/imei.txt
			VERSION_PATH = flyshare + DATA_TXT + File.separator + VERSION_TXT;
			mkdirFile(VERSION_PATH);

			FLOW_DATA_PATH = flyshare + DATA_TXT + File.separator
					+ FLOW_DATA_TXT;
			mkdirFile(FLOW_DATA_PATH);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取SD卡根目录
	 * 
	 * @return
	 */
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = sdCardExist(); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取根目录
		} else {
			// Tools.showLogA("无SD卡，创建flyshare更目录到data下");
			// sdDir = BitmapTools.getDiskCacheDir(context, "flyCache");
			return null;
		}
		return sdDir.toString();

	}

	/**
	 * 判断sd卡是否存在
	 * 
	 * @return true:存在；false:不存在
	 */
	public static boolean sdCardExist() {
		return Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 创建文件夹
	 */
	public static void mdirFiles() {

	}

	/**
	 * 根据key获取value
	 * 该方法适用于简单的格式:{"smsnumber":"13128896155","clientId":"100000","city ":"深圳"}
	 * 
	 * @date 2012-6-13
	 * @param key
	 *            要查询的key
	 * @param path
	 *            eg:该的路径
	 * @return 返回查询到的值，没有该值则返回""
	 * @throws IOException
	 */
	public static String getValue(String key, String path) {
		if (StringTools.isEmpty(SDCARDPATH)) {
			mkdirFileAndPath();
		}
		String value = ""; // 需要返回的值
		try {
			String jsonStr = getJsonString(path); // 获取json格式文件中的内容
			if (StringTools.isNotEmpty(jsonStr)) {
				JSONObject dataJson = new JSONObject(jsonStr);
				if (dataJson.has(key)) {
					value = dataJson.getString(key);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
		return value;
	}

	/**
	 * 获取该频道已保存的maxindex
	 * 
	 * @param channelId
	 *            资讯id
	 * @return
	 */
	public static int getMaxIndex(int channelId) {
		if (StringTools.isEmpty(SDCARDPATH)) {
			mkdirFileAndPath();
		}
		int value = 0; // 需要返回的值
		String path = CHANNELINFO_PATH + File.separator + channelId
				+ File.separator + MAX_INDEX_TXT;
		try {
			String jsonStr = getJsonString(path); // 获取json格式文件中的内容
			if (StringTools.isNotEmpty(jsonStr)) {
				JSONObject dataJson = new JSONObject(jsonStr);
				value = dataJson.getInt(MAXINDEX);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 保存该频道已保存的最大maxindex
	 * 
	 * @param value
	 *            资讯maxindex
	 * @param channelId
	 *            频道id
	 * @throws IOException
	 */
	public static void putMaxIndex(int value, int channelId) throws IOException {
		if (StringTools.isEmpty(SDCARDPATH)) {
			mkdirFileAndPath();
		}
		BufferedWriter bw = null;
		JSONObject dataJson = new JSONObject();
		try {
			String path = CHANNELINFO_PATH + File.separator + channelId
					+ File.separator + MAX_INDEX_TXT;

			mkdirFile(path);
			dataJson = new JSONObject();

			/** 将新的键值对加进来 **/
			dataJson.put(MAXINDEX, value);

			bw = new BufferedWriter(new FileWriter(path));
			String result = dataJson.toString();

			bw.write(result);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != bw) {
				bw.flush();
				bw.close();
			}
		}

	}

	/**
	 * 保存value
	 * 该方法适用于简单的格式:{"smsnumber":"13128896155","clientId":"100000","city ":"深圳"}
	 * 
	 * @param key
	 *            key名
	 * @param value
	 *            key对应的值
	 * @param path
	 *            eg:该文件的路径:sdcard/FlyShare/datatxt/camn.txt
	 * @throws IOException
	 */
	public static synchronized void putValue(String key, String value,
			String path) throws IOException {
		if (StringTools.isNotEmpty(path)) {
			if (StringTools.isEmpty(SDCARDPATH)) {
				mkdirFileAndPath();
			}
			// sd卡满
			if (BitmapTools.getUsableSpace(Environment
					.getExternalStorageDirectory()) == 0) {
				return;
			}
			BufferedWriter bw = null;
			JSONObject dataJson = new JSONObject();
			try {
				String jsonStr = getJsonString(path);

				/** 假如该json格式文件中已有内容则重新实例化JSONObject **/
				if (StringTools.isNotEmpty(jsonStr)) {
					dataJson = new JSONObject(jsonStr);
				}
				/** 将新的键值对加进来 **/
				dataJson.put(key, value);

				bw = new BufferedWriter(new FileWriter(path));
				String result = dataJson.toString();
				bw.write(result);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (null != bw) {
					bw.flush();
					bw.close();
				}
			}

		}

	}

	/**
	 * 同时保存多个value
	 * 该方法适用于简单的格式:{"smsnumber":"13128896155","clientId":"100000","city ":"深圳"}
	 * 
	 * @param key
	 *            key名
	 * @param value
	 *            key对应的值
	 * @param path
	 *            eg:该文件的路径:sdcard/FlyShare/datatxt/camn.txt
	 * @throws IOException
	 */
	public static synchronized void putValueS(HashMap<String, String> hashMap,
			String path) throws IOException {
		if (StringTools.isEmpty(SDCARDPATH)) {
			mkdirFileAndPath();
		}
		BufferedWriter bw = null;
		JSONObject dataJson = new JSONObject();
		try {
			String jsonStr = getJsonString(path);

			/** 假如该json格式文件中已有内容则重新实例化JSONObject **/
			if (StringTools.isNotEmpty(jsonStr)) {
				dataJson = new JSONObject(jsonStr);
			}
			/** 将新的键值对加进来 **/
			Set<Entry<String, String>> set = hashMap.entrySet();
			for (Entry<String, String> entry : set) {
				dataJson.put(entry.getKey(), entry.getValue());
			}

			bw = new BufferedWriter(new FileWriter(path));
			String result = dataJson.toString();
			bw.write(result);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != bw) {
				bw.flush();
				bw.close();
			}
		}

	}

	/**
	 * 获取json格式文件中的字符串
	 * 
	 * @param path
	 *            该文件的路径
	 * @return
	 * @throws IOException
	 */

	public static String getJsonString(String path) {
		if (StringTools.isEmpty(SDCARDPATH)) {
			mkdirFileAndPath();
		}
		Tools.showLog("zms", "从SD卡获取json字符串:" + path);
		String jsonStr = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path));
			jsonStr = br.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != br)
					br.close();
			} catch (IOException e) {
				return null;
			}
		}
		return jsonStr;
	}

	/**
	 * 创建文件
	 * 
	 * @param path
	 * @throws IOException
	 */
	public static synchronized void mkdirFile(String path) throws IOException {
		if (sdCardExist() && StringTools.isNotEmpty(path)) {

			if (StringTools.isEmpty(SDCARDPATH)) {
				mkdirFileAndPath();
			}

			File file = new File(path);

			File fileTemp = new File(file.getParent());
			if (!fileTemp.exists()) {
				fileTemp.mkdirs();
			}
			Tools.showLog("每页资讯路径path=" + path);
			file.createNewFile();
		}

	}

	/**
	 * 将字符串写到文件中
	 * 
	 * @param jsonStr
	 *            需要写在文件中的字符串
	 * @param path
	 *            文件路径
	 * @throws IOException
	 */
	public static synchronized void saveJsonStrToFile(String jsonStr,
			String path) throws IOException {
		if (sdCardExist() && StringTools.isNotEmpty(path)) {
			if (StringTools.isEmpty(SDCARDPATH)) {
				mkdirFileAndPath();
			}
			// sd卡满
			if (BitmapTools.getUsableSpace(Environment
					.getExternalStorageDirectory()) == 0) {
				return;
			}
			File file = new File(path);
			mkdirFile(path);
			if (file.exists()) {
				BufferedWriter bw = null;
				try {
					bw = new BufferedWriter(new FileWriter(path));
					bw.write(jsonStr);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (null != bw)
						bw.close();
				}
			}

		}

	}

	/**
	 * 保存资讯频道内容到SD卡，每六条一页保存为一个文件 ，并且将专题保存在数据库中
	 * 
	 * @param newsInfos
	 *            存放资讯频道的集合
	 * @param channelId
	 *            频道id
	 */
	public synchronized static void saveNewsInfoToSDcard(
			ArrayList<NewsInfo> newsInfoss, int channelId, Context context) {
		try {
			if (StringTools.isEmpty(SDCARDPATH)) {
				mkdirFileAndPath();
			}
			JSONArray jsonArray = new JSONArray();
			ArrayList<NewsInfo> newsInfos = new ArrayList<NewsInfo>(6);
			newsInfos.addAll(newsInfoss);
			int maxIndex = 0; // 每页的最大index
			int tempIndex = 0;
			int len = newsInfos.size();

			int dividend;
			// 图片类型的频道都是1000以上
			if (String.valueOf(channelId).length() < 4) {
				dividend = 6;
			} else { // 美图秀秀是7张一页
				dividend = 7;
			}

			int j = 0;
			for (int i = 0; i < len; i++) {
				NewsInfo newsInfo = newsInfos.get(i);

				if (newsInfo != null) {
					JSONObject jsonObject = new JSONObject();

					int typeId = newsInfo.getId();
					jsonObject.put("id", typeId);

					String title = newsInfo.getTitle();
					jsonObject.put("title", title);

					jsonObject.put("intro", newsInfo.getIntro());

					String source = newsInfo.getSource();
					jsonObject.put("source", source);

					String img = newsInfo.getImg();
					jsonObject.put("img", img);
					String simg = newsInfo.getSimg();
					jsonObject.put("simg", simg);

					String releaseTime = newsInfo.getReleaseTime();
					jsonObject.put("releaseTime", releaseTime);

					int special = newsInfo.getSpecial();
					jsonObject.put("special", special);

					jsonObject.put("major", newsInfo.getMajor());

					jsonObject.put("index", newsInfo.getIndex());
					jsonObject.put("originalUrl", newsInfo.getOriginalUrl());

					jsonArray.put(jsonObject);

					j = i + 1;
					// if (j % 6 == 1) {
					// maxIndex = newsInfo.getIndex(); // 每页的最大index
					// }
					tempIndex = newsInfo.getIndex();
					if (maxIndex < tempIndex) {
						maxIndex = tempIndex; // 每页的最大index
					}

					if (j % dividend == 0) {
						String path = CHANNELINFO_PATH + File.separator
								+ channelId + File.separator + maxIndex
								+ ".txt";
						Tools.showLog("6条保存为一页 每一页资讯路径 path" + path);
						mkdirFile(path); // 创建文件

						saveJsonStrToFile(
								new JSONStringer().object().key("data")
										.value(jsonArray).endObject()
										.toString(), path); // 保存内容到文件中
						jsonArray = null;
						jsonArray = new JSONArray();
						maxIndex = 0;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存美图秀秀内容到SD卡中，每7条为一页保存为一个文件
	 * 
	 * @param meiTuInfos
	 * @param meiTuShowId
	 *            美图秀秀频道的id
	 */
	public static void saveMeiTuInfosToSDcard(ArrayList<MeiTuInfo> meiTuInfos,
			int meiTuShowId) {
		try {
			if (StringTools.isEmpty(SDCARDPATH)) {
				mkdirFileAndPath();
			}
			if (meiTuInfos != null) {
				JSONArray jsonArray = new JSONArray();
				String maxIndex = ""; // 每页的最大index
				ArrayList<MeiTuInfo> newsInfos = new ArrayList<MeiTuInfo>(7);
				newsInfos.addAll(meiTuInfos);
				int len = newsInfos.size();
				int j = 0;
				for (int i = 0; i < len; i++) {
					MeiTuInfo meiTuInfo = newsInfos.get(i);
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("id", meiTuInfo.getmId());
					String url = meiTuInfo.getmImg();
					// String imgName =
					// StringTools.getNameFromUrlWithoutPostfix(url);

					jsonObject.put("img", url);
					String simg = meiTuInfo.getmSimg();
					jsonObject.put("simg", simg);
					jsonObject.put("releaseTime", meiTuInfo.getmReleaseTime());
					jsonObject.put("index", meiTuInfo.getmIndex());

					jsonArray.put(jsonObject);

					j = i + 1;
					if (j % 7 == 1) {
						maxIndex = meiTuInfo.getmIndex(); // 每页的最大index
					}

					if (j % 7 == 0) {
						String path = CHANNELINFO_PATH + File.separator
								+ meiTuShowId + File.separator + maxIndex
								+ ".txt";
						Tools.showLog("每一页资讯路径 path" + path);
						mkdirFile(path); // 创建文件
						String content = new JSONStringer().object()
								.key("data").value(jsonArray).endObject()
								.toString();
						Tools.showLog(content);
						saveJsonStrToFile(content, path); // 保存内容到文件中
						jsonArray = null;
						jsonArray = new JSONArray();
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存频道对应的图片到sd卡中 后缀.png
	 * 
	 * @author mingsong.zhang
	 * @throws IOException
	 * @date 2012-08-28
	 */
	public static void saveChannelImgsToSDcard(String typeCode, InputStream in)
			throws IOException {
		if (StringTools.isEmpty(SDCARDPATH)) {
			mkdirFileAndPath();
		}
		String end = ".png";
		String path = CHANNEL_IMGS_PATH + File.separator + typeCode + end;
		saveImgInputsream(in, path);
	}

	// /**
	// * 保存加油站应用详情图片到本地，图片名字为url后缀
	// *
	// * @param url
	// */
	// public static void saveAppStoreDetailImg(String imgUrl, InputStream in) {
	// if (StringTools.isEmpty(SDCARDPATH)) {
	// mkdirFileAndPath();
	// }
	// String name = StringTools.getNameFromUrlWithoutPostfix(imgUrl);
	// String path = APPS_PATH + File.separator + name + ".+";
	// Tools.showLog("fei.zhang", "图片保存的路径：" + path);
	// try {
	// MkdirFileTools.saveImgInputsream(in, path);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	/**
	 * 根据屏幕的大小从本地获取加油站应用详情的图片应该压缩的尺寸
	 * 
	 * @author fei.zhang
	 * @date 2012-11-7
	 * @param name
	 * @return
	 */
	public static int[] getAppStoreDetailThumbImg(int dWidth, int dHeight) {
		if (sdCardExist()) {
			if (StringTools.isEmpty(SDCARDPATH)) {
				mkdirFileAndPath();
				return null;
			}
			if (dHeight == 800 && dWidth == 480) {
				return new int[] { 170, 283 };
			} else if (dHeight == 480 && dWidth == 320) {
				return new int[] { 113, 170 };
			} else if (dHeight == 960 && dWidth == 540) {
				return new int[] { 204, 318 };
			} else {
				return new int[] { 170, 283 };
			}
		} else
			return null;
	}

	// /**
	// * 从本地获取加油站应用详情的图片
	// *
	// * @author fei.zhang
	// * @date 2012-10-26
	// * @param name
	// * @return
	// */
	// public static Bitmap getAppStoreDetailImg(String imgUrl) {
	// Bitmap bitmap = null;
	// if (sdCardExist()) {
	// if (StringTools.isEmpty(SDCARDPATH)) {
	// mkdirFileAndPath();
	// return null;
	// }
	// Tools.showLogB("imgUrl:" + imgUrl);
	// String name = StringTools.getNameFromUrlWithoutPostfix(imgUrl);
	// String path = APPS_PATH + File.separator + name + ".+";
	//
	// bitmap = BitmapTools.getBitmap(path);
	// }
	// return bitmap;
	// }

	/**
	 * 根据频道ID获取该频道对应的图片 该方法已经对SD卡是否存在做了判断
	 * 
	 * @author mingsong.zhang
	 * @date 2012-08-28
	 * @param typeCode
	 * @return 如果SD卡不存在 或者找不到图片 返回null
	 */
	public static Bitmap getChanneImg(String typeCode, int type) {
		Bitmap bitmap = null;
		if (sdCardExist()) {
			if (StringTools.isEmpty(SDCARDPATH)) {
				mkdirFileAndPath();
			}
			String end = ".png";
			String path = CHANNEL_IMGS_PATH + File.separator + typeCode + end;
			bitmap = BitmapTools.getBitmap(path);
		}
		return bitmap;
	}

	/**
	 * 保存详细资讯到SD卡中
	 * 
	 * @param id
	 *            资讯ID
	 * @param date
	 *            资讯发布时间
	 * @param content
	 *            资讯内容
	 */
	public static void saveNewsDetailToSDcard(int id,
			String content) {
		if (StringTools.isEmpty(SDCARDPATH)) {
			mkdirFileAndPath();
		}
		String path = FULL_CONTENT_PATH + File.separator + id + ".txt";
		Tools.showLogB("保存详细path：" + path);
		try {
			mkdirFile(path);
			saveJsonStrToFile(content, path);
			Tools.showLogB("----------------保存详细完毕------------------");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 从SD卡获取详细资讯
	 * 
	 * @param typeCode
	 *            资讯ID
	 * @param date
	 *            资讯发布时间
	 * @return
	 */
	public static String getNewsDetailFromSDcard(int typeCode) {
		String content = "";
		if (StringTools.isEmpty(SDCARDPATH)) {
			mkdirFileAndPath();
		}

		// String newDateStr = date.substring(0, date.indexOf("-") + 3);
		String path = FULL_CONTENT_PATH + File.separator + typeCode + ".txt";
		Tools.showLog("从SD卡获取详细资讯的路径" + path);
		content = getJsonString(path);

		return content;
	}

	// /**
	// * 保存资讯列表重大新闻或专题图片或详细界面图片到sd卡中 后缀+id+.jpg
	// *
	// * @author hongchang.liu
	// * @throws IOException
	// * @date 2012-08-28
	// */
	// public static void saveNewsDetailImgsToSDcard(String url, int infoId,
	// InputStream in) throws IOException {
	//
	// if (StringTools.isEmpty(SDCARDPATH)) {
	// mkdirFileAndPath();
	// }
	//
	// String name = FileNameFactory.generator(url);
	// String path = IMAGS_PATH + File.separator + name + ".0";
	// saveImgInputsream(in, path);
	//
	// }

	// /**
	// * 获取频道列表重大新闻或专题图片
	// *
	// * @return
	// */
	// public static Drawable getSpecialNewsImg(String url) {
	// Drawable drawable = null;
	// if (sdCardExist()) {
	// if (StringTools.isEmpty(SDCARDPATH)) {
	// mkdirFileAndPath();
	// }
	// String path = DATA_INFO_IMG_PATH + File.separator
	// + StringTools.getNameFromUrlWithoutPostfix(url) + ".+";
	// drawable = Drawable.createFromPath(path);
	// }
	// return drawable;
	// }

	/**
	 * 从本地获取频道列表重大新闻或专题图片 widget用
	 * 
	 * @author mingsong.zhang
	 * @date 2012-10-17
	 * @return
	 */
	// public static Bitmap getSpecialNewsImgBitmap(String url, int infoId) {
	// Bitmap bitmap = null;
	// if (sdCardExist()) {
	// if (StringTools.isEmpty(SDCARDPATH)) {
	// mkdirFileAndPath();
	// }
	// try {
	// String path = DATA_INFO_IMG_PATH + File.separator
	// + StringTools.getNameFromUrlWithoutPostfix(url) + "_"
	//
	// + infoId + ".+";
	// bitmap = BitmapTools.getBitmap(path);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// return bitmap;
	// }

	// /**
	// * 从本地获取频道列表重大新闻或专题图片 返回获取的输入流 widget用
	// *
	// * @author mingsong.zhang
	// * @date 2012-10-29
	// * @return
	// */
	// public static InputStream getSpecialNewsImgInputStream(String url,
	// int infoId) {
	// InputStream in = null;
	// if (sdCardExist()) {
	// if (StringTools.isEmpty(SDCARDPATH)) {
	// mkdirFileAndPath();
	// }
	// try {
	// String path = IMAGS_PATH + File.separator
	// + FileNameFactory.generator(url) + ".0";
	// File file = new File(path);
	// in = new FileInputStream(file);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// return in;
	// }

	// /**
	// * 获取服务区图片 返回输入流
	// *
	// * @author mingsong.zhang
	// * @date 2012-10-29
	// * @param imgPath
	// * @param id
	// * @return
	// */
	// public static InputStream getAdsImgFromSDcardInputStream(String imgPath,
	// int id) {
	// InputStream in = null;
	// if (MkdirFileTools.sdCardExist()) {
	// String name = StringTools.getNameFromUrlWithoutPostfix(imgPath);
	// if (StringTools.isNotEmpty(name)) {
	// String path = MkdirFileTools.ADSIMG_PATH + File.separator
	// + name + "_" + id + ".+";
	// File file = new File(path);
	// try {
	// in = new FileInputStream(file);
	// } catch (FileNotFoundException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// return in;
	// }

	/**
	 * 获取详细资讯的图片。
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	// public static Bitmap getNewsDetailImg(String url, int infoId)
	// throws FileNotFoundException {
	//
	// Bitmap bitmap = null;
	// if (sdCardExist()) {
	// if (StringTools.isEmpty(SDCARDPATH)) {
	// mkdirFileAndPath();
	// }
	// String path = DATA_INFO_IMG_PATH + File.separator
	// + StringTools.getNameFromUrlWithAZ09(url) + "_"
	// + infoId + ".+";
	//
	// File file = new File(path);
	// if (file.exists()) {
	// FileInputStream inputStream = new FileInputStream(file);
	// bitmap = BitmapFactory.decodeStream(inputStream);
	// }
	// }
	// return bitmap;
	// }

	// /**
	// * 从本地路径获取详细资讯的图片。
	// *
	// * @return
	// * @throws FileNotFoundException
	// */
	// public static Bitmap getNewsDetailImgFromCard(String path, int infoId)
	// throws FileNotFoundException {
	//
	// Bitmap bitmap = null;
	// if (sdCardExist()) {
	// if (StringTools.isEmpty(SDCARDPATH)) {
	// mkdirFileAndPath();
	//
	// }
	//
	// File file = new File(path);
	// if (file.exists()) {
	// FileInputStream inputStream = new FileInputStream(file);
	// bitmap = BitmapFactory.decodeStream(inputStream);
	// }
	//
	// }
	// return bitmap;
	// }

	// /**
	// * 获取资讯主界面的图片。
	// *
	// * @return
	// * @throws FileNotFoundException
	// */
	// public static Bitmap getNewsMainImg(String url, int infoId)
	// throws FileNotFoundException {
	//
	// Bitmap bitmap = null;
	// if (sdCardExist()) {
	// if (StringTools.isEmpty(SDCARDPATH)) {
	// mkdirFileAndPath();
	// }
	// String path = DATA_INFO_IMG_PATH + File.separator
	// + StringTools.getNameFromUrlWithoutPostfix(url) + "_"
	// + infoId + ".+";
	// File file = new File(path);
	// if (file.exists()) {
	// FileInputStream inputStream = new FileInputStream(file);
	// bitmap = BitmapFactory.decodeStream(inputStream);
	// }
	//
	// }
	// return bitmap;
	// }

	/**
	 * 保存广告图片
	 * 
	 * @param in
	 * @throws IOException
	 */
	public static void saveADSImgToSDcard(InputStream in) throws IOException {

		if (StringTools.isEmpty(SDCARDPATH)) {
			mkdirFileAndPath();
		}

		String path = DATA_INFO_IMG_PATH + File.separator + COVER;
		saveImgInputsream(in, path);

	}

	/**
	 * 获取广告图片
	 * 
	 * @return
	 */
	public static Bitmap getADSImg() {
		Bitmap bitmap = null;
		if (sdCardExist()) {
			if (StringTools.isEmpty(SDCARDPATH)) {
				mkdirFileAndPath();
			}
			String path = DATA_INFO_IMG_PATH + File.separator + COVER;
			try {
				bitmap = BitmapFactory.decodeFile(path);
			} catch (OutOfMemoryError e) {
				return bitmap;
			}
		}
		return bitmap;
	}

	/**
	 * 获取updatetime
	 * 
	 * @param key
	 * @return
	 */
	public static String getUpdateTime(String key) {
		if (sdCardExist()) {
			if (StringTools.isEmpty(SDCARDPATH)) {
				mkdirFileAndPath();
			}
			return getValue(key, UPDATETIME_TXT_PATH);
		}
		return "";
	}

	/**
	 * 保存updatetime
	 * 
	 * @param key
	 * @param updateTime
	 */
	public static void saveUpdateTime(String key, String updateTime) {
		if (sdCardExist()) {
			if (StringTools.isEmpty(SDCARDPATH)) {
				mkdirFileAndPath();
			}
			try {
				putValue(key, updateTime, UPDATETIME_TXT_PATH);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// /**
	// * 获取widget
	// * @param key
	// * @return
	// */
	// public static int getWidget(String key){
	// if(sdCardExist()){
	// return getIntValue(key, WIDGET_TXT_PATH);
	// }
	// return 0;
	// }
	//
	// /**
	// * 保存widget
	// * @param key
	// * @param widget
	// */
	// public static void saveWidget(String key , int widget){
	// if(sdCardExist()){
	// try {
	// putIntValue(key, widget, WIDGET_TXT_PATH);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	/**
	 * 根据key获取value 返回的是int类型
	 * 
	 * @date 2012-10-10
	 * @param key
	 *            要查询的key
	 * @param path
	 *            路径
	 * @return 返回查询到的值，没有该值则放回0
	 * @throws IOException
	 */
	public static synchronized int getIntValue(String key, String path) {
		if (StringTools.isEmpty(SDCARDPATH)) {
			mkdirFileAndPath();
		}
		int value = 0; // 需要返回的值
		try {
			String jsonStr = getJsonString(path); // 获取.json中的内容
			if (StringTools.isNotEmpty(jsonStr)) {
				JSONObject dataJson = new JSONObject(jsonStr);
				if (dataJson.has(key)) {
					value = dataJson.getInt(key);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 保存int类型的value
	 * 
	 * @param key
	 *            key名
	 * @param value
	 *            key对应的值
	 * @param path
	 *            路径
	 * @throws IOException
	 */
	public static synchronized void putIntValue(String key, int value,
			String path) throws IOException {
		if (StringTools.isEmpty(SDCARDPATH)) {
			mkdirFileAndPath();
		}
		BufferedWriter bw = null;
		JSONObject dataJson = new JSONObject();
		try {
			String jsonStr = getJsonString(path);

			/** 假如.json文件中已有内容则重新实例化JSONObject **/
			if (StringTools.isNotEmpty(jsonStr)) {
				dataJson = new JSONObject(jsonStr);
			}
			/** 将新的键值对加进来 **/
			dataJson.put(key, value);

			bw = new BufferedWriter(new FileWriter(path));
			String result = dataJson.toString();
			bw.write(result);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != bw) {
				bw.flush();
				bw.close();
			}
		}

	}

	/**
	 * 保存资讯主界面图片图片到SD卡
	 * 
	 * @author hongchang.liu
	 * @param bitmap
	 * @throws IOException
	 */

	public static void saveNewsMainImgToSDcard(InputStream in, String urlPath,
			int id) throws IOException {
		if (StringTools.isEmpty(SDCARDPATH)) {
			mkdirFileAndPath();
		}
		String name = StringTools.generator(urlPath);

		Tools.showLog("cbd", "保存资讯主界面的图片名为:" + name);
		String path = MkdirFileTools.IMAGS_PATH + File.separator + name + ".0";// 图片名字，不包括后缀
		saveImgInputsream(in, path);

	}

	/**
	 * 从SD卡获取资讯主界面图片
	 * 
	 * @author hongchang.liu
	 * @param imgPath
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Bitmap getNewsMainImgFromSDcard(String imgPath)
			throws FileNotFoundException {
		Bitmap bitmap = null;
		if (MkdirFileTools.sdCardExist() && StringTools.isNotEmpty(imgPath)) {
			if (StringTools.isEmpty(SDCARDPATH)) {
				mkdirFileAndPath();
			}
			String name = StringTools.generator(imgPath);

			// 路径不会空,用的时候应经做判断
			String path = MkdirFileTools.IMAGS_PATH + File.separator + name
					+ ".0";
			File file = new File(path);
			if (file.exists()) {
				FileInputStream inputStream = new FileInputStream(file);
				bitmap = BitmapFactory.decodeStream(inputStream);
			}

		}
		return bitmap;
	}

	/**
	 * 保存手机服务区界面图片到sd卡中 后缀+id+.jpg
	 * 
	 * @author hongchang.liu
	 * @throws IOException
	 * @date 2012-08-28
	 */
	public static void saveAdsImgsToSDcard(String url, int infoId,
			InputStream in) throws IOException {
		if (StringTools.isEmpty(SDCARDPATH)) {
			mkdirFileAndPath();
		}
		String name = StringTools.getNameFromUrlWithoutPostfix(url);
		String path = ADSIMG_PATH + File.separator + name + "_" + infoId + ".+";
		saveImgInputsream(in, path);

	}

	// /**
	// * 保存字节数据到path路径
	// *
	// * @param bs
	// * @param path
	// */
	// public static void saveByteImg(byte[] bs, int id, String url) {
	// Tools.showLogB("保存图片大小：" + bs.length);
	// String name = StringTools.getNameFromUrlWithoutPostfix(url);
	// String path = MkdirFileTools.DATA_INFO_IMG_PATH + File.separator + name
	// + "_" + id + ".+";// 图片名字，不包括后缀
	// if (sdCardExist()) {
	// if (StringTools.isEmpty(SDCARDPATH)) {
	// mkdirFileAndPath();
	// }
	// OutputStream out = null;
	// try {
	// File file = new File(path);
	// if (!file.exists()) {
	// file.getParentFile().mkdirs();
	// file.createNewFile();
	// }
	// out = new FileOutputStream(file);
	// out.write(bs, 0, bs.length);
	//
	// } catch (IOException e) {
	//
	// } finally {
	// try {
	// out.flush();
	// out.close();
	// } catch (IOException e) {
	// }
	// }
	//
	// }
	// }

	/**
	 * 按照二进制流保存图片原始流到本地对应路径，此方法为公共方法，所有图片都按照此方法保存
	 * 
	 * @author blueming.wu
	 * @param in
	 *            图片二进制流
	 * @param path
	 *            保存路径
	 * @throws IOException
	 */
	public static void saveImgInputsream(InputStream in, String path)
			throws IOException {
		// 如果图片流为null，或者图片流大小大于200KB，则不作保存
		if (in == null || in.available() > 204800)
			return;

		if (sdCardExist()) {
			if (StringTools.isEmpty(SDCARDPATH)) {
				mkdirFileAndPath();
			}
			OutputStream outputStream = null;

			File file = new File(path);
			if (!file.exists()) {
				// 如果不存在，也有可能图片文件目录也不存在
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			outputStream = new FileOutputStream(file);
			byte[] buffer = new byte[2048];
			int len = -1;

			while ((len = in.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}

			outputStream.flush();
			outputStream.close();
			in.close();

		}
	}

	/**
	 * 保存widget新闻资讯上次更新到的maxindex
	 * 
	 * @param value
	 *            资讯maxindex
	 * @param channelId
	 *            频道id
	 * @throws IOException
	 */
	public static void putWidgetNewsIndex(int value, int channelId)
			throws IOException {
		if (StringTools.isEmpty(SDCARDPATH)) {
			mkdirFileAndPath();
		}
		BufferedWriter bw = null;
		JSONObject dataJson = new JSONObject();
		try {
			String path = WIDGET_NEWS_PATH + File.separator + channelId
					+ File.separator + WIDGET_NEWS_INDEX_TXT;

			mkdirFile(path);
			dataJson = new JSONObject();

			/** 将新的键值对加进来 **/
			dataJson.put(MAXINDEX, value);

			bw = new BufferedWriter(new FileWriter(path));
			String result = dataJson.toString();

			bw.write(result);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != bw) {
				bw.flush();
				bw.close();
			}
		}

	}

	/**
	 * 获取该频道widget已保存的上次更新到的maxindex
	 * 
	 * @param channelId
	 *            资讯id
	 * @return
	 */
	public static int getWidgetNewsIndex(int channelId) {
		if (StringTools.isEmpty(SDCARDPATH)) {
			mkdirFileAndPath();
		}
		int value = 0; // 需要返回的值
		String path = WIDGET_NEWS_PATH + File.separator + channelId
				+ File.separator + WIDGET_NEWS_INDEX_TXT;
		try {
			String jsonStr = getJsonString(path); // 获取json格式文件中的内容
			if (StringTools.isNotEmpty(jsonStr)) {
				JSONObject dataJson = new JSONObject(jsonStr);
				value = dataJson.getInt(MAXINDEX);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 保存widget新闻资讯到sd卡
	 * 
	 * @param newsInfos
	 *            存放资讯频道的集合
	 * @param channelId
	 *            频道id
	 */
	public synchronized static void saveWidgetNewsInfoToSDcard(
			ArrayList<NewsInfo> newsInfoss, int channelId, Context context) {

		if (sdCardExist()) {

			try {
				if (StringTools.isEmpty(SDCARDPATH)) {
					mkdirFileAndPath();
				}
				JSONArray jsonArray = new JSONArray();
				ArrayList<NewsInfo> newsInfos = new ArrayList<NewsInfo>(6);
				newsInfos.addAll(newsInfoss);

				int len = newsInfos.size();

				for (int i = 0; i < len; i++) {
					NewsInfo newsInfo = newsInfos.get(i);
					JSONObject jsonObject = new JSONObject();

					int typeId = newsInfo.getId();
					jsonObject.put("id", typeId);

					String title = newsInfo.getTitle();
					jsonObject.put("title", title);

					jsonObject.put("intro", newsInfo.getIntro());

					String source = newsInfo.getSource();
					jsonObject.put("source", source);

					jsonObject.put("simg", newsInfo.getSimg());

					String img = newsInfo.getImg();
					jsonObject.put("img", img);

					String releaseTime = newsInfo.getReleaseTime();
					jsonObject.put("releaseTime", releaseTime);

					int special = newsInfo.getSpecial();
					jsonObject.put("special", special);

					jsonObject.put("major", newsInfo.getMajor());

					jsonObject.put("index", newsInfo.getIndex());

					jsonObject.put("originalUrl", newsInfo.getOriginalUrl());

					jsonObject.put("typeCode", "" + newsInfo.getTypeCode());

					jsonArray.put(jsonObject);

				}
				String path = WIDGET_NEWS_PATH + File.separator + channelId
						+ File.separator + WIDGET_NEWS_TXT;
				mkdirFile(path); // 创建文件

				String content = new JSONStringer().object().key("data")
						.value(jsonArray).endObject().toString();
				if (StringTools.isNotEmpty(content)) {
					saveJsonStrToFile(content, path); // 保存内容到文件中
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 保存短信发送次数到sd卡
	 * 
	 * @author fei.zhang
	 * @data 2012-11-01
	 * @param sendTimes
	 */
	public static void saveSendMsgTimesToSdCard(String sendTimes) {
		if (StringTools.isEmpty(SDCARDPATH)) {
			mkdirFileAndPath();
		}
		BufferedWriter bw = null;
		try {
			mkdirFile(SENDMSG_TIMES_PATH);
			bw = new BufferedWriter(new FileWriter(SENDMSG_TIMES_PATH));
			bw.write(sendTimes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != bw) {
				try {
					bw.flush();
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 从sd卡中获取短信发送次数
	 * 
	 * @author fei.zhang
	 * @data 2012-11-01
	 */
	public static String readSendMsgTimesFromSdCard() {
		if (StringTools.isEmpty(SDCARDPATH)) {
			mkdirFileAndPath();
		}
		String str = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(SENDMSG_TIMES_PATH));
			str = br.readLine();
			if (str == null) {
				str = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != br)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	/**
	 * 清除所有数据
	 * 
	 * @param path
	 */
	public static synchronized void delAllData(String path) {
		if (sdCardExist() && StringTools.isNotEmpty(path)) {
			if (StringTools.isEmpty(SDCARDPATH)) {
				mkdirFileAndPath();
			}

			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(path));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (null != bw) {
					try {
						bw.flush();
						bw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 保存手机加油站的应用列表信息到sd卡
	 * 
	 * @param category
	 */
	public static void saveAppStore(String json, String categoryId, String ipage) {
		if (StringTools.isEmpty(json) || StringTools.isEmpty(categoryId)) {
			return;
		}
		if (sdCardExist()) {

			if (StringTools.isEmpty(SDCARDPATH)) {
				mkdirFileAndPath();
			}

			String path = MkdirFileTools.APPS_PATH + File.separator + "app"
					+ categoryId + "_" + ipage + ".txt";
			try {
				mkdirFile(path);
				saveJsonStrToFile(json, path);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 记录请求服务器接口
	 * 
	 * @param url
	 */
	public static void saveRequestConsume(final String url) {

		if (RECORD) {

			if (StringTools.isEmpty(SDCARDPATH)) {
				mkdirFileAndPath();
			}

			if (StringTools.isNotEmpty(url)) {

				new Thread(new Runnable() {

					@Override
					public void run() {
						outPutRequestData(url);

					}
				}).start();

			}
		}

	}

	private static void outPutRequestData(String url) {
		FileOutputStream fileOutputStream = null;
		File file = new File(REQUEST_PATH);
		synchronized (file) {
			try {
				fileOutputStream = new FileOutputStream(file, true);
				fileOutputStream.write(("\r\n" + url).getBytes());
				fileOutputStream.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 检查路径是否初始化完全 如果有误会重新初始化
	 */
	public static void checkLocalPath() {

		if (StringTools.isEmpty(SDCARDPATH) && sdCardExist()) {
			mkdirFileAndPath();
		}
	}

}
