package com.login;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import com.example.hrv.R;

public class LoadingActivity extends Activity {

	private static final String ACTION = "com.hrv.load.FINISH";
	private mReceiver myReceiver;
	private TextView msgText;
	private String loadText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		
		msgText = (TextView) findViewById(R.id.loadText);
		
		Intent intent = getIntent();
		loadText = intent.getStringExtra("loadMsg");
		if(loadText != null){
			msgText.setText(loadText);
		}

		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION);

		 myReceiver = new mReceiver();
			
		// 注册广播
		registerReceiver(myReceiver, filter);
	}

	
	class mReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			LoadingActivity.this.finish();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(myReceiver);
	}
}
