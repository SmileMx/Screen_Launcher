package com.inveno.piflow.biz;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;

import com.inveno.piflow.R;
import com.inveno.piflow.download.HttpDownload;
import com.inveno.piflow.download.ParseDownloadJson;
import com.inveno.piflow.entity.model.MeiTuInfo;
import com.inveno.piflow.entity.view.LoadingDialog;
import com.inveno.piflow.tools.android.ToastTools;
import com.inveno.piflow.tools.commontools.Const;
import com.inveno.piflow.tools.commontools.MkdirFileTools;
import com.inveno.piflow.tools.commontools.StringTools;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 美图秀秀的数据处理类
 * 
 * @author mingsong.zhang
 * @date 2012-08-27
 */
public class MeituShowBiz {

	/** 上下文 **/
	private Context mContext;

	/** 频道id **/
	private int mTypeCode;

	/** 美图每页取图片数 **/
	private final static int MEITU_EVERY_PAGE_SIZE = 7;

	/** 打开美图界面 **/
	private final static int HANDLER_STARTACTIVITY = 0;

	/** 其他 **/
	private final static int HANDLER_OTHER = 1;

	/** 没新数据 **/
	private final static int HANDLER_NO_NEW = 2;

	public final static String NEXT = "next";

	public final static String PRE = "pre";

	/** 弹窗 **/
	private LoadingDialog mDialog;

	/** 从接口拿数据 **/
	private static AsyncTask<Void, Void, Integer> asyncTask;

	private static AsyncTask<Void, Void, Integer> asyncPreTask;

	private static ArrayList<MeiTuInfo> mMeiTuInfoList;

	private static MeituShowBiz mMeituShowBiz;

	private String channelName;

	private boolean notLoading;

	private MeituShowBiz(Context context) {
		this.mContext = context;
		notLoading = true;
		if (null == mMeiTuInfoList) {
			mMeiTuInfoList = new ArrayList<MeiTuInfo>();
		}

	}

	/**
	 * 单例
	 * 
	 * @return
	 */
	public static MeituShowBiz getInstance(Context context) {
		if (mMeituShowBiz == null) {
			mMeituShowBiz = new MeituShowBiz(context);
		}
		return mMeituShowBiz;
	}

	/**
	 * 获得的美图实体类集合 如果没有新的 则要从SD卡拿
	 * 
	 * @return 如果没有 则返回null
	 */
	private Integer getNewMeiTuInfoList() {

		// 先尝试联网拿最新的
		//
		int index = MkdirFileTools.getMaxIndex(mTypeCode);
		int returnCode = getMeiTuInfosList(mTypeCode, 0, NEXT);

		if (returnCode != HANDLER_STARTACTIVITY) {
			if (index != 0) {

				int page = getLocalMeiTu(index);
				Tools.showLog("meitu", "从本地获取美图的页数：" + page);
				if (page == 0) {
					// 服务器错误或网络不给力
					return HANDLER_OTHER;
				} else {
					return HANDLER_STARTACTIVITY;
				}

			}
		}

		return returnCode;
	}

