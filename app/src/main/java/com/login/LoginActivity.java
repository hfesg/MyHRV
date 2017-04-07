package com.login;

import java.util.ArrayList;

import org.apache.http.Header;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hrv.MainActivity;
import com.example.hrv.R;
import com.hrv.HRV;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

public class LoginActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnDismissListener {

	private static final String ACTION = "com.login.LoginActivity.FINISH";
	private BroadcastReceiver endReceiver;
	
	protected static final String TAG = "LoginActivity";
//	private LinearLayout mLoginLinearLayout; // 登录内容的容器
	private RelativeLayout mUserIdLinearLayout; // 将下拉弹出窗口在此容器下方显示
//	private Animation mTranslate; // 位移动画
	private ImageView mMoreUser; // 下拉图标
	private ImageView mLoginMoreUserView; // 弹出下拉弹出窗的按钮
	private ArrayList<User> mUsers; // 用户列表
	private ListView mUserIdListView; // 下拉弹出窗显示的ListView对象
	private MyAapter mAdapter; // ListView的监听器
	private PopupWindow mPop; // 下拉弹出窗
	private Button btn_login;
	private Button btn_regist;
	private Button btn_exit;
	private EditText edit_username;
	private EditText edit_password;
	private CheckBox rem_password;// 记住密码
//	private CheckBox auto_login;// 自动登录
	private String userName;
	private String password;
	private boolean loginFlag = false;
	private SharedPreferences sp; // 一个接口，用来存储用户登录信息
	private AsyncHttpClient client = ClientUtil.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		PersistentCookieStore myCookieStore = new PersistentCookieStore(
				getApplicationContext());
		client.setCookieStore(myCookieStore);
		sp = this.getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
		initView();
		setListener();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION);
		endReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				//结束界面
				LoginActivity.this.finish();
			}
		};

		registerReceiver(endReceiver, filter);
		
		// 获取已经保存好的用户密码 
		mUsers = Utils.getUserList(LoginActivity.this);

		if (mUsers.size() > 0) {
			/* 将列表中的第一个user显示在编辑框 */
			edit_username.setText(mUsers.get(0).getId());
			// 判断是否为记住密码状态
			if (sp.getBoolean(mUsers.get(0).getId() + "ISCHECK", true)) {
				edit_password.setText(mUsers.get(0).getPwd());
			}
		}
		
		getFromSP();
