package com.inveno.piflow.biz.waterwall;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.httpclient.HttpException;
import org.json.JSONException;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;

import com.inveno.piflow.R;
import com.inveno.piflow.download.IShowflowApi;
import com.inveno.piflow.download.ShowFlowImpl;
import com.inveno.piflow.download.downloadmanager.netstate.NetWorkUtil;
import com.inveno.piflow.entity.model.showflow.ShowFlowNews;
import com.inveno.piflow.entity.model.showflow.ShowFlowStyleConfigs;
import com.inveno.piflow.tools.commontools.Const.ConnectInternetLoadResult;
import com.inveno.piflow.tools.commontools.DeviceConfig;
import com.inveno.piflow.tools.commontools.MkdirFileTools;
import com.inveno.piflow.tools.commontools.StringTools;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 瀑布流业务类
 * 
 * @author mingsong.zhang
 * @date 2013-06-18
 * 
 */
public class WaterwallBiz {

	/** 已适配的分辨率 **/
	public static final String RESOLUTIONS = "";

	/**
	 * 宽320以下(包括320) 单位为px
	 * 
	 * @author mingsong.zhang
	 * 
	 */
	public static final class WaterwallSmallValues {

		public final static int STYLE_01_HEIGHT = 149;
		public final static int STYLE_02_HEIGHT = 234;
		public final static int STYLE_03_HEIGHT = 196;

		/** 第一个变大的字的大小 **/
		public final static int FIRST_TEXT_SIZE = 32;

		public final static int TEXT_SIZE01 = 16;
		public final static int TEXT_SIZE02 = 12;

		public final static int SYTLE_01_TEXT_HEIGHT = 135;

		public final static int SYTLE_02_TITLE_HEIGHT = 63;
		public final static int SYTLE_02_CONTENT_HEIGHT = 110;

		public final static int SYTLE_03_CONTENT_HEIGHT = 105;
		public final static int SYTLE_03_TITLE_HEIGHT = 30;

		/** 一个误差值 **/
		public final static int GAP = 22;

		public final static int IMG_EXTRA_GAP = 64;
		
		public final static int IMG_ABOVE_EXTRA_GAP = 18;
		
		public final static int IMG_TEXT_HEIGHT = 30;
		
		/**图片上标题行间距**/
		public final static int IMG_TITLE_LINE_SPACING = 6;
	}

	/**
	 * 宽480~640区间(包括480,640) 单位为px
	 * 
	 * @author mingsong.zhang
	 * 
	 */
	public static final class WaterwallMiddleValues {

		public final static int STYLE_01_HEIGHT = 220;
		public final static int STYLE_02_HEIGHT = 406;
		public final static int STYLE_03_HEIGHT = 396;

		/** 第一个变大的字的大小 **/
		public final static int FIRST_TEXT_SIZE = 60;

		public final static int TEXT_SIZE01 = 30;
		public final static int TEXT_SIZE02 = 20;

		public final static int SYTLE_01_TEXT_HEIGHT = 183;

		public final static int SYTLE_02_TITLE_HEIGHT = 113;
		public final static int SYTLE_02_CONTENT_HEIGHT = 190;

		public final static int SYTLE_03_CONTENT_HEIGHT = 233;
		public final static int SYTLE_03_TITLE_HEIGHT = 60;

		/** 一个误差值 **/
		public final static int GAP = 12;

		/**字在图片下方时的差值**/
		public final static int IMG_EXTRA_GAP = 108;
		
		/**字在图片上时的差值**/
		public final static int IMG_ABOVE_EXTRA_GAP = 12;
		
		public final static int IMG_TEXT_HEIGHT = 60;
		
		/**图片上标题行间距**/
		public final static int IMG_TITLE_LINE_SPACING = 6;
	}

	/**
	 * 宽720以上(包括720) 单位为px
	 * 
	 * @author mingsong.zhang
	 * 
	 */
	public static final class WaterwallBigValues {

		public final static int STYLE_01_HEIGHT = 376;
		public final static int STYLE_02_HEIGHT = 626;
		public final static int STYLE_03_HEIGHT = 576;

		/** 第一个变大的字的大小 **/
		public final static int FIRST_TEXT_SIZE = 60;

		public final static int TEXT_SIZE01 = 30;
		public final static int TEXT_SIZE02 = 24;

		public final static int SYTLE_01_TEXT_HEIGHT = 334;

		public final static int SYTLE_02_TITLE_HEIGHT = 190;
		public final static int SYTLE_02_CONTENT_HEIGHT = 320;

		public final static int SYTLE_03_CONTENT_HEIGHT = 350;
		public final static int SYTLE_03_TITLE_HEIGHT = 110;

		/** 一个误差值 **/
		public final static int GAP = 9;

		public final static int IMG_EXTRA_GAP = 145;
		
		public final static int IMG_ABOVE_EXTRA_GAP = 9;
		
		public final static int IMG_TEXT_HEIGHT = 90;
		
		/**图片上标题行间距**/
		public final static int IMG_TITLE_LINE_SPACING = 12;

	}

