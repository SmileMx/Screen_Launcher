package com.tcl.simpletv.launcher2;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class FindWallpaper {
	 static final String TAG = "FindWallpaper";
	
	private ArrayList<Integer> mThumbs;
    private ArrayList<Integer> mImages;
    
    public FindWallpaper(Context context){
    	
    	 mThumbs = new ArrayList<Integer>(24);
         mImages = new ArrayList<Integer>(24);
    }

    public void addWallpapers(Resources resources, String packageName, int list) {
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
    
    public ArrayList<Integer> getThumbs(){
    	return mThumbs;
    }
	
    public ArrayList<Integer> getImages(){
    	return mImages;
    }
	
}
