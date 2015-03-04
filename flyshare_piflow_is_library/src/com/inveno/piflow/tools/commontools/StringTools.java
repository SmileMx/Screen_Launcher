package com.inveno.piflow.tools.commontools;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * 
 * @author mingsong.zhang
 * @date 2012-08-07
 */
public class StringTools {

	/**
	 * 将字符串数组拼装成用","隔开的字符串,
	 * 
	 * @param str
	 *            需要拼装的数组
	 * @return 需要返回的字符串
	 * 
	 * 
	 */
	public static String getLinkString(String[] array) {
		String str = "";
		StringBuilder sb = new StringBuilder();
		if (null != array && array.length > 0) {
			for (String s : array) {
				sb.append(s).append(",");
			}
			str = sb.toString();
		}
		if (str.indexOf(",") != -1) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	/**
	 * 将字符串数组拼装成用","隔开的字符串,
	 * 
	 * @param str
	 *            需要拼装的数组
	 * @return 需要返回的字符串
	 * 
	 * 
	 */
	public static String getLinkString(Object[] array) {
		String str = "";
		StringBuilder sb = new StringBuilder();
		if (null != array && array.length > 0) {
			for (Object s : array) {
				sb.append(s.toString()).append(",");
			}
			str = sb.toString();
		}
		if (str.indexOf(",") != -1) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	/**
	 * 将字符串List拼装成用"'1','2','3'"隔开的字符串,
	 * 
	 * @param str
	 *            需要拼装的List
	 * @author yaoyuan
	 * @return 需要返回的字符串
	 * 
	 */
	public static String getSqlInClauseByList(String[] strArray) {
		String str = "";
		StringBuffer sb = new StringBuffer();
		if (null != strArray && strArray.length > 0) {
			for (String s : strArray) {
				sb.append("'");
				sb.append(s);
				sb.append("'");
				sb.append(",");
			}
			str = sb.toString();
		}
		if (str.indexOf(",") != -1) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	/** 判断字符是否有内容 为空则返回true **/
	public static boolean isEmpty(String src) {
		if (src == null || src.trim().length() == 0)
			return true;
		else
			return false;
	}

	/** 判断字符是否有内容 不为空返回true **/
	public static boolean isNotEmpty(String src) {
		return !isEmpty(src);
	}

	/**
	 * 将数字格式化为指定的格式的字符 例如pattern为“00000000” ，number为1 则返回00000001
	 * 
	 * 
	 * @param pattern
	 *            格式模板
	 * @param number
	 *            需要格式的数字
	 * @return 格式化后的字符
	 */
	public static String formatNumber(String pattern, long number) {
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(number);
	}

	public static String decodingFromISO8858_1(String str)
			throws UnsupportedEncodingException {
		if (StringTools.isEmpty(str))
			return "";
		return new String(str.getBytes("ISO8859-1"));
	}

	/**
	 * 去掉字符串中html代码 2009-09-01
	 * 
	 * @author
	 * @param htmlstr
	 * @return
	 */

	public static String removeHtmlTag(String htmlstr) {

		if (!"".equals(htmlstr) && htmlstr != null) {
			Pattern pat = Pattern.compile("\\s*<.*?>\\s*", Pattern.DOTALL
					| Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
			Matcher m = pat.matcher(htmlstr);
			// 去掉所有html标记
			String rs = m.replaceAll("");
			rs = rs.replaceAll("&nbsp;", "");
			rs = rs.replaceAll("&lt;", "<");
			rs = rs.replaceAll("&gt;", ">");
			return rs;
		} else {
			return "";
		}

	}
	
	public static String removeHtmlFlow(String htmlstr) {

		if (!"".equals(htmlstr) && htmlstr != null) {
			Pattern pat = Pattern.compile("<p>\\s+</p>", Pattern.DOTALL
					| Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
			Matcher m = pat.matcher(htmlstr);
			// 去掉所有html标记
			String rs = m.replaceAll("");
			rs = rs.replaceAll("&nbsp;", "");
//			rs = rs.replaceAll("&lt;", "<");
//			rs = rs.replaceAll("&gt;", ">");
			return rs;
		} else {
			return "";
		}

	}

	/**
	 * 把s中所有为oldS替换掉 2008-10-25
	 * 
	 * @yaoyuan
	 * @param s
	 *            需要替换的字符串
	 * @param oldS
	 *            s中旧的字符串
	 * @param newS
	 *            代替oldS的字符串
	 * 
	 * @return 返回替换掉后的新字符串
	 */
	public static String replace(String s, String oldS, String newS) {
		StringBuffer buf = new StringBuffer();
		int i = 0;

		while (true) {
			int pos = s.indexOf(oldS, i);

			if (pos == -1) {
				break;
			}

			buf.append(s.substring(i, pos));
			buf.append(newS);
			i = pos + oldS.length();

			if (i >= s.length()) {
				break;
			}
		}

		buf.append(s.substring(i));

		return buf.toString();
	}

	/**
	 * 把数字转换成多少位的字段串
	 * <P>
	 * e.g. add0(36, 6) will return 000036 NOTES: result is undetermine if
	 * number of decimal of given integer is large then desired length of
	 * string.
	 * 
	 * @param v
	 *            需要转换的数字
	 * @param l
	 *            转换的长度
	 * 
	 * @return the result.
	 */
	public static String add0(int v, int l) {
		long lv = (long) Math.pow(10, l);
		return String.valueOf(lv + v).substring(1);
	}

	/**
	 * 字符解码（UTF-8） Dec 1, 2008
	 * 
	 * @author
	 * @param src
	 * @return
	 */
	public static String urlDecode(String src) {
		try {
			return URLDecoder.decode(src, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	/**
	 * 直接出现在URL中要进行编码 2008-12-23
	 * 
	 * @author
	 * @param src
	 * @return
	 */
	public static String urlEncode(String src) {
		try {
			return URLEncoder.encode(src, "UTF-8");
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 把null转为""
	 * 
	 * 2009-3-21
	 * 
	 * @yaoyuan
	 * @param value
	 * @return
	 */
	public static String convertNullValue(String val) {
		if (null == val)
			return "";
		return val;
	}

	/**
	 * 取得当前操作系统的行分割符 2009-5-13
	 * 
	 * @author yaoyuan
	 * @return
	 */
	public static String getLineSeparator() {
		return System.getProperty("line.separator");
	}

	/**
	 * 把String 是否为数字
	 * 
	 * 2009-8-20
	 * 
	 * @yaoyuan
	 * @param value
	 * @return
	 */
	public static boolean isNumber(String val) {
		try {
			Double.parseDouble(val);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * 将iso-88591编码转换成utf8格式
	 * 
	 * @author LQY 2011-5-6
	 */
	public static String convert88591Toutf8(String strSrc) {
		try {
			return new String(strSrc.getBytes("ISO-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	/**
	 * 获取接口url的字符串
	 * 
	 * @param strs
	 * @return
	 */
	public static String getUrlString(String[] strs) {
		StringBuffer strBuffer = new StringBuffer();
		if (strs != null) {
			int len = strs.length;
			for (int i = 0; i < len; i++) {
				strBuffer.append(strs[i]);
			}
			return strBuffer.toString();
		}

		return null;
	}

	/**
	 * 根据字符串分钟时间转化成毫秒数
	 * 
	 * @param minTime
	 * @return
	 */
	public static long getMillisFromMin(String minTime) {
		if (StringTools.isEmpty(minTime))
			return 0;
		else
			return Integer.parseInt(minTime) * 60000;
	}

	/**
	 * 获取url资源名字 包括后缀 eg:http://www.baidu.com/abc/d.jpg 取d.jpg
	 * 
	 * @author mingsong.zhang
	 * @param url
	 * @return 抛异常则返回""
	 */
	public static String getNameFromUrl(String url) {
		String name = "";
		try {
			if (isNotEmpty(url)) {
				name = url.substring(url.lastIndexOf("/") + 1);
			}

		} catch (Exception e) {
			return "";
		}
		return name;
	}

	/**
	 * 获取url资源名字 不包括后缀 eg:http://www.baidu.com/abc/d.jpg 取d
	 * 
	 * @author mingsong.zhang
	 * @param url
	 * @return 抛异常则返回""
	 */
	public static String getNameFromUrlWithoutPostfix(String url) {
		String name = "";
		try {
			if (isNotEmpty(url)) {

				name = url.substring((url.lastIndexOf("/") + 1),
						url.lastIndexOf("."));
			}
		} catch (Exception e) {
			return "";
		}
		return name;
	}

	/**
	 * 计算资讯发布时间与当前时间相差多少分钟或小时或天数,并返回
	 * 
	 * @param time
	 * @return 失败则返回""
	 */
	public static String differTime(String time) {

		if (isNotEmpty(time)) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date date = (Date) sdf.parse(time);
				long theTime = date.getTime();
				long currentTime = System.currentTimeMillis();
				long differTime = currentTime - theTime;
				long differ = 0;
				if ((differ = differTime / 60000) < 60) {
					// return differ + "分钟前";
					return "";
				} else if ((differ = differTime / 3600000) < 24) {
					// return differ + "小时前";
					return "";
				} else if ((differ = differTime / 86400000) >= 1
						&& (differ = differTime / 86400000) <= 3) {
					return differ + "天前";
				} else if ((differ = differTime / 86400000) > 3) {
					return time.substring(5, time.lastIndexOf("-") + 3);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return "";
	}

	/**
	 * 评论及回复的时间
	 * 
	 * @param time
	 * @return
	 */
	public static String commentTime(String time) {

		if (isNotEmpty(time)) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date date = (Date) sdf.parse(time);
				long theTime = date.getTime();
				long currentTime = System.currentTimeMillis();
				long differTime = currentTime - theTime;
				long differ = 0;
				if ((differ = differTime / 60000) < 60) {
					differ = differ == 0 ? 1 : differ;
					return differ + "分钟前";

				} else if ((differ = differTime / 3600000) < 24) {
					return differ + "小时前";

				} else if ((differ = differTime / 86400000) >= 1
						&& (differ = differTime / 86400000) <= 3) {
					return differ + "天前";
				} else if ((differ = differTime / 86400000) > 3) {
					return time.substring(5, time.lastIndexOf("-") + 3);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return "";
	}

	/**
	 * 将字符串按","分开成字符串数组
	 * 
	 * @param str
	 * @return 字符串为空或者报异常则返回null
	 */
	public static String[] splitStringComma(String str) {
		String[] strs = null;
		try {
			if (isNotEmpty(str)) {
				strs = str.split(",");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strs;
	}

	/**
	 * 得到当前系统时间的字符串表示 格式为“XX小时前“
	 * 
	 * @param m
	 * @return
	 */
	public static String getSysTime(long m) {
		long theTime = m;
		long currentTime = System.currentTimeMillis();
		long differTime = currentTime - theTime;
		long differ = 0;
		if ((differ = differTime / 60000) < 60) {
			return differ + "分钟前";
		} else if ((differ = differTime / 3600000) < 24) {
			return differ + "小时前";
		} else if ((differ = differTime / 86400000) >= 1) {
			return differ + "天前";
		}
		return "";
	}

	/**
	 * 获取最高温度
	 * 
	 * @param temp
	 * @return
	 */
	public static String getWeatherHighTemp(String temp) {
		String str = "";
		if (isNotEmpty(temp)) {
			try {
				str = temp.substring(temp.indexOf("~") + 1, temp.length() - 1)
						+ "°";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return str;
	}

	/**
	 * 将字符串中的中文字符转换成utf-8编码
	 * 
	 * @author mingsong.zhang
	 * @param imgUrl
	 * @return
	 */
	public static String encodeUrl(String imgUrl) {
		if (isNotEmpty(imgUrl)) {
			String encodeUrl = imgUrl;
			ArrayList<Character> chs = new ArrayList<Character>(5);
			for (int i = 0; i < imgUrl.length(); i++) {
				char ch = imgUrl.charAt(i);
				int v = (int) ch;
				if (v >= 19968 && v <= 171941) {
					chs.add(ch);
				}
			}

			for (Character character : chs) {
				try {
					encodeUrl = encodeUrl.replace(character.toString(),
							URLEncoder.encode(character.toString(), "utf-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			return encodeUrl;
		}

		return "";
	}

	/**
	 * 正则表达式检查字符串是否为email格式
	 * 
	 * @author blueming.wu
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 正则表达式验证字符串是否为电话号码
	 * 
	 * @author blueming.wu
	 * @param mobiles
	 * @return
	 */
	public static boolean checkMobile(String mobiles) {
		boolean flag = false;
		try {
			Pattern p = Pattern
					.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
			Matcher m = p.matcher(mobiles);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 验证密码是否符合规则，规则为6-13为数字字母组合
	 * 
	 * @author blueming.wu
	 * @param pwd
	 * @return
	 */
	public static boolean checkPwd(String pwd) {
		boolean flag = false;
		try {
			Pattern p = Pattern.compile("^[a-z0-9A-Z]{6,13}$");
			Matcher m = p.matcher(pwd);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 验证密码是否包含非法字符
	 * 
	 * @author blueming.wu
	 * @param pwd
	 * @return
	 */
	public static boolean checkPwdCharacter(String pwd) {
		boolean flag = false;
		try {

			Pattern p = Pattern.compile("^[a-z0-9A-Z]+$");
			Matcher m = p.matcher(pwd);

			flag = m.matches();

			Tools.showLog("lhc", "checkPwdCharacter m.matches:" + flag);
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 将url转换成md5码加密保存
	 * 
	 * @param key
	 * @return
	 */
	public static String generator(String key) {
		String cacheKey;
		try {
			// 创建指定算法
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	/**
	 * 字符数组转换为16进制字符串
	 * 
	 * @param bytes
	 * @return
	 */
	private static String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	/**
	 * 设置数值保留两位小数显示,四舍五入
	 * 
	 * @param num
	 * @param scale
	 * @param roundingMode
	 * @return
	 */
	public static String setSizetoDoble(double num) {

		BigDecimal mData = new BigDecimal(num).setScale(2,
				BigDecimal.ROUND_HALF_UP);
		return mData.toString();

	}

	/**
	 * 验证是否只包含数字，字母，下划线，汉字
	 * 
	 * @param content
	 * @return
	 */
	public static boolean checkInput(String content) {
		boolean flag = false;
		try {
			Pattern p = Pattern.compile("^[a-zA-Z0-9_\u4e00-\u9fa5]+$");
			Matcher m = p.matcher(content);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 将字符串集合拼成以","隔开的字符串
	 * 
	 * @param list
	 * @return
	 */
	public static String getNewString(ArrayList<String> list) {
		String str = "";
		StringBuilder sb = new StringBuilder();
		if (null != list && list.size() > 0) {
			for (String s : list) {
				sb.append(s).append(",");
			}
			str = sb.toString();
		}
		if (str.indexOf(",") != -1) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	/**
	 * 根据Url获取保存apk的名字
	 * 
	 * @param url
	 * @return
	 */
	public static String getFileNameFromUrl(String url) {

		// 名字不能只用这个
		// 通过 ‘？’ 和 ‘/’ 判断文件名
		String extName = "apk";
		String filename;

		filename = hashKeyForDisk(url) + "." + extName;
		return filename;
	}

	/**
	 * 一个散列方法,改变一个字符串(如URL)到一个散列适合使用作为一个磁盘文件名。
	 */
	public static String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	/**
	 * 去掉字符串中html代码 给tts语音朗读
	 * 
	 * @author
	 * @param htmlstr
	 * @return
	 */

	public static String removeHtmlCssForTts(String htmlstr) {

		if (!"".equals(htmlstr) && htmlstr != null) {
			Pattern pat = Pattern.compile("\\s*<.*?>\\s*", Pattern.DOTALL
					| Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
			Matcher m = pat.matcher(htmlstr);
			// 去掉所有html标记
			String rs = m.replaceAll("");
			rs = rs.replaceAll("&nbsp", "");
			rs = rs.replaceAll("&lt;", "<");
			rs = rs.replaceAll("&gt;", ">");
			rs = rs.substring(rs.lastIndexOf("false") + 7);
			rs = rs.trim();
			return rs;
		} else {
			return "";
		}

	}
	/**
	 * 判断是否为http连接地址
	 * @param http
	 */
	public static boolean httpJudge(String http) {
		
		Pattern pattern = Pattern
				.compile("http://(([a-zA-z0-9]|-){1,}\\.){1,}[a-zA-z0-9]{1,}-*");
		Matcher matcher = pattern.matcher(http);
		if (matcher.find()) {
			return true;
		}else{
			return false;
		}
		
	}
	
	/**
	 * 瀑布流用
	 * @param time
	 * @return
	 */
	public static String waterwallTime(String time) {

		if (isNotEmpty(time)) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm");
				Date date = (Date) sdf.parse(time);
				long theTime = date.getTime();
				long currentTime = System.currentTimeMillis();
				long differTime = currentTime - theTime;
				long differ = 0;
				if ((differ = differTime / 60000) < 60) {
					differ = differ == 0 ? 1 : differ;
					return differ + "分钟前";

				} else if ((differ = differTime / 3600000) < 24) {
					return differ + "小时前";

				} else if ((differ = differTime / 86400000) >= 1
						&& (differ = differTime / 86400000) <= 3) {
					return differ + "天前";
				} else if ((differ = differTime / 86400000) > 3) {
//					return time.substring(5, time.lastIndexOf("-") + 3);
					return "";
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return "";
	}

}
