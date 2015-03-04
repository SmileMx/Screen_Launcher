package com.inveno.piflow.tools.commontools;

import com.inveno.piflow.R;

/**
 * 常量类，记录一些常用的公共常量，可通过实现此接口来直接使用常量 此类里面的常量均为final类型
 * 
 * @author blueming.wu
 * @date 2012-07-16
 */
public interface Const {

	/** 服务器繁忙 **/
	public final static int SERVER_BUSY = 10;
	/** 网络不给力 **/
	public final static int NET_ERROR = 11;
	/** 网络异常，请检查网络 **/
	public final static int NET_DOWN = 12;
	/** 网络异常，请检查网络 **/
	public final static int NORMAL = 13;

	/** 记录请求服务器日志的开关（日志保存路径FlyShare/datatxt/request.txt） */
	public final static boolean RECORD = false;
	
	/**微信平台的appId*  wx32bbf01b18d9f5b8*/
	public final static String WEIXIN_FLYSHARE_APPID="wx32bbf01b18d9f5b8";
	/** 广播前加的包名PackageName **/
	public final static String PACKAGE_NAME = "com_inveno_flyshare_";

	/** 版本号 **/
	public static final String VERSION = "1.0.1";

	/** 手机缓存数据版本号 (20130114文件数据升级，增加原文链接地址，版本升级为2) */
	/** 手机缓存数据版本号 (20130328文件数据升级，增加我的收藏，版本升级为4) */
	/** 主界面重写（20130427主界面模块更改） */
	/** 数据采集PV和启动次数统计增加新字段（20130530） */
	/** 系统新消息增加原文链接（20130617） */
	public static final int DATA_VERSION = 7;

	/** 飞享基本配置数据库名 */
	public static final String FLYSHARE_DB = "flyshare.db";

	/** 数据采集库名 */
	public static final String COLLECTION_DB = "collection.db";

	/** 手机SD卡文件数据版本号 */
	public static final int FOLDER_VERSION = 3;

	/** 厂商电话 **/
	public static final String PHONENUM = "10000";

	/** 默认短信猫号码 */
	public static final String DEFAULT_SMSMOBILE = "13163706308";

	/** 语音TTS包下载地址 */
	public static final String TTS_DOWNLOAD_URL = "http://img.lem88.com/mta/upload/fly/app/20130606/633454996256043.apk";

	/** 拉取资讯和消息服务器地址 */
	public static final String BASE_URL = "http://flyshare.lem88.com/flyshare/client/";
//	 public static final String BASE_URL =
//	 "http://192.168.1.2:8088/flyshare/client/";

	/** 服务器主机地址 */

	public static final String HOST = "http://flyshare.lem88.com";
//	 public static final String HOST = "http://192.168.1.2";

	/** 数据挖掘服务器地址 */

	public static final String MTR_URL = "http://analysis.lem88.com/mtr/client/";
//	 public static final String MTR_URL = "http://192.168.1.2/mtr/client/";

	/** 广告服务器地址 */
//	 public static final String ADPF_URL =
//	 "http://192.168.1.2:8088/pms/client/";
	public static final String ADPF_URL = "http://flyshare.lem88.com/pms/client/";

	/** 默认显示频道 */
	public static final String[] DEFAULT_CHANNEL = { "今日要闻", "环球视野", "新鲜事儿",
			"星天地", "影视界", "如厕ING", "生活百科", "体坛精选", "科技新闻", "军情观察室" };

	/** 默认频道图片url */
	public static final String[] DEFAULT_CHANNEL_ICON = {
			"http://img.lem88.com/flyshare/upload/fly/channelIcon/20121205/613488053273151.png",
			"http://img.lem88.com/flyshare/upload/fly/channelIcon/20121205/613638663705151.png",
			"http://img.lem88.com/flyshare/upload/fly/channelIcon/20121205/613476369883151.png",
			"http://img.lem88.com/flyshare/upload/fly/channelIcon/20130205/5964995941253151.png",
			"http://img.lem88.com/flyshare/upload/fly/channelIcon/20121205/613569491794151.png",
			"http://img.lem88.com/flyshare/upload/fly/channelIcon/20130107/3440962802775151.png",
			"http://img.lem88.com/flyshare/upload/fly/channelIcon/20121205/613507556426151.png",
			"http://img.lem88.com/flyshare/upload/fly/channelIcon/20130107/3440820947476151.png",
			"http://img.lem88.com/flyshare/upload/fly/channelIcon/20121205/613652261726151.png",
			"http://img.lem88.com/flyshare/upload/fly/channelIcon/20121212/1221209627360151.png" };

