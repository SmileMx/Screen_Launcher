package com.tcl.simpletv.launcher2.wallpaper;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.tcl.simpletv.launcher2.R;

public class WallpaperData {

	private static final String TAG = "Launcher.WallpaperData";
	private ArrayList<Integer> mThumbs;
	private ArrayList<Integer> mImages;
	private Context mContext;
	    
	  public WallpaperData(Context context){
		  mContext = context;
	  }
	  	
	  public void findWallpapers() {
	        mThumbs = new ArrayList<Integer>(24);
	        mImages = new ArrayList<Integer>(24);

	        final Resources resources = mContext.getResources();
	        // Context.getPackageName() may return the "original" package name,
	        // com.tcl.simpletv.launcher2; Resources needs the real package name,
	        // com.android.launcher. So we ask Resources for what it thinks the
	        // package name should be.
	        final String packageName = resources.getResourcePackageName(R.array.wallpapers);
	        
	        Log.v(TAG, "findWallpapers ------->  packageName = " + packageName );

	        addWallpapers(resources, packageName, R.array.wallpapers);
	        addWallpapers(resources, packageName, R.array.extra_wallpapers);
	    }

	    private void addWallpapers(Resources resources, String packageName, int list) {
	        final String[] extras = resources.getStringArray(list);
	        for (String extra : extras) {
	            int res = resources.getIdentifier(extra, "drawable", packageName);
	            if (res != 0) {
	                final int thumbRes = resources.getIdentifier(extra + "_small",
	                        "drawable", packageName);

	                if (thumbRes != 0) {
	                    mThumbs.add(thumbRes);
	                    mImages.add(res);
	                    Log.d(TAG, "add: [" + packageName + "]: " + extra + " (" + res + ")");
	                }
	            }
	        }
	    }
	    
	    public ArrayList<Integer> getmThumbs(){
	    	return mThumbs;
	    }
	    
	    public ArrayList<Integer> getmImages(){
	    	return mImages;
	    }
	
}
