package com.inveno.piflow.tools.commontools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 从飞秘v3.20移过来的 时间日期的数据处理类
 * 
 * @author mingsong.zhang
 * @date 2012-09-18
 */
public class DateUtil {

	private static SimpleDateFormat Y_M_D = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 把日期转换成规定格式的字符串
	 * 
	 * @param pattern
	 *            日期格式
	 * @param d
	 *            日期
	 * @return
	 * @author LQY
	 * @date 2011-7-18
	 */
	public static String formatDate(String pattern, java.util.Date d) {
		if (d == null)
			return "";

		return new SimpleDateFormat(pattern).format(d);
	}

	/**
	 * 为某日期添加天数，查询多少天之后的日期
	 * 
	 * 
	 * 
	 * @author LQY
	 * @param date
	 * @param dayNum
	 * @date 2012-4-19
	 * @return
	 */
	public static Date addDayOfMonth(Date date, int dayNum) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, dayNum);
		return cal.getTime();
	}

	/**
	 * 取得某一天的当前时间
	 * 
	 * @author LQY
	 * @date 2012-4-19
	 * @param num
	 *            向前或向后滚动num天，正数向未来，负数向过去
	 * @return
	 */
	public static Date getNowOnOneDay(int num) {
		Calendar cal = Calendar.getInstance();
		cal.roll(Calendar.DATE, num);
		if (num > 0) {
			if (cal.getTime().before(new Date())) {
				cal.add(Calendar.MONTH, 1);
			}
			if (cal.getTime().before(new Date())) {
				cal.add(Calendar.YEAR, 1);
			}
		} else if (num < 0) {
			if (cal.getTime().after(new Date())) {
				cal.add(Calendar.MONTH, -1);
			}
			if (cal.getTime().after(new Date())) {
				cal.add(Calendar.YEAR, -1);
			}
		}
		return cal.getTime();
	}

	/**
	 * 获取多少天后/前的时间
	 **/

	public static Date getNumDayByNow(Date day, int num) {
		if (num > 0)
			day.setTime(day.getTime() + 86400000 * num);
		else
			day.setTime(day.getTime() - 86400000 * num);
		return day;
	}

	/**
	 * 增加一天
	 * 
	 * @date 2012-4-19
	 * @author LQY
	 * @param src
	 * @return
	 */
	public static Calendar add24Hours(Calendar src) {
		src.setTime(new Date(src.getTimeInMillis() + 86400000));
		return src;
	}

	/**
	 * 减去一天
	 * 
	 * @param src
	 * @return
	 * @author LQY
	 * @date 2012-4-19
	 */
	public static Calendar minus24Hours(Calendar src) {
		src.setTime(new Date(src.getTimeInMillis() - 86400000));
		return src;
	}

	/***
	 * 根据日期获取星期 2011-7-29
	 * 
	 * @param dateStr
	 *            yyyy-mm-dd格式
	 */
	public static String getWeekDayByDate(String dateStr) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(format.parse(dateStr));

			int dayForWeek = 0;
			if (c.get(Calendar.DAY_OF_WEEK) == 1) {
				dayForWeek = 7;
			} else {
				dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
			}
			switch (dayForWeek) {
			case 1:
				return "星期一";
			case 2:
				return "星期二";
			case 3:
				return "星期三";
			case 4:
				return "星期四";
			case 5:
				return "星期五";
			case 6:
				return "星期六";
			case 7:
				return "星期天";
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将'yyyy-MM-dd'格式的字符串转化为日期对象
	 * 
	 * @param str
	 *            格式化字符串
	 * @return 日期对象
	 */
	public static Date strToY_M_D(String str) {
		try {
			return Y_M_D.parse(str);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 时间格式化方法 格式:2008-05-20 16:40:40 2008-9-2
	 * 
	 * @return
	 */
	public static String getDateTimeString(Object date) {
		if (date == null)
			return "";
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}

	private static Date setHourToZero(Date date) {
		if (getDateTimeString(date).subSequence(11, 13).equals("12")) {
			date.setTime(date.getTime() - 12 * 60 * 60 * 1000);
		}
		return date;
	}

	/**
	 * 今天0时0分0秒0毫秒
	 * 
	 * @return
	 */
	public static Date getToDay() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.setTime(setHourToZero(cal.getTime()));
		return cal.getTime();
	}

	/**
	 * 解析GTM时间格式
	 */
	public static String getDateTimeByGTM(String datestr) {
		DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",
				Locale.ENGLISH);
		try {
			Date d = format.parse(datestr);
			return formatDate("yyyy-MM-dd hh:mm:ss", d);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * 获取当前是星期几
	 * 
	 * @param dateStr
	 *            yyyy-mm-dd格式
	 */
	public static String getWeekDay() {
		Calendar c = Calendar.getInstance();
		int dayForWeek = 0;
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			dayForWeek = 7;
		} else {
			dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		}
		switch (dayForWeek) {
		case 1:
			return "星期一";
		case 2:
			return "星期二";
		case 3:
			return "星期三";
		case 4:
			return "星期四";
		case 5:
			return "星期五";
		case 6:
			return "星期六";
		case 7:
			return "星期天";
		}
		return "";
	}

	/**
	 * 数据采集到是提取当前系统时间转化为需要上传的时间格式
	 * 
	 * @return
	 */
	public static String formatCurrentTime() {
		SimpleDateFormat simpFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String str = simpFormat.format(Calendar.getInstance().getTime());
		return str;
	}

	/**
	 * 数据采集到是提取当前系统时间转化为需要上传的时间格式
	 * 
	 * @return
	 */
	public static String formatCurrentDay() {
		SimpleDateFormat simpFormat = new SimpleDateFormat("yyyy-MM-dd");
		String str = simpFormat.format(Calendar.getInstance().getTime());
		return str;
	}

	/** 验证时间格式是否为2008-05-20 16:40:40格式 */
	public static boolean checkDateTime(String dateTime) {
		Pattern pattern = Pattern
				.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|(1?[0-9])|(2?[0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
		return pattern.matcher(dateTime).matches();
	}
}
