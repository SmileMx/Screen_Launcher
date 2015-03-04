package com.tcl.simpletv.launcher2.localwidget;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class LocalWidgetManager {
	private static final String TAG = "LocalWidgetManager";
	private LocalWidgetView mLocalWidget;
	private Context mContext;
	private ArrayList<LocalWidgetInfo> mLocalWidgetInfoList;
	public LocalWidgetManager(Context context){
		Log.d(TAG, "----LocalWidgetManager init---");
		
		mContext = context;
		
		long start = System.currentTimeMillis();
		ParseLocalWidgetInfoXMLMsg parseLocalWidgetInfoXMLMsg = new ParseLocalWidgetInfoXMLMsg(mContext);
		mLocalWidgetInfoList = parseLocalWidgetInfoXMLMsg.getLocalWidgetInfos();
		Log.d(TAG, "ParseLocalWidgetInfoXMLMsg time = " + (System.currentTimeMillis() - start));
		Log.d(TAG, "mLocalWidgetInfoList.size() = " + mLocalWidgetInfoList.size());
				
	}
	
	public LocalWidgetView getLocalWidgetById(int localWidgetId){
		
		int temp = 0;
//		if((mLocalWidgetNews == null) || (mLocalWidgetNews.getParent() != null)){
			if((mLocalWidgetInfoList != null) && (mLocalWidgetInfoList.size() > 0)){				
				
				for (int i = 0;i < mLocalWidgetInfoList.size(); i++) {
		            if (mLocalWidgetInfoList.get(i).localWidgetId == localWidgetId) {
		            	
		            	temp = i;
		            	
		            	break;
	            	
		             } 
		        }	
				
				Log.d(TAG, "current localView location is " + temp);
				mLocalWidget = (LocalWidgetView)CreateClassInstanceByName(mLocalWidgetInfoList.get(temp).localWidgetClassName);
			}
						
//		}
		return mLocalWidget;
	}
	
/*	public LocalWidgetInfo getLocalWidgetInfoById(int localWidgetId) {
		return mLocalWidgetInfoList.get(0);
//		LocalWidgetView localWidget = getLocalWidgetById(localWidgetId);
//		if(localWidget != null){
//			return localWidget.mLocalWidgetInfo;
//		}
//		return null;
	}*/
	
	public LocalWidgetInfo getLocalWidgetInfoById(int appWidgetId) {
		 Log.e(TAG, "appWidgetId =  " + appWidgetId);
        for (LocalWidgetInfo widgetInfo : mLocalWidgetInfoList) {
            if (widgetInfo.localWidgetId == appWidgetId) {
            	return widgetInfo;
             } 
        }						
        Log.e(TAG, "LocalWidgetInfo is null !");
		return null;
	}
	
	public ArrayList<LocalWidgetInfo> getLocalWidgetInfoList(){
		Log.d(TAG, "getLocalWidgetInfoList --- mLocalWidgetInfoList.size = " + mLocalWidgetInfoList.size());
		 return mLocalWidgetInfoList;
	}
	
	/**
	 * 根据类名创建类实例  
	 * @param className
	 * @return
	 */
	private Object CreateClassInstanceByName(String className){
		//根据类名获取Class对象
		  Class c;
		  java.lang.reflect.Constructor constructor;
		  Object object = null;
		  
		try {
//			c = Class.forName("com.tcl.simpletv.launcher2.localwidget.LocalWidgetFallsStreamNews");
			c = Class.forName(className);
			
		//参数类型数组
		  Class[] parameterTypes = {Context.class}; 
		  
		//根据参数类型获取相应的构造函数
		  constructor = c.getConstructor(parameterTypes);

		//参数数组
		  Object[] parameters={mContext};
		//根据获取的构造函数和参数，创建实例
		  object = constructor.newInstance(parameters);	
				  
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (Exception e){
			e.printStackTrace();
		}
		
		finally{
			return object;
		}
	}
}
