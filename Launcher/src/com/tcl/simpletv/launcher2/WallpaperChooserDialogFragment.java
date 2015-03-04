/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tcl.simpletv.launcher2;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tcl.simpletv.launcher2.utils.ConstantUtil;
import com.tcl.simpletv.launcher2.wallpaper.HorizontalListView;
import com.tcl.simpletv.launcher2.wallpaper.WallpaperData;
import com.tcl.simpletv.launcher2.wallpaper.WallpaperImageAdapter;

public class WallpaperChooserDialogFragment extends DialogFragment implements
        AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private static final String TAG = "Launcher.WallpaperChooserDialogFragment";
    private static final String EMBEDDED_KEY = "com.tcl.simpletv.launcher2."
            + "WallpaperChooserDialogFragment.EMBEDDED_KEY";
    
    

    private static final int DIEPLAY_WIDTH = 1920;
    private static final int DIEPLAY_HEIGHT = 1080;
    
    private static final int WALLPAPER_SCALED_WIDTH = 3060;
    private static final int WALLPAPER_SCALED_HEIGHT = 2068;

    
    private boolean mEmbedded;
    private Bitmap mBitmap = null;

    private WallpaperLoader mLoader;
    private WallpaperDrawable mWallpaperDrawable = new WallpaperDrawable();
    //remember the current wallpaper index
    private int curWallpaperIndex = 0;
    private WallpaperImageAdapter mAdapter ;
    
    //get the local wallpaper related Values
    private ArrayList<Integer> mThumbs = new ArrayList<Integer>();
    private ArrayList<Integer> mImages = new ArrayList<Integer>();    
    
    private WallpaperData wallpaperData;    
    
    //save the current wallpaper index ,when next time enter, select and display it 
    private SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor mEditor;
    
    public static WallpaperChooserDialogFragment newInstance() {
        WallpaperChooserDialogFragment fragment = new WallpaperChooserDialogFragment();
        fragment.setCancelable(true);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPrefs = LauncherApplication.getPreferenceUtils().getsSharedPreferences();
        mEditor = mSharedPrefs.edit();
        curWallpaperIndex = mSharedPrefs.getInt(ConstantUtil.CURRENT_WALLPAPER_ITEM, 0);
        
        wallpaperData = new WallpaperData(getActivity());
        wallpaperData.findWallpapers();
        mThumbs = wallpaperData.getmThumbs();
        mImages = wallpaperData.getmImages();
        
        if (savedInstanceState != null && savedInstanceState.containsKey(EMBEDDED_KEY)) {
            mEmbedded = savedInstanceState.getBoolean(EMBEDDED_KEY);  
            Log.v(TAG, "onCreate -------> mEmbedded = savedInstanceState.getBoolean(EMBEDDED_KEY) = " + mEmbedded );
        } else {
            mEmbedded = isInLayout();
            Log.v(TAG, "onCreate ------->  mEmbedded = isInLayout() = " + mEmbedded );
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    	 Log.v(TAG, "onSaveInstanceState -------");
        outState.putBoolean(EMBEDDED_KEY, mEmbedded);
    }

    private void cancelLoader() {
        if (mLoader != null && mLoader.getStatus() != WallpaperLoader.Status.FINISHED) {
            mLoader.cancel(true);
            mLoader = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v(TAG, "onDetach -------");
        cancelLoader();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy -------");
        cancelLoader();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        /* On orientation changes, the dialog is effectively "dismissed" so this is called
         * when the activity is no longer associated with this dying dialog fragment. We
         * should just safely ignore this case by checking if getActivity() returns null
         */
        Log.v(TAG, "onDismiss -------");
        Activity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    /* This will only be called when in XLarge mode, since this Fragment is invoked like
     * a dialog in that mode
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	 Log.v(TAG, "onCreateDialog -------");
      //  findWallpapers();
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	 Log.v(TAG, "onCreateView -------");
      //  findWallpapers();

        /* If this fragment is embedded in the layout of this activity, then we should
         * generate a view to display. Otherwise, a dialog will be created in
         * onCreateDialog()
         */
        
        if (mEmbedded) {
            View view = inflater.inflate(R.layout.wallpaper_chooser, container, false);
            view.setBackground(mWallpaperDrawable);
            ImageView img_workspace = (ImageView)view.findViewById(R.id.wallpaper_workspace);  

            String pathName = "data/data/com.tcl.simpletv.launcher2/files/test_bmp.png";
            
            Bitmap test_bitmap = BitmapFactory.decodeFile(pathName);
            
            if(test_bitmap != null){           	
                img_workspace.setImageBitmap(test_bitmap);
            }else{
            	 Log.v(TAG, "onCreateView ----test_bitmap = null");
            }                      

            final HorizontalListView wallPaper_listview = (HorizontalListView)view.findViewById(R.id.wallpaper_listview);            
            
            mAdapter = new WallpaperImageAdapter(getActivity());    		            
            mAdapter.setThumbs(mThumbs);    //prepare data to show 	
            
            wallPaper_listview.setAdapter(mAdapter);
            wallPaper_listview.setOnItemSelectedListener(this);
            wallPaper_listview.setOnItemClickListener(this);
           
           //modified by luoss date:2013/8/26
           ////隐藏前去掉刚进去的默认焦点            
            mAdapter.setCurItem(-1);            
            
            View setButton = view.findViewById(R.id.set);
            
            setButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
               	
                	mAdapter.setCurItem(curWallpaperIndex);
                	
                	Log.v(TAG, "curWallpaperIndex = "+ curWallpaperIndex);               	
                	
                	mEditor.putInt(ConstantUtil.CURRENT_WALLPAPER_ITEM, curWallpaperIndex);
                    mEditor.commit();
                    
                    selectWallpaper(curWallpaperIndex);  //设置选中壁纸             
                }
            });
            return view;
        }
        return null;
    }

    private void selectWallpaper(int position) {
        try {
            WallpaperManager wpm = (WallpaperManager) getActivity().getSystemService(
                    Context.WALLPAPER_SERVICE);
            wpm.setResource(mImages.get(position));
          /*  curWallpaperIndex = position;*/
            Activity activity = getActivity();
            activity.setResult(Activity.RESULT_OK);		
            activity.finish();
            activity.overridePendingTransition(0, R.anim.activity_exit);	//改变activity默认退出时飞入的动画
        } catch (IOException e) {
            Log.e(TAG, "Failed to set wallpaper: " + e);
        }
    }

    // Click handler for the Dialog's GridView
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*selectWallpaper(position);   */ 
    	 if (mLoader != null && mLoader.getStatus() != WallpaperLoader.Status.FINISHED) {
             mLoader.cancel();
         }
    	 Log.v(TAG, "curWallpaperIndex = "+ curWallpaperIndex);         
    	 curWallpaperIndex = position;
         mAdapter.setCurItem(position);
         
         mLoader = (WallpaperLoader) new WallpaperLoader().execute(position);
    }

    // Selection handler for the embedded Gallery view
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mLoader != null && mLoader.getStatus() != WallpaperLoader.Status.FINISHED) {
            mLoader.cancel();
        }
        Log.v(TAG, "curWallpaperIndex = "+ curWallpaperIndex);         
        curWallpaperIndex = position;
        mAdapter.setCurItem(position);
        
        mLoader = (WallpaperLoader) new WallpaperLoader().execute(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    class WallpaperLoader extends AsyncTask<Integer, Void, Bitmap> {
        BitmapFactory.Options mOptions;

        WallpaperLoader() {
            mOptions = new BitmapFactory.Options();
            mOptions.inDither = false;
            mOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            if (isCancelled()) return null;
            try {
              /*  return BitmapFactory.decodeResource(getResources(),
                        mImages.get(params[0]), mOptions);*/
            	Bitmap bitmap1 =  BitmapFactory.decodeResource(getResources(),
                        mImages.get(params[0]), mOptions);
            	
            	int wallpaper_width = bitmap1.getWidth();
            	int wallpaper_height = bitmap1.getHeight();

            	Log.v(TAG, "wallpaper_width = " + wallpaper_width);
            	Log.v(TAG, "wallpaper_height = " + wallpaper_height);           	
            	           
            	Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap1, WALLPAPER_SCALED_WIDTH, WALLPAPER_SCALED_HEIGHT,true);
            	
            	Bitmap bitmap3 = Bitmap.createBitmap(bitmap2, 0, 486, DIEPLAY_WIDTH, DIEPLAY_HEIGHT);

            	
            	return bitmap3;
                
            } catch (OutOfMemoryError e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap b) {
            if (b == null) return;

            if (!isCancelled() && !mOptions.mCancel) {
                // Help the GC
                if (mBitmap != null) {
                    mBitmap.recycle();
                }

                View v = getView();
                if (v != null) {
                    mBitmap = b;
                    mWallpaperDrawable.setBitmap(b);
                    v.postInvalidate();
                } else {
                    mBitmap = null;
                    mWallpaperDrawable.setBitmap(null);
                }
                mLoader = null;
            } else {
               b.recycle();
            }
        }

        void cancel() {
            mOptions.requestCancelDecode();
            super.cancel(true);
        }
    }

    /**
     * Custom drawable that centers the bitmap fed to it.
     */
    static class WallpaperDrawable extends Drawable {

        Bitmap mBitmap;
        int mIntrinsicWidth;
        int mIntrinsicHeight;

        /* package */void setBitmap(Bitmap bitmap) {
            mBitmap = bitmap;
            if (mBitmap == null)
                return;
            mIntrinsicWidth = mBitmap.getWidth();
            mIntrinsicHeight = mBitmap.getHeight();
        }

        @Override
        public void draw(Canvas canvas) {
            if (mBitmap == null) return;
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            int x = (width - mIntrinsicWidth) / 2;
            int y = (height - mIntrinsicHeight) / 2;
            canvas.drawBitmap(mBitmap, x, y, null);
        }

        @Override
        public int getOpacity() {
            return android.graphics.PixelFormat.OPAQUE;
        }

        @Override
        public void setAlpha(int alpha) {
            // Ignore
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            // Ignore
        }
    }
}
