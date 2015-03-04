package com.inveno.piflow.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.EngineInfo;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;

import com.inveno.piflow.activity.BaseFragmentActivity;
import com.inveno.piflow.entity.model.NewsInfo;
import com.inveno.piflow.tools.commontools.StringTools;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 语音业务逻辑
 * 
 * @author hongchang.liu
 * 
 */
public class TtsBiz implements OnUtteranceCompletedListener {

	/** 来自资讯主界面 **/
	public static final int FROM_NEWS_MAIN = 0;
	/** 来自资讯详细页 **/
	public static final int FROM_NEWS_DETAIL = 1;
	/** 来自瀑布流详细页 **/
	public static final int FROM_NEWS_WATERFALL = 2;

	private final String TTS_ENGINE = "com.iflytek.tts";
	private final int MAX_SIZE = 5;

	private static TtsBiz ttsBiz;
	private boolean canbeUse;
	private TextToSpeech tts;

	private String currentStr;

	private boolean stop;

	public boolean isStop() {
		return stop;
	}

	/**资讯主界面内容**/
	private ArrayList<NewsInfo> infos;
	
	private ArrayList<String> waterfallNews;
	
	private ArrayList<String> detailInfos;

	/** 遍历读 计数变量 */
	private int i;

	private int size;

	private Context context;

	private boolean clickPrevious;

	private int pageCount;

	// /** 语音读取位置来源，默认是资讯主界面 **/
	// private boolean fromNewsDetail;

	private int fromWhere;

	// /** 阅读标题还是内容，默认标题 */
	// private boolean readContent;

	public void setFromWhere(int fromWhere) {
		this.fromWhere = fromWhere;
	}

	
	
	public int getI() {
		return i;
	}

	/** 是否暂停状态 */
	private boolean pause;

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	private TtsBiz(final Context context) {

		this.context = context;
		stop = true;

		checkTtsEngine();

	}

	public static TtsBiz getInstance(Context context) {
		if (ttsBiz == null) {
			ttsBiz = new TtsBiz(context);
		}
		return ttsBiz;
	}

	/**
	 * 检查tts Engine是否可用
	 */
	public boolean checkTtsEngine() {

		if (Build.VERSION.SDK_INT >= 14) {
			tts = new TextToSpeech(context, new OnInitListener() {

				@Override
				public void onInit(int status) {
					// TODO Auto-generated method stub
					Tools.showLog("tts", "oninit");
					if (status == TextToSpeech.SUCCESS) {
						int result = tts.setLanguage(Locale.CHINESE);
						// tts.setPitch(0.1f);
						Tools.showLog("tts", "TextToSpeech.SUCCESS");
						if (result == TextToSpeech.LANG_MISSING_DATA
								|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
							canbeUse = false;
							Tools.showLog("tts", "TextToSpeech.faile chinese");
						} else {
							int speech = Tools.getInformain(Tools.TTS_SPEECH,
									10, context);
							tts.setSpeechRate(speech * 0.1f);
							tts.setOnUtteranceCompletedListener((OnUtteranceCompletedListener) TtsBiz.this);
							canbeUse = true;
							Tools.showLog("tts", "TextToSpeech.success chinese");
						}
					}
				}
			}, TTS_ENGINE);

			List<EngineInfo> engineInfos = tts.getEngines();
			EngineInfo engineInfo = null;
			for (int i = 0; i < engineInfos.size(); i++) {
				engineInfo = engineInfos.get(i);
				if (engineInfo.name.equalsIgnoreCase(TTS_ENGINE)) {
					canbeUse = true;

				}
			}
		}

		Tools.showLog("tts", "检查engine 是否可用：" + canbeUse);
		return canbeUse;
	}

	/**
	 * 获取默认语音引擎
	 * 
	 * @return
	 */
	public String getDefaultEngine() {
		String engineLable = null;

		if (tts == null) {
			tts = new TextToSpeech(context, new OnInitListener() {

				@Override
				public void onInit(int status) {
					// TODO Auto-generated method stub
					Tools.showLog("tts", "oninit");

				}
			});
		}
		String defaultEngine = tts.getDefaultEngine();
		List<EngineInfo> engineInfos = tts.getEngines();
		EngineInfo engineInfo = null;
		for (int i = 0; i < engineInfos.size(); i++) {
			engineInfo = engineInfos.get(i);
			if (engineInfo.name.equalsIgnoreCase(defaultEngine)) {
				engineLable = engineInfo.label;
			}
		}
		return engineLable;

	}

