package com.tcl.simpletv.launcher2.screenmanager;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tcl.simpletv.launcher2.CellLayout;
import com.tcl.simpletv.launcher2.DragController;
import com.tcl.simpletv.launcher2.DragSource;
import com.tcl.simpletv.launcher2.DragView;
import com.tcl.simpletv.launcher2.DropTarget;
import com.tcl.simpletv.launcher2.Launcher;
import com.tcl.simpletv.launcher2.LauncherApplication;
import com.tcl.simpletv.launcher2.R;
import com.tcl.simpletv.launcher2.Workspace;
import com.tcl.simpletv.launcher2.DropTarget.DragObject;
import com.tcl.simpletv.launcher2.utils.ConstantUtil;

public class ScreenManager extends GridView implements DragSource, DropTarget {
	private static final String TAG = "ScreenManager";

	private static final long DRUATION_ANIM = 250L;
	private static final long INTERVAL_DRAG_OVER = 100L;
	protected static final int MSG_LOCATION_CHANGE = 10;
	private static int sHasPosCount;
	private static int[] sLeftPosition = new int[9];
	private static int[] sTopPosition = new int[9];
	private DragController mDragController;
	private int mDragLayerHeight;
	private int mDragLayerWidth;
	private ScreenManagerItem mDraggingView;
	private int mDraggingViewIndex;
	private Bitmap mEmptyScreenBitmap;
	private int mEmptyViewIndex;

	private LayoutInflater mInflater;
	private boolean mIsDroped = false;
	private int mLastPostion;
	private Launcher mLauncher;
	private List<Integer> mMapPos = new ArrayList<Integer>();
	private int mPreviewHeight;
	private int mPreviewWidth;
	private List<Bitmap> mScreenBitmaps;
	private ScreenManagerAdapter mScreenMangerAdapter;

	private boolean mWorkspaceFadeInAdjacentScreens;
	
	Handler mHandler = new Handler() {

		public void handleMessage(Message paramMessage) {
			switch (paramMessage.what) {
			case MSG_LOCATION_CHANGE:
				Log.d(TAG, "handleMessage---MSG_LOCATION_CHANGE");
				handleDragScreen(paramMessage.arg1);
				break;
			default:
				break;
			}
		}
	};

	public ScreenManager(Context paramContext) {
		this(paramContext, null);
	}

	public ScreenManager(Context paramContext, AttributeSet paramAttributeSet) {
		this(paramContext, paramAttributeSet, 0);
	}

