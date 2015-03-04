/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.tcl.simpletv.launcher2.toolbar;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import com.tcl.simpletv.launcher2.R;

/**
 * The Apps/Customize page that displays all the applications, widgets, and
 * shortcuts.
 */
public class ToolbarPagedView extends ToolbarPagedViewWithDraggableItems implements
		View.OnClickListener, View.OnKeyListener, ToolbarPagedViewIcon.PressedCallback {
	static final String TAG = "ToolbarPagedView";

	// Refs
	// private Launcher mLauncher;
	// private DragController mDragController;
	private final LayoutInflater mLayoutInflater;
	private final PackageManager mPackageManager;

	// Save and Restore
	// private int mSaveInstanceStateItemIndex = -1;
	// private ToolbarPagedViewIcon mPressedIcon;

	// Content
	private ArrayList<ToolbarApplicationsInfo> mApps;

	// Caching
	// private Canvas mCanvas;
	// private IconCache mIconCache;

	// Dimens
	private int mContentWidth;
	private int mAppIconSize;
	private int mMaxAppCellCountX, mMaxAppCellCountY;
	private ToolbarPagedViewCellLayout mWidgetSpacingLayout;
	private int mNumAppsPages;

	// Relating to the scroll and overscroll effects
	// Workspace.ZInterpolator mZInterpolator = new
	// Workspace.ZInterpolator(0.5f);
	// private static float CAMERA_DISTANCE = 6500;
	// private static float TRANSITION_SCALE_FACTOR = 0.74f;
	// private static float TRANSITION_PIVOT = 0.65f;
	// private static float TRANSITION_MAX_ROTATION = 22;
	// private static final boolean PERFORM_OVERSCROLL_ROTATION = true;
	// private AccelerateInterpolator mAlphaInterpolator = new
	// AccelerateInterpolator(0.9f);
	// private DecelerateInterpolator mLeftScreenAlphaInterpolator = new
	// DecelerateInterpolator(4);

	// Previews & outlines
	// private static final int sPageSleepDelay = 200;

	// private Rect mTmpRect = new Rect();
	// tjq
	private DelToolbarApp mDelToolbarApp;
	// private ToolbarService mToolbarService;
	// private ArrayList<ToolbarApplicationsInfo> mFavoriteAppList;
	private Context mContext;

	public ToolbarPagedView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		Log.d(TAG, "public AppsCustomizePagedView(Context context, AttributeSet attrs)");
		mLayoutInflater = LayoutInflater.from(context);
		mPackageManager = context.getPackageManager();
		mApps = new ArrayList<ToolbarApplicationsInfo>();
		// mIconCache = ((LauncherApplication)
		// context.getApplicationContext()).getIconCache();
		// mCanvas = new Canvas();

		Resources resources = context.getResources();
		mAppIconSize = resources.getDimensionPixelSize(R.dimen.app_icon_size);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AppsCustomizePagedView, 0,
				0);
		mMaxAppCellCountX = a.getInt(R.styleable.AppsCustomizePagedView_maxAppCellCountX, -1);// -1
		mMaxAppCellCountY = a.getInt(R.styleable.AppsCustomizePagedView_maxAppCellCountY, -1);// 7
		a.recycle();
		mWidgetSpacingLayout = new ToolbarPagedViewCellLayout(getContext());

		// The padding on the non-matched dimension for the default widget
		// preview icons
		// (top + bottom)
		mFadeInAdjacentScreens = false;

		// Unless otherwise specified this view is important for accessibility.
		// if (getImportantForAccessibility() ==
		// View.IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
		// setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
		// }
	}

	public static final Comparator<ToolbarApplicationsInfo> getAppNameComparator() {
		final Collator collator = Collator.getInstance();
		return new Comparator<ToolbarApplicationsInfo>() {
			public final int compare(ToolbarApplicationsInfo a, ToolbarApplicationsInfo b) {
				int result = collator.compare(a.title.toString(), b.title.toString());
				if (result == 0) {
					result = a.componentName.compareTo(b.componentName);
				}
				return result;
			}
		};
	}

	public void setApps(ArrayList<ToolbarApplicationsInfo> list) {
		mApps = list;
		Collections.sort(mApps, getAppNameComparator());
		updatePageCounts();
		setDataIsNoReady();
		invalidateOnDataChange();
	}

	private void updatePageCounts() {
		mNumAppsPages = (int) Math.ceil((float) mApps.size() / (mCellCountX * mCellCountY));
		Log.d(TAG, "updatePageCounts()" + "  mCellCountX=" + mCellCountX + "  mCellCountY="
				+ mCellCountY);
	}

	private void invalidateOnDataChange() {
		if (!isDataReady()) {
			// The next layout pass will trigger data-ready if both widgets and
			// apps are set, so
			// request a layout to trigger the page data when ready.
			Log.d(TAG, "Data is no Ready!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			requestLayout();
		} else {
			Log.d(TAG, "Data is Ready!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			// cancelAllTasks();
			invalidatePageData();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		if (!isDataReady()) {
			if (!mApps.isEmpty()) {
				setDataIsReady();
				setMeasuredDimension(width, height);
				onDataReady(width, height);
			}
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	protected void onDataReady(int width, int height) {
		// Note that we transpose the counts in portrait so that we get a
		// similar layout
		boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
		int maxCellCountX = Integer.MAX_VALUE;
		int maxCellCountY = Integer.MAX_VALUE;
		if (true) {// if (LauncherApplication.isScreenLarge()) {
			maxCellCountX = (isLandscape ? 12 : 6);// LauncherModel.getCellCountX():LauncherModel.getCellCountY()
			maxCellCountY = (isLandscape ? 1 : 1);
		}
		if (mMaxAppCellCountX > -1) {
			maxCellCountX = Math.min(maxCellCountX, mMaxAppCellCountX);
		}
		// Temp hack for now: only use the max cell count Y for widget layout
		int maxWidgetCellCountY = maxCellCountY;// 2
		if (mMaxAppCellCountY > -1) {
			maxWidgetCellCountY = Math.min(maxWidgetCellCountY, mMaxAppCellCountY);// (2,7)
		}

		Log.d(TAG, "onDataReady( ):maxCellCountX=" + maxCellCountX);
		// Now that the data is ready, we can calculate the content width, the
		// number of cells to
		// use for each page
		mWidgetSpacingLayout.setGap(mPageLayoutWidthGap, mPageLayoutHeightGap);
		mWidgetSpacingLayout.setPadding(mPageLayoutPaddingLeft, mPageLayoutPaddingTop,
				mPageLayoutPaddingRight, mPageLayoutPaddingBottom);
		mWidgetSpacingLayout.calculateCellCount(width, height, maxCellCountX, maxCellCountY);// (,,8,5)
		mCellCountX = mWidgetSpacingLayout.getCellCountX();// 8
		mCellCountY = mWidgetSpacingLayout.getCellCountY();// 5
		updatePageCounts();
		Log.d(TAG, "onDataReady()" + "  mCellCountX=" + mCellCountX + "  mCellCountY="
				+ mCellCountY);
		// Force a measure to update recalculate the gaps
		int widthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.AT_MOST);
		int heightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.AT_MOST);
		mWidgetSpacingLayout.calculateCellCount(width, height, maxCellCountX, maxWidgetCellCountY);
		mWidgetSpacingLayout.measure(widthSpec, heightSpec);

		mContentWidth = mWidgetSpacingLayout.getContentWidth();

		// Restore the page
		// int page = getPageForComponent(mSaveInstanceStateItemIndex);
		// invalidatePageData(Math.max(0, page), hostIsTransitioning);
		invalidatePageData(0, true);
	}

	@Override
	public void syncPages() {
		removeAllViews();

		Context context = getContext();
		for (int i = 0; i < mNumAppsPages; ++i) {
			ToolbarPagedViewCellLayout layout = new ToolbarPagedViewCellLayout(context);
			setupPage(layout);
			addView(layout);
		}
	}

	private void setupPage(ToolbarPagedViewCellLayout layout) {
		layout.setCellCount(mCellCountX, mCellCountY);
		Log.d(TAG, "setupPage()" + "  mCellCountX=" + mCellCountX + "  mCellCountY=" + mCellCountY);
		Log.d(TAG, "setupPage()--mPageLayoutWidthGap=" + mPageLayoutWidthGap
				+ "   mPageLayoutWidthGap=" + mPageLayoutHeightGap);
		layout.setGap(mPageLayoutWidthGap, mPageLayoutHeightGap);
		layout.setPadding(mPageLayoutPaddingLeft, mPageLayoutPaddingTop, mPageLayoutPaddingRight,
				mPageLayoutPaddingBottom);

		setVisibilityOnChildren(layout, View.GONE);
		int widthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.AT_MOST);
		int heightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.AT_MOST);
		layout.setMinimumWidth(getPageContentWidth());
		layout.measure(widthSpec, heightSpec);
		setVisibilityOnChildren(layout, View.VISIBLE);
	}

	/*
	 * Apps PagedView implementation
	 */
	private void setVisibilityOnChildren(ViewGroup layout, int visibility) {
		int childCount = layout.getChildCount();
		for (int i = 0; i < childCount; ++i) {
			layout.getChildAt(i).setVisibility(visibility);
		}
	}

	public int getPageContentWidth() {
		return mContentWidth;
	}

	@Override
	public void syncPageItems(int page, boolean immediate) {
		if (page < mNumAppsPages) {
			// if(mLauncher.isEditState()){
			// syncEditAppPageItems(page,immediate);
			// }else{
			syncAppsPageItems(page, immediate);
			// }
		}
	}

	ArrayList<ToolbarPagedViewIcon> appItems = new ArrayList<ToolbarPagedViewIcon>();

	public void syncAppsPageItems(int page, boolean immediate) {
		// ensure that we have the right number of items on the pages
		int numCells = mCellCountX * mCellCountY;
		Log.d(TAG, "syncAppsPageItems()" + "  mCellCountX=" + mCellCountX + "  mCellCountY="
				+ mCellCountY);
		int startIndex = page * numCells;
		int endIndex = Math.min(startIndex + numCells, mApps.size());
		ToolbarPagedViewCellLayout layout = (ToolbarPagedViewCellLayout) getPageAt(page);

		layout.removeAllViewsOnPage();
		// ArrayList<Object> items = new ArrayList<Object>();
		// ArrayList<Bitmap> images = new ArrayList<Bitmap>();
		for (int i = startIndex; i < endIndex; ++i) {
			final ToolbarApplicationsInfo info = mApps.get(i);

			// Bitmap temp = info.iconBitmap;
			// Matrix matrix = new Matrix();
			// matrix.setScale(0.8f,0.8f);
			// Bitmap resizedBitmap = Bitmap.createBitmap(temp, 0, 0,
			// temp.getWidth(), temp.getHeight(), matrix, true);
			// info.iconBitmap = resizedBitmap;

			RelativeLayout appLayout = (RelativeLayout) mLayoutInflater.inflate(
					R.layout.toolbar_customize_application, layout, false);

			ToolbarPagedViewIcon icon = (ToolbarPagedViewIcon) appLayout
					.findViewById(R.id.application_icon);
			final ImageView delIcon = (ImageView) appLayout.findViewById(R.id.application_del_icon);
			// final String pkgName = info.getPackageName();
			if (mDelToolbarApp.isEditting()) {
				delIcon.setVisibility(View.VISIBLE);
			}
			delIcon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.d(TAG, "delIcon click ============ ");
					mDelToolbarApp.delIcon(info.getPackageName());
				}
			});
			icon.applyFromApplicationInfo(info, true, this);
			icon.setDelIcon(delIcon);
			icon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.d(TAG, "icon.setOnClickListener============");
					if (!mDelToolbarApp.isEditting() && info.packageName != null) {
						mContext.startActivity(info.intent);
						mDelToolbarApp.hideWindow();
					}else if(info.packageName == null){
						Toast toast=Toast.makeText(mContext, "请在主界面长按图标，可添加应用哦！", Toast.LENGTH_SHORT);
						toast.show();
					}
				}
			});
			icon.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					Log.d(TAG, "icon.onLongClick============");
					// TODO Auto-generated method stub
					if(info.packageName != null){
						setDelIconVisible(View.VISIBLE);
						mDelToolbarApp.setEditStatus(true);
						return false;
					}
					return false;
				}
			});
			icon.setOnTouchListener(this);
			icon.setOnKeyListener(this);
			// icon.setEnabled(false);
			int index = i - startIndex;
			int x = index % mCellCountX;
			int y = index / mCellCountX;

			layout.addViewToCellLayout(appLayout, -1, i,
					new ToolbarPagedViewCellLayout.LayoutParams(x, y, 1, 1));
			appItems.add(icon);
		}

		layout.createHardwareLayers();
	}

	public void setDelIconVisible(int visible) {
		Iterator<ToolbarPagedViewIcon> it = appItems.iterator();
		while (it.hasNext()) {
			Log.d(TAG, "setDelIconVisible");
			ToolbarPagedViewIcon icon = it.next();
			icon.delIcon.setVisibility(visible);
		}
		// for (int i = 0; i < appItems.size(); i++) {
		// appItems.get(i).delIcon.setVisibility(visible);//View.INVISIBLE\View.VISIBLE
		// }
	}

	// public void updateDelIconState() {
	// for (int j = 0; j < mFavoriteAppList.size(); j++) {
	// Log.d(TAG, "" + mFavoriteAppList.get(j).packageName);
	// }
	// for (int i = 0; i < appItems.size(); i++) {
	// for (int j = 0; j < mFavoriteAppList.size(); j++) {
	// if
	// (appItems.get(i).mInfo.packageName.equals(mFavoriteAppList.get(j).packageName))
	// {
	// Log.d(TAG, "if     i=" + i + "    j=" + j);
	// appItems.get(i).delIcon.setVisibility(View.VISIBLE);
	// break;
	// } else {
	// Log.d(TAG, "else   i=" + i + "    j=" + j);
	// appItems.get(i).delIcon.setVisibility(View.INVISIBLE);
	// }
	// }
	// }
	// }

	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onLongClick(View v) {
		return false;
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public void iconPressed(ToolbarPagedViewIcon icon) {

	}

	// public void setFavoriteAppList(ArrayList<ToolbarApplicationsInfo>
	// favoriteAppList) {
	// // mFavoriteAppList = favoriteAppList;
	// }

	public void setInterface(DelToolbarApp delToolbarApp) {
		mDelToolbarApp = delToolbarApp;
	}

	public interface DelToolbarApp {
		void delIcon(String packageName);

		void setEditStatus(boolean isTrue);

		boolean isEditting();

		void hideWindow();
	}

}
