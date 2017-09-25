package com.iflytek.voicedemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.speech.setting.TtsSettings;
import com.iflytek.speech.util.FucUtil;

public class IshangNlpDemo extends Activity implements OnClickListener {
	private static String TAG = IshangNlpDemo.class.getSimpleName();
	
	// 语音合成对象
	private SpeechSynthesizer mTts;

	private Toast mToast;	
	private EditText mNlpText;
	
	private AIUIAgent mAIUIAgent = null;
	private int mAIUIState = AIUIConstant.STATE_IDLE;
	
	@SuppressLint("ShowToast")
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.nlpdemo);
		initLayout();
		
		// 初始化合成对象
		mTts = SpeechSynthesizer.createSynthesizer(IshangNlpDemo.this, mTtsInitListener);
		
		mSharedPreferences = getSharedPreferences(TtsSettings.PREFER_NAME, MODE_PRIVATE);
		
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
	}
	
	/**
	 * 初始化Layout。
	 */
	private void initLayout(){
		findViewById(R.id.text_nlp_start).setOnClickListener(this);
		findViewById(R.id.nlp_start).setOnClickListener(this);

		mNlpText = (EditText)findViewById(R.id.nlp_text);
		
		findViewById(R.id.nlp_stop).setOnClickListener(this);
		findViewById( R.id.update_lexicon).setOnClickListener(this);
	}
	
	int ret = 0;// 函数调用返回值
	@Override
	public void onClick(View view) 
	{				
		if( !checkAIUIAgent() ){
			return;
		}
		
		switch (view.getId()) {
		// 开始文本理解
		case R.id.text_nlp_start:
			startTextNlp();
			break;
		// 开始语音理解
		case R.id.nlp_start:
			startVoiceNlp();
			break;
		// 停止语音理解
		case R.id.nlp_stop:
			// AIUI 是连续会话，一次 start 后，可以连续的录音并返回结果；要停止需要调用 stop
			stopVoiceNlp();
			break;
		case R.id.update_lexicon:
			updateLexicon();
			break;
		default:
			break;
		}
	}
	
	private String getAIUIParams() {
		String params = "";

		AssetManager assetManager = getResources().getAssets();
		try {
			InputStream ins = assetManager.open( "cfg/aiui_phone.cfg" );
			byte[] buffer = new byte[ins.available()];

			ins.read(buffer);
			ins.close();

			params = new String(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return params;
	}
	
	private boolean checkAIUIAgent(){
		if( null == mAIUIAgent ){
			Log.i( TAG, "create aiui agent" );
			mAIUIAgent = AIUIAgent.createAgent( this, getAIUIParams(), mAIUIListener );
			AIUIMessage startMsg = new AIUIMessage(AIUIConstant.CMD_START, 0, 0, null, null);
			mAIUIAgent.sendMessage( startMsg );
		}
		
		if( null == mAIUIAgent ){
			final String strErrorTip = "创建 AIUI Agent 失败！";
			showTip( strErrorTip );
			this.mNlpText.setText( strErrorTip );
		}
		
		return null != mAIUIAgent;
	}
	
	private void startVoiceNlp(){
		Log.i( TAG, "start voice nlp" );
		mNlpText.setText("");
		
		// 先发送唤醒消息，改变AIUI内部状态，只有唤醒状态才能接收语音输入
		if( AIUIConstant.STATE_WORKING != 	this.mAIUIState ){
			AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null);
			mAIUIAgent.sendMessage(wakeupMsg);
		}
		
		// 打开AIUI内部录音机，开始录音
		String params = "sample_rate=16000,data_type=audio";
		AIUIMessage writeMsg = new AIUIMessage( AIUIConstant.CMD_START_RECORD, 0, 0, params, null );
		mAIUIAgent.sendMessage(writeMsg);
	}
	
	private void stopVoiceNlp(){
		Log.i( TAG, "stop voice nlp" );
		// 停止录音
		String params = "sample_rate=16000,data_type=audio";
		AIUIMessage stopWriteMsg = new AIUIMessage(AIUIConstant.CMD_STOP_RECORD, 0, 0, params, null);
		
		mAIUIAgent.sendMessage(stopWriteMsg);
	}
	
	private void startTextNlp(){
		Log.i( TAG, "start text nlp" );
		String text = mNlpText.getText().toString();
		mNlpText.setText("");
		String params = "data_type=text";
		
		if( TextUtils.isEmpty(text) ){
			text = "合肥明天的天气怎么样？";
		}
		
		byte[] textData = text.getBytes();
		
		AIUIMessage msg = new AIUIMessage(AIUIConstant.CMD_WRITE, 0, 0, params, textData);
		mAIUIAgent.sendMessage(msg);
	}
	
	private void updateLexicon(){
		String params = null;
		String contents = FucUtil.readFile(this, "userwords","utf-8");
		try{
			JSONObject joAiuiLexicon = new JSONObject();
			joAiuiLexicon.put( "name", "userword" );
			joAiuiLexicon.put( "content", contents );
			params = joAiuiLexicon.toString();
		}catch( Throwable e ){
			e.printStackTrace();
			showTip( e.getLocalizedMessage() );
		}//end of try-catch
		
		mNlpText.setText(contents);

		AIUIMessage msg = new AIUIMessage(AIUIConstant.CMD_UPLOAD_LEXICON, 0, 0, params, null);
		mAIUIAgent.sendMessage(msg);
	}
	
	private AIUIListener mAIUIListener = new AIUIListener() {

		@Override
		public void onEvent(AIUIEvent event) {
			switch (event.eventType) {
				case AIUIConstant.EVENT_WAKEUP: 
					Log.i( TAG,  "on event: "+ event.eventType );
					showTip( "进入识别状态" );
				break;
	
				case AIUIConstant.EVENT_RESULT: {
					Log.i( TAG,  "on event: "+ event.eventType );
					try {
						JSONObject bizParamJson = new JSONObject(event.info);//{"data":[{"content":[{"dtf":"json","cnt_id":"0","dte":"utf8"}],"params":{"rss":"0","sub":"iat","qisr_handle":"6f0c00134554","rstid":1,"lrst":false}}]}
						JSONObject data = bizParamJson.getJSONArray("data").getJSONObject(0);//{"content":[{"dtf":"json","cnt_id":"0","dte":"utf8"}],"params":{"rss":"0","sub":"iat","qisr_handle":"6f0c00134554","rstid":1,"lrst":false}}
						JSONObject params = data.getJSONObject("params");//{"rss":"0","sub":"iat","qisr_handle":"6f0c00134554","rstid":1,"lrst":false}
						JSONObject content = data.getJSONArray("content").getJSONObject(0);//{"dtf":"json","cnt_id":"0","dte":"utf8"},属于后处理时：{"dtf":"custom","cnt_id":"0","dte":"custom"}
						
						if (content.has("cnt_id")) {
							String cnt_id = content.getString("cnt_id");
							JSONObject cntJson = new JSONObject(new String(event.data.getByteArray(cnt_id), "utf-8"));//{"text":{"bg":0,"sn":1,"ws":[{"bg":0,"cw":[{"w":"天气","sc":0,"wb":0,"wc":0,"we":0}]}],"ls":false,"ed":0}}
	
							mNlpText.append( "\n" );
							mNlpText.append(cntJson.toString());
							
							String sub = params.optString("sub");
							if ("nlp".equals(sub)) {
								// 解析得到语义结果
								String resultStr = cntJson.optString("intent");//{"intent":{"sid":"cid6f193291@ch00b80d1c1be6010001","text":"天气","dialog_stat":"DataValid","answer":{"text":"\"北京\"今天\"晴\"，\"14℃~30℃\"，\"西北风3-4级\""},"service":"weather","data":{"result":[{"wind":"西北风3-4级","windLevel":1,"dateLong":1505750400,"lastUpdateTime":"2017-09-19 11:39:20","weatherType":0,"date":"2017-09-19","city":"北京","exp":{"ct":{"expName":"穿衣指数","prompt":"天气热，建议着短裙、短裤、短薄外套、T恤等夏季服装。","level":"热"}},"humidity":"20%","airQuality":"优","tempRange":"14℃~30℃","weather":"晴","temp":29,"pm25":"13","airData":40},{"wind":"南风微风","windLevel":0,"tempRange":"14℃~27℃","dateLong":1505836800,"lastUpdateTime":"2017-09-19 11:39:20","weather":"晴","weatherType":0,"date":"2017-09-20","city":"北京"},{"wind":"南风微风","windLevel":0,"tempRange":"17℃~28℃","dateLong":1505923200,"lastUpdateTime":"2017-09-19 11:39:20","weather":"多云","weatherType":1,"date":"2017-09-21","city":"北京"},{"wind":"西北风微风","windLevel":0,"tempRange":"15℃~28℃","dateLong":1506009600,"lastUpdateTime":"2017-09-19 11:39:20","weather":"晴","weatherType":0,"date":"2017-09-22","city":"北京"},{"wind":"南风微风","windLevel":0,"tempRange":"18℃~29℃","dateLong":1506096000,"lastUpdateTime":"2017-09-19 11:39:20","weather":"晴转多云","weatherType":0,"date":"2017-09-23","city":"北京"},{"wind":"东风微风","windLevel":0,"tempRange":"19℃~28℃","dateLong":1506182400,"lastUpdateTime":"2017-09-19 11:39:20","weather":"阴","weatherType":2,"date":"2017-09-24","city":"北京"},{"wind":"东南风微风","windLevel":0,"tempRange":"19℃~28℃","dateLong":1506268800,"lastUpdateTime":"2017-09-19 11:39:20","weather":"多云转阴","weatherType":1,"date":"2017-09-25","city":"北京"}]},"semantic":[{"intent":"QUERY","slots":[{"value":"CURRENT_CITY","normValue":"CURRENT_CITY","name":"location.city"},{"value":"CURRENT_POI","normValue":"CURRENT_POI","name":"location.poi"},{"value":"LOC_POI","normValue":"LOC_POI","name":"location.type"},{"value":"内容","name":"queryType"},{"value":"天气状态","name":"subfocus"}]}],"uuid":"atn0091e314@ch484f0d1c1be86f2601","rc":0}}
								
								// 获取answer字段
								JSONObject answer = cntJson.getJSONObject("intent").getJSONObject("answer");
								String answerStr = answer.optString("text");
								Log.i(TAG, "answerStr=" + answerStr);
								
								//朗读answer字段
								if (answerStr.length() > 0) {
									mTts.startSpeaking(answerStr, mTtsListener);
								}
								
								Log.i( TAG, resultStr );
							} else if("tpp".equals(sub)) {//后处理数据
								// 解析得到语义结果
								String resultStr = cntJson.optString("intent");//{"intent":{"sid":"cid6f193291@ch00b80d1c1be6010001","text":"天气","dialog_stat":"DataValid","answer":{"text":"\"北京\"今天\"晴\"，\"14℃~30℃\"，\"西北风3-4级\""},"service":"weather","data":{"result":[{"wind":"西北风3-4级","windLevel":1,"dateLong":1505750400,"lastUpdateTime":"2017-09-19 11:39:20","weatherType":0,"date":"2017-09-19","city":"北京","exp":{"ct":{"expName":"穿衣指数","prompt":"天气热，建议着短裙、短裤、短薄外套、T恤等夏季服装。","level":"热"}},"humidity":"20%","airQuality":"优","tempRange":"14℃~30℃","weather":"晴","temp":29,"pm25":"13","airData":40},{"wind":"南风微风","windLevel":0,"tempRange":"14℃~27℃","dateLong":1505836800,"lastUpdateTime":"2017-09-19 11:39:20","weather":"晴","weatherType":0,"date":"2017-09-20","city":"北京"},{"wind":"南风微风","windLevel":0,"tempRange":"17℃~28℃","dateLong":1505923200,"lastUpdateTime":"2017-09-19 11:39:20","weather":"多云","weatherType":1,"date":"2017-09-21","city":"北京"},{"wind":"西北风微风","windLevel":0,"tempRange":"15℃~28℃","dateLong":1506009600,"lastUpdateTime":"2017-09-19 11:39:20","weather":"晴","weatherType":0,"date":"2017-09-22","city":"北京"},{"wind":"南风微风","windLevel":0,"tempRange":"18℃~29℃","dateLong":1506096000,"lastUpdateTime":"2017-09-19 11:39:20","weather":"晴转多云","weatherType":0,"date":"2017-09-23","city":"北京"},{"wind":"东风微风","windLevel":0,"tempRange":"19℃~28℃","dateLong":1506182400,"lastUpdateTime":"2017-09-19 11:39:20","weather":"阴","weatherType":2,"date":"2017-09-24","city":"北京"},{"wind":"东南风微风","windLevel":0,"tempRange":"19℃~28℃","dateLong":1506268800,"lastUpdateTime":"2017-09-19 11:39:20","weather":"多云转阴","weatherType":1,"date":"2017-09-25","city":"北京"}]},"semantic":[{"intent":"QUERY","slots":[{"value":"CURRENT_CITY","normValue":"CURRENT_CITY","name":"location.city"},{"value":"CURRENT_POI","normValue":"CURRENT_POI","name":"location.poi"},{"value":"LOC_POI","normValue":"LOC_POI","name":"location.type"},{"value":"内容","name":"queryType"},{"value":"天气状态","name":"subfocus"}]}],"uuid":"atn0091e314@ch484f0d1c1be86f2601","rc":0}}
								
								// 获取answer字段
								String answerStr = cntJson.getJSONObject("intent").optString("answer");
								Log.i(TAG, "answerStr=" + answerStr);
								
								//朗读answer字段
								if (answerStr.length() > 0) {
									mTts.startSpeaking(answerStr, mTtsListener);
								}
								
								Log.i( TAG, resultStr );
							}
						}
					} catch (Throwable e) {
						e.printStackTrace();
						mNlpText.append( "\n" );
						mNlpText.append( e.getLocalizedMessage() );
					}
					
					mNlpText.append( "\n" );
				} break;
	
				case AIUIConstant.EVENT_ERROR: {
					Log.i( TAG,  "on event: "+ event.eventType );
					mNlpText.append( "\n" );
					mNlpText.append( "错误: "+event.arg1+"\n"+event.info );
				} break;
	
				case AIUIConstant.EVENT_VAD: {
					if (AIUIConstant.VAD_BOS == event.arg1) {
						showTip("找到vad_bos");
					} else if (AIUIConstant.VAD_EOS == event.arg1) {
						showTip("找到vad_eos");
					} else {
						showTip("" + event.arg2);
					}
				} break;
				
				case AIUIConstant.EVENT_START_RECORD: {
					Log.i( TAG,  "on event: "+ event.eventType );
					showTip("开始录音");
				} break;
				
				case AIUIConstant.EVENT_STOP_RECORD: {
					Log.i( TAG,  "on event: "+ event.eventType );
					showTip("停止录音");
				} break;
	
				case AIUIConstant.EVENT_STATE: {	// 状态事件
					mAIUIState = event.arg1;
					
					if (AIUIConstant.STATE_IDLE == mAIUIState) {
						// 闲置状态，AIUI未开启
						showTip("STATE_IDLE");
					} else if (AIUIConstant.STATE_READY == mAIUIState) {
						// AIUI已就绪，等待唤醒
						showTip("STATE_READY");
					} else if (AIUIConstant.STATE_WORKING == mAIUIState) {
						// AIUI工作中，可进行交互
						showTip("STATE_WORKING");
					}
				} break;
				
				case AIUIConstant.EVENT_CMD_RETURN:{
					if( AIUIConstant.CMD_UPLOAD_LEXICON == event.arg1 ){
						showTip( "上传"+ (0==event.arg2?"成功":"失败") );
					}
				}break;
				
				default:
					break;
			}
		}

	};
 
    @Override
    protected void onDestroy() {
    	super.onDestroy();
        
    	if( null != this.mAIUIAgent ){
    		AIUIMessage stopMsg = new AIUIMessage(AIUIConstant.CMD_STOP, 0, 0, null, null);
    		mAIUIAgent.sendMessage( stopMsg );
    		
    		this.mAIUIAgent.destroy();
    		this.mAIUIAgent = null;
    	}
    }
	
	private void showTip(final String str)
	{
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				mToast.setText(str);
				mToast.show();
			}
		});
	}
	
	/**
	 * 初始化监听。
	 */
	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d(TAG, "InitListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
        		showTip("初始化失败,错误码："+code);
        	} else {
				// 初始化成功，之后可以调用startSpeaking方法
        		// 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
        		// 正确的做法是将onCreate中的startSpeaking调用移至这里
        		setParam();
        		int code1 = mTts.startSpeaking("请说话", mTtsListener);
        		if (code1 != ErrorCode.SUCCESS) {
    				if(code1 == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED){
    					//未安装则跳转到提示安装页面
    					//mInstaller.install();
    				}else {
    					showTip("语音合成失败,错误码: " + code1);	
    				}
    			}
			}		
		}
	};
	
	// 引擎类型
	private String mEngineType = SpeechConstant.TYPE_CLOUD;
	
	// 默认发音人
	private String voicer = "xiaoyan";
	
	private SharedPreferences mSharedPreferences;
	
	/**
	 * 参数设置
	 * @param param
	 * @return 
	 */
	private void setParam(){
		// 清空参数
		mTts.setParameter(SpeechConstant.PARAMS, null);
		// 根据合成引擎设置相应参数
		if(mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
			mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
			// 设置在线合成发音人
			mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
			//设置合成语速
			mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "50"));
			//设置合成音调
			mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"));
			//设置合成音量
			mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "50"));
		}else {
			mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
			// 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
			mTts.setParameter(SpeechConstant.VOICE_NAME, "");
			/**
			 * TODO 本地合成不设置语速、音调、音量，默认使用语记设置
			 * 开发者如需自定义参数，请参考在线合成参数设置
			 */
		}
		//设置播放器音频流类型
		mTts.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"));
		// 设置播放合成音频打断音乐播放，默认为true
		mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
		
		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
		mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");
	}
	
	// 缓冲进度
	private int mPercentForBuffering = 0;
	// 播放进度
	private int mPercentForPlaying = 0;
	
	/**
	 * 合成回调监听。
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener() {
		
		@Override
		public void onSpeakBegin() {
			showTip("开始播放");
		}

		@Override
		public void onSpeakPaused() {
			showTip("暂停播放");
		}

		@Override
		public void onSpeakResumed() {
			showTip("继续播放");
		}

		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos,
				String info) {
			// 合成进度
			mPercentForBuffering = percent;
			showTip(String.format(getString(R.string.tts_toast_format),
					mPercentForBuffering, mPercentForPlaying));
		}

		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
			// 播放进度
			mPercentForPlaying = percent;
			showTip(String.format(getString(R.string.tts_toast_format),
					mPercentForBuffering, mPercentForPlaying));
		}

		@Override
		public void onCompleted(SpeechError error) {
			if (error == null) {
				showTip("播放完成");
			} else if (error != null) {
				showTip(error.getPlainDescription(true));
			}
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			// 若使用本地能力，会话id为null
			//	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			//		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			//		Log.d(TAG, "session id =" + sid);
			//	}
		}
	};
}
