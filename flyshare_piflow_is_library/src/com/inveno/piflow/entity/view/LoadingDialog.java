package com.inveno.piflow.entity.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.Window;

import com.inveno.piflow.R;
import com.inveno.piflow.tools.commontools.Tools;
/**
 * 
 * @author hongchang.liu
 * @date 2012-
 */
public class LoadingDialog extends Dialog{

	private Window window=null;
	static int style;
	static {
		style=Build.VERSION.SDK_INT >=14?android.R.style.Theme_Holo_Dialog_NoActionBar:R.style.Dialog;
	}
	/**
	 * 
	 * @param context
	 * @param canCelable 1该dialog不可取消,否则可以取消.
	 */
	public LoadingDialog(Context context,int canCelable) {
		
		super(context,style);
		
		setContentView(R.layout.loading_dialog);
		window=getWindow();
		window.setBackgroundDrawableResource(R.color.transparent);
		if (canCelable==1) {
			setCancelable(false);
		}
		else
		{
			setCancelable(true);
		}
		
		setCanceledOnTouchOutside(false);
	}
	

	public void showDialog(){
		
		
		if (!this.isShowing()) {
			try {
				show();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		}
		
		
		
	}
	
	public void clearDialog()
	{
		if (this.isShowing()) {
			try {
				dismiss();
			} catch (Exception e) {
				Tools.showLog("exception", "dialog失去载体报错：");
				e.printStackTrace();
			}
			
		}
	}
	
	

}
