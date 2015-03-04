
/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.tcl.simpletv.launcher2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.SearchManager;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.TextKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Advanceable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tcl.simpletv.launcher2.DropTarget.DragObject;
import com.tcl.simpletv.launcher2.localwidget.LocalWidgetInfo;
import com.tcl.simpletv.launcher2.localwidget.LocalWidgetManager;
import com.tcl.simpletv.launcher2.localwidget.LocalWidgetView;
import com.tcl.simpletv.launcher2.popupswitch.ActionItem;
import com.tcl.simpletv.launcher2.popupswitch.Curtain;
import com.tcl.simpletv.launcher2.popupswitch.LampCord;
import com.tcl.simpletv.launcher2.popupswitch.QuickAction;
import com.tcl.simpletv.launcher2.screenmanager.ScreenManager;
import com.tcl.simpletv.launcher2.submenu.LauncherMenuDialog;
import com.tcl.simpletv.launcher2.submenu.AllAppMenuDialog;
import com.tcl.simpletv.launcher2.submenu.AppSortModeDialog;
import com.tcl.simpletv.launcher2.toolbar.ToolbarUtilities;
import com.tcl.simpletv.launcher2.utils.ConstantUtil;
import com.tcl.simpletv.launcher2.utils.FlatwiseListener;
import com.tcl.simpletv.launcher2.wallpaper.HorizontalListView;
import com.tcl.simpletv.launcher2.wallpaper.ImageAdapter;
import com.tcl.simpletv.launcher2.wallpaper.WallpaperData;


/**
 * Default launcher application.
 */