	/** 皮肤颜色数组 */
	public static int skinColor[] = { R.color.skin_01, R.color.skin_02,
			R.color.skin_03, R.color.skin_04, R.color.skin_05, R.color.skin_06,
			R.color.skin_07, R.color.skin_08, R.color.skin_09, R.color.skin_10,
			R.color.skin_11, R.color.skin_12, R.color.skin_13, R.color.skin_14,
			R.color.skin_15, R.color.skin_16, R.color.skin_17, R.color.skin_18,
			R.color.skin_19, R.color.skin_20 };

	/** 固定模块的随机颜色数组 */
	public static int fixedColor[] = { R.color.skin_01, R.color.skin_02,
			R.color.skin_03, R.color.skin_05, R.color.skin_06, R.color.skin_07,
			R.color.skin_08, R.color.skin_09, R.color.skin_11, R.color.skin_14,
			R.color.skin_16, R.color.skin_17, R.color.skin_18, R.color.skin_20 };

	/** 资讯背景颜色数组 */
	public static int newsColor[] = { R.color.n_blue, R.color.n_green,
			R.color.n_orange, R.color.n_purple, R.color.n_skygreen };

	/** 资讯字体及背景颜色数组，黑、白、灰 */
	public static int modeColor[] = { R.color.deep_black, R.color.deep_white,
			R.color.littletext };

	/** 下载文字和图片数据响应时间 */
	public static final int CONNECT_TIMEOUT_TXT = 10000;
	public static final int CONNECT_TIMEOUT_IMG = 30000;
	public static final int READ_TIMEOUT_TXT = 10000;
	public static final int READ_TIMEOUT_IMG = 30000;

	/** 默认心跳执行间隔时间数组(单位：毫秒) */
	public static final long[] DEFALT_HEART_TIME = { 120000, 600000, 900000 };

	/** 指示设置 **/
	public static final String SETTINGS = "settings";
	/** 全部设置 **/
	public static final String ALLSETTINGS = "com.inveno.flyshare_preferences";
	/** 全部设置保存的KEY **/
	// 设置是否在主界面显示
	public static final String FLYSHARE_ALLSET = "flyshare_allset";
	// 仅WIFI下离线下载
	public static final String FLYSHARE_DOWNLOAD = "flyshare_download";
	// Webview字体设置
	public static final String FLYSHARE_FONTSIZE = "flyshare_fontsize";
	// 历史记录是否在主界面显示
	public static final String FLYSHARE_SHOWHISTORY = "flyshare_showhistory";
	// 亮度设置
	public static final String FLYSHARE_LIGHTSET = "flyshare_lightset";
	// 皮肤设置
	public static final String FLYSHARE_SKINSET = "flyshare_skinset";

	// 清理缓存
	public static final String FLYSHARE_CLEANCACHE = "flyshare_cleancache";
	// 版本更新
	public static final String FLYSHARE_VERSIONUPDATE = "flyshare_versionupdate";
	// 日夜模式
	public static final String FLYSHARE_DAY_NIGHT_MODE = "flyshare_day_night_mode";

	//
	public static String cID = null;

	// ------- 接口解析用字符串start ---------

	/** 无网络是返回的常量 */
	public static final String HASNOT_NETWORK = "hasNotNetwork";

	/** 成功获取接口返回字符并成功解析 **/
	public static final String RETURN_OK = "returnOk";

	/** 解析接口数据有异常 **/
	public static final String RETURN_ERROR = "returnError";

	/** 传递的jsonStr为空字符串 **/
	public static final String RETURN_JSONSTR_ISEMPTY = "returnJsonStrIsEmpty";

	/** 接口返回代码 returnCode **/
	public static final String RETURN_CODE = "returnCode";

	/** smsnumber 短信猫号码 **/
	public static final String SMSNUMBER = "smsnumber";

	/** clientId 客户端Id **/
	public static final String CLIENTID = "clientId";

