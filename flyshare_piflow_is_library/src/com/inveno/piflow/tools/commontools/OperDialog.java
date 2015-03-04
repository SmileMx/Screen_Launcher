package com.inveno.piflow.tools.commontools;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.inveno.piflow.R;
import com.inveno.piflow.biz.upload.PvBasicStateDataBiz;
import com.inveno.piflow.download.downloadmanager.download.DownloadService;
import com.inveno.piflow.entity.view.FsDialog;

/**
 * 退出对话框创建及退出机制
 * 
 * @author blueming.wu
 * @date 2012-8-18
 */
public class OperDialog {

	public static OperDialog operDialog = null;

	private OperDialog() {
	}

	/** 创建对话框的类型为退出 */
	public static final int OPERTYPE_EXIT = 0;

	public static OperDialog getOperDialog() {
		if (operDialog == null)
			operDialog = new OperDialog();
		return operDialog;
	}

	public ProgressDialog createProgressDialog(Context mContext, String title,
			String msginfo) {
		ProgressDialog progressDialog = new ProgressDialog(mContext);
		progressDialog.setTitle(title);
		progressDialog.setMessage(msginfo);
		return progressDialog;
	}

	/** 提示下载语音引擎对话框 **/
	public Dialog createDownSpeakEngineDialog(final Context context) {
		Dialog dialog = null;
		FsDialog.Builder customBuilder = new FsDialog.Builder(context);
		customBuilder
				.setTitle("语音引擎下载")
				.setMessage(
						context.getResources().getString(
								R.string.down_loading_egine_content))
				.setPositiveButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.setNegativeButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 启动服务下载引擎,并发送一个通知显示下载进度
						Intent downloadIntent = new Intent(context,
								DownloadService.class);
						downloadIntent.putExtra(
								DownloadService.DOWNLOAD_TASK_KEY,
								DownloadService.TASK_DOWNLOAD_TTS);
						context.startService(downloadIntent);
						dialog.dismiss();
					}
				});
		dialog = customBuilder.create();
		return dialog;

	}

	/**
	 * 创建下载更新的提示对话框
	 * 
	 * @author hongchang.liu
	 * @date 2012-10-30
	 */
	public Dialog createCallDailog(final Context context, final String phoneNum) {
		Dialog dialog = null;
		FsDialog.Builder customBuilder = new FsDialog.Builder(context);
		customBuilder.setTitle("提示").setMessage("是否致电?")
				.setPositiveButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.setNegativeButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Tools.showLog("abc", "call num:" + phoneNum);
						Intent intent = new Intent(Intent.ACTION_CALL, Uri
								.parse("tel:" + phoneNum));
						PvBasicStateDataBiz.BASEFLAG = false;
						context.startActivity(intent);
						dialog.dismiss();
					}
				});
		dialog = customBuilder.create();
		return dialog;
	}

}
