package com.inveno.piflow.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.inveno.piflow.R;
import com.inveno.piflow.biz.DayNightModeBiz;
import com.inveno.piflow.entity.model.showflow.ShowFlowP;
import com.inveno.piflow.tools.bitmap.FlyshareBitmapManager;
import com.inveno.piflow.tools.commontools.DeviceConfig;
import com.inveno.piflow.tools.commontools.StringTools;
import com.inveno.piflow.tools.commontools.Tools;

public class WFNewsDetailAdapter extends BaseAdapter {

	private List<ShowFlowP> showFlowPs;

	private Context context;

	private FlyshareBitmapManager flyshareBitmapManager;

	private int width;
	private ArrayList<String> imgUrls;

	private int size;

	public static boolean isScrolling;

	private int mode;

	private DayNightModeBiz dayNightModeBiz;

	private int fontSize;

	private ListView listView;

	public WFNewsDetailAdapter(Context context, ListView listview,
			List<ShowFlowP> showFlowPs,
			FlyshareBitmapManager flyshareBitmapManager, int imgWidth,
			int dayOrNight, int font, ArrayList<String> imgs) {
		this.context = context;
		this.showFlowPs = showFlowPs;
		this.flyshareBitmapManager = flyshareBitmapManager;
		this.width = imgWidth;
		imgUrls = imgs;
		this.size = showFlowPs.size();
		// for (int i = 0; i < size; i++) {
		// ShowFlowP p = showFlowPs.get(i);
		// if (p.isImg())
		// imgUrls.add(p.getUrl());
		// }
		this.listView = listview;
		this.mode = dayOrNight;
		this.fontSize = font;
		dayNightModeBiz = DayNightModeBiz.getInstance(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return showFlowPs.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return showFlowPs.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder newsHolder;
		if (convertView == null || convertView.getTag() == null) {
			newsHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.wpc_waterfall_newsdetail_list_item, null);

			newsHolder.iv = (ImageView) convertView
					.findViewById(R.id.waterfall_news_item_iv);
			newsHolder.tv = (TextView) convertView
					.findViewById(R.id.waterfall_news_item_tv);

			convertView.setTag(newsHolder);
		} else {
			Tools.showLogA("复用flow详情");
			newsHolder = (ViewHolder) convertView.getTag();
			newsHolder.iv.setImageDrawable(null);
		}

		ShowFlowP p = showFlowPs.get(position);

		boolean isImg = p.isImg();
		if (!isImg) {
			newsHolder.iv.setVisibility(View.GONE);
			Spanned text = Html.fromHtml(p.getContent());
			if (p.isReading()) {
				SpannableString msp = new SpannableString(text);
				msp.setSpan(new BackgroundColorSpan(Color.CYAN), 0,
						text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				newsHolder.tv.setText(msp);
			} else {
				newsHolder.tv.setText(text);
			}

			if (mode == 2) {
				newsHolder.tv.setTextColor(dayNightModeBiz.getBlackNotRead());
			} else {
				newsHolder.tv.setTextColor(Color.BLACK);
			}
			// newsHolder.tv.setText(text);
			newsHolder.tv.setTextSize(fontSize);
			newsHolder.tv.setVisibility(View.VISIBLE);

		} else {
			newsHolder.tv.setVisibility(View.GONE);
			newsHolder.iv.setVisibility(View.VISIBLE);

			final String url = p.getUrl();

			int height = (int) ((((width * 1.0f) / p.getWidth()) * p
					.getHeight()));
			android.view.ViewGroup.LayoutParams params = newsHolder.iv
					.getLayoutParams();
			params.width = width;
			params.height = height;
			newsHolder.iv.setLayoutParams(params);
			// newsHolder.iv.setImageResource(R.drawable.waterwall_list_default);
//			newsHolder.iv.setOnClickListener(new OnClickListener() {
//				private long lastClickTime;
//
//				@Override
//				public void onClick(View v) {
//					long time = System.currentTimeMillis();
//					long timeD = time - lastClickTime;
//					if (0 < timeD && timeD < 500) { // 500毫秒内按钮无效，这样可以控制快速点击，自己调整频率
//						return;
//					}
//					lastClickTime = time;
//
//					Intent intent = new Intent(context, PhotoShowActivity.class);
//					intent.putExtra("FromWhere",
//							PhotoShowActivity.FROM_WATERFALL);
//					intent.putExtra("currentPhoto", url);
//					intent.putStringArrayListExtra("NewsImgUrlList", imgUrls);
//					PvBasicStateDataBiz.BASEFLAG = false;
//					context.startActivity(intent);
//
//				}
//			});

			if (!isScrolling) {
				DeviceConfig deviceConfig = DeviceConfig.getInstance(context);
				int width = deviceConfig.w;
				if (width > 540)
					width = 540;
				flyshareBitmapManager.displayForFlow(newsHolder.iv, url
						+ "&width=" + width, R.drawable.waterwall_list_default,
						R.drawable.waterwall_list_default, width, height, 150,
						false);
			}

			// bitampRunnbles.add(new BitampRunnble(imgUrl,
			// bitmapManager, ivWidth, height,iv));
		}

		return convertView;
	}

	class ViewHolder {
		TextView tv;
		ImageView iv;
	}

	/** 改变日夜模式 **/
	public void changeModeUi(int dayOrNight) {
		if (mode != dayOrNight) {
			mode = dayOrNight;
			this.notifyDataSetChanged();
		}
	}

	/** 改变日夜模式 **/
	public void changeFontSizeUi(int font) {
		this.fontSize = font;
		this.notifyDataSetChanged();

	}

	/** 更新或取消tts阅读时焦点Tv **/
	public synchronized void updateTtsTv(String content, boolean isReading) {

		if (StringTools.isNotEmpty(content)) {
			for (int i = 0; i < size; i++) {
				ShowFlowP p = showFlowPs.get(i);

				if (!p.isImg()) {
					Spanned spanned = Html.fromHtml(p.getContent());
					if (content.equals(spanned.toString())) {
						p.setReading(isReading);
						showFlowPs.set(i, p);
						updateView(i, p, spanned);
					}

				}
			}
		} else {
			for (int i = 0; i < size; i++) {
				ShowFlowP p = showFlowPs.get(i);
				if (!p.isImg()) {
					p.setReading(false);
				}
			}
		}

	}

	private void updateView(int position, ShowFlowP showFlowP, Spanned spanned) {
		int visibleItem = listView.getFirstVisiblePosition();
		int viewItem = position - visibleItem;
		Tools.showLog("tts", "更新时 瀑布流在listview中的位置：" + viewItem);
		if (viewItem < listView.getChildCount() && viewItem >= 0) {
			View view = listView.getChildAt(viewItem + 1);
			ViewHolder viewHolder = null;

			if (view != null) {
				viewHolder = new ViewHolder();
				viewHolder.tv = (TextView) view
						.findViewById(R.id.waterfall_news_item_tv);
				viewHolder.iv = (ImageView) view
						.findViewById(R.id.waterfall_news_item_iv);
				view.setTag(viewHolder);
				// Spanned spanned = Html.fromHtml(showFlowP.getContent());
				if (showFlowP.isReading()) {
					SpannableString msp = new SpannableString(spanned);
					msp.setSpan(new BackgroundColorSpan(Color.CYAN), 0,
							spanned.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					viewHolder.tv.setText(msp);
				} else {
					viewHolder.tv.setText(spanned);
				}

			}
		}
	}

}