	private ShowFlowStyleConfigs showFlowStyleConfigs;

	private Context context;

	private static AsyncTask<Void, Void, Integer> loadNewsAsyncTask;

	private static AsyncTask<Void, Void, Integer> loadLocalNewsAsyncTask;

	private IShowflowApi sShowflowApi;

	private ShowFlowNews showFlowNews;

	private Handler handler;

	/** 字块高度集合 **/
	private ArrayList<Integer> styleHeights;

	private boolean isLoading;

	public static int index = 0;

	// private WaterwallBiz waterwallBiz;

	public WaterwallBiz(Context context, Handler handler) {
		this.context = context;
		this.sShowflowApi = new ShowFlowImpl();
		this.handler = handler;
		this.styleHeights = new ArrayList<Integer>();
		initShowFlowStyleConfigs(context);
	}

	// public static WaterwallBiz getInstance(Context context ,Handler handler){
	// // if(waterwallBiz == null){
	// waterwallBiz = new WaterwallBiz(context,handler);
	// // }
	//
	// return waterwallBiz;
	// }

	/**
	 * 加载网络数据
	 */
	public void loadNews() {

		if (isLoading) {
			return;
		}
		if (loadNewsAsyncTask != null) {
			loadNewsAsyncTask.cancel(true);
			loadNewsAsyncTask = null;
		}

		loadNewsAsyncTask = new AsyncTask<Void, Void, Integer>() {

			@Override
			protected Integer doInBackground(Void... params) {

				if (!NetWorkUtil.isNetworkAvailable(context)) {
					return ConnectInternetLoadResult.HANDLER_WHAT_NO_NET;
				}

				try {

					handler.sendEmptyMessage(ConnectInternetLoadResult.HANDLER_WHAT_START_LOADING);

					isLoading = true;
					showFlowNews = null;

					showFlowNews = sShowflowApi.getShowflowNews(context);
					if (showFlowNews != null && showFlowNews.size() > 0) {
						// infoId =
						// showFlowNews.get(showFlowNews.size()-1).getInfoId();
						return ConnectInternetLoadResult.HANDLER_WHAT_LOAD_OK;
					} else {
						return ConnectInternetLoadResult.HANDLER_WHAT_NO_MORE_DATA;
					}

				} catch (HttpException e) {
					Tools.showLog("WaterwallLayoutMain", "http异常");
					return ConnectInternetLoadResult.HANDLER_WHAT_LINK_ERROR;
				} catch (IOException e) {
					Tools.showLog("WaterwallLayoutMain", "io异常");
					return ConnectInternetLoadResult.HANDLER_WHAT_LINK_ERROR_2;
				} catch (JSONException e) {
					Tools.showLog("WaterwallLayoutMain", "json异常:" + e);
					return ConnectInternetLoadResult.HANDLER_WHAT_LINK_ERROR_2;
				}

			}

			@Override
			protected void onPostExecute(Integer result) {

				handler.sendEmptyMessage(result);
				// isLoading = false;
			};

		};

		loadNewsAsyncTask.execute();
	}

	/**
	 * 加载网络数据
	 */
	public void loadLocalNews() {

		// if (isLoading) {
		// return;
		// }
		if (loadLocalNewsAsyncTask != null) {
			loadLocalNewsAsyncTask.cancel(true);
			loadLocalNewsAsyncTask = null;
		}

		loadLocalNewsAsyncTask = new AsyncTask<Void, Void, Integer>() {

			@Override
			protected Integer doInBackground(Void... params) {

				showFlowNews = null;

				String jsonStr = MkdirFileTools
				 .getJsonString(MkdirFileTools.FLOW_DATA_PATH);

				if (StringTools.isNotEmpty(jsonStr)) {
					try {
						showFlowNews = ShowFlowNews.parse(context, jsonStr);

					} catch (JSONException e) {
						showFlowNews = null;
					}

					if (showFlowNews != null && showFlowNews.size() > 0) {
						handler.sendEmptyMessage(ConnectInternetLoadResult.HANDLER_WHAT_LOAD_OK);
					} else {

						loadNews();
					}
				} else {

					loadNews();
				}

				return 0;
			}

		};

		loadLocalNewsAsyncTask.execute();
	}

	public ShowFlowNews getShowFlowNews() {
		return showFlowNews;
	}

	public void saveLasrNews() throws IOException {
		sShowflowApi.saveLastShowNews();
	}

