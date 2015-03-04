package com.inveno.piflow.biz;

import android.content.Context;

import com.inveno.piflow.tools.commontools.Const;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 日夜模式业务类
 * 
 * @author hongchang.liu
 * @date 2012-12-17
 * 
 */
public class DayNightModeBiz {

	private static DayNightModeBiz dayNightModeBiz;
	private Context mContext;
	private int mode;

	private int black;
	private int grey;
	private int white;

	private int blackbg;
	private int blackNotRead;
	private int blackHasRead;

	private int transparentBlack;
	private int transparentWhite;

	private int allTransparentBlack;
	private int allTransparentWhite;
	
	private int dayLine;
	private int nightLine;
	
	private int daySource;
	private int nightSource;
	
	private int commentHeadDay;
	
	private int commentHeadimgBackgroundDay;
	
	private int commentListivewDividerNight;

	private DayNightModeBiz(Context context) {
		this.mContext = context;
		mode =  Tools.getInformain(Const.ALLSETTINGS,
				Const.FLYSHARE_DAY_NIGHT_MODE, 1, mContext);
		initColor(context);
	}

	public synchronized static DayNightModeBiz getInstance(Context context) {
		if (dayNightModeBiz == null) {
			dayNightModeBiz = new DayNightModeBiz(context);
		}

		return dayNightModeBiz;

	}

	/**
	 * 设置模式
	 * 
	 * @param mode
	 *            1白天,2黑夜
	 */
	public void setMode(int mode) {

		Tools.setInformain(Const.ALLSETTINGS, Const.FLYSHARE_DAY_NIGHT_MODE,
				mode, mContext);

		this.mode = mode;
	}

	/**
	 * 获取模式
	 * 
	 * @return mode 1是白天，2是黑夜
	 */
	public int getMode() {

		mode = Tools.getInformain(Const.ALLSETTINGS,
				Const.FLYSHARE_DAY_NIGHT_MODE, 1, mContext);

		return mode;

	}

	/**
	 * 初始化res的color
	 * 
	 * @param context
	 */
	private void initColor(Context context) {
		black = context.getResources().getColor(
				com.inveno.piflow.R.color.black);
		blackbg = context.getResources().getColor(
				com.inveno.piflow.R.color.black_bg);

		blackNotRead = context.getResources().getColor(
				com.inveno.piflow.R.color.black_notread);
		blackHasRead = context.getResources().getColor(
				com.inveno.piflow.R.color.black_hasread);
		white = context.getResources().getColor(
				com.inveno.piflow.R.color.white);
		grey = context.getResources().getColor(
				com.inveno.piflow.R.color.littletext);
		transparentBlack = context.getResources().getColor(
				com.inveno.piflow.R.color.transparent_black);
		transparentWhite = context.getResources().getColor(
				com.inveno.piflow.R.color.transparent_white);
		allTransparentBlack = context.getResources().getColor(
				com.inveno.piflow.R.color.alltransparent_black);
		allTransparentWhite = context.getResources().getColor(
				com.inveno.piflow.R.color.alltransparent_white);
		dayLine=context.getResources().getColor(
				com.inveno.piflow.R.color.day_line);
		nightLine=context.getResources().getColor(
				com.inveno.piflow.R.color.night_line);
		daySource=context.getResources().getColor(
				com.inveno.piflow.R.color.littletext);
		nightSource=context.getResources().getColor(
				com.inveno.piflow.R.color.night_source);
		commentHeadDay=context.getResources().getColor(
				com.inveno.piflow.R.color.comment_head_day);
		commentHeadimgBackgroundDay = context.getResources().getColor(
				com.inveno.piflow.R.color.comment_headimg_background_day);
		commentListivewDividerNight = context.getResources().getColor(
				com.inveno.piflow.R.color.comment_listivew_divider_night);
		
	}
	
	

	public int getDayLine() {
		return dayLine;
	}

	
	public int getNightLine() {
		return nightLine;
	}

	public int getBlack() {
		return black;
	}

	public int getGrey() {
		return grey;
	}

	public int getWhite() {
		return white;
	}

	public int getTransparentBlack() {
		return transparentBlack;
	}

	public int getTransparentWhite() {
		return transparentWhite;
	}

	public int getAllTransparentBlack() {
		return allTransparentBlack;
	}

	public int getAllTransparentWhite() {
		return allTransparentWhite;
	}

	public int getBlackbg() {
		return blackbg;
	}

	public int getBlackNotRead() {
		return blackNotRead;
	}

	public int getBlackHasRead() {
		return blackHasRead;
	}

	public int getDaySource() {
		return daySource;
	}

	public int getNightSource() {
		return nightSource;
	}

	public int getCommentHeadDay() {
		return commentHeadDay;
	}

	public int getCommentHeadimgBackgroundDay() {
		return commentHeadimgBackgroundDay;
	}

	public int getCommentListivewDividerNight() {
		return commentListivewDividerNight;
	}


	
	
}
