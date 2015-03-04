package com.tcl.simpletv.launcher2.wallpaper;

import java.util.ArrayList;

import com.tcl.simpletv.launcher2.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * wallpaper  Adapter from system setting 
 * @author luoss
 *
 */
public class WallpaperImageAdapter extends BaseAdapter {

	 static final String TAG = "WallpaperImageAdapter";
		
	 private LayoutInflater mLayoutInflater;
     private int mSelectItem;
     private Context mContext;
     private ArrayList<Integer> mThumbs;
     
     public WallpaperImageAdapter(Context context) {        	
      //   mLayoutInflater = activity.getLayoutInflater();
     	mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mSelectItem = 0;
     }
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		//return mStrings.length;
		return mThumbs.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void setCurItem(int curItem){
		mSelectItem = curItem;
		notifyDataSetChanged();
	}
	
	public void setThumbs(ArrayList<Integer> thumbs){
		mThumbs = thumbs;
	}	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
/*		View retval = mLayoutInflater.inflate(R.layout.wallpaper_item,null);
		ImageView image = (ImageView)retval.findViewById(R.id.wallpaper_image);
		  int thumbRes = mThumbs.get(position);
          image.setImageResource(thumbRes);
          Drawable thumbDrawable = image.getDrawable();
          if (thumbDrawable != null) {
              thumbDrawable.setDither(true);
          } else {
              Log.e(TAG, "Error decoding thumbnail resId=" + thumbRes + " for wallpaper #"
                      + position);
          }	
	
          image.setPadding(8, 14, 8, 14);
          
		if(mSelectItem == position){
			image.setScaleType(ScaleType.FIT_XY);
			image.setBackgroundResource(R.drawable.listview_selected);
			
		}
		
		return retval;*/
		
		View retval = mLayoutInflater.inflate(R.layout.wallpaper_item,null);
		LinearLayout linear = (LinearLayout)retval.findViewById(R.id.layout_id);
		ImageView image = (ImageView)retval.findViewById(R.id.wallpaper_image);		
		  int thumbRes = mThumbs.get(position);
          image.setImageResource(thumbRes);
          Drawable thumbDrawable = image.getDrawable();
          if (thumbDrawable != null) {
              thumbDrawable.setDither(true);
          } else {
              Log.e(TAG, "Error decoding thumbnail resId=" + thumbRes + " for wallpaper #"
                      + position);
          }	
          
		if(mSelectItem == position){
						
		linear.setBackgroundResource(R.drawable.listview_item);
		
			final int imageWidth = image.getLayoutParams().width;
			final int imageHeight = image.getLayoutParams().height;						
				
			 Log.e(TAG, "imageWidth = " + imageWidth + " imageHeight = "
                     + imageHeight);			
			
			Animation rotate3d_anim =  new Rotate3dAnimation( imageWidth/2 ,  imageHeight/2 , -70);
			rotate3d_anim.setDuration(400);
			rotate3d_anim.setFillAfter(true);
			
        	linear.startAnimation(rotate3d_anim);  //选中时，这时设置的比较大              	       
			
		}
		return retval;
	}
}
