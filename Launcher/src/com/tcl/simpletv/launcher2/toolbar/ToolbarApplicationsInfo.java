package com.tcl.simpletv.launcher2.toolbar;

import java.net.URISyntaxException;
import java.util.HashMap;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.util.Log;

class ToolbarApplicationsInfo {
	private static final String TAG = "ToolbarApplicationsInfo";

	Bitmap iconBitmap;
	String title;
	Intent intent;
	String packageName;
	ComponentName componentName;

	ToolbarApplicationsInfo() {
	}

	public ToolbarApplicationsInfo(ToolbarApplicationsInfo info) {
		componentName = info.componentName;
		title = info.title.toString();
		intent = new Intent(info.intent);
	}

	public ToolbarApplicationsInfo(PackageManager pm, ResolveInfo info) {
		this.packageName = info.activityInfo.applicationInfo.packageName;
		this.componentName = new ComponentName(packageName, info.activityInfo.name);
		this.intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		this.title = info.loadLabel(pm).toString();
		// this.iconBitmap=info.loadIcon(pm);
	}

	public ToolbarApplicationsInfo(PackageManager pm, ResolveInfo info, ToolbarIconCache iconCache,
			HashMap<Object, CharSequence> labelCache) {
		Log.v(TAG, info.toString());
		this.packageName = info.activityInfo.applicationInfo.packageName;
		this.componentName = new ComponentName(packageName, info.activityInfo.name);
		this.intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//		this.title = info.loadLabel(pm).toString();
//		 this.iconBitmap=info.loadIcon(pm);

		iconCache.getTitleAndIcon(this, info, labelCache);
		this.title = info.loadLabel(pm).toString();
//		Log.d(TAG, "this.title="+this.title);
	}

	public void setApplicationInfo(ToolbarApplicationsInfo info) {
		this.iconBitmap = info.iconBitmap;
		this.componentName = info.componentName;
		this.title = info.title.toString();
		this.intent = new Intent(info.intent);
	}

	
	
	public void setTitle(String string) {
		this.title = string;
	}

	public String getTitle() {
		return this.title;
	}

	public void setIntent(String intentDescription) {
//		Intent ent=new Intent
		Intent intentTemp = null;
		try {
			intentTemp = Intent.parseUri(intentDescription, 0);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.intent = intentTemp;
	}

	public String getIntent() {
		return this.intent.toString();
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public void setPackageName(String packagename) {
		this.packageName=packagename;
	}

	public void setIconBitmap(Bitmap bitmap) {
		this.iconBitmap = bitmap;
	}

	public Bitmap getIconBitmap() {
		return this.iconBitmap;
	}
	@Override
	public String toString() {
		return "ApplicationInfo(title=" + title.toString() + ")";
	}
}
