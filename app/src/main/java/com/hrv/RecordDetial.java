package com.hrv;

import java.text.DecimalFormat;

import android.annotation.SuppressLint;
import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

import com.Rwave.BarchartView;
import com.Rwave.XYChartView;
import com.example.hrv.R;

public class RecordDetial extends ActivityGroup implements View.OnClickListener {

	private String inferDatas;
	private String sandianDatas;
	private String pointStr;
	private String hasFinished;
	private static final String ACTION = "com.hrv.HRV.FINISH";

	public int[] inferDataViews = { R.id.xinlv, R.id.huxi, R.id.jiaolv,
			R.id.SDNN, R.id.NN, R.id.PNN, R.id.rMSSD };

	private Button btnxin;
	// private String DATE = "date";
	// private String TIME = "time";
	// private String DATABASE = "database_name";
	private String dat = null;
	private String tim = null;

	private float[] origxindian = new float[3000];// 存储心电值

	// 时域、频域、散点图分析
	private Button back;
	private TextView[] inferData = new TextView[7];
	private float[] huaTuData = new float[5];
	private float[] pinYuData = new float[4];
	private float[] hrvData ;

	private TextView inferTime;
	private LinearLayout pinyu, sandian;
	private BarchartView barchartView;
	private XYChartView xyChartView;

	// // 定义数据库
	// private GERENsqlHelper gerenDB;
	// private Cursor mCursor;
	// private static String DATABASE_NAME = null;

	// TAB
	private int index_tab = 0;
	private TabWidget tabWidget;
	View tab1;
	View tab2;
	View tab3;

	private TextView dateText;
	// private static int fenbian;
	// private BitmapView bitmap;
	// private StressScore mentalStress;
	private float meanRR = 0;
	private double heartRate;
	private double respRate;
	private double stressScore;
	private double sdnn, nn50, pnn50, r_mssd;
	private double tp, vlf, lf, hf;

	// private LinearLayout bitLayout;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.recorddetial);

		// 发送广播，关闭等待加载界面
		Intent mIntent = new Intent();
		mIntent.setAction("com.hrv.load.FINISH");
		sendBroadcast(mIntent);

		// 初始化控件
		for (int i = 0; i < inferData.length; i++) {
			inferData[i] = (TextView) findViewById(inferDataViews[i]);
		}
		inferTime = (TextView) findViewById(R.id.time);
		btnxin = (Button) findViewById(R.id.xindian);
		dateText = (TextView) findViewById(R.id.date);
		btnxin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent();
				intent.setClass(RecordDetial.this, XindianActivity.class);
				intent.putExtra("origxindian", origxindian);
				startActivity(intent);
			}
		});

		// 获取从采集界面传递过来的心电数据，用来画出心电波形
		Intent intent = getIntent();
		hasFinished = intent.getStringExtra("isDown");
		inferDatas = intent.getStringExtra("inferData");
		sandianDatas = intent.getStringExtra("sandianData");
		pointStr = intent.getStringExtra("pointStr");
		dat = intent.getStringExtra("date");
		tim = intent.getStringExtra("time");
		inferTime.setText(tim);
		dateText.setText(dat);

		if (hasFinished.equals("down")) {
			// 结束测试界面
			Intent finishIntent = new Intent();
			finishIntent.setAction(ACTION);
			sendBroadcast(finishIntent);
		}

		// 获取HRV分析数据，进行显示
		String[] inferdatas = inferDatas.split(":");

		// 散点图数据，即hrv数据
		String[] sanDianDatas = sandianDatas.split(":");
		hrvData = new float[sanDianDatas.length];
		for (int i = 0; i < hrvData.length; i++) {

			hrvData[i] = Float.parseFloat(sanDianDatas[i]);
		}

		DecimalFormat df = new DecimalFormat("0.00");
