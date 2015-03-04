package com.tcl.simpletv.launcher2.utils;

public class ConstantUtil {

	//workspace最多屏幕数
	public static final int WORKSPACE_MAX_SCREEN_COUNT = 9;
	
	//the current wallpaper index 
	public static final String CURRENT_WALLPAPER_ITEM = "CurWallpaperIndex";
	
	
	//Apps sort mode
	public static final int SORT_BY_INSTALLED_TIME = 0;
	public static final int SORT_BY_ALPHABET = 1;
	public static final int SORT_BY_FREQUENCY = 2;
	
	//add for restore the index of current sort mode
	public static final String APP_SORT_MODE = "AppSortModeIndex";		
	
	
	//SharedPreferences参数	
	public static final String FIRST_STARTUP_FLAG = "first_startup_flag";	//第一次启动标志
}
