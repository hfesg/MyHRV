package com.roundProgressbar;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.example.hrv.R;

public class HelpActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_help);
	}
}
