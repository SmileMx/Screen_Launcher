package com.inveno.piflow.tools.android;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.inveno.piflow.R;

public class ToastTools {

	/** toast显示时距离底部的距离，可定制 */
	private static int INSTANCE_FROM_BOTTOM = 130;

	private static Toast toast;

	public static void showToast(Context context, String word) {
		initToastView(context, word);
	}

	public static void showToast(Context context, int word) {
		initToast(context, word);
	}

	public static void showToast(Context context, String word, int fromBottom) {
		INSTANCE_FROM_BOTTOM = fromBottom;
		initToastView(context, word);
	}

	/**
	 * 初始化toast风格，具体可定制
	 */
	private static void initToastView(Context context, String word) {
		View view = LayoutInflater.from(context).inflate(R.layout.toast_layout,
				null);
		TextView toastTV = (TextView) view.findViewById(R.id.toast_tv);
		toastTV.setText(word);
		initToast(context, view);
	}

	/**
	 * 文字为res中的资源
	 * 
	 * @param context
	 * @param word
	 */
	private static void initToast(Context context, int word) {
		View view = LayoutInflater.from(context).inflate(R.layout.toast_layout,
				null);
		TextView toastTV = (TextView) view.findViewById(R.id.toast_tv);
		toastTV.setText(word);
		initToast(context, view);
	}

	private static void initToast(Context context, View view) {
		if (toast == null) {
			toast = new Toast(context);
		}
		toast.setView(view);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0,
				INSTANCE_FROM_BOTTOM);
		toast.show();
	}

}
