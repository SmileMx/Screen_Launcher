package com.inveno.piflow.download.downloadmanager.download;

import android.os.Handler;
import android.os.Message;

/**
 * 下载各个阶段的回调及通知UI线程改变
 * 
 * @author blueming.wu
 * 
 * @date 2013-6-4
 */
public class DownLoadCallback extends Handler {

	/** 各种下载阶段所发送的message */
	protected static final int START_MESSAGE = 0;
	protected static final int ADD_MESSAGE = 1;
	protected static final int PROGRESS_MESSAGE = 2;
	protected static final int SUCCESS_MESSAGE = 3;
	protected static final int FAILURE_MESSAGE = 4;
	protected static final int FINISH_MESSAGE = 5;
	protected static final int STOP_MESSAGE = 6;

	/**
	 * 开始下载
	 */
	public void onStart() {
	}

	/**
	 * 增加一个下载任务
	 * 
	 * @param url
	 *            任务资源地址
	 * @param isInterrupt
	 */
	public void onAdd(String url, Boolean isInterrupt) {
	}

	/**
	 * 报告下载进度
	 * 
	 * @param url
	 * @param speed
	 * @param progress
	 */
	public void onLoading(String url, String speed, long download, int progress) {

	}

	/**
	 * 下载成功
	 * 
	 * @param url
	 */
	public void onSuccess(String url) {
	}

	/**
	 * 下载失败
	 * 
	 * @param url
	 * @param strMsg
	 */
	public void onFailure(String url, String strMsg) {

	}

	/**
	 * 下载结束
	 * 
	 * @param url
	 */
	public void onFinish(String url) {
	}

	/**
	 * 下载中止
	 */
	public void onStop() {
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		Object[] response;

		switch (msg.what) {
		case START_MESSAGE:
			onStart();
			break;
		case ADD_MESSAGE:
			response = (Object[]) msg.obj;
			onAdd((String) response[0], (Boolean) response[1]);
			break;
		case PROGRESS_MESSAGE:
			response = (Object[]) msg.obj;
			onLoading((String) response[0], (String) response[1],
					(Long) response[2], (Integer) response[3]);
			break;
		case SUCCESS_MESSAGE:
			response = (Object[]) msg.obj;
			onSuccess((String) response[0]);
			break;
		case FAILURE_MESSAGE:
			response = (Object[]) msg.obj;
			onFailure((String) response[0], (String) response[1]);
			break;
		case FINISH_MESSAGE:
			response = (Object[]) msg.obj;
			onFinish((String) response[0]);
			break;
		case STOP_MESSAGE:
			onStop();
			break;

		}
	}

	protected void sendSuccessMessage(String url) {
		sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[] { url }));
	}

	protected void sendLoadMessage(String url, String speed, long download,
			int progress) {
		sendMessage(obtainMessage(PROGRESS_MESSAGE, new Object[] { url, speed,
				download, progress }));
	}

	protected void sendAddMessage(String url, Boolean isInterrupt) {
		sendMessage(obtainMessage(ADD_MESSAGE,
				new Object[] { url, isInterrupt }));
	}

	protected void sendFailureMessage(String url, String strMsg) {
		sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[] { url, strMsg }));
	}

	protected void sendStartMessage() {
		sendMessage(obtainMessage(START_MESSAGE, null));
	}

	protected void sendStopMessage() {
		sendMessage(obtainMessage(STOP_MESSAGE, null));
	}

	protected void sendFinishMessage(String url) {
		sendMessage(obtainMessage(FINISH_MESSAGE, new Object[] { url }));
	}
}