	/**
	 * 初始化字块的高度
	 * 
	 * @param context
	 */
	private void initShowFlowStyleConfigs(Context context) {

		int width = DeviceConfig.getInstance(context).w;

		if (RESOLUTIONS.indexOf(width + "") != -1) {

			Resources res = context.getResources();
			this.styleHeights.add(res
					.getInteger(R.integer.waterwall_text_item_01_height));
			this.styleHeights.add(res
					.getInteger(R.integer.waterwall_text_item_02_height));
			this.styleHeights.add(res
					.getInteger(R.integer.waterwall_text_item_03_height));

		} else {

			int textItem01 = 0;
			int textItem02 = 0;
			int textItem03 = 0;

			if (width <= 320) {
				textItem01 = WaterwallSmallValues.STYLE_01_HEIGHT;
				textItem02 = WaterwallSmallValues.STYLE_02_HEIGHT;
				textItem03 = WaterwallSmallValues.STYLE_03_HEIGHT;

				this.showFlowStyleConfigs = new ShowFlowStyleConfigs(
						WaterwallSmallValues.STYLE_01_HEIGHT,
						WaterwallSmallValues.STYLE_02_HEIGHT,
						WaterwallSmallValues.STYLE_03_HEIGHT,
						WaterwallSmallValues.FIRST_TEXT_SIZE,
						WaterwallSmallValues.TEXT_SIZE01,
						WaterwallSmallValues.TEXT_SIZE02,
						WaterwallSmallValues.SYTLE_01_TEXT_HEIGHT,
						WaterwallSmallValues.SYTLE_02_TITLE_HEIGHT,
						WaterwallSmallValues.SYTLE_02_CONTENT_HEIGHT,
						WaterwallSmallValues.SYTLE_03_CONTENT_HEIGHT,
						WaterwallSmallValues.SYTLE_03_TITLE_HEIGHT,
						WaterwallSmallValues.GAP,
						WaterwallSmallValues.IMG_EXTRA_GAP,
						WaterwallSmallValues.IMG_TEXT_HEIGHT,
						WaterwallSmallValues.IMG_ABOVE_EXTRA_GAP,
						WaterwallSmallValues.IMG_TITLE_LINE_SPACING);

			} else if (width > 320 && width <= 640) {
				textItem01 = WaterwallMiddleValues.STYLE_01_HEIGHT;
				textItem02 = WaterwallMiddleValues.STYLE_02_HEIGHT;
				textItem03 = WaterwallMiddleValues.STYLE_03_HEIGHT;

				this.showFlowStyleConfigs = new ShowFlowStyleConfigs(
						WaterwallMiddleValues.STYLE_01_HEIGHT,
						WaterwallMiddleValues.STYLE_02_HEIGHT,
						WaterwallMiddleValues.STYLE_03_HEIGHT,
						WaterwallMiddleValues.FIRST_TEXT_SIZE,
						WaterwallMiddleValues.TEXT_SIZE01,
						WaterwallMiddleValues.TEXT_SIZE02,
						WaterwallMiddleValues.SYTLE_01_TEXT_HEIGHT,
						WaterwallMiddleValues.SYTLE_02_TITLE_HEIGHT,
						WaterwallMiddleValues.SYTLE_02_CONTENT_HEIGHT,
						WaterwallMiddleValues.SYTLE_03_CONTENT_HEIGHT,
						WaterwallMiddleValues.SYTLE_03_TITLE_HEIGHT,
						WaterwallMiddleValues.GAP,
						WaterwallMiddleValues.IMG_EXTRA_GAP,
						WaterwallMiddleValues.IMG_TEXT_HEIGHT,
						WaterwallMiddleValues.IMG_ABOVE_EXTRA_GAP,
						WaterwallMiddleValues.IMG_TITLE_LINE_SPACING);
			} else {
				textItem01 = WaterwallBigValues.STYLE_01_HEIGHT;
				textItem02 = WaterwallBigValues.STYLE_02_HEIGHT;
				textItem03 = WaterwallBigValues.STYLE_03_HEIGHT;

				this.showFlowStyleConfigs = new ShowFlowStyleConfigs(
						WaterwallBigValues.STYLE_01_HEIGHT,
						WaterwallBigValues.STYLE_02_HEIGHT,
						WaterwallBigValues.STYLE_03_HEIGHT,
						WaterwallBigValues.FIRST_TEXT_SIZE,
						WaterwallBigValues.TEXT_SIZE01,
						WaterwallBigValues.TEXT_SIZE02,
						WaterwallBigValues.SYTLE_01_TEXT_HEIGHT,
						WaterwallBigValues.SYTLE_02_TITLE_HEIGHT,
						WaterwallBigValues.SYTLE_02_CONTENT_HEIGHT,
						WaterwallBigValues.SYTLE_03_CONTENT_HEIGHT,
						WaterwallBigValues.SYTLE_03_TITLE_HEIGHT,
						WaterwallBigValues.GAP,
						WaterwallBigValues.IMG_EXTRA_GAP,
						WaterwallBigValues.IMG_TEXT_HEIGHT,
						WaterwallBigValues.IMG_ABOVE_EXTRA_GAP,
						WaterwallBigValues.IMG_TITLE_LINE_SPACING);
			}

			this.styleHeights.add(textItem01);
			this.styleHeights.add(textItem02);
			this.styleHeights.add(textItem03);
		}
	}

	/**
	 * 获取字块高度集合
	 * 
	 * @return
	 */
	public ArrayList<Integer> getStyleHeights() {
		return styleHeights;
	}

	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	public boolean isLoading() {
		return isLoading;
	}

	public ShowFlowStyleConfigs getShowFlowStyleConfigs() {
		return showFlowStyleConfigs;
	}

}