//		LinearLayout parent = (LinearLayout) getLayoutInflater().inflate(
//				R.layout.userifo_listview, null);
//		mUserIdListView = (ListView) parent.findViewById(android.R.id.list);
//		parent.removeView(mUserIdListView); // 必须脱离父子关系,不然会报错
//		mUserIdListView.setOnItemClickListener(this); // 设置点击事
//		mAdapter = new MyAapter(mUsers);
//		mUserIdListView.setAdapter(mAdapter);

	}

	// 从sharedPreferences中获取信息
	private void getFromSP() {
		
		if(edit_username.getText().toString().length() == 0){
			rem_password.setChecked(false);
//			auto_login.setChecked(false);
		}else{
		
			if (sp.getBoolean(edit_username.getText().toString() + "ISCHECK", true)) {
				rem_password.setChecked(true);
//				if (sp.getBoolean(edit_username.getText().toString()
//						+ "AUTO_ISCHECK", true)) {
//					auto_login.setChecked(true);
//				}
			}
		}
	}

	/**
	 * 发送账号信息至服务器进行用户信息验证
	 * 
	 * @param user
	 *            账号
	 * @param passw
	 *            密码
	 */
	private void loginFromServer(final String user, final String passw) {

		String url = "LoginServlet";
		RequestParams params = new RequestParams();
		params.put("username", user);
		params.put("password", passw);

		ClientUtil.post(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				String response1 = "用户名或密码错误";
				String response2 = "登陆成功";
				if (statusCode == 200) {

					if ((response1).equals(new String(responseBody))) {

						Intent intent = new Intent();
						intent.setAction("com.hrv.load.FINISH");
						sendBroadcast(intent);
						showToastInfo(new String(responseBody));
					}
					if ((response2).equals(new String(responseBody))) {

						Intent intent = new Intent();
						intent.setAction("com.hrv.load.FINISH");
						sendBroadcast(intent);
						showToastInfo(new String(responseBody));
						boolean mIsSave = true;
						try {
							Log.i(TAG, "保存用户列表");
							for (User users : mUsers) { // 判断本地文档是否有此ID用户
								if (users.getId().equals(user)) {
									mIsSave = false;
									break;
								}
							}
							if (mIsSave) { // 将新用户加入users
								User mUser = new User(user, passw);
								mUsers.add(mUser);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
						// 跳转至主界面
						sendData(userName);
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				error.printStackTrace();
				showToastInfo("连接超时，请重新登录");
				// 关闭等待加载界面
				Intent mIntent = new Intent();
				mIntent.setAction("com.hrv.load.FINISH");
				sendBroadcast(mIntent);
			}
		});
	}

	
	// ListView的适配器 
	class MyAapter extends ArrayAdapter<User> {

		public MyAapter(ArrayList<User> users) {
			super(LoginActivity.this, 0, users);
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.listview_item, null);
			}

			TextView userIdText = (TextView) convertView
					.findViewById(R.id.listview_userid);
			userIdText.setText(getItem(position).getId());

			ImageView deleteUser = (ImageView) convertView
					.findViewById(R.id.login_delete_user);
			deleteUser.setOnClickListener(new OnClickListener() {
				// 点击删除deleteUser时,在mUsers中删除选中的元素
				@Override
				public void onClick(View v) {

					if (getItem(position).getId().equals(userName)) {
						// 如果要删除的用户Id和Id编辑框当前值相等，则清空
						userName = "";
						password = "";
						edit_username.setText(userName);
						edit_password.setText(password);
					}
					mUsers.remove(getItem(position));
					mAdapter.notifyDataSetChanged(); // 更新ListView
				}
			});
			return convertView;
		}

	}
	
	public void initPop() {
		int width = mUserIdLinearLayout.getWidth() - 4;
		int height = LayoutParams.WRAP_CONTENT;
		mPop = new PopupWindow(mUserIdListView, width, height, true);
		mPop.setOnDismissListener(this);// 设置弹出窗口消失时监听器

		// 注意要加这句代码，点击弹出窗口其它区域才会让窗口消失
		mPop.setBackgroundDrawable(new ColorDrawable(0xffffffff));

	}

	private void initView() {

		btn_login = (Button) findViewById(R.id.login);
		btn_regist = (Button) findViewById(R.id.regist);
//		btn_exit = (Button) findViewById(R.id.exit);
		edit_username = (EditText) findViewById(R.id.edit_username);
		edit_password = (EditText) findViewById(R.id.edit_password);
		rem_password = (CheckBox) findViewById(R.id.cb_mima);
//		auto_login = (CheckBox) findViewById(R.id.cb_auto);
		mMoreUser = (ImageView) findViewById(R.id.login_more_user);
		mLoginMoreUserView = (ImageView) findViewById(R.id.login_more_user);
//		mLoginLinearLayout = (LinearLayout) findViewById(R.id.login_linearLayout);
		mUserIdLinearLayout = (RelativeLayout) findViewById(R.id.userId_LinearLayout);
//		mTranslate = AnimationUtils.loadAnimation(this, R.anim.my_translate); // 初始化动画对象

		// initLoginingDlg();
	}

	private void setListener() {

		// 监听记住密码多选框按钮事件
		rem_password.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (rem_password.isChecked()) {

					System.out.println("记住密码已选中");
					if (edit_username.getText().toString().length() != 0) {
						sp.edit()
								.putBoolean(
										edit_username.getText().toString()
												+ "ISCHECK", true).commit();
					}

				} else {
					if (edit_username.getText().toString().length() != 0) {
						System.out.println("记住密码没有选中");
						sp.edit()
								.putBoolean(
										edit_username.getText().toString()
												+ "ISCHECK", false).commit();
					}
				}
			}
		});

