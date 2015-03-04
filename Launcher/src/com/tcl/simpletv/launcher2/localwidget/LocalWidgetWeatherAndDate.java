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

public class LocalWidgetWeatherAndDate extends LocalWidgetView {

	private static final String TAG = "LocalWidgetWeatherAndDate";

	ImageView weather_date_img;
/*		
	private int WIDGET_WIDTH= 232;
	private int WIDGET_HEIGHT = 366;*/

	
	public LocalWidgetWeatherAndDate(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

		
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.local_widget_weather_and_date, null);
		
		addView(view/*, WIDGET_WIDTH, WIDGET_HEIGHT*/);
		
	/*	LinearLayout widget_layout = (LinearLayout) view.findViewById(R.id.widget_layout_weather_and_date);
		
		weather_date_img = new ImageView(context);
		weather_date_img.setImageResource(R.drawable.weather_date_preview);
		widget_layout.addView(weather_date_img, WIDGET_WIDTH, WIDGET_HEIGHT);*/
		
	}
	
	public LocalWidgetWeatherAndDate(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public LocalWidgetWeatherAndDate(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		Log.d(TAG, "----show-----");
		invalidate();
	}

}