	/** 手机号码所在城市 **/
	public static final String CITY = "city";

	/** 手机号码 **/
	public static final String MOBILE_NUMBER = "mobile";

	/** 心跳时间间隔 （分钟） **/
	public static final String HEART_TIME = "heartTime";

	/** 上传数据间隔时间（分钟）（点击数据上传） **/
	public static final String UPDATA_TIME = "updatatime";

	/** 服务器当天时间 **/
	public static final String SDATE = "sdate";

	/** 接口限制的action **/
	public static final String ACTION = "action";

	/** 资讯页条数 **/
	public static final String INFO_PAGE_SIZE = "infoPageSize";

	/** 心跳获取推送任务接口的任务完成时间戳 **/
	public static final String HEART_BEAT_UPDATE_TIME = "updateTime";

	/** 心跳获取推送任务接口返回的type **/
	public static final String TYPECODE = "type";

	/** 推送资讯的频道location **/
	public static final String LOCATION = "location";

	/** 重大新闻标题 */
	public static final String PUSH_INFO_TITLE = "title";

	/** 重大新闻简介 */
	public static final String PUSH_INFO_INTRO = "intro";

	/** 重大新闻来源 */
	public static final String PUSH_INFO_SOURCE = "source";

	/** 重大新闻缩略图地址 */
	public static final String PUSH_INFO_IMG = "img";

	/** 重大新闻发布时间 */
	public static final String PUSH_INFO_RELEASE_TIME = "releaseTime";

	/** 重大新闻详细内容 */
	public static final String PUSH_INFO_CONTENT = "content";

	/** 获取初始页图片返回的img地址 **/
	public static final String IMG = "img";

	/** 资讯列表的更新时间 */
	public static final String CHANNEL_UPDATETIME = "channelUpdateTime";

	/** 数据开始data **/
	public static final String DATA = "data";

	/** id **/
	public static final String ID = "id";

	/** maxindex **/
	public static final String MAXINDEX = "maxindex";

	/** 产品列表的更新时间 **/
	public static final String PRODUCTS_UPDATETIME = "productUpdateTime";

	// --------- 接口解析用字符串end ---------
	/** 短信发送后的广播 */
	public static final String SENT_SMS_ACTION = "SENT_SMS_ACTION";
	/** 启动Activity的标志 */
	public static final String STARTFLAG = "startFlag";

	/**
	 * 保存在缓存里和SD卡的update数据的key
	 */
	public static final class Settings {

		/** 获取新闻接口的updateTime **/
		public static final String GET_PUSH_INFO_UPDATETIME = "getPushInfoUpdateTime";
		/** 获取推送应用接口的updateTime **/
		public static final String GET_PUSH_APP_UPDATETIME = "getPushAppUpdateTime";
		/** 获取应用激活接口的updateTime **/
		public static final String GET_APP_OPEN_UPDATETIME = "getAppOpenUpdateTime";
		/** 获取厂商新信息接口的updateTime **/
		public static final String GET_FIRM_INFO_UPDATETIME = "getFirmInfoUpdateTime";
		/** 获取厂商回复消息接口的updateTime **/
		public static final String GET_FIRM_REPLAY_INFO_UPDATETIME = "getFirmReplayInfoUpdateTime";
		/** 获取厂商群发消息接口的updateTime **/
		public static final String GET_FIRM_PUSH_INFO_UPDATETIME = "getFirmPushInfoUpdateTime";
		/** 请求获取新资讯数目 **/
		public static final String GET_NEWS_NUM_INFO_UPDATETIME = "getNewsNumInfoUpdateTime";
		/** 请求广告的updateTime */
		public static final String GET_POSTERINFO_UPDATETIME = "getPosterInfoUpdateTime";

	}

	/** 当前主界面在哪个状态 */
	// public static final String MAIN_AT_CHANNELSET = PACKAGE_NAME
	// // + "main_at_channelset";
	// public static final String MAIN_AT_MAINWP = PACKAGE_NAME +
	// "main_at_mainwp";

	/** 主界面删除往频道设置发出的广播 */
	public static final String ACTION_DEL_MAINVIEW_TOCHANNEL = PACKAGE_NAME
			+ "action_del_rssmainview_tochannel";