//		Double score = Double.valueOf(df.format(s));

		// 呼吸
		respRate = Double.valueOf(df.format(Double.parseDouble(inferdatas[0])));
		// 心率
		heartRate = Double.valueOf(df.format(Double.parseDouble(inferdatas[1])));
		// 精神压力
		stressScore = Double.valueOf(df.format(Double.parseDouble(inferdatas[2])));
		
		// 获取时域参数--SDNN:NN50：PNN50：r_MSSD
		sdnn = Double.valueOf(df.format(Double.parseDouble(inferdatas[3])));
		nn50 = Double.valueOf(df.format(Double.parseDouble(inferdatas[4])));
		pnn50 = Double.valueOf(df.format(Double.parseDouble(inferdatas[5])));
		r_mssd = Double.valueOf(df.format(Double.parseDouble(inferdatas[6])));

		// 获取频域参数--TP:VLF:LF:HF
		meanRR = Float.parseFloat(inferdatas[7]);
		tp = Float.parseFloat(inferdatas[8]);
		vlf = Float.parseFloat(inferdatas[9]);
		lf = Float.parseFloat(inferdatas[10]);
		hf = Float.parseFloat(inferdatas[11]);

		for (int i = 0; i < 7; i++) {

			switch (i) {
			case 0:
				// 心率
				inferData[0].setText(heartRate + "");
				break;
			case 1:
				// 呼吸率
				inferData[1].setText(respRate + "");
				break;
			case 2:
				// 精神压力
				inferData[2].setText(stressScore+"");
				break;
			case 3:
				// hrv参数
				inferData[3].setText(sdnn + "");
				break;
			case 4:
				inferData[4].setText(nn50 + "");
				break;
			case 5:
				inferData[5].setText(pnn50 + "");
				break;
			case 6:
				inferData[6].setText(r_mssd + "");
				break;
			default:
				break;
			}
		}

		String[] pointDatas = pointStr.split(":");
		// 获取3000个心电数据，存储至xindian集合中
		for (int n = 0; n < origxindian.length; n++) {
			origxindian[n] = Float.parseFloat(pointDatas[n]);
		}

		// 准备频域图数据
		pinYuData[0] = (float) tp / 3;
		pinYuData[1] = (float) vlf / 3;
		pinYuData[2] = (float) lf / 3;
		pinYuData[3] = (float) hf / 3;

		back = (Button) findViewById(R.id.fanhui);
		// store = (Button) findViewById(R.id.store);
		back.setOnClickListener(this);
		// store.setOnClickListener(this);

		// tab
		TabHost t = (TabHost) findViewById(R.id.t1);
		t.setup(this.getLocalActivityManager());
		t.setPadding(5, 5, 5, 5);
		tabWidget = t.getTabWidget();
		LayoutInflater fi = LayoutInflater.from(RecordDetial.this);
		View view = fi.inflate(R.layout.tab_layout, null);
		LinearLayout ll = (LinearLayout) view.findViewById(R.id.tablayout);
		tab1 = view.findViewById(R.id.tab1);
		tab2 = view.findViewById(R.id.tab2);
		tab3 = view.findViewById(R.id.tab3);
		ll.removeAllViews();
		t.addTab(t.newTabSpec("1").setIndicator(tab1).setContent(R.id.tabview1));
		t.addTab(t.newTabSpec("2").setIndicator(tab2).setContent(R.id.tabview2));
		t.addTab(t.newTabSpec("3").setIndicator(tab3).setContent(R.id.tabview3));
		tabWidget.setBaselineAligned(true);
		tabWidget.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.btn_anniu));
		tab1.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.menu_bg));
		for (int i = 0; i < tabWidget.getChildCount(); i++) {

			tabWidget.getChildAt(i).getLayoutParams().width = 220;
			tabWidget.getChildAt(i).getLayoutParams().height = 120;
		}
		t.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				tabChanged(tabId);
			}
		});

		chartView();
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (view == back) {
			finish();
			if (hasFinished.equals("down")) {
				Intent restartIntent = new Intent(RecordDetial.this, HRV.class);
				startActivity(restartIntent);
			}
		}
		// if (view == store) {
		//
		// Intent intent = new Intent();
		// intent.setClass(RecordDetial.this, Store.class);
		// startActivity(intent);
		//
		// }
	}

	// 捕获tab变化事件
	@SuppressWarnings("deprecation")
	public void tabChanged(String tabId) {
		if (index_tab != (Integer.valueOf(tabId) - 1)) {
			tabWidget.getChildAt(Integer.valueOf(tabId) - 1)
					.setBackgroundDrawable(
							getResources().getDrawable(R.drawable.menu_bg));
			tabWidget.getChildAt(index_tab).setBackgroundDrawable(null);
			index_tab = Integer.valueOf(tabId) - 1;

		}
	}

	private void chartView() {

		pinyu = (LinearLayout) findViewById(R.id.pinyutu);
		sandian = (LinearLayout) findViewById(R.id.sandiantu);
		barchartView = new BarchartView(this, pinYuData, hrvData);
		xyChartView = new XYChartView(this, hrvData, meanRR);
		pinyu.removeAllViews();
		pinyu.addView(barchartView);
		sandian.removeAllViews();
		sandian.addView(xyChartView);

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (hasFinished.equals("down")) {
			Intent restartIntent = new Intent(RecordDetial.this, HRV.class);
			startActivity(restartIntent);
		}
	}

}