	public ScreenManager(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		
		Log.d(TAG, "ScreenManager----new");

		mWorkspaceFadeInAdjacentScreens = getResources().getBoolean(R.bool.config_workspaceFadeAdjacentScreens);
		
		try {
			setOverScrollMode(OVER_SCROLL_NEVER);
			mInflater = LayoutInflater.from(paramContext);
			
			mScreenBitmaps = new ArrayList<Bitmap>();

			mScreenMangerAdapter = new ScreenManagerAdapter();
			setAdapter(mScreenMangerAdapter);
			
			
			setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent,
						View view, int position, long id) {
					Log.d(TAG, "ScreenManager--onItemClick--position = " + position);
					
					if (ScreenManager.isAddScreenPostion(
							mLauncher.mWorkspace.getChildCount(), position) == true) {
						addScreen();
					} else {
						enterToScreen(position);
					}
				}
			});

			setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> parent,
						View paramView, int position, long id) {
					Log.d(TAG, "ScreenManager--onItemLongClick--position = " + position);
					
					if (!ScreenManager.isAddScreenPostion(
							mLauncher.mWorkspace.getChildCount(), position)) {
						startDrag(paramView, position);
					}
					return true;
				}
			});

		} catch (Throwable localThrowable) {
			Log.e(TAG, "ScreenManager init err!");
			localThrowable.printStackTrace();
		}
	}

	private void calculateCellsPosition() {
		
		int count = getChildCount();
		Log.d(TAG, "calculateCellsPosition---sHasPosCount = "+ sHasPosCount + "---count = " + count);
		
		if (count <= sHasPosCount){
			return;
		}
		
		for (int j = sHasPosCount; j < count; ++j) {
			int[] arrayOfInt = new int[2];
			getChildAt(j).getLocationOnScreen(arrayOfInt);
			sLeftPosition[j] = arrayOfInt[0];
			sTopPosition[j] = arrayOfInt[1];
			
			Log.d(TAG, ""+ j + "-----x = " + arrayOfInt[0] + "-----y = " +  arrayOfInt[1]);
		}
		sHasPosCount = count;
	}

	private void cleanCellsPosition() {		
		Log.d(TAG, "----cleanCellsPosition-----");
					
		sHasPosCount = 0;
	}
	
	
	private void changeHomeScreen(int index, boolean refresh) {
		
		int homeIndex = LauncherApplication.getPreferenceUtils().getHomeScreen();
		if (index != homeIndex) {
			LauncherApplication.getPreferenceUtils().saveHomeScreen(index);			
		}
		
		//刷新显示
		if(refresh == true){			
			try {
				((ScreenManagerItem) getChildAt(index)).refreshData(
						(Bitmap) mScreenBitmaps.get(index), mLauncher,
						index);
				((ScreenManagerItem) getChildAt(homeIndex)).refreshData(
						(Bitmap) mScreenBitmaps.get(homeIndex), mLauncher, homeIndex);			
			} catch (Exception localException) {
				localException.printStackTrace();
			}
		}
	}

	private List<Bitmap> createScreenBitmaps() {

		Log.d(TAG, "----createScreenBitmaps----");
		
		Workspace localWorkspace = mLauncher.mWorkspace;
		int screenCount = localWorkspace.getChildCount();
		int count = getItemCount();
		
		ArrayList<Bitmap> localArrayList = new ArrayList<Bitmap>();
		
		Log.d(TAG, "createScreenBitmaps----screenCount = " + screenCount + "----count = " + count);

		for (int i = 0; i < screenCount; i++) {
			Bitmap localBitmap = getViewBitmap(
					localWorkspace.getChildAt(i), mPreviewWidth,
					mPreviewHeight);
			if(localBitmap == null){
				Log.e(TAG, "createScreenBitmaps----getViewBitmap is null!");
				
				localBitmap = Bitmap.createBitmap(mPreviewWidth,
						mPreviewHeight, Bitmap.Config.ARGB_8888);
			}
			localArrayList.add(localBitmap);
		}
		
		//添加空屏  
		if(screenCount < ConstantUtil.WORKSPACE_MAX_SCREEN_COUNT){
			localArrayList.add(mEmptyScreenBitmap);
		}
		
		return localArrayList;
	}

	private int findTartCellIndex(float spanX, float spanY) {
		if(logoCount % 20 == 0){
		Log.d(TAG, "findTartCellIndex----spanX = " + spanX + "---spanY = " + spanY);
		}
		int i = 2;
		int pixelX = mDragLayerWidth / 3;
		int pixelY = mDragLayerHeight / 3;
		int[] arrayOfInt = new int[i];

		arrayOfInt[0] = (int)(spanX / pixelX);
		if (arrayOfInt[0] > i) {
			arrayOfInt[0] = i;
		}

		arrayOfInt[1] = (int)(spanY / pixelY);
		if (arrayOfInt[1] > i) {
			arrayOfInt[1] = i;
		}

		int index = 3 * arrayOfInt[1] + arrayOfInt[0];
		int count = mLauncher.mWorkspace.getChildCount();
		if (index > count - 1) {
			index = count - 1;
		}
		if(logoCount % 20 == 0){
		Log.d(TAG, "findTartCellIndex---index = " + index);
		}
		logoCount += 1;
		
		return index;
	}

	private int getItemCount() {
		int count = 9;
		int num = mLauncher.mWorkspace.getChildCount();
		if (num < count) {
			count = num + 1;
		}
		return count;
	}

	private void initScreenManager(Launcher launcher) {
		mLauncher = launcher;

		int HEIGHT_OFFSET = 30;	//因为底图有阴影，导致布局需要添加默认值来压缩高度  
		
		mDragLayerWidth = mLauncher.mDragLayer.getWidth();
		mDragLayerHeight = mLauncher.mDragLayer.getHeight();
//		mDragLayerWidth = 1920;
//		mDragLayerHeight = 1080;

		int verticalSpace = mLauncher.getResources().getDimensionPixelSize(
				R.dimen.screen_manager_vertical_space);
		int horizontalSpace = mLauncher.getResources().getDimensionPixelSize(
				R.dimen.screen_manager_horizontal_space);
		int paddingTop = mLauncher.getResources().getDimensionPixelSize(
				R.dimen.screen_thumbnail_padding_top);
		int paddingLeft = mLauncher.getResources().getDimensionPixelSize(
				R.dimen.screen_thumbnail_padding_left_right);

		mPreviewWidth = ((mDragLayerWidth - getPaddingLeft() - getPaddingRight())
				/ 3 - horizontalSpace - paddingLeft * 2);
		mPreviewHeight = ((mDragLayerHeight - getPaddingTop() - getPaddingBottom())
				/ 3 - verticalSpace - paddingTop - HEIGHT_OFFSET);

		Log.i(TAG, "verticalSpace=" + verticalSpace + ",horizontalSpace=" + horizontalSpace
				+ ",thumbnailPaddingTop=" + paddingTop+ ",mPreviewWidth="
				+ mPreviewWidth + ",mPreviewHeight=" + mPreviewHeight
				+ ",thumbnailPaddingLeftRight=" + paddingLeft);

		if (mEmptyScreenBitmap != null){
			return;
		}
		
		mEmptyScreenBitmap = Bitmap.createBitmap(mPreviewWidth,
				mPreviewHeight, Bitmap.Config.ARGB_8888);
		
	}

	private void initMapPos(int index){
		
		mMapPos.clear();
		
		int count = getItemCount();
		if(count <= 0){
			Log.e(TAG, "initMapPos---getItemCount err!");
			return;
		}
		
		for(int i = 0; i < count; i++){
			mMapPos.add(i);
		}
	}

	public static boolean isAddScreenPostion(int count, int index) {
		if ((count == index) && (count != 9)){
			return true;
		}
		return false;
	}

	private void moveScreen(int curPostion, int newPosition){

		int screenCount = mLauncher.mWorkspace.getChildCount();
		  
		Log.i(TAG, "moveScreen----curPostion = " + curPostion + "----newPosition = " + newPosition 
				+ ",mDraggingViewIndex = " + mDraggingViewIndex + "---screenCount = " + screenCount);
		
		if ((curPostion < 0) || (curPostion >= screenCount) || (newPosition < 0) || (newPosition >= screenCount)){
		    Log.e(TAG, "moveScreen, position is wrong!");
		    return;
		}

		  printPos(mMapPos, "before move:");
		  mMapPos.remove(curPostion);
		  mMapPos.add(newPosition, -1);
		  printPos(mMapPos, "after move:");
		    
		 		  
		  int smallPostion = curPostion;
		  if (curPostion > newPosition){
			  smallPostion = newPosition;
		  }

		  //刷新数据
		  for(int i = smallPostion; i < mMapPos.size(); i++ ){
			  if (i != newPosition){		  
				  ((ScreenManagerItem)getChildAt(i)).refreshData(mScreenBitmaps.get(mMapPos.get(i)), mLauncher, mMapPos.get(i));
		    }
		  }
		  ((ScreenManagerItem)getChildAt(newPosition)).setEmptyScreenView(mEmptyScreenBitmap);
		  
		//移动动画
		  if(curPostion < newPosition){
			  for(int i = curPostion; i < newPosition; i++){				
				  playViewAnimationFrom(i, i + 1);
			  }
		  }else if(curPostion > newPosition){
			  for(int i = newPosition; i < curPostion; i++){				
				  playViewAnimationFrom(i + 1, i);
			  }	
		  }
		  
	}

	private void playDragViewAnimation(DragView paramDragView, int newPosition) {
		int[] arrayOfInt = new int[2];
		paramDragView.getLocationOnScreen(arrayOfInt);
		AnimationSet localAnimationSet = new AnimationSet(false);
		TranslateAnimation localTranslateAnimation = new TranslateAnimation(
				arrayOfInt[0] - sLeftPosition[newPosition], 0.0F, arrayOfInt[1]
						- sTopPosition[newPosition], 0.0F);
		AlphaAnimation localAlphaAnimation = new AlphaAnimation(0.627451F, 1.0F);
		localAnimationSet.addAnimation(localTranslateAnimation);
		localAnimationSet.addAnimation(localAlphaAnimation);
		localAnimationSet.setDuration(200);
		
		
		
		paramDragView.setVisibility(View.INVISIBLE);
		
		((ViewGroup)(paramDragView.getParent())).removeView(paramDragView);
		
		getChildAt(newPosition).startAnimation(localAnimationSet);
	}

	private void playViewAnimationFrom(int toIndex, int fromIndex) {
		if (fromIndex >= 9){
			Log.e(TAG, "playViewAnimationFrom, position is wrong!");
			return;
		}
		
		Log.d(TAG, "getChildcount = " + getChildCount());
		Log.d(TAG, "playViewAnimationFrom---toIndex = " + toIndex + "---fromIndex = " + fromIndex);
		
		Log.d(TAG, "sLeftPosition[fromIndex] = " + sLeftPosition[fromIndex] 
				+ "---sLeftPosition[toIndex] = " + sLeftPosition[toIndex]);
		
		View localView = getChildAt(toIndex);
		TranslateAnimation localTranslateAnimation = new TranslateAnimation(
				sLeftPosition[fromIndex] - sLeftPosition[toIndex], 0.0F,
				sTopPosition[fromIndex] - sTopPosition[toIndex], 0.0F);
		localTranslateAnimation.setDuration(250L);
		localView.startAnimation(localTranslateAnimation);
		
	}

	private void printPos(List<Integer> mapPos, String title) {
		String str = title;
		for (int i = 0; i < mapPos.size(); ++i){
			str = str + "," + mapPos.get(i);
		}
		Log.i(TAG, str);
	}

	private void refreshScreenManager(int curPostion, int newPosition) {
//		mLauncher.mWorkspace.getChildCount();
		
//		mScreenBitmaps = createScreenBitmaps();
		
		Bitmap screenImage = mScreenBitmaps.get(curPostion);
		mScreenBitmaps.remove(curPostion);
		mScreenBitmaps.add(newPosition, screenImage);
		
		int count = mScreenBitmaps.size();
		for (int j = 0; j < count; ++j){
			((ScreenManagerItem) getChildAt(j)).refreshData(
					mScreenBitmaps.get(j), mLauncher, j);
		}
	}

