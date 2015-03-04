package com.inveno.piflow.biz;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

import com.inveno.piflow.R;
import com.inveno.piflow.tools.commontools.MkdirFileTools;
import com.inveno.piflow.tools.commontools.StringTools;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 处理详细资讯的数据
 * 
 * @author mingsong.zhang
 * @date 2012-09-28
 */
public class NewsDetailBiz {

	/**
	 * js中改变图片地址的方法名, 起名字一定要长, 有时候下载下来的详细资讯内容带有js, 为了避免跟该js中的方法名重复,
	 * 我们自己的js方法名字一定要长
	 **/
	public final static String JS_FUNCTION_NAME = "comflysharenewsdetailwebviewjschangeimagesrc";

	public final static String JS_MODE_NAME = "comflysharenewsdetailwebviewjschangemode";

	public final static String BGCOLOR_WHITE = "#ffffff";

	public final static String BGCOLOR_BLACK = "#151515";

	public final static String TXTCOLOR_WHITE = "#4a4a4a";

	public final static String TXTCOLOR_BLACK = "#000000";

	/** 默认字体大小 */
	private int fontSize = 18;

	private int fontSpacing = 1;

	private int lightHigh = 27;

	/** 默认webView背景颜色 **/
	private String bgColor = BGCOLOR_BLACK;

	/** 默认字体颜色 **/
	private String textColor = TXTCOLOR_WHITE;

	/** 查看原文的白天css样式 */
	private String cssString;
	/** 查看原文的黑色css样式 */
	private String cssStringBlack;

	/** 加在下载下来的详细资讯内容前面的js **/
	public final static String JS = "<script type=\"text/javascript\">function "
			+ JS_FUNCTION_NAME
			+ "(i,str){document.images[i].src = str;return false;}function "
			+ JS_MODE_NAME
			+ "(str,strt){document.bgColor=str;document.body.style.color =strt;return false;}"
			+ "" + "</script>";

	/** 加载下来的TXT文档加样式 */
	private String style = "";

	private String functionStyle = "<style>body{letter-spacing:1px;line-height:27px;font-size:18px;"
			+ "}</style>";

	/** 图片网络地址集合 **/
	public ArrayList<String> imgUrls;
	/** 改变为默认本地图片地址之前原内容中的图片地址集合 **/
	public ArrayList<String> mOldContentImg;

	private Context mContext;

	public NewsDetailBiz(Context context) {
		imgUrls = new ArrayList<String>();
		mOldContentImg = new ArrayList<String>();
		mContext = context;

		cssString = mContext.getString(R.string.ori_css_day);
		cssStringBlack = mContext.getString(R.string.ori_css_night);
	}

	/**
	 * 获取详细资讯中的图片网络地址集合
	 * 
	 * @param content
	 * @return
	 */
	public ArrayList<String> getImgUrls(String content) {
		imgUrls.clear();
		if (StringTools.isNotEmpty(content)) {
			String regex2 = "src=\".+?\"";
			Pattern p = Pattern.compile(regex2);
			Matcher m = p.matcher(content);

			while (m.find()) {
				// 截取的src="..."字段
				String imgSrc = m.group();
				// 图片的原网络地址
				String imgUrl = imgSrc.substring(imgSrc.indexOf("\"") + 1,
						imgSrc.lastIndexOf("\""));
				imgUrls.add(imgUrl);
			}
		}

		Tools.showLog("lhc", "list imgUrl:" + imgUrls.size());
		return imgUrls;

	}

	/**
	 * 处理手机服务活动的内容 将图片地址改为本地adsimg图片地址
	 * 
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public String disposeAdsContent(String content, String id) throws Exception {
		imgUrls.clear();
		String newContent = content;
		String regex2 = "src=\".+?\"";
		Pattern p = Pattern.compile(regex2);
		Matcher m = p.matcher(content);

		while (m.find()) {
			// 截取的src="..."字段
			String imgSrc = m.group();
			// 图片的原网络地址
			String imgUrl = imgSrc.substring(imgSrc.indexOf("\"") + 1,
					imgSrc.lastIndexOf("\""));
			Tools.showLog("abc", "从Json中获取到得imgUrl：" + imgUrl);

			String path = MkdirFileTools.ADSIMG_PATH + File.separator
					+ StringTools.getNameFromUrlWithoutPostfix(imgUrl) + "_"
					+ id + ".+";
			File file = new File(path);
			if (!file.exists()) {
				imgUrls.add(imgUrl);
			}

			newContent = newContent.replace(imgSrc, "src=\"file://" + path
					+ "\"");

		}

		return new StringBuffer(functionStyle).append(newContent).toString();
	}

	/**
	 * 将详细资讯内容的图片地址改为本地默认图片地址
	 * 
	 * @param content
	 * @return
	 */
	public String disposeContentImgDefault(String content, int id,
			ArrayList<String> urls) {
		// 每次进来把之前的清理掉
		mOldContentImg.clear();
		String newContent = content;
		String regex2 = "src=\".+?\"";
		Pattern p = Pattern.compile(regex2);
		Matcher m = p.matcher(content);

		while (m.find()) {
			// 截取的src="..."字段
			String imgSrc = m.group();
			// 图片的原网络地址
			String imgUrl = imgSrc.substring(imgSrc.indexOf("\"") + 1,
					imgSrc.lastIndexOf("\""));
			// 放置全部图片url的集合
			urls.add(imgUrl);

			// 获取本地图片地址
			String path = MkdirFileTools.IMAGS_PATH + File.separator
					+ StringTools.generator(imgUrl) + ".0";

			File file = new File(path);

			Tools.showLog("meitu", "图片本地 path：" + path);
			if (file.exists()) {// 找到本地图片则直接改变src
				newContent = newContent.replace(imgSrc, "src=\"file://" + path
						+ "\"");
			} else {// 如果本地找不到该图片，就加载集合里，并把content src指向默认图片
				mOldContentImg.add(imgUrl);
				newContent = newContent.replace(imgSrc,
						"src=\"file:///android_asset/webviewdefaulticon.png\"");
			}

			// 加上自定义样式
			Tools.showLog("cbd", "disposeContentImgDefault style:" + style);
		}

		return new StringBuffer(style).append(newContent).toString();
	}

