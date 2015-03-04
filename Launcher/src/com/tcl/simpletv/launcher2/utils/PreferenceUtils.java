package com.tcl.simpletv.launcher2.utils;
/**
 * 提供一个公共的统一的SharedPreferences接口方法
 * 
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PreferenceUtils {
	
	private static final String TAG = "PreferenceUtils";
	
	private static final String sSharedPreferencesKey = "com.tcl.simpletv.launcher2.prefs";
	
	private static final int DEFAULT_SCREEN_INDEX = 0;
	private static final int DEFAULT_SCREEN_COUNT = 2;
	
	static SharedPreferences.Editor sEditor;
	static SharedPreferences sSP;
	
	
	
	public SharedPreferences getsSharedPreferences() {
		return sSP;
	}

	public PreferenceUtils(Context context){
		Log.d(TAG, "PreferenceUtils--------new");
		initSP(context);
	}
	
	private void initSP(Context context)
	{
		Log.d(TAG, "------initSP------");
	    sSP = context.getApplicationContext().getSharedPreferences(sSharedPreferencesKey, Context.MODE_PRIVATE);
	    sEditor = sSP.edit();
	}
	
	
	
	public int getHomeScreen(){		
	    int index = sSP.getInt("pref_key_home_screen", DEFAULT_SCREEN_INDEX);
	    int count = sSP.getInt("pref_key_screen_count", DEFAULT_SCREEN_COUNT);
	    if (index >= count)
	    {
	    	index = count - 1;
	        saveHomeScreen(index);
	    }
	    return index;
	   
	}
	
	public void saveHomeScreen(int index){
		Log.d(TAG, "saveHomeScreen-------index = " + index);		
	    sEditor.putInt("pref_key_home_screen", index).commit();
	}
	
	public int getScreenCount(){	   
	    int count = sSP.getInt("pref_key_screen_count", DEFAULT_SCREEN_COUNT);
	    Log.d(TAG, "getScreenCount-------count = " + count);	    
	    return count;	   
	}
	
	public void saveScreenCount(int count){
		Log.d(TAG, "saveScreenCount-------count = " + count);		
	    sEditor.putInt("pref_key_screen_count", count).commit();
	}
}
