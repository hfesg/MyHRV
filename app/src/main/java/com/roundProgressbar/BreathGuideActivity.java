package com.roundProgressbar;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hrv.R;

public class BreathGuideActivity extends Activity {
	private RoundProgressBar mRoundProgressBar2;
	private Button start;
	private Button close;
	private Button stop;
	private Button btn_voice_close;

	private Button btn_voice_open;
	private Button btn_help;

	private MediaPlayer breath;
	private MediaPlayer outBreath;
	private MediaPlayer bingxi;

	private Thread mThread;
	private boolean isRunning = false;
	private boolean voice = true;
	private boolean isPause = false;
	private int green;
	private int green_light;
	private int yellow;

	private TextView MtextView, FtextView;
	private int miao = 60;
	private int fen = 4;
	private int tflag = 1;
	private Timer timer;
	private TimerTask task;
	private Timer proTimer;
	private TimerTask proTask;
	private Handler handler, mHandler;
	private int progress = 1;
	private int NUM = 0;
	
	private ImageView rotaion;
	private Animation anim;
	private TextView breathText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_cricle_progress);

		FtextView = (TextView) findViewById(R.id.fentext);
		MtextView = (TextView) findViewById(R.id.miaotext);
		breathText = (TextView) findViewById(R.id.breath_text);
		mRoundProgressBar2 = (RoundProgressBar) findViewById(R.id.roundProgressBar2);

		rotaion = (ImageView) findViewById(R.id.rotate);
		anim = AnimationUtils.loadAnimation(BreathGuideActivity.this,
				R.anim.rotate_circle_anim);
		
		breath = MediaPlayer.create(this, R.raw.breath);
		outBreath = MediaPlayer.create(this, R.raw.outbreath);
		bingxi = MediaPlayer.create(this, R.raw.bingxi);

		start = (Button) findViewById(R.id.start);
//		stop = (Button) findViewById(R.id.stop);
		close = (Button) findViewById(R.id.close);
		btn_voice_close = (Button) findViewById(R.id.btn_voice_close);
		btn_voice_open = (Button) findViewById(R.id.btn_voice_open);
		btn_help = (Button) findViewById(R.id.btn_help);

		yellow = getResources().getColor(R.color.yellow);
		green = getResources().getColor(R.color.green);
		green_light = getResources().getColor(R.color.green_light);

		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isPause = false;
				
				if (!isRunning) {
					timer = new Timer();
					rotaion.startAnimation(anim);// 开始动画
					initProgress();
					// 简单的通过Handler+Task形成一个定时任务
					handler = new Handler() {
						@Override
						public void handleMessage(Message msg) {
							if (msg.what == 1 && !isPause) {
								dingshi();
								super.handleMessage(msg);
							}
						}
					};

					if (tflag % 2 == 1) {
						task = new TimerTask() { // 定时器
							@Override
							public void run() {

								Message message = new Message();
								message.what = 1; // 设置标志
								handler.sendMessage(message);

							}
						};
						tflag = tflag + 1;

						timer.schedule(task, 1000, 1000);// 运行时间和间隔都是1s

					} else {
						if (tflag % 2 == 0) {
							if (task != null) {
								task.cancel();
							}
							tflag = tflag + 1;
						}
					}
				}
			}
		});

	}
	
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_voice_close:
			voice = false;
			break;

		case R.id.btn_voice_open:
			voice = true;
			break;

//		case R.id.stop:
//			isPause = true;
//			isRunning = true;
//			break;

		case R.id.close:
			// onDestroy();
			//MainActivity.this.finish();
//			openOptionsDialog();
			BreathGuideActivity.this.finish();
			break;

		case R.id.btn_help:
			Intent intent = new Intent(BreathGuideActivity.this, HelpActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
	
//	//退出界面时弹出的对话框
//		private void openOptionsDialog() {
//			new AlertDialog.Builder(BreathGuideActivity.this).setTitle("退出")
//					.setMessage("确定退出吗？")
//					.setNegativeButton("否", new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface arg0, int arg1) {
//							// TODO Auto-generated method stub
//							return;
//						}
//					})
//					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface arg0, int arg1) {
//							// TODO Auto-generated method stub
//							BreathGuideActivity.this.finish();
//						}
//					}
//
//					).show();
//		}
		
		// 重置
		private void reset() {
			isRunning = false;
			miao = 60;
			fen = 4;
			tflag = 1;
			NUM = 0;
			FtextView.setText("05");
			breathText.setText("深吸气");
			mRoundProgressBar2.setProgress(0);
		}
		
		// 定时
		private void dingshi() {

			miao--;
			FtextView.setText("0" + fen);
			if (miao < 0) {
				fen = fen - 1;
				miao = 59;
				FtextView.setText("0" + fen);
				MtextView.setText("" + miao);

			} else {
				if (miao < 10 && miao >= 0) {
					MtextView.setText("0" + miao);
				} else {
					MtextView.setText("" + miao);
				}
			}
			if (miao == 0 && fen == 0) {
				timer.cancel();
				task.cancel();
				// 停止动画
				rotaion.clearAnimation();
			}
		}

		
		// 开始启动进度条
		private void initProgress() {
			isRunning = true;
			proTimer = new Timer();
			
			proTask = new TimerTask() {
				
				@Override
				public void run() {
					Message msg = new Message();
					msg.what = 1;
					mediaHandler.sendMessage(msg);
					
				}
			};
			proTimer.schedule(proTask, 125, 125);
		}
		
		@Override
		protected void onPause() {
			super.onPause();
			
		}
		
		@Override
		protected void onDestroy() {
			super.onDestroy();
			if(breath!=null){
				breath.stop();
				breath.release();
			}
			if(outBreath!=null){
				outBreath.stop();
				outBreath.release();
			}
			if(bingxi!=null){
				bingxi.stop();
				bingxi.release();
			}
			if(proTimer != null){
				proTimer.cancel();
			}
			if(proTask != null){
				proTask.cancel();
			}
			
		}
		
		Handler mediaHandler = new Handler(){
			public void handleMessage(Message msg) {
				if(msg.what == 2) {
					reset();
				}
				if(msg.what == 1){
					
					if(progress <= 120){
					
						if (progress == 1){
							if (voice && !isPause && breath != null) {
								breath.start();
								
							}
							breathText.setText("深吸气");
							mRoundProgressBar2.setRoundProgressColor(green);
						}
						
						if (progress == 40){
							if (voice && !isPause && bingxi != null) {
								bingxi.start();
								
							}
							breathText.setText("屏息");
							mRoundProgressBar2.setRoundProgressColor(yellow);
						}
					
						if (progress == 56){
							if (voice && !isPause && outBreath != null) {
								outBreath.start();
								
							}
							breathText.setText("呼气");
							mRoundProgressBar2.setRoundProgressColor(green_light);
						}
						
						if (!isPause) {
							mRoundProgressBar2.setProgress(progress);
						} else {
							progress = progress - 1;
							return;
					
						}
						
						progress++;
					}else{
						progress = 1;
						NUM++;
					}
					
					if(NUM >= 20){
						proTimer.cancel();
						proTask.cancel();
						reset();
					}
				}
				
			}
			
		};
	
}