	public boolean isCanbeUse() {
		return canbeUse;
	}

	public TextToSpeech getTts() {
		return tts;
	}

	public void previous() {

		clickPrevious = true;

		if (timer != null) {
			timer.cancel();

		}
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (clickPrevious && tts.isSpeaking()) {
					tts.stop();
				}

			}
		}, 500);

	}

	public void next() {

		if (timer != null) {
			timer.cancel();

		}
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!clickPrevious && tts.isSpeaking()) {
					tts.stop();
				}

			}
		}, 500);

	}

	public void start() {
		if (timer != null) {
			timer.cancel();

		}
		if (speakingTimer != null) {
			speakingTimer.cancel();
		}
		switch (fromWhere) {
		case FROM_NEWS_MAIN:
			i = 0;
			currentStr = infos.get(i).getTitle();
			break;
		case FROM_NEWS_DETAIL:
//			i = pageCount;
			currentStr = detailInfos.get(i);
			break;
		case FROM_NEWS_WATERFALL:
			currentStr = waterfallNews.get(i);
			break;

		default:
			break;
		}
		// if (!fromNewsDetail) {
		// i = 0;
		// currentStr = infos.get(i).getTitle();
		// } else {
		// i = pageCount;
		// // if (!readContent) {
		// // currentStr = infos.get(i).getTitle();
		// // }
		// }

		// if (!readContent && infos != null) {
		// currentStr = infos.get(i).getTitle();
		// }

		if (StringTools.isNotEmpty(currentStr)) {

			stop = false;
			Tools.showLog("tts", " start  i：" + i);
			// HashMap<String, String> ttsRender = new HashMap<String,
			// String>();
			// // currentStr = infos.get(i).getTitle();
			// ttsRender.put(
			// TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
			// "tts");
			// tts.speak(currentStr, TextToSpeech.QUEUE_FLUSH,
			// ttsRender);
			if (timer != null) {
				timer.cancel();

			}
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (!stop && StringTools.isNotEmpty(currentStr)) {
						Tools.showLog("tts", "  内容：" + currentStr);
						HashMap<String, String> ttsRender = new HashMap<String, String>();
						// currentStr = infos.get(i).getTitle();
						ttsRender.put(
								TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
								"tts");
						tts.speak(currentStr, TextToSpeech.QUEUE_FLUSH,
								ttsRender);
					}

				}
			}, 1000);
		}

	}

	public void stop() {
		Tools.showLog("tts", "ttsbiaz点击了停止");
		stop = true;
		if (tts != null) {
			tts.stop();
		}
		if (fromWhere == FROM_NEWS_MAIN) {
			i = 0;
		}

	}

	public void destroy() {
		if (timer != null) {
			timer.cancel();

		}
		if (speakingTimer != null) {
			speakingTimer.cancel();
		}
		if (tts != null) {
			stop();
			try {
				tts.shutdown();
			} catch (Exception e) {
				Tools.showLog("exception", "tts 销毁报错！！！！");
				e.printStackTrace();
			}
			i=0;
		}

		ttsBiz = null;
	}

	private Timer timer;
	private Timer speakingTimer;

	/*** 调节语速 **/
	public void speechToSpeak(final int progress) {
		if (tts != null && progress <= 20 && progress > 0) {

			// tts.setSpeechRate(progress * 0.1f);

			if (timer != null) {
				timer.cancel();

			}
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (!stop) {
						// tts.stop();
						tts.setSpeechRate(progress * 0.1f);
					}

				}
			}, 500);
		}
	}

	@Override
	public void onUtteranceCompleted(String utteranceId) {
		// TODO Auto-generated method stub

		((BaseFragmentActivity) context).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Tools.showLog("tts", "完成一次阅读  i:" + i + " stop:" + stop);

				if (!stop) {
					// 来自资讯主界面

					switch (fromWhere) {
					case FROM_NEWS_MAIN:
						if (clickPrevious) {
							if (i == 0) {
								if (pageCount == 0) {
									i = 0;
								} else {
									onSpeakCompletedListener.onSpeakCompleted(
											-1, "", size);
									i = MAX_SIZE;
								}

							} else {
								i--;
							}

						} else {
							i++;
						}

						if (i < 0 || i > MAX_SIZE) {
							onSpeakCompletedListener.onSpeakCompleted(i, "",
									size);
							i = 0;
						}

						currentStr = infos.get(i).getTitle();

						if (!isStop()) {
							onSpeakCompletedListener.onSpeakCompleted(i,
									currentStr, size);
						}

						if (speakingTimer != null) {
							speakingTimer.cancel();
						}
						speakingTimer = new Timer();
						speakingTimer.schedule(new TimerTask() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (!stop && !tts.isSpeaking()) {
									HashMap<String, String> ttsRender = new HashMap<String, String>();

									ttsRender
											.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
													"tts");
									tts.speak(currentStr,
											TextToSpeech.QUEUE_FLUSH, ttsRender);
								}

							}
						}, 1000);

						clickPrevious = false;
						break;
					case FROM_NEWS_DETAIL:
						

						if (clickPrevious) {
							i--;
						} else {
							i++;
						}

						if (i < 0) {
							onSpeakCompletedListener.onSpeakCompleted(i, "",
									size);
							i = 0;
						} else if (i > size - 1) {
							onSpeakCompletedListener.onSpeakCompleted(i, "",
									pageCount);
							if (i > size - 1) {
								i = 0;
							}
							if (i >pageCount - 1) {
								i = 0;
								tts.stop();
								return;
							}
						}
						currentStr=detailInfos.get(i);
						
						if (!isStop()) {
							onSpeakCompletedListener.onSpeakCompleted(i,
									currentStr, size);
						}
						if (speakingTimer != null) {
							speakingTimer.cancel();
						}
						speakingTimer = new Timer();
						speakingTimer.schedule(new TimerTask() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (!stop && !tts.isSpeaking()
										&& StringTools.isNotEmpty(currentStr)) {
									HashMap<String, String> ttsRender = new HashMap<String, String>();

									ttsRender
											.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
													"tts");
									tts.speak(currentStr,
											TextToSpeech.QUEUE_FLUSH, ttsRender);
								}

							}
						}, 1000);
					
						clickPrevious = false;
						
						break;
					case FROM_NEWS_WATERFALL:
						
						if (clickPrevious) {							
							i--;
						} else {
							i++;
						}
						if (i < 0) {
//							onSpeakCompletedListener.onSpeakCompleted(i, "",
//									size);
							i = 0;
						} else if (i > size - 1) {
							onSpeakCompletedListener.onSpeakCompleted(i, "",
									size);
							i=0;
							tts.stop();
							return;


						}
						
						currentStr=waterfallNews.get(i);

						if (!stop) {
							onSpeakCompletedListener.onSpeakCompleted(i,
									currentStr, size);
						}
						if (speakingTimer != null) {
							speakingTimer.cancel();
						}
						speakingTimer = new Timer();
						speakingTimer.schedule(new TimerTask() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (!stop && !tts.isSpeaking()
										&& StringTools.isNotEmpty(currentStr)) {
									HashMap<String, String> ttsRender = new HashMap<String, String>();

									ttsRender
											.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
													"tts");
									tts.speak(currentStr,
											TextToSpeech.QUEUE_FLUSH, ttsRender);
								}

							}
						}, 1000);
					
						clickPrevious = false;
						
						break;

					default:
						break;
					}

				}

			}
		});

	}

	public void speakNewsMain(ArrayList<NewsInfo> newsInfos, int currentPage) {
		if (newsInfos != null && newsInfos.size() != 0) {
			infos = newsInfos;
			this.size = newsInfos.size();
			this.pageCount = currentPage;
		}

	}

	public void speakNewsContent(ArrayList<String> waterNews, int currentPage) {
		this.pageCount = currentPage;
		this.detailInfos=waterNews;
		this.size = waterNews.size();
	}

	public void speakNewsWaterFall(ArrayList<String> waterNews) {
		if (waterNews != null && waterNews.size() != 0) {
			this.waterfallNews=waterNews;
			this.size=waterNews.size();
		}
		
	}
	
	// public void speakNewsDetail(ArrayList<NewsInfo> newsInfos){
	// infos = newsInfos;
	// this.size = newsInfos.size();
	// }

	// /** 阅读标题还是内容，true为读内容，false为标题 **/
	// public void setSpeakTitleOrContent(boolean readWhat) {
	// this.readContent = readWhat;
	// }

	public interface OnSpeakCompletedListener {

		void onSpeakCompleted(int i, String content, int size);
	}

	private OnSpeakCompletedListener onSpeakCompletedListener;

	public void setOnSpeakCompletedListener(
			OnSpeakCompletedListener onSpeakCompletedListener) {
		this.onSpeakCompletedListener = onSpeakCompletedListener;
	}

}
