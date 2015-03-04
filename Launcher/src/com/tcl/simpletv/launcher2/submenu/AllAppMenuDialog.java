package com.tcl.simpletv.launcher2.submenu;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.tcl.simpletv.launcher2.Launcher;
import com.tcl.simpletv.launcher2.R;

/**
 * Custom Dialog2 for menu
 * @author luoss
 *
 */
public class AllAppMenuDialog extends Dialog {

	private static final String TAG = "AllAppMenuDialog";
	private Button menu_btn_delete_app;
	private Button menu_btn_app_sort;
	
	private Launcher mLauncher;
	
	
	public AllAppMenuDialog(Context context) {
		super(context,R.style.CustomDialog);
		mLauncher = (Launcher)context;
		// TODO Auto-generated constructor stub
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu2_layout);
		findView();
		initData();
	}
	
	private void findView(){
		menu_btn_delete_app = (Button)findViewById(R.id.menu_btn_delete_app);
		menu_btn_app_sort = (Button)findViewById(R.id.menu_btn_app_sort);
   	
    	setButtonListener();
	}
	
	private void initData(){
		Window window = this.getWindow();
		window.setGravity(Gravity.CENTER);
	}
	
	
	/* public void setup(Launcher launcher) {
	        mLauncher = launcher;
	 }*/
	 
	private void setButtonListener(){
		
		menu_btn_delete_app.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "menu_btn_wallpaper_setting");
				dismiss();
				mLauncher.mAppsCustomizeContent.onMenuItemClick();
			}
		});
		menu_btn_app_sort.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "menu_btn_add_to_screen");				
				dismiss();
				mLauncher.showSortDialog();
				
			}
		});	
	}
	

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
