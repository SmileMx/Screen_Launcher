package com.inveno.piflow.entity.view.news;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inveno.piflow.R;
import com.inveno.piflow.entity.model.ShareNewsApp;
import com.inveno.piflow.tools.android.BitmapTools;
import com.inveno.piflow.tools.commontools.Const;
import com.inveno.piflow.tools.commontools.StringTools;
import com.inveno.piflow.tools.commontools.Tools;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class ShrareGirdFragment extends DialogFragment implements OnItemClickListener,OnClickListener{


	
	private GridView gridView;
	
	private  ShareAppAdapter appAdapter;
	
	private List<ShareNewsApp>  shareNewsApps;
	
	private String title;
	private String originalUrl;
	
	private IWXAPI api;
	
	private Bitmap screenShot;
	private Bitmap shotThumb;
	
	private Button cancelBtn;

	
	private RelativeLayout ly;
	int padding;
	
	public ShrareGirdFragment() {
	}

	public ShrareGirdFragment(String title,String url,Bitmap bitmap,String original) {
		this.title=title;
		if (StringTools.isNotEmpty(url)) {
			this.originalUrl=Const.BASE_URL+url;
		}else{
			this.originalUrl=original;
		}
		
		this.screenShot=bitmap;
		Tools.showLog("lhc", "网页模式连接："+originalUrl);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setStyle(STYLE_NO_TITLE, R.style.Dialog);
		shareNewsApps=getShareList(getActivity().getPackageManager());
		appAdapter=new ShareAppAdapter(getActivity(), shareNewsApps);
		api=WXAPIFactory.createWXAPI(getActivity(), Const.WEIXIN_FLYSHARE_APPID);
//		horizontalWidth=getActivity().getResources().getInteger(R.integer.share_news_horizontal_width);
		
	
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.wpe_share_news_grid,
				container, false);
		gridView=(GridView) view.findViewById(R.id.share_grid_gv);
		cancelBtn=(Button) view.findViewById(R.id.share_cancel_btn);
		cancelBtn.setOnClickListener(this);
		gridView.setAdapter(appAdapter);
		gridView.setOnItemClickListener(this);
		return view;
	}
	
	
	@Override
	public void onActivityCreated(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onActivityCreated(arg0);
	}
	
	
	class ShareAppAdapter extends BaseAdapter{

		private Context context;
		private List<ShareNewsApp> apps;
		private int size;
		public ShareAppAdapter(Context ct,List<ShareNewsApp> list){
			this.context=ct;
			this.apps=list;
			if (list!=null) {
				size=list.size();
			}
		}
		
		
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return size;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return apps.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;

			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.wpe_share_news_grid_item, null);
				viewHolder.titleTv = (TextView) convertView
						.findViewById(R.id.share_grid_item_name);
				viewHolder.iconIv =  (ImageView) convertView
						.findViewById(R.id.share_grid_item_icon);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			ShareNewsApp app=apps.get(position);
			viewHolder.titleTv.setText(app.getAppName());
			viewHolder.iconIv.setImageDrawable(app.getIcon());
			

			return convertView;
		}
		
		
		
		private class ViewHolder {
			ImageView iconIv;
			TextView titleTv;

		}
		
	}
	
	/**
	 * 获得分享列表（简单实现，只是把核心的东西写了写。不是太实用）
	 * **/
	private List<ShareNewsApp> getShareList(final PackageManager pm) {
		List<ShareNewsApp> apps = null;
		List<ResolveInfo> appList = getShareTargets(pm);
		if (appList.size() > 0) {
			apps=new ArrayList<ShareNewsApp>(5);
			ShareNewsApp app;
			for (int i = 0; i < appList.size(); i++) {
				app=new ShareNewsApp();
				ResolveInfo ri = (ResolveInfo) appList.get(i);
				ApplicationInfo apinfo = ri.activityInfo.applicationInfo;				
				
				app.setAppName(apinfo.loadLabel(
						pm).toString());
				app.setPkName(ri.activityInfo.packageName);
				Tools.showLog("lhc", "shareapp actName:"+ri.activityInfo.name);
				app.setActName(ri.activityInfo.name);
				app.setIcon(apinfo.loadIcon(pm));
				app.setIconRes(apinfo.icon);				
				apps.add(app);
			
			}
			

		}
		return apps; 
		
	

	}
	
	/**
	 * 获得所有带Intent.ACTION_SEND的应用列表。 ResolveInfo 这个东东真不错。
	 * **/
	private List<ResolveInfo> getShareTargets(PackageManager pm) {
		Intent intent = new Intent(Intent.ACTION_SEND, null);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		return pm.queryIntentActivities(intent,
				PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

		if (shareNewsApps!=null) {
			ShareNewsApp app=shareNewsApps.get(position);
			String name=app.getAppName();
			if (name.equals("微信")) {
				WXShareWebPgae();
				
			}else{
				String str = getResources().getString(R.string.share);
				Tools.showLog("lhc", "点击了:"+name+" 包名:"+app.getPkName()+" actName:"+app.getActName());
				createShare(title + " " + this.originalUrl + str, getActivity(), app.getPkName(), app.getActName());
			}
		}
		this.dismiss();
		
	}
	
	/**
	 * 实现自定义分享功能(主要就是启动对应的App)
	 * **/
	private void createShare(String content, Context context,
			String pkNmae,String actName) {

		try {
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent
					.setComponent(new ComponentName(
							pkNmae,
							actName));
			// 这里就是组织内容了，
			// shareIntent.setType("text/plain");
			shareIntent.setType("image/*");
			shareIntent.putExtra(Intent.EXTRA_TEXT, content);
			shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(shareIntent);

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

	}
	
	private void WXShareWebPgae(){
		WXWebpageObject webpage = new WXWebpageObject();
		//"http://flyshare.lem88.com/flyshare/client/getDetailInfo.action?infoId=1129288"
		webpage.webpageUrl = originalUrl;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = title;
		msg.description = "【分享自Flyshare手机安卓版】";
//		Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.send_music_thumb);
//		msg.thumbData = Util.bmpToByteArray(thumb, true);
//		int height=(int) ((((150 * 1.0f) / screenShot.getWidth()) * screenShot.getHeight()));
//    	shotThumb=BitmapTools.extractThumbByte(BitmapTools.bmpToByteArray(screenShot, false), 150, height, false);
//		msg.setThumbImage(shotThumb);
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
//		req.scene = isTimelineCb.isChecked() ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		api.sendReq(req);
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (shotThumb!=null &&!shotThumb.isRecycled()) {
			shotThumb.recycle();
			shotThumb=null;
		}
		
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		this.dismiss();
	}
}
