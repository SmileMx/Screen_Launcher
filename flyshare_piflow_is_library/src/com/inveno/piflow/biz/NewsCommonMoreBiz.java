package com.inveno.piflow.biz;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.inveno.piflow.R;
import com.inveno.piflow.tools.commontools.Const;
import com.inveno.piflow.tools.commontools.DeviceConfig;
import com.inveno.piflow.tools.commontools.PopupWindowTools;
import com.inveno.piflow.tools.commontools.PopupWindowTools.SetOnItemClickListener;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 资讯列表页更多选项
 * 
 * @author mingsong.zhang
 * @date 2012-08-28
 * @update hongchang.liu
 * @date 2013-01-04
 */
public class NewsCommonMoreBiz {

	private Context mContext;

	/** 图片id **/
	private int[] mIds = { R.drawable.more_search_history,
			R.drawable.more_settings };

	/** 与图片对应的 每一项名字 **/
	private String[] mTitles = { "历史记录", "显示设置" };

	private PopupWindowTools mPopupWindowTools;

	/** 更多选项 **/
	private PopupWindow mPopupWindow;

	private PopupWindow fontAndModeWindow;

	private DeviceConfig deviceConfig;

	private DayNightModeBiz dayNightModeBiz;

	/** 显示位置的x偏移量 **/
	private int x;

	/** 显示位置的y偏移量 **/
	private int y;

	// private Button fontAdd;
	// private Button fontCut;
	// private TextView topBg;
	// private RelativeLayout fontBg;
	// private RelativeLayout modeBg;
	// private CheckBox modeCb;

	// private static NewsCommonMoreBiz mNewsCommonMoreBiz;

	private ArrayList<FontSizeChange> sizeChanges;

	private PopupWindow ttsSettingPop;

	private Resources resources;

	public NewsCommonMoreBiz(Context context) {
		this.mContext = context;
		sizeChanges = new ArrayList<NewsCommonMoreBiz.FontSizeChange>();
		deviceConfig = DeviceConfig.getInstance(context);
		dayNightModeBiz = DayNightModeBiz.getInstance(context);
		resources = context.getResources();
	};

	// /**
	// * 获取对象，线程安全，并且效率高，能有多个线程访问！
	// * @param context
	// * @return
	// */
	// public static NewsCommonMoreBiz getInstance(Context context) {
	//
	// if (mNewsCommonMoreBiz == null) {
	//
	// Tools.showLog("pf", "mNewsCommonMoreBiz 实例化！！");
	// synchronized (NewsCommonMoreBiz.class) {
	//
	// if (mNewsCommonMoreBiz == null) {
	// mNewsCommonMoreBiz = new NewsCommonMoreBiz(context);
	// }
	//
	// }
	//
	// }
	//
	// return mNewsCommonMoreBiz;
	//
	// }

	/**
	 * @author hongchang.liu
	 * @data 2012-10-16
	 * @param activity
	 *            加入以便startActivityForResult
	 * @return
	 */
	public PopupWindow createWindow(final Activity activity, final int fromWhere) {
		mPopupWindowTools = new PopupWindowTools();
		// 设置点击事件
		mPopupWindowTools.setOnItemClickListener(new SetOnItemClickListener() {

			@Override
			public void setOnItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {

				case 0:
					// 历史记录
					break;

				case 1:
					// PvBasicStateDataBiz.BASEFLAG = false;
					// Intent intentAllSet = new Intent(mContext,
					// AllSetActivity.class);
					// intentAllSet.putExtra(NewsCommonActivity.GET_FROMWHERE_KEY,
					// fromWhere);
					// NewsHistoryActivity.mCanFinish = true;
					// activity.startActivityForResult(intentAllSet, 120);

					Tools.showLog("hzj", "getX():" + getX());
					Tools.showLog("hzj", "getY():" + getY());

					if (fontAndModeWindow != null) {
						fontAndModeWindow
								.showAtLocation(activity.getWindow()
										.getDecorView(), Gravity.BOTTOM,
										getX(), getY());
					} else {
						createStyleWindow();
						fontAndModeWindow
								.showAtLocation(activity.getWindow()
										.getDecorView(), Gravity.BOTTOM,
										getX(), getY());
					}

					break;
				}
			}
		});

