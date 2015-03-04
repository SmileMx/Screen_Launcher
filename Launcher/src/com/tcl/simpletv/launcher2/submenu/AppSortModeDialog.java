package com.tcl.simpletv.launcher2.submenu;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tcl.simpletv.launcher2.Launcher;
import com.tcl.simpletv.launcher2.LauncherApplication;
import com.tcl.simpletv.launcher2.R;
import com.tcl.simpletv.launcher2.utils.ConstantUtil;

public class AppSortModeDialog extends Dialog {

	private static final String TAG = "AppSortModeDialog";
	private RadioGroup app_sort_mode;
	private RadioButton default_sort;
	private RadioButton sort_by_alphabet;
	private RadioButton sort_by_frequency;
	
	private SharedPreferences mSharedPrefs;
	private SharedPreferences.Editor editor;
	private int cur_sort_index;
	
	private Launcher mLauncher;
	
	
	public AppSortModeDialog(Context context) {
		super(context,R.style.CustomDialog);
		mLauncher = (Launcher)context;
		// TODO Auto-generated constructor stub
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sort_mode_dialog_layout);
		mSharedPrefs = LauncherApplication.getPreferenceUtils().getsSharedPreferences();
		cur_sort_index = mSharedPrefs.getInt(ConstantUtil.APP_SORT_MODE, 0);
		
		findView();
		initData();			
	}
	
	private void findView(){
		app_sort_mode = (RadioGroup)findViewById(R.id.app_sort_mode);
		default_sort = (RadioButton)findViewById(R.id.default_sort);
		sort_by_alphabet = (RadioButton)findViewById(R.id.sort_by_alphabet);
		sort_by_frequency = (RadioButton)findViewById(R.id.sort_by_frequency);
   	
		if(cur_sort_index == ConstantUtil.SORT_BY_INSTALLED_TIME){
			default_sort.setChecked(true);
		}else if(cur_sort_index == ConstantUtil.SORT_BY_ALPHABET){
			sort_by_alphabet.setChecked(true);
		}else if(cur_sort_index == ConstantUtil.SORT_BY_FREQUENCY){
			sort_by_frequency.setChecked(true);
		}
		
		app_sort_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
		
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				
				Log.d(TAG, "onCheckedChanged----checkedId = " + checkedId);
				
				if (checkedId == R.id.default_sort) {
					dismiss();
					mLauncher.mAppsCustomizeContent.AppsCustomizeSort(ConstantUtil.SORT_BY_INSTALLED_TIME);
					editor = mSharedPrefs.edit();
					editor.putInt(ConstantUtil.APP_SORT_MODE, ConstantUtil.SORT_BY_INSTALLED_TIME);
					editor.commit();
					Toast.makeText(mLauncher, "Sort by default", Toast.LENGTH_LONG).show();
				} else if (checkedId == R.id.sort_by_alphabet) {
					dismiss();
					mLauncher.mAppsCustomizeContent.AppsCustomizeSort(ConstantUtil.SORT_BY_ALPHABET);
					editor = mSharedPrefs.edit();
					editor.putInt(ConstantUtil.APP_SORT_MODE, ConstantUtil.SORT_BY_ALPHABET);
					editor.commit();
					Toast.makeText(mLauncher, "Sort by Alphabet", Toast.LENGTH_LONG).show();
				} else if (checkedId == R.id.sort_by_frequency) {
					dismiss();
					mLauncher.mAppsCustomizeContent.AppsCustomizeSort(ConstantUtil.SORT_BY_FREQUENCY);
					editor = mSharedPrefs.edit();
					editor.putInt(ConstantUtil.APP_SORT_MODE, ConstantUtil.SORT_BY_FREQUENCY);
					editor.commit();
					Toast.makeText(mLauncher, "Sort by Frequency", Toast.LENGTH_LONG).show();
				}  
			}			
		});
	}
	
	private void initData(){
		Window window = this.getWindow();
		window.setGravity(Gravity.CENTER);
	}
	
	
	/* public void setup(Launcher launcher) {
	        mLauncher = launcher;
	 }*/
	 

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.v(TAG, "dialog onKeyDown event.getKeyCode()========" + event.getKeyCode());
			switch (event.getKeyCode()) {
				case KeyEvent.KEYCODE_MENU:
					if(this.isShowing()){
						Log.d(TAG, "dismiss");
						this.dismiss();
						//return true;
					}
				case KeyEvent.KEYCODE_BACK:
					if(this.isShowing()){
						Log.d(TAG, "dismiss");
						this.dismiss();
						//return true;
					}
				default:
					break;
			}
		return super.onKeyDown(keyCode, event);
	}
	
}