	/**
	 * 将详细资讯内容的图片地址改为本地默认图片地址
	 * 
	 * @param content
	 * @return
	 */
	public String getSdCardContent(String content, int id) {
		// 每次进来把之前的清理掉
		mOldContentImg.clear();
		String newContent = content;
		String regex2 = "src=\".+?\"";
		Pattern p = Pattern.compile(regex2);
		Matcher m = p.matcher(content);

		while (m.find()) {
			// 截取的src="..."字段
			String imgSrc = m.group();
			// 图片的原网络地址
			String imgUrl = imgSrc.substring(imgSrc.indexOf("\"") + 1,
					imgSrc.lastIndexOf("\""));

			// 获取本地图片地址
			String path = MkdirFileTools.IMAGS_PATH + File.separator
					+ StringTools.generator(imgUrl) + ".0";

			File file = new File(path);
			Tools.showLog("meitu", "图片本地 path：" + path);
			if (file.exists()) {// 找到本地图片则直接改变src
				newContent = newContent.replace(imgSrc, "src=\"file://" + path
						+ "\"");
			} else {// 如果本地找不到该图片，就加载集合里，并把content src指向默认图片
				mOldContentImg.add(imgUrl);
				newContent = newContent.replace(imgSrc,
						"src=\"file:///android_asset/webviewdefaulticon.png\"");
			}

		}

		Tools.showLog("cbd", "getSdCardContent style:" + style);

		return new StringBuffer(style).append(newContent).toString();
	}

	/**
	 * 改变字体大小
	 * 
	 * @param size
	 */
	public void changeFontSize(int size, int mode) {

		fontSpacing = size / 36;
		lightHigh = size + 9;
		fontSize = size;
		if (mode == 1) {
			style = "<style>body{padding:0 5px;letter-spacing:" + fontSpacing
					+ "px;line-height:" + lightHigh + "px;font-size:" + size
					+ "px" + ";" + "background-color:" + bgColor + ";"
					+ "color:" + textColor + "}" + " img{max-width:100%;}"
					+ cssString + "</style>";
		} else {
			style = "<style>body{padding:0 5px;letter-spacing:" + fontSpacing
					+ "px;line-height:" + lightHigh + "px;font-size:" + size
					+ "px" + ";" + "background-color:" + bgColor + ";"
					+ "color:" + textColor + "}" + " img{max-width:100%;}"
					+ cssStringBlack + "</style>";
		}

	}

	/**
	 * 设置夜间模式
	 */
	public void dayMode() {
		Tools.showLog("cbd", "改为日间模式");
		bgColor = BGCOLOR_WHITE;
		textColor = TXTCOLOR_BLACK;
		style = "<style>body{padding:0 5px;letter-spacing:" + fontSpacing
				+ "px;line-height:" + lightHigh + "px;font-size:" + fontSize
				+ "px" + ";" + "background-color:" + bgColor + ";" + "color:"
				+ textColor + "}img{max-width:100%;}" + cssString + "</style>";

	}

	/**
	 * 设置日间模式
	 */
	public void nightMode() {
		Tools.showLog("cbd", "改为夜间模式");
		bgColor = BGCOLOR_BLACK;
		textColor = TXTCOLOR_WHITE;
		style = "<style>body{padding:0 5px;letter-spacing:" + fontSpacing
				+ "px;line-height:" + lightHigh + "px;font-size:" + fontSize
				+ "px" + ";" + "background-color:" + bgColor + ";" + "color:"
				+ textColor + "}img{max-width:100%;}" + cssStringBlack
				+ "</style>";

	}