	/**
	 * 从接口获得的美图实体类集合
	 * 
	 * @param typeCode
	 * @param index
	 * @param flag
	 * @return
	 */
	private Integer getMeiTuInfosList(int typeCode, int index, String flag) {
		ArrayList<MeiTuInfo> meiTuInfoList = null;

		String url = ParseDownloadJson.getMeiTuInfoUrl(typeCode,
				MEITU_EVERY_PAGE_SIZE, index, flag, mContext);

		try {

			String jsonStr = HttpDownload.requestJsonStr(mContext, url, null,
					null, HttpDownload.METHOD_GET);
			if (jsonStr.equals(Const.HASNOT_NETWORK)) {
				return HANDLER_OTHER;
			}

			meiTuInfoList = MeiTuInfo.parse(jsonStr);

			if (meiTuInfoList == null) {
				return HANDLER_OTHER;

			} else if (meiTuInfoList.size() == 0) {
				// 返回除102外的返回码
				return HANDLER_NO_NEW;
				
			} else if (meiTuInfoList.size() % 7 == 0) {

				MkdirFileTools.saveMeiTuInfosToSDcard(meiTuInfoList, mTypeCode);
				if (NEXT.equals(flag)) {
					MkdirFileTools.putMaxIndex(
							Integer.parseInt(meiTuInfoList.get(0).getmIndex()),
							mTypeCode);
				}
				mMeiTuInfoList.addAll(meiTuInfoList);
				return HANDLER_STARTACTIVITY;
			} else {
				//解析报错
				return HANDLER_NO_NEW;
			
			}

		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		return HANDLER_OTHER;
	}

	/**
	 * 加载下一页调用的总方法 直接调用此方法就可以了
	 */
	private Integer loadMeiTuInfosList() {

		int size = mMeiTuInfoList.size();
		if (size > 0) {
			int index = Integer.parseInt(mMeiTuInfoList.get(size - 1)
					.getmIndex());
			Tools.showLog("meitu", "获取集合中的最小index:" + index);

			int page = getLocalMeiTu(index - 1);
			if (page == 0) {
				// 拿网络
				Tools.showLog("meitu", "本地没有,从网络获取以前数据时index:" + index);
				int returnCode = getMeiTuInfosList(mTypeCode, index, PRE);
				return returnCode;

			} else {
				return HANDLER_STARTACTIVITY;
			}

		}

		return HANDLER_OTHER;
	}

	// /**
	// * 拿本地一页
	// *
	// * @param index
	// * @return
	// */
	// private ArrayList<MeiTuInfo> getLocalMeiTuInfo(int index) {
	//
	// String path = MkdirFileTools.CHANNELINFO_PATH + File.separator
	// + mTypeCode + File.separator + index + ".txt";
	//
	// String jsonStr = MkdirFileTools.getJsonString(path);
	//
	// ArrayList<MeiTuInfo> meiTuInfoList =MeiTuInfo.parse(jsonStr);
	//
	// return meiTuInfoList;
	// }

	private Integer getLocalMeiTu(int index) {

		int count = 0;

		for (int i = 0; i < 6; i++) {

			String path = MkdirFileTools.CHANNELINFO_PATH + File.separator
					+ mTypeCode + File.separator + index + ".txt";

			String jsonStr = MkdirFileTools.getJsonString(path);

			if (StringTools.isNotEmpty(jsonStr)) {
				ArrayList<MeiTuInfo> meiTuInfoList = MeiTuInfo.parse(jsonStr);
				if (meiTuInfoList.size() == 7) {
					mMeiTuInfoList.addAll(meiTuInfoList);
					count++;
					index -= 7;
				} else {
					break;
				}
			} else {
				break;
			}

		}

		return count;

	}

	/**
	 * 对点击事件作出响应，处理数据，加载图片跳转等操作
	 * 
	 * @param typeCode
	 *            频道ID
	 */
	public void initData(Context context, int typeCode, LoadingDialog dialog,
			final Handler handler, final String typeName) {
		if (dialog != null)
			this.mDialog = dialog;
		else
			this.mDialog = new LoadingDialog(context, 0);
		
		this.mTypeCode = typeCode;
		Tools.showLog("meitu", "初始化来自MeituBiz typeId:"+mTypeCode);
		this.channelName = typeName;
		mDialog.showDialog();
		downLoadData(context, handler);
	}

	/**
	 * 跳转到界面
	 */
	public void goToActivity() {
//		if (mDialog.isShowing()) {
//			Intent intent = new Intent(mContext, MeiTuShowActivity.class);
//			intent.putExtra("channelName", channelName);
//			intent.putExtra("typeCode", mTypeCode);
//			mContext.startActivity(intent);
//		}

	}

	/**
	 * 下载数据，根据结果做响应
	 */
	private void downLoadData(final Context context, final Handler handler) {

		if (null != asyncTask) {
			asyncTask.cancel(true);
			asyncTask = null;
		}
		if (mMeiTuInfoList != null) {
			mMeiTuInfoList.clear();
		}

		asyncTask = new AsyncTask<Void, Void, Integer>() {

			@Override
			protected void onPreExecute() {
				// showDialog();

				super.onPreExecute();
			}

			@Override
			protected Integer doInBackground(Void... params) {

				// int back = getNewMeiTuInfoList();
				//
				// if (back == 0) {
				// return HANDLER_OTHER;
				// } else {
				// return HANDLER_STARTACTIVITY;
				// }

				return getNewMeiTuInfoList();
				// ArrayList<MeiTuInfo> meiTuInfos = getNewMeiTuInfoList();
				// if (meiTuInfos == null) {
				// return HANDLER_OTHER;
				// } else if (meiTuInfos.size() == 0) {
				// return HANDLER_NO_NEW;
				// } else {
				// mMeiTuInfoList.clear();
				// mMeiTuInfoList.addAll(meiTuInfos);
				// // mApp.setmMeituInfoList(meiTuInfos);
				// return HANDLER_STARTACTIVITY;
				// }
			}

			@Override
			protected void onPostExecute(Integer result) {
				switch (result) {
				case HANDLER_STARTACTIVITY:

//					goToActivity();
//					Tools.showLog("meitu", "跳转页面时集合大小:" + mMeiTuInfoList.size());
//					if (handler != null) {
//						handler.sendEmptyMessage(MeiTuShowActivity.CLEAN_MSG_NUM);
//					}
//					// 打开美图界面
//					mDialog.clearDialog();
					break;

				case HANDLER_NO_NEW:
					mDialog.clearDialog();

					ToastTools.showToast(mContext, "没新数据,请稍候再试!");

					// goToActivity();
					break;

				case HANDLER_OTHER:
					mDialog.clearDialog();

					ToastTools.showToast(mContext, R.string.net_error);
				}
				super.onPostExecute(result);
			}

		};
		asyncTask.execute();

	}

	public void downLoadPre(final Handler handler) {

		Tools.showLog("meitu", "准备加载新数据时notLoading:" + notLoading);
		if (notLoading) {
			notLoading = false;
			if (asyncPreTask != null) {
				asyncPreTask.cancel(true);
				asyncPreTask = null;
			}
			Tools.showLog("meitu", "实例化asyncPreTask ");
			asyncPreTask = new AsyncTask<Void, Void, Integer>() {

				@Override
				protected Integer doInBackground(Void... params) {
					// ArrayList<MeiTuInfo> meiTuInfos = loadMeiTuInfosList();
					// if (meiTuInfos != null) {
					// return 1;
					// }
					return loadMeiTuInfosList();
				}

				@Override
				protected void onPostExecute(Integer result) {

					Tools.showLog("meitu", "result:" + result);
					switch (result) {
					case HANDLER_STARTACTIVITY:
						handler.sendEmptyMessage(HANDLER_STARTACTIVITY);
						notLoading = true;
						break;

					case HANDLER_NO_NEW:
						notLoading = true;
						handler.sendEmptyMessage(HANDLER_NO_NEW);
						break;

					case HANDLER_OTHER:
						handler.sendEmptyMessage(HANDLER_OTHER);
						notLoading = true;
						break;
					}
					//
					//
					// if (result == 1) {
					// Tools.showLog("meitu", "发消息改变页数");
					// handler.sendEmptyMessage(1);
					// }
					super.onPostExecute(result);
				}

			}.execute();
		}

	}

	public int getmTypeCode() {
		return mTypeCode;
	}

	public void setmTypeCode(int mTypeCode) {
		this.mTypeCode = mTypeCode;
	}

	public static ArrayList<MeiTuInfo> getmMeiTuInfoList() {
		return mMeiTuInfoList;
	}

	public static void setmMeiTuInfoList(ArrayList<MeiTuInfo> mMeiTuInfoList) {
		MeituShowBiz.mMeiTuInfoList = mMeiTuInfoList;
	}

	public void setNotLoading(boolean notLoading) {
		this.notLoading = notLoading;
	}

	/**
	 * 清楚业务逻辑里的数据
	 */
	public void quit() {

		if (asyncPreTask != null) {
			asyncPreTask.cancel(true);
			asyncPreTask = null;
		}
		if (asyncTask != null) {
			asyncTask.cancel(true);
			asyncTask = null;
		}
		if (mMeiTuInfoList != null) {
			mMeiTuInfoList.clear();
		}

		if (mDialog != null) {
			mDialog.clearDialog();
		}
	}

}