public final class Launcher extends Activity
        implements View.OnClickListener, OnLongClickListener, LauncherModel.Callbacks,
                   View.OnTouchListener {
    static final String TAG = "Launcher";
    static final boolean LOGD = false;
    
    /*
     * add by ljianwei;
     * date:2013-8-15
     */
    private final int DELETE_ITEM = 0;
    private final int UNINSTALL_ITEM = 1;
    private final int COLLECT_ITEM = 2;
    private final int UNCOLLECT_ITEM = 3;
    private final int SHARE_TTEM = 4;
    private Context mContext;
    private final int SHARE_MSG = 2;
    private static String ITEM_PATH = "/mnt/sdcard/launcherTemp/item.png";
    

    static final boolean PROFILE_STARTUP = false;
    static final boolean DEBUG_WIDGETS = false;
    static final boolean DEBUG_STRICT_MODE = false;   
    
    //Custom menu:local wallPaper/Add/Screen Manager  
    private static final int MENU_GROUP_WALLPAPER = 1;
    
    private static final int REQUEST_CREATE_SHORTCUT = 1;
    private static final int REQUEST_CREATE_APPWIDGET = 5;
    private static final int REQUEST_PICK_APPLICATION = 6;
    private static final int REQUEST_PICK_SHORTCUT = 7;
    private static final int REQUEST_PICK_APPWIDGET = 9;
    private static final int REQUEST_PICK_WALLPAPER = 10;

    private static final int REQUEST_BIND_APPWIDGET = 11;

    static final String EXTRA_SHORTCUT_DUPLICATE = "duplicate";

    static final int SCREEN_COUNT = 0;//5;
    static final int DEFAULT_SCREEN = 0;//2;

    private static final String PREFERENCES = "launcher.preferences";
    static final String FORCE_ENABLE_ROTATION_PROPERTY = "debug.force_enable_rotation";
    static final String DUMP_STATE_PROPERTY = "debug.dumpstate";

    // The Intent extra that defines whether to ignore the launch animation
    
    static final String INTENT_EXTRA_IGNORE_LAUNCH_ANIMATION =
            "com.android.launcher.intent.extra.shortcut.INGORE_LAUNCH_ANIMATION";

    // Type: int
    private static final String RUNTIME_STATE_CURRENT_SCREEN = "launcher.current_screen";
    // Type: int
    private static final String RUNTIME_STATE = "launcher.state";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_CONTAINER = "launcher.add_container";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_SCREEN = "launcher.add_screen";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_CELL_X = "launcher.add_cell_x";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_CELL_Y = "launcher.add_cell_y";
    // Type: boolean
    private static final String RUNTIME_STATE_PENDING_FOLDER_RENAME = "launcher.rename_folder";
    // Type: long
    private static final String RUNTIME_STATE_PENDING_FOLDER_RENAME_ID = "launcher.rename_folder_id";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_SPAN_X = "launcher.add_span_x";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_SPAN_Y = "launcher.add_span_y";
    // Type: parcelable
    private static final String RUNTIME_STATE_PENDING_ADD_WIDGET_INFO = "launcher.add_widget_info";

    private static final String TOOLBAR_ICON_METADATA_NAME = "com.android.launcher.toolbar_icon";
    private static final String TOOLBAR_SEARCH_ICON_METADATA_NAME =
            "com.android.launcher.toolbar_search_icon";
    private static final String TOOLBAR_VOICE_SEARCH_ICON_METADATA_NAME =
            "com.android.launcher.toolbar_voice_search_icon";
    
  /*  private static final String SETWALLPAPER_ACTION =
            "com.tcl.simpletv.launcher2.SETWALLPAPER_ACTION";*/

    /** The different states that Launcher can be in. */
    private enum State { NONE, WORKSPACE, APPS_CUSTOMIZE, APPS_CUSTOMIZE_SPRING_LOADED,EDIT_STATE };
    private static State mState = State.WORKSPACE;
    private AnimatorSet mStateAnimation;
    private AnimatorSet mDividerAnimator;

    static final int APPWIDGET_HOST_ID = 1024;
    private static final int EXIT_SPRINGLOADED_MODE_SHORT_TIMEOUT = 300;
    private static final int EXIT_SPRINGLOADED_MODE_LONG_TIMEOUT = 600;
    private static final int SHOW_CLING_DURATION = 550;
    private static final int DISMISS_CLING_DURATION = 250;

    private static final Object sLock = new Object();
    private static int sScreen = DEFAULT_SCREEN;

    // How long to wait before the new-shortcut animation automatically pans the workspace
    private static int NEW_APPS_ANIMATION_INACTIVE_TIMEOUT_SECONDS = 10;

    private final BroadcastReceiver mCloseSystemDialogsReceiver
            = new CloseSystemDialogsIntentReceiver();
    private final ContentObserver mWidgetObserver = new AppWidgetResetObserver();

    private LayoutInflater mInflater;

    public Workspace mWorkspace;
    
    //hide divider by xiangss 201308014
//    private View mQsbDivider;
//    private View mDockDivider;
    
    public DragLayer mDragLayer;
    private DragController mDragController;

    public ScreenManager mScreenManager;
    
    private AppWidgetManager mAppWidgetManager;
    private LauncherAppWidgetHost mAppWidgetHost;

    private ItemInfo mPendingAddInfo = new ItemInfo();
    private AppWidgetProviderInfo mPendingAddWidgetInfo;

    private int[] mTmpAddItemCellCoordinates = new int[2];

    private FolderInfo mFolderInfo;

    private Hotseat mHotseat;
    private View mAllAppsButton;

    private SearchDropTargetBar mSearchDropTargetBar;
    private AppsCustomizeTabHost mAppsCustomizeTabHost;
    public AppsCustomizePagedView mAppsCustomizeContent;
    /*
     * add by leixp 2013-07-29
     * Edit state show App and widget content 
     * 
     */
    private AppsWidgetCustomizeTabHost mAppsEditCustomizeTabHost;
    private AppsCustomizePagedView mAppsEditCustomizeContent;
    private boolean mAutoAdvanceRunning = false;

    private Bundle mSavedState;
    // We set the state in both onCreate and then onNewIntent in some cases, which causes both
    // scroll issues (because the workspace may not have been measured yet) and extra work.
    // Instead, just save the state that we need to restore Launcher to, and commit it in onResume.
    private State mOnResumeState = State.NONE;

    private SpannableStringBuilder mDefaultKeySsb = null;

    private boolean mWorkspaceLoading = true;

    private boolean mHomeDestroyed = false;
    private boolean mPaused = true;
    private boolean mRestoring;
    private boolean mWaitingForResult;
    private boolean mOnResumeNeedsLoad;

    // Keep track of whether the user has left launcher
    private static boolean sPausedFromUserAction = false;

    private Bundle mSavedInstanceState;

    private LauncherModel mModel;
    private IconCache mIconCache;
    private boolean mUserPresent = true;
    private boolean mVisible = false;
    private boolean mAttached = false;

    private static LocaleConfiguration sLocaleConfiguration = null;

    private static HashMap<Long, FolderInfo> sFolders = new HashMap<Long, FolderInfo>();

    private Intent mAppMarketIntent = null;

    // Related to the auto-advancing of widgets
    private final int ADVANCE_MSG = 1;
    private final int mAdvanceInterval = 20000;
    private final int mAdvanceStagger = 250;
    private long mAutoAdvanceSentTime;
    private long mAutoAdvanceTimeLeft = -1;
    private HashMap<View, AppWidgetProviderInfo> mWidgetsToAdvance =
        new HashMap<View, AppWidgetProviderInfo>();

    // Determines how long to wait after a rotation before restoring the screen orientation to
    // match the sensor state.
    private final int mRestoreScreenOrientationDelay = 500;

    // External icons saved in case of resource changes, orientation, etc.
    private static Drawable.ConstantState[] sGlobalSearchIcon = new Drawable.ConstantState[2];
    private static Drawable.ConstantState[] sVoiceSearchIcon = new Drawable.ConstantState[2];
    private static Drawable.ConstantState[] sAppMarketIcon = new Drawable.ConstantState[2];

    private final ArrayList<Integer> mSynchronouslyBoundPages = new ArrayList<Integer>();

    static final ArrayList<String> sDumpLogs = new ArrayList<String>();

    // We only want to get the SharedPreferences once since it does an FS stat each time we get
    // it from the context.
    private SharedPreferences mSharedPrefs;

    // Holds the page that we need to animate to, and the icon views that we need to animate up
    // when we scroll to that page on resume.
    private int mNewShortcutAnimatePage = -1;
    private ArrayList<View> mNewShortcutAnimateViews = new ArrayList<View>();
    private ImageView mFolderIconImageView;
    private Bitmap mFolderIconBitmap;
    private Canvas mFolderIconCanvas;
    private Rect mRectForFolderAnimation = new Rect();
    
    private LampCord lampCordView;
	private Curtain curtain;

    private BubbleTextView mWaitingForResume;
    
    private FlatwiseListener mFlatwiseListener;	//平放状态监�?    
    /**
     * 壁纸相关变量
     */
    
    //get the related values of wallpaper setting
    private ArrayList<Integer> mThumbs = new ArrayList<Integer>();
    private ArrayList<Integer> mImages = new ArrayList<Integer>();    
    private WallpaperData wallpaperData;  
    //the current wallpaper index
    private int curWallpaperIndex = 0;
    
    private LinearLayout wallpaer_layout;
    
    //壁纸是否显示
    private boolean wallpaperShow = false;  

    private HideFromAccessibilityHelper mHideFromAccessibilityHelper
        = new HideFromAccessibilityHelper();

    private Runnable mBuildLayersRunnable = new Runnable() {
        public void run() {
            if (mWorkspace != null) {
                mWorkspace.buildPageHardwareLayers();
            }
        }
    };

    private static ArrayList<PendingAddArguments> sPendingAddList
            = new ArrayList<PendingAddArguments>();

    private static class PendingAddArguments {
        int requestCode;
        Intent intent;
        long container;
        int screen;
        int cellX;
        int cellY;
    }


    private boolean doesFileExist(String filename) {
        FileInputStream fis = null;
        try {
            fis = openFileInput(filename);
            fis.close();
            return true;
        } catch (java.io.FileNotFoundException e) {
            return false;
        } catch (java.io.IOException e) {
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "---------onCreate-----");
        if (DEBUG_STRICT_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }

        super.onCreate(savedInstanceState);
        mContext = this;
        LauncherApplication app = ((LauncherApplication)getApplication());
        LauncherApplication.setLauncherInstance(this);
//        mSharedPrefs = getSharedPreferences(LauncherApplication.getSharedPreferencesKey(),
//                Context.MODE_PRIVATE);
        mSharedPrefs = LauncherApplication.getPreferenceUtils().getsSharedPreferences();
        
        //开机向导自己判断启动     
//        firstStartup();
        
        LauncherApplication.setLocalWidgetManager(new LocalWidgetManager(this));
        
        mModel = app.setLauncher(this);
        mIconCache = app.getIconCache();
        mDragController = new DragController(this);
        mInflater = getLayoutInflater();

        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetHost = new LauncherAppWidgetHost(this, APPWIDGET_HOST_ID);
        mAppWidgetHost.startListening();

        // If we are getting an onCreate, we can actually preempt onResume and unset mPaused here,
        // this also ensures that any synchronous binding below doesn't re-trigger another
        // LauncherModel load.
        mPaused = false;

        if (PROFILE_STARTUP) {
            android.os.Debug.startMethodTracing(
                    Environment.getExternalStorageDirectory() + "/launcher");
        }

        wallpaperData = new WallpaperData(this);
        wallpaperData.findWallpapers();
        mThumbs = wallpaperData.getmThumbs();
        mImages = wallpaperData.getmImages();
        
        checkForLocaleChange();
        setContentView(R.layout.launcher);
        setupViews();
        
        //显示状态栏子菜单图�?
        //        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NEEDS_MENU_KEY,
        //                WindowManager.LayoutParams.FLAG_NEEDS_MENU_KEY); 
        getWindow().setFlags(0x08000000, 0x08000000);

        
        // hide cling by xiangss 20130821 
//        showFirstRunWorkspaceCling();

        registerContentObservers();

        lockAllApps();

        mSavedState = savedInstanceState;
        restoreState(mSavedState);

        // Update customization drawer _after_ restoring the states
        if (mAppsCustomizeContent != null) {
            mAppsCustomizeContent.onPackagesUpdated();
        }
        // Update Edit state customization drawer _after_ restoring the states
        if (mAppsEditCustomizeContent != null) {
        	mAppsEditCustomizeContent.onPackagesUpdated();
        }

        if (PROFILE_STARTUP) {
            android.os.Debug.stopMethodTracing();
        }

        if (!mRestoring) {
            if (sPausedFromUserAction) {
                // If the user leaves launcher, then we should just load items asynchronously when
                // they return.
                mModel.startLoader(true, -1);
            } else {
                // We only load the page synchronously if the user rotates (or triggers a
                // configuration change) while launcher is in the foreground
                mModel.startLoader(true, mWorkspace.getCurrentPage());
            }
        }

        if (!mModel.isAllAppsLoaded()) {
            ViewGroup appsCustomizeContentParent = (ViewGroup) mAppsCustomizeContent.getParent();
            mInflater.inflate(R.layout.apps_customize_progressbar, appsCustomizeContentParent);
        }

        // For handling default keys
        mDefaultKeySsb = new SpannableStringBuilder();
        Selection.setSelection(mDefaultKeySsb, 0);

        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mCloseSystemDialogsReceiver, filter);

        updateGlobalIcons();

        // On large interfaces, we want the screen to auto-rotate based on the current orientation
        unlockScreenOrientation(true);

        DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
	    getWindowManager().getDefaultDisplay().getMetrics(dm);
		curtain = new Curtain(this,dm.widthPixels);
//		curtain.setViewVisible(true);
		lampCordView = new LampCord(this,dm.widthPixels);
//		lampCordView.setViewVisible(true);
		lampCordView.setCurtain(curtain);

		
		mFlatwiseListener = new FlatwiseListener(this);		
		
       //add for systembar button goto all app page. by xssdmx 2013-07-24		
        if(getIntent().getBooleanExtra("goto_all_APP", false) == true){
        	Log.d(TAG, "onCreate-----goto_all_APP");
        	getIntent().putExtra("goto_all_APP", false);	//clean goto app flag，否则转屏会有影�?
        	showAllApps(false);        	
        }
                  
    }
        
    
    
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	
    	if(mState == State.WORKSPACE){
	    	curtain.setViewVisible(true);
	    	lampCordView.setViewVisible(true);
    	}
    }

    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	curtain.setViewVisible(false);
    	lampCordView.setViewVisible(false);
    }
    
    

    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        sPausedFromUserAction = true;
    }

    private void updateGlobalIcons() {
        boolean searchVisible = false;
        boolean voiceVisible = false;
        // If we have a saved version of these external icons, we load them up immediately
        int coi = getCurrentOrientationIndexForGlobalIcons();
        if (sGlobalSearchIcon[coi] == null || sVoiceSearchIcon[coi] == null ||
                sAppMarketIcon[coi] == null) {
          //  updateAppMarketIcon();
            searchVisible = updateGlobalSearchIcon();
            voiceVisible = updateVoiceSearchIcon(searchVisible);
        }
        if (sGlobalSearchIcon[coi] != null) {
             updateGlobalSearchIcon(sGlobalSearchIcon[coi]);
             searchVisible = true;
        }
        if (sVoiceSearchIcon[coi] != null) {
            updateVoiceSearchIcon(sVoiceSearchIcon[coi]);
            voiceVisible = true;
        }
        if (sAppMarketIcon[coi] != null) {
            updateAppMarketIcon(sAppMarketIcon[coi]);
        }
        if (mSearchDropTargetBar != null) {
            mSearchDropTargetBar.onSearchPackagesChanged(searchVisible, voiceVisible);
        }
    }

    private void checkForLocaleChange() {
        if (sLocaleConfiguration == null) {
            new AsyncTask<Void, Void, LocaleConfiguration>() {
                @Override
                protected LocaleConfiguration doInBackground(Void... unused) {
                    LocaleConfiguration localeConfiguration = new LocaleConfiguration();
                    readConfiguration(Launcher.this, localeConfiguration);
                    return localeConfiguration;
                }

                @Override
                protected void onPostExecute(LocaleConfiguration result) {
                    sLocaleConfiguration = result;
                    checkForLocaleChange();  // recursive, but now with a locale configuration
                }
            }.execute();
            return;
        }

        final Configuration configuration = getResources().getConfiguration();

        final String previousLocale = sLocaleConfiguration.locale;
        final String locale = configuration.locale.toString();

        final int previousMcc = sLocaleConfiguration.mcc;
        final int mcc = configuration.mcc;

        final int previousMnc = sLocaleConfiguration.mnc;
        final int mnc = configuration.mnc;

        boolean localeChanged = !locale.equals(previousLocale) || mcc != previousMcc || mnc != previousMnc;

        if (localeChanged) {
            sLocaleConfiguration.locale = locale;
            sLocaleConfiguration.mcc = mcc;
            sLocaleConfiguration.mnc = mnc;

            mIconCache.flush();

            final LocaleConfiguration localeConfiguration = sLocaleConfiguration;
            new Thread("WriteLocaleConfiguration") {
                @Override
                public void run() {
                    writeConfiguration(Launcher.this, localeConfiguration);
                }
            }.start();
        }
    }

    private static class LocaleConfiguration {
        public String locale;
        public int mcc = -1;
        public int mnc = -1;
    }

    private static void readConfiguration(Context context, LocaleConfiguration configuration) {
        DataInputStream in = null;
        try {
            in = new DataInputStream(context.openFileInput(PREFERENCES));
            configuration.locale = in.readUTF();
            configuration.mcc = in.readInt();
            configuration.mnc = in.readInt();
        } catch (FileNotFoundException e) {
            // Ignore
        } catch (IOException e) {
            // Ignore
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    private static void writeConfiguration(Context context, LocaleConfiguration configuration) {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(context.openFileOutput(PREFERENCES, MODE_PRIVATE));
            out.writeUTF(configuration.locale);
            out.writeInt(configuration.mcc);
            out.writeInt(configuration.mnc);
            out.flush();
        } catch (FileNotFoundException e) {
            // Ignore
        } catch (IOException e) {
            //noinspection ResultOfMethodCallIgnored
            context.getFileStreamPath(PREFERENCES).delete();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    public DragLayer getDragLayer() {
        return mDragLayer;
    }

    boolean isDraggingEnabled() {
        // We prevent dragging when we are loading the workspace as it is possible to pick up a view
        // that is subsequently removed from the workspace in startBinding().
        return !mModel.isLoadingWorkspace();
    }

    static int getScreen() {
        synchronized (sLock) {
            return sScreen;
        }
    }

    static void setScreen(int screen) {
        synchronized (sLock) {
            sScreen = screen;
        }
    }

    /**
     * Returns whether we should delay spring loaded mode -- for shortcuts and widgets that have
     * a configuration step, this allows the proper animations to run after other transitions.
     */
    private boolean completeAdd(PendingAddArguments args) {
        boolean result = false;
        switch (args.requestCode) {
            case REQUEST_PICK_APPLICATION:
                completeAddApplication(args.intent, args.container, args.screen, args.cellX,
                        args.cellY);
                break;
            case REQUEST_PICK_SHORTCUT:
                processShortcut(args.intent);
                break;
            case REQUEST_CREATE_SHORTCUT:
                completeAddShortcut(args.intent, args.container, args.screen, args.cellX,
                        args.cellY);
                result = true;
                break;
            case REQUEST_CREATE_APPWIDGET:
                int appWidgetId = args.intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
                completeAddAppWidget(appWidgetId, args.container, args.screen, null, null);
                result = true;
                break;
            case REQUEST_PICK_WALLPAPER:
                // We just wanted the activity result here so we can clear mWaitingForResult
                break;
        }
        // Before adding this resetAddInfo(), after a shortcut was added to a workspace screen,
        // if you turned the screen off and then back while in All Apps, Launcher would not
        // return to the workspace. Clearing mAddInfo.container here fixes this issue
        resetAddInfo();
        return result;
    }

    @Override
    protected void onActivityResult(
            final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == REQUEST_BIND_APPWIDGET) {
            int appWidgetId = data != null ?
                    data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) : -1;
            if (resultCode == RESULT_CANCELED) {
                completeTwoStageWidgetDrop(RESULT_CANCELED, appWidgetId);
            } else if (resultCode == RESULT_OK) {
                addAppWidgetImpl(appWidgetId, mPendingAddInfo, null, mPendingAddWidgetInfo);
            }
            return;
        }
        boolean delayExitSpringLoadedMode = false;
        boolean isWidgetDrop = (requestCode == REQUEST_PICK_APPWIDGET ||
                requestCode == REQUEST_CREATE_APPWIDGET);
        mWaitingForResult = false;

        // We have special handling for widgets
        if (isWidgetDrop) {
            int appWidgetId = data != null ?
                    data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1) : -1;
            if (appWidgetId < 0) {
                Log.e(TAG, "Error: appWidgetId (EXTRA_APPWIDGET_ID) was not returned from the \\" +
                        "widget configuration activity.");
                completeTwoStageWidgetDrop(RESULT_CANCELED, appWidgetId);
            } else {
                completeTwoStageWidgetDrop(resultCode, appWidgetId);
            }
            return;
        }

        // The pattern used here is that a user PICKs a specific application,
        // which, depending on the target, might need to CREATE the actual target.

        // For example, the user would PICK_SHORTCUT for "Music playlist", and we
        // launch over to the Music app to actually CREATE_SHORTCUT.
        if (resultCode == RESULT_OK && mPendingAddInfo.container != ItemInfo.NO_ID) {
            final PendingAddArguments args = new PendingAddArguments();
            args.requestCode = requestCode;
            args.intent = data;
            args.container = mPendingAddInfo.container;
            args.screen = mPendingAddInfo.screen;
            args.cellX = mPendingAddInfo.cellX;
            args.cellY = mPendingAddInfo.cellY;
            if (isWorkspaceLocked()) {
                sPendingAddList.add(args);
            } else {
                delayExitSpringLoadedMode = completeAdd(args);
            }
        }
        mDragLayer.clearAnimatedView();
        // Exit spring loaded mode if necessary after cancelling the configuration of a widget
        exitSpringLoadedDragModeDelayed((resultCode != RESULT_CANCELED), delayExitSpringLoadedMode,
                null);
    }

    private void completeTwoStageWidgetDrop(final int resultCode, final int appWidgetId) {
        CellLayout cellLayout =
                (CellLayout) mWorkspace.getChildAt(mPendingAddInfo.screen);
        Runnable onCompleteRunnable = null;
        int animationType = 0;

        AppWidgetHostView boundWidget = null;
        if (resultCode == RESULT_OK) {
            animationType = Workspace.COMPLETE_TWO_STAGE_WIDGET_DROP_ANIMATION;
            final AppWidgetHostView layout = mAppWidgetHost.createView(this, appWidgetId,
                    mPendingAddWidgetInfo);
            boundWidget = layout;
            onCompleteRunnable = new Runnable() {
                @Override
                public void run() {
                    completeAddAppWidget(appWidgetId, mPendingAddInfo.container,
                            mPendingAddInfo.screen, layout, null);
                    exitSpringLoadedDragModeDelayed((resultCode != RESULT_CANCELED), false,
                            null);
                }
            };
        } else if (resultCode == RESULT_CANCELED) {
            animationType = Workspace.CANCEL_TWO_STAGE_WIDGET_DROP_ANIMATION;
            onCompleteRunnable = new Runnable() {
                @Override
                public void run() {
                    exitSpringLoadedDragModeDelayed((resultCode != RESULT_CANCELED), false,
                            null);
                }
            };
        }
        if (mDragLayer.getAnimatedView() != null) {
            mWorkspace.animateWidgetDrop(mPendingAddInfo, cellLayout,
                    (DragView) mDragLayer.getAnimatedView(), onCompleteRunnable,
                    animationType, boundWidget, true);
        } else {
            // The animated view may be null in the case of a rotation during widget configuration
            onCompleteRunnable.run();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mFlatwiseListener.registerGSensorListener();
        
        // Restore the previous launcher state
        if (mOnResumeState == State.WORKSPACE) {
            showWorkspace(false);
        } else if (mOnResumeState == State.APPS_CUSTOMIZE) {
            showAllApps(false);
        }
        mOnResumeState = State.NONE;

        // Process any items that were added while Launcher was away
        InstallShortcutReceiver.flushInstallQueue(this);

        mPaused = false;
        sPausedFromUserAction = false;
        if (mRestoring || mOnResumeNeedsLoad) {
            mWorkspaceLoading = true;
            mModel.startLoader(true, -1);
            mRestoring = false;
            mOnResumeNeedsLoad = false;
        }

        // Reset the pressed state of icons that were locked in the press state while activities
        // were launching
        if (mWaitingForResume != null) {
            // Resets the previous workspace icon press state
            mWaitingForResume.setStayPressed(false);
        }
        if (mAppsCustomizeContent != null) {
            // Resets the previous all apps icon press state
            mAppsCustomizeContent.resetDrawableState();
        }
        if (mAppsEditCustomizeContent != null) {
            // Resets the previous all apps icon press state Edit state
        	mAppsEditCustomizeContent.resetDrawableState();
        }
        // It is possible that widgets can receive updates while launcher is not in the foreground.
        // Consequently, the widgets will be inflated in the orientation of the foreground activity
        // (framework issue). On resuming, we ensure that any widgets are inflated for the current
        // orientation.
        getWorkspace().reinflateWidgetsIfNecessary();

        // Again, as with the above scenario, it's possible that one or more of the global icons
        // were updated in the wrong orientation.
        updateGlobalIcons();
    }

    @Override
    protected void onPause() {
        // NOTE: We want all transitions from launcher to act as if the wallpaper were enabled
        // to be consistent.  So re-enable the flag here, and we will re-disable it as necessary
        // when Launcher resumes and we are still in AllApps.
        updateWallpaperVisibility(true);

        super.onPause();
        mPaused = true;
        mDragController.cancelDrag();
        mDragController.resetLastGestureUpTime();
        
        mFlatwiseListener.unregisterGSensorListener();
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        // Flag the loader to stop early before switching
        mModel.stopLoader();
        if (mAppsCustomizeContent != null) {
            mAppsCustomizeContent.surrender();
        }
        if (mAppsEditCustomizeContent != null) {
        	mAppsEditCustomizeContent.surrender();
        }
        return Boolean.TRUE;
    }

    // We can't hide the IME if it was forced open.  So don't bother
    /*
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            final InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            inputManager.hideSoftInputFromWindow(lp.token, 0, new android.os.ResultReceiver(new
                        android.os.Handler()) {
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            Log.d(TAG, "ResultReceiver got resultCode=" + resultCode);
                        }
                    });
            Log.d(TAG, "called hideSoftInputFromWindow from onWindowFocusChanged");
        }
    }
    */

    private boolean acceptFilter() {
        final InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        return !inputManager.isFullscreenMode();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
   	
    	  //在出现壁纸浏览界面时，按任何键可以隐藏快捷壁纸浏�?
    	if(wallpaperShow){
        	wallpaperShow = false;
        	wallpaer_layout.setVisibility(View.GONE); 
    	}
        	
        final int uniChar = event.getUnicodeChar();
        final boolean handled = super.onKeyDown(keyCode, event);
        final boolean isKeyNotWhitespace = uniChar > 0 && !Character.isWhitespace(uniChar);
        if (!handled && acceptFilter() && isKeyNotWhitespace) {
            boolean gotKey = TextKeyListener.getInstance().onKeyDown(mWorkspace, mDefaultKeySsb,
                    keyCode, event);
            if (gotKey && mDefaultKeySsb != null && mDefaultKeySsb.length() > 0) {
                // something usable has been typed - start a search
                // the typed text will be retrieved and cleared by
                // showSearchDialog()
                // If there are multiple keystrokes before the search dialog takes focus,
                // onSearchRequested() will be called for every keystroke,
                // but it is idempotent, so it's fine.
                return onSearchRequested();
            }
        }

        // Eat the long press event so the keyboard doesn't come up.
        if (keyCode == KeyEvent.KEYCODE_MENU && event.isLongPress()) {
            return true;
        }      
        	
        return handled;
    }

    private String getTypedText() {
        return mDefaultKeySsb.toString();
    }

    private void clearTypedText() {
        mDefaultKeySsb.clear();
        mDefaultKeySsb.clearSpans();
        Selection.setSelection(mDefaultKeySsb, 0);
    }

    /**
     * Given the integer (ordinal) value of a State enum instance, convert it to a variable of type
     * State
     */
    private static State intToState(int stateOrdinal) {
        State state = State.WORKSPACE;
        final State[] stateValues = State.values();
        for (int i = 0; i < stateValues.length; i++) {
            if (stateValues[i].ordinal() == stateOrdinal) {
                state = stateValues[i];
                break;
            }
        }
        return state;
    }

    /**
     * Restores the previous state, if it exists.
     *
     * @param savedState The previous state.
     */
    private void restoreState(Bundle savedState) {
        if (savedState == null) {
            return;
        }

        State state = intToState(savedState.getInt(RUNTIME_STATE, State.WORKSPACE.ordinal()));
        if (state == State.APPS_CUSTOMIZE) {
            mOnResumeState = State.APPS_CUSTOMIZE;
        }

        int currentScreen = savedState.getInt(RUNTIME_STATE_CURRENT_SCREEN, -1);
        if (currentScreen > -1) {
            mWorkspace.setCurrentPage(currentScreen);
        }

        final long pendingAddContainer = savedState.getLong(RUNTIME_STATE_PENDING_ADD_CONTAINER, -1);
        final int pendingAddScreen = savedState.getInt(RUNTIME_STATE_PENDING_ADD_SCREEN, -1);

        if (pendingAddContainer != ItemInfo.NO_ID && pendingAddScreen > -1) {
            mPendingAddInfo.container = pendingAddContainer;
            mPendingAddInfo.screen = pendingAddScreen;
            mPendingAddInfo.cellX = savedState.getInt(RUNTIME_STATE_PENDING_ADD_CELL_X);
            mPendingAddInfo.cellY = savedState.getInt(RUNTIME_STATE_PENDING_ADD_CELL_Y);
            mPendingAddInfo.spanX = savedState.getInt(RUNTIME_STATE_PENDING_ADD_SPAN_X);
            mPendingAddInfo.spanY = savedState.getInt(RUNTIME_STATE_PENDING_ADD_SPAN_Y);
            mPendingAddWidgetInfo = savedState.getParcelable(RUNTIME_STATE_PENDING_ADD_WIDGET_INFO);
            mWaitingForResult = true;
            mRestoring = true;
        }


        boolean renameFolder = savedState.getBoolean(RUNTIME_STATE_PENDING_FOLDER_RENAME, false);
        if (renameFolder) {
            long id = savedState.getLong(RUNTIME_STATE_PENDING_FOLDER_RENAME_ID);
            mFolderInfo = mModel.getFolderById(this, sFolders, id);
            mRestoring = true;
        }


        // Restore the AppsCustomize tab
        if (mAppsCustomizeTabHost != null) {
            String curTab = savedState.getString("apps_customize_currentTab");
            if (curTab != null) {
                mAppsCustomizeTabHost.setContentTypeImmediate(
                        mAppsCustomizeTabHost.getContentTypeForTabTag(curTab));
                mAppsCustomizeContent.loadAssociatedPages(
                        mAppsCustomizeContent.getCurrentPage());
            }

            int currentIndex = savedState.getInt("apps_customize_currentIndex");
            mAppsCustomizeContent.restorePageForIndex(currentIndex);
        }
        // Restore the AppsEditCustomize tab
        if (mAppsEditCustomizeTabHost != null) {
            String curTab = savedState.getString("apps_edit_customize_currentTab");
            if (curTab != null) {
            	mAppsEditCustomizeTabHost.setContentTypeImmediate(
            			mAppsEditCustomizeTabHost.getContentTypeForTabTag(curTab));
            	mAppsEditCustomizeContent.loadAssociatedPages(
            			mAppsEditCustomizeContent.getCurrentPage());
            }
            int currentIndex = savedState.getInt("apps_edit_customize_currentIndex");
            mAppsEditCustomizeContent.restorePageForIndex(currentIndex);
        }
    }
    
    ImageAdapter adapter,pull_wallpaper_adapter;
    TextView local_wallpaper_text,pull_wallpaper_text;
    HorizontalListView listview,pull_wallpaper_listview;

    /**
     * Finds all the views we need and configure them properly.
     */
    private void setupViews() {
        final DragController dragController = mDragController;

        mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
        mWorkspace = (Workspace) mDragLayer.findViewById(R.id.workspace);
        
      //hide divider by xiangss 201308014
//        mQsbDivider = (ImageView) findViewById(R.id.qsb_divider);
//        mDockDivider = (ImageView) findViewById(R.id.dock_divider);       

        // Setup the drag layer
        mDragLayer.setup(this, dragController);
        mDragLayer.setOnClickListener(this);

        // hide hotseat by xiangss 20130805
//        // Setup the hotseat
//        mHotseat = (Hotseat) findViewById(R.id.hotseat);
//        if (mHotseat != null) {
//            mHotseat.setup(this);
//        }

        // Setup the workspace
        mWorkspace.setHapticFeedbackEnabled(false);
        mWorkspace.setOnLongClickListener(this);
        mWorkspace.setup(dragController);
        dragController.addDragListener(mWorkspace);

        LauncherApplication.setWorkspace(mWorkspace);
        
        // Get the search/delete bar
        mSearchDropTargetBar = (SearchDropTargetBar) mDragLayer.findViewById(R.id.qsb_bar);

        // Setup AppsCustomize
        mAppsCustomizeTabHost = (AppsCustomizeTabHost)
                findViewById(R.id.apps_customize_pane);
        mAppsCustomizeContent = (AppsCustomizePagedView)
                mAppsCustomizeTabHost.findViewById(R.id.apps_customize_pane_content);
        mAppsCustomizeContent.setup(this, dragController);
               
  /*      SharedPreferences 
        mSharedPrefs = LauncherApplication.getPreferenceUtils().getsSharedPreferences();
		int cur_sort_index = mSharedPrefs.getInt(ConstantUtil.APP_SORT_MODE, 0);
		mAppsCustomizeContent.AppsCustomizeSort(cur_sort_index);*/
        
        /*
         * add by leixp 2013-07-29
         * Setup 
         */
        mAppsEditCustomizeTabHost = (AppsWidgetCustomizeTabHost)
        		findViewById(R.id.apps_widget_customize_pane);
        mAppsEditCustomizeContent = (AppsCustomizePagedView)
        		mAppsEditCustomizeTabHost.findViewById(R.id.apps_tcl_customize_pane_content);
        mAppsEditCustomizeContent.setup(this, dragController);
        
        // Setup the drag controller (drop targets have to be added in reverse order in priority)
        dragController.setDragScoller(mWorkspace);
        dragController.setScrollView(mDragLayer);
        dragController.setMoveTarget(mWorkspace);
        dragController.addDropTarget(mWorkspace);
        if (mSearchDropTargetBar != null) {
            mSearchDropTargetBar.setup(this, dragController);
        }
        
        
        mScreenManager = (ScreenManager)mDragLayer.findViewById(R.id.screen_manager);
        mScreenManager.setDragController(dragController);
//        dragController.addDropTarget(mScreenManager);		
        
        initWallpaper();	
		     
	     if(!wallpaperShow){
	    	//modified by luoss date:2013/8/26
	 		//隐藏前去掉刚进去的默认焦�?       
	    	adapter.setCurItem(-1);
	        wallpaer_layout.setVisibility(View.GONE);     
	        } 	
    }
    
    private void initWallpaper(){
        //setup the wallpaper_layout  add by luoss
        wallpaer_layout = (LinearLayout)findViewById(R.id.wallpaper_chooser_launcher);
        local_wallpaper_text = (TextView)wallpaer_layout.findViewById(R.id.local_wallpaper_text);
        pull_wallpaper_text = (TextView)wallpaer_layout.findViewById(R.id.pull_wallpaper_text);
        
        local_wallpaper_text.setBackgroundResource(R.drawable.tab_selected_holo);
   //     pull_wallpaper_text.setBackgroundResource(R.drawable.list_item_clear);
        
		listview = (HorizontalListView) wallpaer_layout.findViewById(R.id.listview);
		
		adapter = new ImageAdapter(this);
		adapter.setThumbs(mThumbs);
		
	  	
		//modified by luoss date:2013/8/26
		//去掉刚进去的默认焦点   
	//  curWallpaperIndex = mSharedPrefs.getInt(ConstantUtil.CURRENT_WALLPAPER_ITEM, 0);
		adapter.setCurItem(-1);
		
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				// TODO Auto-generated method stub
                adapter.setCurItem(position);
				
				SharedPreferences.Editor editor = mSharedPrefs.edit();
				editor.putInt(ConstantUtil.CURRENT_WALLPAPER_ITEM, position);
                editor.commit();
				
				selectWallpaper(position);
			//	Toast.makeText(Launcher.this, position + " click!", Toast.LENGTH_SHORT).show();				
			}
		});

		
		
		pull_wallpaper_listview = (HorizontalListView) wallpaer_layout.findViewById(R.id.pull_wallpaper_listview);
		
		pull_wallpaper_adapter = new ImageAdapter(this);
		pull_wallpaper_adapter.setThumbs(mThumbs);
		
	  	
		//modified by luoss date:2013/8/26
		//去掉刚进去的默认焦点   
	//  curWallpaperIndex = mSharedPrefs.getInt(ConstantUtil.CURRENT_WALLPAPER_ITEM, 0);
		pull_wallpaper_adapter.setCurItem(-1);
		
		pull_wallpaper_listview.setAdapter(pull_wallpaper_adapter);
		
		pull_wallpaper_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				// TODO Auto-generated method stub
				pull_wallpaper_adapter.setCurItem(position);
				
				SharedPreferences.Editor editor = mSharedPrefs.edit();
				editor.putInt(ConstantUtil.CURRENT_WALLPAPER_ITEM, position);
                editor.commit();
				
				selectWallpaper(position);
			//	Toast.makeText(Launcher.this, position + " click!", Toast.LENGTH_SHORT).show();				
			}
		});
		 
		//修改两个壁纸textView按下/弹起效果         by luoss    
		local_wallpaper_text.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {				  
				   case KeyEvent.ACTION_UP:
					   local_wallpaper_text.setBackgroundResource(R.drawable.tab_selected_holo);
						/*pull_wallpaper_text.setBackgroundResource(R.drawable.list_item_clear);			
						pull_wallpaper_listview.setVisibility(View.GONE); */   //暂时屏蔽
						listview.setVisibility(View.VISIBLE);
					break;
				   case KeyEvent.ACTION_DOWN:
					   local_wallpaper_text.setBackgroundResource(R.drawable.tab_selected_pressed_holo);
					   break;
				   default:
				   
				    break;
				}

				return false;
			}
		});
		
		//以下为下拉壁纸textView变化按钮，暂时屏蔽  		
		/*pull_wallpaper_text.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {				  
				   case KeyEvent.ACTION_UP:
					   pull_wallpaper_text.setBackgroundResource(R.drawable.tab_selected_holo);
					   local_wallpaper_text.setBackgroundResource(R.drawable.list_item_clear);
						pull_wallpaper_listview.setVisibility(View.VISIBLE);
						listview.setVisibility(View.GONE); 
					break;
				   case KeyEvent.ACTION_DOWN:
					   pull_wallpaper_text.setBackgroundResource(R.drawable.tab_selected_pressed_holo);
					   break;
				   default:
				   
				    break;
				}

				return false;
			}
		});
		*/
		
    }
    
    
    
    //调用系统设置壁纸服务设置壁纸        
    private void selectWallpaper(int position) {
        try {
            WallpaperManager wpm = (WallpaperManager) getSystemService(
                    Context.WALLPAPER_SERVICE);
            wpm.setResource(mImages.get(position));

        } catch (IOException e) {
            Log.e(TAG, "Failed to set wallpaper: " + e);
        }
    }
         
    
    /**
     * Creates a view representing a shortcut.
     *
     * @param info The data structure describing the shortcut.
     *
     * @return A View inflated from R.layout.application.
     */
    View createShortcut(ShortcutInfo info) {
        return createShortcut(R.layout.application,
                (ViewGroup) mWorkspace.getChildAt(mWorkspace.getCurrentPage()), info);
    }

    /**
     * Creates a view representing a shortcut inflated from the specified resource.
     *
     * @param layoutResId The id of the XML layout used to create the shortcut.
     * @param parent The group the shortcut belongs to.
     * @param info The data structure describing the shortcut.
     *
     * @return A View inflated from layoutResId.
     */
    View createShortcut(int layoutResId, ViewGroup parent, ShortcutInfo info) {
        BubbleTextView favorite = (BubbleTextView) mInflater.inflate(layoutResId, parent, false);
        favorite.applyFromShortcutInfo(info, mIconCache);
        favorite.setOnClickListener(this);
        return favorite;
    }

    /**
     * Add an application shortcut to the workspace.
     *
     * @param data The intent describing the application.
     * @param cellInfo The position on screen where to create the shortcut.
     */
    void completeAddApplication(Intent data, long container, int screen, int cellX, int cellY) {
        final int[] cellXY = mTmpAddItemCellCoordinates;
        final CellLayout layout = getCellLayout(container, screen);

        // First we check if we already know the exact location where we want to add this item.
        if (cellX >= 0 && cellY >= 0) {
            cellXY[0] = cellX;
            cellXY[1] = cellY;
        } else if (!layout.findCellForSpan(cellXY, 1, 1)) {
            showOutOfSpaceMessage(isHotseatLayout(layout));
            return;
        }

        final ShortcutInfo info = mModel.getShortcutInfo(getPackageManager(), data, this);

        if (info != null) {
            info.setActivity(data.getComponent(), Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            info.container = ItemInfo.NO_ID;
            mWorkspace.addApplicationShortcut(info, layout, container, screen, cellXY[0], cellXY[1],
                    isWorkspaceLocked(), cellX, cellY);
        } else {
            Log.e(TAG, "Couldn't find ActivityInfo for selected application: " + data);
        }
    }

    /**
     * Add a shortcut to the workspace.
     *
     * @param data The intent describing the shortcut.
     * @param cellInfo The position on screen where to create the shortcut.
     */
    private void completeAddShortcut(Intent data, long container, int screen, int cellX,
            int cellY) {
        int[] cellXY = mTmpAddItemCellCoordinates;
        int[] touchXY = mPendingAddInfo.dropPos;
        CellLayout layout = getCellLayout(container, screen);

        boolean foundCellSpan = false;

        ShortcutInfo info = mModel.infoFromShortcutIntent(this, data, null);
        if (info == null) {
            return;
        }
        final View view = createShortcut(info);

        // First we check if we already know the exact location where we want to add this item.
        if (cellX >= 0 && cellY >= 0) {
            cellXY[0] = cellX;
            cellXY[1] = cellY;
            foundCellSpan = true;

            // If appropriate, either create a folder or add to an existing folder
            if (mWorkspace.createUserFolderIfNecessary(view, container, layout, cellXY, 0,
                    true, null,null)) {
                return;
            }
            DragObject dragObject = new DragObject();
            dragObject.dragInfo = info;
            if (mWorkspace.addToExistingFolderIfNecessary(view, layout, cellXY, 0, dragObject,
                    true)) {
                return;
            }
        } else if (touchXY != null) {
            // when dragging and dropping, just find the closest free spot
            int[] result = layout.findNearestVacantArea(touchXY[0], touchXY[1], 1, 1, cellXY);
            foundCellSpan = (result != null);
        } else {
            foundCellSpan = layout.findCellForSpan(cellXY, 1, 1);
        }

        if (!foundCellSpan) {
            showOutOfSpaceMessage(isHotseatLayout(layout));
            return;
        }

        LauncherModel.addItemToDatabase(this, info, container, screen, cellXY[0], cellXY[1], false);

        if (!mRestoring) {
            mWorkspace.addInScreen(view, container, screen, cellXY[0], cellXY[1], 1, 1,
                    isWorkspaceLocked());
        }
    }

    
    static int[] getSpanForWidget(Context context, int minWidth,
            int minHeight) {
      
        // We want to account for the extra amount of padding that we are adding to the widget
        // to ensure that it gets the full amount of space that it has requested
        int requiredWidth = minWidth;
        int requiredHeight = minHeight;
        return CellLayout.rectToCell(context.getResources(), requiredWidth, requiredHeight, null);
    }

    
    static int[] getSpanForWidget(Context context, ComponentName component, int minWidth,
            int minHeight) {
        Rect padding = AppWidgetHostView.getDefaultPaddingForWidget(context, component, null);
        // We want to account for the extra amount of padding that we are adding to the widget
        // to ensure that it gets the full amount of space that it has requested
        int requiredWidth = minWidth + padding.left + padding.right;
        int requiredHeight = minHeight + padding.top + padding.bottom;
        return CellLayout.rectToCell(context.getResources(), requiredWidth, requiredHeight, null);
    }


    static int[] getSpanForWidget(Context context, LocalWidgetInfo info) {
        return getSpanForWidget(context, info.minWidth, info.minHeight);
    }
    static int[] getMinSpanForWidget(Context context, LocalWidgetInfo info) {
        return getSpanForWidget(context, info.minResizeWidth, info.minResizeHeight);
    }    

    
    static int[] getSpanForWidget(Context context, AppWidgetProviderInfo info) {
        return getSpanForWidget(context, info.provider, info.minWidth, info.minHeight);
    }

    static int[] getMinSpanForWidget(Context context, AppWidgetProviderInfo info) {
        return getSpanForWidget(context, info.provider, info.minResizeWidth, info.minResizeHeight);
    }

    static int[] getSpanForWidget(Context context, PendingAddWidgetInfo info) {
        return getSpanForWidget(context, info.componentName, info.minWidth, info.minHeight);
    }

    static int[] getMinSpanForWidget(Context context, PendingAddWidgetInfo info) {
        return getSpanForWidget(context, info.componentName, info.minResizeWidth,
                info.minResizeHeight);
    }

    /**
     * Add a widget to the workspace.
     *
     * @param appWidgetId The app widget id
     * @param cellInfo The position on screen where to create the widget.
     */
    private void completeAddAppWidget(final int appWidgetId, long container, int screen,
            AppWidgetHostView hostView, AppWidgetProviderInfo appWidgetInfo) {
        if (appWidgetInfo == null) {
            appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        }

        // Calculate the grid spans needed to fit this widget
        CellLayout layout = getCellLayout(container, screen);

        int[] minSpanXY = getMinSpanForWidget(this, appWidgetInfo);
        int[] spanXY = getSpanForWidget(this, appWidgetInfo);

        // Try finding open space on Launcher screen
        // We have saved the position to which the widget was dragged-- this really only matters
        // if we are placing widgets on a "spring-loaded" screen
        int[] cellXY = mTmpAddItemCellCoordinates;
        int[] touchXY = mPendingAddInfo.dropPos;
        int[] finalSpan = new int[2];
        boolean foundCellSpan = false;
        if (mPendingAddInfo.cellX >= 0 && mPendingAddInfo.cellY >= 0) {
            cellXY[0] = mPendingAddInfo.cellX;
            cellXY[1] = mPendingAddInfo.cellY;
            spanXY[0] = mPendingAddInfo.spanX;
            spanXY[1] = mPendingAddInfo.spanY;
            foundCellSpan = true;
        } else if (touchXY != null) {
            // when dragging and dropping, just find the closest free spot
            int[] result = layout.findNearestVacantArea(
                    touchXY[0], touchXY[1], minSpanXY[0], minSpanXY[1], spanXY[0],
                    spanXY[1], cellXY, finalSpan);
            spanXY[0] = finalSpan[0];
            spanXY[1] = finalSpan[1];
            foundCellSpan = (result != null);
        } else {
            foundCellSpan = layout.findCellForSpan(cellXY, minSpanXY[0], minSpanXY[1]);
        }

        if (!foundCellSpan) {
            if (appWidgetId != -1) {
                // Deleting an app widget ID is a void call but writes to disk before returning
                // to the caller...
                new Thread("deleteAppWidgetId") {
                    public void run() {
                        mAppWidgetHost.deleteAppWidgetId(appWidgetId);
                    }
                }.start();
            }
            showOutOfSpaceMessage(isHotseatLayout(layout));
            return;
        }

        // Build Launcher-specific widget info and save to database
        LauncherAppWidgetInfo launcherInfo = new LauncherAppWidgetInfo(appWidgetId,
                appWidgetInfo.provider);
        launcherInfo.spanX = spanXY[0];
        launcherInfo.spanY = spanXY[1];
        launcherInfo.minSpanX = mPendingAddInfo.minSpanX;
        launcherInfo.minSpanY = mPendingAddInfo.minSpanY;

        LauncherModel.addItemToDatabase(this, launcherInfo,
                container, screen, cellXY[0], cellXY[1], false);

        if (!mRestoring) {
            if (hostView == null) {
                // Perform actual inflation because we're live
                launcherInfo.hostView = mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
                launcherInfo.hostView.setAppWidget(appWidgetId, appWidgetInfo);
            } else {
                // The AppWidgetHostView has already been inflated and instantiated
                launcherInfo.hostView = hostView;
            }

            launcherInfo.hostView.setTag(launcherInfo);
            launcherInfo.hostView.setVisibility(View.VISIBLE);
            launcherInfo.notifyWidgetSizeChanged(this);

            mWorkspace.addInScreen(launcherInfo.hostView, container, screen, cellXY[0], cellXY[1],
                    launcherInfo.spanX, launcherInfo.spanY, isWorkspaceLocked());

            addWidgetToAutoAdvanceIfNeeded(launcherInfo.hostView, appWidgetInfo);
        }
        resetAddInfo();
    }

    private void completeAddLocalWidget(final int appWidgetId, long container, int screen,
            LocalWidgetView hostView, LocalWidgetInfo appWidgetInfo) {
       
        // Calculate the grid spans needed to fit this widget
        CellLayout layout = getCellLayout(container, screen);

        int[] minSpanXY = getMinSpanForWidget(this, appWidgetInfo);
        int[] spanXY = getSpanForWidget(this, appWidgetInfo);

        // Try finding open space on Launcher screen
        // We have saved the position to which the widget was dragged-- this really only matters
        // if we are placing widgets on a "spring-loaded" screen
        int[] cellXY = mTmpAddItemCellCoordinates;
        int[] touchXY = mPendingAddInfo.dropPos;
        int[] finalSpan = new int[2];
        boolean foundCellSpan = false;
        if (mPendingAddInfo.cellX >= 0 && mPendingAddInfo.cellY >= 0) {
            cellXY[0] = mPendingAddInfo.cellX;
            cellXY[1] = mPendingAddInfo.cellY;
            spanXY[0] = mPendingAddInfo.spanX;
            spanXY[1] = mPendingAddInfo.spanY;
            foundCellSpan = true;
        } else if (touchXY != null) {
            // when dragging and dropping, just find the closest free spot
            int[] result = layout.findNearestVacantArea(
                    touchXY[0], touchXY[1], minSpanXY[0], minSpanXY[1], spanXY[0],
                    spanXY[1], cellXY, finalSpan);
            spanXY[0] = finalSpan[0];
            spanXY[1] = finalSpan[1];
            foundCellSpan = (result != null);
        } else {
            foundCellSpan = layout.findCellForSpan(cellXY, minSpanXY[0], minSpanXY[1]);
        }

        if (!foundCellSpan) {           
            showOutOfSpaceMessage(isHotseatLayout(layout));
            return;
        }

        // Build Launcher-specific widget info and save to database
        LauncherAppWidgetInfo launcherInfo = new LauncherAppWidgetInfo(appWidgetId,
                appWidgetInfo.provider);
        launcherInfo.spanX = spanXY[0];
        launcherInfo.spanY = spanXY[1];
        launcherInfo.minSpanX = mPendingAddInfo.minSpanX;
        launcherInfo.minSpanY = mPendingAddInfo.minSpanY;

        LauncherModel.addItemToDatabase(this, launcherInfo,
                container, screen, cellXY[0], cellXY[1], false);

        if (!mRestoring) {
            if (hostView != null) {

                // The AppWidgetHostView has already been inflated and instantiated
                launcherInfo.localWidgetView = hostView;
                Log.e(TAG, "@@@@@@@@@@@@@@@@@" + hostView.getParent().getClass());
                
                mDragLayer.removeView(hostView);
            }

            launcherInfo.localWidgetView.setTag(launcherInfo);
            launcherInfo.localWidgetView.setVisibility(View.VISIBLE);
//            launcherInfo.notifyWidgetSizeChanged(this);

            launcherInfo.localWidgetView.show();
            
            mWorkspace.addInScreen(launcherInfo.localWidgetView, container, screen, cellXY[0], cellXY[1],
                    launcherInfo.spanX, launcherInfo.spanY, isWorkspaceLocked());
            
        }
        resetAddInfo();
    }
    

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                mUserPresent = false;
                mDragLayer.clearAllResizeFrames();
                updateRunning();

                // Reset AllApps to its initial state only if we are not in the middle of
                // processing a multi-step drop
                if (mAppsCustomizeTabHost != null && mPendingAddInfo.container == ItemInfo.NO_ID) {
                    mAppsCustomizeTabHost.reset();
                    showWorkspace(false);
                }
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                mUserPresent = true;
                updateRunning();
            }
        }
    };

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Listen for broadcasts related to user-presence
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mReceiver, filter);

        mAttached = true;
        mVisible = true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVisible = false;

        if (mAttached) {
            unregisterReceiver(mReceiver);
            mAttached = false;
        }
        updateRunning();
    }

    public void onWindowVisibilityChanged(int visibility) {
        mVisible = visibility == View.VISIBLE;
        updateRunning();
        // The following code used to be in onResume, but it turns out onResume is called when
        // you're in All Apps and click home to go to the workspace. onWindowVisibilityChanged
        // is a more appropriate event to handle
        if (mVisible) {
        	if(isAllAppsVisible()){		
            mAppsCustomizeTabHost.onWindowVisible();
        	}
        	if(isEditState()){
        		mAppsEditCustomizeTabHost.onWindowVisible();
        	}
            if (!mWorkspaceLoading) {
                final ViewTreeObserver observer = mWorkspace.getViewTreeObserver();
                // We want to let Launcher draw itself at least once before we force it to build
                // layers on all the workspace pages, so that transitioning to Launcher from other
                // apps is nice and speedy. Usually the first call to preDraw doesn't correspond to
                // a true draw so we wait until the second preDraw call to be safe
                observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        // We delay the layer building a bit in order to give
                        // other message processing a time to run.  In particular
                        // this avoids a delay in hiding the IME if it was
                        // currently shown, because doing that may involve
                        // some communication back with the app.
                        mWorkspace.postDelayed(mBuildLayersRunnable, 500);

                        observer.removeOnPreDrawListener(this);
                        return true;
                    }
                });
            }
            // When Launcher comes back to foreground, a different Activity might be responsible for
            // the app market intent, so refresh the icon
        //    updateAppMarketIcon();
            clearTypedText();
        }
    }

    private void sendAdvanceMessage(long delay) {
        mHandler.removeMessages(ADVANCE_MSG);
        Message msg = mHandler.obtainMessage(ADVANCE_MSG);
        mHandler.sendMessageDelayed(msg, delay);
        mAutoAdvanceSentTime = System.currentTimeMillis();
    }

    private void updateRunning() {
        boolean autoAdvanceRunning = mVisible && mUserPresent && !mWidgetsToAdvance.isEmpty();
        if (autoAdvanceRunning != mAutoAdvanceRunning) {
            mAutoAdvanceRunning = autoAdvanceRunning;
            if (autoAdvanceRunning) {
                long delay = mAutoAdvanceTimeLeft == -1 ? mAdvanceInterval : mAutoAdvanceTimeLeft;
                sendAdvanceMessage(delay);
            } else {
                if (!mWidgetsToAdvance.isEmpty()) {
                    mAutoAdvanceTimeLeft = Math.max(0, mAdvanceInterval -
                            (System.currentTimeMillis() - mAutoAdvanceSentTime));
                }
                mHandler.removeMessages(ADVANCE_MSG);
                mHandler.removeMessages(0); // Remove messages sent using postDelayed()
            }
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == ADVANCE_MSG) {
                int i = 0;
                for (View key: mWidgetsToAdvance.keySet()) {
                    final View v = key.findViewById(mWidgetsToAdvance.get(key).autoAdvanceViewId);
                    final int delay = mAdvanceStagger * i;
                    if (v instanceof Advanceable) {
                       postDelayed(new Runnable() {
                           public void run() {
                               ((Advanceable) v).advance();
                           }
                       }, delay);
                    }
                    i++;
                }
                sendAdvanceMessage(mAdvanceInterval);
            }else if(msg.what == SHARE_MSG){
            	Intent shareIntent = new Intent(Intent.ACTION_SEND);
				File file = new File(ITEM_PATH);
				shareIntent.putExtra(Intent.EXTRA_STREAM,
						Uri.fromFile(file));
				shareIntent.setType("image/*");
				shareIntent.putExtra(
						Intent.EXTRA_SUBJECT,
						getResources().getString(
								R.string.share_item));
				shareIntent.putExtra(Intent.EXTRA_TEXT,
						"I would like to share this with you...");
				startActivity(Intent.createChooser(shareIntent,
						getTitle()));
            	
            }
        }
    };

    void addWidgetToAutoAdvanceIfNeeded(View hostView, AppWidgetProviderInfo appWidgetInfo) {
        if (appWidgetInfo == null || appWidgetInfo.autoAdvanceViewId == -1) return;
        View v = hostView.findViewById(appWidgetInfo.autoAdvanceViewId);
        if (v instanceof Advanceable) {
            mWidgetsToAdvance.put(hostView, appWidgetInfo);
            ((Advanceable) v).fyiWillBeAdvancedByHostKThx();
            updateRunning();
        }
    }

    void removeWidgetToAutoAdvance(View hostView) {
        if (mWidgetsToAdvance.containsKey(hostView)) {
            mWidgetsToAdvance.remove(hostView);
            updateRunning();
        }
    }

    public void removeAppWidget(LauncherAppWidgetInfo launcherInfo) {
        removeWidgetToAutoAdvance(launcherInfo.hostView);
        launcherInfo.hostView = null;
    }

    void showOutOfSpaceMessage(boolean isHotseatLayout) {
        int strId = (isHotseatLayout ? R.string.hotseat_out_of_space : R.string.out_of_space);
        Toast.makeText(this, getString(strId), Toast.LENGTH_SHORT).show();
    }

    public LauncherAppWidgetHost getAppWidgetHost() {
        return mAppWidgetHost;
    }

    public LauncherModel getModel() {
        return mModel;
    }

    void closeSystemDialogs() {
        getWindow().closeAllPanels();

        // Whatever we were doing is hereby canceled.
        mWaitingForResult = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);//must store the new intent unless getIntent() will return the old one	
        Log.d(TAG, "onNewIntent --- goto_all_APP = " + getIntent().getBooleanExtra("goto_all_APP", false));
        
        // Close the menu
        if (Intent.ACTION_MAIN.equals(intent.getAction())) {
            // also will cancel mWaitingForResult.
            closeSystemDialogs();

            
            //退出卸载模式  
        	if(mAppsCustomizeContent.isUninstallMode() == true){
        		mAppsCustomizeContent.exitUninstallMode();        
        	}
        	
        	//退出屏幕管理模式   
        	if(isScreenManagerMode() == true){
        		closeScreenManager(-1);    
        	}
        	
            
            //add for systembar button goto all app page. by xssdmx 2013-07-24
            if(getIntent().getBooleanExtra("goto_all_APP", false) == true){
            	Log.d(TAG, "onNewIntent-----goto_all_APP");
            	getIntent().putExtra("goto_all_APP", false);	//clean goto app flag，否则转屏会有影�?
            	showAllApps(true);
            	return;
            }
            
            //判断是否进入平放UI 
       		if(getIntent().getBooleanExtra("goto_normal_launcher", false) == true){   			
            	Log.d(TAG, "onNewIntent-----goto_normal_launcher is true.");
            	getIntent().putExtra("goto_normal_launcher", false);	//clean flag   
            	mFlatwiseListener.setCircledeskState(false);
       	    }else{
       	    	if(mFlatwiseListener.isCircledeskState() == true){
       	    		mFlatwiseListener.startCircledeskAction();
       	    		return;
       	    	}
       	    }
            
            
            final boolean alreadyOnHome =
                    ((intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
                        != Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

            Runnable processIntent = new Runnable() {
                public void run() {
                    Folder openFolder = mWorkspace.getOpenFolder();
                    // In all these cases, only animate if we're already on home
                    mWorkspace.exitWidgetResizeMode();
                    if (alreadyOnHome && mState == State.WORKSPACE && !mWorkspace.isTouchActive() &&
                            openFolder == null) {
                        mWorkspace.moveToDefaultScreen(true);
                    }

                    closeFolder();
                    exitSpringLoadedDragMode();

                    // If we are already on home, then just animate back to the workspace,
                    // otherwise, just wait until onResume to set the state back to Workspace
                    if (alreadyOnHome) {
                        showWorkspace(true);
                    } else {
                        mOnResumeState = State.WORKSPACE;
                    }

                    final View v = getWindow().peekDecorView();
                    if (v != null && v.getWindowToken() != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(
                                INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }

                    // Reset AllApps to its initial state
                    if (!alreadyOnHome && mAppsCustomizeTabHost != null) {
                        mAppsCustomizeTabHost.reset();
                    }
                }
            };

            if (alreadyOnHome && !mWorkspace.hasWindowFocus()) {
                // Delay processing of the intent to allow the status bar animation to finish
                // first in order to avoid janky animations.
                mWorkspace.postDelayed(processIntent, 350);
            } else {
                // Process the intent immediately.
                processIntent.run();
            }

        }
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        for (int page: mSynchronouslyBoundPages) {
            mWorkspace.restoreInstanceStateForChild(page);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(RUNTIME_STATE_CURRENT_SCREEN, mWorkspace.getNextPage());
        super.onSaveInstanceState(outState);

        outState.putInt(RUNTIME_STATE, mState.ordinal());
        // We close any open folder since it will not be re-opened, and we need to make sure
        // this state is reflected.
        closeFolder();

        if (mPendingAddInfo.container != ItemInfo.NO_ID && mPendingAddInfo.screen > -1 &&
                mWaitingForResult) {
            outState.putLong(RUNTIME_STATE_PENDING_ADD_CONTAINER, mPendingAddInfo.container);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_SCREEN, mPendingAddInfo.screen);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_CELL_X, mPendingAddInfo.cellX);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_CELL_Y, mPendingAddInfo.cellY);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_SPAN_X, mPendingAddInfo.spanX);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_SPAN_Y, mPendingAddInfo.spanY);
            outState.putParcelable(RUNTIME_STATE_PENDING_ADD_WIDGET_INFO, mPendingAddWidgetInfo);
        }

        if (mFolderInfo != null && mWaitingForResult) {
            outState.putBoolean(RUNTIME_STATE_PENDING_FOLDER_RENAME, true);
            outState.putLong(RUNTIME_STATE_PENDING_FOLDER_RENAME_ID, mFolderInfo.id);
        }

        // Save the current AppsCustomize tab
        if (mAppsCustomizeTabHost != null) {
            String currentTabTag = mAppsCustomizeTabHost.getCurrentTabTag();
            if (currentTabTag != null) {
                outState.putString("apps_customize_currentTab", currentTabTag);
            }
            int currentIndex = mAppsCustomizeContent.getSaveInstanceStateIndex();
            outState.putInt("apps_customize_currentIndex", currentIndex);
        }
        if(mAppsEditCustomizeTabHost != null){
        	 String currentTabTag = mAppsEditCustomizeTabHost.getCurrentTabTag();
             if (currentTabTag != null) {
                 outState.putString("apps_edit_customize_currentTab", currentTabTag);
             }
             int currentIndex = mAppsEditCustomizeContent.getSaveInstanceStateIndex();
             outState.putInt("apps_edit_customize_currentIndex", currentIndex);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mHomeDestroyed = true;
        
        // Remove all pending runnables
        mHandler.removeMessages(ADVANCE_MSG);
        mHandler.removeMessages(0);
        mWorkspace.removeCallbacks(mBuildLayersRunnable);

        // Stop callbacks from LauncherModel
        LauncherApplication app = ((LauncherApplication) getApplication());
        mModel.stopLoader();
        app.setLauncher(null);

        try {
            mAppWidgetHost.stopListening();
        } catch (NullPointerException ex) {
            Log.w(TAG, "problem while stopping AppWidgetHost during Launcher destruction", ex);
        }
        mAppWidgetHost = null;

        mWidgetsToAdvance.clear();

        TextKeyListener.getInstance().release();

        // Disconnect any of the callbacks and drawables associated with ItemInfos on the workspace
        // to prevent leaking Launcher activities on orientation change.
        if (mModel != null) {
            mModel.unbindItemInfosAndClearQueuedBindRunnables();
        }

        getContentResolver().unregisterContentObserver(mWidgetObserver);
        unregisterReceiver(mCloseSystemDialogsReceiver);

        mDragLayer.clearAllResizeFrames();
        ((ViewGroup) mWorkspace.getParent()).removeAllViews();
        mWorkspace.removeAllViews();
        mWorkspace = null;
        mDragController = null;

        LauncherAnimUtils.onDestroyActivity();
    }

    public DragController getDragController() {
        return mDragController;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (requestCode >= 0) mWaitingForResult = true;
        super.startActivityForResult(intent, requestCode);
    }

    /**
     * Indicates that we want global search for this activity by setting the globalSearch
     * argument for {@link #startSearch} to true.
     */
    @Override
    public void startSearch(String initialQuery, boolean selectInitialQuery,
            Bundle appSearchData, boolean globalSearch) {

        showWorkspace(true);

        if (initialQuery == null) {
            // Use any text typed in the launcher as the initial query
            initialQuery = getTypedText();
        }
        if (appSearchData == null) {
            appSearchData = new Bundle();
//            appSearchData.putString(Search.SOURCE, "launcher-search");
        }
        Rect sourceBounds = new Rect();
        if (mSearchDropTargetBar != null) {
            sourceBounds = mSearchDropTargetBar.getSearchBarBounds();
        }

        startGlobalSearch(initialQuery, selectInitialQuery,
            appSearchData, sourceBounds);
    }

    /**
     * Starts the global search activity. This code is a copied from SearchManager
     */
    public void startGlobalSearch(String initialQuery,
            boolean selectInitialQuery, Bundle appSearchData, Rect sourceBounds) {
        final SearchManager searchManager =
            (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName globalSearchActivity = searchManager.getGlobalSearchActivity();
        if (globalSearchActivity == null) {
            Log.w(TAG, "No global search activity found.");
            return;
        }
        Intent intent = new Intent(SearchManager.INTENT_ACTION_GLOBAL_SEARCH);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(globalSearchActivity);
        // Make sure that we have a Bundle to put source in
        if (appSearchData == null) {
            appSearchData = new Bundle();
        } else {
            appSearchData = new Bundle(appSearchData);
        }
        // Set source to package name of app that starts global search, if not set already.
        if (!appSearchData.containsKey("source")) {
            appSearchData.putString("source", getPackageName());
        }
        intent.putExtra(SearchManager.APP_DATA, appSearchData);
        if (!TextUtils.isEmpty(initialQuery)) {
            intent.putExtra(SearchManager.QUERY, initialQuery);
        }
        if (selectInitialQuery) {
            intent.putExtra(SearchManager.EXTRA_SELECT_QUERY, selectInitialQuery);
        }
        intent.setSourceBounds(sourceBounds);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Log.e(TAG, "Global search activity not found: " + globalSearchActivity);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isWorkspaceLocked()) {
            return false;
        }

        super.onCreateOptionsMenu(menu);
        
        menu.add("menu");	// you should add at least one item ,otherwise it will called NullPointException
		return super.onCreateOptionsMenu(menu);
    }    

    
    private Dialog launcher_Menudialog;  	//launcher页面弹出的menu对话框      
    private Dialog allapp_Menudialog;	//所有应用页面弹出的menu对话框        
    
    /**
     * 拦截menu事件，在适当页面显示自己的菜单  
     */
    @Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		// TODO Auto-generated method stub
		//return super.onMenuOpened(featureId, menu);
    	
    	String currentTabTag = mAppsCustomizeTabHost.getCurrentTabTag();
    	Log.e(TAG, "currentTabTag = " + currentTabTag);
   	