	public void setImgUrls(ArrayList<String> imgUrls) {
		this.imgUrls = imgUrls;
	}

	public ArrayList<String> getImgUrls() {
		return imgUrls;
	}

	public ArrayList<String> getmmOldContentImg() {
		return mOldContentImg;
	}

	public void setmOldContentImg(ArrayList<String> oldContentImg) {
		this.mOldContentImg = oldContentImg;
	}

	/***
	 * 除了资讯主界面,其它界面跳转到资讯详细页面都使用该方法跳转
	 * 
	 * @param context
	 * @param fromWhere
	 * @param indexKey
	 * @param newsInfo
	 * @return
	 */
//	public static Intent startAct(Context context, int fromWhere, int indexKey,
//			NewsInfo newsInfo) {
//
//		Intent intent = new Intent(context,
//				com.inveno.piflow.activity.ext.NewsCommonActivity.class);
//
//		switch (fromWhere) {
//		case NewsCommonActivity.FROM_STASTUSBAR:
//			intent.putExtra(NewsCommonActivity.NEWSINFO_KEY, newsInfo);
//			intent.putExtra(NewsCommonActivity.GET_FROMWHERE_KEY,
//					NewsCommonActivity.FROM_STASTUSBAR);
//			break;
//		case NewsCommonActivity.FROM_SYSMSG:
//			intent.putExtra(NewsCommonActivity.NEWSINFO_KEY, newsInfo);
//			intent.putExtra(NewsCommonActivity.GET_FROMWHERE_KEY,
//					NewsCommonActivity.FROM_SYSMSG);
//			break;
//		case NewsCommonActivity.FROM_NEWSHISTORY:
//			intent.putExtra(NewsCommonActivity.GET_INDEX_KEY, indexKey);
//			intent.putExtra(NewsCommonActivity.GET_FROMWHERE_KEY,
//					NewsCommonActivity.FROM_NEWSHISTORY);
//			break;
//
//		case NewsCommonActivity.FROM_NEWSTOPIC:
//			intent.putExtra(NewsCommonActivity.GET_INDEX_KEY, indexKey);
//			intent.putExtra(NewsCommonActivity.GET_FROMWHERE_KEY,
//					NewsCommonActivity.FROM_NEWSTOPIC);
//			break;
//		case NewsCommonActivity.FROM_WIDGET_NEWS:
//			intent.putExtra(NewsCommonActivity.GET_INDEX_KEY, indexKey);
//			intent.putExtra(NewsCommonActivity.GET_FROMWHERE_KEY,
//					NewsCommonActivity.FROM_WIDGET_NEWS);
//			break;
//
//		case NewsCommonActivity.FROM_WIDGET_NEWS_HISTORY:
//			intent.putExtra(NewsCommonActivity.GET_INDEX_KEY, indexKey);
//			intent.putExtra(
//					NewsCommonActivity.GET_FROMWHERE_WIDGET_HISTORY_KEY,
//					NewsCommonActivity.FROM_WIDGET_NEWS_HISTORY);
//			intent.putExtra(NewsCommonActivity.GET_FROMWHERE_KEY,
//					NewsCommonActivity.FROM_NEWSHISTORY);
//			NewsHistoryActivity.mCanFinish = true;
//
//			break;
//
//		case NewsCommonVariable.FROM_NEWS_MYFAVORITE:
//			intent.putExtra(NewsCommonVariable.GET_INDEX_KEY, indexKey);
//			intent.putExtra(NewsCommonVariable.GET_FROMWHERE_KEY,
//					NewsCommonVariable.FROM_NEWS_MYFAVORITE);
//			break;
//
//		default:
//			break;
//		}
//
//		return intent;
//
//	}

	/***
	 * 资讯主界面跳转资讯详细页面使用该方法
	 * 
	 * @param context
	 * @param newsInfo
	 * @param fromWhere
	 * @param currentPage
	 * @param minIndex
	 * @param hasNet
	 * @return
	 */
//	public static Intent startAct(Context context, NewsInfo newsInfo,
//			int fromWhere, int currentPage, int minIndex, boolean hasNet) {
//		Intent intent = null;
//		if (fromWhere == NewsCommonActivity.FROM_NEWSMAIN) {
//
//			intent = new Intent(context,
//					com.inveno.piflow.activity.ext.NewsCommonActivity.class); // Chenxu
//																				// 2012/12/25
//
//			intent.putExtra("NewsInfo", newsInfo);
//			intent.putExtra("currentPage", currentPage);
//			intent.putExtra(NewsCommonActivity.GET_FROMWHERE_KEY,
//					NewsCommonActivity.FROM_NEWSMAIN);
//			intent.putExtra("minIndex", minIndex);
//			intent.putExtra("ifNetWork", hasNet);
//		}
//
//		return intent;
//
//	}
}
