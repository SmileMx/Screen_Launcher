package com.inveno.piflow.entity.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;

import com.inveno.piflow.R;
import com.inveno.piflow.tools.commontools.Tools;

public class TtsSettingFragment extends DialogFragment implements
		OnClickListener, OnCheckedChangeListener {

	private ImageButton settingBtn;
	private CheckBox askBox;
	private Button okBtn;
	private Context context;

	/** 来自资讯主界面还是资讯详细，1为前者、2为后者 **/
	private int fromWhere;
	private int width;
	
	public TtsSettingFragment(int from) {
		Bundle bundle = new Bundle();
		bundle.putInt("fromWhere", from);
		setArguments(bundle);
	}

	
	public TtsSettingFragment(){}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setStyle(STYLE_NO_TITLE, R.style.Dialog);
		context = getActivity();
		fromWhere = getArguments().getInt("fromWhere");
		width=context.getResources().getInteger(R.integer.comment_report_dialog_width);
		super.onCreate(savedInstanceState);
	}
	


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.wpc_tts_setting_dialog,
				container, false);
		
		
//		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(width,LayoutParams.WRAP_CONTENT);
//		view.setLayoutParams(params);
		settingBtn = (ImageButton) view.findViewById(R.id.tts_setting);
		askBox = (CheckBox) view.findViewById(R.id.tts_nomore);
		okBtn = (Button) view.findViewById(R.id.positiveButton);
		settingBtn.setOnClickListener(this);
		okBtn.setOnClickListener(this);
		askBox.setOnCheckedChangeListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id== R.id.tts_setting){

			// 前往系统设置语音界面
			Intent intent = new Intent();
			intent.setAction("com.android.settings.TTS_SETTINGS");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}else if(id == R.id.positiveButton){

//			Tools.showLog("tts", "点击了开始朗读");
//			((BaseFragmentActivity) context).setSpeaking(true);
//			((BaseFragmentActivity) context).resetMenuBtnImg(
//					R.drawable.popup_btn_previous, R.drawable.popup_btn_stop,
//					R.drawable.popup_btn_next, R.drawable.popup_btn_set);
//			
//			if (fromWhere == 1) {
//				((NewsMainActivity) context).setTvSpeakColor(null);
//				((NewsMainActivity) context).showSpeakIcon();
//			}else{
//				((NewsCommonActivity) context).showSpeakIcon();
//			}
//			TtsBiz.getInstance(getActivity()).start();
			positiveListener.onClickPositiveBtn();
			this.dismiss();
			
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if (isChecked) {

			Tools.setBooleaninformain(Tools.TTS_NOMORE_ASK, true, context);

		} else {
			Tools.setBooleaninformain(Tools.TTS_NOMORE_ASK, false, context);
		}

	}

	
	public interface onPositiveListener{
		void onClickPositiveBtn();
	}
	
	private onPositiveListener positiveListener;

	public void setPositiveListener(onPositiveListener positiveListener) {
		this.positiveListener = positiveListener;
	}
	
	
	
}