//	public boolean acceptDrop(DragSource paramDragSource, int paramInt1,
//			int paramInt2, int paramInt3, int paramInt4,
//			DragView paramDragView, Object paramObject) {
//		return paramDragSource instanceof ScreenManager;
//	}
	public boolean acceptDrop(DragObject dragObject) {
		// TODO Auto-generated method stub
//		Log.d(TAG, "--------acceptDrop---------");
		return true;
	}
	
	protected void addScreen() {
		
		Log.i(TAG, "start addScreen, screenCount = " + mLauncher.mWorkspace.getChildCount());
			
		mHandler.removeMessages(MSG_LOCATION_CHANGE);
		
//		mLauncher.mWorkspace.getChildCount();
		
		if (mLauncher.mWorkspace.addScreen()) {
			if (mScreenBitmaps.size() < 9){
				mScreenBitmaps.add(Bitmap.createBitmap(mPreviewWidth,
						mPreviewHeight, Bitmap.Config.ARGB_8888));
			}
			
			LauncherApplication.getPreferenceUtils().saveScreenCount(mLauncher.mWorkspace.getChildCount());
			
			mScreenMangerAdapter.notifyDataSetChanged();
		}
		calculateCellsPosition();
		
		Log.i(TAG, "mScreenBitmaps.size = " + mScreenBitmaps.size());
		
		Log.i(TAG, "end addScreen,screenCount = " + mLauncher.mWorkspace.getChildCount());
	}

	public void closeScreenManager() {
			
		mScreenBitmaps.clear();
		if (mEmptyScreenBitmap == null){
			return;
		}
		mEmptyScreenBitmap.recycle();
		mEmptyScreenBitmap = null;
		
		cleanCellsPosition();
	}
	
