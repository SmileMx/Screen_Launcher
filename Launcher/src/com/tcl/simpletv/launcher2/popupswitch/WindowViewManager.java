package com.tcl.simpletv.launcher2.popupswitch;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

public class WindowViewManager {

	public static boolean removeView(Context paramContext, View paramView) {
		boolean flag = false;
		try {
			((WindowManager) paramContext.getSystemService("window"))
					.removeView(paramView);
			flag = true;
			return flag;
		} catch (Exception localException) {
			return flag;
		}
	}

	public static boolean addView(Context paramContext, View paramView,
			WindowManager.LayoutParams paramLayoutParams) {
		boolean flag = false;
		try {
			((WindowManager) paramContext.getSystemService("window")).addView(
					paramView, paramLayoutParams);
			flag = true;
			return flag;
		} catch (Exception localException) {
			return flag;
		}
	}

	public static boolean updateViewLayout(Context paramContext,  
			View paramView, WindowManager.LayoutParams paramLayoutParams) {
		boolean flag = false;
		try {
			((WindowManager) paramContext.getSystemService("window"))
					.updateViewLayout(paramView, paramLayoutParams);
			flag = true;
			return flag;
		} catch (Exception localException) {
			return flag;
		}
	}

}