		mPopupWindowTools.setmIds(mIds);
		mPopupWindowTools.setmTitles(mTitles);

		mPopupWindow = mPopupWindowTools.createWindow(
				mContext,
				Integer.parseInt(mContext.getResources().getString(
						R.string.news_common_more_popupwindow_width)),
				Integer.parseInt(mContext.getResources().getString(
						R.string.news_common_more_popupwindow_height))); // 创建更多选项

		createStyleWindow();
		return mPopupWindow;

	}

	/**
	 * 创建显示设置的popWindow
	 */
	private void createStyleWindow() {
		// View window = LayoutInflater.from(mContext).inflate(
		// R.layout.news_detail_popwindow, null);
		//
		// fontAdd = (Button) window.findViewById(R.id.news_datail_pop_addbtn);
		// fontCut = (Button) window.findViewById(R.id.news_datail_pop_cutbtn);
		// topBg = (TextView) window.findViewById(R.id.news_datail_pop_topbg);
		// fontTxt=(TextView) window.findViewById(R.id.news_datail_pop_font);
		// modeTxt=(TextView)window.findViewById(R.id.news_datail_pop_mode);
		// fontBg = (RelativeLayout) window
		// .findViewById(R.id.news_datail_pop_fontbg);
		// modeBg = (RelativeLayout) window
		// .findViewById(R.id.news_datail_pop_modebg);
		// modeCb = (CheckBox) window.findViewById(R.id.news_datail_pop_modecb);
		//
		// if (dayNightModeBiz.getMode() == 2) {
		// nightModeWindow();
		// fontTxt.setTextColor(dayNightModeBiz.getBlackNotRead());
		// modeTxt.setTextColor(dayNightModeBiz.getBlackNotRead());
		// } else {
		// dayModeWindow();
		// fontTxt.setTextColor(dayNightModeBiz.getBlack());
		// modeTxt.setTextColor(dayNightModeBiz.getBlack());
		// }
		//
		// fontAdd.setOnClickListener(ClickListener);
		// fontCut.setOnClickListener(ClickListener);
		// topBg.setOnClickListener(ClickListener);
		// modeCb.setOnCheckedChangeListener(BoxChangeListener);
		//
		// fontAndModeWindow = new PopupWindow(window, deviceConfig.w,
		// Math.abs(deviceConfig.h - getY()));
		// fontAndModeWindow.setFocusable(true);
		// fontAndModeWindow.setOutsideTouchable(true);
		// fontAndModeWindow.setBackgroundDrawable(new BitmapDrawable());

	}

	// private OnCheckedChangeListener BoxChangeListener = new
	// OnCheckedChangeListener() {
	//
	// @Override
	// public void onCheckedChanged(CompoundButton buttonView,
	// boolean isChecked) {
	//
	// if (isChecked) {
	// dayNightModeBiz.setMode(2);
	// nightModeWindow();
	//
	// fontTxt.setTextColor(dayNightModeBiz.getBlackNotRead());
	// modeTxt.setTextColor(dayNightModeBiz.getBlackNotRead());
	// if (modeChange != null) {
	// modeChange.onModeChangeListener(2);
	// }
	//
	// } else {
	// dayNightModeBiz.setMode(1);
	// dayModeWindow();
	// fontTxt.setTextColor(dayNightModeBiz.getBlack());
	// modeTxt.setTextColor(dayNightModeBiz.getBlack());
	// if (modeChange != null) {
	// modeChange.onModeChangeListener(1);
	// }
	// }
	//
	// }
	// };

	// private void dayModeWindow() {
	// fontAdd.setBackgroundResource(R.drawable.btn_day_font_add);
	// fontCut.setBackgroundResource(R.drawable.btn_day_font_cut);
	// topBg.setBackgroundColor(dayNightModeBiz.getAllTransparentWhite());
	// fontBg.setBackgroundColor(dayNightModeBiz.getWhite());
	// modeBg.setBackgroundColor(dayNightModeBiz.getWhite());
	// modeCb.setChecked(false);
	// }
	//
	// private void nightModeWindow() {
	// fontAdd.setBackgroundResource(R.drawable.btn_night_font_add);
	// fontCut.setBackgroundResource(R.drawable.btn_night_font_cut);
	// topBg.setBackgroundColor(dayNightModeBiz.getAllTransparentBlack());
	// fontBg.setBackgroundColor(dayNightModeBiz.getBlack());
	// modeBg.setBackgroundColor(dayNightModeBiz.getBlack());
	// modeCb.setChecked(true);
	// }

	/**
	 * 当用户调节字体大小时的回调接口
	 * 
	 * @author hongchang.liu
	 * 
	 */
	public interface FontSizeChange {
		/**
		 * 当用户调节字体大小时的回调接口
		 * 
		 * @param fontSize
		 *            字体大小
		 */
		void onFontSizeChangeListener(int fontSize);
	}

	// private FontSizeChange fontSizeChange;

	public void setFontSizeChange(FontSizeChange fontSizeChange) {

		if (fontSizeChange != null) {
			sizeChanges.add(fontSizeChange);
		}

	}

	public void removeFontSizeChange(FontSizeChange fontSizeChange) {

		if (fontSizeChange != null) {
			sizeChanges.remove(fontSizeChange);
		}

	}

	public void removeAllFontSizeChange() {

		sizeChanges.clear();

	}

	// /**
	// * 日夜模式监听
	// *
	// * @author Administrator
	// *
	// */
	// public interface ModeChange {
	// /**
	// * 用户改变模式
	// *
	// * @param mode
	// * 1为日间模式,2为夜间模式
	// */
	// void onModeChangeListener(int mode);
	// }
	//
	// private ModeChange modeChange;
	//
	// public void setModeChange(ModeChange modeChange) {
	// this.modeChange = modeChange;
	// }

	public int getX() {
		x = Integer.parseInt(mContext.getResources().getString(
				R.string.news_common_more_popupwindow_showatlocation_x));
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		y = Integer.parseInt(mContext.getResources().getString(
				R.string.news_common_more_popupwindow_showatlocation_y));
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	/**
	 * 创建字体大小的PopupWindow
	 * 
	 * @author fei.zhang
	 * @date 2013-01-15
	 * @return
	 */
	public PopupWindow createTextPoputWindow() {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.news_detail_popuwindow, null);
		final TextView textView = (TextView) view.findViewById(R.id.tvSize);
		SeekBar fontBar = (SeekBar) view.findViewById(R.id.sb);
		final SharedPreferences preferences = mContext.getSharedPreferences(
				Const.ALLSETTINGS, Context.MODE_PRIVATE);
		int oldFontSize = preferences.getInt(Const.FLYSHARE_FONTSIZE, 18);
		fontBar.setMax(49);
		fontBar.setProgress(oldFontSize);
		textView.setText("" + (oldFontSize));
		fontBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				textView.setText("" + (progress + 1));
				preferences.edit()
						.putInt(Const.FLYSHARE_FONTSIZE, (progress + 1))
						.commit(); // 进度条改变时改变存储数据

				for (int i = 0; i < sizeChanges.size(); i++) {
					sizeChanges.get(i).onFontSizeChangeListener(progress + 1);
				}
				// fontSizeChange.onFontSizeChangeListener((progress+1));
			}
		});

		PopupWindow popupWindow = new PopupWindow(view, deviceConfig.w,
				mContext.getResources().getInteger(
						R.integer.menu_popupwindow_height));
		// popupWindow.setOutsideTouchable(true);
		// popupWindow.setBackgroundDrawable(new BitmapDrawable());
		return popupWindow;
	}

	/**
	 * 创建亮度的调整popupWindow
	 * 
	 * @author fei.zhang
	 * @date 2013-01-15
	 * @return
	 */
	public PopupWindow createLightPoputWindow() {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.news_detail_popuwindow, null);
		final TextView textView = (TextView) view.findViewById(R.id.tvSize);
		SeekBar lightBar = (SeekBar) view.findViewById(R.id.sb);
		lightBar.setMax(225);
		final SharedPreferences preferences = mContext.getSharedPreferences(
				Const.ALLSETTINGS, Context.MODE_PRIVATE);
		int oldBrightness = preferences.getInt(Const.FLYSHARE_LIGHTSET, 125);
		lightBar.setProgress(oldBrightness);
		int progress = (int) ((oldBrightness / 225.0f) * 100);
		textView.setText(progress + "%");
		PopupWindow popupWindow = new PopupWindow(view, deviceConfig.w,
				mContext.getResources().getInteger(
						R.integer.menu_popupwindow_height));
		lightBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					int newprogess = (int) ((progress / 225.0f) * 100);
					textView.setText(newprogess + "%");
					lightSetListener.setLight(progress);
					preferences.edit()
							.putInt(Const.FLYSHARE_LIGHTSET, progress).commit();
				}
			}
		});
		// popupWindow.setOutsideTouchable(true);
		// popupWindow.setBackgroundDrawable(new BitmapDrawable());
		return popupWindow;
	}

	private SeekBar speechBar;
	private SeekBar volumeBar;
	// private CheckBox modeBox;
	// private TextView modeTv;
	private TextView speechTv;
	private TextView volumeTv;

	private RelativeLayout speechLy;
	private RelativeLayout volumeLy;
	// private RelativeLayout modeLy;

	private View emptyBg;
	private View ttsSettingView;

	/***
	 * 语音设置popwindow
	 * 
	 * @return
	 */
	public PopupWindow createTtsSettingWindow(int maxVolume, int currentVolume,
			int speech, boolean wetherCheck) {

		if (ttsSettingPop == null) {
			ttsSettingView = LayoutInflater.from(mContext).inflate(
					R.layout.wpc_tts_setting, null);
			ttsSettingPop = new PopupWindow(
					ttsSettingView,
					deviceConfig.w,
					deviceConfig.h
							- resources
									.getInteger(R.integer.menu_popupwindow_height));
			speechBar = (SeekBar) ttsSettingView
					.findViewById(R.id.tts_speech_sb);
			volumeBar = (SeekBar) ttsSettingView
					.findViewById(R.id.tts_volume_sb);
			volumeBar.setMax(maxVolume);
			speechBar.setMax(20);

			// modeBox = (CheckBox)
			// ttsSettingView.findViewById(R.id.tts_mode_cb);
			//
			// modeTv = (TextView)
			// ttsSettingView.findViewById(R.id.tts_mode_tv);
			speechTv = (TextView) ttsSettingView
					.findViewById(R.id.tts_speech_tv);
			volumeTv = (TextView) ttsSettingView
					.findViewById(R.id.tts_volume_tv);

			// modeLy = (RelativeLayout)
			// ttsSettingView.findViewById(R.id.tts_ly1);
			volumeLy = (RelativeLayout) ttsSettingView
					.findViewById(R.id.tts_ly2);
			speechLy = (RelativeLayout) ttsSettingView
					.findViewById(R.id.tts_ly3);
			emptyBg = ttsSettingView.findViewById(R.id.tts_ly4);
			emptyBg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (ttsSettingPop != null) {
						ttsSettingPop.dismiss();
					}
				}
			});
			OnSeekBarChangeListener barChangeListener = new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// TODO Auto-generated method stub
					if (fromUser) {
						if (seekBar.getId() == R.id.tts_speech_sb) {
							onPopTtsSeekBarChangeListener.onTtsProgressChanged(
									seekBar, progress, fromUser);
						} else if (seekBar.getId() == R.id.tts_volume_sb) {
							onPopTtsSeekBarChangeListener.onTtsProgressChanged(
									seekBar, progress, fromUser);

						}
					}
				}
			};

			// OnCheckedChangeListener checkedChangeListener = new
			// OnCheckedChangeListener() {
			//
			// @Override
			// public void onCheckedChanged(CompoundButton buttonView,
			// boolean isChecked) {
			// // TODO Auto-generated method stub
			// onPopTtsModeChangeListener.onTtsCheckedChanged(isChecked);
			// if (isChecked) {
			// modeTv.setText("模式：朗读全文");
			// } else {
			// modeTv.setText("模式：朗读标题");
			// }
			// }
			// };

			speechBar.setOnSeekBarChangeListener(barChangeListener);
			volumeBar.setOnSeekBarChangeListener(barChangeListener);
			// modeBox.setOnCheckedChangeListener(checkedChangeListener);

		}

		if (dayNightModeBiz.getMode() == 1) {
			ttsSettingView.setBackgroundColor(dayNightModeBiz
					.getTransparentBlack());
			// modeTv.setTextColor(resources.getColor(R.color.deep_black));
			speechTv.setTextColor(resources.getColor(R.color.deep_black));
			volumeTv.setTextColor(resources.getColor(R.color.deep_black));

			speechLy.setBackgroundColor(resources.getColor(R.color.white));
			volumeLy.setBackgroundColor(resources.getColor(R.color.white));
			// modeLy.setBackgroundColor(resources.getColor(R.color.white));
		} else {
			ttsSettingView.setBackgroundColor(dayNightModeBiz
					.getAllTransparentBlack());
			// modeTv.setTextColor(resources.getColor(R.color.popuNumColor));
			speechTv.setTextColor(resources.getColor(R.color.popuNumColor));
			volumeTv.setTextColor(resources.getColor(R.color.popuNumColor));

			speechLy.setBackgroundColor(resources
					.getColor(R.color.popuSetTextView));
			volumeLy.setBackgroundColor(resources
					.getColor(R.color.popuSetTextView));
			// modeLy.setBackgroundColor(resources
			// .getColor(R.color.popuSetTextView));
		}
		volumeBar.setProgress(currentVolume);
		speechBar.setProgress(speech);
		// if (wetherCheck) {
		// modeTv.setText("模式：朗读全文");
		// modeBox.setChecked(wetherCheck);
		// } else {
		// modeTv.setText("模式：朗读标题");
		// modeBox.setChecked(wetherCheck);
		// }
		return ttsSettingPop;

	}

	public interface OnPopTtsSeekBarChangeListener {
		void onTtsProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser);
	}

	private OnPopTtsSeekBarChangeListener onPopTtsSeekBarChangeListener;

	public void setOnPopTtsSeekBarChangeListener(
			OnPopTtsSeekBarChangeListener onPopTtsSeekBarChangeListener) {
		this.onPopTtsSeekBarChangeListener = onPopTtsSeekBarChangeListener;
	}

	// public interface OnPopTtsModeChangeListener{
	// void onTtsCheckedChanged(boolean isCheck);
	// }
	//
	// private OnPopTtsModeChangeListener onPopTtsModeChangeListener;
	//
	//
	//
	// public void setOnPopTtsModeChangeListener(
	// OnPopTtsModeChangeListener onPopTtsModeChangeListener) {
	// this.onPopTtsModeChangeListener = onPopTtsModeChangeListener;
	// }

	/** 更改亮度的接口 */
	public interface OnPopLightSetListener {
		void setLight(int progress);
	}

	private OnPopLightSetListener lightSetListener;

	public void setLightSetListener(OnPopLightSetListener lightSetListener) {
		this.lightSetListener = lightSetListener;
	}

}