/*	public boolean dispatchTouchEvent(MotionEvent paramMotionEvent) {
		if (paramMotionEvent.getAction() == 2)
			;
		for (boolean bool = true;; bool = super
				.dispatchTouchEvent(paramMotionEvent))
			return bool;
	}*/

//	public void endDrag() {
//		if (mIsDroped){
//			return;
//		}
//		refreshScreenManager();
//		if (mLauncher.mWorkspace.getChildCount() < ConstantUtil.WORKSPACE_MAX_SCREEN_COUNT){			
//			getChildAt(-1 + getChildCount()).setVisibility(View.VISIBLE);
//		}
//	}

	protected void enterToScreen(int paramInt) {
		mLauncher.closeScreenManager(paramInt);
	}

	public Rect estimateDropLocation(DragSource paramDragSource, int paramInt1,
			int paramInt2, int paramInt3, int paramInt4,
			DragView paramDragView, Object paramObject, Rect paramRect) {
		return null;
	}

	public Bitmap getCellLayoutBitmap(CellLayout paramCellLayout,
			int paramInt1, int paramInt2) {
		Bitmap localBitmap = Bitmap.createBitmap(paramInt1, paramInt2,
				Bitmap.Config.ARGB_8888);
		Canvas localCanvas = new Canvas(localBitmap);
		
		localCanvas.scale(paramInt1 / mDragLayerWidth, paramInt2
				/ mDragLayerHeight);
		
		localCanvas.translate(paramCellLayout.getPaddingLeft(),
				3 * paramCellLayout.getPaddingTop());
		
		
		paramCellLayout.setDrawingCacheEnabled(true);
		
		paramCellLayout.buildDrawingCache(true);
		 
//		Bitmap bitmap = Bitmap.createBitmap(paramCellLayout.getDrawingCache());
		paramCellLayout.draw(localCanvas);
		
		paramCellLayout.setDrawingCacheEnabled(false);
		
		return localBitmap;
	}
		

	 /**
     * Draw the view into a bitmap.
     */
	public Bitmap getViewBitmap(View view, int width, int height) {
		Log.d(TAG, "getViewBitmap---width = " + width + "---height = " + height);
		
		view.clearFocus();
		view.setPressed(false);
		boolean willNotCache = view.willNotCacheDrawing();
		view.setWillNotCacheDrawing(false);
		int color = view.getDrawingCacheBackgroundColor();
		view.setDrawingCacheBackgroundColor(0);
		
		float alpha = ((CellLayout)view).getShortcutsAndWidgets().getAlpha();
		
		Log.d(TAG, "getViewBitmap---alpha = " + alpha + "---color = " + color);
				
		((CellLayout)view).getShortcutsAndWidgets().setAlpha(1f);		
//		view.setAlpha(1.0f);
        
		if (color != 0){
			view.destroyDrawingCache();
		}
		
//		view.destroyDrawingCache();  
//		view.setDrawingCacheEnabled(true);
//		view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
		
		
		view.buildDrawingCache();
		Bitmap localBitmap1 = view.getDrawingCache();
		
		if ((view.getWidth() > 0) && (view.getWidth() > 0)
				&& (localBitmap1 == null)) {
			
				Log.i(TAG, "failed getViewBitmap(" + view
						+ ")", new RuntimeException());
				
			localBitmap1 = Bitmap.createBitmap(view.getWidth(),
					view.getHeight(), Bitmap.Config.ARGB_8888);
			view.draw(new Canvas(localBitmap1));
		}
		
		if (localBitmap1 == null){
			Log.i(TAG, "failed getViewBitmap, return null!");
			return null;
		}
		
		Bitmap localBitmap2 = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		localBitmap2 = Bitmap.createScaledBitmap(localBitmap1, width, 
				height, false);
		
		view.destroyDrawingCache();
//		view.setAlpha(alpha);
		((CellLayout)view).getShortcutsAndWidgets().setAlpha(alpha);
		view.setWillNotCacheDrawing(willNotCache);
		view.setDrawingCacheBackgroundColor(color);
		
				
		return localBitmap2;
		
	}

	protected void handleDragScreen(int paramInt) {
		if (mEmptyViewIndex == paramInt){
			return;
		}
		moveScreen(mEmptyViewIndex, paramInt);
		mEmptyViewIndex = paramInt;
	}

	
	int logoCount = 0;
	public void onDragOver(DragObject dragObject) {
		// TODO Auto-generated method stub
//		Log.d(TAG, "--------onDragOver---------");
		
	
//	}
//	public void onDragOver(DragSource paramDragSource, int paramInt1,
//			int paramInt2, int paramInt3, int paramInt4,
//			DragView paramDragView, Object paramObject) {
		
		if (!acceptDrop(null)){
			return;
		}
	
		if(logoCount % 20 == 0){
			Log.d(TAG, "onDragOver---getWidth = " + dragObject.dragView.getWidth() 
					+ "---getHeight = " + dragObject.dragView.getHeight());
		
//			Log.d(TAG, "onDragOver---dragObject.x = " + dragObject.dragView.getLeft()
//					+ "---dragObject.y = " + dragObject.dragView.getTop());
			
			
			
			Log.d(TAG, "onDragOver---dragObject.x = " + dragObject.x
					+ "---dragObject.xOffset = " + dragObject.xOffset
					+ "---dragObject.dragView.getX() = " + dragObject.dragView.getX()
					+ "---dragObject.dragView.getDragRegionLeft() = " + dragObject.dragView.getDragRegionLeft()
					+ "---dragObject.dragView.getPaddingLeft() = " + dragObject.dragView.getPaddingLeft()
								
					);
			
			
		}
		
		if((dragObject.dragView.getWidth() <= 0) || (dragObject.x <= 0)){
			return;
		}
		
		int newIndex = findTartCellIndex(dragObject.dragView.getX() + dragObject.dragView.getWidth() / 2,
				dragObject.dragView.getY() + dragObject.dragView.getHeight() / 2);
		
		if (newIndex != mEmptyViewIndex){
			moveScreen(mEmptyViewIndex, newIndex);
			mEmptyViewIndex = newIndex;
		}
		
	}

	public void onDrop(DragObject dragObject) {
//	public void onDrop(DragSource paramDragSource, int paramInt1, int paramInt2, int paramInt3, int paramInt4, DragView paramDragView, Object paramObject){
  
	    Log.i(TAG, "onDrop,mEmptyViewIndex=" + mEmptyViewIndex + ",mDraggingViewIndex=" 
	    		+ mDraggingViewIndex + ",getChildCount()=" + getChildCount());
	    long start,end;
	    start = System.currentTimeMillis();
	    
	  mHandler.removeMessages(MSG_LOCATION_CHANGE);
	  mIsDroped = true;
	  
	  if (mLauncher.mWorkspace.getChildCount() < ConstantUtil.WORKSPACE_MAX_SCREEN_COUNT){
		  getChildAt(getChildCount() - 1).setVisibility(View.VISIBLE);
	  }
	  
	  end = System.currentTimeMillis();
	  Log.d(TAG, "1111----time = " + (end - start));
	  
	  if (mEmptyViewIndex != mDraggingViewIndex){		  
		  
		  int curIndex = mLauncher.getCurrentWorkspaceScreen();	  
		    
		  mLauncher.mWorkspace.moveScreen(mDraggingViewIndex, mEmptyViewIndex);	 
		  
		  Log.d(TAG, "1111----curIndex = " + curIndex + "========= new " + mLauncher.getCurrentWorkspaceScreen());
		  
		  if((mDraggingViewIndex < curIndex) && (curIndex <= mEmptyViewIndex)){
			  mLauncher.setCurrentWorkspaceScreen(curIndex - 1); 	
			  Log.d(TAG, "setCurrentWorkspaceScreen(curIndex - 1) ");
		  }else if((mDraggingViewIndex > curIndex) && (curIndex >= mEmptyViewIndex)){
			  mLauncher.setCurrentWorkspaceScreen(curIndex + 1); 	
		  }else if(mDraggingViewIndex == curIndex){
			  mLauncher.setCurrentWorkspaceScreen(mEmptyViewIndex); 
		  }
		  Log.d(TAG, "========= new " + mLauncher.getCurrentWorkspaceScreen());

		  int homeScreen = LauncherApplication.getPreferenceUtils().getHomeScreen();    	
    	  if((mDraggingViewIndex < homeScreen) && (homeScreen <= mEmptyViewIndex)){
    		  changeHomeScreen(homeScreen - 1, false); 				 
		  }else if((mDraggingViewIndex > homeScreen) && (homeScreen >= mEmptyViewIndex)){
			  changeHomeScreen(homeScreen + 1, false); 	
		  }else if(mDraggingViewIndex == homeScreen){
			  changeHomeScreen(mEmptyViewIndex, false); 
		  }
    	
	  }
	  
		refreshScreenManager(mDraggingViewIndex, mEmptyViewIndex);
    	
		playDragViewAnimation(dragObject.dragView, mEmptyViewIndex);
		
		end = System.currentTimeMillis();
    	Log.d(TAG, "5555----time = " + (end - start));
	
}

	  public void onDropCompleted(View target, DragObject d, boolean isFlingToDelete,
	            boolean success) {
		  Log.i(TAG, "------onDropCompleted-------"); 
		
		  logoCount = 0;
	    }
	

	protected void removeScreen(final int index) {
		
		if (getChildCount() <= 2){
			Log.e(TAG, "removeScreen, it is last one screen!");
			return;
		}
		if(index < 0){
			Log.e(TAG, "removeScreen index err! index = " + index);
			return;
		}
		
			Workspace workspace = mLauncher.mWorkspace;
//			localWorkspace.getChildCount();
			
			Log.e(TAG, "removeScreen, getChildCount = " + ((CellLayout) workspace.getChildAt(index)).getShortcutsAndWidgetsCount());
			
			if(((CellLayout) workspace.getChildAt(index)).getShortcutsAndWidgetsCount() == 0){
				removeScreenDirectly(index);
			}else{
			AlertDialog dialog = new AlertDialog.Builder(
					mLauncher)
					.setMessage(mLauncher.getString(R.string.delete_screen_msg))
					.setTitle(mLauncher.getString(R.string.delete_screen_title))
					.setNegativeButton(R.string.cancel_action,
							new DialogInterface.OnClickListener() {								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
								}						
							})
					.setPositiveButton(R.string.rename_action,
							new DialogInterface.OnClickListener() {								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
									Log.d(TAG, "removeScreen-----which = " + which);
									removeScreenDirectly(index);
								}
							}).create();
			
			
			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {				
				@Override
				public void onDismiss(DialogInterface dialog) {
					// TODO Auto-generated method stub
					Log.d(TAG, "removeScreen-----dialog onDismiss.");
				}
			});
			
			dialog.show();
			}
		
	}

	public void removeScreenDirectly(int index){	
		
		Log.e(TAG, "removeScreenDirectly index = " + index);
		  
		int size = mScreenBitmaps.size();
		if ((index < 0) || (index >= size)){
			  Log.e(TAG, "removeScreenDirectly index is error!");
			  return;
		}	
		Log.i(TAG, "start removeScreenDirectly,screenCount = " + mLauncher.mWorkspace.getChildCount() 
				+ ",index = " + index + ",HomeScreen = " + LauncherApplication.getPreferenceUtils().getHomeScreen() 
				+ ",currentScreen=" + mLauncher.getCurrentWorkspaceScreen());
		   
		calculateCellsPosition();
		  
	    mHandler.removeMessages(MSG_LOCATION_CHANGE);
	    
	    	    
	    mLauncher.mWorkspace.removeScreen(index);
	    
	    mScreenBitmaps.remove(index);
	    int count = mLauncher.mWorkspace.getChildCount();
	    if (count == ConstantUtil.WORKSPACE_MAX_SCREEN_COUNT - 1){
	      mScreenBitmaps.add(Bitmap.createBitmap(mPreviewWidth, mPreviewHeight, Bitmap.Config.ARGB_8888));
	    }
	    
	    LauncherApplication.getPreferenceUtils().saveScreenCount(count);
	    
	  //调整主页索引
	    int homeIndex = LauncherApplication.getPreferenceUtils().getHomeScreen();
	    if(((index > 0) && (index < homeIndex))
	    		||((index == homeIndex) && (homeIndex == mLauncher.mWorkspace.getChildCount()))){
	    	changeHomeScreen(homeIndex - 1, false);	  	    	
//	    	((ScreenManagerItem)getChildAt(homeIndex - 1)).refreshData(mScreenBitmaps.get(homeIndex - 1), mLauncher, homeIndex - 1);
	    }
	    
	    int curIndex = mLauncher.getCurrentWorkspaceScreen();
	    if(((index > 0) && (index < curIndex))
	    	||((index == curIndex) && (curIndex == mLauncher.mWorkspace.getChildCount()))){
	    	mLauncher.setCurrentWorkspaceScreen(curIndex - 1); 	
//	    	((ScreenManagerItem)getChildAt(curIndex - 1)).refreshData(mScreenBitmaps.get(curIndex - 1), mLauncher, curIndex - 1);
	    }
	  
	    
	    size = mScreenBitmaps.size();
	    Log.d(TAG, "size = " + size);
	    
	    for(int i = 0; i < size; i++){	    	
	    	((ScreenManagerItem)getChildAt(i)).refreshData(mScreenBitmaps.get(i), mLauncher, i);
	    	
	    	if(i >= index){
	    		playViewAnimationFrom(i, i + 1);   
	    	}
	        	      
	    }
	    View localView = getChildAt(index);
	    TranslateAnimation localTranslateAnimation = new TranslateAnimation(sLeftPosition[1] - sLeftPosition[0], 0.0F, 0.0F, 0.0F);
	    localTranslateAnimation.setDuration(250L);
	    localView.startAnimation(localTranslateAnimation);
	    
	  
	    
	    Log.i(TAG, "end removeScreenDirectly,screenCount=" 
	    + mLauncher.mWorkspace.getChildCount() + ",index=" + index 
	    + ",HomeScreen=" + LauncherApplication.getPreferenceUtils().getHomeScreen() 
	    + ",currentScreen=" + mLauncher.getCurrentWorkspaceScreen());
	  
	}

	public void setDragController(DragController paramDragController) {
		mDragController = paramDragController;
	}

	public void showScreenManager(Launcher launcher) {
		Log.i(TAG, "------showScreenManager-------");
		initScreenManager(launcher);
		mScreenBitmaps = createScreenBitmaps();
//		mScreenMangerAdapter = new ScreenManagerAdapter();
//		setAdapter(mScreenMangerAdapter);
		
		mScreenMangerAdapter.notifyDataSetChanged();
		
	}

	protected void startDrag(View view, int postion) {
		
		mHandler.removeMessages(MSG_LOCATION_CHANGE);
		calculateCellsPosition();
		initMapPos(postion);
		mDraggingView = ((ScreenManagerItem) view);
		mLastPostion = postion;
		mDraggingViewIndex = postion;
		mEmptyViewIndex = postion;
//		mDragController.startDrag(mDraggingView, bmp, source, dragInfo, dragAction, dragRegion, initialDragViewScale);
		mDragController.startDrag(mDraggingView, this, null, DragController.DRAG_ACTION_MOVE);
		mDraggingView.setEmptyScreenView(mEmptyScreenBitmap);
		if (mLauncher.mWorkspace.getChildCount() < ConstantUtil.WORKSPACE_MAX_SCREEN_COUNT){
			getChildAt(getChildCount() - 1).setVisibility(View.INVISIBLE);
		}
				
		mIsDroped = false;
	}
	
	
	public boolean isDroped(){
		return mIsDroped;
	}

	static class ViewHolder {  
		ImageView mScreenAdd;
		ImageView mScreenDelete;
//		ImageView mScreenHome;
		ImageView mScreenHomeIcon;
		ImageView mScreenThumbnail;         
    } 
	
	private class ScreenManagerAdapter extends BaseAdapter {
		
		private LayoutInflater inflater; 
		
		public ScreenManagerAdapter() {
			
		}

		public int getCount() {
			return mScreenBitmaps.size();
		}

		public Object getItem(int arg0) {
			return mScreenBitmaps.get(arg0);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
	    	 Log.d(TAG, "getView---position = " + position);
	    	 
	    	 /*
	    	 ViewHolder holder;
	    	  
//	         if (convertView == null) {  
	             convertView = mInflater.inflate(R.layout.screen_manager_item, null);  
	             holder = new ViewHolder();  
	             holder.mScreenAdd = (ImageView) convertView.findViewById(R.id.screen_add);  
	             ImageView mScreenDelete = (ImageView) convertView.findViewById(R.id.screen_delete);	            
	             holder.mScreenHomeIcon = (ImageView) convertView.findViewById(R.id.screen_home);
	             holder.mScreenThumbnail = (ImageView) convertView.findViewById(R.id.screen_thumbnail);  
	             
//	             convertView.setTag(holder);  
//	         } else { 
//	             holder = (ViewHolder) convertView.getTag();  
//	         }
	         mScreenDelete.setClickable(true);
	         mScreenDelete.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int i = indexOfChild(v);
					Log.d(TAG, "mScreenDelete---i = " + i);
					removeScreen(i);
				}
	         });
			
	         holder.mScreenHomeIcon.setClickable(true);
	         holder.mScreenHomeIcon.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int i = indexOfChild(v);
						Log.d(TAG, "changeHomeScreen---i = " + i);
						changeHomeScreen(i, true);
					}
				});
	         
	         
	         ((ScreenManagerItem)convertView).refreshData(
						(Bitmap) mScreenBitmaps.get(position), mLauncher, position);
	         */
	    	 
	    	 ScreenManagerItem screenManagerItem;
	    	 if (convertView == null){
	    		 Log.d(TAG, "getView---convertView is null.");
	    		 screenManagerItem = (ScreenManagerItem)ScreenManager.this.mInflater.inflate(R.layout.screen_manager_item, null);
	    	 }else{
	        	 screenManagerItem = (ScreenManagerItem)convertView;
	         }
	    		 Log.d(TAG, "getView---set listener---start");
	    		 
	    		 if(screenManagerItem.mScreenDelete == null){
	    			 Log.d(TAG, "getView---mScreenDelete is null!");
	    		 }
	    		 
	    		 screenManagerItem.mScreenDelete.setTag(position);
	    		 screenManagerItem.mScreenDelete.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int index = (Integer)v.getTag();//indexOfChild((v);
						Log.d(TAG, " mScreenDelete onClick---index = " + index);
						removeScreen(index);
					}
		         });
	           
	           screenManagerItem.mScreenDelete.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					
					Log.d(TAG, "mScreenDelete onTouch---v.id = " + v.getId());
					return false;
				}
			});
	           
	           screenManagerItem.mScreenHome.setTag(position);
	           screenManagerItem.mScreenHome.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int index = (Integer)v.getTag();
					Log.d(TAG, " mScreenHome onClick---index = " + index);
					changeHomeScreen(index, true);
					
				}
			});
	           
	           Log.d(TAG, "getView---set listener---end");
	           
	           screenManagerItem.refreshData(
						(Bitmap) mScreenBitmaps.get(position), mLauncher, position);
	           
	           return screenManagerItem;
