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
 * Custom Dialog for menu
 * @author luoss
 *
 */
public class LauncherMenuDialog extends Dialog {

	private static final String TAG = "LauncherMenuDialog";
	private Button menu_btn_wallpaper_setting;
	private Button menu_btn_add_to_screen;
	private Button menu_btn_screen_manager;
	
	private Launcher mLauncher;
	
	
	public LauncherMenuDialog(Context context) {
		super(context,R.style.CustomDialog);
		mLauncher = (Launcher)context;
		// TODO Auto-generated constructor stub
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu1_layout);
		findView();
		initData();
	}
	
	private void findView(){
		menu_btn_wallpaper_setting = (Button)findViewById(R.id.menu_btn_wallpaper_setting);
    	menu_btn_add_to_screen = (Button)findViewById(R.id.menu_btn_add_to_screen);
    	menu_btn_screen_manager = (Button)findViewById(R.id.menu_btn_screen_manager);
   	
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
		
		menu_btn_wallpaper_setting.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "menu_btn_wallpaper_setting");
				dismiss();
				mLauncher.ShowWallpaper();
			}
		});
		menu_btn_add_to_screen.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "menu_btn_add_to_screen");
				dismiss();
				mLauncher.EditState();
			}
		});
		menu_btn_screen_manager.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "menu_btn_screen_manager");
				dismiss();
				mLauncher.showScreenManager();
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
