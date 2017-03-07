package com.userInfoActivity;

import com.example.hrv.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class UserInfoActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_userinfo);
	}
}
