package com.tcl.simpletv.launcher2.localwidget;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import com.tcl.simpletv.launcher2.R;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

public class ParseLocalWidgetInfoXMLMsg {

	public static final String TAG = "ParseLocalWidgetInfoXMLMsg";
	
	private Context mContext;
	private ArrayList<LocalWidgetInfo> mLocalWidgetInfoList = new ArrayList<LocalWidgetInfo>();
	
	private final String PACKAGE_NAME_TITLE = "com.tcl.simpletv.launcher2:";
	
	public ParseLocalWidgetInfoXMLMsg(Context context){
		mContext = context;
	}
	
	public boolean ParsingXMLMsgDom(){

		Log.d(TAG, "Enter member function ParsingXMLMsg!\n");

		// Get a SAXParser from the SAXPArserFactory.
//		SAXParserFactory spf = SAXParserFactory.newInstance();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			// 通过文档构建器工厂获取一个文档构建器
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			// 通过文档通过文档构建器构建一个文档实例
			Document doc = db.parse(this.getClass().getResourceAsStream("local_widget_info_list.xml"));
//			Document doc = db.parse(mContext.getResources().getXml(R.xml.local_widget_info_list));
						
			// 获取所有名字为 “LocalWidgerInfo” 的节点
			NodeList nodeList = doc.getElementsByTagName("LocalWidgerInfo");
			for (int i = 0; i < nodeList.getLength(); ++i) 
			{
				Log.d(TAG,"node" + i + " = " + nodeList.item(i).getNodeValue());				
				
				
				LocalWidgetInfo localWidgetInfo = new LocalWidgetInfo();
				Element infoItem = (Element) nodeList.item(i);
				
				Log.d(TAG,"node" + i + " localWidgetName = " + infoItem.getAttribute("localWidgetName"));
				Log.d(TAG,"node" + i + " localWidgetId = " + Integer.valueOf(infoItem.getAttribute("localWidgetId")));
				Log.d(TAG,"node" + i + " icon = " + Integer.valueOf(infoItem.getAttribute("icon")));
				
				localWidgetInfo.localWidgetName = infoItem.getAttribute("localWidgetName");
				
	        	localWidgetInfo.localWidgetId = Integer.valueOf(infoItem.getAttribute("localWidgetId"));
	        	
	        	localWidgetInfo.provider = null;
	        	
	        	localWidgetInfo.minWidth = Integer.valueOf(infoItem.getAttribute("minWidth"));
	            localWidgetInfo.minHeight = Integer.valueOf(infoItem.getAttribute("minHeight"));
	            localWidgetInfo.minResizeWidth = Integer.valueOf(infoItem.getAttribute("minResizeWidth"));
	            localWidgetInfo.minResizeHeight = Integer.valueOf(infoItem.getAttribute("minResizeHeight"));
	            localWidgetInfo.updatePeriodMillis = Integer.valueOf(infoItem.getAttribute("updatePeriodMillis"));
	            localWidgetInfo.initialLayout = Integer.valueOf(infoItem.getAttribute("initialLayout"));
	            localWidgetInfo.initialKeyguardLayout = Integer.valueOf(infoItem.getAttribute("initialKeyguardLayout"));	           	     
	            
	            localWidgetInfo.configure = new ComponentName("launcher", "localwidget");
	            
	            localWidgetInfo.label = mContext.getString(Integer.valueOf(infoItem.getAttribute("label")));
	            localWidgetInfo.icon = Integer.valueOf(infoItem.getAttribute("icon"));
	            localWidgetInfo.previewImage  = Integer.valueOf(infoItem.getAttribute("previewImage"));
	            localWidgetInfo.autoAdvanceViewId = 0;//new LandWidgetLocalPhoto(mLauncher).getId();
	            localWidgetInfo.resizeMode = Integer.valueOf(infoItem.getAttribute("resizeMode"));
	            localWidgetInfo.widgetCategory = Integer.valueOf(infoItem.getAttribute("widgetCategory"));
	            
	            mLocalWidgetInfoList.add(localWidgetInfo);
			}
			
			
			
		} catch (ParserConfigurationException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (SAXException ex) {
			ex.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}


		/* Parsing has finished. */
		return true;
	}
	
	
	public boolean ParsingXMLMsg(){

		Log.d(TAG, "Enter member function ParsingXMLMsg! \n");
		Resources res = mContext.getResources();
		try {
		
			XmlResourceParser xrp = mContext.getResources().getXml(R.xml.local_widget_info_list);
			
			// 判断是否到了文件的结尾
			while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
				//文件的内容的起始标签开始，注意这里的起始标签是local_widget_info_list.xml文件里面<LocalWidgerInfoList>标签下面的第一个标签
				
				if (xrp.getEventType() == XmlResourceParser.START_TAG) {
					String tagname = xrp.getName();
					if (tagname.endsWith("LocalWidgerInfo")) {							
					
						LocalWidgetInfo localWidgetInfo = new LocalWidgetInfo();
												
						Log.d(TAG,"localWidgetId = " + Integer.valueOf(xrp.getAttributeValue(0)));
						Log.d(TAG,"localWidgetName = " + xrp.getAttributeValue(1));
						Log.d(TAG,"icon = " + xrp.getAttributeValue(2));
						Log.d(TAG,"label = " + res.getIdentifier(PACKAGE_NAME_TITLE + xrp.getAttributeValue(10), null, null));
//						Log.d(TAG,"icon = " + res.getIdentifier(xrp.getAttributeValue(2), "drawable", "com.tcl.simpletv.launcher2"));
						Log.d(TAG,"previewImage = " + res.getIdentifier(PACKAGE_NAME_TITLE + xrp.getAttributeValue(11), null, null));
						
						localWidgetInfo.localWidgetId = Integer.valueOf(xrp.getAttributeValue(0));
						localWidgetInfo.localWidgetName = xrp.getAttributeValue(1);
						localWidgetInfo.icon = res.getIdentifier(PACKAGE_NAME_TITLE + xrp.getAttributeValue(2), null, null);
									        	
			        	localWidgetInfo.provider = null;
			        	
			        	localWidgetInfo.minWidth = Integer.valueOf(xrp.getAttributeValue(3));
			            localWidgetInfo.minHeight = Integer.valueOf(xrp.getAttributeValue(4));
			            localWidgetInfo.minResizeWidth = Integer.valueOf(xrp.getAttributeValue(5));
			            localWidgetInfo.minResizeHeight = Integer.valueOf(xrp.getAttributeValue(6));
			            
			            localWidgetInfo.updatePeriodMillis = Integer.valueOf(xrp.getAttributeValue(7));
			            localWidgetInfo.initialLayout = Integer.valueOf(xrp.getAttributeValue(8));
			            localWidgetInfo.initialKeyguardLayout = Integer.valueOf(xrp.getAttributeValue(9));	           	     
			            
			            localWidgetInfo.configure = new ComponentName("launcher", "localwidget");
			            
			            localWidgetInfo.label = mContext.getString(res.getIdentifier(PACKAGE_NAME_TITLE + xrp.getAttributeValue(10), null, null));
			            localWidgetInfo.previewImage = res.getIdentifier(PACKAGE_NAME_TITLE + xrp.getAttributeValue(11), null, null);
			            localWidgetInfo.autoAdvanceViewId = Integer.valueOf(xrp.getAttributeValue(12));
			            localWidgetInfo.resizeMode = Integer.valueOf(xrp.getAttributeValue(13));
			            localWidgetInfo.widgetCategory = Integer.valueOf(xrp.getAttributeValue(14));
			            localWidgetInfo.localWidgetClassName = xrp.getAttributeValue(15);
			            mLocalWidgetInfoList.add(localWidgetInfo);
					}
				}
				// 下面的两个else if什么作用呢？
				else if (xrp.getEventType() == XmlResourceParser.END_TAG) {
					
				} else if (xrp.getEventType() == XmlResourceParser.TEXT) {
					
				} 
				xrp.next();
			}
				
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		/* Parsing has finished. */
		return true;
	}
	public ArrayList<LocalWidgetInfo> getLocalWidgetInfos(){
		
		ParsingXMLMsg();
		
		return mLocalWidgetInfoList;
	}
}
