package com.login;

import org.apache.http.Header;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hrv.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RegistActivity extends Activity {

	private EditText edit_username;
	private EditText edit_passwd;
	private EditText edit_passwd_again;
	private EditText edit_age;
	private RadioButton man;
	private RadioButton women;
	private Button regist;
	private TextView statement;
	private Button exit;

	private String nameStr;
	private String mimaStr;
	private String ageStr;
	private String genderStr;
	private String datetime;//注册时间

	private boolean regFlag = false;//注册成功标识

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.regist);

		edit_username = (EditText) findViewById(R.id.edit_username);
		edit_passwd = (EditText) findViewById(R.id.edit_passwd);
		edit_passwd_again = (EditText) findViewById(R.id.edit_passwd_again);
		edit_age = (EditText) findViewById(R.id.edit_age);
		man = (RadioButton) findViewById(R.id.radio_man);
		women = (RadioButton) findViewById(R.id.radio_women);
		regist = (Button) findViewById(R.id.btn_regist);
		statement = (TextView) findViewById(R.id.btn_statement);
		exit = (Button) findViewById(R.id.exit_regist);

		regist.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				registBefore();
				if (regFlag == true) {
					System.out.println("用户名："+nameStr);
					System.out.println("密码："+mimaStr);
					System.out.println("性别："+genderStr);
					System.out.println("年龄："+ageStr);
					
					sendRegInfoToServer(nameStr, mimaStr, genderStr, ageStr);
					//RegistActivity.this.finish();
				}
			}
		});
		
		exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RegistActivity.this.finish();
			}
		});

	}

	// 发送注册信息至服务器端存储至数据库
	protected void sendRegInfoToServer(String nameStr2, String mimaStr2,
			String genderStr2, String ageStr2) {

		String url = "RegistServlet";

		RequestParams params = new RequestParams();
		params.put("username", nameStr2);
		params.put("password", mimaStr2);
		params.put("gender", genderStr2);
		params.put("age", ageStr2);

		ClientUtil.post(url, params, new AsyncHttpResponseHandler()
		{

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody)
			{
				if (statusCode == 200)
				{
					showToast(new String(responseBody));
					RegistActivity.this.finish();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error)
			{
				error.printStackTrace();
				showToast("连接超时，请检查您的网络连接");
			}
		});

	}

	// 注册前判断注册信息是否合理
	protected void registBefore() {
		String userName;
		String passwd;
		String passwd_again;
		String age;
		regFlag = false;

		userName = edit_username.getText().toString();
		passwd = edit_passwd.getText().toString();
		passwd_again = edit_passwd_again.getText().toString();
		age = edit_age.getText().toString();

		if (userName.length() != 0) {
			if (userName.length() >= 3 && userName.length() <= 12) {
				nameStr = userName;
			} else {
				showToast("账号不能小于6位且不超过12位");
				return;
			}
		} else {
			showToast("账号不能为空");
			return;
		}

		if (passwd.length() != 0) {
			if (passwd.length() >= 3 && passwd.length() <= 12) {
				if ((passwd_again).equals(passwd)) {
					mimaStr = passwd;
				} else {
					showToast("确认密码不匹配，请重新输入");
					edit_passwd_again.setText("");
					return;
				}
			} else {
				showToast("密码不能小于6位且不超过12位");
			}
		} else {
			showToast("密码不能为空");
			return;
		}

		if (man.isChecked()) {
			genderStr = "男";
		} else if (women.isChecked()) {
			genderStr = "女";
		} else {
			showToast("请选择您的性别");
			return;
		}

		if (age.length() != 0) {
			ageStr = age;
		} else {
			showToast("请输入您的年龄");
			return;
		}

		regFlag = true;
	}

	private void showToast(String info) {

		Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
	}
	
	//获取当前时间
	private void getTime()
	{        
	    long dateTaken = System.currentTimeMillis();  
	    if (dateTaken != 0) {  
	        datetime = DateFormat.format("yyyy-MM-dd kk-mm", dateTaken).toString();
	    }
    }
}