//		// 监听自动登录多选框事件
//		auto_login.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			public void onCheckedChanged(CompoundButton buttonView,
//					boolean isChecked) {
//				if (auto_login.isChecked()) {
//					System.out.println("自动登录已选中");
//					if (edit_username.getText().toString().length() != 0) {
//						sp.edit()
//								.putBoolean(
//										edit_username.getText().toString()
//												+ "AUTO_ISCHECK", true)
//								.commit();
//						if (!rem_password.isChecked()) {
//							rem_password.setChecked(true);
//						}
//					}
//
//				} else {
//					System.out.println("自动登录没有选中");
//					if (edit_username.getText().toString().length() != 0) {
//						sp.edit()
//								.putBoolean(
//										edit_username.getText().toString()
//												+ "AUTO_ISCHECK", false)
//								.commit();
//					}
//				}
//			}
//		});

		edit_username.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				userName = s.toString();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
		edit_password.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				password = s.toString();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
		btn_regist.setOnClickListener(this);
		btn_login.setOnClickListener(this);
//		btn_exit.setOnClickListener(this);
		mLoginMoreUserView.setOnClickListener(this);
	}

	/**
	 * 跳转至监测主界面
	 * 
	 * @param user
	 *            用户名
	 */
	protected void sendData(String user) {
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, MainActivity.class);
		intent.putExtra("user_name", user);
		startActivity(intent);
		try {
			Utils.saveUserList(LoginActivity.this, mUsers);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		LoginActivity.this.finish();
	}

	/**
	 * 消息提示
	 * 
	 * @param s
	 *            消息
	 */
	private void showToastInfo(String s) {

		Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 登陆之前，判断账号和密码是否输入
	 */
	private void loginBefore() {

		String userStr;
		String passwdStr;
		userStr = edit_username.getText().toString();
		passwdStr = edit_password.getText().toString();
		loginFlag = false;
		// 判断账号和密码是否为空
		if (userStr.length() != 0) {

			if (passwdStr.length() != 0) {
				userName = userStr;
				password = passwdStr;
			} else {
				showToastInfo("密码不能为空");
				return;
			}
		} else {
			showToastInfo("账号不能为空");
			return;
		}

		loginFlag = true;
	}

//	@Override
//	protected void onPause() {
//		super.onPause();
//		// edit_password.setText("");
//		// edit_username.setText("");
//
//		try {
//			Utils.saveUserList(LoginActivity.this, mUsers);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
////		LoginActivity.this.finish();
//	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		unregisterReceiver(endReceiver);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:
			loginBefore();
			if (loginFlag) {

				// 跳转到加载界面
				Intent intent2 = new Intent();
				intent2.setClass(LoginActivity.this, LoadingActivity.class);
				startActivity(intent2);

				loginFromServer(userName, password);
			}
			break;

		case R.id.regist:
			Intent intent1 = new Intent(LoginActivity.this,
					RegistActivity.class);
			startActivity(intent1);
			break;

		case R.id.exit:
			LoginActivity.this.finish();
			break;

		case R.id.login_more_user:
			if (mPop == null) {
				initPop();
			}
			if (!mPop.isShowing() && mUsers.size() > 0) {
				// Log.i(TAG, "切换为角向上图标");
				mMoreUser.setImageResource(R.drawable.login_more_down); // 切换图标
				mPop.showAsDropDown(mUserIdLinearLayout, 2, 1); // 显示弹出窗口
			}
			break;
		}
	}

	@Override
	public void onDismiss() {
		mMoreUser.setImageResource(R.drawable.login_more_up);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		edit_username.setText(mUsers.get(position).getId());
		// if (rem_password.isChecked()){
		// rem_password.setChecked(false);
		// }
		// if (sp.getBoolean(edit_username.getText().toString()+"ISCHECK", t)) {
		// // 设置默认是记录密码状态
		// rem_password.setChecked(true);
		// }

		if (sp.getBoolean(edit_username.getText().toString() + "ISCHECK", true)) {
			edit_password.setText(mUsers.get(position).getPwd());
			rem_password.setChecked(true);
//			if (sp.getBoolean(edit_username.getText().toString()
//					+ "AUTO_ISCHECK", true)) {
//				auto_login.setChecked(true);
//				// 跳转到加载界面
//				Intent intent2 = new Intent();
//				intent2.setClass(LoginActivity.this, LoadingActivity.class);
//				startActivity(intent2);
//				loginFromServer(edit_username.getText().toString(),
//						edit_password.getText().toString());
//			}
		} else {
			edit_password.setText("");
			rem_password.setChecked(false);
		}

		mPop.dismiss();

	}
}
