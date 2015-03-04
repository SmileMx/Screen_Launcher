package com.tcl.simpletv.launcher2.toolbar;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.tcl.simpletv.launcher2.LauncherApplication;
import com.tcl.simpletv.launcher2.R;
import com.tcl.simpletv.launcher2.toolbar.ToolbarPagedView.DelToolbarApp;
import com.tcl.simpletv.launcher2.toolbar.ToolbarTabHost.BackKeyDown;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TabWidget;
import android.widget.TextView;

public class ToolbarService extends Service implements OnItemClickListener {

	static String TAG = "ToolbarService";

	private WindowManager wm;
	private WindowManager.LayoutParams wmlp;

	private View mToolbar;
	private GridView mfuGridview, latestgridview;
	private AppsAdapter mfuAppAdapter, latestAppAdapter;

	private ToolbarPagedView AppsContent;
	private ToolbarTabHost tabHost;;
	private TextView text0,text1,text2;

	private ArrayList<ToolbarApplicationsInfo> mfuAppList = new ArrayList<ToolbarApplicationsInfo>();
	private ArrayList<ToolbarApplicationsInfo> latestAppList = new ArrayList<ToolbarApplicationsInfo>();
	private ArrayList<ToolbarApplicationsInfo> favoriteAppList = new ArrayList<ToolbarApplicationsInfo>();

	public ToolbarIconCache mIconCache;
	private HashMap<Object, CharSequence> mLabelCache;

	private PackageManager packageManager;
	private AppUpdateReceiver appUpdateReceiver;

	private boolean isEditting = false;
	private int grid_Column_Count;

	public static final String AUTHORITY = "com.tcl.simpletv.appstat";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/appstat");
	public static final String KEY_STATNUM = "statnum";
	public static final String KEY_INTENT = "intent";

	private static final int LANDSREEN_GRIDVIEW_COLUMN = 12;
	private static final int PORTSREEN_GRIDVIEW_COLUMN = 6;

	private ToolbarDatabaseHelper dbHelper;
	private Context mContext;

