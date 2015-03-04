package com.inveno.piflow.tools.commontools;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

import com.inveno.piflow.R;

/**
 * 点击更多按钮弹出来的“更多选项”
 * 
 * @author mingsong.zhang
 * @date 2012-07-20
 * @update 2012-12-19 hongchang.liu 增加显示设置的window
 */
public class PopupWindowTools {
	private PopupWindow window;

	/** 图片id **/
	private int[] mIds = { R.drawable.allset_set }; // 这里设置了些默认数据 避免报错

	/** 与图片对应的 每一项名字 **/
	private String[] mTitles = { "全部设置" }; // 这里设置了些默认数据 避免报错

	/** 图 **/
	private static final String DRAWABLE = "drawable";

	/** 名字 **/
	private static final String TITLE = "title";

	private SetOnItemClickListener mSetOnItemClickListener;

	public PopupWindowTools() {

	}

	/**
	 * 
	 * @param mIds
	 *            图片id数组
	 * @param mTitles
	 *            每个图片对应的每一项名字数组
	 */
	public PopupWindowTools(int[] mIds, String[] mTitles) {
		this.mIds = mIds;
		this.mTitles = mTitles;
	}

	/**
	 * 
	 * @param mIds
	 *            图片id数组
	 * @param mTitles
	 *            每个图片对应的每一项名字数组
	 * @param mSetOnItemClickListener
	 *            实例化这个类，重写里面的点击事件方法来设置点击事件
	 */
	public PopupWindowTools(int[] mIds, String[] mTitles,
			SetOnItemClickListener mSetOnItemClickListener) {
		this.mIds = mIds;
		this.mTitles = mTitles;
		this.mSetOnItemClickListener = mSetOnItemClickListener;
	}

	public int[] getmIds() {
		return mIds;
	}

	public void setmIds(int[] mIds) {
		this.mIds = mIds;
	}

	public String[] getmTitles() {
		return mTitles;
	}

	public void setmTitles(String[] mTitles) {
		this.mTitles = mTitles;
	}

	/**
	 * 创建更多选项的PopupWindow
	 * 
	 * @author mingsong.zhang
	 * @date 2012-07-20
	 * @param context
	 * @param width
	 * @param height
	 * @return PopupWindow
	 */
	public PopupWindow createWindow(Context context, int width, int height) {
		ListView contentView = (ListView) LayoutInflater.from(context).inflate(
				R.layout.channel_popwindow_menu, null);
		window = new PopupWindow(contentView, width, height);

		window.setFocusable(true);
		window.setOutsideTouchable(true);
		window.setBackgroundDrawable(new BitmapDrawable(context.getResources()));

		/** 这里写每一项的点击事件 **/
		contentView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mSetOnItemClickListener.setOnItemClick(parent, view, position,
						id);
				window.dismiss();

			}

		});

		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>(
				10);
		int len = mIds.length;
		for (int i = 0; i < len; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>(2);
			map.put(DRAWABLE, mIds[i]);
			map.put(TITLE, mTitles[i]);
			data.add(map);
		}
		String[] from = { DRAWABLE, TITLE };
		int[] to = { R.id.channel_popwindow_ImageView,
				R.id.channel_popwindow_TextView };
		SimpleAdapter adapter = new SimpleAdapter(context, data,
				R.layout.channel_popwindow_menu_item, from, to);
		contentView.setAdapter(adapter);

		return window;
	}

	/**
	 * 设置more弹窗的点击事件
	 * 
	 * @param setOnItemClickListener
	 */
	public void setOnItemClickListener(
			SetOnItemClickListener setOnItemClickListener) {
		mSetOnItemClickListener = setOnItemClickListener;
	}

	/**
	 * 
	 * @author mingsong.zhang
	 * @date 2012-08-24
	 */
	public interface SetOnItemClickListener {
		/**
		 * 重写这个方法来设置点击事件
		 * 
		 * @param parent
		 * @param view
		 * @param position
		 * @param id
		 */
		void setOnItemClick(AdapterView<?> parent, View view, int position,
				long id);
	}

}
