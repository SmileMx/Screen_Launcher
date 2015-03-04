package com.tcl.simpletv.launcher2.localwidget;

import com.tcl.simpletv.launcher2.R;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class LocalWidgetLocalMusic extends LocalWidgetView {
	
private static final String TAG = "LocalWidgetLocalMusic";

	ImageView local_music_img;
		
	private int WIDGET_WIDTH_LAND = 258;
	private int WIDGET_HEIGHT_LAND = 282;
	
	private int WIDGET_WIDTH_PORT = 282;
	private int WIDGET_HEIGHT_PORT = 258;
	
	private int mWidgetWidth;
	private int mWidgetHeight;
	
//	private Context mContext;
	
	public LocalWidgetLocalMusic(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	//	mContext = context;
		

		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			mWidgetWidth = WIDGET_WIDTH_LAND;
			mWidgetHeight = WIDGET_HEIGHT_LAND;
		}else{
			mWidgetWidth = WIDGET_WIDTH_PORT;
			mWidgetHeight = WIDGET_HEIGHT_PORT;
		}
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.local_widget_local_music, null);
		
		addView(view);
		
		/*LinearLayout widgetLayout_localMusic = (LinearLayout) view.findViewById(R.id.widget_layout_local_music);
		
		local_music_img = new ImageView(context);
		local_music_img.setImageResource(R.drawable.local_music_preview);
		widgetLayout_localMusic.addView(local_music_img, mWidgetWidth, mWidgetHeight);*/
	}
	
	public LocalWidgetLocalMusic(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	

	public LocalWidgetLocalMusic(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		Log.d(TAG, "----show-----");
		//local_music_img.setVisibility(View.VISIBLE);
		invalidate();
	}

}