	final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0x123:
				String packageName = (String) msg.obj;
				ArrayList<String> favoriateList = ToolbarUtilities.favoriateList;
				if (findContain(favoriateList, packageName)) {
					ToolbarUtilities.delFavorites(mContext, packageName);
				}
				setupData();
				break;
			case 0x124:
				Log.d(TAG, "mfuAppList.size()=" + mfuAppList.size() + ",latestAppList.size()="
						+ latestAppList.size());
				if (mfuAppList.size() > 0) {
					mfuAppAdapter.notifyDataSetChanged();
				}
				if (latestAppList.size() > 0) {
					latestAppAdapter.notifyDataSetChanged();
				}
				startRunnable();
				break;
			default:
				break;
			}
		}

	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mContext = this;// getContext();
		dbHelper = new ToolbarDatabaseHelper(this);
		packageManager = this.getPackageManager();

		mIconCache = new ToolbarIconCache(this);
		mLabelCache = new HashMap<Object, CharSequence>();

		// 注册BroadcastReceiver，用于在安装/卸载app时，刷新数据
		appUpdateReceiver = new AppUpdateReceiver();
		IntentFilter intent = new IntentFilter();
		intent.addAction(Intent.ACTION_PACKAGE_CHANGED);
		intent.addAction(Intent.ACTION_PACKAGE_REMOVED);
		intent.addAction(Intent.ACTION_PACKAGE_ADDED);
		intent.addDataScheme("package");
		registerReceiver(appUpdateReceiver, intent);

		initView();

		orientationChanged();

		initWindowLayoutParams();

		getMfuAppList();// 缩短第一次显示时间
	}

	/**
	 * 初始化View
	 */
	private void initView() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		mToolbar = inflater.inflate(R.layout.toolbar, null);

		LinearLayout lin=(LinearLayout)mToolbar.findViewById(R.id.tabwidget_lin);
		lin.setBackground(getResources().getDrawable(R.drawable.widget_title_bg));
		
		View mfuTab = (View) inflater.inflate(R.layout.toolbar_tabmini, null);
		text0 = (TextView) mfuTab.findViewById(R.id.tab_label);
		text0.setText(getResources().getString(R.string.toolbar_mfu_item));

		View laestTab = (View) inflater.inflate(R.layout.toolbar_tabmini, null);
		text1 = (TextView) laestTab.findViewById(R.id.tab_label);
		text1.setText(getResources().getString(R.string.toolbar_latest_item));

		View favoriteTab = (View) inflater.inflate(R.layout.toolbar_tabmini, null);
		text2 = (TextView) favoriteTab.findViewById(R.id.tab_label);
		text2.setText(getResources().getString(R.string.toolbar_favorite_item));
		try {
			tabHost = (ToolbarTabHost) mToolbar.findViewById(R.id.TabHost01);
			tabHost.setup();
			tabHost.addTab(tabHost.newTabSpec("tab_1")
					.setIndicator(mfuTab).setContent(R.id.LinearLayout1));//setIndicator("1"));//
			tabHost.addTab(tabHost.newTabSpec("tab_2")
					.setIndicator(laestTab).setContent(R.id.LinearLayout2));//setIndicator("2"));//
			tabHost.addTab(tabHost.newTabSpec("tab_3")
					.setIndicator(favoriteTab).setContent(R.id.LinearLayout3));//setIndicator("3"));//
			tabHost.setCurrentTab(0);
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.d("EXCEPTION", ex.getMessage());
		}

		final TabWidget tabWidget = tabHost.getTabWidget();
		for (int i = 0; i < tabWidget.getChildCount(); i++) {
//			tabWidget.getChildAt(i).getLayoutParams().height = (int) getResources().getDimension(
//					R.dimen.apps_customize_edit_tab_bar_height);
			tabWidget.getChildAt(i).getLayoutParams().width = 180;
		}

		tabHost.setInterface(new BackKeyDown() {

			@Override
			public void backorTouchOutside() {// 回调，用于tabHost处理返回键和点击空白处事件
				if (isEdit()) {
					Log.d(TAG, "isEdit()==true");
					hideDelIcon();
					setEditstatus(false);
				} else {
					removeToolbar();
					Log.d(TAG, "wm.removeView(mToolbar)");
				}
			}

			@Override
			public void hideToolbars() {
				// TODO Auto-generated method stub
				hideToolbar();
			}
		});

		mfuGridview = (GridView) mToolbar.findViewById(R.id.mfu_gridview);
		mfuAppAdapter = new AppsAdapter(this, mfuAppList);
		mfuGridview.setAdapter(mfuAppAdapter);
		mfuGridview.setOnItemClickListener(this);

		latestgridview = (GridView) mToolbar.findViewById(R.id.latest_gridview);
		latestAppAdapter = new AppsAdapter(this, latestAppList);
		latestgridview.setAdapter(latestAppAdapter);
		latestgridview.setOnItemClickListener(this);

		AppsContent = (ToolbarPagedView) mToolbar.findViewById(R.id.apps_customize_pane_content);
		AppsContent.setInterface(new DelToolbarApp() {

			@Override
			public void delIcon(String packageName) {

				ToolbarUtilities.delFavorites(mContext, packageName);
				getFavoriteAppList();
				if (favoriteAppList.get(0).packageName == null) {
					hideDelIcon();
					setEditstatus(false);
				}
				startRunnable();
			}

			@Override
			public void setEditStatus(boolean isTrue) {
				setEditstatus(isTrue);
			}

			@Override
			public boolean isEditting() {
				return isEditting;
			}

			@Override
			public void hideWindow() {
				if (mToolbar.isShown() == true) {
					removeToolbar();
				}
			}
		});

	}

	/**
	 * 横竖屏不同，GridView列数不同
	 */
	private void orientationChanged() {
		boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
		grid_Column_Count = (isLandscape ? LANDSREEN_GRIDVIEW_COLUMN : PORTSREEN_GRIDVIEW_COLUMN);

		mfuGridview.setNumColumns(grid_Column_Count);
		latestgridview.setNumColumns(grid_Column_Count);

	}

	/**
	 * 刷新PagedView数据
	 */
	private void startRunnable() {
		Runnable setAllAppsRunnable = new Runnable() {
			public void run() {
				if (AppsContent != null) {
					AppsContent.setApps(favoriteAppList);
				}
			}
		};
		setAllAppsRunnable.run();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		isEditting = false;
		if (mToolbar.isShown() == false) {
			LauncherApplication app = (LauncherApplication) mContext.getApplicationContext();
			app.mutexHide();
			wm.addView(mToolbar, wmlp); //
			setupData();
		} else {
			hideToolbar();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void setupData() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getMfuAppList();// 获取最常使用列表
				getLatestAppList();// 获取最新安装列表
				getFavoriteAppList();// 获取收藏应用列表
				// startRunnable();
				Message msg = new Message();
				msg.what = 0x124;
				mHandler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 获取最常使用列表
	 * 
	 * @return
	 */
	private void getMfuAppList() {
		ArrayList<ToolbarApplicationsInfo> AppList = new ArrayList<ToolbarApplicationsInfo>();
		ArrayList<String> mfuApp_name = getStatAppsList();
		if (mfuApp_name.size() > 0) {
			getAppInfo(mfuApp_name, AppList);
		}
		mfuAppList.clear();
		mfuAppList.addAll(AppList);
	}

	/**
	 * 获取最新安装列表
	 */
	private void getLatestAppList() {
		ArrayList<ToolbarApplicationsInfo> AppList = new ArrayList<ToolbarApplicationsInfo>();
		ArrayList<PackageInfo> packageInfoList = new ArrayList<PackageInfo>();
		packageInfoList.clear();

		List<ResolveInfo> resolveInfoList = null;
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveInfoList = packageManager.queryIntentActivities(mainIntent, 0);
		for (int i = 0; i < resolveInfoList.size(); i++) {

			try {
				packageInfoList.add(packageManager.getPackageInfo(
						resolveInfoList.get(i).activityInfo.packageName, 0));
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}

		ArrayList<String> latestApp_name = new ArrayList<String>();
		Collections.sort(packageInfoList, getAppLatestComparator());

		for (int i = 0, j = 0; j < grid_Column_Count && i < packageInfoList.size(); i++) {
			if (findContain(latestApp_name, packageInfoList.get(i).packageName) == false) {
				latestApp_name.add(packageInfoList.get(i).packageName);
				j++;
			}
		}
		getAppInfo(latestApp_name, AppList);

		latestAppList.clear();
		latestAppList.addAll(AppList);

	}

	/**
	 * 获取收藏列表
	 */
	private void getFavoriteAppList() {

		queryFavoritesdb();
		Log.d(TAG, "favoriteAppList.size()= " + favoriteAppList.size());
		if (favoriteAppList.size() < 1) {
			ToolbarApplicationsInfo toolbarInfo = new ToolbarApplicationsInfo();
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.screen_add_normal);
			toolbarInfo.setIconBitmap(bmp);
			toolbarInfo.setTitle(" ");
			favoriteAppList.add(toolbarInfo);
		}

	}

	/**
	 * 查询使用频率数据库，获取包名列表
	 */
	private ArrayList<String> getStatAppsList() {
		Intent appIntent = null;

		ArrayList<String> appStatList = new ArrayList<String>();
		String sortOrder = KEY_STATNUM + " desc";
		ContentResolver contentResolver = this.getContentResolver();
		// 按使用频率排列
		Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, sortOrder);

		if (cursor != null && cursor.moveToFirst()) {

			for (int i = 0, j = 0; i < cursor.getCount() && j < grid_Column_Count; i++) {
				String strIntent = cursor.getString(cursor.getColumnIndex(KEY_INTENT));
				cursor.moveToNext();
				if (strIntent != null) {
					try {
						appIntent = Intent.parseUri(strIntent, 0);
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}

					String packageName = appIntent.getComponent().getPackageName();
					if (findContain(appStatList, packageName) == false) {
						appStatList.add(packageName);
						j++;
					}
				}
			}
		} else {
			Log.e(TAG, "cursor is null!");
		}
		cursor.close();
		return appStatList;
	}

	/**
	 * 查找，存在返回true，否则false
	 */
	private boolean findContain(ArrayList<String> apps, String packageName) {
		for (String info : apps) {
			if (info.equals(packageName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 配置LayoutParams
	 */
	private void initWindowLayoutParams() {

		wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		wmlp = new WindowManager.LayoutParams();
		wmlp.type = 2002;
		wmlp.format = 1;
		wmlp.gravity = Gravity.BOTTOM;
		wmlp.x = 0;
		wmlp.y = 0;
		wmlp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		// | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;// WRAP_CONTENT;//240;//
		// wmlp.height = 259;// 230;// 300Ϊ��һ�θ߶�
		wmlp.height = (int) getResources().getDimension(R.dimen.Edit_height);
		// /WindowManager.LayoutParams.MATCH_PARENT;//.WRAP_CONTENT;//
		wmlp.windowAnimations =android.R.style.Animation_InputMethod;// R.style.mywindow;//android.R.style.; 
		// wmParams.dimAmount=0.9f;
		// wmParams.horizontalMargin=50;
		// wmParams.verticalMargin=250;

	}

	/**
	 * 比较器
	 */
	private Comparator<PackageInfo> getAppLatestComparator() {
		return new Comparator<PackageInfo>() {
			public final int compare(PackageInfo a, PackageInfo b) {
				return (b.lastUpdateTime > a.lastUpdateTime) ? 1 : -1;
			}
		};
	}

	/**
	 * 根据packageName，获取ToolbarApplicationsInfo
	 */
	private void getAppInfo(ArrayList<String> arr, ArrayList<ToolbarApplicationsInfo> appList) {

		Intent intent = null;
		for (int i = 0; i < grid_Column_Count && i < arr.size(); i++) {
			try {
				intent = packageManager.getLaunchIntentForPackage(arr.get(i));
			} catch (Exception e) {
				// TODO: handle exception
			}
			if (arr.get(i) != null && intent != null) {
				ResolveInfo resolveInfo0 = packageManager.resolveActivity(intent,
						PackageManager.GET_RESOLVED_FILTER);
				appList.add(new ToolbarApplicationsInfo(packageManager, resolveInfo0, mIconCache,
						mLabelCache));
			}
		}
	}

	/**
	 * 根据packageName，获取ToolbarApplicationsInfo
	 */
	private void getAppInfo(String[] arr, ArrayList<ToolbarApplicationsInfo> appList) {
		int i = 0;
		Intent intent = null;
		for (i = 0; i < grid_Column_Count && i < arr.length; i++) {
			try {
				intent = packageManager.getLaunchIntentForPackage(arr[i]);
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (arr[i] == null && intent == null) {
				appList.add(new ToolbarApplicationsInfo());
				// Log.v(TAG, "null");
			} else {
				ResolveInfo resolveInfo0 = packageManager.resolveActivity(intent,
						PackageManager.GET_RESOLVED_FILTER);
				appList.add(new ToolbarApplicationsInfo(packageManager, resolveInfo0, mIconCache,
						mLabelCache));
				// Log.v(TAG, appList.get(i).packageName);
			}
		}
		if (i < grid_Column_Count) {
			for (int j = arr.length; j < 6; j++) {
				appList.add(new ToolbarApplicationsInfo());
				// Log.v(TAG, "null");
			}
		}

	}

	/**
	 * 查询db，更新收藏应用列表favoriteAppList
	 */
	private void queryFavoritesdb() {
		ArrayList<ToolbarApplicationsInfo> AppList = new ArrayList<ToolbarApplicationsInfo>();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query(ToolbarDatabaseHelper.TOOLBAR_TABLE_NAME, null, null, null, null,
				null, null);
		cursor.moveToFirst();
		// AppList.clear();
		String packageName;
		Intent intent = null;
		while (!cursor.isAfterLast()) {
			packageName = cursor
					.getString(cursor.getColumnIndex(ToolbarDatabaseHelper.PACKAGENAME));
			try {
				intent = packageManager.getLaunchIntentForPackage(packageName);
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (intent != null) {
				Log.d(TAG, "intent=" + intent.toString());
				ResolveInfo resolveInfo = packageManager.resolveActivity(intent,
						PackageManager.GET_RESOLVED_FILTER);
				if (resolveInfo == null)
					Log.d(TAG, "resolveInfo1=null");
				AppList.add(new ToolbarApplicationsInfo(packageManager, resolveInfo, mIconCache,
						mLabelCache));
			}
			cursor.moveToNext();
		}
		cursor.close();
		db.close();

		favoriteAppList.clear();
		favoriteAppList.addAll(AppList);
	}

	/**
	 * 横竖屏切换回调，更改GridView列数以及刷新数据
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		Log.d(TAG, "onConfigurationChanged()");
		super.onConfigurationChanged(newConfig);

		orientationChanged();
		setupData();
		
		text0.setText(getResources().getString(R.string.toolbar_mfu_item));
		text1.setText(getResources().getString(R.string.toolbar_latest_item));
		text2.setText(getResources().getString(R.string.toolbar_favorite_item));
	}
	
	/**
	 * service挂掉，隐藏工具栏
	 */
	@Override
	public void onDestroy() {

		if (mToolbar.isShown() == true) {
			hideToolbar();
		}
		unregisterReceiver(appUpdateReceiver);
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	/**
	 * GridView item点击事件监听：启动app，隐藏工具栏
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		switch (arg0.getId()) {
		case R.id.mfu_gridview:
			setupActivity(mfuAppList, arg2);
			break;
		case R.id.latest_gridview:
			setupActivity(latestAppList, arg2);
			break;

		default:
			break;
		}
	}

	private void setupActivity(ArrayList<ToolbarApplicationsInfo> list, int arg) {
		if (list.get(arg).intent != null) {
			startActivity(list.get(arg).intent);
			if (mToolbar.isShown() == true) {
				hideToolbar();
			}
		}
	}

	/**
	 * 隐藏工具栏和DelIcon图标
	 */
	private void hideToolbar() {
		hideDelIcon();
		removeToolbar();
	}

	/**
	 * 隐藏DelIcon图标
	 */
	private void hideDelIcon() {
		AppsContent.setDelIconVisible(View.INVISIBLE);
	}

	/**
	 * 隐藏工具栏
	 */
	private void removeToolbar() {
		if (mToolbar.isShown() == true) {
			wm.removeView(mToolbar);
		}
	}

	public boolean isEdit() {
		return isEditting;
	}

	/**
	 * 设置编辑状态
	 */
	public void setEditstatus(boolean isTrue) {
		isEditting = isTrue;
	}

	private Context mContext1;
	
	class AppsAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		ArrayList<ToolbarApplicationsInfo> list = null;

		public AppsAdapter(Context context, ArrayList<ToolbarApplicationsInfo> applist) {
			this.list = applist;
			inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			if (list == null) {
				return 0;
			}
			return this.list.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.toolbar_gridview_item, null);
				viewHolder = new ViewHolder();
				viewHolder.appname = (TextView) convertView.findViewById(R.id.name_textview);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.appname.setCompoundDrawablesWithIntrinsicBounds(null,
					new ToolbarFastBitmapDrawable(list.get(position).getIconBitmap()), null, null);
			viewHolder.appname.setText(list.get(position).getTitle());

			return convertView;
		}

	}

	/**
	 * BroadcastReceiver，接收app安装、更新和卸载的广播
	 */
	public class AppUpdateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			Log.d(TAG, action + "  onReceive intent=" + intent);
			if (Intent.ACTION_PACKAGE_CHANGED.equals(action)
					|| Intent.ACTION_PACKAGE_REMOVED.equals(action)
					|| Intent.ACTION_PACKAGE_ADDED.equals(action)) {

				String packageName = intent.getData().getSchemeSpecificPart();

				Message msg = new Message();
				msg.what = 0x123;
				msg.obj = packageName;
				mHandler.sendMessage(msg);

			}
		}

	}

	class ViewHolder {
		public TextView appname;
	}

}