//    	boolean allAppsVisible = (mAppsCustomizeTabHost.getVisibility() == View.VISIBLE);
   	
    	if(isAllAppsVisible() == true){
    		
    		if(currentTabTag.equals("APPS") && !mAppsCustomizeContent.isUninstallMode()){	//dispaly only under apps Tabs
    			if (allapp_Menudialog == null) {
    				allapp_Menudialog = new AllAppMenuDialog(this);
    				allapp_Menudialog.show();
        		} else {
        			allapp_Menudialog.show();
        		}	
    		}
    		
    	}else{
    		 //record the state of desktop editing or screen managerment mode in order not to dispaly the menu
    		if(isEditState()){
    			
    			Log.e(TAG, "isEditState = " + isEditState());
    			
    		}else if(isScreenManagerMode()){
    			
    			Log.e(TAG, "isScreenManagerMode = " + isScreenManagerMode());
    			
    		}else{
    			
    			if (launcher_Menudialog == null) {
    				launcher_Menudialog = new LauncherMenuDialog(this);
    				launcher_Menudialog.show();
        		} else {
        			launcher_Menudialog.show();
        		}
    			
    		}    		
    		
    	}	
		
		return false;//返回true，则显示系统menu
	}
    

	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mAppsCustomizeTabHost.isTransitioning()) {
            return false;
        }
        boolean allAppsVisible = (mAppsCustomizeTabHost.getVisibility() == View.VISIBLE);
        menu.setGroupVisible(MENU_GROUP_WALLPAPER, !allAppsVisible);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSearchRequested() {
        startSearch(null, false, null, true);
        // Use a custom animation for launching search
        return true;
    }

    public boolean isWorkspaceLocked() {
        return mWorkspaceLoading || mWaitingForResult;
    }

    private void resetAddInfo() {
        mPendingAddInfo.container = ItemInfo.NO_ID;
        mPendingAddInfo.screen = -1;
        mPendingAddInfo.cellX = mPendingAddInfo.cellY = -1;
        mPendingAddInfo.spanX = mPendingAddInfo.spanY = -1;
        mPendingAddInfo.minSpanX = mPendingAddInfo.minSpanY = -1;
        mPendingAddInfo.dropPos = null;
    }

    void addAppWidgetImpl(final int appWidgetId, ItemInfo info, AppWidgetHostView boundWidget,
            AppWidgetProviderInfo appWidgetInfo) {
        if (appWidgetInfo.configure != null) {
            mPendingAddWidgetInfo = appWidgetInfo;

            // Launch over to configure widget, if needed
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityForResultSafely(intent, REQUEST_CREATE_APPWIDGET);
        } else {
            // Otherwise just add it
            completeAddAppWidget(appWidgetId, info.container, info.screen, boundWidget,
                    appWidgetInfo);
            // Exit spring loaded mode if necessary after adding the widget
            exitSpringLoadedDragModeDelayed(true, false, null);
        }
    }

    void addLocalWidgetImpl(final int appWidgetId, ItemInfo info, LocalWidgetView boundWidget,
            LocalWidgetInfo widgetInfo) {
       
        // Otherwise just add it
    	completeAddLocalWidget(appWidgetId, info.container, info.screen, boundWidget,
                widgetInfo);
        // Exit spring loaded mode if necessary after adding the widget
        exitSpringLoadedDragModeDelayed(true, false, null);
       
    }

    /**
     * Process a shortcut drop.
     *
     * @param componentName The name of the component
     * @param screen The screen where it should be added
     * @param cell The cell it should be added to, optional
     * @param position The location on the screen where it was dropped, optional
     */
    void processShortcutFromDrop(ComponentName componentName, long container, int screen,
            int[] cell, int[] loc) {
        resetAddInfo();
        mPendingAddInfo.container = container;
        mPendingAddInfo.screen = screen;
        mPendingAddInfo.dropPos = loc;

        if (cell != null) {
            mPendingAddInfo.cellX = cell[0];
            mPendingAddInfo.cellY = cell[1];
        }

        Intent createShortcutIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
        createShortcutIntent.setComponent(componentName);
        processShortcut(createShortcutIntent);
    }

    /**
     * Process a widget drop.
     *
     * @param info The PendingAppWidgetInfo of the widget being added.
     * @param screen The screen where it should be added
     * @param cell The cell it should be added to, optional
     * @param position The location on the screen where it was dropped, optional
     */
    void addAppWidgetFromDrop(PendingAddWidgetInfo info, long container, int screen,
            int[] cell, int[] span, int[] loc) {
        resetAddInfo();
        mPendingAddInfo.container = info.container = container;
        mPendingAddInfo.screen = info.screen = screen;
        mPendingAddInfo.dropPos = loc;
        mPendingAddInfo.minSpanX = info.minSpanX;
        mPendingAddInfo.minSpanY = info.minSpanY;

        if (cell != null) {
            mPendingAddInfo.cellX = cell[0];
            mPendingAddInfo.cellY = cell[1];
        }
        if (span != null) {
            mPendingAddInfo.spanX = span[0];
            mPendingAddInfo.spanY = span[1];
        }

        if(info.isLocalWidget == true){
        	
        	addLocalWidgetImpl(info.localWidgetInfo.localWidgetId, info, info.localWidgetView, info.localWidgetInfo);
        	return;
        }
        
        AppWidgetHostView hostView = info.boundWidget;
        int appWidgetId;
        if (hostView != null) {
            appWidgetId = hostView.getAppWidgetId();
            addAppWidgetImpl(appWidgetId, info, hostView, info.info);
        } else {
            // In this case, we either need to start an activity to get permission to bind
            // the widget, or we need to start an activity to configure the widget, or both.
            appWidgetId = getAppWidgetHost().allocateAppWidgetId();
            Bundle options = info.bindOptions;

            boolean success = false;
            if (options != null) {
                success = mAppWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId,
                        info.componentName, options);
            } else {
                success = mAppWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId,
                        info.componentName);
            }
            if (success) {
                addAppWidgetImpl(appWidgetId, info, null, info.info);
            } else {
                mPendingAddWidgetInfo = info.info;
                Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_BIND);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, info.componentName);
                // TODO: we need to make sure that this accounts for the options bundle.
                // intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS, options);
                startActivityForResult(intent, REQUEST_BIND_APPWIDGET);
            }
        }
    }

    void processShortcut(Intent intent) {
        // Handle case where user selected "Applications"
        String applicationName = getResources().getString(R.string.group_applications);
        String shortcutName = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);

        if (applicationName != null && applicationName.equals(shortcutName)) {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
            pickIntent.putExtra(Intent.EXTRA_INTENT, mainIntent);
            pickIntent.putExtra(Intent.EXTRA_TITLE, getText(R.string.title_select_application));
            startActivityForResultSafely(pickIntent, REQUEST_PICK_APPLICATION);
        } else {
            startActivityForResultSafely(intent, REQUEST_CREATE_SHORTCUT);
        }
    }

    void processWallpaper(Intent intent) {
        startActivityForResult(intent, REQUEST_PICK_WALLPAPER);
    }

    FolderIcon addFolder(CellLayout layout, long container, final int screen, int cellX,
            int cellY) {
        final FolderInfo folderInfo = new FolderInfo();
        folderInfo.title = getText(R.string.folder_name);

        // Update the model
        LauncherModel.addItemToDatabase(Launcher.this, folderInfo, container, screen, cellX, cellY,
                false);
        sFolders.put(folderInfo.id, folderInfo);

        // Create the view
        FolderIcon newFolder =
            FolderIcon.fromXml(R.layout.folder_icon, this, layout, folderInfo, mIconCache);
        mWorkspace.addInScreen(newFolder, container, screen, cellX, cellY, 1, 1,
                isWorkspaceLocked());
        return newFolder;
    }

    void removeFolder(FolderInfo folder) {
        sFolders.remove(folder.id);
    }

    private void startWallpaper() {
        showWorkspace(true);
        final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
    //    final Intent pickWallpaper = new Intent(Launcher.ACTION_SET_WALLPAPER);
        
        Intent chooser = Intent.createChooser(pickWallpaper,
                getText(R.string.chooser_wallpaper));
        // NOTE: Adds a configure option to the chooser if the wallpaper supports it
        //       Removed in Eclair MR1
//        WallpaperManager wm = (WallpaperManager)
//                getSystemService(Context.WALLPAPER_SERVICE);
//        WallpaperInfo wi = wm.getWallpaperInfo();
//        if (wi != null && wi.getSettingsActivity() != null) {
//            LabeledIntent li = new LabeledIntent(getPackageName(),
//                    R.string.configure_wallpaper, 0);
//            li.setClassName(wi.getPackageName(), wi.getSettingsActivity());
//            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { li });
//        }
        startActivityForResult(chooser, REQUEST_PICK_WALLPAPER);
        overridePendingTransition(R.anim.activity_enter, 0);	//改变activity切换时飞入动画为淡入动画
    }

    /**
     * Registers various content observers. The current implementation registers
     * only a favorites observer to keep track of the favorites applications.
     */
    private void registerContentObservers() {
        ContentResolver resolver = getContentResolver();
        resolver.registerContentObserver(LauncherProvider.CONTENT_APPWIDGET_RESET_URI,
                true, mWidgetObserver);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
    	
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_HOME:
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (doesFileExist(DUMP_STATE_PROPERTY)) {
                        dumpState();
                        return true;
                    }
                    break;
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_HOME:
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
    	Log.d(TAG, "----onBackPressed----");
    	
    	//�˳�ж��ģ?? 
    	if(mAppsCustomizeContent.isUninstallMode() == true){
    		mAppsCustomizeContent.exitUninstallMode();
    		return;
    	}
    	
        if (isAllAppsVisible()) {
            showWorkspace(true);
        }else if(isEditState()){
        	mAppsCustomizeTabHost.setVisibility(View.GONE);
            showWorkspace(true);
        } else if (mWorkspace.getOpenFolder() != null) {
            Folder openFolder = mWorkspace.getOpenFolder();
            if (openFolder.isEditingName()) {
                openFolder.dismissEditingName();
            } else {
                closeFolder();
            }
        } else if (mScreenManager.isShown() == true) {
        	
        	closeScreenManager(-1);
        	
        } else {
            mWorkspace.exitWidgetResizeMode();

            // Back button is a no-op here, but give at least some feedback for the button press
            mWorkspace.showOutlinesTemporarily();
        }
    }

    /**
     * Re-listen when widgets are reset.
     */
    private void onAppWidgetReset() {
        if (mAppWidgetHost != null) {
            mAppWidgetHost.startListening();
        }
    }

    /**
     * Launches the intent referred by the clicked shortcut.
     *
     * @param v The view representing the clicked shortcut.
     */
    public void onClick(View v) {
        // Make sure that rogue clicks don't get through while allapps is launching, or after the
        // view has detached (it's possible for this to happen if the view is removed mid touch).
    	
    	Log.d(TAG, "onClick------v.id = " + v.getId());
    	Log.d(TAG, "onClick------v.getTag = " + v.getTag());
        if(wallpaperShow){
        	wallpaperShow = false;
        	adapter.setCurItem(-1);
        	wallpaer_layout.setVisibility(View.GONE); 
        }else{
        	
            if (v.getWindowToken() == null) {
                return;
            }

            if (!mWorkspace.isFinishedSwitchingState()) {
                return;
            }

            Object tag = v.getTag();
            if (mState == State. EDIT_STATE) {
                if (tag instanceof FolderInfo) {
                     if (v instanceof FolderIcon) {
                         FolderIcon fi = (FolderIcon) v;
                         handleFolderClick(fi);
                     }
               } else if(tag instanceof ShortcutInfo){
               } else{
                     onBackPressed();
               }
           }else if (tag instanceof ShortcutInfo) {
                // Open shortcut
                final Intent intent = ((ShortcutInfo) tag).intent;
                int[] pos = new int[2];
                v.getLocationOnScreen(pos);
                intent.setSourceBounds(new Rect(pos[0], pos[1],
                        pos[0] + v.getWidth(), pos[1] + v.getHeight()));

                boolean success = startActivitySafely(v, intent, tag);

                if (success && v instanceof BubbleTextView) {
                    mWaitingForResume = (BubbleTextView) v;
                    mWaitingForResume.setStayPressed(true);
                }
            } else if (tag instanceof FolderInfo) {
                if (v instanceof FolderIcon) {
                    FolderIcon fi = (FolderIcon) v;
                    handleFolderClick(fi);
                }
            } else if (v == mAllAppsButton) {
                if (isAllAppsVisible()) {
                    showWorkspace(true);             	
                } else {
                    onClickAllAppsButton(v);
                }
            }        	
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        // this is an intercepted event being forwarded from mWorkspace;
        // clicking anywhere on the workspace causes the customization drawer to slide down
    	Log.d(TAG, "----onTouch-----");
    	showWorkspace(true);
        return false;
    }

    /**
     * Event handler for the search button
     *
     * @param v The view that was clicked.
     */
    public void onClickSearchButton(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        onSearchRequested();
    }

    /**
     * Event handler for the voice button
     *
     * @param v The view that was clicked.
     */
    public void onClickVoiceButton(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        try {
            final SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            ComponentName activityName = searchManager.getGlobalSearchActivity();
            Intent intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (activityName != null) {
                intent.setPackage(activityName.getPackageName());
            }
            startActivity(null, intent, "onClickVoiceButton");
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivitySafely(null, intent, "onClickVoiceButton");
        }
    }

    /**
     * Event handler for the "grid" button that appears on the home screen, which
     * enters all apps mode.
     *
     * @param v The view that was clicked.
     */
    public void onClickAllAppsButton(View v) {
    	Log.d(TAG, "-----onClickAllAppsButton-----");
        showAllApps(true);
    }

    public void onTouchDownAllAppsButton(View v) {
        // Provide the same haptic feedback that the system offers for virtual keys.
    	Log.d(TAG, "-----onTouchDownAllAppsButton-----");
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }

    public void onClickAppMarketButton(View v) {
        if (mAppMarketIntent != null) {
            startActivitySafely(v, mAppMarketIntent, "app market");
        } else {
            Log.e(TAG, "Invalid app market intent.");
        }
    }
    
    
    /**
     * 卸载按钮事件监听    
     * @param v
     */
    public void onClickUninstallButton(View v) {
    	if (mAppsCustomizeContent != null) {
    		mAppsCustomizeContent.onClickUninstallButton(v);
    	}
    	
    }
    

   /**
    * 商店按钮事件监听   (点击弹出已安装的商店)
    * @param v
    */
    public void onClickStoreButton(View v){
    	
    	//Using IntentChooser to open installed market list
    	
    	final Intent pickMarket = new Intent("android.intent.action.MAIN"); 
		pickMarket.addCategory("android.intent.category.APP_MARKET");
        
		Intent market_chooser = Intent.createChooser(pickMarket,getText(R.string.chooser_market));
	       
		startActivity(market_chooser);	
    }
    
    
    void startApplicationDetailsActivity(ComponentName componentName) {
        String packageName = componentName.getPackageName();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivitySafely(null, intent, "startApplicationDetailsActivity");
    }

    void startApplicationUninstallActivity(ApplicationInfo appInfo) {
        if ((appInfo.flags & ApplicationInfo.DOWNLOADED_FLAG) == 0) {
            // System applications cannot be installed. For now, show a toast explaining that.
            // We may give them the option of disabling apps this way.
            int messageId = R.string.uninstall_system_app_text;
            Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
        } else {
            String packageName = appInfo.componentName.getPackageName();
            String className = appInfo.componentName.getClassName();
            Log.d("test", "packageName = "+packageName+" className = "+className);
            Intent intent = new Intent(
                    Intent.ACTION_DELETE, Uri.fromParts("package", packageName, className));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        }
    }

    boolean startActivity(View v, Intent intent, Object tag) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            // Only launch using the new animation if the shortcut has not opted out (this is a
            // private contract between launcher and may be ignored in the future).
            boolean useLaunchAnimation = (v != null) &&
                    !intent.hasExtra(INTENT_EXTRA_IGNORE_LAUNCH_ANIMATION);
            if (useLaunchAnimation) {
                ActivityOptions opts = ActivityOptions.makeScaleUpAnimation(v, 0, 0,
                        v.getMeasuredWidth(), v.getMeasuredHeight());

                startActivity(intent, opts.toBundle());
            } else {
                startActivity(intent);
            }
            return true;
        } catch (SecurityException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Launcher does not have the permission to launch " + intent +
                    ". Make sure to create a MAIN intent-filter for the corresponding activity " +
                    "or use the exported attribute for this activity. "
                    + "tag="+ tag + " intent=" + intent, e);
        }
        return false;
    }

    boolean startActivitySafely(View v, Intent intent, Object tag) {
        boolean success = false;
        try {
            success = startActivity(v, intent, tag);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Unable to launch. tag=" + tag + " intent=" + intent, e);
        }
        return success;
    }

    void startActivityForResultSafely(Intent intent, int requestCode) {
        try {
            startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Launcher does not have the permission to launch " + intent +
                    ". Make sure to create a MAIN intent-filter for the corresponding activity " +
                    "or use the exported attribute for this activity.", e);
        }
    }

    private void handleFolderClick(FolderIcon folderIcon) {
        final FolderInfo info = folderIcon.getFolderInfo();
        Folder openFolder = mWorkspace.getFolderForTag(info);

        // If the folder info reports that the associated folder is open, then verify that
        // it is actually opened. There have been a few instances where this gets out of sync.
        if (info.opened && openFolder == null) {
            Log.d(TAG, "Folder info marked as open, but associated folder is not open. Screen: "
                    + info.screen + " (" + info.cellX + ", " + info.cellY + ")");
            info.opened = false;
        }

        if (!info.opened && !folderIcon.getFolder().isDestroyed()) {
            // Close any open folder
            closeFolder();
            // Open the requested folder
            openFolder(folderIcon);
        } else {
            // Find the open folder...
            int folderScreen;
            if (openFolder != null) {
                folderScreen = mWorkspace.getPageForView(openFolder);
                // .. and close it
                closeFolder(openFolder);
                if (folderScreen != mWorkspace.getCurrentPage()) {
                    // Close any folder open on the current screen
                    closeFolder();
                    // Pull the folder onto this screen
                    openFolder(folderIcon);
                }
            }
        }
    }

    /**
     * This method draws the FolderIcon to an ImageView and then adds and positions that ImageView
     * in the DragLayer in the exact absolute location of the original FolderIcon.
     */
    private void copyFolderIconToImage(FolderIcon fi) {
        final int width = fi.getMeasuredWidth();
        final int height = fi.getMeasuredHeight();

        // Lazy load ImageView, Bitmap and Canvas
        if (mFolderIconImageView == null) {
            mFolderIconImageView = new ImageView(this);
        }
        if (mFolderIconBitmap == null || mFolderIconBitmap.getWidth() != width ||
                mFolderIconBitmap.getHeight() != height) {
            mFolderIconBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mFolderIconCanvas = new Canvas(mFolderIconBitmap);
        }

        DragLayer.LayoutParams lp;
        if (mFolderIconImageView.getLayoutParams() instanceof DragLayer.LayoutParams) {
            lp = (DragLayer.LayoutParams) mFolderIconImageView.getLayoutParams();
        } else {
            lp = new DragLayer.LayoutParams(width, height);
        }

        // The layout from which the folder is being opened may be scaled, adjust the starting
        // view size by this scale factor.
        float scale = mDragLayer.getDescendantRectRelativeToSelf(fi, mRectForFolderAnimation);
        lp.customPosition = true;
        lp.x = mRectForFolderAnimation.left;
        lp.y = mRectForFolderAnimation.top;
        lp.width = (int) (scale * width);
        lp.height = (int) (scale * height);

        mFolderIconCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        fi.draw(mFolderIconCanvas);
        mFolderIconImageView.setImageBitmap(mFolderIconBitmap);
        if (fi.getFolder() != null) {
            mFolderIconImageView.setPivotX(fi.getFolder().getPivotXForIconAnimation());
            mFolderIconImageView.setPivotY(fi.getFolder().getPivotYForIconAnimation());
        }
        // Just in case this image view is still in the drag layer from a previous animation,
        // we remove it and re-add it.
        if (mDragLayer.indexOfChild(mFolderIconImageView) != -1) {
            mDragLayer.removeView(mFolderIconImageView);
        }
        mDragLayer.addView(mFolderIconImageView, lp);
        if (fi.getFolder() != null) {
            fi.getFolder().bringToFront();
        }
    }

    private void growAndFadeOutFolderIcon(FolderIcon fi) {
        if (fi == null) return;
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.5f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.5f);

        FolderInfo info = (FolderInfo) fi.getTag();
        if (info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            CellLayout cl = (CellLayout) fi.getParent().getParent();
            CellLayout.LayoutParams lp = (CellLayout.LayoutParams) fi.getLayoutParams();
            cl.setFolderLeaveBehindCell(lp.cellX, lp.cellY);
        }

        // Push an ImageView copy of the FolderIcon into the DragLayer and hide the original
        copyFolderIconToImage(fi);
        fi.setVisibility(View.INVISIBLE);

        ObjectAnimator oa = LauncherAnimUtils.ofPropertyValuesHolder(mFolderIconImageView, alpha,
                scaleX, scaleY);
        oa.setDuration(getResources().getInteger(R.integer.config_folderAnimDuration));
        oa.start();
    }

    private void shrinkAndFadeInFolderIcon(final FolderIcon fi) {
        if (fi == null) return;
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 1.0f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f);

        final CellLayout cl = (CellLayout) fi.getParent().getParent();

        // We remove and re-draw the FolderIcon in-case it has changed
        mDragLayer.removeView(mFolderIconImageView);
        copyFolderIconToImage(fi);
        ObjectAnimator oa = LauncherAnimUtils.ofPropertyValuesHolder(mFolderIconImageView, alpha,
                scaleX, scaleY);
        oa.setDuration(getResources().getInteger(R.integer.config_folderAnimDuration));
        oa.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (cl != null) {
                    cl.clearFolderLeaveBehind();
                    // Remove the ImageView copy of the FolderIcon and make the original visible.
                    mDragLayer.removeView(mFolderIconImageView);
                    fi.setVisibility(View.VISIBLE);
                }
            }
        });
        oa.start();
    }

    /**
     * Opens the user folder described by the specified tag. The opening of the folder
     * is animated relative to the specified View. If the View is null, no animation
     * is played.
     *
     * @param folderInfo The FolderInfo describing the folder to open.
     */
    public void openFolder(FolderIcon folderIcon) {
        Folder folder = folderIcon.getFolder();
        FolderInfo info = folder.mInfo;

        info.opened = true;

        // Just verify that the folder hasn't already been added to the DragLayer.
        // There was a one-off crash where the folder had a parent already.
        if (folder.getParent() == null) {
            mDragLayer.addView(folder);
            mDragController.addDropTarget((DropTarget) folder);
        } else {
            Log.w(TAG, "Opening folder (" + folder + ") which already has a parent (" +
                    folder.getParent() + ").");
        }
        folder.animateOpen();
        growAndFadeOutFolderIcon(folderIcon);
    }

    public void closeFolder() {
        Folder folder = mWorkspace.getOpenFolder();
        if (folder != null) {
            if (folder.isEditingName()) {
                folder.dismissEditingName();
            }
            closeFolder(folder);

            // Dismiss the folder cling
         // hide cling by xiangss 20130821 
//            dismissFolderCling(null);
        }
    }

    void closeFolder(Folder folder) {
        folder.getInfo().opened = false;

        ViewGroup parent = (ViewGroup) folder.getParent().getParent();
        if (parent != null) {
            FolderIcon fi = (FolderIcon) mWorkspace.getViewForTag(folder.mInfo);
            shrinkAndFadeInFolderIcon(fi);
        }
        folder.animateClosed();
    }

    public boolean onLongClick(View v) {
    	Log.i(TAG,"----in onLongClick--v-->"+v);
        if (!isDraggingEnabled()) return false;
        if (isWorkspaceLocked()) return false;
        if ((mState != State.WORKSPACE) && (mState !=State.EDIT_STATE)) return false;

        if (!(v instanceof CellLayout)) {
            v = (View) v.getParent().getParent();
        }

        resetAddInfo();
        CellLayout.CellInfo longClickCellInfo = (CellLayout.CellInfo) v.getTag();
        // This happens when long clicking an item with the dpad/trackball
        if (longClickCellInfo == null) {
            return true;
        }
        // The hotseat touch handling does not go through Workspace, and we always allow long press
        // on hotseat items.
        final View itemUnderLongClick = longClickCellInfo.cell;
        boolean allowLongPress = isHotseatLayout(v) || mWorkspace.allowLongPress();
        if (allowLongPress && !mDragController.isDragging()) {
            if (itemUnderLongClick == null) {
                // User long pressed on empty space
                mWorkspace.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,
                        HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
               // startWallpaper();
                //取消长按单独弹出壁纸设置，出现三个菜单项（本地壁纸、添加、屏幕）
               /* if (launcher_Menudialog == null) {
        			launcher_Menudialog = new AlertDialog.Builder(this).setView(menuView11).show();
        		} else {
        			launcher_Menudialog.show();
        		}*/
//        		if (launcher_Menudialog == null) {
//        			launcher_Menudialog = new MenuDialog(this);
//        			launcher_Menudialog.show();
//        		} else {
//        			launcher_Menudialog.show();
//        		}
                if(!isEditState()){
                	EditState();
                }             
            } else {
                if (!(itemUnderLongClick instanceof Folder)) {
                    // User long pressed on an item
                	mWorkspace.startDrag(longClickCellInfo);
                	popupDialog(itemUnderLongClick);
                	
                }
            }
        }
        return true;
    }
    
    /*
     * modidied by ljianwei;
     * note:get the corresponding ApplicationInfo from the packagename;
     * date:2013-8-26;
     */
    private ApplicationInfo findApplicationInfo(String packageName) {
    	Log.d("ljianwei", "packageName = "+packageName);
    	ArrayList<ApplicationInfo> data = mModel.getAllAppList().data;
        for (ApplicationInfo info: data) {
            final ComponentName component = info.intent.getComponent();
            if (packageName.equals(component.getPackageName())) {
                return info;
            }
        }
        return null;
    }
    
    
    /*
     * add by ljianwei;
     * note:achieve the popupDialog when long press the shortcut or widget or folder;
     * date:2013-8-22;
     */
	QuickAction popDialog;
	public void popupDialog(final View itemUnderLongClick) {
		ActionItem deleteItem = new ActionItem(DELETE_ITEM, getResources()
				.getString(R.string.delete_item), getResources().getDrawable(
				R.drawable.icon_del));
		ActionItem uninstallItem = new ActionItem(UNINSTALL_ITEM,
				getResources().getString(R.string.unistall_item),
				getResources().getDrawable(R.drawable.icon_uninstall));
		ActionItem collectItem = new ActionItem(COLLECT_ITEM, getResources()
				.getString(R.string.collect_item), getResources().getDrawable(
				R.drawable.icon_collect));
		ActionItem unCollectItem = new ActionItem(UNCOLLECT_ITEM, getResources()
				.getString(R.string.uncollect_item), getResources().getDrawable(
				R.drawable.icon_collect));
		ActionItem shareItem = new ActionItem(SHARE_TTEM, getResources()
				.getString(R.string.share_item), getResources().getDrawable(
				R.drawable.icon_share));

		popDialog = new QuickAction(this);
		popDialog.addPopuItem(deleteItem);
		if (itemUnderLongClick instanceof BubbleTextView) {
			ShortcutInfo appInfo = (ShortcutInfo) itemUnderLongClick.getTag();
		    Log.d("ljianwei", "appInfo = "+appInfo);
		    Log.d("ljianwei", " appInfo.intent =  "+ appInfo.intent);
			ApplicationInfo info = findApplicationInfo(appInfo.getPackageName());
			 Log.d("ljianwei", " info =  "+ info);
			if (info != null) {
				if ((info.flags & ApplicationInfo.DOWNLOADED_FLAG) == 0) {
				} else {
					popDialog.addPopuItem(uninstallItem);
				}
				boolean collected = false;
				for (int i = 0; i < ToolbarUtilities.favoriateList.size(); i++) {
					if (appInfo.getPackageName().equals(
							ToolbarUtilities.favoriateList.get(i))) {
						collected = true;
						break;
					}
				}
				if (collected) {
					popDialog.addPopuItem(unCollectItem);
				} else {
					popDialog.addPopuItem(collectItem);
				}
				popDialog.addPopuItem(shareItem);
			}
		} else if (itemUnderLongClick instanceof FolderIcon) {

		} else if (itemUnderLongClick instanceof AppWidgetHostView) {
			
		}
		popDialog.setOnPopuItemClickListener(new QuickAction.OnPopuItemClickListener() {
				@Override
				public void onItemClick(QuickAction source, int pos,
						int actionId) {
					if (actionId == DELETE_ITEM) {
						mSearchDropTargetBar.getDeleteDropTarget().deleteItem(mDragController.getDragObject());
						mWorkspace.getParentCellLayoutForView(
								itemUnderLongClick).removeView(
								itemUnderLongClick);
						if (itemUnderLongClick instanceof DropTarget) {
							mDragController
									.removeDropTarget((DropTarget) itemUnderLongClick);
						}

					} else if (actionId == UNINSTALL_ITEM) {
						ShortcutInfo appInfo = (ShortcutInfo) itemUnderLongClick
								.getTag();
						ComponentName componentName = appInfo.intent
								.getComponent();
						String packageName = componentName.getPackageName();
						String className = componentName.getClassName();
						Intent intent = new Intent(Intent.ACTION_DELETE,
								Uri.fromParts("package", packageName,
										className));
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
								| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
						startActivity(intent);
					} else if (actionId == SHARE_TTEM) {
						new CreateWidgetTask().execute(itemUnderLongClick);
					 }else if(actionId == COLLECT_ITEM){
						ShortcutInfo appInfo = (ShortcutInfo) itemUnderLongClick
									.getTag();
						ToolbarUtilities.addFavorites(mContext, appInfo.getPackageName());
					
					 }else if(actionId == UNCOLLECT_ITEM){
						 ShortcutInfo appInfo = (ShortcutInfo) itemUnderLongClick
									.getTag();
						ToolbarUtilities.delFavorites(mContext, appInfo.getPackageName());
						
					 }
					popDialog.dismiss();
					popDialog = null;

				}

			});

		popDialog.show(itemUnderLongClick);
	}
	
	

	/*
	 * 异步任务生成item图片
	 */
	class CreateWidgetTask extends AsyncTask<Object, Void, Boolean> {
		View itemView;
		protected Boolean doInBackground(Object... params) {
			itemView = (View) params[0];
			String filePath = ITEM_PATH;
			File itemFile = new File(filePath);
			if (itemView != null) {
				itemView.destroyDrawingCache();
				itemView.setDrawingCacheEnabled(true);
				itemView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
				Bitmap bit = itemView.getDrawingCache();
				Bitmap bitmap = null;
				int size;
				try {
					bitmap = Bitmap.createBitmap(bit);
					bit.recycle();
					bit = null;
					itemView.setDrawingCacheEnabled(false);
					if (itemFile.exists()) {
						Log.i("ljianwei",
								"doInBackground------file is exists. delete!");
						itemFile.delete();
					}
					saveItemBitmap(bitmap, filePath);
				} catch (Exception e) {
					Log.d("ljianwei", "Exception info ========" + e.toString());
					return false;
				}
				return true;
			}
			return false;
		}

		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.i("ljianwei", "GetDataTask result=" + result);
			if (result == true) {
				mHandler.sendEmptyMessage(SHARE_MSG);
			}
		}

	}

	/**
	 * 保存图片
	 * 
	 * @param bitName
	 * @param mBitmap
	 * @throws IOException
	 */
	public void saveItemBitmap(Bitmap mBitmap, String bitName)
			throws IOException {
//		File f = new File("/data/data/launcherTemp/" + bitName + ".png");
		File f = new File(bitName);
		Log.d("ljianwei", "f.getParentFile() = "+f.getParentFile().getAbsolutePath());
		if(!f.getParentFile().exists()){
			f.getParentFile().mkdirs();
		}
		f.createNewFile();
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mBitmap.recycle();
		mBitmap = null;
	}
	
	
	public void disPopupDialog(){
		if(popDialog!=null){
			popDialog.dismiss();
			popDialog = null;
		}
	}
    
    public void EditState(){
    	hideAllOverlayWindow();
    	showEditAllAppWidget(true);
    }

	//display the wallpaper layout
    public 	void ShowWallpaper(){
    	curWallpaperIndex = mSharedPrefs.getInt(ConstantUtil.CURRENT_WALLPAPER_ITEM, 0);		
  		/*adapter.setCurItem(curWallpaperIndex);*/
    	wallpaer_layout.setVisibility(View.VISIBLE);
        wallpaperShow = true;
    }	
    
    
    private AppSortModeDialog appSortDialog;
    public void showSortDialog(){
    	appSortDialog = new AppSortModeDialog(this);
    	appSortDialog.show();
    }
    
    boolean isHotseatLayout(View layout) {
    	
    	// hide hotseat by xiangss 20130805
    	return false;
//        return mHotseat != null && layout != null &&
//                (layout instanceof CellLayout) && (layout == mHotseat.getLayout());
    }
    Hotseat getHotseat() {
        return mHotseat;
    }
    
    AppsWidgetCustomizeTabHost getEditAppsWidgetCustomizeTabHost(){
    	return mAppsEditCustomizeTabHost;
    }
    SearchDropTargetBar getSearchBar() {
        return mSearchDropTargetBar;
    }

    /**
     * Returns the CellLayout of the specified container at the specified screen.
     */
    CellLayout getCellLayout(long container, int screen) {
        if (container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            if (mHotseat != null) {
                return mHotseat.getLayout();
            } else {
                return null;
            }
        } else {
            return (CellLayout) mWorkspace.getChildAt(screen);
        }
    }

    Workspace getWorkspace() {
        return mWorkspace;
    }

    // Now a part of LauncherModel.Callbacks. Used to reorder loading steps.
    public boolean isAllAppsVisible() {
        return (mState == State.APPS_CUSTOMIZE) || (mOnResumeState == State.APPS_CUSTOMIZE);
    }

    public boolean isAllAppsButtonRank(int rank) {
    	
    	// hide hotseat by xiangss 20130805
    	return false;
//        return mHotseat.isAllAppsButtonRank(rank);
        
    }
    // is Edit state  add by leixp 2013-07-29
    public static boolean isEditState(){
    	return (mState == State.EDIT_STATE);
    }

    /**
     * Helper method for the cameraZoomIn/cameraZoomOut animations
     * @param view The view being animated
     * @param state The state that we are moving in or out of (eg. APPS_CUSTOMIZE)
     * @param scaleFactor The scale factor used for the zoom
     */
    private void setPivotsForZoom(View view, float scaleFactor) {
        view.setPivotX(view.getWidth() / 2.0f);
        view.setPivotY(view.getHeight() / 2.0f);
    }

    void disableWallpaperIfInAllApps() {
        // Only disable it if we are in all apps
        if (isAllAppsVisible()) {
            if (mAppsCustomizeTabHost != null &&
                    !mAppsCustomizeTabHost.isTransitioning()) {
            	
            	//need show wallpaper xiangss 20130821
//                updateWallpaperVisibility(false);
            	updateWallpaperVisibility(true);
            }
        }
        if(isEditState()){
        	if(mAppsEditCustomizeTabHost != null && !mAppsEditCustomizeTabHost.isTransitioning()){
        		updateWallpaperVisibility(true);
        	}
        }
    }

    /*
     * 是否设置壁纸可见，壁纸可见的条件之一是窗口属性中的WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER位设置为1
     */
    void updateWallpaperVisibility(boolean visible) {
        int wpflags = visible ? WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER : 0;
        int curflags = getWindow().getAttributes().flags
                & WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER;
        if (wpflags != curflags) {
            getWindow().setFlags(wpflags, WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);
        }
    }

    private void dispatchOnLauncherTransitionPrepare(View v, boolean animated, boolean toWorkspace) {
        if (v instanceof LauncherTransitionable) {
            ((LauncherTransitionable) v).onLauncherTransitionPrepare(this, animated, toWorkspace);
        }
    }

    private void dispatchOnLauncherTransitionStart(View v, boolean animated, boolean toWorkspace) {
        if (v instanceof LauncherTransitionable) {
            ((LauncherTransitionable) v).onLauncherTransitionStart(this, animated, toWorkspace);
        }

        // Update the workspace transition step as well
        dispatchOnLauncherTransitionStep(v, 0f);
    }

    private void dispatchOnLauncherTransitionStep(View v, float t) {
        if (v instanceof LauncherTransitionable) {
            ((LauncherTransitionable) v).onLauncherTransitionStep(this, t);
        }
    }

    private void dispatchOnLauncherTransitionEnd(View v, boolean animated, boolean toWorkspace) {
        if (v instanceof LauncherTransitionable) {
            ((LauncherTransitionable) v).onLauncherTransitionEnd(this, animated, toWorkspace);
        }

        // Update the workspace transition step as well
        dispatchOnLauncherTransitionStep(v, 1f);
    }

    /**
     * Things to test when changing the following seven functions.
     *   - Home from workspace
     *          - from center screen
     *          - from other screens
     *   - Home from all apps
     *          - from center screen
     *          - from other screens
     *   - Back from all apps
     *          - from center screen
     *          - from other screens
     *   - Launch app from workspace and quit
     *          - with back
     *          - with home
     *   - Launch app from all apps and quit
     *          - with back
     *          - with home
     *   - Go to a screen that's not the default, then all
     *     apps, and launch and app, and go back
     *          - with back
     *          -with home
     *   - On workspace, long press power and go back
     *          - with back
     *          - with home
     *   - On all apps, long press power and go back
     *          - with back
     *          - with home
     *   - On workspace, power off
     *   - On all apps, power off
     *   - Launch an app and turn off the screen while in that app
     *          - Go back with home key
     *          - Go back with back key  TODO: make this not go to workspace
     *          - From all apps
     *          - From workspace
     *   - Enter and exit car mode (becuase it causes an extra configuration changed)
     *          - From all apps
     *          - From the center workspace
     *          - From another workspace
     */

    /**
     * Zoom the camera out from the workspace to reveal 'toView'.
     * Assumes that the view to show is anchored at either the very top or very bottom
     * of the screen.
     */
    private void showAppsCustomizeHelper(final boolean animated, final boolean springLoaded) {
        if (mStateAnimation != null) {
            mStateAnimation.cancel();
            mStateAnimation = null;
        }
        final Resources res = getResources();

        final int duration = res.getInteger(R.integer.config_appsCustomizeZoomInTime);
        final int fadeDuration = res.getInteger(R.integer.config_appsCustomizeFadeInTime);
        final float scale = (float) res.getInteger(R.integer.config_appsCustomizeZoomScaleFactor);
        final View fromView = mWorkspace;
        final AppsCustomizeTabHost toView = mAppsCustomizeTabHost;
        final int startDelay =
                res.getInteger(R.integer.config_workspaceAppsCustomizeAnimationStagger);

        setPivotsForZoom(toView, scale);

        // Shrink workspaces away if going to AppsCustomize from workspace
        Animator workspaceAnim =
                mWorkspace.getChangeStateAnimation(Workspace.State.SMALL, animated);

        if (animated) {
            toView.setScaleX(scale);
            toView.setScaleY(scale);
            final LauncherViewPropertyAnimator scaleAnim = new LauncherViewPropertyAnimator(toView);
            scaleAnim.
                scaleX(1f).scaleY(1f).
                setDuration(duration).
                setInterpolator(new Workspace.ZoomOutInterpolator());

            toView.setVisibility(View.VISIBLE);
            toView.setAlpha(0f);
            final ObjectAnimator alphaAnim = ObjectAnimator
                .ofFloat(toView, "alpha", 0f, 1f)
                .setDuration(fadeDuration);
            alphaAnim.setInterpolator(new DecelerateInterpolator(1.5f));
            alphaAnim.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (animation == null) {
                        throw new RuntimeException("animation is null");
                    }
                    float t = (Float) animation.getAnimatedValue();
                    dispatchOnLauncherTransitionStep(fromView, t);
                    dispatchOnLauncherTransitionStep(toView, t);
                }
            });

            // toView should appear right at the end of the workspace shrink
            // animation
            mStateAnimation = LauncherAnimUtils.createAnimatorSet();
            mStateAnimation.play(scaleAnim).after(startDelay);
            mStateAnimation.play(alphaAnim).after(startDelay);            
                        
            mStateAnimation.addListener(new AnimatorListenerAdapter() {
                boolean animationCancelled = false;

                @Override
                public void onAnimationStart(Animator animation) {
                    updateWallpaperVisibility(true);
                    // Prepare the position
                    toView.setTranslationX(0.0f);
                    toView.setTranslationY(0.0f);
                    toView.setVisibility(View.VISIBLE);
                    toView.bringToFront();
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    dispatchOnLauncherTransitionEnd(fromView, animated, false);
                    dispatchOnLauncherTransitionEnd(toView, animated, false);

                    if (mWorkspace != null && !springLoaded && !LauncherApplication.isScreenLarge()) {
                        // Hide the workspace scrollbar
                        mWorkspace.hideScrollingIndicator(true);
                        hideDockDivider();
                    }
                    
                  //need show wallpaper xiangss 20130821
//                    if (!animationCancelled) {
//                        updateWallpaperVisibility(false);
//                    }

                    // Hide the search bar
                    if (mSearchDropTargetBar != null) {
                        mSearchDropTargetBar.hideSearchBar(false);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    animationCancelled = true;
                }
            });

            if (workspaceAnim != null) {
                mStateAnimation.play(workspaceAnim);
            }

            boolean delayAnim = false;
            final ViewTreeObserver observer;

            dispatchOnLauncherTransitionPrepare(fromView, animated, false);
            dispatchOnLauncherTransitionPrepare(toView, animated, false);

            // If any of the objects being animated haven't been measured/laid out
            // yet, delay the animation until we get a layout pass
            if ((((LauncherTransitionable) toView).getContent().getMeasuredWidth() == 0) ||
                    (mWorkspace.getMeasuredWidth() == 0) ||
                    (toView.getMeasuredWidth() == 0)) {
                observer = mWorkspace.getViewTreeObserver();
                delayAnim = true;
            } else {
                observer = null;
            }

            if(observer == null){
            	Log.d(TAG, "showAppsCustomizeHelper---observer is null!");
            }else{
            	Log.d(TAG, "showAppsCustomizeHelper---observer.isAlive() = " + observer.isAlive());
            }
            
            final AnimatorSet stateAnimation = mStateAnimation;
            final Runnable startAnimRunnable = new Runnable() {
                public void run() {
                    // Check that mStateAnimation hasn't changed while
                    // we waited for a layout/draw pass
                    if (mStateAnimation != stateAnimation)
                        return;
                    setPivotsForZoom(toView, scale);
                    dispatchOnLauncherTransitionStart(fromView, animated, false);
                    dispatchOnLauncherTransitionStart(toView, animated, false);
                    toView.post(new Runnable() {
                        public void run() {
                            // Check that mStateAnimation hasn't changed while
                            // we waited for a layout/draw pass
                            if (mStateAnimation != stateAnimation)
                                return;
                            mStateAnimation.start();
                        }
                    });
                }
            };
            if (delayAnim) {
                final OnGlobalLayoutListener delayedStart = new OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        toView.post(startAnimRunnable);
                        if(observer.isAlive() == true){
                        	observer.removeOnGlobalLayoutListener(this);
                        }
                    }
                };
                if(observer.isAlive() == true){
                	observer.addOnGlobalLayoutListener(delayedStart);
                }
            } else {
                startAnimRunnable.run();
            }
        } else {
            toView.setTranslationX(0.0f);
            toView.setTranslationY(0.0f);
            toView.setScaleX(1.0f);
            toView.setScaleY(1.0f);
            toView.setVisibility(View.VISIBLE);
            toView.bringToFront();

            if (!springLoaded && !LauncherApplication.isScreenLarge()) {
                // Hide the workspace scrollbar
                mWorkspace.hideScrollingIndicator(true);
                hideDockDivider();

                // Hide the search bar
                if (mSearchDropTargetBar != null) {
                    mSearchDropTargetBar.hideSearchBar(false);
                }
            }
            dispatchOnLauncherTransitionPrepare(fromView, animated, false);
            dispatchOnLauncherTransitionStart(fromView, animated, false);
            dispatchOnLauncherTransitionEnd(fromView, animated, false);
            dispatchOnLauncherTransitionPrepare(toView, animated, false);
            dispatchOnLauncherTransitionStart(toView, animated, false);
            dispatchOnLauncherTransitionEnd(toView, animated, false);
                                    
            //need show wallpaper xiangss 20130821
//            updateWallpaperVisibility(false);
        }
    }
   
    /**
     * add by leixp 2013--7-29
     * Things to test when changing the following one functions.
     *   - Home from Edit State
     *          - from center screen
     *          - from other screens
     *   
     *   - On workspace, power off
     *   - On Edit State, power off
     *   - Launch an app and turn off the screen while in that app
     *          - Go back with home key
     *          - Go back with back key  TODO: make this not go to workspace
     *          - From Edit State
     *          - From workspace
     *   - Enter and exit car mode (becuase it causes an extra configuration changed)
     *          - From Edit State
     *          - From the center workspace
     *          - From another workspace
     */

    /**
     * Zoom the camera out from the workspace to reveal 'toView'.
     * Assumes that the view to show is anchored at either the very top or very bottom
     * of the screen.
     */
    private void showAppsEditCustomizeHelper(final boolean animated, final boolean springLoaded){
    	if (mStateAnimation != null) {
            mStateAnimation.cancel();
            mStateAnimation = null;
        }
        final Resources res = getResources();

        final int duration = res.getInteger(R.integer.config_appsCustomizeZoomInTime);
        final int fadeDuration = res.getInteger(R.integer.config_appsCustomizeFadeInTime);
        final float scale = (float) res.getInteger(R.integer.config_appsCustomizeZoomScaleFactor);
        final AppsWidgetCustomizeTabHost toView = mAppsEditCustomizeTabHost;
        final int startDelay =
                res.getInteger(R.integer.config_workspaceAppsCustomizeAnimationStagger);
        setPivotsForZoom(toView, scale);
        // Shrink workspaces away if going to AppsCustomize from workspace
        Animator workspaceAnim =
                mWorkspace.getChangeStateAnimation(Workspace.State.EDIT, animated);
        
     // hide hotseat by xiangss 20130805
//        hideHotseat(animated);
        
        hideDockDivider();
        mWorkspace.hideScrollingIndicator(true);
        mWorkspace.showOutlines();
        mWorkspace.showBackagroundCell(animated);
        if (animated) {
            toView.setScaleX(scale);
            toView.setScaleY(scale);
            final LauncherViewPropertyAnimator scaleAnim = new LauncherViewPropertyAnimator(toView);
            scaleAnim.
                scaleX(1f).scaleY(1f).
                setDuration(duration).
                setInterpolator(new Workspace.ZoomOutInterpolator());
            toView.setVisibility(View.VISIBLE);
            toView.setAlpha(0f);
            final ObjectAnimator alphaAnim = ObjectAnimator
                .ofFloat(toView, "alpha", 0f, 1f)
                .setDuration(fadeDuration);
            alphaAnim.setInterpolator(new DecelerateInterpolator(1.5f));
            alphaAnim.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (animation == null) {
                        throw new RuntimeException("animation is null");
                    }
                    float t = (Float) animation.getAnimatedValue();
                    dispatchOnLauncherTransitionStep(toView, t);
                }
            });

            // toView should appear right at the end of the workspace shrink
            // animation
            mStateAnimation = LauncherAnimUtils.createAnimatorSet();
            mStateAnimation.play(scaleAnim).after(startDelay);
            mStateAnimation.play(alphaAnim).after(startDelay);

            mStateAnimation.addListener(new AnimatorListenerAdapter() {
                boolean animationCancelled = false;

                @Override
                public void onAnimationStart(Animator animation) {
                    updateWallpaperVisibility(true);
                    // Prepare the position
                    toView.setTranslationX(0.0f);
                    toView.setTranslationY(0.0f);
                    toView.setVisibility(View.VISIBLE);
                    toView.bringToFront();
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                   // dispatchOnLauncherTransitionEnd(fromView, animated, false);
                    dispatchOnLauncherTransitionEnd(toView, animated, false);

                    //if (mWorkspace != null && !springLoaded && !LauncherApplication.isScreenLarge()) {
                    if (mWorkspace != null && !springLoaded ) {
                        // Hide the workspace scrollbar
                     //   mWorkspace.hideScrollingIndicator(true);
                        hideDockDivider();
                        mWorkspace.showOutlines();
                        mWorkspace.showOutlinesTemporarily();
                        
                    }
                    if (!animationCancelled) {
                        updateWallpaperVisibility(true);
                    }

                    // Hide the search bar
                    if (mSearchDropTargetBar != null) {
                        mSearchDropTargetBar.hideSearchBar(true);
                    }
                    if( mSearchDropTargetBar != null){
                    	mSearchDropTargetBar.showEditHelpInfo(true);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    animationCancelled = true;
                }
            });

            if (workspaceAnim != null) {
                mStateAnimation.play(workspaceAnim);
            }

            boolean delayAnim = false;
            final ViewTreeObserver observer;

           // dispatchOnLauncherTransitionPrepare(fromView, animated, false);
            dispatchOnLauncherTransitionPrepare(toView, animated, false);

            // If any of the objects being animated haven't been measured/laid out
            // yet, delay the animation until we get a layout pass
            if ((((LauncherTransitionable) toView).getContent().getMeasuredWidth() == 0) ||
                    (mWorkspace.getMeasuredWidth() == 0) ||
                    (toView.getMeasuredWidth() == 0)) {
                observer = mWorkspace.getViewTreeObserver();
                delayAnim = true;
            } else {
                observer = null;
            }

            final AnimatorSet stateAnimation = mStateAnimation;
            final Runnable startAnimRunnable = new Runnable() {
                public void run() {
                    // Check that mStateAnimation hasn't changed while
                    // we waited for a layout/draw pass
                    if (mStateAnimation != stateAnimation)
                        return;
                    setPivotsForZoom(toView, scale);
                  // dispatchOnLauncherTransitionStart(fromView, animated, false);
                    dispatchOnLauncherTransitionStart(toView, animated, false);
                    toView.post(new Runnable() {
                        public void run() {
                            // Check that mStateAnimation hasn't changed while
                            // we waited for a layout/draw pass
                            if (mStateAnimation != stateAnimation)
                                return;
                            mStateAnimation.start();
                        }
                    });
                }
            };
            if (delayAnim) {
                final OnGlobalLayoutListener delayedStart = new OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        toView.post(startAnimRunnable);
                        observer.removeOnGlobalLayoutListener(this);
                    }
                };
                observer.addOnGlobalLayoutListener(delayedStart);
            } else {
                startAnimRunnable.run();
            }
        } else {
            toView.setTranslationX(0.0f);
            toView.setTranslationY(0.0f);
            toView.setScaleX(1.0f);
            toView.setScaleY(1.0f);
            toView.setVisibility(View.VISIBLE);
            toView.bringToFront();

            if (!springLoaded && !LauncherApplication.isScreenLarge()) {
                // Hide the workspace scrollbar
                mWorkspace.hideScrollingIndicator(true);
                hideDockDivider();

                // Hide the search bar
                if (mSearchDropTargetBar != null) {
                    mSearchDropTargetBar.hideSearchBar(false);
                }
                if( mSearchDropTargetBar != null){
                	mSearchDropTargetBar.showEditHelpInfo(false);
                }
            }
          //  dispatchOnLauncherTransitionPrepare(fromView, animated, false);
          //  dispatchOnLauncherTransitionStart(fromView, animated, false);
          //  dispatchOnLauncherTransitionEnd(fromView, animated, false);
            dispatchOnLauncherTransitionPrepare(toView, animated, false);
            dispatchOnLauncherTransitionStart(toView, animated, false);
            dispatchOnLauncherTransitionEnd(toView, animated, false);
            updateWallpaperVisibility(true);
        }
    	
    }
    /**
     * Zoom the camera back into the workspace, hiding 'fromView'.
     * This is the opposite of showAppsCustomizeHelper.
     * @param animated If true, the transition will be animated.
     */
    private void hideAppsCustomizeHelper(State toState, final boolean animated,
            final boolean springLoaded, final Runnable onCompleteRunnable) {

        if (mStateAnimation != null) {
            mStateAnimation.cancel();
            mStateAnimation = null;
        }
        Resources res = getResources();

        final int duration = res.getInteger(R.integer.config_appsCustomizeZoomOutTime);
        final int fadeOutDuration =
                res.getInteger(R.integer.config_appsCustomizeFadeOutTime);
        final float scaleFactor = (float)
                res.getInteger(R.integer.config_appsCustomizeZoomScaleFactor);
        final View fromView = mAppsCustomizeTabHost;
        final View toView = mWorkspace;
        Animator workspaceAnim = null;

        if (toState == State.WORKSPACE) {
            int stagger = res.getInteger(R.integer.config_appsCustomizeWorkspaceAnimationStagger);
            workspaceAnim = mWorkspace.getChangeStateAnimation(
                    Workspace.State.NORMAL, animated, stagger);
        } else if (toState == State.APPS_CUSTOMIZE_SPRING_LOADED) {
            workspaceAnim = mWorkspace.getChangeStateAnimation(
                    Workspace.State.SPRING_LOADED, animated);
        }

        setPivotsForZoom(fromView, scaleFactor);
        updateWallpaperVisibility(true);
        
     // hide hotseat by xiangss 20130805
//        showHotseat(animated);
        
        if (animated) {
            final LauncherViewPropertyAnimator scaleAnim =
                    new LauncherViewPropertyAnimator(fromView);
            scaleAnim.
                scaleX(scaleFactor).scaleY(scaleFactor).
                setDuration(duration).
                setInterpolator(new Workspace.ZoomInInterpolator());

            final ObjectAnimator alphaAnim = ObjectAnimator
                .ofFloat(fromView, "alpha", 1f, 0f)
                .setDuration(fadeOutDuration);
            alphaAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            alphaAnim.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float t = 1f - (Float) animation.getAnimatedValue();
                    dispatchOnLauncherTransitionStep(fromView, t);
                    dispatchOnLauncherTransitionStep(toView, t);
                }
            });

            mStateAnimation = LauncherAnimUtils.createAnimatorSet();

            dispatchOnLauncherTransitionPrepare(fromView, animated, true);
            dispatchOnLauncherTransitionPrepare(toView, animated, true);
            mAppsCustomizeContent.pauseScrolling();

            mStateAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    updateWallpaperVisibility(true);
                    fromView.setVisibility(View.GONE);
                    dispatchOnLauncherTransitionEnd(fromView, animated, true);
                    dispatchOnLauncherTransitionEnd(toView, animated, true);
                    if (mWorkspace != null) {
                        mWorkspace.hideScrollingIndicator(false);
                    }
                    if (onCompleteRunnable != null) {
                        onCompleteRunnable.run();
                    }
                    mAppsCustomizeContent.updateCurrentPageScroll();
                    mAppsCustomizeContent.resumeScrolling();
                }
            });

            mStateAnimation.playTogether(scaleAnim, alphaAnim);
            if (workspaceAnim != null) {
                mStateAnimation.play(workspaceAnim);
            }
            dispatchOnLauncherTransitionStart(fromView, animated, true);
            dispatchOnLauncherTransitionStart(toView, animated, true);
            final Animator stateAnimation = mStateAnimation;
            mWorkspace.post(new Runnable() {
                public void run() {
                    if (stateAnimation != mStateAnimation)
                        return;
                    mStateAnimation.start();
                }
            });
        } else {
            fromView.setVisibility(View.GONE);
            dispatchOnLauncherTransitionPrepare(fromView, animated, true);
            dispatchOnLauncherTransitionStart(fromView, animated, true);
            dispatchOnLauncherTransitionEnd(fromView, animated, true);
            dispatchOnLauncherTransitionPrepare(toView, animated, true);
            dispatchOnLauncherTransitionStart(toView, animated, true);
            dispatchOnLauncherTransitionEnd(toView, animated, true);
            mWorkspace.hideScrollingIndicator(false);
        }
    }
    /**
     * add by leixp 2013-07-29
     * Zoom the camera back into the workspace, hiding 'fromView'.
     * This is the opposite of showAppsEditCustomizeHelper.
     * @param animated If true, the transition will be animated.
     */
    private void hideAppsEditCustomizeHelper(State toState, final boolean animated,
            final boolean springLoaded, final Runnable onCompleteRunnable) {
        if (mStateAnimation != null) {
            mStateAnimation.cancel();
            mStateAnimation = null;
        }
        Resources res = getResources();

        final int duration = res.getInteger(R.integer.config_appsCustomizeZoomOutTime);
        final int fadeOutDuration =
                res.getInteger(R.integer.config_appsCustomizeFadeOutTime);
        final float scaleFactor = (float)
                res.getInteger(R.integer.config_appsCustomizeZoomScaleFactor);
        final View fromView = mAppsEditCustomizeTabHost;
        final View toView = mWorkspace;
        Animator workspaceAnim = null;

        if (toState == State.WORKSPACE) {
            int stagger = res.getInteger(R.integer.config_appsCustomizeWorkspaceAnimationStagger);
            workspaceAnim = mWorkspace.getChangeStateAnimation(
                    Workspace.State.NORMAL, animated, stagger);
        } else if (toState == State.APPS_CUSTOMIZE_SPRING_LOADED) {
            workspaceAnim = mWorkspace.getChangeStateAnimation(
                    Workspace.State.SPRING_LOADED, animated);
        }

        setPivotsForZoom(fromView, scaleFactor);
        updateWallpaperVisibility(true);
        mWorkspace.hideBackagroundCell(animated);
        //mWorkspace.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        
     // hide hotseat by xiangss 20130805
//        showHotseat(animated);
        
        showDockDivider(animated);
        
        if (animated) {
            final LauncherViewPropertyAnimator scaleAnim =
                    new LauncherViewPropertyAnimator(fromView);
            scaleAnim.
                scaleX(scaleFactor).scaleY(scaleFactor).
                setDuration(duration).
                setInterpolator(new Workspace.ZoomInInterpolator());

            final ObjectAnimator alphaAnim = ObjectAnimator
                .ofFloat(fromView, "alpha", 1f, 0f)
                .setDuration(fadeOutDuration);
            alphaAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            alphaAnim.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float t = 1f - (Float) animation.getAnimatedValue();
                    dispatchOnLauncherTransitionStep(fromView, t);
                    dispatchOnLauncherTransitionStep(toView, t);
                }
            });

            mStateAnimation = LauncherAnimUtils.createAnimatorSet();

            dispatchOnLauncherTransitionPrepare(fromView, animated, true);
            dispatchOnLauncherTransitionPrepare(toView, animated, true);
            mAppsEditCustomizeContent.pauseScrolling();

            mStateAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    updateWallpaperVisibility(true);
                    fromView.setVisibility(View.GONE);
                    dispatchOnLauncherTransitionEnd(fromView, animated, true);
                    dispatchOnLauncherTransitionEnd(toView, animated, true);
                    if (mWorkspace != null) {
                        mWorkspace.hideScrollingIndicator(false);
                    }
                    if (onCompleteRunnable != null) {
                        onCompleteRunnable.run();
                    }
                    mAppsEditCustomizeContent.updateCurrentPageScroll();
                    mAppsEditCustomizeContent.resumeScrolling();
                }
            });

            mStateAnimation.playTogether(scaleAnim, alphaAnim);
            if (workspaceAnim != null) {
                mStateAnimation.play(workspaceAnim);
            }
            dispatchOnLauncherTransitionStart(fromView, animated, true);
            dispatchOnLauncherTransitionStart(toView, animated, true);
            final Animator stateAnimation = mStateAnimation;
            mWorkspace.post(new Runnable() {
                public void run() {
                    if (stateAnimation != mStateAnimation)
                        return;
                    mStateAnimation.start();
                }
            });
        } else {
            fromView.setVisibility(View.GONE);
            dispatchOnLauncherTransitionPrepare(fromView, animated, true);
            dispatchOnLauncherTransitionStart(fromView, animated, true);
            dispatchOnLauncherTransitionEnd(fromView, animated, true);
            dispatchOnLauncherTransitionPrepare(toView, animated, true);
            dispatchOnLauncherTransitionStart(toView, animated, true);
            dispatchOnLauncherTransitionEnd(toView, animated, true);
            mWorkspace.hideScrollingIndicator(false);
        }
    }
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            mAppsCustomizeTabHost.onTrimMemory();
        }
        if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
        	mAppsEditCustomizeTabHost.onTrimMemory();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
            // When another window occludes launcher (like the notification shade, or recents),
            // ensure that we enable the wallpaper flag so that transitions are done correctly.
            updateWallpaperVisibility(true);
        } else {
            // When launcher has focus again, disable the wallpaper if we are in AllApps
            mWorkspace.postDelayed(new Runnable() {
                @Override
                public void run() {
                    disableWallpaperIfInAllApps();
                }
            }, 500);
        }
    }

    void showWorkspace(boolean animated) {
        showWorkspace(animated, null);        
    }

    void showWorkspace(boolean animated, Runnable onCompleteRunnable) {
        if (mState != State.WORKSPACE) {
            boolean wasInSpringLoadedMode = (mState == State.APPS_CUSTOMIZE_SPRING_LOADED);
            mWorkspace.setVisibility(View.VISIBLE);
            if(isAllAppsVisible()||mState == State.APPS_CUSTOMIZE_SPRING_LOADED){
            hideAppsCustomizeHelper(State.WORKSPACE, animated, false, onCompleteRunnable);
            }
            // hide Edit state
            if(isEditState()){
            	hideAppsEditCustomizeHelper(State.WORKSPACE, animated, false, onCompleteRunnable);
            }

            // Show the search bar (only animate if we were showing the drop target bar in spring
            // loaded mode)
            if (mSearchDropTargetBar != null) {
                mSearchDropTargetBar.showSearchBar(true);
                mSearchDropTargetBar.hideEditHelpInfo(true);
            }

            // We only need to animate in the dock divider if we're going from spring loaded mode
            showDockDivider(animated && wasInSpringLoadedMode);

            // Set focus to the AppsCustomize button
            if (mAllAppsButton != null) {
                mAllAppsButton.requestFocus();
            }
        }

        mWorkspace.flashScrollingIndicator(animated);

        // Change the state *after* we've called all the transition code
        mState = State.WORKSPACE;

        // Resume the auto-advance of widgets
        mUserPresent = true;
        updateRunning();

        //show wallpaper shortcut
        curtain.setViewVisible(true);
    	lampCordView.setViewVisible(true);
    	
    	
        // Send an accessibility event to announce the context change
        getWindow().getDecorView()
                .sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
    }

    void showAllApps(boolean animated) {
        if (mState != State.WORKSPACE) return;
        disPopupDialog();
        showAppsCustomizeHelper(animated, false);
        mAppsCustomizeTabHost.requestFocus();

        // Change the state *after* we've called all the transition code
        mState = State.APPS_CUSTOMIZE;

        // Pause the auto-advance of widgets until we are out of AllApps
        mUserPresent = false;
        updateRunning();
        closeFolder();

        
        //close wallpaper shortcut
        curtain.setViewVisible(false);
    	lampCordView.setViewVisible(false);
        
        
        // Send an accessibility event to announce the context change
        getWindow().getDecorView()
                .sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
    }
    /*
     * add by leixp 2013-07-29
     * Enter into Edit state 
     */
    void showEditAllAppWidget(boolean animated) {
    	if (mState != State.WORKSPACE) return;
    	
    	showAppsEditCustomizeHelper(animated,false);
    	mAppsEditCustomizeTabHost.requestFocus();
    	
    	 // Change the state *after* we've called all the transition code
    	mState = State.EDIT_STATE;
    	
    	// Pause the auto-advance of widgets until we are out of AllApps
        mUserPresent = false;
    	updateRunning();
    	closeFolder();
    	
    	// Send an accessibility event to announce the context change
        getWindow().getDecorView()
                .sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
    }

    void enterSpringLoadedDragMode() {
        if (isAllAppsVisible()) {
            hideAppsCustomizeHelper(State.APPS_CUSTOMIZE_SPRING_LOADED, true, true, null);
            hideDockDivider();
            mState = State.APPS_CUSTOMIZE_SPRING_LOADED;
        }
    }

    void exitSpringLoadedDragModeDelayed(final boolean successfulDrop, boolean extendedDelay,
            final Runnable onCompleteRunnable) {
        if (mState != State.APPS_CUSTOMIZE_SPRING_LOADED) return;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (successfulDrop) {
                    // Before we show workspace, hide all apps again because
                    // exitSpringLoadedDragMode made it visible. This is a bit hacky; we should
                    // clean up our state transition functions
                    mAppsCustomizeTabHost.setVisibility(View.GONE);
                    showWorkspace(true, onCompleteRunnable);
                } else {
                    exitSpringLoadedDragMode();
                }
            }
        }, (extendedDelay ?
                EXIT_SPRINGLOADED_MODE_LONG_TIMEOUT :
                EXIT_SPRINGLOADED_MODE_SHORT_TIMEOUT));
    }

    void exitSpringLoadedDragMode() {
        if (mState == State.APPS_CUSTOMIZE_SPRING_LOADED) {
            final boolean animated = true;
            final boolean springLoaded = true;
            showAppsCustomizeHelper(animated, springLoaded);
            mState = State.APPS_CUSTOMIZE;
        }
        // Otherwise, we are not in spring loaded mode, so don't do anything.
    }

    void hideDockDivider() {
    	
    	/* //hide divider by xiangss 201308014
    	// modified by leixp 2013-07-29
    	mDockDivider.setVisibility(View.INVISIBLE);
        if (mQsbDivider != null && mDockDivider != null) {
            mQsbDivider.setVisibility(View.INVISIBLE);
            mDockDivider.setVisibility(View.INVISIBLE);
        }
        */
    }

    void showDockDivider(boolean animated) {
    	    	
    	/* //hide divider by xiangss 201308014
    	// modified by leixp 2013-07-29
    	mDockDivider.setVisibility(View.VISIBLE);
        if (mQsbDivider != null && mDockDivider != null) {
            mQsbDivider.setVisibility(View.VISIBLE);
            mDockDivider.setVisibility(View.VISIBLE);
            if (mDividerAnimator != null) {
                mDividerAnimator.cancel();
                mQsbDivider.setAlpha(1f);
                mDockDivider.setAlpha(1f);
                mDividerAnimator = null;
            }
            if (animated) {
                mDividerAnimator = LauncherAnimUtils.createAnimatorSet();
                mDividerAnimator.playTogether(LauncherAnimUtils.ofFloat(mQsbDivider, "alpha", 1f),
                        LauncherAnimUtils.ofFloat(mDockDivider, "alpha", 1f));
                int duration = 0;
                if (mSearchDropTargetBar != null) {
                    duration = mSearchDropTargetBar.getTransitionInDuration();
                }
                mDividerAnimator.setDuration(duration);
                mDividerAnimator.start();
            }
        }
        */
    }

    void lockAllApps() {
        // TODO
    }

    void unlockAllApps() {
        // TODO
    }


 // hide hotseat by xiangss 20130805
    /**
     * Shows the hotseat area.
     */
   /* void showHotseat(boolean animated) {
    	// modified by leixp 2013-07-29
       // if (!LauncherApplication.isScreenLarge()) {
            if (animated) {
                if (mHotseat.getAlpha() != 1f) {
                    int duration = 0;
                    if (mSearchDropTargetBar != null) {
                        duration = mSearchDropTargetBar.getTransitionInDuration();
                    }
                    mHotseat.animate().alpha(1f).setDuration(duration);
                    mHotseat.setVisibility(View.VISIBLE);
                }
            } else {
                mHotseat.setAlpha(1f);
                mHotseat.setVisibility(View.VISIBLE);
            }
       // }
    }*/

 // hide hotseat by xiangss 20130805
    /**
     * Hides the hotseat area.
     */
    /*void hideHotseat(boolean animated) {
    	// modified by leixp 2013-07-29
       // if (!LauncherApplication.isScreenLarge()) {
            if (animated) {
                if (mHotseat.getAlpha() != 0f) {
                    int duration = 0;
                    if (mSearchDropTargetBar != null) {
                        duration = mSearchDropTargetBar.getTransitionOutDuration();
                    }
                    mHotseat.animate().alpha(0f).setDuration(duration);
                }
            } else {
                mHotseat.setAlpha(0f);
            }
       // }
    }*/

    /**
     * Add an item from all apps or customize onto the given workspace screen.
     * If layout is null, add to the current screen.
     */
    void addExternalItemToScreen(ItemInfo itemInfo, final CellLayout layout) {
        if (!mWorkspace.addExternalItemToScreen(itemInfo, layout)) {
            showOutOfSpaceMessage(isHotseatLayout(layout));
        }
    }

    /** Maps the current orientation to an index for referencing orientation correct global icons */
    private int getCurrentOrientationIndexForGlobalIcons() {
        // default - 0, landscape - 1
        switch (getResources().getConfiguration().orientation) {
        case Configuration.ORIENTATION_LANDSCAPE:
            return 1;
        default:
            return 0;
        }
    }

    private Drawable getExternalPackageToolbarIcon(ComponentName activityName, String resourceName) {
        try {
            PackageManager packageManager = getPackageManager();
            // Look for the toolbar icon specified in the activity meta-data
            Bundle metaData = packageManager.getActivityInfo(
                    activityName, PackageManager.GET_META_DATA).metaData;
            if (metaData != null) {
                int iconResId = metaData.getInt(resourceName);
                if (iconResId != 0) {
                    Resources res = packageManager.getResourcesForActivity(activityName);
                    return res.getDrawable(iconResId);
                }
            }
        } catch (NameNotFoundException e) {
            // This can happen if the activity defines an invalid drawable
            Log.w(TAG, "Failed to load toolbar icon; " + activityName.flattenToShortString() +
                    " not found", e);
        } catch (Resources.NotFoundException nfe) {
            // This can happen if the activity defines an invalid drawable
            Log.w(TAG, "Failed to load toolbar icon from " + activityName.flattenToShortString(),
                    nfe);
        }
        return null;
    }

    // if successful in getting icon, return it; otherwise, set button to use default drawable
    private Drawable.ConstantState updateTextButtonWithIconFromExternalActivity(
            int buttonId, ComponentName activityName, int fallbackDrawableId,
            String toolbarResourceName) {
        Drawable toolbarIcon = getExternalPackageToolbarIcon(activityName, toolbarResourceName);
        Resources r = getResources();
        int w = r.getDimensionPixelSize(R.dimen.toolbar_external_icon_width);
        int h = r.getDimensionPixelSize(R.dimen.toolbar_external_icon_height);

        TextView button = (TextView) findViewById(buttonId);
        // If we were unable to find the icon via the meta-data, use a generic one
        if (toolbarIcon == null) {
            toolbarIcon = r.getDrawable(fallbackDrawableId);
            toolbarIcon.setBounds(0, 0, w, h);
            if (button != null) {
                button.setCompoundDrawables(toolbarIcon, null, null, null);
            }
            return null;
        } else {
            toolbarIcon.setBounds(0, 0, w, h);
            if (button != null) {
                button.setCompoundDrawables(toolbarIcon, null, null, null);
            }
            return toolbarIcon.getConstantState();
        }
    }

    // if successful in getting icon, return it; otherwise, set button to use default drawable
    private Drawable.ConstantState updateButtonWithIconFromExternalActivity(
            int buttonId, ComponentName activityName, int fallbackDrawableId,
            String toolbarResourceName) {
        ImageView button = (ImageView) findViewById(buttonId);
        Drawable toolbarIcon = getExternalPackageToolbarIcon(activityName, toolbarResourceName);

        if (button != null) {
            // If we were unable to find the icon via the meta-data, use a
            // generic one
            if (toolbarIcon == null) {
                button.setImageResource(fallbackDrawableId);
            } else {
                button.setImageDrawable(toolbarIcon);
            }
        }

        return toolbarIcon != null ? toolbarIcon.getConstantState() : null;

    }

    private void updateTextButtonWithDrawable(int buttonId, Drawable d) {
        TextView button = (TextView) findViewById(buttonId);
        button.setCompoundDrawables(d, null, null, null);
    }

    private void updateButtonWithDrawable(int buttonId, Drawable.ConstantState d) {
        ImageView button = (ImageView) findViewById(buttonId);
        button.setImageDrawable(d.newDrawable(getResources()));
    }

    private void invalidatePressedFocusedStates(View container, View button) {
        if (container instanceof HolographicLinearLayout) {
            HolographicLinearLayout layout = (HolographicLinearLayout) container;
            layout.invalidatePressedFocusedStates();
        } else if (button instanceof HolographicImageView) {
            HolographicImageView view = (HolographicImageView) button;
            view.invalidatePressedFocusedStates();
        }
    }

    private boolean updateGlobalSearchIcon() {
        final View searchButtonContainer = findViewById(R.id.search_button_container);
        final ImageView searchButton = (ImageView) findViewById(R.id.search_button);
        final View voiceButtonContainer = findViewById(R.id.voice_button_container);
        final View voiceButton = findViewById(R.id.voice_button);
        final View voiceButtonProxy = findViewById(R.id.voice_button_proxy);

        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName activityName = searchManager.getGlobalSearchActivity();
        if (activityName != null) {
            int coi = getCurrentOrientationIndexForGlobalIcons();
            sGlobalSearchIcon[coi] = updateButtonWithIconFromExternalActivity(
                    R.id.search_button, activityName, R.drawable.ic_home_search_normal_holo,
                    TOOLBAR_SEARCH_ICON_METADATA_NAME);
            if (sGlobalSearchIcon[coi] == null) {
                sGlobalSearchIcon[coi] = updateButtonWithIconFromExternalActivity(
                        R.id.search_button, activityName, R.drawable.ic_home_search_normal_holo,
                        TOOLBAR_ICON_METADATA_NAME);
            }

            if (searchButtonContainer != null) searchButtonContainer.setVisibility(View.VISIBLE);
            searchButton.setVisibility(View.VISIBLE);
            invalidatePressedFocusedStates(searchButtonContainer, searchButton);
            return true;
        } else {
            // We disable both search and voice search when there is no global search provider
            if (searchButtonContainer != null) searchButtonContainer.setVisibility(View.GONE);
            if (voiceButtonContainer != null) voiceButtonContainer.setVisibility(View.GONE);
            searchButton.setVisibility(View.GONE);
            voiceButton.setVisibility(View.GONE);
            if (voiceButtonProxy != null) {
                voiceButtonProxy.setVisibility(View.GONE);
            }
            return false;
        }
    }

    private void updateGlobalSearchIcon(Drawable.ConstantState d) {
        final View searchButtonContainer = findViewById(R.id.search_button_container);
        final View searchButton = (ImageView) findViewById(R.id.search_button);
        updateButtonWithDrawable(R.id.search_button, d);
        invalidatePressedFocusedStates(searchButtonContainer, searchButton);
    }

    private boolean updateVoiceSearchIcon(boolean searchVisible) {
        final View voiceButtonContainer = findViewById(R.id.voice_button_container);
        final View voiceButton = findViewById(R.id.voice_button);
        final View voiceButtonProxy = findViewById(R.id.voice_button_proxy);

        // We only show/update the voice search icon if the search icon is enabled as well
        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName globalSearchActivity = searchManager.getGlobalSearchActivity();

        ComponentName activityName = null;
        if (globalSearchActivity != null) {
            // Check if the global search activity handles voice search
            Intent intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
            intent.setPackage(globalSearchActivity.getPackageName());
            activityName = intent.resolveActivity(getPackageManager());
        }

        if (activityName == null) {
            // Fallback: check if an activity other than the global search activity
            // resolves this
            Intent intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
            activityName = intent.resolveActivity(getPackageManager());
        }
        if (searchVisible && activityName != null) {
            int coi = getCurrentOrientationIndexForGlobalIcons();
            sVoiceSearchIcon[coi] = updateButtonWithIconFromExternalActivity(
                    R.id.voice_button, activityName, R.drawable.ic_home_voice_search_holo,
                    TOOLBAR_VOICE_SEARCH_ICON_METADATA_NAME);
            if (sVoiceSearchIcon[coi] == null) {
                sVoiceSearchIcon[coi] = updateButtonWithIconFromExternalActivity(
                        R.id.voice_button, activityName, R.drawable.ic_home_voice_search_holo,
                        TOOLBAR_ICON_METADATA_NAME);
            }
            if (voiceButtonContainer != null) voiceButtonContainer.setVisibility(View.VISIBLE);
            voiceButton.setVisibility(View.VISIBLE);
            if (voiceButtonProxy != null) {
                voiceButtonProxy.setVisibility(View.VISIBLE);
            }
            invalidatePressedFocusedStates(voiceButtonContainer, voiceButton);
            return true;
        } else {
            if (voiceButtonContainer != null) voiceButtonContainer.setVisibility(View.GONE);
            voiceButton.setVisibility(View.GONE);
            if (voiceButtonProxy != null) {
                voiceButtonProxy.setVisibility(View.GONE);
            }
            return false;
        }
    }

    private void updateVoiceSearchIcon(Drawable.ConstantState d) {
        final View voiceButtonContainer = findViewById(R.id.voice_button_container);
        final View voiceButton = findViewById(R.id.voice_button);
        updateButtonWithDrawable(R.id.voice_button, d);
        invalidatePressedFocusedStates(voiceButtonContainer, voiceButton);
    }

    /**
     * Sets the app market icon
     */
    private void updateAppMarketIcon() {
        final View marketButton = findViewById(R.id.market_button);
        Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MARKET);
        // Find the app market activity by resolving an intent.
        // (If multiple app markets are installed, it will return the ResolverActivity.)
        ComponentName activityName = intent.resolveActivity(getPackageManager());
        if (activityName != null) {
            int coi = getCurrentOrientationIndexForGlobalIcons();
            mAppMarketIntent = intent;
            sAppMarketIcon[coi] = updateTextButtonWithIconFromExternalActivity(
                    R.id.market_button, activityName, R.drawable.ic_launcher_market_holo,
                    TOOLBAR_ICON_METADATA_NAME);
            marketButton.setVisibility(View.VISIBLE);
        } else {
            // We should hide and disable the view so that we don't try and restore the visibility
            // of it when we swap between drag & normal states from IconDropTarget subclasses.
            marketButton.setVisibility(View.GONE);
            marketButton.setEnabled(false);
        }
    }

    private void updateAppMarketIcon(Drawable.ConstantState d) {
        // Ensure that the new drawable we are creating has the approprate toolbar icon bounds
        Resources r = getResources();
        Drawable marketIconDrawable = d.newDrawable(r);
        int w = r.getDimensionPixelSize(R.dimen.toolbar_external_icon_width);
        int h = r.getDimensionPixelSize(R.dimen.toolbar_external_icon_height);
        marketIconDrawable.setBounds(0, 0, w, h);

        updateTextButtonWithDrawable(R.id.market_button, marketIconDrawable);
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        final boolean result = super.dispatchPopulateAccessibilityEvent(event);
        final List<CharSequence> text = event.getText();
        text.clear();
        // Populate event with a fake title based on the current state.
        if (mState == State.APPS_CUSTOMIZE) {
            text.add(getString(R.string.all_apps_button_label));
        } else {
            text.add(getString(R.string.all_apps_home_button_label));
        }
        return result;
    }

    /**
     * Receives notifications when system dialogs are to be closed.
     */
    private class CloseSystemDialogsIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            closeSystemDialogs();
        }
    }

    /**
     * Receives notifications whenever the appwidgets are reset.
     */
    private class AppWidgetResetObserver extends ContentObserver {
        public AppWidgetResetObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            onAppWidgetReset();
        }
    }

    /**
     * If the activity is currently paused, signal that we need to re-run the loader
     * in onResume.
     *
     * This needs to be called from incoming places where resources might have been loaded
     * while we are paused.  That is becaues the Configuration might be wrong
     * when we're not running, and if it comes back to what it was when we
     * were paused, we are not restarted.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     *
     * @return true if we are currently paused.  The caller might be able to
     * skip some work in that case since we will come back again.
     */
    public boolean setLoadOnResume() {
        if (mPaused) {
            Log.i(TAG, "setLoadOnResume");
            mOnResumeNeedsLoad = true;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public int getCurrentWorkspaceScreen() {
        if (mWorkspace != null) {
            return mWorkspace.getCurrentPage();
        } else {
            return SCREEN_COUNT / 2;
        }
    }

    /**
     * 切换workspace到指定页
     * @param curPage
     */
    public void setCurrentWorkspaceScreen(int curPage) {
        if (mWorkspace != null) {
            mWorkspace.setCurrentPage(curPage);        
        }
    }
    
    
    /**
     * Refreshes the shortcuts shown on the workspace.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void startBinding() {
        final Workspace workspace = mWorkspace;

        mNewShortcutAnimatePage = -1;
        mNewShortcutAnimateViews.clear();
        mWorkspace.clearDropTargets();
        int count = workspace.getChildCount();
        for (int i = 0; i < count; i++) {
            // Use removeAllViewsInLayout() to avoid an extra requestLayout() and invalidate().
            final CellLayout layoutParent = (CellLayout) workspace.getChildAt(i);
            layoutParent.removeAllViewsInLayout();
        }
        mWidgetsToAdvance.clear();
        if (mHotseat != null) {
            mHotseat.resetLayout();
        }
    }

    /**
     * Bind the items start-end from the list.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindItems(ArrayList<ItemInfo> shortcuts, int start, int end) {
        setLoadOnResume();

        // Get the list of added shortcuts and intersect them with the set of shortcuts here
        Set<String> newApps = new HashSet<String>();
        newApps = mSharedPrefs.getStringSet(InstallShortcutReceiver.NEW_APPS_LIST_KEY, newApps);

        Workspace workspace = mWorkspace;
        for (int i = start; i < end; i++) {
            final ItemInfo item = shortcuts.get(i);

            // Short circuit if we are loading dock items for a configuration which has no dock
            if (item.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT &&
                    mHotseat == null) {
                continue;
            }

            switch (item.itemType) {
                case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
                case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
                    ShortcutInfo info = (ShortcutInfo) item;
                    String uri = info.intent.toUri(0).toString();
                    View shortcut = createShortcut(info);
                    workspace.addInScreen(shortcut, item.container, item.screen, item.cellX,
                            item.cellY, 1, 1, false);
                    boolean animateIconUp = false;
                    synchronized (newApps) {
                        if (newApps.contains(uri)) {
                            animateIconUp = newApps.remove(uri);
                        }
                    }
                    if (animateIconUp) {
                        // Prepare the view to be animated up
                        shortcut.setAlpha(0f);
                        shortcut.setScaleX(0f);
                        shortcut.setScaleY(0f);
                        mNewShortcutAnimatePage = item.screen;
                        if (!mNewShortcutAnimateViews.contains(shortcut)) {
                            mNewShortcutAnimateViews.add(shortcut);
                        }
                    }
                    break;
                case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:
                    FolderIcon newFolder = FolderIcon.fromXml(R.layout.folder_icon, this,
                            (ViewGroup) workspace.getChildAt(workspace.getCurrentPage()),
                            (FolderInfo) item, mIconCache);
                    workspace.addInScreen(newFolder, item.container, item.screen, item.cellX,
                            item.cellY, 1, 1, false);
                    break;
            }
        }

        workspace.requestLayout();
    }

    /**
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindFolders(HashMap<Long, FolderInfo> folders) {
        setLoadOnResume();
        sFolders.clear();
        sFolders.putAll(folders);
    }

    /**
     * Add the views for a widget to the workspace.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindAppWidget(LauncherAppWidgetInfo item) {
        setLoadOnResume();

        final long start = DEBUG_WIDGETS ? SystemClock.uptimeMillis() : 0;
        if (DEBUG_WIDGETS) {
            Log.d(TAG, "bindAppWidget: " + item);
        }
        final Workspace workspace = mWorkspace;

        final int appWidgetId = item.appWidgetId;
        
        Log.d(TAG, "bindAppWidget------appWidgetId = " + appWidgetId);
        Log.d(TAG, "bindAppWidget------providerName = " + item.providerName);
        
        if(appWidgetId <= 0){
        	item.localWidgetView = LauncherApplication.getLocalWidgetManager().getLocalWidgetById(appWidgetId);
        	
	        item.localWidgetView.setTag(item);	      
	
	        item.localWidgetView.show();
	        
	        workspace.addInScreen(item.localWidgetView, item.container, item.screen, item.cellX,
	                item.cellY, item.spanX, item.spanY, false);
	        
        	
        }else{

        
	        final AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
	        if (DEBUG_WIDGETS) {
	            Log.d(TAG, "bindAppWidget: id=" + item.appWidgetId + " belongs to component " + appWidgetInfo.provider);
	        }
	
	        item.hostView = mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
	
	        item.hostView.setTag(item);
	        item.onBindAppWidget(this);
	
	        workspace.addInScreen(item.hostView, item.container, item.screen, item.cellX,
	                item.cellY, item.spanX, item.spanY, false);
	        addWidgetToAutoAdvanceIfNeeded(item.hostView, appWidgetInfo);
        }
        workspace.requestLayout();

        if (DEBUG_WIDGETS) {
            Log.d(TAG, "bound widget id="+item.appWidgetId+" in "
                    + (SystemClock.uptimeMillis()-start) + "ms");
        }
    }

    public void onPageBoundSynchronously(int page) {
        mSynchronouslyBoundPages.add(page);
    }

    /**
     * Callback saying that there aren't any more items to bind.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void finishBindingItems() {
        setLoadOnResume();

        if (mSavedState != null) {
            if (!mWorkspace.hasFocus()) {
                mWorkspace.getChildAt(mWorkspace.getCurrentPage()).requestFocus();
            }
            mSavedState = null;
        }

        mWorkspace.restoreInstanceStateForRemainingPages();

        // If we received the result of any pending adds while the loader was running (e.g. the
        // widget configuration forced an orientation change), process them now.
        for (int i = 0; i < sPendingAddList.size(); i++) {
            completeAdd(sPendingAddList.get(i));
        }
        sPendingAddList.clear();

        // Update the market app icon as necessary (the other icons will be managed in response to
        // package changes in bindSearchablesChanged()
      //  updateAppMarketIcon();

        // Animate up any icons as necessary
        if (mVisible || mWorkspaceLoading) {
            Runnable newAppsRunnable = new Runnable() {
                @Override
                public void run() {
                    runNewAppsAnimation(false);
                }
            };

            boolean willSnapPage = mNewShortcutAnimatePage > -1 &&
                    mNewShortcutAnimatePage != mWorkspace.getCurrentPage();
            if (canRunNewAppsAnimation()) {
                // If the user has not interacted recently, then either snap to the new page to show
                // the new-apps animation or just run them if they are to appear on the current page
                if (willSnapPage) {
                    mWorkspace.snapToPage(mNewShortcutAnimatePage, newAppsRunnable);
                } else {
                    runNewAppsAnimation(false);
                }
            } else {
                // If the user has interacted recently, then just add the items in place if they
                // are on another page (or just normally if they are added to the current page)
                runNewAppsAnimation(willSnapPage);
            }
        }

        mWorkspaceLoading = false;
    }

    private boolean canRunNewAppsAnimation() {
        long diff = System.currentTimeMillis() - mDragController.getLastGestureUpTime();
        return diff > (NEW_APPS_ANIMATION_INACTIVE_TIMEOUT_SECONDS * 1000);
    }

    /**
     * Runs a new animation that scales up icons that were added while Launcher was in the
     * background.
     *
     * @param immediate whether to run the animation or show the results immediately
     */
    private void runNewAppsAnimation(boolean immediate) {
        AnimatorSet anim = LauncherAnimUtils.createAnimatorSet();
        Collection<Animator> bounceAnims = new ArrayList<Animator>();

        // Order these new views spatially so that they animate in order
        Collections.sort(mNewShortcutAnimateViews, new Comparator<View>() {
            @Override
            public int compare(View a, View b) {
                CellLayout.LayoutParams alp = (CellLayout.LayoutParams) a.getLayoutParams();
                CellLayout.LayoutParams blp = (CellLayout.LayoutParams) b.getLayoutParams();
                int cellCountX = LauncherModel.getCellCountX();
                return (alp.cellY * cellCountX + alp.cellX) - (blp.cellY * cellCountX + blp.cellX);
            }
        });

        // Animate each of the views in place (or show them immediately if requested)
        if (immediate) {
            for (View v : mNewShortcutAnimateViews) {
                v.setAlpha(1f);
                v.setScaleX(1f);
                v.setScaleY(1f);
            }
        } else {
            for (int i = 0; i < mNewShortcutAnimateViews.size(); ++i) {
                View v = mNewShortcutAnimateViews.get(i);
                ValueAnimator bounceAnim = LauncherAnimUtils.ofPropertyValuesHolder(v,
                        PropertyValuesHolder.ofFloat("alpha", 1f),
                        PropertyValuesHolder.ofFloat("scaleX", 1f),
                        PropertyValuesHolder.ofFloat("scaleY", 1f));
                bounceAnim.setDuration(InstallShortcutReceiver.NEW_SHORTCUT_BOUNCE_DURATION);
                bounceAnim.setStartDelay(i * InstallShortcutReceiver.NEW_SHORTCUT_STAGGER_DELAY);
                bounceAnim.setInterpolator(new SmoothPagedView.OvershootInterpolator());
                bounceAnims.add(bounceAnim);
            }
            anim.playTogether(bounceAnims);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mWorkspace != null) {
                        mWorkspace.postDelayed(mBuildLayersRunnable, 500);
                    }
                }
            });
            anim.start();
        }

        // Clean up
        mNewShortcutAnimatePage = -1;
        mNewShortcutAnimateViews.clear();
        new Thread("clearNewAppsThread") {
            public void run() {
                mSharedPrefs.edit()
                            .putInt(InstallShortcutReceiver.NEW_APPS_PAGE_KEY, -1)
                            .putStringSet(InstallShortcutReceiver.NEW_APPS_LIST_KEY, null)
                            .commit();
            }
        }.start();
    }

    @Override
    public void bindSearchablesChanged() {
        boolean searchVisible = updateGlobalSearchIcon();
        boolean voiceVisible = updateVoiceSearchIcon(searchVisible);
        if (mSearchDropTargetBar != null) {
            mSearchDropTargetBar.onSearchPackagesChanged(searchVisible, voiceVisible);
        }
    }

    /**
     * Add the icons for all apps.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindAllApplications(final ArrayList<ApplicationInfo> apps) {
        Runnable setAllAppsRunnable = new Runnable() {
            public void run() {
                if (mAppsCustomizeContent != null) {
                    mAppsCustomizeContent.setApps(apps);
                }
                if(mAppsEditCustomizeContent != null){
                	mAppsEditCustomizeContent.setApps(apps);
                }
            }
        };

        // Remove the progress bar entirely; we could also make it GONE
        // but better to remove it since we know it's not going to be used
        View progressBar = mAppsCustomizeTabHost.
            findViewById(R.id.apps_customize_progress_bar);
        if (progressBar != null) {
            ((ViewGroup)progressBar.getParent()).removeView(progressBar);

            // We just post the call to setApps so the user sees the progress bar
            // disappear-- otherwise, it just looks like the progress bar froze
            // which doesn't look great
            mAppsCustomizeTabHost.post(setAllAppsRunnable);
            mAppsEditCustomizeTabHost.post(setAllAppsRunnable);
        } else {
            // If we did not initialize the spinner in onCreate, then we can directly set the
            // list of applications without waiting for any progress bars views to be hidden.
            setAllAppsRunnable.run();
        }
    }

    /**
     * A package was installed.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindAppsAdded(ArrayList<ApplicationInfo> apps) {
        setLoadOnResume();

        if (mAppsCustomizeContent != null) {
            mAppsCustomizeContent.addApps(apps);
        }
        if(mAppsEditCustomizeContent != null){
        	mAppsEditCustomizeContent.addApps(apps);
        }
    }

    /**
     * A package was updated.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindAppsUpdated(ArrayList<ApplicationInfo> apps) {
        setLoadOnResume();
        if (mWorkspace != null) {
            mWorkspace.updateShortcuts(apps);
        }

        if (mAppsCustomizeContent != null) {
            mAppsCustomizeContent.updateApps(apps);
        }
        if(mAppsEditCustomizeContent != null){
        	mAppsEditCustomizeContent.updateApps(apps);
        }
    }

    /**
     * A package was uninstalled.
     *
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindAppsRemoved(ArrayList<String> packageNames, boolean permanent) {
        if (permanent) {
            mWorkspace.removeItems(packageNames);
        }

        if (mAppsCustomizeContent != null) {
            mAppsCustomizeContent.removeApps(packageNames);
        }
        if(mAppsEditCustomizeContent != null){
        	mAppsEditCustomizeContent.removeApps(packageNames);
        }

        // Notify the drag controller
        mDragController.onAppsRemoved(packageNames, this);
    }

    /**
     * A number of packages were updated.
     */
    public void bindPackagesUpdated() {
        if (mAppsCustomizeContent != null) {
            mAppsCustomizeContent.onPackagesUpdated();
        }
        if(mAppsEditCustomizeContent != null){
        	mAppsEditCustomizeContent.onPackagesUpdated();
        }
    }

    private int mapConfigurationOriActivityInfoOri(int configOri) {
        final Display d = getWindowManager().getDefaultDisplay();
        int naturalOri = Configuration.ORIENTATION_LANDSCAPE;
        switch (d.getRotation()) {
        case Surface.ROTATION_0:
        case Surface.ROTATION_180:
            // We are currently in the same basic orientation as the natural orientation
            naturalOri = configOri;
            break;
        case Surface.ROTATION_90:
        case Surface.ROTATION_270:
            // We are currently in the other basic orientation to the natural orientation
            naturalOri = (configOri == Configuration.ORIENTATION_LANDSCAPE) ?
                    Configuration.ORIENTATION_PORTRAIT : Configuration.ORIENTATION_LANDSCAPE;
            break;
        }

        int[] oriMap = {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT,
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        };
        // Since the map starts at portrait, we need to offset if this device's natural orientation
        // is landscape.
        int indexOffset = 0;
        if (naturalOri == Configuration.ORIENTATION_LANDSCAPE) {
            indexOffset = 1;
        }
        return oriMap[(d.getRotation() + indexOffset) % 4];
    }

    public boolean isRotationEnabled() {
        boolean forceEnableRotation = doesFileExist(FORCE_ENABLE_ROTATION_PROPERTY);
        boolean enableRotation = forceEnableRotation ||
                getResources().getBoolean(R.bool.allow_rotation);
        return enableRotation;
    }
    public void lockScreenOrientation() {
        if (isRotationEnabled()) {
            setRequestedOrientation(mapConfigurationOriActivityInfoOri(getResources()
                    .getConfiguration().orientation));
        }
    }
    public void unlockScreenOrientation(boolean immediate) {
        if (isRotationEnabled()) {
            if (immediate) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            } else {
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    }
                }, mRestoreScreenOrientationDelay);
            }
        }
    }

    
    /*  // hide cling by xiangss 20130821 
     Cling related 
    private boolean isClingsEnabled() {
        // disable clings when running in a test harness
        if(ActivityManager.isRunningInTestHarness()) return false;

        return true;
    }

    private Cling initCling(int clingId, int[] positionData, boolean animate, int delay) {
        final Cling cling = (Cling) findViewById(clingId);
        if (cling != null) {
            cling.init(this, positionData);
            cling.setVisibility(View.VISIBLE);
            cling.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            if (animate) {
                cling.buildLayer();
                cling.setAlpha(0f);
                cling.animate()
                    .alpha(1f)
                    .setInterpolator(new AccelerateInterpolator())
                    .setDuration(SHOW_CLING_DURATION)
                    .setStartDelay(delay)
                    .start();
            } else {
                cling.setAlpha(1f);
            }
            cling.setFocusableInTouchMode(true);
            cling.post(new Runnable() {
                public void run() {
                    cling.setFocusable(true);
                    cling.requestFocus();
                }
            });
            mHideFromAccessibilityHelper.setImportantForAccessibilityToNo(
                    mDragLayer, clingId == R.id.all_apps_cling);
        }
        return cling;
    }

    private void dismissCling(final Cling cling, final String flag, int duration) {
        if (cling != null && cling.getVisibility() == View.VISIBLE) {
            ObjectAnimator anim = LauncherAnimUtils.ofFloat(cling, "alpha", 0f);
            anim.setDuration(duration);
            anim.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    cling.setVisibility(View.GONE);
                    cling.cleanup();
                    // We should update the shared preferences on a background thread
                    new Thread("dismissClingThread") {
                        public void run() {
                            SharedPreferences.Editor editor = mSharedPrefs.edit();
                            editor.putBoolean(flag, true);
                            editor.commit();
                        }
                    }.start();
                };
            });
            anim.start();
            mHideFromAccessibilityHelper.restoreImportantForAccessibility(mDragLayer);
        }
    }

    private void removeCling(int id) {
        final View cling = findViewById(id);
        if (cling != null) {
            final ViewGroup parent = (ViewGroup) cling.getParent();
            parent.post(new Runnable() {
                @Override
                public void run() {
                    parent.removeView(cling);
                }
            });
            mHideFromAccessibilityHelper.restoreImportantForAccessibility(mDragLayer);
        }
    }

    private boolean skipCustomClingIfNoAccounts() {
        Cling cling = (Cling) findViewById(R.id.workspace_cling);
        boolean customCling = cling.getDrawIdentifier().equals("workspace_custom");
        if (customCling) {
            AccountManager am = AccountManager.get(this);
            Account[] accounts = am.getAccountsByType("com.google");
            return accounts.length == 0;
        }
        return false;
    }

     public void showFirstRunWorkspaceCling() {
        // Enable the clings only if they have not been dismissed before
        if (isClingsEnabled() &&
                !mSharedPrefs.getBoolean(Cling.WORKSPACE_CLING_DISMISSED_KEY, false) &&
                !skipCustomClingIfNoAccounts() ) {
            // If we're not using the default workspace layout, replace workspace cling
            // with a custom workspace cling (usually specified in an overlay)
            // For now, only do this on tablets
            if (mSharedPrefs.getInt(LauncherProvider.DEFAULT_WORKSPACE_RESOURCE_ID, 0) != 0 &&
                    getResources().getBoolean(R.bool.config_useCustomClings)) {
                // Use a custom cling
                View cling = findViewById(R.id.workspace_cling);
                ViewGroup clingParent = (ViewGroup) cling.getParent();
                int clingIndex = clingParent.indexOfChild(cling);
                clingParent.removeViewAt(clingIndex);
                View customCling = mInflater.inflate(R.layout.custom_workspace_cling, clingParent, false);
                clingParent.addView(customCling, clingIndex);
                customCling.setId(R.id.workspace_cling);
            }
            initCling(R.id.workspace_cling, null, false, 0);
        } else {
            removeCling(R.id.workspace_cling);
        }
    }
    public void showFirstRunAllAppsCling(int[] position) {
        // Enable the clings only if they have not been dismissed before
        if (isClingsEnabled() &&
                !mSharedPrefs.getBoolean(Cling.ALLAPPS_CLING_DISMISSED_KEY, false)) {
            initCling(R.id.all_apps_cling, position, true, 0);
        } else {
            removeCling(R.id.all_apps_cling);
        }
    }
    public Cling showFirstRunFoldersCling() {
        // Enable the clings only if they have not been dismissed before
        if (isClingsEnabled() &&
                !mSharedPrefs.getBoolean(Cling.FOLDER_CLING_DISMISSED_KEY, false)) {
            return initCling(R.id.folder_cling, null, true, 0);
        } else {
            removeCling(R.id.folder_cling);
            return null;
        }
    }*/
    
    public boolean isFolderClingVisible() {
        Cling cling = (Cling) findViewById(R.id.folder_cling);
        if (cling != null) {
            return cling.getVisibility() == View.VISIBLE;
        }
        return false;
    }
   
    /* // hide cling by xiangss 20130821 
    public void dismissWorkspaceCling(View v) {
        Cling cling = (Cling) findViewById(R.id.workspace_cling);
        dismissCling(cling, Cling.WORKSPACE_CLING_DISMISSED_KEY, DISMISS_CLING_DURATION);
    }
    public void dismissAllAppsCling(View v) {
        Cling cling = (Cling) findViewById(R.id.all_apps_cling);
        dismissCling(cling, Cling.ALLAPPS_CLING_DISMISSED_KEY, DISMISS_CLING_DURATION);
    }
    public void dismissFolderCling(View v) {
        Cling cling = (Cling) findViewById(R.id.folder_cling);
        dismissCling(cling, Cling.FOLDER_CLING_DISMISSED_KEY, DISMISS_CLING_DURATION);
    }*/

    /**
     * Prints out out state for debugging.
     */
    public void dumpState() {
        Log.d(TAG, "BEGIN launcher2 dump state for launcher " + this);
        Log.d(TAG, "mSavedState=" + mSavedState);
        Log.d(TAG, "mWorkspaceLoading=" + mWorkspaceLoading);
        Log.d(TAG, "mRestoring=" + mRestoring);
        Log.d(TAG, "mWaitingForResult=" + mWaitingForResult);
        Log.d(TAG, "mSavedInstanceState=" + mSavedInstanceState);
        Log.d(TAG, "sFolders.size=" + sFolders.size());
        mModel.dumpState();

        if (mAppsCustomizeContent != null) {
            mAppsCustomizeContent.dumpState();
        }
        if (mAppsEditCustomizeContent != null) {
        	mAppsEditCustomizeContent.dumpState();
        }
        Log.d(TAG, "END launcher2 dump state");
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        writer.println(" ");
        writer.println("Debug logs: ");
        for (int i = 0; i < sDumpLogs.size(); i++) {
            writer.println("  " + sDumpLogs.get(i));
        }
    }

    public static void dumpDebugLogsToConsole() {
        Log.d(TAG, "");
        Log.d(TAG, "*********************");
        Log.d(TAG, "Launcher debug logs: ");
        for (int i = 0; i < sDumpLogs.size(); i++) {
            Log.d(TAG, "  " + sDumpLogs.get(i));
        }
        Log.d(TAG, "*********************");
        Log.d(TAG, "");
    }
    
    
    //------------------------ScreenManager----------------------------
    
    /**
     * 关闭屏幕管理页面
     * @param screenIndex 返回到指定workspace页面
     */
    public void closeScreenManager(int screenIndex)
    { 
    	Log.d(TAG, "------closeScreenManager-----screenIndex = " + screenIndex);
    	
    	if (screenIndex >= 0){
//    	  	mWorkspace.setCurrentPage(screenIndex);
    		mWorkspace.snapToPage(screenIndex);
    	}
      
      mDragController.removeDropTarget(mScreenManager);
      
      int width = mDragLayer.getWidth() / 3;
      int height = mDragLayer.getHeight() / 3;
      int count = getCurrentWorkspaceScreen();
      int pivotX = count % 3;
      int pivotY = count / 3;
      
      ScaleAnimation closeAnimation = new ScaleAnimation(1.0F, 3.0F,
    		  1.0F, 3.0F, width * pivotX + width / 2, height * pivotY + height / 2);
      closeAnimation.setDuration(200);
      mScreenManager.startAnimation(closeAnimation);
      
      
      mScreenManager.setVisibility(View.GONE);
      mScreenManager.closeScreenManager();   
      mWorkspace.setVisibility(View.VISIBLE);
      
   // hide hotseat by xiangss 20130805
//      mHotseat.setVisibility(View.VISIBLE);
      
      
      //显示下拉换壁纸    
	  lampCordView.setViewVisible(true);
	  curtain.setViewVisible(true);
    		
	// Show the search bar
      if (mSearchDropTargetBar != null) {
          mSearchDropTargetBar.showSearchBar(false);
      }
      
//      if (PreferenceUtils.getUserGuidePref(this, "pref_userguide_manage_screen_with_fingers") >= 3)
//        return;
//      showUserGuideToast("pref_userguide_manage_screen_with_fingers");
    }
    
    /**
     * 判断是否在屏幕管理页
     * @return
     */
    public boolean isScreenManagerMode()
    {
    	boolean isScreenMode = false;
    	
        if (mScreenManager.getVisibility() == View.VISIBLE){
        	isScreenMode = true;
        }
        
        Log.d(TAG, "isScreenManagerMode--------isScreenMode = " + isScreenMode);
        return isScreenMode;
    }
    
    
    /**
     * 启动屏幕管理页面
     */
	public void showScreenManager() {
		
		Log.d(TAG, "------showScreenManager-----");
		
		if (this.mWorkspaceLoading){
			Log.d(TAG, "showScreenManager---mWorkspaceLoading is ture!");
			return;
		}

		if(mState != State.WORKSPACE){
			Log.d(TAG, "showScreenManager---state is not workspace!");
			return;
		}
		
		if(isScreenManagerMode() == true){
			Log.d(TAG, "showScreenManager---isScreenManagerMode true!");
			return;
		}
		
		//隐藏下拉换壁纸   
		lampCordView.setViewVisible(false);
		curtain.setViewVisible(false);
		
		
		// Hide the search bar
        if (mSearchDropTargetBar != null) {
            mSearchDropTargetBar.hideSearchBar(false);
        }
        
//			changeToNormalMode();
//		mState = State.WORKSPACE;
		
		closeFolder();
		
		
		mScreenManager.showScreenManager(this);
		
		mScreenManager.setVisibility(View.VISIBLE);
		int width = this.mDragLayer.getWidth() / 3;
		int height = this.mDragLayer.getHeight() / 3;
		int count = getCurrentWorkspaceScreen();
		int pivotX = count % 3;
		int pivotY = count / 3;
		ScaleAnimation localScaleAnimation = new ScaleAnimation(2.0F, 1.0F,
				2.0F, 1.0F, width * pivotX + width / 2, height * pivotY + height / 2);
		localScaleAnimation.setDuration(200L);
		
		mDragController.addDropTarget(mScreenManager);
		
		mScreenManager.startAnimation(localScaleAnimation);
	
		mWorkspace.setVisibility(View.GONE);
		
		// hide hotseat by xiangss 20130805
//		mHotseat.setVisibility(View.GONE);
		
		
//			if (PreferenceUtils.getUserGuidePref(this,
//					"pref_userguide_set_homescreen") >= 3)
//				continue;
//			showUserGuideToast("pref_userguide_set_homescreen");
//			if (PreferenceUtils.getUserGuidePref(this,
//					"pref_userguide_edit_screen_seq") >= 3)
//				continue;
		
		
//		mHandler.postDelayed(new Runnable() {
//			public void run() {
//				if (Launcher.this.mHomeDestroyed){
//
////						Launcher.this
////								.showUserGuideToast("pref_userguide_edit_screen_seq");
//				}
//			}
//		}, 3000L);
	}
    
	
	 /**
	  * 判断是否启动开机向导   
	  */
    private void firstStartup(){   	
      	 
    	boolean isFirstStartup = mSharedPrefs.getBoolean(ConstantUtil.FIRST_STARTUP_FLAG, true);
	  	  
    	Log.d(TAG, "firstStartup------isFirstStartup = " + isFirstStartup);      
    	if(isFirstStartup == true){      	   	
    		mSharedPrefs.edit().putBoolean(ConstantUtil.FIRST_STARTUP_FLAG, false).commit(); 		
    		
    		Intent intent = new Intent();
    		intent.setClassName("com.tctmobile.guide", "com.tctmobile.guide.StartGuideActivity");	
    		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		try {
    			startActivity(intent);
    		} catch (android.content.ActivityNotFoundException anf) {
    			Log.e(TAG, "android.content.ActivityNotFoundException");
    		} catch (Exception e) {
    			Log.e(TAG, e.toString());
    		}
    	
    		try {
    			Log.d(TAG, "firstStartup----sleep start!");
    			Thread.sleep(2000);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			Log.e(TAG, "launchView----Thread sleep err!!!");
    			e.printStackTrace();
    		}  
    		Log.d(TAG, "firstStartup----sleep end!");		
    	}      
      Log.d(TAG, "isFirstStartup------end");
    }
    
    
    /**
     * 为了应用匣子和主界面其他弹出框互斥，添加此方法供应用匣子调用  
     */
    public void hideAllOverlayWindow(){
    	Log.d(TAG, "hideAllOverlayWindow------start");
    	if(isEditState()){
    		exitEditStatus(false);
    	}
    	closeMenuDialogAndWallpaper();
    	Log.d(TAG, "hideAllOverlayWindow------end");
    }
    
    //返回壁纸是否正在显示
    private boolean wallpaperShow(){
    	return wallpaperShow;
    }      
    
    //解决菜单或壁纸和应用匣子的互斥问题       
    private void closeMenuDialogAndWallpaper(){
    	
    	Log.d(TAG, "closeMenuDialogAndWallpaper------end");
    	
    	//如果launcher页面菜单正在显示，将其关闭         
    	if(launcher_Menudialog != null && launcher_Menudialog.isShowing()){
    		Log.d(TAG, "close------myMenudialog1");
    		launcher_Menudialog.dismiss();
    		return;
    	}
    	
    	//如果allApp页面菜单正在显示，将其关闭        
    	if(allapp_Menudialog != null && allapp_Menudialog.isShowing()){
    		Log.d(TAG, "close------allapp_Menudialog2");
    		allapp_Menudialog.dismiss();
    		return;
    	}
    	//如果allApp页面应用排序对话框正在显示，将其关闭     
    	if(appSortDialog != null && appSortDialog.isShowing()){
    		Log.d(TAG, "close------appSortDialog");
    		appSortDialog.dismiss();
    		return;
    	}    	    	
    	
    	//如果本地壁纸 正在显示，将其关闭             
    	if(wallpaperShow()){ 
    		Log.d(TAG, "close------localwallpaper");
    		adapter.setCurItem(-1);
	        wallpaer_layout.setVisibility(View.GONE);  
	        wallpaperShow = false;
	        return;
    	}
    }
    
    private void exitEditStatus(boolean animated){
    	showWorkspace(animated);
    }
}



interface LauncherTransitionable {
    View getContent();
    void onLauncherTransitionPrepare(Launcher l, boolean animated, boolean toWorkspace);
    void onLauncherTransitionStart(Launcher l, boolean animated, boolean toWorkspace);
    void onLauncherTransitionStep(Launcher l, float t);
    void onLauncherTransitionEnd(Launcher l, boolean animated, boolean toWorkspace);
}