	/** 收到推送消息需要在应用内提醒的广播 */
	public static final String ACTION_PUSHMSG_SHOW_ATMAIN = PACKAGE_NAME
			+ "action_pushmsg_show_atmain";

	/** 账户资料修改失败的广播 */
	public static final String ACTION_UPDATE_USERINFO_FAILED = PACKAGE_NAME
			+ "aciont_update_userinfo_failed";

	/**
	 * widget的3个key
	 * 
	 * @author mingsong.zhang
	 * @date 2012-10-10
	 */
	public static final class Widget {
		/** 时钟widget **/
		public static final String WIDGET_CLOCK_KEY = "widget_clock_key";
		/** 新闻widget设置显示哪个频道的key **/
		public static final String WIDGET_NEWS_CHANNEL_SET_KEY = "widget_news_channel_set_key";
		/** widget设置后刷新接收的广播action **/
		public static final String WIDGET_NEWS_SET_REFRESH = PACKAGE_NAME
				+ "widget_news_set_refresh";
		/** 保存在缓存中的自动刷新key **/
		public static final String AUTO_REFRESH_KEY = "widget_setting_set_news_auto_refresh_key";
		/** 保存在缓存中的自动刷新值：勾选 **/
		public static final String AUTO_REFRESH_CHECKED = "widget_setting_set_news_auto_refresh_checked";
		/** 保存在缓存中的自动刷新值：取消勾选 **/
		public static final String AUTO_REFRESH_UNCHECKED = "widget_setting_set_news_auto_refresh_unchecked";
		/** widget刷新 拿的本地新闻资讯 **/
		public static final String WIDGET_SETTING_REFRESH_NO_NET = PACKAGE_NAME
				+ "widget_setting_refresh_no_net";
		/** 更新中... **/
		public static final String WIDGET_NEWS_LOADING = "更新中...";
		/** 刷新激活widget **/
		public static final String REFRESH_WIDGET_LIVE = PACKAGE_NAME
				+ "refresh_widget_live";
		/** 打开widget设置页面发送广播关闭后台页面 **/
		public static final String WIDGET_SETTING_START_SET_ACT_FINISH_BACK_ACT = PACKAGE_NAME
				+ "WIDGET_SETTING_START_SET_ACT_FINISH_BACK_ACT";
	}

	// -------销统start---------

	/** key：imei **/
	public final static String IMEI = "E";

	/** 如果获取不到 则默认的IMSI **/
	public final static String DEFAULT_IMSI = "999999999999999";

	// -------销统end-----------

	/** 详细页面的css样式 */

	/** 查看原文div */
	public final static String ORIGNAL = "<div id='look' onclick='window.demo.clickGo()'>开启原文</div>";

	/********************************** 资讯图片加载*模式 *********************************/
	/** 全图模式，默认 */
	public final static String ALWAYS_LOAD_MODE = "always_load_mode";
	/** 无图模式 */
	public final static String NOT_LOAD_MODE = "not_load_mode";
	/** 智能模式，仅wifi下载 */
	public final static String SMART_LOAD_MODE = "smart_load_mode";

	/** 瀑布流 资讯item背景颜色组 **/
	public static final int[] WATERWALL_ITEM_BG_COLORS = {
			R.color.waterwall_item_bg_color_tcl_01,
			R.color.waterwall_item_bg_color_tcl_02,
			R.color.waterwall_item_bg_color_tcl_03};

	/**
	 * 连接网络结果可以用的一些参数
	 * 
	 * @author mingsong.zhang
	 * @date 2013-06-20
	 */
	public static final class ConnectInternetLoadResult {
		/** 发送给handler的下载OK what=1 **/
		public static final int HANDLER_WHAT_LOAD_OK = 1;
		/** 发送给handler的下载连接网络失败 what=2 **/
		public static final int HANDLER_WHAT_LINK_ERROR = 2;
		/** 发送给handler的下载服务器繁忙 what=3 **/
		public static final int HANDLER_WHAT_LINK_ERROR_2 = 3;
		/** 发送给handler的下载没有新数据了 **/
		public static final int HANDLER_WHAT_NO_MORE_DATA = 4;
		/** 开始刷新 **/
		public static final int HANDLER_WHAT_START_LOADING = 5;
		/** 没有连接网络 **/
		public static final int HANDLER_WHAT_NO_NET = 6;

	}
}
