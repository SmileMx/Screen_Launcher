package com.inveno.piflow.activity.ext;

import com.inveno.piflow.tools.commontools.Const;


/**
 * 2012-12-27
 * @author chenxu
 *
 */
public interface NewsCommonVariable {


	/** 从专题资讯跳转过来 */
	public static final int FROM_NEWSTOPIC = 31;

	/** 从历史记录跳转过来 */
	public static final int FROM_NEWSHISTORY = 32;

	/** 从状态栏跳转过来 */
	public static final int FROM_STASTUSBAR = 33;

	/** 从资讯主页跳转过来 */
	public static final int FROM_NEWSMAIN = 34;

	/** 从系统消息跳转过来 */
	public static final int FROM_SYSMSG = 35;

	/** 从桌面widget新闻跳转过来 */
	public static final int FROM_WIDGET_NEWS = 36;

	/** 先从桌面widget新闻跳转到历史记录页面，然后再跳转过来 */
	public static final int FROM_WIDGET_NEWS_HISTORY = 37;
	
	/** 从收藏记录跳转过来 */
	public static final int FROM_NEWS_MYFAVORITE = 38;

	public static final String GET_FROMWHERE_KEY = "fromwhere_tonews";

	/** 从intent中获取预先准备好的显示数据的key */
	public static final String NEWSINFO_KEY = "newsInfo";

	/** 从intent中获取预先准备好的显示数据的key */
	public static final String TOPICNEWSINFO_KEY = "topicNewsInfo";

	/** 从intent中取对应详细集合的当前点击的index */
	public static final String GET_INDEX_KEY = "_index";

	/** 接收到广播 需要关闭本activity了 **/
	public static final String NEED_FINISH = Const.PACKAGE_NAME+"need_finish";

	/** 从哪里点击过来 **/
	public static final String GET_FROMWHERE_WIDGET_HISTORY_KEY = "from_widget_history_key";

	
	/**收起键盘*/
	public static final int SHOW_OFF_KEYBOARD = 102;
	/**显示“赞”动画*/
	public static final int SHOW_PRAISE_ANIMATION = 103;
	/**显示键盘*/
	public static final int SHOW_ON_KEYBOARD = 104;
	
	public static final int CALCULATE_SCROLLY=105;
	
	
	/**上传举报成功**/
	public static final int UPLOAD_REPORT_SUCESS = 110;
	/**上传举报失败**/
	public static final int UPLOAD_REPORT_FAILE = 111;
	/**上传回复成功**/
	public static final int UPLOAD_REPLY_SUCESS = 112;
	/**上传回复失败**/
	public static final int UPLOAD_REPLY_FAILE = 113;
	
	/**上传评论成功**/
	public static final int UPLOAD_COMMENT_SUCESS = 114;
	/**上传评论失败**/
	public static final int UPLOAD_COMMENT_FAILE = 115;
	
	/**白天模式**/
	public static final int DAY = 200;
	/**黑夜模式**/
	public static final int NIGHT = 201;
}