//	         return convertView;   
	     } 
		
	}

	@Override
	public boolean isDropEnabled() {
		// TODO Auto-generated method stub
//		Log.d(TAG, "--------isDropEnabled---------");
		return true;
	}

//	@Override
//	public void onDrop(DragObject dragObject) {
//		// TODO Auto-generated method stub
//		Log.d(TAG, "--------onDrop---------");
//	}

	@Override
	public void onDragEnter(DragObject dragObject) {
		// TODO Auto-generated method stub
		Log.d(TAG, "--------onDragEnter---------");
	}

//	@Override
//	public void onDragOver(DragObject dragObject) {
//		// TODO Auto-generated method stub
//		Log.d(TAG, "--------onDragOver---------");
//	}

	@Override
	public void onDragExit(DragObject dragObject) {
		// TODO Auto-generated method stub
		Log.d(TAG, "--------onDragExit---------");
		logoCount = 0;
	}

	@Override
	public void onFlingToDelete(DragObject dragObject, int x, int y, PointF vec) {
		// TODO Auto-generated method stub
		Log.d(TAG, "--------onFlingToDelete---------");
	}

	@Override
	public DropTarget getDropTargetDelegate(DragObject dragObject) {
		// TODO Auto-generated method stub
//		Log.d(TAG, "--------DropTarget---------");
		return null;
	}


	@Override
	public void getLocationInDragLayer(int[] loc) {
		// TODO Auto-generated method stub
//		Log.d(TAG, "--------getLocationInDragLayer---------");
	}

	@Override
	public boolean supportsFlingToDelete() {
		// TODO Auto-generated method stub
		Log.d(TAG, "--------supportsFlingToDelete---------");
		return false;
	}

	@Override
	public void onFlingToDeleteCompleted() {
		// TODO Auto-generated method stub
		Log.d(TAG, "--------onFlingToDeleteCompleted---------");
	}

}
