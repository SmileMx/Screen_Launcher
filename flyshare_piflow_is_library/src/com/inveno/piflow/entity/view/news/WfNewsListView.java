package com.inveno.piflow.entity.view.news;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class WfNewsListView extends ListView{	
	
	
	private BaseAdapter adapter;
	public static boolean mIsScrolling;
	public WfNewsListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public WfNewsListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
//	int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
//            MeasureSpec.AT_MOST);  
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
//		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
//	            MeasureSpec.AT_MOST);  
		super.onMeasure(widthMeasureSpec,heightMeasureSpec);
	}


	
	
	
	

}
